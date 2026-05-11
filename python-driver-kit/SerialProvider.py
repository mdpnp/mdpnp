import SerialSocket

from abc import ABC, abstractmethod


class SerialProvider(ABC):
    """
    Interface for serial providers
    """

    @abstractmethod
    def getPortNames(self) -> list[str]:
        """
        Gets a list of port names
        
        :returns names: A list of strings of the port names
        """

        pass


    @abstractmethod
    def connect(port_identifier: str, timeout: int) -> SerialSocket.SerialSocket:
        """
        Connects to a serial socket
        
        :param port_identifier: The port identifier of the port to connnect to
        :param timout: The timeout before connection fails
        :returns socket: The serial socket that has been connected
        """

        pass


    @abstractmethod
    def cancelConnect(self) -> None:
        """
        Cancels the connection to the port
        """

        pass


    @abstractmethod
    def setDefaultSerialSettings(self, baudrate: int, 
                                 data_bits: SerialSocket.DataBits, 
                                 parity: SerialSocket.Parity, 
                                 stop_bits: SerialSocket.StopBits, 
                                 flow_control: SerialSocket.FlowControl = None) -> None:
        
        """
        Initialises the default serial settings for the connection

        :param baudrate: The agreed baudrate for the serial communication
        :param data_bits: The agreed data bits for the serial communication
        :param parity: The agreed parity for the serial communication
        :param stop_bits: The agreed stop bits for the serial communication
        :param flow_control: The (optional) agreed flow control for the serial communication
        """

        pass


    @abstractmethod
    def duplicate(self) -> 'SerialProvider':
        """
        Duplicates the current instance

        :returns duplicate: The newly created duplicate
        """

        pass
