import logging
import traceback
from typing import TypeVar, Generic, Final
import threading
import time


T = TypeVar('T')

class StateMachine(Generic[T]):
    """
    Handles the `ConnectionState` of an OpenICE device
    """

    _log: logging.Logger = logging.getLogger("StateMachine")


    def __init__(self, legalTransitions: list[list[T]], initialState: T, transitionNote: str) -> None:
        """
        Initialises a new `StateMachine` instance

        :param legalTransitions: A list of pairs of states, showing legal transitions between state 0, and state 1
        :param initialState: The initial state of the `StateMachine`
        :param transitionNote: The initial transition note for the `StateMachine`
        """

        self.__state: T = initialState
        self.__legalTransitions: Final[list[list[T]]] = legalTransitions
        self.__transitionNote: str = transitionNote
        self._lock: threading.Condition = threading.Condition()


    def wait(self, state: T, timeout: int) -> bool:
        """
        Waits for the current state to reach the provided state, or for the timeout to be reached
        
        :param state: The target state waiting to be reached
        :param timout: The max timeout to wait for before failing (in milliseconds)
        :returns reached: True if the state was reached within the timeout period, or False if not
        """
        
        with self._lock:
            giveup: int = (time.time() * 1000) + timeout

            if timeout < 10:
                self.__log.warning(f"Blocking in 10ms increments so timeout of {timeout}ms is promoted")

            if (timeout % 10) != 0:
                self.__log.warning(f"Blocking in 10ms increments so timeout of {timeout}ms made coarser")


            while not state == self.__state and (time.time() * 1000) < giveup:
                try:
                    self._lock.wait(0.01)
                except Exception as e:
                    self.__log.error(f"Interrupted waiting for {state}", exc_info=e)

            return state == self.__state
        

    def legalTransition(self, state: T) -> bool:
        """
        Determines whether it is legal to transition from the current state to the provided new state

        :param state: The new state to validate whether or not it is legal to transition to
        :returns is_legal: True if it is legal to transition to the provided state, or False if not
        """

        with self._lock:
            for i, _ in enumerate(self.__legalTransitions):
                if self.__state == self.__legalTransitions[i][0] and state == self.__legalTransitions[i][1]:
                    return True
                
            return False


    def emit(self, newState: T, oldState: T, transitionNote: str) -> None:
        pass

    
    def getState(self) -> T:
        """
        Gets the current state of the `StateMachine` instance

        :returns state: The current state
        """

        with self._lock:
            return self.__state


    def getTransitionNote(self) -> str:
        """
        Gets the tranition note of the current `StateMachine` instance
        
        :returns note: The transition note of the current instance
        """

        with self._lock:
            return self.__transitionNote
    

    def transitionIfLegal(self, state: T, transitionNote: str) -> bool:
        """
        Transitions the `StateMachine` to the new state if it is legal. Also updates the transitionNote
        
        :param state: The new state to transition to if it is legal
        :param transitionNote: The transitionNote to store
        :returns legal: True if the state transition was legal and therefore successful, or False otherwise
        """

        with self._lock:
            if self.legalTransition(state):
                oldState = self.__state
                self.__state = state
                self.__transitionNote = transitionNote
                self._lock.notify_all()
                self.emit(state, oldState, transitionNote)
                return True
            
            else:
                stack = ''.join(traceback.format_stack())
                self.__log.debug(f"NO {self.__state} --/--> {state}\n{stack}")
                return False


    def transitionWhenLegal(self, state: T, transitionNote: str, timeout: int = None, priorState: list[T] = None) -> bool:
        """
        Transitions the state of the `StateMachine` when it becomes legal

        :param state: The new state to transition to when it becomes legal
        :param transitionNote: A new transition note to go with the transition
        :param timeout: An option timeout that controls how long to wait before failing. If not provided, the timeout is calculated using '_getTransitionTimeout'
        :param priorState: An option list of priorStates. If this is provided, the old state adds to this list
        :returns successful: True if the state is transitioned during the timeout period, otherwise False.
        :raises RuntimeError: If the timeout is reached and state cannot be transitioned, or if another exceptions occur in the process of executing
        """

        with self._lock:
            if timeout is None:
                timeout = self._getTransitionTimeout()

            giveup = (time.time() * 1000) + timeout

            while not self.legalTransition(state) and (time.time() * 1000) < giveup:
                try:
                    self._lock.wait(giveup - (time.time() * 1000))

                except Exception as e:
                    raise RuntimeError(e)
                
            _priorState = self.__state
            if not self.transitionIfLegal(state, transitionNote):

                if self._isTimeoutFatal():
                    raise RuntimeError(f"Unable to transition from {self.__state} to {state} after waiting {timeout}ms")
                else:
                    return False
                
            if priorState:
                priorState[0] = _priorState

            return True


    def _getTransitionTimeout() -> int:
        """
        Gets the timeout for each transition
        
        :returns timeout: The timeout for a transition (in milliseconds)
        """

        return 2000
    

    def _isTimeoutFatal() -> bool:
        """
        Returns whether the timeout was fatal

        :returns fatal: True if the timeout was fatal, or False otherwise
        """

        return True