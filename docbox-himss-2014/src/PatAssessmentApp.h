/**
 * @file    PatAssessmentApp.h
 *
 * @brief   Declares a class which runs a publisher or subscriber.
 */
//=============================================================================
#ifndef PAT_ASSESSMENT_APP_H
#define PAT_ASSESSMENT_APP_H

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>
#ifndef _WIN32
#include <sys/time.h>
#endif
#include "ndds/ndds_cpp.h"
#include "ndds/ndds_namespace_cpp.h"
#include "DDSTypeWrapper.h"
#include "himss_2014_patassessment.h"
#include "himss_2014_patassessmentSupport.h"


class PatAssessmentApp
{
public:

  PatAssessmentApp();
  ~PatAssessmentApp();

  std::string get_statusmsg();

  /**
   * Print help message.
   * @param [in] thisprogramname The name of the executable (argv[0]).
   * @param [in] exitprogram 0 or 1 value signifying whether or not to close
   * application after help message is printed.
   */
  static void CmdLineHelp(const char *thisprogramname, int exitprogram);

  int ParseCommandLineArgs(int argc, char* argv[]);
  int RunApp(const char *programname);

  bool IsPub() const { return _is_pub; };
  bool IsSub() const { return !_is_pub; };

private:
  // Disallow use of implicitly generated member functions:
  PatAssessmentApp(const PatAssessmentApp &src);
  PatAssessmentApp &operator=(const PatAssessmentApp &rhs);

  /**
   * Wait for condition to trigger and sample to be received.
   * @param [in] rti_dds_impl Pointer to RTIDDSImpl class
   * @param [in] cond Pointer to condition which a WaitSet will attach.
   * @param [in] timeout WaitSet returns after this period expires or
   *   an event triggers the condition.
   * @return Returns zero for success.
   */
  //int WaitForSample(RTIDDSImpl* rti_dds_impl,
  //  DDSCondition* cond, DDS_Duration_t timeout);

  /**
   * Run publisher. This function sends the patient assessment data.
   */
  int PublisherMain();

  /**
   * Run subscriber. This function subscribes to the patient assessment
   * data.
   * @return Returns zero for success.
   */
  int SubscriberMain();

  /**
   * Create Read condition to trigger when data is available.
   * @param [in] reader Pointer to DataReader
   * @return Condition that was created. Returns null for failure.
   */
  DDSCondition* CreateReadCondition(DDSDataReader* reader);

  /**
   * Load defaults
   */
  void LoadDefaults(DdsAutoType<ice::PatientAssessment> & patient_assessment);
 
  /**
   * Load information from a configuration file.
   */
  int LoadConfiguration(const char* file, DdsAutoType<ice::PatientAssessment> & patient_assessment);

  std::string _statusmsg;
  long _domain_id;
  bool _is_pub;
  bool _is_sub;
};

#endif
