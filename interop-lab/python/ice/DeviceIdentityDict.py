import rticonnextdds_connector as rti
from DeviceIdentity import DeviceIdentity

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("DeviceIdentitySubscriber::DeviceIdentityReader")

class DeviceIdentityDict:
    '''Class that stores a dictionary of all DeviceIdentities recieved from DDS, keyed by UDI'''
    
    def __init__(self):
        '''Initialises the DeviceIdentityDict class by creating an empty dictionary'''

        self.deviceIdentityDict = {}
    

    def clear(self):
        '''Clears the DeviceIdentity dictionary back to the inital state'''

        if self.deviceIdentityDict != None: self.deviceIdentityDict = {}


    def update(self):
        '''Uses an API call via DDS to obtain all of the DeviceIdentities being published over the domain and stores them in the DeviceIdentity dictionary'''
        
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


    def fetch(self, udi = None):
        '''Returns a list of DeviceIdentity objects (Option to fetch by UDI)'''
        
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