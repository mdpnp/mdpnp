from sim import ice_GlobalSimulationObjective
from sim import ice_GlobalSimulationObjectiveTopic

from GlobalSimulationObjectiveListener import GlobalSimulationObjectiveListener
from ice_DataReader import ice_DataReader
from EventLoop import EventLoop, ConditionHandler

import rti.connextdds as dds

from overrides import overrides


class GSOReaderHandler(ConditionHandler):
    """
    Concrete implimentation of `ConditionHandler` for reading `GlobalSimulationObjective`
    """

    def __init__(self, monitor: 'GlobalSimulationObjectiveMonitor'):
        """
        Initialises a new `ConditionHandler`
        
        :param monitor: A `GlobalSimulationObjectiveMonitor` instance. Allows access to internal methods
        """

        self.__monitor: GlobalSimulationObjectiveMonitor = monitor


    @overrides
    def conditionChanged(self, condition: dds.Condition):
        samples = self.__monitor._globalSimulationObjectiveReader.read_w_condition(condition)

        for sample in samples:
            si = sample.info()

            if not si.valid:
                continue

            gso: ice_GlobalSimulationObjective = sample.data()
            self.__monitor.__listener.simulatedNumeric(gso)


class GlobalSimulationObjectiveMonitor:
    """
    Class the monitors `GlobalSimulationObjective` instances from DDS
    """

    def __init__(self, listener: GlobalSimulationObjectiveListener) -> None:
        """
        Initialises a new `GlobalSimulationObjectiveMonitor` instance
        
        :param listener: A `GlobalSimulationObjectiveListener used to generate simulated numerics
        """

        self.__listener = listener
        self.__subscriber: dds.Subscriber = None
        self.__eventLoop: EventLoop = None
        self.__rc: dds.ReadCondition = None
        self._globalSimulationObjective: ice_GlobalSimulationObjective = None
        self._globalSimulationObjectiveReader: ice_DataReader[ice_GlobalSimulationObjective] = None


    def unregister(self) -> None:
        """
        Unregisters the current instance from reading DDS traffic
        """

        self.__eventLoop.removeHandler(self.__rc)
        
        self.__rc = None
        self.__eventLoop = None

        self._globalSimulationObjectiveReader = None
        self.__subscriber = None
        

    def register(self, subscriber: dds.Subscriber, event_loop: EventLoop) -> None:
        """
        Registers the `GlobalSimulationObjectiveMonitor` to start reading objectives from DDS.

        :param subscriber: The `Subscriber` used to create the datareader
        :param event_loop: The `EventLoop` instance that is updated with the read conditions
        """

        self.__subscriber = subscriber
        self.__eventLoop = event_loop

        self._globalSimulationObjective = ice_GlobalSimulationObjective()

        self._globalSimulationObjectiveReader = ice_DataReader(self.__subscriber.participant, ice_GlobalSimulationObjectiveTopic, ice_GlobalSimulationObjective)

        self.__rc = dds.ReadCondition(self._globalSimulationObjectiveReader.reader, dds.DataState(dds.SampleState.NOT_READ,
                                                                                                  dds.ViewState.ANY,
                                                                                                  dds.InstanceState.ANY))
        
        self.__eventLoop.addHandler(self.__rc, GSOReaderHandler(self))
        
        
