/**
 * @file    PatAssessmentWriterInterface.h
 *
 * @brief   Defines a class which writes to the PatientAssessment topic. 
 * 
 */
//=============================================================================
#include "PatAssessmentWriterInterface.h"

using namespace ice;

// ----------------------------------------------------------------------------
// Constructor.
// Creates the domain participant, publisher, topic and writer. 
PatAssessmentWriterInterface::PatAssessmentWriterInterface(long domain_id)
{
  _communicator = new DDSCommunicator();

  // Create a DomainParticipant
  if (NULL == _communicator->CreateParticipant(domain_id))
  {
    std::stringstream errss;
    errss << " if (pub == NULL) Failed to create DomainParticipant object";
    throw errss.str();
  }

  DDS::DomainParticipant* participant = _communicator->GetParticipant(); 

  // Create a Publisher
  DDS::Publisher *pub = _communicator->CreatePublisher();
  if (NULL == pub)
  {
    std::stringstream errss;
    errss << "Failed to create Publisher object";
    throw errss.str();
  }

  // Create Topic
  DDS::Topic *topic = _communicator->CreateTopic<PatientAssessment>(ice::PatientAssessmentTopic); 

  // Create DataWriter
  DDS::DataWriter *writer = pub->create_datawriter_with_profile(topic, 
    "dices_dim_library", 
    "dices_dim_durable_profile",
    NULL,
    DDS::STATUS_MASK_NONE);

  if (writer == NULL)
  {
    std::stringstream errss;
    errss << "Failed to create DataWriter";
    throw errss.str();
  }

  // Downcast the generic datawriter to a patient assessment DataWriter 
  _pat_assessment_writer = PatientAssessmentDataWriter::narrow(writer);
  if (_pat_assessment_writer == NULL)
  {
    std::stringstream errss;
    errss << "Failed to cast patient assessment writer";
    throw errss.str();
  }

}

// ----------------------------------------------------------------------------
// Destructor.
// Deletes the DataWriter, and the Communicator object
PatAssessmentWriterInterface::~PatAssessmentWriterInterface()
{
	DDS::Publisher *pub = _pat_assessment_writer->get_publisher();
	pub->delete_datawriter(_pat_assessment_writer);
	_pat_assessment_writer = NULL;

	delete _communicator;
}


// ----------------------------------------------------------------------------
// This writes the PatientAssessment data using RTI Connext DDS to any DataReader
// that shares the same Topic
bool PatAssessmentWriterInterface::Write(DdsAutoType<PatientAssessment> data)
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;
  DDS_InstanceHandle_t handle = DDS_HANDLE_NIL;

  // Note: not registering the instance since this demo is not making use of the keys
  retcode = _pat_assessment_writer->write(data, handle);

  if (retcode != DDS_RETCODE_OK) 
  {
    return false;
  }

  return true;

}

// ----------------------------------------------------------------------------
// Sends a deletion message for the patient assessment data. This uses the 
// unregister_instance call to notify other applications that this patient assessment
// has gone away and should be deleted
// 
bool PatAssessmentWriterInterface::Delete(DdsAutoType<PatientAssessment> data)
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;
  DDS_InstanceHandle_t handle = DDS_HANDLE_NIL;

  // Note that the deletion maps to an "unregister" in the RTI Connext
  // DDS world.  This allows the instance to be cleaned up entirely, 
  // so the space can be reused for another instance.  If you call
  // "dispose" it will not clean up the space for a new instance - 
  // instead it marks the current instance disposed and expects that you
  // might reuse the same instance again later.
  retcode = _pat_assessment_writer->unregister_instance(data, handle);

  if (retcode != DDS_RETCODE_OK)
  {
    return false;
  }

  return true;
}
