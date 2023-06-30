import rticonnextdds_connector as rti
from SampleArray import SampleArray
import time

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("SampleArraySubscriber::SampleArrayReader")

class SampleArrayDict:
    '''Class that stores a dictionary of the most recent SampleArrays recieved from DDS, keyed by UDI\n
       Additionally, for each UDI, the SampleArrays are keyed by metric_id'''

    def __init__(self):
        '''Initialises the SampleArrayDict class by creating an empty dictionary'''

        self.sampleArrayDict = {}
    

    def clear(self):
        '''Clears the SampleArray dictionary back to the inital state'''

        if self.sampleArrayDict != None: self.sampleArrayDict = {}


    def update(self):
        '''Uses an API call via DDS to obtain all of the SampleArrays being published over the domain and stores them in the SampleArray dictionary'''

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


    def fetch(self, udi = None, metric_id = None):
        '''Returns a list of SampleArray objects (Option to fetch by UDI and/or metric_id)'''

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