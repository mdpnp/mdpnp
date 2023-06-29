from ImageData import ImageData
import PIL.Image as Img # pip install pillow
import io
import tempfile

class Image:
    def __init__(self):
        self.content_type = ""
        self.image = ImageData()
        self.temp = tempfile.NamedTemporaryFile()

    def clear(self):
        self.content_type = ""
        if self.image != None: self.image.clear()

    def update_fields(self, dictionary):
        self.content_type = dictionary['content_type']
        self.image.update_data(dictionary['image'])

    def render(self):
        byte_string = bytearray(self.image.userData)
        self.rendered_image = Img.open(io.BytesIO(byte_string))
        self.image_path = f'{self.temp.name}.png'
        self.rendered_image.save(self.image_path)

    def set_image(self, image_path):
        image = open(image_path, "rb")
        file = image.read()
        byte_string = bytearray(file)
        self.image.update_data(list(byte_string))

    def publish_fields(self):
        publishing_dict = {}
        publishing_dict['content_type'] = self.content_type
        publishing_dict['image'] = self.image.publish_data()

        return publishing_dict


# Testing
if __name__ == '__main__':
    img = Image()
    img.set_image('interop-lab/python/ice/pythonlogo.png')
    print(img.image.userData)