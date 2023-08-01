FROM eclipse-temurin:17
COPY interop-lab/demo-apps/flat /opt/mdpnp/flat
WORKDIR /opt/mdpnp/flat
CMD java -jar demo-apps-1.5.0-SNAPSHOT.jar -app ICE_Device_Interface -domain 10 -device Controllable_Pump
