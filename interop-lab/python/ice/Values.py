class Values:
    '''Class that stores all of the information of an OpenICE Values object'''

    def __init__(self):
        '''Initialises userData to an empty list'''
        
        self.userData = []


    def clear(self):
        '''Clears userData back to the inital state'''

        if self.userData != None: self.userData = []


    def update_data(self, data):
        '''Updates userData by taking in a list of values'''

        self.userData = data


    def publish_data(self):
        '''Returns a userData as a list'''

        return self.userData