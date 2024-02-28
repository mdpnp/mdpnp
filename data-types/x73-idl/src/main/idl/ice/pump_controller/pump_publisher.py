
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

class ice_InfusionProgramPublisher:

    @staticmethod
    def run_publisher(domain_id: int, udi: str, rate: float):

        # A DomainParticipant allows an application to begin communicating in
        # a DDS domain. Typically there is one DomainParticipant per application.
        # DomainParticipant QoS is configured in USER_QOS_PROFILES.xml
        qos_provider = dds.QosProvider("ice_library.xml");
        participant = dds.DomainParticipant(domain_id, qos_provider.participant_qos)

        # A Topic has a name and a datatype.
        topic = dds.Topic(participant, ice.InfusionProgramTopic, ice.InfusionProgram, qos_provider.topic_qos_from_profile("ice_library::state"))

        # This DataWriter will write data on Topic "Example ice_InfusionProgram"
        # DataWriter QoS is configured in USER_QOS_PROFILES.xml
        writer = dds.DataWriter(participant.implicit_publisher, topic, qos_provider.datawriter_qos_from_profile("ice_library::state"))
        sample = ice.InfusionProgram()        

        # Catch control-C interrupt
        try:
            # Modify the data to be sent here
            sample.unique_device_identifier = udi
            sample.requestor = "Python Controller"
            sample.head = 1
            sample.infusionRate = rate
            sample.VTBI = -1
            sample.bolusVolume = -1
            sample.bolusRate = -1
            sample.seconds = -1
             
            print(f"Writing ice.InfusionProgram for {sample}")
            writer.write(sample)
            print("Press enter to exit")
            input()
        except KeyboardInterrupt:
            print("Interrupt by user...")

        print("preparing to shut down...")


if __name__ == "__main__":
    ice_InfusionProgramPublisher.run_publisher(
            domain_id=0, udi="SpUAIMJq5ZoH7SycuhMuUWyFDn471dA5uRpB", rate=5.0)
