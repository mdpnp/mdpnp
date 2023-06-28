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

    