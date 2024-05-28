import rticonnextdds_connector as rti
from Numeric import Numeric

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("NumericSubscriber::NumericReader")

class NumericDict:
    '''Class that stores a dictionary of the most recent Numerics recieved from DDS, keyed by UDI\n
       Additionally, for each UDI, the Numerics are keyed by metric_id'''
    
    def __init__(self):
        '''Initialises the NumericDict class by creating an empty dictionary'''

        self.numericDict = {}
    

    def clear(self):
        '''Clears the Numeric dictionary back to the inital state'''

        if self.numericDict != None: self.numericDict = {}


    def update(self):
        '''Uses an API call via DDS to obtain all of the Numerics being published over the domain and stores them in the Numeric dictionary'''

        input.take()
        numOfSamples = input.samples.getLength()
        
        if numOfSamples > 0:
            for j in range (0, numOfSamples):         
                if input.infos.isValid(j):             
                    numeric = input.samples.getDictionary(j)
                    udi = numeric['unique_device_identifier']
                    metric_id = numeric['metric_id']
                    
                    if udi not in self.numericDict: self.numericDict[udi] = {}

                    currentNumeric = Numeric()
                    currentNumeric.update_fields(numeric)

                    self.numericDict[udi][metric_id] = currentNumeric


    def fetch(self, udi = None, metric_id = None):
        '''Returns a list of Numeric objects (Option to fetch by UDI and/or metric_id)'''

        numerics = []
        
        try:
            # Fetches all Numerics with matching UDI and metric_id
            if udi != None and metric_id != None:        
                numerics.append(self.numericDict[udi][metric_id])
            
            # Fetches all Numerics with matching UDI
            elif udi != None:                               
                for metric_id in self.numericDict[udi]:
                    numerics.append(self.numericDict[udi][metric_id])
            
            # Fetches all Numerics with matching metric_id
            elif metric_id != None:
                for udi in self.numericDict:
                    numerics.append(self.numericDict[udi][metric_id])
            
            # Fetches all Numerics
            else:
                for udi in self.numericDict:
                    for metric_id in self.numericDict[udi]:
                        numerics.append(self.numericDict[udi][metric_id])

        except:
            print('Key provided not found in NumericDict')

        return numerics