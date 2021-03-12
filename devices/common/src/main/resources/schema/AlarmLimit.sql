CREATE TABLE AlarmLimit (
	unique_device_identifier varchar(64),
	metric_id varchar(64),
	limit_type varchar(64),
	unit_identifier varchar(64),
	value float
);