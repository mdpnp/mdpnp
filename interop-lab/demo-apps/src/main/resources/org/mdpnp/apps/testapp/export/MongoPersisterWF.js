var System  = java.lang.System;
var VitalSign = org.mdpnp.apps.testapp.vital.VitalSign;

var UpdateOptions = (new com.mongodb.client.model.UpdateOptions()).upsert(true);

// This is a persister script to save data in the warfighter's 'tiles' demo
// database.
//
//
// Called for each sample to be saved with the following arguments:
// 
// 1. com.mongodb.client.MongoDatabase mongoDatabase
// 2. org.mdpnp.apps.testapp.export.NumericsDataCollector.NumericSampleEvent value
// 
// See java object for the description of the APIs
//

var persistNumeric = function(mongoDatabase, value) {

    var patient = value.getPatientId();
    if(patient === null && patient === "undefined")
        return { "status" : "unresolved patient"};

    var collection = mongoDatabase.getCollection("datasample_second");

    var filter = new org.bson.Document();
    var seconds = Math.floor(value.getDevTime()/1000);
    filter.put("timeStamp", seconds);
    filter.put("patientID", patient);

    var vs = VitalSign.lookupByMetricId(value.getMetricId());

    if(vs === null || vs === "undefined")
        return { "status" : "unresolved " + value.getMetricId()};
    
    var values = new org.bson.Document();
    values.put(vs.name() + ".sum",   value.getValue());
    values.put(vs.name() + ".count", 1);

    var document = new org.bson.Document();
    document.put("$inc",  values);

    collection.updateOne(filter, document, UpdateOptions);

    var ret = {
        "status" : "OK",
        "doc" : document
    };

    return ret;
};

