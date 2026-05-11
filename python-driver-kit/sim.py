
# WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

# This file was generated from sim.idl
# using RTI Code Generator (rtiddsgen) version 4.3.0.
# The rtiddsgen tool is part of the RTI Connext DDS distribution.
# For more information, type 'rtiddsgen -help' at a command shell
# or consult the Code Generator User's Manual.

from dataclasses import field
from typing import Union, Sequence, Optional
import rti.idl as idl
from enum import IntEnum
import sys
import os

sys.path.append(os.path.join(os.path.dirname(__file__), 'ice/'))
from ice import *
sys.path.pop()

ice = idl.get_module("ice")

@idl.struct(
    type_annotations = [idl.type_name("ice::GlobalSimulationObjective")],
    member_annotations = {
        'metric_id': [idl.key, idl.bound(64)],
    }
)
class ice_GlobalSimulationObjective:
    metric_id: str = ""
    value: idl.float32 = 0.0
    jitterStep: idl.float32 = 0.0
    floor: idl.float32 = 0.0
    ceil: idl.float32 = 0.0

ice.GlobalSimulationObjective = ice_GlobalSimulationObjective

ice_GlobalSimulationObjectiveTopic = "GlobalSimulationObjective"

ice.GlobalSimulationObjectiveTopic = ice_GlobalSimulationObjectiveTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::LocalSimulationObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'metric_id': [idl.key, idl.bound(64)],
    }
)
class ice_LocalSimulationObjective:
    unique_device_identifier: str = ""
    metric_id: str = ""
    value: idl.float32 = 0.0

ice.LocalSimulationObjective = ice_LocalSimulationObjective

ice_LocalSimulationObjectiveTopic = "LocalSimulationObjective"

ice.LocalSimulationObjectiveTopic = ice_LocalSimulationObjectiveTopic
