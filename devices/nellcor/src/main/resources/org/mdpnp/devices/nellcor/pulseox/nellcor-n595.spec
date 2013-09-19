^(\S+)\s+VERSION\s+([0-9.]+)\s+INFO\s+CRC\:(\S+)\s+SEC\:([0-9 /]+)\s*$
	fireDevice
	setName	applyRoot
	version
	crc
	setGuid
^(.{18})\s+([0-9-*]+)\s+([0-9-*]+)\s+([0-9-*]+)(?:\s+([A-Z]{2}))*\s*$
	firePulseOximeter
	lastPoint
		dd-MMM-yy HH:mm:ss
	spo2	filterStar
	heartRate	filterStar
	pulseAmplitude	filterStar
	status
		AO	AlarmOff
		AS	AlarmSilence
		LB	LowBattery
		LM	LossOfPulseInterference
		LP	LossOfPulse
		MO	InterferenceDetected
		PH	PulseRateUpperLimitAlarm
		PL	PulseRateLowerLimitAlarm
		PS	PulseSearch
		SH	SaturationUpperLimitAlarm
		SL	SaturationLowerLimitAlarm
		SD	SensorDisconnect
		SO	SensorOff
^(\S+)\s+VERSION ([0-9.]+)\s+CRC\:(\S+)\s+SpO2 Limit: ?([0-9]+)\- ?([0-9]+)(\S+)\s+PR Limit: ?([0-9]+)\- ?([0-9]+)(\S+).*$
	fireAlarmPulseOximeter
	setName	applyRoot
	version
	crc
	spO2Lower
	spO2Upper	
	spO2Units
	pRLower
	pRUpper
	pRUnits
^\s+(\w+)\s+(\d+)SAT\-S\s+SPO2 RESP MODE: (\w+).*$
	fireAlarmPulseOximeter
	limitsType
		ADULT	Adult
		NEO	NeoNatal
	satS
	spO2RespMode
		NORMAL	Normal
		FAST	Fast
^\s*TIME\s+%SPO2\s+BPM\s+PA\s+Status\s*$
^$
^    Command\?$
^NPB-595   Interactive Mode$
^    1\) Dump Instrument Info$
^    2\) Set Date and Time$
^    3\) Dump Trend$
^    4\) Dump Error Log$
^    0\) Exit Interactive Mode$