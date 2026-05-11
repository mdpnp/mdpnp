from ice import ice_DeviceConnectivity
from ice import ice_DeviceConnectivityTopic
from ice import ice_ConnectionType
from ice import ice_ConnectionState

from AbstractDevice import AbstractDevice
from ice_DataWriter import ice_DataWriter
from StateMachine import StateMachine

from rti.connextdds import InstanceHandle

from abc import ABC, abstractmethod
from typing import Final
from overrides import overrides
import logging


class ConnectionStateMachine(StateMachine[ice_ConnectionState]):
    """
    A `StateMachine` derivative that handles `ConnectionStates`
    """

    def __init__(self, legalTransitions, initialState, transitionNote, device: 'AbstractConnectedDevice'):
        """
        Initialises a new `ConnectionStateMachine` instance

        :param legalTransitions: A list of pairs of states, showing legal transitions between state 0, and state 1
        :param initialState: The initial state of the `StateMachine`
        :param transitionNote: The initial transition note for the `StateMachine`
        :param device: The `AbstractConnectedDevice` instance using the state machine. Gives access to internal methods and attributes
        """

        super().__init__(legalTransitions, initialState, transitionNote)
        self.__device = device


    @overrides
    def emit(self, newState, oldState, transitionNote) -> None:
        """
        Handles and publishes a state transition
        
        :param newState: The new state being transitioned to
        :param oldState: The old state being transitioned from
        :param transitionNote: The transition note message to associate with the transition
        """
        
        self.__device.stateChanging(newState, oldState, transitionNote)
        self._log.debug(f"{oldState}==>{newState} ({transitionNote})")
        self.__device._deviceConnectivity.state = newState
        self.__device._deviceConnectivity.info = transitionNote
        handle: InstanceHandle = self.__device._deviceConnectivityHandle

        if handle is not None:
            self.__device._writeDeviceConnectivity()
        
        self.__device.stateChanged(newState, oldState, transitionNote)


class AbstractConnectedDevice(AbstractDevice, ABC):
    """
    Python equivalent to the AbstractConnectedDevice Java class. All connected OpenICE devices 
    inherit from this class.
    """

    # Statics
    __log: logging.Logger = logging.getLogger("AbstractConnectedDevice")
    __legalTransitions: list[list[ice_ConnectionState]] = [
            # Normal "flow"
            # A "connect" was requested, from this transition on the device adapter will
            # attempt to maintain / re-establish connectivity
            [ ice_ConnectionState.Initial, ice_ConnectionState.Connecting ],
            # Connection was established
            [ ice_ConnectionState.Connecting, ice_ConnectionState.Negotiating ],
            # Connection still open but no active session (silence on the
            # RS-232 line for example)
            [ ice_ConnectionState.Connected, ice_ConnectionState.Negotiating ],
            # A fatal error in data processing that has caused us to close the connection
            # and to attempt to reopen it
            [ ice_ConnectionState.Connected, ice_ConnectionState.Connecting ],
            # Negotiation was successful
            [ ice_ConnectionState.Negotiating, ice_ConnectionState.Connected ],
            # A lack of an open connection while trying to attempt to negotiate
            [ ice_ConnectionState.Negotiating, ice_ConnectionState.Connecting ],
            # Explicit disconnect has been invoked, the Terminal state is Terminal
            # A fatal error occurred in the Negotiating state
            [ ice_ConnectionState.Negotiating, ice_ConnectionState.Terminal ],
            # A fatal error occurred in the Connecting state
            [ ice_ConnectionState.Connecting, ice_ConnectionState.Terminal ],
            # A fatal error occurred in the Connected state
            [ ice_ConnectionState.Connected, ice_ConnectionState.Terminal ]]


    def __init__(self, subscriber, publisher, event_loop):
        super().__init__(subscriber, publisher, event_loop)

        self.__deviceConnectivityWriter: Final[ice_DataWriter[ice_DeviceConnectivity]] = ice_DataWriter(self._domainParticipant, ice_DeviceConnectivityTopic, ice_DeviceConnectivity)
        if not self.__deviceConnectivityWriter:
            raise RuntimeError("__deviceConnectivityWriter not created")
        
        self._stateMachine: ConnectionStateMachine = ConnectionStateMachine(self.__legalTransitions, ice_ConnectionState.Initial, "initial state", self)
        self._deviceConnectivity = ice_DeviceConnectivity()
        self._deviceConnectivityHandle: InstanceHandle = None
        self._deviceConnectivity.type = self.getConnectionType()
        self._deviceConnectivity.state = self.getState()


    def stateChanging(self, newState: ice_ConnectionState, oldState: ice_ConnectionState, transitionNote: str) -> None:
        pass


    def stateChanged(self, newState: ice_ConnectionState, oldState: ice_ConnectionState, transitionNote: str) -> None:
        """
        Should always be called when the state has changed. If the device has become unconnected, then calls `_unregisterAllInstances` via `EventLoop`
        
        :param newState: The new state being transitioned to
        :param oldState: The old state being transitioned from
        :param transitionNote: The transition note going alongside the state transition
        """

        if oldState == ice_ConnectionState.Connected and not newState == ice_ConnectionState.Connected:
            self.eventLoop.doLater(self._unregisterAllInstances)


    @overrides
    def shutdown(self) -> None:
        if self._deviceConnectivityHandle is not None:
            handle = self._deviceConnectivityHandle
            self._deviceConnectivityHandle = None
            self.__deviceConnectivityWriter.dispose(handle)
        
        super().shutdown()


    @abstractmethod
    def connect(self, device_name: str) -> bool:
        """
        Connects the device
        
        :param device_name: The name of the device attempting to connect to
        """

        pass


    @abstractmethod
    def disconnect(self) -> None:
        """
        Disconnects the device
        """

        pass


    @abstractmethod
    def getConnectionType(self) -> ice_ConnectionType:
        """
        Gets the current device's connection type
        
        :returns type: The `ConnectionType` of the current device
        """

        pass


    def getState(self) -> ice_ConnectionState:
        """
        Gets the current connection state of the device

        :returns state: The current `ConnectionState` of the device
        """

        return self._stateMachine.getState()


    def awaitState(self, state: ice_ConnectionState, timeout: int) -> bool:
        """
        Waits for the state machine to reach the provided state, or for the timeout to be reached
        
        :param state: The target state waiting to be reached
        :param timout: The max timeout to wait for before failing (in milliseconds)
        :returns reached: True if the state was reached within the timeout period, or False if not
        """

        return self._stateMachine.wait(state, timeout)


    def _setConnectionInfo(self, connectionInfo: str):
        """
        Sets the connection info and publishes it to DDS
        
        :param connectionInfo: The new connectionInfo to set and publish
        """

        if not connectionInfo:
            self.__log.warning("Attemtp to set connectionInfo null")
            connectionInfo = ""

        if not connectionInfo == self._deviceConnectivity.info:
            self._deviceConnectivity.info = connectionInfo
            self._writeDeviceConnectivity()


    @overrides
    def _writeDeviceIdentity(self) -> None:
        super()._writeDeviceIdentity()

        if self._deviceConnectivityHandle is None:
            self._writeDeviceConnectivity()


    def _writeDeviceConnectivity(self) -> None:
        """
        Writes the `DeviceConnectivity` to DDS

        :raises RuntimeError: If `DeviceConnectivity.unique_device_identifier` is not populated before calling
        """

        self._deviceConnectivity.unique_device_identifier = self._deviceIdentity.unique_device_identifier
        if not self._deviceConnectivity.unique_device_identifier:
            raise RuntimeError("No UDI when calling _writeDeviceConnectivity")

        if self._deviceConnectivityHandle is None:
            self._deviceConnectivityHandle = self.__deviceConnectivityWriter.register_instance(self._deviceConnectivity)

        self.__deviceConnectivityWriter.write(self._deviceConnectivity, self._deviceConnectivityHandle)
