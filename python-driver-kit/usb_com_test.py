import rti.connextdds as dds
from EventLoop import EventLoop
from SimInfusionPump import SimInfusionPump

import serial
import serial.tools.list_ports
import time
import re


def list_usb_ports():
    ports = [port for port in serial.tools.list_ports.comports() if "cu" in port.device.lower()]
    if not ports:
        print("No CU ports found.")
        return []
    
    print("Available CU ports:")
    for idx, port in enumerate(ports):
        print(f"{idx + 1}: {port.device} ({port.description})")
    
    return ports


def select_port(ports):
    while True:
        try:
            choice = int(input("Select a port number to open: "))
            if 1 <= choice <= len(ports):
                return ports[choice - 1].device
            else:
                print("Invalid number, try again.")
        except ValueError:
            print("Please enter a valid number.")


def filter_data(data: bytes) -> str | None:

    str_data = str(data).strip("b").strip("'").rstrip("\\r\\n")
    
    pattern = re.compile("^(.{18})\s+([0-9-*]+)\s+([0-9-*]+)\s+([0-9-*]+)(?:\s+([A-Z]{2}))*\s*$")
    matched = pattern.match(str_data)

    if matched:
        o2 = int(matched.groups()[1]) if matched.groups()[1] != '---' else 0
        bpm = int(matched.groups()[2]) if matched.groups()[2] != '---' else 0 
        return o2, bpm
    
    else:
        return None, None


def listen_on_port(port_name: str, device: SimInfusionPump, baudrate=9600, startup_commands=None):
    try:
        # Replace 'cu' with 'tty' for actual listening port
        port_name = port_name.replace("cu", "tty")
        with serial.Serial(port_name, baudrate, timeout=1) as ser:
            # Send startup commands if provided
            if startup_commands:
                for cmd in startup_commands:
                    ser.write(cmd)
                    # Optional: short delay between commands
                    time.sleep(0.1)
                print("Startup commands sent.")
            
            print(f"Listening on {port_name}... Press Ctrl+C to stop.")
            while True:
                if ser.in_waiting:
                    o2, bpm = filter_data(ser.readline())
                    if o2 and bpm:
                        # Print raw bytes or decode if needed
                        device.set_bmp_o2(bpm, o2)
                  
    except serial.SerialException as e:
        print(f"Error opening port {port_name}: {e}")
    except KeyboardInterrupt:
        print("\nStopped listening.")

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

    nellcor = SimInfusionPump(subscriber, publisher, eventLoop)
    nellcor.connect("Hacky NellcorN595")


    ports = list_usb_ports()
    if ports:
        port_name = select_port(ports).replace("cu", "tty")
        
        startup_commands = [
            b'\x03\x03',
            b'\x31\x0D\x0A',
            b'\x30\x0D\x0A'
        ]
        
        listen_on_port(port_name, nellcor, baudrate=9600, startup_commands=startup_commands)

