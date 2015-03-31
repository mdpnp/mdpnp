title=Release 0.6.0 Notes
type=doc
status=published
description=Release notes for OpenICE version 0.6.0
~~~~~~

This major release of OpenICE features the switch from [Java Swing](http://en.wikipedia.org/wiki/Swing_%28Java%29) to [Java FX](http://en.wikipedia.org/wiki/JavaFX).  The primary reason for the switch was to use a better supported and actively developed UI subsystem.  We discovered other benefits in the course of the switch such as reactive property bindings, layouts express in XML (FXML), styles delegated to CSS, a supported common packager for distribution that bundles the appropriate JRE for maximum portability, etc.

Note that at this point in the development of OpenICE data model changes may happen between any versions.  In this case nodes using v0.5.0 should not be used in the same system with those using 0.6.0.  A future goal is to provide more stability but at this point numerous changes were made (for cause) that break compatibility.

(Abridged) summary of changes since 0.5.0

* __Switch to Java 8 compliance__
  To utilize the latest features of JavaFX (and the Java language) we have standardized on Oracle Java 1.8.0_40 for this release.

* __Puritan Bennett 840 Ventilator Driver Improvements__
  A lot of work was done to improve this driver.  Nomenclature used was cross-referenced between reference documentation and the actual user documentation to ensure that it is as meaningful as possible.  The nomenclature codes used are now maintained in a human (Excel) readable file that can be used as a reference for anyone using the driver.  In time we will provide the same rigorous cross-reference and documentation for other drivers.  In the course of the work a vendor_metric_id was added to the data model for runtime validation of nomenclature mapping used.

* __TimeManager duplication for devices__
  A bug was fixed that duplicated Heartbeat and TimeSync interactions for devices

* __System Explorer__
  A barebones app that shows various types of system data at runtime was added in the spirit of https://www.openice.info/diagnostics.html

* __Alert History__
  An app that shows a history of alarms that have been activated in the system.

* __HL7 Export__
  An app (still under development) that exports system data in either HL7 v2.6 or HL7 FHIR DSTU2 format.  The capabilities of the app are still very limited but it will evolve over time.  See http://hl7api.sourceforge.net and http://jamesagnew.github.io/hapi-fhir/index.html for more information.

* __/etc/issue__
  Dennis at NIST submitted a patch that more appropriately uses /etc/os-release to identify the local operating system build instead of the less appropriate /etc/issue

* __jbake documentation__
  jbake modules have been added for maintaining and building documentation (like what you are reading now) alongside the code.

* __Data Timestamps__
  The API mechanism for assigning timestamps to emitted data has been made much more flexible.  In addition we've moved away from using the transport-level SampleInfo.source_timestamp for application-level purposes in favor of a newly added presentation_time.  The presentation_time field represents the timestamp at which the data should be shown to the user (when viewing historically or in chart form).

* __Patient/Device Assignment__
  An app (still under development) for assigning devices to a patient's ICE environment.  This supervisory app instructs a device (via an objective state) to publish data to a logical partition created for a particular patient.

* __Unit Testing__
  For new development a renewed effort has been made to create accompanying unit tests.  Over time the team would like to add more unit tests to existing code and as bugs are fixed we've made a renewed commitment to unit testing to avoid regressions.

* __Highlight clock differences__
  Utilizing the infrastructure of TimeManager the UI will now highlight devices with clock's that our not in sync with the supervisor. Synchronization is still meant to be achieved with NTP; this application-level check is to verify that the system (and nodes) are sufficiently synchronized to operate safely at runtime.

* __Charting__
  An app (still under development) to chart numeric vital signs over time.

* __Simulation Changes__
  More sophistication has been added to the simulation capability to specify the amount that values out to vary over time.  Eventually the goal is to create a pseudo-realistic patient simulation which, while not physiologically correct, may aid in the testing of applications.