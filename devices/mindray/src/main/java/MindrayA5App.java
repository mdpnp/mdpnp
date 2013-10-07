/**
 * @file    MindrayA5Reader.java
 *
 * @brief   Interfaces with a Mindray A5 Series Anesthesia System. The
 * Mindray connects to the host computer via Ethernet cable. The host
 * computer, executing this program, listens on a port for HL7 messages.
 * The program uses an open source HL7 application programming interface (HAPI)
 * to receive and parse HL7 messages from the Mindray. The Mindray uses a
 * communications protocol called Minimal Lower Layer Protocol (MLLP).
 *
 * @author  M Szwaja
 */
//=============================================================================
import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;


/**
 * Class contains main method which starts the application. The application
 * interfaces with a Mindray A5 Series Anesthesia System.
 */
public final class MindrayA5App
{
  static final int kDefaultPort = 5000;
  static final int kDefaultDomainId = 0;
  static final int kAsciiEsc = 0x1B;

  private static int port = kDefaultPort;
  private static int domain_id = kDefaultDomainId;
  private static boolean enable_logger = false;
  private static String log_file_name = "";


  private MindrayA5App()
  {
  }


  private static void PrintCMDLineHelp()
  {
    String help_message = "\nUSAGE:\n"
      + "   MindrayA5Reader [-port <number>] | [-help]\n"
      + "   {} Required\n"
      + "   [] Optional\n"
      + "   |  Separator between option choices\n"
      + "where:\n"
      + "   -help              Print help message.\n"
      + "   -port <number>     Configure port (optional, default 5000)\n" 
      + "   -domainId <id>     Set domain ID for DDS domain participant\n"
      + "   -enableLogger      Use log file to store received HL7 messages\n"
      + "   -l <file_name>     Set log file name (optional, default\n"
      + "                      \"./dat/mindray_a5_log.dat\"\n"
      + "\n"
      + "Brief Description:\n"
      + "Interfaces with Mindary A5 Series Anestesia System.\n\n";
    System.out.println(help_message);
  }


  /**
   * Parse command line parameters and save configuration settings for
   * the application.
   * @param args
   * @return Returns zero for success
   */
  private static int ParseCommandLine(final String [] args)
  {
    for (int ix = 0; ix < args.length; ix++)
    {
      if (args[ix].equalsIgnoreCase("-help"))
      {
        PrintCMDLineHelp();
        return 1;
      }
      else if (args[ix].equalsIgnoreCase("-port"))
      {
        if (ix >= args.length - 1 || args[ix + 1].startsWith("-"))
        {
          System.err.println("ParseCommandline() Error: bad parameter list. "
            + "Missing <number> after \"-port\"");
          return 1;
        }

        try
        {
          port = Integer.parseInt(args[ix + 1]);
          ix++;
        }
        catch (NumberFormatException ex)
        {
          System.err.println("ParseCommandline() Error: \"-port\" must be "
            + "followed by an integer");
          return 1;
        }
      }
      else if (args[ix].equalsIgnoreCase("-domainId"))
      {
        if (ix >= args.length - 1 || args[ix + 1].startsWith("-"))
        {
          System.err.println("ParseCommandline() Error: bad parameter list. "
            + "Missing <id> after \"-domainId\"");
          return 1;
        }

        try
        {
          domain_id = Integer.parseInt(args[ix + 1]);
          ix++;
        }
        catch (NumberFormatException ex)
        {
          System.err.println("ParseCommandline() Error: \"-domainId\" must be "
            + "followed by an integer");
          return 1;
        }
      }
      else if (args[ix].equalsIgnoreCase("-enableLogger"))
      {
        enable_logger = true;
      }
      else if (args[ix].equalsIgnoreCase("-l"))
      {
        if (ix >= args.length - 1 || args[ix + 1].startsWith("-"))
        {
          System.err.println("ParseCommandline() Error: bad parameter list. "
            + "Missing <file_name> after \"-l\"");
          return 1;
        }

        log_file_name = args[ix + 1];
        ix++;
      }
      else
      {
        System.err.printf("ParseCommandline() Error: %s is not an option. "
          + "Enter \"-help\" option for help", args[ix]);
        return 1;
      }
    }

    return 0;
  }


  /**
   * Parses command line options and creates one server to interface with the
   * Mindray. The server listens for incoming messages.
   * @param args
   * @throws HL7Exception
   * @throws EncodingNotSupportedException
   * @throws IOException
   * @throws LLPException
   */
  public static void main(final String[] args) throws IOException
  {
    if (ParseCommandLine(args) != 0) return;

    MindrayA5Reader mindray_reader = new MindrayA5Reader(port);

    mindray_reader.set_enable_logger(enable_logger);

    if (log_file_name.length() > 0)
      mindray_reader.set_log_file_name(log_file_name);

    if (mindray_reader.InitDDS(domain_id) != 0)
    {
      System.out.println(mindray_reader.get_statusmsg());
      return;
    }

    System.out.printf("Server listening on port %d\n\n", port);

    try
    {
      if (mindray_reader.StartMindrayServer() != 0)
      {
        System.out.println(mindray_reader.get_statusmsg());
        return;
      }
    }
    catch (EncodingNotSupportedException exc)
    {
      System.out.println("main() EncodingNotSupportedException: "
        + exc.getMessage());
      return;
    }
    catch (HL7Exception exc)
    {
      System.out.println("main() HL7Exception: " + exc.getMessage());
      return;
    }
    catch (IOException exc)
    {
      System.out.println("main() IOException: " + exc.getMessage());
      return;
    }

    char cc = 0;

    // Program executes until operator presses Escape key.
    // Requires the operator to press the Enter key.
    while (cc != kAsciiEsc)
    {
      cc = 0;
      cc = (char) System.in.read(); // Blocks for enter key
    }

    // Shutdown the server and DDS
    if (mindray_reader.StopMindrayServer() != 0)
      System.out.println(mindray_reader.get_statusmsg());

    if (mindray_reader.ShutdownDDS() != 0)
      System.out.println(mindray_reader.get_statusmsg());

    mindray_reader = null;

    System.runFinalization(); // Complete finalization up to this point
    System.gc(); // Run garbage collector

    System.out.println("Exiting...");

    // TODO Why does it take so long (30 secs to 1 min) to return from main?
    // Debugging proved that the culprit is the SimpleServer start() method
    // Tried just about everything to stop SimpleServer. Unfortunately there
    // is no Thread.interrupt() inside any of the SimpleServer stop() methods.
    //System.exit(0); // ????
  }
}
