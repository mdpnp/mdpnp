CREATE TABLE LocalAlarmLimitObjective (
	unique_device_identifier varchar(64),
	metric_id varchar(64),
	limit_type LimitType,
	unit_identifier varchar(64),
	value float
);