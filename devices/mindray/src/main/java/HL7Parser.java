/**
 * @file    HL7Parser.java
 * 
 * @brief   Parses HL7 messages. The contents are stored in DDS data types.
 * The user of this class can print and/or publish the data.
 *
 * @author M Szwaja
 */
//=============================================================================
import java.util.List;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.primitive.ID;
import ca.uhn.hl7v2.model.v26.datatype.CWE;
import ca.uhn.hl7v2.model.v26.datatype.CX;
import ca.uhn.hl7v2.model.v26.datatype.DTM;
import ca.uhn.hl7v2.model.v26.datatype.EI;
import ca.uhn.hl7v2.model.v26.datatype.IS;
import ca.uhn.hl7v2.model.v26.datatype.NM;
import ca.uhn.hl7v2.model.v26.datatype.PL;
import ca.uhn.hl7v2.model.v26.datatype.SN;
import ca.uhn.hl7v2.model.v26.datatype.XAD;
import ca.uhn.hl7v2.model.v26.datatype.XCN;
import ca.uhn.hl7v2.model.v26.datatype.XPN;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.OBR;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.model.v26.segment.PV1;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;

import ice.PatientDemographics;
import ice.Numeric;
import ice.PatientRace;
import ice.PatientSex;


/**
 * Parses HL7 messages and prints the results.
 */
public class HL7Parser
{
  private DDSImpl _rti_dds_impl;
  private String _statusmsg;
  private PatientDemographics _pat_demog;
  private Numeric _numeric;
  private static String mindray_udi = "Mindray A5 UDI";
  static final int kYearsPerCentury = 100;
  static final int kOBX5CodeOffset = 262144;
  static final int kOBX6CodeOffset = 262144;
  static final int kOBX3CodeOffset = 131072;


  public HL7Parser()
  {
    _rti_dds_impl = null;
    _pat_demog = new PatientDemographics();
    _numeric = new Numeric();
  }


  /**
   * Sets DDSImpl.
   * @param dds_impl DDSImpl to be set
   * @return Returns zero for success
   */
  public final int set_rti_dds_impl(final DDSImpl dds_impl)
  {
    if (dds_impl == null)
    {
      _statusmsg = "set_rti_dds_impl() dds_impl is null";
      return 1;
    }
    _rti_dds_impl = dds_impl;
 
    return 0;
  }


  public final String get_statusmsg()
  {
    return _statusmsg;
  }


  /**
   * Precondition: DDSDomainParticipant has alreay been created.
   * @return Return zero for success
   */
  public final int InitDDS()
  {
    if (_rti_dds_impl == null)
    {
      _statusmsg = "DDSImpl::InitDDS() _rti_dds_impl is null";
      return 1; 
    }

    if (_rti_dds_impl.CreateNumericWriter() != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    if (_rti_dds_impl.CreatePatientDemographicsWriter() != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    return 0;
  }
  

  /**
   * Parses and prints the contents of an HL7 message with the Message Code ORU
   * and the Trigger Event R01, otherwise known as an ORU^RO1 message.
   * @param hapiMsg
   * @throws HL7Exception
   * @return Returns zero for success
   */
  public final int ParseORURO1Message(final Message hapiMsg)
    throws HL7Exception
  {
    System.out.println("Parsing message...");

    // The message is an ORU^R01. It's an HL7 data type consisting of several
    // components, so we will cast it as such. The ORU_R01 class extends
    // from Message, providing specialized accessors for ORU^R01's segments.
    // HAPI provides several versions of the ORU_R01 class, each in a
    // different package (note the import statement above) corresponding to
    // the HL7 version for the message.
    ORU_R01 oru_msg = (ORU_R01) hapiMsg;

    // Get Message Header
    MSH msh = oru_msg.getMSH();

    ParseAndPrintMSH(msh);

    // Get all patient results. The patient result contains
    // segments for further parsing.
    List<ORU_R01_PATIENT_RESULT> pat_result_list =
      oru_msg.getPATIENT_RESULTAll();

    for (int ix = 0; ix < pat_result_list.size(); ix++)
    {
      System.out.println("---------------- PATIENT_RESULT " + ix + " Start "
        + "----------------");

      // Parse and print Patient Identification
      if (ParseAndPrintPid(pat_result_list.get(ix).getPATIENT().getPID()) != 0)
        return 1;

      // Parse and print Patient Visit Information 
      ParseAndPrintPV1(pat_result_list.get(ix).getPATIENT().getVISIT().
        getPV1());

      // Get all Observation Requests contained in this patient result
      List<ORU_R01_ORDER_OBSERVATION> obr_list = pat_result_list.get(ix).
        getORDER_OBSERVATIONAll();

      for (int obr_index = 0; obr_index < obr_list.size(); obr_index++)
      {
        ParseAndPrintOBR(obr_list.get(obr_index).getOBR());

        // Get all Observation Segments contained in each Observation Request.
        List<ORU_R01_OBSERVATION> obx_list = obr_list.get(obr_index).
          getOBSERVATIONAll();

        for (int obx_index = 0; obx_index < obx_list.size(); obx_index++)
          ParseAndPrintOBX(obx_list.get(obx_index).getOBX());
      }

      System.out.println("---------------- PATIENT_RESULT " + ix + " End "
       + "----------------");
    }

    return 0;
  }


  /**
   * Parses and prints the contents of an HL7 message with the Message Code ORU
   * and the Trigger Event R01, otherwise known as an ORU^RO1 message.
   * @param hapiMsg
   * @throws HL7Exception
   * @return Returns zero for success
   */
  public final int ParseORURO1MessageToDDS(final Message hapiMsg)
    throws HL7Exception
  {
    // The message is an ORU^R01. It's an HL7 data type consisting of several
    // components, so we will cast it as such. The ORU_R01 class extends
    // from Message, providing specialized accessors for ORU^R01's segments.
    // HAPI provides several versions of the ORU_R01 class, each in a
    // different package (note the import statement above) corresponding to
    // the HL7 version for the message.
    ORU_R01 oru_msg = (ORU_R01) hapiMsg;

    // Currently don't use values from MSH

    // Get all patient results. The patient result contains
    // segments for further parsing.
    List<ORU_R01_PATIENT_RESULT> pat_result_list =
      oru_msg.getPATIENT_RESULTAll();

    for (int ix = 0; ix < pat_result_list.size(); ix++)
    {
      // Parse and print Patient Identification
      if (ParseToDDSPid(pat_result_list.get(ix).getPATIENT().getPID()) != 0)
        return 1;

      // Parse and print Patient Visit Information 
      if (ParseToDDSPV1(pat_result_list.get(ix).getPATIENT().getVISIT().
        getPV1()) != 0) return 1;

      // Get all Observation Requests contained in this patient result
      List<ORU_R01_ORDER_OBSERVATION> obr_list = pat_result_list.get(ix).
        getORDER_OBSERVATIONAll();

      for (int obr_index = 0; obr_index < obr_list.size(); obr_index++)
      {
        // Get all Observation Segments contained in each Observation Request.
        List<ORU_R01_OBSERVATION> obx_list = obr_list.get(obr_index).
          getOBSERVATIONAll();

        for (int obx_index = 0; obx_index < obx_list.size(); obx_index++)
        {
          if (ParseToDDSOBX(obx_list.get(obx_index).getOBX()) != 0)
            return 1;
        }
      }
    }

    return 0;
  }


  /**
   * Retrieve data from Patient Identifier (PID) segment.
   * @param pid
   */
  private int ParseToDDSPid(final PID pid)
  {
    // PID-3
    CX[] pid3_pat_ident = pid.getPid3_PatientIdentifierList();

    // PID-5
    XPN[] pid5_pat_name = pid.getPid5_PatientName();

    // PID-7
    DTM pid7_birth_date = pid.getPid7_DateTimeOfBirth();

    // PID-8
    String pid8_sex = pid.getPid8_AdministrativeSex().getValue();

    // PID-10
    CWE[] pid10_race = pid.getPid10_Race();


    _pat_demog.unique_device_identifier = mindray_udi;

    if (pid3_pat_ident.length != 0)
    {
      for (int ix = 0; ix < pid3_pat_ident.length; ix++)
      {
        if (pid3_pat_ident[ix] == null) continue;

        if (pid3_pat_ident[ix].getCx1_IDNumber() != null)
          _pat_demog.patient_id = pid3_pat_ident[ix].
            getCx1_IDNumber().getValue();
      }
    }

    if (pid5_pat_name.length != 0)
    {
      for (int ix = 0; ix < pid5_pat_name.length; ix++)
      {
        if (pid5_pat_name[ix] == null) continue;

        if (pid5_pat_name[ix].getFamilyName().getFn1_Surname().
          getValue() != null)
            _pat_demog.family_name = pid5_pat_name[ix].
              getXpn1_FamilyName().getFn1_Surname().getValue();

        if (pid5_pat_name[ix].getXpn2_GivenName().getValue() != null)
          _pat_demog.given_name  = pid5_pat_name[ix].
            getXpn2_GivenName().getValue();

        if (pid5_pat_name[ix].getXpn2_GivenName().getValue() != null
          && pid5_pat_name[ix].getFamilyName().getFn1_Surname().getValue()
            != null)
          _pat_demog.name = pid5_pat_name[ix].getXpn2_GivenName().getValue()
            + " " + pid5_pat_name[ix].getFamilyName().getFn1_Surname().
              getValue();
      }
    }

    if (pid7_birth_date != null)
    {
      try
      {
        _pat_demog.date_of_birth.century =
          pid7_birth_date.getYear() / kYearsPerCentury;
        _pat_demog.date_of_birth.year =
          pid7_birth_date.getYear() % kYearsPerCentury;
        _pat_demog.date_of_birth.month = pid7_birth_date.getMonth();
        _pat_demog.date_of_birth.day = pid7_birth_date.getDay();
      }
      catch (DataTypeException exc)
      {
        _statusmsg = "ParseToDDSPid() Exception: " + exc.getMessage();
        return 1;
      }
    }

    if (pid8_sex != null)
    {
      if (pid8_sex.compareToIgnoreCase("male") == 0
        || pid8_sex.compareToIgnoreCase("m") == 0)
          _pat_demog.sex = PatientSex.male;
      else if (pid8_sex.compareToIgnoreCase("female") == 0
        || pid8_sex.compareToIgnoreCase("f") == 0)
          _pat_demog.sex = PatientSex.female;
      else
        _pat_demog.sex = PatientSex.sex_unknown;
    }
    else
    {
      _pat_demog.sex = PatientSex.sex_unspecified;
    }

    if (pid10_race.length != 0)
    {
      for (int ix = 0; ix < pid10_race.length; ix++)
      {
        if (pid10_race[ix].getText().getValue().
          compareToIgnoreCase("caucasian") == 0)
            _pat_demog.race = PatientRace.race_caucasian;
        else if (pid10_race[ix].getText().getValue().
          compareToIgnoreCase("black") == 0)
            _pat_demog.race = PatientRace.race_black;
        else
          _pat_demog.race = PatientRace.race_oriental;
      }
    }
    else
    {
      _pat_demog.race = PatientRace.race_unspecified;
    }

    return 0;
  }


  /**
   * Retrieve data from Patient Visit Information (PV1) segment.
   * @param pv1
   */
  private int ParseToDDSPV1(final PV1 pv1)
  {
    // PV1-13-3
    String pv1_13_3_bed = pv1.getPv13_AssignedPatientLocation().getPl3_Bed().
      getValue();

    if (pv1_13_3_bed != null)
      _pat_demog.bed_id = pv1_13_3_bed;

    if (_rti_dds_impl.WritePatientDemographics(_pat_demog) != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    return 0;
  }


  /**
   * Retrieve data from Observation (OBX) segment.
   * @param obx
   */
  private int ParseToDDSOBX(final OBX obx)
  {
    _numeric.unique_device_identifier = mindray_udi;

    // OBX-3
    CWE obx3_obs_id = obx.getObx3_ObservationIdentifier();
    String obx3_cwe3_code_system = null;

    // OBX-4
    String obx4_obs_sub_id = obx.getObx4_ObservationSubID().getValue();

    // OBX-5
    Varies[] obx5_obs_value = obx.getObx5_ObservationValue();

    // OBX-6
    String obx6_units = obx.getObx6_Units().getCwe1_Identifier().getValue();

    // OBX-14
    DTM obx14_obs_date_time = obx.getObx14_DateTimeOfTheObservation();

    // Begin parsing
    if (obx3_obs_id != null)
    {
      String obx3_cwe1 = obx3_obs_id.getCwe1_Identifier().getValue();
      obx3_cwe3_code_system = obx3_obs_id.getCwe3_NameOfCodingSystem().
        getValue();

      int term_code = 0;
      if (obx3_cwe1 != null)
      {
        try
        {
          term_code = Integer.parseInt(obx3_cwe1);
        }
        catch (NumberFormatException exc)
        {
          _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
          return 1;
        }

        if (obx3_cwe3_code_system != null)
        {
          if (obx3_cwe3_code_system.equals("MDC"))
          {
            int result_11073_code = 0;
            // MDC_EVT_STAT_DEV for some reason requires double offset
            if (term_code == 268422) // MDC_EVT_STAT_DEV
            {
              result_11073_code = term_code - (2 * kOBX3CodeOffset);
            }
            else
            {
              // Calculate 11073 value using term offset
              result_11073_code = term_code - kOBX3CodeOffset;
            }
            _numeric.name = result_11073_code;
          }
          else if (obx3_cwe3_code_system.equals("99MNDRY"))
          {
            // Keeping Mindray code as is.
            _numeric.name = term_code; 
          }
        }
      }
    }

    if (obx4_obs_sub_id != null)
    {
      int start_index = obx4_obs_sub_id.lastIndexOf('.');
      if (start_index < 0)
      {
        _statusmsg = "ParseToDDSOBX() Parsing error: OBX-4 SubID \".\" "
          + "doesn't exist in sub id";
        return 1;
      }

      // Skip over decimal point
      start_index++;

      int code = 0;
      try
      {
        String name_str = obx4_obs_sub_id.substring(start_index);
        code = Integer.parseInt(name_str);
      }
      catch (IndexOutOfBoundsException exc)
      {
        _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
        return 1;
      }
      catch (NumberFormatException exc)
      {
        _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
        return 1;
      }

      // The following is a repeat of the conversion in OBX-3. Parsing OBX-4 
      // is unnecessary for now.
      if (obx3_cwe3_code_system != null)
      {
        if (obx3_cwe3_code_system.equals("MDC"))
        {
          int result_11073_code = 0;
          // MDC_EVT_STAT_DEV for some reason requires double offset
          if (code == 268422) // MDC_EVT_STAT_DEV
          {
            result_11073_code = code - (2 * kOBX3CodeOffset);
          }
          else
          {
            // Calculate 11073 value using term offset
            result_11073_code = code - kOBX3CodeOffset;
          }
          _numeric.name = result_11073_code;
        }
        else if (obx3_cwe3_code_system.equals("99MNDRY"))
        {
          // Keeping Mindray code as is.
          _numeric.name = code; 
        }
      }
    }

    if (obx5_obs_value.length > 1)
    {
      _statusmsg = "ParseToDDSOBX() Parsing error: OBX-5 Observation Value "
        + "length (" + obx5_obs_value.length + ") has not been implemented.";
      return 1;
    }

    if (obx5_obs_value.length > 0) // length = 1
    {
      String data_type_name = obx5_obs_value[0].getData().getName().toString();
      if (data_type_name != null)
      {
        if (data_type_name.equals("NM"))
        {
          NM nm_data_type =  (NM) obx5_obs_value[0].getData();
            
          String numeric_val_str = nm_data_type.getValue().toString();
          if (numeric_val_str != null)
          {
            try
            {
              _numeric.value = Float.parseFloat(numeric_val_str);
            }
            catch (NumberFormatException exc)
            {
              _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
              return 1;
            }
          }
        }
        else if (data_type_name.equals("SN"))
        {
          SN sn_data_type =  (SN) obx5_obs_value[0].getData();
          String separator_str = sn_data_type.getSn3_SeparatorSuffix().
            toString();
          String numerator_str = sn_data_type.getNum1().getValue().toString();
          String denominator_str = sn_data_type.getNum2().getValue().toString();

          if (separator_str != null && numerator_str != null
            && denominator_str != null)
          {
            if (separator_str.equals(":")) // Looking for ratio
            {
              int num = 0;
              int denom = 0;
              try
              {
                num = Integer.parseInt(sn_data_type.getNum1().getValue().
                  toString());
                denom = Integer.parseInt(sn_data_type.getNum2().getValue().
                  toString());
              }
              catch (NumberFormatException exc)
              {
                _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
                return 1;
              }

              // Convert Ratio to float. This operation can be reversed by the
              // receiving application.
              _numeric.value = (float) num / (float) denom;
            }
            else
            {
              _statusmsg = "ParseToDDSOBX() Parsing error: OBX-5 Observation "
                + "Value name " + data_type_name + " with separator (" 
                + separator_str + ") has not been implemented.";
              return 1;
            }
          }
        }
        else if (data_type_name.equals("CWE"))
        {
          CWE cwe_data_type =  (CWE) obx5_obs_value[0].getData();
          if (cwe_data_type != null)
          {
            String cwe1 = cwe_data_type.getCwe1_Identifier().getValue();
            String cwe3 = cwe_data_type.getCwe3_NameOfCodingSystem().getValue();
            if (cwe1 != null)
            {
              int code = 0;
              try
              {
                code = Integer.parseInt(cwe1);
              }
              catch (NumberFormatException exc)
              {
                _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
                return 1;
              }
              
              if (cwe3.equals("MDC"))
              {
                // Convert code to 11073 code
                int result_11073_code = code - kOBX5CodeOffset;
                _numeric.value = result_11073_code;
              }
              else if (cwe3.equals("99MNDRY"))
              {
                // Keeping Mindray code as is
                _numeric.value = code;
              }
            }
          }
        }
      }
    }

    if (obx6_units != null)
    {
      int code_offset = 0;
      try
      {
        code_offset = Integer.parseInt(obx6_units);
      }
      catch (NumberFormatException exc)
      {
        _statusmsg = "ParseToDDSOBX() " + exc.getMessage();
        return 1;
      }

      // Convert to 11073 unit code
      int code = code_offset - kOBX6CodeOffset;
      _numeric.unit_code = code;
    }

    if (obx14_obs_date_time != null)
    {
      try
      {
        _numeric.absolute_time_stamp.century =
          obx14_obs_date_time.getYear() / kYearsPerCentury;
        _numeric.absolute_time_stamp.year =
          obx14_obs_date_time.getYear() % kYearsPerCentury;
        _numeric.absolute_time_stamp.month =
          obx14_obs_date_time.getMonth();
        _numeric.absolute_time_stamp.day =
          obx14_obs_date_time.getDay();
        _numeric.absolute_time_stamp.hour =
          obx14_obs_date_time.getHour();
        _numeric.absolute_time_stamp.minute =
          obx14_obs_date_time.getMinute();
        _numeric.absolute_time_stamp.second =
          obx14_obs_date_time.getSecond();
        _numeric.absolute_time_stamp.sec_fractions =
          (int) obx14_obs_date_time.getFractSecond();
      }
      catch (DataTypeException exc)
      {
        _statusmsg = "ParseToDDSOBX() Exception: " + exc.getMessage(); 
        return 1;
      }
    }

    if (_rti_dds_impl.WriteNumeric(_numeric) != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    return 0;
  }


  /**
   * Retrieve data from Message Header (MSH) segment.
   * @param msh
   */
  private void ParseAndPrintMSH(final MSH msh)
  {
    // MSH-1
    String msh1_field_separator = msh.getFieldSeparator().getValue();

    // MSH-2
    String msh2_encoding_chars = msh.getEncodingCharacters().getValue();

    // MSH-3
    String msh3_namespace_id = msh.getMsh3_SendingApplication().
      getHd1_NamespaceID().getValue();
    String msh3_universal_id = msh.getMsh3_SendingApplication().
      getHd2_UniversalID().getValue();
    String msh3_universal_id_type = msh.getMsh3_SendingApplication().
      getHd3_UniversalIDType().getValue();

    // MSH-4
    String msh4_namespace_id = msh.getMsh4_SendingFacility().
      getHd1_NamespaceID().getValue();
    String msh4_universal_id = msh.getMsh4_SendingFacility().
      getHd2_UniversalID().getValue();
    String msh4_universal_id_type = msh.getMsh4_SendingFacility().
      getHd3_UniversalIDType().getValue();

    // MSH-5
    String msh5_namespace_id = msh.getMsh5_ReceivingApplication().
      getHd1_NamespaceID().getValue();
    String msh5_universal_id = msh.getMsh5_ReceivingApplication().
      getHd2_UniversalID().getValue();
    String msh5_universal_id_type = msh.getMsh5_ReceivingApplication().
      getHd3_UniversalIDType().getValue();

    // MSH-6
    String msh6_namespace_id = msh.getMsh6_ReceivingFacility().
      getHd1_NamespaceID().getValue();
    String msh6_universal_id = msh.getMsh6_ReceivingFacility().
      getHd2_UniversalID().getValue();
    String msh6_universal_id_type = msh.getMsh6_ReceivingFacility().
      getHd3_UniversalIDType().getValue();

    // MSH-7
    String msh7_date_time_of_message = msh.getDateTimeOfMessage().getValue();

    // MSH-8
    String msh8_security = msh.getMsh8_Security().getValue();

    // MSH-9
    String msh9_msg_code = msh.getMsh9_MessageType().
      getMsg1_MessageCode().getValue();
    String msh9_trigger_event = msh.getMsh9_MessageType().
      getMsg2_TriggerEvent().getValue();
    String msh9_message_structure = msh.getMsh9_MessageType().
      getMsg3_MessageStructure().getValue();

    // MSH-10
    String msh10_msg_code = msh.getMsh10_MessageControlID().getValue();

    // MSH-11
    String msh11_processing_id = msh.getMsh11_ProcessingID().
      getPt1_ProcessingID().getValue();
    String msh11_processing_mode = msh.getMsh11_ProcessingID().
      getPt2_ProcessingMode().getValue();

    // MSH-12
    String msh12_version_id = msh.getMsh12_VersionID().getVid1_VersionID().
      getValue();

    // MSH-13
    String msh13_sequence_number = msh.getMsh13_SequenceNumber().getValue();

    // MSH-14
    String msh14_continuation_pointer = msh.getMsh14_ContinuationPointer().
      getValue();

    // MSH-15
    String msh15_accept_ack_type = msh.getMsh15_AcceptAcknowledgmentType().
      getValue();

    // MSH-16
    String msh16_app_ack_type = msh.getMsh16_ApplicationAcknowledgmentType().
      getValue();

    // MSH-17
    String msh17_country_code = msh.getMsh17_CountryCode().getValue();

    // MSH-18
    ID[] msh18_character_set = msh.getMsh18_CharacterSet();

    // MSH-19
    String msh19_code_sys_ver_id = msh.getMsh19_PrincipalLanguageOfMessage().
      getCodingSystemVersionID().getValue();

    // MSH-20
    String msh20_alt_char_set_hanldling_scheme = 
      msh.getMsh20_AlternateCharacterSetHandlingScheme().getValue();

    // MSH-21
    EI[] msh21_msg_profile_id = msh.getMsh21_MessageProfileIdentifier();

    // Print results
    if (msh1_field_separator != null)
      System.out.println("MSH-1 (Field Separator): " + msh1_field_separator);

    if (msh2_encoding_chars != null)
      System.out.println("MSH-2 (Encoded Characters): " + msh2_encoding_chars);

    if (msh3_namespace_id != null
      || msh3_universal_id != null
      || msh3_universal_id_type != null)
    {
      System.out.println("MSH-3 (Sending Application)");

      if (msh3_namespace_id != null)
      System.out.println("\tNamespace ID: " + msh3_namespace_id);

      if (msh3_universal_id != null)
        System.out.println("\tUniversal ID: " + msh3_universal_id);

      if (msh3_universal_id_type != null)
      System.out.println("\tUniversal ID Type: " + msh3_universal_id_type);
    }

    if (msh4_namespace_id != null
      || msh4_universal_id != null
      || msh4_universal_id_type != null)
    {
      System.out.println("MSH-4 (Sending Facility)");

      if (msh4_namespace_id != null)
        System.out.println("\tNamespace ID: " + msh4_namespace_id);

      if (msh4_universal_id != null)
        System.out.println("\tUniversal ID: " + msh4_universal_id);

      if (msh4_universal_id_type != null) 
        System.out.println("\tUniversal ID Type: " + msh4_universal_id_type);
    }

    if (msh5_namespace_id != null
      || msh5_universal_id != null
      || msh5_universal_id_type != null)
    {
      System.out.println("MSH-5 (Receiving Application)");

      if (msh5_namespace_id != null)
      System.out.println("\tNamespace ID: " + msh5_namespace_id);

      if (msh5_universal_id != null)
      System.out.println("\tUniversal ID: " + msh5_universal_id);

      if (msh5_universal_id_type != null)
      System.out.println("\tUniversal ID Type: " + msh5_universal_id_type);
    }

    if (msh6_namespace_id != null
      || msh6_universal_id != null
      || msh6_universal_id_type != null)
    {
      System.out.println("MSH-6 (Receiving Facility)");

      if (msh6_namespace_id != null)
        System.out.println("\tNamespace ID: " + msh6_namespace_id);

      if (msh6_universal_id != null)
        System.out.println("\tUniversal ID: " + msh6_universal_id);

      if (msh6_universal_id_type != null)
        System.out.println("\tUniversal ID Type: " + msh6_universal_id_type);
    }

    if (msh7_date_time_of_message != null)
      System.out.println("MSH-7 (Date/Time of Message): "
        + msh7_date_time_of_message);

    if (msh8_security != null)
      System.out.println("MSH-8 (Security): " + msh8_security);

    if (msh9_msg_code != null
      || msh9_trigger_event != null
      || msh9_message_structure != null)
    {
      System.out.println("MSH-9 (Message Type)");

      if (msh9_msg_code != null)
        System.out.println("\tMessage Code: " + msh9_msg_code);

      if (msh9_trigger_event != null)
        System.out.println("\tTrigger Event: " + msh9_trigger_event);

      if (msh9_message_structure != null)
        System.out.println("\tMessage Structure: " + msh9_message_structure);
    }

    if (msh8_security != null)
      System.out.println("MSH-10 (Message Code): " + msh10_msg_code);

    if (msh11_processing_id != null
      || msh11_processing_mode != null)
    {
      System.out.println("MSH-11 (Processing ID)");

      if (msh11_processing_id != null)
        System.out.println("\tProcessing ID: " + msh11_processing_id);

      if (msh11_processing_mode != null)
        System.out.println("\tProcessing Mode: " + msh11_processing_mode);
    }

    if (msh12_version_id != null)
      System.out.println("MSH-12 (Version ID): " + msh12_version_id);

    if (msh13_sequence_number != null)
      System.out.println("MSH-13 (Sequence Number): " + msh13_sequence_number);

    if (msh14_continuation_pointer != null)
      System.out.println("MSH-14 (Continuation Pointer): "
        + msh14_continuation_pointer);

    if (msh15_accept_ack_type != null)
      System.out.println("MSH-15 (Accept Acknowledgment Type): "
        + msh15_accept_ack_type);

    if (msh16_app_ack_type != null)
      System.out.println("MSH-16 (Application Acknowledgment Type): "
        + msh16_app_ack_type);

    if (msh17_country_code != null)
      System.out.println("MSH-17 (Country Code): " + msh17_country_code);

    if (msh18_character_set.length != 0)
    {
      System.out.print("MSH-18 (Character Set): ");
      for (int ix = 0; ix < msh18_character_set.length; ix++)
        System.out.print(msh18_character_set[ix].getValue());
      System.out.println();
    }

    if (msh19_code_sys_ver_id != null)
      System.out.println("MSH-19 (Principle Language of System): "
        + msh19_code_sys_ver_id);

    if (msh20_alt_char_set_hanldling_scheme != null)
      System.out.print("MSH-20 (Alternate Character Set Handling Scheme): "
        + msh20_alt_char_set_hanldling_scheme);

    if (msh21_msg_profile_id.length != 0)
    {
      System.out.println("MSH-21 (Message Profile Identifier)");
      for (int ix = 0; ix < msh21_msg_profile_id.length; ix++)
      {
        if (msh21_msg_profile_id[ix].getEi1_EntityIdentifier().getValue()
          != null)
            System.out.println("\tEntity Identifier: "
              + msh21_msg_profile_id[ix].getEi1_EntityIdentifier().getValue());

        if (msh21_msg_profile_id[ix].getEi2_NamespaceID().getValue()
          != null)
            System.out.println("\tNamespace ID: " + msh21_msg_profile_id[ix].
              getEi2_NamespaceID().getValue());

        if (msh21_msg_profile_id[ix].getEi3_UniversalID().getValue()
          != null)
            System.out.println("\tUniversal ID: " + msh21_msg_profile_id[ix].
              getEi3_UniversalID().getValue());

        if (msh21_msg_profile_id[ix].getEi4_UniversalIDType().getValue()
          != null)
            System.out.println("\tUniversal ID Type: "
              + msh21_msg_profile_id[ix].getEi4_UniversalIDType().getValue());
      }

      System.out.println();
    }
  }


  /**
   * Retrieve data from Patient Identifier (PID) segment.
   * @param pid
   */
  private int ParseAndPrintPid(final PID pid)
  {
    // PID-3
    CX[] pid3_pat_ident = pid.getPid3_PatientIdentifierList();

    // PID-5
    XPN[] pid5_pat_name = pid.getPid5_PatientName();

    // PID-7
    DTM pid7_birth_date = pid.getPid7_DateTimeOfBirth();

    // PID-8
    String pid8_sex = pid.getPid8_AdministrativeSex().getValue();

    // PID-10
    CWE[] pid10_race = pid.getPid10_Race();

    // PID-11
    XAD[] pid11_pat_addr = pid.getPid11_PatientAddress();

    if (pid3_pat_ident.length != 0)
    {
      System.out.println("PID-3 (Patient Identifier List)");
      for (int ix = 0; ix < pid3_pat_ident.length; ix++)
      {
        if (pid3_pat_ident[ix] == null) continue;

        if (pid3_pat_ident[ix].getCx1_IDNumber() != null)
          System.out.println("\tID Number: " + pid3_pat_ident[ix].
            getCx1_IDNumber().getValue());

        if (pid3_pat_ident[ix].getCx4_AssigningAuthority().
          getHd1_NamespaceID().getValue() != null)
            System.out.println("\tAssigning Authority: "
              + pid3_pat_ident[ix].getCx4_AssigningAuthority().
                getHd1_NamespaceID().getValue());

        if (pid3_pat_ident[ix].getCx5_IdentifierTypeCode().
          getValue() != null)
            System.out.println("\tIdentifier Type Code: "
              + pid3_pat_ident[ix].getCx5_IdentifierTypeCode().
                getValue());

        if (pid3_pat_ident[ix].getCx6_AssigningFacility().
          getHd3_UniversalIDType().getValue() != null)
            System.out.println("\tAssigning Facility: "
              + pid3_pat_ident[ix].getCx6_AssigningFacility().
                getHd3_UniversalIDType().getValue());
      }
    }

    if (pid5_pat_name.length != 0)
    {
      System.out.println("PID-5 (Patient Name)");
      for (int ix = 0; ix < pid5_pat_name.length; ix++)
      {
        if (pid5_pat_name[ix] == null) continue;

        if (pid5_pat_name[ix].getFamilyName().getFn1_Surname() != null)
          System.out.println("\tFamily Name: " + pid5_pat_name[ix].
            getXpn1_FamilyName().getFn1_Surname());

        if (pid5_pat_name[ix].getXpn2_GivenName().getValue() != null)
          System.out.println("\tGiven Name: " + pid5_pat_name[ix].
            getXpn2_GivenName().getValue());

        if (pid5_pat_name[ix].getXpn7_NameTypeCode().getValue() != null)
          System.out.println("\tName Type Code: " + pid5_pat_name[ix].
            getXpn7_NameTypeCode().getValue());
      }
    }

    if (pid7_birth_date != null)
    {
      System.out.println("PID-7 (Birth Date/Time)");

      try
      {
        System.out.println("\tCentury: " + (pid7_birth_date.getYear()
          / kYearsPerCentury)); 
        System.out.println("\tYear: " + (pid7_birth_date.getYear()
          % kYearsPerCentury));
        System.out.println("\tMonth: " + pid7_birth_date.getMonth());
        System.out.println("\tDay: " + pid7_birth_date.getDay());
      }
      catch (DataTypeException exc)
      {
        _statusmsg = "ParseAndPrintPid() Exception: " + exc.getMessage();
        return 1;
      }
    }

    if (pid8_sex != null)
      System.out.println("PID-8 (Sex):" + pid8_sex);

    if (pid10_race.length != 0)
        System.out.println("PID-10 (Race):" + pid10_race);

    if (pid11_pat_addr.length != 0)
    {
      System.out.println("PID-11 (Patient Address)");
      for (int ix = 0; ix < pid11_pat_addr.length; ix++)
      {
        if (pid11_pat_addr[ix] == null) continue;

        if (pid11_pat_addr[ix].getXad1_StreetAddress().
          getSad1_StreetOrMailingAddress().getValue() != null)
            System.out.println("\tStreet Address: " + pid11_pat_addr[ix].
              getXad1_StreetAddress().getSad1_StreetOrMailingAddress().
                getValue());

        if (pid11_pat_addr[ix].getXad3_City().getValue() != null)
          System.out.println("\tCity: " + pid11_pat_addr[ix].
            getXad3_City().getValue());

        if (pid11_pat_addr[ix].getXad4_StateOrProvince().getValue() != null)
          System.out.println("\tState of Province: " + pid11_pat_addr[ix].
            getXad4_StateOrProvince().getValue());

        if (pid11_pat_addr[ix].getXad5_ZipOrPostalCode().getValue() != null)
          System.out.println("\tZip Code: " + pid11_pat_addr[ix].
            getXad5_ZipOrPostalCode().getValue());

        if (pid11_pat_addr[ix].getXad7_AddressType().getValue() != null)
          System.out.println("\tAddress Type: " + pid11_pat_addr[ix].
            getXad7_AddressType().getValue());
      }
    }

    System.out.println();
    return 0;
  }


  /**
   * Retrieve data from Patient Visit Information (PV1) segment.
   * @param pv1
   */
  private void ParseAndPrintPV1(final PV1 pv1)
  {
    // PV1-2
    String pv12 = pv1.getPv12_PatientClass().getValue();

    // PV1-3
    PL pv13 = pv1.getPv13_AssignedPatientLocation();

    if (pv12 != null)
      System.out.println("PV1-2 (Patient Class): " + pv12);

    if (pv13 != null)
    {
      System.out.println("PV1-3 (Assigned Point Location)");

      String pv13_pl1 = pv13.getPl1_PointOfCare().getValue();
      String pv13_pl2 = pv13.getPl2_Room().getValue();
      String pv13_pl3 = pv13.getPl3_Bed().getValue();
      String pv13_pl4 = pv13.getPl4_Facility().getHd1_NamespaceID().getValue();
      String pv13_pl6 = pv13.getPl6_PersonLocationType().getValue();

      if (pv13_pl1 != null)
        System.out.println("\tPoint of Care: " + pv13_pl1);

      if (pv13_pl2 != null)
        System.out.println("\tRoom: " + pv13_pl2);

      if (pv13_pl3 != null)
        System.out.println("\tBed: " + pv13_pl3);

      if (pv13_pl4 != null)
        System.out.println("\tFacility: " + pv13_pl4);

      if (pv13_pl6 != null)
          System.out.println("\tPersonal Location Type: " + pv13_pl6);
    }

    System.out.println();
  }


  /**
   * Retrieve data from Observation Request (OBR) segment.
   * @param obr
   */
  private void ParseAndPrintOBR(final OBR obr)
  {
    // OBR-1
    String obr1 = obr.getObr1_SetIDOBR().getValue();

    // OBR-2
    EI obr2 = obr.getObr2_PlacerOrderNumber();
    
    // OBR-3
    EI obr3 = obr.getObr3_FillerOrderNumber();
    
    // OBR-4
    CWE obr4 = obr.getObr4_UniversalServiceIdentifier();
    
    // OBR-7
    String obr7 = obr.getObr7_ObservationDateTime().getValue();

    if (obr1 != null)
      System.out.println("OBR-1 (Set ID OBR): " + obr1);

    if (obr2 != null)
    {
      System.out.println("OBR-2 (Place Order Number)");

      String obr2_ei1 = obr2.getEi1_EntityIdentifier().getValue();
      String obr2_ei2 = obr2.getEi2_NamespaceID().getValue();
      String obr2_ei3 = obr2.getEi3_UniversalID().getValue();
      String obr2_ei4 = obr2.getEi4_UniversalIDType().getValue();
  
      if (obr2_ei1 != null)
        System.out.println("\tEntity Identifier: " + obr2_ei1);

      if (obr2_ei2 != null)
        System.out.println("\tNamespace ID: " + obr2_ei2);

      if (obr2_ei3 != null)
        System.out.println("\tUniversal ID: " + obr2_ei3);

      if (obr2_ei4 != null)
        System.out.println("\tUniversal ID Type: " + obr2_ei4);
    }

    if (obr3 != null)
    {
      System.out.println("OBR-3 (Filter Order Number)");

      String obr3_ei1 = obr3.getEi1_EntityIdentifier().getValue();
      String obr3_ei2 = obr3.getEi2_NamespaceID().getValue();
      String obr3_ei3 = obr3.getEi3_UniversalID().getValue();
      String obr3_ei4 = obr3.getEi4_UniversalIDType().getValue();

      if (obr3_ei1 != null)
        System.out.println("\tEntity Identifier: " + obr3_ei1);

      if (obr3_ei2 != null)
        System.out.println("\tNamespace ID: " + obr3_ei2);

      if (obr3_ei3 != null)
        System.out.println("\tUniversal ID: " + obr3_ei3);

      if (obr3_ei4 != null)
        System.out.println("\tUniversal ID Type: " + obr3_ei4);
    }

    if (obr4 != null)
    {
      System.out.println("OBR-4 (Universal Service Identifier)");

      String obr4_cwe1 = obr4.getCwe1_Identifier().getValue();
      String obr4_cwe2 = obr4.getCwe2_Text().getValue();
      String obr4_cwe3 = obr4.getCwe3_NameOfCodingSystem().getValue();
      
      if (obr4_cwe1 != null)
        System.out.println("\tIdentifier: " + obr4_cwe1);

      if (obr4_cwe2 != null)
        System.out.println("\tText: " + obr4_cwe2);

      if (obr4_cwe3 != null)
        System.out.println("\tName of Coding System: " + obr4_cwe3);
    }

    if (obr7 != null)
      System.out.println("OBR-7 (Observation Date/Time): " + obr7);

    System.out.println();
  }


  /**
   * Retrieve data from Observation (OBX) segment.
   * @param obx
   */
  private void ParseAndPrintOBX(final OBX obx)
  {
    // OBX-1
    String obx1_set_id = obx.getObx1_SetIDOBX().getValue();

    // OBX-2
    String obx2_val_type = obx.getObx2_ValueType().getValue();

    // OBX-3
    CWE obx3_obs_identifier = obx.getObx3_ObservationIdentifier();

    // OBX-4
    String obx4_obs_sub_id = obx.getObx4_ObservationSubID().getValue();

    // OBX-5
    Varies[] obx5_obs_value = obx.getObx5_ObservationValue();

    // OBX-6
    CWE obx6_units = obx.getObx6_Units();

    // OBX-7
    String obx7_ref_range = obx.getObx7_ReferencesRange().getValue();

    // OBX-8
    IS[] obx8_abnormal_flags = obx.getObx8_AbnormalFlags();

    // OBX-11
    String obx11_obs_result_status =
      obx.getObx11_ObservationResultStatus().getValue();

    // OBX-14
    String obx14_date_time = obx.getObx14_DateTimeOfTheObservation().getValue();

    // OBX-16
    XCN[] obx16_responsible_obsvr = obx.getObx16_ResponsibleObserver();

    // OBX-17
    CWE[] obx17_obs_method = obx.getObx17_ObservationMethod();

    // OBX-18
    EI[] obx18_equip_inst_id = obx.getObx18_EquipmentInstanceIdentifier();

    // OBX-19
    String obx19_date_time = obx.getObx19_DateTimeOfTheAnalysis().getValue();

    // OBX-20
    CWE[] obx20_obs_site = obx.getObx20_ObservationSite();

    // Print OBX fields
    if (obx1_set_id != null)
      System.out.println("OBX-1 (Set ID-OBX): " + obx1_set_id);

    if (obx2_val_type != null)
        System.out.println("OBX-2 (Value Type): " + obx2_val_type);

    if (obx3_obs_identifier != null)
    {
      System.out.println("OBX-3 (Observation Identifier)");

      String obx3_cwe1 = obx3_obs_identifier.getCwe1_Identifier().getValue();
      String obx3_cwe2 = obx3_obs_identifier.getCwe2_Text().getValue();
      String obx3_cwe3 = obx3_obs_identifier.getCwe3_NameOfCodingSystem().
        getValue();
      String obx3_cwe4 = obx3_obs_identifier.getCwe4_AlternateIdentifier().
        getValue();
      String obx3_cwe5 = obx3_obs_identifier.getCwe5_AlternateText().getValue();
      String obx3_cwe6 = obx3_obs_identifier.
        getCwe6_NameOfAlternateCodingSystem().getValue();

      if (obx3_cwe1 != null)
        System.out.println("\tIdentifier: " + obx3_cwe1);

      if (obx3_cwe2 != null)
        System.out.println("\tText: " + obx3_cwe2);

      if (obx3_cwe3 != null)
        System.out.println("\tName of Coding System: " + obx3_cwe3);

      if (obx3_cwe4 != null)
        System.out.println("\tAlternate Identifier: " + obx3_cwe4);

      if (obx3_cwe5 != null)
        System.out.println("\tAlternate Text: " + obx3_cwe5);

      if (obx3_cwe6 != null)
        System.out.println("\tName of Coding System: " + obx3_cwe6);
    }

    if (obx4_obs_sub_id != null)
      System.out.println("OBX-4 (Obersvation Sub-ID): " + obx4_obs_sub_id);

    if (obx5_obs_value.length > 0)
    {
      System.out.print("OBX-5 (Observation Value): ");

      for (int ix = 0; ix < obx5_obs_value.length; ix++)
        System.out.println(obx5_obs_value[ix].getData());
    }

    if (obx6_units != null)
    {
      System.out.println("OBX-6 (Units)");

      String obx6_cwe1 = obx6_units.getCwe1_Identifier().getValue();
      String obx6_cwe2 = obx6_units.getCwe2_Text().getValue();
      String obx6_cwe3 = obx6_units.getCwe3_NameOfCodingSystem().getValue();
              
      if (obx6_cwe1 != null)
        System.out.println("\tIdentifier: " + obx6_cwe1);

      if (obx6_cwe2 != null)
        System.out.println("\tText: " + obx6_cwe2);

      if (obx6_cwe3 != null)
        System.out.println("\tName of Coding System: " + obx6_cwe3);
    }

    if (obx7_ref_range != null)
      System.out.println("OBX-7 (Reference Range): " + obx7_ref_range);

    if (obx8_abnormal_flags.length > 0)
    {
      System.out.print("OBX-8 (Abnormal Flags): ");
      for (int ix = 0; ix < obx8_abnormal_flags.length; ix++)
      {
        if (obx8_abnormal_flags[ix].getValue() != null)
        System.out.print(obx8_abnormal_flags[ix].getValue() + " ");
      }
      System.out.println();
    }

    if (obx11_obs_result_status != null)
      System.out.println("OBX-11 (Observation Result Status): "
        + obx11_obs_result_status);

    if (obx14_date_time != null)
      System.out.println("OBX-14 (Date/Time of the Observation): "
        + obx14_date_time);

    if (obx16_responsible_obsvr.length > 0)
    {
      System.out.println("OBX-16 (Responsible Observer)");

      for (int ix = 0; ix < obx16_responsible_obsvr.length; ix++)
      {
        String obx16_xcn1 = obx16_responsible_obsvr[ix].getXcn1_IDNumber().
          getValue();
        String obx16_xcn2 = obx16_responsible_obsvr[ix].getXcn2_FamilyName().
          getFn1_Surname().getValue();
        String obx16_xcn3 = obx16_responsible_obsvr[ix].getXcn3_GivenName().
          getValue();

        if (obx16_xcn1 != null)
          System.out.println("\tID Number: " + obx16_xcn1);

        if (obx16_xcn2 != null)
          System.out.println("\tFamily Name: " + obx16_xcn2);

        if (obx16_xcn3 != null)
          System.out.println("\tGiven Name: " + obx16_xcn3);
      }
    }

    if (obx17_obs_method.length > 0)
    {
      System.out.println("OBX-17 (Observation Method)");
        System.out.println("\tObservation Method: "
          + obx17_obs_method.toString());
    }

    if (obx18_equip_inst_id.length > 0)
    {
      System.out.println("OBX-18 (Equipment Instance Identifier)");

      for (int ix = 0; ix < obx18_equip_inst_id.length; ix++)
      {
        String obx18_ei1 = obx18_equip_inst_id[ix].getEi1_EntityIdentifier().
          getValue();
        String obx18_ei2 = obx18_equip_inst_id[ix].getEi2_NamespaceID().
          getValue();
        String obx18_ei3 = obx18_equip_inst_id[ix].getEi3_UniversalID().
          getValue();
        String obx18_ei4 = obx18_equip_inst_id[ix].getEi4_UniversalIDType().
          getValue();

        if (obx18_ei1 != null)
          System.out.println("\tEntity Identifier: " + obx18_ei1);

        if (obx18_ei2 != null)
          System.out.println("\tNamespace ID: " + obx18_ei2);

        if (obx18_ei3 != null)
          System.out.println("\tUniversal ID: " + obx18_ei3);

        if (obx18_ei4 != null)
          System.out.println("\tUniversal ID Type: " + obx18_ei4);
      }
    }

    if (obx19_date_time != null)
      System.out.println("OBX-19 (Date/Time of Analysis): " + obx19_date_time);

    
    if (obx20_obs_site.length > 0)
    {
      System.out.println("OBX-20 (Observation Site)");

      for (int ix = 0; ix < obx20_obs_site.length; ix++)
      {
        String obx20_cwe1 = obx20_obs_site[ix].getCwe1_Identifier().getValue();

        if (obx20_cwe1 != null)
          System.out.println("\tObservation Site: " + obx20_cwe1);
      }
    }

    System.out.println();
  }
}
