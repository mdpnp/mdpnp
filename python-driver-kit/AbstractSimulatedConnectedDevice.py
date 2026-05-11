from ice import ice_ConnectionState
from ice import ice_ConnectionType
from ice import ice_AlarmLimit
from ice import ice_LimitType
from ice import ice_LocalAlarmLimitObjective
from ice import ice_GlobalAlarmLimitObjective

import units

from AbstractConnectedDevice import AbstractConnectedDevice
from AbstractSimulatedDevice import AbstractSimulatedDevice
from AbstractDevice import InstanceHolder
from GlobalSimulationObjectiveMonitor import GlobalSimulationObjectiveMonitor
from GlobalSimulationObjectiveListener import GlobalSimulationObjectiveListener

from abc import ABC, abstractmethod
from overrides import overrides
from typing import Optional, Final


class AbstractSimulatedConnectedDevice(AbstractConnectedDevice, ABC, GlobalSimulationObjectiveListener):
    """
    An abstract base class for all Simulated Connected Devices.
    """

    def __init__(self, subscriber, publisher, event_loop) -> None:
        super().__init__(subscriber, publisher, event_loop)
        AbstractSimulatedDevice.random_udi(self._deviceIdentity)
        self._writeDeviceIdentity()
        self._last_error: Exception = None

        self.__localAlarmLimit: dict[str, InstanceHolder[ice_LocalAlarmLimitObjective]] = {}
        self.__alarmLimit: dict[str, InstanceHolder[ice_AlarmLimit]] = {}

        self._monitor: Final[GlobalSimulationObjectiveMonitor] = GlobalSimulationObjectiveMonitor(self)


    def getLastError(self) -> Optional[Exception]:
        """
        Gets the most recent error to occur
        
        :returns error: The most recent error. `Exception` or subclass of `Exception`
        """

        return self._last_error
    

    @overrides
    def connect(self, device_name: str) -> bool:
        """
        Connects the device
        
        :param device_name: The name of the device attempting to connect to
        :returns connected: True if connected successfully
        :raises RuntimeError: If unable to connect at any stage
        """

        self._monitor.register(self._subscriber, self.eventLoop)
        state = self.getState()
        if (state == ice_ConnectionState.Connected or 
            state == ice_ConnectionState.Connecting or 
            state == ice_ConnectionState.Negotiating):
            pass

        else:
            if not self._stateMachine.transitionWhenLegal(ice_ConnectionState.Connecting, f"connect requested to {device_name}", 1000):
                raise RuntimeError("Unable to enter Connecting State")
            
            if not self._stateMachine.transitionWhenLegal(ice_ConnectionState.Negotiating, f"connect requested", 1000):
                raise RuntimeError("Unable to enter Negotiating State")
            
            if not self._stateMachine.transitionWhenLegal(ice_ConnectionState.Connected, f"connect requested", 1000):
                raise RuntimeError("Unable to enter Connected State")

        return True
    

    @overrides
    def disconnect(self) -> None:
        """
        Disconnects the device.

        :raises RuntimeError: If the device is unable to reach the Terminal `ConnectionState`
        """

        self._monitor.unregister()
        if not self.getState() == ice_ConnectionState.Terminal:
            if not self._stateMachine.transitionWhenLegal(ice_ConnectionState.Terminal, "disconnect requested", 2000):
                raise RuntimeError("Unable to enter Terminal State")


    @overrides
    def getConnectionType(self) -> ice_ConnectionType:

        return ice_ConnectionType.Simulated


    def getConnectionInfo(self) -> str:
        """
        Gets the current connection info
        
        :returns info: The connection info as a string
        """

        return None


    @overrides
    def _unregisterAlarmLimitObjectiveInstance(self, holder) -> None:

        self.__localAlarmLimit.clear()
        super()._unregisterAlarmLimitObjectiveInstance(holder)


    @overrides
    def _unregisterAlarmLimitInstance(self, holder) -> None:
        
        self.__alarmLimit.clear()
        super()._unregisterAlarmLimitInstance(holder)


    @staticmethod
    def __alarmLimitKey(alarm_limit: ice_GlobalAlarmLimitObjective = None, metric_id: str = None, limit_type: ice_LimitType = None) -> str:
        """
        Returns a key string for an alarm limit.
        To call the function, either provide ONLY alarmLimit (A `GlobalAlarmLimitObjective` instance),
        OR BOTH metric_id AND limit_type.

        :param alarm_limit: A `GlobalAlarmLimitObjective` instance to get the key for. If provided, it should be provided on its own.
        :param metric_id: A metric_id to concatenate into a key. Should only be provided with limit_type and no alarm_limit.
        :param limit_type: A `LimitType` instance to concatenate into a key. Should only be provided with metric_id and no alarm_limit.
        :raises ValueError: If the method is called without following the instructions above
        """
        
        if alarm_limit:
            return f"{alarm_limit.metric_id}-{alarm_limit.limit_type}"
        
        elif metric_id and limit_type is not None:
            return f"{metric_id}-{limit_type}"
        
        else:
            raise ValueError("Method can only be called providing EITHER alarmLimit OR metric_id and limit_type")
        

    @overrides
    def setAlarmLimit(self, objective):
        super().setAlarmLimit(objective)

        self.__localAlarmLimit[f"{objective.metric_id}_{objective.limit_type}"] = self._alarmLimitObjectiveSample(self.__localAlarmLimit.get(objective.metric_id), objective.unit_identifier, objective.value, objective.metric_id, objective.limit_type)

        self.__alarmLimit[self.__alarmLimitKey(objective)] = self._alarmLimitSample(self.__alarmLimit.get(objective.metric_id), objective.unit_identifier, objective.value, objective.metric_id, objective.limit_type)


    @overrides
    def _numericSample(self, holder, new_value, time, metric_id = None, vendor_metric_id = None, unit_id = units.rosetta_MDC_DIM_DIMLESS, instance_id = 0):
        super()._numericSample(holder, new_value, time, metric_id, vendor_metric_id, unit_id, instance_id)

        if holder:
            identifier = f"{holder.data.metric_id}-{holder.data.instance_id}"
            lowAlarmLimit: InstanceHolder[ice_AlarmLimit] = self.__alarmLimit.get(self.__alarmLimitKey(metric_id=holder.data.metric_id, limit_type=ice_LimitType.low_limit))
            highAlarmLimit: InstanceHolder[ice_AlarmLimit] = self.__alarmLimit.get(self.__alarmLimitKey(metric_id=holder.data.metric_id, limit_type=ice_LimitType.high_limit))

            if lowAlarmLimit is None and highAlarmLimit is None:
                return
            
            if lowAlarmLimit is not None and lowAlarmLimit.data.value > new_value:
                self.__log.debug(f"For {identifier} lower limit is exceeded {new_value} < {lowAlarmLimit.data.value}")
                self._writePatientAlert(identifier, "LOW")

            elif highAlarmLimit is not None and highAlarmLimit.data.value < new_value:
                self.__log.debug(f"For {identifier} upper limit is exceeded {new_value} > {highAlarmLimit.data.value}")
                self._writePatientAlert(identifier, "HIGH")

            else:
                self.__log.debug(f"For {identifier} is in range {new_value} in [{'?' if not lowAlarmLimit else lowAlarmLimit.data.value}-{'?' if not highAlarmLimit else highAlarmLimit.data.value}]")
                self._writePatientAlert(identifier, "NORMAL")

    
    @overrides
    def unsetAlarmLimit(self, metric_id, limit_type):

        return super().unsetAlarmLimit(metric_id, limit_type)
    