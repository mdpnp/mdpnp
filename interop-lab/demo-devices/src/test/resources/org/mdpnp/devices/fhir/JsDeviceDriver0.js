var System      = java.lang.System;
var LibC        = org.mdpnp.devices.fhir.FhirDeviceTest$LibC;
var IdentityBld = org.mdpnp.devices.DeviceIdentityBuilder;

var start = function (arg) {
    __dataProvider.start();
};

var stop = function () {
    __dataProvider.close();
};

var getDeviceId = function() {

    var obj =
    {
        "unique_device_identifier": IdentityBld.randomUDI(),
        "manufacturer": "mdpnp",
        "model": "embedded-js",
        "serial_number": __dataProvider.getName()
    };

    return obj;
};

var getSampleRateMs = function() {
    return 1000;
};

//
// "Simulated" |  "Network" | "Serial"
//
var getConnectionType = function() {
    return "Simulated";
};

var readObservations = function() {

    var deviceTime = new Date();
    var d = deviceTime.toUTCString();

    var arr = [
        __makeObservation('MDC_AWAY_CO2_ET',    __dataProvider.getReading(), d),
        __makeObservation('MDC_ECG_HEART_RATE', __dataProvider.getReading(), d)
    ];
    return arr;
};

var __dataProvider = {

    start : function() {},
    getName : function()    { return "Random Data Provider"; },
    getReading : function() { return LibC.time(null) * Math.random(); },
    close : function() {}

};

var __makeObservation = function(code, value, dt) {

    var obj =
    {
        "resourceType":"Observation",
        "valueQuantity":{
            "value":value,
            "units":"MDC_DIM_DIMLESS",
            "system":"OpenICE",
            "code": code
        },
        "appliesDateTime": dt,
        "status":"preliminary",
        "subject":{
            "reference":"Patient/1"
        }
    };

    return obj;
};
