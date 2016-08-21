
# Patient Record Lifecycle.

At the device level, association is expressed as one of the DDS ‘partitions’ the device 
could be assign to. There could be many ‘partitions’ device could participate in – for 
example: “ER-Room”, “Phillips-Equipment’,  ‘Ventilators’. By convention, one of the partitions 
is of the following format: ‘MRN=XXXXXXXX’; the letters that follow ‘MRN=’ are the patient ID 
for that device. Partition file can contain only one MRN entry. Upon startup the device broadcasts 
its partition assignment as MDSHandler.Connectivity.MDSEvent event.

# Patient Controller App

Patient Controller App matches the MRN information that it discovers from listening to 
the device connectivity messages with the patients information it reads from the EMR system. 
Two EMR interfaces are implemented:

-	FHIR Server API
-	JDBC Persistence

Only the patient records are being stored. Patient/device association information is 
not being persisted as duplication data from the partition file could introduce system-wide 
inconsistency since there is no infrastructure to maintain them in sync. Thecould be future 
interest to keep some history of device connectivity, but at this moment there is no application 
that could take advantage of it. 

# Note on the JDBC Persistence

Default persistence is local storage on file system accessible to the supervisor application. 
If another supervisor is modifying device configuration, MRN will be announced and stored 
locally without other patient information (such as name or DOB). If connection to FHIR server 
becomes available, it will be assumed to have ‘correct’ information and the local database 
will be updated to reflect data from the FHIR server.


