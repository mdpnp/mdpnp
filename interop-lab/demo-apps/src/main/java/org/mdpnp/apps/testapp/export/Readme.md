

## ICE Data Export Application
 
# General architecture notes

In the center of the application there is a DataCollectorApp object. It works as a mux connecting available 
DataCollectors and PersisterUIControllers. The components are tied together using quavas EventBus. 
This design provides a flexible approach to the implementation of data writers – a particular writer 
can support a subset of data types that are being published by data collectors. The list of available 
DataCollectiors and Persistent controllers is defined in the factory and passed to the DataCollectorApp 
during the setup phase.

The responsibility of DataCollector is to listen to DDS/JavaFx data traffic, intercept it and convert to data 
events suitable for consumption by the writers. The following data collectors are available:

* NumericsDataCollector 
* SampleArrayDataCollector 
* PatientAssessmentDataCollector


Available data writers and their support of various data types is listed below:

|                   | Numerics |Arrays|Observations|
|-------------------|:--------:|:----:|-----------:|
|CSVPersister       | x        | x    | x          |     
|JdbcPersister      | x        | x    | x          |
|VerilogVCDPersister| x        |      |            |
|MongoPersister     | x        |      |            |



#CSVPersister

All data is dumped in the same CSV file in the following format:

```
TYPE,DEVICE_ID,METRIC_ID,INSTANCE_ID,DATE,MRN,N,V-0,V-1,... V-N
```

where:
* TYPE: integer  1 - numerics; 2 - array; 3 observation
* DATE: yyyyMMddHHmmssZ format
* N: integer - number of values to follow

#JdbcPersister

Numerics and Arrays are stored in the same table. Arrays are broken up into individual 
numeric samples. Observations are stored separately. See DbSchema.sql for schema 
definition.








