var System  = java.lang.System;
var VitalSign = org.mdpnp.apps.testapp.vital.VitalSign;

// Called for each sample to be saved with the following arguments:
// 
// 1. com.mongodb.client.MongoDatabase mongoDatabase
// 2. ice.Patient patient
// 3. org.mdpnp.apps.testapp.export.Value value
// 
// See java object for the description of the APIs
//
var persist = function(mongoDatabase, patient, value) {

    var collection = mongoDatabase.getCollection("datasample_second");
    var document = new org.bson.Document();

    var vital = VitalSign.lookupByMetricId(value.getMetricId());
    if(vital !== null && vital !== "undefined")
        document.put("vital_sign",  vital.name());

    if(patient !== null && patient !== "undefined")
        document.put("patientId",  patient.mrn);

    document.put("deviceId",  value.getUniqueDeviceIdentifier());
    document.put("metricId",  value.getMetricId());
    document.put("timeStamp", value.getDevTime());
    document.put("numeric",   value.getValue());

    collection.insertOne(document);
    
    var ret = {
        "status" : "OK",
        "doc" : document
    };

    return ret;
};