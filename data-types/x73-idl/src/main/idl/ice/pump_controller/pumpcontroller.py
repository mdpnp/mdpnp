
# (c) Copyright, Real-Time Innovations, 2022.  All rights reserved.
# RTI grants Licensee a license to use, modify, compile, and create derivative
# works of the software solely for use with RTI Connext DDS. Licensee may
# redistribute copies of the software provided that all such copies are subject
# to this license. The software is provided "as is", with no warranty of any
# type, including any warranty for fitness for any purpose. RTI is under no
# obligation to maintain or support the software. RTI shall not be liable for
# any incidental or consequential damages arising out of the use or inability
# to use the software.

import argparse
import sys
from dataclasses import dataclass

from pump_publisher import ice_InfusionProgramPublisher
from udi_subscriber import ice_DeviceIdentitySubscriber

@dataclass
class ApplicationArguments:
    domain: int
    udi: str
    rate: float

def main():
    """ The Main function runs the publisher or the subscriber """
    args: ApplicationArguments = parse_arguments()

    print(f"Looking for device identity record for pump on domain {args.domain}")
    pump_udi=ice_DeviceIdentitySubscriber.get_udi(
        domain_id=args.domain)

    print(f"Available pumps are");
    i=0
    while i < len(pump_udi):
        print(f"{i+1} {pump_udi[i]}")
        i=i+1

    print("Enter a number to control the pump with that UDI")
    udiIndex=int(input())

    print(f"Running ice_InfusionProgramPublisher on domain {args.domain}")
    ice_InfusionProgramPublisher.run_publisher(
        domain_id=args.domain,
        #udi=args.udi,
        udi=pump_udi[udiIndex-1],
        rate=args.rate)

def check_sample_count_range(value):
    """ Check if the sample count is in the expected range """
    try:
        value = int(value)
        if value <= 0:
            raise argparse.ArgumentTypeError(
                f"The sample count ({value}) must be larger than 0")
    except ValueError:
        raise argparse.ArgumentTypeError(
            f"The sample count ({value}) must be an integer")

    return value

def check_domain_range(value):
    """ Check if the domain id is in the expected range """
    try:
        value = int(value)
        if value < 0:
            raise argparse.ArgumentTypeError(
                f"The domain id ({value}) must be equal to or larger than 0")
    except ValueError:
        raise argparse.ArgumentTypeError(
            f"The domain id ({value}) must be an integer")

    return value

def check_flow_rate(value):
    """ Check if the flow rate is in the expected range """
    try:
        value = float(value)
        if value < 0:
            raise argparse.ArgumentTypeError(
                f"The float rate ({value}) must be equal to or larger than 0")
        if value > 1500:
            raise argparse.ArgumentTypeError(
                f"The float rate ({value}) must be less than or equal to 1500")
    except ValueError:
        raise argparse.ArgumentTypeError(
            f"The float rate ({value}) must be a floating point value")

    return value

def parse_arguments():
    """Uses the argparse library to parse the command line arguments. """
    
    parser = argparse.ArgumentParser(
        description="Example program that publishes an objective of type InfusionProgram.")

    parser.add_argument(
        "-d",
        "--domain",
        help="Domain ID used to create the DomainParticipant",
        default=0,
        type=check_domain_range,
        metavar="[>=0]")
    #parser.add_argument(
    #    "-u",
    #    "--udi",
    #    help="Device UDI to write the objective for")
    parser.add_argument(
        "-r",
        "--rate",
        help="Flow rate to request from the pump",
        type=check_flow_rate,
        default=5.0)

    return parser.parse_args(namespace=ApplicationArguments)

if __name__ == "__main__":
    main()
