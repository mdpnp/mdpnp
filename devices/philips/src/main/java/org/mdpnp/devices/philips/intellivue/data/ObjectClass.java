package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum ObjectClass implements OrdinalEnum.IntType {
    /**
     * VMO
     * Hex: 0x01
     * Dec: 1
     */
    NOM_MOC_VMO(1),

    NOM_MOC_VMO_METRIC_ENUM(5),
    
    /**
     * Numeric
     * Hex: 0x06
     * Dec: 6
     */
    NOM_MOC_VMO_METRIC_NU(6),
    /**
     * RealtimeSampleArray
     * Hex: 0x09
     * Dec: 9
     */
    NOM_MOC_VMO_METRIC_SA_RT(9),
    /**
     * Scanner
     * Hex: 0x10
     * Dec: 16
     */
    NOM_MOC_SCAN(16),
    
    /**
     * Configurable Scanner
     * Hex: 0x11
     * Dec: 17
     */
    NOM_MOC_SCAN_CFG(17),
    
    /**
     * Episodic Configurable Scanner
     * Hex: 0x12
     * Dec: 18
     */
    NOM_MOC_SCAN_CFG_EPI(18),
    /**
     * Periodic Configurable Scanner
     * Hex: 0x13
     * Dec: 19
     */
    NOM_MOC_SCAN_CFG_PERI(19),
    /**
     * Fast Periodic Configurable Scanner
     * Hex: 0x14
     * Dec: 20
     */
    NOM_MOC_SCAN_CFG_PERI_FAST(20),
    /**
     * Unconfigurable Scanner
     * Hex: 0x15
     * Dec: 21
     */
    NOM_MOC_SCAN_UCFG(21),
    /**
     * Context Scanner
     * Hex: 0x17
     * Dec: 23
     */
    NOM_MOC_SCAN_UCFG_CTXT(23),
    /**
     * Alert Scanner
     * Hex: 0x16
     * Dec: 22
     */
    NOM_MOC_SCAN_UCFG_ALSTAT(22),
    /**
     * Operating Scanner
     * Hex: 0x1A
     * Dec: 24
     */
    NOM_SCAN_UCFG_OP(24),

    

    /**
     * MDS
     * Hex: 0x21
     * Dec: 33
     */
    NOM_MOC_VMS_MDS(33),

    /**
     * CompositSingleBedMDS
     * Hex: 0x23
     * Dec: 35
     */
    NOM_MOC_VMS_MDS_COMPOS_SINGLE_BED(35),

    /**
     * SimpleMDS
     * Hex: 0x25
     * Dec: 37
     */
    NOM_MOC_VMS_MDS_SIMP(37),

    /**
     * Battery
     * Hex: 0x29
     * Dec: 41
     */
    NOM_MOC_BATT(41),

    /**
     * PatientDemographics
     * Hex: 0x2A
     * Dec: 42
     */
    NOM_MOC_PT_DEMOG(42),

    /**
     * AlertMonitor
     * Hex: 0x36
     * Dec: 54
     */
    NOM_MOC_VMO_AL_MON(54),

    /**
     * PollAction
     * Hex: 0xC16
     * Dec: 3094
     */
    NOM_ACT_POLL_MDIB_DATA(3094),

    /**
     * MDSCreate
     * Hex: 0xD06
     * Dec: 3334
     */
    NOM_NOTI_MDS_CREAT(3334),

    /**
     * ConnectIndication
     * Hex: 0xD17
     * Dec: 3351
     */
    NOM_NOTI_CONN_INDIC(3351),

    /**
     * satO2
     * Hex: 0x100A
     * Dec: 4106
     */
    NOM_DEV_ANALY_SAT_O2_VMD(4106),

    /**
     * GasAnalyzer
     * Hex: 0x1011
     * Dec: 4113
     */
    NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_MDS(4113),

    /**
     * Gas
     * Hex: 0x1012
     * Dec: 4114
     */
    NOM_DEV_ANALY_CONC_GAS_MULTI_PARAM_VMD(4114),

    /**
     * FlowAway
     * Hex: 0x1022
     * Dec: 4130
     */
    NOM_DEV_ANALY_FLOW_AWAY_VMD(4130),

    /**
     * C.O.
     * Hex: 0x1026
     * Dec: 4134
     */
    NOM_DEV_ANALY_CARD_OUTPUT_VMD(4134),

    /**
     * Press
     * Hex: 0x104E
     * Dec: 4174
     */
    NOM_DEV_ANALY_PRESS_BLD_VMD(4174),

    /**
     * RR
     * Hex: 0x105A
     * Dec: 4186
     */
    NOM_DEV_ANALY_RESP_RATE_VMD(4186),

    /**
     * Calculation
     * Hex: 0x106E
     * Dec: 4206
     */
    NOM_DEV_CALC_VMD(4206),

    /**
     * ECG
     * Hex: 0x10A6
     * Dec: 4262
     */
    NOM_DEV_ECG_VMD(4262),

    /**
     * SkinGas
     * Hex: 0x10A8
     * Dec: 4264
     */
    NOM_DEV_METER_CONC_SKIN_GAS(4264),

    /**
     * SkinGas
     * Hex: 0x10A9
     * Dec: 4265
     */
    NOM_DEV_METER_CONC_SKIN_GAS_MDS(4265),

    /**
     * SkinGas
     * Hex: 0x10AA
     * Dec: 4266
     */
    NOM_DEV_METER_CONC_SKIN_GAS_VMD(4266),

    /**
     * EEG
     * Hex: 0x10B2
     * Dec: 4274
     */
    NOM_DEV_EEG_VMD(4274),

    /**
     * BloodFlow
     * Hex: 0x10BC
     * Dec: 4284
     */
    NOM_DEV_METER_FLOW_BLD(4284),

    /**
     * BloodTemp
     * Hex: 0x10FE
     * Dec: 4350
     */
    NOM_DEV_METER_TEMP_BLD_VMD(4350),

    /**
     * Temp
     * Hex: 0x110E
     * Dec: 4366
     */
    NOM_DEV_METER_TEMP_VMD(4366),

    /**
     * BldChem
     * Hex: 0x112E
     * Dec: 4398
     */
    NOM_DEV_MON_BLD_CHEM_MULTI_PARAM_VMD(4398),

    /**
     * Multi-Param
     * Hex: 0x114D
     * Dec: 4429
     */
    NOM_DEV_MON_PHYSIO_MULTI_PARAM_MDS(4429),

    /**
     * PumpInfus
     * Hex: 0x1161
     * Dec: 4449
     */
    NOM_DEV_PUMP_INFUS_MDS(4449),

    /**
     * Ventilator
     * Hex: 0x1171
     * Dec: 4465
     */
    NOM_DEV_SYS_PT_VENT_MDS(4465),

    /**
     * Ventilator
     * Hex: 0x1172
     * Dec: 4466
     */
    NOM_DEV_SYS_PT_VENT_VMD(4466),

    /**
     * Multi-ModalMDS
     * Hex: 0x118D
     * Dec: 4493
     */
    NOM_DEV_SYS_MULTI_MODAL_MDS(4493),

    /**
     * Multi-Modal
     * Hex: 0x118E
     * Dec: 4494
     */
    NOM_DEV_SYS_MULTI_MODAL_VMD(4494),

    /**
     * Aneshesia
     * Hex: 0x119A
     * Dec: 4506
     */
    NOM_DEV_SYS_ANESTH_VMD(4506),

    /**
     * General
     * Hex: 0x1402
     * Dec: 5122
     */
    NOM_DEV_GENERAL_VMD(5122),

    /**
     * ECG-Resp
     * Hex: 0x140A
     * Dec: 5130
     */
    NOM_DEV_ECG_RESP_VMD(5130),

    /**
     * Arrythmia
     * Hex: 0x140E
     * Dec: 5134
     */
    NOM_DEV_ARRHY_VMD(5134),

    /**
     * Pulse
     * Hex: 0x1412
     * Dec: 5138
     */
    NOM_DEV_PULS_VMD(5138),

    /**
     * ST
     * Hex: 0x1416
     * Dec: 5142
     */
    NOM_DEV_ST_VMD(5142),

    /**
     * CO2
     * Hex: 0x141A
     * Dec: 5146
     */
    NOM_DEV_CO2_VMD(5146),

    /**
     * NoninvPress
     * Hex: 0x141E
     * Dec: 5150
     */
    NOM_DEV_PRESS_BLD_NONINV_VMD(5150),

    /**
     * CerebPerf
     * Hex: 0x1422
     * Dec: 5154
     */
    NOM_DEV_CEREB_PERF_VMD(5154),

    /**
     * CO2CTS
     * Hex: 0x1426
     * Dec: 5158
     */
    NOM_DEV_CO2_CTS_VMD(5158),

    /**
     * TcCO2
     * Hex: 0x142A
     * Dec: 5162
     */
    NOM_DEV_CO2_TCUT_VMD(5162),

    /**
     * O2
     * Hex: 0x142E
     * Dec: 5166
     */
    NOM_DEV_O2_VMD(5166),

    /**
     * CTS
     * Hex: 0x1432
     * Dec: 5170
     */
    NOM_DEV_O2_CTS_VMD(5170),

    /**
     * Tc02
     * Hex: 0x1436
     * Dec: 5174
     */
    NOM_DEV_O2_TCUT_VMD(5174),

    /**
     * DiffTemp
     * Hex: 0x143A
     * Dec: 5178
     */
    NOM_DEV_TEMP_DIFF_VMD(5178),

    /**
     * Control
     * Hex: 0x143E
     * Dec: 5182
     */
    NOM_DEV_CNTRL_VMD(5182),

    /**
     * Wedge
     * Hex: 0x1446
     * Dec: 5190
     */
    NOM_DEV_WEDGE_VMD(5190),

    /**
     * O2VentSat
     * Hex: 0x144A
     * Dec: 5194
     */
    NOM_DEV_O2_VEN_SAT_VMD(5194),

    /**
     * HR
     * Hex: 0x1452
     * Dec: 5202
     */
    NOM_DEV_CARD_RATE_VMD(5202),

    /**
     * configMDS
     * Hex: 0x1459
     * Dec: 5209
     */
    NOM_DEV_SYS_VS_CONFIG_MDS(5209),

    /**
     * unconfigMDS
     * Hex: 0x145D
     * Dec: 5213
     */
    NOM_DEV_SYS_VS_UNCONFIG_MDS(5213),

    /**
     * Pleth
     * Hex: 0x1476
     * Dec: 5238
     */
    NOM_DEV_PLETH_VMD(5238),

    /**
     * PrivateAttribute112
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_SAT_O2_TONE_FREQ(61448),

    /**
     * Key
     * Hex: 0xF090
     * Dec: 61584
     */
    NOM_OBJ_HIF_KEY(61584),

    /**
     * Display
     * Hex: 0xF0B0
     * Dec: 61616
     */
    NOM_OBJ_DISP(61616),

    /**
     * SoundGenerator
     * Hex: 0xF0D0
     * Dec: 61648
     */
    NOM_OBJ_SOUND_GEN(61648),

    /**
     * Setting
     * Hex: 0xF0D1
     * Dec: 61649
     */
    NOM_OBJ_SETTING(61649),

    /**
     * Printer
     * Hex: 0xF0D2
     * Dec: 61650
     */
    NOM_OBJ_PRINTER(61650),

    /**
     * Event
     * Hex: 0xF0F3
     * Dec: 61683
     */
    NOM_OBJ_EVENT(61683),

    /**
     * BatteryCharger
     * Hex: 0xF0FA
     * Dec: 61690
     */
    NOM_OBJ_BATT_CHARGER(61690),

    /**
     * ECGout
     * Hex: 0xF0FB
     * Dec: 61691
     */
    NOM_OBJ_ECG_OUT(61691),

    /**
     * InputDevice
     * Hex: 0xF0FC
     * Dec: 61692
     */
    NOM_OBJ_INPUT_DEV(61692),

    /**
     * Network
     * Hex: 0xF0FD
     * Dec: 61693
     */
    NOM_OBJ_NETWORK(61693),

    /**
     * QuicklinkBar
     * Hex: 0xF0FE
     * Dec: 61694
     */
    NOM_OBJ_QUICKLINK(61694),

    /**
     * Speaker
     * Hex: 0xF0FF
     * Dec: 61695
     */
    NOM_OBJ_SPEAKER(61695),

    /**
     * Pump
     * Hex: 0xF114
     * Dec: 61716
     */
    NOM_OBJ_PUMP(61716),

    /**
     * IR
     * Hex: 0xF115
     * Dec: 61717
     */
    NOM_OBJ_IR(61717),

    /**
     * ExtendedPollAction
     * Hex: 0xF13B
     * Dec: 61755
     */
    NOM_ACT_POLL_MDIB_DATA_EXT(61755),

    /**
     * PulsCont
     * Hex: 0xF168
     * Dec: 61800
     */
    NOM_DEV_ANALY_PULS_CONT(61800),

    /**
     * BIS
     * Hex: 0xF16E
     * Dec: 61806
     */
    NOM_DEV_ANALY_BISPECTRAL_INDEX_VMD(61806),

    /**
     * HiresTrend
     * Hex: 0xF17C
     * Dec: 61820
     */
    NOM_DEV_HIRES_TREND(61820),

    /**
     * HiresTrend
     * Hex: 0xF17D
     * Dec: 61821
     */
    NOM_DEV_HIRES_TREND_MDS(61821),

    /**
     * HiresTrend
     * Hex: 0xF17E
     * Dec: 61822
     */
    NOM_DEV_HIRES_TREND_VMD(61822),

    /**
     * Events
     * Hex: 0xF182
     * Dec: 61826
     */
    NOM_DEV_MON_PT_EVENT_VMD(61826),

    /**
     * DerivedMeasurement
     * Hex: 0xF184
     * Dec: 61828
     */
    NOM_DEV_DERIVED_MSMT(61828),

    /**
     * DerivedMeasurement
     * Hex: 0xF185
     * Dec: 61829
     */
    NOM_DEV_DERIVED_MSMT_MDS(61829),

    /**
     * DerivedMeasurement
     * Hex: 0xF186
     * Dec: 61830
     */
    NOM_DEV_DERIVED_MSMT_VMD(61830),

    /**
     * Sensor
     * Hex: 0xF1CE
     * Dec: 61902
     */
    NOM_OBJ_SENSOR(61902),

    /**
     * Transducer
     * Hex: 0xF1CF
     * Dec: 61903
     */
    NOM_OBJ_XDUCR(61903),

    /**
     * Channel1
     * Hex: 0xF1DC
     * Dec: 61916
     */
    NOM_OBJ_CHAN_1(61916),

    /**
     * Channel2
     * Hex: 0xF1DD
     * Dec: 61917
     */
    NOM_OBJ_CHAN_2(61917),

    /**
     * Agent1
     * Hex: 0xF1DE
     * Dec: 61918
     */
    NOM_OBJ_AWAY_AGENT_1(61918),

    /**
     * Agent2
     * Hex: 0xF1DF
     * Dec: 61919
     */
    NOM_OBJ_AWAY_AGENT_2(61919),

    /**
     * MOUSE
     * Hex: 0xF21F
     * Dec: 61983
     */
    NOM_OBJ_HIF_MOUSE(61983),

    /**
     * TOUCH
     * Hex: 0xF220
     * Dec: 61984
     */
    NOM_OBJ_HIF_TOUCH(61984),

    /**
     * Speedpoint
     * Hex: 0xF221
     * Dec: 61985
     */
    NOM_OBJ_HIF_SPEEDPOINT(61985),

    /**
     * Alarmbox
     * Hex: 0xF222
     * Dec: 61986
     */
    NOM_OBJ_HIF_ALARMBOX(61986),

    /**
     * I2CBus
     * Hex: 0xF223
     * Dec: 61987
     */
    NOM_OBJ_BUS_I2C(61987),

    /**
     * 2ndCPU
     * Hex: 0xF224
     * Dec: 61988
     */
    NOM_OBJ_CPU_SEC(61988),

    /**
     * LED
     * Hex: 0xF226
     * Dec: 61990
     */
    NOM_OBJ_LED(61990),

    /**
     * Relay
     * Hex: 0xF227
     * Dec: 61991
     */
    NOM_OBJ_RELAY(61991),

    /**
     * Battery1
     * Hex: 0xF22C
     * Dec: 61996
     */
    NOM_OBJ_BATT_1(61996),

    /**
     * Battery2
     * Hex: 0xF22D
     * Dec: 61997
     */
    NOM_OBJ_BATT_2(61997),

    /**
     * 2ndDisplay
     * Hex: 0xF22E
     * Dec: 61998
     */
    NOM_OBJ_DISP_SEC(61998),

    /**
     * AGM
     * Hex: 0xF22F
     * Dec: 61999
     */
    NOM_OBJ_AGM(61999),

    /**
     * TeleMon
     * Hex: 0xF23E
     * Dec: 62014
     */
    NOM_OBJ_TELEMON(62014),

    /**
     * Transmitter
     * Hex: 0xF23F
     * Dec: 62015
     */
    NOM_OBJ_XMTR(62015),

    /**
     * Cable
     * Hex: 0xF240
     * Dec: 62016
     */
    NOM_OBJ_CABLE(62016),

    /**
     * TelemetryTransmitter
     * Hex: 0xF265
     * Dec: 62053
     */
    NOM_OBJ_TELEMETRY_XMTR(62053),

    /**
     * MMS
     * Hex: 0xF276
     * Dec: 62070
     */
    NOM_OBJ_MMS(62070),

    /**
     * ThirdDisplay
     * Hex: 0xF279
     * Dec: 62073
     */
    NOM_OBJ_DISP_THIRD(62073),

    /**
     * Battery
     * Hex: 0xF27E
     * Dec: 62078
     */
    NOM_OBJ_BATT(62078),

    /**
     * BatteryTele
     * Hex: 0xF28B
     * Dec: 62091
     */
    NOM_OBJ_BATT_TELE(62091),

    /**
     * ProtocolWatchgeneric
     * Hex: 0xF28F
     * Dec: 62095
     */
    NOM_DEV_PROT_WATCH_CHAN(62095),

    /**
     * ProtocolWatchProtocolNo.1
     * Hex: 0xF291
     * Dec: 62097
     */
    NOM_OBJ_PROT_WATCH_1(62097),

    /**
     * ProtocolWatchProtocolNo.2
     * Hex: 0xF292
     * Dec: 62098
     */
    NOM_OBJ_PROT_WATCH_2(62098),

    /**
     * ProtocolWatchProtocolNo.3
     * Hex: 0xF293
     * Dec: 62099
     */
    NOM_OBJ_PROT_WATCH_3(62099),

    /**
     * ECGSync
     * Hex: 0xF2C3
     * Dec: 62147
     */
    NOM_OBJ_ECG_SYNC(62147),

    /**
     * Metabolism
     * Hex: 0xF2D2
     * Dec: 62162
     */
    NOM_DEV_METAB_VMD(62162),

    /**
     * SENSORO2CO2
     * Hex: 0xF2D5
     * Dec: 62165
     */
    NOM_OBJ_SENSOR_O2_CO2(26165),

    /**
     * SRRInterface1
     * Hex: 0xF300
     * Dec: 62208
     */
    NOM_OBJ_SRR_IF_1(62208),

    /**
     * REMOTEDISPLAY114
     * Hex: 0xF314
     * Dec: 62228
     */
    NOM_OBJ_DISP_REMOTE(62228),

;
    
    private final int x;
    
    private ObjectClass(final int x) {
        this.x = x;
    }
    
    private static final Map<Integer, ObjectClass> map = OrdinalEnum.buildInt(ObjectClass.class);
    
    public OIDType asOID() {
    	return OIDType.lookup(asInt());
    }
    public int asInt() {
        return x;
    }
    public static ObjectClass valueOf(int s) {
        return map.get(s);
    }
}