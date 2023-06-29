from Time_t import Time_t
from Values import Values

class SampleArray:
    def __init__(self):
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
        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.frequency = 0
        if self.values != None: self.values.clear()
        if self.device_time != None: self.device_time.clear()
        if self.presentation_time != None: self.presentation_time.clear()

    # Updates the fields of the Numeric object by taking in a dictionary of all of the required fields
    def update_fields(self, dictionary):
        self.unique_device_identifier = dictionary['unique_device_identifier']
        self.metric_id = dictionary['metric_id']
        self.vendor_metric_id = dictionary['vendor_metric_id']
        self.instance_id = dictionary['instance_id']
        self.unit_id = dictionary['unit_id']
        self.frequency = dictionary['frequency']
        self.values.update_data(dictionary['values'])
        self.device_time.update_fields(dictionary['device_time'])
        self.presentation_time.update_fields(dictionary['presentation_time'])
