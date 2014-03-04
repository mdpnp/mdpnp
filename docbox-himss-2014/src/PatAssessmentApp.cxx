/**
 * @file    PatAssessmentApp.cxx
 *
 * @brief   Defines a class which handles the publication and subscription of patient
 * assessment data. This class is used to test the PatAssessmentWriterInterface
 * and PatAssessmentReaderInterface.
 *
 */
//=============================================================================

#include "PatAssessmentWriterInterface.h"
#include "PatAssessmentReaderInterface.h"
#include "PatAssessmentApp.h"
#include <stdlib.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdio.h>

#ifdef _WIN32
#define STRNCASECMP _strnicmp
#else
#define STRNCASECMP strncasecmp
#endif
#define IS_OPTION(str, option) (STRNCASECMP(str, option, strlen(str)) == 0)

using namespace std;
using namespace ice;
using namespace DDS;

class PatAssessmentWriterInterface;

PatAssessmentApp::PatAssessmentApp()
  : _domain_id(0),
    _is_pub(false),
    _is_sub(false)
{
}

PatAssessmentApp::~PatAssessmentApp()
{
}

string PatAssessmentApp::get_statusmsg()
{
  return _statusmsg;
}

/**
 * Print help message.
 * @param [in] thisprogramname The name of the executable (argv[0]).
 * @param [in] exitprogram 0 or 1 value signifying whether or not to close
 * application after help message is printed.
 */
void PatAssessmentApp::CmdLineHelp(const char *thisprogramname, int exitprogram)
{
  const char* smsg =
    "\nUSAGE:\n\n"
    "  %s  -pub | -sub\n"
    "  {} Required\n"
    "  [] Optional\n"
    "  |  Separator between option choices\n"
    "where:\n"
    "  -pub             Run publisher (-pub or -sub required)\n"
    "  -sub             Run subscriber (-pub or -sub required)\n"
    "  -help            Print help message\n"
    "  -domainId <id>   DDS domain id (optional, default 0)\n"
    "\n"
    "Brief Description:\n"
    "Test patient assessment publisher and subscriber.\n\n";

  printf(smsg, thisprogramname);
  if (exitprogram) exit(0);
}

int PatAssessmentApp::ParseCommandLineArgs(int argc, char* argv[])
{
  if (argc < 2)
  {
    _statusmsg = "Show help ...";
    return __LINE__;
  }

  // Load command line parameters
  for (int ix = 0; ix < argc; ++ix)
  {
    if (IS_OPTION(argv[ix], "-help")) CmdLineHelp(argv[0], 1);
    if (IS_OPTION(argv[ix], "-?")) CmdLineHelp(argv[0], 1);

    if (IS_OPTION(argv[ix], "-domainId"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "ParseCommandLineArgs() Error: "
          "Missing <id> after -domainId";
        return __LINE__;
      }
      _domain_id = strtol(argv[ix], 0, 10);
      if ((_domain_id < 0) || (errno == ERANGE))
      {
        _statusmsg =
          "ParseCommandLineArgs error: "
          "the value entered for domain id is out of range";
        return __LINE__;
      }
    }
    else if (IS_OPTION(argv[ix], "-pub"))
    {
      _is_pub = true;
    }
    else if (IS_OPTION(argv[ix], "-sub"))
    {
      _is_sub = true;
    }
  }

  if (_is_pub && _is_sub)
  {
    _statusmsg =
      "ParseCommandLineArgs error: "
      "you must specify either -pub OR -sub. Not both.";
    return __LINE__;
  }

  if (!_is_pub && !_is_sub)
  {
    _statusmsg =
      "ParseCommandLineArgs error: "
      "you must specify either -pub or -sub.";
    return __LINE__;
  }

  return 0;
}

void PatAssessmentApp::LoadDefaults(DdsAutoType<ice::PatientAssessment> & pat_assessment_instance) {
    // assign assessment data
    strncpy(pat_assessment_instance.demographics.given_name, "Randall", 32);
    strncpy(pat_assessment_instance.demographics.family_name, "Jones", 32);
    pat_assessment_instance.demographics.date_of_birth.century = 19;
    pat_assessment_instance.demographics.date_of_birth.year = 80;
    pat_assessment_instance.demographics.date_of_birth.month = 1;
    pat_assessment_instance.demographics.date_of_birth.day = 1;
    snprintf(pat_assessment_instance.demographics.mrn, 64, "%s", "10101");
    pat_assessment_instance.demographics.height = 1.8;
    pat_assessment_instance.demographics.weight = 90;
    pat_assessment_instance.pain_assessment = noticeable;
    pat_assessment_instance.sedation_assessment._d = alert;
    pat_assessment_instance.sedation_assessment._u.alert_sedation_score = calm;
    pat_assessment_instance.activity_assessment = limited_walking;
    pat_assessment_instance.nutrition_assessment = eats_most_meals_offered;
    strncpy(pat_assessment_instance.clinician_name, "Jane Smith, RN", 32);
    strncpy(pat_assessment_instance.notes, "Patient is on X medication.", 32);
}

int PatAssessmentApp::LoadConfiguration(const char* file, DdsAutoType<ice::PatientAssessment> & patient_assessment) {
        struct stat s;
	char buf[500];
	char *name, *value;
	char *tab, *newline;
	DDS_ExceptionCode_t ex;
	int offset = 0;
     
	name = (char*) buf;

        if(stat(file, &s)) {
            perror("No initialization file; using defaults");
	    return offset;
        } else if(S_ISREG(s.st_mode)) {
	  FILE *F = fopen(file, "r");
            while(fgets(buf, 500, F)) {
              tab = strchr(buf, '\t');
	      newline = strchr(buf, '\n');
	      if(newline) {
			*newline = '\0';
		}
	      if(tab) {
                *tab = '\0';
                value = tab + 1;
		//	fprintf(stderr, "name=%s value=%s\n", name, value);
		if(0==strncmp("offset", name, 100)) {
		  offset = atoi(value);
		} else if(0==strncmp("demographics.given_name", name, 100)) {
		  strncpy(patient_assessment.demographics.given_name, value, 32);
		} else if(0==strncmp("demographics.family_name", name, 100)) {
		  strncpy(patient_assessment.demographics.family_name, value, 32);
		} else if(0==strncmp("demographics.date_of_birth.century", name, 100)) {
		  patient_assessment.demographics.date_of_birth.century = atoi(value);
		} else if(0==strncmp("demographics.date_of_birth.year", name, 100)) {
		  patient_assessment.demographics.date_of_birth.year = atoi(value);
		} else if(0==strncmp("demographics.date_of_birth.month", name, 100)) {
                  patient_assessment.demographics.date_of_birth.month = atoi(value);
                } else if(0==strncmp("demographics.date_of_birth.day", name, 100)) {
                  patient_assessment.demographics.date_of_birth.day = atoi(value);
                } else if(0==strncmp("demographics.mrn", name, 100)) {
                  strncpy(patient_assessment.demographics.mrn, value, 64);
                } else if(0==strncmp("demographics.height", name, 100)) {
		  patient_assessment.demographics.height = atof(value);
		} else if(0==strncmp("demographics.weight", name, 100)) {
		  patient_assessment.demographics.weight = atof(value);
		} else if(0==strncmp("activity_assessment", name, 100)) {
		  patient_assessment.activity_assessment = (ActivityScore) ActivityScore_get_typecode()->find_member_by_name(value, ex);
	          //fprintf(stderr, "Activity Assessment %d\n", patient_assessment.activity_assessment);
		} else if(0==strncmp("nutrition_assessment", name, 100)) {
		  patient_assessment.nutrition_assessment = (NutritionScore) NutritionScore_get_typecode()->find_member_by_name(value, ex);
                  //fprintf(stderr, "Nutrition Assessment %d\n", patient_assessment.nutrition_assessment);
		} else if(0==strncmp("pain_assessment", name, 100)) {
		  patient_assessment.pain_assessment = (PainScore) PainScore_get_typecode()->find_member_by_name(value, ex);
                  //fprintf(stderr, "Pain Assessment %d\n", patient_assessment.pain_assessment);
		} else if(0==strncmp("notes", name, 100)) {
		  strncpy(patient_assessment.notes, value, 100 /*128*/);
		} else if(0==strncmp("clinician_name", name, 100)) {
		  strncpy(patient_assessment.clinician_name, value, 100);
		} else {
		  fprintf(stderr, "Unrecognized name=%s value=%s\n", name, value);
                }
	      }
	    }
	    fclose(F);
	    return offset;
        } else {
            fprintf(stderr, "Initialization file is not a regular file.\n");
	    return offset;
        }
    
}

int PatAssessmentApp::PublisherMain()
{
  int istat = 0;
  try 
  {
    PatAssessmentWriterInterface patAssessmentWriterInterface(_domain_id);

    DDS_Duration_t send_period = {10,000000000};

    cout << "Sending assessment data" << endl;

    // Allocate a patient assessment structure
    DdsAutoType<PatientAssessment> pat_assessment_instance;
    
    LoadDefaults(pat_assessment_instance);
    int time_offset = 0;

    DDS_Time_t time;

    while (true)
      {
	time_offset = LoadConfiguration("PatAssessmentApp.ini", pat_assessment_instance);
      patAssessmentWriterInterface.GetCommunicator()->GetParticipant()->get_current_time(time);
      pat_assessment_instance.timestamp.seconds = time.sec+time_offset;
      pat_assessment_instance.timestamp.nanoseconds = time.nanosec;

      // Write the data									
      patAssessmentWriterInterface.Write(pat_assessment_instance);

      // Wait before writing another sample
      NDDSUtility::sleep(send_period);
    }
  }
  catch (string message)
  {
    cout << "Application exception: " << message << endl;
    istat = 1;
  }

  return istat;
}

int PatAssessmentApp::SubscriberMain()
{
  int istat = 0;
  try 
  {
    PatAssessmentReaderInterface patAssessmentReaderInterface(_domain_id);
    patAssessmentReaderInterface.WaitForPatientAssessments();
  }
  catch (string message)
  {
    cout << "Application exception: " << message << endl;
    istat = 1;
  }

  return istat;
}

int PatAssessmentApp::RunApp(const char *programname)
{
  int istat = 0;

  cout << endl << "Run " << programname << " as ";
  cout << ((_is_pub) ? "Publisher" : "Subscriber") << endl;
  cout << "  domain id = " << _domain_id << endl;
  
  if (_is_pub)
  {
    istat = PublisherMain();
  }
  else
  {
    istat = SubscriberMain();
  }
  return istat;
}
