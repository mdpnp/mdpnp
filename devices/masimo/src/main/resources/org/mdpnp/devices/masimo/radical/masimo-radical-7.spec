^(.{17})\s+SN\=(\S+)\s+SPO2\=(\S+)\%\s+BPM\=(\S+)\s+PI\=(\S+)\%\s+SPCO\=(\S+)\%\s+SPMET\=(\S+)\%\s+DESAT\=(\S+)\s+PIDELTA\=(\S+)\s+PVI\=(\S+)\s+ALARM\=\S+\s+EXC\=\S+\s*$
	firePulseOximeter
	lastPoint
		MM/dd/yy HH:mm:ss
	guid
	spo2	filter
	heartRate	filter
	perfusionIndex	filter
	spco	filter
	spmet	filter
	pidelta	filter
	pvi	filter