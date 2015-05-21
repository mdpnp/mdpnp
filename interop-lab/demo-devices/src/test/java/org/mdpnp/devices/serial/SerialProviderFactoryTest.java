package org.mdpnp.devices.serial;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mfeinberg
 */
public class SerialProviderFactoryTest {

  @Test
  public void testTCPSerialProvider() throws Exception {
    testDefaultProviderPortNames(new String[] { "org.mdpnp.devices.serial.TCPSerialProvider" }, 0);
  }

  // This could fail. The provider is optional and the jar could be missing in the default configuration
  //@Test
  public void testPureJavaCommSerialProvider() throws Exception {
    testDefaultProviderPortNames(new String[] { "org.mdpnp.data.serial.PureJavaCommSerialProvider" }, 4);
  }

  // This could fail. The provider is optional and the jar could be missing in the default configuration
  @Test
  public void testDummyProvider() throws Exception {
    testDefaultProviderPortNames(new String[]
                                     { "org.mdpnp.devices.serial.SerialProviderFactoryTest$SerialProviderImpl",
                                       "this.will.not.resolve.ever"
                                     },
                                 1);
  }

  void testDefaultProviderPortNames(String[] providers, int expectedNPorts) throws Exception {


    // we can only test with the provider that included on the
    // class path of the test; when running in IDE with every
    // application jar available, other providers could be tested.
    //
    List l = Arrays.asList(providers);
    SerialProvider sp = SerialProviderFactory.locateDefaultProvider(l);
    Assert.assertNotNull("Could not locate provider", sp);
    List<String> ports = sp.getPortNames();
    Assert.assertNotNull("Invalid port list", ports);
    Assert.assertEquals("Port list is empty", ports.size(), expectedNPorts);

  }

  public static class SerialProviderImpl implements SerialProvider {
    @Override
    public List<String> getPortNames() {
      List l = new ArrayList<>();
      l.add("mock");
      return l;
    }

    @Override
    public SerialSocket connect(String portIdentifier, long timeout) throws IOException {
      return null;
    }

    @Override
    public void cancelConnect() {
    }

    @Override
    public void setDefaultSerialSettings(int baudrate, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits) {
      throw new UnsupportedOperationException("MOCK Object");
    }

    @Override
    public void setDefaultSerialSettings(int baudrate, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits, SerialSocket.FlowControl flowControl) {
      throw new UnsupportedOperationException("MOCK Object");
    }

    @Override
    public SerialProvider duplicate() {
      return null;
    }
  }
}
