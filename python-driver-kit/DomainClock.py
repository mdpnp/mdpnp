import DeviceClock
from ice import ice_Time_t

from rti.connextdds import Time
from rti.connextdds import DomainParticipant

from datetime import datetime, timezone, timedelta
import time
import logging
from typing import Final
from overrides import overrides


class DomainClock(DeviceClock.DeviceClock):

    _log: Final[logging.Logger] = logging.getLogger("AbstractDevice")

    DEFAULT_SAMPLE_ARRAY_RESOLUTION: Final[int] = 1_000_000_000


    def __init__(self, domain_participant: DomainParticipant) -> None:
        """
        Initialises a new `DomainClock` instance.

        :param domain_participant: The `rti.connextdds.DomainParticipant` for the new instance
        :raises ValueError: If there is no domain_participant provided in the constructor
        """

        if not domain_participant:
            raise ValueError("domain_participant cannot be null")
        
        self.__domain_participant: Final[DomainParticipant] = domain_participant

        # Resolution of SampleArray samples will be reduced
        # dynamically based upon what SampleArrays are registered
        # at what frequency.
        self.__current_array_resolution_ns_per_sample = self.DEFAULT_SAMPLE_ARRAY_RESOLUTION


    @staticmethod
    def to_DDS_time(timestamp: int | datetime, target: Time | ice_Time_t = None) -> Time | None:
        """
        Converts a timestamp to DDS time format.

        :param timestamp: An integer representing the timestamp in milliseconds
        :param target: The target `rti.connextdds.Time` instance or `ice_Time_t` instance to update
        :returns time: A new `rti.connextdds.Time` instance with correct timestamp if no target is provided, otherwise None
        :raises TypeError: If the target provided is not an `rti.connextdds.Time` or `ice_Time_t` instance
        :raises TypeError: If the timestamp provided is not an integer or a `datetime.datetime` instance
        """
        
        if target and not isinstance(target, (Time, ice_Time_t)):
            raise TypeError(f"target must be either instance of rti.connextdds.Time or ice_Time_t, not: {type(target)}")
        
        if not isinstance(timestamp, (int, datetime)):
            raise TypeError(f"timestamp must either be an integer or an instance of datetime.datetime, not: {type(timestamp)}")
        elif isinstance(timestamp, datetime):
            milliseconds = timestamp.timestamp() * 1000
        else:
            milliseconds = timestamp

        if target == None:
            return Time(int(milliseconds / 1000), int(milliseconds % 1000 * 1_000_000))
        
        target.sec = int(milliseconds / 1000)
        target.nanosec = int(milliseconds % 1000 * 1_000_000)


    @staticmethod
    def to_milliseconds(timestamp: Time) -> int:
        """
        Converts a timestamp into an integer number of milliseconds
        
        :param timestamp: A `rti.connextdds.Time` instance with the timestamp
        :returns milliseconds: The converted timestamp in milliseconds
        """

        return int(1000*timestamp.sec + timestamp.nanosec / 1_000_000)
    

    @staticmethod
    def ensure_resolution_for_frequency(current_resolution_ns_per_sample: int, hertz: int, size: int) -> int:
        """
        Sanity checks the sample resolution for a given frequency and updates the resolution if required

        :param current_resolution_ns_per_sample: The sample resolution in nanoseconds per sample for calculation
        :param hertz: The frequency for validation in hertz (Hz)
        :param size: The number of samples per block
        :returns: The updated resolution in nanoseconds (ns) per sample
        :raises ValueError: If the frequency overflows the size provided
        """
        
        period_ns: int = int((1_000_000_000 * size) / hertz)
        
        if period_ns < current_resolution_ns_per_sample:
            if period_ns < 0:
                raise ValueError(f"Frequency {hertz}Hz overflow for size: {size}")
            
            DomainClock._log.info(f"Increase resolution array_resolution_ns for {size} samples at {hertz}Hz from minimum period of {current_resolution_ns_per_sample} ns to {period_ns} ns")
            current_resolution_ns_per_sample = period_ns

        return current_resolution_ns_per_sample


    @staticmethod
    def time_sample_array_resolution(resolution_ns_per_sample: int, timestamp: Time | datetime) -> Time | datetime:
        """
        Binds/rounds the provided timestamp to the nearest sample point.

        :param resolution_ns_per_sample: The sample interval in nanoseconds (ns) to bind to
        :param timestamp: The timestamp to bind
        :returns timestamp: The newly binded/rounded timestamp. A `rti.connextdds.Time` or `datetime` instance, matching the format of timestamp provided
        :raises ValueError: If the timestamp is not an instance of either `rti.connextdds.Time` or `datetime`
        """
        
        if not isinstance(timestamp, (datetime, Time)):
            raise ValueError(f"timestamp must be an instance of either datetime or rti.connextdds.Time, not: {type(timestamp)}")
        
        if isinstance(timestamp, datetime):
            sec = int(timestamp.timestamp())
            nanosec = timestamp.microsecond * 1000
            is_datetime = True

        elif isinstance(timestamp, Time):
            sec = timestamp.sec
            nanosec = timestamp.nanosec
            is_datetime = False

        if resolution_ns_per_sample >= 1_000_000_000:
            seconds_mod = resolution_ns_per_sample // 1_000_000_000
            nanoseconds_mod = resolution_ns_per_sample % 1_000_000_000

            sec -= 0 if seconds_mod == 0 else (sec % seconds_mod)
            if nanoseconds_mod == 0:
                # max res (min sample period) is an even number of seconds
                nanosec = 0
            else:
                nanosec -= 0 if nanoseconds_mod == 0 else (nanosec % nanoseconds_mod)

        else:
            nanosec -= 0 if resolution_ns_per_sample == 0 else (nanosec % resolution_ns_per_sample)

        return datetime.fromtimestamp(sec, tz=timezone.utc).replace(microsecond=nanosec // 1000) if is_datetime else Time(sec, nanosec)


    def current_time(self) -> datetime:
        """
        Gets the current time of the current instance's `DomainParticipant`

        :returns time: A `datetime` instance representing the current time
        """

        dds: Time = self.__domain_participant.current_time
        t = datetime.fromtimestamp(dds.sec, tz=timezone.utc) + timedelta(microseconds=dds.nanosec / 1000)

        return t
    

    @overrides
    def instant(self) -> DeviceClock.Reading:
        __ms: datetime = self.current_time()

        class _Reading(DeviceClock.Reading):

            @overrides
            def get_device_time(self) -> datetime:
                return None


            @overrides
            def has_device_time(self) -> bool:
                return False


            @overrides
            def get_time(self) -> datetime:
                return DomainClock.time_sample_array_resolution(self_outer.__current_array_resolution_ns_per_sample, __ms)


            @overrides
            def refine_resolution_for_frequency(self, hertz: int, size: int) -> DeviceClock.Reading:
                self_outer.__current_array_resolution_ns_per_sample = (
                    DomainClock.ensure_resolution_for_frequency(
                        self_outer.__current_array_resolution_ns_per_sample,
                        hertz,
                        size,
                    ))
                return self

        self_outer = self
        return _Reading()
