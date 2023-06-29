import rticonnextdds_connector as rti
from DeviceIdentity import DeviceIdentity

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("DeviceIdentitySubscriber::DeviceIdentityReader")

class DeviceIdentityDict:
    # Initialises the DeviceIdentityDict class by creating a deviceIdentityDict dictionary
    def __init__(self):
        self.deviceIdentityDict = {}
    

    # Clears the deviceIdentityDict dictionary
    def clear(self):
        if self.deviceIdentityDict != None: self.deviceIdentityDict = {}


    # Uses an API call via DDS to obtain all of the Device Identities being published over the domain and stores them in the numericDict dictionary
    def update(self):
        input.take()
        numOfSamples = input.samples.getLength()
        
        if numOfSamples > 0:
            for j in range (0, numOfSamples):         
                if input.infos.isValid(j):             
                    deviceIdentity = input.samples.getDictionary(j)
                    udi = input.samples.getString(j, "unique_device_identifier") 
                    
                    currentDeviceIdentity = DeviceIdentity()
                    currentDeviceIdentity.update_fields(deviceIdentity)
                    self.deviceIdentityDict[udi] = currentDeviceIdentity


    # Returns a list of Numerics that fit the supplied conditions
    def fetch(self, udi = None):
        deviceIdentities = []
        
        try:
            # Fetches all Numerics with matching UDI
            if udi != None:                               
                deviceIdentities.append(self.deviceIdentityDict[udi])
            
            # Fetches all Numerics
            else:
                for udi in self.deviceIdentityDict:
                    deviceIdentities.append(self.deviceIdentityDict[udi])

        except:
            print('Key provided not found in NumericDict')

        return deviceIdentities


# Testing
if __name__ == '__main__':
    current_dict = DeviceIdentityDict()

    while True:
        current_dict.update()
        if len(current_dict.deviceIdentityDict) > 0:
            print('Fetched')
            print(current_dict.fetch()[0].icon.image.userData)

#kUaeOTPiEIPG51wur3ioqCwCBYSRSn3mpsSV