package org.mdpnp.devices.philips.intellivue.data;

public enum AttributeId {
    /**
     * MetricObservedValueGroup
     * Hex: 0x801
     * Dec: 2049
     */
    NOM_ATTR_GRP_AL_MON,

    /**
     * PatientDemographicsAttributeGroup
     * Hex: 0x803
     * Dec: 2051
     */
    NOM_ATTR_GRP_METRIC_VAL_OBS,

    /**
     * SystemApplicationAttributeGroup
     * Hex: 0x807
     * Dec: 2055
     */
    NOM_ATTR_GRP_PT_DEMOG,

    /**
     * SystemIdentificationAttributeGroup
     * Hex: 0x80A
     * Dec: 2058
     */
    NOM_ATTR_GRP_SYS_APPL,

    /**
     * SystemProductionAttributeGroup
     * Hex: 0x80B
     * Dec: 2059
     */
    NOM_ATTR_GRP_SYS_ID,

    /**
     * VMODynamicAttributeGroup
     * Hex: 0x80C
     * Dec: 2060
     */
    NOM_ATTR_GRP_SYS_PROD,

    /**
     * VMOStaticAttributeGroup
     * Hex: 0x810
     * Dec: 2064
     */
    NOM_ATTR_GRP_VMO_DYN,

    /**
     * 0x0801
     * Hex: 0x811
     * Dec: 2065
     */
    NOM_ATTR_GRP_VMO_STATIC,

    /**
     * DeviceT-AlarmList
     * Hex: 0x902
     * Dec: 2306
     */
    NOM_ATTR_AL_MON_P_AL_LIST,

    /**
     * Altitude
     * Hex: 0x904
     * Dec: 2308
     */
    NOM_ATTR_AL_MON_T_AL_LIST,

    /**
     * ApplicationArea
     * Hex: 0x90C
     * Dec: 2316
     */
    NOM_ATTR_ALTITUDE,

    /**
     * Color
     * Hex: 0x90D
     * Dec: 2317
     */
    NOM_ATTR_AREA_APPL,

    /**
     * DeviceAlertCondition
     * Hex: 0x911
     * Dec: 2321
     */
    NOM_ATTR_COLOR,

    /**
     * DisplayResolution
     * Hex: 0x916
     * Dec: 2326
     */
    NOM_ATTR_DEV_AL_COND,

    /**
     * VisualGrid
     * Hex: 0x917
     * Dec: 2327
     */
    NOM_ATTR_DISP_RES,

    /**
     * AssociationInvokeId
     * Hex: 0x91A
     * Dec: 2330
     */
    NOM_ATTR_GRID_VIS_I16,

    /**
     * BedLabel
     * Hex: 0x91D
     * Dec: 2333
     */
    NOM_ATTR_ID_ASSOC_NO,

    /**
     * ObjectHandle
     * Hex: 0x91E
     * Dec: 2334
     */
    NOM_ATTR_ID_BED_LABEL,

    /**
     * Label
     * Hex: 0x921
     * Dec: 2337
     */
    NOM_ATTR_ID_HANDLE,

    /**
     * LabelString
     * Hex: 0x924
     * Dec: 2340
     */
    NOM_ATTR_ID_LABEL,

    /**
     * AttributeIDs
     * Hex: 0x927
     * Dec: 2343
     */
    NOM_ATTR_ID_LABEL_STRING,

    /**
     * ProductSpecification
     * Hex: 0x928
     * Dec: 2344
     */
    NOM_ATTR_ID_MODEL,

    /**
     * ObjectType
     * Hex: 0x92D
     * Dec: 2349
     */
    NOM_ATTR_ID_PROD_SPECN,

    /**
     * LineFrequency
     * Hex: 0x92F
     * Dec: 2351
     */
    NOM_ATTR_ID_TYPE,

    /**
     * SystemLocalization
     * Hex: 0x935
     * Dec: 2357
     */
    NOM_ATTR_LINE_FREQ,

    /**
     * MetricInfoLabel
     * Hex: 0x937
     * Dec: 2359
     */
    NOM_ATTR_LOCALIZN,

    /**
     * MetricInfoLabelString
     * Hex: 0x93C
     * Dec: 2364
     */
    NOM_ATTR_METRIC_INFO_LABEL,

    /**
     * MetricSpecification
     * Hex: 0x93D
     * Dec: 2365
     */
    NOM_ATTR_METRIC_INFO_LABEL_STR,

    /**
     * MetricState
     * Hex: 0x93F
     * Dec: 2367
     */
    NOM_ATTR_METRIC_SPECN,

    /**
     * MeasureMode
     * Hex: 0x940
     * Dec: 2368
     */
    NOM_ATTR_METRIC_STAT,

    /**
     * OperatingMode
     * Hex: 0x945
     * Dec: 2373
     */
    NOM_ATTR_MODE_MSMT,

    /**
     * NomenclatureVersion
     * Hex: 0x946
     * Dec: 2374
     */
    NOM_ATTR_MODE_OP,

    /**
     * CompoundNumericObservedValue
     * Hex: 0x948
     * Dec: 2376
     */
    NOM_ATTR_NOM_VERS,

    /**
     * NumericObservedValue
     * Hex: 0x94B
     * Dec: 2379
     */
    NOM_ATTR_NU_CMPD_VAL_OBS,

    /**
     * PatientBSA
     * Hex: 0x950
     * Dec: 2384
     */
    NOM_ATTR_NU_VAL_OBS,

    /**
     * PatDemoState
     * Hex: 0x956
     * Dec: 2390
     */
    NOM_ATTR_PT_BSA,

    /**
     * PatientDateofBirth
     * Hex: 0x957
     * Dec: 2391
     */
    NOM_ATTR_PT_DEMOG_ST,

    /**
     * PatientID
     * Hex: 0x958
     * Dec: 2392
     */
    NOM_ATTR_PT_DOB,

    /**
     * FamilyName
     * Hex: 0x95A
     * Dec: 2394
     */
    NOM_ATTR_PT_ID,

    /**
     * GivenName
     * Hex: 0x95C
     * Dec: 2396
     */
    NOM_ATTR_PT_NAME_FAMILY,

    /**
     * PatientSex
     * Hex: 0x95D
     * Dec: 2397
     */
    NOM_ATTR_PT_NAME_GIVEN,

    /**
     * PatientType
     * Hex: 0x961
     * Dec: 2401
     */
    NOM_ATTR_PT_SEX,

    /**
     * SampleArrayCalibrationSpecification
     * Hex: 0x962
     * Dec: 2402
     */
    NOM_ATTR_PT_TYPE,

    /**
     * CompoundSampleArrayObservedValue
     * Hex: 0x964
     * Dec: 2404
     */
    NOM_ATTR_SA_CALIB_I16,

    /**
     * SampleArrayPhysiologicalRange
     * Hex: 0x967
     * Dec: 2407
     */
    NOM_ATTR_SA_CMPD_VAL_OBS,

    /**
     * SampleArraySpecification
     * Hex: 0x96A
     * Dec: 2410
     */
    NOM_ATTR_SA_RANGE_PHYS_I16,

    /**
     * SampleArrayObservedValue
     * Hex: 0x96D
     * Dec: 2413
     */
    NOM_ATTR_SA_SPECN,

    /**
     * ScaleandRangeSpecification
     * Hex: 0x96E
     * Dec: 2414
     */
    NOM_ATTR_SA_VAL_OBS,

    /**
     * SafetyStandard
     * Hex: 0x96F
     * Dec: 2415
     */
    NOM_ATTR_SCALE_SPECN_I16,

    /**
     * SystemID
     * Hex: 0x982
     * Dec: 2434
     */
    NOM_ATTR_STD_SAFETY,

    /**
     * SystemSpecification
     * Hex: 0x984
     * Dec: 2436
     */
    NOM_ATTR_SYS_ID,

    /**
     * SystemType
     * Hex: 0x985
     * Dec: 2437
     */
    NOM_ATTR_SYS_SPECN,

    /**
     * DateandTime
     * Hex: 0x986
     * Dec: 2438
     */
    NOM_ATTR_SYS_TYPE,

    /**
     * SamplePeriod
     * Hex: 0x987
     * Dec: 2439
     */
    NOM_ATTR_TIME_ABS,

    /**
     * RelativeTime
     * Hex: 0x98D
     * Dec: 2445
     */
    NOM_ATTR_TIME_PD_SAMP,

    /**
     * AbsoluteTimeStamp
     * Hex: 0x98F
     * Dec: 2447
     */
    NOM_ATTR_TIME_REL,

    /**
     * RelativeTimeStamp
     * Hex: 0x990
     * Dec: 2448
     */
    NOM_ATTR_TIME_STAMP_ABS,

    /**
     * UnitCode
     * Hex: 0x991
     * Dec: 2449
     */
    NOM_ATTR_TIME_STAMP_REL,

    /**
     * EnumerationObservedValue
     * Hex: 0x996
     * Dec: 2454
     */
    NOM_ATTR_UNIT_CODE,

    /**
     * MDSStatus
     * Hex: 0x99E
     * Dec: 2462
     */
    NOM_ATTR_VAL_ENUM_OBS,

    /**
     * PatientAge
     * Hex: 0x9A7
     * Dec: 2471
     */
    NOM_ATTR_VMS_MDS_STAT,

    /**
     * PatientHeight
     * Hex: 0x9D8
     * Dec: 2520
     */
    NOM_ATTR_PT_AGE,

    /**
     * PatientWeight
     * Hex: 0x9DC
     * Dec: 2524
     */
    NOM_ATTR_PT_HEIGHT,

    /**
     * SampleArrayFixedValuesSpecification
     * Hex: 0x9DF
     * Dec: 2527
     */
    NOM_ATTR_PT_WEIGHT,

    /**
     * PatientPacedMode
     * Hex: 0xA16
     * Dec: 2582
     */
    NOM_ATTR_SA_FIXED_VAL_SPECN,

    /**
     * InternalPatientID
     * Hex: 0xA1E
     * Dec: 2590
     */
    NOM_ATTR_PT_PACED_MODE,

    /**
     * PrivateAttribute
     * Hex: 0xF001
     * Dec: 61441
     */
    NOM_ATTR_PT_ID_INT,

    /**
     * PrivateAttribute
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_SAT_O2_TONE_FREQ,

    /**
     * IPAddressInformation
     * Hex: 0xF009
     * Dec: 61449
     */
    NOM_ATTR_CMPD_REF_LIST,

    /**
     * ProtocolSupport
     * Hex: 0xF100
     * Dec: 61696
     */
    NOM_ATTR_NET_ADDR_INFO,

    /**
     * Notes1
     * Hex: 0xF101
     * Dec: 61697
     */
    NOM_ATTR_PCOL_SUPPORT,

    /**
     * Notes2
     * Hex: 0xF129
     * Dec: 61737
     */
    NOM_ATTR_PT_NOTES1,

    /**
     * TimeforPeriodicPolling
     * Hex: 0xF12A
     * Dec: 61738
     */
    NOM_ATTR_PT_NOTES2,

    /**
     * PatientBSAFormula
     * Hex: 0xF13E
     * Dec: 61758
     */
    NOM_ATTR_TIME_PD_POLL,

    /**
     * MdsGeneralSystemInfo
     * Hex: 0xF1EC
     * Dec: 61932
     */
    NOM_ATTR_PT_BSA_FORMULA,

    /**
     * noofprioritizedobjectsforpollrequest
     * Hex: 0xF1FA
     * Dec: 61946
     */
    NOM_ATTR_MDS_GEN_INFO,

    /**
     * NumericObjectPriorityList
     * Hex: 0xF228
     * Dec: 61992
     */
    NOM_ATTR_POLL_OBJ_PRIO_NUM,

    /**
     * WaveObjectPriorityList
     * Hex: 0xF239
     * Dec: 62009
     */
    NOM_ATTR_POLL_NU_PRIO_LIST,

    /**
     * MetricModality
     * Hex: 0xF23A
     * Dec: 62010
     */
    NOM_ATTR_POLL_RTSA_PRIO_LIST,

    /**
     * ComponentIDs
     * Hex: 0xF294
     * Dec: 62100
     */
    NOM_ATTR_METRIC_MODALITY,

;
    public int asInt() {
        switch(this) {
        case NOM_ATTR_GRP_AL_MON:
            return 2049;
        case NOM_ATTR_GRP_METRIC_VAL_OBS:
            return 2051;
        case NOM_ATTR_GRP_PT_DEMOG:
            return 2055;
        case NOM_ATTR_GRP_SYS_APPL:
            return 2058;
        case NOM_ATTR_GRP_SYS_ID:
            return 2059;
        case NOM_ATTR_GRP_SYS_PROD:
            return 2060;
        case NOM_ATTR_GRP_VMO_DYN:
            return 2064;
        case NOM_ATTR_GRP_VMO_STATIC:
            return 2065;
        case NOM_ATTR_AL_MON_P_AL_LIST:
            return 2306;
        case NOM_ATTR_AL_MON_T_AL_LIST:
            return 2308;
        case NOM_ATTR_ALTITUDE:
            return 2316;
        case NOM_ATTR_AREA_APPL:
            return 2317;
        case NOM_ATTR_COLOR:
            return 2321;
        case NOM_ATTR_DEV_AL_COND:
            return 2326;
        case NOM_ATTR_DISP_RES:
            return 2327;
        case NOM_ATTR_GRID_VIS_I16:
            return 2330;
        case NOM_ATTR_ID_ASSOC_NO:
            return 2333;
        case NOM_ATTR_ID_BED_LABEL:
            return 2334;
        case NOM_ATTR_ID_HANDLE:
            return 2337;
        case NOM_ATTR_ID_LABEL:
            return 2340;
        case NOM_ATTR_ID_LABEL_STRING:
            return 2343;
        case NOM_ATTR_ID_MODEL:
            return 2344;
        case NOM_ATTR_ID_PROD_SPECN:
            return 2349;
        case NOM_ATTR_ID_TYPE:
            return 2351;
        case NOM_ATTR_LINE_FREQ:
            return 2357;
        case NOM_ATTR_LOCALIZN:
            return 2359;
        case NOM_ATTR_METRIC_INFO_LABEL:
            return 2364;
        case NOM_ATTR_METRIC_INFO_LABEL_STR:
            return 2365;
        case NOM_ATTR_METRIC_SPECN:
            return 2367;
        case NOM_ATTR_METRIC_STAT:
            return 2368;
        case NOM_ATTR_MODE_MSMT:
            return 2373;
        case NOM_ATTR_MODE_OP:
            return 2374;
        case NOM_ATTR_NOM_VERS:
            return 2376;
        case NOM_ATTR_NU_CMPD_VAL_OBS:
            return 2379;
        case NOM_ATTR_NU_VAL_OBS:
            return 2384;
        case NOM_ATTR_PT_BSA:
            return 2390;
        case NOM_ATTR_PT_DEMOG_ST:
            return 2391;
        case NOM_ATTR_PT_DOB:
            return 2392;
        case NOM_ATTR_PT_ID:
            return 2394;
        case NOM_ATTR_PT_NAME_FAMILY:
            return 2396;
        case NOM_ATTR_PT_NAME_GIVEN:
            return 2397;
        case NOM_ATTR_PT_SEX:
            return 2401;
        case NOM_ATTR_PT_TYPE:
            return 2402;
        case NOM_ATTR_SA_CALIB_I16:
            return 2404;
        case NOM_ATTR_SA_CMPD_VAL_OBS:
            return 2407;
        case NOM_ATTR_SA_RANGE_PHYS_I16:
            return 2410;
        case NOM_ATTR_SA_SPECN:
            return 2413;
        case NOM_ATTR_SA_VAL_OBS:
            return 2414;
        case NOM_ATTR_SCALE_SPECN_I16:
            return 2415;
        case NOM_ATTR_STD_SAFETY:
            return 2434;
        case NOM_ATTR_SYS_ID:
            return 2436;
        case NOM_ATTR_SYS_SPECN:
            return 2437;
        case NOM_ATTR_SYS_TYPE:
            return 2438;
        case NOM_ATTR_TIME_ABS:
            return 2439;
        case NOM_ATTR_TIME_PD_SAMP:
            return 2445;
        case NOM_ATTR_TIME_REL:
            return 2447;
        case NOM_ATTR_TIME_STAMP_ABS:
            return 2448;
        case NOM_ATTR_TIME_STAMP_REL:
            return 2449;
        case NOM_ATTR_UNIT_CODE:
            return 2454;
        case NOM_ATTR_VAL_ENUM_OBS:
            return 2462;
        case NOM_ATTR_VMS_MDS_STAT:
            return 2471;
        case NOM_ATTR_PT_AGE:
            return 2520;
        case NOM_ATTR_PT_HEIGHT:
            return 2524;
        case NOM_ATTR_PT_WEIGHT:
            return 2527;
        case NOM_ATTR_SA_FIXED_VAL_SPECN:
            return 2582;
        case NOM_ATTR_PT_PACED_MODE:
            return 2590;
        case NOM_ATTR_PT_ID_INT:
            return 61441;
        case NOM_SAT_O2_TONE_FREQ:
            return 61448;
        case NOM_ATTR_CMPD_REF_LIST:
            return 61449;
        case NOM_ATTR_NET_ADDR_INFO:
            return 61696;
        case NOM_ATTR_PCOL_SUPPORT:
            return 61697;
        case NOM_ATTR_PT_NOTES1:
            return 61737;
        case NOM_ATTR_PT_NOTES2:
            return 61738;
        case NOM_ATTR_TIME_PD_POLL:
            return 61758;
        case NOM_ATTR_PT_BSA_FORMULA:
            return 61932;
        case NOM_ATTR_MDS_GEN_INFO:
            return 61946;
        case NOM_ATTR_POLL_OBJ_PRIO_NUM:
            return 61992;
        case NOM_ATTR_POLL_NU_PRIO_LIST:
            return 62009;
        case NOM_ATTR_POLL_RTSA_PRIO_LIST:
            return 62010;
        case NOM_ATTR_METRIC_MODALITY:
            return 62100;
        default:
            throw new IllegalArgumentException("Unknown attributeid:"+this);
        }
    }
    public static final AttributeId valueOf(int s) {
        switch(s) {
        case 2049:
            return NOM_ATTR_GRP_AL_MON;
        case 2051:
            return NOM_ATTR_GRP_METRIC_VAL_OBS;
        case 2055:
            return NOM_ATTR_GRP_PT_DEMOG;
        case 2058:
            return NOM_ATTR_GRP_SYS_APPL;
        case 2059:
            return NOM_ATTR_GRP_SYS_ID;
        case 2060:
            return NOM_ATTR_GRP_SYS_PROD;
        case 2064:
            return NOM_ATTR_GRP_VMO_DYN;
        case 2065:
            return NOM_ATTR_GRP_VMO_STATIC;
        case 2306:
            return NOM_ATTR_AL_MON_P_AL_LIST;
        case 2308:
            return NOM_ATTR_AL_MON_T_AL_LIST;
        case 2316:
            return NOM_ATTR_ALTITUDE;
        case 2317:
            return NOM_ATTR_AREA_APPL;
        case 2321:
            return NOM_ATTR_COLOR;
        case 2326:
            return NOM_ATTR_DEV_AL_COND;
        case 2327:
            return NOM_ATTR_DISP_RES;
        case 2330:
            return NOM_ATTR_GRID_VIS_I16;
        case 2333:
            return NOM_ATTR_ID_ASSOC_NO;
        case 2334:
            return NOM_ATTR_ID_BED_LABEL;
        case 2337:
            return NOM_ATTR_ID_HANDLE;
        case 2340:
            return NOM_ATTR_ID_LABEL;
        case 2343:
            return NOM_ATTR_ID_LABEL_STRING;
        case 2344:
            return NOM_ATTR_ID_MODEL;
        case 2349:
            return NOM_ATTR_ID_PROD_SPECN;
        case 2351:
            return NOM_ATTR_ID_TYPE;
        case 2357:
            return NOM_ATTR_LINE_FREQ;
        case 2359:
            return NOM_ATTR_LOCALIZN;
        case 2364:
            return NOM_ATTR_METRIC_INFO_LABEL;
        case 2365:
            return NOM_ATTR_METRIC_INFO_LABEL_STR;
        case 2367:
            return NOM_ATTR_METRIC_SPECN;
        case 2368:
            return NOM_ATTR_METRIC_STAT;
        case 2373:
            return NOM_ATTR_MODE_MSMT;
        case 2374:
            return NOM_ATTR_MODE_OP;
        case 2376:
            return NOM_ATTR_NOM_VERS;
        case 2379:
            return NOM_ATTR_NU_CMPD_VAL_OBS;
        case 2384:
            return NOM_ATTR_NU_VAL_OBS;
        case 2390:
            return NOM_ATTR_PT_BSA;
        case 2391:
            return NOM_ATTR_PT_DEMOG_ST;
        case 2392:
            return NOM_ATTR_PT_DOB;
        case 2394:
            return NOM_ATTR_PT_ID;
        case 2396:
            return NOM_ATTR_PT_NAME_FAMILY;
        case 2397:
            return NOM_ATTR_PT_NAME_GIVEN;
        case 2401:
            return NOM_ATTR_PT_SEX;
        case 2402:
            return NOM_ATTR_PT_TYPE;
        case 2404:
            return NOM_ATTR_SA_CALIB_I16;
        case 2407:
            return NOM_ATTR_SA_CMPD_VAL_OBS;
        case 2410:
            return NOM_ATTR_SA_RANGE_PHYS_I16;
        case 2413:
            return NOM_ATTR_SA_SPECN;
        case 2414:
            return NOM_ATTR_SA_VAL_OBS;
        case 2415:
            return NOM_ATTR_SCALE_SPECN_I16;
        case 2434:
            return NOM_ATTR_STD_SAFETY;
        case 2436:
            return NOM_ATTR_SYS_ID;
        case 2437:
            return NOM_ATTR_SYS_SPECN;
        case 2438:
            return NOM_ATTR_SYS_TYPE;
        case 2439:
            return NOM_ATTR_TIME_ABS;
        case 2445:
            return NOM_ATTR_TIME_PD_SAMP;
        case 2447:
            return NOM_ATTR_TIME_REL;
        case 2448:
            return NOM_ATTR_TIME_STAMP_ABS;
        case 2449:
            return NOM_ATTR_TIME_STAMP_REL;
        case 2454:
            return NOM_ATTR_UNIT_CODE;
        case 2462:
            return NOM_ATTR_VAL_ENUM_OBS;
        case 2471:
            return NOM_ATTR_VMS_MDS_STAT;
        case 2520:
            return NOM_ATTR_PT_AGE;
        case 2524:
            return NOM_ATTR_PT_HEIGHT;
        case 2527:
            return NOM_ATTR_PT_WEIGHT;
        case 2582:
            return NOM_ATTR_SA_FIXED_VAL_SPECN;
        case 2590:
            return NOM_ATTR_PT_PACED_MODE;
        case 61441:
            return NOM_ATTR_PT_ID_INT;
        case 61448:
            return NOM_SAT_O2_TONE_FREQ;
        case 61449:
            return NOM_ATTR_CMPD_REF_LIST;
        case 61696:
            return NOM_ATTR_NET_ADDR_INFO;
        case 61697:
            return NOM_ATTR_PCOL_SUPPORT;
        case 61737:
            return NOM_ATTR_PT_NOTES1;
        case 61738:
            return NOM_ATTR_PT_NOTES2;
        case 61758:
            return NOM_ATTR_TIME_PD_POLL;
        case 61932:
            return NOM_ATTR_PT_BSA_FORMULA;
        case 61946:
            return NOM_ATTR_MDS_GEN_INFO;
        case 61992:
            return NOM_ATTR_POLL_OBJ_PRIO_NUM;
        case 62009:
            return NOM_ATTR_POLL_NU_PRIO_LIST;
        case 62010:
            return NOM_ATTR_POLL_RTSA_PRIO_LIST;
        case 62100:
            return NOM_ATTR_METRIC_MODALITY;
        default:
        	return null;
//            throw new IllegalArgumentException("Unknown attributeid:"+s);
        }
    }
    
    public OIDType asOid() {
    	return OIDType.lookup(asInt());
    }
}
