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
