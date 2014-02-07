/*********************************************************************************************
(c) 2005-2013 Copyright, Real-Time Innovations, Inc.  All rights reserved.    	                             
RTI grants Licensee a license to use, modify, compile, and create derivative works 
of the Software.  Licensee has the right to distribute object form only for use with RTI 
products.  The Software is provided �as is�, with no warranty of any type, including 
any warranty for fitness for any purpose. RTI is under no obligation to maintain or 
support the Software.  RTI shall not be liable for any incidental or consequential 
damages arising out of the use or inability to use the software.
**********************************************************************************************/
#ifndef DDS_COMMUNICATOR_H
#define DDS_COMMUNICATOR_H

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>
#include <vector>
#include <map>
#include "ndds/ndds_cpp.h"
#include "ndds/ndds_namespace_cpp.h"


// ------------------------------------------------------------------------- //
// Function that is used to unregister types from the DomainParticipant.
// types are automatically registered when you create the topic, and 
// unregistered at shutdown.

typedef DDS_ReturnCode_t (*unregister_fn)(DDSDomainParticipant *, const char *);

// ------------------------------------------------------------------------- //
// 
// UnregisterInfo:
// This structure maintains a mapping between a data type and its unregister
// function, which can be used at shutdown when cleaning up the the 
// DomainParticipant and all of its registered data types.
//
// ------------------------------------------------------------------------- //
struct UnregisterInfo 
{
  std::string typeName;
  unregister_fn unregisterFunction;
};

// ------------------------------------------------------------------------- //
//
// DDSCommunicator:
// This class is used by all RTI Connext DDS interfaces to create the core
// communication objects, such as a DomainParticipant, Publisher and/or 
// Subscriber.
//
// ------------------------------------------------------------------------- //
class DDSCommunicator 
{

public:
	// --- Constructor and Destructor --- 
	DDSCommunicator() : _participant(NULL), _pub(NULL), _sub(NULL)
	{}

	~DDSCommunicator();

	// --- Creating a DomainParticipant --- 

	// A DomainParticipant starts the DDS discovery process.  It creates
	// several threads, sends and receives discovery information over one or 
	// more transports, and maintains an in-memory discovery database of 
	// remote DomainParticipants, remote DataWriters, and remote DataReaders

	// Quality of Service can be applied on the level of the DomainParticipant.  
	// This QoS controls the characteristics of:
	// 1. Transport properties such as which type of network (UDPv4, UDPv6, 
	//    shared memory) or which network interfaces it is allowed to use
	// 2. Which applications this discovers.  By default, apps will discover
	//    other DDS applications over multicast, loopback, and shared memory.
	// 3. Resource limits for the DomainParticipant
	//
	// For more information on participant QoS, see the .xml files in the 
	// Config directory

	// Creates a DomainParticipant with default QoS in domain 0
	DDS::DomainParticipant* CreateParticipant();

	// Creates a DomainParticipant with default QoS in the specified domain
	DDS::DomainParticipant* CreateParticipant(long domain);

	// Creates a DomainParticipant with specified QoS in the specified domain
	DDS::DomainParticipant* CreateParticipant(long domain, 
		const std::string &participantQosLibrary, 
		const std::string &participantQosProfile);

	// Loads a set of XML files to load QoS profile information
	// Creates a DomainParticipant with specified QoS in the specified domain
	DDS::DomainParticipant* CreateParticipant(long domain, 
		std::vector<std::string>fileNames, 
		const std::string &participantQosLibrary, 
		const std::string &participantQosProfile) ;

	// --- Getting the DomainParticipant --- 

	// Returns the DomainParticipant created by the Communicator.
	DDS::DomainParticipant* GetParticipant() 
	{
		return _participant;
	}

	// --- Creating a Publisher --- 

	// The Publisher object is a factory for creating type-specific DataWriters
	// Note that the same publisher can be used for multiple DataWriters - 
	// typically you will create zero or one Publisher per application.

	// Create a Publisher with default QoS
	DDS::Publisher* CreatePublisher();

	// Create a Publisher with specified QoS
	DDS::Publisher* CreatePublisher(const std::string &qosLibrary, 
		const std::string &qosProfile);

	// --- Getting the Publisher --- 

	// Returns the Publisher created by the Communicator.
	DDS::Publisher* GetPublisher() 
	{
		return _pub;
	}

	// --- Creating a Subscriber --- 

	// The Subscriber object is a factory for creating type-specific 
	// DataReaders.
	// Note that the same subscriber can be used for multiple DataReaders - 
	// typically you will create zero or one Subscriber per application.

	// Create a Subscriber with default QoS
	DDS::Subscriber* CreateSubscriber();

	// Create a Subscriber with specified QoS
	DDS::Subscriber* CreateSubscriber(const std::string &qosLibrary, 
		const std::string &qosProfile);
	
	// --- Getting the Subscriber --- 

	// Returns the Publisher created by the Communicator.
	DDS::Subscriber* GetSubscriber() 
	{
		return _sub;
	}

	// --- Create a Topic --- 

	// Creates a Topic.  Templatized with the type name to 
	// allow storage and deletion of the data type at 
	// shutdown.
	template <typename T>
	DDS::Topic *CreateTopic(std::string topicName)
	{
		// Register the data type with the DomainParticipant - this
		// tells the DomainParticipant how to create/destroy/
		// serialize/deserialize this data type.
		const char *typeName = T::TypeSupport::get_type_name();

		DDS_ReturnCode_t retcode = T::TypeSupport::register_type(
				GetParticipant(), typeName);
		if (retcode != DDS_RETCODE_OK) 
		{
			std::stringstream errss;
			errss << "Failure to register type. Regisetered twice?";
			throw errss.str();
		}

		// Create the Topic object, using the associated data type that
		// was registered above.
		DDS::Topic *topic = GetParticipant()->create_topic(
			topicName.c_str(),
			typeName, DDS_TOPIC_QOS_DEFAULT, NULL /* listener */,
			DDS_STATUS_MASK_NONE);
		if (topic == NULL) 
		{
			std::stringstream errss;
			errss << "CreateTopic(): failure to create Topic. Created twice?";
			throw errss.str();
		}

		// Save the type unregister information for a clean shutdown.  This is
		// not strictly necessary, but prevents what looks like a memory leak
		// at shutdown.
		UnregisterInfo unregisterInfo;
		unregisterInfo.typeName = typeName;
		unregisterInfo.unregisterFunction = 
			T::TypeSupport::unregister_type;
		_typeCleanupFunctions[typeName] = unregisterInfo;

		return topic;
	}

private:

	// --- Private members ---

	// Used to create other DDS entities
	DDS::DomainParticipant* _participant;

	// Used to create DataWriters
	DDS::Publisher* _pub;

	// Used to create DataReaders
	DDS::Subscriber* _sub;

	// Map between type names and unregistration functions.  Functions are 
	// added to this in CreateTopic(), after each data type is registered 
	// with the DomainParticipant.  This cleans up a small amount of memory
	// that would otherwise appear as a memory leak at shutdown.
	std::map<std::string, UnregisterInfo> _typeCleanupFunctions;


};

#endif
