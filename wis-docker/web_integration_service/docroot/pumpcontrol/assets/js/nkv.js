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
	initOpModeNames()
    initControlMappings();
    initSettingIdToButtonMap();
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

	PUMP_MANU.push("Alaris");
    PUMP_MODELS.push("Asena");

	PUMP_MANU.push("Neurowave");
    PUMP_MODELS.push("AP-4000");
    
    PUMP_MANU.push("ICE");
    PUMP_MODELS.push("Controllable Pump");

    VENT_MANU.push("Nihon Koden");
    VENT_MODELS.push("NKV550");
}

function initOpModeNames() {
	opModeNames.push("ACMV_VC");
	opModeNames.push("ACMV_PC");
	opModeNames.push("ACMV_PRVC");
	opModeNames.push("SIMV_VC");
	opModeNames.push("SIMV_PC");
	opModeNames.push("SIMV_PRVC");
	opModeNames.push("SPONT_CPAP");
	opModeNames.push("SPONT_PS");
	opModeNames.push("SPONT_VS");
	opModeNames.push("SPONT_ARPV");
}

class MetricData {
	constructor(label, unit) {
		this.label=label;
		this.unit=unit;
	}
}

function initControlMappings() {
    metricToControlMap.set("MDC_PULS_OXIM_SAT_O2","spo2_cell");
    metricToControlMap.set("MDC_RESP_RATE","rr_cell");
    metricToControlMap.set("MDC_ECG_HEART_RATE","hr_cell");
	//metricToControlMap.set("MDC_PRESS_CUFF_DIA","dia_cell");
	//metricToControlMap.set("MDC_PRESS_CUFF_SYS","sys_cell");
	//metricToControlMap.set("MDC_PRESS_BLD_NONINV_MEAN","map_cell");
    
/*
    metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
    metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
    metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
*/

	metricToVentControlMap.set("MDC_RESP_RATE", new MetricData("Resp. Rate","per/min"));
	metricToVentControlMap.set("MDC_AWAY_CO2_ET", new MetricData("ET CO<sub>2</sub>","mmHg"));
	metricToVentControlMap.set("MDC_PRESS_AWAY_INSP_PEAK", new MetricData("Peak Insp. Pressure","cmH<sub>2</sub>O"));
	metricToVentControlMap.set("MDC_PRESS_RESP_PLAT", new MetricData("Resp Plateau Pressure","cmH<sub>2</sub>O"));
	metricToVentControlMap.set("ICE_PEEP", new MetricData("PEEP","cmH<sub>2</sub>O"));
	metricToVentControlMap.set("ICE_FIO2", new MetricData("FiO<sub>2</sub>","%"));
	metricToVentControlMap.set("MDC_VENT_VOL_LEAK", new MetricData("Leak %","%"));
	metricToVentControlMap.set("NKV_550_OP_MODE", new MetricData("Operating Mode",""));
    /*
		monitorsMetricsMap=new HashMap<>();
		monitorsMetricsMap.put("RR<sub>TOT</sub>", new String[] {rosetta.MDC_RESP_RATE.VALUE, "MDC_DIM_RESP_PER_MIN"});
		monitorsMetricsMap.put("EtCO<sub>2</sub>", new String[] {rosetta.MDC_AWAY_CO2_ET.VALUE, rosetta.MDC_DIM_MMHG.VALUE});
		monitorsMetricsMap.put("P<sub>PEAK</sub>", new String[] {rosetta.MDC_PRESS_AWAY_INSP_PEAK.VALUE, rosetta.MDC_DIM_CM_H2O.VALUE});
		monitorsMetricsMap.put("P<sub>PLAT</sub>", new String[] {rosetta.MDC_PRESS_RESP_PLAT.VALUE, rosetta.MDC_DIM_CM_H2O.VALUE});
		monitorsMetricsMap.put("PEEP", new String[] {"ICE_PEEP", rosetta.MDC_DIM_CM_H2O.VALUE});	//TODO: Confirm there is no MDC_ for PEEP
		monitorsMetricsMap.put("FiO<sub>2</sub>%", new String[] {"ICE_FIO2", rosetta.MDC_DIM_PERCENT.VALUE});	//TODO: Confirm there is no MDC_ for FiO2
		monitorsMetricsMap.put("Leak %", new String[] {rosetta.MDC_VENT_VOL_LEAK.VALUE, rosetta.MDC_DIM_PERCENT.VALUE});	//TODO: Confirm there is no MDC_ for FiO2
*/


/*
    metricToVentControlMap.set("PB_DELIVERED_OXYGEN_PERCENT", "vent_o2_cell");
    metricToVentControlMap.set("ICE_PEEP", "vent_peep_cell");
    metricToVentControlMap.set("MDC_PRESS_AWAY_INSP_PEAK", "vent_peak_cell");
    metricToVentControlMap.set("MDC_RESP_RATE", "vent_rr_cell");
    metricToVentControlMap.set("PB_EXHALED_TIDAL_VOLUME", "vent_vt_cell"); 
    metricToVentControlMap.set("PB_IE_EXPIRATORY_COMPONENT", "vent_ie_cell");
	metricToVentControlMap.set("NKV_550_OP_MODE", null);
	*/
	
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

function initSettingIdToButtonMap() {
	/*
	<button class="btn opModeBtn" type="button" id="vtButton">VT</button>
			<button class="btn opModeBtn" type="button" id="tButton">T</button>
			<button class="btn opModeBtn" type="button" id="rrButton">RR</button>
			<button class="btn opModeBtn" type="button" id="peepButton">PEEP</button>
			<button class="btn opModeBtn" type="button" id="fiO2Button">FiO<sub>2</sub></button>
			<button class="btn opModeBtn" type="button" id="fTrigButton">FTrig</button>
			<button class="btn opModeBtn" type="button" id="deltaPCButton">&Delta;PC</button>
			<button class="btn opModeBtn" type="button" id="psButton">PS</button>
			<button class="btn opModeBtn" type="button" id="cpapButton">CPAP</button>
			<button class="btn opModeBtn" type="button" id="pHighButton">Phigh</button>
			<button class="btn opModeBtn" type="button" id="pLowButton">Plow</button>
			<button class="btn opModeBtn" type="button" id="tHighButton">Thigh</button>
			<button class="btn opModeBtn" type="button" id="tLowButton">Tlow</button>
			*/
			/*
			settingIdToSettings.put(2, new NKV550Settings(opModeHolder, "NKV_550_OP_MODE", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(64,new NKV550Settings(vtSettingHolder, "NKV_550_VT_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(66,new NKV550Settings(psSettingHolder, "NKV_550_PRESSURE_SUPPORT_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(67,new NKV550Settings(peepSettingHolder, "NKV_550_PEEP_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(68,new NKV550Settings(aprvPressureHighSettingHolder, "NKV_550_APRV_PRES_HIGH_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(69,new NKV550Settings(aprvPressureLowSettingHolder, "NKV_550_APRV_PRES_LOW_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(70,new NKV550Settings(aprvTimeHighSettingHolder, "NKV_550_APRV_TIME_HIGH_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(71,new NKV550Settings(aprvTimeLowSettingHolder, "NKV_550_APRV_TIME_LOW_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(75,new NKV550Settings(tiSettingHolder, "NKV_550_TI_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(77,new NKV550Settings(rrSettingHolder, "NKV_550_RR_SETTING", "MDC_DIM_RESP_PER_MIN"));
		settingIdToSettings.put(78,new NKV550Settings(fiO2SettingHolder, "NKV_550_FIO2_SETTING", rosetta.MDC_DIM_PERCENT.VALUE));
		settingIdToSettings.put(80,new NKV550Settings(fTrigSettingHolder, "NKV_550_FTRIG_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(86,new NKV550Settings(cpapSettingHolder, "NKV_550_CPAP_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(131,new NKV550Settings(deltaPCSettingHolder, "NKV_550_DELTAPC_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		*/
		//CHANGE THIS HERE MAP SO THAT THE VALUES ARE AN ARRAY THAT INCLUDES THE BASE TEXT FOR THE BUTTON LABEL,
		//AS WELL AS THE CONTROL NAME.  THEN CAN ALWAYS GENERATE THE FULL BUTTON LABEL BY LOOKING UP THE BASE TEXT
		//HERE AND ADDING THE VALUE.  THAT OVERCOMES THE PROBLEM WITH TRYING TO SUBSTRING THE VALUE TO TRIM OFF THE
		//CURRENT ONE.
	settingIdToButtonMap.set("NKV_550_VT_SETTING", "vtButton");
	settingIdToButtonMap.set("NKV_550_PRESSURE_SUPPORT_SETTING","psButton");
	settingIdToButtonMap.set("NKV_550_PEEP_SETTING","peepButton");
	settingIdToButtonMap.set("NKV_550_APRV_PRES_HIGH_SETTING","pHighButton");
	settingIdToButtonMap.set("NKV_550_APRV_PRES_LOW_SETTING","pLowButton");
	settingIdToButtonMap.set("NKV_550_APRV_TIME_HIGH_SETTING","tHighButton");
	settingIdToButtonMap.set("NKV_550_APRV_TIME_LOW_SETTING","tLowButton");
	settingIdToButtonMap.set("NKV_550_APRV_TIME_LOW_SETTING","tLowButton");
	settingIdToButtonMap.set("NKV_550_TI_SETTING","tButton");
	settingIdToButtonMap.set("NKV_550_RR_SETTING","rrButton");
	settingIdToButtonMap.set("NKV_550_FIO2_SETTING","fiO2Button");
	settingIdToButtonMap.set("NKV_550_FTRIG_SETTING","fTrigButton");
	settingIdToButtonMap.set("NKV_550_CPAP_SETTING","cpapButton");
	settingIdToButtonMap.set("NKV_550_DELTAPC_SETTING","deltaPCButton");
	
	let tmpSettingsKey=Array.from(settingIdToButtonMap.keys());
	ventKeysAsArray=ventKeysAsArray.concat(tmpSettingsKey);
	//console.log("ventKeysAsArray is "+ventKeysAsArray);
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
		
        if( PUMP_MANU.includes(device.manufacturer) && PUMP_MODELS.includes(device.model) ) {
	        let already=false;
			$("#pump_selector").children().each( function(index, opt) {
				if(opt.value==udi) {
					already=true;
				}
			});
			if(!already) {
				//UDI not in the selector
				$("#pump_selector").append(
					"<option value=\""+udi+"\">"+device.manufacturer+" "+device.model+"</option>"
				);
				udiToMakeAndModel.set(udi,device.model);
			}
			/*
            PUMP_DEVICE=udi;
            let deviceName=device.manufacturer+" "+device.model;
            let pumpElem=document.getElementById("pump_device");
			if(pumpElem!=null) {
				pumpElem.value=deviceName;
			}
			*/
			udiToMakeAndModel.set(udi,device.model);
			/*
			let name1=deviceName.toLowerCase();
			console.log("name1 is "+name1);
			let name2=name1.replace(/ /g,"_");
			console.log("name2 is "+name2);
			//let htmlFileName=deviceName.replace("%20","_").toLowerCase()+".html";
			let htmlFileName=name2+".html";
			console.log("htmlFileName is "+htmlFileName);
			$.get(htmlFileName).done(
				function(data) {
					document.getElementById("pump_placeholder").innerHTML=data;
				}
			).fail(function() {
				console.log("Could not retrieve "+htmlFileName);
			});
			*/
        }
        if( VENT_MANU.includes(device.manufacturer) && VENT_MODELS.includes(device.model) ) {
            VENT_DEVICE=udi;
            let deviceName=device.manufacturer+" "+device.model;
            document.getElementById("vent_device").innerText=deviceName;
			document.getElementById("vent_udi").innerText=udi;
			udiToMakeAndModel.set(udi,device.model);
        }


    });
    
    if(VITALS_DEVICE==null || VENT_MANU==null || VENT_DEVICE==null) {
        //console.log("Did not find v device");
		updateDeviceList();
        setTimeout(findDevices,5000);
    }
}

function startVitalSignsMonitoring() {
	runSamplesLoop();
	runNumericsLoop();
}

function runSamplesLoop() {
	latestSamples(getVentSamples);
	setTimeout(runSamplesLoop, 1000);
	//TODO check if sample time is the same as the one already on the graph.
}

function runNumericsLoop() {
	latestNumerics(getVentData, getPumpData, getVitalsData);
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
				let outerArray=samples[0];
		let outerCount=outerArray.length-1;
		let bpSource=outerArray[outerCount];
		//let bpSource=samples[0][0];
		if(bpSource!=undefined) {
			getSysDia(bpSource);
		}
    } else {
        //console.log("getVitalsData doesn't have VITALS_DEVICE yet");
    }
}

function getPumpData() {
    if(PUMP_DEVICE!=null) {
        //We won't call latestNumerics() here - we'll rely on the ones from startVitalSignsMonitoring();
        let latestVitals=getLatestNumeric(PUMP_DEVICE,...pumpKeysAsArray);
        latestVitals.forEach( (vital) => {
            let targetField=metricToPumpControlMap.get(vital.metric_id);
            if(targetField!=null) {
                let updateThis=document.getElementById(targetField);
				if(updateThis!=null) {
					updateThis.innerText=vital.value.toFixed(2);					
				}
                
            }
        });
    }        
}

//WE ARE GOING TO NEED TO EDIT THIS SO WE CAN PUBLISH A GET CURRENT SETTINGS REQUEST TO FORCE THE 550 TO PUBLISH THE OP MODE
//OTHERWISE WE DON'T SEE IT
function getVentData() {
    if(VENT_DEVICE!=null) {
        //We won't call latestNumerics() here - we'll rely on the ones from startVitalSignsMonitoring();
		if(lastOpMode==-1 && (settingsCounter++ % 10 )== 0) {
		  //console.log("requesting latest 550 settings");
		  requestLatest550Settings(VENT_DEVICE);	//We don't yet know the op mode.  We still need a way to avoid flooding with this request though.  Maybe a thread?	
		}
        let latestVitals=getLatestNumeric(VENT_DEVICE,...ventKeysAsArray);
		let tbody=$("#vent_table > tbody");
        latestVitals.forEach( (vital) => {
			//console.log("vital metric id in getVentData is "+vital.metric_id);
            let mapEntry=metricToVentControlMap.get(vital.metric_id);
            if(mapEntry!=null) {
				let tableRow=document.getElementById("vent_row_"+vital.metric_id);
				if(tableRow==null) {
					//Need to make a new row.  mapEntry is an instance of MetricData with label and unit
					if(vital.metric_id!="NKV_550_OP_MODE") {
						let rowHTML="<tr id=\"vent_row_"+vital.metric_id+"\"><td>"+mapEntry.label+"</td><td id=\""+vital.metric_id+"\">"+vital.value.toFixed(2)+"</td><td>"+mapEntry.unit+"</td></tr>";
        				tbody.append(rowHTML);
					}
				} else {
					var updateThis=document.getElementById(vital.metric_id);
					updateThis.innerText=vital.value.toFixed(2);
				}
                
            }
			if(vital.metric_id=="NKV_550_OP_MODE") {
				let intVal=vital.value.toFixed(0);
				let buttonLabel=opModeNames[intVal];
				$("#opModeButton").val(intVal);
				configureButtonsForMode(intVal);
				$("#vent_mode_cell").text(buttonLabel);
			}
			if(settingIdToButtonMap.has(vital.metric_id)) {
				let controlName=settingIdToButtonMap.get(vital.metric_id);
				let settingButton=document.getElementById(controlName);
				let baseText=settingButton.getAttribute("data-basetext");
				let newText=baseText+"<br/>"+vital.value.toFixed(1);
				settingButton.innerHTML=newText;
			}
        });
        latestTechAlerts(getVentAlerts);
    }        
}

function configureButtonsForMode(newMode) {
	if(newMode==lastOpMode) {
		//Nothing to do.
		return;
	}
	let modeButtons=[
		["opModeButton", "vtButton", "tButton", "rrButton", "peepButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "deltaPCButton", "tButton", "rrButton", "peepButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "vtButton", "tButton", "rrButton", "peepButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "vtButton", "tButton", "rrButton", "peepButton", "fiO2Button", "psButton", "ftrigButton"],
		["opModeButton", "deltaPCButton", "tButton", "rrButton", "peepButton", "fiO2Button", "psButton", "ftrigButton"],
		["opModeButton", "vtButton", "tButton", "rrButton", "peepButton", "fiO2Button", "psButton", "ftrigButton"],
		["opModeButton", "cpapButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "psButton", "peepButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "vtButton", "peepButton", "fiO2Button", "ftrigButton"],
		["opModeButton", "pHighButton", "pLowButton", "tHighButton", "tLowButton", "fiO2Button", "ftrigButton"]
	];
	///Obtain all buttons.
	let allButtons=document.getElementsByClassName("opModeBtn");
	//And hide them.
	for(let i=0;i<allButtons.length;i++) {
		$("#"+allButtons[i].id).hide();	
	}
	let buttonsForMode=modeButtons[newMode];
	for(let i=0;i<buttonsForMode.length;i++) {
		$("#"+buttonsForMode[i]).show();
	}
	lastOpMode=newMode;
}

function getVentAlerts() {
    //No check for VENT_DEVICE here because we only call this from inside getVentData()
    //This proves that we need to call these "latest" things with a callback to invoke the handling when the data is available.  This is likely to always be one pass behind because of async...
    //latestTechAlerts();
	let star=new Array();
	star.push("*");
    let latestAlerts=getLatestPatientAlerts(VENT_DEVICE,star);
	let tbody=$("#alarms_table > tbody");
	tbody.empty();
    latestAlerts.forEach( (alert) => {
				let  alertDev=udiToMakeAndModel.get(alert.data.unique_device_identifier);
				let alertTime=new Date(alert.read_sample_info.source_timestamp.sec*1000);
				let rowHTML="<tr><td>"+alertTime+"</td><td>"+alertDev+"</td><td>"+alert.data.identifier+"</td><td>"+alert.data.text+"</td></tr>";
				tbody.append(rowHTML);
    });

}

function getVentSamples() {
	//latestSamples() call moved to the start of the vitals monitoring, because it's needed for SYS/DIA
	let samples=getLatestSamples(VENT_DEVICE,"MDC_PRESS_AWAY","MDC_FLOW_AWAY","MDC_VOL_AWAY_TIDAL");
	samples.forEach( (sampleArray) => {
		let whichChart;
		if(sampleArray[0].metric_id=="MDC_PRESS_AWAY") {
			whichChart=pressureChart;
		}
		if(sampleArray[0].metric_id=="MDC_FLOW_AWAY") {
			whichChart=flowChart;
		}
		if(sampleArray[0].metric_id=="MDC_VOL_AWAY_TIDAL") {
			whichChart=volumeChart;
		}
		sampleArray.forEach( (sample) => {
			let currentData=whichChart.data.datasets[0].data;
			if(currentData.length<1300) {
				let newData=currentData.concat(sample.values);
				whichChart.data.datasets[0].data=newData;
			} else {
				let trimmed=currentData.slice(130);
				let newData=trimmed.concat(sample.values);
				whichChart.data.datasets[0].data=newData;
			}
		});

		let diffLength=whichChart.data.datasets[0].data.length-whichChart.data.labels.length;
		let j=0;
		for(let i=0;i<diffLength;i++) {
			whichChart.data.labels.push("");
			j++;
		}
		//console.log("Added "+j+" labels");
		let presentationTS=new Date(sampleArray[sampleArray.length-1].presentation_time.sec*1000);
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
                  },
				  min: -60,
				  max: 60
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   },
		   animation: false
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
                  },
				  min: -5,
				  max: 50
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   },
		   animation: false
       }
    });
	const volume_ctx=document.getElementById('volume_graph_canvas').getContext('2d');
    volumeChart=new Chart(volume_ctx, {
       type: 'line',
       data: {
           labels: [],    //This will be times
           datasets: [{
               label: 'Volume',
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
                  },
				  //min: -60,
				  //max: 600
               }
           },
		   elements: {
			   point: {
				   radius: 0
			   }
		   },
		   animation: false
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

function setParam() {
	let targetButton=event.target;
	let whichParam=targetButton.getAttribute("data-basetext");
	let val=prompt("Enter new value for "+whichParam);
	let keyToSet=targetButton.getAttribute("data-metricid");
	set550KeyValue(VENT_DEVICE, keyToSet, val);
	console.log("return val was "+val);
	
	
	
}

function executePumpProgram() {
	/*
	 * We want to call this
     * function programPump(udi, head, infusionRate, vtbi, bolusVolume, bolusRate, bolusDuration)
	 *
	 * BUT we need to handle cases where the fields don't exist, so although the code seems a bit verbose,
	 * this approach lets us do that.  It assumes that ANY PUMP PAGE uses the same field names.
	 *
	 * Any attempt to call the .val() method on a field that doesn't exist just returns undefined.  That works
	 * perfectly for our purposes, because we can just pass undefined into the pump program and it will use
	 * default values instead.
	 */
	let pumpUDI=PUMP_DEVICE;
	let head=1;	//For now - we'll handle multiple later
	let infusionRate=$("#target_infusion_rate").val();
	let vtbi=$("#target_vtbi").val();
	let bolusVolume=$("#target_bolus_volume").val();
	let bolusRate=$("#target_bolus_rate").val();
	let bolusDuration=$("#target_bolus_duration").val();
	programPump(pumpUDI, head, infusionRate, vtbi, bolusVolume, bolusRate, bolusDuration);
}

function executePumpProgramForChannel(channelNumber) {
	/*
	 * We want to call this
     * function programPump(udi, head, infusionRate, vtbi, bolusVolume, bolusRate, bolusDuration)
	 *
	 * BUT we need to handle cases where the fields don't exist, so although the code seems a bit verbose,
	 * this approach lets us do that.  It assumes that ANY PUMP PAGE uses the same field names.
	 *
	 * Any attempt to call the .val() method on a field that doesn't exist just returns undefined.  That works
	 * perfectly for our purposes, because we can just pass undefined into the pump program and it will use
	 * default values instead.
	 */
	let pumpUDI=PUMP_DEVICE;
	let head=1;	//For now - we'll handle multiple later
	let infusionRate=$("#target_infusion_rate").val();
	let vtbi=$("#target_vtbi").val();
	let bolusVolume=$("#target_bolus_volume").val();
	let bolusRate=$("#target_bolus_rate").val();
	let bolusDuration=$("#target_bolus_duration").val();
	programPump(pumpUDI, head, infusionRate, vtbi, bolusVolume, bolusRate, bolusDuration);
}

function executePumpProgramNeurowave(head) {
	let pumpUDI=PUMP_DEVICE;
	let infusionRate=$("#target_infusion_rate_"+head).val();
	if(infusionRate.length==0) {
		//No value
		infusionRate=-1;
	}
	let vtbi=$("#target_vtbi_"+head).val();
	if(vtbi.length==0) {
		//No value
		vtbi=-1;
	}
	//function programPump(udi, head = 1, infusionRate = -1, vtbi = -1, bolusVolume = -1, bolusRate = -1, bolusDuration = -1)
	let bolusVolume=$("#target_bolus_volume_"+head).val();
	if(bolusVolume.length==0) {
		//No value
		bolusVolume=-1;
	}
	let bolusRate=$("#target_bolus_rate_"+head).val();
	if(bolusRate.length==0) {
		//No value
		bolusRate=-1;
	}
	/*
	let bolusDuration=$("#target_bolus_duration_"+head).val();
	if(bolusDuration.length==0) {
		//No value
		bolusDuration=-1;
	}
	*/
	programPump(pumpUDI, head, infusionRate, vtbi, bolusVolume, bolusRate);
	alert("Submitted pump program");
}

function executePumpProgramAlaris() {
	let pumpUDI=PUMP_DEVICE;
	let head=1;	//We only have one.
	let infusionRate=$("#target_infusion_rate").val();
	let vtbi=$("#target_vtbi").val();
	//function programPump(udi, head = 1, infusionRate = -1, vtbi = -1, bolusVolume = -1, bolusRate = -1, bolusDuration = -1) {
	programPump(pumpUDI, head, infusionRate, vtbi);	//We omit the bolus arguments and the defaults will take care of them.
	alert("Submitted pump program");
}

function executePumpProgramQCore() {
	let pumpUDI=PUMP_DEVICE;
	let head=1;	//We only have one.
	let infusionRate=$("#target_infusion_rate").val();
	//function programPump(udi, head = 1, infusionRate = -1, vtbi = -1, bolusVolume = -1, bolusRate = -1, bolusDuration = -1) {
	programPump(pumpUDI, head, infusionRate);	//We omit the vtbi and bolus arguments and the defaults will take care of them.
	alert("Submitted pump program");
}

function pumpSelected() {
	PUMP_DEVICE=$("#pump_selector").val();
	let device=udiToDevice.get(PUMP_DEVICE);
    let deviceName=device.manufacturer+" "+device.model;
	let name1=deviceName.toLowerCase();
	//console.log("name1 is "+name1);
	let name2=name1.replace(/ /g,"_");
	//console.log("name2 is "+name2);
	//let htmlFileName=deviceName.replace("%20","_").toLowerCase()+".html";
	let htmlFileName=name2+".html";
	//console.log("htmlFileName is "+htmlFileName);
	$.get(htmlFileName).done(
		function(data) {
			document.getElementById("pump_placeholder").innerHTML=data;
			latestPatientAlerts(getPatientInfo);
			latestTechAlerts(getDrugInfo);
			configureMetricsForPump(name2);
			let pumpElem=document.getElementById("pump_device");
			if(pumpElem!=null) {
				pumpElem.value=deviceName;
			}
		}
	).fail(function() {
		console.log("Could not retrieve "+htmlFileName);
	});
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
	if(pumpName=="ice_controllable_pump") {
		metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
	}
	/*
	metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
    metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
    metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
	*/
	pumpKeysAsArray=Array.from(metricToPumpControlMap.keys());
}
