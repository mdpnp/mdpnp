/**
 * @file    PatAssessmentReaderInterface.h
 *
 * @brief   Declares a class which manages DDS entities for reading the PatientAsessement 
 * data type
 */
//=============================================================================

#ifndef PAT_ASSESSMENT_READER_INTERFACE_H
#define PAT_ASSESSMENT_READER_INTERFACE_H

#include <sstream>
#include <iostream>
#include "ndds/ndds_cpp.h"
#include "DDSCommunicator.h"
#include "DDSTypeWrapper.h"
#include "himss_2014_patassessment.h"
#include "himss_2014_patassessmentSupport.h"

class PatAssessmentReaderInterface 
{

public:

	// --- Constructor --- 
	// This will create a Domain Participant, Subscriber, Topic and Reader
    // for the PatientAssessment data.
	PatAssessmentReaderInterface(long domain_id);


	// --- Destructor --- 
	~PatAssessmentReaderInterface();

	// --- Receive Patient Assessment Data --- 
	// This method will simply continue to wait for PatientAssessment
    // reports using a DDS Waitset and write them to the console.
	void WaitForPatientAssessments();

private:

	// --- Private members ---

	// Used to create basic DDS entities that all applications need
	DDSCommunicator *_communicator;

	// patient assessment reader
	ice::PatientAssessmentDataReader *_pat_assessment_reader;

	// Objects to block a thread until patient assessment data arrives
	DDS::WaitSet *_waitSet;
	DDS::StatusCondition *_condition;
    DDSReadCondition *_instance_alive;
    DDSReadCondition *_instance_disposed;
    DDSReadCondition *_instance_no_writers;
};

#endif



