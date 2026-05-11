from AbstractDevice import AbstractDevice
from DeviceIdentityBuilder import DeviceIdentityBuilder
from ice import ice_DeviceIdentity

from abc import ABC
import logging


class AbstractSimulatedDevice(AbstractDevice, ABC):
    """
    Abstract class for all simulated devices to inherit from
    """

    __log: logging.Logger = logging.getLogger("AbstractSimulatedDevice")


    def __init__(self, subscriber, publisher, event_loop):
        super().__init__(subscriber, publisher, event_loop)
        AbstractSimulatedDevice.random_udi(self._deviceIdentity)
        self._writeDeviceIdentity()


    @staticmethod
    def random_udi(device_identity: ice_DeviceIdentity) -> None:
        """
        Generates a random Unique Device Identifier (UDI) and populates the provided `DeviceIdentity`

        :param device_identity: The `DeviceIdentity` instance to populate with the new random UDI 
        """

        device_identity.unique_device_identifier = DeviceIdentityBuilder.random_udi()
        AbstractSimulatedDevice.__log.debug(f"Created Random UDI: {device_identity.unique_device_identifier}")
