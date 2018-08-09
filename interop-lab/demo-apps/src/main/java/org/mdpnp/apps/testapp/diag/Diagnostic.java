package org.mdpnp.apps.testapp.diag;

import javafx.collections.ObservableList;

import org.mdpnp.apps.fxbeans.AlarmLimitFx;
import org.mdpnp.apps.fxbeans.AlarmLimitFxList;
import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.BypassStatusFxList;
import org.mdpnp.apps.fxbeans.GlobalAlarmLimitObjectiveFx;
import org.mdpnp.apps.fxbeans.GlobalAlarmLimitObjectiveFxList;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.LocalAlarmLimitObjectiveFx;
import org.mdpnp.apps.fxbeans.LocalAlarmLimitObjectiveFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.validate.ValidationOracle;

public class Diagnostic {
	private final NumericFxList numerics;
	private final AlertFxList patientAlerts, technicalAlerts;
	private final SampleArrayFxList sampleArrays;
	private final ValidationOracle validationOracle;
	private final InfusionStatusFxList infusionStatusList;
	private final BypassStatusFxList bypassStatusList;
	private final AlarmLimitFxList alarmLimits;
	private final LocalAlarmLimitObjectiveFxList localAlarmLimitObjectives;
	private final GlobalAlarmLimitObjectiveFxList globalAlarmLimitObjectives;

	public Diagnostic(AlertFxList patientAlerts, AlertFxList technicalAlerts, NumericFxList numerics,
			SampleArrayFxList sampleArrayList, ValidationOracle validationOracle,
			InfusionStatusFxList infusionStatusList, BypassStatusFxList bypassStatusList, AlarmLimitFxList alarmLimits,
			LocalAlarmLimitObjectiveFxList localAlarmLimitObjectives,
			GlobalAlarmLimitObjectiveFxList globalAlarmLimitObjectives) {
		this.numerics = numerics;
		this.sampleArrays = sampleArrayList;
		this.patientAlerts = patientAlerts;
		this.technicalAlerts = technicalAlerts;
		this.validationOracle = validationOracle;
		this.infusionStatusList = infusionStatusList;
		this.bypassStatusList = bypassStatusList;
		this.alarmLimits = alarmLimits;
		this.localAlarmLimitObjectives = localAlarmLimitObjectives;
		this.globalAlarmLimitObjectives = globalAlarmLimitObjectives;
	}

	public ObservableList<NumericFx> getNumericModel() {
		return numerics;
	}

	public ObservableList<AlertFx> getPatientAlertModel() {
		return patientAlerts;
	}

	public ObservableList<AlertFx> getTechnicalAlertModel() {
		return technicalAlerts;
	}

	public ObservableList<SampleArrayFx> getSampleArrayModel() {
		return sampleArrays;
	}

	public ValidationOracle getValidationOracle() {
		return validationOracle;
	}

	public InfusionStatusFxList getInfusionStatusList() {
		return infusionStatusList;
	}

	public BypassStatusFxList getBypassStatusList() {
		return bypassStatusList;
	}

	public ObservableList<AlarmLimitFx> getAlarmLimitModel() {
		return alarmLimits;
	}

	public ObservableList<GlobalAlarmLimitObjectiveFx> getGlobalAlarmLimitObjectiveModel() {
		return globalAlarmLimitObjectives;
	}

	public ObservableList<LocalAlarmLimitObjectiveFx> getLocalAlarmLimitObjectiveModel() {
		return localAlarmLimitObjectives;
	}

}
