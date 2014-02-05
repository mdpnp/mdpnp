/*********************************************************************************************
(c) 2005-2013 Copyright, Real-Time Innovations, Inc.  All rights reserved.    	                             
RTI grants Licensee a license to use, modify, compile, and create derivative works 
of the Software.  Licensee has the right to distribute object form only for use with RTI 
products.  The Software is provided �as is�, with no warranty of any type, including 
any warranty for fitness for any purpose. RTI is under no obligation to maintain or 
support the Software.  RTI shall not be liable for any incidental or consequential 
damages arising out of the use or inability to use the software.
**********************************************************************************************/
#include "DDSCommunicator.h"

using namespace DDS;

// ------------------------------------------------------------------------- //
// Destruction of a DDS communication interface.  This first deletes all the
// entities created by the DomainParticipant object.  Then, it cleans up the 
// types that have been registered with the DomainParticipant.  (This is not
// strictly necessary, but will cause a very small memory leak at shutdown if
// all types are not unregistered.  Thirdly, this deletes the 
// DomainParticipant.  Lastly, this finalizes the DomainParticipantFactory.
DDSCommunicator::~DDSCommunicator() 
{
	if (_participant != NULL) 
	{

		// Delete DataWriters, DataReaders, Topics, Subscribers, and Publishers
		// created by this DomainParticipant.
		_participant->delete_contained_entities();

		// Unregister all the data types registered with this DomainParticipant
		for (std::map<std::string, 
			UnregisterInfo>::iterator it =
			_typeCleanupFunctions.begin();
				it != _typeCleanupFunctions.end();  it++)
		{
			(*it).second.unregisterFunction(_participant, (
				*it).first.c_str());
		}

		// Delete the DomainParticipant
		TheParticipantFactory->delete_participant(_participant);

		// You finalize the participant factory here, but this
		// will not work if you have multiple communicators - for example
		// in different DDS domains.  In that case, you must be more careful
		// to only finalize the participant factory after all 
		// DomainParticipants have been deleted.
		TheParticipantFactory->finalize_instance();
	}
}

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
// For more information on participant QoS, see the USER_QOS_PROFILES.xml
// file

// ------------------------------------------------------------------------- //
// Creating a DomainParticipant with a specified domain ID  
DomainParticipant* DDSCommunicator::CreateParticipant(long domain) 
{
	_participant =
		TheParticipantFactory->create_participant(domain, 
		PARTICIPANT_QOS_DEFAULT, NULL, STATUS_MASK_NONE);

	if (_participant == NULL)
	{
		std::stringstream errss;
		errss << "Failed to create DomainParticipant object";
		throw errss.str();
	} 

	return _participant;
}

// ------------------------------------------------------------------------- //
// Creating a DomainParticipant with a domain ID of zero
DomainParticipant* DDSCommunicator::CreateParticipant() 
{
	_participant = 
		TheParticipantFactory->create_participant(
									0, 
									PARTICIPANT_QOS_DEFAULT, 
									NULL, STATUS_MASK_NONE);

	if (_participant == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create DomainParticipant object";
		throw errss.str();
	} 

	return _participant;
}


// ------------------------------------------------------------------------- //
// Creating a DomainParticipant with a specified domain ID and specified QoS 
DomainParticipant* DDSCommunicator::CreateParticipant(
	long domain, 
	const std::string &participantQosLibrary, 
	const std::string &participantQosProfile) 
{
	_participant = 
		TheParticipantFactory->create_participant_with_profile(
									domain, 
									participantQosLibrary.c_str(), 
									participantQosProfile.c_str(), 
									NULL, 
									STATUS_MASK_NONE);

	if (_participant == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create DomainParticipant object";
		throw errss.str();
	} 

	return _participant;

}


// ------------------------------------------------------------------------- //
// Creating a DomainParticipant with a specified domain ID, specified QoS file
// names, and specified QoS 
DomainParticipant* DDSCommunicator::CreateParticipant(long domain, 
	std::vector<std::string>fileNames, 
	const std::string &participantQosLibrary, 
	const std::string &participantQosProfile) 
{

	// Adding a list of explicit file names to the DomainParticipantFactory
	// This gives the middleware a set of places to search for the files
	DomainParticipantFactoryQos factoryQos;
	TheParticipantFactory->get_qos(factoryQos);
	factoryQos.profile.url_profile.ensure_length(fileNames.size(),
												fileNames.size());

	for (unsigned int i = 0; i < fileNames.size(); i++) 
	{
		// Note that we copy the file names here, so they cannot go out of 
		// scope
		factoryQos.profile.url_profile[i] = DDS_String_dup(
			fileNames[i].c_str());
	}

	DDS_ReturnCode_t retcode = TheParticipantFactory->set_qos(factoryQos);
		
	if (retcode != DDS_RETCODE_OK) 
	{
		std::stringstream errss;
		errss << "Failed to create DomainParticipant object";
		throw errss.str();
	}

	// Actually creating the DomainParticipant
	_participant = 
		TheParticipantFactory->create_participant_with_profile(
									domain, 
									participantQosLibrary.c_str(), 
									participantQosProfile.c_str(), 
									NULL, 
									STATUS_MASK_NONE);

	if (_participant == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create DomainParticipant object";
		throw errss.str();
	} 

	return _participant;

}


// ------------------------------------------------------------------------- //
// Creating a Publisher object.  This is used to create type-specific 
// DataWriter objects in the application
Publisher* DDSCommunicator::CreatePublisher()
{
	if (GetParticipant() == NULL) 
	{
		std::cout << "create publisher - get participant returned null" << std::endl;
		std::stringstream errss;
		errss << 
			"DomainParticipant NULL - communicator not properly " << 
				"initialized";
		throw errss.str();
	}

	// Creating a Publisher.  
	// This object is used to create type-specific DataWriter objects that 
	// can actually send data.  
	// 
	_pub = GetParticipant()->create_publisher(
									DDS_PUBLISHER_QOS_DEFAULT, 
									NULL, DDS_STATUS_MASK_NONE);	

	if (_pub == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create Publisher";
		throw errss.str();
	}

	return _pub;
}

// ------------------------------------------------------------------------- //
// Creating a Publisher object with specified QoS.  This is used to create 
// type-specific DataWriter objects in the application
Publisher* DDSCommunicator::CreatePublisher(
	const std::string &qosLibrary, 
	const std::string &qosProfile)
{
	if (GetParticipant() == NULL) 
	{
		std::stringstream errss;
		errss << 
			"DomainParticipant NULL - communicator not properly " << 
				"initialized";
		throw errss.str();
	}

	// Creating a Publisher.  
	// This object is used to create type-specific DataWriter objects that 
	// can actually send data.  
	// 
	_pub = GetParticipant()->create_publisher_with_profile(
						qosLibrary.c_str(), 
						qosProfile.c_str(),
						NULL, DDS_STATUS_MASK_NONE);	

	if (_pub == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create Publisher";
		throw errss.str();
	}

	return _pub;
}


// ------------------------------------------------------------------------- //
// Creating a Subscriber object.  This is used to create type-specific 
// DataReader objects in the application
Subscriber* DDSCommunicator::CreateSubscriber()
{
	if (GetParticipant() == NULL) 
	{
		std::stringstream errss;
		errss << 
			"DomainParticipant NULL - communicator not properly " << 
				"initialized";
		throw errss.str();
	}

	// Creating a Subscriber.  
	// This object is used to create type-specific DataReader objects that 
	// can actually receive data.  The Subscriber object is being created
	//  in the DDSCommunicator class because one Subscriber can be used to
	//  create multiple DDS DataReaders. 
	// 
	_sub = GetParticipant()->create_subscriber(
								DDS_SUBSCRIBER_QOS_DEFAULT, 
								NULL, DDS_STATUS_MASK_NONE);	

	if (_sub == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create Subscriber";
		throw errss.str();
	}

	return _sub;
}

// ------------------------------------------------------------------------- //
// Creating a Subscriber object with specified QoS.  This is used to create 
// type-specific DataReader objects in the application
Subscriber* DDSCommunicator::CreateSubscriber(
	const std::string &qosLibrary,
	const std::string &qosProfile)
{
	if (GetParticipant() == NULL) 
	{
		std::stringstream errss;
		errss << 
			"DomainParticipant NULL - communicator not properly " <<
				"initialized";
		throw errss.str();
	}

	// Creating a Subscriber.  
	// This object is used to create type-specific DataReader objects that 
	// can actually receive data.  The Subscriber object is being created
	//  in the DDSCommunicator class because one Subscriber can be used to
	//  create multiple DDS DataReaders. 
	// 
	_sub = GetParticipant()->create_subscriber_with_profile(
						qosLibrary.c_str(), 
						qosProfile.c_str(), 
						NULL, DDS_STATUS_MASK_NONE);	
	if (_sub == NULL) 
	{
		std::stringstream errss;
		errss << "Failed to create Subscriber";
		throw errss.str();
	}

	return _sub;

}
