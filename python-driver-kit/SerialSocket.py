from abc import ABC, abstractmethod
import enum
from typing import BinaryIO


class StopBits(enum.Enum):
    ONE = 1
    ONE_AND_ONE_HALF = 1.5
    TWO = 2


class DataBits(enum.Enum):
    SEVEN = 7
    EIGHT = 8


class Parity(enum.Enum):
    NONE = "N"
    ODD = "O"
    EVEN = "E"


class FlowControl(enum.Enum):
    HARDWARE = "hardware"
    SOFTWARE = "software"
    NONE = "none"


class SerialSocket(ABC):
    """
    Class representing a serial socket connection
    """

    @abstractmethod
    def get_port_identifier(self) -> str:
        """
        Gets the identifier for the port (e.g. ttyUSB0).

        :returns identifier: The string identifier for a serial socket
        """

        pass


    @abstractmethod
    def close(self) -> None:
        """
        Close the serial connection
        """

        pass


    @abstractmethod
    def get_input_stream(self) -> BinaryIO:
        """
        Return an input stream for reading.

        :returns stream: The input stream used for reading
        """

        pass


    @abstractmethod
    def get_output_stream(self) -> BinaryIO:
        """
        Return an output stream for writing
        
        :returns output: The output stream used for writing
        """

        pass


    @abstractmethod
    def set_serial_params(
        self,
        baud: int,
        data_bits: DataBits,
        parity: Parity,
        stop_bits: StopBits,
        flow_control: FlowControl,
    ) -> None:
        
        """
        Configures the serial parameters

        :param baud: The agreed baud for the serial communication
        :param data_bits: The agreed data bits for the serial communication
        :param parity: The agreed parity for the serial communication
        :param stop_bits: The agreed stop bits for the serial communication
        :param flow_control: The agreed flow control for the serial communication
        """

        pass
