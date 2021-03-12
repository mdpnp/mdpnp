CREATE TABLE DeviceIdentity (
	unique_device_identifier varchar(64),
	manufacturer varchar(128),
	model varchar(128),
	serial_number varchar(128),
	icon_content_type varchar(64),
	icon_image blob,
	build varchar(128),
	operating_system varchar(128)
);