package org.mdpnp.devices.philips.intellivue.attribute;

import java.lang.reflect.InvocationTargetException;

import org.mdpnp.devices.philips.intellivue.data.AbsoluteTime;
import org.mdpnp.devices.philips.intellivue.data.Altitude;
import org.mdpnp.devices.philips.intellivue.data.ApplicationArea;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.ByteArray;
import org.mdpnp.devices.philips.intellivue.data.CompoundNumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.DisplayResolution;
import org.mdpnp.devices.philips.intellivue.data.EnumMessage;
import org.mdpnp.devices.philips.intellivue.data.EnumValue;
import org.mdpnp.devices.philips.intellivue.data.EnumValueImpl;
import org.mdpnp.devices.philips.intellivue.data.Handle;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.InvokeId;
import org.mdpnp.devices.philips.intellivue.data.LineFrequency;
import org.mdpnp.devices.philips.intellivue.data.MDSGeneralSystemInfo;
import org.mdpnp.devices.philips.intellivue.data.MDSStatus;
import org.mdpnp.devices.philips.intellivue.data.MdibObjectSupport;
import org.mdpnp.devices.philips.intellivue.data.MeasureMode;
import org.mdpnp.devices.philips.intellivue.data.MetricModality;
import org.mdpnp.devices.philips.intellivue.data.MetricSpecification;
import org.mdpnp.devices.philips.intellivue.data.MetricState;
import org.mdpnp.devices.philips.intellivue.data.NomenclatureVersion;
import org.mdpnp.devices.philips.intellivue.data.NumericObservedValue;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.OperatingMode;
import org.mdpnp.devices.philips.intellivue.data.PatientBSAFormula;
import org.mdpnp.devices.philips.intellivue.data.PatientDemographicState;
import org.mdpnp.devices.philips.intellivue.data.PatientMeasurement;
import org.mdpnp.devices.philips.intellivue.data.PatientPacedMode;
import org.mdpnp.devices.philips.intellivue.data.PatientSex;
import org.mdpnp.devices.philips.intellivue.data.PatientType;
import org.mdpnp.devices.philips.intellivue.data.PollProfileExtensions;
import org.mdpnp.devices.philips.intellivue.data.PollProfileSupport;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecification;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayCompoundObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayFixedValueSpecification;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayObservedValue;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayPhysiologicalRange;
import org.mdpnp.devices.philips.intellivue.data.SampleArraySpecification;
import org.mdpnp.devices.philips.intellivue.data.ScaleAndRangeSpecification;
import org.mdpnp.devices.philips.intellivue.data.SimpleColor;
import org.mdpnp.devices.philips.intellivue.data.SystemLocalization;
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.data.SystemSpecification;
import org.mdpnp.devices.philips.intellivue.data.TextId;
import org.mdpnp.devices.philips.intellivue.data.TextIdList;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.data.UnitCode;
import org.mdpnp.devices.philips.intellivue.data.Value;
import org.mdpnp.devices.philips.intellivue.data.VisualGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeFactory {
    public static final Attribute<PollProfileExtensions> getPollProfileExtensions() {
        return getAttribute(0xF001, PollProfileExtensions.class);
    }
    public static final Attribute<PollProfileSupport> getPollProfileSupport() {
        return getAttribute(0x0001, PollProfileSupport.class);
    }

    public static final Attribute<MdibObjectSupport> getMdibObjectSupport() {
        return getAttribute(0x102, MdibObjectSupport.class);
    }

	public static final <T extends Value> Attribute<T> getAttribute(int oid, Class<T> valueClass) {
        return getAttribute(OIDType.lookup(oid), valueClass);
    }
	
	public static final <T extends Value> Attribute<T> getAttribute(AttributeId aid, Class<T> valueClass) {
		return getAttribute(aid.asOid(), valueClass);
	}
	
	public static final <T extends Value> Attribute<T> getAttribute(OIDType oid, Class<T> valueClass) {
        try {
            return new AttributeImpl<T>(oid, valueClass.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static final <T extends EnumMessage<T>> Attribute<EnumValue<T>> getEnumAttribute(OIDType oid, Class<T> enumClass) {

        try {
            return new AttributeImpl<EnumValue<T>>(oid, new EnumValueImpl<T>((T) ((Object[])enumClass.getMethod("values", new Class<?>[0]).invoke(null))[0]));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }


    public static Class<?> valueType(OIDType oid) {
        AttributeId id = AttributeId.valueOf(oid.getType());
        if(null == id) {
            return null;
        } else {
            switch(id) {
            case NOM_ATTR_ID_TYPE:
                return Type.class;
            case NOM_ATTR_ID_HANDLE:
                return Handle.class;
            case NOM_ATTR_NU_VAL_OBS:
                return NumericObservedValue.class;
            case NOM_ATTR_NU_CMPD_VAL_OBS:
                return CompoundNumericObservedValue.class;
            case NOM_ATTR_TIME_STAMP_ABS:
                return AbsoluteTime.class;
            case NOM_ATTR_TIME_STAMP_REL:
                return RelativeTime.class;
            case NOM_ATTR_ID_LABEL:
                return TextId.class;
            case NOM_ATTR_DISP_RES:
                return DisplayResolution.class;
            case NOM_ATTR_COLOR:
                return SimpleColor.class;
            case NOM_ATTR_METRIC_SPECN:
                return MetricSpecification.class;
            case NOM_ATTR_METRIC_MODALITY:
                return MetricModality.class;
            case NOM_ATTR_SA_SPECN:
                return SampleArraySpecification.class;
            case NOM_ATTR_SA_FIXED_VAL_SPECN:
                return SampleArrayFixedValueSpecification.class;
            case NOM_ATTR_TIME_PD_SAMP:
                return RelativeTime.class;
            case NOM_ATTR_METRIC_STAT:
                return MetricState.class;
            case NOM_ATTR_UNIT_CODE:
                return UnitCode.class;
            case NOM_ATTR_MODE_MSMT:
                return MeasureMode.class;
            case NOM_ATTR_METRIC_INFO_LABEL_STR:
            case NOM_ATTR_ID_LABEL_STRING:
            case NOM_ATTR_ID_BED_LABEL:
            case NOM_ATTR_PT_NAME_GIVEN:
//				case NOM_ATTR_PT_NAME_MIDDLE:
            case NOM_ATTR_PT_NAME_FAMILY:
            case NOM_ATTR_PT_ID:
//				case NOM_ATTR_PT_ENCOUNTER_ID:
            case NOM_ATTR_PT_NOTES1:
            case NOM_ATTR_PT_NOTES2:
                return org.mdpnp.devices.philips.intellivue.data.String.class;
            case NOM_ATTR_SCALE_SPECN_I16:
                return ScaleAndRangeSpecification.class;
            case NOM_ATTR_SA_RANGE_PHYS_I16:
                return SampleArrayPhysiologicalRange.class;
            case NOM_ATTR_GRID_VIS_I16:
                return VisualGrid.class;
            case NOM_ATTR_SA_VAL_OBS:
                return SampleArrayObservedValue.class;
            case NOM_ATTR_SA_CMPD_VAL_OBS:
                return SampleArrayCompoundObservedValue.class;
            case NOM_ATTR_SYS_TYPE:
                return Type.class;
            case NOM_ATTR_PCOL_SUPPORT:
                return ProtocolSupport.class;
            case NOM_ATTR_LOCALIZN:
                return SystemLocalization.class;
            case NOM_ATTR_NET_ADDR_INFO:
                return IPAddressInformation.class;
            case NOM_ATTR_SYS_ID:
                return ByteArray.class;
            case NOM_ATTR_ID_ASSOC_NO:
                return InvokeId.class;
            case NOM_ATTR_ID_MODEL:
                return SystemModel.class;
            case NOM_ATTR_NOM_VERS:
                return NomenclatureVersion.class;
            case NOM_ATTR_MODE_OP:
                return OperatingMode.class;
            case NOM_ATTR_AREA_APPL:
                return ApplicationArea.class;
            case NOM_ATTR_LINE_FREQ:
                return LineFrequency.class;
            case NOM_ATTR_TIME_REL:
                return RelativeTime.class;
            case NOM_ATTR_TIME_ABS:
            case NOM_ATTR_PT_DOB:
                return AbsoluteTime.class;
            case NOM_ATTR_ALTITUDE:
                return Altitude.class;
            case NOM_ATTR_VMS_MDS_STAT:
                return MDSStatus.class;
            case NOM_ATTR_MDS_GEN_INFO:
                return MDSGeneralSystemInfo.class;
            case NOM_ATTR_ID_PROD_SPECN:
                return ProductionSpecification.class;
            case NOM_ATTR_TIME_PD_POLL:
                return RelativeTime.class;
            case NOM_ATTR_POLL_RTSA_PRIO_LIST:
            case NOM_ATTR_POLL_NU_PRIO_LIST:
            case NOM_ATTR_POLL_OBJ_PRIO_NUM:
                return TextIdList.class;
            case NOM_ATTR_PT_DEMOG_ST:
                return PatientDemographicState.class;
            case NOM_ATTR_PT_TYPE:
                return PatientType.class;
            case NOM_ATTR_PT_PACED_MODE:
                return PatientPacedMode.class;
            case NOM_ATTR_PT_SEX:
                return PatientSex.class;
            case NOM_ATTR_PT_WEIGHT:
            case NOM_ATTR_PT_AGE:
            case NOM_ATTR_PT_HEIGHT:
            case NOM_ATTR_PT_BSA:
                return PatientMeasurement.class;
            case NOM_ATTR_PT_BSA_FORMULA:
                return PatientBSAFormula.class;
            case NOM_ATTR_SYS_SPECN:
                return SystemSpecification.class;
            default:
                return null;
            }
        }

    }

    private static final Logger log = LoggerFactory.getLogger(AttributeFactory.class);

    @SuppressWarnings("unchecked")
    public static final <T extends EnumMessage<T>> Attribute<?> getAttribute(OIDType oid) {
		Class<?> valueType = valueType(oid);
		if(null == valueType) {
		    return null;
		} else if(valueType.isEnum()) {
			return getEnumAttribute(oid, (Class<T>)valueType);
		} else {
			return getAttribute(oid, ((Class<Value>)valueType(oid)));
		}
    }
}
