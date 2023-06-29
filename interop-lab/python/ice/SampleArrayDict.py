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
        
        if numOfSamples > 0:
            for j in range (0, numOfSamples):         
                if input.infos.isValid(j):             
                    sampleArray = input.samples.getDictionary(j)
                    udi = sampleArray['unique_device_identifier']
                    metric_id = sampleArray['metric_id']
                    
                    if udi not in self.sampleArrayDict: self.sampleArrayDict[udi] = {}

                    currentSampleArray = SampleArray()
                    currentSampleArray.update_fields(sampleArray)
                    
                    self.sampleArrayDict[udi][metric_id] = currentSampleArray


    # Returns a list of Numerics that fit the supplied conditions
    def fetch(self, udi = None, metric_id = None):
        sampleArrays = []
        
        try:
            # Fetches all Numerics with matching UDI and metric_id
            if udi != None and metric_id != None:           
                sampleArrays.append(self.sampleArrayDict[udi][metric_id])
            
            # Fetches all Numerics with matching UDI
            elif udi != None:                               
                for metric_id in self.sampleArrayDict[udi]:
                    sampleArrays.append(self.sampleArrayDict[udi][metric_id])
            
            # Fetches all Numerics with matching metric_id
            elif metric_id != None:
                for udi in self.sampleArrayDict:
                    sampleArrays.append(self.sampleArrayDict[udi][metric_id])
            
            # Fetches all Numerics
            else:
                for udi in self.sampleArrayDict:
                    for metric_id in self.sampleArrayDict[udi]:
                        sampleArrays.append(self.sampleArrayDict[udi][metric_id])

        except:
            print('Key provided not found in SampleArrayDict')

        return sampleArrays


# Testing
if __name__ == '__main__':
    current_dict = SampleArrayDict()

    while True:
        current_dict.update()
        '''if len(current_dict.sampleArrayDict) > 0: 
            for sampleArray in current_dict.fetch():
                print(sampleArray.metric_id)'''
        print(current_dict.fetch(metric_id='MDC_PULS_OXIM_PLETH'))