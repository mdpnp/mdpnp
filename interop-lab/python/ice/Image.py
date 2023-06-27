from ImageData import ImageData

class Image:
    def __init__(self):
        self.content_type = ""
        self.image = ImageData()

    def clear(self):
        self.content_type = ""
        if self.image != None: self.image.clear()