package org.mdpnp.devices;


import com.rti.dds.infrastructure.BadKind;
import com.rti.dds.infrastructure.Bounds;
import ice.DeviceIdentityTypeCode;
import org.mdpnp.rtiapi.data.TypeCodeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ice.DeviceIdentity;

public class DeviceIdentityBuilder {

    private static final Logger log = LoggerFactory.getLogger(DeviceIdentityBuilder.class);

    DeviceIdentityBuilder() {
        this(new DeviceIdentity());
    }

    DeviceIdentityBuilder(DeviceIdentity p) {
        pojo = p;
    }

    final private DeviceIdentity pojo;

    public DeviceIdentity build() {
        pojo.build = BuildInfo.getDescriptor();
        return pojo;
    }

    public DeviceIdentityBuilder softwareRev() {
        pojo.build = BuildInfo.getDescriptor();
        return this;
    }

    public DeviceIdentityBuilder model(String model) {
        pojo.model = model;
        return this;
    }
    
    public DeviceIdentityBuilder osName() {

        String osName = System.getProperty("os.name");

        int maxOperatingSystemLength = 0;
        try {
            maxOperatingSystemLength = TypeCodeHelper.fieldLength(DeviceIdentityTypeCode.VALUE, "operating_system");
        } catch (BadKind | Bounds e) {
            log.warn("Unable to find length of DeviceIdentity.operating_system", e);
        }

        if (maxOperatingSystemLength > 0) {
            if ("Linux".equals(osName)) {

                final String OS_RELEASE_FILE = "/etc/os-release";
                Path osRelease = Paths.get(OS_RELEASE_FILE);
                File f = osRelease.toFile();
                if (f.exists() && f.canRead()) {
                    try {

                        List<String> definitions = Files.readAllLines(osRelease, Charset.forName("UTF-8"));
                        for (String line : definitions) {
                            try {
                                if (line.startsWith("PRETTY_NAME=")) {
                                    String value = line.substring(line.indexOf("=") + 1);
                                    if (value.startsWith("\"")) { // Remove the quotes around the value.
                                        value = value.substring(1, value.length() - 1);
                                    }
                                    osName = value;
                                    break;
                                }
                            } catch (IndexOutOfBoundsException e) {
                                log.debug(e.getMessage(), e);
                            }
                        }
                    } catch (IOException e1) {
                        log.info("Unable to read " + OS_RELEASE_FILE + " on this Linux system", e1);
                    }
                }
            }
            String operatingSystem = osName + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version");

            if (operatingSystem.length() > maxOperatingSystemLength) {
                operatingSystem = operatingSystem.substring(0, maxOperatingSystemLength);
            }
            pojo.operating_system = operatingSystem;
        } else {
            pojo.operating_system = "";
        }

        return this;
    }

    public DeviceIdentityBuilder withIcon(Object dev, String iconResourceName) {
        return withIcon(dev.getClass(), iconResourceName);
    }

    public DeviceIdentityBuilder withIcon(Class dev, String iconResourceName) {
        try {
            iconFromResource(dev, iconResourceName);
        } catch (IOException e1) {
            log.warn("", e1);
        }

        return this;
    }

    private boolean iconFromResource(Class dev, String iconResourceName) throws IOException {
        if (null == iconResourceName) {
            pojo.icon.content_type = "";
            pojo.icon.image.clear();
            return true;
        }

        // are there other types that should be supported?
        pojo.icon.content_type = "image/png";

        InputStream is = dev.getResourceAsStream(iconResourceName);
        if (null != is) {
            try {
                {
                    byte[] xfer = new byte[1024];
                    int len = is.read(xfer);

                    pojo.icon.image.userData.clear();

                    while (len >= 0) {
                        pojo.icon.image.userData.addAllByte(xfer, 0, len);
                        len = is.read(xfer);
                    }
                    is.close();
                }
                return true;
            } catch (Exception e) {
                log.error("error in iconUpdateFromResource", e);
            }
        }
        return false;
    }

    private static final int UDI_LENGTH = 36;
    private static final char[] UDI_CHARS = new char[26 * 2 + 10];

    static {
        int x = 0;
        for (char i = 'A'; i <= 'Z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for (char i = 'a'; i <= 'z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for (char i = '0'; i <= '9'; i++) {
            UDI_CHARS[x++] = i;
        }
    }

    public static String randomUDI() {
        String udi = System.getProperty("randomUDI");
        if (null != udi && !"".equals(udi)) {
            return udi;
        } else {
            StringBuilder sb = new StringBuilder();
            java.util.Random random = new java.util.Random(System.currentTimeMillis());
            for (int i = 0; i < UDI_LENGTH; i++) {
                sb.append(UDI_CHARS[random.nextInt(UDI_CHARS.length)]);
            }
            return sb.toString();
        }
    }
}
