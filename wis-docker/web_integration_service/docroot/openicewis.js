class MapWithEvents extends Map {
	
	deleteListeners=new Array();
	setListeners=new Array();
	
	set(k,v) {
		super.set(k,v);
		this.setListeners.forEach( l => {
			l(k);
		});
	}
	
	addSetListener(f) {
		this.setListeners.push(f);
	}
	
	delete(k) {
		super.delete(k);
		this.deleteListeners.forEach( l => {
			l(k);
		});

	}

	addDeleteListener(f) {
		this.deleteListeners.push(f);
	}
	
}

class ICEAlert {
    constructor(time, udi, ident, text) {
        this.time=time;
        this.udi=udi;
        this.ident=ident;
        this.text=text;
    }
}

var deviceList=new MapWithEvents();
var handleToUDI=new Map();


function updateDeviceList() {
	
	var mimeType="application/dds-web+json";
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/DeviceIdentitySubscriber/data_readers/DeviceIdentityReader?sampleFormat=json&removeFromReaderCache=false&instanceStateMask=ALIVE",
				contentType: mimeType
			}
		).done( function(deviceInfos) {
			//console.log("Done in deviceInfos");
			deviceInfos.forEach(function(deviceInfo, counter) {
				
				//console.log("counter is "+counter+" udi is "+deviceInfo.data.unique_device_identifier);
				
				let valid=deviceInfo.read_sample_info.valid_data;
				if(!valid) return;	//No further processing of this one.
				
				/*
				We might need to deal with instance_handle still being set, but UDI having gone to null,
				and using that as a way of detecting deleted dvices.
				*/
				let instance_handle=deviceInfo.read_sample_info.instance_handle;
				let instance_state=deviceInfo.read_sample_info.instance_state;
				
				if(instance_state=="NOT_ALIVE_NO_WRITERS") {
					//Check and remove a previous instance
					let testUDI=handleToUDI.get(instance_handle);
					if(testUDI!==undefined) {
						if(deviceList.has(testUDI)) {
							//Device was around, but isn't anymore.
							deviceList.delete(testUDI);
						}
					}
				}
				
				
				let udi=deviceInfo.data.unique_device_identifier;
				let manu=deviceInfo.data.manufacturer;
				let model=deviceInfo.data.model;
				
				let testUDI=handleToUDI.get(instance_handle);
				if(testUDI===undefined) {
					//Test here is manu length is > 0 to avoid setting the UDI instance from before the manu etc. was set
					if(manu.length>0) {
						handleToUDI.set(instance_handle,udi);
						deviceList.set(udi,deviceInfo.data);
					}
				}
			});
		}).fail(function( jqXHR, textStatus, errorThrown ) {
			console.log(textStatus);
			console.log(errorThrown);
		});
	
	
	
}

/**
 * Map contains metric_id as a key so that consumers can just look at that primarily.
 * The objects are another map, using the UDI as a key.  That way we can reasonably quickly
 * look up a numeric for a metric and UDI combination.  It's the job of latestNumerics to
 * keep that up to date.
 */
var knownNumerics=new Map();

/**
 * This function parses the returned JSON and puts the returned numerics into knownNumerics.
 * We rely heavily (for now) on the notion that the DDS read command returns the numerics in
 * the order they were produced - so any duplicates in there means that the later one can just
 * replace any earlier one.
 */
function latestNumerics(...callbacks) {
	var mimeType="application/dds-web+json";
	$.ajax(
		{
			url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/NumericSubscriber/data_readers/NumericReader?sampleFormat=json&removeFromReaderCache=true",
			contentType: mimeType
		}
	).done( function(numerics) {
		//console.log("Done in numerics");
		
		numerics.forEach(function(numeric, counter) {
			let metricId=numeric.data.metric_id;
			let udi=numeric.data.unique_device_identifier;
			if(counter<10) {
				//console.log("numeric has "+metricId+" and "+udi);
			}
			if(metricId===undefined || udi===undefined) {
				return;
			}
			if(knownNumerics.has(metricId)) {
				if(counter<10) {
					//console.log("Numeric is already known");
				}
				var udiMap=knownNumerics.get(metricId);
				//We just set the numeric in the map at this point, regardless of whether it was there or not.
				//By doing that, we are relying on the idea that the numerics will appear in time order, and so
				//the current numeric we are processing now is newer than any numeric that was in this map key
				//previously.
				udiMap.set(udi,numeric.data);
			} else {
				if(counter<10) {
					//console.log("Numeric is not known, making new map");
				}
				var udiMap=new Map();
				udiMap.set(udi, numeric.data)
				knownNumerics.set(metricId,udiMap);
			}
			
		});	//End of forEach
		callbacks.forEach( (callback) => {
			//console.log("latestNumerics calling a callback");
			callback();
		});
	}).fail(function( jqXHR, textStatus, errorThrown ) {
		console.log("Failed to retrieve numerics");
	});
}

/**
 * Retrieve the latest numeric for the given UDI and metrics.
 *  


 */
function getLatestNumeric(udi, ...metrics) {
	var returnedNumerics=new Array();
	metrics.forEach( (metric) => {
		if(knownNumerics.has(metric)) {
			var numeric=knownNumerics.get(metric).get(udi);
			if(numeric!==undefined) {
				returnedNumerics.push(numeric);
			}
		}
	});
	
	return returnedNumerics;
	
}

/**
 * Map contains alert identifier as a key so that consumers can just look at that primarily.
 * The objects are another map, using the UDI as a key.  That way we can reasonably quickly
 * look up an alert for an identifier and UDI combination.  It's the job of latestTechAlerts to
 * keep that up to date.
 */
var knownTechAlerts=new Map();
var knownPatientAlerts=new Map();

/**
 * This function parses the returned JSON and puts the returned numerics into knownNumerics.
 * We rely heavily (for now) on the notion that the DDS read command returns the numerics in
 * the order they were produced - so any duplicates in there means that the later one can just
 * replace any earlier one.
 */
function latestTechAlerts(...callbacks) {
	var mimeType="application/dds-web+json";
	$.ajax(
		{
			url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/TechnicalAlertSubscriber/data_readers/TechnicalAlertReader?sampleFormat=json&removeFromReaderCache=false",
			contentType: mimeType
		}
	).done( function(alerts) {
		//console.log("Done in techalerts");
		
		alerts.forEach(function(alert, counter) {
			let identifier=alert.data.identifier;
			let udi=alert.data.unique_device_identifier;
			if(counter<10) {
				//console.log("techalert has "+identifier+" and "+udi);
			}
			if(identifier===undefined || udi===undefined) {
				return;
			}
			if(knownTechAlerts.has(identifier)) {
				if(counter<10) {
					//console.log("TechAlert is already known");
				}
				var udiMap=knownTechAlerts.get(identifier);
				//We just set the numeric in the map at this point, regardless of whether it was there or not.
				//By doing that, we are relying on the idea that the numerics will appear in time order, and so
				//the current numeric we are processing now is newer than any numeric that was in this map key
				//previously.
				udiMap.set(udi,alert);
			} else {
				if(counter<10) {
					//console.log("TechAlert is not known, making new map");
				}
				var udiMap=new Map();
				udiMap.set(udi, alert)
				knownTechAlerts.set(identifier,udiMap);
			}
			
		});	//End of forEach
		callbacks.forEach( (callback) => {
			callback();
		});

	}).fail(function( jqXHR, textStatus, errorThrown ) {
		console.log("Failed to retrieve tech alerts");
		
		
	});
}

function latestPatientAlerts(...callbacks) {
	var mimeType="application/dds-web+json";
	$.ajax(
		{
			url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/PatientAlertSubscriber/data_readers/PatientAlertReader?sampleFormat=json&removeFromReaderCache=false",
			contentType: mimeType
		}
	).done( function(alerts) {
		//console.log("Done in techalerts");
		
		alerts.forEach(function(alert, counter) {
			let identifier=alert.data.identifier;
			let udi=alert.data.unique_device_identifier;
			if(counter<10) {
				//console.log("techalert has "+identifier+" and "+udi);
			}
			if(identifier===undefined || udi===undefined) {
				return;
			}
			if(knownPatientAlerts.has(identifier)) {
				if(counter<10) {
					//console.log("TechAlert is already known");
				}
				var udiMap=knownPatientAlerts.get(identifier);
				//We just set the numeric in the map at this point, regardless of whether it was there or not.
				//By doing that, we are relying on the idea that the numerics will appear in time order, and so
				//the current numeric we are processing now is newer than any numeric that was in this map key
				//previously.
				udiMap.set(udi,alert);
			} else {
				if(counter<10) {
					//console.log("TechAlert is not known, making new map");
				}
				var udiMap=new Map();
				udiMap.set(udi, alert)
				knownPatientAlerts.set(identifier,udiMap);
			}
			
		});	//End of forEach
		callbacks.forEach( (callback) => {
			callback();
		});

	}).fail(function( jqXHR, textStatus, errorThrown ) {
		console.log("Failed to retrieve patient alerts");
		
		
	});
}

/**
 * Retrieve the latest tech alert for the given UDI and identifier.
 * 
 * As a special case, pass in * for identifiers, which causes a search
 * across all identifiers matching the UDI. 
 */
function getLatestTechAlerts(udi, ...identifiers) {
	var returnedTechAlerts=new Array();
	if(identifiers.length==1 && identifiers[0]=="*") {
		//knownTechAlerts.values().next().value.keys().next().value
		for(const [key, val] of knownTechAlerts ) {
			for( const [innerkey, innerval] of val) {
				console.log("Alert inner key is "+innerkey);
				if(innerkey==udi) {
					returnedTechAlerts.push(innerval);
				}
			}
		}
		return returnedTechAlerts;
	}
	
	identifiers.forEach( (identifier) => {
		if(knownTechAlerts.has(identifier)) {
			var techalert=knownTechAlerts.get(identifier).get(udi);
			if(techalert!==undefined) {
				returnedTechAlerts.push(techalert);
			}
		}
	});
	
	return returnedTechAlerts;
	
}

function getLatestPatientAlerts(udi, ...identifiers) {
	var returnedPatientAlerts=new Array();
	if(identifiers.length==1 && identifiers[0]=="*") {
		for(const [key, val] of knownPatientAlerts ) {
			for( const [innerkey, innerval] of val) {
				console.log("Alert inner key is "+innerkey);
				if(innerkey==udi) {
					returnedPatientAlerts.push(innerval);
				}
			}
		}
		return returnedPatientAlerts;
	}
	
	identifiers.forEach( (identifier) => {
		if(knownPatientAlerts.has(identifier)) {
			let patientalert=knownPatientAlerts.get(identifier).get(udi);
			if(patientalert!==undefined) {
				returnedTechAlerts.push(patientalert);
			}
		}
	});
	
	return returnedPatientAlerts;
	
}


function setSpeed(udi, rate) {
  var mimeType="application/dds-web+json";
  var speedObj={
	  unique_device_identifier: udi,
	  requestor: "WebIntegrationService",
	  newFlowRate: rate,
  };
  var theJSON=JSON.stringify(speedObj);
  $.ajax({
	  type: "POST",
	  url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/publishers/FlowRatePublisher/data_writers/FlowRateObjectiveWriter",
	  data: theJSON,
	  contentType: mimeType
  })
  .done( function(response) {
	  console.log(response);
  }).fail(function( jqXHR, textStatus, errorThrown ) {
	console.log("Could not POST speed request");
	console.log(textStatus);
	console.log(errorThrown);

  });


}

/*
struct InfusionProgram {
  	UniqueDeviceIdentifier unique_device_identifier; //@key
  	LongString requestor;
  	long head;	//Which head/pump etc. we are requesting to change.  Can't use int
  	float infusionRate;	//Infusion rate
  	float VTBI;			//Volume to be infused
  	float bolusVolume;	//Bolus volume
  	float bolusRate;	//Bolus Infusion Rate.
  	long seconds;		//Bolus duration...
  };
*/
function programPump(udi, head = 1, infusionRate = -1, vtbi = -1, bolusVolume = -1, bolusRate = -1, bolusDuration = -1) {
  var mimeType="application/dds-web+json";
  var programObj={
	  unique_device_identifier: udi,
	  requestor: "WebIntegrationService",
      head: head,
      infusionRate: infusionRate,
      VTBI: vtbi,
      bolusVolume: bolusVolume,
      bolusRate: bolusRate,
      seconds: bolusDuration
  };
  var theJSON=JSON.stringify(programObj);
  $.ajax({
	  type: "POST",
	  url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/publishers/InfusionProgramPublisher/data_writers/InfusionProgramWriter",
	  data: theJSON,
	  contentType: mimeType
  })
  .done( function(response) {
	  console.log(response);
  }).fail(function( jqXHR, textStatus, errorThrown ) {
	console.log("Could not POST infusion program");
	console.log(textStatus);
	console.log(errorThrown);

  });
}

function requestLatest550Settings(udi) {
  var mimeType="application/dds-web+json";
  var speedObj={
	  unique_device_identifier: udi,
	  requestor: "WebIntegrationService"
  };
  var theJSON=JSON.stringify(speedObj);
  $.ajax({
	  type: "POST",
	  url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/publishers/NKVSettingsPublisher/data_writers/NKVSettingsWriter",
	  data: theJSON,
	  contentType: mimeType
  })
  .done( function(response) {
	  console.log(response);
  }).fail(function( jqXHR, textStatus, errorThrown ) {
	console.log("Could not POST 550 settings request");
	console.log(textStatus);
	console.log(errorThrown);

  });


}

function set550KeyValue(udi,k,v) {
  var mimeType="application/dds-web+json";
  var speedObj={
	  unique_device_identifier: udi,
	  requestor: "WebIntegrationService",
      paramName: k,
      newValue: v
  };
  var theJSON=JSON.stringify(speedObj);
  $.ajax({
	  type: "POST",
	  url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/publishers/NKVKeyValuePublisher/data_writers/NKVKeyValueWriter",
	  data: theJSON,
	  contentType: mimeType
  })
  .done( function(response) {
	  console.log(response);
  }).fail(function( jqXHR, textStatus, errorThrown ) {
	console.log("Could not POST 550 settings request");
	console.log(textStatus);
	console.log(errorThrown);

  });


}

/**
Because this is called from an onChange event, we can't pass parameters, so we have to get
the values from the HTML.
*/
function setNewVentMode() {
	let mode=$("#opModeButton").val();
	let mimeType="application/dds-web+json";
	let modeObj={
	 unique_device_identifier: VENT_DEVICE,
	 requestor: "WebIntegrationService",
     newMode: mode
	};
	let theJSON=JSON.stringify(modeObj);
	$.ajax({
	 type: "POST",
	 url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/publishers/NKVModePublisher/data_writers/NKVModeWriter",
	 data: theJSON,
	 contentType: mimeType
	})
	.done( function(response) {
	  console.log(response);
	}).fail(function( jqXHR, textStatus, errorThrown ) {
	  console.log("Could not POST mode request");
	  console.log(textStatus);
	  console.log(errorThrown);
    });

}



/**
 * Map contains metric_id as a key so that consumers can just look at that primarily.
 * The objects are another map, using the UDI as a key.  That way we can reasonably quickly
 * look up a sample for a metric and UDI combination.  It's the job of latestSamples to
 * keep that up to date.
 */
var knownSamples=new Map();

/**
 * This function parses the returned JSON and puts the returned numerics into knownNumerics.
 * We rely heavily (for now) on the notion that the DDS read command returns the numerics in
 * the order they were produced - so any duplicates in there means that the later one can just
 * replace any earlier one.
 */
function latestSamples(...callbacks) {
	var mimeType="application/dds-web+json";
	$.ajax(
		{
			url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/SampleArraySubscriber/data_readers/SampleArrayReader?sampleFormat=json&removeFromReaderCache=true",
			contentType: mimeType
		}
	).done( function(samples) {
		//console.log("Done in latestSamples");
		let newSampleCount=0;
		
	    samples.forEach(function(sample, counter) {
			let metricId=sample.data.metric_id;
			let udi=sample.data.unique_device_identifier;
			if(counter<10) {
				//console.log("numeric has "+metricId+" and "+udi);
			}
			if(metricId===undefined || udi===undefined) {
				return;
			}
			if(knownSamples.has(metricId)) {
				if(counter<10) {
					//console.log("Numeric is already known");
				}
				var udiMap=knownSamples.get(metricId);
				
				if(udiMap.has(udi)) {
					//Already something with this key
					let existing=udiMap.get(udi);
					existing.push(sample.data);
				} else {
					let a=new Array();
					a.push(sample.data);
					udiMap.set(udi,a);
				}
				
				
				//We just set the sample in the map at this point, regardless of whether it was there or not.
				//By doing that, we are relying on the idea that the samples will appear in time order, and so
				//the current sample we are processing now is newer than any numeric that was in this map key
				//previously.
				//udiMap.set(udi,sample.data);
				
			} else {
				if(counter<10) {
					//console.log("Numeric is not known, making new map");
				}
				var udiMap=new Map();
				let a=new Array();
				a.push(sample.data);
				udiMap.set(udi, a)
				knownSamples.set(metricId,udiMap);
			}
			newSampleCount++;
		});	//End of forEach
		//console.log("latestSamples got "+newSampleCount+" new samples");
		callbacks.forEach( (callback) => {
			//console.log("latestSamples calling a callback");
			callback();
		});
	}).fail(function( jqXHR, textStatus, errorThrown ) {
		console.log("Failed to retrieve samples]");
		
		
	});
}

/**
 * Retrieve the latest numeric for the given UDI and metrics.
 *  


 */
function getLatestSamples(udi, ...metrics) {
	var returnedSamples=new Array();
	metrics.forEach( (metric) => {
		if(knownSamples.has(metric)) {
			var sample=knownSamples.get(metric).get(udi);
			if(sample!==undefined) {
				returnedSamples.push(sample);
			}
		}
	});
	
	return returnedSamples;
	
}
