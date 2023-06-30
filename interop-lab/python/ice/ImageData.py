class ImageData:
    '''Class that stores the image data for an Image object'''

    def __init__(self):
        '''Initialises userData to an empty list'''

        self.userData = []


    def clear(self):
        '''Clears userData back to the inital state'''

        if self.userData != None: self.userData = []


    def update_data(self, data):
        '''Updates the userData by taking a list of integers (0-255)'''

        self.userData = data


    def publish_data(self):
        '''Returns the userData as a list of integers'''

        return self.userData