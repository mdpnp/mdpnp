import rticonnextdds_connector as rti
import time
import sys

from DeviceIdentity import DeviceIdentity
from Numeric import Numeric
from SampleArray import SampleArray

connector = rti.Connector("iceParticipantLibrary::iceParticipant", "/home/benstacey/from-simon/mdpnp/interop-lab/python/ice/icepython.xml")

dioutput = connector.get_output("DeviceIdentityPublisher::DeviceIdentityWriter")
numericoutput = connector.get_output("NumericPublisher::NumericWriter")
waveformoutput = connector.get_output("SampleArrayPublisher::SampleArrayWriter")

current_device_identity = DeviceIdentity()
current_numeric = Numeric()
current_waveform = SampleArray()

current_device_identity.update_fields({'unique_device_identifier': '8nT4kv5naVPhOrPyaiCOEamOOOzz16rrSMUC', 'manufacturer': 'ICE', 'model': 'Controllable Pump', 'serial_number': '', 'icon': {'content_type': 'image/png', 'image': []}, 'build': 'Development Version on 17.0.5', 'operating_system': 'Fedora Linux 37 (Workstation Edition) amd64 6.0.13-300.fc37.x86_64'})
current_device_identity.set_image('interop-lab/python/ice/pythonlogo.png')
current_device_identity.manufacturer = 'Python'

current_waveform.update_fields({'unique_device_identifier': '8nT4kv5naVPhOrPyaiCOEamOOOzz16rrSMUC', 'metric_id': 'MDC_ECG_LEAD_I', 'vendor_metric_id': '', 'instance_id': 0, 'unit_id': 'MDC_DIM_DIMLESS', 'frequency': 200, 'values': [125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 126, 127, 127, 128, 128, 128, 128, 128, 128, 128, 128, 128, 127, 126, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 123, 123, 126, 132, 140, 148, 154, 155, 155, 143, 133, 125, 121, 120, 120, 124, 125, 125, 125, 124, 124, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 126, 126, 126, 127, 127, 127, 127, 127, 128, 128, 128, 129, 130, 130, 130, 131, 131, 131, 131, 130, 130, 130, 129, 128, 127, 125, 125, 125, 124, 124, 124, 124, 124, 124, 125, 124, 124, 125, 124, 124, 124, 124, 124, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125], 'device_time': {'sec': 1688049900, 'nanosec': 0}, 'presentation_time': {'sec': 1688049900, 'nanosec': 0}})

dioutput.instance.set_dictionary(current_device_identity.publish_fields())

dioutput.write()
dioutput.wait()

while True:
    try:
        for rate in range(1, 10):
            current_time = round(time.time())
            current_numeric.update_fields({'unique_device_identifier': '8nT4kv5naVPhOrPyaiCOEamOOOzz16rrSMUC', 'metric_id': 'MDC_ECG_HEART_RATE', 'vendor_metric_id': '', 'instance_id': 0, 'unit_id': 'MDC_DIM_DIMLESS', 'value': rate, 'device_time': {'sec': 1688047573, 'nanosec': 367000000}, 'presentation_time': {'sec': 1688047573, 'nanosec': 367000000}})

            current_numeric.device_time.sec = current_time
            current_numeric.presentation_time.sec = current_time
            current_waveform.device_time.sec = current_time
            current_waveform.presentation_time.sec = current_time

            numericoutput.instance.set_dictionary(current_numeric.publish_fields())
            waveformoutput.instance.set_dictionary(current_waveform.publish_fields())

            numericoutput.write()
            waveformoutput.write()

            time.sleep(1.0)

    except KeyboardInterrupt:
        print("Need to clean up")
        dioutput.write(action='unregister')
        dioutput.write(action='dispose')
        numericoutput.write(action='unregister')
        numericoutput.write(action='dispose')
        sys.exit(0)
        