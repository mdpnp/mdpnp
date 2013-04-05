package org.mdpnp.devices.philips.intellivue.data;

public enum ObservedValue {
	   /**
     * ECG Unspecific ECG wave
     */
    NOM_ECG_ELEC_POTL,
    /**
     * L I Lead I - ECG wave label
     */
    NOM_ECG_ELEC_POTL_I,
    /**
     * L II Lead II - ECG wave label
     */
    NOM_ECG_ELEC_POTL_II,
    /**
     * L V1 Lead V1 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V1,
    /**
     * L V2 Lead V2 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V2,
    /**
     * L V3 Lead V3 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V3,
    /**
     * L V4 Lead V4 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V4,
    /**
     * L V5 Lead V5 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V5,
    /**
     * L V6 Lead V6 - ECG wave label
     */
    NOM_ECG_ELEC_POTL_V6,
    /**
     * L III Lead III - ECG wave label
     */
    NOM_ECG_ELEC_POTL_III,
    /**
     * L aVR Lead aVR - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVR,
    /**
     * L aVL Lead aVL - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVL,
    /**
     * L aVF Lead aVF - ECG wave label
     */
    NOM_ECG_ELEC_POTL_AVF,
    /**
     * V ECG Lead V
     */
    NOM_ECG_ELEC_POTL_V,
    /**
     * MCL ECG Lead MCL
     */
    NOM_ECG_ELEC_POTL_MCL,
    /**
     * MCL1 ECG Lead MCL1
     */
    NOM_ECG_ELEC_POTL_MCL1,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_I,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_II,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V1,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V2,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V3,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V4,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V5,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V6,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_III,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVR,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVL,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AVF,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_V,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_MCL,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_ES,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AS,
    /**
     * ST ST generic label
     */
    NOM_ECG_AMPL_ST_AI,
    /**
     * QT
     */
    NOM_ECG_TIME_PD_QT_GL,
    /**
     * QTc
     */
    NOM_ECG_TIME_PD_QTc,
    /**
     * NOM_ECG_RHY_UNANALYZEABLE Cannot Analyze ECG 0x4011
     */
    NOM_ECG_RHY_ABSENT,
    /**
     * NOM_ECG_RHY_UNANALYZEABLE Cannot Analyze ECG 0x4011
     */
    NOM_ECG_RHY_NOS,
    /**
     * extHR denotes a Heart Rate received from an external device
     */
    NOM_ECG_CARD_BEAT_RATE,
    /**
     * btbHR Cardiac Beat-to-Beat Rate
     */
    NOM_ECG_CARD_BEAT_RATE_BTB,
    /**
     * PVC Premature Ventricular Contractions
     */
    NOM_ECG_V_P_C_CNT,
    /**
     * NLS_PRESS_NAMES_PULSE_FROM_P4 0x8003542B
     */
    NOM_PULS_RATE,
    /**
     * NLS_SPO2_NAMES_PULS_OXIM_PULS_RATE_LEFT 0x80155401
     */
    NOM_PLETH_PULS_RATE,
    /**
     * SVRI Systemic Vascular Resistance Index
     */
    NOM_RES_VASC_SYS_INDEX,
    /**
     * LVSWI Left Ventricular Stroke Volume Index
     */
    NOM_WK_LV_STROKE_INDEX,
    /**
     * RVSWI Right Ventricular Stroke Work Index
     */
    NOM_WK_RV_STROKE_INDEX,
    /**
     * C.I. Cardiac Index
     */
    NOM_OUTPUT_CARD_INDEX,
    /**
     * BP Unspecified Blood Pressure
     */
    NOM_PRESS_BLD,
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_SYS,
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_DIA,
    /**
     * P unspecific pressure
     */
    NOM_PRESS_BLD_MEAN,
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV,
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_SYS,
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_DIA,
    /**
     * NBP non-invasive blood pressure
     */
    NOM_PRESS_BLD_NONINV_MEAN,
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT,
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_SYS,
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_DIA,
    /**
     * Ao Arterial Blood Pressure in the Aorta (Ao)
     */
    NOM_PRESS_BLD_AORT_MEAN,
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART,
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_SYS,
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_DIA,
    /**
     * ART Arterial Blood Pressure (ART)
     */
    NOM_PRESS_BLD_ART_MEAN,
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP,
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_SYS,
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_DIA,
    /**
     * ABP Arterial Blood Pressure (ABP)
     */
    NOM_PRESS_BLD_ART_ABP_MEAN,
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM,
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_SYS,
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_DIA,
    /**
     * PAP Pulmonary Arterial Pressure (PAP)
     */
    NOM_PRESS_BLD_ART_PULM_MEAN,
    /**
     * PAWP Pulmonary Artery Wedge Pressure
     */
    NOM_PRESS_BLD_ART_PULM_WEDGE,
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB,
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_SYS,
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_DIA,
    /**
     * UAP Umbilical Arterial Pressure (UAP)
     */
    NOM_PRESS_BLD_ART_UMB_MEAN,
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT,
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_SYS,
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_DIA,
    /**
     * LAP Left Atrial Pressure (LAP)
     */
    NOM_PRESS_BLD_ATR_LEFT_MEAN,
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT,
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_SYS,
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_DIA,
    /**
     * RAP Right Atrial Pressure (RAP)
     */
    NOM_PRESS_BLD_ATR_RIGHT_MEAN,
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT,
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_SYS,
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_DIA,
    /**
     * CVP Central Venous Pressure (CVP)
     */
    NOM_PRESS_BLD_VEN_CENT_MEAN,
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB,
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_SYS,
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_DIA,
    /**
     * UVP Umbilical Venous Pressure (UVP)
     */
    NOM_PRESS_BLD_VEN_UMB_MEAN,
    /**
     * VO2 Oxygen Consumption VO2
     */
    NOM_SAT_O2_CONSUMP,
    /**
     * C.O. Cardiac Output
     */
    NOM_OUTPUT_CARD,
    /**
     * PVR Pulmonary vascular Resistance
     */
    NOM_RES_VASC_PULM,
    /**
     * SVR Systemic Vascular Resistance
     */
    NOM_RES_VASC_SYS,
    /**
     * SO2 O2 Saturation
     */
    NOM_SAT_O2,
    /**
     * 'SO2 Calculated SO2
     */
    NOM_SAT_O2_ART,
    /**
     * 'SvO2 Calculated SvO2
     */
    NOM_SAT_O2_VEN,
    /**
     * AaDO2 Alveolar- Arterial Oxygen Difference
     */
    NOM_SAT_DIFF_O2_ART_ALV,
    /**
     * sTemp Desired Environmental Temperature
     */
    NOM_SETT_TEMP,
    /**
     * Tart Areterial Temperature
     */
    NOM_TEMP_ART,
    /**
     * Tairwy Airway Temperature
     */
    NOM_TEMP_AWAY,
    /**
     * Tcore Core (Body) Temperature
     */
    NOM_TEMP_CORE,
    /**
     * Tesoph Esophagial Temperature
     */
    NOM_TEMP_ESOPH,
    /**
     * Tinj Injectate Temperature
     */
    NOM_TEMP_INJ,
    /**
     * Tnaso Naso pharyngial Temperature
     */
    NOM_TEMP_NASOPH,
    /**
     * Tskin Skin Temperature
     */
    NOM_TEMP_SKIN,
    /**
     * Ttymp Tympanic Temperature
     */
    NOM_TEMP_TYMP,
    /**
     * Tven Venous Temperature
     */
    NOM_TEMP_VEN,
    /**
     * SV Stroke Volume
     */
    NOM_VOL_BLD_STROKE,
    /**
     * LCW Left Cardiac Work
     */
    NOM_WK_CARD_LEFT,
    /**
     * RCW Right Cardiac Work
     */
    NOM_WK_CARD_RIGHT,
    /**
     * LVSW Left Ventricular Stroke Volume
     */
    NOM_WK_LV_STROKE,
    /**
     * RVSW Right Ventricular Stroke Volume
     */
    NOM_WK_RV_STROKE,
    /**
     * Perf Perfusion Indicator
     */
    NOM_PULS_OXIM_PERF_REL,
    /**
     * PLETH2 PLETH from the second SpO2/PLETH module
     */
    NOM_PLETH,
    /**
     * SpO2 Arterial Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2,
    /**
     * DeltaSpO2 Difference between two SpO2 Values (like Left - Right)
     */
    NOM_PULS_OXIM_SAT_O2_DIFF,
    /**
     * SpO2 l Arterial Oxigen Saturation (left)
     */
    NOM_PULS_OXIM_SAT_O2_ART_LEFT,
    /**
     * SpO2 r Arterial Oxigen Saturation (right)
     */
    NOM_PULS_OXIM_SAT_O2_ART_RIGHT,
    /**
     * CCO Continuous Cardiac Output
     */
    NOM_OUTPUT_CARD_CTS,
    /**
     * ESV End Systolic Volume
     */
    NOM_VOL_VENT_L_END_SYS,
    /**
     * dPmax Index of Left Ventricular Contractility
     */
    NOM_GRAD_PRESS_BLD_AORT_POS_MAX,
    /**
     * Resp Imedance RESP wave
     */
    NOM_RESP,
    /**
     * RR Respiration Rate
     */
    NOM_RESP_RATE,
    /**
     * sRRaw Setting: Airway Respiration Rate. Used by the Ohmeda Ventilator.
     */
    NOM_AWAY_RESP_RATE,
    /**
     * VC Vital Lung Capacity
     */
    NOM_CAPAC_VITAL,
    /**
     * COMP generic label Lung Compliance
     */
    NOM_COMPL_LUNG,
    /**
     * Cdyn Dynamic Lung Compliance
     */
    NOM_COMPL_LUNG_DYN,
    /**
     * Cstat Static Lung Compliance
     */
    NOM_COMPL_LUNG_STATIC,
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2,
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2_ET,
    /**
     * CO2 CO2 concentration
     */
    NOM_AWAY_CO2_INSP_MIN,
    /**
     * tcpCO2 Transcutaneous Carbon Dioxide Partial Pressure
     */
    NOM_CO2_TCUT,
    /**
     * tcpO2 Transcutaneous Oxygen Partial Pressure
     */
    NOM_O2_TCUT,
    /**
     * AWF Airway Flow Wave
     */
    NOM_FLOW_AWAY,
    /**
     * PEF Expiratory Peak Flow
     */
    NOM_FLOW_AWAY_EXP_MAX,
    /**
     * PIF Inspiratory Peak Flow
     */
    NOM_FLOW_AWAY_INSP_MAX,
    /**
     * VCO2 CO2 Production
     */
    NOM_FLOW_CO2_PROD_RESP,
    /**
     * T.I. Transthoracic Impedance
     */
    NOM_IMPED_TTHOR,
    /**
     * Pplat Plateau Pressure
     */
    NOM_PRESS_RESP_PLAT,
    /**
     * AWP Airway Pressure Wave
     */
    NOM_PRESS_AWAY,
    /**
     * sPmin Setting: Low Inspiratory Pressure
     */
    NOM_SETT_PRESS_AWAY_MIN,
    /**
     * sCPAP Setting: Continuous Positive Airway Pressure Value
     */
    NOM_PRESS_AWAY_CTS_POS,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_PRESS_AWAY_NEG_MAX,
    /**
     * iPEEP Intrinsic PEEP Breathing Pressure
     */
    NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC,
    /**
     * AWPin Airway Pressure Wave - measured in the inspiratory path
     */
    NOM_PRESS_AWAY_INSP,
    /**
     * sPIP Setting: Positive Inspiratory Pressure
     */
    NOM_PRESS_AWAY_INSP_MAX,
    /**
     * MnAwP Mean Airway Pressure. Printer Context
     */
    NOM_PRESS_AWAY_INSP_MEAN,
    /**
     * sIE 1: Setting: Inspiration to Expiration Ratio.
     */
    NOM_RATIO_IE,
    /**
     * Vd/Vt Ratio of Deadspace to Tidal Volume Vd/Vt
     */
    NOM_RATIO_AWAY_DEADSP_TIDAL,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_RES_AWAY,
    /**
     * Rexp Expiratory Resistance
     */
    NOM_RES_AWAY_EXP,
    /**
     * Rinsp Inspiratory Resistance
     */
    NOM_RES_AWAY_INSP,
    /**
     * ApneaD Apnea Time
     */
    NOM_TIME_PD_APNEA,
    /**
     * sTV Setting: Tidal Volume
     */
    NOM_VOL_AWAY_TIDAL,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VOL_MINUTE_AWAY,
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NOM_VOL_MINUTE_AWAY_EXP,
    /**
     * MINVOL Airway Minute Volum Inspiratory
     */
    NOM_VOL_MINUTE_AWAY_INSP,
    /**
     * FICO2 Airway CO2 inspiration
     */
    NOM_VENT_CONC_AWAY_CO2_INSP,
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2,
    /**
     * DeltaO2 relative Dead Space
     */
    NOM_VENT_CONC_AWAY_O2_DELTA,
    /**
     * PECO2 Partial O2 Venous
     */
    NOM_VENT_AWAY_CO2_EXP,
    /**
     * AWFin Airway Flow Wave - measured in the inspiratory path
     */
    NOM_VENT_FLOW_INSP,
    /**
     * VQI Ventilation Perfusion Index
     */
    NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX,
    /**
     * Poccl Occlusion Pressure
     */
    NOM_VENT_PRESS_OCCL,
    /**
     * sPEEP Setting: PEEP/CPAP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS,
    /**
     * Vd Dead Space Volume Vd
     */
    NOM_VENT_VOL_AWAY_DEADSP,
    /**
     * relVd relative Dead Space
     */
    NOM_VENT_VOL_AWAY_DEADSP_REL,
    /**
     * sTrVol Setting: Trigger Flow/Volume
     */
    NOM_SETT_VENT_VOL_LUNG_TRAPD,
    /**
     * sMMV Setting: Mandatory Minute Volume
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND,
    /**
     * DCO2 High Frequency Gas Transport Coefficient value
     */
    NOM_COEF_GAS_TRAN,
    /**
     * DES generic Desflurane label
     */
    NOM_CONC_AWAY_DESFL,
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL,
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH,
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL,
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL,
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_AWAY_DESFL_ET,
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL_ET,
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH_ET,
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL_ET,
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL_ET,
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O_ET,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_AWAY_DESFL_INSP,
    /**
     * ENF generic Enflurane label
     */
    NOM_CONC_AWAY_ENFL_INSP,
    /**
     * HAL generic Halothane label
     */
    NOM_CONC_AWAY_HALOTH_INSP,
    /**
     * SEV generic Sevoflurane label
     */
    NOM_CONC_AWAY_SEVOFL_INSP,
    /**
     * ISO generic Isoflurane label
     */
    NOM_CONC_AWAY_ISOFL_INSP,
    /**
     * N2O generic Nitrous Oxide label
     */
    NOM_CONC_AWAY_N2O_INSP,
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2_INSP,
    /**
     * DPosP Duration Above Base Pressure
     */
    NOM_VENT_TIME_PD_PPV,
    /**
     * PEinsp Respiration Pressure Plateau
     */
    NOM_VENT_PRESS_RESP_PLAT,
    /**
     * Leak Leakage
     */
    NOM_VENT_VOL_LEAK,
    /**
     * ALVENT Alveolar Ventilation ALVENT
     */
    NOM_VENT_VOL_LUNG_ALV,
    /**
     * O2 Generic oxigen measurement label
     */
    NOM_CONC_AWAY_O2_ET,
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2,
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2_ET,
    /**
     * N2 generic N2 label
     */
    NOM_CONC_AWAY_N2_INSP,
    /**
     * AGTs Anesthetic Agent - secondary agent
     */
    NOM_CONC_AWAY_AGENT,
    /**
     * etAGTs EndTidal secondary Anesthetic Agent
     */
    NOM_CONC_AWAY_AGENT_ET,
    /**
     * inAGTs Inspired secondary Anesthetic Agent
     */
    NOM_CONC_AWAY_AGENT_INSP,
    /**
     * CPP Cerebral Perfusion Pressure
     */
    NOM_PRESS_CEREB_PERF,
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN,
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_SYS,
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_DIA,
    /**
     * ICP Intra-cranial Pressure (ICP)
     */
    NOM_PRESS_INTRA_CRAN_MEAN,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SCORE_GLAS_COMA,
    /**
     * EyeRsp SubScore of the GCS: Eye Response
     */
    NOM_SCORE_EYE_SUBSC_GLAS_COMA,
    /**
     * MotRsp SubScore of the GCS: Motoric Response
     */
    NOM_SCORE_MOTOR_SUBSC_GLAS_COMA,
    /**
     * VblRsp SubScore of the GCS: Verbal Response
     */
    NOM_SCORE_SUBSC_VERBAL_GLAS_COMA,
    /**
     * HC Head Circumferince
     */
    NOM_CIRCUM_HEAD,
    /**
     * PRL Pupil Reaction Left eye - light reaction of left eye's pupil
     */
    NOM_TIME_PD_PUPIL_REACT_LEFT,
    /**
     * PRR Pupil Reaction Righteye - light reaction of right eye's pupil
     */
    NOM_TIME_PD_PUPIL_REACT_RIGHT,
    /**
     * RT EEG Right channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX,
    /**
     * EMG Electromyography
     */
    NOM_EMG_ELEC_POTL_MUSCL,
    /**
     * RT MDF Mean Dominant Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN,
    /**
     * RT PPF Peak Power Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK,
    /**
     * RT SEF Spectral Edge Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE,
    /**
     * RT TP Total Power - Right Side
     */
    NOM_EEG_PWR_SPEC_TOT,
    /**
     * RT %AL Percent Alpha - Right (RT) Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_REL,
    /**
     * RT %BE Percent Beta - Right Side
     */
    NOM_EEG_PWR_SPEC_BETA_REL,
    /**
     * RT %DL Percent Delta - Right Side
     */
    NOM_EEG_PWR_SPEC_DELTA_REL,
    /**
     * RT %TH Percent Theta - Right Side
     */
    NOM_EEG_PWR_SPEC_THETA_REL,
    /**
     * UrFl Urimeter - Urine Flow.
     */
    NOM_FLOW_URINE_INSTANT,
    /**
     * UrVol Urine Volume
     */
    NOM_VOL_URINE_BAL_PD,
    /**
     * BagVol Current fluid (Urine) in the Urine Bag
     */
    NOM_VOL_URINE_COL,
    /**
     * sDRate Setting: Infusion Pump Delivery Rate
     */
    NOM_SETT_FLOW_FLUID_PUMP,
    /**
     * AccVol Infusion Pump Accumulated volume. Measured value
     */
    NOM_VOL_INFUS_ACTUAL_TOTAL,
    /**
     * &pHa Adjusted pH in the arterial Blood
     */
    NOM_CONC_PH_ART,
    /**
     * PaCO2 Partial Pressure of arterial Carbon Dioxide
     */
    NOM_CONC_PCO2_ART,
    /**
     * PaO2 Partial O2 arterial
     */
    NOM_CONC_PO2_ART,
    /**
     * 'Hb Calculated Hemoglobin
     */
    NOM_CONC_HB_ART,
    /**
     * CaO2 Arterial Oxygen Content CaO2
     */
    NOM_CONC_HB_O2_ART,
    /**
     * &pHv Adjusted pH value in the venous Blood
     */
    NOM_CONC_PH_VEN,
    /**
     * &PvCO2 Computed PvCO2 at Patient Temperature
     */
    NOM_CONC_PCO2_VEN,
    /**
     * &PvO2 Adjusted PvO2 at Patient Temperature
     */
    NOM_CONC_PO2_VEN,
    /**
     * CvO2 Venous Oxygen Content
     */
    NOM_CONC_HB_O2_VEN,
    /**
     * UrpH pH value in the Urine
     */
    NOM_CONC_PH_URINE,
    /**
     * UrNa Natrium in Urine
     */
    NOM_CONC_NA_URINE,
    /**
     * SerNa Natrium in Serum
     */
    NOM_CONC_NA_SERUM,
    /**
     * pH pH in the Blood Plasma
     */
    NOM_CONC_PH_GEN,
    /**
     * 'HCO3 Calculated HCO3
     */
    NOM_CONC_HCO3_GEN,
    /**
     * Na Natrium (Sodium)
     */
    NOM_CONC_NA_GEN,
    /**
     * K Kalium (Potassium)
     */
    NOM_CONC_K_GEN,
    /**
     * Glu Glucose
     */
    NOM_CONC_GLU_GEN,
    /**
     * iCa ionized Calcium
     */
    NOM_CONC_CA_GEN,
    /**
     * &PCO2 Computed PCO2 at Patient Temperature
     */
    NOM_CONC_PCO2_GEN,
    /**
     * Cl Chloride
     */
    NOM_CONC_CHLORIDE_GEN,
    /**
     * 'BE,B Calculated Base Excess in Blood
     */
    NOM_BASE_EXCESS_BLD_ART,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_PO2_GEN,
    /**
     * Met-Hb MetHemoglobin
     */
    NOM_CONC_HB_MET_GEN,
    /**
     * CO-Hb Carboxy Hemoglobin
     */
    NOM_CONC_HB_CO_GEN,
    /**
     * Hct Haematocrit
     */
    NOM_CONC_HCT_GEN,
    /**
     * sFIO2 Setting: Inspired Oxygen Concentration
     */
    NOM_VENT_CONC_AWAY_O2_INSP,
    /**
     * sIMV Setting: Ventilation Frequency in IMV Mode
     */
    NOM_VENT_MODE_MAND_INTERMIT,
    /**
     * Trect Rectal Temperature
     */
    NOM_TEMP_RECT,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TEMP_BLD,
    /**
     * DeltaTemp Difference Temperature
     */
    NOM_TEMP_DIFF,
    /**
     * AWVex Expiratory Airway Volume Wave. Measured in l.
     */
    NOM_METRIC_NOS,
    /**
     * STindx ST Index
     */
    NOM_ECG_AMPL_ST_INDEX,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TIME_TCUT_SENSOR,
    /**
     * SensrT Sensor Temperature
     */
    NOM_TEMP_TCUT_SENSOR,
    /**
     * ITBV Intrathoracic Blood Volume
     */
    NOM_VOL_BLD_INTRA_THOR,
    /**
     * ITBVI Intrathoracic Blood Volume Index
     */
    NOM_VOL_BLD_INTRA_THOR_INDEX,
    /**
     * EVLW Extravascular Lung Water
     */
    NOM_VOL_LUNG_WATER_EXTRA_VASC,
    /**
     * EVLWI Extravascular Lung Water Index
     */
    NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX,
    /**
     * EDV End Diastolic Volume
     */
    NOM_VOL_GLOBAL_END_DIA,
    /**
     * EDVI End Diastolic Volume Index
     */
    NOM_VOL_GLOBAL_END_DIA_INDEX,
    /**
     * CFI Cardiac Function Index
     */
    NOM_CARD_FUNC_INDEX,
    /**
     * CCI Continuous Cardiac Output Index
     */
    NOM_OUTPUT_CARD_INDEX_CTS,
    /**
     * SI Stroke Index
     */
    NOM_VOL_BLD_STROKE_INDEX,
    /**
     * SVV Stroke Volume Variation
     */
    NOM_VOL_BLD_STROKE_VAR,
    /**
     * SR Suppression Ratio
     */
    NOM_EEG_RATIO_SUPPRN,
    /**
     * SQI Signal Quality Index
     */
    NOM_EEG_BIS_SIG_QUAL_INDEX,
    /**
     * BIS Bispectral Index
     */
    NOM_EEG_BISPECTRAL_INDEX,
    /**
     * tcGas Generic Term for the Transcutaneous Gases
     */
    NOM_GAS_TCUT,
    /**
     * MAC Airway MAC Concentration
     */
    NOM_CONC_AWAY_SUM_MAC_ET,
    /**
     * MAC Airway MAC Concentration
     */
    NOM_CONC_AWAY_SUM_MAC_INSP,
    /**
     * PVRI Pulmonary vascular Resistance PVRI
     */
    NOM_RES_VASC_PULM_INDEX,
    /**
     * LCWI Left Cardiac Work Index
     */
    NOM_WK_CARD_LEFT_INDEX,
    /**
     * RCWI Right Cardiac Work Index
     */
    NOM_WK_CARD_RIGHT_INDEX,
    /**
     * VO2I Oxygen Consumption Index VO2I
     */
    NOM_SAT_O2_CONSUMP_INDEX,
    /**
     * PB Barometric Pressure = Ambient Pressure
     */
    NOM_PRESS_AIR_AMBIENT,
    /**
     * Sp-vO2 Difference between Spo2 and SvO2
     */
    NOM_SAT_DIFF_O2_ART_VEN,
    /**
     * DO2 Oxygen Availability DO2
     */
    NOM_SAT_O2_DELIVER,
    /**
     * DO2I Oxygen Availability Index
     */
    NOM_SAT_O2_DELIVER_INDEX,
    /**
     * O2ER Oxygen Extraction Ratio
     */
    NOM_RATIO_SAT_O2_CONSUMP_DELIVER,
    /**
     * Qs/Qt Percent Alveolarvenous Shunt Qs/Qt
     */
    NOM_RATIO_ART_VEN_SHUNT,
    /**
     * BSA(D) BSA formula: Dubois
     */
    NOM_AREA_BODY_SURFACE,
    /**
     * LI Light Intenisty. SvO2
     */
    NOM_INTENS_LIGHT,
    /**
     * HeatPw NOM_DIM_MILLI_WATT
     */
    NOM_HEATING_PWR_TCUT_SENSOR,
    /**
     * InjVol Injectate Volume (Cardiac Output)
     */
    NOM_VOL_INJ,
    /**
     * ETVI ExtraVascular Thermo Volume Index. Cardiac Output.
     */
    NOM_VOL_THERMO_EXTRA_VASC_INDEX,
    /**
     * CathCt Generic Numeric Calculation Constant
     */
    NOM_NUM_CATHETER_CONST,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_PULS_OXIM_PERF_REL_LEFT,
    /**
     * Perf r Relative Perfusion Right label
     */
    NOM_PULS_OXIM_PERF_REL_RIGHT,
    /**
     * PLETHr PLETH wave (right)
     */
    NOM_PULS_OXIM_PLETH_RIGHT,
    /**
     * PLETHl PLETH wave (left)
     */
    NOM_PULS_OXIM_PLETH_LEFT,
    /**
     * BUN Blood Urea Nitrogen
     */
    NOM_CONC_BLD_UREA_NITROGEN,
    /**
     * 'BEecf Calculated Base Excess
     */
    NOM_CONC_BASE_EXCESS_ECF,
    /**
     * SpMV Spontaneous Minute Volume
     */
    NOM_VENT_VOL_MINUTE_AWAY_SPONT,
    /**
     * Ca-vO2 Arteriovenous Oxygen Difference Ca-vO2
     */
    NOM_CONC_DIFF_HB_O2_ATR_VEN,
    /**
     * Weight Patient Weight
     */
    NOM_PAT_WEIGHT,
    /**
     * Height Patient Height
     */
    NOM_PAT_HEIGHT,
    /**
     * MAC Minimum Alveolar Concentration
     */
    NOM_CONC_AWAY_MAC,
    /**
     * PlethT Pleth wave from Telemetry
     */
    NOM_PULS_OXIM_PLETH_TELE,
    /**
     * %SpO2T SpO2 parameter label as sourced by the Telemetry system
     */
    NOM_PULS_OXIM_SAT_O2_TELE,
    /**
     * PulseT Pulse parameter label as sourced by the Telemetry system
     */
    NOM_PULS_OXIM_PULS_RATE_TELE,
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1,
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_SYS,
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_DIA,
    /**
     * P1 Generic Pressure 1 (P1)
     */
    NOM_PRESS_GEN_1_MEAN,
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2,
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_SYS,
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_DIA,
    /**
     * P2 Generic Pressure 2 (P2)
     */
    NOM_PRESS_GEN_2_MEAN,
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3,
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3_SYS,
    /**
     * P3 Generic Pressure 3 (P3)
     */
    NOM_PRESS_GEN_3_MEAN,
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4,
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_SYS,
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_DIA,
    /**
     * P4 Generic Pressure 4 (P4)
     */
    NOM_PRESS_GEN_4_MEAN,
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1,
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_SYS,
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_DIA,
    /**
     * IC1 Intracranial Pressure 1 (IC1)
     */
    NOM_PRESS_INTRA_CRAN_1_MEAN,
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2,
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_SYS,
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_DIA,
    /**
     * IC2 Intracranial Pressure 2 (IC2)
     */
    NOM_PRESS_INTRA_CRAN_2_MEAN,
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL,
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_SYS,
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_DIA,
    /**
     * FAP Femoral Arterial Pressure (FAP)
     */
    NOM_PRESS_BLD_ART_FEMORAL_MEAN,
    /**
     * BAP Brachial Arterial Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL,
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_SYS,
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_DIA,
    /**
     * BAP Brachial Arterial Blood Pressure (BAP)
     */
    NOM_PRESS_BLD_ART_BRACHIAL_MEAN,
    /**
     * Tvesic Temperature of the Urine fluid
     */
    NOM_TEMP_VESICAL,
    /**
     * Tcereb Cerebral Temperature
     */
    NOM_TEMP_CEREBRAL,
    /**
     * Tamb Ambient Temperature
     */
    NOM_TEMP_AMBIENT,
    /**
     * T1 Generic Temperature 1 (T1)
     */
    NOM_TEMP_GEN_1,
    /**
     * T2 Generic Temperature 2 (T2)
     */
    NOM_TEMP_GEN_2,
    /**
     * T3 Generic Temperature 3 (T3)
     */
    NOM_TEMP_GEN_3,
    /**
     * T4 Generic Temperature 4 (T4)
     */
    NOM_TEMP_GEN_4,
    /**
     * sTVin Setting: inspired Tidal Volume
     */
    NOM_SETT_VOL_AWAY_INSP_TIDAL,
    /**
     * TVexp expired Tidal Volume
     */
    NOM_VOL_AWAY_EXP_TIDAL,
    /**
     * RRspir Respiration Rate from Spirometry
     */
    NOM_AWAY_RESP_RATE_SPIRO,
    /**
     * PPV Pulse Pressure Variation
     */
    NOM_PULS_PRESS_VAR,
    /**
     * Pulse Pulse from NBP
     */
    NOM_PRESS_BLD_NONINV_PULS_RATE,
    /**
     * MRR Mandatory Respiratory Rate
     */
    NOM_VENT_RESP_RATE_MAND,
    /**
     * MTV Mandatory Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_MAND,
    /**
     * SpTV Spontaneuous Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_SPONT,
    /**
     * cTnI Cardiac Troponin I
     */
    NOM_CARDIAC_TROPONIN_I,
    /**
     * CPB Cardio Pulmonary Bypass Flag
     */
    NOM_CARDIO_PULMONARY_BYPASS_MODE,
    /**
     * BNP Cardiac Brain Natriuretic Peptide
     */
    NOM_BNP,
    /**
     * sPltTi Setting: Plateau Time
     */
    NOM_SETT_TIME_PD_RESP_PLAT,
    /**
     * ScvO2 Central Venous Oxygen Saturation
     */
    NOM_SAT_O2_VEN_CENT,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SNR,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_HUMID,
    /**
     * GEF Global Ejection Fraction
     */
    NOM_FRACT_EJECT,
    /**
     * PVPI Pulmonary Vascular Permeability Index
     */
    NOM_PERM_VASC_PULM_INDEX,
    /**
     * pToral Predictive Oral Temperature
     */
    NOM_TEMP_ORAL_PRED,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TEMP_RECT_PRED,
    /**
     * pTaxil Predictive Axillary Temperature
     */
    NOM_TEMP_AXIL_PRED,
    /**
     * Air T Air Temperature in the Incubator
     */
    NOM_TEMP_AIR_INCUB,
    /**
     * Perf T Perf from Telemetry
     */
    NOM_PULS_OXIM_PERF_REL_TELE,
    /**
     * RLShnt Right-to-Left Heart Shunt
     */
    NOM_SHUNT_RIGHT_LEFT,
    /**
     * QT-HR QT HEARTRATE
     */
    NOM_ECG_TIME_PD_QT_HEART_RATE,
    /**
     * QT Bsl
     */
    NOM_ECG_TIME_PD_QT_BASELINE,
    /**
     * DeltaQTc
     */
    NOM_ECG_TIME_PD_QTc_DELTA,
    /**
     * QTHRBl QT BASELINE HEARTRATE
     */
    NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE,
    /**
     * pHc pH value in the capillaries
     */
    NOM_CONC_PH_CAP,
    /**
     * PcCO2 Partial CO2 in the capillaries
     */
    NOM_CONC_PCO2_CAP,
    /**
     * PcO2 Partial O2 in the capillaries
     */
    NOM_CONC_PO2_CAP,
    /**
     * iMg ionized Magnesium
     */
    NOM_CONC_MG_ION,
    /**
     * SerMg Magnesium in Serum
     */
    NOM_CONC_MG_SER,
    /**
     * tSerCa total of Calcium in Serum
     */
    NOM_CONC_tCA_SER,
    /**
     * SerPho Phosphat in Serum
     */
    NOM_CONC_P_SER,
    /**
     * SerCl Clorid in Serum
     */
    NOM_CONC_CHLOR_SER,
    /**
     * Fe Ferrum
     */
    NOM_CONC_FE_GEN,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_ALB_SER,
    /**
     * 'SaO2 Calculated SaO2
     */
    NOM_SAT_O2_ART_CALC,
    /**
     * HbF Fetal Hemoglobin
     */
    NOM_CONC_HB_FETAL,
    /**
     * Plts Platelets (thrombocyte count)
     */
    NOM_PLTS_CNT,
    /**
     * WBC White Blood Count (leucocyte count)
     */
    NOM_WB_CNT,
    /**
     * RBC Red Blood Count (erithrocyte count)
     */
    NOM_RB_CNT,
    /**
     * RC Reticulocyte Count
     */
    NOM_RET_CNT,
    /**
     * PlOsm Plasma Osmolarity
     */
    NOM_PLASMA_OSM,
    /**
     * CreaCl Creatinine Clearance
     */
    NOM_CONC_CREA_CLR,
    /**
     * NsLoss Nitrogen Balance
     */
    NOM_NSLOSS,
    /**
     * Chol Cholesterin
     */
    NOM_CONC_CHOLESTEROL,
    /**
     * TGL Triglyzeride
     */
    NOM_CONC_TGL,
    /**
     * HDL High Density Lipoprotein
     */
    NOM_CONC_HDL,
    /**
     * LDL Low Density Lipoprotein
     */
    NOM_CONC_LDL,
    /**
     * Urea Urea used by the i-Stat
     */
    NOM_CONC_UREA_GEN,
    /**
     * Crea Creatinine - Measured Value by the i-Stat Module
     */
    NOM_CONC_CREA,
    /**
     * Lact Lactate. SMeasured value by the i-Stat module
     */
    NOM_CONC_LACT,
    /**
     * tBili total Bilirubin
     */
    NOM_CONC_BILI_TOT,
    /**
     * SerPro (Total) Protein in Serum
     */
    NOM_CONC_PROT_SER,
    /**
     * tPro Total Protein
     */
    NOM_CONC_PROT_TOT,
    /**
     * dBili direct Bilirubin
     */
    NOM_CONC_BILI_DIRECT,
    /**
     * LDH Lactate Dehydrogenase
     */
    NOM_CONC_LDH,
    /**
     * ESR Erithrocyte sedimentation rate
     */
    NOM_ES_RATE,
    /**
     * PCT Procalcitonin
     */
    NOM_CONC_PCT,
    /**
     * CK-MM Creatine Cinase of type muscle
     */
    NOM_CONC_CREA_KIN_MM,
    /**
     * SerCK Creatinin Kinase
     */
    NOM_CONC_CREA_KIN_SER,
    /**
     * CK-MB Creatine Cinase of type muscle-brain
     */
    NOM_CONC_CREA_KIN_MB,
    /**
     * CHE Cholesterinesterase
     */
    NOM_CONC_CHE,
    /**
     * CRP C-reactive Protein
     */
    NOM_CONC_CRP,
    /**
     * AST Aspartin - Aminotransferase
     */
    NOM_CONC_AST,
    /**
     * AP Alkalische Phosphatase
     */
    NOM_CONC_AP,
    /**
     * alphaA Alpha Amylase
     */
    NOM_CONC_ALPHA_AMYLASE,
    /**
     * GPT Glutamic-Pyruvic-Transaminase
     */
    NOM_CONC_GPT,
    /**
     * GOT Glutamic Oxaloacetic Transaminase
     */
    NOM_CONC_GOT,
    /**
     * GGT Gamma GT = Gamma Glutamyltranspeptidase
     */
    NOM_CONC_GGT,
    /**
     * ACT Activated Clotting Time. Measured value by the i-Stat module
     */
    NOM_TIME_PD_ACT,
    /**
     * PT Prothrombin Time
     */
    NOM_TIME_PD_PT,
    /**
     * PT INR Prothrombin Time - International Normalized Ratio
     */
    NOM_PT_INTL_NORM_RATIO,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_TIME_PD_aPTT_WB,
    /**
     * aPTTPE aPTT Plasma Equivalent Time
     */
    NOM_TIME_PD_aPTT_PE,
    /**
     * PT WB Prothrombin Time (Blood)
     */
    NOM_TIME_PD_PT_WB,
    /**
     * PT PE Prothrombin Time (Plasma)
     */
    NOM_TIME_PD_PT_PE,
    /**
     * TT Thrombin Time
     */
    NOM_TIME_PD_THROMBIN,
    /**
     * CT Coagulation Time
     */
    NOM_TIME_PD_COAGULATION,
    /**
     * Quick Thromboplastine Time
     */
    NOM_TIME_PD_THROMBOPLAS,
    /**
     * FeNa Fractional Excretion of Sodium
     */
    NOM_FRACT_EXCR_NA,
    /**
     * UrUrea Urine Urea
     */
    NOM_CONC_UREA_URINE,
    /**
     * UrCrea Urine Creatinine
     */
    NOM_CONC_CREA_URINE,
    /**
     * UrK Urine Potassium
     */
    NOM_CONC_K_URINE,
    /**
     * UrKEx Urinary Potassium Excretion
     */
    NOM_CONC_K_URINE_EXCR,
    /**
     * UrOsm Urine Osmolarity
     */
    NOM_CONC_OSM_URINE,
    /**
     * UrCl Clorid in Urine
     */
    NOM_CONC_CHLOR_URINE,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_CONC_PRO_URINE,
    /**
     * UrCa Calzium in Urine
     */
    NOM_CONC_CA_URINE,
    /**
     * UrDens Density of the Urine fluid
     */
    NOM_FLUID_DENS_URINE,
    /**
     * UrHb Hemoglobin (Urine)
     */
    NOM_CONC_HB_URINE,
    /**
     * UrGlu Glucose in Urine
     */
    NOM_CONC_GLU_URINE,
    /**
     * 'ScO2 Calculated ScO2
     */
    NOM_SAT_O2_CAP_CALC,
    /**
     * 'AnGap Calculated AnionGap
     */
    NOM_CONC_AN_GAP_CALC,
    /**
     * SpO2pr Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL,
    /**
     * SpO2po Oxigen Saturation
     */
    NOM_PULS_OXIM_SAT_O2_POST_DUCTAL,
    /**
     * PerfPo Relative Perfusion Left
     */
    NOM_PULS_OXIM_PERF_REL_POST_DUCTAL,
    /**
     * PerfPr Relative Perfusion Left
     */
    NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL,
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5,
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_SYS,
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_DIA,
    /**
     * P5 Generic Pressure 5 (P5)
     */
    NOM_PRESS_GEN_5_MEAN,
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6,
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_SYS,
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_DIA,
    /**
     * P6 Generic Pressure 6 (P6)
     */
    NOM_PRESS_GEN_6_MEAN,
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7,
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7_SYS,
    /**
     * P7 Generic Pressure 7 (P7)
     */
    NOM_PRESS_GEN_7_MEAN,
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8,
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_SYS,
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_DIA,
    /**
     * P8 Generic Pressure 8 (P8)
     */
    NOM_PRESS_GEN_8_MEAN,
    /**
     * Rf-I ST Reference Value for Lead I
     */
    NOM_ECG_AMPL_ST_BASELINE_I,
    /**
     * Rf-II ST Reference Value for Lead II
     */
    NOM_ECG_AMPL_ST_BASELINE_II,
    /**
     * Rf-V1 ST Reference Value for Lead V1
     */
    NOM_ECG_AMPL_ST_BASELINE_V1,
    /**
     * Rf-V2 ST Reference Value for Lead V2
     */
    NOM_ECG_AMPL_ST_BASELINE_V2,
    /**
     * Rf-V3 ST Reference Value for Lead V3
     */
    NOM_ECG_AMPL_ST_BASELINE_V3,
    /**
     * Rf-V4 ST Reference Value for Lead V4
     */
    NOM_ECG_AMPL_ST_BASELINE_V4,
    /**
     * Rf-V5 ST Reference Value for Lead V5
     */
    NOM_ECG_AMPL_ST_BASELINE_V5,
    /**
     * Rf-V6 ST Reference Value for Lead V6
     */
    NOM_ECG_AMPL_ST_BASELINE_V6,
    /**
     * Rf-III ST Reference Value for Lead III
     */
    NOM_ECG_AMPL_ST_BASELINE_III,
    /**
     * Rf-aVR ST Reference Value for Lead aVR
     */
    NOM_ECG_AMPL_ST_BASELINE_AVR,
    /**
     * Rf-aVL ST Reference Value for Lead aVL
     */
    NOM_ECG_AMPL_ST_BASELINE_AVL,
    /**
     * Rf-aVF ST Reference Value for Lead aVF
     */
    NOM_ECG_AMPL_ST_BASELINE_AVF,
    /**
     * Age actual patient age. measured in years
     */
    NOM_AGE,
    /**
     * G.Age Gestational age for neonatal
     */
    NOM_AGE_GEST,
    /**
     * r Correlation Coefficient
     */
    NOM_AWAY_CORR_COEF,
    /**
     * SpAWRR Spontaneous Airway Respiration Rate
     */
    NOM_AWAY_RESP_RATE_SPONT,
    /**
     * TC Time Constant
     */
    NOM_AWAY_TC,
    /**
     * Length Length for neonatal/pediatric
     */
    NOM_BIRTH_LENGTH,
    /**
     * RSBI Rapid Shallow Breathing Index
     */
    NOM_BREATH_RAPID_SHALLOW_INDEX,
    /**
     * C20/C Overdistension Index
     */
    NOM_C20_PER_C_INDEX,
    /**
     * HI Heart Contractility Index
     */
    NOM_CARD_CONTRACT_HEATHER_INDEX,
    /**
     * ALP Alveolarproteinose Rosen-Castleman-Liebow- Syndrom
     */
    NOM_CONC_ALP,
    /**
     * iCa(N) ionized Calcium Normalized
     */
    NOM_CONC_CA_GEN_NORM,
    /**
     * SerCa Calcium in Serum
     */
    NOM_CONC_CA_SER,
    /**
     * tCO2 total of CO2 - result of Blood gas Analysis
     */
    NOM_CONC_CO2_TOT,
    /**
     * 'tCO2 Calculated total CO2
     */
    NOM_CONC_CO2_TOT_CALC,
    /**
     * SCrea Serum Creatinine
     */
    NOM_CONC_CREA_SER,
    /**
     * SpRR Spontaneous Respiration Rate
     */
    NOM_RESP_RATE_SPONT,
    /**
     * SerGlo Globulin in Serum
     */
    NOM_CONC_GLO_SER,
    /**
     * SerGlu Glucose in Serum
     */
    NOM_CONC_GLU_SER,
    /**
     * MCHC Mean Corpuscular Hemoglobin Concentration
     */
    NOM_CONC_HB_CORP_MEAN,
    /**
     * SerK Kalium (Potassium) in Serum
     */
    NOM_CONC_K_SER,
    /**
     * UrNaEx Urine Sodium Excretion
     */
    NOM_CONC_NA_EXCR,
    /**
     * &PaCO2 Computed PaCO2 at Patient Temperature on the arterial blood
     */
    NOM_CONC_PCO2_ART_ADJ,
    /**
     * &PcCO2 Computed PcO2 at Patient Temperature
     */
    NOM_CONC_PCO2_CAP_ADJ,
    /**
     * &pHc Adjusted pH value in the capillaries
     */
    NOM_CONC_PH_CAP_ADJ,
    /**
     * &pH Adjusted pH at &Patient Temperature
     */
    NOM_CONC_PH_GEN_ADJ,
    /**
     * &PaO2 Adjusted PaO2 at Patient Temperature on the arterial blood
     */
    NOM_CONC_PO2_ART_ADJ,
    /**
     * &PcO2 Adjusted PcO2 at Patient Temperature
     */
    NOM_CONC_PO2_CAP_ADJ,
    /**
     * COsm Osmolar Clearance
     */
    NOM_CREA_OSM,
    /**
     * BSI Burst Suppression Indicator
     */
    NOM_EEG_BURST_SUPPRN_INDEX,
    /**
     * LSCALE Scale of the Left Channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT,
    /**
     * RSCALE Scale of the Right Channel EEG wave
     */
    NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT,
    /**
     * LT MPF Median Power Frequency - Left Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT,
    /**
     * RT MPF Median Power Frequency - Right Side
     */
    NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT,
    /**
     * LT AL Absolute Alpha - Left Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT,
    /**
     * RT AL Absolute Alpha - Right Side
     */
    NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT,
    /**
     * LT BE Absolute Beta - Left Side
     */
    NOM_EEG_PWR_SPEC_BETA_ABS_LEFT,
    /**
     * RT BE Absolute Beta - Right Side
     */
    NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT,
    /**
     * LT DL Absolute Delta - Left Side
     */
    NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT,
    /**
     * RT DL Absolute Delta - Right Side
     */
    NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT,
    /**
     * LT TH Absolute Theta - Left Side
     */
    NOM_EEG_PWR_SPEC_THETA_ABS_LEFT,
    /**
     * RT TH Absolute Theta - Right Side
     */
    NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT,
    /**
     * AAI A-Line ARX Index
     */
    NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI,
    /**
     * O2EI Oxygen Extraction Index
     */
    NOM_EXTRACT_O2_INDEX,
    /**
     * sfgAir Setting: Total fresh gas Air flow on the mixer
     */
    NOM_SETT_FLOW_AWAY_AIR,
    /**
     * eeFlow Expiratory Peak Flow
     */
    NOM_FLOW_AWAY_EXP_ET,
    /**
     * SpPkFl Spontaneous Peak Flow
     */
    NOM_FLOW_AWAY_MAX_SPONT,
    /**
     * sfgFl Setting: Total fresh gas Flow on the mixer
     */
    NOM_SETT_FLOW_AWAY_TOT,
    /**
     * VCO2ti CO2 Tidal Production
     */
    NOM_FLOW_CO2_PROD_RESP_TIDAL,
    /**
     * U/O Daily Urine output
     */
    NOM_FLOW_URINE_PREV_24HR,
    /**
     * CH2O Free Water Clearance
     */
    NOM_FREE_WATER_CLR,
    /**
     * MCH Mean Corpuscular Hemoglobin. Is the erithrocyte hemoglobin content
     */
    NOM_HB_CORP_MEAN,
    /**
     * Power Power requ'd to set the Air&Pat Temp in the incubator
     */
    NOM_HEATING_PWR_INCUBATOR,
    /**
     * ACI Accelerated Cardiac Index
     */
    NOM_OUTPUT_CARD_INDEX_ACCEL,
    /**
     * PTC Post Tetatic Count stimulation
     */
    NOM_PTC_CNT,
    /**
     * PlGain Pleth Gain
     */
    NOM_PULS_OXIM_PLETH_GAIN,
    /**
     * RVrat Rate Volume Ratio
     */
    NOM_RATIO_AWAY_RATE_VOL_AWAY,
    /**
     * BUN/cr BUN Creatinine Ratio
     */
    NOM_RATIO_BUN_CREA,
    /**
     * 'B/Cre Ratio BUN/Creatinine. Calculated value by the i-Stat module
     */
    NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC,
    /**
     * 'U/Cre Ratio Urea/Creatinine. Calculated value by the i-Stat module
     */
    NOM_RATIO_CONC_URINE_CREA_CALC,
    /**
     * U/SCr Urine Serum Creatinine Ratio
     */
    NOM_RATIO_CONC_URINE_CREA_SER,
    /**
     * UrNa/K Urine Sodium/Potassium Ratio
     */
    NOM_RATIO_CONC_URINE_NA_K,
    /**
     * PaFIO2 PaO2 to FIO2 ratio. Expressed in mmHg to % ratio
     */
    NOM_RATIO_PaO2_FIO2,
    /**
     * PTrat Prothrombin Time Ratio
     */
    NOM_RATIO_TIME_PD_PT,
    /**
     * PTTrat Activated Partial Thromboplastin Time Ratio
     */
    NOM_RATIO_TIME_PD_PTT,
    /**
     * TOFrat Train Of Four (TOF) ratio
     */
    NOM_RATIO_TRAIN_OF_FOUR,
    /**
     * U/POsm Urine Plasma Osmolarity Ratio
     */
    NOM_RATIO_URINE_SER_OSM,
    /**
     * Rdyn Dynamic Lung Resistance
     */
    NOM_RES_AWAY_DYN,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_RESP_BREATH_ASSIST_CNT,
    /**
     * REF Right Heart Ejection Fraction
     */
    NOM_RIGHT_HEART_FRACT_EJECT,
    /**
     * RemTi Remaining Time until next stimulation
     */
    NOM_TIME_PD_EVOK_REMAIN,
    /**
     * ExpTi Expiratory Time
     */
    NOM_TIME_PD_EXP,
    /**
     * Elapse Time to Elapse Counter
     */
    NOM_TIME_PD_FROM_LAST_MSMT,
    /**
     * InsTi Spontaneous Inspiration Time
     */
    NOM_TIME_PD_INSP,
    /**
     * KCT Kaolin cephalin time
     */
    NOM_TIME_PD_KAOLIN_CEPHALINE,
    /**
     * PTT Partial Thromboplastin Time
     */
    NOM_TIME_PD_PTT,
    /**
     * sRepTi Setting: Preset Train Of Four (Slow TOF) repetition time
     */
    NOM_SETT_TIME_PD_TRAIN_OF_FOUR,
    /**
     * TOF1 TrainOf Four (TOF) first response value TOF1
     */
    NOM_TRAIN_OF_FOUR_1,
    /**
     * TOF2 TrainOf Four (TOF) first response value TOF2
     */
    NOM_TRAIN_OF_FOUR_2,
    /**
     * TOF3 TrainOf Four (TOF) first response value TOF3
     */
    NOM_TRAIN_OF_FOUR_3,
    /**
     * TOF4 TrainOf Four (TOF) first response value TOF4
     */
    NOM_TRAIN_OF_FOUR_4,
    /**
     * TOFcnt Train Of Four (TOF) count - Number of TOF responses.
     */
    NOM_TRAIN_OF_FOUR_CNT,
    /**
     * Twitch Twitch height of the 1Hz/0.1Hz stimulation response
     */
    NOM_TWITCH_AMPL,
    /**
     * SrUrea Serum Urea
     */
    NOM_UREA_SER,
    /**
     * sUrTi Setting: Preset period of time for the UrVol numeric
     */
    NOM_SETT_URINE_BAL_PD,
    /**
     * PtVent Parameter which informs whether the Patient is ventilated
     */
    NOM_VENT_ACTIVE,
    /**
     * HFVAmp High Frequency Ventilation Amplitude
     */
    NOM_VENT_AMPL_HFV,
    /**
     * i-eAGT Inspired - EndTidal Agent
     */
    NOM_VENT_CONC_AWAY_AGENT_DELTA,
    /**
     * i-eDES Inspired - EndTidal Desfluran
     */
    NOM_VENT_CONC_AWAY_DESFL_DELTA,
    /**
     * i-eENF Inspired - EndTidal Enfluran
     */
    NOM_VENT_CONC_AWAY_ENFL_DELTA,
    /**
     * i-eHAL Inspired - EndTidal Halothane
     */
    NOM_VENT_CONC_AWAY_HALOTH_DELTA,
    /**
     * i-eISO Inspired - EndTidal Isofluran
     */
    NOM_VENT_CONC_AWAY_ISOFL_DELTA,
    /**
     * i-eN2O Inspired - EndTidal N2O
     */
    NOM_VENT_CONC_AWAY_N2O_DELTA,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VENT_CONC_AWAY_O2_CIRCUIT,
    /**
     * i-eSEV Inspired - EndTidal Sevofluran
     */
    NOM_VENT_CONC_AWAY_SEVOFL_DELTA,
    /**
     * loPEEP Alarm Limit: Low PEEP/CPAP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO,
    /**
     * sPSV Setting: Pressure Support Ventilation
     */
    NOM_SETT_VENT_PRESS_AWAY_PV,
    /**
     * RiseTi Rise Time
     */
    NOM_VENT_TIME_PD_RAMP,
    /**
     * HFTVin Inspired High Frequency Tidal Volume
     */
    NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV,
    /**
     * HFVTV High Frequency Fraction Ventilation Tidal Volume
     */
    NOM_VENT_VOL_TIDAL_HFV,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VENT_VOL_TIDAL_SIGH,
    /**
     * SpTVex Spontaenous Expired Tidal Volume
     */
    NOM_VOL_AWAY_EXP_TIDAL_SPONT,
    /**
     * TVPSV Tidal Volume (TV) in Pressure Support Ventilation mode
     */
    NOM_VOL_AWAY_TIDAL_PSV,
    /**
     * MCV Mean Corpuscular Volume
     */
    NOM_VOL_CORP_MEAN,
    /**
     * TFC Thoracic Fluid Content
     */
    NOM_VOL_FLUID_THORAC,
    /**
     * TFI Thoracic Fluid Content Index
     */
    NOM_VOL_FLUID_THORAC_INDEX,
    /**
     * AGTLev Liquid level in the anesthetic agent bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_AGENT,
    /**
     * DESLev Liquid level in the DESflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_DESFL,
    /**
     * ENFLev Liquid level in the ENFlurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_ENFL,
    /**
     * HALLev Liquid level in the HALothane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH,
    /**
     * ISOLev Liquid level in the ISOflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL,
    /**
     * SEVLev Liquid level in the SEVoflurane bottle
     */
    NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL,
    /**
     * HFMVin Inspired High Frequency Mandatory Minute Volume
     */
    NOM_VOL_MINUTE_AWAY_INSP_HFV,
    /**
     * tUrVol Total Urine Volume of the current measurement period
     */
    NOM_VOL_URINE_BAL_PD_INSTANT,
    /**
     * UrVSht Urimeter - Urine Shift Volume.
     */
    NOM_VOL_URINE_SHIFT,
    /**
     * ESVI End Systolic Volume Index
     */
    NOM_VOL_VENT_L_END_SYS_INDEX,
    /**
     * BagWgt Weight of the Urine Disposable Bag
     */
    NOM_WEIGHT_URINE_COL,
    /**
     * sAADel Setting: Apnea Ventilation Delay
     */
    NOM_SETT_APNEA_ALARM_DELAY,
    /**
     * sARR Setting: Apnea Respiration Rate
     */
    NOM_SETT_AWAY_RESP_RATE_APNEA,
    /**
     * sHFVRR Setting: High Frequency Ventilation Respiration Rate
     */
    NOM_SETT_AWAY_RESP_RATE_HFV,
    /**
     * sChrge Setting: Preset stimulation charge
     */
    NOM_SETT_EVOK_CHARGE,
    /**
     * sCurnt Setting: Preset stimulation current
     */
    NOM_SETT_EVOK_CURR,
    /**
     * sExpFl Setting: Expiratory Flow
     */
    NOM_SETT_FLOW_AWAY_EXP,
    /**
     * sHFVFl Setting: High Freqyency Ventilation Flow
     */
    NOM_SETT_FLOW_AWAY_HFV,
    /**
     * sInsFl Setting: Inspiratory Flow.
     */
    NOM_SETT_FLOW_AWAY_INSP,
    /**
     * sAPkFl Setting: Apnea Peak Flow
     */
    NOM_SETT_FLOW_AWAY_INSP_APNEA,
    /**
     * sHFVAm Setting: HFV Amplitude (Peak to Peak Pressure)
     */
    NOM_SETT_HFV_AMPL,
    /**
     * loPmax Setting: Low Maximum Airway Pressure Alarm Setting.
     */
    NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO,
    /**
     * sPVE Setting: Pressure Ventilation E component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_EXP_PV,
    /**
     * sAPVE Setting: Apnea Pressure Ventilation E component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_EXP_PV_APNEA,
    /**
     * sPVI Setting: Pressure Ventilation I component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_INSP_PV,
    /**
     * sAPVI Setting: Apnea Pressure Ventilation I component of I:E Ratio
     */
    NOM_SETT_RATIO_IE_INSP_PV_APNEA,
    /**
     * sSens Setting: Assist Sensitivity. Used by the Bear 1000 ventilator.
     */
    NOM_SETT_SENS_LEVEL,
    /**
     * sPulsD Setting: Preset stimulation impulse duration
     */
    NOM_SETT_TIME_PD_EVOK,
    /**
     * sCycTi Setting: Cycle Time
     */
    NOM_SETT_TIME_PD_MSMT,
    /**
     * sO2Mon Setting: O2 Monitoring
     */
    NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE,
    /**
     * sBkgFl Setting: Background Flow Setting. Range is 2 - 30 l/min
     */
    NOM_SETT_VENT_AWAY_FLOW_BACKGROUND,
    /**
     * sBasFl Setting: Flow-by Base Flow
     */
    NOM_SETT_VENT_AWAY_FLOW_BASE,
    /**
     * sSenFl Setting: Flow-by Sensitivity Flow
     */
    NOM_SETT_VENT_AWAY_FLOW_SENSE,
    /**
     * sPincR Setting: Pressure Increase Rate
     */
    NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE,
    /**
     * sAFIO2 Setting: Apnea Inspired O2 Concentration
     */
    NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA,
    /**
     * sAPVO2 Setting: Apnea Pressure Ventilation Oxygen Concentration
     */
    NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA,
    /**
     * highO2 Alarm Limit. High Oxygen (O2) Alarm Limit
     */
    NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI,
    /**
     * lowO2 Alarm Limit: Low Oxygen (O2) Alarm Limit
     */
    NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO,
    /**
     * sFlow Setting: Flow
     */
    NOM_SETT_VENT_FLOW,
    /**
     * sFlas Setting: Flow Assist level for the CPAP mode
     */
    NOM_SETT_VENT_FLOW_AWAY_ASSIST,
    /**
     * sTrgFl Setting: Flow Trigger - delivered by the Evita 2 Vuelink Driver
     */
    NOM_SETT_VENT_FLOW_INSP_TRIG,
    /**
     * sGasPr Setting: Gas Sample point for the oxygen measurement
     */
    NOM_SETT_VENT_GAS_PROBE_POSN,
    /**
     * sCMV Setting: Controlled mechanical ventilation
     */
    NOM_SETT_VENT_MODE_MAND_CTS_ONOFF,
    /**
     * sEnSgh Setting: Enable Sigh
     */
    NOM_SETT_VENT_MODE_SIGH,
    /**
     * sSIMV Setting: Synchronized intermittent mandatory ventilation
     */
    NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT,
    /**
     * sO2Cal Setting: O2 Calibration
     */
    NOM_SETT_VENT_O2_CAL_MODE,
    /**
     * sO2Pr Setting: Gas sample point for oxygen measurement
     */
    NOM_SETT_VENT_O2_PROBE_POSN,
    /**
     * sO2Suc Setting: Suction Oxygen Concentration
     */
    NOM_SETT_VENT_O2_SUCTION_MODE,
    /**
     * sSPEEP Setting: Pressure Support PEEP
     */
    NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT,
    /**
     * sPlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_PRESS_AWAY_EXP_APRV,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VENT_PRESS_AWAY_INSP_APRV,
    /**
     * highP Alarm Limit: High Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI,
    /**
     * sAPVhP Setting: Apnea Pressure Ventilation High Airway Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA,
    /**
     * sAPVcP Setting: Apnea Pressure Ventilation Control Pressure
     */
    NOM_SETT_VENT_PRESS_AWAY_PV_APNEA,
    /**
     * sustP Alarm Limit: Sustained Pressure Alarm Limit.
     */
    NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI,
    /**
     * sfmax Setting: Panting Limit
     */
    NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT,
    /**
     * sIPPV Setting: Ventilation Frequency in IPPV Mode
     */
    NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP,
    /**
     * sAPVRR Setting: Apnea Pressure Ventilation Respiration Rate
     */
    NOM_SETT_VENT_RESP_RATE_PV_APNEA,
    /**
     * sSghNr Setting: Multiple Sigh Number
     */
    NOM_SETT_VENT_SIGH_MULT_RATE,
    /**
     * sSghR Setting: Sigh Rate
     */
    NOM_SETT_VENT_SIGH_RATE,
    /**
     * sExpTi Setting: Exhaled Time
     */
    NOM_SETT_VENT_TIME_PD_EXP,
    /**
     * sTlow Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_TIME_PD_EXP_APRV,
    /**
     * sInsTi Setting: Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP,
    /**
     * sThigh Setting: part of the Evita 4 Airway Pressure Release Ventilation Mode
     */
    NOM_SETT_VENT_TIME_PD_INSP_APRV,
    /**
     * sPVinT Setting: Pressure Ventilation Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP_PV,
    /**
     * sAPVTi Setting: Apnea Pressure Ventilation Inspiratory Time
     */
    NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA,
    /**
     * sALMRT Setting: Alarm Percentage on Rise Time.
     */
    NOM_SETT_VENT_TIME_PD_RAMP_AL,
    /**
     * sVolas Setting: Volume Assist level for the CPAP mode
     */
    NOM_SETT_VENT_VOL_AWAY_ASSIST,
    /**
     * sVmax Setting: Volume Warning - delivered by the Evita 2 Vuelink Driver
     */
    NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF,
    /**
     * highMV Alarm Limit: High Minute Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI,
    /**
     * lowMV Alarm Limit: Low Minute Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO,
    /**
     * highTV Alarm Limit: High Tidal Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI,
    /**
     * lowTV Alarm Limit: Low Tidal Volume Alarm Limit
     */
    NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO,
    /**
     * sATV Setting: Apnea Tidal Volume
     */
    NOM_SETT_VOL_AWAY_TIDAL_APNEA,
    /**
     * sTVap Setting: Applied Tidal Volume.
     */
    NOM_SETT_VOL_AWAY_TIDAL_APPLIED,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_SETT_VOL_MINUTE_ALARM_DELAY,
    /**
     * StO2 O2 Saturation (tissue)
     */
    NOM_SAT_O2_TISSUE,
    /**
     * CSI
     */
    NOM_CEREB_STATE_INDEX,
    /**
     * SO2 1 O2 Saturation 1 (generic)
     */
    NOM_SAT_O2_GEN_1,
    /**
     * SO2 2 O2 Saturation 2 (generic)
     */
    NOM_SAT_O2_GEN_2,
    /**
     * SO2 3 O2 Saturation 3 (generic)
     */
    NOM_SAT_O2_GEN_3,
    /**
     * SO2 4 O2 Saturation 4 (generic)
     */
    NOM_SAT_O2_GEN_4,
    /**
     * T1Core Core Temperature 1 (generic)
     */
    NOM_TEMP_CORE_GEN_1,
    /**
     * T2Core Core Temperature 2 (generic)
     */
    NOM_TEMP_CORE_GEN_2,
    /**
     * DeltaP Blood Pressure difference
     */
    NOM_PRESS_BLD_DIFF,
    /**
     * DeltaP1 Blood Pressure difference 1 (generic)
     */
    NOM_PRESS_BLD_DIFF_GEN_1,
    /**
     * DeltaP2 Blood Pressure difference 2 (generic)
     */
    NOM_PRESS_BLD_DIFF_GEN_2,
    /**
     * HLMfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_MAIN,
    /**
     * SlvPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_SLAVE,
    /**
     * SucPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_SUCTION,
    /**
     * AuxPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_AUX,
    /**
     * PlePfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * SplPfl
     */
    NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * AxOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START,
    /**
     * AxOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP,
    /**
     * AxDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX,
    /**
     * AxTVol
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX,
    /**
     * AxPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX,
    /**
     * CpOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START,
    /**
     * CpOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP,
    /**
     * CpDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CpTVol
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CpPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN,
    /**
     * CsOnTi
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START,
    /**
     * CsOffT
     */
    NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP,
    /**
     * CsDVol
     */
    NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * Physiological Identifier 7 Attribute Data Types and Constants Used
     */
    NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * CsPlTi
     */
    NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE,
    /**
     * Tin/Tt
     */
    NOM_RATIO_INSP_TOTAL_BREATH_SPONT,
    /**
     * tPEEP
     */
    NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL,
    /**
     * Cpav
     */
    NOM_COMPL_LUNG_PAV,
    /**
     * Rpav
     */
    NOM_RES_AWAY_PAV,
    /**
     * Rtot
     */
    NOM_RES_AWAY_EXP_TOTAL,
    /**
     * Epav
     */
    NOM_ELAS_LUNG_PAV,
    /**
     * RSBInm
     */
    NOM_BREATH_RAPID_SHALLOW_INDEX_NORM,
;

    public static final ObservedValue valueOf(int x) {
        switch(x) {
        case 0x0100:
            return NOM_ECG_ELEC_POTL;
        case 0x0101:
            return NOM_ECG_ELEC_POTL_I;
        case 0x0102:
            return NOM_ECG_ELEC_POTL_II;
        case 0x0103:
            return NOM_ECG_ELEC_POTL_V1;
        case 0x0104:
            return NOM_ECG_ELEC_POTL_V2;
        case 0x0105:
            return NOM_ECG_ELEC_POTL_V3;
        case 0x0106:
            return NOM_ECG_ELEC_POTL_V4;
        case 0x0107:
            return NOM_ECG_ELEC_POTL_V5;
        case 0x0108:
            return NOM_ECG_ELEC_POTL_V6;
        case 0x013D:
            return NOM_ECG_ELEC_POTL_III;
        case 0x013E:
            return NOM_ECG_ELEC_POTL_AVR;
        case 0x013F:
            return NOM_ECG_ELEC_POTL_AVL;
        case 0x0140:
            return NOM_ECG_ELEC_POTL_AVF;
        case 0x0143:
            return NOM_ECG_ELEC_POTL_V;
        case 0x014B:
            return NOM_ECG_ELEC_POTL_MCL;
        case 0x014C:
            return NOM_ECG_ELEC_POTL_MCL1;
        case 0x0301:
            return NOM_ECG_AMPL_ST_I;
        case 0x0302:
            return NOM_ECG_AMPL_ST_II;
        case 0x0303:
            return NOM_ECG_AMPL_ST_V1;
        case 0x0304:
            return NOM_ECG_AMPL_ST_V2;
        case 0x0305:
            return NOM_ECG_AMPL_ST_V3;
        case 0x0306:
            return NOM_ECG_AMPL_ST_V4;
        case 0x0307:
            return NOM_ECG_AMPL_ST_V5;
        case 0x0308:
            return NOM_ECG_AMPL_ST_V6;
        case 0x033D:
            return NOM_ECG_AMPL_ST_III;
        case 0x033E:
            return NOM_ECG_AMPL_ST_AVR;
        case 0x033F:
            return NOM_ECG_AMPL_ST_AVL;
        case 0x0340:
            return NOM_ECG_AMPL_ST_AVF;
        case 0x0343:
            return NOM_ECG_AMPL_ST_V;
        case 0x034B:
            return NOM_ECG_AMPL_ST_MCL;
        case 0x0364:
            return NOM_ECG_AMPL_ST_ES;
        case 0x0365:
            return NOM_ECG_AMPL_ST_AS;
        case 0x0366:
            return NOM_ECG_AMPL_ST_AI;
        case 0x3F20:
            return NOM_ECG_TIME_PD_QT_GL;
        case 0x3F24:
            return NOM_ECG_TIME_PD_QTc;
        case 0x400B:
            return NOM_ECG_RHY_ABSENT;
        case 0x403F:
            return NOM_ECG_RHY_NOS;
        case 0x4182:
            return NOM_ECG_CARD_BEAT_RATE;
        case 0x418A:
            return NOM_ECG_CARD_BEAT_RATE_BTB;
        case 0x4261:
            return NOM_ECG_V_P_C_CNT;
        case 0x480A:
            return NOM_PULS_RATE;
        case 0x4822:
            return NOM_PLETH_PULS_RATE;
        case 0x4900:
            return NOM_RES_VASC_SYS_INDEX;
        case 0x4904:
            return NOM_WK_LV_STROKE_INDEX;
        case 0x4908:
            return NOM_WK_RV_STROKE_INDEX;
        case 0x490C:
            return NOM_OUTPUT_CARD_INDEX;
        case 0x4A00:
            return NOM_PRESS_BLD;
        case 0x4A01:
            return NOM_PRESS_BLD_SYS;
        case 0x4A02:
            return NOM_PRESS_BLD_DIA;
        case 0x4A03:
            return NOM_PRESS_BLD_MEAN;
        case 0x4A04:
            return NOM_PRESS_BLD_NONINV;
        case 0x4A05:
            return NOM_PRESS_BLD_NONINV_SYS;
        case 0x4A06:
            return NOM_PRESS_BLD_NONINV_DIA;
        case 0x4A07:
            return NOM_PRESS_BLD_NONINV_MEAN;
        case 0x4A0C:
            return NOM_PRESS_BLD_AORT;
        case 0x4A0D:
            return NOM_PRESS_BLD_AORT_SYS;
        case 0x4A0E:
            return NOM_PRESS_BLD_AORT_DIA;
        case 0x4A0F:
            return NOM_PRESS_BLD_AORT_MEAN;
        case 0x4A10:
            return NOM_PRESS_BLD_ART;
        case 0x4A11:
            return NOM_PRESS_BLD_ART_SYS;
        case 0x4A12:
            return NOM_PRESS_BLD_ART_DIA;
        case 0x4A13:
            return NOM_PRESS_BLD_ART_MEAN;
        case 0x4A14:
            return NOM_PRESS_BLD_ART_ABP;
        case 0x4A15:
            return NOM_PRESS_BLD_ART_ABP_SYS;
        case 0x4A16:
            return NOM_PRESS_BLD_ART_ABP_DIA;
        case 0x4A17:
            return NOM_PRESS_BLD_ART_ABP_MEAN;
        case 0x4A1C:
            return NOM_PRESS_BLD_ART_PULM;
        case 0x4A1D:
            return NOM_PRESS_BLD_ART_PULM_SYS;
        case 0x4A1E:
            return NOM_PRESS_BLD_ART_PULM_DIA;
        case 0x4A1F:
            return NOM_PRESS_BLD_ART_PULM_MEAN;
        case 0x4A24:
            return NOM_PRESS_BLD_ART_PULM_WEDGE;
        case 0x4A28:
            return NOM_PRESS_BLD_ART_UMB;
        case 0x4A29:
            return NOM_PRESS_BLD_ART_UMB_SYS;
        case 0x4A2A:
            return NOM_PRESS_BLD_ART_UMB_DIA;
        case 0x4A2B:
            return NOM_PRESS_BLD_ART_UMB_MEAN;
        case 0x4A30:
            return NOM_PRESS_BLD_ATR_LEFT;
        case 0x4A31:
            return NOM_PRESS_BLD_ATR_LEFT_SYS;
        case 0x4A32:
            return NOM_PRESS_BLD_ATR_LEFT_DIA;
        case 0x4A33:
            return NOM_PRESS_BLD_ATR_LEFT_MEAN;
        case 0x4A34:
            return NOM_PRESS_BLD_ATR_RIGHT;
        case 0x4A35:
            return NOM_PRESS_BLD_ATR_RIGHT_SYS;
        case 0x4A36:
            return NOM_PRESS_BLD_ATR_RIGHT_DIA;
        case 0x4A37:
            return NOM_PRESS_BLD_ATR_RIGHT_MEAN;
        case 0x4A44:
            return NOM_PRESS_BLD_VEN_CENT;
        case 0x4A45:
            return NOM_PRESS_BLD_VEN_CENT_SYS;
        case 0x4A46:
            return NOM_PRESS_BLD_VEN_CENT_DIA;
        case 0x4A47:
            return NOM_PRESS_BLD_VEN_CENT_MEAN;
        case 0x4A48:
            return NOM_PRESS_BLD_VEN_UMB;
        case 0x4A49:
            return NOM_PRESS_BLD_VEN_UMB_SYS;
        case 0x4A4A:
            return NOM_PRESS_BLD_VEN_UMB_DIA;
        case 0x4A4B:
            return NOM_PRESS_BLD_VEN_UMB_MEAN;
        case 0x4B00:
            return NOM_SAT_O2_CONSUMP;
        case 0x4B04:
            return NOM_OUTPUT_CARD;
        case 0x4B24:
            return NOM_RES_VASC_PULM;
        case 0x4B28:
            return NOM_RES_VASC_SYS;
        case 0x4B2C:
            return NOM_SAT_O2;
        case 0x4B34:
            return NOM_SAT_O2_ART;
        case 0x4B3C:
            return NOM_SAT_O2_VEN;
        case 0x4B40:
            return NOM_SAT_DIFF_O2_ART_ALV;
        case 0x4B48:
            return NOM_SETT_TEMP;
        case 0x4B50:
            return NOM_TEMP_ART;
        case 0x4B54:
            return NOM_TEMP_AWAY;
        case 0x4B60:
            return NOM_TEMP_CORE;
        case 0x4B64:
            return NOM_TEMP_ESOPH;
        case 0x4B68:
            return NOM_TEMP_INJ;
        case 0x4B6C:
            return NOM_TEMP_NASOPH;
        case 0x4B74:
            return NOM_TEMP_SKIN;
        case 0x4B78:
            return NOM_TEMP_TYMP;
        case 0x4B7C:
            return NOM_TEMP_VEN;
        case 0x4B84:
            return NOM_VOL_BLD_STROKE;
        case 0x4B90:
            return NOM_WK_CARD_LEFT;
        case 0x4B94:
            return NOM_WK_CARD_RIGHT;
        case 0x4B9C:
            return NOM_WK_LV_STROKE;
        case 0x4BA4:
            return NOM_WK_RV_STROKE;
        case 0x4BB0:
            return NOM_PULS_OXIM_PERF_REL;
        case 0x4BB4:
            return NOM_PLETH;
        case 0x4BB8:
            return NOM_PULS_OXIM_SAT_O2;
        case 0x4BC4:
            return NOM_PULS_OXIM_SAT_O2_DIFF;
        case 0x4BC8:
            return NOM_PULS_OXIM_SAT_O2_ART_LEFT;
        case 0x4BCC:
            return NOM_PULS_OXIM_SAT_O2_ART_RIGHT;
        case 0x4BDC:
            return NOM_OUTPUT_CARD_CTS;
        case 0x4C04:
            return NOM_VOL_VENT_L_END_SYS;
        case 0x4C25:
            return NOM_GRAD_PRESS_BLD_AORT_POS_MAX;
        case 0x5000:
            return NOM_RESP;
        case 0x500A:
            return NOM_RESP_RATE;
        case 0x5012:
            return NOM_AWAY_RESP_RATE;
        case 0x5080:
            return NOM_CAPAC_VITAL;
        case 0x5088:
            return NOM_COMPL_LUNG;
        case 0x508C:
            return NOM_COMPL_LUNG_DYN;
        case 0x5090:
            return NOM_COMPL_LUNG_STATIC;
        case 0x50AC:
            return NOM_AWAY_CO2;
        case 0x50B0:
            return NOM_AWAY_CO2_ET;
        case 0x50BA:
            return NOM_AWAY_CO2_INSP_MIN;
        case 0x50CC:
            return NOM_CO2_TCUT;
        case 0x50D0:
            return NOM_O2_TCUT;
        case 0x50D4:
            return NOM_FLOW_AWAY;
        case 0x50D9:
            return NOM_FLOW_AWAY_EXP_MAX;
        case 0x50DD:
            return NOM_FLOW_AWAY_INSP_MAX;
        case 0x50E0:
            return NOM_FLOW_CO2_PROD_RESP;
        case 0x50E4:
            return NOM_IMPED_TTHOR;
        case 0x50E8:
            return NOM_PRESS_RESP_PLAT;
        case 0x50F0:
            return NOM_PRESS_AWAY;
        case 0x50F2:
            return NOM_SETT_PRESS_AWAY_MIN;
        case 0x50F4:
            return NOM_PRESS_AWAY_CTS_POS;
        case 0x50F9:
            return NOM_PRESS_AWAY_NEG_MAX;
        case 0x5100:
            return NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC;
        case 0x5108:
            return NOM_PRESS_AWAY_INSP;
        case 0x5109:
            return NOM_PRESS_AWAY_INSP_MAX;
        case 0x510B:
            return NOM_PRESS_AWAY_INSP_MEAN;
        case 0x5118:
            return NOM_RATIO_IE;
        case 0x511C:
            return NOM_RATIO_AWAY_DEADSP_TIDAL;
        case 0x5120:
            return NOM_RES_AWAY;
        case 0x5124:
            return NOM_RES_AWAY_EXP;
        case 0x5128:
            return NOM_RES_AWAY_INSP;
        case 0x5130:
            return NOM_TIME_PD_APNEA;
        case 0x513C:
            return NOM_VOL_AWAY_TIDAL;
        case 0x5148:
            return NOM_VOL_MINUTE_AWAY;
        case 0x514C:
            return NOM_VOL_MINUTE_AWAY_EXP;
        case 0x5150:
            return NOM_VOL_MINUTE_AWAY_INSP;
        case 0x5160:
            return NOM_VENT_CONC_AWAY_CO2_INSP;
        case 0x5164:
            return NOM_CONC_AWAY_O2;
        case 0x5168:
            return NOM_VENT_CONC_AWAY_O2_DELTA;
        case 0x517C:
            return NOM_VENT_AWAY_CO2_EXP;
        case 0x518C:
            return NOM_VENT_FLOW_INSP;
        case 0x5190:
            return NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX;
        case 0x519C:
            return NOM_VENT_PRESS_OCCL;
        case 0x51A8:
            return NOM_VENT_PRESS_AWAY_END_EXP_POS;
        case 0x51B0:
            return NOM_VENT_VOL_AWAY_DEADSP;
        case 0x51B4:
            return NOM_VENT_VOL_AWAY_DEADSP_REL;
        case 0x51B8:
            return NOM_SETT_VENT_VOL_LUNG_TRAPD;
        case 0x51CC:
            return NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND;
        case 0x51D4:
            return NOM_COEF_GAS_TRAN;
        case 0x51D8:
            return NOM_CONC_AWAY_DESFL;
        case 0x51DC:
            return NOM_CONC_AWAY_ENFL;
        case 0x51E0:
            return NOM_CONC_AWAY_HALOTH;
        case 0x51E4:
            return NOM_CONC_AWAY_SEVOFL;
        case 0x51E8:
            return NOM_CONC_AWAY_ISOFL;
        case 0x51F0:
            return NOM_CONC_AWAY_N2O;
        case 0x5214:
            return NOM_CONC_AWAY_DESFL_ET;
        case 0x5218:
            return NOM_CONC_AWAY_ENFL_ET;
        case 0x521C:
            return NOM_CONC_AWAY_HALOTH_ET;
        case 0x5220:
            return NOM_CONC_AWAY_SEVOFL_ET;
        case 0x5224:
            return NOM_CONC_AWAY_ISOFL_ET;
        case 0x522C:
            return NOM_CONC_AWAY_N2O_ET;
        case 0x5268:
            return NOM_CONC_AWAY_DESFL_INSP;
        case 0x526C:
            return NOM_CONC_AWAY_ENFL_INSP;
        case 0x5270:
            return NOM_CONC_AWAY_HALOTH_INSP;
        case 0x5274:
            return NOM_CONC_AWAY_SEVOFL_INSP;
        case 0x5278:
            return NOM_CONC_AWAY_ISOFL_INSP;
        case 0x5280:
            return NOM_CONC_AWAY_N2O_INSP;
        case 0x5284:
            return NOM_CONC_AWAY_O2_INSP;
        case 0x5360:
            return NOM_VENT_TIME_PD_PPV;
        case 0x5368:
            return NOM_VENT_PRESS_RESP_PLAT;
        case 0x5370:
            return NOM_VENT_VOL_LEAK;
        case 0x5374:
            return NOM_VENT_VOL_LUNG_ALV;
        case 0x5378:
            return NOM_CONC_AWAY_O2_ET;
        case 0x537C:
            return NOM_CONC_AWAY_N2;
        case 0x5380:
            return NOM_CONC_AWAY_N2_ET;
        case 0x5384:
            return NOM_CONC_AWAY_N2_INSP;
        case 0x5388:
            return NOM_CONC_AWAY_AGENT;
        case 0x538C:
            return NOM_CONC_AWAY_AGENT_ET;
        case 0x5390:
            return NOM_CONC_AWAY_AGENT_INSP;
        case 0x5804:
            return NOM_PRESS_CEREB_PERF;
        case 0x5808:
            return NOM_PRESS_INTRA_CRAN;
        case 0x5809:
            return NOM_PRESS_INTRA_CRAN_SYS;
        case 0x580A:
            return NOM_PRESS_INTRA_CRAN_DIA;
        case 0x580B:
            return NOM_PRESS_INTRA_CRAN_MEAN;
        case 0x5880:
            return NOM_SCORE_GLAS_COMA;
        case 0x5882:
            return NOM_SCORE_EYE_SUBSC_GLAS_COMA;
        case 0x5883:
            return NOM_SCORE_MOTOR_SUBSC_GLAS_COMA;
        case 0x5884:
            return NOM_SCORE_SUBSC_VERBAL_GLAS_COMA;
        case 0x5900:
            return NOM_CIRCUM_HEAD;
        case 0x5924:
            return NOM_TIME_PD_PUPIL_REACT_LEFT;
        case 0x5928:
            return NOM_TIME_PD_PUPIL_REACT_RIGHT;
        case 0x592C:
            return NOM_EEG_ELEC_POTL_CRTX;
        case 0x593C:
            return NOM_EMG_ELEC_POTL_MUSCL;
        case 0x597C:
            return NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN;
        case 0x5984:
            return NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK;
        case 0x5988:
            return NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE;
        case 0x59B8:
            return NOM_EEG_PWR_SPEC_TOT;
        case 0x59D4:
            return NOM_EEG_PWR_SPEC_ALPHA_REL;
        case 0x59D8:
            return NOM_EEG_PWR_SPEC_BETA_REL;
        case 0x59DC:
            return NOM_EEG_PWR_SPEC_DELTA_REL;
        case 0x59E0:
            return NOM_EEG_PWR_SPEC_THETA_REL;
        case 0x680C:
            return NOM_FLOW_URINE_INSTANT;
        case 0x6824:
            return NOM_VOL_URINE_BAL_PD;
        case 0x6830:
            return NOM_VOL_URINE_COL;
        case 0x6858:
            return NOM_SETT_FLOW_FLUID_PUMP;
        case 0x68FC:
            return NOM_VOL_INFUS_ACTUAL_TOTAL;
        case 0x7004:
            return NOM_CONC_PH_ART;
        case 0x7008:
            return NOM_CONC_PCO2_ART;
        case 0x700C:
            return NOM_CONC_PO2_ART;
        case 0x7014:
            return NOM_CONC_HB_ART;
        case 0x7018:
            return NOM_CONC_HB_O2_ART;
        case 0x7034:
            return NOM_CONC_PH_VEN;
        case 0x7038:
            return NOM_CONC_PCO2_VEN;
        case 0x703C:
            return NOM_CONC_PO2_VEN;
        case 0x7048:
            return NOM_CONC_HB_O2_VEN;
        case 0x7064:
            return NOM_CONC_PH_URINE;
        case 0x706C:
            return NOM_CONC_NA_URINE;
        case 0x70D8:
            return NOM_CONC_NA_SERUM;
        case 0x7104:
            return NOM_CONC_PH_GEN;
        case 0x7108:
            return NOM_CONC_HCO3_GEN;
        case 0x710C:
            return NOM_CONC_NA_GEN;
        case 0x7110:
            return NOM_CONC_K_GEN;
        case 0x7114:
            return NOM_CONC_GLU_GEN;
        case 0x7118:
            return NOM_CONC_CA_GEN;
        case 0x7140:
            return NOM_CONC_PCO2_GEN;
        case 0x7168:
            return NOM_CONC_CHLORIDE_GEN;
        case 0x716C:
            return NOM_BASE_EXCESS_BLD_ART;
        case 0x7174:
            return NOM_CONC_PO2_GEN;
        case 0x717C:
            return NOM_CONC_HB_MET_GEN;
        case 0x7180:
            return NOM_CONC_HB_CO_GEN;
        case 0x7184:
            return NOM_CONC_HCT_GEN;
        case 0x7498:
            return NOM_VENT_CONC_AWAY_O2_INSP;
        case 0xD02A:
            return NOM_VENT_MODE_MAND_INTERMIT;
        case 0xE004:
            return NOM_TEMP_RECT;
        case 0xE014:
            return NOM_TEMP_BLD;
        case 0xE018:
            return NOM_TEMP_DIFF;
        case 0xEFFF:
            return NOM_METRIC_NOS;
        case 0xF03D:
            return NOM_ECG_AMPL_ST_INDEX;
        case 0xF03E:
            return NOM_TIME_TCUT_SENSOR;
        case 0xF03F:
            return NOM_TEMP_TCUT_SENSOR;
        case 0xF040:
            return NOM_VOL_BLD_INTRA_THOR;
        case 0xF041:
            return NOM_VOL_BLD_INTRA_THOR_INDEX;
        case 0xF042:
            return NOM_VOL_LUNG_WATER_EXTRA_VASC;
        case 0xF043:
            return NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX;
        case 0xF044:
            return NOM_VOL_GLOBAL_END_DIA;
        case 0xF045:
            return NOM_VOL_GLOBAL_END_DIA_INDEX;
        case 0xF046:
            return NOM_CARD_FUNC_INDEX;
        case 0xF047:
            return NOM_OUTPUT_CARD_INDEX_CTS;
        case 0xF048:
            return NOM_VOL_BLD_STROKE_INDEX;
        case 0xF049:
            return NOM_VOL_BLD_STROKE_VAR;
        case 0xF04A:
            return NOM_EEG_RATIO_SUPPRN;
        case 0xF04D:
            return NOM_EEG_BIS_SIG_QUAL_INDEX;
        case 0xF04E:
            return NOM_EEG_BISPECTRAL_INDEX;
        case 0xF051:
            return NOM_GAS_TCUT;
        case 0xF05E:
            return NOM_CONC_AWAY_SUM_MAC_ET;
        case 0xF05F:
            return NOM_CONC_AWAY_SUM_MAC_INSP;
        case 0xF067:
            return NOM_RES_VASC_PULM_INDEX;
        case 0xF068:
            return NOM_WK_CARD_LEFT_INDEX;
        case 0xF069:
            return NOM_WK_CARD_RIGHT_INDEX;
        case 0xF06A:
            return NOM_SAT_O2_CONSUMP_INDEX;
        case 0xF06B:
            return NOM_PRESS_AIR_AMBIENT;
        case 0xF06C:
            return NOM_SAT_DIFF_O2_ART_VEN;
        case 0xF06D:
            return NOM_SAT_O2_DELIVER;
        case 0xF06E:
            return NOM_SAT_O2_DELIVER_INDEX;
        case 0xF06F:
            return NOM_RATIO_SAT_O2_CONSUMP_DELIVER;
        case 0xF070:
            return NOM_RATIO_ART_VEN_SHUNT;
        case 0xF071:
            return NOM_AREA_BODY_SURFACE;
        case 0xF072:
            return NOM_INTENS_LIGHT;
        case 0xF076:
            return NOM_HEATING_PWR_TCUT_SENSOR;
        case 0xF079:
            return NOM_VOL_INJ;
        case 0xF07A:
            return NOM_VOL_THERMO_EXTRA_VASC_INDEX;
        case 0xF07C:
            return NOM_NUM_CATHETER_CONST;
        case 0xF08A:
            return NOM_PULS_OXIM_PERF_REL_LEFT;
        case 0xF08B:
            return NOM_PULS_OXIM_PERF_REL_RIGHT;
        case 0xF08C:
            return NOM_PULS_OXIM_PLETH_RIGHT;
        case 0xF08D:
            return NOM_PULS_OXIM_PLETH_LEFT;
        case 0xF08F:
            return NOM_CONC_BLD_UREA_NITROGEN;
        case 0xF090:
            return NOM_CONC_BASE_EXCESS_ECF;
        case 0xF091:
            return NOM_VENT_VOL_MINUTE_AWAY_SPONT;
        case 0xF092:
            return NOM_CONC_DIFF_HB_O2_ATR_VEN;
        case 0xF093:
            return NOM_PAT_WEIGHT;
        case 0xF094:
            return NOM_PAT_HEIGHT;
        case 0xF099:
            return NOM_CONC_AWAY_MAC;
        case 0xF09B:
            return NOM_PULS_OXIM_PLETH_TELE;
        case 0xF09C:
            return NOM_PULS_OXIM_SAT_O2_TELE;
        case 0xF09D:
            return NOM_PULS_OXIM_PULS_RATE_TELE;
        case 0xF0A4:
            return NOM_PRESS_GEN_1;
        case 0xF0A5:
            return NOM_PRESS_GEN_1_SYS;
        case 0xF0A6:
            return NOM_PRESS_GEN_1_DIA;
        case 0xF0A7:
            return NOM_PRESS_GEN_1_MEAN;
        case 0xF0A8:
            return NOM_PRESS_GEN_2;
        case 0xF0A9:
            return NOM_PRESS_GEN_2_SYS;
        case 0xF0AA:
            return NOM_PRESS_GEN_2_DIA;
        case 0xF0AB:
            return NOM_PRESS_GEN_2_MEAN;
        case 0xF0AC:
            return NOM_PRESS_GEN_3;
        case 0xF0AD:
            return NOM_PRESS_GEN_3_SYS;
        case 0xF0AF:
            return NOM_PRESS_GEN_3_MEAN;
        case 0xF0B0:
            return NOM_PRESS_GEN_4;
        case 0xF0B1:
            return NOM_PRESS_GEN_4_SYS;
        case 0xF0B2:
            return NOM_PRESS_GEN_4_DIA;
        case 0xF0B3:
            return NOM_PRESS_GEN_4_MEAN;
        case 0xF0B4:
            return NOM_PRESS_INTRA_CRAN_1;
        case 0xF0B5:
            return NOM_PRESS_INTRA_CRAN_1_SYS;
        case 0xF0B6:
            return NOM_PRESS_INTRA_CRAN_1_DIA;
        case 0xF0B7:
            return NOM_PRESS_INTRA_CRAN_1_MEAN;
        case 0xF0B8:
            return NOM_PRESS_INTRA_CRAN_2;
        case 0xF0B9:
            return NOM_PRESS_INTRA_CRAN_2_SYS;
        case 0xF0BA:
            return NOM_PRESS_INTRA_CRAN_2_DIA;
        case 0xF0BB:
            return NOM_PRESS_INTRA_CRAN_2_MEAN;
        case 0xF0BC:
            return NOM_PRESS_BLD_ART_FEMORAL;
        case 0xF0BD:
            return NOM_PRESS_BLD_ART_FEMORAL_SYS;
        case 0xF0BE:
            return NOM_PRESS_BLD_ART_FEMORAL_DIA;
        case 0xF0BF:
            return NOM_PRESS_BLD_ART_FEMORAL_MEAN;
        case 0xF0C0:
            return NOM_PRESS_BLD_ART_BRACHIAL;
        case 0xF0C1:
            return NOM_PRESS_BLD_ART_BRACHIAL_SYS;
        case 0xF0C2:
            return NOM_PRESS_BLD_ART_BRACHIAL_DIA;
        case 0xF0C3:
            return NOM_PRESS_BLD_ART_BRACHIAL_MEAN;
        case 0xF0C4:
            return NOM_TEMP_VESICAL;
        case 0xF0C5:
            return NOM_TEMP_CEREBRAL;
        case 0xF0C6:
            return NOM_TEMP_AMBIENT;
        case 0xF0C7:
            return NOM_TEMP_GEN_1;
        case 0xF0C8:
            return NOM_TEMP_GEN_2;
        case 0xF0C9:
            return NOM_TEMP_GEN_3;
        case 0xF0CA:
            return NOM_TEMP_GEN_4;
        case 0xF0E0:
            return NOM_SETT_VOL_AWAY_INSP_TIDAL;
        case 0xF0E1:
            return NOM_VOL_AWAY_EXP_TIDAL;
        case 0xF0E2:
            return NOM_AWAY_RESP_RATE_SPIRO;
        case 0xF0E3:
            return NOM_PULS_PRESS_VAR;
        case 0xF0E5:
            return NOM_PRESS_BLD_NONINV_PULS_RATE;
        case 0xF0F1:
            return NOM_VENT_RESP_RATE_MAND;
        case 0xF0F2:
            return NOM_VENT_VOL_TIDAL_MAND;
        case 0xF0F3:
            return NOM_VENT_VOL_TIDAL_SPONT;
        case 0xF0F4:
            return NOM_CARDIAC_TROPONIN_I;
        case 0xF0F5:
            return NOM_CARDIO_PULMONARY_BYPASS_MODE;
        case 0xF0F6:
            return NOM_BNP;
        case 0xF0FF:
            return NOM_SETT_TIME_PD_RESP_PLAT;
        case 0xF100:
            return NOM_SAT_O2_VEN_CENT;
        case 0xF101:
            return NOM_SNR;
        case 0xF103:
            return NOM_HUMID;
        case 0xF105:
            return NOM_FRACT_EJECT;
        case 0xF106:
            return NOM_PERM_VASC_PULM_INDEX;
        case 0xF110:
            return NOM_TEMP_ORAL_PRED;
        case 0xF114:
            return NOM_TEMP_RECT_PRED;
        case 0xF118:
            return NOM_TEMP_AXIL_PRED;
        case 0xF12A:
            return NOM_TEMP_AIR_INCUB;
        case 0xF12C:
            return NOM_PULS_OXIM_PERF_REL_TELE;
        case 0xF14A:
            return NOM_SHUNT_RIGHT_LEFT;
        case 0xF154:
            return NOM_ECG_TIME_PD_QT_HEART_RATE;
        case 0xF155:
            return NOM_ECG_TIME_PD_QT_BASELINE;
        case 0xF156:
            return NOM_ECG_TIME_PD_QTc_DELTA;
        case 0xF157:
            return NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE;
        case 0xF158:
            return NOM_CONC_PH_CAP;
        case 0xF159:
            return NOM_CONC_PCO2_CAP;
        case 0xF15A:
            return NOM_CONC_PO2_CAP;
        case 0xF15B:
            return NOM_CONC_MG_ION;
        case 0xF15C:
            return NOM_CONC_MG_SER;
        case 0xF15D:
            return NOM_CONC_tCA_SER;
        case 0xF15E:
            return NOM_CONC_P_SER;
        case 0xF15F:
            return NOM_CONC_CHLOR_SER;
        case 0xF160:
            return NOM_CONC_FE_GEN;
        case 0xF163:
            return NOM_CONC_ALB_SER;
        case 0xF164:
            return NOM_SAT_O2_ART_CALC;
        case 0xF165:
            return NOM_CONC_HB_FETAL;
        case 0xF167:
            return NOM_PLTS_CNT;
        case 0xF168:
            return NOM_WB_CNT;
        case 0xF169:
            return NOM_RB_CNT;
        case 0xF16A:
            return NOM_RET_CNT;
        case 0xF16B:
            return NOM_PLASMA_OSM;
        case 0xF16C:
            return NOM_CONC_CREA_CLR;
        case 0xF16D:
            return NOM_NSLOSS;
        case 0xF16E:
            return NOM_CONC_CHOLESTEROL;
        case 0xF16F:
            return NOM_CONC_TGL;
        case 0xF170:
            return NOM_CONC_HDL;
        case 0xF171:
            return NOM_CONC_LDL;
        case 0xF172:
            return NOM_CONC_UREA_GEN;
        case 0xF173:
            return NOM_CONC_CREA;
        case 0xF174:
            return NOM_CONC_LACT;
        case 0xF177:
            return NOM_CONC_BILI_TOT;
        case 0xF178:
            return NOM_CONC_PROT_SER;
        case 0xF179:
            return NOM_CONC_PROT_TOT;
        case 0xF17A:
            return NOM_CONC_BILI_DIRECT;
        case 0xF17B:
            return NOM_CONC_LDH;
        case 0xF17C:
            return NOM_ES_RATE;
        case 0xF17D:
            return NOM_CONC_PCT;
        case 0xF17F:
            return NOM_CONC_CREA_KIN_MM;
        case 0xF180:
            return NOM_CONC_CREA_KIN_SER;
        case 0xF181:
            return NOM_CONC_CREA_KIN_MB;
        case 0xF182:
            return NOM_CONC_CHE;
        case 0xF183:
            return NOM_CONC_CRP;
        case 0xF184:
            return NOM_CONC_AST;
        case 0xF185:
            return NOM_CONC_AP;
        case 0xF186:
            return NOM_CONC_ALPHA_AMYLASE;
        case 0xF187:
            return NOM_CONC_GPT;
        case 0xF188:
            return NOM_CONC_GOT;
        case 0xF189:
            return NOM_CONC_GGT;
        case 0xF18A:
            return NOM_TIME_PD_ACT;
        case 0xF18B:
            return NOM_TIME_PD_PT;
        case 0xF18C:
            return NOM_PT_INTL_NORM_RATIO;
        case 0xF18D:
            return NOM_TIME_PD_aPTT_WB;
        case 0xF18E:
            return NOM_TIME_PD_aPTT_PE;
        case 0xF18F:
            return NOM_TIME_PD_PT_WB;
        case 0xF190:
            return NOM_TIME_PD_PT_PE;
        case 0xF191:
            return NOM_TIME_PD_THROMBIN;
        case 0xF192:
            return NOM_TIME_PD_COAGULATION;
        case 0xF193:
            return NOM_TIME_PD_THROMBOPLAS;
        case 0xF194:
            return NOM_FRACT_EXCR_NA;
        case 0xF195:
            return NOM_CONC_UREA_URINE;
        case 0xF196:
            return NOM_CONC_CREA_URINE;
        case 0xF197:
            return NOM_CONC_K_URINE;
        case 0xF198:
            return NOM_CONC_K_URINE_EXCR;
        case 0xF199:
            return NOM_CONC_OSM_URINE;
        case 0xF19A:
            return NOM_CONC_CHLOR_URINE;
        case 0xF19B:
            return NOM_CONC_PRO_URINE;
        case 0xF19C:
            return NOM_CONC_CA_URINE;
        case 0xF19D:
            return NOM_FLUID_DENS_URINE;
        case 0xF19E:
            return NOM_CONC_HB_URINE;
        case 0xF19F:
            return NOM_CONC_GLU_URINE;
        case 0xF1A0:
            return NOM_SAT_O2_CAP_CALC;
        case 0xF1A1:
            return NOM_CONC_AN_GAP_CALC;
        case 0xF1C0:
            return NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL;
        case 0xF1D4:
            return NOM_PULS_OXIM_SAT_O2_POST_DUCTAL;
        case 0xF1DC:
            return NOM_PULS_OXIM_PERF_REL_POST_DUCTAL;
        case 0xF22C:
            return NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL;
        case 0xF3F4:
            return NOM_PRESS_GEN_5;
        case 0xF3F5:
            return NOM_PRESS_GEN_5_SYS;
        case 0xF3F6:
            return NOM_PRESS_GEN_5_DIA;
        case 0xF3F7:
            return NOM_PRESS_GEN_5_MEAN;
        case 0xF3F8:
            return NOM_PRESS_GEN_6;
        case 0xF3F9:
            return NOM_PRESS_GEN_6_SYS;
        case 0xF3FA:
            return NOM_PRESS_GEN_6_DIA;
        case 0xF3FB:
            return NOM_PRESS_GEN_6_MEAN;
        case 0xF3FC:
            return NOM_PRESS_GEN_7;
        case 0xF3FD:
            return NOM_PRESS_GEN_7_SYS;
        case 0xF3FF:
            return NOM_PRESS_GEN_7_MEAN;
        case 0xF400:
            return NOM_PRESS_GEN_8;
        case 0xF401:
            return NOM_PRESS_GEN_8_SYS;
        case 0xF402:
            return NOM_PRESS_GEN_8_DIA;
        case 0xF403:
            return NOM_PRESS_GEN_8_MEAN;
        case 0xF411:
            return NOM_ECG_AMPL_ST_BASELINE_I;
        case 0xF412:
            return NOM_ECG_AMPL_ST_BASELINE_II;
        case 0xF413:
            return NOM_ECG_AMPL_ST_BASELINE_V1;
        case 0xF414:
            return NOM_ECG_AMPL_ST_BASELINE_V2;
        case 0xF415:
            return NOM_ECG_AMPL_ST_BASELINE_V3;
        case 0xF416:
            return NOM_ECG_AMPL_ST_BASELINE_V4;
        case 0xF417:
            return NOM_ECG_AMPL_ST_BASELINE_V5;
        case 0xF418:
            return NOM_ECG_AMPL_ST_BASELINE_V6;
        case 0xF44D:
            return NOM_ECG_AMPL_ST_BASELINE_III;
        case 0xF44E:
            return NOM_ECG_AMPL_ST_BASELINE_AVR;
        case 0xF44F:
            return NOM_ECG_AMPL_ST_BASELINE_AVL;
        case 0xF450:
            return NOM_ECG_AMPL_ST_BASELINE_AVF;
        case 0xF810:
            return NOM_AGE;
        case 0xF811:
            return NOM_AGE_GEST;
        case 0xF814:
            return NOM_AWAY_CORR_COEF;
        case 0xF815:
            return NOM_AWAY_RESP_RATE_SPONT;
        case 0xF816:
            return NOM_AWAY_TC;
        case 0xF818:
            return NOM_BIRTH_LENGTH;
        case 0xF819:
            return NOM_BREATH_RAPID_SHALLOW_INDEX;
        case 0xF81A:
            return NOM_C20_PER_C_INDEX;
        case 0xF81C:
            return NOM_CARD_CONTRACT_HEATHER_INDEX;
        case 0xF81D:
            return NOM_CONC_ALP;
        case 0xF822:
            return NOM_CONC_CA_GEN_NORM;
        case 0xF824:
            return NOM_CONC_CA_SER;
        case 0xF825:
            return NOM_CONC_CO2_TOT;
        case 0xF826:
            return NOM_CONC_CO2_TOT_CALC;
        case 0xF827:
            return NOM_CONC_CREA_SER;
        case 0xF828:
            return NOM_RESP_RATE_SPONT;
        case 0xF829:
            return NOM_CONC_GLO_SER;
        case 0xF82A:
            return NOM_CONC_GLU_SER;
        case 0xF82C:
            return NOM_CONC_HB_CORP_MEAN;
        case 0xF82F:
            return NOM_CONC_K_SER;
        case 0xF830:
            return NOM_CONC_NA_EXCR;
        case 0xF832:
            return NOM_CONC_PCO2_ART_ADJ;
        case 0xF833:
            return NOM_CONC_PCO2_CAP_ADJ;
        case 0xF837:
            return NOM_CONC_PH_CAP_ADJ;
        case 0xF838:
            return NOM_CONC_PH_GEN_ADJ;
        case 0xF83B:
            return NOM_CONC_PO2_ART_ADJ;
        case 0xF83C:
            return NOM_CONC_PO2_CAP_ADJ;
        case 0xF83F:
            return NOM_CREA_OSM;
        case 0xF840:
            return NOM_EEG_BURST_SUPPRN_INDEX;
        case 0xF841:
            return NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT;
        case 0xF842:
            return NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT;
        case 0xF84B:
            return NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT;
        case 0xF84C:
            return NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT;
        case 0xF855:
            return NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT;
        case 0xF856:
            return NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT;
        case 0xF85B:
            return NOM_EEG_PWR_SPEC_BETA_ABS_LEFT;
        case 0xF85C:
            return NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT;
        case 0xF863:
            return NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT;
        case 0xF864:
            return NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT;
        case 0xF869:
            return NOM_EEG_PWR_SPEC_THETA_ABS_LEFT;
        case 0xF86A:
            return NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT;
        case 0xF873:
            return NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI;
        case 0xF875:
            return NOM_EXTRACT_O2_INDEX;
        case 0xF877:
            return NOM_SETT_FLOW_AWAY_AIR;
        case 0xF87A:
            return NOM_FLOW_AWAY_EXP_ET;
        case 0xF87D:
            return NOM_FLOW_AWAY_MAX_SPONT;
        case 0xF881:
            return NOM_SETT_FLOW_AWAY_TOT;
        case 0xF882:
            return NOM_FLOW_CO2_PROD_RESP_TIDAL;
        case 0xF883:
            return NOM_FLOW_URINE_PREV_24HR;
        case 0xF884:
            return NOM_FREE_WATER_CLR;
        case 0xF885:
            return NOM_HB_CORP_MEAN;
        case 0xF886:
            return NOM_HEATING_PWR_INCUBATOR;
        case 0xF889:
            return NOM_OUTPUT_CARD_INDEX_ACCEL;
        case 0xF88B:
            return NOM_PTC_CNT;
        case 0xF88D:
            return NOM_PULS_OXIM_PLETH_GAIN;
        case 0xF88E:
            return NOM_RATIO_AWAY_RATE_VOL_AWAY;
        case 0xF88F:
            return NOM_RATIO_BUN_CREA;
        case 0xF890:
            return NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC;
        case 0xF891:
            return NOM_RATIO_CONC_URINE_CREA_CALC;
        case 0xF892:
            return NOM_RATIO_CONC_URINE_CREA_SER;
        case 0xF893:
            return NOM_RATIO_CONC_URINE_NA_K;
        case 0xF894:
            return NOM_RATIO_PaO2_FIO2;
        case 0xF895:
            return NOM_RATIO_TIME_PD_PT;
        case 0xF896:
            return NOM_RATIO_TIME_PD_PTT;
        case 0xF897:
            return NOM_RATIO_TRAIN_OF_FOUR;
        case 0xF898:
            return NOM_RATIO_URINE_SER_OSM;
        case 0xF899:
            return NOM_RES_AWAY_DYN;
        case 0xF89A:
            return NOM_RESP_BREATH_ASSIST_CNT;
        case 0xF89B:
            return NOM_RIGHT_HEART_FRACT_EJECT;
        case 0xF8A0:
            return NOM_TIME_PD_EVOK_REMAIN;
        case 0xF8A1:
            return NOM_TIME_PD_EXP;
        case 0xF8A2:
            return NOM_TIME_PD_FROM_LAST_MSMT;
        case 0xF8A3:
            return NOM_TIME_PD_INSP;
        case 0xF8A4:
            return NOM_TIME_PD_KAOLIN_CEPHALINE;
        case 0xF8A5:
            return NOM_TIME_PD_PTT;
        case 0xF8A6:
            return NOM_SETT_TIME_PD_TRAIN_OF_FOUR;
        case 0xF8A7:
            return NOM_TRAIN_OF_FOUR_1;
        case 0xF8A8:
            return NOM_TRAIN_OF_FOUR_2;
        case 0xF8A9:
            return NOM_TRAIN_OF_FOUR_3;
        case 0xF8AA:
            return NOM_TRAIN_OF_FOUR_4;
        case 0xF8AB:
            return NOM_TRAIN_OF_FOUR_CNT;
        case 0xF8AC:
            return NOM_TWITCH_AMPL;
        case 0xF8AD:
            return NOM_UREA_SER;
        case 0xF8AF:
            return NOM_SETT_URINE_BAL_PD;
        case 0xF8B0:
            return NOM_VENT_ACTIVE;
        case 0xF8B1:
            return NOM_VENT_AMPL_HFV;
        case 0xF8B2:
            return NOM_VENT_CONC_AWAY_AGENT_DELTA;
        case 0xF8B3:
            return NOM_VENT_CONC_AWAY_DESFL_DELTA;
        case 0xF8B4:
            return NOM_VENT_CONC_AWAY_ENFL_DELTA;
        case 0xF8B5:
            return NOM_VENT_CONC_AWAY_HALOTH_DELTA;
        case 0xF8B6:
            return NOM_VENT_CONC_AWAY_ISOFL_DELTA;
        case 0xF8B7:
            return NOM_VENT_CONC_AWAY_N2O_DELTA;
        case 0xF8B8:
            return NOM_VENT_CONC_AWAY_O2_CIRCUIT;
        case 0xF8B9:
            return NOM_VENT_CONC_AWAY_SEVOFL_DELTA;
        case 0xF8BA:
            return NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO;
        case 0xF8BC:
            return NOM_SETT_VENT_PRESS_AWAY_PV;
        case 0xF8BD:
            return NOM_VENT_TIME_PD_RAMP;
        case 0xF8BE:
            return NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV;
        case 0xF8BF:
            return NOM_VENT_VOL_TIDAL_HFV;
        case 0xF8C0:
            return NOM_SETT_VENT_VOL_TIDAL_SIGH;
        case 0xF8C2:
            return NOM_VOL_AWAY_EXP_TIDAL_SPONT;
        case 0xF8C3:
            return NOM_VOL_AWAY_TIDAL_PSV;
        case 0xF8C4:
            return NOM_VOL_CORP_MEAN;
        case 0xF8C5:
            return NOM_VOL_FLUID_THORAC;
        case 0xF8C6:
            return NOM_VOL_FLUID_THORAC_INDEX;
        case 0xF8C7:
            return NOM_VOL_LVL_LIQUID_BOTTLE_AGENT;
        case 0xF8C8:
            return NOM_VOL_LVL_LIQUID_BOTTLE_DESFL;
        case 0xF8C9:
            return NOM_VOL_LVL_LIQUID_BOTTLE_ENFL;
        case 0xF8CA:
            return NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH;
        case 0xF8CB:
            return NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL;
        case 0xF8CC:
            return NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL;
        case 0xF8CD:
            return NOM_VOL_MINUTE_AWAY_INSP_HFV;
        case 0xF8CE:
            return NOM_VOL_URINE_BAL_PD_INSTANT;
        case 0xF8CF:
            return NOM_VOL_URINE_SHIFT;
        case 0xF8D1:
            return NOM_VOL_VENT_L_END_SYS_INDEX;
        case 0xF8D3:
            return NOM_WEIGHT_URINE_COL;
        case 0xF8D9:
            return NOM_SETT_APNEA_ALARM_DELAY;
        case 0xF8DE:
            return NOM_SETT_AWAY_RESP_RATE_APNEA;
        case 0xF8DF:
            return NOM_SETT_AWAY_RESP_RATE_HFV;
        case 0xF8E6:
            return NOM_SETT_EVOK_CHARGE;
        case 0xF8E7:
            return NOM_SETT_EVOK_CURR;
        case 0xF8EA:
            return NOM_SETT_FLOW_AWAY_EXP;
        case 0xF8EB:
            return NOM_SETT_FLOW_AWAY_HFV;
        case 0xF8EC:
            return NOM_SETT_FLOW_AWAY_INSP;
        case 0xF8ED:
            return NOM_SETT_FLOW_AWAY_INSP_APNEA;
        case 0xF8F3:
            return NOM_SETT_HFV_AMPL;
        case 0xF8FB:
            return NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO;
        case 0xF900:
            return NOM_SETT_RATIO_IE_EXP_PV;
        case 0xF901:
            return NOM_SETT_RATIO_IE_EXP_PV_APNEA;
        case 0xF902:
            return NOM_SETT_RATIO_IE_INSP_PV;
        case 0xF903:
            return NOM_SETT_RATIO_IE_INSP_PV_APNEA;
        case 0xF904:
            return NOM_SETT_SENS_LEVEL;
        case 0xF908:
            return NOM_SETT_TIME_PD_EVOK;
        case 0xF909:
            return NOM_SETT_TIME_PD_MSMT;
        case 0xF90E:
            return NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE;
        case 0xF90F:
            return NOM_SETT_VENT_AWAY_FLOW_BACKGROUND;
        case 0xF910:
            return NOM_SETT_VENT_AWAY_FLOW_BASE;
        case 0xF911:
            return NOM_SETT_VENT_AWAY_FLOW_SENSE;
        case 0xF912:
            return NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE;
        case 0xF917:
            return NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA;
        case 0xF918:
            return NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA;
        case 0xF919:
            return NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI;
        case 0xF91A:
            return NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO;
        case 0xF91B:
            return NOM_SETT_VENT_FLOW;
        case 0xF91C:
            return NOM_SETT_VENT_FLOW_AWAY_ASSIST;
        case 0xF91D:
            return NOM_SETT_VENT_FLOW_INSP_TRIG;
        case 0xF920:
            return NOM_SETT_VENT_GAS_PROBE_POSN;
        case 0xF922:
            return NOM_SETT_VENT_MODE_MAND_CTS_ONOFF;
        case 0xF923:
            return NOM_SETT_VENT_MODE_SIGH;
        case 0xF924:
            return NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT;
        case 0xF926:
            return NOM_SETT_VENT_O2_CAL_MODE;
        case 0xF927:
            return NOM_SETT_VENT_O2_PROBE_POSN;
        case 0xF928:
            return NOM_SETT_VENT_O2_SUCTION_MODE;
        case 0xF92C:
            return NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT;
        case 0xF92D:
            return NOM_SETT_VENT_PRESS_AWAY_EXP_APRV;
        case 0xF92E:
            return NOM_SETT_VENT_PRESS_AWAY_INSP_APRV;
        case 0xF930:
            return NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI;
        case 0xF931:
            return NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA;
        case 0xF933:
            return NOM_SETT_VENT_PRESS_AWAY_PV_APNEA;
        case 0xF935:
            return NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI;
        case 0xF937:
            return NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT;
        case 0xF939:
            return NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP;
        case 0xF93A:
            return NOM_SETT_VENT_RESP_RATE_PV_APNEA;
        case 0xF93B:
            return NOM_SETT_VENT_SIGH_MULT_RATE;
        case 0xF93C:
            return NOM_SETT_VENT_SIGH_RATE;
        case 0xF93F:
            return NOM_SETT_VENT_TIME_PD_EXP;
        case 0xF940:
            return NOM_SETT_VENT_TIME_PD_EXP_APRV;
        case 0xF941:
            return NOM_SETT_VENT_TIME_PD_INSP;
        case 0xF942:
            return NOM_SETT_VENT_TIME_PD_INSP_APRV;
        case 0xF943:
            return NOM_SETT_VENT_TIME_PD_INSP_PV;
        case 0xF944:
            return NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA;
        case 0xF946:
            return NOM_SETT_VENT_TIME_PD_RAMP_AL;
        case 0xF948:
            return NOM_SETT_VENT_VOL_AWAY_ASSIST;
        case 0xF949:
            return NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF;
        case 0xF94B:
            return NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI;
        case 0xF94C:
            return NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO;
        case 0xF94D:
            return NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI;
        case 0xF94E:
            return NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO;
        case 0xF951:
            return NOM_SETT_VOL_AWAY_TIDAL_APNEA;
        case 0xF952:
            return NOM_SETT_VOL_AWAY_TIDAL_APPLIED;
        case 0xF953:
            return NOM_SETT_VOL_MINUTE_ALARM_DELAY;
        case 0xF960:
            return NOM_SAT_O2_TISSUE;
        case 0xF961:
            return NOM_CEREB_STATE_INDEX;
        case 0xF962:
            return NOM_SAT_O2_GEN_1;
        case 0xF963:
            return NOM_SAT_O2_GEN_2;
        case 0xF964:
            return NOM_SAT_O2_GEN_3;
        case 0xF965:
            return NOM_SAT_O2_GEN_4;
        case 0xF966:
            return NOM_TEMP_CORE_GEN_1;
        case 0xF967:
            return NOM_TEMP_CORE_GEN_2;
        case 0xF968:
            return NOM_PRESS_BLD_DIFF;
        case 0xF96C:
            return NOM_PRESS_BLD_DIFF_GEN_1;
        case 0xF970:
            return NOM_PRESS_BLD_DIFF_GEN_2;
        case 0xF974:
            return NOM_FLOW_PUMP_HEART_LUNG_MAIN;
        case 0xF975:
            return NOM_FLOW_PUMP_HEART_LUNG_SLAVE;
        case 0xF976:
            return NOM_FLOW_PUMP_HEART_LUNG_SUCTION;
        case 0xF977:
            return NOM_FLOW_PUMP_HEART_LUNG_AUX;
        case 0xF978:
            return NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0xF979:
            return NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0xF97A:
            return NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START;
        case 0xF97B:
            return NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP;
        case 0xF97C:
            return NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX;
        case 0xF97D:
            return NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX;
        case 0xF97E:
            return NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX;
        case 0xF97F:
            return NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START;
        case 0xF980:
            return NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP;
        case 0xF981:
            return NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0xF982:
            return NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0xF983:
            return NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN;
        case 0xF984:
            return NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START;
        case 0xF985:
            return NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP;
        case 0xF986:
            return NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0xF987:
            return NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0xF988:
            return NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE;
        case 0xF990:
            return NOM_RATIO_INSP_TOTAL_BREATH_SPONT;
        case 0xF991:
            return NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL;
        case 0xF992:
            return NOM_COMPL_LUNG_PAV;
        case 0xF993:
            return NOM_RES_AWAY_PAV;
        case 0xF994:
            return NOM_RES_AWAY_EXP_TOTAL;
        case 0xF995:
            return NOM_ELAS_LUNG_PAV;
        case 0xF996:
            return NOM_BREATH_RAPID_SHALLOW_INDEX_NORM;
        default:
        	return null;
//            throw new IllegalArgumentException("Unknown ObservedValue:"+x);
        }
    }
    public final OIDType asOID() {
    	return OIDType.lookup(asInt());
    }
    public final int asInt()  {
        switch(this) {
        case NOM_ECG_ELEC_POTL:
            return 0x0100;
        case NOM_ECG_ELEC_POTL_I:
            return 0x0101;
        case NOM_ECG_ELEC_POTL_II:
            return 0x0102;
        case NOM_ECG_ELEC_POTL_V1:
            return 0x0103;
        case NOM_ECG_ELEC_POTL_V2:
            return 0x0104;
        case NOM_ECG_ELEC_POTL_V3:
            return 0x0105;
        case NOM_ECG_ELEC_POTL_V4:
            return 0x0106;
        case NOM_ECG_ELEC_POTL_V5:
            return 0x0107;
        case NOM_ECG_ELEC_POTL_V6:
            return 0x0108;
        case NOM_ECG_ELEC_POTL_III:
            return 0x013D;
        case NOM_ECG_ELEC_POTL_AVR:
            return 0x013E;
        case NOM_ECG_ELEC_POTL_AVL:
            return 0x013F;
        case NOM_ECG_ELEC_POTL_AVF:
            return 0x0140;
        case NOM_ECG_ELEC_POTL_V:
            return 0x0143;
        case NOM_ECG_ELEC_POTL_MCL:
            return 0x014B;
        case NOM_ECG_ELEC_POTL_MCL1:
            return 0x014C;
        case NOM_ECG_AMPL_ST_I:
            return 0x0301;
        case NOM_ECG_AMPL_ST_II:
            return 0x0302;
        case NOM_ECG_AMPL_ST_V1:
            return 0x0303;
        case NOM_ECG_AMPL_ST_V2:
            return 0x0304;
        case NOM_ECG_AMPL_ST_V3:
            return 0x0305;
        case NOM_ECG_AMPL_ST_V4:
            return 0x0306;
        case NOM_ECG_AMPL_ST_V5:
            return 0x0307;
        case NOM_ECG_AMPL_ST_V6:
            return 0x0308;
        case NOM_ECG_AMPL_ST_III:
            return 0x033D;
        case NOM_ECG_AMPL_ST_AVR:
            return 0x033E;
        case NOM_ECG_AMPL_ST_AVL:
            return 0x033F;
        case NOM_ECG_AMPL_ST_AVF:
            return 0x0340;
        case NOM_ECG_AMPL_ST_V:
            return 0x0343;
        case NOM_ECG_AMPL_ST_MCL:
            return 0x034B;
        case NOM_ECG_AMPL_ST_ES:
            return 0x0364;
        case NOM_ECG_AMPL_ST_AS:
            return 0x0365;
        case NOM_ECG_AMPL_ST_AI:
            return 0x0366;
        case NOM_ECG_TIME_PD_QT_GL:
            return 0x3F20;
        case NOM_ECG_TIME_PD_QTc:
            return 0x3F24;
        case NOM_ECG_RHY_ABSENT:
            return 0x400B;
        case NOM_ECG_RHY_NOS:
            return 0x403F;
        case NOM_ECG_CARD_BEAT_RATE:
            return 0x4182;
        case NOM_ECG_CARD_BEAT_RATE_BTB:
            return 0x418A;
        case NOM_ECG_V_P_C_CNT:
            return 0x4261;
        case NOM_PULS_RATE:
            return 0x480A;
        case NOM_PLETH_PULS_RATE:
            return 0x4822;
        case NOM_RES_VASC_SYS_INDEX:
            return 0x4900;
        case NOM_WK_LV_STROKE_INDEX:
            return 0x4904;
        case NOM_WK_RV_STROKE_INDEX:
            return 0x4908;
        case NOM_OUTPUT_CARD_INDEX:
            return 0x490C;
        case NOM_PRESS_BLD:
            return 0x4A00;
        case NOM_PRESS_BLD_SYS:
            return 0x4A01;
        case NOM_PRESS_BLD_DIA:
            return 0x4A02;
        case NOM_PRESS_BLD_MEAN:
            return 0x4A03;
        case NOM_PRESS_BLD_NONINV:
            return 0x4A04;
        case NOM_PRESS_BLD_NONINV_SYS:
            return 0x4A05;
        case NOM_PRESS_BLD_NONINV_DIA:
            return 0x4A06;
        case NOM_PRESS_BLD_NONINV_MEAN:
            return 0x4A07;
        case NOM_PRESS_BLD_AORT:
            return 0x4A0C;
        case NOM_PRESS_BLD_AORT_SYS:
            return 0x4A0D;
        case NOM_PRESS_BLD_AORT_DIA:
            return 0x4A0E;
        case NOM_PRESS_BLD_AORT_MEAN:
            return 0x4A0F;
        case NOM_PRESS_BLD_ART:
            return 0x4A10;
        case NOM_PRESS_BLD_ART_SYS:
            return 0x4A11;
        case NOM_PRESS_BLD_ART_DIA:
            return 0x4A12;
        case NOM_PRESS_BLD_ART_MEAN:
            return 0x4A13;
        case NOM_PRESS_BLD_ART_ABP:
            return 0x4A14;
        case NOM_PRESS_BLD_ART_ABP_SYS:
            return 0x4A15;
        case NOM_PRESS_BLD_ART_ABP_DIA:
            return 0x4A16;
        case NOM_PRESS_BLD_ART_ABP_MEAN:
            return 0x4A17;
        case NOM_PRESS_BLD_ART_PULM:
            return 0x4A1C;
        case NOM_PRESS_BLD_ART_PULM_SYS:
            return 0x4A1D;
        case NOM_PRESS_BLD_ART_PULM_DIA:
            return 0x4A1E;
        case NOM_PRESS_BLD_ART_PULM_MEAN:
            return 0x4A1F;
        case NOM_PRESS_BLD_ART_PULM_WEDGE:
            return 0x4A24;
        case NOM_PRESS_BLD_ART_UMB:
            return 0x4A28;
        case NOM_PRESS_BLD_ART_UMB_SYS:
            return 0x4A29;
        case NOM_PRESS_BLD_ART_UMB_DIA:
            return 0x4A2A;
        case NOM_PRESS_BLD_ART_UMB_MEAN:
            return 0x4A2B;
        case NOM_PRESS_BLD_ATR_LEFT:
            return 0x4A30;
        case NOM_PRESS_BLD_ATR_LEFT_SYS:
            return 0x4A31;
        case NOM_PRESS_BLD_ATR_LEFT_DIA:
            return 0x4A32;
        case NOM_PRESS_BLD_ATR_LEFT_MEAN:
            return 0x4A33;
        case NOM_PRESS_BLD_ATR_RIGHT:
            return 0x4A34;
        case NOM_PRESS_BLD_ATR_RIGHT_SYS:
            return 0x4A35;
        case NOM_PRESS_BLD_ATR_RIGHT_DIA:
            return 0x4A36;
        case NOM_PRESS_BLD_ATR_RIGHT_MEAN:
            return 0x4A37;
        case NOM_PRESS_BLD_VEN_CENT:
            return 0x4A44;
        case NOM_PRESS_BLD_VEN_CENT_SYS:
            return 0x4A45;
        case NOM_PRESS_BLD_VEN_CENT_DIA:
            return 0x4A46;
        case NOM_PRESS_BLD_VEN_CENT_MEAN:
            return 0x4A47;
        case NOM_PRESS_BLD_VEN_UMB:
            return 0x4A48;
        case NOM_PRESS_BLD_VEN_UMB_SYS:
            return 0x4A49;
        case NOM_PRESS_BLD_VEN_UMB_DIA:
            return 0x4A4A;
        case NOM_PRESS_BLD_VEN_UMB_MEAN:
            return 0x4A4B;
        case NOM_SAT_O2_CONSUMP:
            return 0x4B00;
        case NOM_OUTPUT_CARD:
            return 0x4B04;
        case NOM_RES_VASC_PULM:
            return 0x4B24;
        case NOM_RES_VASC_SYS:
            return 0x4B28;
        case NOM_SAT_O2:
            return 0x4B2C;
        case NOM_SAT_O2_ART:
            return 0x4B34;
        case NOM_SAT_O2_VEN:
            return 0x4B3C;
        case NOM_SAT_DIFF_O2_ART_ALV:
            return 0x4B40;
        case NOM_SETT_TEMP:
            return 0x4B48;
        case NOM_TEMP_ART:
            return 0x4B50;
        case NOM_TEMP_AWAY:
            return 0x4B54;
        case NOM_TEMP_CORE:
            return 0x4B60;
        case NOM_TEMP_ESOPH:
            return 0x4B64;
        case NOM_TEMP_INJ:
            return 0x4B68;
        case NOM_TEMP_NASOPH:
            return 0x4B6C;
        case NOM_TEMP_SKIN:
            return 0x4B74;
        case NOM_TEMP_TYMP:
            return 0x4B78;
        case NOM_TEMP_VEN:
            return 0x4B7C;
        case NOM_VOL_BLD_STROKE:
            return 0x4B84;
        case NOM_WK_CARD_LEFT:
            return 0x4B90;
        case NOM_WK_CARD_RIGHT:
            return 0x4B94;
        case NOM_WK_LV_STROKE:
            return 0x4B9C;
        case NOM_WK_RV_STROKE:
            return 0x4BA4;
        case NOM_PULS_OXIM_PERF_REL:
            return 0x4BB0;
        case NOM_PLETH:
            return 0x4BB4;
        case NOM_PULS_OXIM_SAT_O2:
            return 0x4BB8;
        case NOM_PULS_OXIM_SAT_O2_DIFF:
            return 0x4BC4;
        case NOM_PULS_OXIM_SAT_O2_ART_LEFT:
            return 0x4BC8;
        case NOM_PULS_OXIM_SAT_O2_ART_RIGHT:
            return 0x4BCC;
        case NOM_OUTPUT_CARD_CTS:
            return 0x4BDC;
        case NOM_VOL_VENT_L_END_SYS:
            return 0x4C04;
        case NOM_GRAD_PRESS_BLD_AORT_POS_MAX:
            return 0x4C25;
        case NOM_RESP:
            return 0x5000;
        case NOM_RESP_RATE:
            return 0x500A;
        case NOM_AWAY_RESP_RATE:
            return 0x5012;
        case NOM_CAPAC_VITAL:
            return 0x5080;
        case NOM_COMPL_LUNG:
            return 0x5088;
        case NOM_COMPL_LUNG_DYN:
            return 0x508C;
        case NOM_COMPL_LUNG_STATIC:
            return 0x5090;
        case NOM_AWAY_CO2:
            return 0x50AC;
        case NOM_AWAY_CO2_ET:
            return 0x50B0;
        case NOM_AWAY_CO2_INSP_MIN:
            return 0x50BA;
        case NOM_CO2_TCUT:
            return 0x50CC;
        case NOM_O2_TCUT:
            return 0x50D0;
        case NOM_FLOW_AWAY:
            return 0x50D4;
        case NOM_FLOW_AWAY_EXP_MAX:
            return 0x50D9;
        case NOM_FLOW_AWAY_INSP_MAX:
            return 0x50DD;
        case NOM_FLOW_CO2_PROD_RESP:
            return 0x50E0;
        case NOM_IMPED_TTHOR:
            return 0x50E4;
        case NOM_PRESS_RESP_PLAT:
            return 0x50E8;
        case NOM_PRESS_AWAY:
            return 0x50F0;
        case NOM_SETT_PRESS_AWAY_MIN:
            return 0x50F2;
        case NOM_PRESS_AWAY_CTS_POS:
            return 0x50F4;
        case NOM_PRESS_AWAY_NEG_MAX:
            return 0x50F9;
        case NOM_PRESS_AWAY_END_EXP_POS_INTRINSIC:
            return 0x5100;
        case NOM_PRESS_AWAY_INSP:
            return 0x5108;
        case NOM_PRESS_AWAY_INSP_MAX:
            return 0x5109;
        case NOM_PRESS_AWAY_INSP_MEAN:
            return 0x510B;
        case NOM_RATIO_IE:
            return 0x5118;
        case NOM_RATIO_AWAY_DEADSP_TIDAL:
            return 0x511C;
        case NOM_RES_AWAY:
            return 0x5120;
        case NOM_RES_AWAY_EXP:
            return 0x5124;
        case NOM_RES_AWAY_INSP:
            return 0x5128;
        case NOM_TIME_PD_APNEA:
            return 0x5130;
        case NOM_VOL_AWAY_TIDAL:
            return 0x513C;
        case NOM_VOL_MINUTE_AWAY:
            return 0x5148;
        case NOM_VOL_MINUTE_AWAY_EXP:
            return 0x514C;
        case NOM_VOL_MINUTE_AWAY_INSP:
            return 0x5150;
        case NOM_VENT_CONC_AWAY_CO2_INSP:
            return 0x5160;
        case NOM_CONC_AWAY_O2:
            return 0x5164;
        case NOM_VENT_CONC_AWAY_O2_DELTA:
            return 0x5168;
        case NOM_VENT_AWAY_CO2_EXP:
            return 0x517C;
        case NOM_VENT_FLOW_INSP:
            return 0x518C;
        case NOM_VENT_FLOW_RATIO_PERF_ALV_INDEX:
            return 0x5190;
        case NOM_VENT_PRESS_OCCL:
            return 0x519C;
        case NOM_VENT_PRESS_AWAY_END_EXP_POS:
            return 0x51A8;
        case NOM_VENT_VOL_AWAY_DEADSP:
            return 0x51B0;
        case NOM_VENT_VOL_AWAY_DEADSP_REL:
            return 0x51B4;
        case NOM_SETT_VENT_VOL_LUNG_TRAPD:
            return 0x51B8;
        case NOM_SETT_VENT_VOL_MINUTE_AWAY_MAND:
            return 0x51CC;
        case NOM_COEF_GAS_TRAN:
            return 0x51D4;
        case NOM_CONC_AWAY_DESFL:
            return 0x51D8;
        case NOM_CONC_AWAY_ENFL:
            return 0x51DC;
        case NOM_CONC_AWAY_HALOTH:
            return 0x51E0;
        case NOM_CONC_AWAY_SEVOFL:
            return 0x51E4;
        case NOM_CONC_AWAY_ISOFL:
            return 0x51E8;
        case NOM_CONC_AWAY_N2O:
            return 0x51F0;
        case NOM_CONC_AWAY_DESFL_ET:
            return 0x5214;
        case NOM_CONC_AWAY_ENFL_ET:
            return 0x5218;
        case NOM_CONC_AWAY_HALOTH_ET:
            return 0x521C;
        case NOM_CONC_AWAY_SEVOFL_ET:
            return 0x5220;
        case NOM_CONC_AWAY_ISOFL_ET:
            return 0x5224;
        case NOM_CONC_AWAY_N2O_ET:
            return 0x522C;
        case NOM_CONC_AWAY_DESFL_INSP:
            return 0x5268;
        case NOM_CONC_AWAY_ENFL_INSP:
            return 0x526C;
        case NOM_CONC_AWAY_HALOTH_INSP:
            return 0x5270;
        case NOM_CONC_AWAY_SEVOFL_INSP:
            return 0x5274;
        case NOM_CONC_AWAY_ISOFL_INSP:
            return 0x5278;
        case NOM_CONC_AWAY_N2O_INSP:
            return 0x5280;
        case NOM_CONC_AWAY_O2_INSP:
            return 0x5284;
        case NOM_VENT_TIME_PD_PPV:
            return 0x5360;
        case NOM_VENT_PRESS_RESP_PLAT:
            return 0x5368;
        case NOM_VENT_VOL_LEAK:
            return 0x5370;
        case NOM_VENT_VOL_LUNG_ALV:
            return 0x5374;
        case NOM_CONC_AWAY_O2_ET:
            return 0x5378;
        case NOM_CONC_AWAY_N2:
            return 0x537C;
        case NOM_CONC_AWAY_N2_ET:
            return 0x5380;
        case NOM_CONC_AWAY_N2_INSP:
            return 0x5384;
        case NOM_CONC_AWAY_AGENT:
            return 0x5388;
        case NOM_CONC_AWAY_AGENT_ET:
            return 0x538C;
        case NOM_CONC_AWAY_AGENT_INSP:
            return 0x5390;
        case NOM_PRESS_CEREB_PERF:
            return 0x5804;
        case NOM_PRESS_INTRA_CRAN:
            return 0x5808;
        case NOM_PRESS_INTRA_CRAN_SYS:
            return 0x5809;
        case NOM_PRESS_INTRA_CRAN_DIA:
            return 0x580A;
        case NOM_PRESS_INTRA_CRAN_MEAN:
            return 0x580B;
        case NOM_SCORE_GLAS_COMA:
            return 0x5880;
        case NOM_SCORE_EYE_SUBSC_GLAS_COMA:
            return 0x5882;
        case NOM_SCORE_MOTOR_SUBSC_GLAS_COMA:
            return 0x5883;
        case NOM_SCORE_SUBSC_VERBAL_GLAS_COMA:
            return 0x5884;
        case NOM_CIRCUM_HEAD:
            return 0x5900;
        case NOM_TIME_PD_PUPIL_REACT_LEFT:
            return 0x5924;
        case NOM_TIME_PD_PUPIL_REACT_RIGHT:
            return 0x5928;
        case NOM_EEG_ELEC_POTL_CRTX:
            return 0x592C;
        case NOM_EMG_ELEC_POTL_MUSCL:
            return 0x593C;
        case NOM_EEG_FREQ_PWR_SPEC_CRTX_DOM_MEAN:
            return 0x597C;
        case NOM_EEG_FREQ_PWR_SPEC_CRTX_PEAK:
            return 0x5984;
        case NOM_EEG_FREQ_PWR_SPEC_CRTX_SPECTRAL_EDGE:
            return 0x5988;
        case NOM_EEG_PWR_SPEC_TOT:
            return 0x59B8;
        case NOM_EEG_PWR_SPEC_ALPHA_REL:
            return 0x59D4;
        case NOM_EEG_PWR_SPEC_BETA_REL:
            return 0x59D8;
        case NOM_EEG_PWR_SPEC_DELTA_REL:
            return 0x59DC;
        case NOM_EEG_PWR_SPEC_THETA_REL:
            return 0x59E0;
        case NOM_FLOW_URINE_INSTANT:
            return 0x680C;
        case NOM_VOL_URINE_BAL_PD:
            return 0x6824;
        case NOM_VOL_URINE_COL:
            return 0x6830;
        case NOM_SETT_FLOW_FLUID_PUMP:
            return 0x6858;
        case NOM_VOL_INFUS_ACTUAL_TOTAL:
            return 0x68FC;
        case NOM_CONC_PH_ART:
            return 0x7004;
        case NOM_CONC_PCO2_ART:
            return 0x7008;
        case NOM_CONC_PO2_ART:
            return 0x700C;
        case NOM_CONC_HB_ART:
            return 0x7014;
        case NOM_CONC_HB_O2_ART:
            return 0x7018;
        case NOM_CONC_PH_VEN:
            return 0x7034;
        case NOM_CONC_PCO2_VEN:
            return 0x7038;
        case NOM_CONC_PO2_VEN:
            return 0x703C;
        case NOM_CONC_HB_O2_VEN:
            return 0x7048;
        case NOM_CONC_PH_URINE:
            return 0x7064;
        case NOM_CONC_NA_URINE:
            return 0x706C;
        case NOM_CONC_NA_SERUM:
            return 0x70D8;
        case NOM_CONC_PH_GEN:
            return 0x7104;
        case NOM_CONC_HCO3_GEN:
            return 0x7108;
        case NOM_CONC_NA_GEN:
            return 0x710C;
        case NOM_CONC_K_GEN:
            return 0x7110;
        case NOM_CONC_GLU_GEN:
            return 0x7114;
        case NOM_CONC_CA_GEN:
            return 0x7118;
        case NOM_CONC_PCO2_GEN:
            return 0x7140;
        case NOM_CONC_CHLORIDE_GEN:
            return 0x7168;
        case NOM_BASE_EXCESS_BLD_ART:
            return 0x716C;
        case NOM_CONC_PO2_GEN:
            return 0x7174;
        case NOM_CONC_HB_MET_GEN:
            return 0x717C;
        case NOM_CONC_HB_CO_GEN:
            return 0x7180;
        case NOM_CONC_HCT_GEN:
            return 0x7184;
        case NOM_VENT_CONC_AWAY_O2_INSP:
            return 0x7498;
        case NOM_VENT_MODE_MAND_INTERMIT:
            return 0xD02A;
        case NOM_TEMP_RECT:
            return 0xE004;
        case NOM_TEMP_BLD:
            return 0xE014;
        case NOM_TEMP_DIFF:
            return 0xE018;
        case NOM_METRIC_NOS:
            return 0xEFFF;
        case NOM_ECG_AMPL_ST_INDEX:
            return 0xF03D;
        case NOM_TIME_TCUT_SENSOR:
            return 0xF03E;
        case NOM_TEMP_TCUT_SENSOR:
            return 0xF03F;
        case NOM_VOL_BLD_INTRA_THOR:
            return 0xF040;
        case NOM_VOL_BLD_INTRA_THOR_INDEX:
            return 0xF041;
        case NOM_VOL_LUNG_WATER_EXTRA_VASC:
            return 0xF042;
        case NOM_VOL_LUNG_WATER_EXTRA_VASC_INDEX:
            return 0xF043;
        case NOM_VOL_GLOBAL_END_DIA:
            return 0xF044;
        case NOM_VOL_GLOBAL_END_DIA_INDEX:
            return 0xF045;
        case NOM_CARD_FUNC_INDEX:
            return 0xF046;
        case NOM_OUTPUT_CARD_INDEX_CTS:
            return 0xF047;
        case NOM_VOL_BLD_STROKE_INDEX:
            return 0xF048;
        case NOM_VOL_BLD_STROKE_VAR:
            return 0xF049;
        case NOM_EEG_RATIO_SUPPRN:
            return 0xF04A;
        case NOM_EEG_BIS_SIG_QUAL_INDEX:
            return 0xF04D;
        case NOM_EEG_BISPECTRAL_INDEX:
            return 0xF04E;
        case NOM_GAS_TCUT:
            return 0xF051;
        case NOM_CONC_AWAY_SUM_MAC_ET:
            return 0xF05E;
        case NOM_CONC_AWAY_SUM_MAC_INSP:
            return 0xF05F;
        case NOM_RES_VASC_PULM_INDEX:
            return 0xF067;
        case NOM_WK_CARD_LEFT_INDEX:
            return 0xF068;
        case NOM_WK_CARD_RIGHT_INDEX:
            return 0xF069;
        case NOM_SAT_O2_CONSUMP_INDEX:
            return 0xF06A;
        case NOM_PRESS_AIR_AMBIENT:
            return 0xF06B;
        case NOM_SAT_DIFF_O2_ART_VEN:
            return 0xF06C;
        case NOM_SAT_O2_DELIVER:
            return 0xF06D;
        case NOM_SAT_O2_DELIVER_INDEX:
            return 0xF06E;
        case NOM_RATIO_SAT_O2_CONSUMP_DELIVER:
            return 0xF06F;
        case NOM_RATIO_ART_VEN_SHUNT:
            return 0xF070;
        case NOM_AREA_BODY_SURFACE:
            return 0xF071;
        case NOM_INTENS_LIGHT:
            return 0xF072;
        case NOM_HEATING_PWR_TCUT_SENSOR:
            return 0xF076;
        case NOM_VOL_INJ:
            return 0xF079;
        case NOM_VOL_THERMO_EXTRA_VASC_INDEX:
            return 0xF07A;
        case NOM_NUM_CATHETER_CONST:
            return 0xF07C;
        case NOM_PULS_OXIM_PERF_REL_LEFT:
            return 0xF08A;
        case NOM_PULS_OXIM_PERF_REL_RIGHT:
            return 0xF08B;
        case NOM_PULS_OXIM_PLETH_RIGHT:
            return 0xF08C;
        case NOM_PULS_OXIM_PLETH_LEFT:
            return 0xF08D;
        case NOM_CONC_BLD_UREA_NITROGEN:
            return 0xF08F;
        case NOM_CONC_BASE_EXCESS_ECF:
            return 0xF090;
        case NOM_VENT_VOL_MINUTE_AWAY_SPONT:
            return 0xF091;
        case NOM_CONC_DIFF_HB_O2_ATR_VEN:
            return 0xF092;
        case NOM_PAT_WEIGHT:
            return 0xF093;
        case NOM_PAT_HEIGHT:
            return 0xF094;
        case NOM_CONC_AWAY_MAC:
            return 0xF099;
        case NOM_PULS_OXIM_PLETH_TELE:
            return 0xF09B;
        case NOM_PULS_OXIM_SAT_O2_TELE:
            return 0xF09C;
        case NOM_PULS_OXIM_PULS_RATE_TELE:
            return 0xF09D;
        case NOM_PRESS_GEN_1:
            return 0xF0A4;
        case NOM_PRESS_GEN_1_SYS:
            return 0xF0A5;
        case NOM_PRESS_GEN_1_DIA:
            return 0xF0A6;
        case NOM_PRESS_GEN_1_MEAN:
            return 0xF0A7;
        case NOM_PRESS_GEN_2:
            return 0xF0A8;
        case NOM_PRESS_GEN_2_SYS:
            return 0xF0A9;
        case NOM_PRESS_GEN_2_DIA:
            return 0xF0AA;
        case NOM_PRESS_GEN_2_MEAN:
            return 0xF0AB;
        case NOM_PRESS_GEN_3:
            return 0xF0AC;
        case NOM_PRESS_GEN_3_SYS:
            return 0xF0AD;
        case NOM_PRESS_GEN_3_MEAN:
            return 0xF0AF;
        case NOM_PRESS_GEN_4:
            return 0xF0B0;
        case NOM_PRESS_GEN_4_SYS:
            return 0xF0B1;
        case NOM_PRESS_GEN_4_DIA:
            return 0xF0B2;
        case NOM_PRESS_GEN_4_MEAN:
            return 0xF0B3;
        case NOM_PRESS_INTRA_CRAN_1:
            return 0xF0B4;
        case NOM_PRESS_INTRA_CRAN_1_SYS:
            return 0xF0B5;
        case NOM_PRESS_INTRA_CRAN_1_DIA:
            return 0xF0B6;
        case NOM_PRESS_INTRA_CRAN_1_MEAN:
            return 0xF0B7;
        case NOM_PRESS_INTRA_CRAN_2:
            return 0xF0B8;
        case NOM_PRESS_INTRA_CRAN_2_SYS:
            return 0xF0B9;
        case NOM_PRESS_INTRA_CRAN_2_DIA:
            return 0xF0BA;
        case NOM_PRESS_INTRA_CRAN_2_MEAN:
            return 0xF0BB;
        case NOM_PRESS_BLD_ART_FEMORAL:
            return 0xF0BC;
        case NOM_PRESS_BLD_ART_FEMORAL_SYS:
            return 0xF0BD;
        case NOM_PRESS_BLD_ART_FEMORAL_DIA:
            return 0xF0BE;
        case NOM_PRESS_BLD_ART_FEMORAL_MEAN:
            return 0xF0BF;
        case NOM_PRESS_BLD_ART_BRACHIAL:
            return 0xF0C0;
        case NOM_PRESS_BLD_ART_BRACHIAL_SYS:
            return 0xF0C1;
        case NOM_PRESS_BLD_ART_BRACHIAL_DIA:
            return 0xF0C2;
        case NOM_PRESS_BLD_ART_BRACHIAL_MEAN:
            return 0xF0C3;
        case NOM_TEMP_VESICAL:
            return 0xF0C4;
        case NOM_TEMP_CEREBRAL:
            return 0xF0C5;
        case NOM_TEMP_AMBIENT:
            return 0xF0C6;
        case NOM_TEMP_GEN_1:
            return 0xF0C7;
        case NOM_TEMP_GEN_2:
            return 0xF0C8;
        case NOM_TEMP_GEN_3:
            return 0xF0C9;
        case NOM_TEMP_GEN_4:
            return 0xF0CA;
        case NOM_SETT_VOL_AWAY_INSP_TIDAL:
            return 0xF0E0;
        case NOM_VOL_AWAY_EXP_TIDAL:
            return 0xF0E1;
        case NOM_AWAY_RESP_RATE_SPIRO:
            return 0xF0E2;
        case NOM_PULS_PRESS_VAR:
            return 0xF0E3;
        case NOM_PRESS_BLD_NONINV_PULS_RATE:
            return 0xF0E5;
        case NOM_VENT_RESP_RATE_MAND:
            return 0xF0F1;
        case NOM_VENT_VOL_TIDAL_MAND:
            return 0xF0F2;
        case NOM_VENT_VOL_TIDAL_SPONT:
            return 0xF0F3;
        case NOM_CARDIAC_TROPONIN_I:
            return 0xF0F4;
        case NOM_CARDIO_PULMONARY_BYPASS_MODE:
            return 0xF0F5;
        case NOM_BNP:
            return 0xF0F6;
        case NOM_SETT_TIME_PD_RESP_PLAT:
            return 0xF0FF;
        case NOM_SAT_O2_VEN_CENT:
            return 0xF100;
        case NOM_SNR:
            return 0xF101;
        case NOM_HUMID:
            return 0xF103;
        case NOM_FRACT_EJECT:
            return 0xF105;
        case NOM_PERM_VASC_PULM_INDEX:
            return 0xF106;
        case NOM_TEMP_ORAL_PRED:
            return 0xF110;
        case NOM_TEMP_RECT_PRED:
            return 0xF114;
        case NOM_TEMP_AXIL_PRED:
            return 0xF118;
        case NOM_TEMP_AIR_INCUB:
            return 0xF12A;
        case NOM_PULS_OXIM_PERF_REL_TELE:
            return 0xF12C;
        case NOM_SHUNT_RIGHT_LEFT:
            return 0xF14A;
        case NOM_ECG_TIME_PD_QT_HEART_RATE:
            return 0xF154;
        case NOM_ECG_TIME_PD_QT_BASELINE:
            return 0xF155;
        case NOM_ECG_TIME_PD_QTc_DELTA:
            return 0xF156;
        case NOM_ECG_TIME_PD_QT_BASELINE_HEART_RATE:
            return 0xF157;
        case NOM_CONC_PH_CAP:
            return 0xF158;
        case NOM_CONC_PCO2_CAP:
            return 0xF159;
        case NOM_CONC_PO2_CAP:
            return 0xF15A;
        case NOM_CONC_MG_ION:
            return 0xF15B;
        case NOM_CONC_MG_SER:
            return 0xF15C;
        case NOM_CONC_tCA_SER:
            return 0xF15D;
        case NOM_CONC_P_SER:
            return 0xF15E;
        case NOM_CONC_CHLOR_SER:
            return 0xF15F;
        case NOM_CONC_FE_GEN:
            return 0xF160;
        case NOM_CONC_ALB_SER:
            return 0xF163;
        case NOM_SAT_O2_ART_CALC:
            return 0xF164;
        case NOM_CONC_HB_FETAL:
            return 0xF165;
        case NOM_PLTS_CNT:
            return 0xF167;
        case NOM_WB_CNT:
            return 0xF168;
        case NOM_RB_CNT:
            return 0xF169;
        case NOM_RET_CNT:
            return 0xF16A;
        case NOM_PLASMA_OSM:
            return 0xF16B;
        case NOM_CONC_CREA_CLR:
            return 0xF16C;
        case NOM_NSLOSS:
            return 0xF16D;
        case NOM_CONC_CHOLESTEROL:
            return 0xF16E;
        case NOM_CONC_TGL:
            return 0xF16F;
        case NOM_CONC_HDL:
            return 0xF170;
        case NOM_CONC_LDL:
            return 0xF171;
        case NOM_CONC_UREA_GEN:
            return 0xF172;
        case NOM_CONC_CREA:
            return 0xF173;
        case NOM_CONC_LACT:
            return 0xF174;
        case NOM_CONC_BILI_TOT:
            return 0xF177;
        case NOM_CONC_PROT_SER:
            return 0xF178;
        case NOM_CONC_PROT_TOT:
            return 0xF179;
        case NOM_CONC_BILI_DIRECT:
            return 0xF17A;
        case NOM_CONC_LDH:
            return 0xF17B;
        case NOM_ES_RATE:
            return 0xF17C;
        case NOM_CONC_PCT:
            return 0xF17D;
        case NOM_CONC_CREA_KIN_MM:
            return 0xF17F;
        case NOM_CONC_CREA_KIN_SER:
            return 0xF180;
        case NOM_CONC_CREA_KIN_MB:
            return 0xF181;
        case NOM_CONC_CHE:
            return 0xF182;
        case NOM_CONC_CRP:
            return 0xF183;
        case NOM_CONC_AST:
            return 0xF184;
        case NOM_CONC_AP:
            return 0xF185;
        case NOM_CONC_ALPHA_AMYLASE:
            return 0xF186;
        case NOM_CONC_GPT:
            return 0xF187;
        case NOM_CONC_GOT:
            return 0xF188;
        case NOM_CONC_GGT:
            return 0xF189;
        case NOM_TIME_PD_ACT:
            return 0xF18A;
        case NOM_TIME_PD_PT:
            return 0xF18B;
        case NOM_PT_INTL_NORM_RATIO:
            return 0xF18C;
        case NOM_TIME_PD_aPTT_WB:
            return 0xF18D;
        case NOM_TIME_PD_aPTT_PE:
            return 0xF18E;
        case NOM_TIME_PD_PT_WB:
            return 0xF18F;
        case NOM_TIME_PD_PT_PE:
            return 0xF190;
        case NOM_TIME_PD_THROMBIN:
            return 0xF191;
        case NOM_TIME_PD_COAGULATION:
            return 0xF192;
        case NOM_TIME_PD_THROMBOPLAS:
            return 0xF193;
        case NOM_FRACT_EXCR_NA:
            return 0xF194;
        case NOM_CONC_UREA_URINE:
            return 0xF195;
        case NOM_CONC_CREA_URINE:
            return 0xF196;
        case NOM_CONC_K_URINE:
            return 0xF197;
        case NOM_CONC_K_URINE_EXCR:
            return 0xF198;
        case NOM_CONC_OSM_URINE:
            return 0xF199;
        case NOM_CONC_CHLOR_URINE:
            return 0xF19A;
        case NOM_CONC_PRO_URINE:
            return 0xF19B;
        case NOM_CONC_CA_URINE:
            return 0xF19C;
        case NOM_FLUID_DENS_URINE:
            return 0xF19D;
        case NOM_CONC_HB_URINE:
            return 0xF19E;
        case NOM_CONC_GLU_URINE:
            return 0xF19F;
        case NOM_SAT_O2_CAP_CALC:
            return 0xF1A0;
        case NOM_CONC_AN_GAP_CALC:
            return 0xF1A1;
        case NOM_PULS_OXIM_SAT_O2_PRE_DUCTAL:
            return 0xF1C0;
        case NOM_PULS_OXIM_SAT_O2_POST_DUCTAL:
            return 0xF1D4;
        case NOM_PULS_OXIM_PERF_REL_POST_DUCTAL:
            return 0xF1DC;
        case NOM_PULS_OXIM_PERF_REL_PRE_DUCTAL:
            return 0xF22C;
        case NOM_PRESS_GEN_5:
            return 0xF3F4;
        case NOM_PRESS_GEN_5_SYS:
            return 0xF3F5;
        case NOM_PRESS_GEN_5_DIA:
            return 0xF3F6;
        case NOM_PRESS_GEN_5_MEAN:
            return 0xF3F7;
        case NOM_PRESS_GEN_6:
            return 0xF3F8;
        case NOM_PRESS_GEN_6_SYS:
            return 0xF3F9;
        case NOM_PRESS_GEN_6_DIA:
            return 0xF3FA;
        case NOM_PRESS_GEN_6_MEAN:
            return 0xF3FB;
        case NOM_PRESS_GEN_7:
            return 0xF3FC;
        case NOM_PRESS_GEN_7_SYS:
            return 0xF3FD;
        case NOM_PRESS_GEN_7_MEAN:
            return 0xF3FF;
        case NOM_PRESS_GEN_8:
            return 0xF400;
        case NOM_PRESS_GEN_8_SYS:
            return 0xF401;
        case NOM_PRESS_GEN_8_DIA:
            return 0xF402;
        case NOM_PRESS_GEN_8_MEAN:
            return 0xF403;
        case NOM_ECG_AMPL_ST_BASELINE_I:
            return 0xF411;
        case NOM_ECG_AMPL_ST_BASELINE_II:
            return 0xF412;
        case NOM_ECG_AMPL_ST_BASELINE_V1:
            return 0xF413;
        case NOM_ECG_AMPL_ST_BASELINE_V2:
            return 0xF414;
        case NOM_ECG_AMPL_ST_BASELINE_V3:
            return 0xF415;
        case NOM_ECG_AMPL_ST_BASELINE_V4:
            return 0xF416;
        case NOM_ECG_AMPL_ST_BASELINE_V5:
            return 0xF417;
        case NOM_ECG_AMPL_ST_BASELINE_V6:
            return 0xF418;
        case NOM_ECG_AMPL_ST_BASELINE_III:
            return 0xF44D;
        case NOM_ECG_AMPL_ST_BASELINE_AVR:
            return 0xF44E;
        case NOM_ECG_AMPL_ST_BASELINE_AVL:
            return 0xF44F;
        case NOM_ECG_AMPL_ST_BASELINE_AVF:
            return 0xF450;
        case NOM_AGE:
            return 0xF810;
        case NOM_AGE_GEST:
            return 0xF811;
        case NOM_AWAY_CORR_COEF:
            return 0xF814;
        case NOM_AWAY_RESP_RATE_SPONT:
            return 0xF815;
        case NOM_AWAY_TC:
            return 0xF816;
        case NOM_BIRTH_LENGTH:
            return 0xF818;
        case NOM_BREATH_RAPID_SHALLOW_INDEX:
            return 0xF819;
        case NOM_C20_PER_C_INDEX:
            return 0xF81A;
        case NOM_CARD_CONTRACT_HEATHER_INDEX:
            return 0xF81C;
        case NOM_CONC_ALP:
            return 0xF81D;
        case NOM_CONC_CA_GEN_NORM:
            return 0xF822;
        case NOM_CONC_CA_SER:
            return 0xF824;
        case NOM_CONC_CO2_TOT:
            return 0xF825;
        case NOM_CONC_CO2_TOT_CALC:
            return 0xF826;
        case NOM_CONC_CREA_SER:
            return 0xF827;
        case NOM_RESP_RATE_SPONT:
            return 0xF828;
        case NOM_CONC_GLO_SER:
            return 0xF829;
        case NOM_CONC_GLU_SER:
            return 0xF82A;
        case NOM_CONC_HB_CORP_MEAN:
            return 0xF82C;
        case NOM_CONC_K_SER:
            return 0xF82F;
        case NOM_CONC_NA_EXCR:
            return 0xF830;
        case NOM_CONC_PCO2_ART_ADJ:
            return 0xF832;
        case NOM_CONC_PCO2_CAP_ADJ:
            return 0xF833;
        case NOM_CONC_PH_CAP_ADJ:
            return 0xF837;
        case NOM_CONC_PH_GEN_ADJ:
            return 0xF838;
        case NOM_CONC_PO2_ART_ADJ:
            return 0xF83B;
        case NOM_CONC_PO2_CAP_ADJ:
            return 0xF83C;
        case NOM_CREA_OSM:
            return 0xF83F;
        case NOM_EEG_BURST_SUPPRN_INDEX:
            return 0xF840;
        case NOM_EEG_ELEC_POTL_CRTX_GAIN_LEFT:
            return 0xF841;
        case NOM_EEG_ELEC_POTL_CRTX_GAIN_RIGHT:
            return 0xF842;
        case NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_LEFT:
            return 0xF84B;
        case NOM_EEG_FREQ_PWR_SPEC_CRTX_MEDIAN_RIGHT:
            return 0xF84C;
        case NOM_EEG_PWR_SPEC_ALPHA_ABS_LEFT:
            return 0xF855;
        case NOM_EEG_PWR_SPEC_ALPHA_ABS_RIGHT:
            return 0xF856;
        case NOM_EEG_PWR_SPEC_BETA_ABS_LEFT:
            return 0xF85B;
        case NOM_EEG_PWR_SPEC_BETA_ABS_RIGHT:
            return 0xF85C;
        case NOM_EEG_PWR_SPEC_DELTA_ABS_LEFT:
            return 0xF863;
        case NOM_EEG_PWR_SPEC_DELTA_ABS_RIGHT:
            return 0xF864;
        case NOM_EEG_PWR_SPEC_THETA_ABS_LEFT:
            return 0xF869;
        case NOM_EEG_PWR_SPEC_THETA_ABS_RIGHT:
            return 0xF86A;
        case NOM_ELEC_EVOK_POTL_CRTX_ACOUSTIC_AAI:
            return 0xF873;
        case NOM_EXTRACT_O2_INDEX:
            return 0xF875;
        case NOM_SETT_FLOW_AWAY_AIR:
            return 0xF877;
        case NOM_FLOW_AWAY_EXP_ET:
            return 0xF87A;
        case NOM_FLOW_AWAY_MAX_SPONT:
            return 0xF87D;
        case NOM_SETT_FLOW_AWAY_TOT:
            return 0xF881;
        case NOM_FLOW_CO2_PROD_RESP_TIDAL:
            return 0xF882;
        case NOM_FLOW_URINE_PREV_24HR:
            return 0xF883;
        case NOM_FREE_WATER_CLR:
            return 0xF884;
        case NOM_HB_CORP_MEAN:
            return 0xF885;
        case NOM_HEATING_PWR_INCUBATOR:
            return 0xF886;
        case NOM_OUTPUT_CARD_INDEX_ACCEL:
            return 0xF889;
        case NOM_PTC_CNT:
            return 0xF88B;
        case NOM_PULS_OXIM_PLETH_GAIN:
            return 0xF88D;
        case NOM_RATIO_AWAY_RATE_VOL_AWAY:
            return 0xF88E;
        case NOM_RATIO_BUN_CREA:
            return 0xF88F;
        case NOM_RATIO_CONC_BLD_UREA_NITROGEN_CREA_CALC:
            return 0xF890;
        case NOM_RATIO_CONC_URINE_CREA_CALC:
            return 0xF891;
        case NOM_RATIO_CONC_URINE_CREA_SER:
            return 0xF892;
        case NOM_RATIO_CONC_URINE_NA_K:
            return 0xF893;
        case NOM_RATIO_PaO2_FIO2:
            return 0xF894;
        case NOM_RATIO_TIME_PD_PT:
            return 0xF895;
        case NOM_RATIO_TIME_PD_PTT:
            return 0xF896;
        case NOM_RATIO_TRAIN_OF_FOUR:
            return 0xF897;
        case NOM_RATIO_URINE_SER_OSM:
            return 0xF898;
        case NOM_RES_AWAY_DYN:
            return 0xF899;
        case NOM_RESP_BREATH_ASSIST_CNT:
            return 0xF89A;
        case NOM_RIGHT_HEART_FRACT_EJECT:
            return 0xF89B;
        case NOM_TIME_PD_EVOK_REMAIN:
            return 0xF8A0;
        case NOM_TIME_PD_EXP:
            return 0xF8A1;
        case NOM_TIME_PD_FROM_LAST_MSMT:
            return 0xF8A2;
        case NOM_TIME_PD_INSP:
            return 0xF8A3;
        case NOM_TIME_PD_KAOLIN_CEPHALINE:
            return 0xF8A4;
        case NOM_TIME_PD_PTT:
            return 0xF8A5;
        case NOM_SETT_TIME_PD_TRAIN_OF_FOUR:
            return 0xF8A6;
        case NOM_TRAIN_OF_FOUR_1:
            return 0xF8A7;
        case NOM_TRAIN_OF_FOUR_2:
            return 0xF8A8;
        case NOM_TRAIN_OF_FOUR_3:
            return 0xF8A9;
        case NOM_TRAIN_OF_FOUR_4:
            return 0xF8AA;
        case NOM_TRAIN_OF_FOUR_CNT:
            return 0xF8AB;
        case NOM_TWITCH_AMPL:
            return 0xF8AC;
        case NOM_UREA_SER:
            return 0xF8AD;
        case NOM_SETT_URINE_BAL_PD:
            return 0xF8AF;
        case NOM_VENT_ACTIVE:
            return 0xF8B0;
        case NOM_VENT_AMPL_HFV:
            return 0xF8B1;
        case NOM_VENT_CONC_AWAY_AGENT_DELTA:
            return 0xF8B2;
        case NOM_VENT_CONC_AWAY_DESFL_DELTA:
            return 0xF8B3;
        case NOM_VENT_CONC_AWAY_ENFL_DELTA:
            return 0xF8B4;
        case NOM_VENT_CONC_AWAY_HALOTH_DELTA:
            return 0xF8B5;
        case NOM_VENT_CONC_AWAY_ISOFL_DELTA:
            return 0xF8B6;
        case NOM_VENT_CONC_AWAY_N2O_DELTA:
            return 0xF8B7;
        case NOM_VENT_CONC_AWAY_O2_CIRCUIT:
            return 0xF8B8;
        case NOM_VENT_CONC_AWAY_SEVOFL_DELTA:
            return 0xF8B9;
        case NOM_VENT_PRESS_AWAY_END_EXP_POS_LIMIT_LO:
            return 0xF8BA;
        case NOM_SETT_VENT_PRESS_AWAY_PV:
            return 0xF8BC;
        case NOM_VENT_TIME_PD_RAMP:
            return 0xF8BD;
        case NOM_VENT_VOL_AWAY_INSP_TIDAL_HFV:
            return 0xF8BE;
        case NOM_VENT_VOL_TIDAL_HFV:
            return 0xF8BF;
        case NOM_SETT_VENT_VOL_TIDAL_SIGH:
            return 0xF8C0;
        case NOM_VOL_AWAY_EXP_TIDAL_SPONT:
            return 0xF8C2;
        case NOM_VOL_AWAY_TIDAL_PSV:
            return 0xF8C3;
        case NOM_VOL_CORP_MEAN:
            return 0xF8C4;
        case NOM_VOL_FLUID_THORAC:
            return 0xF8C5;
        case NOM_VOL_FLUID_THORAC_INDEX:
            return 0xF8C6;
        case NOM_VOL_LVL_LIQUID_BOTTLE_AGENT:
            return 0xF8C7;
        case NOM_VOL_LVL_LIQUID_BOTTLE_DESFL:
            return 0xF8C8;
        case NOM_VOL_LVL_LIQUID_BOTTLE_ENFL:
            return 0xF8C9;
        case NOM_VOL_LVL_LIQUID_BOTTLE_HALOTH:
            return 0xF8CA;
        case NOM_VOL_LVL_LIQUID_BOTTLE_ISOFL:
            return 0xF8CB;
        case NOM_VOL_LVL_LIQUID_BOTTLE_SEVOFL:
            return 0xF8CC;
        case NOM_VOL_MINUTE_AWAY_INSP_HFV:
            return 0xF8CD;
        case NOM_VOL_URINE_BAL_PD_INSTANT:
            return 0xF8CE;
        case NOM_VOL_URINE_SHIFT:
            return 0xF8CF;
        case NOM_VOL_VENT_L_END_SYS_INDEX:
            return 0xF8D1;
        case NOM_WEIGHT_URINE_COL:
            return 0xF8D3;
        case NOM_SETT_APNEA_ALARM_DELAY:
            return 0xF8D9;
        case NOM_SETT_AWAY_RESP_RATE_APNEA:
            return 0xF8DE;
        case NOM_SETT_AWAY_RESP_RATE_HFV:
            return 0xF8DF;
        case NOM_SETT_EVOK_CHARGE:
            return 0xF8E6;
        case NOM_SETT_EVOK_CURR:
            return 0xF8E7;
        case NOM_SETT_FLOW_AWAY_EXP:
            return 0xF8EA;
        case NOM_SETT_FLOW_AWAY_HFV:
            return 0xF8EB;
        case NOM_SETT_FLOW_AWAY_INSP:
            return 0xF8EC;
        case NOM_SETT_FLOW_AWAY_INSP_APNEA:
            return 0xF8ED;
        case NOM_SETT_HFV_AMPL:
            return 0xF8F3;
        case NOM_SETT_PRESS_AWAY_INSP_MAX_LIMIT_LO:
            return 0xF8FB;
        case NOM_SETT_RATIO_IE_EXP_PV:
            return 0xF900;
        case NOM_SETT_RATIO_IE_EXP_PV_APNEA:
            return 0xF901;
        case NOM_SETT_RATIO_IE_INSP_PV:
            return 0xF902;
        case NOM_SETT_RATIO_IE_INSP_PV_APNEA:
            return 0xF903;
        case NOM_SETT_SENS_LEVEL:
            return 0xF904;
        case NOM_SETT_TIME_PD_EVOK:
            return 0xF908;
        case NOM_SETT_TIME_PD_MSMT:
            return 0xF909;
        case NOM_SETT_VENT_ANALY_CONC_GAS_O2_MODE:
            return 0xF90E;
        case NOM_SETT_VENT_AWAY_FLOW_BACKGROUND:
            return 0xF90F;
        case NOM_SETT_VENT_AWAY_FLOW_BASE:
            return 0xF910;
        case NOM_SETT_VENT_AWAY_FLOW_SENSE:
            return 0xF911;
        case NOM_SETT_VENT_AWAY_PRESS_RATE_INCREASE:
            return 0xF912;
        case NOM_SETT_VENT_CONC_AWAY_O2_INSP_APNEA:
            return 0xF917;
        case NOM_SETT_VENT_CONC_AWAY_O2_INSP_PV_APNEA:
            return 0xF918;
        case NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_HI:
            return 0xF919;
        case NOM_SETT_VENT_CONC_AWAY_O2_LIMIT_LO:
            return 0xF91A;
        case NOM_SETT_VENT_FLOW:
            return 0xF91B;
        case NOM_SETT_VENT_FLOW_AWAY_ASSIST:
            return 0xF91C;
        case NOM_SETT_VENT_FLOW_INSP_TRIG:
            return 0xF91D;
        case NOM_SETT_VENT_GAS_PROBE_POSN:
            return 0xF920;
        case NOM_SETT_VENT_MODE_MAND_CTS_ONOFF:
            return 0xF922;
        case NOM_SETT_VENT_MODE_SIGH:
            return 0xF923;
        case NOM_SETT_VENT_MODE_SYNC_MAND_INTERMIT:
            return 0xF924;
        case NOM_SETT_VENT_O2_CAL_MODE:
            return 0xF926;
        case NOM_SETT_VENT_O2_PROBE_POSN:
            return 0xF927;
        case NOM_SETT_VENT_O2_SUCTION_MODE:
            return 0xF928;
        case NOM_SETT_VENT_PRESS_AWAY_END_EXP_POS_INTERMIT:
            return 0xF92C;
        case NOM_SETT_VENT_PRESS_AWAY_EXP_APRV:
            return 0xF92D;
        case NOM_SETT_VENT_PRESS_AWAY_INSP_APRV:
            return 0xF92E;
        case NOM_SETT_VENT_PRESS_AWAY_LIMIT_HI:
            return 0xF930;
        case NOM_SETT_VENT_PRESS_AWAY_MAX_PV_APNEA:
            return 0xF931;
        case NOM_SETT_VENT_PRESS_AWAY_PV_APNEA:
            return 0xF933;
        case NOM_SETT_VENT_PRESS_AWAY_SUST_LIMIT_HI:
            return 0xF935;
        case NOM_SETT_VENT_RESP_RATE_LIMIT_HI_PANT:
            return 0xF937;
        case NOM_SETT_VENT_RESP_RATE_MODE_PPV_INTERMIT_PAP:
            return 0xF939;
        case NOM_SETT_VENT_RESP_RATE_PV_APNEA:
            return 0xF93A;
        case NOM_SETT_VENT_SIGH_MULT_RATE:
            return 0xF93B;
        case NOM_SETT_VENT_SIGH_RATE:
            return 0xF93C;
        case NOM_SETT_VENT_TIME_PD_EXP:
            return 0xF93F;
        case NOM_SETT_VENT_TIME_PD_EXP_APRV:
            return 0xF940;
        case NOM_SETT_VENT_TIME_PD_INSP:
            return 0xF941;
        case NOM_SETT_VENT_TIME_PD_INSP_APRV:
            return 0xF942;
        case NOM_SETT_VENT_TIME_PD_INSP_PV:
            return 0xF943;
        case NOM_SETT_VENT_TIME_PD_INSP_PV_APNEA:
            return 0xF944;
        case NOM_SETT_VENT_TIME_PD_RAMP_AL:
            return 0xF946;
        case NOM_SETT_VENT_VOL_AWAY_ASSIST:
            return 0xF948;
        case NOM_SETT_VENT_VOL_LIMIT_AL_HI_ONOFF:
            return 0xF949;
        case NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_HI:
            return 0xF94B;
        case NOM_SETT_VENT_VOL_MINUTE_AWAY_LIMIT_LO:
            return 0xF94C;
        case NOM_SETT_VENT_VOL_TIDAL_LIMIT_HI:
            return 0xF94D;
        case NOM_SETT_VENT_VOL_TIDAL_LIMIT_LO:
            return 0xF94E;
        case NOM_SETT_VOL_AWAY_TIDAL_APNEA:
            return 0xF951;
        case NOM_SETT_VOL_AWAY_TIDAL_APPLIED:
            return 0xF952;
        case NOM_SETT_VOL_MINUTE_ALARM_DELAY:
            return 0xF953;
        case NOM_SAT_O2_TISSUE:
            return 0xF960;
        case NOM_CEREB_STATE_INDEX:
            return 0xF961;
        case NOM_SAT_O2_GEN_1:
            return 0xF962;
        case NOM_SAT_O2_GEN_2:
            return 0xF963;
        case NOM_SAT_O2_GEN_3:
            return 0xF964;
        case NOM_SAT_O2_GEN_4:
            return 0xF965;
        case NOM_TEMP_CORE_GEN_1:
            return 0xF966;
        case NOM_TEMP_CORE_GEN_2:
            return 0xF967;
        case NOM_PRESS_BLD_DIFF:
            return 0xF968;
        case NOM_PRESS_BLD_DIFF_GEN_1:
            return 0xF96C;
        case NOM_PRESS_BLD_DIFF_GEN_2:
            return 0xF970;
        case NOM_FLOW_PUMP_HEART_LUNG_MAIN:
            return 0xF974;
        case NOM_FLOW_PUMP_HEART_LUNG_SLAVE:
            return 0xF975;
        case NOM_FLOW_PUMP_HEART_LUNG_SUCTION:
            return 0xF976;
        case NOM_FLOW_PUMP_HEART_LUNG_AUX:
            return 0xF977;
        case NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0xF978;
        case NOM_FLOW_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0xF979;
        case NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_START:
            return 0xF97A;
        case NOM_TIME_PD_PUMP_HEART_LUNG_AUX_SINCE_STOP:
            return 0xF97B;
        case NOM_VOL_DELIV_PUMP_HEART_LUNG_AUX:
            return 0xF97C;
        case NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_AUX:
            return 0xF97D;
        case NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_AUX:
            return 0xF97E;
        case NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_START:
            return 0xF97F;
        case NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN_SINCE_STOP:
            return 0xF980;
        case NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0xF981;
        case NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0xF982;
        case NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_MAIN:
            return 0xF983;
        case NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_START:
            return 0xF984;
        case NOM_TIME_PD_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE_SINCE_STOP:
            return 0xF985;
        case NOM_VOL_DELIV_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0xF986;
        case NOM_VOL_DELIV_TOTAL_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0xF987;
        case NOM_TIME_PD_PLEGIA_PUMP_HEART_LUNG_CARDIOPLEGIA_SLAVE:
            return 0xF988;
        case NOM_RATIO_INSP_TOTAL_BREATH_SPONT:
            return 0xF990;
        case NOM_VENT_PRESS_AWAY_END_EXP_POS_TOTAL:
            return 0xF991;
        case NOM_COMPL_LUNG_PAV:
            return 0xF992;
        case NOM_RES_AWAY_PAV:
            return 0xF993;
        case NOM_RES_AWAY_EXP_TOTAL:
            return 0xF994;
        case NOM_ELAS_LUNG_PAV:
            return 0xF995;
        case NOM_BREATH_RAPID_SHALLOW_INDEX_NORM:
            return 0xF996;
        default:
            throw new IllegalArgumentException("Unknown int:"+this);
        }
    }

}
