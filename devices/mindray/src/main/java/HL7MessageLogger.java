/**
 * @file    HL7MessagaeLogger.java
 *
 * @breif   Defines a class which handles a log file. The messages written
 * to the file are appended to the last message. A backup file is created 
 * when the maximum file size is reached.
 *
 * @author  M Szwaja
 */
//=============================================================================
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import ca.uhn.hl7v2.HL7Exception;


/*
 * Manages a log file.
 */
public class HL7MessageLogger
{
  private DataOutputStream _output_stream;
  private static int kMaxLogFileSize = 1000000; // 1MB
  private String _file_name;
  private File _file;


  public HL7MessageLogger()
  {
    _output_stream = null;
    _file_name = "";
    _file = null;
  }


  /**
   * Opens log file with the given file name.
   * @param file_name Path to filename
   * @param append True sets append mode.
   * @return Returns zero for success.
   */
  public final int OpenLogfile(final String file_name, final boolean append)
  {
    _file_name = file_name;
    _file = new File(file_name);
 
    try
    {
      _output_stream =
        new DataOutputStream(new FileOutputStream(file_name, append));
    }
    catch (FileNotFoundException exc)
    {
      System.out.println("HL7MessageLogger::OpenLogfile() file not found " 
        + exc.getMessage());
      return 1;
    }

    return 0;
  }


  /**
   * Closes the log file.
   * @return Returns zero for success.
   */
  public final int CloseLogfile()
  {
    if (_output_stream == null)
    {
      System.out.println("HL7MessageLogger::CloseLogfile() output stream "
        + "is a pointer to null");
      return 1;
    }

    try
    {
      _output_stream.close();
    }
    catch (IOException exc)
    {
      System.out.println("CloseLogfile IOException " + exc.getMessage());
      return 1;
    }

    return 0;
  }
  
  
  /**
   * Creates a backup file for the current log file. The backup file has the
   * same file name with an additional ".bak" extension. The original file
   * is erased and reopened.
   * @return Returns zero for success.
   */
  private final int BackupFile()
  {
    if (_file == null)
    {
      System.out.println("HL7MessageLogger::WriteBuffer() file "
        + "is a pointer to null");
      return 1;
    }

    if (CloseLogfile() != 0)
    {
      System.out.println("HL7MessageLogger::WriteBuffer() unable to "
        + "close open file");
      return 1;
    }

    File file_dest = new File(_file_name.concat(".bak"));
 
    if (!_file.renameTo(file_dest))
    {
      System.out.println("HL7MessageLogger::WriteBuffer() unable to "
        + " rename file with \".bak\"");
      return 1;
    }

    if (OpenLogfile(_file_name, true) != 0)
    {
      System.out.println("HL7MessageLogger::WriteBuffer() unable to "
        + " open new file");
      return 1;
    }

    return 0;
  }


  /**
   * Write string of bytes to an open log file. If the file size reaches the 
   * limit, a backup file is created, and the string is written to an empty
   * file with the original file name.
   * @param hapi_str
   * @return Returns zero for success
   * @throws HL7Exception
   */
  public final int WriteBuffer(final String hapi_str)
    throws HL7Exception
  {
    if (_output_stream == null)
    {
      System.out.println("HL7MessageLogger::WriteBuffer() output stream "
        + "is a pointer to null");
      return 1;
    }

    if (_output_stream.size() > kMaxLogFileSize)
    {
      if (BackupFile() != 0) return 1;
    }

    try
    {
      _output_stream.write(hapi_str.getBytes());
    }
    catch (IOException exc)
    {
      System.out.println("HL7MessageLogger::WriteBuffer() IOException "
        + exc.getMessage());
      return 1;
    }

    return 0;
  }
}
