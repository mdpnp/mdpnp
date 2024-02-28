
# (c) Copyright, Real-Time Innovations, 2022.  All rights reserved.
# RTI grants Licensee a license to use, modify, compile, and create derivative
# works of the software solely for use with RTI Connext DDS. Licensee may
# redistribute copies of the software provided that all such copies are subject
# to this license. The software is provided "as is", with no warranty of any
# type, including any warranty for fitness for any purpose. RTI is under no
# obligation to maintain or support the software. RTI shall not be liable for
# any incidental or consequential damages arising out of the use or inability
# to use the software.

import time
import sys
import rti.connextdds as dds
from ice import ice

class ice_DeviceIdentitySubscriber:

    #Initially empty list of pumps
    found_pump_udi=[]

    @staticmethod
    def process_data(reader):
        # take_data() returns copies of all the data samples in the reader
        # and removes them. To also take the SampleInfo meta-data, use take().
        # To not remove the data from the reader, use read_data() or read().
        samples = reader.take_data()
        for sample in samples:
            print(f"Received: {sample.unique_device_identifier} with manu {sample.manufacturer} and model {sample.model}")
            #ICE and model Controllable Pump
            if(sample.manufacturer=="ICE" and sample.model=="Controllable Pump"):
                print("Setting found_pump_udi...")
                ice_DeviceIdentitySubscriber.found_pump_udi.append(sample.unique_device_identifier)
    
        return len(samples)

    @staticmethod
    def get_udi(domain_id: int):

        # A DomainParticipant allows an application to begin communicating in
        # a DDS domain. Typically there is one DomainParticipant per application.
        # DomainParticipant QoS is configured in USER_QOS_PROFILES.xml
        qos_provider = dds.QosProvider("ice_library.xml");
        participant = dds.DomainParticipant(domain_id, qos_provider.participant_qos)

        # A Topic has a name and a datatype.
        topic = dds.Topic(participant, ice.DeviceIdentityTopic, ice.DeviceIdentity, qos_provider.topic_qos)

        # This DataReader reads data on Topic "Example ice_DeviceIdentity".
        # DataReader QoS is configured in USER_QOS_PROFILES.xml
        reader = dds.DataReader(participant.implicit_subscriber, topic, qos_provider.datareader_qos_from_profile("ice_library::device_identity"))

        # Initialize samples_read to zero
        samples_read = 0

        # Associate a handler with the status condition. This will run when the
        # condition is triggered, in the context of the dispatch call (see below)
        # condition argument is not used
        def condition_handler(_):
            nonlocal samples_read
            nonlocal reader
            samples_read += ice_DeviceIdentitySubscriber.process_data(reader)

        # Obtain the DataReader's Status Condition
        status_condition = dds.StatusCondition(reader)

        # Enable the "data available" status and set the handler.
        status_condition.enabled_statuses = dds.StatusMask.DATA_AVAILABLE
        status_condition.set_handler(condition_handler)

        # Create a WaitSet and attach the StatusCondition
        waitset = dds.WaitSet()
        waitset += status_condition

        #while found_pump_udi==None:
        counter=0
        while (len(ice_DeviceIdentitySubscriber.found_pump_udi)==0) or counter<10:
            # Catch control-C interrupt
            try:
                # Dispatch will call the handlers associated to the WaitSet conditions
                # when they activate
                print('Loop {counter} to get all pump devices - currently found {len}'.format(counter=counter,len=len(ice_DeviceIdentitySubscriber.found_pump_udi)))

                waitset.dispatch(dds.Duration(1))  # Wait up to 1s each time
                counter=counter+1
            except KeyboardInterrupt:
                break

        if(len(ice_DeviceIdentitySubscriber.found_pump_udi)==0):
            raise Exception("No pumps were found")
        return ice_DeviceIdentitySubscriber.found_pump_udi


if __name__ == "__main__":
    ice_DeviceIdentitySubscriber.run_subscriber(
            domain_id=0,
            sample_count=sys.maxsize)
