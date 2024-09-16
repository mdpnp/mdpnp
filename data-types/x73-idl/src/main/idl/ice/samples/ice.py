
# WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

# This file was generated from ice.idl
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


ice = idl.get_module("ice")

ice_UniqueDeviceIdentifier = str

ice.UniqueDeviceIdentifier = ice_UniqueDeviceIdentifier

ice_MetricIdentifier = str

ice.MetricIdentifier = ice_MetricIdentifier

ice_VendorMetricIdentifier = str

ice.VendorMetricIdentifier = ice_VendorMetricIdentifier

ice_InstanceIdentifier = idl.int32

ice.InstanceIdentifier = ice_InstanceIdentifier

ice_UnitIdentifier = str

ice.UnitIdentifier = ice_UnitIdentifier

ice_LongString = str

ice.LongString = ice_LongString

@idl.alias(
    annotations = [idl.bound(128), idl.element_annotations([idl.bound(128), idl.utf16]),]
)
class ice_ValidTargets:
    value: Sequence[str] = field(default_factory = list)

ice.ValidTargets = ice_ValidTargets

@idl.alias(
    annotations = [idl.bound(65530),]
)
class ice_ImageData:
    value: Sequence[idl.uint8] = field(default_factory = idl.array_factory(idl.uint8))

ice.ImageData = ice_ImageData

@idl.alias(
    annotations = [idl.bound(1024),]
)
class ice_Values:
    value: Sequence[idl.float32] = field(default_factory = idl.array_factory(idl.float32))

ice.Values = ice_Values

@idl.struct(
    type_annotations = [idl.final, idl.type_name("ice::Time_t")])
class ice_Time_t:
    sec: idl.int32 = 0
    nanosec: idl.int32 = 0

ice.Time_t = ice_Time_t

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::HeartBeat")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'type': [idl.bound(32)],
    }
)
class ice_HeartBeat:
    unique_device_identifier: str = ""
    type: str = ""

ice.HeartBeat = ice_HeartBeat

ice_HeartBeatTopic = "HeartBeat"

ice.HeartBeatTopic = ice_HeartBeatTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::TimeSync")],
    member_annotations = {
        'heartbeat_source': [idl.key, idl.bound(64)],
        'heartbeat_recipient': [idl.key, idl.bound(64)],
    }
)
class ice_TimeSync:
    heartbeat_source: str = ""
    heartbeat_recipient: str = ""
    source_source_timestamp: ice.Time_t = field(default_factory = ice.Time_t)
    recipient_receipt_timestamp: ice.Time_t = field(default_factory = ice.Time_t)

ice.TimeSync = ice_TimeSync

ice_TimeSyncTopic = "TimeSync"

ice.TimeSyncTopic = ice_TimeSyncTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::Image")],
    member_annotations = {
        'content_type': [idl.bound(64)],
    }
)
class ice_Image:
    content_type: str = ""
    image: ice.ImageData = field(default_factory = ice.ImageData)

ice.Image = ice_Image

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::DeviceIdentity")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'manufacturer': [idl.bound(128), idl.utf16],
        'model': [idl.bound(128), idl.utf16],
        'serial_number': [idl.bound(128), idl.utf16],
        'build': [idl.bound(128)],
        'operating_system': [idl.bound(128)],
    }
)
class ice_DeviceIdentity:
    unique_device_identifier: str = ""
    manufacturer: str = ""
    model: str = ""
    serial_number: str = ""
    icon: ice.Image = field(default_factory = ice.Image)
    build: str = ""
    operating_system: str = ""

ice.DeviceIdentity = ice_DeviceIdentity

ice_DeviceIdentityTopic = "DeviceIdentity"

ice.DeviceIdentityTopic = ice_DeviceIdentityTopic

@idl.enum
class ice_ConnectionState(IntEnum):
    Initial = 0
    Connected = 1
    Connecting = 2
    Negotiating = 3
    Terminal = 4

ice.ConnectionState = ice_ConnectionState

@idl.enum
class ice_ConnectionType(IntEnum):
    Serial = 0
    Simulated = 1
    Network = 2

ice.ConnectionType = ice_ConnectionType

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::DeviceConnectivity")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'info': [idl.bound(128), idl.utf16],
        'comPort': [idl.bound(16)],
    }
)
class ice_DeviceConnectivity:
    unique_device_identifier: str = ""
    state: ice.ConnectionState = ice.ConnectionState.Initial
    type: ice.ConnectionType = ice.ConnectionType.Serial
    info: str = ""
    valid_targets: ice.ValidTargets = field(default_factory = ice.ValidTargets)
    comPort: str = ""

ice.DeviceConnectivity = ice_DeviceConnectivity

ice_DeviceConnectivityTopic = "DeviceConnectivity"

ice.DeviceConnectivityTopic = ice_DeviceConnectivityTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::MDSConnectivity")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'partition': [idl.bound(128)],
    }
)
class ice_MDSConnectivity:
    unique_device_identifier: str = ""
    partition: str = ""

ice.MDSConnectivity = ice_MDSConnectivity

ice_MDSConnectivityTopic = "MDSConnectivity"

ice.MDSConnectivityTopic = ice_MDSConnectivityTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::MDSConnectivityObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'partition': [idl.bound(128)],
    }
)
class ice_MDSConnectivityObjective:
    unique_device_identifier: str = ""
    partition: str = ""

ice.MDSConnectivityObjective = ice_MDSConnectivityObjective

ice_MDSConnectivityObjectiveTopic = "MDSConnectivityObjective"

ice.MDSConnectivityObjectiveTopic = ice_MDSConnectivityObjectiveTopic

ice_MDC_PRESS_CUFF_NEXT_INFLATION = "MDC_PRESS_CUFF_NEXT_INFLATION"

ice.MDC_PRESS_CUFF_NEXT_INFLATION = ice_MDC_PRESS_CUFF_NEXT_INFLATION

ice_MDC_PRESS_CUFF_INFLATION = "MDC_PRESS_CUFF_INFLATION"

ice.MDC_PRESS_CUFF_INFLATION = ice_MDC_PRESS_CUFF_INFLATION

ice_MDC_HR_ECG_MODE = "MDC_HR_ECG_MODE"

ice.MDC_HR_ECG_MODE = ice_MDC_HR_ECG_MODE

ice_MDC_RR_APNEA = "MDC_RR_APNEA"

ice.MDC_RR_APNEA = ice_MDC_RR_APNEA

ice_MDC_SPO2_C_LOCK = "MDC_SPO2_C_LOCK"

ice.MDC_SPO2_C_LOCK = ice_MDC_SPO2_C_LOCK

ice_MDC_TIME_PD_INSPIRATORY = "MDC_TIME_PD_INSPIRATORY"

ice.MDC_TIME_PD_INSPIRATORY = ice_MDC_TIME_PD_INSPIRATORY

ice_MDC_START_INSPIRATORY_CYCLE = "MDC_START_INSPIRATORY_CYCLE"

ice.MDC_START_INSPIRATORY_CYCLE = ice_MDC_START_INSPIRATORY_CYCLE

ice_MDC_START_EXPIRATORY_CYCLE = "MDC_START_EXPIRATORY_CYCLE"

ice.MDC_START_EXPIRATORY_CYCLE = ice_MDC_START_EXPIRATORY_CYCLE

ice_MDC_END_OF_BREATH = "MDC_END_OF_BREATH"

ice.MDC_END_OF_BREATH = ice_MDC_END_OF_BREATH

ice_MDC_VENT_TIME_PD_PPV = "MDC_VENT_TIME_PD_PPV"

ice.MDC_VENT_TIME_PD_PPV = ice_MDC_VENT_TIME_PD_PPV

ice_MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP = 6250

ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP = ice_MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP

ice_MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS = 6222

ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS = ice_MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS

ice_MDC_EVT_STAT_OFF = 6226

ice.MDC_EVT_STAT_OFF = ice_MDC_EVT_STAT_OFF

ice_MDC_ECG_LEAD_I = "MDC_ECG_LEAD_I"

ice.MDC_ECG_LEAD_I = ice_MDC_ECG_LEAD_I

ice_MDC_ECG_LEAD_II = "MDC_ECG_LEAD_II"

ice.MDC_ECG_LEAD_II = ice_MDC_ECG_LEAD_II

ice_MDC_ECG_LEAD_III = "MDC_ECG_LEAD_III"

ice.MDC_ECG_LEAD_III = ice_MDC_ECG_LEAD_III

ice_MDC_ECG_LEAD_V1 = "MDC_ECG_LEAD_V1"

ice.MDC_ECG_LEAD_V1 = ice_MDC_ECG_LEAD_V1

ice_MDC_ECG_LEAD_V2 = "MDC_ECG_LEAD_V2"

ice.MDC_ECG_LEAD_V2 = ice_MDC_ECG_LEAD_V2

ice_MDC_ECG_LEAD_V3 = "MDC_ECG_LEAD_V3"

ice.MDC_ECG_LEAD_V3 = ice_MDC_ECG_LEAD_V3

ice_MDC_ECG_LEAD_V4 = "MDC_ECG_LEAD_V4"

ice.MDC_ECG_LEAD_V4 = ice_MDC_ECG_LEAD_V4

ice_MDC_ECG_LEAD_V5 = "MDC_ECG_LEAD_V5"

ice.MDC_ECG_LEAD_V5 = ice_MDC_ECG_LEAD_V5

ice_MDC_ECG_LEAD_V6 = "MDC_ECG_LEAD_V6"

ice.MDC_ECG_LEAD_V6 = ice_MDC_ECG_LEAD_V6

ice_MDC_ECG_LEAD_AVR = "MDC_ECG_LEAD_AVR"

ice.MDC_ECG_LEAD_AVR = ice_MDC_ECG_LEAD_AVR

ice_MDC_ECG_LEAD_AVF = "MDC_ECG_LEAD_AVF"

ice.MDC_ECG_LEAD_AVF = ice_MDC_ECG_LEAD_AVF

ice_MDC_ECG_LEAD_AVL = "MDC_ECG_LEAD_AVL"

ice.MDC_ECG_LEAD_AVL = ice_MDC_ECG_LEAD_AVL

ice_SP02_SOFT_CAN_GET_AVERAGING_RATE = "SP02_SOFT_CAN_GET_AVERAGING_RATE"

ice.SP02_SOFT_CAN_GET_AVERAGING_RATE = ice_SP02_SOFT_CAN_GET_AVERAGING_RATE

ice_SP02_OPER_CAN_GET_AVERAGING_RATE = "SPO2_OPER_CAN_GET_AVERAGING_RATE"

ice.SP02_OPER_CAN_GET_AVERAGING_RATE = ice_SP02_OPER_CAN_GET_AVERAGING_RATE

ice_SP02_AVERAGING_RATE = "SP02_AVERAGING_RATE"

ice.SP02_AVERAGING_RATE = ice_SP02_AVERAGING_RATE

ice_SP02_OPER_CAN_SET_AVERAGING_RATE = "SP02_OPER_CAN_SET_AVERAGING_RATE"

ice.SP02_OPER_CAN_SET_AVERAGING_RATE = ice_SP02_OPER_CAN_SET_AVERAGING_RATE

ice_SP02_SOFT_CAN_SET_AVERAGING_RATE = "SP02_SOFT_CAN_SET_AVERAGING_RATE"

ice.SP02_SOFT_CAN_SET_AVERAGING_RATE = ice_SP02_SOFT_CAN_SET_AVERAGING_RATE

ice_ICE_DERIVED_RESPIRATORY_RATE = "ICE_DERIVED_RESPIRATORY_RATE"

ice.ICE_DERIVED_RESPIRATORY_RATE = ice_ICE_DERIVED_RESPIRATORY_RATE

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::Numeric")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'metric_id': [idl.key, idl.bound(64)],
        'vendor_metric_id': [idl.key, idl.bound(64)],
        'instance_id': [idl.key, ],
        'unit_id': [idl.key, idl.bound(64)],
    }
)
class ice_Numeric:
    unique_device_identifier: str = ""
    metric_id: str = ""
    vendor_metric_id: str = ""
    instance_id: idl.int32 = 0
    unit_id: str = ""
    value: idl.float32 = 0.0
    device_time: ice.Time_t = field(default_factory = ice.Time_t)
    presentation_time: ice.Time_t = field(default_factory = ice.Time_t)

ice.Numeric = ice_Numeric

ice_NumericTopic = "Numeric"

ice.NumericTopic = ice_NumericTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::SampleArray")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'metric_id': [idl.key, idl.bound(64)],
        'vendor_metric_id': [idl.key, idl.bound(64)],
        'instance_id': [idl.key, ],
        'unit_id': [idl.key, idl.bound(64)],
        'frequency': [idl.key, ],
    }
)
class ice_SampleArray:
    unique_device_identifier: str = ""
    metric_id: str = ""
    vendor_metric_id: str = ""
    instance_id: idl.int32 = 0
    unit_id: str = ""
    frequency: idl.int32 = 0
    values: ice.Values = field(default_factory = ice.Values)
    device_time: ice.Time_t = field(default_factory = ice.Time_t)
    presentation_time: ice.Time_t = field(default_factory = ice.Time_t)

ice.SampleArray = ice_SampleArray

ice_SampleArrayTopic = "SampleArray"

ice.SampleArrayTopic = ice_SampleArrayTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::InfusionObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_InfusionObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    stopInfusion: bool = False
    head: idl.int32 = 0

ice.InfusionObjective = ice_InfusionObjective

ice_InfusionObjectiveTopic = "InfusionObjective"

ice.InfusionObjectiveTopic = ice_InfusionObjectiveTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::InfusionProgram")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_InfusionProgram:
    unique_device_identifier: str = ""
    requestor: str = ""
    head: idl.int32 = 0
    infusionRate: idl.float32 = 0.0
    VTBI: idl.float32 = 0.0
    bolusVolume: idl.float32 = 0.0
    bolusRate: idl.float32 = 0.0
    seconds: idl.int32 = 0

ice.InfusionProgram = ice_InfusionProgram

ice_InfusionProgramTopic = "InfusionProgram"

ice.InfusionProgramTopic = ice_InfusionProgramTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::VentModeObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_VentModeObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    newMode: idl.float32 = 0.0

ice.VentModeObjective = ice_VentModeObjective

ice_VentModeObjectiveTopic = "VentModeObjective"

ice.VentModeObjectiveTopic = ice_VentModeObjectiveTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::KeyValueObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
        'paramName': [idl.bound(128), idl.utf16],
    }
)
class ice_KeyValueObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    paramName: str = ""
    newValue: idl.float32 = 0.0

ice.KeyValueObjective = ice_KeyValueObjective

ice_KeyValueObjectiveTopic = "KeyValueObjective"

ice.KeyValueObjectiveTopic = ice_KeyValueObjectiveTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::RequestNKVSettingsObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_RequestNKVSettingsObjective:
    unique_device_identifier: str = ""
    requestor: str = ""

ice.RequestNKVSettingsObjective = ice_RequestNKVSettingsObjective

ice_RequestNKVSettingsObjectiveTopic = "RequestNKVSettingsObjective"

ice.RequestNKVSettingsObjectiveTopic = ice_RequestNKVSettingsObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::OximetryAveragingObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_OximetryAveragingObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    newAverageTime: idl.int32 = 0

ice.OximetryAveragingObjective = ice_OximetryAveragingObjective

ice_OximetryAveragingObjectiveTopic = "OximetryAveragingObjective"

ice.OximetryAveragingObjectiveTopic = ice_OximetryAveragingObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::FlowRateObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_FlowRateObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    newFlowRate: idl.float32 = 0.0

ice.FlowRateObjective = ice_FlowRateObjective

ice_FlowRateObjectiveTopic = "FlowRateObjective"

ice.FlowRateObjectiveTopic = ice_FlowRateObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::BPObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_BPObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    changeBy: idl.float32 = 0.0

ice.BPObjective = ice_BPObjective

ice_BPObjectiveTopic = "BPObjective"

ice.BPObjectiveTopic = ice_BPObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::BPPauseResumeObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'requestor': [idl.bound(128), idl.utf16],
    }
)
class ice_BPPauseResumeObjective:
    unique_device_identifier: str = ""
    requestor: str = ""
    running: bool = False

ice.BPPauseResumeObjective = ice_BPPauseResumeObjective

ice_BPPauseResumeObjectiveTopic = "BPPauseResumeObjective"

ice.BPPauseResumeObjectiveTopic = ice_BPPauseResumeObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::InfusionStatus")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'drug_name': [idl.bound(128), idl.utf16],
    }
)
class ice_InfusionStatus:
    unique_device_identifier: str = ""
    infusionActive: bool = False
    drug_name: str = ""
    drug_mass_mcg: idl.int32 = 0
    solution_volume_ml: idl.int32 = 0
    volume_to_be_infused_ml: idl.int32 = 0
    infusion_duration_seconds: idl.int32 = 0
    infusion_fraction_complete: idl.float32 = 0.0

ice.InfusionStatus = ice_InfusionStatus

ice_InfusionStatusTopic = "InfusionStatus"

ice.InfusionStatusTopic = ice_InfusionStatusTopic

@idl.enum
class ice_LimitType(IntEnum):
    low_limit = 0
    high_limit = 1

ice.LimitType = ice_LimitType

@idl.enum
class ice_AlarmPriority(IntEnum):
    low = 0
    medium = 1
    high = 2

ice.AlarmPriority = ice_AlarmPriority

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::AlarmLimit")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'metric_id': [idl.key, idl.bound(64)],
        'limit_type': [idl.key, ],
        'unit_identifier': [idl.bound(64)],
    }
)
class ice_AlarmLimit:
    unique_device_identifier: str = ""
    metric_id: str = ""
    limit_type: ice.LimitType = ice.LimitType.low_limit
    unit_identifier: str = ""
    value: idl.float32 = 0.0

ice.AlarmLimit = ice_AlarmLimit

ice_AlarmLimitTopic = "AlarmLimit"

ice.AlarmLimitTopic = ice_AlarmLimitTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::GlobalAlarmLimitObjective")],
    member_annotations = {
        'metric_id': [idl.key, idl.bound(64)],
        'limit_type': [idl.key, ],
        'unit_identifier': [idl.bound(64)],
    }
)
class ice_GlobalAlarmLimitObjective:
    metric_id: str = ""
    limit_type: ice.LimitType = ice.LimitType.low_limit
    unit_identifier: str = ""
    value: idl.float32 = 0.0

ice.GlobalAlarmLimitObjective = ice_GlobalAlarmLimitObjective

ice_GlobalAlarmLimitObjectiveTopic = "GlobalAlarmLimitObjective"

ice.GlobalAlarmLimitObjectiveTopic = ice_GlobalAlarmLimitObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::LocalAlarmLimitObjective")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'metric_id': [idl.key, idl.bound(64)],
        'limit_type': [idl.key, ],
        'unit_identifier': [idl.bound(64)],
    }
)
class ice_LocalAlarmLimitObjective:
    unique_device_identifier: str = ""
    metric_id: str = ""
    limit_type: ice.LimitType = ice.LimitType.low_limit
    unit_identifier: str = ""
    value: idl.float32 = 0.0

ice.LocalAlarmLimitObjective = ice_LocalAlarmLimitObjective

ice_LocalAlarmLimitObjectiveTopic = "LocalAlarmLimitObjective"

ice.LocalAlarmLimitObjectiveTopic = ice_LocalAlarmLimitObjectiveTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::DeviceAlertCondition")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'alert_state': [idl.bound(256)],
    }
)
class ice_DeviceAlertCondition:
    unique_device_identifier: str = ""
    alert_state: str = ""

ice.DeviceAlertCondition = ice_DeviceAlertCondition

ice_DeviceAlertConditionTopic = "DeviceAlertCondition"

ice.DeviceAlertConditionTopic = ice_DeviceAlertConditionTopic

@idl.struct(
    type_annotations = [idl.mutable, idl.type_name("ice::Alert")],
    member_annotations = {
        'unique_device_identifier': [idl.key, idl.bound(64)],
        'identifier': [idl.key, idl.bound(256)],
        'text': [idl.bound(256)],
    }
)
class ice_Alert:
    unique_device_identifier: str = ""
    identifier: str = ""
    text: str = ""

ice.Alert = ice_Alert

ice_PatientAlertTopic = "PatientAlert"

ice.PatientAlertTopic = ice_PatientAlertTopic

ice_TechnicalAlertTopic = "TechnicalAlert"

ice.TechnicalAlertTopic = ice_TechnicalAlertTopic

@idl.struct(
    type_annotations = [idl.type_name("ice::Patient")],
    member_annotations = {
        'mrn': [idl.key, idl.bound(16)],
        'given_name': [idl.bound(256), idl.utf16],
        'family_name': [idl.bound(256), idl.utf16],
    }
)
class ice_Patient:
    mrn: str = ""
    given_name: str = ""
    family_name: str = ""

ice.Patient = ice_Patient

ice_PatientTopic = "Patient"

ice.PatientTopic = ice_PatientTopic
