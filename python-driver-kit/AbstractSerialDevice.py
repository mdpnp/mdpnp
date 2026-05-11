from ice import ice_ConnectionState
from ice import ice_ConnectionType

from AbstractConnectedDevice import AbstractConnectedDevice
from EventLoop import EventLoop
from TimeAwareInputStream import TimeAwareInputStream
from SerialProvider import SerialProvider
from SerialSocket import SerialSocket

from rti.connextdds import Subscriber
from rti.connextdds import Publisher

from abc import ABC, abstractmethod
from overrides import overrides
import threading
import logging
import time


class SerialDevice:

    def __init__(self, idx: int, device: 'AbstractSerialDevice') -> None:
        """
        Initialises a new `SerialDevice` instance

        :param idx: The id index of the serial device
        """

        self.__idx = idx
        self.__log = logging.Logger("SerialDevice")
        self.__device = device


    def __call__(self):
        """
        Allows a `SerialDevice` instance to be called. This is usually done on a separate thread
        """
        
        self.__log.debug(f"{threading.current_thread().name} ({threading.current_thread().ident}) begins")

        socket: SerialSocket = None

        now = time.time() * 1000

        while now < self.__device._previousAttempt[self.__idx] + self.__device._getConnectInterval(self.__idx):
            if self.__idx == 0:
                self.__device._setConnectionInfo(f"Waiting to reconnect... {(self.__device._previousAttempt[self.__idx] + self.__device._getConnectInterval(self.__idx)) - now}ms")

                try:
                    time.sleep(0.1)
                except Exception as e:
                    self.__log.error("", exc_info=e)
                
                now = time.time() * 1000

        if self.__idx == 0:
            self.__device._setConnectionInfo("")

        self.__device._previousAttempt[self.__idx] = now
        try:
            self.__log.debug(f"Invoking SerialProvider({self.__idx}).connect({self.__device._portIdentifier[self.__idx]})")

            socket = self.__device.getSerialProvider(self.__idx).connect(self.__device._portIdentifier[self.__idx], 1000)
            
            if socket is None:
                self.__log.debug("socket is null after connect")
                return
            
            else:
                if self.__idx == 0:
                    with self.__device._stateMachine._lock:
                        if self.__device._stateMachine.getState() == ice_ConnectionState.Connecting:
                            if not self.__device._stateMachine.transitionIfLegal(ice_ConnectionState.Negotiating, "serial port opened"):
                                raise RuntimeError(f"Cannot begin negotiating from {self.__device.getState()}")
                            
                        else:
                            self.__log.debug("Aborting connection processing because no longer in the Connecting state")
                            return
            
            stream = TimeAwareInputStream(socket.get_input_stream())
            self.__device._socket[self.__idx] = socket
            self.__device.process(self.__idx, stream, socket.get_output_stream())

        except Exception as e:
            self.__log.error("Processing thread ends with Exception", exc_info=e)

        finally:
            self.__log.info(f"{threading.current_thread().name} ({threading.current_thread().ident}) ends")
            
            priorState = self.__device.getState()
            self.__device._close(socket)
            self.__device._socket[self.__idx] = None
            self.__device._timeAwareInputStream[self.__idx]

            if self.__idx == 0:
                self.__device._stateMachine.transitionIfLegal(ice_ConnectionState.Connecting,  "serial port reached EOF, reconnecting...")
                self.__log.debug("process thread died unexpectedly, trying to reconnect")
                self.__device._connect(self.__idx)

            else:
                self.__device._connect(self.__idx)


class AbstractSerialDevice(AbstractConnectedDevice, ABC):
    """
    An abstract base class for all serial devices to inherit from.
    """

    def __init__(self, subscriber: Subscriber, publisher: Publisher, event_loop: EventLoop, count_serial_ports: int = 1) -> None:
        """
        Initialises a new `AbstractSerialDevice`

        :param subscriber: The `rti.connextdds.Subscriber` instance for the current subscriber
        :param publisher: The `rti.connextdds.Publisher` instance for the current publisher
        :param event_loop: The `EventLoop` instance for this device
        :param count_serial_ports: The number of serial ports the device has
        :raises RuntimeError: If the _maximumQuietTime of a given idx is less than or equal to 0
        """
        super().__init__(subscriber, publisher, event_loop)

        self._serialProvider: list[SerialProvider] = [None] * count_serial_ports
        self._lastError: list[Exception] = [None] * count_serial_ports
        self._socket: list[SerialSocket] = [None] * count_serial_ports
        self._timeAwareInputStream: list[TimeAwareInputStream] = [None] * count_serial_ports
        self.__currentThread: list[threading.Thread] = [None] * count_serial_ports
        self._portIdentifier: list[str] = [None] * count_serial_ports
        self._previousAttempt: list[int] = [None] * count_serial_ports
        self.__lastIssueInitCommands: list[int] = [None] * count_serial_ports
        self.__lock: threading.Lock = threading.Lock()
        self.__executorThread: threading.Thread = None

        serialPorts: set[str] = set()
        for idx in range(count_serial_ports):
            if self._getMaximumQuietTime(idx) <= 0:
                raise RuntimeError(f"A positive _maximumQuietTime({idx}) is required")
            
            if self._getMaximumQuietTime(idx) < 100 or self._getMaximumQuietTime(idx) % 100 != 0:
                self.__log.warning(f"Watchdog interrupts at 10Hz, consider a different _getMaximumQuietTime({idx})")

            serialPorts.update(self.getSerialProvider(idx).getPortNames())

        self._deviceConnectivity.valid_targets.value.extend(serialPorts)


    @abstractmethod
    def doInitCommands(idx: int) -> None:
        """
        Performs intialisation commands for a serial device
        """
        
        pass


    @abstractmethod
    def process(idx: int, inputStream: 'InputStream', outStream: 'OutputStream'):
        """
        Processes an input stream
        """

        pass

    
    def setSerialProvider(self, idx: int, serial_provider: SerialProvider) -> None:
        """
        Sets the serial provider for a given id index
        
        :param idx: The id index of the serial port to update the serial provider for
        :param serial_provider: The serial provider to set for the given port idx
        """

        self._serialProvider[idx] = serial_provider


    def getSerialProvider(self, idx: int) -> SerialProvider:
        """
        Gets the serial provider for the serial port at given id index
        
        :param idx: The id index of the port to get the serial provider for
        :returns provider: The `SerialProvider` for the given id index
        """

        if self._serialProvider[idx] is None:
            self._serialProvider[idx] = SerialProvider()
            #TODO: CHANGE THIS TO USE SERIALPROVIDERFACTORY

        return self._serialProvider[idx]


    def _setLastError(self, last_error: Exception, idx: int = 0) -> None:
        """
        Sets the last error that occurred

        :param last_error: The error that just occurred
        :param idx: The id index of the serial socket the error occurred on. 0 by default
        """

        self.__log.error(f"_setLastError({idx}) {last_error}")
        self._lastError[idx] = last_error


    def _getLastError(self, idx: int = 0) -> Exception:
        """
        Gets the last error that occurred for a given id index
        
        :param idx: The id index of the socket to find the last error from. 0 by default
        :returns error: The last error that occurred on the given socket id index
        """
        
        return self._lastError[idx]


    @overrides
    def disconnect(self) -> None:
        """
        Disconnects the `AbstractSerialDevice` and closes it if necessary
        """
        
        should_cancel = False
        should_close = False

        self.__log.debug("disconnect requested")
        with self._stateMachine._lock:
            state = self._stateMachine.getState()

            if state == ice_ConnectionState.Terminal:
                self.__log.debug(f"Nothing to do getState()={state}")

            elif state == ice_ConnectionState.Connecting:
                self.__log.debug(f"getState()={state} entering Terminal")
                self._stateMachine.transitionIfLegal(ice_ConnectionState.Terminal, "disconnect requested from Connecting state")
                should_cancel = True

            elif state == ice_ConnectionState.Connected or state == ice_ConnectionState.Negotiating:
                self.__log.debug(f"getState()={state} entering Terminal")
                self._stateMachine.transitionIfLegal(ice_ConnectionState.Terminal, "disconnect requested from Connected or Negotiating states")
                should_close = True

        if should_cancel:
            for idx, provider in enumerate(self._serialProvider):
                provider.cancelConnect()
                self.__log.debug(f"canceled connecting({idx})")

        if should_close:
            self.__log.debug("Closing the AbstractSerialDevice")
            self._close()
            
        
    def _close(self, socket: SerialSocket = None) -> None:
        """
        Closes the current instance of `AbstractSerialDevice`
        
        :param socket: The `SerialSocket` instance to close. If None provided, all sockets in self._socket array will be closed
        """

        if not socket:
            for soc in self._socket:
                if soc is not None:
                    self._close(soc)
        
        else:
            self.__log.debug("close")

            try:
                self.__log.debug("Attempting to close socket")
                socket.close()
                self.__log.debug("close - socket closed without error")

            except Exception as e:
                self._setLastError(e)


    @overrides
    def connect(self, device_name) -> bool:
       
        comma_separated = device_name.split(',')
        count_serial_ports = min(len(comma_separated), len(self._serialProvider))

        with self.__lock:
            state = self.getState()

            for idx in range(count_serial_ports):
                self._portIdentifier[idx] = comma_separated[idx]
                self.__log.debug(f"connect({idx}) requested to {self._portIdentifier[idx]}")

                if idx == 0:
                    if state == ice_ConnectionState.Terminal:
                        self.__log.warning(f"connect({device_name})")
                        return False
                   
                    if state == ice_ConnectionState.Initial:
                        self._stateMachine.transitionWhenLegal(ice_ConnectionState.Connecting, "connect requested from Disconnected or Disconnecting states")
                        self._connect(idx)

                    else:
                        self.__log.warning(f"will not connect({device_name})")

                else:
                    self._connect(idx)

        self.__executorThread = threading.Thread(target=self._watchdog())
        self.__executorThread.start()

        return True
                


    def _connect(self, idx: int) -> None:
        """
        Helper connect method that starts the required thread
        
        :param idx: The id index of the thread to start
        """
        
        self.__currentThread[idx] = threading.Thread(target=SerialDevice(idx), name=f"AbstractSerialDevice({idx}) Processing", daemon=True)
        self.__currentThread[idx].start()


    def _watchdog(self) -> None:
        """
        Watches the connection with the serial socket and renegotiates the connection if needed.
        Should be started on a new thread, as uses a blocking loop to schedule running every 100ms
        """

        # SIMULATING SCHEDULE AT FIXED RATE
        while True:
            with self._stateMachine._lock:
                state = self.getState()
                if state == ice_ConnectionState.Connected:
                    for idx in range(len(self._timeAwareInputStream)):
                        tais = self._timeAwareInputStream[idx]

                        if tais is not None:
                            quietTime = time.time() * 1000 - self._timeAwareInputStream[idx].get_last_read_time()

                            if quietTime > self._getMaximumQuietTime(idx):
                                self.__log.warning(f"WATCHDOG({idx}) - back to Negotiating after {quietTime}ms quiet time (exceeds {self._getMaximumQuietTime(idx)})")
                                if not self._stateMachine.transitionIfLegal(ice_ConnectionState.Negotiating, f"watchdog({idx}) {quietTime}ms quiet time (exceeds {self._getMaximumQuietTime(idx)})"):
                                    self.__log.warning(f"WATCHDOG({idx}) - unable to move from Connecting to Negotiating state (due to silence on the line)")

            with self._stateMachine._lock:
                state = self.getState()
                if state == ice_ConnectionState.Negotiating:
                    for idx in range(len(self._socket)):
                        if time.time()*1000 >= (self.__lastIssueInitCommands[idx] + self._getNegotiateInterval(idx)):
                            self.__log.debug(f"invoking doInitCommands({idx})")
                            self.__lastIssueInitCommands[idx] = time.time() * 1000
                            socket = self._socket[idx]

                            if socket is not None:
                                try:
                                    self.doInitCommands(idx)
                                
                                except Exception as e:
                                    self._setLastError(e, idx)

                        else:
                            self.__log.warning(f"Cannot issue doInitCommands({idx}) on a null socket")

            time.sleep(0.1) # PERIOD OF DELAY


    def _getMaximumQuietTime(self, idx: int) -> int:
        """
        Gets the maximum quiet time for a serial connection

        :param idx: The id of the serial connection to determine quiet time of
        """

        return -1


    @overrides
    def getConnectionType(self) -> ice_ConnectionType:

        return ice_ConnectionType.Serial
    

    @overrides
    def shutdown(self):

        self._close()
        super().shutdown()


    def _getConnectInterval(self, idx: int) -> int:
        """
        Gets the milliseconds to wait between connect attempts

        :param idx: The id index of the socket to determine the connect interval for
        :returns interval: The interval to wait between connect attempts in milliseconds
        """

        return 20_000
    

    def _getNegotiateInterval(self, idx: int) -> int:
        """
        Gets the milliseconds to wait between doInitCommands whilst in the Negotiating state

        :param idx: The id index of the socket to determine the connect interval for
        :returns interval: The interval to wait between connect attempts in milliseconds
        """

        return 10_000
    

    def _getPortIdentifier(self) -> str:
        """
        Gets the identifier for the current port

        :returns identifier: The current port identifier string
        """

        return self._portIdentifier[0]
