import rticonnextdds_connector as rti
from SampleArray import SampleArray
import time

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("SampleArraySubscriber::SampleArrayReader")

class SampleArrayDict:
    # Initialises the NumericDict class by creating a numericDict dictionary
    def __init__(self):
        self.sampleArrayDict = {}
    

    # Clears the numericDict dictionary
    def clear(self):
        if self.sampleArrayDict != None: self.sampleArrayDict = {}


    # Uses an API call via DDS to obtain all of the Numerics being published over the domain and stores them in the numericDict dictionary
    def update(self):
        input.take()
        numOfSamples = input.samples.getLength()
        
        #print(numOfSamples)
        if numOfSamples > 0:

            for j in range (0, numOfSamples):         
                if input.infos.isValid(j):             
                    sampleArray = input.samples.getDictionary(j)
                    udi = input.samples.getString(j, "unique_device_identifier") 
                    
                    if udi not in self.sampleArrayDict: self.sampleArrayDict[udi] = []

                    currentSampleArray = SampleArray()
                    currentSampleArray.update_fields(sampleArray)
                    self.sampleArrayDict[udi].append(currentSampleArray)
                
        #for udi in self.sampleArrayDict: print(len(self.sampleArrayDict[udi]))
        for udi in self.sampleArrayDict: print(f'Length of sampleArrayDict is :{len(self.sampleArrayDict[udi])}')


    # Returns a list of Numerics that fit the supplied conditions
    def fetch(self, udi = None, metric_id = None):
        sampleArrays = []
        
        try:
            # Fetches all Numerics with matching UDI and metric_id
            if udi != None and metric_id != None:           
                for sampleArray in self.sampleArrayDict[udi]:
                    if sampleArray.metric_id == metric_id: sampleArrays.append(sampleArray)
            
            # Fetches all Numerics with matching UDI
            elif udi != None:                               
                for sampleArray in self.sampleArrayDict[udi]:
                    sampleArrays.append(sampleArray)
            
            # Fetches all Numerics with matching metric_id
            elif metric_id != None:
                for udi in self.sampleArrayDict:
                    for sampleArray in self.sampleArrayDict[udi]:
                        if sampleArray.metric_id == metric_id: sampleArrays.append(sampleArray)
            
            # Fetches all Numerics
            else:
                for udi in self.sampleArrayDict:
                    for sampleArray in self.sampleArrayDict[udi]:
                        sampleArrays.append(sampleArray)

        except:
            print('Key provided not found in SampleArrayDict')

        return sampleArrays


# Testing
if __name__ == '__main__':
    current_dict = SampleArrayDict()

    while True:
        time.sleep(1.0)
        current_dict.update()
        #print(current_dict.fetch(udi = 'UEw3GF1koyHkiJ6YVqNHUcOsnA0nIb9KS7jG'))
        #print(current_dict.fetch(udi = 'Coa7yzECPNilGhUpxGwVtfe24mML0uGhfjqd', metric_id='MDC_PULS_OXIM_PLETH'))
        #if len(current_dict.sampleArrayDict) > 0: print(current_dict.fetch(udi='Coa7yzECPNilGhUpxGwVtfe24mML0uGhfjqd')[0].values.userData)    

# Coa7yzECPNilGhUpxGwVtfe24mML0uGhfjqd