import rticonnextdds_connector as rti

connector = rti.Connector("iceParticipantLibrary::iceParticipant", "/home/benstacey/from-simon/mdpnp/interop-lab/python/ice/icepython.xml")

input = connector.getInput("DeviceIdentitySubscriber::DeviceIdentityReader")

def printSampleData(numOfSamples):   
    for j in range (0, numOfSamples):         
        if input.infos.isValid(j):             
            deviceIdentity = input.samples.getDictionary(j)
            print(deviceIdentity)
            '''
            udi = input.samples.getString(j, "unique_device_identifier")             
            value = input.samples.getNumber(j, "value")
            device_time = input.samples.getDictionary(j, "device_time") 
            print(f"UDI: {udi}, Value: {value}, Time: {device_time['sec']}")
            '''

while True:
    input.take()
    numOfSamples = input.samples.getLength()
    
    if numOfSamples > 0:
        #print(numOfSamples)
        printSampleData(numOfSamples)