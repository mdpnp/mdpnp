class ImageData:
    def __init__(self):
        self.userData = []

    def clear(self):
        if self.userData != None: self.userData = []

    def update_data(self, data):
        self.userData = data