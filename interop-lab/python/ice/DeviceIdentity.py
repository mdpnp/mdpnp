from Image import Image

class DeviceIdentity:
    def __init__(self):
        self.unique_device_identifier = ""
        self.manufacturer = ""
        self.model = ""
        self.serial_number = ""
        self.icon = Image()
        self.build = ""
        self.operating_system = ""

    def clear(self):
        self.unique_device_identifier = ""
        self.manufacturer = ""
        self.model = ""
        self.serial_number = ""
        if self.icon != None: self.icon.clear()
        self.build = ""
        self.operating_system = ""

    def update_fields(self, dictionary):
        self.unique_device_identifier = dictionary['unique_device_identifier']
        self.manufacturer = dictionary['manufacturer']
        self.model = dictionary['model']
        self.serial_number = dictionary['serial_number']
        self.icon.update_fields(dictionary['icon'])
        self.build = dictionary['build']
        self.operating_system = dictionary['operating_system']

    def set_image(self, image_path):
        self.icon.set_image(image_path)

    def publish_fields(self):
        publishing_dict = {}
        publishing_dict['unique_device_identifier'] = self.unique_device_identifier
        publishing_dict['manufacturer'] = self.manufacturer
        publishing_dict['model'] = self.model
        publishing_dict['serial_number'] = self.serial_number
        publishing_dict['icon'] = self.icon.publish_fields()
        publishing_dict['build'] = self.build
        publishing_dict['operating_system'] = self.operating_system

        return publishing_dict