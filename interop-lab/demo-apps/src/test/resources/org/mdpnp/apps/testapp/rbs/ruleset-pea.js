var System      = java.lang.System;
var VitalSign   = org.mdpnp.apps.testapp.vital.VitalSign;
var State       = org.mdpnp.apps.testapp.vital.VitalModel.State;

var create = function (model) {

    var hr = VitalSign.ECGHeartRate.addToModel(model);
    hr.setRequired(true);
    hr.setModelStateTransitionCondition(State.Normal);
    hr.setCriticalLow(25);
    hr.setWarningLow(40);
    hr.setWarningHigh(150);
    hr.setCriticalHigh(175);

    var spo2 = VitalSign.SpO2.addToModel(model);
    spo2.setRequired(true);
    spo2.setModelStateTransitionCondition(State.Alarm);
    spo2.setCriticalLow(80);
    spo2.setWarningLow(80);
    spo2.setWarningHigh(100);
    spo2.setCriticalHigh(100);

    var spo2pr = VitalSign.SpO2PulseRate.addToModel(model);
    spo2pr.setRequired(true);
    spo2pr.setModelStateTransitionCondition(State.Alarm);
    spo2pr.setCriticalLow(50);
    spo2pr.setWarningLow(50);
    spo2pr.setWarningHigh(200);
    spo2pr.setCriticalHigh(200);

    var ibp = VitalSign.InvSystolic.addToModel(model);
    ibp.setRequired(true);
    ibp.setModelStateTransitionCondition(State.Alarm);
    ibp.setCriticalLow(30);
    ibp.setWarningLow(30);
    ibp.setWarningHigh(180);
    ibp.setCriticalHigh(200);

    var etco2 = VitalSign.EndTidalCO2.addToModel(model);
    etco2.setRequired(true);
    etco2.setModelStateTransitionCondition(State.Alarm);
    etco2.setCriticalLow(15);
    etco2.setWarningLow(15);
    etco2.setWarningHigh(100);
    etco2.setCriticalHigh(120);

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

