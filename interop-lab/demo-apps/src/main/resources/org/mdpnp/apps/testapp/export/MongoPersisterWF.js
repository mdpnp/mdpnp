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
    if(patient === null || patient === "undefined" || patient == 'UNDEFINED')
        return { "status" : "unresolved patient"};

    var collection = mongoDatabase.getCollection("datasample_second");

    var filter = new org.bson.Document();
    var seconds = Math.floor(value.getDevTime()/1000);
    filter.put("timeStamp", seconds);
    filter.put("patientID", patient);

    var vs = lookupVitalByMDC(value.getMetricId());
    if(vs === null || vs === "undefined")
        return { "status" : "unresolved " + value.getMetricId()};
    
    var values = new org.bson.Document();
    values.put(vs + ".sum",   value.getValue());
    values.put(vs + ".count", 1);

    var document = new org.bson.Document();
    document.put("$inc",  values);

    collection.updateOne(filter, document, UpdateOptions);

    var ret = {
        "status" : "OK",
        "doc" : document
    };

    return ret;
};

var lookupVitalByMetricId = function(metricId) {

    var vs = VitalSign.lookupByMetricId(metricId);
    if(vs === null || vs === "undefined")
        return null;
    else
        return vs.name();
};


var MDCLOOKUP = {};
MDCLOOKUP['MDC_TEMP_BLD']            = 'Temperature';
MDCLOOKUP['MDC_PRESS_CUFF_SYS']      = 'NIBPSystolic';
MDCLOOKUP['MDC_PRESS_CUFF_DIA']      = 'NIBPDiastolic';
MDCLOOKUP['MDC_ECG_HEART_RATE']      = 'ECGHeartRate';
MDCLOOKUP['MDC_PULS_OXIM_SAT_O2']    = 'SpO2';
MDCLOOKUP['MDC_PULS_OXIM_PULS_RATE'] = 'SpO2PulseRate';

var lookupVitalByMDC = function(metricId) {

    var vs = MDCLOOKUP[metricId];
    if(vs === null || vs === "undefined")
        return null;
    else
        return vs;
};
