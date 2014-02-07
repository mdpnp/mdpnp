/**
 * @file    PatAssessmentWriterInterface.h
 *
 * @brief   Declares a class which manages DDS entities for writing the PatientAsessement 
 * data type
 */
//=============================================================================

#ifndef PAT_ASSESSMENT_WRITER_INTERFACE_H
#define PAT_ASSESSMENT_WRITER_INTERFACE_H

#include <sstream>
#include <iostream>
#include "ndds/ndds_cpp.h"
#include "DDSCommunicator.h"
#include "DDSTypeWrapper.h"
#include "himss_2014_patassessment.h"
#include "himss_2014_patassessmentSupport.h"

class PatAssessmentWriterInterface
{

public:

	// --- Constructor --- 
	// This will create a Domain Participant, Publisher, Topic and Writer
    // for the PatientAssessment data.
	PatAssessmentWriterInterface(long domain_id);
	//PatAssessmentWriterInterface();

	// --- Destructor --- 
	~PatAssessmentWriterInterface();

	// --- Getter for Communicator --- 
	// Accessor for the communicator (the class that sets up the basic
	// DDS infrastructure like the DomainParticipant).
	// This allows access to the DDS DomainParticipant/Publisher/Subscriber
	// classes
	DDSCommunicator *GetCommunicator() 
	{ 
		return _communicator; 
	}

	// --- Sends the patient assessment data ---
	bool Write(DdsAutoType<ice::PatientAssessment> data);

	// --- Deletes the patient assessment data ---
	bool Delete(DdsAutoType<ice::PatientAssessment> data);

private:
	// --- Private members ---

	// Used to create basic DDS entities that all applications need
	DDSCommunicator *_communicator;

	// patient assessment writer
	ice::PatientAssessmentDataWriter *_pat_assessment_writer;
};

#endif
