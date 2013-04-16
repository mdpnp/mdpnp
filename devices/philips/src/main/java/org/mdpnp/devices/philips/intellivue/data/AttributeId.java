package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum AttributeId implements OrdinalEnum.IntType {
    /**
     * MetricObservedValueGroup
     * Hex: 0x801
     * Dec: 2049
     */
    NOM_ATTR_GRP_AL_MON(2049),

    /**
     * PatientDemographicsAttributeGroup
     * Hex: 0x803
     * Dec: 2051
     */
    NOM_ATTR_GRP_METRIC_VAL_OBS(2051),

    /**
     * SystemApplicationAttributeGroup
     * Hex: 0x807
     * Dec: 2055
     */
    NOM_ATTR_GRP_PT_DEMOG(2055),

    /**
     * SystemIdentificationAttributeGroup
     * Hex: 0x80A
     * Dec: 2058
     */
    NOM_ATTR_GRP_SYS_APPL(2058),

    /**
     * SystemProductionAttributeGroup
     * Hex: 0x80B
     * Dec: 2059
     */
    NOM_ATTR_GRP_SYS_ID(2059),

    /**
     * VMODynamicAttributeGroup
     * Hex: 0x80C
     * Dec: 2060
     */
    NOM_ATTR_GRP_SYS_PROD(2060),

    /**
     * VMOStaticAttributeGroup
     * Hex: 0x810
     * Dec: 2064
     */
    NOM_ATTR_GRP_VMO_DYN(2064),

    /**
     * 0x0801
     * Hex: 0x811
     * Dec: 2065
     */
    NOM_ATTR_GRP_VMO_STATIC(2065),

    /**
     * DeviceT-AlarmList
     * Hex: 0x902
     * Dec: 2306
     */
    NOM_ATTR_AL_MON_P_AL_LIST(2306),

    /**
     * Altitude
     * Hex: 0x904
     * Dec: 2308
     */
    NOM_ATTR_AL_MON_T_AL_LIST(2308),

    /**
     * ApplicationArea
     * Hex: 0x90C
     * Dec: 2316
     */
    NOM_ATTR_ALTITUDE(2316),

    /**
     * Color
     * Hex: 0x90D
     * Dec: 2317
     */
    NOM_ATTR_AREA_APPL(2317),

    /**
     * DeviceAlertCondition
     * Hex: 0x911
     * Dec: 2321
     */
    NOM_ATTR_COLOR(2321),

    /**
     * DisplayResolution
     * Hex: 0x916
     * Dec: 2326
     */
    NOM_ATTR_DEV_AL_COND(2326),

    /**
     * VisualGrid
     * Hex: 0x917
     * Dec: 2327
     */
    NOM_ATTR_DISP_RES(2327),

    /**
     * AssociationInvokeId
     * Hex: 0x91A
     * Dec: 2330
     */
    NOM_ATTR_GRID_VIS_I16(2330),

    /**
     * BedLabel
     * Hex: 0x91D
     * Dec: 2333
     */
    NOM_ATTR_ID_ASSOC_NO(2333),

    /**
     * ObjectHandle
     * Hex: 0x91E
     * Dec: 2334
     */
    NOM_ATTR_ID_BED_LABEL(2334),

    /**
     * Label
     * Hex: 0x921
     * Dec: 2337
     */
    NOM_ATTR_ID_HANDLE(2337),

    /**
     * LabelString
     * Hex: 0x924
     * Dec: 2340
     */
    NOM_ATTR_ID_LABEL(2340),

    /**
     * AttributeIDs
     * Hex: 0x927
     * Dec: 2343
     */
    NOM_ATTR_ID_LABEL_STRING(2343),

    /**
     * ProductSpecification
     * Hex: 0x928
     * Dec: 2344
     */
    NOM_ATTR_ID_MODEL(2344),

    /**
     * ObjectType
     * Hex: 0x92D
     * Dec: 2349
     */
    NOM_ATTR_ID_PROD_SPECN(2349),

    /**
     * LineFrequency
     * Hex: 0x92F
     * Dec: 2351
     */
    NOM_ATTR_ID_TYPE(2351),

    /**
     * SystemLocalization
     * Hex: 0x935
     * Dec: 2357
     */
    NOM_ATTR_LINE_FREQ(2357),

    /**
     * MetricInfoLabel
     * Hex: 0x937
     * Dec: 2359
     */
    NOM_ATTR_LOCALIZN(2359),

    /**
     * MetricInfoLabelString
     * Hex: 0x93C
     * Dec: 2364
     */
    NOM_ATTR_METRIC_INFO_LABEL(2364),

    /**
     * MetricSpecification
     * Hex: 0x93D
     * Dec: 2365
     */
    NOM_ATTR_METRIC_INFO_LABEL_STR(2365),

    /**
     * MetricState
     * Hex: 0x93F
     * Dec: 2367
     */
    NOM_ATTR_METRIC_SPECN(2367),

    /**
     * MeasureMode
     * Hex: 0x940
     * Dec: 2368
     */
    NOM_ATTR_METRIC_STAT(2368),

    /**
     * OperatingMode
     * Hex: 0x945
     * Dec: 2373
     */
    NOM_ATTR_MODE_MSMT(2373),

    /**
     * NomenclatureVersion
     * Hex: 0x946
     * Dec: 2374
     */
    NOM_ATTR_MODE_OP(2374),

    /**
     * CompoundNumericObservedValue
     * Hex: 0x948
     * Dec: 2376
     */
    NOM_ATTR_NOM_VERS(2376),

    /**
     * NumericObservedValue
     * Hex: 0x94B
     * Dec: 2379
     */
    NOM_ATTR_NU_CMPD_VAL_OBS(2379),

    /**
     * PatientBSA
     * Hex: 0x950
     * Dec: 2384
     */
    NOM_ATTR_NU_VAL_OBS(2384),

    /**
     * PatDemoState
     * Hex: 0x956
     * Dec: 2390
     */
    NOM_ATTR_PT_BSA(2390),

    /**
     * PatientDateofBirth
     * Hex: 0x957
     * Dec: 2391
     */
    NOM_ATTR_PT_DEMOG_ST(2391),

    /**
     * PatientID
     * Hex: 0x958
     * Dec: 2392
     */
    NOM_ATTR_PT_DOB(2392),

    /**
     * FamilyName
     * Hex: 0x95A
     * Dec: 2394
     */
    NOM_ATTR_PT_ID(2394),

    /**
     * GivenName
     * Hex: 0x95C
     * Dec: 2396
     */
    NOM_ATTR_PT_NAME_FAMILY(2396),

    /**
     * PatientSex
     * Hex: 0x95D
     * Dec: 2397
     */
    NOM_ATTR_PT_NAME_GIVEN(2397),

    /**
     * PatientType
     * Hex: 0x961
     * Dec: 2401
     */
    NOM_ATTR_PT_SEX(2401),

    /**
     * SampleArrayCalibrationSpecification
     * Hex: 0x962
     * Dec: 2402
     */
    NOM_ATTR_PT_TYPE(2402),

    /**
     * CompoundSampleArrayObservedValue
     * Hex: 0x964
     * Dec: 2404
     */
    NOM_ATTR_SA_CALIB_I16(2404),

    /**
     * SampleArrayPhysiologicalRange
     * Hex: 0x967
     * Dec: 2407
     */
    NOM_ATTR_SA_CMPD_VAL_OBS(2407),

    /**
     * SampleArraySpecification
     * Hex: 0x96A
     * Dec: 2410
     */
    NOM_ATTR_SA_RANGE_PHYS_I16(2410),

    /**
     * SampleArrayObservedValue
     * Hex: 0x96D
     * Dec: 2413
     */
    NOM_ATTR_SA_SPECN(2413),

    /**
     * ScaleandRangeSpecification
     * Hex: 0x96E
     * Dec: 2414
     */
    NOM_ATTR_SA_VAL_OBS(2414),

    /**
     * SafetyStandard
     * Hex: 0x96F
     * Dec: 2415
     */
    NOM_ATTR_SCALE_SPECN_I16(2415),

    /**
     * SystemID
     * Hex: 0x982
     * Dec: 2434
     */
    NOM_ATTR_STD_SAFETY(2434),

    /**
     * SystemSpecification
     * Hex: 0x984
     * Dec: 2436
     */
    NOM_ATTR_SYS_ID(2436),

    /**
     * SystemType
     * Hex: 0x985
     * Dec: 2437
     */
    NOM_ATTR_SYS_SPECN(2437),

    /**
     * DateandTime
     * Hex: 0x986
     * Dec: 2438
     */
    NOM_ATTR_SYS_TYPE(2438),

    /**
     * SamplePeriod
     * Hex: 0x987
     * Dec: 2439
     */
    NOM_ATTR_TIME_ABS(2439),

    /**
     * RelativeTime
     * Hex: 0x98D
     * Dec: 2445
     */
    NOM_ATTR_TIME_PD_SAMP(2445),

    /**
     * AbsoluteTimeStamp
     * Hex: 0x98F
     * Dec: 2447
     */
    NOM_ATTR_TIME_REL(2447),

    /**
     * RelativeTimeStamp
     * Hex: 0x990
     * Dec: 2448
     */
    NOM_ATTR_TIME_STAMP_ABS(2448),

    /**
     * UnitCode
     * Hex: 0x991
     * Dec: 2449
     */
    NOM_ATTR_TIME_STAMP_REL(2449),

    /**
     * EnumerationObservedValue
     * Hex: 0x996
     * Dec: 2454
     */
    NOM_ATTR_UNIT_CODE(2454),

    /**
     * MDSStatus
     * Hex: 0x99E
     * Dec: 2462
     */
    NOM_ATTR_VAL_ENUM_OBS(2462),

    /**
     * PatientAge
     * Hex: 0x9A7
     * Dec: 2471
     */
    NOM_ATTR_VMS_MDS_STAT(2471),

    /**
     * PatientHeight
     * Hex: 0x9D8
     * Dec: 2520
     */
    NOM_ATTR_PT_AGE(2520),

    /**
     * PatientWeight
     * Hex: 0x9DC
     * Dec: 2524
     */
    NOM_ATTR_PT_HEIGHT(2524),

    /**
     * SampleArrayFixedValuesSpecification
     * Hex: 0x9DF
     * Dec: 2527
     */
    NOM_ATTR_PT_WEIGHT(2527),

    /**
     * PatientPacedMode
     * Hex: 0xA16
     * Dec: 2582
     */
    NOM_ATTR_SA_FIXED_VAL_SPECN(2582),

    /**
     * InternalPatientID
     * Hex: 0xA1E
     * Dec: 2590
     */
    NOM_ATTR_PT_PACED_MODE(2590),

    /**
     * PrivateAttribute
     * Hex: 0xF001
     * Dec: 61441
     */
    NOM_ATTR_PT_ID_INT(61441),

    /**
     * PrivateAttribute
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_SAT_O2_TONE_FREQ(61448),

    /**
     * IPAddressInformation
     * Hex: 0xF009
     * Dec: 61449
     */
    NOM_ATTR_CMPD_REF_LIST(61449),

    /**
     * ProtocolSupport
     * Hex: 0xF100
     * Dec: 61696
     */
    NOM_ATTR_NET_ADDR_INFO(61696),

    /**
     * Notes1
     * Hex: 0xF101
     * Dec: 61697
     */
    NOM_ATTR_PCOL_SUPPORT(61697),

    /**
     * Notes2
     * Hex: 0xF129
     * Dec: 61737
     */
    NOM_ATTR_PT_NOTES1(61737),

    /**
     * TimeforPeriodicPolling
     * Hex: 0xF12A
     * Dec: 61738
     */
    NOM_ATTR_PT_NOTES2(61738),

    /**
     * PatientBSAFormula
     * Hex: 0xF13E
     * Dec: 61758
     */
    NOM_ATTR_TIME_PD_POLL(61758),

    /**
     * MdsGeneralSystemInfo
     * Hex: 0xF1EC
     * Dec: 61932
     */
    NOM_ATTR_PT_BSA_FORMULA(61932),

    /**
     * noofprioritizedobjectsforpollrequest
     * Hex: 0xF1FA
     * Dec: 61946
     */
    NOM_ATTR_MDS_GEN_INFO(61946),

    /**
     * NumericObjectPriorityList
     * Hex: 0xF228
     * Dec: 61992
     */
    NOM_ATTR_POLL_OBJ_PRIO_NUM(61992),

    /**
     * WaveObjectPriorityList
     * Hex: 0xF239
     * Dec: 62009
     */
    NOM_ATTR_POLL_NU_PRIO_LIST(62009),

    /**
     * MetricModality
     * Hex: 0xF23A
     * Dec: 62010
     */
    NOM_ATTR_POLL_RTSA_PRIO_LIST(62010),

    /**
     * ComponentIDs
     * Hex: 0xF294
     * Dec: 62100
     */
    NOM_ATTR_METRIC_MODALITY(62100),

;
    
    private final int x;
    
    private AttributeId(int x) {
        this.x = x;
    }
    
    private final static Map<Integer, AttributeId> map = OrdinalEnum.buildInt(AttributeId.class);
    
    public int asInt() {
        return x;
    }
    public static final AttributeId valueOf(int s) {
        return map.get(s);
    }
    
    public OIDType asOid() {
    	return OIDType.lookup(asInt());
    }
}
