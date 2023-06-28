from ImageData import ImageData
import PIL.Image as Img # pip install pillow
import io

class Image:
    def __init__(self):
        self.content_type = ""
        self.image = ImageData()

    def clear(self):
        self.content_type = ""
        if self.image != None: self.image.clear()

    def update_fields(self, dictionary):
        self.content_type = dictionary['content_type']
        self.image.update_data(dictionary['image'])

    def render(self):
        byte_string = bytearray(self.image.userData)
        self.rendered_image = Img.open(io.BytesIO(byte_string))

