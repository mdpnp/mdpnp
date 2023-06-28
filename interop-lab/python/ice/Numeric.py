from Time_t import Time_t


# The Numeric class that stores all of the information of an OpenICE Numeric object

class Numeric:
    # Initialises all of the fields of the Numeric object as empty strings or 0s
    def __init__(self):
        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.value = 0
        self.device_time = Time_t()
        self.presentation_time = Time_t()


    # Clears all of the fields of the Numeric object
    def clear(self):
        self.unique_device_identifier = ""
        self.metric_id = ""
        self.vendor_metric_id = ""
        self.instance_id = 0
        self.unit_id = ""
        self.value = 0
        if self.device_time != None: self.device_time.clear()
        if self.presentation_time != None: self.presentation_time.clear()


    # Updates the fields of the Numeric object by taking in a dictionary of all of the required fields
    def update_fields(self, dictionary):
        self.unique_device_identifier = dictionary['unique_device_identifier']
        self.metric_id = dictionary['metric_id']
        self.vendor_metric_id = dictionary['vendor_metric_id']
        self.instance_id = dictionary['instance_id']
        self.unit_id = dictionary['unit_id']
        self.value = dictionary['value']
        self.device_time.update_fields(dictionary['device_time'])
        self.presentation_time.update_fields(dictionary['presentation_time'])

# Testing
if __name__ == "__main__":    
    numeric = Numeric()

    print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

    numeric.device_time.sec = 9999999
    numeric.device_time.nanosec = 100000

    print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

    numeric.clear()

    print(f"Device time is: {numeric.device_time.sec}s and {numeric.device_time.nanosec}")

