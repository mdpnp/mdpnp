import rticonnextdds_connector as rti

from Image import Image

# Setting up API publishing Connection
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")#
deviceIdentityOutput = connector.get_output("DeviceIdentityPublisher::DeviceIdentityWriter")

class DeviceIdentity:
    '''Class that stores all of the information of an OpenICE DeviceIdentity object'''

    def __init__(self):
        '''Initialises all of the fields of the DeviceIdentity object as empty strings'''

        self.unique_device_identifier = ""
        self.manufacturer = ""
        self.model = ""
        self.serial_number = ""
        self.icon = Image()
        self.build = ""
        self.operating_system = ""


    def clear(self):
        '''Clears all of the fields of the DeviceIdentity object back to the initial state'''

        self.unique_device_identifier = ""
        self.manufacturer = ""
        self.model = ""
        self.serial_number = ""
        if self.icon != None: self.icon.clear()
        self.build = ""
        self.operating_system = ""


    def update_fields(self, dictionary):
        '''Updates the fields of the DeviceIdentity object by taking in a dictionary of all of the required fields\n
            Required Fields:\n
            unique_device_identifier: string,\n
            manufacturer: string,\n
            model: string,\n
            serial_number: string,\n
            icon: dictionary containing content_type: string and image: list of integers 0 to 255,\n
            build: string,\n
            operating_system: string'''
        
        self.unique_device_identifier = dictionary['unique_device_identifier']
        self.manufacturer = dictionary['manufacturer']
        self.model = dictionary['model']
        self.serial_number = dictionary['serial_number']
        self.icon.update_fields(dictionary['icon'])
        self.build = dictionary['build']
        self.operating_system = dictionary['operating_system']


    def set_image(self, image_path):
        '''Takes an image path and loads as the object's "icon"'''

        self.icon.set_image(image_path)


    def publish_fields(self):
        '''Returns a dictionary in a form that can be directly published to DDS'''

        publishing_dict = {}
        publishing_dict['unique_device_identifier'] = self.unique_device_identifier
        publishing_dict['manufacturer'] = self.manufacturer
        publishing_dict['model'] = self.model
        publishing_dict['serial_number'] = self.serial_number
        publishing_dict['icon'] = self.icon.publish_fields()
        publishing_dict['build'] = self.build
        publishing_dict['operating_system'] = self.operating_system

        return publishing_dict
    

    def publish_to_dds(self):
        '''Publishes the data stored in the DeviceIdentity object to DDS via the API'''

        deviceIdentityOutput.instance.set_dictionary(self.publish_fields())
        deviceIdentityOutput.write()
        deviceIdentityOutput.wait()