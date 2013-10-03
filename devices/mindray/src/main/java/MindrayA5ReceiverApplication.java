/**
 * @file    MindrayA5ReceiverApplication.java
 * 
 * @brief   This class implements an HAPI Application. This class is
 * registered by the SimpleServer to receive HL7 messages from the port. The
 * messages received are expected to have a message type "ORU" and 
 * a trigger event "R01".
 * 
 * @author  M Szwaja
 */
//=============================================================================
import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;


/**
 * Application class for receiving HL7 messages.
 */
public class MindrayA5ReceiverApplication implements Application
{
  private HL7Parser _hl7_parser;
  private DDSImpl _rti_dds_impl;
  private String _statusmsg;
  private boolean _enable_logger;
  private HL7MessageLogger _hl7_logger;


  public MindrayA5ReceiverApplication()
  {
    _hl7_parser = null;
    _rti_dds_impl = null;
    _enable_logger = false;

    _hl7_parser = new HL7Parser();
    _hl7_logger = new HL7MessageLogger();
  }


  public final String get_statusmsg()
  {
    return _statusmsg;
  }


  public final void set_enable_logger(final boolean torf)
  {
    _enable_logger = torf;
  }


  public final int OpenLogfile(final String file_name, final boolean append)
  {
    return _hl7_logger.OpenLogfile(file_name, append);
  }


  public final int CloseLogfile()
  {
    return _hl7_logger.CloseLogfile();
  }


  /**
   * Precondition: DDS Domain Participant is already created.
   * Sets _rti_dds_impl. 
   * @param dds_impl DDSImpl object
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


  /**
   * Creates DDS entities required by the HL7 parser, such as
   * PatientDemographics and Numeric DataWriters.
   * @return Returns zero for success.
   */
  public final int InitDDS()
  {
    if (_rti_dds_impl == null)
    {
      System.out.println("processMessage() _rti_dds_impl is null");
      return 1;
    }

    if (_hl7_parser.set_rti_dds_impl(_rti_dds_impl) != 0)
    {
      System.out.println(_hl7_parser.get_statusmsg());
      return 1;
    }

    if (_hl7_parser.InitDDS() != 0)
    {
      System.out.println(_hl7_parser.get_statusmsg());
      return 1;
    }

    return 0;
  }


  /**
   * {@inheritDoc}
   */
  public final boolean canProcess(final Message theIn)
  {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  public final Message processMessage(final Message theIn)
    throws ApplicationException, HL7Exception
  {
    Message retval = null;

    try
    {
      retval = theIn.generateACK();
    }
    catch (IOException exc)
    {
      System.out.println("processMessage() Exception: " + exc.getMessage());
      return retval;
    }

    // Print the message as a string.
    String encoded_message = new PipeParser().encode(theIn);
    System.out.println("Received message:\n" + encoded_message + "\n\n");

    if (_enable_logger)
    {
      // Log Mindray Message
      if (_hl7_logger.WriteBuffer(encoded_message) != 0) return retval;
    }

    // Parse and publish samples
    if (_hl7_parser.ParseORURO1MessageToDDS(theIn) != 0)
    {
      System.out.println(_hl7_parser.get_statusmsg());
      return retval;
    }

    return retval; 
  }
}
