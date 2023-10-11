var metricToControlMap=new Map();
var metricToPumpControlMap=new Map();
var metricToVentControlMap=new Map();
var techAlertsToVentControlMap=new Map();
var VITALS_DEVICE=null;

const VITALS_MANU=new Array();
const VITALS_MODELS=new Array();

const ventSettingsMap=new Map();
var ventAlertsForTableMap=new Map();
var settingIdToButtonMap=new Map();

var vitalsKeysAsArray;
var pumpKeysAsArray;
var ventKeysAsArray;
var ventTechAlertsKeysAsArray;
var ventSettingsKeysAsArray;
var ventAlertsForTableKeysAsArray;

var hrChart;
var mapChart;
var rrChart;
var flowChart;
var pressureChart;
var volumeChart;

var udiToMakeAndModel=new Map();

var udiToDevice=new Map();

var opModeNames=Array();

var currentSys;
var currentDia;

var lastOpMode=-1;

//A counter to make sure we don't ask for current settings too often.
var settingsCounter=0;


function initPage() {
    initDeviceArrays();
	initControlMappings();
    updateDeviceList();
    findDevices();
    createCharts();
    populateVitalsTable();
    startVitalSignsMonitoring();
}

function initDeviceArrays() {
    VITALS_MANU.push("Philips");
    VITALS_MODELS.push("MX800");
    VITALS_MODELS.push("Intellivue Device");

	VITALS_MANU.push("ICE");
    VITALS_MODELS.push("Multiparameter (Simulated)");
    
	//deviceIdentity.manufacturer="ICE";
    //    deviceIdentity.model = "Multiparameter (Simulated)";
}



class MetricData {
	constructor(label, unit) {
		this.label=label;
		this.unit=unit;
	}
}

function initControlMappings() {
    metricToControlMap.set("MDC_PULS_OXIM_SAT_O2","spo2_cell");
    metricToControlMap.set("MDC_CO2_RESP_RATE","rr_cell");
    metricToControlMap.set("MDC_ECG_HEART_RATE","hr_cell");
	metricToControlMap.set("MDC_PRESS_BLD_ART_ABP_SYS","sys_cell");
	metricToControlMap.set("MDC_PRESS_BLD_ART_ABP_DIA","dia_cell");
	
    /*
	pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "");
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "");
        
        respiratoryRate = createNumericInstance(rosetta.MDC_CO2_RESP_RATE.VALUE, "");
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_ET.VALUE, "");

        ecgRespiratoryRate = createNumericInstance(rosetta.MDC_TTHOR_RESP_RATE.VALUE, "");
        heartRate = createNumericInstance(rosetta.MDC_ECG_HEART_RATE.VALUE, "");
        
        systolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, "");
        diastolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, "");
	*/


    vitalsKeysAsArray=Array.from(metricToControlMap.keys());
       
}


function findDevices() {
    //Why on earth does Map.forEach have the value first then the key?
    deviceList.forEach( (device,udi) => {
	    //Why have we never used a map to whole device before?
		udiToDevice.set(udi, device);
        //console.log("dev is "+device.manufacturer+" "+device.model);
        if(  VITALS_MANU.includes(device.manufacturer) && VITALS_MODELS.includes(device.model) ) {
            VITALS_DEVICE=udi;
            //console.log("Found "+device.model+" with udi "+udi);
            let deviceName=device.manufacturer+" "+device.model;
            document.getElementById("monitor_device").value=deviceName;
			udiToMakeAndModel.set(udi,device.model);
        }
		
        


    });
    
    if(VITALS_DEVICE==null) {
        //console.log("Did not find v device");
		updateDeviceList();
        setTimeout(findDevices,5000);
    }
}

function startVitalSignsMonitoring() {
	runNumericsLoop();
}

function runNumericsLoop() {
	latestNumerics(getVitalsData);
	setTimeout(runNumericsLoop, 1000);
}

function getVitalsData() {
	if(VITALS_DEVICE!=null) {
        let latestVitals=getLatestNumeric(VITALS_DEVICE,...vitalsKeysAsArray);
        latestVitals.forEach( (vital) => {
            let targetField=metricToControlMap.get(vital.metric_id);
            if(targetField!=null) {
                var updateThis=document.getElementById(targetField);
                updateThis.innerText=vital.value.toFixed(0);
            }
            //Later, we need another mapping for graphs
            if(vital.metric_id=='MDC_ECG_HEART_RATE') {
                var presentationTS=new Date(vital.presentation_time.sec*1000);
                hrChart.data.labels.push(presentationTS);
                hrChart.data.datasets[0].data.push(vital.value);
                hrChart.update();
            }
            if(vital.metric_id=='MDC_CO2_RESP_RATE') {
                var presentationTS=new Date(vital.presentation_time.sec*1000);
                rrChart.data.labels.push(presentationTS);
                rrChart.data.datasets[0].data.push(vital.value);
                rrChart.update();
            }
			if(vital.metric_id=='MDC_PRESS_BLD_NONINV_MEAN') {
                var presentationTS=new Date(vital.presentation_time.sec*1000);
                mapChart.data.labels.push(presentationTS);
                mapChart.data.datasets[0].data.push(vital.value);
                mapChart.update();
            }
        });
		//After processing, calculate the mean.
		/*
		let samples=getLatestSamples(VITALS_DEVICE,"MDC_PRESS_BLD_ART_ABP");
		let bpSource=undefined;
		let outerArray=samples[0];
		if(outerArray.length>0) {
			let outerCount=outerArray.length-1;
			bpSource=outerArray[outerCount];
		}
		//let bpSource=samples[0][0];
		if(bpSource!=undefined) {
			getSysDia(bpSource);
		}
		*/
    } else {
        //console.log("getVitalsData doesn't have VITALS_DEVICE yet");
    }
}

function setFlowRate() {
    let speed=$("#target_infusion_rate").val();
    setSpeed(PUMP_DEVICE, speed);
    alert("Submitted flow rate request");
    
    
}

function createCharts() {
    const hr_ctx=document.getElementById('hr_graph_canvas').getContext('2d');
    hrChart=new Chart(hr_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Heart Rate',
               data: [],
               fill: false,
               borderColor: 'rgb(75, 192, 192)',
               tension: 0.1
           }]
       },
       options: {
           scales: {
               x: {
                 type: 'time',  
               },
               y: {
                  title: {
                      display: true,
                      text: "Heart Rate"
                  } 
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   }
       }
    });
	
	//map_graph_canvas
	const map_ctx=document.getElementById('map_graph_canvas').getContext('2d');
    mapChart=new Chart(map_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Mean Arterial Pressure',
               data: [],
               fill: false,
               borderColor: 'rgb(192,75, 192)',
               tension: 0.1
           }]
       },
       options: {
           scales: {
               x: {
                 type: 'time',  
               },
               y: {
                  title: {
                      display: true,
                      text: "MAP"
                  } 
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   }
       }
    });
	
    const rr_ctx=document.getElementById('rr_graph_canvas').getContext('2d');
    rrChart=new Chart(rr_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Respiratory Rate',
               data: [],
               fill: false,
               borderColor: 'rgb(192,75, 192)',
               tension: 0.1
           }]
       },
       options: {
           scales: {
               x: {
                 type: 'time',  
               },
               y: {
                  title: {
                      display: true,
                      text: "Resp. Rate"
                  } 
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   }
       }
    });
	
	

    
}

function populateVentTable() {
    var ventTableRows = [
        [ "O\u2082" , "vent_o2_cell", "%"],
        [ "PEEP","vent_peep_cell", "cmH\u2082O" ],
        [ "Ppeak", "vent_peak_cell", "cmH\u2082O"],
        [ "RR", "vent_rr_cell", "bpm"],
        [ "VT", "vent_vt_cell", "L"],
        [ "I:E", "vent_ie_cell", ""]
    ];
    
    $("#vent_table > tbody").empty();   //Just in case.
/*
    for(let i=0;i<ventTableRows.length;i++) {
        let rowHTML="<tr><td>"+ventTableRows[i][0]+"</td><td class=\"numeric\" id=\""+ventTableRows[i][1]+"\"></td><td>"+ventTableRows[i][2]+"</td>";
        $("#vent_table > tbody").append(rowHTML);
    }
*/
    
}

function populateVitalsTable() {
    var vitalsTableRows = [
		[ "SYS", "sys_cell", "mmHg"],
		[ "DIA", "dia_cell", "mmHg"],
		[ "MAP", "map_cell", "mmHg"],
        [ "SpO\u2082" , "spo2_cell", "%"],
        [ "RR", "rr_cell", "bpm"],
        [ "HR", "hr_cell", "bpm"],
		
    ];
    
    $("#vitals_table > tbody").empty();   //Just in case.
    for(let i=0;i<vitalsTableRows.length;i++) {
	    let floatVal=parseFloat()
        let rowHTML="<tr><td>"+vitalsTableRows[i][0]+"</td><td class=\"numeric\" id=\""+vitalsTableRows[i][1]+"\"></td><td>"+vitalsTableRows[i][2]+"</td>";
        $("#vitals_table > tbody").append(rowHTML);
    }
}

function clearFlowRate() {
	$("#target_infusion_rate").val("");
}

function getSysDia(bpSource) {
	let values=bpSource.values;
	let newSys=values[0];
	let newDia=values[0];
	for(let i=1;i<values.length;i++) {
		if(values[i]>newSys) {
			newSys=values[i];
		}
		if(values[i]<newDia) {
			newDia=values[i];
		}
	}
	currentSys=newSys;
	currentDia=newDia;
	dia_cell.innerText=currentDia.toFixed(0);
	sys_cell.innerText=currentSys.toFixed(0);
	//MAP = DP + 1/3(SP - DP) 
	let mean=currentDia+ ( (currentSys-currentDia) / 3 );
	map_cell.innerText=mean.toFixed(0);
	let presentation_time=bpSource.presentation_time.sec*1000;
	var presentationTS=new Date(presentation_time);
	mapChart.data.labels.push(presentationTS);
	mapChart.data.datasets[0].data.push(mean.toFixed(0));
	mapChart.update();
}

function setParam() {
	let targetButton=event.target;
	let whichParam=targetButton.getAttribute("data-basetext");
	let val=prompt("Enter new value for "+whichParam);
	let keyToSet=targetButton.getAttribute("data-metricid");
	set550KeyValue(VENT_DEVICE, keyToSet, val);
	console.log("return val was "+val);
	
	
	
}



function getPatientInfo() {
	/* AP-4000 publishes the following
		writePatientAlert("PT_WEIGHT",weight);
		writePatientAlert("PT_HEIGHT",height);
		writePatientAlert("PT_AGE",age);
		writePatientAlert("PT_GENDER",gender);
		writePatientAlert("PT_STATE",anState);
		*/
	let testElement=null;
	let possWeightAlert=knownPatientAlerts.get("PT_WEIGHT");	//Points to a map by UDI
	if(possWeightAlert!=null) {
		let weightAlert=possWeightAlert.get(PUMP_DEVICE);
		if(weightAlert!=null) {
			testElement=document.getElementById("weight_span");
			if(testElement!=null) {
				testElement.innerText="Weight (kg): "+weightAlert.data.text;
			}
		}
	}
	let possHeightAlert=knownPatientAlerts.get("PT_HEIGHT");	//Points to a map by UDI
	if(possHeightAlert!=null) {
		let heightAlert=possHeightAlert.get(PUMP_DEVICE);
		if(heightAlert!=null) {
			testElement=document.getElementById("height_span");
			if(testElement!=null) {
				testElement.innerText="Height (cm): "+heightAlert.data.text;
			}
		}
	}
	let possAgeAlert=knownPatientAlerts.get("PT_AGE");	//Points to a map by UDI
	if(possAgeAlert!=null) {
		let ageAlert=possAgeAlert.get(PUMP_DEVICE);
		if(ageAlert!=null) {
			testElement=document.getElementById("age_span");
			if(testElement!=null) {
				testElement.innerText="Age: "+ageAlert.data.text;
			}
		}
	}
	let possASAAlert=knownPatientAlerts.get("PT_STATE");	//Points to a map by UDI
	if(possASAAlert!=null) {
		let asaAlert=possASAAlert.get(PUMP_DEVICE);
		if(asaAlert!=null) {
			testElement=document.getElementById("asa_span");
			if(testElement!=null) {
				testElement.innerText="ASA: "+asaAlert.data.text;
			}
		}
	}
}

function getDrugInfo() {
/*
	writeTechnicalAlert("CASE_ID", sessionCaseId);
	writeTechnicalAlert("SYS_ID", sysOper);
	writeTechnicalAlert("CHANNEL1_DRUG_NAME",channelOneDrugName);
	writeTechnicalAlert("CHANNEL1_DRUG_CONC",channelOneDrugConc);
	writeTechnicalAlert("CHANNEL2_DRUG_NAME",channelTwoDrugName);
	writeTechnicalAlert("CHANNEL2_DRUG_CONC",channelTwoDrugConc);
	*/
	//drug_name_[12] and drug_concentration_[12] are the field names.
	
	setDrugInfo("CHANNEL1_DRUG_NAME", PUMP_DEVICE, "drug_name_1");
	setDrugInfo("CHANNEL2_DRUG_NAME", PUMP_DEVICE, "drug_name_2");
	setDrugInfo("CHANNEL1_DRUG_CONC", PUMP_DEVICE, "drug_concentration_1");
	setDrugInfo("CHANNEL2_DRUG_CONC", PUMP_DEVICE, "drug_concentration_2");
}

function setDrugInfo(alert,device,targetElement) {
	let alertByName=knownTechAlerts.get(alert);
	if(alertByName!=null) {
		let alertByDev=alertByName.get(device);
		if(alertByDev!=null) {
			let testElement=document.getElementById(targetElement);
			if(testElement!=null) {
				testElement.innerText=alertByDev.data.text;
			}
		}
	}
}

function configureMetricsForPump(pumpName) {
	metricToPumpControlMap.clear();	//Empty any existing entries
	if(pumpName=="neurowave_ap-4000") {
		metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP_1","infusion_rate_1");
		metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP_2","infusion_rate_2");
		metricToPumpControlMap.set("MDC_VOL_FLUID_DELIVERED_1","volume_infused_1");
		metricToPumpControlMap.set("MDC_VOL_FLUID_DELIVERED_2","volume_infused_2");
		metricToPumpControlMap.set("MDC_VOL_FLUID_TBI_REMAIN_1","volume_remaining_1");
		metricToPumpControlMap.set("MDC_VOL_FLUID_TBI_REMAIN_2","volume_remaining_2");
		metricToPumpControlMap.set("ICE_PROGRAMMED_VTBI_1","vtbi_1");
		metricToPumpControlMap.set("ICE_PROGRAMMED_VTBI_2","vtbi_2");
		metricToPumpControlMap.set("ICE_BOLUS_SESSION_TOTAL_1","total_bolus_volume_1");
		metricToPumpControlMap.set("ICE_BOLUS_SESSION_TOTAL_2","total_bolus_volume_2");
		metricToPumpControlMap.set("ICE_BOLUS_INFUSED_1","current_bolus_volume_1");
		metricToPumpControlMap.set("ICE_BOLUS_INFUSED_2","current_bolus_volume_2");
		//current_bolus_volume_2
		//metricToPumpControlMap.set("ICE_BOLUS_INFUSED_2","bolus_rate_1");
		console.log("Added metric map entries for ap4000");
	}
	if(pumpName=="qcore_sapphire") {
		metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
		metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
		metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
	
	}
	if(pumpName=="alaris_asena") {
		metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
		metricToPumpControlMap.set("VOLUME_INFUSED","volume_infused");
		metricToPumpControlMap.set("VTBI","vtbi");
	}
	/*
	metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
    metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
    metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
	*/
	pumpKeysAsArray=Array.from(metricToPumpControlMap.keys());
}