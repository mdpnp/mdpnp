from ImageData import ImageData
import PIL.Image as Img # pip install pillow
import io
import tempfile

class Image:
    '''Class that stores all of the information of an OpenICE Image object'''

    def __init__(self):
        '''Initialises the content_type to an empty string and image to an empty ImageData object'''

        self.content_type = ""
        self.image = ImageData()
        self.temp = tempfile.NamedTemporaryFile()


    def clear(self):
        '''Clears all of the fields of the Image object back to the inital state'''

        self.content_type = ""
        if self.image != None: self.image.clear()


    def update_fields(self, dictionary):
        '''Updates the fields of the Image object by taking in a dictionary of all of the required fields\n
            Required Fields:\n
            content_type: string,\n
            image: list of integers from 0 to 255'''

        self.content_type = dictionary['content_type']
        self.image.update_data(dictionary['image'])


    def render(self):
        '''Turns the userData stored in the ImageData object into a png that is saved in a temporary file'''

        byte_string = bytearray(self.image.userData)
        self.rendered_image = Img.open(io.BytesIO(byte_string))
        self.image_path = f'{self.temp.name}.png'
        self.rendered_image.save(self.image_path)


    def set_image(self, image_path):
        '''Takes the relative or absolute path of an image (less than 65530 bytes) and converts it to a list of integers to be stored as userData in the ImageData object'''

        image = open(image_path, "rb")
        file = image.read()
        byte_string = bytearray(file)
        self.image.update_data(list(byte_string))


    def publish_fields(self):
        '''Returns a dictionary in a form that can be directly published to DDS'''

        publishing_dict = {}
        publishing_dict['content_type'] = self.content_type
        publishing_dict['image'] = self.image.publish_data()

        return publishing_dict