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
    hr.setModelStateTransitionCondition(State.Alarm);

    var spo2 = VitalSign.SpO2.addToModel(model);
    spo2.setCriticalLow(50);
    spo2.setWarningLow(70);
    spo2.setWarningHigh(100);
    spo2.setCriticalHigh(100);
    spo2.setRequired(true);
    spo2.setModelStateTransitionCondition(State.Alarm);

    var temperature = VitalSign.Temperature.addToModel(model);
    temperature.setRequired(true);
    temperature.setModelStateTransitionCondition(State.Normal);

    var obj =
    {
        "version":1,
        "ruleDescription":"ruleset-1-info.html"
    };

    return obj;
};

var handleAlarm = function() {

    var obj =
    {
        "statusInformation": "ruleset-1-fire.html"
    };

    return obj;
};

