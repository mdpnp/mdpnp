import rti.connextdds as dds
from typing import Generic, TypeVar
from ice import ice_DeviceIdentity


T = TypeVar('T')
class ice_DataWriter(Generic[T]):
    """
    A Generic class that handles writing of data to DDS. 
    
    Generic on a Type `T`, which is an ice object defined in the `ice.idl` file
    """

    def __init__(self, participant: dds.DomainParticipant, topic_name: str, data_type: T) -> None:
        """
        Initialises a new `ice_DataWriter` instance

        :param participant: A DDS `DomainParticipant` object used to initialise the writer
        :param topic_name: The topic name of the data type being used
        :param data_type: An ice datatype class that the writer instance will be handling
        """

        qos_provider = dds.QosProvider("data-types/x73-idl-rti-dds/src/main/resources/META-INF/ice_library.xml")
        
        if data_type != ice_DeviceIdentity:
            writer_qos = qos_provider.datawriter_qos_from_profile("ice_library::default_profile")
            topic_qos = qos_provider.topic_qos_from_profile("ice_library::default_profile")

            pub_qos = qos_provider.publisher_qos_from_profile("ice_library::default_profile")
        else:
            writer_qos = qos_provider.datawriter_qos_from_profile("ice_library::device_identity")
            topic_qos = qos_provider.topic_qos_from_profile("ice_library::device_identity")
            pub_qos = qos_provider.publisher_qos_from_profile("ice_library::device_identity")
        
        publisher = dds.Publisher(participant, pub_qos)
        self.topic = dds.Topic(participant, topic_name, data_type, qos=topic_qos)
        self.writer = dds.DataWriter(publisher, self.topic, qos=writer_qos)


    def register_instance(self, data: T) -> dds.InstanceHandle:
        """
        Registers an instance on DDS
        
        :param data: The data to register on DDS
        """

        return self.writer.register_instance(data)


    def register_instance_w_timestamp(self, data: T, timestamp: dds.Time) -> dds.InstanceHandle:
        """
        Registers and instance on DDS with a timestamp

        :param data: The data to register on DDS
        :param timestamp: A `rti.connextdds.Time` instance containing the desired timestamp
        """

        params = dds.WriteParams()
        params.source_timestamp = timestamp
        return self.writer.register_instance(data, params)


    def register_instance_w_params(self, data: T, params: dds.WriteParams) -> dds.InstanceHandle:
        """
        Registers an instance on DDS with parameters

        :param data: The data to register on DDS
        :param params: A `rti.connextdds.WriteParams` instance containing the desired parameters 
        """

        return self.writer.register_instance(data, params)


    def unregister_instance(self, handle: dds.InstanceHandle) -> None:
        """
        Unregisters an instance from DDS

        :param handle: The `rti.connextdds.InstanceHandle` instance that is handling the instance you want to unregister
        """

        self.writer.unregister_instance(handle)


    def unregister_instance_w_timestamp(self, handle: dds.InstanceHandle, timestamp: dds.Time) -> None:
        """
        Unregisters an instance from DDS with a timestamp

        :param handle: The `rti.connextdds.InstanceHandle` instance that is handling the instance you want to unregister
        :param timestamp: The `rti.connextdds.Time` instance containing the desired timestamp
        """

        params = dds.WriteParams()
        params.source_timestamp = timestamp
        self.writer.unregister_instance(handle, params)


    def unregister_instance_w_params(self, handle: dds.InstanceHandle, params: dds.WriteParams) -> None:
        """
        Unregisters an instance from DDS with parameters

        :param handle: The `rti.connextdds.InstanceHandle` instance that is handling the instance you want to unregister
        :param params: The `rti.connextdds.WriteParams` instance containing the desired parameters
        """
                
        self.writer.unregister_instance(handle, params)


    def write(self, data: T, handle: dds.InstanceHandle) -> None:
        """
        Writes data to DDS by updating the DDS instance registered at the provided `InstanceHandle`

        :param data: An ice datatype instance that contains the data wanting to be published
        :param handle: A `rti.connextdds.InstanceHandle` instance representing an instance that has already been registered to DDS
        """

        self.writer.write(data, handle)


    def write_w_timestamp(self, data: T, handle: dds.InstanceHandle, timestamp: dds.Time) -> None:
        """
        Writes data to DDS with a timestamp by updating the DDS instance registered at the provided `InstanceHandle`

        :param data: An ice datatype instance that contains the data wanting to be published
        :param handle: A `rti.connextdds.InstanceHandle` instance representing an instance that has already been registered to DDS
        :param timestamp: A `rti.connextdds.Time` instance containing the desired timestamp
        """

        params = dds.WriteParams()
        params.source_timestamp = timestamp
        self.writer.write(data, handle, params)


    def write_w_params(self, data: T, params: dds.WriteParams) -> None:
        """
        Writes data to DDS with parameters by updating the DDS instance registered at the provided `InstanceHandle`

        :param data: An ice datatype instance that contains the data wanting to be published
        :param params: A `rti.connextdds.WriteParams` instance containing the desired parameters
        """
                
        self.writer.write(data, params)


    def dispose(self, handle: dds.InstanceHandle) -> None:
        """
        Disposes of an instance on DDS

        :param handle: The `rti.connextdds.InstanceHandle` instance representing the instance on DDS you want to dispose of
        """

        self.writer.dispose_instance(handle)


    def dispose_w_timestamp(self, handle: dds.InstanceHandle, timestamp: dds.Time) -> None:
        """
        Disposes of an instance on DDS with a timestamp

        :param handle: The `rti.connextdds.InstanceHandle` instance representing the instance on DDS you want to dispose of
        :param timestamp: An `rti.connextdds.Time` instance containing the desired timestamp
        """

        self.writer.dispose_instance(handle, timestamp)


    def dispose_w_params(self, params: dds.WriteParams) -> None:
        """
        Disposes of an instance on DDS with parameters
        
        :param params: An `rti.connextdds.WriteParams` instance containing the desired parameters
        """

        self.writer.dispose_instance(params)


    def get_key_value(self, handle: dds.InstanceHandle) -> T:
        """
        Retrieves the instance key that corresponds to the provided InstanceHandle

        :param handle: The `rti.connextdds.InstanceHandle` instance representing the instance on DDS you want to query
        """

        return self.writer.key_value(handle)


    def lookup_instance(self, key_holder: T) -> dds.InstanceHandle:
        """
        Retrieves the `InstanceHandle` that correstponds to an instance key_holder

        :param key_holder: The instance key_holder to retrieve the handle of
        """

        return self.writer.lookup_instance(key_holder)
    
