#FROM alpine:20230329
FROM fedora:latest
COPY rti/ /opt/rti/
COPY web_integration_service /opt/web_integration_service/
ENV LD_LIBRARY_PATH /opt/rti
ENV RTI_LICENSE_FILE /opt/web_integration_service/OpenICE_license.dat
CMD /opt/rti/rtiwebintegrationservice -cfgName openicewis -cfgFile /opt/web_integration_service/OPENICE_WEB_INTEGRATION_SERVICE.xml -documentRoot /opt/web_integration_service/docroot
#cd C:\Users\HPWorkStation\Documents\rti_workspace\6.0.1\user_config\web_integration_service
#"C:\Program Files\rti_connext_dds-6.0.1\bin\rtiwebintegrationservice.bat" -cfgName openicewis -cfgFile C:\Users\HPWorkStation\feb23demo\mdpnp\web_integration_service\OPENICE_WEB_INTEGRATION_SERVICE.xml -documentRoot C:\Users\HPWorkStation\feb23demo\mdpnp\web_integration_service\docroot
