import rticonnextdds_connector as rti
import time

from Time_t import Time_t
from Values import Values

# Setting up API publishing Connection
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")#
sampleArrayOutput = connector.get_output("SampleArrayPublisher::SampleArrayWriter")

class SampleArray:
    '''Class that stores all of the information of an OpenICE SampleArray object'''

    def __init__(self):
        '''Initialises all of the fields of the SampleArray object as empty strings or 0s'''

        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.frequency = 0
        self.values = Values()
        self.device_time = Time_t()
        self.presentation_time = Time_t()


    def clear(self):
        '''Clears all of the fields of the SampleArray object back to the inital state'''

        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.frequency = 0
        if self.values != None: self.values.clear()
        if self.device_time != None: self.device_time.clear()
        if self.presentation_time != None: self.presentation_time.clear()


    def update_fields(self, dictionary):
        '''Updates the fields of the SampleArray object by taking in a dictionary of all of the required fields\n
            Required Fields:\n
            unique_device_identifier: string,\n
            metric_id: string,\n
            vendor_metric_id: string,\n
            instance_id: int,\n
            unit_id: string,\n
            frequency: int,\n
            values: list of values,\n
            device_time: dictionary containing sec: int and nanosec: int,\n
            presentation_time: dictionary containing sec: int and nanosec: int'''
        
        self.unique_device_identifier = dictionary['unique_device_identifier']
        self.metric_id = dictionary['metric_id']
        self.vendor_metric_id = dictionary['vendor_metric_id']
        self.instance_id = dictionary['instance_id']
        self.unit_id = dictionary['unit_id']
        self.frequency = dictionary['frequency']
        self.values.update_data(dictionary['values'])
        self.device_time.update_fields(dictionary['device_time'])
        self.presentation_time.update_fields(dictionary['presentation_time'])


    def publish_fields(self):
        '''Returns a dictionary in a form that can be directly published to DDS'''
        
        publishing_dict = {}
        publishing_dict['unique_device_identifier'] = self.unique_device_identifier
        publishing_dict['metric_id'] = self.metric_id
        publishing_dict['vendor_metric_id'] = self.vendor_metric_id
        publishing_dict['instance_id'] = self.instance_id
        publishing_dict['unit_id'] = self.unit_id
        publishing_dict['frequency'] = self.frequency
        publishing_dict['values'] = self.values.publish_data()
        publishing_dict['device_time'] = self.device_time.publish_fields()
        publishing_dict['presentation_time'] = self.device_time.publish_fields()

        return publishing_dict
    

    def publish_to_dds(self):
        '''Publishes the data stored in the SampleArray object to DDS via the API'''

        current_time = str(time.time())
        current_time = current_time.split('.')
        
        self.presentation_time.sec = int(current_time[0])
        self.presentation_time.nanosec = int(current_time[1])

        sampleArrayOutput.instance.set_dictionary(self.publish_fields())
        sampleArrayOutput.write()
