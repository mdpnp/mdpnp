import rti.connextdds as dds
from typing import Optional, TypeVar, Generic
from ice import ice_DeviceIdentity


T = TypeVar('T')
class ice_DataReader(Generic[T]):
    """
    A Generic class that handles reading of data from DDS. 
    
    Generic on a Type `T`, which is an ice object defined in the `ice.idl` file
    """

    def __init__(self, participant: dds.DomainParticipant, topic_name: str, data_type: T) -> None:
        """
        Initialises a new `ice_DataReader` instance

        :param participant: A DDS `DomainParticipant` object used to initialise the reader
        :param topic_name: The topic name of the data type being used
        :param data_type: An ice datatype class that the reader instance will be handling
        """

        qos_provider = dds.QosProvider("python-driver-kit/USER_QOS_PROFILES.xml")
        reader_qos = qos_provider.datareader_qos_from_profile("ice_Library::ice_Profile")
        topic_qos = qos_provider.topic_qos_from_profile("ice_Library::ice_Profile")

        sub_qos = qos_provider.subscriber_qos_from_profile("ice_Library::ice_Profile")

        qos_provider = dds.QosProvider("data-types/x73-idl-rti-dds/src/main/resources/META-INF/ice_library.xml")
        
        if data_type != ice_DeviceIdentity:
            reader_qos = qos_provider.datareader_qos_from_profile("ice_library::default_profile")
            topic_qos = qos_provider.topic_qos_from_profile("ice_library::default_profile")

            sub_qos = qos_provider.subscriber_qos_from_profile("ice_library::default_profile")
        else:
            reader_qos = qos_provider.datareader_qos_from_profile("ice_library::device_identity")
            topic_qos = qos_provider.topic_qos_from_profile("ice_library::device_identity")
            sub_qos = qos_provider.subscriber_qos_from_profile("ice_library::device_identity")
        
        subscriber = dds.Subscriber(participant, sub_qos)
        self.topic = dds.Topic(participant, topic_name, data_type, qos=topic_qos)
        self.reader = dds.DataReader(subscriber, self.topic, qos=reader_qos)


    def read(self):
        return self.reader.read()
    

    def read_w_condition(self, condition: dds.ReadCondition):
        """
        Reads data from DDS with a provided `ReadCondition`
        
        :param condition: The `ReadCondition` providing the conditions of the read
        """
        
        return self.reader.select().state(condition.state_filter).read()


    def get_key_value(self, handle: dds.InstanceHandle) -> T:
        """
        Gets the key value for a given instance handle.

        :param handle: `InstanceHandle` identifying the instance
        """

        return self.reader.key_value(handle)


    def lookup_instance(self, key_holder: T) -> dds.InstanceHandle:
        """
        Looks up an instance by key value.

        :param key_holder: Instance containing the key
        :return: InstanceHandle of the matching instance
        """

        return self.reader.lookup_instance(key_holder)
