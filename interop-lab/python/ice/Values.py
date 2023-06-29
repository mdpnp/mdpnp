class Values:
    def __init__(self):
        self.userData = []

    def clear(self):
        if self.userData != None: self.userData = []

    def update_data(self, data):
        self.userData = data

    def publish_data(self):
        return self.userData