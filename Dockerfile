FROM eclipse-temurin:17
COPY interop-lab/demo-apps/flat /opt/mdpnp/flat
WORKDIR /opt/mdpnp/flat
ENV LD_LIBRARY_PATH /opt/mdpnp/flat
ENV RTI_LICENSE_FILE /opt/mdpnp/flat/OpenICE_license.dat
#This will just be a base image so other images can be built from this layer that make use of the content to run different devices.
#You can extend it by using it as a FROM base and then adding a command like this
#CMD java -jar demo-apps-1.5.0-SNAPSHOT.jar -app ICE_Device_Interface -domain 10 -device Controllable_Pump
