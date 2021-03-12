CREATE TABLE InfusionStatus (
	unique_device_identifier varchar(64),
	infusionActive boolean,
	drug_name varchar(128),
	drug_mass_mcg bigint,
	solution_volume_ml bigint,
	volume_to_be_infused_ml bigint,
	infusion_duration_seconds bigint,
	infusion_fraction_complete float
);