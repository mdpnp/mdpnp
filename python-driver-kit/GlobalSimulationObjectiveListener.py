from sim import ice_GlobalSimulationObjective

from NumberWithJitter import NumberWithJitter

from numbers import Number


class GlobalSimulationObjectiveListener:

    def simulatedNumeric(objective: ice_GlobalSimulationObjective) -> None:
        pass


    @staticmethod
    def toIntegerNumber(obj: ice_GlobalSimulationObjective) -> int:
        """
        Returns an integer version of the `GlobalSimulationObjective` value, with jitter applied
        
        :returns number: The jittered integer. If `GlobalSimulationObjective`'s jitterStep is 0, then no jitter is applied
        """
        
        if obj.jitterStep == 0:
            return int(obj.value)
        
        return int(NumberWithJitter(obj.value, obj.jitterStep, obj.floor, obj.ceil))
    

    @staticmethod
    def toFloatNumber(obj: ice_GlobalSimulationObjective) -> float:
        """
        Returns an float version of the `GlobalSimulationObjective` value, with jitter applied
        
        :returns number: The jittered float. If `GlobalSimulationObjective`'s jitterStep is 0, then no jitter is applied
        """
        
        if obj.jitterStep == 0:
            return float(obj.value)
        
        return float(NumberWithJitter(obj.value, obj.jitterStep, obj.floor, obj.ceil))


