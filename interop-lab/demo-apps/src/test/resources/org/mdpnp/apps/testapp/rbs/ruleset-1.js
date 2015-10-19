var System      = java.lang.System;
var VitalSign   = org.mdpnp.apps.testapp.vital.VitalSign;
var State       = org.mdpnp.apps.testapp.vital.VitalModel.State;

var create = function (model) {

    var hr = VitalSign.HeartRate.addToModel(model);
    hr.setCriticalLow(20);
    hr.setWarningLow(30);
    hr.setWarningHigh(60);
    hr.setCriticalHigh(110);
    hr.setRequired(true);

    var spo2 = VitalSign.SpO2.addToModel(model);
    spo2.setCriticalLow(50);
    spo2.setWarningLow(70);
    spo2.setWarningHigh(100);
    spo2.setCriticalHigh(100);
    spo2.setRequired(true);

    var temperature = VitalSign.Temperature.addToModel(model);
    temperature.setRequired(true);

    var obj =
    {
        "version":1,
        "ruleDescription":"ruleset-1-info.html"
    };

    return obj;
};

var evaluate = function(advisories) {

    var hr =          advisories.get(VitalSign.HeartRate);
    var spo2 =        advisories.get(VitalSign.SpO2);
    var temperature = advisories.get(VitalSign.Temperature);

    if(temperature == null &&
        (hr   != null && hr.state == State.Alarm) &&
        (spo2 != null && spo2.state == State.Alarm)) {
        return State.Alarm;
    } else {
        return State.Normal;
    }
};

var handleAlarm = function() {

    var obj =
    {
        "statusInformation": "ruleset-1-fire.html"
    };

    return obj;
};

