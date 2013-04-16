package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum ObservedValue implements OrdinalEnum.IntType {
	   /**
     * ECG Unspecific ECG wave
     */
    NOM_ECG_ELEC_POTL(0x0100),
    /**
     * L I Lead I - ECG wave label
     */
    NOM_ECG_ELEC_POTL_I(0x0101),
    /**
     * L II Lead II - ECG wave label
     */
    NOM_ECG_ELEC_POTL_II(0x0102),
    /**
     * L V1 Lead V1 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V1(0x0103),
    /**
     * L V2 Lead V2 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V2(0x0104),
    /**
     * L V3 Lead V3 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V3(0x0105),
    /**
     * L V4 Lead V4 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V4(0x0106),
    /**
     * L V5 Lead V5 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V5(0x0107),
    /**
     * L V6 Lead V6 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V6(0x0108),
    /**
     * L III Lead III - ECG wave label
     */
    NOM_ECG_ELEC_POTL_III(0x013D),
    /**
     * L aVR Lead aVR - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVR(0x013E),
    /**
     * L aVL Lead aVL - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVL(0x013F),
    /**
     * L aVF Lead aVF - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVF(0x0140),
    /**
     * V ECG Lead V
     */
    NOM_ECG_ELEC_POTL_V(0x0143),
    /**
     * MCL ECG Lead MCL
     */
    NOM_ECG_ELEC_POTL_MCL(0x014B),
    /**
     * MCL1 ECG Lead MCL1
     */
    NOM_ECG_ELEC_POTL_MCL1(0x014C),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_I(0x0301),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_II(0x0302),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V1(0x0303),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V2(0x0304),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V3(0x0305),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V4(0x0306),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V5(0x0307),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V6(0x0308),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_III(0x033D),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVR(0x033E),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVL(0x033F),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVF(0x0340),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V(0x0343),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_MCL(0x034B),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_ES(0x0364),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AS(0x0365),
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AI(0x0366),
    /**
     * QT
     */
    NOM_ECG_TIME_PD_QT_GL(0x3F20),
    /**
     * QTc
     */
    NOM_ECG_TIME_PD_QTc(0x3F24),
    /**
     * NOM_ECG_RHY_UNANALYZEABLE Cannot Analyze ECG 0x4011
     */
    NOM_ECG_RHY_ABSENT(0x400B),
    /**
     * NOM_ECG_RHY_UNANALYZEABLE Cannot Analyze ECG 0x4011
     */
    NOM_ECG_RHY_NOS(0x403F),
    /**
     * extHR denotes a Heart Rate received from an external device
     */
    NOM_ECG_CARD_BEAT_RATE(0x4182),
    /**
     * btbHR Cardiac Beat-to-Beat Rate
     */
    NOM_ECG_CARD_BEAT_RATE_BTB(0x418A),
    /**
     * PVC Premature Ventricular Contractions
     */
    NOM_ECG_V_P_C_CNT(0x4261),
    /**
     * NLS_PRESS_NAMES_PULSE_FROM_P4 0x8003542B
     */
    NOM_PULS_RATE(0x480A),
    /**
     * NLS_SPO2_NAMES_PULS_OXIM_PULS_RATE_LEFT 0x80155401
     */
    NOM_PLETH_PULS_RATE(0x4822),
    /**
     * SVRI Systemic Vascular Resistance Index
     */
    NOM_RES_VASC_SYS_INDEX(0x4900),
    /**
     * LVSWI Left Ventricular Stroke Volume Index
     */
    NOM_WK_LV_STROKE_INDEX(0x4904),
    /**
     * RVSWI Right Ventricular Stroke Work Index
     */
    NOM_WK_RV_STROKE_INDEX(0x4908),
    /**
     * C.I. Cardiac Index
     */
    NOM_OUTPUT_CARD_INDEX(0x490C),
    /**
     * BP Unspecified Blood Pressure
     */
    NOM_PRESS_BLD(0x4A00),
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_SYS(0x4A01),
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_DIA(0x4A02),
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_MEAN(0x4A03),
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV(0x4A04),
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_SYS(0x4A05),
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_DIA(0x4A06),
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_MEAN(0x4A07),
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT(0x4A0C),
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_SYS(0x4A0D),
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_DIA(0x4A0E),
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_MEAN(0x4A0F),
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART(0x4A10),
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_SYS(0x4A11),
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_DIA(0x4A12),
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_MEAN(0x4A13),
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP(0x4A14),
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_SYS(0x4A15),
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_DIA(0x4A16),
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_MEAN(0x4A17),
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM(0x4A1C),
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_SYS(0x4A1D),
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_DIA(0x4A1E),
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_MEAN(0x4A1F),
    /**
     * PAWP Pulmonary Artery Wedge Pressure
     */
    NOM_PRESS_BLD_ART_PULM_WEDGE(0x4A24),
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB(0x4A28),
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_SYS(0x4A29),
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_DIA(0x4A2A),
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_MEAN(0x4A2B),
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT(0x4A30),
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_SYS(0x4A31),
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_DIA(0x4A32),
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_MEAN(0x4A33),
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT(0x4A34),
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_SYS(0x4A35),
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_DIA(0x4A36),
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_MEAN(0x4A37),
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT(0x4A44),
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_SYS(0x4A45),
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_DIA(0x4A46),
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_MEAN(0x4A47),
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB(0x4A48),
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_SYS(0x4A49),
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_DIA(0x4A4A),
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_MEAN(0x4A4B),
    /**
     * VO2 Oxygen Consumption VO2
     */
    NOM_SAT_O2_CONSUMP(0x4B00),
    /**
     * C.O. Cardiac Output
     */
    NOM_OUTPUT_CARD(0x4B04),
    /**
     * PVR Pulmonary vascular Resistance
     */
    NOM_RES_VASC_PULM(0x4B24),
    /**
     * SVR Systemic Vascular Resistance
     */
    NOM_RES_VASC_SYS(0x4B28),
    /**
     * SO2 O2 Saturation
     */
    NOM_SAT_O2(0x4B2C),
    /**
     * 'SO2 Calculated SO2
     */
    NOM_SAT_O2_ART(0x4B34),
    /**
     * 'SvO2 Calculated SvO2
     */
    NOM_SAT_O2_VEN(0x4B3C),
    /**
     * AaDO2 Alveolar- Arterial Oxygen Difference
     */
    NOM_SAT_DIFF_O2_ART_ALV(0x4B40),
    /**
     * sTemp Desired Environmental Temperature
     */
    NOM_SETT_TEMP(0x4B48),
    /**
     * Tart Areterial Temperature
     */
    NOM_TEMP_ART(0x4B50),
    /**
     * Tairwy Airway Temperature
     */
    NOM_TEMP_AWAY(0x4B54),
    /**
     * Tcore Core (Body) Temperature
     */
    NOM_TEMP_CORE(0x4B60),
    /**
     * Tesoph Esophagial Temperature
     */
    NOM_TEMP_ESOPH(0x4B64),
    /**
     * Tinj Injectate Temperature
     */
    NOM_TEMP_INJ(0x4B68),
    /**
     * Tnaso Naso pharyngial Temperature
     */
    NOM_TEMP_NASOPH(0x4B6C),
    /**
     * Tskin Skin Temperature
     */
    NOM_TEMP_SKIN(0x4B74),
    /**
     * Ttymp Tympanic Temperature
     */
    NOM_TEMP_TYMP(0x4B78),
    /**
     * Tven Venous Temperature
     */
    NOM_TEMP_VEN(0x4B7C),
    /**
     * SV Stroke Volume
     */
    NOM_VOL_BLD_STROKE(0x4B84),
    /**
     * LCW Left Cardiac Work
     */
    NOM_WK_CARD_LEFT(0x4B90),
    /**
     * RCW Right Cardiac Work
     */
    NOM_WK_CARD_RIGHT(0x4B94),
    /**
     * LVSW Left Ventricular Stroke Volume
     */
    NOM_WK_LV_STROKE(0x4B9C),
    /**
     * RVSW Right Ventricular Stroke Volume
     */
    NOM_WK_RV_STROKE(0x4BA4),
    /**
     * Perf Perfusion Indicator
     */
    NOM_PULS_OXIM_PERF_REL(0x4BB0),
    /**
     * PLETH2 PLETH from the second SpO2/PLETH module
     */
    NOM_PLETH(0x4BB4),
    /**
     * SpO2 Arterial Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2(0x4BB8),
    /**
     * DeltaSpO2 Difference between two SpO2 Values (like Left - Right)
     */
    NOM_PULS_OXIM_SAT_O2_DIFF(0x4BC4),
    /**
     * SpO2 l Arterial Oxigen Saturation (left)
     */
    NOM_PULS_OXIM_SAT_O2_ART_LEFT(0x4BC8),
    /**
     * SpO2 r Arterial Oxigen Saturation (right)
     */
    NOM_PULS_OXIM_SAT_O2_ART_RIGHT(0x4BCC),
    /**
     * CCO Continuous Cardiac Output
     */
    NOM_OUTPUT_CARD_CTS(0x4BDC),
    /**
     * ESV End Systolic Volume
     */
    NOM_VOL_VENT_L_END_SYS(0x4C04),
    /**
     * dPmax Index of Left Ventricular Contractility
     */
    NOM_GRAD_PRESS_BLD_AORT_POS_MAX(0x4C25),
    /**
     * Resp Imedance RESP wave
     */
    NOM_RESP(0x5000),
    /**
     * RR Respiration Rate
     */
    NOM_RESP_RATE(0x500A),
    /**
     * sRRaw Setting: Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NOM_AWAY_RESP_RATE(0x5012),
    /**
     * VC Vital Lung Capacity
     */
    NOM_CAPAC_VITAL(0x5080),
    /**
     * COMP generic label Lung Compliance
     */
    NOM_COMPL_LUNG(0x5088),
    /**
     * Cdyn Dynamic Lung Compliance
     */
    NOM_COMPL_LUNG_DYN(0x508C),
    /**
     * Cstat Static Lung Compliance
     */
    NOM_COMPL_LUNG_STATIC(0x5090),
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2(0x50AC),
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2_ET(0x50B0),
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2_INSP_MIN(0x50BA),
    /**
     * tcpCO2 Transcutaneous Carbon Dioxide Partial Pressure
     */
    NOM_CO2_TCUT(0x50CC),
    /**
     * tcpO2 Transcutaneous Oxygen Partial Pressure
     */
    NOM_O2_TCUT(0x50D0),
    /**
     * AWF Airway Flow Wave
     */
    NOM_FLOW_AWAY(0x50D4),
    /**
     * PEF Expiratory Peak Flow
     */
    NOM_FLOW_AWAY_EXP_MAX(0x50D9),
    /**
     * PIF Inspiratory Peak Flow
     */
    NOM_FLOW_AWAY_INSP_MAX(0x50DD),
    /**
     * VCO2 CO2 Production
     */
    NOM_FLOW_CO2_PROD_RESP(0x50E0),
    /**
     * T.I. Transthoracic Impedance
     */
    NOM_IMPED_TTHOR(0x50E4),
    /**
     * Pplat Plateau Pressure
     */
    NOM_PRESS_RESP_PLAT(0x50E8),
    /**
     * AWP Airway Pressure Wave
     */
    NOM_PRESS_AWAY(0x50F0),
    /**
     * sPmin Setting: Low Inspiratory Pressure
     */
    NOM_SETT_PRESS_AWAY_MIN(0x50F2),
    /**
     * sCPAP Setting: Continuous Positive Airway Pressure Value
     */
    NOM_PRESS_AWAY_CTS_POS(0x50F4),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_PRESS_AWAY_NEG_MAX(0x50F9),
    /**
     * iPEEP Intrinsic PEEP Breathing Pressure
     */
    NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC(0x5100),
    /**
     * AWPin Airway Pressure Wave - measured in the inspiratory path
     */
    NOM_PRESS_AWAY_INSP(0x5108),
    /**
     * sPIP Setting: Positive Inspiratory Pressure
     */
    NOM_PRESS_AWAY_INSP_MAX(0x5109),
    /**
     * MnAwP Mean Airway Pressure. Printer Context
     */
    NOM_PRESS_AWAY_INSP_MEAN(0x510B),
    /**
     * sIE 1: Setting: Inspiration to Expiration Ratio.
     */
    NOM_RATIO_IE(0x5118),
    /**
     * Vd/Vt Ratio of Deadspace to Tidal Volume Vd/Vt
     */
    NOM_RATIO_AWAY_DEADSP_TIDAL(0x511C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_RES_AWAY(0x5120),
    /**
     * Rexp Expiratory Resistance
     */
    NOM_RES_AWAY_EXP(0x5124),
    /**
     * Rinsp Inspiratory Resistance
     */
    NOM_RES_AWAY_INSP(0x5128),
    /**
     * ApneaD Apnea Time
     */
    NOM_TIME_PD_APNEA(0x5130),
    /**
     * sTV Setting: Tidal Volume
     */
    NOM_VOL_AWAY_TIDAL(0x513C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VOL_MINUTE_AWAY(0x5148),
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NOM_VOL_MINUTE_AWAY_EXP(0x514C),
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NOM_VOL_MINUTE_AWAY_INSP(0x5150),
    /**
     * FICO2 Airway CO2 inspiration
     */
    NOM_VENT_CONC_AWAY_CO2_INSP(0x5160),
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2(0x5164),
    /**
     * DeltaO2 relative Dead Space
     */
    NOM_VENT_CONC_AWAY_O2_DELTA(0x5168),
    /**
     * PECO2 Partial O2 Venous
     */
    NOM_VENT_AWAY_CO2_EXP(0x517C),
    /**
     * AWFin Airway Flow Wave - measured in the inspiratory path
     */
    NOM_VENT_FLOW_INSP(0x518C),
    /**
     * VQI Ventilation Perfusion Index
     */
    NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX(0x5190),
    /**
     * Poccl Occlusion Pressure
     */
    NOM_VENT_PRESS_OCCL(0x519C),
    /**
     * sPEEP Setting: PEEP/CPAP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS(0x51A8),
    /**
     * Vd Dead Space Volume Vd
     */
    NOM_VENT_VOL_AWAY_DEADSP(0x51B0),
    /**
     * relVd relative Dead Space
     */
    NOM_VENT_VOL_AWAY_DEADSP_REL(0x51B4),
    /**
     * sTrVol Setting: Trigger Flow/Volume
     */
    NOM_SETT_VENT_VOL_LUNG_TRAPD(0x51B8),
    /**
     * sMMV Setting: Mandatory Minute Volume
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND(0x51CC),
    /**
     * DCO2 High Frequency Gas Transport Coefficient value
     */
    NOM_COEF_GAS_TRAN(0x51D4),
    /**
     * DES generic Desflurane label
     */
    NOM_CONC_AWAY_DESFL(0x51D8),
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL(0x51DC),
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH(0x51E0),
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL(0x51E4),
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL(0x51E8),
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O(0x51F0),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_AWAY_DESFL_ET(0x5214),
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL_ET(0x5218),
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH_ET(0x521C),
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL_ET(0x5220),
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL_ET(0x5224),
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O_ET(0x522C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_AWAY_DESFL_INSP(0x5268),
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL_INSP(0x526C),
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH_INSP(0x5270),
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL_INSP(0x5274),
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL_INSP(0x5278),
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O_INSP(0x5280),
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2_INSP(0x5284),
    /**
     * DPosP Duration Above Base Pressure
     */
    NOM_VENT_TIME_PD_PPV(0x5360),
    /**
     * PEinsp Respiration Pressure Plateau
     */
    NOM_VENT_PRESS_RESP_PLAT(0x5368),
    /**
     * Leak Leakage
     */
    NOM_VENT_VOL_LEAK(0x5370),
    /**
     * ALVENT Alveolar Ventilation ALVENT
     */
    NOM_VENT_VOL_LUNG_ALV(0x5374),
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2_ET(0x5378),
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2(0x537C),
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2_ET(0x5380),
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2_INSP(0x5384),
    /**
     * AGTs Anesthetic Agent - secondary agent
     */
    NOM_CONC_AWAY_AGENT(0x5388),
    /**
     * etAGTs EndTidal secondary Anesthetic Agent
     */
    NOM_CONC_AWAY_AGENT_ET(0x538C),
    /**
     * inAGTs Inspired secondary Anesthetic Agent
     */
    NOM_CONC_AWAY_AGENT_INSP(0x5390),
    /**
     * CPP Cerebral Perfusion Pressure
     */
    NOM_PRESS_CEREB_PERF(0x5804),
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN(0x5808),
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_SYS(0x5809),
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_DIA(0x580A),
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_MEAN(0x580B),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SCORE_GLAS_COMA(0x5880),
    /**
     * EyeRsp SubScore of the GCS: Eye Response
     */
    NOM_SCORE_EYE_SUBSC_GLAS_COMA(0x5882),
    /**
     * MotRsp SubScore of the GCS: Motoric Response
     */
    NOM_SCORE_MOTOR_SUBSC_GLAS_COMA(0x5883),
    /**
     * VblRsp SubScore of the GCS: Verbal Response
     */
    NOM_SCORE_SUBSC_VERBAL_GLAS_COMA(0x5884),
    /**
     * HC Head Circumferince
     */
    NOM_CIRCUM_HEAD(0x5900),
    /**
     * PRL Pupil Reaction Left eye - light reaction of left eye's pupil
     */
    NOM_TIME_PD_PUPIL_REACT_LEFT(0x5924),
    /**
     * PRR Pupil Reaction Righteye - light reaction of right eye's pupil
     */
    NOM_TIME_PD_PUPIL_REACT_RIGHT(0x5928),
    /**
     * RT EEG Right channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX(0x592C),
    /**
     * EMG Electromyography
     */
    NOM_EMG_ELEC_POTL_MUSCL(0x593C),
    /**
     * RT MDF Mean Dominant Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN(0x597C),
    /**
     * RT PPF Peak Power Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK(0x5984),
    /**
     * RT SEF Spectral Edge Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE(0x5988),
    /**
     * RT TP Total Power - Right Side
     */
    NOM_EEG_PWR_SPEC_TOT(0x59B8),
    /**
     * RT %AL Percent Alpha - Right (RT) Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_REL(0x59D4),
    /**
     * RT %BE Percent Beta - Right Side
     */
    NOM_EEG_PWR_SPEC_BETA_REL(0x59D8),
    /**
     * RT %DL Percent Delta - Right Side
     */
    NOM_EEG_PWR_SPEC_DELTA_REL(0x59DC),
    /**
     * RT %TH Percent Theta - Right Side
     */
    NOM_EEG_PWR_SPEC_THETA_REL(0x59E0),
    /**
     * UrFl Urimeter - Urine Flow.
     */
    NOM_FLOW_URINE_INSTANT(0x680C),
    /**
     * UrVol Urine Volume
     */
    NOM_VOL_URINE_BAL_PD(0x6824),
    /**
     * BagVol Current fluid (Urine) in the Urine Bag
     */
    NOM_VOL_URINE_COL(0x6830),
    /**
     * sDRate Setting: Infusion Pump Delivery Rate
     */
    NOM_SETT_FLOW_FLUID_PUMP(0x6858),
    /**
     * AccVol Infusion Pump Accumulated volume. Measured value
     */
    NOM_VOL_INFUS_ACTUAL_TOTAL(0x68FC),
    /**
     * &pHa Adjusted pH in the arterial Blood
     */
    NOM_CONC_PH_ART(0x7004),
    /**
     * PaCO2 Partial Pressure of arterial Carbon Dioxide
     */
    NOM_CONC_PCO2_ART(0x7008),
    /**
     * PaO2 Partial O2 arterial
     */
    NOM_CONC_PO2_ART(0x700C),
    /**
     * 'Hb Calculated Hemoglobin
     */
    NOM_CONC_HB_ART(0x7014),
    /**
     * CaO2 Arterial Oxygen Content CaO2
     */
    NOM_CONC_HB_O2_ART(0x7018),
    /**
     * &pHv Adjusted pH value in the venous Blood
     */
    NOM_CONC_PH_VEN(0x7034),
    /**
     * &PvCO2 Computed PvCO2 at Patient Temperature
     */
    NOM_CONC_PCO2_VEN(0x7038),
    /**
     * &PvO2 Adjusted PvO2 at Patient Temperature
     */
    NOM_CONC_PO2_VEN(0x703C),
    /**
     * CvO2 Venous Oxygen Content
     */
    NOM_CONC_HB_O2_VEN(0x7048),
    /**
     * UrpH pH value in the Urine
     */
    NOM_CONC_PH_URINE(0x7064),
    /**
     * UrNa Natrium in Urine
     */
    NOM_CONC_NA_URINE(0x706C),
    /**
     * SerNa Natrium in Serum
     */
    NOM_CONC_NA_SERUM(0x70D8),
    /**
     * pH pH in the Blood Plasma
     */
    NOM_CONC_PH_GEN(0x7104),
    /**
     * 'HCO3 Calculated HCO3
     */
    NOM_CONC_HCO3_GEN(0x7108),
    /**
     * Na Natrium (Sodium)
     */
    NOM_CONC_NA_GEN(0x710C),
    /**
     * K Kalium (Potassium)
     */
    NOM_CONC_K_GEN(0x7110),
    /**
     * Glu Glucose
     */
    NOM_CONC_GLU_GEN(0x7114),
    /**
     * iCa ionized Calcium
     */
    NOM_CONC_CA_GEN(0x7118),
    /**
     * &PCO2 Computed PCO2 at Patient Temperature
     */
    NOM_CONC_PCO2_GEN(0x7140),
    /**
     * Cl Chloride
     */
    NOM_CONC_CHLORIDE_GEN(0x7168),
    /**
     * 'BE,B Calculated Base Excess in Blood
     */
    NOM_BASE_EXCESS_BLD_ART(0x716C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_PO2_GEN(0x7174),
    /**
     * Met-Hb MetHemoglobin
     */
    NOM_CONC_HB_MET_GEN(0x717C),
    /**
     * CO-Hb Carboxy Hemoglobin
     */
    NOM_CONC_HB_CO_GEN(0x7180),
    /**
     * Hct Haematocrit
     */
    NOM_CONC_HCT_GEN(0x7184),
    /**
     * sFIO2 Setting: Inspired Oxygen Concentration
     */
    NOM_VENT_CONC_AWAY_O2_INSP(0x7498),
    /**
     * sIMV Setting: Ventilation Frequency in IMV Mode
     */
    NOM_VENT_MODE_MAND_INTERMIT(0xD02A),
    /**
     * Trect Rectal Temperature
     */
    NOM_TEMP_RECT(0xE004),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TEMP_BLD(0xE014),
    /**
     * DeltaTemp Difference Temperature
     */
    NOM_TEMP_DIFF(0xE018),
    /**
     * AWVex Expiratory Airway Volume Wave. Measured in l.
     */
    NOM_METRIC_NOS(0xEFFF),
    /**
     * STindx ST Index
     */
    NOM_ECG_AMPL_ST_INDEX(0xF03D),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TIME_TCUT_SENSOR(0xF03E),
    /**
     * SensrT Sensor Temperature
     */
    NOM_TEMP_TCUT_SENSOR(0xF03F),
    /**
     * ITBV Intrathoracic Blood Volume
     */
    NOM_VOL_BLD_INTRA_THOR(0xF040),
    /**
     * ITBVI Intrathoracic Blood Volume Index
     */
    NOM_VOL_BLD_INTRA_THOR_INDEX(0xF041),
    /**
     * EVLW Extravascular Lung Water
     */
    NOM_VOL_LUNG_WATER_EXTRA_VASC(0xF042),
    /**
     * EVLWI Extravascular Lung Water Index
     */
    NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX(0xF043),
    /**
     * EDV End Diastolic Volume
     */
    NOM_VOL_GLOBAL_END_DIA(0xF044),
    /**
     * EDVI End Diastolic Volume Index
     */
    NOM_VOL_GLOBAL_END_DIA_INDEX(0xF045),
    /**
     * CFI Cardiac Function Index
     */
    NOM_CARD_FUNC_INDEX(0xF046),
    /**
     * CCI Continuous Cardiac Output Index
     */
    NOM_OUTPUT_CARD_INDEX_CTS(0xF047),
    /**
     * SI Stroke Index
     */
    NOM_VOL_BLD_STROKE_INDEX(0xF048),
    /**
     * SVV Stroke Volume Variation
     */
    NOM_VOL_BLD_STROKE_VAR(0xF049),
    /**
     * SR Suppression Ratio
     */
    NOM_EEG_RATIO_SUPPRN(0xF04A),
    /**
     * SQI Signal Quality Index
     */
    NOM_EEG_BIS_SIG_QUAL_INDEX(0xF04D),
    /**
     * BIS Bispectral Index
     */
    NOM_EEG_BISPECTRAL_INDEX(0xF04E),
    /**
     * tcGas Generic Term for the Transcutaneous Gases
     */
    NOM_GAS_TCUT(0xF051),
    /**
     * MAC Airway MAC Concentration
     */
    NOM_CONC_AWAY_SUM_MAC_ET(0xF05E),
    /**
     * MAC Airway MAC Concentration
     */
    NOM_CONC_AWAY_SUM_MAC_INSP(0xF05F),
    /**
     * PVRI Pulmonary vascular Resistance PVRI
     */
    NOM_RES_VASC_PULM_INDEX(0xF067),
    /**
     * LCWI Left Cardiac Work Index
     */
    NOM_WK_CARD_LEFT_INDEX(0xF068),
    /**
     * RCWI Right Cardiac Work Index
     */
    NOM_WK_CARD_RIGHT_INDEX(0xF069),
    /**
     * VO2I Oxygen Consumption Index VO2I
     */
    NOM_SAT_O2_CONSUMP_INDEX(0xF06A),
    /**
     * PB Barometric Pressure = Ambient Pressure
     */
    NOM_PRESS_AIR_AMBIENT(0xF06B),
    /**
     * Sp-vO2 Difference between Spo2 and SvO2
     */
    NOM_SAT_DIFF_O2_ART_VEN(0xF06C),
    /**
     * DO2 Oxygen Availability DO2
     */
    NOM_SAT_O2_DELIVER(0xF06D),
    /**
     * DO2I Oxygen Availability Index
     */
    NOM_SAT_O2_DELIVER_INDEX(0xF06E),
    /**
     * O2ER Oxygen Extraction Ratio
     */
    NOM_RATIO_SAT_O2_CONSUMP_DELIVER(0xF06F),
    /**
     * Qs/Qt Percent Alveolarvenous Shunt Qs/Qt
     */
    NOM_RATIO_ART_VEN_SHUNT(0xF070),
    /**
     * BSA(D) BSA formula: Dubois
     */
    NOM_AREA_BODY_SURFACE(0xF071),
    /**
     * LI Light Intenisty. SvO2
     */
    NOM_INTENS_LIGHT(0xF072),
    /**
     * HeatPw NOM_DIM_MILLI_WATT
     */
    NOM_HEATING_PWR_TCUT_SENSOR(0xF076),
    /**
     * InjVol Injectate Volume (Cardiac Output)
     */
    NOM_VOL_INJ(0xF079),
    /**
     * ETVI ExtraVascular Thermo Volume Index. Cardiac Output.
     */
    NOM_VOL_THERMO_EXTRA_VASC_INDEX(0xF07A),
    /**
     * CathCt Generic Numeric Calculation Constant
     */
    NOM_NUM_CATHETER_CONST(0xF07C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_PULS_OXIM_PERF_REL_LEFT(0xF08A),
    /**
     * Perf r Relative Perfusion Right label
     */
    NOM_PULS_OXIM_PERF_REL_RIGHT(0xF08B),
    /**
     * PLETHr PLETH wave (right)
     */
    NOM_PULS_OXIM_PLETH_RIGHT(0xF08C),
    /**
     * PLETHl PLETH wave (left)
     */
    NOM_PULS_OXIM_PLETH_LEFT(0xF08D),
    /**
     * BUN Blood Urea Nitrogen
     */
    NOM_CONC_BLD_UREA_NITROGEN(0xF08F),
    /**
     * 'BEecf Calculated Base Excess
     */
    NOM_CONC_BASE_EXCESS_ECF(0xF090),
    /**
     * SpMV Spontaneous Minute Volume
     */
    NOM_VENT_VOL_MINUTE_AWAY_SPONT(0xF091),
    /**
     * Ca-vO2 Arteriovenous Oxygen Difference Ca-vO2
     */
    NOM_CONC_DIFF_HB_O2_ATR_VEN(0xF092),
    /**
     * Weight Patient Weight
     */
    NOM_PAT_WEIGHT(0xF093),
    /**
     * Height Patient Height
     */
    NOM_PAT_HEIGHT(0xF094),
    /**
     * MAC Minimum Alveolar Concentration
     */
    NOM_CONC_AWAY_MAC(0xF099),
    /**
     * PlethT Pleth wave from Telemetry
     */
    NOM_PULS_OXIM_PLETH_TELE(0xF09B),
    /**
     * %SpO2T SpO2 parameter label as sourced by the Telemetry system
     */
    NOM_PULS_OXIM_SAT_O2_TELE(0xF09C),
    /**
     * PulseT Pulse parameter label as sourced by the Telemetry system
     */
    NOM_PULS_OXIM_PULS_RATE_TELE(0xF09D),
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1(0xF0A4),
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_SYS(0xF0A5),
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_DIA(0xF0A6),
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_MEAN(0xF0A7),
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2(0xF0A8),
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_SYS(0xF0A9),
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_DIA(0xF0AA),
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_MEAN(0xF0AB),
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3(0xF0AC),
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3_SYS(0xF0AD),
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3_MEAN(0xF0AF),
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4(0xF0B0),
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_SYS(0xF0B1),
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_DIA(0xF0B2),
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_MEAN(0xF0B3),
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1(0xF0B4),
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_SYS(0xF0B5),
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_DIA(0xF0B6),
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_MEAN(0xF0B7),
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2(0xF0B8),
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_SYS(0xF0B9),
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_DIA(0xF0BA),
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_MEAN(0xF0BB),
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL(0xF0BC),
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_SYS(0xF0BD),
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_DIA(0xF0BE),
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_MEAN(0xF0BF),
    /**
     * BAP Brachial Arterial Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL(0xF0C0),
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_SYS(0xF0C1),
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_DIA(0xF0C2),
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_MEAN(0xF0C3),
    /**
     * Tvesic Temperature of the Urine fluid
     */
    NOM_TEMP_VESICAL(0xF0C4),
    /**
     * Tcereb Cerebral Temperature
     */
    NOM_TEMP_CEREBRAL(0xF0C5),
    /**
     * Tamb Ambient Temperature
     */
    NOM_TEMP_AMBIENT(0xF0C6),
    /**
     * T1 Generic Temperature 1 (T1)
     */
    NOM_TEMP_GEN_1(0xF0C7),
    /**
     * T2 Generic Temperature 2 (T2)
     */
    NOM_TEMP_GEN_2(0xF0C8),
    /**
     * T3 Generic Temperature 3 (T3)
     */
    NOM_TEMP_GEN_3(0xF0C9),
    /**
     * T4 Generic Temperature 4 (T4)
     */
    NOM_TEMP_GEN_4(0xF0CA),
    /**
     * sTVin Setting: inspired Tidal Volume
     */
    NOM_SETT_VOL_AWAY_INSP_TIDAL(0xF0E0),
    /**
     * TVexp expired Tidal Volume
     */
    NOM_VOL_AWAY_EXP_TIDAL(0xF0E1),
    /**
     * RRspir Respiration Rate from Spirometry
     */
    NOM_AWAY_RESP_RATE_SPIRO(0xF0E2),
    /**
     * PPV Pulse Pressure Variation
     */
    NOM_PULS_PRESS_VAR(0xF0E3),
    /**
     * Pulse Pulse from NBP
     */
    NOM_PRESS_BLD_NONINV_PULS_RATE(0xF0E5),
    /**
     * MRR Mandatory Respiratory Rate
     */
    NOM_VENT_RESP_RATE_MAND(0xF0F1),
    /**
     * MTV Mandatory Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_MAND(0xF0F2),
    /**
     * SpTV Spontaneuous Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_SPONT(0xF0F3),
    /**
     * cTnI Cardiac Troponin I
     */
    NOM_CARDIAC_TROPONIN_I(0xF0F4),
    /**
     * CPB Cardio Pulmonary Bypass Flag
     */
    NOM_CARDIO_PULMONARY_BYPASS_MODE(0xF0F5),
    /**
     * BNP Cardiac Brain Natriuretic Peptide
     */
    NOM_BNP(0xF0F6),
    /**
     * sPltTi Setting: Plateau Time
     */
    NOM_SETT_TIME_PD_RESP_PLAT(0xF0FF),
    /**
     * ScvO2 Central Venous Oxygen Saturation
     */
    NOM_SAT_O2_VEN_CENT(0xF100),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SNR(0xF101),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_HUMID(0xF103),
    /**
     * GEF Global Ejection Fraction
     */
    NOM_FRACT_EJECT(0xF105),
    /**
     * PVPI Pulmonary Vascular Permeability Index
     */
    NOM_PERM_VASC_PULM_INDEX(0xF106),
    /**
     * pToral Predictive Oral Temperature
     */
    NOM_TEMP_ORAL_PRED(0xF110),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TEMP_RECT_PRED(0xF114),
    /**
     * pTaxil Predictive Axillary Temperature
     */
    NOM_TEMP_AXIL_PRED(0xF118),
    /**
     * Air T Air Temperature in the Incubator
     */
    NOM_TEMP_AIR_INCUB(0xF12A),
    /**
     * Perf T Perf from Telemetry
     */
    NOM_PULS_OXIM_PERF_REL_TELE(0xF12C),
    /**
     * RLShnt Right-to-Left Heart Shunt
     */
    NOM_SHUNT_RIGHT_LEFT(0xF14A),
    /**
     * QT-HR QT HEARTRATE
     */
    NOM_ECG_TIME_PD_QT_HEART_RATE(0xF154),
    /**
     * QT Bsl
     */
    NOM_ECG_TIME_PD_QT_BASELINE(0xF155),
    /**
     * DeltaQTc
     */
    NOM_ECG_TIME_PD_QTc_DELTA(0xF156),
    /**
     * QTHRBl QT BASELINE HEARTRATE
     */
    NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE(0xF157),
    /**
     * pHc pH value in the capillaries
     */
    NOM_CONC_PH_CAP(0xF158),
    /**
     * PcCO2 Partial CO2 in the capillaries
     */
    NOM_CONC_PCO2_CAP(0xF159),
    /**
     * PcO2 Partial O2 in the capillaries
     */
    NOM_CONC_PO2_CAP(0xF15A),
    /**
     * iMg ionized Magnesium
     */
    NOM_CONC_MG_ION(0xF15B),
    /**
     * SerMg Magnesium in Serum
     */
    NOM_CONC_MG_SER(0xF15C),
    /**
     * tSerCa total of Calcium in Serum
     */
    NOM_CONC_tCA_SER(0xF15D),
    /**
     * SerPho Phosphat in Serum
     */
    NOM_CONC_P_SER(0xF15E),
    /**
     * SerCl Clorid in Serum
     */
    NOM_CONC_CHLOR_SER(0xF15F),
    /**
     * Fe Ferrum
     */
    NOM_CONC_FE_GEN(0xF160),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_ALB_SER(0xF163),
    /**
     * 'SaO2 Calculated SaO2
     */
    NOM_SAT_O2_ART_CALC(0xF164),
    /**
     * HbF Fetal Hemoglobin
     */
    NOM_CONC_HB_FETAL(0xF165),
    /**
     * Plts Platelets (thrombocyte count)
     */
    NOM_PLTS_CNT(0xF167),
    /**
     * WBC White Blood Count (leucocyte count)
     */
    NOM_WB_CNT(0xF168),
    /**
     * RBC Red Blood Count (erithrocyte count)
     */
    NOM_RB_CNT(0xF169),
    /**
     * RC Reticulocyte Count
     */
    NOM_RET_CNT(0xF16A),
    /**
     * PlOsm Plasma Osmolarity
     */
    NOM_PLASMA_OSM(0xF16B),
    /**
     * CreaCl Creatinine Clearance
     */
    NOM_CONC_CREA_CLR(0xF16C),
    /**
     * NsLoss Nitrogen Balance
     */
    NOM_NSLOSS(0xF16D),
    /**
     * Chol Cholesterin
     */
    NOM_CONC_CHOLESTEROL(0xF16E),
    /**
     * TGL Triglyzeride
     */
    NOM_CONC_TGL(0xF16F),
    /**
     * HDL High Density Lipoprotein
     */
    NOM_CONC_HDL(0xF170),
    /**
     * LDL Low Density Lipoprotein
     */
    NOM_CONC_LDL(0xF171),
    /**
     * Urea Urea used by the i-Stat
     */
    NOM_CONC_UREA_GEN(0xF172),
    /**
     * Crea Creatinine - Measured Value by the i-Stat Module
     */
    NOM_CONC_CREA(0xF173),
    /**
     * Lact Lactate. SMeasured value by the i-Stat module
     */
    NOM_CONC_LACT(0xF174),
    /**
     * tBili total Bilirubin
     */
    NOM_CONC_BILI_TOT(0xF177),
    /**
     * SerPro (Total) Protein in Serum
     */
    NOM_CONC_PROT_SER(0xF178),
    /**
     * tPro Total Protein
     */
    NOM_CONC_PROT_TOT(0xF179),
    /**
     * dBili direct Bilirubin
     */
    NOM_CONC_BILI_DIRECT(0xF17A),
    /**
     * LDH Lactate Dehydrogenase
     */
    NOM_CONC_LDH(0xF17B),
    /**
     * ESR Erithrocyte sedimentation rate
     */
    NOM_ES_RATE(0xF17C),
    /**
     * PCT Procalcitonin
     */
    NOM_CONC_PCT(0xF17D),
    /**
     * CK-MM Creatine Cinase of type muscle
     */
    NOM_CONC_CREA_KIN_MM(0xF17F),
    /**
     * SerCK Creatinin Kinase
     */
    NOM_CONC_CREA_KIN_SER(0xF180),
    /**
     * CK-MB Creatine Cinase of type muscle-brain
     */
    NOM_CONC_CREA_KIN_MB(0xF181),
    /**
     * CHE Cholesterinesterase
     */
    NOM_CONC_CHE(0xF182),
    /**
     * CRP C-reactive Protein
     */
    NOM_CONC_CRP(0xF183),
    /**
     * AST Aspartin - Aminotransferase
     */
    NOM_CONC_AST(0xF184),
    /**
     * AP Alkalische Phosphatase
     */
    NOM_CONC_AP(0xF185),
    /**
     * alphaA Alpha Amylase
     */
    NOM_CONC_ALPHA_AMYLASE(0xF186),
    /**
     * GPT Glutamic-Pyruvic-Transaminase
     */
    NOM_CONC_GPT(0xF187),
    /**
     * GOT Glutamic Oxaloacetic Transaminase
     */
    NOM_CONC_GOT(0xF188),
    /**
     * GGT Gamma GT = Gamma Glutamyltranspeptidase
     */
    NOM_CONC_GGT(0xF189),
    /**
     * ACT Activated Clotting Time. Measured value by the i-Stat module
     */
    NOM_TIME_PD_ACT(0xF18A),
    /**
     * PT Prothrombin Time
     */
    NOM_TIME_PD_PT(0xF18B),
    /**
     * PT INR Prothrombin Time - International Normalized Ratio
     */
    NOM_PT_INTL_NORM_RATIO(0xF18C),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TIME_PD_aPTT_WB(0xF18D),
    /**
     * aPTTPE aPTT Plasma Equivalent Time
     */
    NOM_TIME_PD_aPTT_PE(0xF18E),
    /**
     * PT WB Prothrombin Time (Blood)
     */
    NOM_TIME_PD_PT_WB(0xF18F),
    /**
     * PT PE Prothrombin Time (Plasma)
     */
    NOM_TIME_PD_PT_PE(0xF190),
    /**
     * TT Thrombin Time
     */
    NOM_TIME_PD_THROMBIN(0xF191),
    /**
     * CT Coagulation Time
     */
    NOM_TIME_PD_COAGULATION(0xF192),
    /**
     * Quick Thromboplastine Time
     */
    NOM_TIME_PD_THROMBOPLAS(0xF193),
    /**
     * FeNa Fractional Excretion of Sodium
     */
    NOM_FRACT_EXCR_NA(0xF194),
    /**
     * UrUrea Urine Urea
     */
    NOM_CONC_UREA_URINE(0xF195),
    /**
     * UrCrea Urine Creatinine
     */
    NOM_CONC_CREA_URINE(0xF196),
    /**
     * UrK Urine Potassium
     */
    NOM_CONC_K_URINE(0xF197),
    /**
     * UrKEx Urinary Potassium Excretion
     */
    NOM_CONC_K_URINE_EXCR(0xF198),
    /**
     * UrOsm Urine Osmolarity
     */
    NOM_CONC_OSM_URINE(0xF199),
    /**
     * UrCl Clorid in Urine
     */
    NOM_CONC_CHLOR_URINE(0xF19A),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_PRO_URINE(0xF19B),
    /**
     * UrCa Calzium in Urine
     */
    NOM_CONC_CA_URINE(0xF19C),
    /**
     * UrDens Density of the Urine fluid
     */
    NOM_FLUID_DENS_URINE(0xF19D),
    /**
     * UrHb Hemoglobin (Urine)
     */
    NOM_CONC_HB_URINE(0xF19E),
    /**
     * UrGlu Glucose in Urine
     */
    NOM_CONC_GLU_URINE(0xF19F),
    /**
     * 'ScO2 Calculated ScO2
     */
    NOM_SAT_O2_CAP_CALC(0xF1A0),
    /**
     * 'AnGap Calculated AnionGap
     */
    NOM_CONC_AN_GAP_CALC(0xF1A1),
    /**
     * SpO2pr Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL(0xF1C0),
    /**
     * SpO2po Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2_POST_DUCTAL(0xF1D4),
    /**
     * PerfPo Relative Perfusion Left
     */
    NOM_PULS_OXIM_PERF_REL_POST_DUCTAL(0xF1DC),
    /**
     * PerfPr Relative Perfusion Left
     */
    NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL(0xF22C),
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5(0xF3F4),
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_SYS(0xF3F5),
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_DIA(0xF3F6),
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_MEAN(0xF3F7),
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6(0xF3F8),
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_SYS(0xF3F9),
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_DIA(0xF3FA),
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_MEAN(0xF3FB),
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7(0xF3FC),
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7_SYS(0xF3FD),
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7_MEAN(0xF3FF),
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8(0xF400),
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_SYS(0xF401),
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_DIA(0xF402),
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_MEAN(0xF403),
    /**
     * Rf-I ST Reference Value for Lead I
     */
    NOM_ECG_AMPL_ST_BASELINE_I(0xF411),
    /**
     * Rf-II ST Reference Value for Lead II
     */
    NOM_ECG_AMPL_ST_BASELINE_II(0xF412),
    /**
     * Rf-V1 ST Reference Value for Lead V1
     */
    NOM_ECG_AMPL_ST_BASELINE_V1(0xF413),
    /**
     * Rf-V2 ST Reference Value for Lead V2
     */
    NOM_ECG_AMPL_ST_BASELINE_V2(0xF414),
    /**
     * Rf-V3 ST Reference Value for Lead V3
     */
    NOM_ECG_AMPL_ST_BASELINE_V3(0xF415),
    /**
     * Rf-V4 ST Reference Value for Lead V4
     */
    NOM_ECG_AMPL_ST_BASELINE_V4(0xF416),
    /**
     * Rf-V5 ST Reference Value for Lead V5
     */
    NOM_ECG_AMPL_ST_BASELINE_V5(0xF417),
    /**
     * Rf-V6 ST Reference Value for Lead V6
     */
    NOM_ECG_AMPL_ST_BASELINE_V6(0xF418),
    /**
     * Rf-III ST Reference Value for Lead III
     */
    NOM_ECG_AMPL_ST_BASELINE_III(0xF44D),
    /**
     * Rf-aVR ST Reference Value for Lead aVR
     */
    NOM_ECG_AMPL_ST_BASELINE_AVR(0xF44E),
    /**
     * Rf-aVL ST Reference Value for Lead aVL
     */
    NOM_ECG_AMPL_ST_BASELINE_AVL(0xF44F),
    /**
     * Rf-aVF ST Reference Value for Lead aVF
     */
    NOM_ECG_AMPL_ST_BASELINE_AVF(0xF450),
    /**
     * Age actual patient age. measured in years
     */
    NOM_AGE(0xF810),
    /**
     * G.Age Gestational age for neonatal
     */
    NOM_AGE_GEST(0xF811),
    /**
     * r Correlation Coefficient
     */
    NOM_AWAY_CORR_COEF(0xF814),
    /**
     * SpAWRR Spontaneous Airway Respiration Rate
     */
    NOM_AWAY_RESP_RATE_SPONT(0xF815),
    /**
     * TC Time Constant
     */
    NOM_AWAY_TC(0xF816),
    /**
     * Length Length for neonatal/pediatric
     */
    NOM_BIRTH_LENGTH(0xF818),
    /**
     * RSBI Rapid Shallow Breathing Index
     */
    NOM_BREATH_RAPID_SHALLOW_INDEX(0xF819),
    /**
     * C20/C Overdistension Index
     */
    NOM_C20_PER_C_INDEX(0xF81A),
    /**
     * HI Heart Contractility Index
     */
    NOM_CARD_CONTRACT_HEATHER_INDEX(0xF81C),
    /**
     * ALP Alveolarproteinose Rosen-Castleman-Liebow- Syndrom
     */
    NOM_CONC_ALP(0xF81D),
    /**
     * iCa(N) ionized Calcium Normalized
     */
    NOM_CONC_CA_GEN_NORM(0xF822),
    /**
     * SerCa Calcium in Serum
     */
    NOM_CONC_CA_SER(0xF824),
    /**
     * tCO2 total of CO2 - result of Blood gas Analysis
     */
    NOM_CONC_CO2_TOT(0xF825),
    /**
     * 'tCO2 Calculated total CO2
     */
    NOM_CONC_CO2_TOT_CALC(0xF826),
    /**
     * SCrea Serum Creatinine
     */
    NOM_CONC_CREA_SER(0xF827),
    /**
     * SpRR Spontaneous Respiration Rate
     */
    NOM_RESP_RATE_SPONT(0xF828),
    /**
     * SerGlo Globulin in Serum
     */
    NOM_CONC_GLO_SER(0xF829),
    /**
     * SerGlu Glucose in Serum
     */
    NOM_CONC_GLU_SER(0xF82A),
    /**
     * MCHC Mean Corpuscular Hemoglobin Concentration
     */
    NOM_CONC_HB_CORP_MEAN(0xF82C),
    /**
     * SerK Kalium (Potassium) in Serum
     */
    NOM_CONC_K_SER(0xF82F),
    /**
     * UrNaEx Urine Sodium Excretion
     */
    NOM_CONC_NA_EXCR(0xF830),
    /**
     * &PaCO2 Computed PaCO2 at Patient Temperature on the arterial blood
     */
    NOM_CONC_PCO2_ART_ADJ(0xF832),
    /**
     * &PcCO2 Computed PcO2 at Patient Temperature
     */
    NOM_CONC_PCO2_CAP_ADJ(0xF833),
    /**
     * &pHc Adjusted pH value in the capillaries
     */
    NOM_CONC_PH_CAP_ADJ(0xF837),
    /**
     * &pH Adjusted pH at &Patient Temperature
     */
    NOM_CONC_PH_GEN_ADJ(0xF838),
    /**
     * &PaO2 Adjusted PaO2 at Patient Temperature on the arterial blood
     */
    NOM_CONC_PO2_ART_ADJ(0xF83B),
    /**
     * &PcO2 Adjusted PcO2 at Patient Temperature
     */
    NOM_CONC_PO2_CAP_ADJ(0xF83C),
    /**
     * COsm Osmolar Clearance
     */
    NOM_CREA_OSM(0xF83F),
    /**
     * BSI Burst Suppression Indicator
     */
    NOM_EEG_BURST_SUPPRN_INDEX(0xF840),
    /**
     * LSCALE Scale of the Left Channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT(0xF841),
    /**
     * RSCALE Scale of the Right Channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT(0xF842),
    /**
     * LT MPF Median Power Frequency - Left Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT(0xF84B),
    /**
     * RT MPF Median Power Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT(0xF84C),
    /**
     * LT AL Absolute Alpha - Left Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT(0xF855),
    /**
     * RT AL Absolute Alpha - Right Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT(0xF856),
    /**
     * LT BE Absolute Beta - Left Side
     */
    NOM_EEG_PWR_SPEC_BETA_ABS_LEFT(0xF85B),
    /**
     * RT BE Absolute Beta - Right Side
     */
    NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT(0xF85C),
    /**
     * LT DL Absolute Delta - Left Side
     */
    NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT(0xF863),
    /**
     * RT DL Absolute Delta - Right Side
     */
    NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT(0xF864),
    /**
     * LT TH Absolute Theta - Left Side
     */
    NOM_EEG_PWR_SPEC_THETA_ABS_LEFT(0xF869),
    /**
     * RT TH Absolute Theta - Right Side
     */
    NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT(0xF86A),
    /**
     * AAI A-Line ARX Index
     */
    NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI(0xF873),
    /**
     * O2EI Oxygen Extraction Index
     */
    NOM_EXTRACT_O2_INDEX(0xF875),
    /**
     * sfgAir Setting: Total fresh gas Air flow on the mixer
     */
    NOM_SETT_FLOW_AWAY_AIR(0xF877),
    /**
     * eeFlow Expiratory Peak Flow
     */
    NOM_FLOW_AWAY_EXP_ET(0xF87A),
    /**
     * SpPkFl Spontaneous Peak Flow
     */
    NOM_FLOW_AWAY_MAX_SPONT(0xF87D),
    /**
     * sfgFl Setting: Total fresh gas Flow on the mixer
     */
    NOM_SETT_FLOW_AWAY_TOT(0xF881),
    /**
     * VCO2ti CO2 Tidal Production
     */
    NOM_FLOW_CO2_PROD_RESP_TIDAL(0xF882),
    /**
     * U/O Daily Urine output
     */
    NOM_FLOW_URINE_PREV_24HR(0xF883),
    /**
     * CH2O Free Water Clearance
     */
    NOM_FREE_WATER_CLR(0xF884),
    /**
     * MCH Mean Corpuscular Hemoglobin. Is the erithrocyte hemoglobin content
     */
    NOM_HB_CORP_MEAN(0xF885),
    /**
     * Power Power requ'd to set the Air&Pat Temp in the incubator
     */
    NOM_HEATING_PWR_INCUBATOR(0xF886),
    /**
     * ACI Accelerated Cardiac Index
     */
    NOM_OUTPUT_CARD_INDEX_ACCEL(0xF889),
    /**
     * PTC Post Tetatic Count stimulation
     */
    NOM_PTC_CNT(0xF88B),
    /**
     * PlGain Pleth Gain
     */
    NOM_PULS_OXIM_PLETH_GAIN(0xF88D),
    /**
     * RVrat Rate Volume Ratio
     */
    NOM_RATIO_AWAY_RATE_VOL_AWAY(0xF88E),
    /**
     * BUN/cr BUN Creatinine Ratio
     */
    NOM_RATIO_BUN_CREA(0xF88F),
    /**
     * 'B/Cre Ratio BUN/Creatinine. Calculated value by the i-Stat module
     */
    NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC(0xF890),
    /**
     * 'U/Cre Ratio Urea/Creatinine. Calculated value by the i-Stat module
     */
    NOM_RATIO_CONC_URINE_CREA_CALC(0xF891),
    /**
     * U/SCr Urine Serum Creatinine Ratio
     */
    NOM_RATIO_CONC_URINE_CREA_SER(0xF892),
    /**
     * UrNa/K Urine Sodium/Potassium Ratio
     */
    NOM_RATIO_CONC_URINE_NA_K(0xF893),
    /**
     * PaFIO2 PaO2 to FIO2 ratio. Expressed in mmHg to % ratio
     */
    NOM_RATIO_PaO2_FIO2(0xF894),
    /**
     * PTrat Prothrombin Time Ratio
     */
    NOM_RATIO_TIME_PD_PT(0xF895),
    /**
     * PTTrat Activated Partial Thromboplastin Time Ratio
     */
    NOM_RATIO_TIME_PD_PTT(0xF896),
    /**
     * TOFrat Train Of Four (TOF) ratio
     */
    NOM_RATIO_TRAIN_OF_FOUR(0xF897),
    /**
     * U/POsm Urine Plasma Osmolarity Ratio
     */
    NOM_RATIO_URINE_SER_OSM(0xF898),
    /**
     * Rdyn Dynamic Lung Resistance
     */
    NOM_RES_AWAY_DYN(0xF899),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_RESP_BREATH_ASSIST_CNT(0xF89A),
    /**
     * REF Right Heart Ejection Fraction
     */
    NOM_RIGHT_HEART_FRACT_EJECT(0xF89B),
    /**
     * RemTi Remaining Time until next stimulation
     */
    NOM_TIME_PD_EVOK_REMAIN(0xF8A0),
    /**
     * ExpTi Expiratory Time
     */
    NOM_TIME_PD_EXP(0xF8A1),
    /**
     * Elapse Time to Elapse Counter
     */
    NOM_TIME_PD_FROM_LAST_MSMT(0xF8A2),
    /**
     * InsTi Spontaneous Inspiration Time
     */
    NOM_TIME_PD_INSP(0xF8A3),
    /**
     * KCT Kaolin cephalin time
     */
    NOM_TIME_PD_KAOLIN_CEPHALINE(0xF8A4),
    /**
     * PTT Partial Thromboplastin Time
     */
    NOM_TIME_PD_PTT(0xF8A5),
    /**
     * sRepTi Setting: Preset Train Of Four (Slow TOF) repetition time
     */
    NOM_SETT_TIME_PD_TRAIN_OF_FOUR(0xF8A6),
    /**
     * TOF1 TrainOf Four (TOF) first response value TOF1
     */
    NOM_TRAIN_OF_FOUR_1(0xF8A7),
    /**
     * TOF2 TrainOf Four (TOF) first response value TOF2
     */
    NOM_TRAIN_OF_FOUR_2(0xF8A8),
    /**
     * TOF3 TrainOf Four (TOF) first response value TOF3
     */
    NOM_TRAIN_OF_FOUR_3(0xF8A9),
    /**
     * TOF4 TrainOf Four (TOF) first response value TOF4
     */
    NOM_TRAIN_OF_FOUR_4(0xF8AA),
    /**
     * TOFcnt Train Of Four (TOF) count - Number of TOF responses.
     */
    NOM_TRAIN_OF_FOUR_CNT(0xF8AB),
    /**
     * Twitch Twitch height of the 1Hz/0.1Hz stimulation response
     */
    NOM_TWITCH_AMPL(0xF8AC),
    /**
     * SrUrea Serum Urea
     */
    NOM_UREA_SER(0xF8AD),
    /**
     * sUrTi Setting: Preset period of time for the UrVol numeric
     */
    NOM_SETT_URINE_BAL_PD(0xF8AF),
    /**
     * PtVent Parameter which informs whether the Patient is ventilated
     */
    NOM_VENT_ACTIVE(0xF8B0),
    /**
     * HFVAmp High Frequency Ventilation Amplitude
     */
    NOM_VENT_AMPL_HFV(0xF8B1),
    /**
     * i-eAGT Inspired - EndTidal Agent
     */
    NOM_VENT_CONC_AWAY_AGENT_DELTA(0xF8B2),
    /**
     * i-eDES Inspired - EndTidal Desfluran
     */
    NOM_VENT_CONC_AWAY_DESFL_DELTA(0xF8B3),
    /**
     * i-eENF Inspired - EndTidal Enfluran
     */
    NOM_VENT_CONC_AWAY_ENFL_DELTA(0xF8B4),
    /**
     * i-eHAL Inspired - EndTidal Halothane
     */
    NOM_VENT_CONC_AWAY_HALOTH_DELTA(0xF8B5),
    /**
     * i-eISO Inspired - EndTidal Isofluran
     */
    NOM_VENT_CONC_AWAY_ISOFL_DELTA(0xF8B6),
    /**
     * i-eN2O Inspired - EndTidal N2O
     */
    NOM_VENT_CONC_AWAY_N2O_DELTA(0xF8B7),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VENT_CONC_AWAY_O2_CIRCUIT(0xF8B8),
    /**
     * i-eSEV Inspired - EndTidal Sevofluran
     */
    NOM_VENT_CONC_AWAY_SEVOFL_DELTA(0xF8B9),
    /**
     * loPEEP Alarm Limit: Low PEEP/CPAP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO(0xF8BA),
    /**
     * sPSV Setting: Pressure Support Ventilation
     */
    NOM_SETT_VENT_PRESS_AWAY_PV(0xF8BC),
    /**
     * RiseTi Rise Time
     */
    NOM_VENT_TIME_PD_RAMP(0xF8BD),
    /**
     * HFTVin Inspired High Frequency Tidal Volume
     */
    NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV(0xF8BE),
    /**
     * HFVTV High Frequency Fraction Ventilation Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_HFV(0xF8BF),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VENT_VOL_TIDAL_SIGH(0xF8C0),
    /**
     * SpTVex Spontaenous Expired Tidal Volume
     */
    NOM_VOL_AWAY_EXP_TIDAL_SPONT(0xF8C2),
    /**
     * TVPSV Tidal Volume (TV) in Pressure Support Ventilation mode
     */
    NOM_VOL_AWAY_TIDAL_PSV(0xF8C3),
    /**
     * MCV Mean Corpuscular Volume
     */
    NOM_VOL_CORP_MEAN(0xF8C4),
    /**
     * TFC Thoracic Fluid Content
     */
    NOM_VOL_FLUID_THORAC(0xF8C5),
    /**
     * TFI Thoracic Fluid Content Index
     */
    NOM_VOL_FLUID_THORAC_INDEX(0xF8C6),
    /**
     * AGTLev Liquid level in the anesthetic agent bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_AGENT(0xF8C7),
    /**
     * DESLev Liquid level in the DESflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_DESFL(0xF8C8),
    /**
     * ENFLev Liquid level in the ENFlurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_ENFL(0xF8C9),
    /**
     * HALLev Liquid level in the HALothane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH(0xF8CA),
    /**
     * ISOLev Liquid level in the ISOflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL(0xF8CB),
    /**
     * SEVLev Liquid level in the SEVoflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL(0xF8CC),
    /**
     * HFMVin Inspired High Frequency Mandatory Minute Volume
     */
    NOM_VOL_MINUTE_AWAY_INSP_HFV(0xF8CD),
    /**
     * tUrVol Total Urine Volume of the current measurement period
     */
    NOM_VOL_URINE_BAL_PD_INSTANT(0xF8CE),
    /**
     * UrVSht Urimeter - Urine Shift Volume.
     */
    NOM_VOL_URINE_SHIFT(0xF8CF),
    /**
     * ESVI End Systolic Volume Index
     */
    NOM_VOL_VENT_L_END_SYS_INDEX(0xF8D1),
    /**
     * BagWgt Weight of the Urine Disposable Bag
     */
    NOM_WEIGHT_URINE_COL(0xF8D3),
    /**
     * sAADel Setting: Apnea Ventilation Delay
     */
    NOM_SETT_APNEA_ALARM_DELAY(0xF8D9),
    /**
     * sARR Setting: Apnea Respiration Rate
     */
    NOM_SETT_AWAY_RESP_RATE_APNEA(0xF8DE),
    /**
     * sHFVRR Setting: High Frequency Ventilation Respiration Rate
     */
    NOM_SETT_AWAY_RESP_RATE_HFV(0xF8DF),
    /**
     * sChrge Setting: Preset stimulation charge
     */
    NOM_SETT_EVOK_CHARGE(0xF8E6),
    /**
     * sCurnt Setting: Preset stimulation current
     */
    NOM_SETT_EVOK_CURR(0xF8E7),
    /**
     * sExpFl Setting: Expiratory Flow
     */
    NOM_SETT_FLOW_AWAY_EXP(0xF8EA),
    /**
     * sHFVFl Setting: High Freqyency Ventilation Flow
     */
    NOM_SETT_FLOW_AWAY_HFV(0xF8EB),
    /**
     * sInsFl Setting: Inspiratory Flow.
     */
    NOM_SETT_FLOW_AWAY_INSP(0xF8EC),
    /**
     * sAPkFl Setting: Apnea Peak Flow
     */
    NOM_SETT_FLOW_AWAY_INSP_APNEA(0xF8ED),
    /**
     * sHFVAm Setting: HFV Amplitude (Peak to Peak Pressure)
     */
    NOM_SETT_HFV_AMPL(0xF8F3),
    /**
     * loPmax Setting: Low Maximum Airway Pressure Alarm Setting.
     */
    NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO(0xF8FB),
    /**
     * sPVE Setting: Pressure Ventilation E component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_EXP_PV(0xF900),
    /**
     * sAPVE Setting: Apnea Pressure Ventilation E component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_EXP_PV_APNEA(0xF901),
    /**
     * sPVI Setting: Pressure Ventilation I component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_INSP_PV(0xF902),
    /**
     * sAPVI Setting: Apnea Pressure Ventilation I component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_INSP_PV_APNEA(0xF903),
    /**
     * sSens Setting: Assist Sensitivity. Used by the Bear 1000 ventilator.
     */
    NOM_SETT_SENS_LEVEL(0xF904),
    /**
     * sPulsD Setting: Preset stimulation impulse duration
     */
    NOM_SETT_TIME_PD_EVOK(0xF908),
    /**
     * sCycTi Setting: Cycle Time
     */
    NOM_SETT_TIME_PD_MSMT(0xF909),
    /**
     * sO2Mon Setting: O2 Monitoring
     */
    NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE(0xF90E),
    /**
     * sBkgFl Setting: Background Flow Setting. Range is 2 - 30 l/min
     */
    NOM_SETT_VENT_AWAY_FLOW_BACKGROUND(0xF90F),
    /**
     * sBasFl Setting: Flow-by Base Flow
     */
    NOM_SETT_VENT_AWAY_FLOW_BASE(0xF910),
    /**
     * sSenFl Setting: Flow-by Sensitivity Flow
     */
    NOM_SETT_VENT_AWAY_FLOW_SENSE(0xF911),
    /**
     * sPincR Setting: Pressure Increase Rate
     */
    NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE(0xF912),
    /**
     * sAFIO2 Setting: Apnea Inspired O2 Concentration
     */
    NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA(0xF917),
    /**
     * sAPVO2 Setting: Apnea Pressure Ventilation Oxygen Concentration
     */
    NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA(0xF918),
    /**
     * highO2 Alarm Limit. High Oxygen (O2) Alarm Limit
     */
    NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI(0xF919),
    /**
     * lowO2 Alarm Limit: Low Oxygen (O2) Alarm Limit
     */
    NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO(0xF91A),
    /**
     * sFlow Setting: Flow
     */
    NOM_SETT_VENT_FLOW(0xF91B),
    /**
     * sFlas Setting: Flow Assist level for the CPAP mode
     */
    NOM_SETT_VENT_FLOW_AWAY_ASSIST(0xF91C),
    /**
     * sTrgFl Setting: Flow Trigger - delivered by the Evita 2 Vuelink Driver
     */
    NOM_SETT_VENT_FLOW_INSP_TRIG(0xF91D),
    /**
     * sGasPr Setting: Gas Sample point for the oxygen measurement
     */
    NOM_SETT_VENT_GAS_PROBE_POSN(0xF920),
    /**
     * sCMV Setting: Controlled mechanical ventilation
     */
    NOM_SETT_VENT_MODE_MAND_CTS_ONOFF(0xF922),
    /**
     * sEnSgh Setting: Enable Sigh
     */
    NOM_SETT_VENT_MODE_SIGH(0xF923),
    /**
     * sSIMV Setting: Synchronized intermittent mandatory ventilation
     */
    NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT(0xF924),
    /**
     * sO2Cal Setting: O2 Calibration
     */
    NOM_SETT_VENT_O2_CAL_MODE(0xF926),
    /**
     * sO2Pr Setting: Gas sample point for oxygen measurement
     */
    NOM_SETT_VENT_O2_PROBE_POSN(0xF927),
    /**
     * sO2Suc Setting: Suction Oxygen Concentration
     */
    NOM_SETT_VENT_O2_SUCTION_MODE(0xF928),
    /**
     * sSPEEP Setting: Pressure Support PEEP
     */
    NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT(0xF92C),
    /**
     * sPlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_PRESS_AWAY_EXP_APRV(0xF92D),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VENT_PRESS_AWAY_INSP_APRV(0xF92E),
    /**
     * highP Alarm Limit: High Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI(0xF930),
    /**
     * sAPVhP Setting: Apnea Pressure Ventilation High Airway Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA(0xF931),
    /**
     * sAPVcP Setting: Apnea Pressure Ventilation Control Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_PV_APNEA(0xF933),
    /**
     * sustP Alarm Limit: Sustained Pressure Alarm Limit.
     */
    NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI(0xF935),
    /**
     * sfmax Setting: Panting Limit
     */
    NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT(0xF937),
    /**
     * sIPPV Setting: Ventilation Frequency in IPPV Mode
     */
    NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP(0xF939),
    /**
     * sAPVRR Setting: Apnea Pressure Ventilation Respiration Rate
     */
    NOM_SETT_VENT_RESP_RATE_PV_APNEA(0xF93A),
    /**
     * sSghNr Setting: Multiple Sigh Number
     */
    NOM_SETT_VENT_SIGH_MULT_RATE(0xF93B),
    /**
     * sSghR Setting: Sigh Rate
     */
    NOM_SETT_VENT_SIGH_RATE(0xF93C),
    /**
     * sExpTi Setting: Exhaled Time
     */
    NOM_SETT_VENT_TIME_PD_EXP(0xF93F),
    /**
     * sTlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_TIME_PD_EXP_APRV(0xF940),
    /**
     * sInsTi Setting: Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP(0xF941),
    /**
     * sThigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_TIME_PD_INSP_APRV(0xF942),
    /**
     * sPVinT Setting: Pressure Ventilation Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP_PV(0xF943),
    /**
     * sAPVTi Setting: Apnea Pressure Ventilation Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA(0xF944),
    /**
     * sALMRT Setting: Alarm Percentage on Rise Time.
     */
    NOM_SETT_VENT_TIME_PD_RAMP_AL(0xF946),
    /**
     * sVolas Setting: Volume Assist level for the CPAP mode
     */
    NOM_SETT_VENT_VOL_AWAY_ASSIST(0xF948),
    /**
     * sVmax Setting: Volume Warning - delivered by the Evita 2 Vuelink Driver
     */
    NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF(0xF949),
    /**
     * highMV Alarm Limit: High Minute Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI(0xF94B),
    /**
     * lowMV Alarm Limit: Low Minute Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO(0xF94C),
    /**
     * highTV Alarm Limit: High Tidal Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI(0xF94D),
    /**
     * lowTV Alarm Limit: Low Tidal Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO(0xF94E),
    /**
     * sATV Setting: Apnea Tidal Volume
     */
    NOM_SETT_VOL_AWAY_TIDAL_APNEA(0xF951),
    /**
     * sTVap Setting: Applied Tidal Volume.
     */
    NOM_SETT_VOL_AWAY_TIDAL_APPLIED(0xF952),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VOL_MINUTE_ALARM_DELAY(0xF953),
    /**
     * StO2 O2 Saturation (tissue)
     */
    NOM_SAT_O2_TISSUE(0xF960),
    /**
     * CSI
     */
    NOM_CEREB_STATE_INDEX(0xF961),
    /**
     * SO2 1 O2 Saturation 1 (generic)
     */
    NOM_SAT_O2_GEN_1(0xF962),
    /**
     * SO2 2 O2 Saturation 2 (generic)
     */
    NOM_SAT_O2_GEN_2(0xF963),
    /**
     * SO2 3 O2 Saturation 3 (generic)
     */
    NOM_SAT_O2_GEN_3(0xF964),
    /**
     * SO2 4 O2 Saturation 4 (generic)
     */
    NOM_SAT_O2_GEN_4(0xF965),
    /**
     * T1Core Core Temperature 1 (generic)
     */
    NOM_TEMP_CORE_GEN_1(0xF966),
    /**
     * T2Core Core Temperature 2 (generic)
     */
    NOM_TEMP_CORE_GEN_2(0xF967),
    /**
     * DeltaP Blood Pressure difference
     */
    NOM_PRESS_BLD_DIFF(0xF968),
    /**
     * DeltaP1 Blood Pressure difference 1 (generic)
     */
    NOM_PRESS_BLD_DIFF_GEN_1(0xF96C),
    /**
     * DeltaP2 Blood Pressure difference 2 (generic)
     */
    NOM_PRESS_BLD_DIFF_GEN_2(0xF970),
    /**
     * HLMfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_MAIN(0xF974),
    /**
     * SlvPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_SLAVE(0xF975),
    /**
     * SucPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_SUCTION(0xF976),
    /**
     * AuxPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_AUX(0xF977),
    /**
     * PlePfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0xF978),
    /**
     * SplPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0xF979),
    /**
     * AxOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START(0xF97A),
    /**
     * AxOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP(0xF97B),
    /**
     * AxDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX(0xF97C),
    /**
     * AxTVol
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX(0xF97D),
    /**
     * AxPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX(0xF97E),
    /**
     * CpOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START(0xF97F),
    /**
     * CpOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP(0xF980),
    /**
     * CpDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0xF981),
    /**
     * CpTVol
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0xF982),
    /**
     * CpPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0xF983),
    /**
     * CsOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START(0xF984),
    /**
     * CsOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP(0xF985),
    /**
     * CsDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0xF986),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0xF987),
    /**
     * CsPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0xF988),
    /**
     * Tin/Tt
     */
    NOM_RATIO_INSP_TOTAL_BREATH_SPONT(0xF990),
    /**
     * tPEEP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL(0xF991),
    /**
     * Cpav
     */
    NOM_COMPL_LUNG_PAV(0xF992),
    /**
     * Rpav
     */
    NOM_RES_AWAY_PAV(0xF993),
    /**
     * Rtot
     */
    NOM_RES_AWAY_EXP_TOTAL(0xF994),
    /**
     * Epav
     */
    NOM_ELAS_LUNG_PAV(0xF995),
    /**
     * RSBInm
     */
    NOM_BREATH_RAPID_SHALLOW_INDEX_NORM(0xF996),
;
    
    private final int x;
    
    private ObservedValue(final int x) {
        this.x = x;
    }
    
    private static final Map<Integer, ObservedValue> map = OrdinalEnum.buildInt(ObservedValue.class);

    public static final ObservedValue valueOf(int x) {
        return map.get(x);
    }
    public final OIDType asOID() {
    	return OIDType.lookup(asInt());
    }
    public final int asInt()  {
        return x;
    }

}
