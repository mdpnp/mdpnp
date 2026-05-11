import time
import serial

class TimeAwareInputStream:
    """
    Wraps a serial stream to allow reading, whilst also keeping track of the timestamps when reads have taken place
    """

    def __init__(self, stream: serial.Serial):
        """
        Initialises a new `TimeAwareInputStream` instance

        :param stream: A stream that can be read from and written to
        """

        self._stream = stream
        self._last_read = 0


    def read(self, size=1) -> bytes:
        """
        Read bytes from the USB/serial stream

        :returns data: The read data from the serial stream as bytes
        """

        data = self._stream.read(size)
        if data and len(data) > 0:
            self._last_read = int(time.time() * 1000)

        return data


    def get_last_read_time(self) -> int:
        """
        Gets the last time data was read from the stream
        
        :returns time: The time in milliseconds since the last read of data from the stream
        """

        return self._last_read


    def promote_last_read_time(self) -> None:
        """
        Sets the last read time to the current time
        """

        self._last_read = int(time.time() * 1000)


    def close(self):
        """
        Closes the serial stream
        """

        self._stream.close()


    def __enter__(self):
        return self


    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()
