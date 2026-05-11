from ice import ice_Numeric
import rosetta
import units

from AbstractSimulatedDevice import AbstractSimulatedDevice
from AbstractSerialDevice import AbstractSerialDevice
import DeviceClock
from AbstractDevice import InstanceHolder
from EventLoop import EventLoop

import rti.connextdds as dds

from overrides import overrides
import time
import random


class NellcorN595(AbstractSerialDevice):
    """
    A Python driver for the NellcorN595 heartrate monitor
    """

    def __init__(self, subscriber, publisher, event_loop, count_serial_ports = 1):
        """
        Initialises a new `NellcorN595` driver

        :param subscriber: The `rti.connextdds.Subscriber` instance for the current subscriber
        :param publisher: The `rti.connextdds.Publisher` instance for the current publisher
        :param event_loop: The `EventLoop` instance for this device
        :param count_serial_ports: The number of serial ports the device has
        :raises RuntimeError: If the _maximumQuietTime of a given idx is less than or equal to 0
        """

        self._pulse: InstanceHolder[ice_Numeric] = None
        self._spo2: InstanceHolder[ice_Numeric] = None

        super().__init__(subscriber, publisher, event_loop, count_serial_ports)
        AbstractSimulatedDevice.random_udi(self._deviceIdentity)
        self._deviceIdentity.manufacturer = "Nellcor"
        self._deviceIdentity.model = "595"
        self._writeDeviceIdentity()


    @overrides
    def doInitCommands(idx):
        pass


    def firePulseOximeter(self):

        sampleTime = DeviceClock.ReadingImpl(time_value=time.time()*1000)

        self._pulse = self._numericSample(self._pulse, random.randint(60, 80), sampleTime, rosetta.rosetta_MDC_PULS_OXIM_PULS_RATE, "BEN_VENDOR", units.rosetta_MDC_DIM_BEAT_PER_MIN)

        self._spo2 = self._numericSample(self._spo2, random.randint(90, 100), sampleTime, rosetta.rosetta_MDC_PULS_OXIM_SAT_O2, "", units.rosetta_MDC_DIM_PERCENT)


    @overrides
    def process(idx, inputStream, outStream):
        return super().process(inputStream, outStream)


    @overrides
    def _getMaximumQuietTime(self, idx) -> int:
        return 2200
    

    @overrides
    def _getConnectInterval(self, idx) -> int:
        return 4000
    

    @overrides
    def _getNegotiateInterval(self, idx) -> int:
        return 2000


    @overrides
    def _getIconPath(self) -> str:
        return "interop-lab/demo-devices/src/main/resources/org/mdpnp/devices/nellcor/pulseox/n595.png"
    

if __name__ == "__main__":

    qos_provider = dds.QosProvider("python-driver-kit/USER_QOS_PROFILES.xml")
    particpant_qos = qos_provider.participant_qos_from_profile("ice_Library::ice_Profile")
    particpant_qos.resource_limits.type_code_max_serialized_length = 512 # AGAIN TEMP QOS FIX
    sub_qos = qos_provider.subscriber_qos_from_profile("ice_Library::ice_Profile")
    pub_qos = qos_provider.publisher_qos_from_profile("ice_Library::ice_Profile")

    participant = dds.DomainParticipant(0, particpant_qos)
    subscriber = dds.Subscriber(participant, sub_qos)
    publisher = dds.Publisher(participant, pub_qos)
    eventLoop = EventLoop()

    nellcor = NellcorN595(subscriber, publisher, eventLoop)
    nellcor.connect("TestNellcorN595")