var System      = java.lang.System;
var VitalSign   = org.mdpnp.apps.testapp.vital.VitalSign;
var State       = org.mdpnp.apps.testapp.vital.VitalModel.State;

var create = function (model) {

    var hr = VitalSign.HeartRate.addToModel(model);
    hr.setRequired(true);
    hr.setModelStateTransitionCondition(State.Normal);
    hr.setCriticalLow(25);
    hr.setWarningLow(30);
    hr.setWarningHigh(150);
    hr.setCriticalHigh(175);

    var spo2 = VitalSign.SpO2.addToModel(model);
    spo2.setRequired(true);
    spo2.setModelStateTransitionCondition(State.Alarm);
    spo2.setCriticalLow(70);
    spo2.setWarningLow(75);
    spo2.setWarningHigh(101);
    spo2.setCriticalHigh(101);

    var ibp = VitalSign.InvSystolic.addToModel(model);
    ibp.setRequired(true);
    ibp.setModelStateTransitionCondition(State.Alarm);
    ibp.setCriticalLow(20);
    ibp.setWarningLow(30);
    ibp.setWarningHigh(200);
    ibp.setCriticalHigh(200);

    var obj =
    {
        "version":1,
        "ruleDescription":"ruleset-pea-info.html"
    };

    return obj;
};

var handleAlarm = function() {

    var obj =
    {
        "statusInformation": "ruleset-pea-fire.html"
    };

    return obj;
};

