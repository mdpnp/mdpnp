var metricToControlMap=new Map();
var metricToPumpControlMap=new Map();
var metricToVentControlMap=new Map();
var techAlertsToVentControlMap=new Map();
var VITALS_DEVICE=null;
var VENT_DEVICE=null;
var PUMP_DEVICE=null;
const VITALS_MANU=new Array();
const VITALS_MODELS=new Array();
const PUMP_MANU=new Array();
const PUMP_MODELS=new Array();
const VENT_MANU=new Array();
const VENT_MODELS=new Array();
const ventSettingsMap=new Map();
var ventAlertsForTableMap=new Map();

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

var udiToMakeAndModel=new Map();

var currentSys;
var currentDia;


function initPage() {
    initDeviceArrays();
    initControlMappings();
    updateDeviceList();
    findDevices();
    createCharts();
	createVentAlarmMap();
    populateVentTable();
    populateVitalsTable();
    startVitalSignsMonitoring();
}

function initDeviceArrays() {
    VITALS_MANU.push("Philips");
    VITALS_MODELS.push("MX800");
    VITALS_MODELS.push("Intellivue Device");

    PUMP_MANU.push("QCore");
    PUMP_MODELS.push("Sapphire");
    
    PUMP_MANU.push("ICE");
    PUMP_MODELS.push("Controllable Pump");

    VENT_MANU.push("Puritan Bennett");
    VENT_MODELS.push("PB840");
    VENT_MODELS.push("PB980");
}

function initControlMappings() {
    metricToControlMap.set("MDC_PULS_OXIM_SAT_O2","spo2_cell");
    metricToControlMap.set("MDC_RESP_RATE","rr_cell");
    metricToControlMap.set("MDC_ECG_HEART_RATE","hr_cell");
	//metricToControlMap.set("MDC_PRESS_CUFF_DIA","dia_cell");
	//metricToControlMap.set("MDC_PRESS_CUFF_SYS","sys_cell");
	//metricToControlMap.set("MDC_PRESS_BLD_NONINV_MEAN","map_cell");
    
    metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
    metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
    metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
    
    metricToVentControlMap.set("PB_DELIVERED_OXYGEN_PERCENT", "vent_o2_cell");
    metricToVentControlMap.set("PB_SETTING_PEEP", "vent_peep_cell");
    metricToVentControlMap.set("PB_PEAK_CIRCUIT_PRESSURE", "vent_peak_cell");
    metricToVentControlMap.set("PB_TOTAL_RESPIRATORY_RATE", "vent_rr_cell");
    metricToVentControlMap.set("PB_EXHALED_TIDAL_VOLUME", "vent_vt_cell"); 
    metricToVentControlMap.set("PB_IE_EXPIRATORY_COMPONENT", "vent_ie_cell"); 
	
    techAlertsToVentControlMap.set("PB_SETTING_MODE", "vent_mode_cell");

    vitalsKeysAsArray=Array.from(metricToControlMap.keys());
    pumpKeysAsArray=Array.from(metricToPumpControlMap.keys());
    ventKeysAsArray=Array.from(metricToVentControlMap.keys());
    ventTechAlertsKeysAsArray=Array.from(techAlertsToVentControlMap.keys());

    const ventSetting1=new Array();
    ventSetting1.push("VTi");	//Label
    ventSetting1.push("ml");	//Units
    ventSettingsMap.set("PB_SETTING_TIDAL_VOLUME",ventSetting1);

    const ventSetting2=new Array();
    ventSetting2.push("PEEP");	//Label
    ventSetting2.push("mHg");	//Units
    ventSettingsMap.set("PB_SETTING_PEEP",ventSetting2);

    const ventSetting3=new Array();
    ventSetting3.push("RR");	//Label
    ventSetting3.push("bpm");	//Units
    ventSettingsMap.set("PB_SETTING_RESPIRATORY_RATE",ventSetting3);

    const ventSetting4=new Array();
    ventSetting4.push("O2");	//Label
    ventSetting4.push("%");	//Units
    ventSettingsMap.set("PB_SETTING_OXYGEN_PERCENT",ventSetting4);

    ventSettingsKeysAsArray=Array.from(ventSettingsMap.keys());
    
}

function findDevices() {
    //Why on earth does Map.forEach have the value first then the key?
    deviceList.forEach( (device,udi) => {
        console.log("dev is "+device.manufacturer+" "+device.model);
        if(  VITALS_MANU.includes(device.manufacturer) && VITALS_MODELS.includes(device.model) ) {
            VITALS_DEVICE=udi;
            console.log("Found "+device.model+" with udi "+udi);
            let deviceName=device.manufacturer+" "+device.model;
            document.getElementById("monitor_device").value=deviceName;
			udiToMakeAndModel.set(udi,device.model);
        }

        if( PUMP_MANU.includes(device.manufacturer) && PUMP_MODELS.includes(device.model) ) {
            PUMP_DEVICE=udi;
            let deviceName=device.manufacturer+" "+device.model;
            document.getElementById("pump_device").value=deviceName;
			udiToMakeAndModel.set(udi,device.model);
        }
        if( VENT_MANU.includes(device.manufacturer) && VENT_MODELS.includes(device.model) ) {
            VENT_DEVICE=udi;
            let deviceName=device.manufacturer+" "+device.model;
            document.getElementById("vent_device").innerText=deviceName;
			udiToMakeAndModel.set(udi,device.model);
        }


    });
    
    if(VITALS_DEVICE==null) {
        console.log("Did not find vitals monitoring device");
        setTimeout(findDevices,5000);
    }
}

function startVitalSignsMonitoring() {
    console.log("startVitalSignsMonitoring called at "+Date.now());
    console.trace();
    if(VITALS_DEVICE!=null) {
        latestNumerics();
		latestSamples();
        let latestVitals=getLatestNumeric(VITALS_DEVICE,...vitalsKeysAsArray);
        latestVitals.forEach( (vital) => {
            let targetField=metricToControlMap.get(vital.metric_id);
            if(targetField!=null) {
				console.log("handling vital "+vital.metric_id+" with value "+vital.value);
				if(isNaN(vital.value)) {
					console.log("Skipping update to cell as value is NaN");
				} else {
					var updateThis=document.getElementById(targetField);
					updateThis.innerText=vital.value.toFixed(2);					
				}
            }
            //Later, we need another mapping for graphs
            if(vital.metric_id=='MDC_ECG_HEART_RATE') {
                var presentationTS=new Date(vital.presentation_time.sec*1000);
                hrChart.data.labels.push(presentationTS);
                hrChart.data.datasets[0].data.push(vital.value);
                hrChart.update();
            }
            if(vital.metric_id=='MDC_RESP_RATE') {
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
		let samples=getLatestSamples(VITALS_DEVICE,"MDC_PRESS_BLD_ART_ABP");
		let bpSource=samples[0];
		if(bpSource!=undefined) {
			getSysDia(bpSource);
		}
        getPumpData();
    } else {
        console.log("startVitalSignsMonitoring doesn't have VITALS_DEVICE yet");
    }
    setTimeout(startVitalSignsMonitoring, 2000);
}

function getPumpData() {
    if(PUMP_DEVICE!=null) {
        //We won't call latestNumerics() here - we'll rely on the ones from startVitalSignsMonitoring();
        let latestVitals=getLatestNumeric(PUMP_DEVICE,...pumpKeysAsArray);
        latestVitals.forEach( (vital) => {
            let targetField=metricToPumpControlMap.get(vital.metric_id);
            if(targetField!=null) {
                var updateThis=document.getElementById(targetField);
                updateThis.innerText=vital.value.toFixed(2);
            }
        });
        getVentData();
    }        
}

function getVentData() {
    if(VENT_DEVICE!=null) {
        //We won't call latestNumerics() here - we'll rely on the ones from startVitalSignsMonitoring();
        let latestVitals=getLatestNumeric(VENT_DEVICE,...ventKeysAsArray);
        latestVitals.forEach( (vital) => {
            let targetField=metricToVentControlMap.get(vital.metric_id);
            if(targetField!=null) {
                var updateThis=document.getElementById(targetField);
				if(vital.metric_id=="PB_IE_EXPIRATORY_COMPONENT") {
					//Is it worth having a callback function for every entry just to get the correct contents for the call?
					updateThis.innerText="1:"+vital.value.toFixed(2);
				} else {
					updateThis.innerText=vital.value.toFixed(2);
				}
            }
        });
        getVentTechAlerts();
    }        
}

function getVentTechAlerts() {
    //No check for VENT_DEVICE here because we only call this from inside getVentData()
    //This proves that we need to call these "latest" things with a callback to invoke the handling when the data is available.  This is likely to always be one pass behind because of async...
    latestTechAlerts();
    let latestAlerts=getLatestTechAlerts(VENT_DEVICE,...ventTechAlertsKeysAsArray);
    latestAlerts.forEach( (alert) => {
        let targetField=techAlertsToVentControlMap.get(alert.data.identifier);
        if(targetField!=null) {
            var updateThis=document.getElementById(targetField);
            updateThis.innerText=alert.data.text;
        }  
    });

    let latestSettings=getLatestNumeric(VENT_DEVICE,...ventSettingsKeysAsArray);
    let settingText="";
    latestSettings.forEach( (setting) => {
        let labelAndUnit=ventSettingsMap.get(setting.metric_id);
        settingText+=labelAndUnit[0];
        settingText+="&nbsp;";
        settingText+=setting.value.toFixed(2);
        settingText+="&nbsp;";
        settingText+=labelAndUnit[1];
        settingText+="&nbsp;";
    });
    document.getElementById("vent_settings_cell").innerHTML=settingText;
	
	//Table alerts
	
	let latestAlertsForTable=getLatestTechAlerts(VENT_DEVICE,...ventAlertsForTableKeysAsArray);
	$("#alarms_table > tbody").empty();
	latestAlertsForTable.forEach( (alert) => {
		let mappedAlert=ventAlertsForTableMap.get(alert.data.identifier);
		let textArray=mappedAlert.filterif;
		if(!textArray.includes(alert.data.text)) {
			let  alertDev=udiToMakeAndModel.get(alert.data.unique_device_identifier);
			//{"read_sample_info":{"source_timestamp":{"sec"
			let alertTime=new Date(alert.read_sample_info.source_timestamp.sec*1000);
			let rowHTML="<tr><td>"+alertTime+"</td><td>"+alertDev+"</td><td>"+mappedAlert.displaytext+"</td><td>"+alert.data.text+"</td></tr>";
			$("#alarms_table > tbody").append(rowHTML);
		}
	});
	
	
	
    getVentSamples();


}

function getVentSamples() {
	//latestSamples() call moved to the start of the vitals monitoring, because it's needed for SYS/DIA
	let samples=getLatestSamples(VENT_DEVICE,"MDC_PRESS_AWAY","MDC_FLOW_AWAY");
	samples.forEach( (sample) => {
		let whichChart;
		if(sample.metric_id=="MDC_PRESS_AWAY") {
			whichChart=pressureChart;
		} else {
			whichChart=flowChart;
		}
		whichChart.data.datasets[0].data=sample.values;
		let diffLength=sample.values.length-whichChart.data.labels.length;
		for(let i=0;i<diffLength;i++) {
			whichChart.data.labels.push("");
		}
		let presentationTS=new Date(sample.presentation_time.sec*1000);
		//whichChart.data.labels[0]=presentationTS;
		whichChart.config.options.scales['x'].title.text=presentationTS;
		whichChart.config.options.scales['x'].title.display=true;
		whichChart.config.options.scales['x'].grid.display=false;
        whichChart.update();
		
	});
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
	
	const flow_ctx=document.getElementById('flow_graph_canvas').getContext('2d');
	//options.elements.point.radius=0
    flowChart=new Chart(flow_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Flow',
               data: [],
               fill: false,
               borderColor: 'rgb(75, 192, 192)',
               tension: 0.1
           }]
       },
       options: {
           scales: {
               y: {
                  title: {
                      display: true,
                      text: ""
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
	const pressure_ctx=document.getElementById('pressure_graph_canvas').getContext('2d');
    pressureChart=new Chart(pressure_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Pressure',
               data: [],
               fill: false,
               borderColor: 'rgb(75, 192, 192)',
               tension: 0.1
           }]
       },
       options: {
           scales: {
               y: {
                  title: {
                      display: true,
                      text: ""
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
    for(let i=0;i<ventTableRows.length;i++) {
        let rowHTML="<tr><td>"+ventTableRows[i][0]+"</td><td class=\"numeric\" id=\""+ventTableRows[i][1]+"\"></td><td>"+ventTableRows[i][2]+"</td>";
        $("#vent_table > tbody").append(rowHTML);
    }
    
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
        let rowHTML="<tr><td>"+vitalsTableRows[i][0]+"</td><td class=\"numeric\" id=\""+vitalsTableRows[i][1]+"\"></td><td>"+vitalsTableRows[i][2]+"</td>";
        $("#vitals_table > tbody").append(rowHTML);
    }
}

function createVentAlarmMap() {
	$.getJSON("/techalerts.json", function (alerts) {
		$.each(alerts, function(counter, alert) {
			ventAlertsForTableMap.set(alert.metric, alert);
		});
		ventAlertsForTableKeysAsArray=Array.from(ventAlertsForTableMap.keys());
	}).fail(function(jqXHR, textStatus, errorThrown) {
		console.log("Failed to get techalerts.json");
	});
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