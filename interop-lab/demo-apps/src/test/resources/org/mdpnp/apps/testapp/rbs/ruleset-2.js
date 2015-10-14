var System      = java.lang.System;


var create = function (model) {

    model.clear();

    var hr = org.mdpnp.apps.testapp.pca.VitalSign.HeartRate.addToModel(model);
    hr.setCriticalLow(20);
    hr.setWarningLow(30);
    hr.setWarningHigh(60);
    hr.setCriticalHigh(110);
    hr.setRequired(true);

    var spo2 = org.mdpnp.apps.testapp.pca.VitalSign.SpO2.addToModel(model);
    spo2.setCriticalLow(50);
    spo2.setWarningLow(70);
    spo2.setWarningHigh(100);
    spo2.setCriticalHigh(100);
    spo2.setRequired(true);

    var temp = org.mdpnp.apps.testapp.pca.VitalSign.Temperature.addToModel(model);
    temp.setCriticalLow(20.0);
    temp.setWarningLow(35.0);
    temp.setWarningHigh(50.0);
    temp.setCriticalHigh(60.0);
    temp.setRequired(true);

    model.setCountWarningsBecomeAlarm(3);

};

var handleAlarm = function(stateChange) {

    var obj =
    {
        "resourceType":"alarm handler",
        "status": stateChange.state

    };

    return obj;
};
