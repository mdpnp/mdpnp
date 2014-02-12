/**
 * @file    PatAssessmentReaderInterface.cxx
 *
 * @brief   Defines a class which reads from the PatientAsessement topic.
 * 
 */
//=============================================================================

#include "PatAssessmentReaderInterface.h"

using namespace std;
using namespace ice;


// -------------------------------------------------------------------------
// Constructor
PatAssessmentReaderInterface::PatAssessmentReaderInterface(long domain_id)
{ 
  _communicator = new DDSCommunicator();

  // Create a DomainParticipant
  if (NULL == _communicator->CreateParticipant(domain_id))
  {
    std::stringstream errss;
    errss << "Failed to create DomainParticipant object";
    throw errss.str();
  }

  DDS::DomainParticipant* participant = _communicator->GetParticipant(); 

  // Create a subscriber 
  DDS::Subscriber *sub = _communicator->CreateSubscriber();
  if (sub == NULL) 
  {
    std::stringstream errss;
    errss << "Failed to create Subscriber object";
    throw errss.str();
  }

  // Create topic
  DDS::Topic *topic = _communicator->CreateTopic<PatientAssessment>(ice::PatientAssessmentTopic); 

  // Create DataReader
  DDS::DataReader *reader = sub->create_datareader_with_profile(topic, 
    "dices_dim_library", 
    "dices_dim_durable_profile",
    NULL, 
    DDS::STATUS_MASK_NONE);

  if (reader == NULL)
  {
    std::stringstream errss;
    errss << "Failed to create DataReader";
    throw errss.str();
  }

  // Downcast the generic data reader to a patient assessment DataReader 
  _pat_assessment_reader = PatientAssessmentDataReader::narrow(reader);
  if (_pat_assessment_reader == NULL)
  {
    std::stringstream errss;
    errss << "Failed to cast patient assessment reader";
    throw errss.str();
  }

  // This WaitSet object will be used to block a thread until one or more 
  // conditions become true.  In this case, there is a single condition that
  // will wake up the WaitSet when the reader receives data
  _waitSet = new DDS::WaitSet();
  if (_waitSet == NULL) 
  {
    std::stringstream errss;
    errss << "PatAssessmentReaderInterface(): failure to create WaitSet.";
    throw errss.str();
  }

  // Use this status condition to wake up the thread when data becomes 
  // available
  _condition = _pat_assessment_reader->get_statuscondition();

  // Wake up the thread when data is available
  _condition->set_enabled_statuses(DDS_DATA_AVAILABLE_STATUS);
  if (_condition == NULL) 
  {
    std::stringstream errss;
    errss << "PatAssessmentReaderInterface(): failure to initialize condition.";
    throw errss.str();
  }

  _instance_alive = _pat_assessment_reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_ALIVE_INSTANCE_STATE);
  _instance_disposed = _pat_assessment_reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE,DDS_NOT_ALIVE_DISPOSED_INSTANCE_STATE); 
  _instance_no_writers = _pat_assessment_reader->create_readcondition(DDS_ANY_SAMPLE_STATE, DDS_ANY_VIEW_STATE, DDS_NOT_ALIVE_NO_WRITERS_INSTANCE_STATE);

  // Attaching the conditions to the WaitSet
  _waitSet->attach_condition(_condition);
  _waitSet->attach_condition(_instance_alive);
  _waitSet->attach_condition(_instance_disposed);
  _waitSet->attach_condition(_instance_no_writers);

}


// -------------------------------------------------------------------------
// Destructor
PatAssessmentReaderInterface::~PatAssessmentReaderInterface()
{
  _waitSet->detach_condition(_condition);
  _waitSet->detach_condition(_instance_alive);
  _waitSet->detach_condition(_instance_disposed);
  _waitSet->detach_condition(_instance_no_writers);
  delete _waitSet;

  _pat_assessment_reader->delete_contained_entities();
  DDS::Subscriber *sub = _pat_assessment_reader->get_subscriber();
  sub->delete_datareader(_pat_assessment_reader);
}

// -------------------------------------------------------------------------
// Wait for data
void PatAssessmentReaderInterface::WaitForPatientAssessments()
{

  DDS::ConditionSeq active_conditions;
  DDS::SampleInfoSeq info_seq;

  // How long to block for data at a time
  DDS_Duration_t timeout = {0, 1000000}; // 1ms

  while (true)
  {
    // Block thread for assessment data to arrive
    DDS::ReturnCode_t retcode = _waitSet->wait(active_conditions, timeout);

	if (retcode == DDS::RETCODE_TIMEOUT) 
	{
		// OK to timeout
	}
	else if (retcode != DDS::RETCODE_OK) 
	{
		std::stringstream errss;
		errss << "WaitForPatientAssessments(): error " << retcode << " when receiving assessments.";		
		throw errss.str();
	}
    else if (retcode == DDS::RETCODE_OK)
    {
      for (int i = 0; i < active_conditions.length(); ++i)
      {
        if (active_conditions[i] == _instance_alive)
        {
          PatientAssessmentSeq pat_assesments;
          if (_pat_assessment_reader->take_w_condition(pat_assesments, info_seq, DDS::LENGTH_UNLIMITED, _instance_alive) == DDS_RETCODE_OK)
          {
            for(int j = 0; j < pat_assesments.length(); j++)
        	{
              if (info_seq[j].valid_data)
              {
                cout << "Patient Assessment Data" << endl;
                PatientAssessmentTypeSupport::print_data(&pat_assesments[j]);
              }
            }
            _pat_assessment_reader->return_loan(pat_assesments, info_seq);
          }
        }
      }
    }
  }
}
