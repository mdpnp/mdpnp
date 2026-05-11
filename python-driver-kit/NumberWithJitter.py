from numbers import Number
import random
import threading


class NumberWithJitter(Number):
    """
    A number wrapper that applies jitter on each access,
    constrained by upper and lower bounds.
    """

    def __init__(self, initial_value: Number, increment: Number, floor: Number = None, ceil: Number = None, max_delta: Number = None):
        """
        Initialises a new `NumberWithJitter` instance

        :param initial_value: The initial value of the number
        :param increment: The increment to jitter the number by (this will then be randomly scaled between 0-1x when jittering)
        :param floor: The lowest number the number can jitter to.
        :param ceil: The highest number the number can jitter to.
        :param max_delta: The maximum amount the number can change by either positively or negatively. (If provided, will override the floor and ceil fields)
        """

        if max_delta is not None:
            floor = initial_value - max_delta
            ceil = initial_value + max_delta

        self.__increment: Number = increment
        self.__floor: Number = floor
        self._ceil: Number = ceil
        self.__initial_value: Number = initial_value
        self.__current_value: Number = initial_value

        self.__lock = threading.Lock()


    def __int__(self) -> int:
        """
        Converts the number stored in the instance to a single integer

        :returns num: The number with jitter applied as an integer
        """

        return int(self.next())


    def __float__(self) -> float:
        """
        Converts the number stored in the instance to a single integer

        :returns num: The number with jitter applied as a float
        """

        return float(self.next())


    def next(self) -> Number:
        """
        Gets the next iteration of the internal number, with jitter applied

        :returns num: The new number with jitter applied
        """

        with self.__lock:
            diff = self.__increment - (2 * self.__increment * random.random())
            next_value = self.__current_value + diff

            if next_value < self.__floor or next_value > self._ceil:
                next_value = self.__current_value - diff

            to_return = self.__current_value
            self.__current_value = next_value
            return to_return


    def getIncrement(self) -> Number:
        """
        Gets the current increment
        
        :returns increment: The current increment for the instance
        """

        return self.__increment


    def getFloor(self) -> Number:
        """
        Gets the lower bound of the jitter
        
        :returns floor: The lower bound that the number can jitter to
        """

        return self.__floor


    def getCeil(self) -> Number:
        """
        Gets the upper bound of the jitter

        :returns ceil: The upper bound that the number can jitter to
        """

        return self._ceil


    def getInitialValue(self) -> Number:
        """
        Gets the intial value put into the instance without jitter applied

        :returns initial: The initial value with no jitter applied
        """

        return self.__initial_value


    def getCurrentValue(self) -> Number:
        """
        Gets the current value

        :returns num: The current number stored in the instance
        """
    
        return self.__current_value


    def __str__(self) -> str:
        """
        :returns str: The string representation of the `NumberWithJitter` instance
        """

        return (f"NumberWithJitter{{"
                f"increment={self.__increment}, "
                f"floor={self.__floor}, "
                f"ceil={self._ceil}, "
                f"initialValue={self.__initial_value}, "
                f"currentValue={self.__current_value}}}")

