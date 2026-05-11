from rti.connextdds import Condition
from rti.connextdds import WaitSet
from rti.connextdds import WaitSetProperty
from rti.connextdds import GuardCondition
from rti.connextdds import ConditionSeq
from rti.connextdds import Duration

from abc import ABC, abstractmethod
from typing import Final, Callable
import logging
from overrides import overrides
import traceback
import threading
import time


class ConditionHandler(ABC):
    """
    An interface that can be implimented to allow for condition handling
    """

    @abstractmethod
    def conditionChanged(self, condition: Condition) -> None:
        """
        Handles a change of condition
        
        :param condition: The new condition to update
        
        """
        pass


class MutateHandler(ConditionHandler):
    """
    An implimentation of `ConditionHandler` for handling mutations
    """

    def __init__(self, outer: 'EventLoop') -> None:
        """
        Initialises a new `MutateHandler` instance

        :param outer: The `EventLoop` instance using the handler. This gives access to the internal methods and attributes
        """

        self.outer = outer
        self.__lock = threading.Lock()


    @overrides
    def conditionChanged(self, condition: Condition):
        with self.__lock:
            mutations = self.outer.__queuedMutations.copy()
            self.outer.__queuedMutations.clear()

            condition.trigger_value = False

        for m in mutations:
            self.outer._handleMutation(m)


class CallableHandler(ConditionHandler):
    """
    An implimentation of `ConditionHandler` for handling callables
    """

    def __init__(self, outer: 'EventLoop') -> None:
        """
        Initialises a new `CallableHandler` instance

        :param outer: The `EventLoop` instance using the handler. This gives access to the internal methods and attributes
        """
        
        self.outer = outer
        self.__lock = threading.Lock()


    @overrides
    def conditionChanged(self, condition: Condition):
        with self.__lock:
            callables = self.outer.__queuedCallables.copy()
            self.outer.__queuedCallables.clear()

            condition.trigger_value = False

        for c in callables:
            print(f"Calling: {c.__name__}")
            c()


class Mutation:
    """
    A class that represents mutations
    """

    def __init__(self, add: bool, condition: Condition, condition_handler: ConditionHandler) -> None:
        """
        Initialises a new `Mutation` instance
        
        :param add: A boolean representing whether this condition should be added to the `EventLoop` instance's `WaitSet` when handled
        :param condition: The `Condition` of the mutation
        :param condition_handler: The `ConditionHandler` instance which handles the condition
        """

        self.__add: Final[bool] = add
        self.__condition: Final[Condition] = condition
        self.__condition_handler: Final[ConditionHandler] = condition_handler
        self.__trace: Final[list[traceback.StackSummary]] = traceback.extract_stack()
        self.__done: threading.Event = threading.Event()


    def is_add(self) -> bool:
        """
        Returns whether or not this `Mutation` should be added to a `EventLoop`'s `WaitSet` conditions

        :returns is_add: True if the current mutation should be added, or False otherwise
        """

        return self.__add
    

    def get_condition(self) -> Condition:
        """
        Gets the internal `Condition` of the `Mutation`
        
        :returns condition: The current `Mutation`'s internal `Condition`
        """

        return self.__condition


    def get_condition_handler(self) -> ConditionHandler:
        """
        Gets the `Mutation`'s internal `ConditionHandler`

        :returns handler: The current `Mutation` instance's `ConditionHandler`
        """

        return self.__condition_handler
    

    def get_trace(self) -> traceback.StackSummary:
        """
        Gets the traceback for the current `Mutation`

        :returns traceback: The current `Mutation`'s stack trace. This is a `traceback.StackSummary` instance
        """

        return self.__trace


    def done(self) -> None:
        """
        Signals the thread that the mutatation is done
        """

        self.__done.set()


    def await_mutation(self) -> None:
        """
        Waits until the mutation has finished
        """

        self.__done.wait()


class NestedCallable:
    """
    Nests a callable object inside an instance of this class
    """

    def __init__(self, callable: Callable, logger: logging.Logger) -> None:
        """
        Initialises a new `NestedCallable` instance
        
        :param callable: The callable to wrap with the `NestedCallable` class
        :param logger: A logger to log errors to if the callable fails.
        """

        self.__callable: Final[Callable] = callable
        self.__done: bool = False
        self.__lock: threading.Condition = threading.Condition()
        self.__log: logging.Logger = logger


    def __call__(self) -> None:
        """
        Calls the internal callable object
        """

        try:
            self.__callable()

        finally:
            with self.__lock:
                self.__done = True
                self.__lock.notify_all()

    
    def wait_till_done(self) -> None:
        """
        Waits until the callable object has finished executing
        """

        with self.__lock:
            while not self.__done:
                try:
                    self.__lock.wait()
                
                except Exception as e:
                    self.__log.error("Interrupted waiting for task completion", exc_info=e)



class EventLoop:
    """
    An event loop for a device driver, controlling the activity of the drive
    """

    __log = logging.getLogger("EventLoop")
    __WARNING_ELAPSED_TIME_NANOSECONDS = 100_000_000


    def __init__(self, properties: WaitSetProperty = None):
        """
        Initialises a new `EventLoop` instance
        
        :param properties: An optional parameter that if provided, will initialise the internal `WaitSet` using the `WaitSetProperties` instance
        """

        self.__currentServiceThread: threading.Thread = threading.current_thread()
        self.__lock: threading.Condition = threading.Condition()
        self.__conditionHandlers: Final[dict[int, ConditionHandler]] = {} # Need to map id to ConditionHandler as Condition is not hashable
        self.__queuedMutations: Final[list[Mutation]] = []
        self.__queuedCallables: Final[list[Callable[[], None]]] = []
        self.__mutate: Final[GuardCondition] = GuardCondition()
        self.__callable: Final[GuardCondition] = GuardCondition()

        if not properties:
            self.__waitSet: Final[WaitSet] = WaitSet()
        else:
            self.__waitSet: Final[WaitSet] = WaitSet(properties)

        self.__waitSet.attach_condition(self.__mutate)
        self.__waitSet.attach_condition(self.__callable)

        self.__mutateHandler: Final[MutateHandler] = MutateHandler(self)
        self.__callableHandler: Final[CallableHandler] = CallableHandler(self)
        
        self.__conditionHandlers[id(self.__mutate)] = self.__mutateHandler
        self.__conditionHandlers[id(self.__callable)] = self.__callableHandler


    def _handleMutation(self, mutation: Mutation) -> None:
        """
        Handles a mutation

        :param mutation: The `Mutation` instance to handle
        """

        if mutation.is_add():
            self.__conditionHandlers[id(mutation.get_condition())] = mutation.get_condition_handler()
            self.__waitSet.attach_condition(mutation.get_condition())

        else:
            if not self.__conditionHandlers.pop(id(mutation.get_condition()), None):
                self.__log.warning(f"Attempt to detach unknown condition: {mutation.get_condition()}")
                trace = mutation.get_trace()
                for line in trace:
                    self.__log.warning(f"\tat {line.name}({line.filename}:{line.lineno})")

            else:
                self.__waitSet.detach_condition(mutation.get_condition())

        mutation.done()
            

    def waitAndHandle(self, dur: Duration) -> bool:
        """
        Waits for the supplied duration, and then handles conditions

        :param dur: The duration to wait for
        """

        if dur.infinite:
            giveup = float('inf')
        else:
            giveup = (time.time() + dur.sec) * 1000 + (dur.nanosec / 1_000_000)

        now = time.time() * 1000
        with self.__lock:
            while self.__currentServiceThread is not None and now < giveup:
                if dur.zero:
                    raise TimeoutError("Timed out waiting to become service thread")
                
                remaining = giveup - now
                if remaining > 0:
                    self.__lock.wait(remaining)

                now = time.time() * 1000

            self.__currentServiceThread = threading.current_thread()

        if not dur.zero and now >= giveup:
            raise TimeoutError("Timed out waiting to become service thread")
        
        cond_seq = self.__waitSet.wait(dur)
        try:
            for condition in cond_seq:
                ch = self.__conditionHandlers.get(id(condition))
                if ch is not None:
                    s = time.perf_counter_ns()
                    ch.conditionChanged(condition)
                    elapsed = time.perf_counter_ns() - s
                    if elapsed >= self.__WARNING_ELAPSED_TIME_NANOSECONDS:
                        self.__log.warning(f"{elapsed} ns to service {ch}")

                else:
                    self.__log.warning(f"No ConditionHandler for Condition {condition}")

        except TimeoutError:
            return False
        
        finally:
            with self.__lock:
                self.__currentServiceThread = None
                self.__lock.notify_all()


    def is_current_service_thread(self) -> bool:
        """
        Checks if the calling thread is the current service thread.

        :returns is_current_thread: True if the calling thread is the current service thread, or False otherwise
        """

        with self.__lock:
            return threading.current_thread() == self.__currentServiceThread


    def addHandler(self, condition: Condition, conditionHandler: ConditionHandler):
        """
        Adds a new condition with a handler to the `EventLoop`

        :param condition: The condition to add to the `EventLoop`
        :param conditionHandler: The `ConditionHandler` instance to handle the added `Condition`
        """
        m = Mutation(True, condition, conditionHandler)

        if self.is_current_service_thread():
            self._handleMutation(m)

        else:
            with self.__lock:
                self.__queuedMutations.append(m)
                self.__mutate.trigger_value = True

            m.await_mutation()


    def removeHandler(self, condition: Condition):
        """
        Removes the given condition from the `EventLoop`

        :param condition: The `Condition` instance to remove
        """

        m = Mutation(False, condition, None)
        if self.is_current_service_thread():
            self._handleMutation(m)

        else:
            with self.__lock:
                self.__queuedMutations.append(m)
                self.__mutate.trigger_value = True

            m.await_mutation()


    def doLater(self, c: Callable) -> None:
        """
        Sets a callable object to run later by adding it to the queuedCallables queue

        :param c: The callable object to add to the queue
        """

        with self.__lock:
            self.__queuedCallables.append(c)
            self.__callable.trigger_value = True


    def doNow(self, c: Callable) -> None:
        """
        Executes a callable object now

        :param c: The callable object to execute
        """

        if self.is_current_service_thread():
            c()

        else:
            nr = NestedCallable()
            with self.__lock:
                self.__queuedCallables.append(nr)
                self.__callable.trigger_value = True

            nr.wait_till_done()
