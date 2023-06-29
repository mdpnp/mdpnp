# The Time_t class that stores sec and nanosec for the Numeric Objects

class Time_t():
    # Initialises the fields to 0
    def __init__(self):
        self.sec = 0
        self.nanosec = 0


    # Clears the fields of the Time_t object to 0
    def clear(self):
        self.sec = 0
        self.nanosec = 0


    # Updates the fields of the Numeric object by taking in a dictionary of all of the required fields
    def update_fields(self, dictionary):
        self.sec = dictionary['sec']
        self.nanosec = dictionary['nanosec']

    
    def publish_fields(self):
        publishing_dict = {}
        publishing_dict['sec'] = self.sec
        publishing_dict['nanosec'] = self.nanosec

        return publishing_dict