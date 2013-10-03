/**
 * @file    MindrayA5Reader.java
 *
 * @brief   Manages a HAPI SimpleServer which Interfaces with a Mindray A5
 * Series Anesthesia System. The SimpleServer listens on a port for HL7
 * messages. The SimpleServer uses a communications protocol called Minimal
 * Lower Layer Protocol (MLLP). This class publishes DDS data types to report
 * device identity and device connectivity.
 *
 * @author M Szwaja
 */
//=============================================================================

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.SimpleServer;

import ice.DeviceIdentity;
import ice.DeviceConnectivity;
import ice.ConnectionState;
import ice.ConnectionType;


/**
 * Class MindrayA5Reader. Creates and manages a SimpleServer to communicate
 * with the Mindray A5
 */
public class MindrayA5Reader
{
  private int _port;
  private SimpleServer _server;
  private Application _handler;
  private DDSImpl _rti_dds_impl;
  public static final String kMindrayUDI = "Mindray A5 UDI";
  private String _statusmsg;
  private String _log_file_name = "./dat/mindray_a5_log.dat";
  private Boolean _enable_logger;


  public MindrayA5Reader(final int iport)
  {
    _port = iport;
    _server = null;
    _handler = null;
    _rti_dds_impl = null;
    _enable_logger = false;
  }


  public final void set_port(final int port)
  {
    _port = port;
  }


  public final void set_log_file_name(final String name)
  {
    _log_file_name = name;
  }


  public final void set_enable_logger(final boolean torf)
  {
    _enable_logger = torf;
  }


  public final String get_statusmsg()
  {
    return _statusmsg;
  }


  /**
   * Creates DDS enitities, including Domain Participant and DataWriters
   * One DataWriter publishes DeviceIdentity samples, and the other 
   * publishes DeviceConnectivity samples.
   * @param domain_id Domain for Domain Participant
   * @return Returns zero for success.
   */
  public final int InitDDS(final int domain_id)
  {
    _rti_dds_impl = new DDSImpl();

    if (_rti_dds_impl == null)
    {
      _statusmsg = "InitDDS() unable to create DDSImpl";
      return 1;
    }

    if (_rti_dds_impl.CreateDomainParticipant(domain_id) != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    if (_rti_dds_impl.CreateDeviceIdentityWriter() != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    if (_rti_dds_impl.CreateDeviceConnectivityWriter() != 0)
    {
      _statusmsg = _rti_dds_impl.get_statusmsg();
      return 1;
    }

    return 0;
  }


  /**
   * Shutdown DDS Domain Participant and all entities contained within it.
   * @return Returns zero for success
   */
  public final int ShutdownDDS()
  {
    if (_rti_dds_impl != null)
    {
      int istat = _rti_dds_impl.ParticipantShutdown();
      if (istat != 0)
      {
        _statusmsg = _rti_dds_impl.get_statusmsg();
        return 1;
      }
    }
    return 0;
  }


  /**
   * Stores data into DeviceIdentity structure and publishes the sample.
   * @return Returns zero for success
   */
  private int WriteDeviceIdentity()
  {
    if (_rti_dds_impl != null)
    {
      DeviceIdentity device_identity = new DeviceIdentity();

      // Set device identity information
      device_identity.unique_device_identifier = kMindrayUDI;
      device_identity.manufacturer = "Mindray";
      device_identity.model = "A5";
      device_identity.serial_number = "123456789";

      // Publish the sample
      if (_rti_dds_impl.WriteDeviceIdentity(device_identity) != 0)
      {
        _statusmsg = _rti_dds_impl.get_statusmsg();
        return 1;
      }
    }

    return 0;
  }


  /**
   * Stores data into DeviceConnectivity structure and publishes the sample.
   * @return Returns zero for success
   */
  private int WriteDeviceConnectivity(final ConnectionState state,
    final ConnectionType type)
  {
    if (_rti_dds_impl == null)
    {
      _statusmsg = "WriteDeviceConnectivity() _rti_dds_impl is null";
      return 1;
    }

    if (_rti_dds_impl != null)
    {
      DeviceConnectivity device_conn = new DeviceConnectivity();

      // Set device connectivity information
      device_conn.state = state;
      device_conn.type = type;
      device_conn.unique_device_identifier = kMindrayUDI;

      // Gather all valid IP addresses
      try
      {
        Enumeration<NetworkInterface> net_list = NetworkInterface.
          getNetworkInterfaces();
        while (net_list.hasMoreElements())
        {
          NetworkInterface net_iface =
            (NetworkInterface) net_list.nextElement();
          Enumeration<InetAddress> addr_list = net_iface.getInetAddresses();

          // Add IP addresses to valid_targets
          while (addr_list.hasMoreElements())
          {
            InetAddress inet_add = (InetAddress) addr_list.nextElement();

            if (device_conn.valid_targets.size() 
              < device_conn.valid_targets.getMaximum())
                device_conn.valid_targets.add(inet_add.getHostAddress());
          }
        }
      }
      catch (SocketException exc)
      {
        _statusmsg = "WriteDeviceConnectivity() Exception" + exc.getMessage();
        return 1;
      }

      if (_rti_dds_impl.WriteDeviceConnectivity(device_conn) != 0)
      {
        _statusmsg = _rti_dds_impl.get_statusmsg();
        return 1;
      }
    }

    return 0;
  }


  /**
   * Create a server to listen for incoming HL7 messages. The Mindray uses
   * message type "ORU" and trigger event "R01".
   * @throws HL7Exception
   * @throws IOException
   * @return Returns zero for success
   */
  public final int StartMindrayServer()
    throws HL7Exception, IOException
  {
    if (WriteDeviceIdentity() != 0) return 1;

    // Mindray uses Minimal Lower Layer Protocol (MLLP). Creates a SimpleServer
    // that listens on the given port, using MLLP and a standard PipeParser.
    _server = new SimpleServer(_port);

    // The server may have any number of "application" objects registered to
    // handle messages.
    _handler = new MindrayA5ReceiverApplication();

    if (((MindrayA5ReceiverApplication) _handler).
      set_rti_dds_impl(_rti_dds_impl) != 0)
    {
      _statusmsg = ((MindrayA5ReceiverApplication) _handler).get_statusmsg();
      return 1;
    }

    if (((MindrayA5ReceiverApplication) _handler).InitDDS() != 0)
    {
      _statusmsg = ((MindrayA5ReceiverApplication) _handler).get_statusmsg();
      return 1;
    }

    if (_enable_logger)
    {
      ((MindrayA5ReceiverApplication) _handler).set_enable_logger(true);
      ((MindrayA5ReceiverApplication) _handler).OpenLogfile(_log_file_name,
        false);
    }
    else
    {
      ((MindrayA5ReceiverApplication) _handler).set_enable_logger(false);
    }

    // Register application with the  message type "ORU" and trigger event 
    // "R01".
    _server.registerApplication("ORU", "R01", _handler);

    // Server is binding to port
    if (WriteDeviceConnectivity(ConnectionState.Connecting,
      ConnectionType.Network) != 0) return 1;

    // Start the server and listen for messages
    _server.start();

    // Publish Device Connectivity Connected
    if (WriteDeviceConnectivity(ConnectionState.Connected,
      ConnectionType.Network) != 0) return 1;

    return 0;
  }


  /**
   * Stops the SimpleServer if it's running. Publishes DeviceConnectivity
   * sample to update status
   * @return Returns zero for success
   */
  public final int StopMindrayServer()
  {
    // Publish Device Connectivity Disconnecting...
    if (WriteDeviceConnectivity(ConnectionState.Disconnecting,
      ConnectionType.Network) != 0) return 1;

    if (_server.isRunning()) _server.stop();

    // Publish Device Connectivity Disconnected
    if (WriteDeviceConnectivity(ConnectionState.Disconnected,
      ConnectionType.Network) != 0) return 1;

    if (_enable_logger)
      if (((MindrayA5ReceiverApplication) _handler).CloseLogfile() != 0)
        return 1;

    _server = null;
    _handler = null;

    return 0;
  }
}
