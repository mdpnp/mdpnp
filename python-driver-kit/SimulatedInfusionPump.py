import threading
import time


class SimulatedInfusionPump:
    """
    Class that simulates an infusion pump producing values.
    These values can then be read by a driver to publish to DDS.
    """

    _UPDATE_PERIOD = 1000
    _WAITING_PERIOD = 10_000


    def __init__(self) -> None:
        """
        Initialises a new `SimulatedInfusionPump
        """

        self.__drugName: str = "Morphine"
        self.__infusionActive: bool = True
        self.__interlockStop: bool = False
        self.__drugMassMcg: int = 20
        self.__solutionVolumeMl: int = 120
        self.__volumeToBeInfusedMl: int = 100
        self.__infusionDurationSeconds: int = 3600
        self.__infusionFractionComplete: float = 0.0
        self.__resumeTime: int = 0

        self._stop_event = threading.Event()
        self._thread = None


    def __infuse(self) -> None:
        """
        Simulates the infusion of a pump. Calls back to _receivePumpStatus
        """

        while not self._stop_event.is_set():

            now = time.time() * 1000

            if self.__resumeTime > 0:
                if self.__resumeTime <= now:
                    # restart infusion
                    self.__infusionFractionComplete = 0.0
                    self.__infusionActive = not self.__interlockStop
                    self.__resumeTime = 0
                else:
                    # waiting period
                    self.__infusionActive = False
            else:
                if self.__interlockStop:
                    self.__infusionActive = False
                else:
                    self.__infusionFractionComplete += 100.0 / self.__infusionDurationSeconds
                    if self.__infusionFractionComplete >= 100.0:
                        self.__infusionActive = False
                        self.__resumeTime = now + self._WAITING_PERIOD
                    else:
                        self.__infusionActive = True


            self._receivePumpStatus(
                self.__drugName,
                self.__infusionActive,
                self.__drugMassMcg,
                self.__solutionVolumeMl,
                self.__volumeToBeInfusedMl,
                self.__infusionDurationSeconds,
                self.__infusionFractionComplete,
            )
            
            time.sleep(self._UPDATE_PERIOD / 1000)


    def _receivePumpStatus(self, drug_name: str,
                        infustion_active: bool,
                        drug_mass_mcg: int,
                        solution_volume_ml: int,
                        volume_to_be_infused_ml: int,
                        infusion_duration_seconds: int,
                        infusion_fraction_complete: float) -> None:
        
        """
        A hook into the daemon thread to recieve the current status of the infusion pump.
        Called once per 'UPDATE_PERIOD'

        :param drug_name: The drug name stored by the pump
        :param drug_mass_mcg: The mass of drug being administered by the pump in micrograms
        :param solution_volume_ml: The volume of solution being pumped in total in milliletres
        :param volume_to_be_infused: The total volume to be infused
        :param infusion_duration_seconds: The length of time the pump will be infusing for
        :param infusion_fraction_complete: The fraction of the infusion completed so far
        """

        pass


    def connect(self) -> None:
        """
        Connects the pump, and starts the infusion simulation
        """

        self.disconnect()
        self._stop_event.clear()
        self._thread = threading.Thread(target=self.__infuse, daemon=True)
        self._thread.start()


    def disconnect(self) -> None:
        """
        Disconnects the pump and ends the infusion process
        """

        if self._thread is not None:
            self._stop_event.set()
            self._thread.join(timeout=1.0)
            self._thread = None


    def setInterlockStop(self, interLockStop: bool) -> None:
        """
        Sets the interlockStop of the simulated pump
        
        :param interLockStop: The new interlockStop value
        """

        self.__interlockStop = interLockStop
