var System  = java.lang.System;


var persist = function(mongoDatabase, patient, value) {

    var collection = mongoDatabase.getCollection("datasample_second");
    var document = new org.bson.Document();

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

