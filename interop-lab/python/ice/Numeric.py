from Time_t import Time_t

class Numeric:
    def __init__(self, ):
        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.value = 0
        self.device_time = Time_t()
        self.presentation_time = Time_t()

    def clear(self):
        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.value = 0
        if self.device_time != None: self.device_time.clear()
        if self.presentation_time != None: self.presentation_time.clear()

numeric = Numeric()

print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

numeric.device_time.sec = 9999999
numeric.device_time.nanosec = 100000

print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

numeric.clear()

print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

