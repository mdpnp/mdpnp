class Time_t():
    '''Class that stores all of the information of an OpenICE Time_t object'''
    
    def __init__(self):
        '''Initialises all of the fields of the Time_t object to 0s'''
        
        self.sec = 0
        self.nanosec = 0


    def clear(self):
        '''Clears all of the fields of the Numeric object back to the inital state'''

        self.sec = 0
        self.nanosec = 0


    def update_fields(self, dictionary):
        '''Updates the fields of the Time_t object by taking in a dictionary of all of the required fields\n
            Required Fields:\n
            sec: int,\n
            nanosec: int'''

        self.sec = dictionary['sec']
        self.nanosec = dictionary['nanosec']

    
    def publish_fields(self):
        '''Returns a dictionary in a form that can be directly published to DDS'''

        publishing_dict = {}
        publishing_dict['sec'] = self.sec
        publishing_dict['nanosec'] = self.nanosec

        return publishing_dict