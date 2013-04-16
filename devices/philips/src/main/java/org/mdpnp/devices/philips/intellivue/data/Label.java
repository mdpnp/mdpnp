package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum Label implements OrdinalEnum.LongType {
    /**
     * ECG Unspecific ECG wave
     */
    NLS_NOM_ECG_ELEC_POTL(0x00020100L),
    /**
     * I ECG Lead I
     */
    NLS_NOM_ECG_ELEC_POTL_I(0x00020101L),
    /**
     * II ECG Lead II
     */
    NLS_NOM_ECG_ELEC_POTL_II(0x00020102L),
    /**
     * V1 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V1(0x00020103L),
    /**
     * V2 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V2(0x00020104L),
    /**
     * V3 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V3(0x00020105L),
    /**
     * V4 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V4(0x00020106L),
    /**
     * V5 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V5(0x00020107L),
    /**
     * V6 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V6(0x00020108L),
    /**
     * III ECG Lead III
     */
    NLS_NOM_ECG_ELEC_POTL_III(0x0002013DL),
    /**
     * aVR ECG Lead AVR
     */
    NLS_NOM_ECG_ELEC_POTL_AVR(0x0002013EL),
    /**
     * aVL ECG Lead AVL
     */
    NLS_NOM_ECG_ELEC_POTL_AVL(0x0002013FL),
    /**
     * aVF ECG Lead AVF
     */
    NLS_NOM_ECG_ELEC_POTL_AVF(0x00020140L),
    /**
     * V ECG Lead V
     */
    NLS_NOM_ECG_ELEC_POTL_V(0x00020143L),
    /**
     * MCL ECG Lead MCL
     */
    NLS_NOM_ECG_ELEC_POTL_MCL(0x0002014BL),
    /**
     * MCL1 ECG Lead MCL1
     */
    NLS_NOM_ECG_ELEC_POTL_MCL1(0x0002014CL),
    /**
     * ST ST generic label
     */
    NLS_NOM_ECG_AMPL_ST(0x00020300L),
    /**
     * QT
     */
    NLS_NOM_ECG_TIME_PD_QT_GL(0x00023F20L),
    /**
     * QTc
     */
    NLS_NOM_ECG_TIME_PD_QTc(0x00023F24L),
    /**
     * HR Heart Rate
     */
    NLS_NOM_ECG_CARD_BEAT_RATE(0x00024182L),
    /**
     * btbHR Cardiac Beat-to-Beat Rate
     */
    NLS_NOM_ECG_CARD_BEAT_RATE_BTB(0x0002418AL),
    /**
     * PVC Premature Ventricular Contractions
     */
    NLS_NOM_ECG_V_P_C_CNT(0x00024261L),
    /**
     * Pulse Pulse Rate
     */
    NLS_NOM_PULS_RATE(0x0002480AL),
    /**
     * Pulse Pulse Rate from Plethysmogram
     */
    NLS_NOM_PULS_OXIM_PULS_RATE(0x00024822L),
    /**
     * SVRI Systemic Vascular Resistance Index
     */
    NLS_NOM_RES_VASC_SYS_INDEX(0x00024900L),
    /**
     * LVSWI Left Ventricular Stroke Volume Index
     */
    NLS_NOM_WK_LV_STROKE_INDEX(0x00024904L),
    /**
     * RVSWI Right Ventricular Stroke Work Index
     */
    NLS_NOM_WK_RV_STROKE_INDEX(0x00024908L),
    /**
     * C.I. Cardiac Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX(0x0002490CL),
    /**
     * P unspecific pressure
     */
    NLS_NOM_PRESS_BLD(0x00024A00L),
    /**
     * NBP non-invasive blood pressure
     */
    NLS_NOM_PRESS_BLD_NONINV(0x00024A04L),
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NLS_NOM_PRESS_BLD_AORT(0x00024A0CL),
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NLS_NOM_PRESS_BLD_ART(0x00024A10L),
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NLS_NOM_PRESS_BLD_ART_ABP(0x00024A14L),
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NLS_NOM_PRESS_BLD_ART_PULM(0x00024A1CL),
    /**
     * PAWP Pulmonary Artery Wedge Pressure
     */
    NLS_NOM_PRESS_BLD_ART_PULM_WEDGE(0x00024A24L),
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NLS_NOM_PRESS_BLD_ART_UMB(0x00024A28L),
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NLS_NOM_PRESS_BLD_ATR_LEFT(0x00024A30L),
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NLS_NOM_PRESS_BLD_ATR_RIGHT(0x00024A34L),
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NLS_NOM_PRESS_BLD_VEN_CENT(0x00024A44L),
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NLS_NOM_PRESS_BLD_VEN_UMB(0x00024A48L),
    /**
     * VO2 Oxygen Consumption VO2
     */
    NLS_NOM_SAT_O2_CONSUMP(0x00024B00L),
    /**
     * C.O. Cardiac Output
     */
    NLS_NOM_OUTPUT_CARD(0x00024B04L),
    /**
     * PVR Pulmonary vascular Resistance
     */
    NLS_NOM_RES_VASC_PULM(0x00024B24L),
    /**
     * SVR Systemic Vascular Resistance
     */
    NLS_NOM_RES_VASC_SYS(0x00024B28L),
    /**
     * SO2 O2 Saturation
     */
    NLS_NOM_SAT_O2(0x00024B2CL),
    /**
     * SaO2 Oxygen Saturation
     */
    NLS_NOM_SAT_O2_ART(0x00024B34L),
    /**
     * SvO2 Mixed Venous Oxygen Saturation
     */
    NLS_NOM_SAT_O2_VEN(0x00024B3CL),
    /**
     * AaDO2 Alveolar- Arterial Oxygen Difference
     */
    NLS_NOM_SAT_DIFF_O2_ART_ALV(0x00024B40L),
    /**
     * Temp Unspecific Temperature
     */
    NLS_NOM_TEMP(0x00024B48L),
    /**
     * Tart Areterial Temperature
     */
    NLS_NOM_TEMP_ART(0x00024B50L),
    /**
     * Tairwy Airway Temperature
     */
    NLS_NOM_TEMP_AWAY(0x00024B54L),
    /**
     * Tbody Patient Temperature
     */
    NLS_NOM_TEMP_BODY(0x00024B5CL),
    /**
     * Tcore Core (Body) Temperature
     */
    NLS_NOM_TEMP_CORE(0x00024B60L),
    /**
     * Tesoph Esophagial Temperature
     */
    NLS_NOM_TEMP_ESOPH(0x00024B64L),
    /**
     * Tinj Injectate Temperature
     */
    NLS_NOM_TEMP_INJ(0x00024B68L),
    /**
     * Tnaso Naso pharyngial Temperature
     */
    NLS_NOM_TEMP_NASOPH(0x00024B6CL),
    /**
     * Tskin Skin Temperature
     */
    NLS_NOM_TEMP_SKIN(0x00024B74L),
    /**
     * Ttymp Tympanic Temperature
     */
    NLS_NOM_TEMP_TYMP(0x00024B78L),
    /**
     * Tven Venous Temperature
     */
    NLS_NOM_TEMP_VEN(0x00024B7CL),
    /**
     * SV Stroke Volume
     */
    NLS_NOM_VOL_BLD_STROKE(0x00024B84L),
    /**
     * LCW Left Cardiac Work
     */
    NLS_NOM_WK_CARD_LEFT(0x00024B90L),
    /**
     * RCW Right Cardiac Work
     */
    NLS_NOM_WK_CARD_RIGHT(0x00024B94L),
    /**
     * LVSW Left Ventricular Stroke Volume
     */
    NLS_NOM_WK_LV_STROKE(0x00024B9CL),
    /**
     * RVSW Right Ventricular Stroke Volume
     */
    NLS_NOM_WK_RV_STROKE(0x00024BA4L),
    /**
     * Perf Perfusion Indicator
     */
    NLS_NOM_PULS_OXIM_PERF_REL(0x00024BB0L),
    /**
     * Pleth PLETH wave label
     */
    NLS_NOM_PULS_OXIM_PLETH(0x00024BB4L),
    /**
     * SpO2 Arterial Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2(0x00024BB8L),
    /**
     * DeltaSpO2 Difference between two SpO2 Values (like Left - Right)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_DIFF(0x00024BC4L),
    /**
     * SpO2 l Arterial Oxigen Saturation (left)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_ART_LEFT(0x00024BC8L),
    /**
     * SpO2 r Arterial Oxigen Saturation (right)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_ART_RIGHT(0x00024BCCL),
    /**
     * CCO Continuous Cardiac Output
     */
    NLS_NOM_OUTPUT_CARD_CTS(0x00024BDCL),
    /**
     * EDV End Diastolic Volume
     */
    NLS_NOM_VOL_VENT_L_END_DIA(0x00024C00L),
    /**
     * ESV End Systolic Volume
     */
    NLS_NOM_VOL_VENT_L_END_SYS(0x00024C04L),
    /**
     * dPmax Index of Left Ventricular Contractility
     */
    NLS_NOM_GRAD_PRESS_BLD_AORT_POS_MAX(0x00024C25L),
    /**
     * Resp Imedance RESP wave
     */
    NLS_NOM_RESP(0x00025000L),
    /**
     * RR Respiration Rate
     */
    NLS_NOM_RESP_RATE(0x0002500AL),
    /**
     * awRR Airway Respiration Rate
     */
    NLS_NOM_AWAY_RESP_RATE(0x00025012L),
    /**
     * RRaw Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NLS_NOM_VENT_RESP_RATE(0x00025022L),
    /**
     * VC Vital Lung Capacity
     */
    NLS_NOM_CAPAC_VITAL(0x00025080L),
    /**
     * COMP generic label Lung Compliance
     */
    NLS_NOM_COMPL_LUNG(0x00025088L),
    /**
     * Cdyn Dynamic Lung Compliance
     */
    NLS_NOM_COMPL_LUNG_DYN(0x0002508CL),
    /**
     * Cstat Static Lung Compliance
     */
    NLS_NOM_COMPL_LUNG_STATIC(0x00025090L),
    /**
     * CO2 CO2 concentration
     */
    NLS_NOM_AWAY_CO2(0x000250ACL),
    /**
     * tcpCO2 Transcutaneous Carbon Dioxide Partial Pressure
     */
    NLS_NOM_CO2_TCUT(0x000250CCL),
    /**
     * tcpO2 Transcutaneous Oxygen Partial Pressure
     */
    NLS_NOM_O2_TCUT(0x000250D0L),
    /**
     * AWF Airway Flow Wave
     */
    NLS_NOM_FLOW_AWAY(0x000250D4L),
    /**
     * PEF Expiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_EXP_MAX(0x000250D9L),
    /**
     * PIF Inspiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_INSP_MAX(0x000250DDL),
    /**
     * VCO2 CO2 Production
     */
    NLS_NOM_FLOW_CO2_PROD_RESP(0x000250E0L),
    /**
     * T.I. Transthoracic Impedance
     */
    NLS_NOM_IMPED_TTHOR(0x000250E4L),
    /**
     * Pplat Plateau Pressure
     */
    NLS_NOM_PRESS_RESP_PLAT(0x000250E8L),
    /**
     * AWP Airway Pressure Wave
     */
    NLS_NOM_PRESS_AWAY(0x000250F0L),
    /**
     * AWPmin Airway Pressure Minimum
     */
    NLS_NOM_PRESS_AWAY_MIN(0x000250F2L),
    /**
     * CPAP Continuous Positive Airway Pressure
     */
    NLS_NOM_PRESS_AWAY_CTS_POS(0x000250F4L),
    /**
     * NgInsP Negative Inspiratory Pressure
     */
    NLS_NOM_PRESS_AWAY_NEG_MAX(0x000250F9L),
    /**
     * iPEEP Intrinsic PEEP Breathing Pressure
     */
    NLS_NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC(0x00025100L),
    /**
     * AWPin Airway Pressure Wave - measured in the inspiratory path
     */
    NLS_NOM_PRESS_AWAY_INSP(0x00025108L),
    /**
     * PIP Positive Inspiratory ressure
     */
    NLS_NOM_PRESS_AWAY_INSP_MAX(0x00025109L),
    /**
     * MnAwP Mean Airway Pressure. Printer Context
     */
    NLS_NOM_PRESS_AWAY_INSP_MEAN(0x0002510BL),
    /**
     * I:E 1: Inpired:Expired Ratio
     */
    NLS_NOM_RATIO_IE(0x00025118L),
    /**
     * Vd/Vt Ratio of Deadspace to Tidal Volume Vd/Vt
     */
    NLS_NOM_RATIO_AWAY_DEADSP_TIDAL(0x0002511CL),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_RES_AWAY(0x00025120L),
    /**
     * Rexp Expiratory Resistance
     */
    NLS_NOM_RES_AWAY_EXP(0x00025124L),
    /**
     * Rinsp Inspiratory Resistance
     */
    NLS_NOM_RES_AWAY_INSP(0x00025128L),
    /**
     * ApneaD Apnea Time
     */
    NLS_NOM_TIME_PD_APNEA(0x00025130L),
    /**
     * TV Tidal Volume
     */
    NLS_NOM_VOL_AWAY_TIDAL(0x0002513CL),
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NLS_NOM_VOL_MINUTE_AWAY(0x00025148L),
    /**
     * FICO2 Airway CO2 inspiration
     */
    NLS_NOM_VENT_CONC_AWAY_CO2_INSP(0x00025160L),
    /**
     * O2 Generic oxigen measurement label
     */
    NLS_NOM_CONC_AWAY_O2(0x00025164L),
    /**
     * DeltaO2 relative Dead Space
     */
    NLS_NOM_VENT_CONC_AWAY_O2_DELTA(0x00025168L),
    /**
     * PECO2 Partial O2 Venous
     */
    NLS_NOM_VENT_AWAY_CO2_EXP(0x0002517CL),
    /**
     * AWFin Airway Flow Wave - measured in the inspiratory path
     */
    NLS_NOM_VENT_FLOW_INSP(0x0002518CL),
    /**
     * VQI Ventilation Perfusion Index
     */
    NLS_NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX(0x00025190L),
    /**
     * Poccl Occlusion Pressure
     */
    NLS_NOM_VENT_PRESS_OCCL(0x0002519CL),
    /**
     * PEEP Positive End-Expiratory Pressure PEEP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS(0x000251A8L),
    /**
     * Vd Dead Space Volume Vd
     */
    NLS_NOM_VENT_VOL_AWAY_DEADSP(0x000251B0L),
    /**
     * relVd relative Dead Space
     */
    NLS_NOM_VENT_VOL_AWAY_DEADSP_REL(0x000251B4L),
    /**
     * TrpVol Lung Volume Trapped
     */
    NLS_NOM_VENT_VOL_LUNG_TRAPD(0x000251B8L),
    /**
     * MMV Mandatory Minute Volume
     */
    NLS_NOM_VENT_VOL_MINUTE_AWAY_MAND(0x000251CCL),
    /**
     * DCO2 High Frequency Gas Transport Coefficient value
     */
    NLS_NOM_COEF_GAS_TRAN(0x000251D4L),
    /**
     * DES generic Desflurane label
     */
    NLS_NOM_CONC_AWAY_DESFL(0x000251D8L),
    /**
     * ENF generic Enflurane label
     */
    NLS_NOM_CONC_AWAY_ENFL(0x000251DCL),
    /**
     * HAL generic Halothane label
     */
    NLS_NOM_CONC_AWAY_HALOTH(0x000251E0L),
    /**
     * SEV generic Sevoflurane label
     */
    NLS_NOM_CONC_AWAY_SEVOFL(0x000251E4L),
    /**
     * ISO generic Isoflurane label
     */
    NLS_NOM_CONC_AWAY_ISOFL(0x000251E8L),
    /**
     * N2O generic Nitrous Oxide label
     */
    NLS_NOM_CONC_AWAY_N2O(0x000251F0L),
    /**
     * DPosP Duration Above Base Pressure
     */
    NLS_NOM_VENT_TIME_PD_PPV(0x00025360L),
    /**
     * PEinsp Respiration Pressure Plateau
     */
    NLS_NOM_VENT_PRESS_RESP_PLAT(0x00025368L),
    /**
     * Leak Leakage
     */
    NLS_NOM_VENT_VOL_LEAK(0x00025370L),
    /**
     * ALVENT Alveolar Ventilation ALVENT
     */
    NLS_NOM_VENT_VOL_LUNG_ALV(0x00025374L),
    /**
     * N2 generic N2 label
     */
    NLS_NOM_CONC_AWAY_N2(0x0002537CL),
    /**
     * AGT generic Agent label
     */
    NLS_NOM_CONC_AWAY_AGENT(0x00025388L),
    /**
     * inAGT Generic Inspired Agent Concentration
     */
    NLS_NOM_CONC_AWAY_AGENT_INSP(0x00025390L),
    /**
     * CPP Cerebral Perfusion Pressure
     */
    NLS_NOM_PRESS_CEREB_PERF(0x00025804L),
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NLS_NOM_PRESS_INTRA_CRAN(0x00025808L),
    /**
     * GCS Glasgow Coma Score
     */
    NLS_NOM_SCORE_GLAS_COMA(0x00025880L),
    /**
     * EyeRsp SubScore of the GCS: Eye Response
     */
    NLS_NOM_SCORE_EYE_SUBSC_GLAS_COMA(0x00025882L),
    /**
     * MotRsp SubScore of the GCS: Motoric Response
     */
    NLS_NOM_SCORE_MOTOR_SUBSC_GLAS_COMA(0x00025883L),
    /**
     * VblRsp SubScore of the GCS: Verbal Response
     */
    NLS_NOM_SCORE_SUBSC_VERBAL_GLAS_COMA(0x00025884L),
    /**
     * HC Head Circumferince
     */
    NLS_NOM_CIRCUM_HEAD(0x00025900L),
    /**
     * PRL Pupil Reaction Left eye - light reaction of left eye's pupil
     */
    NLS_NOM_TIME_PD_PUPIL_REACT_LEFT(0x00025924L),
    /**
     * PRR Pupil Reaction Righteye - light reaction of right eye's pupil
     */
    NLS_NOM_TIME_PD_PUPIL_REACT_RIGHT(0x00025928L),
    /**
     * EEG generic EEG and BIS label
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX(0x0002592CL),
    /**
     * EMG Electromyography
     */
    NLS_NOM_EMG_ELEC_POTL_MUSCL(0x0002593CL),
    /**
     * MDF Mean Dominant Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN(0x0002597CL),
    /**
     * PPF Peak Power Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK(0x00025984L),
    /**
     * SEF Spectral Edge Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE(0x00025988L),
    /**
     * TP Total Power
     */
    NLS_NOM_EEG_PWR_SPEC_TOT(0x000259B8L),
    /**
     * UrFl Urimeter - Urine Flow.
     */
    NLS_NOM_FLOW_URINE_INSTANT(0x0002680CL),
    /**
     * UrVol Urine Volume
     */
    NLS_NOM_VOL_URINE_BAL_PD(0x00026824L),
    /**
     * BagVol Current fluid (Urine) in the Urine Bag
     */
    NLS_NOM_VOL_URINE_COL(0x00026830L),
    /**
     * AccVol Infusion Pump Accumulated volume. Measured value
     */
    NLS_NOM_VOL_INFUS_ACTUAL_TOTAL(0x000268FCL),
    /**
     * pHa pH in arterial Blood
     */
    NLS_NOM_CONC_PH_ART(0x00027004L),
    /**
     * PaCO2 Partial Pressure of arterial Carbon Dioxide
     */
    NLS_NOM_CONC_PCO2_ART(0x00027008L),
    /**
     * PaO2 Partial O2 arterial
     */
    NLS_NOM_CONC_PO2_ART(0x0002700CL),
    /**
     * Hb Hemoglobin in arterial Blood
     */
    NLS_NOM_CONC_HB_ART(0x00027014L),
    /**
     * CaO2 Arterial Oxygen Content CaO2
     */
    NLS_NOM_CONC_HB_O2_ART(0x00027018L),
    /**
     * pHv pH in venous Blood
     */
    NLS_NOM_CONC_PH_VEN(0x00027034L),
    /**
     * PvCO2 Partial CO2 in the venous blood
     */
    NLS_NOM_CONC_PCO2_VEN(0x00027038L),
    /**
     * PvO2 Partial O2 Venous
     */
    NLS_NOM_CONC_PO2_VEN(0x0002703CL),
    /**
     * CvO2 Venous Oxygen Content
     */
    NLS_NOM_CONC_HB_O2_VEN(0x00027048L),
    /**
     * UrpH pH value in the Urine
     */
    NLS_NOM_CONC_PH_URINE(0x00027064L),
    /**
     * UrNa Natrium in Urine
     */
    NLS_NOM_CONC_NA_URINE(0x0002706CL),
    /**
     * SerNa Natrium in Serum
     */
    NLS_NOM_CONC_NA_SERUM(0x000270D8L),
    /**
     * pH pH in the Blood Plasma
     */
    NLS_NOM_CONC_PH_GEN(0x00027104L),
    /**
     * HCO3 Hydrocarbon concentration in Blood Plasma
     */
    NLS_NOM_CONC_HCO3_GEN(0x00027108L),
    /**
     * Na Natrium (Sodium)
     */
    NLS_NOM_CONC_NA_GEN(0x0002710CL),
    /**
     * K Kalium (Potassium)
     */
    NLS_NOM_CONC_K_GEN(0x00027110L),
    /**
     * Glu Glucose
     */
    NLS_NOM_CONC_GLU_GEN(0x00027114L),
    /**
     * iCa ionized Calcium
     */
    NLS_NOM_CONC_CA_GEN(0x00027118L),
    /**
     * PCO2 Partial CO2
     */
    NLS_NOM_CONC_PCO2_GEN(0x00027140L),
    /**
     * Cl Chloride
     */
    NLS_NOM_CONC_CHLORIDE_GEN(0x00027168L),
    /**
     * BE Base Excess of Blood
     */
    NLS_NOM_BASE_EXCESS_BLD_ART(0x0002716CL),
    /**
     * PO2 Partial O2.
     */
    NLS_NOM_CONC_PO2_GEN(0x00027174L),
    /**
     * Met-Hb MetHemoglobin
     */
    NLS_NOM_CONC_HB_MET_GEN(0x0002717CL),
    /**
     * CO-Hb Carboxy Hemoglobin
     */
    NLS_NOM_CONC_HB_CO_GEN(0x00027180L),
    /**
     * Hct Haematocrit
     */
    NLS_NOM_CONC_HCT_GEN(0x00027184L),
    /**
     * FIO2 Fractional Inspired Oxygen FIO2
     */
    NLS_NOM_VENT_CONC_AWAY_O2_INSP(0x00027498L),
    /**
     * EctSta ECG Ectopic Status label
     */
    NLS_NOM_ECG_STAT_ECT(0x0002D006L),
    /**
     * RytSta ECG Rhythm Status label
     */
    NLS_NOM_ECG_STAT_RHY(0x0002D007L),
    /**
     * IMV Intermittent Mandatory Ventilation
     */
    NLS_NOM_VENT_MODE_MAND_INTERMIT(0x0002D02AL),
    /**
     * Trect Rectal Temperature
     */
    NLS_NOM_TEMP_RECT(0x0002E004L),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_TEMP_BLD(0x0002E014L),
    /**
     * DeltaTemp Difference Temperature
     */
    NLS_NOM_TEMP_DIFF(0x0002E018L),
    /**
     * STindx ST Index
     */
    NLS_NOM_ECG_AMPL_ST_INDEX(0x0002F03DL),
    /**
     * SitTim NOM_DIM_MIN
     */
    NLS_NOM_TIME_TCUT_SENSOR(0x0002F03EL),
    /**
     * SensrT Sensor Temperature
     */
    NLS_NOM_TEMP_TCUT_SENSOR(0x0002F03FL),
    /**
     * ITBV Intrathoracic Blood Volume
     */
    NLS_NOM_VOL_BLD_INTRA_THOR(0x0002F040L),
    /**
     * ITBVI Intrathoracic Blood Volume Index
     */
    NLS_NOM_VOL_BLD_INTRA_THOR_INDEX(0x0002F041L),
    /**
     * EVLW Extravascular Lung Water
     */
    NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC(0x0002F042L),
    /**
     * EVLWI Extravascular Lung Water Index
     */
    NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX(0x0002F043L),
    /**
     * GEDV Global End Diastolic Volume
     */
    NLS_NOM_VOL_GLOBAL_END_DIA(0x0002F044L),
    /**
     * GEDVI Global End Diastolic Volume Index
     */
    NLS_NOM_VOL_GLOBAL_END_DIA_INDEX(0x0002F045L),
    /**
     * CFI Cardiac Function Index
     */
    NLS_NOM_CARD_FUNC_INDEX(0x0002F046L),
    /**
     * CCI Continuous Cardiac Output Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX_CTS(0x0002F047L),
    /**
     * SI Stroke Index
     */
    NLS_NOM_VOL_BLD_STROKE_INDEX(0x0002F048L),
    /**
     * SVV Stroke Volume Variation
     */
    NLS_NOM_VOL_BLD_STROKE_VAR(0x0002F049L),
    /**
     * SR Suppression Ratio
     */
    NLS_NOM_EEG_RATIO_SUPPRN(0x0002F04AL),
    /**
     * SQI Signal Quality Index
     */
    NLS_NOM_EEG_BIS_SIG_QUAL_INDEX(0x0002F04DL),
    /**
     * BIS Bispectral Index
     */
    NLS_NOM_EEG_BISPECTRAL_INDEX(0x0002F04EL),
    /**
     * tcGas Generic Term for the Transcutaneous Gases
     */
    NLS_NOM_GAS_TCUT(0x0002F051L),
    /**
     * MAC Airway MAC Concentration
     */
    NLS_NOM_CONC_AWAY_SUM_MAC(0x0002F05DL),
    /**
     * PVRI Pulmonary vascular Resistance PVRI
     */
    NLS_NOM_RES_VASC_PULM_INDEX(0x0002F067L),
    /**
     * LCWI Left Cardiac Work Index
     */
    NLS_NOM_WK_CARD_LEFT_INDEX(0x0002F068L),
    /**
     * RCWI Right Cardiac Work Index
     */
    NLS_NOM_WK_CARD_RIGHT_INDEX(0x0002F069L),
    /**
     * VO2I Oxygen Consumption Index VO2I
     */
    NLS_NOM_SAT_O2_CONSUMP_INDEX(0x0002F06AL),
    /**
     * PB Barometric Pressure = Ambient Pressure
     */
    NLS_NOM_PRESS_AIR_AMBIENT(0x0002F06BL),
    /**
     * Sp-vO2 Difference between Spo2 and SvO2
     */
    NLS_NOM_SAT_DIFF_O2_ART_VEN(0x0002F06CL),
    /**
     * DO2 Oxygen Availability DO2
     */
    NLS_NOM_SAT_O2_DELIVER(0x0002F06DL),
    /**
     * DO2I Oxygen Availability Index
     */
    NLS_NOM_SAT_O2_DELIVER_INDEX(0x0002F06EL),
    /**
     * O2ER Oxygen Extraction Ratio
     */
    NLS_NOM_RATIO_SAT_O2_CONSUMP_DELIVER(0x0002F06FL),
    /**
     * Qs/Qt Percent Alveolarvenous Shunt Qs/Qt
     */
    NLS_NOM_RATIO_ART_VEN_SHUNT(0x0002F070L),
    /**
     * BSA Body Surface Area
     */
    NLS_NOM_AREA_BODY_SURFACE(0x0002F071L),
    /**
     * LI Light Intenisty. SvO2
     */
    NLS_NOM_INTENS_LIGHT(0x0002F072L),
    /**
     * HeatPw NOM_DIM_MILLI_WATT
     */
    NLS_NOM_HEATING_PWR_TCUT_SENSOR(0x0002F076L),
    /**
     * InjVol Injectate Volume (Cardiac Output)
     */
    NLS_NOM_VOL_INJ(0x0002F079L),
    /**
     * ETVI ExtraVascular Thermo Volume Index. Cardiac Output.
     */
    NLS_NOM_VOL_THERMO_EXTRA_VASC_INDEX(0x0002F07AL),
    /**
     * CompCt Generic Numeric Calculation Constant
     */
    NLS_NOM_NUM_CALC_CONST(0x0002F07BL),
    /**
     * CathCt Generic Numeric Calculation Constant
     */
    NLS_NOM_NUM_CATHETER_CONST(0x0002F07CL),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_PULS_OXIM_PERF_REL_LEFT(0x0002F08AL),
    /**
     * Perf r Relative Perfusion Right label
     */
    NLS_NOM_PULS_OXIM_PERF_REL_RIGHT(0x0002F08BL),
    /**
     * PLETHr PLETH wave (right)
     */
    NLS_NOM_PULS_OXIM_PLETH_RIGHT(0x0002F08CL),
    /**
     * PLETHl PLETH wave (left)
     */
    NLS_NOM_PULS_OXIM_PLETH_LEFT(0x0002F08DL),
    /**
     * BUN Blood Urea Nitrogen
     */
    NLS_NOM_CONC_BLD_UREA_NITROGEN(0x0002F08FL),
    /**
     * BEecf Base Excess of Extra-Cellular Fluid
     */
    NLS_NOM_CONC_BASE_EXCESS_ECF(0x0002F090L),
    /**
     * SpMV Spontaneous Minute Volume
     */
    NLS_NOM_VENT_VOL_MINUTE_AWAY_SPONT(0x0002F091L),
    /**
     * Ca-vO2 Arteriovenous Oxygen Difference Ca-vO2
     */
    NLS_NOM_CONC_DIFF_HB_O2_ATR_VEN(0x0002F092L),
    /**
     * Weight Patient Weight
     */
    NLS_NOM_PAT_WEIGHT(0x0002F093L),
    /**
     * Height Patient Height
     */
    NLS_NOM_PAT_HEIGHT(0x0002F094L),
    /**
     * MAC Minimum Alveolar Concentration
     */
    NLS_NOM_CONC_AWAY_MAC(0x0002F099L),
    /**
     * PlethT Pleth wave from Telemetry
     */
    NLS_NOM_PULS_OXIM_PLETH_TELE(0x0002F09BL),
    /**
     * %SpO2T SpO2 parameter label as sourced by the Telemetry system
     */
    NLS_NOM_PULS_OXIM_SAT_O2_TELE(0x0002F09CL),
    /**
     * PulseT Pulse parameter label as sourced by the Telemetry system
     */
    NLS_NOM_PULS_OXIM_PULS_RATE_TELE(0x0002F09DL),
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NLS_NOM_PRESS_GEN_1(0x0002F0A4L),
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NLS_NOM_PRESS_GEN_2(0x0002F0A8L),
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NLS_NOM_PRESS_GEN_3(0x0002F0ACL),
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NLS_NOM_PRESS_GEN_4(0x0002F0B0L),
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NLS_NOM_PRESS_INTRA_CRAN_1(0x0002F0B4L),
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NLS_NOM_PRESS_INTRA_CRAN_2(0x0002F0B8L),
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NLS_NOM_PRESS_BLD_ART_FEMORAL(0x0002F0BCL),
    /**
     * BAP Brachial Arterial Pressure (BAP)
     */
    NLS_NOM_PRESS_BLD_ART_BRACHIAL(0x0002F0C0L),
    /**
     * Tvesic Temperature of the Urine fluid
     */
    NLS_NOM_TEMP_VESICAL(0x0002F0C4L),
    /**
     * Tcereb Cerebral Temperature
     */
    NLS_NOM_TEMP_CEREBRAL(0x0002F0C5L),
    /**
     * Tamb Ambient Temperature
     */
    NLS_NOM_TEMP_AMBIENT(0x0002F0C6L),
    /**
     * T1 Generic Temperature 1 (T1)
     */
    NLS_NOM_TEMP_GEN_1(0x0002F0C7L),
    /**
     * T2 Generic Temperature 2 (T2)
     */
    NLS_NOM_TEMP_GEN_2(0x0002F0C8L),
    /**
     * T3 Generic Temperature 3 (T3)
     */
    NLS_NOM_TEMP_GEN_3(0x0002F0C9L),
    /**
     * T4 Generic Temperature 4 (T4)
     */
    NLS_NOM_TEMP_GEN_4(0x0002F0CAL),
    /**
     * IUP Intra-Uterine Pressure
     */
    NLS_NOM_PRESS_INTRA_UTERAL(0x0002F0D8L),
    /**
     * TVin inspired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_INSP_TIDAL(0x0002F0E0L),
    /**
     * TVexp expired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_EXP_TIDAL(0x0002F0E1L),
    /**
     * RRspir Respiration Rate from Spirometry
     */
    NLS_NOM_AWAY_RESP_RATE_SPIRO(0x0002F0E2L),
    /**
     * PPV Pulse Pressure Variation
     */
    NLS_NOM_PULS_PRESS_VAR(0x0002F0E3L),
    /**
     * Pulse Pulse from NBP
     */
    NLS_NOM_PRESS_BLD_NONINV_PULS_RATE(0x0002F0E5L),
    /**
     * MRR Mandatory Respiratory Rate
     */
    NLS_NOM_VENT_RESP_RATE_MAND(0x0002F0F1L),
    /**
     * MTV Mandatory Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_MAND(0x0002F0F2L),
    /**
     * SpTV Spontaneuous Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_SPONT(0x0002F0F3L),
    /**
     * cTnI Cardiac Troponin I
     */
    NLS_NOM_CARDIAC_TROPONIN_I(0x0002F0F4L),
    /**
     * CPB Cardio Pulmonary Bypass Flag
     */
    NLS_NOM_CARDIO_PULMONARY_BYPASS_MODE(0x0002F0F5L),
    /**
     * BNP Cardiac Brain Natriuretic Peptide
     */
    NLS_NOM_BNP(0x0002F0F6L),
    /**
     * PlatTi Plateau Time
     */
    NLS_NOM_TIME_PD_RESP_PLAT(0x0002F0FFL),
    /**
     * ScvO2 Central Venous Oxygen Saturation
     */
    NLS_NOM_SAT_O2_VEN_CENT(0x0002F100L),
    /**
     * SNR Signal to Noise ratio
     */
    NLS_NOM_SNR(0x0002F101L),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_HUMID(0x0002F103L),
    /**
     * GEF Global Ejection Fraction
     */
    NLS_NOM_FRACT_EJECT(0x0002F105L),
    /**
     * PVPI Pulmonary Vascular Permeability Index
     */
    NLS_NOM_PERM_VASC_PULM_INDEX(0x0002F106L),
    /**
     * pToral Predictive Oral Temperature
     */
    NLS_NOM_TEMP_ORAL_PRED(0x0002F110L),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_TEMP_RECT_PRED(0x0002F114L),
    /**
     * pTaxil Predictive Axillary Temperature
     */
    NLS_NOM_TEMP_AXIL_PRED(0x0002F118L),
    /**
     * Air T Air Temperature in the Incubator
     */
    NLS_NOM_TEMP_AIR_INCUB(0x0002F12AL),
    /**
     * Perf T Perf from Telemetry
     */
    NLS_NOM_PULS_OXIM_PERF_REL_TELE(0x0002F12CL),
    /**
     * RLShnt Right-to-Left Heart Shunt
     */
    NLS_NOM_SHUNT_RIGHT_LEFT(0x0002F14AL),
    /**
     * QT-HR QT HEARTRATE
     */
    NLS_NOM_ECG_TIME_PD_QT_HEART_RATE(0x0002F154L),
    /**
     * QT Bsl
     */
    NLS_NOM_ECG_TIME_PD_QT_BASELINE(0x0002F155L),
    /**
     * DeltaQTc
     */
    NLS_NOM_ECG_TIME_PD_QTc_DELTA(0x0002F156L),
    /**
     * QTHRBl QT BASELINE HEARTRATE
     */
    NLS_NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE(0x0002F157L),
    /**
     * pHc pH value in the capillaries
     */
    NLS_NOM_CONC_PH_CAP(0x0002F158L),
    /**
     * PcCO2 Partial CO2 in the capillaries
     */
    NLS_NOM_CONC_PCO2_CAP(0x0002F159L),
    /**
     * PcO2 Partial O2 in the capillaries
     */
    NLS_NOM_CONC_PO2_CAP(0x0002F15AL),
    /**
     * iMg ionized Magnesium
     */
    NLS_NOM_CONC_MG_ION(0x0002F15BL),
    /**
     * SerMg Magnesium in Serum
     */
    NLS_NOM_CONC_MG_SER(0x0002F15CL),
    /**
     * tSerCa total of Calcium in Serum
     */
    NLS_NOM_CONC_tCA_SER(0x0002F15DL),
    /**
     * SerPho Phosphat in Serum
     */
    NLS_NOM_CONC_P_SER(0x0002F15EL),
    /**
     * SerCl Clorid in Serum
     */
    NLS_NOM_CONC_CHLOR_SER(0x0002F15FL),
    /**
     * Fe Ferrum
     */
    NLS_NOM_CONC_FE_GEN(0x0002F160L),
    /**
     * SerAlb Albumine in Serum
     */
    NLS_NOM_CONC_ALB_SER(0x0002F163L),
    /**
     * 'SaO2 Calculated SaO2
     */
    NLS_NOM_SAT_O2_ART_CALC(0x0002F164L),
    /**
     * HbF Fetal Hemoglobin
     */
    NLS_NOM_CONC_HB_FETAL(0x0002F165L),
    /**
     * 'SvO2 Calculated SvO2
     */
    NLS_NOM_SAT_O2_VEN_CALC(0x0002F166L),
    /**
     * Plts Platelets (thrombocyte count)
     */
    NLS_NOM_PLTS_CNT(0x0002F167L),
    /**
     * WBC White Blood Count (leucocyte count)
     */
    NLS_NOM_WB_CNT(0x0002F168L),
    /**
     * RBC Red Blood Count (erithrocyte count)
     */
    NLS_NOM_RB_CNT(0x0002F169L),
    /**
     * RC Reticulocyte Count
     */
    NLS_NOM_RET_CNT(0x0002F16AL),
    /**
     * PlOsm Plasma Osmolarity
     */
    NLS_NOM_PLASMA_OSM(0x0002F16BL),
    /**
     * CreaCl Creatinine Clearance
     */
    NLS_NOM_CONC_CREA_CLR(0x0002F16CL),
    /**
     * NsLoss Nitrogen Balance
     */
    NLS_NOM_NSLOSS(0x0002F16DL),
    /**
     * Chol Cholesterin
     */
    NLS_NOM_CONC_CHOLESTEROL(0x0002F16EL),
    /**
     * TGL Triglyzeride
     */
    NLS_NOM_CONC_TGL(0x0002F16FL),
    /**
     * HDL High Density Lipoprotein
     */
    NLS_NOM_CONC_HDL(0x0002F170L),
    /**
     * LDL Low Density Lipoprotein
     */
    NLS_NOM_CONC_LDL(0x0002F171L),
    /**
     * Urea Urea used by the i-Stat
     */
    NLS_NOM_CONC_UREA_GEN(0x0002F172L),
    /**
     * Crea Creatinine - Measured Value by the i-Stat Module
     */
    NLS_NOM_CONC_CREA(0x0002F173L),
    /**
     * Lact Lactate. SMeasured value by the i-Stat module
     */
    NLS_NOM_CONC_LACT(0x0002F174L),
    /**
     * tBili total Bilirubin
     */
    NLS_NOM_CONC_BILI_TOT(0x0002F177L),
    /**
     * SerPro (Total) Protein in Serum
     */
    NLS_NOM_CONC_PROT_SER(0x0002F178L),
    /**
     * tPro Total Protein
     */
    NLS_NOM_CONC_PROT_TOT(0x0002F179L),
    /**
     * dBili direct Bilirubin
     */
    NLS_NOM_CONC_BILI_DIRECT(0x0002F17AL),
    /**
     * LDH Lactate Dehydrogenase
     */
    NLS_NOM_CONC_LDH(0x0002F17BL),
    /**
     * ESR Erithrocyte sedimentation rate
     */
    NLS_NOM_ES_RATE(0x0002F17CL),
    /**
     * PCT Procalcitonin
     */
    NLS_NOM_CONC_PCT(0x0002F17DL),
    /**
     * CK-MM Creatine Cinase of type muscle
     */
    NLS_NOM_CONC_CREA_KIN_MM(0x0002F17FL),
    /**
     * SerCK Creatinin Kinase
     */
    NLS_NOM_CONC_CREA_KIN_SER(0x0002F180L),
    /**
     * CK-MB Creatine Cinase of type muscle-brain
     */
    NLS_NOM_CONC_CREA_KIN_MB(0x0002F181L),
    /**
     * CHE Cholesterinesterase
     */
    NLS_NOM_CONC_CHE(0x0002F182L),
    /**
     * CRP C-reactive Protein
     */
    NLS_NOM_CONC_CRP(0x0002F183L),
    /**
     * AST Aspartin - Aminotransferase
     */
    NLS_NOM_CONC_AST(0x0002F184L),
    /**
     * AP Alkalische Phosphatase
     */
    NLS_NOM_CONC_AP(0x0002F185L),
    /**
     * alphaA Alpha Amylase
     */
    NLS_NOM_CONC_ALPHA_AMYLASE(0x0002F186L),
    /**
     * GPT Glutamic-Pyruvic-Transaminase
     */
    NLS_NOM_CONC_GPT(0x0002F187L),
    /**
     * GOT Glutamic Oxaloacetic Transaminase
     */
    NLS_NOM_CONC_GOT(0x0002F188L),
    /**
     * GGT Gamma GT = Gamma Glutamyltranspeptidase
     */
    NLS_NOM_CONC_GGT(0x0002F189L),
    /**
     * ACT Activated Clotting Time. Measured value by the i-Stat module
     */
    NLS_NOM_TIME_PD_ACT(0x0002F18AL),
    /**
     * PT Prothrombin Time
     */
    NLS_NOM_TIME_PD_PT(0x0002F18BL),
    /**
     * PT INR Prothrombin Time - International Normalized Ratio
     */
    NLS_NOM_PT_INTL_NORM_RATIO(0x0002F18CL),
    /**
     * aPTTWB aPTT Whole Blood
     */
    NLS_NOM_TIME_PD_aPTT_WB(0x0002F18DL),
    /**
     * aPTTPE aPTT Plasma Equivalent Time
     */
    NLS_NOM_TIME_PD_aPTT_PE(0x0002F18EL),
    /**
     * PT WB Prothrombin Time (Blood)
     */
    NLS_NOM_TIME_PD_PT_WB(0x0002F18FL),
    /**
     * PT PE Prothrombin Time (Plasma)
     */
    NLS_NOM_TIME_PD_PT_PE(0x0002F190L),
    /**
     * TT Thrombin Time
     */
    NLS_NOM_TIME_PD_THROMBIN(0x0002F191L),
    /**
     * CT Coagulation Time
     */
    NLS_NOM_TIME_PD_COAGULATION(0x0002F192L),
    /**
     * Quick Thromboplastine Time
     */
    NLS_NOM_TIME_PD_THROMBOPLAS(0x0002F193L),
    /**
     * FeNa Fractional Excretion of Sodium
     */
    NLS_NOM_FRACT_EXCR_NA(0x0002F194L),
    /**
     * UrUrea Urine Urea
     */
    NLS_NOM_CONC_UREA_URINE(0x0002F195L),
    /**
     * UrCrea Urine Creatinine
     */
    NLS_NOM_CONC_CREA_URINE(0x0002F196L),
    /**
     * UrK Urine Potassium
     */
    NLS_NOM_CONC_K_URINE(0x0002F197L),
    /**
     * UrKEx Urinary Potassium Excretion
     */
    NLS_NOM_CONC_K_URINE_EXCR(0x0002F198L),
    /**
     * UrOsm Urine Osmolarity
     */
    NLS_NOM_CONC_OSM_URINE(0x0002F199L),
    /**
     * UrCl Clorid in Urine
     */
    NLS_NOM_CONC_CHLOR_URINE(0x0002F19AL),
    /**
     * UrPro (Total) Protein in Urine
     */
    NLS_NOM_CONC_PRO_URINE(0x0002F19BL),
    /**
     * UrCa Calzium in Urine
     */
    NLS_NOM_CONC_CA_URINE(0x0002F19CL),
    /**
     * UrDens Density of the Urine fluid
     */
    NLS_NOM_FLUID_DENS_URINE(0x0002F19DL),
    /**
     * UrHb Hemoglobin (Urine)
     */
    NLS_NOM_CONC_HB_URINE(0x0002F19EL),
    /**
     * UrGlu Glucose in Urine
     */
    NLS_NOM_CONC_GLU_URINE(0x0002F19FL),
    /**
     * 'ScO2 Calculated ScO2
     */
    NLS_NOM_SAT_O2_CAP_CALC(0x0002F1A0L),
    /**
     * 'AnGap Calculated AnionGap
     */
    NLS_NOM_CONC_AN_GAP_CALC(0x0002F1A1L),
    /**
     * SpO2pr Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL(0x0002F1C0L),
    /**
     * SpO2po Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2_POST_DUCTAL(0x0002F1D4L),
    /**
     * PerfPo Relative Perfusion Left
     */
    NLS_NOM_PULS_OXIM_PERF_REL_POST_DUCTAL(0x0002F1DCL),
    /**
     * PerfPr Relative Perfusion Left
     */
    NLS_NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL(0x0002F22CL),
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NLS_NOM_PRESS_GEN_5(0x0002F3F4L),
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NLS_NOM_PRESS_GEN_6(0x0002F3F8L),
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NLS_NOM_PRESS_GEN_7(0x0002F3FCL),
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NLS_NOM_PRESS_GEN_8(0x0002F400L),
    /**
     * Rf-I ST Reference Value for Lead I
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_I(0x0002F411L),
    /**
     * Rf-II ST Reference Value for Lead II
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_II(0x0002F412L),
    /**
     * Rf-V1 ST Reference Value for Lead V1
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V1(0x0002F413L),
    /**
     * Rf-V2 ST Reference Value for Lead V2
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V2(0x0002F414L),
    /**
     * Rf-V3 ST Reference Value for Lead V3
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V3(0x0002F415L),
    /**
     * Rf-V4 ST Reference Value for Lead V4
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V4(0x0002F416L),
    /**
     * Rf-V5 ST Reference Value for Lead V5
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V5(0x0002F417L),
    /**
     * Rf-V6 ST Reference Value for Lead V6
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V6(0x0002F418L),
    /**
     * Rf-III ST Reference Value for Lead III
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_III(0x0002F44DL),
    /**
     * Rf-aVR ST Reference Value for Lead aVR
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVR(0x0002F44EL),
    /**
     * Rf-aVL ST Reference Value for Lead aVL
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVL(0x0002F44FL),
    /**
     * Rf-aVF ST Reference Value for Lead aVF
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVF(0x0002F450L),
    /**
     * Age actual patient age. measured in years
     */
    NLS_NOM_AGE(0x0002F810L),
    /**
     * G.Age Gestational age for neonatal
     */
    NLS_NOM_AGE_GEST(0x0002F811L),
    /**
     * BSA(B) BSA formula: Boyd
     */
    NLS_NOM_AREA_BODY_SURFACE_ACTUAL_BOYD(0x0002F812L),
    /**
     * BSA(D) BSA formula: Dubois
     */
    NLS_NOM_AREA_BODY_SURFACE_ACTUAL_DUBOIS(0x0002F813L),
    /**
     * r Correlation Coefficient
     */
    NLS_NOM_AWAY_CORR_COEF(0x0002F814L),
    /**
     * SpAWRR Spontaneous Airway Respiration Rate
     */
    NLS_NOM_AWAY_RESP_RATE_SPONT(0x0002F815L),
    /**
     * TC Time Constant
     */
    NLS_NOM_AWAY_TC(0x0002F816L),
    /**
     * 'BE,B Calculated Base Excess in Blood
     */
    NLS_NOM_BASE_EXCESS_BLD_ART_CALC(0x0002F817L),
    /**
     * Length Length for neonatal/pediatric
     */
    NLS_NOM_BIRTH_LENGTH(0x0002F818L),
    /**
     * RSBI Rapid Shallow Breathing Index
     */
    NLS_NOM_BREATH_RAPID_SHALLOW_INDEX(0x0002F819L),
    /**
     * C20/C Overdistension Index
     */
    NLS_NOM_C20_PER_C_INDEX(0x0002F81AL),
    /**
     * extHR denotes a Heart Rate received from an external device
     */
    NLS_NOM_CARD_BEAT_RATE_EXT(0x0002F81BL),
    /**
     * HI Heart Contractility Index
     */
    NLS_NOM_CARD_CONTRACT_HEATHER_INDEX(0x0002F81CL),
    /**
     * ALP Alveolarproteinose Rosen-Castleman-Liebow- Syndrom
     */
    NLS_NOM_CONC_ALP(0x0002F81DL),
    /**
     * etAGTs EndTidal secondary Anesthetic Agent
     */
    NLS_NOM_CONC_AWAY_AGENT_ET_SEC(0x0002F81EL),
    /**
     * inAGTs Inspired secondary Anesthetic Agent
     */
    NLS_NOM_CONC_AWAY_AGENT_INSP_SEC(0x0002F81FL),
    /**
     * 'BEecf Calculated Base Excess
     */
    NLS_NOM_CONC_BASE_EXCESS_ECF_CALC(0x0002F821L),
    /**
     * iCa(N) ionized Calcium Normalized
     */
    NLS_NOM_CONC_CA_GEN_NORM(0x0002F822L),
    /**
     * SerCa Calcium in Serum
     */
    NLS_NOM_CONC_CA_SER(0x0002F824L),
    /**
     * tCO2 total of CO2 - result of Blood gas Analysis
     */
    NLS_NOM_CONC_CO2_TOT(0x0002F825L),
    /**
     * 'tCO2 Calculated total CO2
     */
    NLS_NOM_CONC_CO2_TOT_CALC(0x0002F826L),
    /**
     * SCrea Serum Creatinine
     */
    NLS_NOM_CONC_CREA_SER(0x0002F827L),
    /**
     * SpRR Spontaneous Respiration Rate
     */
    NLS_NOM_RESP_RATE_SPONT(0x0002F828L),
    /**
     * SerGlo Globulin in Serum
     */
    NLS_NOM_CONC_GLO_SER(0x0002F829L),
    /**
     * SerGlu Glucose in Serum
     */
    NLS_NOM_CONC_GLU_SER(0x0002F82AL),
    /**
     * 'Hb Calculated Hemoglobin
     */
    NLS_NOM_CONC_HB_ART_CALC(0x0002F82BL),
    /**
     * MCHC Mean Corpuscular Hemoglobin Concentration
     */
    NLS_NOM_CONC_HB_CORP_MEAN(0x0002F82CL),
    /**
     * 'HCO3 Calculated HCO3
     */
    NLS_NOM_CONC_HCO3_GEN_CALC(0x0002F82EL),
    /**
     * SerK Kalium (Potassium) in Serum
     */
    NLS_NOM_CONC_K_SER(0x0002F82FL),
    /**
     * UrNaEx Urine Sodium Excretion
     */
    NLS_NOM_CONC_NA_EXCR(0x0002F830L),
    /**
     * &PaCO2 Computed PaCO2 at Patient Temperature on the arterial blood
     */
    NLS_NOM_CONC_PCO2_ART_ADJ(0x0002F832L),
    /**
     * &PcCO2 Computed PcO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_CAP_ADJ(0x0002F833L),
    /**
     * &PCO2 Computed PCO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_GEN_ADJ(0x0002F834L),
    /**
     * &PvCO2 Computed PvCO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_VEN_ADJ(0x0002F835L),
    /**
     * &pHa Adjusted pH in the arterial Blood
     */
    NLS_NOM_CONC_PH_ART_ADJ(0x0002F836L),
    /**
     * &pHc Adjusted pH value in the capillaries
     */
    NLS_NOM_CONC_PH_CAP_ADJ(0x0002F837L),
    /**
     * &pH Adjusted pH at &Patient Temperature
     */
    NLS_NOM_CONC_PH_GEN_ADJ(0x0002F838L),
    /**
     * &pHv Adjusted pH value in the venous Blood
     */
    NLS_NOM_CONC_PH_VEN_ADJ(0x0002F839L),
    /**
     * &PaO2 Adjusted PaO2 at Patient Temperature on the arterial blood
     */
    NLS_NOM_CONC_PO2_ART_ADJ(0x0002F83BL),
    /**
     * &PcO2 Adjusted PcO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_CAP_ADJ(0x0002F83CL),
    /**
     * &PO2 Adjusted PO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_GEN_ADJ(0x0002F83DL),
    /**
     * &PvO2 Adjusted PvO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_VEN_ADJ(0x0002F83EL),
    /**
     * COsm Osmolar Clearance
     */
    NLS_NOM_CREA_OSM(0x0002F83FL),
    /**
     * BSI Burst Suppression Indicator
     */
    NLS_NOM_EEG_BURST_SUPPRN_INDEX(0x0002F840L),
    /**
     * LSCALE Scale of the Left Channel EEG wave
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT(0x0002F841L),
    /**
     * RSCALE Scale of the Right Channel EEG wave
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT(0x0002F842L),
    /**
     * LT MDF Mean Dominant Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_LEFT(0x0002F849L),
    /**
     * RT MDF Mean Dominant Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_RIGHT(0x0002F84AL),
    /**
     * LT MPF Median Power Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT(0x0002F84BL),
    /**
     * RT MPF Median Power Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT(0x0002F84CL),
    /**
     * LT PPF Peak Power Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_LEFT(0x0002F84FL),
    /**
     * RT PPF Peak Power Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_RIGHT(0x0002F850L),
    /**
     * LT SEF Spectral Edge Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_LEFT(0x0002F853L),
    /**
     * RT SEF Spectral Edge Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_RIGHT(0x0002F854L),
    /**
     * LT AL Absolute Alpha - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT(0x0002F855L),
    /**
     * RT AL Absolute Alpha - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT(0x0002F856L),
    /**
     * LT %AL Percent Alpha - Left (LT) Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_LEFT(0x0002F859L),
    /**
     * RT %AL Percent Alpha - Right (RT) Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_RIGHT(0x0002F85AL),
    /**
     * LT BE Absolute Beta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_ABS_LEFT(0x0002F85BL),
    /**
     * RT BE Absolute Beta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT(0x0002F85CL),
    /**
     * LT %BE Percent Beta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_REL_LEFT(0x0002F85FL),
    /**
     * RT %BE Percent Beta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_REL_RIGHT(0x0002F860L),
    /**
     * LT DL Absolute Delta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT(0x0002F863L),
    /**
     * RT DL Absolute Delta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT(0x0002F864L),
    /**
     * LT %DL Percent Delta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_REL_LEFT(0x0002F867L),
    /**
     * RT %DL Percent Delta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_REL_RIGHT(0x0002F868L),
    /**
     * LT TH Absolute Theta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_ABS_LEFT(0x0002F869L),
    /**
     * RT TH Absolute Theta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT(0x0002F86AL),
    /**
     * LT %TH Percent Theta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_REL_LEFT(0x0002F86DL),
    /**
     * RT %TH Percent Theta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_REL_RIGHT(0x0002F86EL),
    /**
     * LT TP Total Power - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_TOT_LEFT(0x0002F871L),
    /**
     * RT TP Total Power - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_TOT_RIGHT(0x0002F872L),
    /**
     * AAI A-Line ARX Index
     */
    NLS_NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI(0x0002F873L),
    /**
     * O2EI Oxygen Extraction Index
     */
    NLS_NOM_EXTRACT_O2_INDEX(0x0002F875L),
    /**
     * fgAGT Fresh gas Anesthetic Agent
     */
    NLS_NOM_FLOW_AWAY_AGENT(0x0002F876L),
    /**
     * fgAir Fresh Gas Flow of Air
     */
    NLS_NOM_FLOW_AWAY_AIR(0x0002F877L),
    /**
     * fgDES fresh gas agent for DESflurane
     */
    NLS_NOM_FLOW_AWAY_DESFL(0x0002F878L),
    /**
     * fgENF fresh gas agent for ENFlurane
     */
    NLS_NOM_FLOW_AWAY_ENFL(0x0002F879L),
    /**
     * eeFlow Expiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_EXP_ET(0x0002F87AL),
    /**
     * fgHAL fresh gas agent for HALothane
     */
    NLS_NOM_FLOW_AWAY_HALOTH(0x0002F87BL),
    /**
     * fgISO fresh gas agent for ISOflurane
     */
    NLS_NOM_FLOW_AWAY_ISOFL(0x0002F87CL),
    /**
     * SpPkFl Spontaneous Peak Flow
     */
    NLS_NOM_FLOW_AWAY_MAX_SPONT(0x0002F87DL),
    /**
     * fgN2O N2O concentration in the fresh gas line
     */
    NLS_NOM_FLOW_AWAY_N2O(0x0002F87EL),
    /**
     * fgO2 Oxygen concentration in the fresh gas line
     */
    NLS_NOM_FLOW_AWAY_O2(0x0002F87FL),
    /**
     * fgSEV fresh gas agent for SEVoflurane
     */
    NLS_NOM_FLOW_AWAY_SEVOFL(0x0002F880L),
    /**
     * fgFlow Total Fresh Gas Flow
     */
    NLS_NOM_FLOW_AWAY_TOT(0x0002F881L),
    /**
     * VCO2ti CO2 Tidal Production
     */
    NLS_NOM_FLOW_CO2_PROD_RESP_TIDAL(0x0002F882L),
    /**
     * U/O Daily Urine output
     */
    NLS_NOM_FLOW_URINE_PREV_24HR(0x0002F883L),
    /**
     * CH2O Free Water Clearance
     */
    NLS_NOM_FREE_WATER_CLR(0x0002F884L),
    /**
     * MCH Mean Corpuscular Hemoglobin. Is the erithrocyte hemoglobin content
     */
    NLS_NOM_HB_CORP_MEAN(0x0002F885L),
    /**
     * Power Power requ'd to set the Air&Pat Temp in the incubator
     */
    NLS_NOM_HEATING_PWR_INCUBATOR(0x0002F886L),
    /**
     * ACI Accelerated Cardiac Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX_ACCEL(0x0002F889L),
    /**
     * PTC Post Tetatic Count stimulation
     */
    NLS_NOM_PTC_CNT(0x0002F88BL),
    /**
     * PlGain Pleth Gain
     */
    NLS_NOM_PULS_OXIM_PLETH_GAIN(0x0002F88DL),
    /**
     * RVrat Rate Volume Ratio
     */
    NLS_NOM_RATIO_AWAY_RATE_VOL_AWAY(0x0002F88EL),
    /**
     * BUN/cr BUN Creatinine Ratio
     */
    NLS_NOM_RATIO_BUN_CREA(0x0002F88FL),
    /**
     * 'B/Cre Ratio BUN/Creatinine. Calculated value by the i-Stat module
     */
    NLS_NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC(0x0002F890L),
    /**
     * 'U/Cre Ratio Urea/Creatinine. Calculated value by the i-Stat module
     */
    NLS_NOM_RATIO_CONC_URINE_CREA_CALC(0x0002F891L),
    /**
     * U/SCr Urine Serum Creatinine Ratio
     */
    NLS_NOM_RATIO_CONC_URINE_CREA_SER(0x0002F892L),
    /**
     * UrNa/K Urine Sodium/Potassium Ratio
     */
    NLS_NOM_RATIO_CONC_URINE_NA_K(0x0002F893L),
    /**
     * PaFIO2 PaO2 to FIO2 ratio. Expressed in mmHg to % ratio
     */
    NLS_NOM_RATIO_PaO2_FIO2(0x0002F894L),
    /**
     * PTrat Prothrombin Time Ratio
     */
    NLS_NOM_RATIO_TIME_PD_PT(0x0002F895L),
    /**
     * PTTrat Activated Partial Thromboplastin Time Ratio
     */
    NLS_NOM_RATIO_TIME_PD_PTT(0x0002F896L),
    /**
     * TOFrat Train Of Four (TOF) ratio
     */
    NLS_NOM_RATIO_TRAIN_OF_FOUR(0x0002F897L),
    /**
     * U/POsm Urine Plasma Osmolarity Ratio
     */
    NLS_NOM_RATIO_URINE_SER_OSM(0x0002F898L),
    /**
     * Rdyn Dynamic Lung Resistance
     */
    NLS_NOM_RES_AWAY_DYN(0x0002F899L),
    /**
     * RRsync Sync Breath Rate
     */
    NLS_NOM_RESP_BREATH_ASSIST_CNT(0x0002F89AL),
    /**
     * REF Right Heart Ejection Fraction
     */
    NLS_NOM_RIGHT_HEART_FRACT_EJECT(0x0002F89BL),
    /**
     * 'SO2 Calculated SO2
     */
    NLS_NOM_SAT_O2_CALC(0x0002F89CL),
    /**
     * SO2 l Oxygen Saturation Left Side
     */
    NLS_NOM_SAT_O2_LEFT(0x0002F89DL),
    /**
     * SO2 r Oxygen Saturation Right Side
     */
    NLS_NOM_SAT_O2_RIGHT(0x0002F89EL),
    /**
     * RemTi Remaining Time until next stimulation
     */
    NLS_NOM_TIME_PD_EVOK_REMAIN(0x0002F8A0L),
    /**
     * ExpTi Expiratory Time
     */
    NLS_NOM_TIME_PD_EXP(0x0002F8A1L),
    /**
     * Elapse Time to Elapse Counter
     */
    NLS_NOM_TIME_PD_FROM_LAST_MSMT(0x0002F8A2L),
    /**
     * InsTi Spontaneous Inspiration Time
     */
    NLS_NOM_TIME_PD_INSP(0x0002F8A3L),
    /**
     * KCT Kaolin cephalin time
     */
    NLS_NOM_TIME_PD_KAOLIN_CEPHALINE(0x0002F8A4L),
    /**
     * PTT Partial Thromboplastin Time
     */
    NLS_NOM_TIME_PD_PTT(0x0002F8A5L),
    /**
     * TOF1 TrainOf Four (TOF) first response value TOF1
     */
    NLS_NOM_TRAIN_OF_FOUR_1(0x0002F8A7L),
    /**
     * TOF2 TrainOf Four (TOF) first response value TOF2
     */
    NLS_NOM_TRAIN_OF_FOUR_2(0x0002F8A8L),
    /**
     * TOF3 TrainOf Four (TOF) first response value TOF3
     */
    NLS_NOM_TRAIN_OF_FOUR_3(0x0002F8A9L),
    /**
     * TOF4 TrainOf Four (TOF) first response value TOF4
     */
    NLS_NOM_TRAIN_OF_FOUR_4(0x0002F8AAL),
    /**
     * TOFcnt Train Of Four (TOF) count - Number of TOF responses.
     */
    NLS_NOM_TRAIN_OF_FOUR_CNT(0x0002F8ABL),
    /**
     * Twitch Twitch height of the 1Hz/0.1Hz stimulation response
     */
    NLS_NOM_TWITCH_AMPL(0x0002F8ACL),
    /**
     * SrUrea Serum Urea
     */
    NLS_NOM_UREA_SER(0x0002F8ADL),
    /**
     * PtVent Parameter which informs whether the Patient is ventilated
     */
    NLS_NOM_VENT_ACTIVE(0x0002F8B0L),
    /**
     * HFVAmp High Frequency Ventilation Amplitude
     */
    NLS_NOM_VENT_AMPL_HFV(0x0002F8B1L),
    /**
     * i-eAGT Inspired - EndTidal Agent
     */
    NLS_NOM_VENT_CONC_AWAY_AGENT_DELTA(0x0002F8B2L),
    /**
     * i-eDES Inspired - EndTidal Desfluran
     */
    NLS_NOM_VENT_CONC_AWAY_DESFL_DELTA(0x0002F8B3L),
    /**
     * i-eENF Inspired - EndTidal Enfluran
     */
    NLS_NOM_VENT_CONC_AWAY_ENFL_DELTA(0x0002F8B4L),
    /**
     * i-eHAL Inspired - EndTidal Halothane
     */
    NLS_NOM_VENT_CONC_AWAY_HALOTH_DELTA(0x0002F8B5L),
    /**
     * i-eISO Inspired - EndTidal Isofluran
     */
    NLS_NOM_VENT_CONC_AWAY_ISOFL_DELTA(0x0002F8B6L),
    /**
     * i-eN2O Inspired - EndTidal N2O
     */
    NLS_NOM_VENT_CONC_AWAY_N2O_DELTA(0x0002F8B7L),
    /**
     * cktO2 O2 measured in the Patient Circuit
     */
    NLS_NOM_VENT_CONC_AWAY_O2_CIRCUIT(0x0002F8B8L),
    /**
     * i-eSEV Inspired - EndTidal Sevofluran
     */
    NLS_NOM_VENT_CONC_AWAY_SEVOFL_DELTA(0x0002F8B9L),
    /**
     * loPEEP Alarm Limit: Low PEEP/CPAP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO(0x0002F8BAL),
    /**
     * Pmax Maximum Pressure during a breathing cycle
     */
    NLS_NOM_VENT_PRESS_AWAY_INSP_MAX(0x0002F8BBL),
    /**
     * PVcP Pressure Ventilation Control Pressure
     */
    NLS_NOM_VENT_PRESS_AWAY_PV(0x0002F8BCL),
    /**
     * RiseTi Rise Time
     */
    NLS_NOM_VENT_TIME_PD_RAMP(0x0002F8BDL),
    /**
     * HFTVin Inspired High Frequency Tidal Volume
     */
    NLS_NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV(0x0002F8BEL),
    /**
     * HFVTV High Frequency Fraction Ventilation Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_HFV(0x0002F8BFL),
    /**
     * SpTVex Spontaenous Expired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_EXP_TIDAL_SPONT(0x0002F8C2L),
    /**
     * TVPSV Tidal Volume (TV) in Pressure Support Ventilation mode
     */
    NLS_NOM_VOL_AWAY_TIDAL_PSV(0x0002F8C3L),
    /**
     * MCV Mean Corpuscular Volume
     */
    NLS_NOM_VOL_CORP_MEAN(0x0002F8C4L),
    /**
     * TFC Thoracic Fluid Content
     */
    NLS_NOM_VOL_FLUID_THORAC(0x0002F8C5L),
    /**
     * TFI Thoracic Fluid Content Index
     */
    NLS_NOM_VOL_FLUID_THORAC_INDEX(0x0002F8C6L),
    /**
     * AGTLev Liquid level in the anesthetic agent bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_AGENT(0x0002F8C7L),
    /**
     * DESLev Liquid level in the DESflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_DESFL(0x0002F8C8L),
    /**
     * ENFLev Liquid level in the ENFlurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ENFL(0x0002F8C9L),
    /**
     * HALLev Liquid level in the HALothane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH(0x0002F8CAL),
    /**
     * ISOLev Liquid level in the ISOflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL(0x0002F8CBL),
    /**
     * SEVLev Liquid level in the SEVoflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL(0x0002F8CCL),
    /**
     * HFMVin Inspired High Frequency Mandatory Minute Volume
     */
    NLS_NOM_VOL_MINUTE_AWAY_INSP_HFV(0x0002F8CDL),
    /**
     * tUrVol Total Urine Volume of the current measurement period
     */
    NLS_NOM_VOL_URINE_BAL_PD_INSTANT(0x0002F8CEL),
    /**
     * UrVSht Urimeter - Urine Shift Volume.
     */
    NLS_NOM_VOL_URINE_SHIFT(0x0002F8CFL),
    /**
     * EDVI End Diastolic Volume Index
     */
    NLS_NOM_VOL_VENT_L_END_DIA_INDEX(0x0002F8D0L),
    /**
     * ESVI End Systolic Volume Index
     */
    NLS_NOM_VOL_VENT_L_END_SYS_INDEX(0x0002F8D1L),
    /**
     * BagWgt Weight of the Urine Disposable Bag
     */
    NLS_NOM_WEIGHT_URINE_COL(0x0002F8D3L),
    /**
     * StO2 O2 Saturation (tissue)
     */
    NLS_NOM_SAT_O2_TISSUE(0x0002F960L),
    /**
     * CSI
     */
    NLS_NOM_CEREB_STATE_INDEX(0x0002F961L),
    /**
     * SO2 1 O2 Saturation 1 (generic)
     */
    NLS_NOM_SAT_O2_GEN_1(0x0002F962L),
    /**
     * SO2 2 O2 Saturation 2 (generic)
     */
    NLS_NOM_SAT_O2_GEN_2(0x0002F963L),
    /**
     * SO2 3 O2 Saturation 3 (generic)
     */
    NLS_NOM_SAT_O2_GEN_3(0x0002F964L),
    /**
     * SO2 4 O2 Saturation 4 (generic)
     */
    NLS_NOM_SAT_O2_GEN_4(0x0002F965L),
    /**
     * T1Core Core Temperature 1 (generic)
     */
    NLS_NOM_TEMP_CORE_GEN_1(0x0002F966L),
    /**
     * T2Core Core Temperature 2 (generic)
     */
    NLS_NOM_TEMP_CORE_GEN_2(0x0002F967L),
    /**
     * DeltaP Blood Pressure difference
     */
    NLS_NOM_PRESS_BLD_DIFF(0x0002F968L),
    /**
     * DeltaP1 Blood Pressure difference 1 (generic)
     */
    NLS_NOM_PRESS_BLD_DIFF_GEN_1(0x0002F96CL),
    /**
     * DeltaP2 Blood Pressure difference 2 (generic)
     */
    NLS_NOM_PRESS_BLD_DIFF_GEN_2(0x0002F970L),
    /**
     * HLMfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_MAIN(0x0002F974L),
    /**
     * SlvPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_SLAVE(0x0002F975L),
    /**
     * SucPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_SUCTION(0x0002F976L),
    /**
     * AuxPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_AUX(0x0002F977L),
    /**
     * PlePfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0x0002F978L),
    /**
     * SplPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0x0002F979L),
    /**
     * AxOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START(0x0002F97AL),
    /**
     * AxOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP(0x0002F97BL),
    /**
     * AxDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX(0x0002F97CL),
    /**
     * AxTVol
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX(0x0002F97DL),
    /**
     * AxPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX(0x0002F97EL),
    /**
     * CpOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START(0x0002F97FL),
    /**
     * CpOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP(0x0002F980L),
    /**
     * CpDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0x0002F981L),
    /**
     * CpTVol
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0x0002F982L),
    /**
     * CpPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN(0x0002F983L),
    /**
     * CsOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START(0x0002F984L),
    /**
     * CsOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP(0x0002F985L),
    /**
     * CsDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0x0002F986L),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0x0002F987L),
    /**
     * CsPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE(0x0002F988L),
    /**
     * Tin/Tt
     */
    NLS_NOM_RATIO_INSP_TOTAL_BREATH_SPONT(0x0002F990L),
    /**
     * tPEEP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL(0x0002F991L),
    /**
     * Cpav
     */
    NLS_NOM_COMPL_LUNG_PAV(0x0002F992L),
    /**
     * Rpav
     */
    NLS_NOM_RES_AWAY_PAV(0x0002F993L),
    /**
     * Rtot
     */
    NLS_NOM_RES_AWAY_EXP_TOTAL(0x0002F994L),
    /**
     * Epav
     */
    NLS_NOM_ELAS_LUNG_PAV(0x0002F995L),
    /**
     * RSBInm
     */
    NLS_NOM_BREATH_RAPID_SHALLOW_INDEX_NORM(0x0002F996L),
    /**
     * P_1 non-specific label for Pressure 1
     */
    NLS_NOM_EMFC_P1(0x04010030L),
    /**
     * P_2 non-specific label for Pressure 2
     */
    NLS_NOM_EMFC_P2(0x04010034L),
    /**
     * P_3 non-specific label for Pressure 3
     */
    NLS_NOM_EMFC_P3(0x04010038L),
    /**
     * P_4 non-specific label for Pressure 4
     */
    NLS_NOM_EMFC_P4(0x0401003CL),
    /**
     * IUP Intra-Uterine Pressure
     */
    NLS_NOM_EMFC_IUP(0x04010054L),
    /**
     * AUX Auxiliary Wave/Parameter
     */
    NLS_NOM_EMFC_AUX(0x040100B4L),
    /**
     * P_5 non-specific label for Pressure 5
     */
    NLS_NOM_EMFC_P5(0x04010400L),
    /**
     * P_6 non-specific label for Pressure 6
     */
    NLS_NOM_EMFC_P6(0x04010404L),
    /**
     * P_7 non-specific label for Pressure 7
     */
    NLS_NOM_EMFC_P7(0x04010408L),
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_EMFC_P8(0x0401040CL),
    /**
     * AWV Airway Volume Wave
     */
    NLS_NOM_EMFC_AWV(0x04010668L),
    /**
     * L V1 Lead V1 - ECG wave label
     */
    NLS_NOM_EMFC_L_V1(0x04010764L),
    /**
     * L V2 Lead V2 - ECG wave label
     */
    NLS_NOM_EMFC_L_V2(0x04010768L),
    /**
     * L V3 Lead V3 - ECG wave label
     */
    NLS_NOM_EMFC_L_V3(0x0401076CL),
    /**
     * L V4 Lead V4 - ECG wave label
     */
    NLS_NOM_EMFC_L_V4(0x04010770L),
    /**
     * L V5 Lead V5 - ECG wave label
     */
    NLS_NOM_EMFC_L_V5(0x04010774L),
    /**
     * L V6 Lead V6 - ECG wave label
     */
    NLS_NOM_EMFC_L_V6(0x04010778L),
    /**
     * L I Lead I - ECG wave label
     */
    NLS_NOM_EMFC_L_I(0x0401077CL),
    /**
     * L II Lead II - ECG wave label
     */
    NLS_NOM_EMFC_L_II(0x04010780L),
    /**
     * L III Lead III - ECG wave label
     */
    NLS_NOM_EMFC_L_III(0x04010784L),
    /**
     * L aVR Lead aVR - ECG wave label
     */
    NLS_NOM_EMFC_L_aVR(0x04010788L),
    /**
     * L aVL Lead aVL - ECG wave label
     */
    NLS_NOM_EMFC_L_aVL(0x0401078CL),
    /**
     * L aVF Lead aVF - ECG wave label
     */
    NLS_NOM_EMFC_L_aVF(0x04010790L),
    /**
     * AWVex Expiratory Airway Volume Wave. Measured in l.
     */
    NLS_NOM_EMFC_AWVex(0x04010794L),
    /**
     * PLETH2 PLETH from the second SpO2/PLETH module
     */
    NLS_NOM_EMFC_PLETH2(0x0401079CL),
    /**
     * LT EEG Left channel EEG wave
     */
    NLS_NOM_EMFC_LT_EEG(0x040107F0L),
    /**
     * RT EEG Right channel EEG wave
     */
    NLS_NOM_EMFC_RT_EEG(0x0401082CL),
    /**
     * BP Unspecified Blood Pressure
     */
    NLS_NOM_EMFC_BP(0x04010888L),
    /**
     * AGTs Anesthetic Agent - secondary agent
     */
    NLS_NOM_EMFC_AGTs(0x04010CE4L),
    /**
     * vECG Vector ECG taken from ICG
     */
    NLS_NOM_EMFC_vECG(0x0401119CL),
    /**
     * ICG Impedance Cardiography
     */
    NLS_NOM_EMFC_ICG(0x040111A0L),
    /**
     * sTemp Desired Environmental Temperature
     */
    NLS_NOM_SETT_TEMP(0x04024B48L),
    /**
     * sAWRR Setting: Airway Respiratory Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE(0x04025012L),
    /**
     * sRRaw Setting: Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NLS_NOM_SETT_VENT_RESP_RATE(0x04025022L),
    /**
     * sPIF Setting: Peak Inspiratory Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP_MAX(0x040250DDL),
    /**
     * sPmin Setting: Low Inspiratory Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_MIN(0x040250F2L),
    /**
     * sCPAP Setting: Continuous Positive Airway Pressure Value
     */
    NLS_NOM_SETT_PRESS_AWAY_CTS_POS(0x040250F4L),
    /**
     * sPin Setting: Pressure Ventilation Control Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP(0x04025108L),
    /**
     * sPIP Setting: Positive Inspiratory Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP_MAX(0x04025109L),
    /**
     * sIE 1: Setting: Inspiration to Expiration Ratio.
     */
    NLS_NOM_SETT_RATIO_IE(0x04025118L),
    /**
     * sTV Setting: Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL(0x0402513CL),
    /**
     * sMV Setting: Minute Volume
     */
    NLS_NOM_SETT_VOL_MINUTE_AWAY(0x04025148L),
    /**
     * sO2 Enumeration Type - denotes type of Instrument.
     */
    NLS_NOM_SETT_CONC_AWAY_O2(0x04025164L),
    /**
     * sPEEP Setting: PEEP/CPAP
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS(0x040251A8L),
    /**
     * sTrVol Setting: Trigger Flow/Volume
     */
    NLS_NOM_SETT_VENT_VOL_LUNG_TRAPD(0x040251B8L),
    /**
     * sMMV Setting: Mandatory Minute Volume
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND(0x040251CCL),
    /**
     * sDES Setting: Vaporizer concentration for DESflurane
     */
    NLS_NOM_SETT_CONC_AWAY_DESFL(0x040251D8L),
    /**
     * sENF Setting: Vaporizer concentration for ENFlurane
     */
    NLS_NOM_SETT_CONC_AWAY_ENFL(0x040251DCL),
    /**
     * sHAL Setting: Vaporizer concentration for HALothane
     */
    NLS_NOM_SETT_CONC_AWAY_HALOTH(0x040251E0L),
    /**
     * sSEV Setting: Vaporizer concentration for SEVoflurane
     */
    NLS_NOM_SETT_CONC_AWAY_SEVOFL(0x040251E4L),
    /**
     * sISO Setting: Vaporizer concentration for ISOflurane
     */
    NLS_NOM_SETT_CONC_AWAY_ISOFL(0x040251E8L),
    /**
     * sDRate Setting: Infusion Pump Delivery Rate
     */
    NLS_NOM_SETT_FLOW_FLUID_PUMP(0x04026858L),
    /**
     * sFIO2 Setting: Inspired Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP(0x04027498L),
    /**
     * sTVin Setting: inspired Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_INSP_TIDAL(0x0402F0E0L),
    /**
     * sPltTi Setting: Plateau Time
     */
    NLS_NOM_SETT_TIME_PD_RESP_PLAT(0x0402F0FFL),
    /**
     * sAGT Setting: Vaporizer concentration.
     */
    NLS_NOM_SETT_FLOW_AWAY_AGENT(0x0402F876L),
    /**
     * sfgAir Setting: Total fresh gas Air flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_AIR(0x0402F877L),
    /**
     * sfgN2O Setting: fresh gas N2O flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_N2O(0x0402F87EL),
    /**
     * sfgO2 Setting: Fresh gas oxygen Flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_O2(0x0402F87FL),
    /**
     * sfgFl Setting: Total fresh gas Flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_TOT(0x0402F881L),
    /**
     * sRepTi Setting: Preset Train Of Four (Slow TOF) repetition time
     */
    NLS_NOM_SETT_TIME_PD_TRAIN_OF_FOUR(0x0402F8A6L),
    /**
     * sUrTi Setting: Preset period of time for the UrVol numeric
     */
    NLS_NOM_SETT_URINE_BAL_PD(0x0402F8AFL),
    /**
     * sPmax Setting: Maximum Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_MAX(0x0402F8BBL),
    /**
     * sPSV Setting: Pressure Support Ventilation
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_PV(0x0402F8BCL),
    /**
     * sSghTV Setting: Sigh Tidal Volume
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_SIGH(0x0402F8C0L),
    /**
     * sAADel Setting: Apnea Ventilation Delay
     */
    NLS_NOM_SETT_APNEA_ALARM_DELAY(0x0402F8D9L),
    /**
     * sARR Setting: Apnea Respiration Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE_APNEA(0x0402F8DEL),
    /**
     * sHFVRR Setting: High Frequency Ventilation Respiration Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE_HFV(0x0402F8DFL),
    /**
     * sChrge Setting: Preset stimulation charge
     */
    NLS_NOM_SETT_EVOK_CHARGE(0x0402F8E6L),
    /**
     * sCurnt Setting: Preset stimulation current
     */
    NLS_NOM_SETT_EVOK_CURR(0x0402F8E7L),
    /**
     * sExpFl Setting: Expiratory Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_EXP(0x0402F8EAL),
    /**
     * sHFVFl Setting: High Freqyency Ventilation Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_HFV(0x0402F8EBL),
    /**
     * sInsFl Setting: Inspiratory Flow.
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP(0x0402F8ECL),
    /**
     * sAPkFl Setting: Apnea Peak Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP_APNEA(0x0402F8EDL),
    /**
     * sHFVAm Setting: HFV Amplitude (Peak to Peak Pressure)
     */
    NLS_NOM_SETT_HFV_AMPL(0x0402F8F3L),
    /**
     * loPmax Setting: Low Maximum Airway Pressure Alarm Setting.
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO(0x0402F8FBL),
    /**
     * sPVE Setting: Pressure Ventilation E component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_EXP_PV(0x0402F900L),
    /**
     * sAPVE Setting: Apnea Pressure Ventilation E component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_EXP_PV_APNEA(0x0402F901L),
    /**
     * sPVI Setting: Pressure Ventilation I component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_INSP_PV(0x0402F902L),
    /**
     * sAPVI Setting: Apnea Pressure Ventilation I component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_INSP_PV_APNEA(0x0402F903L),
    /**
     * sSens Setting: Assist Sensitivity. Used by the Bear 1000 ventilator.
     */
    NLS_NOM_SETT_SENS_LEVEL(0x0402F904L),
    /**
     * sPulsD Setting: Preset stimulation impulse duration
     */
    NLS_NOM_SETT_TIME_PD_EVOK(0x0402F908L),
    /**
     * sCycTi Setting: Cycle Time
     */
    NLS_NOM_SETT_TIME_PD_MSMT(0x0402F909L),
    /**
     * sO2Mon Setting: O2 Monitoring
     */
    NLS_NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE(0x0402F90EL),
    /**
     * sBkgFl Setting: Background Flow Setting. Range is 2 - 30 l/min
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_BACKGROUND(0x0402F90FL),
    /**
     * sBasFl Setting: Flow-by Base Flow
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_BASE(0x0402F910L),
    /**
     * sSenFl Setting: Flow-by Sensitivity Flow
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_SENSE(0x0402F911L),
    /**
     * sPincR Setting: Pressure Increase Rate
     */
    NLS_NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE(0x0402F912L),
    /**
     * sAFIO2 Setting: Apnea Inspired O2 Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA(0x0402F917L),
    /**
     * sAPVO2 Setting: Apnea Pressure Ventilation Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA(0x0402F918L),
    /**
     * highO2 Alarm Limit. High Oxygen (O2) Alarm Limit
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI(0x0402F919L),
    /**
     * lowO2 Alarm Limit: Low Oxygen (O2) Alarm Limit
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO(0x0402F91AL),
    /**
     * sFlow Setting: Flow
     */
    NLS_NOM_SETT_VENT_FLOW(0x0402F91BL),
    /**
     * sFlas Setting: Flow Assist level for the CPAP mode
     */
    NLS_NOM_SETT_VENT_FLOW_AWAY_ASSIST(0x0402F91CL),
    /**
     * sTrgFl Setting: Flow Trigger - delivered by the Evita 2 Vuelink Driver
     */
    NLS_NOM_SETT_VENT_FLOW_INSP_TRIG(0x0402F91DL),
    /**
     * sGasPr Setting: Gas Sample point for the oxygen measurement
     */
    NLS_NOM_SETT_VENT_GAS_PROBE_POSN(0x0402F920L),
    /**
     * sCMV Setting: Controlled mechanical ventilation
     */
    NLS_NOM_SETT_VENT_MODE_MAND_CTS_ONOFF(0x0402F922L),
    /**
     * sEnSgh Setting: Enable Sigh
     */
    NLS_NOM_SETT_VENT_MODE_SIGH(0x0402F923L),
    /**
     * sSIMV Setting: Synchronized intermittent mandatory ventilation
     */
    NLS_NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT(0x0402F924L),
    /**
     * sO2Cal Setting: O2 Calibration
     */
    NLS_NOM_SETT_VENT_O2_CAL_MODE(0x0402F926L),
    /**
     * sO2Pr Setting: Gas sample point for oxygen measurement
     */
    NLS_NOM_SETT_VENT_O2_PROBE_POSN(0x0402F927L),
    /**
     * sO2Suc Setting: Suction Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_O2_SUCTION_MODE(0x0402F928L),
    /**
     * sSPEEP Setting: Pressure Support PEEP
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT(0x0402F92CL),
    /**
     * sPlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_EXP_APRV(0x0402F92DL),
    /**
     * sPhigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_APRV(0x0402F92EL),
    /**
     * highP Alarm Limit: High Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI(0x0402F930L),
    /**
     * sAPVhP Setting: Apnea Pressure Ventilation High Airway Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA(0x0402F931L),
    /**
     * sAPVcP Setting: Apnea Pressure Ventilation Control Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_PV_APNEA(0x0402F933L),
    /**
     * sustP Alarm Limit: Sustained Pressure Alarm Limit.
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI(0x0402F935L),
    /**
     * sfmax Setting: Panting Limit
     */
    NLS_NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT(0x0402F937L),
    /**
     * sIMV Setting: Ventilation Frequency in IMV Mode
     */
    NLS_NOM_SETT_VENT_RESP_RATE_MODE_MAND_INTERMITT(0x0402F938L),
    /**
     * sIPPV Setting: Ventilation Frequency in IPPV Mode
     */
    NLS_NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP(0x0402F939L),
    /**
     * sAPVRR Setting: Apnea Pressure Ventilation Respiration Rate
     */
    NLS_NOM_SETT_VENT_RESP_RATE_PV_APNEA(0x0402F93AL),
    /**
     * sSghNr Setting: Multiple Sigh Number
     */
    NLS_NOM_SETT_VENT_SIGH_MULT_RATE(0x0402F93BL),
    /**
     * sSghR Setting: Sigh Rate
     */
    NLS_NOM_SETT_VENT_SIGH_RATE(0x0402F93CL),
    /**
     * sExpTi Setting: Exhaled Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_EXP(0x0402F93FL),
    /**
     * sTlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_TIME_PD_EXP_APRV(0x0402F940L),
    /**
     * sInsTi Setting: Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP(0x0402F941L),
    /**
     * sThigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_APRV(0x0402F942L),
    /**
     * sPVinT Setting: Pressure Ventilation Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_PV(0x0402F943L),
    /**
     * sAPVTi Setting: Apnea Pressure Ventilation Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA(0x0402F944L),
    /**
     * sALMRT Setting: Alarm Percentage on Rise Time.
     */
    NLS_NOM_SETT_VENT_TIME_PD_RAMP_AL(0x0402F946L),
    /**
     * sVolas Setting: Volume Assist level for the CPAP mode
     */
    NLS_NOM_SETT_VENT_VOL_AWAY_ASSIST(0x0402F948L),
    /**
     * sVmax Setting: Volume Warning - delivered by the Evita 2 Vuelink Driver
     */
    NLS_NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF(0x0402F949L),
    /**
     * highMV Alarm Limit: High Minute Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI(0x0402F94BL),
    /**
     * lowMV Alarm Limit: Low Minute Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO(0x0402F94CL),
    /**
     * highTV Alarm Limit: High Tidal Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI(0x0402F94DL),
    /**
     * lowTV Alarm Limit: Low Tidal Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO(0x0402F94EL),
    /**
     * sATV Setting: Apnea Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL_APNEA(0x0402F951L),
    /**
     * sTVap Setting: Applied Tidal Volume.
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL_APPLIED(0x0402F952L),
    /**
     * sMVDel Setting: Minute Volume Alarm Delay
     */
    NLS_NOM_SETT_VOL_MINUTE_ALARM_DELAY(0x0402F953L),
;

    private final long x;
    
    private Label(long x) {
        this.x = x;
    }
    
    private final static Map<Long, Label> map = OrdinalEnum.buildLong(Label.class); 
    
    public final long asLong()  {
        return x;
    }
    public static final Label valueOf(long x) {
        return map.get(x);
    }
}
