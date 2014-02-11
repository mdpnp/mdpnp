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

int PatAssessmentApp::PublisherMain()
{
  int istat = 0;
  try 
  {
    PatAssessmentWriterInterface patAssessmentWriterInterface(_domain_id);

    DDS_Duration_t send_period = {0,100000000};

    cout << "Sending assessment data" << endl;

    // Allocate a patient assessment structure
    DdsAutoType<PatientAssessment> pat_assessment_instance;
 
    // assign assessment data
	sprintf(pat_assessment_instance.demographics.given_name, "%s", "Randall");
	sprintf(pat_assessment_instance.demographics.family_name, "%s", "Jones");
    pat_assessment_instance.demographics.date_of_birth.century = 19;
    pat_assessment_instance.demographics.date_of_birth.year = 80;
    pat_assessment_instance.demographics.date_of_birth.month = 1;
    pat_assessment_instance.demographics.date_of_birth.day = 1;
	sprintf(pat_assessment_instance.demographics.mrn, "%s", "10101");
    pat_assessment_instance.demographics.height = 1.8;
    pat_assessment_instance.demographics.weight = 90;
    pat_assessment_instance.pain_assessment = noticeable;
    pat_assessment_instance.sedation_assessment._d = alert;
    pat_assessment_instance.sedation_assessment._u.alert_sedation_score = calm;
    pat_assessment_instance.activity_assessment = limited_walking;
    pat_assessment_instance.nutrition_assessment = eats_most_meals_offered;
	sprintf(pat_assessment_instance.clinician_name, "%s", "Jane Smith, RN");
	sprintf(pat_assessment_instance.notes, "%s", "Patient is on X medication.");

    while (true)
    {
      // Timestamp data
      DDS_Time_t time;
      patAssessmentWriterInterface.GetCommunicator()->GetParticipant()->get_current_time(time);
      pat_assessment_instance.timestamp.seconds = time.sec;
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
