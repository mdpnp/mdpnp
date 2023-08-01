FROM eclipse-temurin:17
COPY interop-lab/demo-apps/flat /opt/mdpnp/flat
WORKDIR /opt/mdpnp/flat
ENV LD_LIBRARY_PATH /opt/mdpnp/flat
ENV RTI_LICENSE_FILE /opt/mdpnp/flat/OpenICE_license.dat
RUN java -jar demo-apps-1.5.0-SNAPSHOT.jar -app ICE_Device_Interface -domain 10 -device Controllable_Pump
