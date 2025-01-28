package org.mdpnp.apps.testapp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;

import ice.ConnectionType;

public class DeviceListCreator {

	public static void main(String[] args) {
		try {
			final String preamble="This is the list of currently supported devices in OpenICE.";
			final String when="It was automatically created at "+Date.from(Instant.now());
			DeviceDriverProvider[] providers=DeviceFactory.getAvailableDevices();
			ArrayList<DeviceDriverProvider> serial=new ArrayList<>(),network=new ArrayList<>(),simulated=new ArrayList<>();
			for(DeviceDriverProvider provider : providers) {
				DeviceType dt=provider.getDeviceType();
				System.out.println(dt.getManufacturer()+" - "+dt.getModel());
				if(dt.getConnectionType().equals(ConnectionType.Serial)) {
					serial.add(provider);
				}
				if(dt.getConnectionType().equals(ConnectionType.Network)) {
					network.add(provider);
				}
				if(dt.getConnectionType().equals(ConnectionType.Simulated)) {
					simulated.add(provider);
				}
			}
			System.err.println(System.getProperty(("user.dir")));
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream("../../devs.html"));
			PrintStream htmlout=new PrintStream(bos);
			
			BufferedOutputStream bos2=new BufferedOutputStream(new FileOutputStream("../../devs.md"));
			PrintStream mdout=new PrintStream(bos2);
			
			htmlout.println("<html><head><style>th,td {border: solid; padding-left: 10px; padding-right: 10px} table {border-collapse: collapse;}</style><body>");
			
			htmlout.println("<h1>"+preamble+"</h1>");
			htmlout.println("<h2>"+when+"</h2>");
			mdout.println("# "+preamble);
			mdout.println("## "+when);
			
			htmlout.println("<h3>Serial Devices</h3><table><thead>");
			htmlout.println("<tr><th>Manufacturer</th><th>Model</th></tr><tbody>");
			mdout.println("### Serial Devices");
			mdout.println();
			mdout.println("| Manufacturer | Model |");
			mdout.println("-------------- | -------");
			serial.forEach( p -> {
				htmlout.printf("<tr><td>%s</td><td>%s</td></tr>", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
				mdout.printf("| %s | %s |\n", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
			});
			htmlout.println("<tbody></table>");
			
			htmlout.println("<h3>Network Devices</h3><table><thead>");
			htmlout.println("<tr><th>Manufacturer</th><th>Model</th></tr><tbody>");
			mdout.println("### Network Devices");
			mdout.println();
			mdout.println("| Manufacturer | Model |");
			mdout.println("-------------- | -------");
			network.forEach( p -> {
				htmlout.printf("<tr><td>%s</td><td>%s</td></tr>", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
				mdout.printf("| %s | %s |\n", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
			});
			htmlout.println("<tbody></table>");
			
			htmlout.println("<html><body><h3>Simulated Devices</h3><table><thead>");
			htmlout.println("<tr><th>Manufacturer</th><th>Model</th></tr><tbody>");
			mdout.println("### Simulated Devices");
			mdout.println();
			mdout.println("| Manufacturer | Model |");
			mdout.println("-------------- | -------");
			simulated.forEach( p -> {
				htmlout.printf("<tr><td>%s</td><td>%s</td></tr>", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
				mdout.printf("| %s | %s |\n", p.getDeviceType().getManufacturer(), p.getDeviceType().getModel());
			});
			htmlout.println("<tbody></table>");
			mdout.println();
			bos.flush();
			bos.close();
			bos2.flush();
			bos2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
