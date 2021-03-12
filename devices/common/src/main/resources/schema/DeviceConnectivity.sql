CREATE TABLE DeviceConnectivity (
	unique_device_identifier varchar(64),
	state ConnectionState,
	type ConnectionType,
	info varchar(128),
	valid_targets blob,
	comPort varchar(16)
);