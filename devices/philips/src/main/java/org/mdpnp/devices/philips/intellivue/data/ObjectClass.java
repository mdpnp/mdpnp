package org.mdpnp.devices.philips.intellivue.data;

public enum ObjectClass {
    /**
     * VMO
     * Hex: 0x01
     * Dec: 1
     */
    NOM_MOC_VMO,

    /**
     * Numeric
     * Hex: 0x06
     * Dec: 6
     */
    NOM_MOC_VMO_METRIC_NU,

    /**
     * RealtimeSampleArray
     * Hex: 0x09
     * Dec: 9
     */
    NOM_MOC_VMO_METRIC_SA_RT,

    /**
     * MDS
     * Hex: 0x21
     * Dec: 33
     */
    NOM_MOC_VMS_MDS,

    /**
     * CompositSingleBedMDS
     * Hex: 0x23
     * Dec: 35
     */
    NOM_MOC_VMS_MDS_COMPOS_SINGLE_BED,

    /**
     * SimpleMDS
     * Hex: 0x25
     * Dec: 37
     */
    NOM_MOC_VMS_MDS_SIMP,

    /**
     * Battery
     * Hex: 0x29
     * Dec: 41
     */
    NOM_MOC_BATT,

    /**
     * PatientDemographics
     * Hex: 0x2A
     * Dec: 42
     */
    NOM_MOC_PT_DEMOG,

    /**
     * AlertMonitor
     * Hex: 0x36
     * Dec: 54
     */
    NOM_MOC_VMO_AL_MON,

    /**
     * PollAction
     * Hex: 0xC16
     * Dec: 3094
     */
    NOM_ACT_POLL_MDIB_DATA,

    /**
     * MDSCreate
     * Hex: 0xD06
     * Dec: 3334
     */
    NOM_NOTI_MDS_CREAT,

    /**
     * ConnectIndication
     * Hex: 0xD17
     * Dec: 3351
     */
    NOM_NOTI_CONN_INDIC,

    /**
     * satO2
     * Hex: 0x100A
     * Dec: 4106
     */
    NOM_DEV_ANALY_SAT_O2_VMD,

    /**
     * GasAnalyzer
     * Hex: 0x1011
     * Dec: 4113
     */
    NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_MDS,

    /**
     * Gas
     * Hex: 0x1012
     * Dec: 4114
     */
    NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_VMD,

    /**
     * FlowAway
     * Hex: 0x1022
     * Dec: 4130
     */
    NOM_DEV_ANALY_FLOW_AWAY_VMD,

    /**
     * C.O.
     * Hex: 0x1026
     * Dec: 4134
     */
    NOM_DEV_ANALY_CARD_OUTPUT_VMD,

    /**
     * Press
     * Hex: 0x104E
     * Dec: 4174
     */
    NOM_DEV_ANALY_PRESS_BLD_VMD,

    /**
     * RR
     * Hex: 0x105A
     * Dec: 4186
     */
    NOM_DEV_ANALY_RESP_RATE_VMD,

    /**
     * Calculation
     * Hex: 0x106E
     * Dec: 4206
     */
    NOM_DEV_CALC_VMD,

    /**
     * ECG
     * Hex: 0x10A6
     * Dec: 4262
     */
    NOM_DEV_ECG_VMD,

    /**
     * SkinGas
     * Hex: 0x10A8
     * Dec: 4264
     */
    NOM_DEV_METER_CONC_SKIN_GAS,

    /**
     * SkinGas
     * Hex: 0x10A9
     * Dec: 4265
     */
    NOM_DEV_METER_CONC_SKIN_GAS_MDS,

    /**
     * SkinGas
     * Hex: 0x10AA
     * Dec: 4266
     */
    NOM_DEV_METER_CONC_SKIN_GAS_VMD,

    /**
     * EEG
     * Hex: 0x10B2
     * Dec: 4274
     */
    NOM_DEV_EEG_VMD,

    /**
     * BloodFlow
     * Hex: 0x10BC
     * Dec: 4284
     */
    NOM_DEV_METER_FLOW_BLD,

    /**
     * BloodTemp
     * Hex: 0x10FE
     * Dec: 4350
     */
    NOM_DEV_METER_TEMP_BLD_VMD,

    /**
     * Temp
     * Hex: 0x110E
     * Dec: 4366
     */
    NOM_DEV_METER_TEMP_VMD,

    /**
     * BldChem
     * Hex: 0x112E
     * Dec: 4398
     */
    NOM_DEV_MON_BLD_CHEM_MULTI_PARAM_VMD,

    /**
     * Multi-Param
     * Hex: 0x114D
     * Dec: 4429
     */
    NOM_DEV_MON_PHYSIO_MULTI_PARAM_MDS,

    /**
     * PumpInfus
     * Hex: 0x1161
     * Dec: 4449
     */
    NOM_DEV_PUMP_INFUS_MDS,

    /**
     * Ventilator
     * Hex: 0x1171
     * Dec: 4465
     */
    NOM_DEV_SYS_PT_VENT_MDS,

    /**
     * Ventilator
     * Hex: 0x1172
     * Dec: 4466
     */
    NOM_DEV_SYS_PT_VENT_VMD,

    /**
     * Multi-ModalMDS
     * Hex: 0x118D
     * Dec: 4493
     */
    NOM_DEV_SYS_MULTI_MODAL_MDS,

    /**
     * Multi-Modal
     * Hex: 0x118E
     * Dec: 4494
     */
    NOM_DEV_SYS_MULTI_MODAL_VMD,

    /**
     * Aneshesia
     * Hex: 0x119A
     * Dec: 4506
     */
    NOM_DEV_SYS_ANESTH_VMD,

    /**
     * General
     * Hex: 0x1402
     * Dec: 5122
     */
    NOM_DEV_GENERAL_VMD,

    /**
     * ECG-Resp
     * Hex: 0x140A
     * Dec: 5130
     */
    NOM_DEV_ECG_RESP_VMD,

    /**
     * Arrythmia
     * Hex: 0x140E
     * Dec: 5134
     */
    NOM_DEV_ARRHY_VMD,

    /**
     * Pulse
     * Hex: 0x1412
     * Dec: 5138
     */
    NOM_DEV_PULS_VMD,

    /**
     * ST
     * Hex: 0x1416
     * Dec: 5142
     */
    NOM_DEV_ST_VMD,

    /**
     * CO2
     * Hex: 0x141A
     * Dec: 5146
     */
    NOM_DEV_CO2_VMD,

    /**
     * NoninvPress
     * Hex: 0x141E
     * Dec: 5150
     */
    NOM_DEV_PRESS_BLD_NONINV_VMD,

    /**
     * CerebPerf
     * Hex: 0x1422
     * Dec: 5154
     */
    NOM_DEV_CEREB_PERF_VMD,

    /**
     * CO2CTS
     * Hex: 0x1426
     * Dec: 5158
     */
    NOM_DEV_CO2_CTS_VMD,

    /**
     * TcCO2
     * Hex: 0x142A
     * Dec: 5162
     */
    NOM_DEV_CO2_TCUT_VMD,

    /**
     * O2
     * Hex: 0x142E
     * Dec: 5166
     */
    NOM_DEV_O2_VMD,

    /**
     * CTS
     * Hex: 0x1432
     * Dec: 5170
     */
    NOM_DEV_O2_CTS_VMD,

    /**
     * Tc02
     * Hex: 0x1436
     * Dec: 5174
     */
    NOM_DEV_O2_TCUT_VMD,

    /**
     * DiffTemp
     * Hex: 0x143A
     * Dec: 5178
     */
    NOM_DEV_TEMP_DIFF_VMD,

    /**
     * Control
     * Hex: 0x143E
     * Dec: 5182
     */
    NOM_DEV_CNTRL_VMD,

    /**
     * Wedge
     * Hex: 0x1446
     * Dec: 5190
     */
    NOM_DEV_WEDGE_VMD,

    /**
     * O2VentSat
     * Hex: 0x144A
     * Dec: 5194
     */
    NOM_DEV_O2_VEN_SAT_VMD,

    /**
     * HR
     * Hex: 0x1452
     * Dec: 5202
     */
    NOM_DEV_CARD_RATE_VMD,

    /**
     * configMDS
     * Hex: 0x1459
     * Dec: 5209
     */
    NOM_DEV_SYS_VS_CONFIG_MDS,

    /**
     * unconfigMDS
     * Hex: 0x145D
     * Dec: 5213
     */
    NOM_DEV_SYS_VS_UNCONFIG_MDS,

    /**
     * Pleth
     * Hex: 0x1476
     * Dec: 5238
     */
    NOM_DEV_PLETH_VMD,

    /**
     * PrivateAttribute112
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_SAT_O2_TONE_FREQ,

    /**
     * Key
     * Hex: 0xF090
     * Dec: 61584
     */
    NOM_OBJ_HIF_KEY,

    /**
     * Display
     * Hex: 0xF0B0
     * Dec: 61616
     */
    NOM_OBJ_DISP,

    /**
     * SoundGenerator
     * Hex: 0xF0D0
     * Dec: 61648
     */
    NOM_OBJ_SOUND_GEN,

    /**
     * Setting
     * Hex: 0xF0D1
     * Dec: 61649
     */
    NOM_OBJ_SETTING,

    /**
     * Printer
     * Hex: 0xF0D2
     * Dec: 61650
     */
    NOM_OBJ_PRINTER,

    /**
     * Event
     * Hex: 0xF0F3
     * Dec: 61683
     */
    NOM_OBJ_EVENT,

    /**
     * BatteryCharger
     * Hex: 0xF0FA
     * Dec: 61690
     */
    NOM_OBJ_BATT_CHARGER,

    /**
     * ECGout
     * Hex: 0xF0FB
     * Dec: 61691
     */
    NOM_OBJ_ECG_OUT,

    /**
     * InputDevice
     * Hex: 0xF0FC
     * Dec: 61692
     */
    NOM_OBJ_INPUT_DEV,

    /**
     * Network
     * Hex: 0xF0FD
     * Dec: 61693
     */
    NOM_OBJ_NETWORK,

    /**
     * QuicklinkBar
     * Hex: 0xF0FE
     * Dec: 61694
     */
    NOM_OBJ_QUICKLINK,

    /**
     * Speaker
     * Hex: 0xF0FF
     * Dec: 61695
     */
    NOM_OBJ_SPEAKER,

    /**
     * Pump
     * Hex: 0xF114
     * Dec: 61716
     */
    NOM_OBJ_PUMP,

    /**
     * IR
     * Hex: 0xF115
     * Dec: 61717
     */
    NOM_OBJ_IR,

    /**
     * ExtendedPollAction
     * Hex: 0xF13B
     * Dec: 61755
     */
    NOM_ACT_POLL_MDIB_DATA_EXT,

    /**
     * PulsCont
     * Hex: 0xF168
     * Dec: 61800
     */
    NOM_DEV_ANALY_PULS_CONT,

    /**
     * BIS
     * Hex: 0xF16E
     * Dec: 61806
     */
    NOM_DEV_ANALY_BISPECTRAL_INDEX_VMD,

    /**
     * HiresTrend
     * Hex: 0xF17C
     * Dec: 61820
     */
    NOM_DEV_HIRES_TREND,

    /**
     * HiresTrend
     * Hex: 0xF17D
     * Dec: 61821
     */
    NOM_DEV_HIRES_TREND_MDS,

    /**
     * HiresTrend
     * Hex: 0xF17E
     * Dec: 61822
     */
    NOM_DEV_HIRES_TREND_VMD,

    /**
     * Events
     * Hex: 0xF182
     * Dec: 61826
     */
    NOM_DEV_MON_PT_EVENT_VMD,

    /**
     * DerivedMeasurement
     * Hex: 0xF184
     * Dec: 61828
     */
    NOM_DEV_DERIVED_MSMT,

    /**
     * DerivedMeasurement
     * Hex: 0xF185
     * Dec: 61829
     */
    NOM_DEV_DERIVED_MSMT_MDS,

    /**
     * DerivedMeasurement
     * Hex: 0xF186
     * Dec: 61830
     */
    NOM_DEV_DERIVED_MSMT_VMD,

    /**
     * Sensor
     * Hex: 0xF1CE
     * Dec: 61902
     */
    NOM_OBJ_SENSOR,

    /**
     * Transducer
     * Hex: 0xF1CF
     * Dec: 61903
     */
    NOM_OBJ_XDUCR,

    /**
     * Channel1
     * Hex: 0xF1DC
     * Dec: 61916
     */
    NOM_OBJ_CHAN_1,

    /**
     * Channel2
     * Hex: 0xF1DD
     * Dec: 61917
     */
    NOM_OBJ_CHAN_2,

    /**
     * Agent1
     * Hex: 0xF1DE
     * Dec: 61918
     */
    NOM_OBJ_AWAY_AGENT_1,

    /**
     * Agent2
     * Hex: 0xF1DF
     * Dec: 61919
     */
    NOM_OBJ_AWAY_AGENT_2,

    /**
     * MOUSE
     * Hex: 0xF21F
     * Dec: 61983
     */
    NOM_OBJ_HIF_MOUSE,

    /**
     * TOUCH
     * Hex: 0xF220
     * Dec: 61984
     */
    NOM_OBJ_HIF_TOUCH,

    /**
     * Speedpoint
     * Hex: 0xF221
     * Dec: 61985
     */
    NOM_OBJ_HIF_SPEEDPOINT,

    /**
     * Alarmbox
     * Hex: 0xF222
     * Dec: 61986
     */
    NOM_OBJ_HIF_ALARMBOX,

    /**
     * I2CBus
     * Hex: 0xF223
     * Dec: 61987
     */
    NOM_OBJ_BUS_I2C,

    /**
     * 2ndCPU
     * Hex: 0xF224
     * Dec: 61988
     */
    NOM_OBJ_CPU_SEC,

    /**
     * LED
     * Hex: 0xF226
     * Dec: 61990
     */
    NOM_OBJ_LED,

    /**
     * Relay
     * Hex: 0xF227
     * Dec: 61991
     */
    NOM_OBJ_RELAY,

    /**
     * Battery1
     * Hex: 0xF22C
     * Dec: 61996
     */
    NOM_OBJ_BATT_1,

    /**
     * Battery2
     * Hex: 0xF22D
     * Dec: 61997
     */
    NOM_OBJ_BATT_2,

    /**
     * 2ndDisplay
     * Hex: 0xF22E
     * Dec: 61998
     */
    NOM_OBJ_DISP_SEC,

    /**
     * AGM
     * Hex: 0xF22F
     * Dec: 61999
     */
    NOM_OBJ_AGM,

    /**
     * TeleMon
     * Hex: 0xF23E
     * Dec: 62014
     */
    NOM_OBJ_TELEMON,

    /**
     * Transmitter
     * Hex: 0xF23F
     * Dec: 62015
     */
    NOM_OBJ_XMTR,

    /**
     * Cable
     * Hex: 0xF240
     * Dec: 62016
     */
    NOM_OBJ_CABLE,

    /**
     * TelemetryTransmitter
     * Hex: 0xF265
     * Dec: 62053
     */
    NOM_OBJ_TELEMETRY_XMTR,

    /**
     * MMS
     * Hex: 0xF276
     * Dec: 62070
     */
    NOM_OBJ_MMS,

    /**
     * ThirdDisplay
     * Hex: 0xF279
     * Dec: 62073
     */
    NOM_OBJ_DISP_THIRD,

    /**
     * Battery
     * Hex: 0xF27E
     * Dec: 62078
     */
    NOM_OBJ_BATT,

    /**
     * BatteryTele
     * Hex: 0xF28B
     * Dec: 62091
     */
    NOM_OBJ_BATT_TELE,

    /**
     * ProtocolWatchgeneric
     * Hex: 0xF28F
     * Dec: 62095
     */
    NOM_DEV_PROT_WATCH_CHAN,

    /**
     * ProtocolWatchProtocolNo.1
     * Hex: 0xF291
     * Dec: 62097
     */
    NOM_OBJ_PROT_WATCH_1,

    /**
     * ProtocolWatchProtocolNo.2
     * Hex: 0xF292
     * Dec: 62098
     */
    NOM_OBJ_PROT_WATCH_2,

    /**
     * ProtocolWatchProtocolNo.3
     * Hex: 0xF293
     * Dec: 62099
     */
    NOM_OBJ_PROT_WATCH_3,

    /**
     * ECGSync
     * Hex: 0xF2C3
     * Dec: 62147
     */
    NOM_OBJ_ECG_SYNC,

    /**
     * Metabolism
     * Hex: 0xF2D2
     * Dec: 62162
     */
    NOM_DEV_METAB_VMD,

    /**
     * SENSORO2CO2
     * Hex: 0xF2D5
     * Dec: 62165
     */
    NOM_OBJ_SENSOR_O2_CO2,

    /**
     * SRRInterface1
     * Hex: 0xF300
     * Dec: 62208
     */
    NOM_OBJ_SRR_IF_1,

    /**
     * REMOTEDISPLAY114
     * Hex: 0xF314
     * Dec: 62228
     */
    NOM_OBJ_DISP_REMOTE,

;
    
    public OIDType asOID() {
    	return OIDType.lookup(asInt());
    }
    public int asInt() {
        switch(this) {
        case NOM_MOC_VMO:
            return 1;
        case NOM_MOC_VMO_METRIC_NU:
            return 6;
        case NOM_MOC_VMO_METRIC_SA_RT:
            return 9;
        case NOM_MOC_VMS_MDS:
            return 33;
        case NOM_MOC_VMS_MDS_COMPOS_SINGLE_BED:
            return 35;
        case NOM_MOC_VMS_MDS_SIMP:
            return 37;
        case NOM_MOC_BATT:
            return 41;
        case NOM_MOC_PT_DEMOG:
            return 42;
        case NOM_MOC_VMO_AL_MON:
            return 54;
        case NOM_ACT_POLL_MDIB_DATA:
            return 3094;
        case NOM_NOTI_MDS_CREAT:
            return 3334;
        case NOM_NOTI_CONN_INDIC:
            return 3351;
        case NOM_DEV_ANALY_SAT_O2_VMD:
            return 4106;
        case NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_MDS:
            return 4113;
        case NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_VMD:
            return 4114;
        case NOM_DEV_ANALY_FLOW_AWAY_VMD:
            return 4130;
        case NOM_DEV_ANALY_CARD_OUTPUT_VMD:
            return 4134;
        case NOM_DEV_ANALY_PRESS_BLD_VMD:
            return 4174;
        case NOM_DEV_ANALY_RESP_RATE_VMD:
            return 4186;
        case NOM_DEV_CALC_VMD:
            return 4206;
        case NOM_DEV_ECG_VMD:
            return 4262;
        case NOM_DEV_METER_CONC_SKIN_GAS:
            return 4264;
        case NOM_DEV_METER_CONC_SKIN_GAS_MDS:
            return 4265;
        case NOM_DEV_METER_CONC_SKIN_GAS_VMD:
            return 4266;
        case NOM_DEV_EEG_VMD:
            return 4274;
        case NOM_DEV_METER_FLOW_BLD:
            return 4284;
        case NOM_DEV_METER_TEMP_BLD_VMD:
            return 4350;
        case NOM_DEV_METER_TEMP_VMD:
            return 4366;
        case NOM_DEV_MON_BLD_CHEM_MULTI_PARAM_VMD:
            return 4398;
        case NOM_DEV_MON_PHYSIO_MULTI_PARAM_MDS:
            return 4429;
        case NOM_DEV_PUMP_INFUS_MDS:
            return 4449;
        case NOM_DEV_SYS_PT_VENT_MDS:
            return 4465;
        case NOM_DEV_SYS_PT_VENT_VMD:
            return 4466;
        case NOM_DEV_SYS_MULTI_MODAL_MDS:
            return 4493;
        case NOM_DEV_SYS_MULTI_MODAL_VMD:
            return 4494;
        case NOM_DEV_SYS_ANESTH_VMD:
            return 4506;
        case NOM_DEV_GENERAL_VMD:
            return 5122;
        case NOM_DEV_ECG_RESP_VMD:
            return 5130;
        case NOM_DEV_ARRHY_VMD:
            return 5134;
        case NOM_DEV_PULS_VMD:
            return 5138;
        case NOM_DEV_ST_VMD:
            return 5142;
        case NOM_DEV_CO2_VMD:
            return 5146;
        case NOM_DEV_PRESS_BLD_NONINV_VMD:
            return 5150;
        case NOM_DEV_CEREB_PERF_VMD:
            return 5154;
        case NOM_DEV_CO2_CTS_VMD:
            return 5158;
        case NOM_DEV_CO2_TCUT_VMD:
            return 5162;
        case NOM_DEV_O2_VMD:
            return 5166;
        case NOM_DEV_O2_CTS_VMD:
            return 5170;
        case NOM_DEV_O2_TCUT_VMD:
            return 5174;
        case NOM_DEV_TEMP_DIFF_VMD:
            return 5178;
        case NOM_DEV_CNTRL_VMD:
            return 5182;
        case NOM_DEV_WEDGE_VMD:
            return 5190;
        case NOM_DEV_O2_VEN_SAT_VMD:
            return 5194;
        case NOM_DEV_CARD_RATE_VMD:
            return 5202;
        case NOM_DEV_SYS_VS_CONFIG_MDS:
            return 5209;
        case NOM_DEV_SYS_VS_UNCONFIG_MDS:
            return 5213;
        case NOM_DEV_PLETH_VMD:
            return 5238;
        case NOM_SAT_O2_TONE_FREQ:
            return 61448;
        case NOM_OBJ_HIF_KEY:
            return 61584;
        case NOM_OBJ_DISP:
            return 61616;
        case NOM_OBJ_SOUND_GEN:
            return 61648;
        case NOM_OBJ_SETTING:
            return 61649;
        case NOM_OBJ_PRINTER:
            return 61650;
        case NOM_OBJ_EVENT:
            return 61683;
        case NOM_OBJ_BATT_CHARGER:
            return 61690;
        case NOM_OBJ_ECG_OUT:
            return 61691;
        case NOM_OBJ_INPUT_DEV:
            return 61692;
        case NOM_OBJ_NETWORK:
            return 61693;
        case NOM_OBJ_QUICKLINK:
            return 61694;
        case NOM_OBJ_SPEAKER:
            return 61695;
        case NOM_OBJ_PUMP:
            return 61716;
        case NOM_OBJ_IR:
            return 61717;
        case NOM_ACT_POLL_MDIB_DATA_EXT:
            return 61755;
        case NOM_DEV_ANALY_PULS_CONT:
            return 61800;
        case NOM_DEV_ANALY_BISPECTRAL_INDEX_VMD:
            return 61806;
        case NOM_DEV_HIRES_TREND:
            return 61820;
        case NOM_DEV_HIRES_TREND_MDS:
            return 61821;
        case NOM_DEV_HIRES_TREND_VMD:
            return 61822;
        case NOM_DEV_MON_PT_EVENT_VMD:
            return 61826;
        case NOM_DEV_DERIVED_MSMT:
            return 61828;
        case NOM_DEV_DERIVED_MSMT_MDS:
            return 61829;
        case NOM_DEV_DERIVED_MSMT_VMD:
            return 61830;
        case NOM_OBJ_SENSOR:
            return 61902;
        case NOM_OBJ_XDUCR:
            return 61903;
        case NOM_OBJ_CHAN_1:
            return 61916;
        case NOM_OBJ_CHAN_2:
            return 61917;
        case NOM_OBJ_AWAY_AGENT_1:
            return 61918;
        case NOM_OBJ_AWAY_AGENT_2:
            return 61919;
        case NOM_OBJ_HIF_MOUSE:
            return 61983;
        case NOM_OBJ_HIF_TOUCH:
            return 61984;
        case NOM_OBJ_HIF_SPEEDPOINT:
            return 61985;
        case NOM_OBJ_HIF_ALARMBOX:
            return 61986;
        case NOM_OBJ_BUS_I2C:
            return 61987;
        case NOM_OBJ_CPU_SEC:
            return 61988;
        case NOM_OBJ_LED:
            return 61990;
        case NOM_OBJ_RELAY:
            return 61991;
        case NOM_OBJ_BATT_1:
            return 61996;
        case NOM_OBJ_BATT_2:
            return 61997;
        case NOM_OBJ_DISP_SEC:
            return 61998;
        case NOM_OBJ_AGM:
            return 61999;
        case NOM_OBJ_TELEMON:
            return 62014;
        case NOM_OBJ_XMTR:
            return 62015;
        case NOM_OBJ_CABLE:
            return 62016;
        case NOM_OBJ_TELEMETRY_XMTR:
            return 62053;
        case NOM_OBJ_MMS:
            return 62070;
        case NOM_OBJ_DISP_THIRD:
            return 62073;
        case NOM_OBJ_BATT:
            return 62078;
        case NOM_OBJ_BATT_TELE:
            return 62091;
        case NOM_DEV_PROT_WATCH_CHAN:
            return 62095;
        case NOM_OBJ_PROT_WATCH_1:
            return 62097;
        case NOM_OBJ_PROT_WATCH_2:
            return 62098;
        case NOM_OBJ_PROT_WATCH_3:
            return 62099;
        case NOM_OBJ_ECG_SYNC:
            return 62147;
        case NOM_DEV_METAB_VMD:
            return 62162;
        case NOM_OBJ_SENSOR_O2_CO2:
            return 62165;
        case NOM_OBJ_SRR_IF_1:
            return 62208;
        case NOM_OBJ_DISP_REMOTE:
            return 62228;
        default:
            throw new IllegalArgumentException("Unknown Object Class:"+this);
        }
    }
    public static ObjectClass valueOf(int s) {
        switch(s) {
        case 1:
            return NOM_MOC_VMO;
        case 6:
            return NOM_MOC_VMO_METRIC_NU;
        case 9:
            return NOM_MOC_VMO_METRIC_SA_RT;
        case 33:
            return NOM_MOC_VMS_MDS;
        case 35:
            return NOM_MOC_VMS_MDS_COMPOS_SINGLE_BED;
        case 37:
            return NOM_MOC_VMS_MDS_SIMP;
        case 41:
            return NOM_MOC_BATT;
        case 42:
            return NOM_MOC_PT_DEMOG;
        case 54:
            return NOM_MOC_VMO_AL_MON;
        case 3094:
            return NOM_ACT_POLL_MDIB_DATA;
        case 3334:
            return NOM_NOTI_MDS_CREAT;
        case 3351:
            return NOM_NOTI_CONN_INDIC;
        case 4106:
            return NOM_DEV_ANALY_SAT_O2_VMD;
        case 4113:
            return NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_MDS;
        case 4114:
            return NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_VMD;
        case 4130:
            return NOM_DEV_ANALY_FLOW_AWAY_VMD;
        case 4134:
            return NOM_DEV_ANALY_CARD_OUTPUT_VMD;
        case 4174:
            return NOM_DEV_ANALY_PRESS_BLD_VMD;
        case 4186:
            return NOM_DEV_ANALY_RESP_RATE_VMD;
        case 4206:
            return NOM_DEV_CALC_VMD;
        case 4262:
            return NOM_DEV_ECG_VMD;
        case 4264:
            return NOM_DEV_METER_CONC_SKIN_GAS;
        case 4265:
            return NOM_DEV_METER_CONC_SKIN_GAS_MDS;
        case 4266:
            return NOM_DEV_METER_CONC_SKIN_GAS_VMD;
        case 4274:
            return NOM_DEV_EEG_VMD;
        case 4284:
            return NOM_DEV_METER_FLOW_BLD;
        case 4350:
            return NOM_DEV_METER_TEMP_BLD_VMD;
        case 4366:
            return NOM_DEV_METER_TEMP_VMD;
        case 4398:
            return NOM_DEV_MON_BLD_CHEM_MULTI_PARAM_VMD;
        case 4429:
            return NOM_DEV_MON_PHYSIO_MULTI_PARAM_MDS;
        case 4449:
            return NOM_DEV_PUMP_INFUS_MDS;
        case 4465:
            return NOM_DEV_SYS_PT_VENT_MDS;
        case 4466:
            return NOM_DEV_SYS_PT_VENT_VMD;
        case 4493:
            return NOM_DEV_SYS_MULTI_MODAL_MDS;
        case 4494:
            return NOM_DEV_SYS_MULTI_MODAL_VMD;
        case 4506:
            return NOM_DEV_SYS_ANESTH_VMD;
        case 5122:
            return NOM_DEV_GENERAL_VMD;
        case 5130:
            return NOM_DEV_ECG_RESP_VMD;
        case 5134:
            return NOM_DEV_ARRHY_VMD;
        case 5138:
            return NOM_DEV_PULS_VMD;
        case 5142:
            return NOM_DEV_ST_VMD;
        case 5146:
            return NOM_DEV_CO2_VMD;
        case 5150:
            return NOM_DEV_PRESS_BLD_NONINV_VMD;
        case 5154:
            return NOM_DEV_CEREB_PERF_VMD;
        case 5158:
            return NOM_DEV_CO2_CTS_VMD;
        case 5162:
            return NOM_DEV_CO2_TCUT_VMD;
        case 5166:
            return NOM_DEV_O2_VMD;
        case 5170:
            return NOM_DEV_O2_CTS_VMD;
        case 5174:
            return NOM_DEV_O2_TCUT_VMD;
        case 5178:
            return NOM_DEV_TEMP_DIFF_VMD;
        case 5182:
            return NOM_DEV_CNTRL_VMD;
        case 5190:
            return NOM_DEV_WEDGE_VMD;
        case 5194:
            return NOM_DEV_O2_VEN_SAT_VMD;
        case 5202:
            return NOM_DEV_CARD_RATE_VMD;
        case 5209:
            return NOM_DEV_SYS_VS_CONFIG_MDS;
        case 5213:
            return NOM_DEV_SYS_VS_UNCONFIG_MDS;
        case 5238:
            return NOM_DEV_PLETH_VMD;
        case 61448:
            return NOM_SAT_O2_TONE_FREQ;
        case 61584:
            return NOM_OBJ_HIF_KEY;
        case 61616:
            return NOM_OBJ_DISP;
        case 61648:
            return NOM_OBJ_SOUND_GEN;
        case 61649:
            return NOM_OBJ_SETTING;
        case 61650:
            return NOM_OBJ_PRINTER;
        case 61683:
            return NOM_OBJ_EVENT;
        case 61690:
            return NOM_OBJ_BATT_CHARGER;
        case 61691:
            return NOM_OBJ_ECG_OUT;
        case 61692:
            return NOM_OBJ_INPUT_DEV;
        case 61693:
            return NOM_OBJ_NETWORK;
        case 61694:
            return NOM_OBJ_QUICKLINK;
        case 61695:
            return NOM_OBJ_SPEAKER;
        case 61716:
            return NOM_OBJ_PUMP;
        case 61717:
            return NOM_OBJ_IR;
        case 61755:
            return NOM_ACT_POLL_MDIB_DATA_EXT;
        case 61800:
            return NOM_DEV_ANALY_PULS_CONT;
        case 61806:
            return NOM_DEV_ANALY_BISPECTRAL_INDEX_VMD;
        case 61820:
            return NOM_DEV_HIRES_TREND;
        case 61821:
            return NOM_DEV_HIRES_TREND_MDS;
        case 61822:
            return NOM_DEV_HIRES_TREND_VMD;
        case 61826:
            return NOM_DEV_MON_PT_EVENT_VMD;
        case 61828:
            return NOM_DEV_DERIVED_MSMT;
        case 61829:
            return NOM_DEV_DERIVED_MSMT_MDS;
        case 61830:
            return NOM_DEV_DERIVED_MSMT_VMD;
        case 61902:
            return NOM_OBJ_SENSOR;
        case 61903:
            return NOM_OBJ_XDUCR;
        case 61916:
            return NOM_OBJ_CHAN_1;
        case 61917:
            return NOM_OBJ_CHAN_2;
        case 61918:
            return NOM_OBJ_AWAY_AGENT_1;
        case 61919:
            return NOM_OBJ_AWAY_AGENT_2;
        case 61983:
            return NOM_OBJ_HIF_MOUSE;
        case 61984:
            return NOM_OBJ_HIF_TOUCH;
        case 61985:
            return NOM_OBJ_HIF_SPEEDPOINT;
        case 61986:
            return NOM_OBJ_HIF_ALARMBOX;
        case 61987:
            return NOM_OBJ_BUS_I2C;
        case 61988:
            return NOM_OBJ_CPU_SEC;
        case 61990:
            return NOM_OBJ_LED;
        case 61991:
            return NOM_OBJ_RELAY;
        case 61996:
            return NOM_OBJ_BATT_1;
        case 61997:
            return NOM_OBJ_BATT_2;
        case 61998:
            return NOM_OBJ_DISP_SEC;
        case 61999:
            return NOM_OBJ_AGM;
        case 62014:
            return NOM_OBJ_TELEMON;
        case 62015:
            return NOM_OBJ_XMTR;
        case 62016:
            return NOM_OBJ_CABLE;
        case 62053:
            return NOM_OBJ_TELEMETRY_XMTR;
        case 62070:
            return NOM_OBJ_MMS;
        case 62073:
            return NOM_OBJ_DISP_THIRD;
        case 62078:
            return NOM_OBJ_BATT;
        case 62091:
            return NOM_OBJ_BATT_TELE;
        case 62095:
            return NOM_DEV_PROT_WATCH_CHAN;
        case 62097:
            return NOM_OBJ_PROT_WATCH_1;
        case 62098:
            return NOM_OBJ_PROT_WATCH_2;
        case 62099:
            return NOM_OBJ_PROT_WATCH_3;
        case 62147:
            return NOM_OBJ_ECG_SYNC;
        case 62162:
            return NOM_DEV_METAB_VMD;
        case 62165:
            return NOM_OBJ_SENSOR_O2_CO2;
        case 62208:
            return NOM_OBJ_SRR_IF_1;
        case 62228:
            return NOM_OBJ_DISP_REMOTE;
        default:
        	return null;
//            throw new IllegalArgumentException("Unknown objectclass:"+s);
        }
    }
}