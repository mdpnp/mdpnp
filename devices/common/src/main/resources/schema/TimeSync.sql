CREATE TABLE TimeSync (
	heartbeat_source varchar(64),
	heartbeat_recipient varchar(64),
	source_source_timestamp_sec bigint,
	source_source_timestamp_nanosec bigint,
	recipient_receipt_timestamp_sec bigint,
	recipient_receipt_timestamp_nanosec bigint
);