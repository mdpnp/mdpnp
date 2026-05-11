from abc import ABC, abstractmethod
from datetime import datetime, timezone
from overrides import overrides
import time
from typing import Final


class Reading(ABC):
    """
    Interface for clock readings

    A `Reading` is a point on the time-line as perceived by the instance of the clock that is returning this reading.
    
    In reality, just like different calendars have different conventions of now to name a particular year -
    Gregorian vs Julian vs etc calendars, a moment in time can be described by the system clock and by the
    device clock and the two are not guaranteed to be the same.  But unlike the dates that can be converted
    from one calendar to another, the clocks are not guaranteed to be in sync. Therefore we would
    want to carry both timestamps around.

    :See also: `CombinedReading`
    """

    @abstractmethod
    def get_time(self) -> datetime:
        """
        Gets the current instance's time
        
        :returns time: A `datetime` instance containing current time
        """
        pass


    @abstractmethod
    def has_device_time(self) -> bool:
        """
        Returns whether the instance stores the current device time
        
        :returns has_device_time: Boolean value `True` if reading has device time, or `False` otherwise
        """
        pass


    @abstractmethod
    def get_device_time(self) -> datetime:
        """
        Gets the current device time

        :returns time: A `datetime` instance with the current device's time
        """
        pass


    @abstractmethod
    def refine_resolution_for_frequency(self, hertz: int, size: int) -> 'Reading':
        """
        Refines the resolution of the reading for the provided frequency

        :param hertz: The frequency to refine the reading for.
        :param size: The size to refine the reading for
        :returns refined_reading: The refined reading as a `Reading` instance
        """
        pass


class DeviceClock(ABC):
    """
    Interface for device clocks
    """

    @abstractmethod
    def instant(self) -> Reading:
        """
        Return the current reading from the clock.

        :returns reading: A reading representing the current instant as defined by the clock, not null
        """
        pass


class WallClock(DeviceClock):
    """
    An implimentation of a wall-clock. This provides actual human time
    """

    @overrides
    def instant(self) -> Reading:
        """
        Returns the current wall-clock time

        :returns reading: The current time in a `Reading` instance
        """

        return ReadingImpl(self._get_time_in_millis())


    def _get_time_in_millis(self) -> int:
        """
        Gets the current time since the Epoch in milliseconds.

        :returns time: The current time in milliseconds
        """

        return int(time.time() * 1000)


class Metronome(WallClock):
    """
    Implimentation of a metronome that extends `WallClock`
    """

    def __init__(self, update_period_ms: int) -> None:
        """
        Initialises a new `Metronome` instance

        :param update_period_ms: The update period for the metronome in milliseconds
        """

        self.update_period_ms: Final[int] = update_period_ms


    @overrides
    def _get_time_in_millis(self) -> int:
        """
        Returns the current time as a multiple of the initialised update_period.
        
        :returns time: The current metronome time as an integer
        """

        now = int(time.time() * 1000)
        return now - (now % self.update_period_ms)


class ReadingImpl(Reading):
    """
    Concrete implimentation of a `Reading`
    """

    def __init__(self, time_value: int | datetime) -> None:
        """
        Initialises a new `ReadingImpl` instance

        :param time_value: The time to initialise the `ReadingImpl` instance with. Must be an integer (in milliseconds) or `datetime`
        :raises TypeError: If time_value is not an integer or `datetime`
        """

        if isinstance(time_value, int):
            self.ms: datetime = datetime.fromtimestamp(time_value / 1000, tz=timezone.utc)
        elif isinstance(time_value, datetime):
            self.ms: datetime = time_value
        else:
            raise TypeError("time_value must be int (in milliseconds) or datetime")


    @overrides
    def __str__(self) -> str:
        """
        Returns a string representation of a `ReadingImpl` instance
        """

        return self.ms.isoformat()
    

    @overrides
    def get_time(self) -> datetime:
        return self.ms


    @overrides
    def get_device_time(self) -> datetime:
        return self.ms


    @overrides
    def has_device_time(self) -> bool:
        return True

    
    @overrides
    def refine_resolution_for_frequency(self, hertz: int, size: int) -> Reading:
        return self


class CombinedReading(Reading):
    """
    An object combining a refined reading and a device reading and storing them together
    """

    def __init__(self, ref: Reading, dev: Reading):
        """
        Initialises a new `CombinedReading` instance

        :param ref: The refined time `Reading` instance to store
        :param dev: The device time `Reading` instance to store
        """

        self.ref: Reading = ref
        self.dev: Reading = dev


    @overrides
    def __str__(self):
        """
        Returns a string representation of a `CombinedReading` instance
        """

        return f"{self.ref} {self.dev}"


    @overrides
    def get_time(self) -> datetime:
        return self.ref.get_time()


    @overrides
    def has_device_time(self) -> bool:
        return self.dev.has_device_time()
    

    @overrides
    def get_device_time(self) -> datetime:
        return self.dev.get_device_time()


    def refine_resolution_for_frequency(self, hertz: int, size: int) -> Reading:
        self.ref.refine_resolution_for_frequency(hertz, size)
        return self
