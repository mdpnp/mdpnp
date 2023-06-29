import rticonnextdds_connector as rti
from Numeric import Numeric

#Setting up the API connection with DDS
connector = rti.Connector("iceParticipantLibrary::iceParticipant", "interop-lab/python/ice/icepython.xml")
input = connector.getInput("NumericSubscriber::NumericReader")

class NumericDict:
    # Initialises the NumericDict class by creating a numericDict dictionary
    def __init__(self):
        self.numericDict = {}
    

    # Clears the numericDict dictionary
    def clear(self):
        if self.numericDict != None: self.numericDict = {}


    # Uses an API call via DDS to obtain all of the Numerics being published over the domain and stores them in the numericDict dictionary
    def update(self):
        input.take()
        numOfSamples = input.samples.getLength()
        
        if numOfSamples > 0:
            for j in range (0, numOfSamples):         
                if input.infos.isValid(j):             
                    numeric = input.samples.getDictionary(j)
                    udi = input.samples.getString(j, "unique_device_identifier") 
                    
                    self.numericDict[udi] = []
                    currentNumeric = Numeric()
                    currentNumeric.update_fields(numeric)
                    self.numericDict[udi].append(currentNumeric)


    # Returns a list of Numerics that fit the supplied conditions
    def fetch(self, udi = None, metric_id = None):
        numerics = []
        
        try:
            # Fetches all Numerics with matching UDI and metric_id
            if udi != None and metric_id != None:        
                for numeric in self.numericDict[udi]:
                    if numeric.metric_id == metric_id: numerics.append(numeric)
            
            # Fetches all Numerics with matching UDI
            elif udi != None:                               
                for numeric in self.numericDict[udi]:
                    numerics.append(numeric)
            
            # Fetches all Numerics with matching metric_id
            elif metric_id != None:
                for udi in self.numericDict:
                    for numeric in self.numericDict[udi]:
                        if numeric.metric_id == metric_id: numerics.append(numeric)
            
            # Fetches all Numerics
            else:
                for udi in self.numericDict:
                    for numeric in self.numericDict[udi]:
                        numerics.append(numeric)

        except:
            print('Key provided not found in NumericDict')

        return numerics


# Testing
if __name__ == '__main__':
    current_dict = NumericDict()

    while True:
        current_dict.update()
        #if len(current_dict.numericDict) > 0: print(current_dict.fetch()[0].unique_device_identifier)
        if len(current_dict.numericDict) > 0: print(current_dict.fetch(udi = 'UPXjQH67bTtFkx2OYskwgydUTivXjFqAn7yU')[0].metric_id)
        #print(current_dict.fetch(udi = 'UPXjQH67bTtFkx2OYskwgydUTivXjFqAn7yU ', metric_id='MDC_FLOW_FLUID_PUMP'))
        #if len(current_dict.numericDict) > 0: print(current_dict.fetch()[0].unit_id)    

#UPXjQH67bTtFkx2OYskwgydUTivXjFqAn7yU       