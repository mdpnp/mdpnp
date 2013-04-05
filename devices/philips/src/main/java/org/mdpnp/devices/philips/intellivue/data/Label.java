package org.mdpnp.devices.philips.intellivue.data;

public enum Label {
    /**
     * ECG Unspecific ECG wave
     */
    NLS_NOM_ECG_ELEC_POTL,
    /**
     * I ECG Lead I
     */
    NLS_NOM_ECG_ELEC_POTL_I,
    /**
     * II ECG Lead II
     */
    NLS_NOM_ECG_ELEC_POTL_II,
    /**
     * V1 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V1,
    /**
     * V2 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V2,
    /**
     * V3 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V3,
    /**
     * V4 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V4,
    /**
     * V5 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V5,
    /**
     * V6 ECG Lead V1
     */
    NLS_NOM_ECG_ELEC_POTL_V6,
    /**
     * III ECG Lead III
     */
    NLS_NOM_ECG_ELEC_POTL_III,
    /**
     * aVR ECG Lead AVR
     */
    NLS_NOM_ECG_ELEC_POTL_AVR,
    /**
     * aVL ECG Lead AVL
     */
    NLS_NOM_ECG_ELEC_POTL_AVL,
    /**
     * aVF ECG Lead AVF
     */
    NLS_NOM_ECG_ELEC_POTL_AVF,
    /**
     * V ECG Lead V
     */
    NLS_NOM_ECG_ELEC_POTL_V,
    /**
     * MCL ECG Lead MCL
     */
    NLS_NOM_ECG_ELEC_POTL_MCL,
    /**
     * MCL1 ECG Lead MCL1
     */
    NLS_NOM_ECG_ELEC_POTL_MCL1,
    /**
     * ST ST generic label
     */
    NLS_NOM_ECG_AMPL_ST,
    /**
     * QT
     */
    NLS_NOM_ECG_TIME_PD_QT_GL,
    /**
     * QTc
     */
    NLS_NOM_ECG_TIME_PD_QTc,
    /**
     * HR Heart Rate
     */
    NLS_NOM_ECG_CARD_BEAT_RATE,
    /**
     * btbHR Cardiac Beat-to-Beat Rate
     */
    NLS_NOM_ECG_CARD_BEAT_RATE_BTB,
    /**
     * PVC Premature Ventricular Contractions
     */
    NLS_NOM_ECG_V_P_C_CNT,
    /**
     * Pulse Pulse Rate
     */
    NLS_NOM_PULS_RATE,
    /**
     * Pulse Pulse Rate from Plethysmogram
     */
    NLS_NOM_PULS_OXIM_PULS_RATE,
    /**
     * SVRI Systemic Vascular Resistance Index
     */
    NLS_NOM_RES_VASC_SYS_INDEX,
    /**
     * LVSWI Left Ventricular Stroke Volume Index
     */
    NLS_NOM_WK_LV_STROKE_INDEX,
    /**
     * RVSWI Right Ventricular Stroke Work Index
     */
    NLS_NOM_WK_RV_STROKE_INDEX,
    /**
     * C.I. Cardiac Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX,
    /**
     * P unspecific pressure
     */
    NLS_NOM_PRESS_BLD,
    /**
     * NBP non-invasive blood pressure
     */
    NLS_NOM_PRESS_BLD_NONINV,
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NLS_NOM_PRESS_BLD_AORT,
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NLS_NOM_PRESS_BLD_ART,
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NLS_NOM_PRESS_BLD_ART_ABP,
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NLS_NOM_PRESS_BLD_ART_PULM,
    /**
     * PAWP Pulmonary Artery Wedge Pressure
     */
    NLS_NOM_PRESS_BLD_ART_PULM_WEDGE,
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NLS_NOM_PRESS_BLD_ART_UMB,
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NLS_NOM_PRESS_BLD_ATR_LEFT,
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NLS_NOM_PRESS_BLD_ATR_RIGHT,
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NLS_NOM_PRESS_BLD_VEN_CENT,
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NLS_NOM_PRESS_BLD_VEN_UMB,
    /**
     * VO2 Oxygen Consumption VO2
     */
    NLS_NOM_SAT_O2_CONSUMP,
    /**
     * C.O. Cardiac Output
     */
    NLS_NOM_OUTPUT_CARD,
    /**
     * PVR Pulmonary vascular Resistance
     */
    NLS_NOM_RES_VASC_PULM,
    /**
     * SVR Systemic Vascular Resistance
     */
    NLS_NOM_RES_VASC_SYS,
    /**
     * SO2 O2 Saturation
     */
    NLS_NOM_SAT_O2,
    /**
     * SaO2 Oxygen Saturation
     */
    NLS_NOM_SAT_O2_ART,
    /**
     * SvO2 Mixed Venous Oxygen Saturation
     */
    NLS_NOM_SAT_O2_VEN,
    /**
     * AaDO2 Alveolar- Arterial Oxygen Difference
     */
    NLS_NOM_SAT_DIFF_O2_ART_ALV,
    /**
     * Temp Unspecific Temperature
     */
    NLS_NOM_TEMP,
    /**
     * Tart Areterial Temperature
     */
    NLS_NOM_TEMP_ART,
    /**
     * Tairwy Airway Temperature
     */
    NLS_NOM_TEMP_AWAY,
    /**
     * Tbody Patient Temperature
     */
    NLS_NOM_TEMP_BODY,
    /**
     * Tcore Core (Body) Temperature
     */
    NLS_NOM_TEMP_CORE,
    /**
     * Tesoph Esophagial Temperature
     */
    NLS_NOM_TEMP_ESOPH,
    /**
     * Tinj Injectate Temperature
     */
    NLS_NOM_TEMP_INJ,
    /**
     * Tnaso Naso pharyngial Temperature
     */
    NLS_NOM_TEMP_NASOPH,
    /**
     * Tskin Skin Temperature
     */
    NLS_NOM_TEMP_SKIN,
    /**
     * Ttymp Tympanic Temperature
     */
    NLS_NOM_TEMP_TYMP,
    /**
     * Tven Venous Temperature
     */
    NLS_NOM_TEMP_VEN,
    /**
     * SV Stroke Volume
     */
    NLS_NOM_VOL_BLD_STROKE,
    /**
     * LCW Left Cardiac Work
     */
    NLS_NOM_WK_CARD_LEFT,
    /**
     * RCW Right Cardiac Work
     */
    NLS_NOM_WK_CARD_RIGHT,
    /**
     * LVSW Left Ventricular Stroke Volume
     */
    NLS_NOM_WK_LV_STROKE,
    /**
     * RVSW Right Ventricular Stroke Volume
     */
    NLS_NOM_WK_RV_STROKE,
    /**
     * Perf Perfusion Indicator
     */
    NLS_NOM_PULS_OXIM_PERF_REL,
    /**
     * Pleth PLETH wave label
     */
    NLS_NOM_PULS_OXIM_PLETH,
    /**
     * SpO2 Arterial Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2,
    /**
     * DeltaSpO2 Difference between two SpO2 Values (like Left - Right)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_DIFF,
    /**
     * SpO2 l Arterial Oxigen Saturation (left)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_ART_LEFT,
    /**
     * SpO2 r Arterial Oxigen Saturation (right)
     */
    NLS_NOM_PULS_OXIM_SAT_O2_ART_RIGHT,
    /**
     * CCO Continuous Cardiac Output
     */
    NLS_NOM_OUTPUT_CARD_CTS,
    /**
     * EDV End Diastolic Volume
     */
    NLS_NOM_VOL_VENT_L_END_DIA,
    /**
     * ESV End Systolic Volume
     */
    NLS_NOM_VOL_VENT_L_END_SYS,
    /**
     * dPmax Index of Left Ventricular Contractility
     */
    NLS_NOM_GRAD_PRESS_BLD_AORT_POS_MAX,
    /**
     * Resp Imedance RESP wave
     */
    NLS_NOM_RESP,
    /**
     * RR Respiration Rate
     */
    NLS_NOM_RESP_RATE,
    /**
     * awRR Airway Respiration Rate
     */
    NLS_NOM_AWAY_RESP_RATE,
    /**
     * RRaw Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NLS_NOM_VENT_RESP_RATE,
    /**
     * VC Vital Lung Capacity
     */
    NLS_NOM_CAPAC_VITAL,
    /**
     * COMP generic label Lung Compliance
     */
    NLS_NOM_COMPL_LUNG,
    /**
     * Cdyn Dynamic Lung Compliance
     */
    NLS_NOM_COMPL_LUNG_DYN,
    /**
     * Cstat Static Lung Compliance
     */
    NLS_NOM_COMPL_LUNG_STATIC,
    /**
     * CO2 CO2 concentration
     */
    NLS_NOM_AWAY_CO2,
    /**
     * tcpCO2 Transcutaneous Carbon Dioxide Partial Pressure
     */
    NLS_NOM_CO2_TCUT,
    /**
     * tcpO2 Transcutaneous Oxygen Partial Pressure
     */
    NLS_NOM_O2_TCUT,
    /**
     * AWF Airway Flow Wave
     */
    NLS_NOM_FLOW_AWAY,
    /**
     * PEF Expiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_EXP_MAX,
    /**
     * PIF Inspiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_INSP_MAX,
    /**
     * VCO2 CO2 Production
     */
    NLS_NOM_FLOW_CO2_PROD_RESP,
    /**
     * T.I. Transthoracic Impedance
     */
    NLS_NOM_IMPED_TTHOR,
    /**
     * Pplat Plateau Pressure
     */
    NLS_NOM_PRESS_RESP_PLAT,
    /**
     * AWP Airway Pressure Wave
     */
    NLS_NOM_PRESS_AWAY,
    /**
     * AWPmin Airway Pressure Minimum
     */
    NLS_NOM_PRESS_AWAY_MIN,
    /**
     * CPAP Continuous Positive Airway Pressure
     */
    NLS_NOM_PRESS_AWAY_CTS_POS,
    /**
     * NgInsP Negative Inspiratory Pressure
     */
    NLS_NOM_PRESS_AWAY_NEG_MAX,
    /**
     * iPEEP Intrinsic PEEP Breathing Pressure
     */
    NLS_NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC,
    /**
     * AWPin Airway Pressure Wave - measured in the inspiratory path
     */
    NLS_NOM_PRESS_AWAY_INSP,
    /**
     * PIP Positive Inspiratory ressure
     */
    NLS_NOM_PRESS_AWAY_INSP_MAX,
    /**
     * MnAwP Mean Airway Pressure. Printer Context
     */
    NLS_NOM_PRESS_AWAY_INSP_MEAN,
    /**
     * I:E 1: Inpired:Expired Ratio
     */
    NLS_NOM_RATIO_IE,
    /**
     * Vd/Vt Ratio of Deadspace to Tidal Volume Vd/Vt
     */
    NLS_NOM_RATIO_AWAY_DEADSP_TIDAL,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_RES_AWAY,
    /**
     * Rexp Expiratory Resistance
     */
    NLS_NOM_RES_AWAY_EXP,
    /**
     * Rinsp Inspiratory Resistance
     */
    NLS_NOM_RES_AWAY_INSP,
    /**
     * ApneaD Apnea Time
     */
    NLS_NOM_TIME_PD_APNEA,
    /**
     * TV Tidal Volume
     */
    NLS_NOM_VOL_AWAY_TIDAL,
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NLS_NOM_VOL_MINUTE_AWAY,
    /**
     * FICO2 Airway CO2 inspiration
     */
    NLS_NOM_VENT_CONC_AWAY_CO2_INSP,
    /**
     * O2 Generic oxigen measurement label
     */
    NLS_NOM_CONC_AWAY_O2,
    /**
     * DeltaO2 relative Dead Space
     */
    NLS_NOM_VENT_CONC_AWAY_O2_DELTA,
    /**
     * PECO2 Partial O2 Venous
     */
    NLS_NOM_VENT_AWAY_CO2_EXP,
    /**
     * AWFin Airway Flow Wave - measured in the inspiratory path
     */
    NLS_NOM_VENT_FLOW_INSP,
    /**
     * VQI Ventilation Perfusion Index
     */
    NLS_NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX,
    /**
     * Poccl Occlusion Pressure
     */
    NLS_NOM_VENT_PRESS_OCCL,
    /**
     * PEEP Positive End-Expiratory Pressure PEEP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS,
    /**
     * Vd Dead Space Volume Vd
     */
    NLS_NOM_VENT_VOL_AWAY_DEADSP,
    /**
     * relVd relative Dead Space
     */
    NLS_NOM_VENT_VOL_AWAY_DEADSP_REL,
    /**
     * TrpVol Lung Volume Trapped
     */
    NLS_NOM_VENT_VOL_LUNG_TRAPD,
    /**
     * MMV Mandatory Minute Volume
     */
    NLS_NOM_VENT_VOL_MINUTE_AWAY_MAND,
    /**
     * DCO2 High Frequency Gas Transport Coefficient value
     */
    NLS_NOM_COEF_GAS_TRAN,
    /**
     * DES generic Desflurane label
     */
    NLS_NOM_CONC_AWAY_DESFL,
    /**
     * ENF generic Enflurane label
     */
    NLS_NOM_CONC_AWAY_ENFL,
    /**
     * HAL generic Halothane label
     */
    NLS_NOM_CONC_AWAY_HALOTH,
    /**
     * SEV generic Sevoflurane label
     */
    NLS_NOM_CONC_AWAY_SEVOFL,
    /**
     * ISO generic Isoflurane label
     */
    NLS_NOM_CONC_AWAY_ISOFL,
    /**
     * N2O generic Nitrous Oxide label
     */
    NLS_NOM_CONC_AWAY_N2O,
    /**
     * DPosP Duration Above Base Pressure
     */
    NLS_NOM_VENT_TIME_PD_PPV,
    /**
     * PEinsp Respiration Pressure Plateau
     */
    NLS_NOM_VENT_PRESS_RESP_PLAT,
    /**
     * Leak Leakage
     */
    NLS_NOM_VENT_VOL_LEAK,
    /**
     * ALVENT Alveolar Ventilation ALVENT
     */
    NLS_NOM_VENT_VOL_LUNG_ALV,
    /**
     * N2 generic N2 label
     */
    NLS_NOM_CONC_AWAY_N2,
    /**
     * AGT generic Agent label
     */
    NLS_NOM_CONC_AWAY_AGENT,
    /**
     * inAGT Generic Inspired Agent Concentration
     */
    NLS_NOM_CONC_AWAY_AGENT_INSP,
    /**
     * CPP Cerebral Perfusion Pressure
     */
    NLS_NOM_PRESS_CEREB_PERF,
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NLS_NOM_PRESS_INTRA_CRAN,
    /**
     * GCS Glasgow Coma Score
     */
    NLS_NOM_SCORE_GLAS_COMA,
    /**
     * EyeRsp SubScore of the GCS: Eye Response
     */
    NLS_NOM_SCORE_EYE_SUBSC_GLAS_COMA,
    /**
     * MotRsp SubScore of the GCS: Motoric Response
     */
    NLS_NOM_SCORE_MOTOR_SUBSC_GLAS_COMA,
    /**
     * VblRsp SubScore of the GCS: Verbal Response
     */
    NLS_NOM_SCORE_SUBSC_VERBAL_GLAS_COMA,
    /**
     * HC Head Circumferince
     */
    NLS_NOM_CIRCUM_HEAD,
    /**
     * PRL Pupil Reaction Left eye - light reaction of left eye's pupil
     */
    NLS_NOM_TIME_PD_PUPIL_REACT_LEFT,
    /**
     * PRR Pupil Reaction Righteye - light reaction of right eye's pupil
     */
    NLS_NOM_TIME_PD_PUPIL_REACT_RIGHT,
    /**
     * EEG generic EEG and BIS label
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX,
    /**
     * EMG Electromyography
     */
    NLS_NOM_EMG_ELEC_POTL_MUSCL,
    /**
     * MDF Mean Dominant Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN,
    /**
     * PPF Peak Power Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK,
    /**
     * SEF Spectral Edge Frequency
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE,
    /**
     * TP Total Power
     */
    NLS_NOM_EEG_PWR_SPEC_TOT,
    /**
     * UrFl Urimeter - Urine Flow.
     */
    NLS_NOM_FLOW_URINE_INSTANT,
    /**
     * UrVol Urine Volume
     */
    NLS_NOM_VOL_URINE_BAL_PD,
    /**
     * BagVol Current fluid (Urine) in the Urine Bag
     */
    NLS_NOM_VOL_URINE_COL,
    /**
     * AccVol Infusion Pump Accumulated volume. Measured value
     */
    NLS_NOM_VOL_INFUS_ACTUAL_TOTAL,
    /**
     * pHa pH in arterial Blood
     */
    NLS_NOM_CONC_PH_ART,
    /**
     * PaCO2 Partial Pressure of arterial Carbon Dioxide
     */
    NLS_NOM_CONC_PCO2_ART,
    /**
     * PaO2 Partial O2 arterial
     */
    NLS_NOM_CONC_PO2_ART,
    /**
     * Hb Hemoglobin in arterial Blood
     */
    NLS_NOM_CONC_HB_ART,
    /**
     * CaO2 Arterial Oxygen Content CaO2
     */
    NLS_NOM_CONC_HB_O2_ART,
    /**
     * pHv pH in venous Blood
     */
    NLS_NOM_CONC_PH_VEN,
    /**
     * PvCO2 Partial CO2 in the venous blood
     */
    NLS_NOM_CONC_PCO2_VEN,
    /**
     * PvO2 Partial O2 Venous
     */
    NLS_NOM_CONC_PO2_VEN,
    /**
     * CvO2 Venous Oxygen Content
     */
    NLS_NOM_CONC_HB_O2_VEN,
    /**
     * UrpH pH value in the Urine
     */
    NLS_NOM_CONC_PH_URINE,
    /**
     * UrNa Natrium in Urine
     */
    NLS_NOM_CONC_NA_URINE,
    /**
     * SerNa Natrium in Serum
     */
    NLS_NOM_CONC_NA_SERUM,
    /**
     * pH pH in the Blood Plasma
     */
    NLS_NOM_CONC_PH_GEN,
    /**
     * HCO3 Hydrocarbon concentration in Blood Plasma
     */
    NLS_NOM_CONC_HCO3_GEN,
    /**
     * Na Natrium (Sodium)
     */
    NLS_NOM_CONC_NA_GEN,
    /**
     * K Kalium (Potassium)
     */
    NLS_NOM_CONC_K_GEN,
    /**
     * Glu Glucose
     */
    NLS_NOM_CONC_GLU_GEN,
    /**
     * iCa ionized Calcium
     */
    NLS_NOM_CONC_CA_GEN,
    /**
     * PCO2 Partial CO2
     */
    NLS_NOM_CONC_PCO2_GEN,
    /**
     * Cl Chloride
     */
    NLS_NOM_CONC_CHLORIDE_GEN,
    /**
     * BE Base Excess of Blood
     */
    NLS_NOM_BASE_EXCESS_BLD_ART,
    /**
     * PO2 Partial O2.
     */
    NLS_NOM_CONC_PO2_GEN,
    /**
     * Met-Hb MetHemoglobin
     */
    NLS_NOM_CONC_HB_MET_GEN,
    /**
     * CO-Hb Carboxy Hemoglobin
     */
    NLS_NOM_CONC_HB_CO_GEN,
    /**
     * Hct Haematocrit
     */
    NLS_NOM_CONC_HCT_GEN,
    /**
     * FIO2 Fractional Inspired Oxygen FIO2
     */
    NLS_NOM_VENT_CONC_AWAY_O2_INSP,
    /**
     * EctSta ECG Ectopic Status label
     */
    NLS_NOM_ECG_STAT_ECT,
    /**
     * RytSta ECG Rhythm Status label
     */
    NLS_NOM_ECG_STAT_RHY,
    /**
     * IMV Intermittent Mandatory Ventilation
     */
    NLS_NOM_VENT_MODE_MAND_INTERMIT,
    /**
     * Trect Rectal Temperature
     */
    NLS_NOM_TEMP_RECT,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_TEMP_BLD,
    /**
     * DeltaTemp Difference Temperature
     */
    NLS_NOM_TEMP_DIFF,
    /**
     * STindx ST Index
     */
    NLS_NOM_ECG_AMPL_ST_INDEX,
    /**
     * SitTim NOM_DIM_MIN
     */
    NLS_NOM_TIME_TCUT_SENSOR,
    /**
     * SensrT Sensor Temperature
     */
    NLS_NOM_TEMP_TCUT_SENSOR,
    /**
     * ITBV Intrathoracic Blood Volume
     */
    NLS_NOM_VOL_BLD_INTRA_THOR,
    /**
     * ITBVI Intrathoracic Blood Volume Index
     */
    NLS_NOM_VOL_BLD_INTRA_THOR_INDEX,
    /**
     * EVLW Extravascular Lung Water
     */
    NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC,
    /**
     * EVLWI Extravascular Lung Water Index
     */
    NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX,
    /**
     * GEDV Global End Diastolic Volume
     */
    NLS_NOM_VOL_GLOBAL_END_DIA,
    /**
     * GEDVI Global End Diastolic Volume Index
     */
    NLS_NOM_VOL_GLOBAL_END_DIA_INDEX,
    /**
     * CFI Cardiac Function Index
     */
    NLS_NOM_CARD_FUNC_INDEX,
    /**
     * CCI Continuous Cardiac Output Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX_CTS,
    /**
     * SI Stroke Index
     */
    NLS_NOM_VOL_BLD_STROKE_INDEX,
    /**
     * SVV Stroke Volume Variation
     */
    NLS_NOM_VOL_BLD_STROKE_VAR,
    /**
     * SR Suppression Ratio
     */
    NLS_NOM_EEG_RATIO_SUPPRN,
    /**
     * SQI Signal Quality Index
     */
    NLS_NOM_EEG_BIS_SIG_QUAL_INDEX,
    /**
     * BIS Bispectral Index
     */
    NLS_NOM_EEG_BISPECTRAL_INDEX,
    /**
     * tcGas Generic Term for the Transcutaneous Gases
     */
    NLS_NOM_GAS_TCUT,
    /**
     * MAC Airway MAC Concentration
     */
    NLS_NOM_CONC_AWAY_SUM_MAC,
    /**
     * PVRI Pulmonary vascular Resistance PVRI
     */
    NLS_NOM_RES_VASC_PULM_INDEX,
    /**
     * LCWI Left Cardiac Work Index
     */
    NLS_NOM_WK_CARD_LEFT_INDEX,
    /**
     * RCWI Right Cardiac Work Index
     */
    NLS_NOM_WK_CARD_RIGHT_INDEX,
    /**
     * VO2I Oxygen Consumption Index VO2I
     */
    NLS_NOM_SAT_O2_CONSUMP_INDEX,
    /**
     * PB Barometric Pressure = Ambient Pressure
     */
    NLS_NOM_PRESS_AIR_AMBIENT,
    /**
     * Sp-vO2 Difference between Spo2 and SvO2
     */
    NLS_NOM_SAT_DIFF_O2_ART_VEN,
    /**
     * DO2 Oxygen Availability DO2
     */
    NLS_NOM_SAT_O2_DELIVER,
    /**
     * DO2I Oxygen Availability Index
     */
    NLS_NOM_SAT_O2_DELIVER_INDEX,
    /**
     * O2ER Oxygen Extraction Ratio
     */
    NLS_NOM_RATIO_SAT_O2_CONSUMP_DELIVER,
    /**
     * Qs/Qt Percent Alveolarvenous Shunt Qs/Qt
     */
    NLS_NOM_RATIO_ART_VEN_SHUNT,
    /**
     * BSA Body Surface Area
     */
    NLS_NOM_AREA_BODY_SURFACE,
    /**
     * LI Light Intenisty. SvO2
     */
    NLS_NOM_INTENS_LIGHT,
    /**
     * HeatPw NOM_DIM_MILLI_WATT
     */
    NLS_NOM_HEATING_PWR_TCUT_SENSOR,
    /**
     * InjVol Injectate Volume (Cardiac Output)
     */
    NLS_NOM_VOL_INJ,
    /**
     * ETVI ExtraVascular Thermo Volume Index. Cardiac Output.
     */
    NLS_NOM_VOL_THERMO_EXTRA_VASC_INDEX,
    /**
     * CompCt Generic Numeric Calculation Constant
     */
    NLS_NOM_NUM_CALC_CONST,
    /**
     * CathCt Generic Numeric Calculation Constant
     */
    NLS_NOM_NUM_CATHETER_CONST,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_PULS_OXIM_PERF_REL_LEFT,
    /**
     * Perf r Relative Perfusion Right label
     */
    NLS_NOM_PULS_OXIM_PERF_REL_RIGHT,
    /**
     * PLETHr PLETH wave (right)
     */
    NLS_NOM_PULS_OXIM_PLETH_RIGHT,
    /**
     * PLETHl PLETH wave (left)
     */
    NLS_NOM_PULS_OXIM_PLETH_LEFT,
    /**
     * BUN Blood Urea Nitrogen
     */
    NLS_NOM_CONC_BLD_UREA_NITROGEN,
    /**
     * BEecf Base Excess of Extra-Cellular Fluid
     */
    NLS_NOM_CONC_BASE_EXCESS_ECF,
    /**
     * SpMV Spontaneous Minute Volume
     */
    NLS_NOM_VENT_VOL_MINUTE_AWAY_SPONT,
    /**
     * Ca-vO2 Arteriovenous Oxygen Difference Ca-vO2
     */
    NLS_NOM_CONC_DIFF_HB_O2_ATR_VEN,
    /**
     * Weight Patient Weight
     */
    NLS_NOM_PAT_WEIGHT,
    /**
     * Height Patient Height
     */
    NLS_NOM_PAT_HEIGHT,
    /**
     * MAC Minimum Alveolar Concentration
     */
    NLS_NOM_CONC_AWAY_MAC,
    /**
     * PlethT Pleth wave from Telemetry
     */
    NLS_NOM_PULS_OXIM_PLETH_TELE,
    /**
     * %SpO2T SpO2 parameter label as sourced by the Telemetry system
     */
    NLS_NOM_PULS_OXIM_SAT_O2_TELE,
    /**
     * PulseT Pulse parameter label as sourced by the Telemetry system
     */
    NLS_NOM_PULS_OXIM_PULS_RATE_TELE,
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NLS_NOM_PRESS_GEN_1,
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NLS_NOM_PRESS_GEN_2,
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NLS_NOM_PRESS_GEN_3,
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NLS_NOM_PRESS_GEN_4,
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NLS_NOM_PRESS_INTRA_CRAN_1,
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NLS_NOM_PRESS_INTRA_CRAN_2,
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NLS_NOM_PRESS_BLD_ART_FEMORAL,
    /**
     * BAP Brachial Arterial Pressure (BAP)
     */
    NLS_NOM_PRESS_BLD_ART_BRACHIAL,
    /**
     * Tvesic Temperature of the Urine fluid
     */
    NLS_NOM_TEMP_VESICAL,
    /**
     * Tcereb Cerebral Temperature
     */
    NLS_NOM_TEMP_CEREBRAL,
    /**
     * Tamb Ambient Temperature
     */
    NLS_NOM_TEMP_AMBIENT,
    /**
     * T1 Generic Temperature 1 (T1)
     */
    NLS_NOM_TEMP_GEN_1,
    /**
     * T2 Generic Temperature 2 (T2)
     */
    NLS_NOM_TEMP_GEN_2,
    /**
     * T3 Generic Temperature 3 (T3)
     */
    NLS_NOM_TEMP_GEN_3,
    /**
     * T4 Generic Temperature 4 (T4)
     */
    NLS_NOM_TEMP_GEN_4,
    /**
     * IUP Intra-Uterine Pressure
     */
    NLS_NOM_PRESS_INTRA_UTERAL,
    /**
     * TVin inspired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_INSP_TIDAL,
    /**
     * TVexp expired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_EXP_TIDAL,
    /**
     * RRspir Respiration Rate from Spirometry
     */
    NLS_NOM_AWAY_RESP_RATE_SPIRO,
    /**
     * PPV Pulse Pressure Variation
     */
    NLS_NOM_PULS_PRESS_VAR,
    /**
     * Pulse Pulse from NBP
     */
    NLS_NOM_PRESS_BLD_NONINV_PULS_RATE,
    /**
     * MRR Mandatory Respiratory Rate
     */
    NLS_NOM_VENT_RESP_RATE_MAND,
    /**
     * MTV Mandatory Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_MAND,
    /**
     * SpTV Spontaneuous Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_SPONT,
    /**
     * cTnI Cardiac Troponin I
     */
    NLS_NOM_CARDIAC_TROPONIN_I,
    /**
     * CPB Cardio Pulmonary Bypass Flag
     */
    NLS_NOM_CARDIO_PULMONARY_BYPASS_MODE,
    /**
     * BNP Cardiac Brain Natriuretic Peptide
     */
    NLS_NOM_BNP,
    /**
     * PlatTi Plateau Time
     */
    NLS_NOM_TIME_PD_RESP_PLAT,
    /**
     * ScvO2 Central Venous Oxygen Saturation
     */
    NLS_NOM_SAT_O2_VEN_CENT,
    /**
     * SNR Signal to Noise ratio
     */
    NLS_NOM_SNR,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_HUMID,
    /**
     * GEF Global Ejection Fraction
     */
    NLS_NOM_FRACT_EJECT,
    /**
     * PVPI Pulmonary Vascular Permeability Index
     */
    NLS_NOM_PERM_VASC_PULM_INDEX,
    /**
     * pToral Predictive Oral Temperature
     */
    NLS_NOM_TEMP_ORAL_PRED,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_TEMP_RECT_PRED,
    /**
     * pTaxil Predictive Axillary Temperature
     */
    NLS_NOM_TEMP_AXIL_PRED,
    /**
     * Air T Air Temperature in the Incubator
     */
    NLS_NOM_TEMP_AIR_INCUB,
    /**
     * Perf T Perf from Telemetry
     */
    NLS_NOM_PULS_OXIM_PERF_REL_TELE,
    /**
     * RLShnt Right-to-Left Heart Shunt
     */
    NLS_NOM_SHUNT_RIGHT_LEFT,
    /**
     * QT-HR QT HEARTRATE
     */
    NLS_NOM_ECG_TIME_PD_QT_HEART_RATE,
    /**
     * QT Bsl
     */
    NLS_NOM_ECG_TIME_PD_QT_BASELINE,
    /**
     * DeltaQTc
     */
    NLS_NOM_ECG_TIME_PD_QTc_DELTA,
    /**
     * QTHRBl QT BASELINE HEARTRATE
     */
    NLS_NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE,
    /**
     * pHc pH value in the capillaries
     */
    NLS_NOM_CONC_PH_CAP,
    /**
     * PcCO2 Partial CO2 in the capillaries
     */
    NLS_NOM_CONC_PCO2_CAP,
    /**
     * PcO2 Partial O2 in the capillaries
     */
    NLS_NOM_CONC_PO2_CAP,
    /**
     * iMg ionized Magnesium
     */
    NLS_NOM_CONC_MG_ION,
    /**
     * SerMg Magnesium in Serum
     */
    NLS_NOM_CONC_MG_SER,
    /**
     * tSerCa total of Calcium in Serum
     */
    NLS_NOM_CONC_tCA_SER,
    /**
     * SerPho Phosphat in Serum
     */
    NLS_NOM_CONC_P_SER,
    /**
     * SerCl Clorid in Serum
     */
    NLS_NOM_CONC_CHLOR_SER,
    /**
     * Fe Ferrum
     */
    NLS_NOM_CONC_FE_GEN,
    /**
     * SerAlb Albumine in Serum
     */
    NLS_NOM_CONC_ALB_SER,
    /**
     * 'SaO2 Calculated SaO2
     */
    NLS_NOM_SAT_O2_ART_CALC,
    /**
     * HbF Fetal Hemoglobin
     */
    NLS_NOM_CONC_HB_FETAL,
    /**
     * 'SvO2 Calculated SvO2
     */
    NLS_NOM_SAT_O2_VEN_CALC,
    /**
     * Plts Platelets (thrombocyte count)
     */
    NLS_NOM_PLTS_CNT,
    /**
     * WBC White Blood Count (leucocyte count)
     */
    NLS_NOM_WB_CNT,
    /**
     * RBC Red Blood Count (erithrocyte count)
     */
    NLS_NOM_RB_CNT,
    /**
     * RC Reticulocyte Count
     */
    NLS_NOM_RET_CNT,
    /**
     * PlOsm Plasma Osmolarity
     */
    NLS_NOM_PLASMA_OSM,
    /**
     * CreaCl Creatinine Clearance
     */
    NLS_NOM_CONC_CREA_CLR,
    /**
     * NsLoss Nitrogen Balance
     */
    NLS_NOM_NSLOSS,
    /**
     * Chol Cholesterin
     */
    NLS_NOM_CONC_CHOLESTEROL,
    /**
     * TGL Triglyzeride
     */
    NLS_NOM_CONC_TGL,
    /**
     * HDL High Density Lipoprotein
     */
    NLS_NOM_CONC_HDL,
    /**
     * LDL Low Density Lipoprotein
     */
    NLS_NOM_CONC_LDL,
    /**
     * Urea Urea used by the i-Stat
     */
    NLS_NOM_CONC_UREA_GEN,
    /**
     * Crea Creatinine - Measured Value by the i-Stat Module
     */
    NLS_NOM_CONC_CREA,
    /**
     * Lact Lactate. SMeasured value by the i-Stat module
     */
    NLS_NOM_CONC_LACT,
    /**
     * tBili total Bilirubin
     */
    NLS_NOM_CONC_BILI_TOT,
    /**
     * SerPro (Total) Protein in Serum
     */
    NLS_NOM_CONC_PROT_SER,
    /**
     * tPro Total Protein
     */
    NLS_NOM_CONC_PROT_TOT,
    /**
     * dBili direct Bilirubin
     */
    NLS_NOM_CONC_BILI_DIRECT,
    /**
     * LDH Lactate Dehydrogenase
     */
    NLS_NOM_CONC_LDH,
    /**
     * ESR Erithrocyte sedimentation rate
     */
    NLS_NOM_ES_RATE,
    /**
     * PCT Procalcitonin
     */
    NLS_NOM_CONC_PCT,
    /**
     * CK-MM Creatine Cinase of type muscle
     */
    NLS_NOM_CONC_CREA_KIN_MM,
    /**
     * SerCK Creatinin Kinase
     */
    NLS_NOM_CONC_CREA_KIN_SER,
    /**
     * CK-MB Creatine Cinase of type muscle-brain
     */
    NLS_NOM_CONC_CREA_KIN_MB,
    /**
     * CHE Cholesterinesterase
     */
    NLS_NOM_CONC_CHE,
    /**
     * CRP C-reactive Protein
     */
    NLS_NOM_CONC_CRP,
    /**
     * AST Aspartin - Aminotransferase
     */
    NLS_NOM_CONC_AST,
    /**
     * AP Alkalische Phosphatase
     */
    NLS_NOM_CONC_AP,
    /**
     * alphaA Alpha Amylase
     */
    NLS_NOM_CONC_ALPHA_AMYLASE,
    /**
     * GPT Glutamic-Pyruvic-Transaminase
     */
    NLS_NOM_CONC_GPT,
    /**
     * GOT Glutamic Oxaloacetic Transaminase
     */
    NLS_NOM_CONC_GOT,
    /**
     * GGT Gamma GT = Gamma Glutamyltranspeptidase
     */
    NLS_NOM_CONC_GGT,
    /**
     * ACT Activated Clotting Time. Measured value by the i-Stat module
     */
    NLS_NOM_TIME_PD_ACT,
    /**
     * PT Prothrombin Time
     */
    NLS_NOM_TIME_PD_PT,
    /**
     * PT INR Prothrombin Time - International Normalized Ratio
     */
    NLS_NOM_PT_INTL_NORM_RATIO,
    /**
     * aPTTWB aPTT Whole Blood
     */
    NLS_NOM_TIME_PD_aPTT_WB,
    /**
     * aPTTPE aPTT Plasma Equivalent Time
     */
    NLS_NOM_TIME_PD_aPTT_PE,
    /**
     * PT WB Prothrombin Time (Blood)
     */
    NLS_NOM_TIME_PD_PT_WB,
    /**
     * PT PE Prothrombin Time (Plasma)
     */
    NLS_NOM_TIME_PD_PT_PE,
    /**
     * TT Thrombin Time
     */
    NLS_NOM_TIME_PD_THROMBIN,
    /**
     * CT Coagulation Time
     */
    NLS_NOM_TIME_PD_COAGULATION,
    /**
     * Quick Thromboplastine Time
     */
    NLS_NOM_TIME_PD_THROMBOPLAS,
    /**
     * FeNa Fractional Excretion of Sodium
     */
    NLS_NOM_FRACT_EXCR_NA,
    /**
     * UrUrea Urine Urea
     */
    NLS_NOM_CONC_UREA_URINE,
    /**
     * UrCrea Urine Creatinine
     */
    NLS_NOM_CONC_CREA_URINE,
    /**
     * UrK Urine Potassium
     */
    NLS_NOM_CONC_K_URINE,
    /**
     * UrKEx Urinary Potassium Excretion
     */
    NLS_NOM_CONC_K_URINE_EXCR,
    /**
     * UrOsm Urine Osmolarity
     */
    NLS_NOM_CONC_OSM_URINE,
    /**
     * UrCl Clorid in Urine
     */
    NLS_NOM_CONC_CHLOR_URINE,
    /**
     * UrPro (Total) Protein in Urine
     */
    NLS_NOM_CONC_PRO_URINE,
    /**
     * UrCa Calzium in Urine
     */
    NLS_NOM_CONC_CA_URINE,
    /**
     * UrDens Density of the Urine fluid
     */
    NLS_NOM_FLUID_DENS_URINE,
    /**
     * UrHb Hemoglobin (Urine)
     */
    NLS_NOM_CONC_HB_URINE,
    /**
     * UrGlu Glucose in Urine
     */
    NLS_NOM_CONC_GLU_URINE,
    /**
     * 'ScO2 Calculated ScO2
     */
    NLS_NOM_SAT_O2_CAP_CALC,
    /**
     * 'AnGap Calculated AnionGap
     */
    NLS_NOM_CONC_AN_GAP_CALC,
    /**
     * SpO2pr Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL,
    /**
     * SpO2po Oxigen Saturation
     */
    NLS_NOM_PULS_OXIM_SAT_O2_POST_DUCTAL,
    /**
     * PerfPo Relative Perfusion Left
     */
    NLS_NOM_PULS_OXIM_PERF_REL_POST_DUCTAL,
    /**
     * PerfPr Relative Perfusion Left
     */
    NLS_NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL,
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NLS_NOM_PRESS_GEN_5,
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NLS_NOM_PRESS_GEN_6,
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NLS_NOM_PRESS_GEN_7,
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NLS_NOM_PRESS_GEN_8,
    /**
     * Rf-I ST Reference Value for Lead I
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_I,
    /**
     * Rf-II ST Reference Value for Lead II
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_II,
    /**
     * Rf-V1 ST Reference Value for Lead V1
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V1,
    /**
     * Rf-V2 ST Reference Value for Lead V2
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V2,
    /**
     * Rf-V3 ST Reference Value for Lead V3
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V3,
    /**
     * Rf-V4 ST Reference Value for Lead V4
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V4,
    /**
     * Rf-V5 ST Reference Value for Lead V5
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V5,
    /**
     * Rf-V6 ST Reference Value for Lead V6
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_V6,
    /**
     * Rf-III ST Reference Value for Lead III
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_III,
    /**
     * Rf-aVR ST Reference Value for Lead aVR
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVR,
    /**
     * Rf-aVL ST Reference Value for Lead aVL
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVL,
    /**
     * Rf-aVF ST Reference Value for Lead aVF
     */
    NLS_NOM_ECG_AMPL_ST_BASELINE_AVF,
    /**
     * Age actual patient age. measured in years
     */
    NLS_NOM_AGE,
    /**
     * G.Age Gestational age for neonatal
     */
    NLS_NOM_AGE_GEST,
    /**
     * BSA(B) BSA formula: Boyd
     */
    NLS_NOM_AREA_BODY_SURFACE_ACTUAL_BOYD,
    /**
     * BSA(D) BSA formula: Dubois
     */
    NLS_NOM_AREA_BODY_SURFACE_ACTUAL_DUBOIS,
    /**
     * r Correlation Coefficient
     */
    NLS_NOM_AWAY_CORR_COEF,
    /**
     * SpAWRR Spontaneous Airway Respiration Rate
     */
    NLS_NOM_AWAY_RESP_RATE_SPONT,
    /**
     * TC Time Constant
     */
    NLS_NOM_AWAY_TC,
    /**
     * 'BE,B Calculated Base Excess in Blood
     */
    NLS_NOM_BASE_EXCESS_BLD_ART_CALC,
    /**
     * Length Length for neonatal/pediatric
     */
    NLS_NOM_BIRTH_LENGTH,
    /**
     * RSBI Rapid Shallow Breathing Index
     */
    NLS_NOM_BREATH_RAPID_SHALLOW_INDEX,
    /**
     * C20/C Overdistension Index
     */
    NLS_NOM_C20_PER_C_INDEX,
    /**
     * extHR denotes a Heart Rate received from an external device
     */
    NLS_NOM_CARD_BEAT_RATE_EXT,
    /**
     * HI Heart Contractility Index
     */
    NLS_NOM_CARD_CONTRACT_HEATHER_INDEX,
    /**
     * ALP Alveolarproteinose Rosen-Castleman-Liebow- Syndrom
     */
    NLS_NOM_CONC_ALP,
    /**
     * etAGTs EndTidal secondary Anesthetic Agent
     */
    NLS_NOM_CONC_AWAY_AGENT_ET_SEC,
    /**
     * inAGTs Inspired secondary Anesthetic Agent
     */
    NLS_NOM_CONC_AWAY_AGENT_INSP_SEC,
    /**
     * 'BEecf Calculated Base Excess
     */
    NLS_NOM_CONC_BASE_EXCESS_ECF_CALC,
    /**
     * iCa(N) ionized Calcium Normalized
     */
    NLS_NOM_CONC_CA_GEN_NORM,
    /**
     * SerCa Calcium in Serum
     */
    NLS_NOM_CONC_CA_SER,
    /**
     * tCO2 total of CO2 - result of Blood gas Analysis
     */
    NLS_NOM_CONC_CO2_TOT,
    /**
     * 'tCO2 Calculated total CO2
     */
    NLS_NOM_CONC_CO2_TOT_CALC,
    /**
     * SCrea Serum Creatinine
     */
    NLS_NOM_CONC_CREA_SER,
    /**
     * SpRR Spontaneous Respiration Rate
     */
    NLS_NOM_RESP_RATE_SPONT,
    /**
     * SerGlo Globulin in Serum
     */
    NLS_NOM_CONC_GLO_SER,
    /**
     * SerGlu Glucose in Serum
     */
    NLS_NOM_CONC_GLU_SER,
    /**
     * 'Hb Calculated Hemoglobin
     */
    NLS_NOM_CONC_HB_ART_CALC,
    /**
     * MCHC Mean Corpuscular Hemoglobin Concentration
     */
    NLS_NOM_CONC_HB_CORP_MEAN,
    /**
     * 'HCO3 Calculated HCO3
     */
    NLS_NOM_CONC_HCO3_GEN_CALC,
    /**
     * SerK Kalium (Potassium) in Serum
     */
    NLS_NOM_CONC_K_SER,
    /**
     * UrNaEx Urine Sodium Excretion
     */
    NLS_NOM_CONC_NA_EXCR,
    /**
     * &PaCO2 Computed PaCO2 at Patient Temperature on the arterial blood
     */
    NLS_NOM_CONC_PCO2_ART_ADJ,
    /**
     * &PcCO2 Computed PcO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_CAP_ADJ,
    /**
     * &PCO2 Computed PCO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_GEN_ADJ,
    /**
     * &PvCO2 Computed PvCO2 at Patient Temperature
     */
    NLS_NOM_CONC_PCO2_VEN_ADJ,
    /**
     * &pHa Adjusted pH in the arterial Blood
     */
    NLS_NOM_CONC_PH_ART_ADJ,
    /**
     * &pHc Adjusted pH value in the capillaries
     */
    NLS_NOM_CONC_PH_CAP_ADJ,
    /**
     * &pH Adjusted pH at &Patient Temperature
     */
    NLS_NOM_CONC_PH_GEN_ADJ,
    /**
     * &pHv Adjusted pH value in the venous Blood
     */
    NLS_NOM_CONC_PH_VEN_ADJ,
    /**
     * &PaO2 Adjusted PaO2 at Patient Temperature on the arterial blood
     */
    NLS_NOM_CONC_PO2_ART_ADJ,
    /**
     * &PcO2 Adjusted PcO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_CAP_ADJ,
    /**
     * &PO2 Adjusted PO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_GEN_ADJ,
    /**
     * &PvO2 Adjusted PvO2 at Patient Temperature
     */
    NLS_NOM_CONC_PO2_VEN_ADJ,
    /**
     * COsm Osmolar Clearance
     */
    NLS_NOM_CREA_OSM,
    /**
     * BSI Burst Suppression Indicator
     */
    NLS_NOM_EEG_BURST_SUPPRN_INDEX,
    /**
     * LSCALE Scale of the Left Channel EEG wave
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT,
    /**
     * RSCALE Scale of the Right Channel EEG wave
     */
    NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT,
    /**
     * LT MDF Mean Dominant Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_LEFT,
    /**
     * RT MDF Mean Dominant Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_RIGHT,
    /**
     * LT MPF Median Power Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT,
    /**
     * RT MPF Median Power Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT,
    /**
     * LT PPF Peak Power Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_LEFT,
    /**
     * RT PPF Peak Power Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_RIGHT,
    /**
     * LT SEF Spectral Edge Frequency - Left Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_LEFT,
    /**
     * RT SEF Spectral Edge Frequency - Right Side
     */
    NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_RIGHT,
    /**
     * LT AL Absolute Alpha - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT,
    /**
     * RT AL Absolute Alpha - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT,
    /**
     * LT %AL Percent Alpha - Left (LT) Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_LEFT,
    /**
     * RT %AL Percent Alpha - Right (RT) Side
     */
    NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_RIGHT,
    /**
     * LT BE Absolute Beta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_ABS_LEFT,
    /**
     * RT BE Absolute Beta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT,
    /**
     * LT %BE Percent Beta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_REL_LEFT,
    /**
     * RT %BE Percent Beta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_BETA_REL_RIGHT,
    /**
     * LT DL Absolute Delta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT,
    /**
     * RT DL Absolute Delta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT,
    /**
     * LT %DL Percent Delta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_REL_LEFT,
    /**
     * RT %DL Percent Delta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_DELTA_REL_RIGHT,
    /**
     * LT TH Absolute Theta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_ABS_LEFT,
    /**
     * RT TH Absolute Theta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT,
    /**
     * LT %TH Percent Theta - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_REL_LEFT,
    /**
     * RT %TH Percent Theta - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_THETA_REL_RIGHT,
    /**
     * LT TP Total Power - Left Side
     */
    NLS_NOM_EEG_PWR_SPEC_TOT_LEFT,
    /**
     * RT TP Total Power - Right Side
     */
    NLS_NOM_EEG_PWR_SPEC_TOT_RIGHT,
    /**
     * AAI A-Line ARX Index
     */
    NLS_NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI,
    /**
     * O2EI Oxygen Extraction Index
     */
    NLS_NOM_EXTRACT_O2_INDEX,
    /**
     * fgAGT Fresh gas Anesthetic Agent
     */
    NLS_NOM_FLOW_AWAY_AGENT,
    /**
     * fgAir Fresh Gas Flow of Air
     */
    NLS_NOM_FLOW_AWAY_AIR,
    /**
     * fgDES fresh gas agent for DESflurane
     */
    NLS_NOM_FLOW_AWAY_DESFL,
    /**
     * fgENF fresh gas agent for ENFlurane
     */
    NLS_NOM_FLOW_AWAY_ENFL,
    /**
     * eeFlow Expiratory Peak Flow
     */
    NLS_NOM_FLOW_AWAY_EXP_ET,
    /**
     * fgHAL fresh gas agent for HALothane
     */
    NLS_NOM_FLOW_AWAY_HALOTH,
    /**
     * fgISO fresh gas agent for ISOflurane
     */
    NLS_NOM_FLOW_AWAY_ISOFL,
    /**
     * SpPkFl Spontaneous Peak Flow
     */
    NLS_NOM_FLOW_AWAY_MAX_SPONT,
    /**
     * fgN2O N2O concentration in the fresh gas line
     */
    NLS_NOM_FLOW_AWAY_N2O,
    /**
     * fgO2 Oxygen concentration in the fresh gas line
     */
    NLS_NOM_FLOW_AWAY_O2,
    /**
     * fgSEV fresh gas agent for SEVoflurane
     */
    NLS_NOM_FLOW_AWAY_SEVOFL,
    /**
     * fgFlow Total Fresh Gas Flow
     */
    NLS_NOM_FLOW_AWAY_TOT,
    /**
     * VCO2ti CO2 Tidal Production
     */
    NLS_NOM_FLOW_CO2_PROD_RESP_TIDAL,
    /**
     * U/O Daily Urine output
     */
    NLS_NOM_FLOW_URINE_PREV_24HR,
    /**
     * CH2O Free Water Clearance
     */
    NLS_NOM_FREE_WATER_CLR,
    /**
     * MCH Mean Corpuscular Hemoglobin. Is the erithrocyte hemoglobin content
     */
    NLS_NOM_HB_CORP_MEAN,
    /**
     * Power Power requ'd to set the Air&Pat Temp in the incubator
     */
    NLS_NOM_HEATING_PWR_INCUBATOR,
    /**
     * ACI Accelerated Cardiac Index
     */
    NLS_NOM_OUTPUT_CARD_INDEX_ACCEL,
    /**
     * PTC Post Tetatic Count stimulation
     */
    NLS_NOM_PTC_CNT,
    /**
     * PlGain Pleth Gain
     */
    NLS_NOM_PULS_OXIM_PLETH_GAIN,
    /**
     * RVrat Rate Volume Ratio
     */
    NLS_NOM_RATIO_AWAY_RATE_VOL_AWAY,
    /**
     * BUN/cr BUN Creatinine Ratio
     */
    NLS_NOM_RATIO_BUN_CREA,
    /**
     * 'B/Cre Ratio BUN/Creatinine. Calculated value by the i-Stat module
     */
    NLS_NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC,
    /**
     * 'U/Cre Ratio Urea/Creatinine. Calculated value by the i-Stat module
     */
    NLS_NOM_RATIO_CONC_URINE_CREA_CALC,
    /**
     * U/SCr Urine Serum Creatinine Ratio
     */
    NLS_NOM_RATIO_CONC_URINE_CREA_SER,
    /**
     * UrNa/K Urine Sodium/Potassium Ratio
     */
    NLS_NOM_RATIO_CONC_URINE_NA_K,
    /**
     * PaFIO2 PaO2 to FIO2 ratio. Expressed in mmHg to % ratio
     */
    NLS_NOM_RATIO_PaO2_FIO2,
    /**
     * PTrat Prothrombin Time Ratio
     */
    NLS_NOM_RATIO_TIME_PD_PT,
    /**
     * PTTrat Activated Partial Thromboplastin Time Ratio
     */
    NLS_NOM_RATIO_TIME_PD_PTT,
    /**
     * TOFrat Train Of Four (TOF) ratio
     */
    NLS_NOM_RATIO_TRAIN_OF_FOUR,
    /**
     * U/POsm Urine Plasma Osmolarity Ratio
     */
    NLS_NOM_RATIO_URINE_SER_OSM,
    /**
     * Rdyn Dynamic Lung Resistance
     */
    NLS_NOM_RES_AWAY_DYN,
    /**
     * RRsync Sync Breath Rate
     */
    NLS_NOM_RESP_BREATH_ASSIST_CNT,
    /**
     * REF Right Heart Ejection Fraction
     */
    NLS_NOM_RIGHT_HEART_FRACT_EJECT,
    /**
     * 'SO2 Calculated SO2
     */
    NLS_NOM_SAT_O2_CALC,
    /**
     * SO2 l Oxygen Saturation Left Side
     */
    NLS_NOM_SAT_O2_LEFT,
    /**
     * SO2 r Oxygen Saturation Right Side
     */
    NLS_NOM_SAT_O2_RIGHT,
    /**
     * RemTi Remaining Time until next stimulation
     */
    NLS_NOM_TIME_PD_EVOK_REMAIN,
    /**
     * ExpTi Expiratory Time
     */
    NLS_NOM_TIME_PD_EXP,
    /**
     * Elapse Time to Elapse Counter
     */
    NLS_NOM_TIME_PD_FROM_LAST_MSMT,
    /**
     * InsTi Spontaneous Inspiration Time
     */
    NLS_NOM_TIME_PD_INSP,
    /**
     * KCT Kaolin cephalin time
     */
    NLS_NOM_TIME_PD_KAOLIN_CEPHALINE,
    /**
     * PTT Partial Thromboplastin Time
     */
    NLS_NOM_TIME_PD_PTT,
    /**
     * TOF1 TrainOf Four (TOF) first response value TOF1
     */
    NLS_NOM_TRAIN_OF_FOUR_1,
    /**
     * TOF2 TrainOf Four (TOF) first response value TOF2
     */
    NLS_NOM_TRAIN_OF_FOUR_2,
    /**
     * TOF3 TrainOf Four (TOF) first response value TOF3
     */
    NLS_NOM_TRAIN_OF_FOUR_3,
    /**
     * TOF4 TrainOf Four (TOF) first response value TOF4
     */
    NLS_NOM_TRAIN_OF_FOUR_4,
    /**
     * TOFcnt Train Of Four (TOF) count - Number of TOF responses.
     */
    NLS_NOM_TRAIN_OF_FOUR_CNT,
    /**
     * Twitch Twitch height of the 1Hz/0.1Hz stimulation response
     */
    NLS_NOM_TWITCH_AMPL,
    /**
     * SrUrea Serum Urea
     */
    NLS_NOM_UREA_SER,
    /**
     * PtVent Parameter which informs whether the Patient is ventilated
     */
    NLS_NOM_VENT_ACTIVE,
    /**
     * HFVAmp High Frequency Ventilation Amplitude
     */
    NLS_NOM_VENT_AMPL_HFV,
    /**
     * i-eAGT Inspired - EndTidal Agent
     */
    NLS_NOM_VENT_CONC_AWAY_AGENT_DELTA,
    /**
     * i-eDES Inspired - EndTidal Desfluran
     */
    NLS_NOM_VENT_CONC_AWAY_DESFL_DELTA,
    /**
     * i-eENF Inspired - EndTidal Enfluran
     */
    NLS_NOM_VENT_CONC_AWAY_ENFL_DELTA,
    /**
     * i-eHAL Inspired - EndTidal Halothane
     */
    NLS_NOM_VENT_CONC_AWAY_HALOTH_DELTA,
    /**
     * i-eISO Inspired - EndTidal Isofluran
     */
    NLS_NOM_VENT_CONC_AWAY_ISOFL_DELTA,
    /**
     * i-eN2O Inspired - EndTidal N2O
     */
    NLS_NOM_VENT_CONC_AWAY_N2O_DELTA,
    /**
     * cktO2 O2 measured in the Patient Circuit
     */
    NLS_NOM_VENT_CONC_AWAY_O2_CIRCUIT,
    /**
     * i-eSEV Inspired - EndTidal Sevofluran
     */
    NLS_NOM_VENT_CONC_AWAY_SEVOFL_DELTA,
    /**
     * loPEEP Alarm Limit: Low PEEP/CPAP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO,
    /**
     * Pmax Maximum Pressure during a breathing cycle
     */
    NLS_NOM_VENT_PRESS_AWAY_INSP_MAX,
    /**
     * PVcP Pressure Ventilation Control Pressure
     */
    NLS_NOM_VENT_PRESS_AWAY_PV,
    /**
     * RiseTi Rise Time
     */
    NLS_NOM_VENT_TIME_PD_RAMP,
    /**
     * HFTVin Inspired High Frequency Tidal Volume
     */
    NLS_NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV,
    /**
     * HFVTV High Frequency Fraction Ventilation Tidal Volume
     */
    NLS_NOM_VENT_VOL_TIDAL_HFV,
    /**
     * SpTVex Spontaenous Expired Tidal Volume
     */
    NLS_NOM_VOL_AWAY_EXP_TIDAL_SPONT,
    /**
     * TVPSV Tidal Volume (TV) in Pressure Support Ventilation mode
     */
    NLS_NOM_VOL_AWAY_TIDAL_PSV,
    /**
     * MCV Mean Corpuscular Volume
     */
    NLS_NOM_VOL_CORP_MEAN,
    /**
     * TFC Thoracic Fluid Content
     */
    NLS_NOM_VOL_FLUID_THORAC,
    /**
     * TFI Thoracic Fluid Content Index
     */
    NLS_NOM_VOL_FLUID_THORAC_INDEX,
    /**
     * AGTLev Liquid level in the anesthetic agent bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_AGENT,
    /**
     * DESLev Liquid level in the DESflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_DESFL,
    /**
     * ENFLev Liquid level in the ENFlurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ENFL,
    /**
     * HALLev Liquid level in the HALothane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH,
    /**
     * ISOLev Liquid level in the ISOflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL,
    /**
     * SEVLev Liquid level in the SEVoflurane bottle
     */
    NLS_NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL,
    /**
     * HFMVin Inspired High Frequency Mandatory Minute Volume
     */
    NLS_NOM_VOL_MINUTE_AWAY_INSP_HFV,
    /**
     * tUrVol Total Urine Volume of the current measurement period
     */
    NLS_NOM_VOL_URINE_BAL_PD_INSTANT,
    /**
     * UrVSht Urimeter - Urine Shift Volume.
     */
    NLS_NOM_VOL_URINE_SHIFT,
    /**
     * EDVI End Diastolic Volume Index
     */
    NLS_NOM_VOL_VENT_L_END_DIA_INDEX,
    /**
     * ESVI End Systolic Volume Index
     */
    NLS_NOM_VOL_VENT_L_END_SYS_INDEX,
    /**
     * BagWgt Weight of the Urine Disposable Bag
     */
    NLS_NOM_WEIGHT_URINE_COL,
    /**
     * StO2 O2 Saturation (tissue)
     */
    NLS_NOM_SAT_O2_TISSUE,
    /**
     * CSI
     */
    NLS_NOM_CEREB_STATE_INDEX,
    /**
     * SO2 1 O2 Saturation 1 (generic)
     */
    NLS_NOM_SAT_O2_GEN_1,
    /**
     * SO2 2 O2 Saturation 2 (generic)
     */
    NLS_NOM_SAT_O2_GEN_2,
    /**
     * SO2 3 O2 Saturation 3 (generic)
     */
    NLS_NOM_SAT_O2_GEN_3,
    /**
     * SO2 4 O2 Saturation 4 (generic)
     */
    NLS_NOM_SAT_O2_GEN_4,
    /**
     * T1Core Core Temperature 1 (generic)
     */
    NLS_NOM_TEMP_CORE_GEN_1,
    /**
     * T2Core Core Temperature 2 (generic)
     */
    NLS_NOM_TEMP_CORE_GEN_2,
    /**
     * DeltaP Blood Pressure difference
     */
    NLS_NOM_PRESS_BLD_DIFF,
    /**
     * DeltaP1 Blood Pressure difference 1 (generic)
     */
    NLS_NOM_PRESS_BLD_DIFF_GEN_1,
    /**
     * DeltaP2 Blood Pressure difference 2 (generic)
     */
    NLS_NOM_PRESS_BLD_DIFF_GEN_2,
    /**
     * HLMfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_MAIN,
    /**
     * SlvPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_SLAVE,
    /**
     * SucPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_SUCTION,
    /**
     * AuxPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_AUX,
    /**
     * PlePfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * SplPfl
     */
    NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * AxOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START,
    /**
     * AxOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP,
    /**
     * AxDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX,
    /**
     * AxTVol
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX,
    /**
     * AxPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX,
    /**
     * CpOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START,
    /**
     * CpOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP,
    /**
     * CpDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CpTVol
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CpPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CsOnTi
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START,
    /**
     * CsOffT
     */
    NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP,
    /**
     * CsDVol
     */
    NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * CsPlTi
     */
    NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * Tin/Tt
     */
    NLS_NOM_RATIO_INSP_TOTAL_BREATH_SPONT,
    /**
     * tPEEP
     */
    NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL,
    /**
     * Cpav
     */
    NLS_NOM_COMPL_LUNG_PAV,
    /**
     * Rpav
     */
    NLS_NOM_RES_AWAY_PAV,
    /**
     * Rtot
     */
    NLS_NOM_RES_AWAY_EXP_TOTAL,
    /**
     * Epav
     */
    NLS_NOM_ELAS_LUNG_PAV,
    /**
     * RSBInm
     */
    NLS_NOM_BREATH_RAPID_SHALLOW_INDEX_NORM,
    /**
     * P_1 non-specific label for Pressure 1
     */
    NLS_NOM_EMFC_P1,
    /**
     * P_2 non-specific label for Pressure 2
     */
    NLS_NOM_EMFC_P2,
    /**
     * P_3 non-specific label for Pressure 3
     */
    NLS_NOM_EMFC_P3,
    /**
     * P_4 non-specific label for Pressure 4
     */
    NLS_NOM_EMFC_P4,
    /**
     * IUP Intra-Uterine Pressure
     */
    NLS_NOM_EMFC_IUP,
    /**
     * AUX Auxiliary Wave/Parameter
     */
    NLS_NOM_EMFC_AUX,
    /**
     * P_5 non-specific label for Pressure 5
     */
    NLS_NOM_EMFC_P5,
    /**
     * P_6 non-specific label for Pressure 6
     */
    NLS_NOM_EMFC_P6,
    /**
     * P_7 non-specific label for Pressure 7
     */
    NLS_NOM_EMFC_P7,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NLS_NOM_EMFC_P8,
    /**
     * AWV Airway Volume Wave
     */
    NLS_NOM_EMFC_AWV,
    /**
     * L V1 Lead V1 - ECG wave label
     */
    NLS_NOM_EMFC_L_V1,
    /**
     * L V2 Lead V2 - ECG wave label
     */
    NLS_NOM_EMFC_L_V2,
    /**
     * L V3 Lead V3 - ECG wave label
     */
    NLS_NOM_EMFC_L_V3,
    /**
     * L V4 Lead V4 - ECG wave label
     */
    NLS_NOM_EMFC_L_V4,
    /**
     * L V5 Lead V5 - ECG wave label
     */
    NLS_NOM_EMFC_L_V5,
    /**
     * L V6 Lead V6 - ECG wave label
     */
    NLS_NOM_EMFC_L_V6,
    /**
     * L I Lead I - ECG wave label
     */
    NLS_NOM_EMFC_L_I,
    /**
     * L II Lead II - ECG wave label
     */
    NLS_NOM_EMFC_L_II,
    /**
     * L III Lead III - ECG wave label
     */
    NLS_NOM_EMFC_L_III,
    /**
     * L aVR Lead aVR - ECG wave label
     */
    NLS_NOM_EMFC_L_aVR,
    /**
     * L aVL Lead aVL - ECG wave label
     */
    NLS_NOM_EMFC_L_aVL,
    /**
     * L aVF Lead aVF - ECG wave label
     */
    NLS_NOM_EMFC_L_aVF,
    /**
     * AWVex Expiratory Airway Volume Wave. Measured in l.
     */
    NLS_NOM_EMFC_AWVex,
    /**
     * PLETH2 PLETH from the second SpO2/PLETH module
     */
    NLS_NOM_EMFC_PLETH2,
    /**
     * LT EEG Left channel EEG wave
     */
    NLS_NOM_EMFC_LT_EEG,
    /**
     * RT EEG Right channel EEG wave
     */
    NLS_NOM_EMFC_RT_EEG,
    /**
     * BP Unspecified Blood Pressure
     */
    NLS_NOM_EMFC_BP,
    /**
     * AGTs Anesthetic Agent - secondary agent
     */
    NLS_NOM_EMFC_AGTs,
    /**
     * vECG Vector ECG taken from ICG
     */
    NLS_NOM_EMFC_vECG,
    /**
     * ICG Impedance Cardiography
     */
    NLS_NOM_EMFC_ICG,
    /**
     * sTemp Desired Environmental Temperature
     */
    NLS_NOM_SETT_TEMP,
    /**
     * sAWRR Setting: Airway Respiratory Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE,
    /**
     * sRRaw Setting: Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NLS_NOM_SETT_VENT_RESP_RATE,
    /**
     * sPIF Setting: Peak Inspiratory Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP_MAX,
    /**
     * sPmin Setting: Low Inspiratory Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_MIN,
    /**
     * sCPAP Setting: Continuous Positive Airway Pressure Value
     */
    NLS_NOM_SETT_PRESS_AWAY_CTS_POS,
    /**
     * sPin Setting: Pressure Ventilation Control Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP,
    /**
     * sPIP Setting: Positive Inspiratory Pressure
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP_MAX,
    /**
     * sIE 1: Setting: Inspiration to Expiration Ratio.
     */
    NLS_NOM_SETT_RATIO_IE,
    /**
     * sTV Setting: Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL,
    /**
     * sMV Setting: Minute Volume
     */
    NLS_NOM_SETT_VOL_MINUTE_AWAY,
    /**
     * sO2 Enumeration Type - denotes type of Instrument.
     */
    NLS_NOM_SETT_CONC_AWAY_O2,
    /**
     * sPEEP Setting: PEEP/CPAP
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS,
    /**
     * sTrVol Setting: Trigger Flow/Volume
     */
    NLS_NOM_SETT_VENT_VOL_LUNG_TRAPD,
    /**
     * sMMV Setting: Mandatory Minute Volume
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND,
    /**
     * sDES Setting: Vaporizer concentration for DESflurane
     */
    NLS_NOM_SETT_CONC_AWAY_DESFL,
    /**
     * sENF Setting: Vaporizer concentration for ENFlurane
     */
    NLS_NOM_SETT_CONC_AWAY_ENFL,
    /**
     * sHAL Setting: Vaporizer concentration for HALothane
     */
    NLS_NOM_SETT_CONC_AWAY_HALOTH,
    /**
     * sSEV Setting: Vaporizer concentration for SEVoflurane
     */
    NLS_NOM_SETT_CONC_AWAY_SEVOFL,
    /**
     * sISO Setting: Vaporizer concentration for ISOflurane
     */
    NLS_NOM_SETT_CONC_AWAY_ISOFL,
    /**
     * sDRate Setting: Infusion Pump Delivery Rate
     */
    NLS_NOM_SETT_FLOW_FLUID_PUMP,
    /**
     * sFIO2 Setting: Inspired Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP,
    /**
     * sTVin Setting: inspired Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_INSP_TIDAL,
    /**
     * sPltTi Setting: Plateau Time
     */
    NLS_NOM_SETT_TIME_PD_RESP_PLAT,
    /**
     * sAGT Setting: Vaporizer concentration.
     */
    NLS_NOM_SETT_FLOW_AWAY_AGENT,
    /**
     * sfgAir Setting: Total fresh gas Air flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_AIR,
    /**
     * sfgN2O Setting: fresh gas N2O flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_N2O,
    /**
     * sfgO2 Setting: Fresh gas oxygen Flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_O2,
    /**
     * sfgFl Setting: Total fresh gas Flow on the mixer
     */
    NLS_NOM_SETT_FLOW_AWAY_TOT,
    /**
     * sRepTi Setting: Preset Train Of Four (Slow TOF) repetition time
     */
    NLS_NOM_SETT_TIME_PD_TRAIN_OF_FOUR,
    /**
     * sUrTi Setting: Preset period of time for the UrVol numeric
     */
    NLS_NOM_SETT_URINE_BAL_PD,
    /**
     * sPmax Setting: Maximum Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_MAX,
    /**
     * sPSV Setting: Pressure Support Ventilation
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_PV,
    /**
     * sSghTV Setting: Sigh Tidal Volume
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_SIGH,
    /**
     * sAADel Setting: Apnea Ventilation Delay
     */
    NLS_NOM_SETT_APNEA_ALARM_DELAY,
    /**
     * sARR Setting: Apnea Respiration Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE_APNEA,
    /**
     * sHFVRR Setting: High Frequency Ventilation Respiration Rate
     */
    NLS_NOM_SETT_AWAY_RESP_RATE_HFV,
    /**
     * sChrge Setting: Preset stimulation charge
     */
    NLS_NOM_SETT_EVOK_CHARGE,
    /**
     * sCurnt Setting: Preset stimulation current
     */
    NLS_NOM_SETT_EVOK_CURR,
    /**
     * sExpFl Setting: Expiratory Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_EXP,
    /**
     * sHFVFl Setting: High Freqyency Ventilation Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_HFV,
    /**
     * sInsFl Setting: Inspiratory Flow.
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP,
    /**
     * sAPkFl Setting: Apnea Peak Flow
     */
    NLS_NOM_SETT_FLOW_AWAY_INSP_APNEA,
    /**
     * sHFVAm Setting: HFV Amplitude (Peak to Peak Pressure)
     */
    NLS_NOM_SETT_HFV_AMPL,
    /**
     * loPmax Setting: Low Maximum Airway Pressure Alarm Setting.
     */
    NLS_NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO,
    /**
     * sPVE Setting: Pressure Ventilation E component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_EXP_PV,
    /**
     * sAPVE Setting: Apnea Pressure Ventilation E component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_EXP_PV_APNEA,
    /**
     * sPVI Setting: Pressure Ventilation I component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_INSP_PV,
    /**
     * sAPVI Setting: Apnea Pressure Ventilation I component of I:E Ratio
     */
    NLS_NOM_SETT_RATIO_IE_INSP_PV_APNEA,
    /**
     * sSens Setting: Assist Sensitivity. Used by the Bear 1000 ventilator.
     */
    NLS_NOM_SETT_SENS_LEVEL,
    /**
     * sPulsD Setting: Preset stimulation impulse duration
     */
    NLS_NOM_SETT_TIME_PD_EVOK,
    /**
     * sCycTi Setting: Cycle Time
     */
    NLS_NOM_SETT_TIME_PD_MSMT,
    /**
     * sO2Mon Setting: O2 Monitoring
     */
    NLS_NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE,
    /**
     * sBkgFl Setting: Background Flow Setting. Range is 2 - 30 l/min
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_BACKGROUND,
    /**
     * sBasFl Setting: Flow-by Base Flow
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_BASE,
    /**
     * sSenFl Setting: Flow-by Sensitivity Flow
     */
    NLS_NOM_SETT_VENT_AWAY_FLOW_SENSE,
    /**
     * sPincR Setting: Pressure Increase Rate
     */
    NLS_NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE,
    /**
     * sAFIO2 Setting: Apnea Inspired O2 Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA,
    /**
     * sAPVO2 Setting: Apnea Pressure Ventilation Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA,
    /**
     * highO2 Alarm Limit. High Oxygen (O2) Alarm Limit
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI,
    /**
     * lowO2 Alarm Limit: Low Oxygen (O2) Alarm Limit
     */
    NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO,
    /**
     * sFlow Setting: Flow
     */
    NLS_NOM_SETT_VENT_FLOW,
    /**
     * sFlas Setting: Flow Assist level for the CPAP mode
     */
    NLS_NOM_SETT_VENT_FLOW_AWAY_ASSIST,
    /**
     * sTrgFl Setting: Flow Trigger - delivered by the Evita 2 Vuelink Driver
     */
    NLS_NOM_SETT_VENT_FLOW_INSP_TRIG,
    /**
     * sGasPr Setting: Gas Sample point for the oxygen measurement
     */
    NLS_NOM_SETT_VENT_GAS_PROBE_POSN,
    /**
     * sCMV Setting: Controlled mechanical ventilation
     */
    NLS_NOM_SETT_VENT_MODE_MAND_CTS_ONOFF,
    /**
     * sEnSgh Setting: Enable Sigh
     */
    NLS_NOM_SETT_VENT_MODE_SIGH,
    /**
     * sSIMV Setting: Synchronized intermittent mandatory ventilation
     */
    NLS_NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT,
    /**
     * sO2Cal Setting: O2 Calibration
     */
    NLS_NOM_SETT_VENT_O2_CAL_MODE,
    /**
     * sO2Pr Setting: Gas sample point for oxygen measurement
     */
    NLS_NOM_SETT_VENT_O2_PROBE_POSN,
    /**
     * sO2Suc Setting: Suction Oxygen Concentration
     */
    NLS_NOM_SETT_VENT_O2_SUCTION_MODE,
    /**
     * sSPEEP Setting: Pressure Support PEEP
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT,
    /**
     * sPlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_EXP_APRV,
    /**
     * sPhigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_APRV,
    /**
     * highP Alarm Limit: High Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI,
    /**
     * sAPVhP Setting: Apnea Pressure Ventilation High Airway Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA,
    /**
     * sAPVcP Setting: Apnea Pressure Ventilation Control Pressure
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_PV_APNEA,
    /**
     * sustP Alarm Limit: Sustained Pressure Alarm Limit.
     */
    NLS_NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI,
    /**
     * sfmax Setting: Panting Limit
     */
    NLS_NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT,
    /**
     * sIMV Setting: Ventilation Frequency in IMV Mode
     */
    NLS_NOM_SETT_VENT_RESP_RATE_MODE_MAND_INTERMITT,
    /**
     * sIPPV Setting: Ventilation Frequency in IPPV Mode
     */
    NLS_NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP,
    /**
     * sAPVRR Setting: Apnea Pressure Ventilation Respiration Rate
     */
    NLS_NOM_SETT_VENT_RESP_RATE_PV_APNEA,
    /**
     * sSghNr Setting: Multiple Sigh Number
     */
    NLS_NOM_SETT_VENT_SIGH_MULT_RATE,
    /**
     * sSghR Setting: Sigh Rate
     */
    NLS_NOM_SETT_VENT_SIGH_RATE,
    /**
     * sExpTi Setting: Exhaled Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_EXP,
    /**
     * sTlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_TIME_PD_EXP_APRV,
    /**
     * sInsTi Setting: Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP,
    /**
     * sThigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_APRV,
    /**
     * sPVinT Setting: Pressure Ventilation Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_PV,
    /**
     * sAPVTi Setting: Apnea Pressure Ventilation Inspiratory Time
     */
    NLS_NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA,
    /**
     * sALMRT Setting: Alarm Percentage on Rise Time.
     */
    NLS_NOM_SETT_VENT_TIME_PD_RAMP_AL,
    /**
     * sVolas Setting: Volume Assist level for the CPAP mode
     */
    NLS_NOM_SETT_VENT_VOL_AWAY_ASSIST,
    /**
     * sVmax Setting: Volume Warning - delivered by the Evita 2 Vuelink Driver
     */
    NLS_NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF,
    /**
     * highMV Alarm Limit: High Minute Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI,
    /**
     * lowMV Alarm Limit: Low Minute Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO,
    /**
     * highTV Alarm Limit: High Tidal Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI,
    /**
     * lowTV Alarm Limit: Low Tidal Volume Alarm Limit
     */
    NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO,
    /**
     * sATV Setting: Apnea Tidal Volume
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL_APNEA,
    /**
     * sTVap Setting: Applied Tidal Volume.
     */
    NLS_NOM_SETT_VOL_AWAY_TIDAL_APPLIED,
    /**
     * sMVDel Setting: Minute Volume Alarm Delay
     */
    NLS_NOM_SETT_VOL_MINUTE_ALARM_DELAY,
;

    public final long aslong()  {
        switch(this) {
        case NLS_NOM_ECG_ELEC_POTL:
            return 0x00020100L;
        case NLS_NOM_ECG_ELEC_POTL_I:
            return 0x00020101L;
        case NLS_NOM_ECG_ELEC_POTL_II:
            return 0x00020102L;
        case NLS_NOM_ECG_ELEC_POTL_V1:
            return 0x00020103L;
        case NLS_NOM_ECG_ELEC_POTL_V2:
            return 0x00020104L;
        case NLS_NOM_ECG_ELEC_POTL_V3:
            return 0x00020105L;
        case NLS_NOM_ECG_ELEC_POTL_V4:
            return 0x00020106L;
        case NLS_NOM_ECG_ELEC_POTL_V5:
            return 0x00020107L;
        case NLS_NOM_ECG_ELEC_POTL_V6:
            return 0x00020108L;
        case NLS_NOM_ECG_ELEC_POTL_III:
            return 0x0002013DL;
        case NLS_NOM_ECG_ELEC_POTL_AVR:
            return 0x0002013EL;
        case NLS_NOM_ECG_ELEC_POTL_AVL:
            return 0x0002013FL;
        case NLS_NOM_ECG_ELEC_POTL_AVF:
            return 0x00020140L;
        case NLS_NOM_ECG_ELEC_POTL_V:
            return 0x00020143L;
        case NLS_NOM_ECG_ELEC_POTL_MCL:
            return 0x0002014BL;
        case NLS_NOM_ECG_ELEC_POTL_MCL1:
            return 0x0002014CL;
        case NLS_NOM_ECG_AMPL_ST:
            return 0x00020300L;
        case NLS_NOM_ECG_TIME_PD_QT_GL:
            return 0x00023F20L;
        case NLS_NOM_ECG_TIME_PD_QTc:
            return 0x00023F24L;
        case NLS_NOM_ECG_CARD_BEAT_RATE:
            return 0x00024182L;
        case NLS_NOM_ECG_CARD_BEAT_RATE_BTB:
            return 0x0002418AL;
        case NLS_NOM_ECG_V_P_C_CNT:
            return 0x00024261L;
        case NLS_NOM_PULS_RATE:
            return 0x0002480AL;
        case NLS_NOM_PULS_OXIM_PULS_RATE:
            return 0x00024822L;
        case NLS_NOM_RES_VASC_SYS_INDEX:
            return 0x00024900L;
        case NLS_NOM_WK_LV_STROKE_INDEX:
            return 0x00024904L;
        case NLS_NOM_WK_RV_STROKE_INDEX:
            return 0x00024908L;
        case NLS_NOM_OUTPUT_CARD_INDEX:
            return 0x0002490CL;
        case NLS_NOM_PRESS_BLD:
            return 0x00024A00L;
        case NLS_NOM_PRESS_BLD_NONINV:
            return 0x00024A04L;
        case NLS_NOM_PRESS_BLD_AORT:
            return 0x00024A0CL;
        case NLS_NOM_PRESS_BLD_ART:
            return 0x00024A10L;
        case NLS_NOM_PRESS_BLD_ART_ABP:
            return 0x00024A14L;
        case NLS_NOM_PRESS_BLD_ART_PULM:
            return 0x00024A1CL;
        case NLS_NOM_PRESS_BLD_ART_PULM_WEDGE:
            return 0x00024A24L;
        case NLS_NOM_PRESS_BLD_ART_UMB:
            return 0x00024A28L;
        case NLS_NOM_PRESS_BLD_ATR_LEFT:
            return 0x00024A30L;
        case NLS_NOM_PRESS_BLD_ATR_RIGHT:
            return 0x00024A34L;
        case NLS_NOM_PRESS_BLD_VEN_CENT:
            return 0x00024A44L;
        case NLS_NOM_PRESS_BLD_VEN_UMB:
            return 0x00024A48L;
        case NLS_NOM_SAT_O2_CONSUMP:
            return 0x00024B00L;
        case NLS_NOM_OUTPUT_CARD:
            return 0x00024B04L;
        case NLS_NOM_RES_VASC_PULM:
            return 0x00024B24L;
        case NLS_NOM_RES_VASC_SYS:
            return 0x00024B28L;
        case NLS_NOM_SAT_O2:
            return 0x00024B2CL;
        case NLS_NOM_SAT_O2_ART:
            return 0x00024B34L;
        case NLS_NOM_SAT_O2_VEN:
            return 0x00024B3CL;
        case NLS_NOM_SAT_DIFF_O2_ART_ALV:
            return 0x00024B40L;
        case NLS_NOM_TEMP:
            return 0x00024B48L;
        case NLS_NOM_TEMP_ART:
            return 0x00024B50L;
        case NLS_NOM_TEMP_AWAY:
            return 0x00024B54L;
        case NLS_NOM_TEMP_BODY:
            return 0x00024B5CL;
        case NLS_NOM_TEMP_CORE:
            return 0x00024B60L;
        case NLS_NOM_TEMP_ESOPH:
            return 0x00024B64L;
        case NLS_NOM_TEMP_INJ:
            return 0x00024B68L;
        case NLS_NOM_TEMP_NASOPH:
            return 0x00024B6CL;
        case NLS_NOM_TEMP_SKIN:
            return 0x00024B74L;
        case NLS_NOM_TEMP_TYMP:
            return 0x00024B78L;
        case NLS_NOM_TEMP_VEN:
            return 0x00024B7CL;
        case NLS_NOM_VOL_BLD_STROKE:
            return 0x00024B84L;
        case NLS_NOM_WK_CARD_LEFT:
            return 0x00024B90L;
        case NLS_NOM_WK_CARD_RIGHT:
            return 0x00024B94L;
        case NLS_NOM_WK_LV_STROKE:
            return 0x00024B9CL;
        case NLS_NOM_WK_RV_STROKE:
            return 0x00024BA4L;
        case NLS_NOM_PULS_OXIM_PERF_REL:
            return 0x00024BB0L;
        case NLS_NOM_PULS_OXIM_PLETH:
            return 0x00024BB4L;
        case NLS_NOM_PULS_OXIM_SAT_O2:
            return 0x00024BB8L;
        case NLS_NOM_PULS_OXIM_SAT_O2_DIFF:
            return 0x00024BC4L;
        case NLS_NOM_PULS_OXIM_SAT_O2_ART_LEFT:
            return 0x00024BC8L;
        case NLS_NOM_PULS_OXIM_SAT_O2_ART_RIGHT:
            return 0x00024BCCL;
        case NLS_NOM_OUTPUT_CARD_CTS:
            return 0x00024BDCL;
        case NLS_NOM_VOL_VENT_L_END_DIA:
            return 0x00024C00L;
        case NLS_NOM_VOL_VENT_L_END_SYS:
            return 0x00024C04L;
        case NLS_NOM_GRAD_PRESS_BLD_AORT_POS_MAX:
            return 0x00024C25L;
        case NLS_NOM_RESP:
            return 0x00025000L;
        case NLS_NOM_RESP_RATE:
            return 0x0002500AL;
        case NLS_NOM_AWAY_RESP_RATE:
            return 0x00025012L;
        case NLS_NOM_VENT_RESP_RATE:
            return 0x00025022L;
        case NLS_NOM_CAPAC_VITAL:
            return 0x00025080L;
        case NLS_NOM_COMPL_LUNG:
            return 0x00025088L;
        case NLS_NOM_COMPL_LUNG_DYN:
            return 0x0002508CL;
        case NLS_NOM_COMPL_LUNG_STATIC:
            return 0x00025090L;
        case NLS_NOM_AWAY_CO2:
            return 0x000250ACL;
        case NLS_NOM_CO2_TCUT:
            return 0x000250CCL;
        case NLS_NOM_O2_TCUT:
            return 0x000250D0L;
        case NLS_NOM_FLOW_AWAY:
            return 0x000250D4L;
        case NLS_NOM_FLOW_AWAY_EXP_MAX:
            return 0x000250D9L;
        case NLS_NOM_FLOW_AWAY_INSP_MAX:
            return 0x000250DDL;
        case NLS_NOM_FLOW_CO2_PROD_RESP:
            return 0x000250E0L;
        case NLS_NOM_IMPED_TTHOR:
            return 0x000250E4L;
        case NLS_NOM_PRESS_RESP_PLAT:
            return 0x000250E8L;
        case NLS_NOM_PRESS_AWAY:
            return 0x000250F0L;
        case NLS_NOM_PRESS_AWAY_MIN:
            return 0x000250F2L;
        case NLS_NOM_PRESS_AWAY_CTS_POS:
            return 0x000250F4L;
        case NLS_NOM_PRESS_AWAY_NEG_MAX:
            return 0x000250F9L;
        case NLS_NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC:
            return 0x00025100L;
        case NLS_NOM_PRESS_AWAY_INSP:
            return 0x00025108L;
        case NLS_NOM_PRESS_AWAY_INSP_MAX:
            return 0x00025109L;
        case NLS_NOM_PRESS_AWAY_INSP_MEAN:
            return 0x0002510BL;
        case NLS_NOM_RATIO_IE:
            return 0x00025118L;
        case NLS_NOM_RATIO_AWAY_DEADSP_TIDAL:
            return 0x0002511CL;
        case NLS_NOM_RES_AWAY:
            return 0x00025120L;
        case NLS_NOM_RES_AWAY_EXP:
            return 0x00025124L;
        case NLS_NOM_RES_AWAY_INSP:
            return 0x00025128L;
        case NLS_NOM_TIME_PD_APNEA:
            return 0x00025130L;
        case NLS_NOM_VOL_AWAY_TIDAL:
            return 0x0002513CL;
        case NLS_NOM_VOL_MINUTE_AWAY:
            return 0x00025148L;
        case NLS_NOM_VENT_CONC_AWAY_CO2_INSP:
            return 0x00025160L;
        case NLS_NOM_CONC_AWAY_O2:
            return 0x00025164L;
        case NLS_NOM_VENT_CONC_AWAY_O2_DELTA:
            return 0x00025168L;
        case NLS_NOM_VENT_AWAY_CO2_EXP:
            return 0x0002517CL;
        case NLS_NOM_VENT_FLOW_INSP:
            return 0x0002518CL;
        case NLS_NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX:
            return 0x00025190L;
        case NLS_NOM_VENT_PRESS_OCCL:
            return 0x0002519CL;
        case NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS:
            return 0x000251A8L;
        case NLS_NOM_VENT_VOL_AWAY_DEADSP:
            return 0x000251B0L;
        case NLS_NOM_VENT_VOL_AWAY_DEADSP_REL:
            return 0x000251B4L;
        case NLS_NOM_VENT_VOL_LUNG_TRAPD:
            return 0x000251B8L;
        case NLS_NOM_VENT_VOL_MINUTE_AWAY_MAND:
            return 0x000251CCL;
        case NLS_NOM_COEF_GAS_TRAN:
            return 0x000251D4L;
        case NLS_NOM_CONC_AWAY_DESFL:
            return 0x000251D8L;
        case NLS_NOM_CONC_AWAY_ENFL:
            return 0x000251DCL;
        case NLS_NOM_CONC_AWAY_HALOTH:
            return 0x000251E0L;
        case NLS_NOM_CONC_AWAY_SEVOFL:
            return 0x000251E4L;
        case NLS_NOM_CONC_AWAY_ISOFL:
            return 0x000251E8L;
        case NLS_NOM_CONC_AWAY_N2O:
            return 0x000251F0L;
        case NLS_NOM_VENT_TIME_PD_PPV:
            return 0x00025360L;
        case NLS_NOM_VENT_PRESS_RESP_PLAT:
            return 0x00025368L;
        case NLS_NOM_VENT_VOL_LEAK:
            return 0x00025370L;
        case NLS_NOM_VENT_VOL_LUNG_ALV:
            return 0x00025374L;
        case NLS_NOM_CONC_AWAY_N2:
            return 0x0002537CL;
        case NLS_NOM_CONC_AWAY_AGENT:
            return 0x00025388L;
        case NLS_NOM_CONC_AWAY_AGENT_INSP:
            return 0x00025390L;
        case NLS_NOM_PRESS_CEREB_PERF:
            return 0x00025804L;
        case NLS_NOM_PRESS_INTRA_CRAN:
            return 0x00025808L;
        case NLS_NOM_SCORE_GLAS_COMA:
            return 0x00025880L;
        case NLS_NOM_SCORE_EYE_SUBSC_GLAS_COMA:
            return 0x00025882L;
        case NLS_NOM_SCORE_MOTOR_SUBSC_GLAS_COMA:
            return 0x00025883L;
        case NLS_NOM_SCORE_SUBSC_VERBAL_GLAS_COMA:
            return 0x00025884L;
        case NLS_NOM_CIRCUM_HEAD:
            return 0x00025900L;
        case NLS_NOM_TIME_PD_PUPIL_REACT_LEFT:
            return 0x00025924L;
        case NLS_NOM_TIME_PD_PUPIL_REACT_RIGHT:
            return 0x00025928L;
        case NLS_NOM_EEG_ELEC_POTL_CRTX:
            return 0x0002592CL;
        case NLS_NOM_EMG_ELEC_POTL_MUSCL:
            return 0x0002593CL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN:
            return 0x0002597CL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK:
            return 0x00025984L;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE:
            return 0x00025988L;
        case NLS_NOM_EEG_PWR_SPEC_TOT:
            return 0x000259B8L;
        case NLS_NOM_FLOW_URINE_INSTANT:
            return 0x0002680CL;
        case NLS_NOM_VOL_URINE_BAL_PD:
            return 0x00026824L;
        case NLS_NOM_VOL_URINE_COL:
            return 0x00026830L;
        case NLS_NOM_VOL_INFUS_ACTUAL_TOTAL:
            return 0x000268FCL;
        case NLS_NOM_CONC_PH_ART:
            return 0x00027004L;
        case NLS_NOM_CONC_PCO2_ART:
            return 0x00027008L;
        case NLS_NOM_CONC_PO2_ART:
            return 0x0002700CL;
        case NLS_NOM_CONC_HB_ART:
            return 0x00027014L;
        case NLS_NOM_CONC_HB_O2_ART:
            return 0x00027018L;
        case NLS_NOM_CONC_PH_VEN:
            return 0x00027034L;
        case NLS_NOM_CONC_PCO2_VEN:
            return 0x00027038L;
        case NLS_NOM_CONC_PO2_VEN:
            return 0x0002703CL;
        case NLS_NOM_CONC_HB_O2_VEN:
            return 0x00027048L;
        case NLS_NOM_CONC_PH_URINE:
            return 0x00027064L;
        case NLS_NOM_CONC_NA_URINE:
            return 0x0002706CL;
        case NLS_NOM_CONC_NA_SERUM:
            return 0x000270D8L;
        case NLS_NOM_CONC_PH_GEN:
            return 0x00027104L;
        case NLS_NOM_CONC_HCO3_GEN:
            return 0x00027108L;
        case NLS_NOM_CONC_NA_GEN:
            return 0x0002710CL;
        case NLS_NOM_CONC_K_GEN:
            return 0x00027110L;
        case NLS_NOM_CONC_GLU_GEN:
            return 0x00027114L;
        case NLS_NOM_CONC_CA_GEN:
            return 0x00027118L;
        case NLS_NOM_CONC_PCO2_GEN:
            return 0x00027140L;
        case NLS_NOM_CONC_CHLORIDE_GEN:
            return 0x00027168L;
        case NLS_NOM_BASE_EXCESS_BLD_ART:
            return 0x0002716CL;
        case NLS_NOM_CONC_PO2_GEN:
            return 0x00027174L;
        case NLS_NOM_CONC_HB_MET_GEN:
            return 0x0002717CL;
        case NLS_NOM_CONC_HB_CO_GEN:
            return 0x00027180L;
        case NLS_NOM_CONC_HCT_GEN:
            return 0x00027184L;
        case NLS_NOM_VENT_CONC_AWAY_O2_INSP:
            return 0x00027498L;
        case NLS_NOM_ECG_STAT_ECT:
            return 0x0002D006L;
        case NLS_NOM_ECG_STAT_RHY:
            return 0x0002D007L;
        case NLS_NOM_VENT_MODE_MAND_INTERMIT:
            return 0x0002D02AL;
        case NLS_NOM_TEMP_RECT:
            return 0x0002E004L;
        case NLS_NOM_TEMP_BLD:
            return 0x0002E014L;
        case NLS_NOM_TEMP_DIFF:
            return 0x0002E018L;
        case NLS_NOM_ECG_AMPL_ST_INDEX:
            return 0x0002F03DL;
        case NLS_NOM_TIME_TCUT_SENSOR:
            return 0x0002F03EL;
        case NLS_NOM_TEMP_TCUT_SENSOR:
            return 0x0002F03FL;
        case NLS_NOM_VOL_BLD_INTRA_THOR:
            return 0x0002F040L;
        case NLS_NOM_VOL_BLD_INTRA_THOR_INDEX:
            return 0x0002F041L;
        case NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC:
            return 0x0002F042L;
        case NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX:
            return 0x0002F043L;
        case NLS_NOM_VOL_GLOBAL_END_DIA:
            return 0x0002F044L;
        case NLS_NOM_VOL_GLOBAL_END_DIA_INDEX:
            return 0x0002F045L;
        case NLS_NOM_CARD_FUNC_INDEX:
            return 0x0002F046L;
        case NLS_NOM_OUTPUT_CARD_INDEX_CTS:
            return 0x0002F047L;
        case NLS_NOM_VOL_BLD_STROKE_INDEX:
            return 0x0002F048L;
        case NLS_NOM_VOL_BLD_STROKE_VAR:
            return 0x0002F049L;
        case NLS_NOM_EEG_RATIO_SUPPRN:
            return 0x0002F04AL;
        case NLS_NOM_EEG_BIS_SIG_QUAL_INDEX:
            return 0x0002F04DL;
        case NLS_NOM_EEG_BISPECTRAL_INDEX:
            return 0x0002F04EL;
        case NLS_NOM_GAS_TCUT:
            return 0x0002F051L;
        case NLS_NOM_CONC_AWAY_SUM_MAC:
            return 0x0002F05DL;
        case NLS_NOM_RES_VASC_PULM_INDEX:
            return 0x0002F067L;
        case NLS_NOM_WK_CARD_LEFT_INDEX:
            return 0x0002F068L;
        case NLS_NOM_WK_CARD_RIGHT_INDEX:
            return 0x0002F069L;
        case NLS_NOM_SAT_O2_CONSUMP_INDEX:
            return 0x0002F06AL;
        case NLS_NOM_PRESS_AIR_AMBIENT:
            return 0x0002F06BL;
        case NLS_NOM_SAT_DIFF_O2_ART_VEN:
            return 0x0002F06CL;
        case NLS_NOM_SAT_O2_DELIVER:
            return 0x0002F06DL;
        case NLS_NOM_SAT_O2_DELIVER_INDEX:
            return 0x0002F06EL;
        case NLS_NOM_RATIO_SAT_O2_CONSUMP_DELIVER:
            return 0x0002F06FL;
        case NLS_NOM_RATIO_ART_VEN_SHUNT:
            return 0x0002F070L;
        case NLS_NOM_AREA_BODY_SURFACE:
            return 0x0002F071L;
        case NLS_NOM_INTENS_LIGHT:
            return 0x0002F072L;
        case NLS_NOM_HEATING_PWR_TCUT_SENSOR:
            return 0x0002F076L;
        case NLS_NOM_VOL_INJ:
            return 0x0002F079L;
        case NLS_NOM_VOL_THERMO_EXTRA_VASC_INDEX:
            return 0x0002F07AL;
        case NLS_NOM_NUM_CALC_CONST:
            return 0x0002F07BL;
        case NLS_NOM_NUM_CATHETER_CONST:
            return 0x0002F07CL;
        case NLS_NOM_PULS_OXIM_PERF_REL_LEFT:
            return 0x0002F08AL;
        case NLS_NOM_PULS_OXIM_PERF_REL_RIGHT:
            return 0x0002F08BL;
        case NLS_NOM_PULS_OXIM_PLETH_RIGHT:
            return 0x0002F08CL;
        case NLS_NOM_PULS_OXIM_PLETH_LEFT:
            return 0x0002F08DL;
        case NLS_NOM_CONC_BLD_UREA_NITROGEN:
            return 0x0002F08FL;
        case NLS_NOM_CONC_BASE_EXCESS_ECF:
            return 0x0002F090L;
        case NLS_NOM_VENT_VOL_MINUTE_AWAY_SPONT:
            return 0x0002F091L;
        case NLS_NOM_CONC_DIFF_HB_O2_ATR_VEN:
            return 0x0002F092L;
        case NLS_NOM_PAT_WEIGHT:
            return 0x0002F093L;
        case NLS_NOM_PAT_HEIGHT:
            return 0x0002F094L;
        case NLS_NOM_CONC_AWAY_MAC:
            return 0x0002F099L;
        case NLS_NOM_PULS_OXIM_PLETH_TELE:
            return 0x0002F09BL;
        case NLS_NOM_PULS_OXIM_SAT_O2_TELE:
            return 0x0002F09CL;
        case NLS_NOM_PULS_OXIM_PULS_RATE_TELE:
            return 0x0002F09DL;
        case NLS_NOM_PRESS_GEN_1:
            return 0x0002F0A4L;
        case NLS_NOM_PRESS_GEN_2:
            return 0x0002F0A8L;
        case NLS_NOM_PRESS_GEN_3:
            return 0x0002F0ACL;
        case NLS_NOM_PRESS_GEN_4:
            return 0x0002F0B0L;
        case NLS_NOM_PRESS_INTRA_CRAN_1:
            return 0x0002F0B4L;
        case NLS_NOM_PRESS_INTRA_CRAN_2:
            return 0x0002F0B8L;
        case NLS_NOM_PRESS_BLD_ART_FEMORAL:
            return 0x0002F0BCL;
        case NLS_NOM_PRESS_BLD_ART_BRACHIAL:
            return 0x0002F0C0L;
        case NLS_NOM_TEMP_VESICAL:
            return 0x0002F0C4L;
        case NLS_NOM_TEMP_CEREBRAL:
            return 0x0002F0C5L;
        case NLS_NOM_TEMP_AMBIENT:
            return 0x0002F0C6L;
        case NLS_NOM_TEMP_GEN_1:
            return 0x0002F0C7L;
        case NLS_NOM_TEMP_GEN_2:
            return 0x0002F0C8L;
        case NLS_NOM_TEMP_GEN_3:
            return 0x0002F0C9L;
        case NLS_NOM_TEMP_GEN_4:
            return 0x0002F0CAL;
        case NLS_NOM_PRESS_INTRA_UTERAL:
            return 0x0002F0D8L;
        case NLS_NOM_VOL_AWAY_INSP_TIDAL:
            return 0x0002F0E0L;
        case NLS_NOM_VOL_AWAY_EXP_TIDAL:
            return 0x0002F0E1L;
        case NLS_NOM_AWAY_RESP_RATE_SPIRO:
            return 0x0002F0E2L;
        case NLS_NOM_PULS_PRESS_VAR:
            return 0x0002F0E3L;
        case NLS_NOM_PRESS_BLD_NONINV_PULS_RATE:
            return 0x0002F0E5L;
        case NLS_NOM_VENT_RESP_RATE_MAND:
            return 0x0002F0F1L;
        case NLS_NOM_VENT_VOL_TIDAL_MAND:
            return 0x0002F0F2L;
        case NLS_NOM_VENT_VOL_TIDAL_SPONT:
            return 0x0002F0F3L;
        case NLS_NOM_CARDIAC_TROPONIN_I:
            return 0x0002F0F4L;
        case NLS_NOM_CARDIO_PULMONARY_BYPASS_MODE:
            return 0x0002F0F5L;
        case NLS_NOM_BNP:
            return 0x0002F0F6L;
        case NLS_NOM_TIME_PD_RESP_PLAT:
            return 0x0002F0FFL;
        case NLS_NOM_SAT_O2_VEN_CENT:
            return 0x0002F100L;
        case NLS_NOM_SNR:
            return 0x0002F101L;
        case NLS_NOM_HUMID:
            return 0x0002F103L;
        case NLS_NOM_FRACT_EJECT:
            return 0x0002F105L;
        case NLS_NOM_PERM_VASC_PULM_INDEX:
            return 0x0002F106L;
        case NLS_NOM_TEMP_ORAL_PRED:
            return 0x0002F110L;
        case NLS_NOM_TEMP_RECT_PRED:
            return 0x0002F114L;
        case NLS_NOM_TEMP_AXIL_PRED:
            return 0x0002F118L;
        case NLS_NOM_TEMP_AIR_INCUB:
            return 0x0002F12AL;
        case NLS_NOM_PULS_OXIM_PERF_REL_TELE:
            return 0x0002F12CL;
        case NLS_NOM_SHUNT_RIGHT_LEFT:
            return 0x0002F14AL;
        case NLS_NOM_ECG_TIME_PD_QT_HEART_RATE:
            return 0x0002F154L;
        case NLS_NOM_ECG_TIME_PD_QT_BASELINE:
            return 0x0002F155L;
        case NLS_NOM_ECG_TIME_PD_QTc_DELTA:
            return 0x0002F156L;
        case NLS_NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE:
            return 0x0002F157L;
        case NLS_NOM_CONC_PH_CAP:
            return 0x0002F158L;
        case NLS_NOM_CONC_PCO2_CAP:
            return 0x0002F159L;
        case NLS_NOM_CONC_PO2_CAP:
            return 0x0002F15AL;
        case NLS_NOM_CONC_MG_ION:
            return 0x0002F15BL;
        case NLS_NOM_CONC_MG_SER:
            return 0x0002F15CL;
        case NLS_NOM_CONC_tCA_SER:
            return 0x0002F15DL;
        case NLS_NOM_CONC_P_SER:
            return 0x0002F15EL;
        case NLS_NOM_CONC_CHLOR_SER:
            return 0x0002F15FL;
        case NLS_NOM_CONC_FE_GEN:
            return 0x0002F160L;
        case NLS_NOM_CONC_ALB_SER:
            return 0x0002F163L;
        case NLS_NOM_SAT_O2_ART_CALC:
            return 0x0002F164L;
        case NLS_NOM_CONC_HB_FETAL:
            return 0x0002F165L;
        case NLS_NOM_SAT_O2_VEN_CALC:
            return 0x0002F166L;
        case NLS_NOM_PLTS_CNT:
            return 0x0002F167L;
        case NLS_NOM_WB_CNT:
            return 0x0002F168L;
        case NLS_NOM_RB_CNT:
            return 0x0002F169L;
        case NLS_NOM_RET_CNT:
            return 0x0002F16AL;
        case NLS_NOM_PLASMA_OSM:
            return 0x0002F16BL;
        case NLS_NOM_CONC_CREA_CLR:
            return 0x0002F16CL;
        case NLS_NOM_NSLOSS:
            return 0x0002F16DL;
        case NLS_NOM_CONC_CHOLESTEROL:
            return 0x0002F16EL;
        case NLS_NOM_CONC_TGL:
            return 0x0002F16FL;
        case NLS_NOM_CONC_HDL:
            return 0x0002F170L;
        case NLS_NOM_CONC_LDL:
            return 0x0002F171L;
        case NLS_NOM_CONC_UREA_GEN:
            return 0x0002F172L;
        case NLS_NOM_CONC_CREA:
            return 0x0002F173L;
        case NLS_NOM_CONC_LACT:
            return 0x0002F174L;
        case NLS_NOM_CONC_BILI_TOT:
            return 0x0002F177L;
        case NLS_NOM_CONC_PROT_SER:
            return 0x0002F178L;
        case NLS_NOM_CONC_PROT_TOT:
            return 0x0002F179L;
        case NLS_NOM_CONC_BILI_DIRECT:
            return 0x0002F17AL;
        case NLS_NOM_CONC_LDH:
            return 0x0002F17BL;
        case NLS_NOM_ES_RATE:
            return 0x0002F17CL;
        case NLS_NOM_CONC_PCT:
            return 0x0002F17DL;
        case NLS_NOM_CONC_CREA_KIN_MM:
            return 0x0002F17FL;
        case NLS_NOM_CONC_CREA_KIN_SER:
            return 0x0002F180L;
        case NLS_NOM_CONC_CREA_KIN_MB:
            return 0x0002F181L;
        case NLS_NOM_CONC_CHE:
            return 0x0002F182L;
        case NLS_NOM_CONC_CRP:
            return 0x0002F183L;
        case NLS_NOM_CONC_AST:
            return 0x0002F184L;
        case NLS_NOM_CONC_AP:
            return 0x0002F185L;
        case NLS_NOM_CONC_ALPHA_AMYLASE:
            return 0x0002F186L;
        case NLS_NOM_CONC_GPT:
            return 0x0002F187L;
        case NLS_NOM_CONC_GOT:
            return 0x0002F188L;
        case NLS_NOM_CONC_GGT:
            return 0x0002F189L;
        case NLS_NOM_TIME_PD_ACT:
            return 0x0002F18AL;
        case NLS_NOM_TIME_PD_PT:
            return 0x0002F18BL;
        case NLS_NOM_PT_INTL_NORM_RATIO:
            return 0x0002F18CL;
        case NLS_NOM_TIME_PD_aPTT_WB:
            return 0x0002F18DL;
        case NLS_NOM_TIME_PD_aPTT_PE:
            return 0x0002F18EL;
        case NLS_NOM_TIME_PD_PT_WB:
            return 0x0002F18FL;
        case NLS_NOM_TIME_PD_PT_PE:
            return 0x0002F190L;
        case NLS_NOM_TIME_PD_THROMBIN:
            return 0x0002F191L;
        case NLS_NOM_TIME_PD_COAGULATION:
            return 0x0002F192L;
        case NLS_NOM_TIME_PD_THROMBOPLAS:
            return 0x0002F193L;
        case NLS_NOM_FRACT_EXCR_NA:
            return 0x0002F194L;
        case NLS_NOM_CONC_UREA_URINE:
            return 0x0002F195L;
        case NLS_NOM_CONC_CREA_URINE:
            return 0x0002F196L;
        case NLS_NOM_CONC_K_URINE:
            return 0x0002F197L;
        case NLS_NOM_CONC_K_URINE_EXCR:
            return 0x0002F198L;
        case NLS_NOM_CONC_OSM_URINE:
            return 0x0002F199L;
        case NLS_NOM_CONC_CHLOR_URINE:
            return 0x0002F19AL;
        case NLS_NOM_CONC_PRO_URINE:
            return 0x0002F19BL;
        case NLS_NOM_CONC_CA_URINE:
            return 0x0002F19CL;
        case NLS_NOM_FLUID_DENS_URINE:
            return 0x0002F19DL;
        case NLS_NOM_CONC_HB_URINE:
            return 0x0002F19EL;
        case NLS_NOM_CONC_GLU_URINE:
            return 0x0002F19FL;
        case NLS_NOM_SAT_O2_CAP_CALC:
            return 0x0002F1A0L;
        case NLS_NOM_CONC_AN_GAP_CALC:
            return 0x0002F1A1L;
        case NLS_NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL:
            return 0x0002F1C0L;
        case NLS_NOM_PULS_OXIM_SAT_O2_POST_DUCTAL:
            return 0x0002F1D4L;
        case NLS_NOM_PULS_OXIM_PERF_REL_POST_DUCTAL:
            return 0x0002F1DCL;
        case NLS_NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL:
            return 0x0002F22CL;
        case NLS_NOM_PRESS_GEN_5:
            return 0x0002F3F4L;
        case NLS_NOM_PRESS_GEN_6:
            return 0x0002F3F8L;
        case NLS_NOM_PRESS_GEN_7:
            return 0x0002F3FCL;
        case NLS_NOM_PRESS_GEN_8:
            return 0x0002F400L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_I:
            return 0x0002F411L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_II:
            return 0x0002F412L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V1:
            return 0x0002F413L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V2:
            return 0x0002F414L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V3:
            return 0x0002F415L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V4:
            return 0x0002F416L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V5:
            return 0x0002F417L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_V6:
            return 0x0002F418L;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_III:
            return 0x0002F44DL;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_AVR:
            return 0x0002F44EL;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_AVL:
            return 0x0002F44FL;
        case NLS_NOM_ECG_AMPL_ST_BASELINE_AVF:
            return 0x0002F450L;
        case NLS_NOM_AGE:
            return 0x0002F810L;
        case NLS_NOM_AGE_GEST:
            return 0x0002F811L;
        case NLS_NOM_AREA_BODY_SURFACE_ACTUAL_BOYD:
            return 0x0002F812L;
        case NLS_NOM_AREA_BODY_SURFACE_ACTUAL_DUBOIS:
            return 0x0002F813L;
        case NLS_NOM_AWAY_CORR_COEF:
            return 0x0002F814L;
        case NLS_NOM_AWAY_RESP_RATE_SPONT:
            return 0x0002F815L;
        case NLS_NOM_AWAY_TC:
            return 0x0002F816L;
        case NLS_NOM_BASE_EXCESS_BLD_ART_CALC:
            return 0x0002F817L;
        case NLS_NOM_BIRTH_LENGTH:
            return 0x0002F818L;
        case NLS_NOM_BREATH_RAPID_SHALLOW_INDEX:
            return 0x0002F819L;
        case NLS_NOM_C20_PER_C_INDEX:
            return 0x0002F81AL;
        case NLS_NOM_CARD_BEAT_RATE_EXT:
            return 0x0002F81BL;
        case NLS_NOM_CARD_CONTRACT_HEATHER_INDEX:
            return 0x0002F81CL;
        case NLS_NOM_CONC_ALP:
            return 0x0002F81DL;
        case NLS_NOM_CONC_AWAY_AGENT_ET_SEC:
            return 0x0002F81EL;
        case NLS_NOM_CONC_AWAY_AGENT_INSP_SEC:
            return 0x0002F81FL;
        case NLS_NOM_CONC_BASE_EXCESS_ECF_CALC:
            return 0x0002F821L;
        case NLS_NOM_CONC_CA_GEN_NORM:
            return 0x0002F822L;
        case NLS_NOM_CONC_CA_SER:
            return 0x0002F824L;
        case NLS_NOM_CONC_CO2_TOT:
            return 0x0002F825L;
        case NLS_NOM_CONC_CO2_TOT_CALC:
            return 0x0002F826L;
        case NLS_NOM_CONC_CREA_SER:
            return 0x0002F827L;
        case NLS_NOM_RESP_RATE_SPONT:
            return 0x0002F828L;
        case NLS_NOM_CONC_GLO_SER:
            return 0x0002F829L;
        case NLS_NOM_CONC_GLU_SER:
            return 0x0002F82AL;
        case NLS_NOM_CONC_HB_ART_CALC:
            return 0x0002F82BL;
        case NLS_NOM_CONC_HB_CORP_MEAN:
            return 0x0002F82CL;
        case NLS_NOM_CONC_HCO3_GEN_CALC:
            return 0x0002F82EL;
        case NLS_NOM_CONC_K_SER:
            return 0x0002F82FL;
        case NLS_NOM_CONC_NA_EXCR:
            return 0x0002F830L;
        case NLS_NOM_CONC_PCO2_ART_ADJ:
            return 0x0002F832L;
        case NLS_NOM_CONC_PCO2_CAP_ADJ:
            return 0x0002F833L;
        case NLS_NOM_CONC_PCO2_GEN_ADJ:
            return 0x0002F834L;
        case NLS_NOM_CONC_PCO2_VEN_ADJ:
            return 0x0002F835L;
        case NLS_NOM_CONC_PH_ART_ADJ:
            return 0x0002F836L;
        case NLS_NOM_CONC_PH_CAP_ADJ:
            return 0x0002F837L;
        case NLS_NOM_CONC_PH_GEN_ADJ:
            return 0x0002F838L;
        case NLS_NOM_CONC_PH_VEN_ADJ:
            return 0x0002F839L;
        case NLS_NOM_CONC_PO2_ART_ADJ:
            return 0x0002F83BL;
        case NLS_NOM_CONC_PO2_CAP_ADJ:
            return 0x0002F83CL;
        case NLS_NOM_CONC_PO2_GEN_ADJ:
            return 0x0002F83DL;
        case NLS_NOM_CONC_PO2_VEN_ADJ:
            return 0x0002F83EL;
        case NLS_NOM_CREA_OSM:
            return 0x0002F83FL;
        case NLS_NOM_EEG_BURST_SUPPRN_INDEX:
            return 0x0002F840L;
        case NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT:
            return 0x0002F841L;
        case NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT:
            return 0x0002F842L;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_LEFT:
            return 0x0002F849L;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_RIGHT:
            return 0x0002F84AL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT:
            return 0x0002F84BL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT:
            return 0x0002F84CL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_LEFT:
            return 0x0002F84FL;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_RIGHT:
            return 0x0002F850L;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_LEFT:
            return 0x0002F853L;
        case NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_RIGHT:
            return 0x0002F854L;
        case NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT:
            return 0x0002F855L;
        case NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT:
            return 0x0002F856L;
        case NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_LEFT:
            return 0x0002F859L;
        case NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_RIGHT:
            return 0x0002F85AL;
        case NLS_NOM_EEG_PWR_SPEC_BETA_ABS_LEFT:
            return 0x0002F85BL;
        case NLS_NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT:
            return 0x0002F85CL;
        case NLS_NOM_EEG_PWR_SPEC_BETA_REL_LEFT:
            return 0x0002F85FL;
        case NLS_NOM_EEG_PWR_SPEC_BETA_REL_RIGHT:
            return 0x0002F860L;
        case NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT:
            return 0x0002F863L;
        case NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT:
            return 0x0002F864L;
        case NLS_NOM_EEG_PWR_SPEC_DELTA_REL_LEFT:
            return 0x0002F867L;
        case NLS_NOM_EEG_PWR_SPEC_DELTA_REL_RIGHT:
            return 0x0002F868L;
        case NLS_NOM_EEG_PWR_SPEC_THETA_ABS_LEFT:
            return 0x0002F869L;
        case NLS_NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT:
            return 0x0002F86AL;
        case NLS_NOM_EEG_PWR_SPEC_THETA_REL_LEFT:
            return 0x0002F86DL;
        case NLS_NOM_EEG_PWR_SPEC_THETA_REL_RIGHT:
            return 0x0002F86EL;
        case NLS_NOM_EEG_PWR_SPEC_TOT_LEFT:
            return 0x0002F871L;
        case NLS_NOM_EEG_PWR_SPEC_TOT_RIGHT:
            return 0x0002F872L;
        case NLS_NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI:
            return 0x0002F873L;
        case NLS_NOM_EXTRACT_O2_INDEX:
            return 0x0002F875L;
        case NLS_NOM_FLOW_AWAY_AGENT:
            return 0x0002F876L;
        case NLS_NOM_FLOW_AWAY_AIR:
            return 0x0002F877L;
        case NLS_NOM_FLOW_AWAY_DESFL:
            return 0x0002F878L;
        case NLS_NOM_FLOW_AWAY_ENFL:
            return 0x0002F879L;
        case NLS_NOM_FLOW_AWAY_EXP_ET:
            return 0x0002F87AL;
        case NLS_NOM_FLOW_AWAY_HALOTH:
            return 0x0002F87BL;
        case NLS_NOM_FLOW_AWAY_ISOFL:
            return 0x0002F87CL;
        case NLS_NOM_FLOW_AWAY_MAX_SPONT:
            return 0x0002F87DL;
        case NLS_NOM_FLOW_AWAY_N2O:
            return 0x0002F87EL;
        case NLS_NOM_FLOW_AWAY_O2:
            return 0x0002F87FL;
        case NLS_NOM_FLOW_AWAY_SEVOFL:
            return 0x0002F880L;
        case NLS_NOM_FLOW_AWAY_TOT:
            return 0x0002F881L;
        case NLS_NOM_FLOW_CO2_PROD_RESP_TIDAL:
            return 0x0002F882L;
        case NLS_NOM_FLOW_URINE_PREV_24HR:
            return 0x0002F883L;
        case NLS_NOM_FREE_WATER_CLR:
            return 0x0002F884L;
        case NLS_NOM_HB_CORP_MEAN:
            return 0x0002F885L;
        case NLS_NOM_HEATING_PWR_INCUBATOR:
            return 0x0002F886L;
        case NLS_NOM_OUTPUT_CARD_INDEX_ACCEL:
            return 0x0002F889L;
        case NLS_NOM_PTC_CNT:
            return 0x0002F88BL;
        case NLS_NOM_PULS_OXIM_PLETH_GAIN:
            return 0x0002F88DL;
        case NLS_NOM_RATIO_AWAY_RATE_VOL_AWAY:
            return 0x0002F88EL;
        case NLS_NOM_RATIO_BUN_CREA:
            return 0x0002F88FL;
        case NLS_NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC:
            return 0x0002F890L;
        case NLS_NOM_RATIO_CONC_URINE_CREA_CALC:
            return 0x0002F891L;
        case NLS_NOM_RATIO_CONC_URINE_CREA_SER:
            return 0x0002F892L;
        case NLS_NOM_RATIO_CONC_URINE_NA_K:
            return 0x0002F893L;
        case NLS_NOM_RATIO_PaO2_FIO2:
            return 0x0002F894L;
        case NLS_NOM_RATIO_TIME_PD_PT:
            return 0x0002F895L;
        case NLS_NOM_RATIO_TIME_PD_PTT:
            return 0x0002F896L;
        case NLS_NOM_RATIO_TRAIN_OF_FOUR:
            return 0x0002F897L;
        case NLS_NOM_RATIO_URINE_SER_OSM:
            return 0x0002F898L;
        case NLS_NOM_RES_AWAY_DYN:
            return 0x0002F899L;
        case NLS_NOM_RESP_BREATH_ASSIST_CNT:
            return 0x0002F89AL;
        case NLS_NOM_RIGHT_HEART_FRACT_EJECT:
            return 0x0002F89BL;
        case NLS_NOM_SAT_O2_CALC:
            return 0x0002F89CL;
        case NLS_NOM_SAT_O2_LEFT:
            return 0x0002F89DL;
        case NLS_NOM_SAT_O2_RIGHT:
            return 0x0002F89EL;
        case NLS_NOM_TIME_PD_EVOK_REMAIN:
            return 0x0002F8A0L;
        case NLS_NOM_TIME_PD_EXP:
            return 0x0002F8A1L;
        case NLS_NOM_TIME_PD_FROM_LAST_MSMT:
            return 0x0002F8A2L;
        case NLS_NOM_TIME_PD_INSP:
            return 0x0002F8A3L;
        case NLS_NOM_TIME_PD_KAOLIN_CEPHALINE:
            return 0x0002F8A4L;
        case NLS_NOM_TIME_PD_PTT:
            return 0x0002F8A5L;
        case NLS_NOM_TRAIN_OF_FOUR_1:
            return 0x0002F8A7L;
        case NLS_NOM_TRAIN_OF_FOUR_2:
            return 0x0002F8A8L;
        case NLS_NOM_TRAIN_OF_FOUR_3:
            return 0x0002F8A9L;
        case NLS_NOM_TRAIN_OF_FOUR_4:
            return 0x0002F8AAL;
        case NLS_NOM_TRAIN_OF_FOUR_CNT:
            return 0x0002F8ABL;
        case NLS_NOM_TWITCH_AMPL:
            return 0x0002F8ACL;
        case NLS_NOM_UREA_SER:
            return 0x0002F8ADL;
        case NLS_NOM_VENT_ACTIVE:
            return 0x0002F8B0L;
        case NLS_NOM_VENT_AMPL_HFV:
            return 0x0002F8B1L;
        case NLS_NOM_VENT_CONC_AWAY_AGENT_DELTA:
            return 0x0002F8B2L;
        case NLS_NOM_VENT_CONC_AWAY_DESFL_DELTA:
            return 0x0002F8B3L;
        case NLS_NOM_VENT_CONC_AWAY_ENFL_DELTA:
            return 0x0002F8B4L;
        case NLS_NOM_VENT_CONC_AWAY_HALOTH_DELTA:
            return 0x0002F8B5L;
        case NLS_NOM_VENT_CONC_AWAY_ISOFL_DELTA:
            return 0x0002F8B6L;
        case NLS_NOM_VENT_CONC_AWAY_N2O_DELTA:
            return 0x0002F8B7L;
        case NLS_NOM_VENT_CONC_AWAY_O2_CIRCUIT:
            return 0x0002F8B8L;
        case NLS_NOM_VENT_CONC_AWAY_SEVOFL_DELTA:
            return 0x0002F8B9L;
        case NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO:
            return 0x0002F8BAL;
        case NLS_NOM_VENT_PRESS_AWAY_INSP_MAX:
            return 0x0002F8BBL;
        case NLS_NOM_VENT_PRESS_AWAY_PV:
            return 0x0002F8BCL;
        case NLS_NOM_VENT_TIME_PD_RAMP:
            return 0x0002F8BDL;
        case NLS_NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV:
            return 0x0002F8BEL;
        case NLS_NOM_VENT_VOL_TIDAL_HFV:
            return 0x0002F8BFL;
        case NLS_NOM_VOL_AWAY_EXP_TIDAL_SPONT:
            return 0x0002F8C2L;
        case NLS_NOM_VOL_AWAY_TIDAL_PSV:
            return 0x0002F8C3L;
        case NLS_NOM_VOL_CORP_MEAN:
            return 0x0002F8C4L;
        case NLS_NOM_VOL_FLUID_THORAC:
            return 0x0002F8C5L;
        case NLS_NOM_VOL_FLUID_THORAC_INDEX:
            return 0x0002F8C6L;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_AGENT:
            return 0x0002F8C7L;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_DESFL:
            return 0x0002F8C8L;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ENFL:
            return 0x0002F8C9L;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH:
            return 0x0002F8CAL;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL:
            return 0x0002F8CBL;
        case NLS_NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL:
            return 0x0002F8CCL;
        case NLS_NOM_VOL_MINUTE_AWAY_INSP_HFV:
            return 0x0002F8CDL;
        case NLS_NOM_VOL_URINE_BAL_PD_INSTANT:
            return 0x0002F8CEL;
        case NLS_NOM_VOL_URINE_SHIFT:
            return 0x0002F8CFL;
        case NLS_NOM_VOL_VENT_L_END_DIA_INDEX:
            return 0x0002F8D0L;
        case NLS_NOM_VOL_VENT_L_END_SYS_INDEX:
            return 0x0002F8D1L;
        case NLS_NOM_WEIGHT_URINE_COL:
            return 0x0002F8D3L;
        case NLS_NOM_SAT_O2_TISSUE:
            return 0x0002F960L;
        case NLS_NOM_CEREB_STATE_INDEX:
            return 0x0002F961L;
        case NLS_NOM_SAT_O2_GEN_1:
            return 0x0002F962L;
        case NLS_NOM_SAT_O2_GEN_2:
            return 0x0002F963L;
        case NLS_NOM_SAT_O2_GEN_3:
            return 0x0002F964L;
        case NLS_NOM_SAT_O2_GEN_4:
            return 0x0002F965L;
        case NLS_NOM_TEMP_CORE_GEN_1:
            return 0x0002F966L;
        case NLS_NOM_TEMP_CORE_GEN_2:
            return 0x0002F967L;
        case NLS_NOM_PRESS_BLD_DIFF:
            return 0x0002F968L;
        case NLS_NOM_PRESS_BLD_DIFF_GEN_1:
            return 0x0002F96CL;
        case NLS_NOM_PRESS_BLD_DIFF_GEN_2:
            return 0x0002F970L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_MAIN:
            return 0x0002F974L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_SLAVE:
            return 0x0002F975L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_SUCTION:
            return 0x0002F976L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_AUX:
            return 0x0002F977L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0x0002F978L;
        case NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0x0002F979L;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START:
            return 0x0002F97AL;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP:
            return 0x0002F97BL;
        case NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX:
            return 0x0002F97CL;
        case NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX:
            return 0x0002F97DL;
        case NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX:
            return 0x0002F97EL;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START:
            return 0x0002F97FL;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP:
            return 0x0002F980L;
        case NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0x0002F981L;
        case NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0x0002F982L;
        case NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0x0002F983L;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START:
            return 0x0002F984L;
        case NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP:
            return 0x0002F985L;
        case NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0x0002F986L;
        case NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0x0002F987L;
        case NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0x0002F988L;
        case NLS_NOM_RATIO_INSP_TOTAL_BREATH_SPONT:
            return 0x0002F990L;
        case NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL:
            return 0x0002F991L;
        case NLS_NOM_COMPL_LUNG_PAV:
            return 0x0002F992L;
        case NLS_NOM_RES_AWAY_PAV:
            return 0x0002F993L;
        case NLS_NOM_RES_AWAY_EXP_TOTAL:
            return 0x0002F994L;
        case NLS_NOM_ELAS_LUNG_PAV:
            return 0x0002F995L;
        case NLS_NOM_BREATH_RAPID_SHALLOW_INDEX_NORM:
            return 0x0002F996L;
        case NLS_NOM_EMFC_P1:
            return 0x04010030L;
        case NLS_NOM_EMFC_P2:
            return 0x04010034L;
        case NLS_NOM_EMFC_P3:
            return 0x04010038L;
        case NLS_NOM_EMFC_P4:
            return 0x0401003CL;
        case NLS_NOM_EMFC_IUP:
            return 0x04010054L;
        case NLS_NOM_EMFC_AUX:
            return 0x040100B4L;
        case NLS_NOM_EMFC_P5:
            return 0x04010400L;
        case NLS_NOM_EMFC_P6:
            return 0x04010404L;
        case NLS_NOM_EMFC_P7:
            return 0x04010408L;
        case NLS_NOM_EMFC_P8:
            return 0x0401040CL;
        case NLS_NOM_EMFC_AWV:
            return 0x04010668L;
        case NLS_NOM_EMFC_L_V1:
            return 0x04010764L;
        case NLS_NOM_EMFC_L_V2:
            return 0x04010768L;
        case NLS_NOM_EMFC_L_V3:
            return 0x0401076CL;
        case NLS_NOM_EMFC_L_V4:
            return 0x04010770L;
        case NLS_NOM_EMFC_L_V5:
            return 0x04010774L;
        case NLS_NOM_EMFC_L_V6:
            return 0x04010778L;
        case NLS_NOM_EMFC_L_I:
            return 0x0401077CL;
        case NLS_NOM_EMFC_L_II:
            return 0x04010780L;
        case NLS_NOM_EMFC_L_III:
            return 0x04010784L;
        case NLS_NOM_EMFC_L_aVR:
            return 0x04010788L;
        case NLS_NOM_EMFC_L_aVL:
            return 0x0401078CL;
        case NLS_NOM_EMFC_L_aVF:
            return 0x04010790L;
        case NLS_NOM_EMFC_AWVex:
            return 0x04010794L;
        case NLS_NOM_EMFC_PLETH2:
            return 0x0401079CL;
        case NLS_NOM_EMFC_LT_EEG:
            return 0x040107F0L;
        case NLS_NOM_EMFC_RT_EEG:
            return 0x0401082CL;
        case NLS_NOM_EMFC_BP:
            return 0x04010888L;
        case NLS_NOM_EMFC_AGTs:
            return 0x04010CE4L;
        case NLS_NOM_EMFC_vECG:
            return 0x0401119CL;
        case NLS_NOM_EMFC_ICG:
            return 0x040111A0L;
        case NLS_NOM_SETT_TEMP:
            return 0x04024B48L;
        case NLS_NOM_SETT_AWAY_RESP_RATE:
            return 0x04025012L;
        case NLS_NOM_SETT_VENT_RESP_RATE:
            return 0x04025022L;
        case NLS_NOM_SETT_FLOW_AWAY_INSP_MAX:
            return 0x040250DDL;
        case NLS_NOM_SETT_PRESS_AWAY_MIN:
            return 0x040250F2L;
        case NLS_NOM_SETT_PRESS_AWAY_CTS_POS:
            return 0x040250F4L;
        case NLS_NOM_SETT_PRESS_AWAY_INSP:
            return 0x04025108L;
        case NLS_NOM_SETT_PRESS_AWAY_INSP_MAX:
            return 0x04025109L;
        case NLS_NOM_SETT_RATIO_IE:
            return 0x04025118L;
        case NLS_NOM_SETT_VOL_AWAY_TIDAL:
            return 0x0402513CL;
        case NLS_NOM_SETT_VOL_MINUTE_AWAY:
            return 0x04025148L;
        case NLS_NOM_SETT_CONC_AWAY_O2:
            return 0x04025164L;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS:
            return 0x040251A8L;
        case NLS_NOM_SETT_VENT_VOL_LUNG_TRAPD:
            return 0x040251B8L;
        case NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND:
            return 0x040251CCL;
        case NLS_NOM_SETT_CONC_AWAY_DESFL:
            return 0x040251D8L;
        case NLS_NOM_SETT_CONC_AWAY_ENFL:
            return 0x040251DCL;
        case NLS_NOM_SETT_CONC_AWAY_HALOTH:
            return 0x040251E0L;
        case NLS_NOM_SETT_CONC_AWAY_SEVOFL:
            return 0x040251E4L;
        case NLS_NOM_SETT_CONC_AWAY_ISOFL:
            return 0x040251E8L;
        case NLS_NOM_SETT_FLOW_FLUID_PUMP:
            return 0x04026858L;
        case NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP:
            return 0x04027498L;
        case NLS_NOM_SETT_VOL_AWAY_INSP_TIDAL:
            return 0x0402F0E0L;
        case NLS_NOM_SETT_TIME_PD_RESP_PLAT:
            return 0x0402F0FFL;
        case NLS_NOM_SETT_FLOW_AWAY_AGENT:
            return 0x0402F876L;
        case NLS_NOM_SETT_FLOW_AWAY_AIR:
            return 0x0402F877L;
        case NLS_NOM_SETT_FLOW_AWAY_N2O:
            return 0x0402F87EL;
        case NLS_NOM_SETT_FLOW_AWAY_O2:
            return 0x0402F87FL;
        case NLS_NOM_SETT_FLOW_AWAY_TOT:
            return 0x0402F881L;
        case NLS_NOM_SETT_TIME_PD_TRAIN_OF_FOUR:
            return 0x0402F8A6L;
        case NLS_NOM_SETT_URINE_BAL_PD:
            return 0x0402F8AFL;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_MAX:
            return 0x0402F8BBL;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_PV:
            return 0x0402F8BCL;
        case NLS_NOM_SETT_VENT_VOL_TIDAL_SIGH:
            return 0x0402F8C0L;
        case NLS_NOM_SETT_APNEA_ALARM_DELAY:
            return 0x0402F8D9L;
        case NLS_NOM_SETT_AWAY_RESP_RATE_APNEA:
            return 0x0402F8DEL;
        case NLS_NOM_SETT_AWAY_RESP_RATE_HFV:
            return 0x0402F8DFL;
        case NLS_NOM_SETT_EVOK_CHARGE:
            return 0x0402F8E6L;
        case NLS_NOM_SETT_EVOK_CURR:
            return 0x0402F8E7L;
        case NLS_NOM_SETT_FLOW_AWAY_EXP:
            return 0x0402F8EAL;
        case NLS_NOM_SETT_FLOW_AWAY_HFV:
            return 0x0402F8EBL;
        case NLS_NOM_SETT_FLOW_AWAY_INSP:
            return 0x0402F8ECL;
        case NLS_NOM_SETT_FLOW_AWAY_INSP_APNEA:
            return 0x0402F8EDL;
        case NLS_NOM_SETT_HFV_AMPL:
            return 0x0402F8F3L;
        case NLS_NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO:
            return 0x0402F8FBL;
        case NLS_NOM_SETT_RATIO_IE_EXP_PV:
            return 0x0402F900L;
        case NLS_NOM_SETT_RATIO_IE_EXP_PV_APNEA:
            return 0x0402F901L;
        case NLS_NOM_SETT_RATIO_IE_INSP_PV:
            return 0x0402F902L;
        case NLS_NOM_SETT_RATIO_IE_INSP_PV_APNEA:
            return 0x0402F903L;
        case NLS_NOM_SETT_SENS_LEVEL:
            return 0x0402F904L;
        case NLS_NOM_SETT_TIME_PD_EVOK:
            return 0x0402F908L;
        case NLS_NOM_SETT_TIME_PD_MSMT:
            return 0x0402F909L;
        case NLS_NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE:
            return 0x0402F90EL;
        case NLS_NOM_SETT_VENT_AWAY_FLOW_BACKGROUND:
            return 0x0402F90FL;
        case NLS_NOM_SETT_VENT_AWAY_FLOW_BASE:
            return 0x0402F910L;
        case NLS_NOM_SETT_VENT_AWAY_FLOW_SENSE:
            return 0x0402F911L;
        case NLS_NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE:
            return 0x0402F912L;
        case NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA:
            return 0x0402F917L;
        case NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA:
            return 0x0402F918L;
        case NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI:
            return 0x0402F919L;
        case NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO:
            return 0x0402F91AL;
        case NLS_NOM_SETT_VENT_FLOW:
            return 0x0402F91BL;
        case NLS_NOM_SETT_VENT_FLOW_AWAY_ASSIST:
            return 0x0402F91CL;
        case NLS_NOM_SETT_VENT_FLOW_INSP_TRIG:
            return 0x0402F91DL;
        case NLS_NOM_SETT_VENT_GAS_PROBE_POSN:
            return 0x0402F920L;
        case NLS_NOM_SETT_VENT_MODE_MAND_CTS_ONOFF:
            return 0x0402F922L;
        case NLS_NOM_SETT_VENT_MODE_SIGH:
            return 0x0402F923L;
        case NLS_NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT:
            return 0x0402F924L;
        case NLS_NOM_SETT_VENT_O2_CAL_MODE:
            return 0x0402F926L;
        case NLS_NOM_SETT_VENT_O2_PROBE_POSN:
            return 0x0402F927L;
        case NLS_NOM_SETT_VENT_O2_SUCTION_MODE:
            return 0x0402F928L;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT:
            return 0x0402F92CL;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_EXP_APRV:
            return 0x0402F92DL;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_APRV:
            return 0x0402F92EL;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI:
            return 0x0402F930L;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA:
            return 0x0402F931L;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_PV_APNEA:
            return 0x0402F933L;
        case NLS_NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI:
            return 0x0402F935L;
        case NLS_NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT:
            return 0x0402F937L;
        case NLS_NOM_SETT_VENT_RESP_RATE_MODE_MAND_INTERMITT:
            return 0x0402F938L;
        case NLS_NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP:
            return 0x0402F939L;
        case NLS_NOM_SETT_VENT_RESP_RATE_PV_APNEA:
            return 0x0402F93AL;
        case NLS_NOM_SETT_VENT_SIGH_MULT_RATE:
            return 0x0402F93BL;
        case NLS_NOM_SETT_VENT_SIGH_RATE:
            return 0x0402F93CL;
        case NLS_NOM_SETT_VENT_TIME_PD_EXP:
            return 0x0402F93FL;
        case NLS_NOM_SETT_VENT_TIME_PD_EXP_APRV:
            return 0x0402F940L;
        case NLS_NOM_SETT_VENT_TIME_PD_INSP:
            return 0x0402F941L;
        case NLS_NOM_SETT_VENT_TIME_PD_INSP_APRV:
            return 0x0402F942L;
        case NLS_NOM_SETT_VENT_TIME_PD_INSP_PV:
            return 0x0402F943L;
        case NLS_NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA:
            return 0x0402F944L;
        case NLS_NOM_SETT_VENT_TIME_PD_RAMP_AL:
            return 0x0402F946L;
        case NLS_NOM_SETT_VENT_VOL_AWAY_ASSIST:
            return 0x0402F948L;
        case NLS_NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF:
            return 0x0402F949L;
        case NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI:
            return 0x0402F94BL;
        case NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO:
            return 0x0402F94CL;
        case NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI:
            return 0x0402F94DL;
        case NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO:
            return 0x0402F94EL;
        case NLS_NOM_SETT_VOL_AWAY_TIDAL_APNEA:
            return 0x0402F951L;
        case NLS_NOM_SETT_VOL_AWAY_TIDAL_APPLIED:
            return 0x0402F952L;
        case NLS_NOM_SETT_VOL_MINUTE_ALARM_DELAY:
            return 0x0402F953L;
        default:
            throw new IllegalArgumentException("Unknown long:"+this);
        }
    }
    public static final Label valueOf(int x) {
        switch(x) {
        case 0x00020100:
            return NLS_NOM_ECG_ELEC_POTL;
        case 0x00020101:
            return NLS_NOM_ECG_ELEC_POTL_I;
        case 0x00020102:
            return NLS_NOM_ECG_ELEC_POTL_II;
        case 0x00020103:
            return NLS_NOM_ECG_ELEC_POTL_V1;
        case 0x00020104:
            return NLS_NOM_ECG_ELEC_POTL_V2;
        case 0x00020105:
            return NLS_NOM_ECG_ELEC_POTL_V3;
        case 0x00020106:
            return NLS_NOM_ECG_ELEC_POTL_V4;
        case 0x00020107:
            return NLS_NOM_ECG_ELEC_POTL_V5;
        case 0x00020108:
            return NLS_NOM_ECG_ELEC_POTL_V6;
        case 0x0002013D:
            return NLS_NOM_ECG_ELEC_POTL_III;
        case 0x0002013E:
            return NLS_NOM_ECG_ELEC_POTL_AVR;
        case 0x0002013F:
            return NLS_NOM_ECG_ELEC_POTL_AVL;
        case 0x00020140:
            return NLS_NOM_ECG_ELEC_POTL_AVF;
        case 0x00020143:
            return NLS_NOM_ECG_ELEC_POTL_V;
        case 0x0002014B:
            return NLS_NOM_ECG_ELEC_POTL_MCL;
        case 0x0002014C:
            return NLS_NOM_ECG_ELEC_POTL_MCL1;
        case 0x00020300:
            return NLS_NOM_ECG_AMPL_ST;
        case 0x00023F20:
            return NLS_NOM_ECG_TIME_PD_QT_GL;
        case 0x00023F24:
            return NLS_NOM_ECG_TIME_PD_QTc;
        case 0x00024182:
            return NLS_NOM_ECG_CARD_BEAT_RATE;
        case 0x0002418A:
            return NLS_NOM_ECG_CARD_BEAT_RATE_BTB;
        case 0x00024261:
            return NLS_NOM_ECG_V_P_C_CNT;
        case 0x0002480A:
            return NLS_NOM_PULS_RATE;
        case 0x00024822:
            return NLS_NOM_PULS_OXIM_PULS_RATE;
        case 0x00024900:
            return NLS_NOM_RES_VASC_SYS_INDEX;
        case 0x00024904:
            return NLS_NOM_WK_LV_STROKE_INDEX;
        case 0x00024908:
            return NLS_NOM_WK_RV_STROKE_INDEX;
        case 0x0002490C:
            return NLS_NOM_OUTPUT_CARD_INDEX;
        case 0x00024A00:
            return NLS_NOM_PRESS_BLD;
        case 0x00024A04:
            return NLS_NOM_PRESS_BLD_NONINV;
        case 0x00024A0C:
            return NLS_NOM_PRESS_BLD_AORT;
        case 0x00024A10:
            return NLS_NOM_PRESS_BLD_ART;
        case 0x00024A14:
            return NLS_NOM_PRESS_BLD_ART_ABP;
        case 0x00024A1C:
            return NLS_NOM_PRESS_BLD_ART_PULM;
        case 0x00024A24:
            return NLS_NOM_PRESS_BLD_ART_PULM_WEDGE;
        case 0x00024A28:
            return NLS_NOM_PRESS_BLD_ART_UMB;
        case 0x00024A30:
            return NLS_NOM_PRESS_BLD_ATR_LEFT;
        case 0x00024A34:
            return NLS_NOM_PRESS_BLD_ATR_RIGHT;
        case 0x00024A44:
            return NLS_NOM_PRESS_BLD_VEN_CENT;
        case 0x00024A48:
            return NLS_NOM_PRESS_BLD_VEN_UMB;
        case 0x00024B00:
            return NLS_NOM_SAT_O2_CONSUMP;
        case 0x00024B04:
            return NLS_NOM_OUTPUT_CARD;
        case 0x00024B24:
            return NLS_NOM_RES_VASC_PULM;
        case 0x00024B28:
            return NLS_NOM_RES_VASC_SYS;
        case 0x00024B2C:
            return NLS_NOM_SAT_O2;
        case 0x00024B34:
            return NLS_NOM_SAT_O2_ART;
        case 0x00024B3C:
            return NLS_NOM_SAT_O2_VEN;
        case 0x00024B40:
            return NLS_NOM_SAT_DIFF_O2_ART_ALV;
        case 0x00024B48:
            return NLS_NOM_TEMP;
        case 0x00024B50:
            return NLS_NOM_TEMP_ART;
        case 0x00024B54:
            return NLS_NOM_TEMP_AWAY;
        case 0x00024B5C:
            return NLS_NOM_TEMP_BODY;
        case 0x00024B60:
            return NLS_NOM_TEMP_CORE;
        case 0x00024B64:
            return NLS_NOM_TEMP_ESOPH;
        case 0x00024B68:
            return NLS_NOM_TEMP_INJ;
        case 0x00024B6C:
            return NLS_NOM_TEMP_NASOPH;
        case 0x00024B74:
            return NLS_NOM_TEMP_SKIN;
        case 0x00024B78:
            return NLS_NOM_TEMP_TYMP;
        case 0x00024B7C:
            return NLS_NOM_TEMP_VEN;
        case 0x00024B84:
            return NLS_NOM_VOL_BLD_STROKE;
        case 0x00024B90:
            return NLS_NOM_WK_CARD_LEFT;
        case 0x00024B94:
            return NLS_NOM_WK_CARD_RIGHT;
        case 0x00024B9C:
            return NLS_NOM_WK_LV_STROKE;
        case 0x00024BA4:
            return NLS_NOM_WK_RV_STROKE;
        case 0x00024BB0:
            return NLS_NOM_PULS_OXIM_PERF_REL;
        case 0x00024BB4:
            return NLS_NOM_PULS_OXIM_PLETH;
        case 0x00024BB8:
            return NLS_NOM_PULS_OXIM_SAT_O2;
        case 0x00024BC4:
            return NLS_NOM_PULS_OXIM_SAT_O2_DIFF;
        case 0x00024BC8:
            return NLS_NOM_PULS_OXIM_SAT_O2_ART_LEFT;
        case 0x00024BCC:
            return NLS_NOM_PULS_OXIM_SAT_O2_ART_RIGHT;
        case 0x00024BDC:
            return NLS_NOM_OUTPUT_CARD_CTS;
        case 0x00024C00:
            return NLS_NOM_VOL_VENT_L_END_DIA;
        case 0x00024C04:
            return NLS_NOM_VOL_VENT_L_END_SYS;
        case 0x00024C25:
            return NLS_NOM_GRAD_PRESS_BLD_AORT_POS_MAX;
        case 0x00025000:
            return NLS_NOM_RESP;
        case 0x0002500A:
            return NLS_NOM_RESP_RATE;
        case 0x00025012:
            return NLS_NOM_AWAY_RESP_RATE;
        case 0x00025022:
            return NLS_NOM_VENT_RESP_RATE;
        case 0x00025080:
            return NLS_NOM_CAPAC_VITAL;
        case 0x00025088:
            return NLS_NOM_COMPL_LUNG;
        case 0x0002508C:
            return NLS_NOM_COMPL_LUNG_DYN;
        case 0x00025090:
            return NLS_NOM_COMPL_LUNG_STATIC;
        case 0x000250AC:
            return NLS_NOM_AWAY_CO2;
        case 0x000250CC:
            return NLS_NOM_CO2_TCUT;
        case 0x000250D0:
            return NLS_NOM_O2_TCUT;
        case 0x000250D4:
            return NLS_NOM_FLOW_AWAY;
        case 0x000250D9:
            return NLS_NOM_FLOW_AWAY_EXP_MAX;
        case 0x000250DD:
            return NLS_NOM_FLOW_AWAY_INSP_MAX;
        case 0x000250E0:
            return NLS_NOM_FLOW_CO2_PROD_RESP;
        case 0x000250E4:
            return NLS_NOM_IMPED_TTHOR;
        case 0x000250E8:
            return NLS_NOM_PRESS_RESP_PLAT;
        case 0x000250F0:
            return NLS_NOM_PRESS_AWAY;
        case 0x000250F2:
            return NLS_NOM_PRESS_AWAY_MIN;
        case 0x000250F4:
            return NLS_NOM_PRESS_AWAY_CTS_POS;
        case 0x000250F9:
            return NLS_NOM_PRESS_AWAY_NEG_MAX;
        case 0x00025100:
            return NLS_NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC;
        case 0x00025108:
            return NLS_NOM_PRESS_AWAY_INSP;
        case 0x00025109:
            return NLS_NOM_PRESS_AWAY_INSP_MAX;
        case 0x0002510B:
            return NLS_NOM_PRESS_AWAY_INSP_MEAN;
        case 0x00025118:
            return NLS_NOM_RATIO_IE;
        case 0x0002511C:
            return NLS_NOM_RATIO_AWAY_DEADSP_TIDAL;
        case 0x00025120:
            return NLS_NOM_RES_AWAY;
        case 0x00025124:
            return NLS_NOM_RES_AWAY_EXP;
        case 0x00025128:
            return NLS_NOM_RES_AWAY_INSP;
        case 0x00025130:
            return NLS_NOM_TIME_PD_APNEA;
        case 0x0002513C:
            return NLS_NOM_VOL_AWAY_TIDAL;
        case 0x00025148:
            return NLS_NOM_VOL_MINUTE_AWAY;
        case 0x00025160:
            return NLS_NOM_VENT_CONC_AWAY_CO2_INSP;
        case 0x00025164:
            return NLS_NOM_CONC_AWAY_O2;
        case 0x00025168:
            return NLS_NOM_VENT_CONC_AWAY_O2_DELTA;
        case 0x0002517C:
            return NLS_NOM_VENT_AWAY_CO2_EXP;
        case 0x0002518C:
            return NLS_NOM_VENT_FLOW_INSP;
        case 0x00025190:
            return NLS_NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX;
        case 0x0002519C:
            return NLS_NOM_VENT_PRESS_OCCL;
        case 0x000251A8:
            return NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS;
        case 0x000251B0:
            return NLS_NOM_VENT_VOL_AWAY_DEADSP;
        case 0x000251B4:
            return NLS_NOM_VENT_VOL_AWAY_DEADSP_REL;
        case 0x000251B8:
            return NLS_NOM_VENT_VOL_LUNG_TRAPD;
        case 0x000251CC:
            return NLS_NOM_VENT_VOL_MINUTE_AWAY_MAND;
        case 0x000251D4:
            return NLS_NOM_COEF_GAS_TRAN;
        case 0x000251D8:
            return NLS_NOM_CONC_AWAY_DESFL;
        case 0x000251DC:
            return NLS_NOM_CONC_AWAY_ENFL;
        case 0x000251E0:
            return NLS_NOM_CONC_AWAY_HALOTH;
        case 0x000251E4:
            return NLS_NOM_CONC_AWAY_SEVOFL;
        case 0x000251E8:
            return NLS_NOM_CONC_AWAY_ISOFL;
        case 0x000251F0:
            return NLS_NOM_CONC_AWAY_N2O;
        case 0x00025360:
            return NLS_NOM_VENT_TIME_PD_PPV;
        case 0x00025368:
            return NLS_NOM_VENT_PRESS_RESP_PLAT;
        case 0x00025370:
            return NLS_NOM_VENT_VOL_LEAK;
        case 0x00025374:
            return NLS_NOM_VENT_VOL_LUNG_ALV;
        case 0x0002537C:
            return NLS_NOM_CONC_AWAY_N2;
        case 0x00025388:
            return NLS_NOM_CONC_AWAY_AGENT;
        case 0x00025390:
            return NLS_NOM_CONC_AWAY_AGENT_INSP;
        case 0x00025804:
            return NLS_NOM_PRESS_CEREB_PERF;
        case 0x00025808:
            return NLS_NOM_PRESS_INTRA_CRAN;
        case 0x00025880:
            return NLS_NOM_SCORE_GLAS_COMA;
        case 0x00025882:
            return NLS_NOM_SCORE_EYE_SUBSC_GLAS_COMA;
        case 0x00025883:
            return NLS_NOM_SCORE_MOTOR_SUBSC_GLAS_COMA;
        case 0x00025884:
            return NLS_NOM_SCORE_SUBSC_VERBAL_GLAS_COMA;
        case 0x00025900:
            return NLS_NOM_CIRCUM_HEAD;
        case 0x00025924:
            return NLS_NOM_TIME_PD_PUPIL_REACT_LEFT;
        case 0x00025928:
            return NLS_NOM_TIME_PD_PUPIL_REACT_RIGHT;
        case 0x0002592C:
            return NLS_NOM_EEG_ELEC_POTL_CRTX;
        case 0x0002593C:
            return NLS_NOM_EMG_ELEC_POTL_MUSCL;
        case 0x0002597C:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN;
        case 0x00025984:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK;
        case 0x00025988:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE;
        case 0x000259B8:
            return NLS_NOM_EEG_PWR_SPEC_TOT;
        case 0x0002680C:
            return NLS_NOM_FLOW_URINE_INSTANT;
        case 0x00026824:
            return NLS_NOM_VOL_URINE_BAL_PD;
        case 0x00026830:
            return NLS_NOM_VOL_URINE_COL;
        case 0x000268FC:
            return NLS_NOM_VOL_INFUS_ACTUAL_TOTAL;
        case 0x00027004:
            return NLS_NOM_CONC_PH_ART;
        case 0x00027008:
            return NLS_NOM_CONC_PCO2_ART;
        case 0x0002700C:
            return NLS_NOM_CONC_PO2_ART;
        case 0x00027014:
            return NLS_NOM_CONC_HB_ART;
        case 0x00027018:
            return NLS_NOM_CONC_HB_O2_ART;
        case 0x00027034:
            return NLS_NOM_CONC_PH_VEN;
        case 0x00027038:
            return NLS_NOM_CONC_PCO2_VEN;
        case 0x0002703C:
            return NLS_NOM_CONC_PO2_VEN;
        case 0x00027048:
            return NLS_NOM_CONC_HB_O2_VEN;
        case 0x00027064:
            return NLS_NOM_CONC_PH_URINE;
        case 0x0002706C:
            return NLS_NOM_CONC_NA_URINE;
        case 0x000270D8:
            return NLS_NOM_CONC_NA_SERUM;
        case 0x00027104:
            return NLS_NOM_CONC_PH_GEN;
        case 0x00027108:
            return NLS_NOM_CONC_HCO3_GEN;
        case 0x0002710C:
            return NLS_NOM_CONC_NA_GEN;
        case 0x00027110:
            return NLS_NOM_CONC_K_GEN;
        case 0x00027114:
            return NLS_NOM_CONC_GLU_GEN;
        case 0x00027118:
            return NLS_NOM_CONC_CA_GEN;
        case 0x00027140:
            return NLS_NOM_CONC_PCO2_GEN;
        case 0x00027168:
            return NLS_NOM_CONC_CHLORIDE_GEN;
        case 0x0002716C:
            return NLS_NOM_BASE_EXCESS_BLD_ART;
        case 0x00027174:
            return NLS_NOM_CONC_PO2_GEN;
        case 0x0002717C:
            return NLS_NOM_CONC_HB_MET_GEN;
        case 0x00027180:
            return NLS_NOM_CONC_HB_CO_GEN;
        case 0x00027184:
            return NLS_NOM_CONC_HCT_GEN;
        case 0x00027498:
            return NLS_NOM_VENT_CONC_AWAY_O2_INSP;
        case 0x0002D006:
            return NLS_NOM_ECG_STAT_ECT;
        case 0x0002D007:
            return NLS_NOM_ECG_STAT_RHY;
        case 0x0002D02A:
            return NLS_NOM_VENT_MODE_MAND_INTERMIT;
        case 0x0002E004:
            return NLS_NOM_TEMP_RECT;
        case 0x0002E014:
            return NLS_NOM_TEMP_BLD;
        case 0x0002E018:
            return NLS_NOM_TEMP_DIFF;
        case 0x0002F03D:
            return NLS_NOM_ECG_AMPL_ST_INDEX;
        case 0x0002F03E:
            return NLS_NOM_TIME_TCUT_SENSOR;
        case 0x0002F03F:
            return NLS_NOM_TEMP_TCUT_SENSOR;
        case 0x0002F040:
            return NLS_NOM_VOL_BLD_INTRA_THOR;
        case 0x0002F041:
            return NLS_NOM_VOL_BLD_INTRA_THOR_INDEX;
        case 0x0002F042:
            return NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC;
        case 0x0002F043:
            return NLS_NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX;
        case 0x0002F044:
            return NLS_NOM_VOL_GLOBAL_END_DIA;
        case 0x0002F045:
            return NLS_NOM_VOL_GLOBAL_END_DIA_INDEX;
        case 0x0002F046:
            return NLS_NOM_CARD_FUNC_INDEX;
        case 0x0002F047:
            return NLS_NOM_OUTPUT_CARD_INDEX_CTS;
        case 0x0002F048:
            return NLS_NOM_VOL_BLD_STROKE_INDEX;
        case 0x0002F049:
            return NLS_NOM_VOL_BLD_STROKE_VAR;
        case 0x0002F04A:
            return NLS_NOM_EEG_RATIO_SUPPRN;
        case 0x0002F04D:
            return NLS_NOM_EEG_BIS_SIG_QUAL_INDEX;
        case 0x0002F04E:
            return NLS_NOM_EEG_BISPECTRAL_INDEX;
        case 0x0002F051:
            return NLS_NOM_GAS_TCUT;
        case 0x0002F05D:
            return NLS_NOM_CONC_AWAY_SUM_MAC;
        case 0x0002F067:
            return NLS_NOM_RES_VASC_PULM_INDEX;
        case 0x0002F068:
            return NLS_NOM_WK_CARD_LEFT_INDEX;
        case 0x0002F069:
            return NLS_NOM_WK_CARD_RIGHT_INDEX;
        case 0x0002F06A:
            return NLS_NOM_SAT_O2_CONSUMP_INDEX;
        case 0x0002F06B:
            return NLS_NOM_PRESS_AIR_AMBIENT;
        case 0x0002F06C:
            return NLS_NOM_SAT_DIFF_O2_ART_VEN;
        case 0x0002F06D:
            return NLS_NOM_SAT_O2_DELIVER;
        case 0x0002F06E:
            return NLS_NOM_SAT_O2_DELIVER_INDEX;
        case 0x0002F06F:
            return NLS_NOM_RATIO_SAT_O2_CONSUMP_DELIVER;
        case 0x0002F070:
            return NLS_NOM_RATIO_ART_VEN_SHUNT;
        case 0x0002F071:
            return NLS_NOM_AREA_BODY_SURFACE;
        case 0x0002F072:
            return NLS_NOM_INTENS_LIGHT;
        case 0x0002F076:
            return NLS_NOM_HEATING_PWR_TCUT_SENSOR;
        case 0x0002F079:
            return NLS_NOM_VOL_INJ;
        case 0x0002F07A:
            return NLS_NOM_VOL_THERMO_EXTRA_VASC_INDEX;
        case 0x0002F07B:
            return NLS_NOM_NUM_CALC_CONST;
        case 0x0002F07C:
            return NLS_NOM_NUM_CATHETER_CONST;
        case 0x0002F08A:
            return NLS_NOM_PULS_OXIM_PERF_REL_LEFT;
        case 0x0002F08B:
            return NLS_NOM_PULS_OXIM_PERF_REL_RIGHT;
        case 0x0002F08C:
            return NLS_NOM_PULS_OXIM_PLETH_RIGHT;
        case 0x0002F08D:
            return NLS_NOM_PULS_OXIM_PLETH_LEFT;
        case 0x0002F08F:
            return NLS_NOM_CONC_BLD_UREA_NITROGEN;
        case 0x0002F090:
            return NLS_NOM_CONC_BASE_EXCESS_ECF;
        case 0x0002F091:
            return NLS_NOM_VENT_VOL_MINUTE_AWAY_SPONT;
        case 0x0002F092:
            return NLS_NOM_CONC_DIFF_HB_O2_ATR_VEN;
        case 0x0002F093:
            return NLS_NOM_PAT_WEIGHT;
        case 0x0002F094:
            return NLS_NOM_PAT_HEIGHT;
        case 0x0002F099:
            return NLS_NOM_CONC_AWAY_MAC;
        case 0x0002F09B:
            return NLS_NOM_PULS_OXIM_PLETH_TELE;
        case 0x0002F09C:
            return NLS_NOM_PULS_OXIM_SAT_O2_TELE;
        case 0x0002F09D:
            return NLS_NOM_PULS_OXIM_PULS_RATE_TELE;
        case 0x0002F0A4:
            return NLS_NOM_PRESS_GEN_1;
        case 0x0002F0A8:
            return NLS_NOM_PRESS_GEN_2;
        case 0x0002F0AC:
            return NLS_NOM_PRESS_GEN_3;
        case 0x0002F0B0:
            return NLS_NOM_PRESS_GEN_4;
        case 0x0002F0B4:
            return NLS_NOM_PRESS_INTRA_CRAN_1;
        case 0x0002F0B8:
            return NLS_NOM_PRESS_INTRA_CRAN_2;
        case 0x0002F0BC:
            return NLS_NOM_PRESS_BLD_ART_FEMORAL;
        case 0x0002F0C0:
            return NLS_NOM_PRESS_BLD_ART_BRACHIAL;
        case 0x0002F0C4:
            return NLS_NOM_TEMP_VESICAL;
        case 0x0002F0C5:
            return NLS_NOM_TEMP_CEREBRAL;
        case 0x0002F0C6:
            return NLS_NOM_TEMP_AMBIENT;
        case 0x0002F0C7:
            return NLS_NOM_TEMP_GEN_1;
        case 0x0002F0C8:
            return NLS_NOM_TEMP_GEN_2;
        case 0x0002F0C9:
            return NLS_NOM_TEMP_GEN_3;
        case 0x0002F0CA:
            return NLS_NOM_TEMP_GEN_4;
        case 0x0002F0D8:
            return NLS_NOM_PRESS_INTRA_UTERAL;
        case 0x0002F0E0:
            return NLS_NOM_VOL_AWAY_INSP_TIDAL;
        case 0x0002F0E1:
            return NLS_NOM_VOL_AWAY_EXP_TIDAL;
        case 0x0002F0E2:
            return NLS_NOM_AWAY_RESP_RATE_SPIRO;
        case 0x0002F0E3:
            return NLS_NOM_PULS_PRESS_VAR;
        case 0x0002F0E5:
            return NLS_NOM_PRESS_BLD_NONINV_PULS_RATE;
        case 0x0002F0F1:
            return NLS_NOM_VENT_RESP_RATE_MAND;
        case 0x0002F0F2:
            return NLS_NOM_VENT_VOL_TIDAL_MAND;
        case 0x0002F0F3:
            return NLS_NOM_VENT_VOL_TIDAL_SPONT;
        case 0x0002F0F4:
            return NLS_NOM_CARDIAC_TROPONIN_I;
        case 0x0002F0F5:
            return NLS_NOM_CARDIO_PULMONARY_BYPASS_MODE;
        case 0x0002F0F6:
            return NLS_NOM_BNP;
        case 0x0002F0FF:
            return NLS_NOM_TIME_PD_RESP_PLAT;
        case 0x0002F100:
            return NLS_NOM_SAT_O2_VEN_CENT;
        case 0x0002F101:
            return NLS_NOM_SNR;
        case 0x0002F103:
            return NLS_NOM_HUMID;
        case 0x0002F105:
            return NLS_NOM_FRACT_EJECT;
        case 0x0002F106:
            return NLS_NOM_PERM_VASC_PULM_INDEX;
        case 0x0002F110:
            return NLS_NOM_TEMP_ORAL_PRED;
        case 0x0002F114:
            return NLS_NOM_TEMP_RECT_PRED;
        case 0x0002F118:
            return NLS_NOM_TEMP_AXIL_PRED;
        case 0x0002F12A:
            return NLS_NOM_TEMP_AIR_INCUB;
        case 0x0002F12C:
            return NLS_NOM_PULS_OXIM_PERF_REL_TELE;
        case 0x0002F14A:
            return NLS_NOM_SHUNT_RIGHT_LEFT;
        case 0x0002F154:
            return NLS_NOM_ECG_TIME_PD_QT_HEART_RATE;
        case 0x0002F155:
            return NLS_NOM_ECG_TIME_PD_QT_BASELINE;
        case 0x0002F156:
            return NLS_NOM_ECG_TIME_PD_QTc_DELTA;
        case 0x0002F157:
            return NLS_NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE;
        case 0x0002F158:
            return NLS_NOM_CONC_PH_CAP;
        case 0x0002F159:
            return NLS_NOM_CONC_PCO2_CAP;
        case 0x0002F15A:
            return NLS_NOM_CONC_PO2_CAP;
        case 0x0002F15B:
            return NLS_NOM_CONC_MG_ION;
        case 0x0002F15C:
            return NLS_NOM_CONC_MG_SER;
        case 0x0002F15D:
            return NLS_NOM_CONC_tCA_SER;
        case 0x0002F15E:
            return NLS_NOM_CONC_P_SER;
        case 0x0002F15F:
            return NLS_NOM_CONC_CHLOR_SER;
        case 0x0002F160:
            return NLS_NOM_CONC_FE_GEN;
        case 0x0002F163:
            return NLS_NOM_CONC_ALB_SER;
        case 0x0002F164:
            return NLS_NOM_SAT_O2_ART_CALC;
        case 0x0002F165:
            return NLS_NOM_CONC_HB_FETAL;
        case 0x0002F166:
            return NLS_NOM_SAT_O2_VEN_CALC;
        case 0x0002F167:
            return NLS_NOM_PLTS_CNT;
        case 0x0002F168:
            return NLS_NOM_WB_CNT;
        case 0x0002F169:
            return NLS_NOM_RB_CNT;
        case 0x0002F16A:
            return NLS_NOM_RET_CNT;
        case 0x0002F16B:
            return NLS_NOM_PLASMA_OSM;
        case 0x0002F16C:
            return NLS_NOM_CONC_CREA_CLR;
        case 0x0002F16D:
            return NLS_NOM_NSLOSS;
        case 0x0002F16E:
            return NLS_NOM_CONC_CHOLESTEROL;
        case 0x0002F16F:
            return NLS_NOM_CONC_TGL;
        case 0x0002F170:
            return NLS_NOM_CONC_HDL;
        case 0x0002F171:
            return NLS_NOM_CONC_LDL;
        case 0x0002F172:
            return NLS_NOM_CONC_UREA_GEN;
        case 0x0002F173:
            return NLS_NOM_CONC_CREA;
        case 0x0002F174:
            return NLS_NOM_CONC_LACT;
        case 0x0002F177:
            return NLS_NOM_CONC_BILI_TOT;
        case 0x0002F178:
            return NLS_NOM_CONC_PROT_SER;
        case 0x0002F179:
            return NLS_NOM_CONC_PROT_TOT;
        case 0x0002F17A:
            return NLS_NOM_CONC_BILI_DIRECT;
        case 0x0002F17B:
            return NLS_NOM_CONC_LDH;
        case 0x0002F17C:
            return NLS_NOM_ES_RATE;
        case 0x0002F17D:
            return NLS_NOM_CONC_PCT;
        case 0x0002F17F:
            return NLS_NOM_CONC_CREA_KIN_MM;
        case 0x0002F180:
            return NLS_NOM_CONC_CREA_KIN_SER;
        case 0x0002F181:
            return NLS_NOM_CONC_CREA_KIN_MB;
        case 0x0002F182:
            return NLS_NOM_CONC_CHE;
        case 0x0002F183:
            return NLS_NOM_CONC_CRP;
        case 0x0002F184:
            return NLS_NOM_CONC_AST;
        case 0x0002F185:
            return NLS_NOM_CONC_AP;
        case 0x0002F186:
            return NLS_NOM_CONC_ALPHA_AMYLASE;
        case 0x0002F187:
            return NLS_NOM_CONC_GPT;
        case 0x0002F188:
            return NLS_NOM_CONC_GOT;
        case 0x0002F189:
            return NLS_NOM_CONC_GGT;
        case 0x0002F18A:
            return NLS_NOM_TIME_PD_ACT;
        case 0x0002F18B:
            return NLS_NOM_TIME_PD_PT;
        case 0x0002F18C:
            return NLS_NOM_PT_INTL_NORM_RATIO;
        case 0x0002F18D:
            return NLS_NOM_TIME_PD_aPTT_WB;
        case 0x0002F18E:
            return NLS_NOM_TIME_PD_aPTT_PE;
        case 0x0002F18F:
            return NLS_NOM_TIME_PD_PT_WB;
        case 0x0002F190:
            return NLS_NOM_TIME_PD_PT_PE;
        case 0x0002F191:
            return NLS_NOM_TIME_PD_THROMBIN;
        case 0x0002F192:
            return NLS_NOM_TIME_PD_COAGULATION;
        case 0x0002F193:
            return NLS_NOM_TIME_PD_THROMBOPLAS;
        case 0x0002F194:
            return NLS_NOM_FRACT_EXCR_NA;
        case 0x0002F195:
            return NLS_NOM_CONC_UREA_URINE;
        case 0x0002F196:
            return NLS_NOM_CONC_CREA_URINE;
        case 0x0002F197:
            return NLS_NOM_CONC_K_URINE;
        case 0x0002F198:
            return NLS_NOM_CONC_K_URINE_EXCR;
        case 0x0002F199:
            return NLS_NOM_CONC_OSM_URINE;
        case 0x0002F19A:
            return NLS_NOM_CONC_CHLOR_URINE;
        case 0x0002F19B:
            return NLS_NOM_CONC_PRO_URINE;
        case 0x0002F19C:
            return NLS_NOM_CONC_CA_URINE;
        case 0x0002F19D:
            return NLS_NOM_FLUID_DENS_URINE;
        case 0x0002F19E:
            return NLS_NOM_CONC_HB_URINE;
        case 0x0002F19F:
            return NLS_NOM_CONC_GLU_URINE;
        case 0x0002F1A0:
            return NLS_NOM_SAT_O2_CAP_CALC;
        case 0x0002F1A1:
            return NLS_NOM_CONC_AN_GAP_CALC;
        case 0x0002F1C0:
            return NLS_NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL;
        case 0x0002F1D4:
            return NLS_NOM_PULS_OXIM_SAT_O2_POST_DUCTAL;
        case 0x0002F1DC:
            return NLS_NOM_PULS_OXIM_PERF_REL_POST_DUCTAL;
        case 0x0002F22C:
            return NLS_NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL;
        case 0x0002F3F4:
            return NLS_NOM_PRESS_GEN_5;
        case 0x0002F3F8:
            return NLS_NOM_PRESS_GEN_6;
        case 0x0002F3FC:
            return NLS_NOM_PRESS_GEN_7;
        case 0x0002F400:
            return NLS_NOM_PRESS_GEN_8;
        case 0x0002F411:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_I;
        case 0x0002F412:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_II;
        case 0x0002F413:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V1;
        case 0x0002F414:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V2;
        case 0x0002F415:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V3;
        case 0x0002F416:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V4;
        case 0x0002F417:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V5;
        case 0x0002F418:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_V6;
        case 0x0002F44D:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_III;
        case 0x0002F44E:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_AVR;
        case 0x0002F44F:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_AVL;
        case 0x0002F450:
            return NLS_NOM_ECG_AMPL_ST_BASELINE_AVF;
        case 0x0002F810:
            return NLS_NOM_AGE;
        case 0x0002F811:
            return NLS_NOM_AGE_GEST;
        case 0x0002F812:
            return NLS_NOM_AREA_BODY_SURFACE_ACTUAL_BOYD;
        case 0x0002F813:
            return NLS_NOM_AREA_BODY_SURFACE_ACTUAL_DUBOIS;
        case 0x0002F814:
            return NLS_NOM_AWAY_CORR_COEF;
        case 0x0002F815:
            return NLS_NOM_AWAY_RESP_RATE_SPONT;
        case 0x0002F816:
            return NLS_NOM_AWAY_TC;
        case 0x0002F817:
            return NLS_NOM_BASE_EXCESS_BLD_ART_CALC;
        case 0x0002F818:
            return NLS_NOM_BIRTH_LENGTH;
        case 0x0002F819:
            return NLS_NOM_BREATH_RAPID_SHALLOW_INDEX;
        case 0x0002F81A:
            return NLS_NOM_C20_PER_C_INDEX;
        case 0x0002F81B:
            return NLS_NOM_CARD_BEAT_RATE_EXT;
        case 0x0002F81C:
            return NLS_NOM_CARD_CONTRACT_HEATHER_INDEX;
        case 0x0002F81D:
            return NLS_NOM_CONC_ALP;
        case 0x0002F81E:
            return NLS_NOM_CONC_AWAY_AGENT_ET_SEC;
        case 0x0002F81F:
            return NLS_NOM_CONC_AWAY_AGENT_INSP_SEC;
        case 0x0002F821:
            return NLS_NOM_CONC_BASE_EXCESS_ECF_CALC;
        case 0x0002F822:
            return NLS_NOM_CONC_CA_GEN_NORM;
        case 0x0002F824:
            return NLS_NOM_CONC_CA_SER;
        case 0x0002F825:
            return NLS_NOM_CONC_CO2_TOT;
        case 0x0002F826:
            return NLS_NOM_CONC_CO2_TOT_CALC;
        case 0x0002F827:
            return NLS_NOM_CONC_CREA_SER;
        case 0x0002F828:
            return NLS_NOM_RESP_RATE_SPONT;
        case 0x0002F829:
            return NLS_NOM_CONC_GLO_SER;
        case 0x0002F82A:
            return NLS_NOM_CONC_GLU_SER;
        case 0x0002F82B:
            return NLS_NOM_CONC_HB_ART_CALC;
        case 0x0002F82C:
            return NLS_NOM_CONC_HB_CORP_MEAN;
        case 0x0002F82E:
            return NLS_NOM_CONC_HCO3_GEN_CALC;
        case 0x0002F82F:
            return NLS_NOM_CONC_K_SER;
        case 0x0002F830:
            return NLS_NOM_CONC_NA_EXCR;
        case 0x0002F832:
            return NLS_NOM_CONC_PCO2_ART_ADJ;
        case 0x0002F833:
            return NLS_NOM_CONC_PCO2_CAP_ADJ;
        case 0x0002F834:
            return NLS_NOM_CONC_PCO2_GEN_ADJ;
        case 0x0002F835:
            return NLS_NOM_CONC_PCO2_VEN_ADJ;
        case 0x0002F836:
            return NLS_NOM_CONC_PH_ART_ADJ;
        case 0x0002F837:
            return NLS_NOM_CONC_PH_CAP_ADJ;
        case 0x0002F838:
            return NLS_NOM_CONC_PH_GEN_ADJ;
        case 0x0002F839:
            return NLS_NOM_CONC_PH_VEN_ADJ;
        case 0x0002F83B:
            return NLS_NOM_CONC_PO2_ART_ADJ;
        case 0x0002F83C:
            return NLS_NOM_CONC_PO2_CAP_ADJ;
        case 0x0002F83D:
            return NLS_NOM_CONC_PO2_GEN_ADJ;
        case 0x0002F83E:
            return NLS_NOM_CONC_PO2_VEN_ADJ;
        case 0x0002F83F:
            return NLS_NOM_CREA_OSM;
        case 0x0002F840:
            return NLS_NOM_EEG_BURST_SUPPRN_INDEX;
        case 0x0002F841:
            return NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT;
        case 0x0002F842:
            return NLS_NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT;
        case 0x0002F849:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_LEFT;
        case 0x0002F84A:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN_RIGHT;
        case 0x0002F84B:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT;
        case 0x0002F84C:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT;
        case 0x0002F84F:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_LEFT;
        case 0x0002F850:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK_RIGHT;
        case 0x0002F853:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_LEFT;
        case 0x0002F854:
            return NLS_NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE_RIGHT;
        case 0x0002F855:
            return NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT;
        case 0x0002F856:
            return NLS_NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT;
        case 0x0002F859:
            return NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_LEFT;
        case 0x0002F85A:
            return NLS_NOM_EEG_PWR_SPEC_ALPHA_REL_RIGHT;
        case 0x0002F85B:
            return NLS_NOM_EEG_PWR_SPEC_BETA_ABS_LEFT;
        case 0x0002F85C:
            return NLS_NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT;
        case 0x0002F85F:
            return NLS_NOM_EEG_PWR_SPEC_BETA_REL_LEFT;
        case 0x0002F860:
            return NLS_NOM_EEG_PWR_SPEC_BETA_REL_RIGHT;
        case 0x0002F863:
            return NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT;
        case 0x0002F864:
            return NLS_NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT;
        case 0x0002F867:
            return NLS_NOM_EEG_PWR_SPEC_DELTA_REL_LEFT;
        case 0x0002F868:
            return NLS_NOM_EEG_PWR_SPEC_DELTA_REL_RIGHT;
        case 0x0002F869:
            return NLS_NOM_EEG_PWR_SPEC_THETA_ABS_LEFT;
        case 0x0002F86A:
            return NLS_NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT;
        case 0x0002F86D:
            return NLS_NOM_EEG_PWR_SPEC_THETA_REL_LEFT;
        case 0x0002F86E:
            return NLS_NOM_EEG_PWR_SPEC_THETA_REL_RIGHT;
        case 0x0002F871:
            return NLS_NOM_EEG_PWR_SPEC_TOT_LEFT;
        case 0x0002F872:
            return NLS_NOM_EEG_PWR_SPEC_TOT_RIGHT;
        case 0x0002F873:
            return NLS_NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI;
        case 0x0002F875:
            return NLS_NOM_EXTRACT_O2_INDEX;
        case 0x0002F876:
            return NLS_NOM_FLOW_AWAY_AGENT;
        case 0x0002F877:
            return NLS_NOM_FLOW_AWAY_AIR;
        case 0x0002F878:
            return NLS_NOM_FLOW_AWAY_DESFL;
        case 0x0002F879:
            return NLS_NOM_FLOW_AWAY_ENFL;
        case 0x0002F87A:
            return NLS_NOM_FLOW_AWAY_EXP_ET;
        case 0x0002F87B:
            return NLS_NOM_FLOW_AWAY_HALOTH;
        case 0x0002F87C:
            return NLS_NOM_FLOW_AWAY_ISOFL;
        case 0x0002F87D:
            return NLS_NOM_FLOW_AWAY_MAX_SPONT;
        case 0x0002F87E:
            return NLS_NOM_FLOW_AWAY_N2O;
        case 0x0002F87F:
            return NLS_NOM_FLOW_AWAY_O2;
        case 0x0002F880:
            return NLS_NOM_FLOW_AWAY_SEVOFL;
        case 0x0002F881:
            return NLS_NOM_FLOW_AWAY_TOT;
        case 0x0002F882:
            return NLS_NOM_FLOW_CO2_PROD_RESP_TIDAL;
        case 0x0002F883:
            return NLS_NOM_FLOW_URINE_PREV_24HR;
        case 0x0002F884:
            return NLS_NOM_FREE_WATER_CLR;
        case 0x0002F885:
            return NLS_NOM_HB_CORP_MEAN;
        case 0x0002F886:
            return NLS_NOM_HEATING_PWR_INCUBATOR;
        case 0x0002F889:
            return NLS_NOM_OUTPUT_CARD_INDEX_ACCEL;
        case 0x0002F88B:
            return NLS_NOM_PTC_CNT;
        case 0x0002F88D:
            return NLS_NOM_PULS_OXIM_PLETH_GAIN;
        case 0x0002F88E:
            return NLS_NOM_RATIO_AWAY_RATE_VOL_AWAY;
        case 0x0002F88F:
            return NLS_NOM_RATIO_BUN_CREA;
        case 0x0002F890:
            return NLS_NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC;
        case 0x0002F891:
            return NLS_NOM_RATIO_CONC_URINE_CREA_CALC;
        case 0x0002F892:
            return NLS_NOM_RATIO_CONC_URINE_CREA_SER;
        case 0x0002F893:
            return NLS_NOM_RATIO_CONC_URINE_NA_K;
        case 0x0002F894:
            return NLS_NOM_RATIO_PaO2_FIO2;
        case 0x0002F895:
            return NLS_NOM_RATIO_TIME_PD_PT;
        case 0x0002F896:
            return NLS_NOM_RATIO_TIME_PD_PTT;
        case 0x0002F897:
            return NLS_NOM_RATIO_TRAIN_OF_FOUR;
        case 0x0002F898:
            return NLS_NOM_RATIO_URINE_SER_OSM;
        case 0x0002F899:
            return NLS_NOM_RES_AWAY_DYN;
        case 0x0002F89A:
            return NLS_NOM_RESP_BREATH_ASSIST_CNT;
        case 0x0002F89B:
            return NLS_NOM_RIGHT_HEART_FRACT_EJECT;
        case 0x0002F89C:
            return NLS_NOM_SAT_O2_CALC;
        case 0x0002F89D:
            return NLS_NOM_SAT_O2_LEFT;
        case 0x0002F89E:
            return NLS_NOM_SAT_O2_RIGHT;
        case 0x0002F8A0:
            return NLS_NOM_TIME_PD_EVOK_REMAIN;
        case 0x0002F8A1:
            return NLS_NOM_TIME_PD_EXP;
        case 0x0002F8A2:
            return NLS_NOM_TIME_PD_FROM_LAST_MSMT;
        case 0x0002F8A3:
            return NLS_NOM_TIME_PD_INSP;
        case 0x0002F8A4:
            return NLS_NOM_TIME_PD_KAOLIN_CEPHALINE;
        case 0x0002F8A5:
            return NLS_NOM_TIME_PD_PTT;
        case 0x0002F8A7:
            return NLS_NOM_TRAIN_OF_FOUR_1;
        case 0x0002F8A8:
            return NLS_NOM_TRAIN_OF_FOUR_2;
        case 0x0002F8A9:
            return NLS_NOM_TRAIN_OF_FOUR_3;
        case 0x0002F8AA:
            return NLS_NOM_TRAIN_OF_FOUR_4;
        case 0x0002F8AB:
            return NLS_NOM_TRAIN_OF_FOUR_CNT;
        case 0x0002F8AC:
            return NLS_NOM_TWITCH_AMPL;
        case 0x0002F8AD:
            return NLS_NOM_UREA_SER;
        case 0x0002F8B0:
            return NLS_NOM_VENT_ACTIVE;
        case 0x0002F8B1:
            return NLS_NOM_VENT_AMPL_HFV;
        case 0x0002F8B2:
            return NLS_NOM_VENT_CONC_AWAY_AGENT_DELTA;
        case 0x0002F8B3:
            return NLS_NOM_VENT_CONC_AWAY_DESFL_DELTA;
        case 0x0002F8B4:
            return NLS_NOM_VENT_CONC_AWAY_ENFL_DELTA;
        case 0x0002F8B5:
            return NLS_NOM_VENT_CONC_AWAY_HALOTH_DELTA;
        case 0x0002F8B6:
            return NLS_NOM_VENT_CONC_AWAY_ISOFL_DELTA;
        case 0x0002F8B7:
            return NLS_NOM_VENT_CONC_AWAY_N2O_DELTA;
        case 0x0002F8B8:
            return NLS_NOM_VENT_CONC_AWAY_O2_CIRCUIT;
        case 0x0002F8B9:
            return NLS_NOM_VENT_CONC_AWAY_SEVOFL_DELTA;
        case 0x0002F8BA:
            return NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO;
        case 0x0002F8BB:
            return NLS_NOM_VENT_PRESS_AWAY_INSP_MAX;
        case 0x0002F8BC:
            return NLS_NOM_VENT_PRESS_AWAY_PV;
        case 0x0002F8BD:
            return NLS_NOM_VENT_TIME_PD_RAMP;
        case 0x0002F8BE:
            return NLS_NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV;
        case 0x0002F8BF:
            return NLS_NOM_VENT_VOL_TIDAL_HFV;
        case 0x0002F8C2:
            return NLS_NOM_VOL_AWAY_EXP_TIDAL_SPONT;
        case 0x0002F8C3:
            return NLS_NOM_VOL_AWAY_TIDAL_PSV;
        case 0x0002F8C4:
            return NLS_NOM_VOL_CORP_MEAN;
        case 0x0002F8C5:
            return NLS_NOM_VOL_FLUID_THORAC;
        case 0x0002F8C6:
            return NLS_NOM_VOL_FLUID_THORAC_INDEX;
        case 0x0002F8C7:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_AGENT;
        case 0x0002F8C8:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_DESFL;
        case 0x0002F8C9:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ENFL;
        case 0x0002F8CA:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH;
        case 0x0002F8CB:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL;
        case 0x0002F8CC:
            return NLS_NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL;
        case 0x0002F8CD:
            return NLS_NOM_VOL_MINUTE_AWAY_INSP_HFV;
        case 0x0002F8CE:
            return NLS_NOM_VOL_URINE_BAL_PD_INSTANT;
        case 0x0002F8CF:
            return NLS_NOM_VOL_URINE_SHIFT;
        case 0x0002F8D0:
            return NLS_NOM_VOL_VENT_L_END_DIA_INDEX;
        case 0x0002F8D1:
            return NLS_NOM_VOL_VENT_L_END_SYS_INDEX;
        case 0x0002F8D3:
            return NLS_NOM_WEIGHT_URINE_COL;
        case 0x0002F960:
            return NLS_NOM_SAT_O2_TISSUE;
        case 0x0002F961:
            return NLS_NOM_CEREB_STATE_INDEX;
        case 0x0002F962:
            return NLS_NOM_SAT_O2_GEN_1;
        case 0x0002F963:
            return NLS_NOM_SAT_O2_GEN_2;
        case 0x0002F964:
            return NLS_NOM_SAT_O2_GEN_3;
        case 0x0002F965:
            return NLS_NOM_SAT_O2_GEN_4;
        case 0x0002F966:
            return NLS_NOM_TEMP_CORE_GEN_1;
        case 0x0002F967:
            return NLS_NOM_TEMP_CORE_GEN_2;
        case 0x0002F968:
            return NLS_NOM_PRESS_BLD_DIFF;
        case 0x0002F96C:
            return NLS_NOM_PRESS_BLD_DIFF_GEN_1;
        case 0x0002F970:
            return NLS_NOM_PRESS_BLD_DIFF_GEN_2;
        case 0x0002F974:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_MAIN;
        case 0x0002F975:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_SLAVE;
        case 0x0002F976:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_SUCTION;
        case 0x0002F977:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_AUX;
        case 0x0002F978:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0x0002F979:
            return NLS_NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0x0002F97A:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START;
        case 0x0002F97B:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP;
        case 0x0002F97C:
            return NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX;
        case 0x0002F97D:
            return NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX;
        case 0x0002F97E:
            return NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX;
        case 0x0002F97F:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START;
        case 0x0002F980:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP;
        case 0x0002F981:
            return NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0x0002F982:
            return NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0x0002F983:
            return NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0x0002F984:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START;
        case 0x0002F985:
            return NLS_NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP;
        case 0x0002F986:
            return NLS_NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0x0002F987:
            return NLS_NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0x0002F988:
            return NLS_NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0x0002F990:
            return NLS_NOM_RATIO_INSP_TOTAL_BREATH_SPONT;
        case 0x0002F991:
            return NLS_NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL;
        case 0x0002F992:
            return NLS_NOM_COMPL_LUNG_PAV;
        case 0x0002F993:
            return NLS_NOM_RES_AWAY_PAV;
        case 0x0002F994:
            return NLS_NOM_RES_AWAY_EXP_TOTAL;
        case 0x0002F995:
            return NLS_NOM_ELAS_LUNG_PAV;
        case 0x0002F996:
            return NLS_NOM_BREATH_RAPID_SHALLOW_INDEX_NORM;
        case 0x04010030:
            return NLS_NOM_EMFC_P1;
        case 0x04010034:
            return NLS_NOM_EMFC_P2;
        case 0x04010038:
            return NLS_NOM_EMFC_P3;
        case 0x0401003C:
            return NLS_NOM_EMFC_P4;
        case 0x04010054:
            return NLS_NOM_EMFC_IUP;
        case 0x040100B4:
            return NLS_NOM_EMFC_AUX;
        case 0x04010400:
            return NLS_NOM_EMFC_P5;
        case 0x04010404:
            return NLS_NOM_EMFC_P6;
        case 0x04010408:
            return NLS_NOM_EMFC_P7;
        case 0x0401040C:
            return NLS_NOM_EMFC_P8;
        case 0x04010668:
            return NLS_NOM_EMFC_AWV;
        case 0x04010764:
            return NLS_NOM_EMFC_L_V1;
        case 0x04010768:
            return NLS_NOM_EMFC_L_V2;
        case 0x0401076C:
            return NLS_NOM_EMFC_L_V3;
        case 0x04010770:
            return NLS_NOM_EMFC_L_V4;
        case 0x04010774:
            return NLS_NOM_EMFC_L_V5;
        case 0x04010778:
            return NLS_NOM_EMFC_L_V6;
        case 0x0401077C:
            return NLS_NOM_EMFC_L_I;
        case 0x04010780:
            return NLS_NOM_EMFC_L_II;
        case 0x04010784:
            return NLS_NOM_EMFC_L_III;
        case 0x04010788:
            return NLS_NOM_EMFC_L_aVR;
        case 0x0401078C:
            return NLS_NOM_EMFC_L_aVL;
        case 0x04010790:
            return NLS_NOM_EMFC_L_aVF;
        case 0x04010794:
            return NLS_NOM_EMFC_AWVex;
        case 0x0401079C:
            return NLS_NOM_EMFC_PLETH2;
        case 0x040107F0:
            return NLS_NOM_EMFC_LT_EEG;
        case 0x0401082C:
            return NLS_NOM_EMFC_RT_EEG;
        case 0x04010888:
            return NLS_NOM_EMFC_BP;
        case 0x04010CE4:
            return NLS_NOM_EMFC_AGTs;
        case 0x0401119C:
            return NLS_NOM_EMFC_vECG;
        case 0x040111A0:
            return NLS_NOM_EMFC_ICG;
        case 0x04024B48:
            return NLS_NOM_SETT_TEMP;
        case 0x04025012:
            return NLS_NOM_SETT_AWAY_RESP_RATE;
        case 0x04025022:
            return NLS_NOM_SETT_VENT_RESP_RATE;
        case 0x040250DD:
            return NLS_NOM_SETT_FLOW_AWAY_INSP_MAX;
        case 0x040250F2:
            return NLS_NOM_SETT_PRESS_AWAY_MIN;
        case 0x040250F4:
            return NLS_NOM_SETT_PRESS_AWAY_CTS_POS;
        case 0x04025108:
            return NLS_NOM_SETT_PRESS_AWAY_INSP;
        case 0x04025109:
            return NLS_NOM_SETT_PRESS_AWAY_INSP_MAX;
        case 0x04025118:
            return NLS_NOM_SETT_RATIO_IE;
        case 0x0402513C:
            return NLS_NOM_SETT_VOL_AWAY_TIDAL;
        case 0x04025148:
            return NLS_NOM_SETT_VOL_MINUTE_AWAY;
        case 0x04025164:
            return NLS_NOM_SETT_CONC_AWAY_O2;
        case 0x040251A8:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS;
        case 0x040251B8:
            return NLS_NOM_SETT_VENT_VOL_LUNG_TRAPD;
        case 0x040251CC:
            return NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND;
        case 0x040251D8:
            return NLS_NOM_SETT_CONC_AWAY_DESFL;
        case 0x040251DC:
            return NLS_NOM_SETT_CONC_AWAY_ENFL;
        case 0x040251E0:
            return NLS_NOM_SETT_CONC_AWAY_HALOTH;
        case 0x040251E4:
            return NLS_NOM_SETT_CONC_AWAY_SEVOFL;
        case 0x040251E8:
            return NLS_NOM_SETT_CONC_AWAY_ISOFL;
        case 0x04026858:
            return NLS_NOM_SETT_FLOW_FLUID_PUMP;
        case 0x04027498:
            return NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP;
        case 0x0402F0E0:
            return NLS_NOM_SETT_VOL_AWAY_INSP_TIDAL;
        case 0x0402F0FF:
            return NLS_NOM_SETT_TIME_PD_RESP_PLAT;
        case 0x0402F876:
            return NLS_NOM_SETT_FLOW_AWAY_AGENT;
        case 0x0402F877:
            return NLS_NOM_SETT_FLOW_AWAY_AIR;
        case 0x0402F87E:
            return NLS_NOM_SETT_FLOW_AWAY_N2O;
        case 0x0402F87F:
            return NLS_NOM_SETT_FLOW_AWAY_O2;
        case 0x0402F881:
            return NLS_NOM_SETT_FLOW_AWAY_TOT;
        case 0x0402F8A6:
            return NLS_NOM_SETT_TIME_PD_TRAIN_OF_FOUR;
        case 0x0402F8AF:
            return NLS_NOM_SETT_URINE_BAL_PD;
        case 0x0402F8BB:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_MAX;
        case 0x0402F8BC:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_PV;
        case 0x0402F8C0:
            return NLS_NOM_SETT_VENT_VOL_TIDAL_SIGH;
        case 0x0402F8D9:
            return NLS_NOM_SETT_APNEA_ALARM_DELAY;
        case 0x0402F8DE:
            return NLS_NOM_SETT_AWAY_RESP_RATE_APNEA;
        case 0x0402F8DF:
            return NLS_NOM_SETT_AWAY_RESP_RATE_HFV;
        case 0x0402F8E6:
            return NLS_NOM_SETT_EVOK_CHARGE;
        case 0x0402F8E7:
            return NLS_NOM_SETT_EVOK_CURR;
        case 0x0402F8EA:
            return NLS_NOM_SETT_FLOW_AWAY_EXP;
        case 0x0402F8EB:
            return NLS_NOM_SETT_FLOW_AWAY_HFV;
        case 0x0402F8EC:
            return NLS_NOM_SETT_FLOW_AWAY_INSP;
        case 0x0402F8ED:
            return NLS_NOM_SETT_FLOW_AWAY_INSP_APNEA;
        case 0x0402F8F3:
            return NLS_NOM_SETT_HFV_AMPL;
        case 0x0402F8FB:
            return NLS_NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO;
        case 0x0402F900:
            return NLS_NOM_SETT_RATIO_IE_EXP_PV;
        case 0x0402F901:
            return NLS_NOM_SETT_RATIO_IE_EXP_PV_APNEA;
        case 0x0402F902:
            return NLS_NOM_SETT_RATIO_IE_INSP_PV;
        case 0x0402F903:
            return NLS_NOM_SETT_RATIO_IE_INSP_PV_APNEA;
        case 0x0402F904:
            return NLS_NOM_SETT_SENS_LEVEL;
        case 0x0402F908:
            return NLS_NOM_SETT_TIME_PD_EVOK;
        case 0x0402F909:
            return NLS_NOM_SETT_TIME_PD_MSMT;
        case 0x0402F90E:
            return NLS_NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE;
        case 0x0402F90F:
            return NLS_NOM_SETT_VENT_AWAY_FLOW_BACKGROUND;
        case 0x0402F910:
            return NLS_NOM_SETT_VENT_AWAY_FLOW_BASE;
        case 0x0402F911:
            return NLS_NOM_SETT_VENT_AWAY_FLOW_SENSE;
        case 0x0402F912:
            return NLS_NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE;
        case 0x0402F917:
            return NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA;
        case 0x0402F918:
            return NLS_NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA;
        case 0x0402F919:
            return NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI;
        case 0x0402F91A:
            return NLS_NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO;
        case 0x0402F91B:
            return NLS_NOM_SETT_VENT_FLOW;
        case 0x0402F91C:
            return NLS_NOM_SETT_VENT_FLOW_AWAY_ASSIST;
        case 0x0402F91D:
            return NLS_NOM_SETT_VENT_FLOW_INSP_TRIG;
        case 0x0402F920:
            return NLS_NOM_SETT_VENT_GAS_PROBE_POSN;
        case 0x0402F922:
            return NLS_NOM_SETT_VENT_MODE_MAND_CTS_ONOFF;
        case 0x0402F923:
            return NLS_NOM_SETT_VENT_MODE_SIGH;
        case 0x0402F924:
            return NLS_NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT;
        case 0x0402F926:
            return NLS_NOM_SETT_VENT_O2_CAL_MODE;
        case 0x0402F927:
            return NLS_NOM_SETT_VENT_O2_PROBE_POSN;
        case 0x0402F928:
            return NLS_NOM_SETT_VENT_O2_SUCTION_MODE;
        case 0x0402F92C:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT;
        case 0x0402F92D:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_EXP_APRV;
        case 0x0402F92E:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_INSP_APRV;
        case 0x0402F930:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI;
        case 0x0402F931:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA;
        case 0x0402F933:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_PV_APNEA;
        case 0x0402F935:
            return NLS_NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI;
        case 0x0402F937:
            return NLS_NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT;
        case 0x0402F938:
            return NLS_NOM_SETT_VENT_RESP_RATE_MODE_MAND_INTERMITT;
        case 0x0402F939:
            return NLS_NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP;
        case 0x0402F93A:
            return NLS_NOM_SETT_VENT_RESP_RATE_PV_APNEA;
        case 0x0402F93B:
            return NLS_NOM_SETT_VENT_SIGH_MULT_RATE;
        case 0x0402F93C:
            return NLS_NOM_SETT_VENT_SIGH_RATE;
        case 0x0402F93F:
            return NLS_NOM_SETT_VENT_TIME_PD_EXP;
        case 0x0402F940:
            return NLS_NOM_SETT_VENT_TIME_PD_EXP_APRV;
        case 0x0402F941:
            return NLS_NOM_SETT_VENT_TIME_PD_INSP;
        case 0x0402F942:
            return NLS_NOM_SETT_VENT_TIME_PD_INSP_APRV;
        case 0x0402F943:
            return NLS_NOM_SETT_VENT_TIME_PD_INSP_PV;
        case 0x0402F944:
            return NLS_NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA;
        case 0x0402F946:
            return NLS_NOM_SETT_VENT_TIME_PD_RAMP_AL;
        case 0x0402F948:
            return NLS_NOM_SETT_VENT_VOL_AWAY_ASSIST;
        case 0x0402F949:
            return NLS_NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF;
        case 0x0402F94B:
            return NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI;
        case 0x0402F94C:
            return NLS_NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO;
        case 0x0402F94D:
            return NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI;
        case 0x0402F94E:
            return NLS_NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO;
        case 0x0402F951:
            return NLS_NOM_SETT_VOL_AWAY_TIDAL_APNEA;
        case 0x0402F952:
            return NLS_NOM_SETT_VOL_AWAY_TIDAL_APPLIED;
        case 0x0402F953:
            return NLS_NOM_SETT_VOL_MINUTE_ALARM_DELAY;
        default:
            throw new IllegalArgumentException("Unknown Label:"+x);
        }
    }
}
