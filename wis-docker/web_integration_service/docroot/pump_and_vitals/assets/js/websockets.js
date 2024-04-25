var metricToControlMap=new Map();
var metricToPumpControlMap=new Map();
var VITALS_DEVICE=null;
var PUMP_DEVICE=null;
const VITALS_MANU=new Array();
const VITALS_MODELS=new Array();
const PUMP_MANU=new Array();
const PUMP_MODELS=new Array();
var settingIdToButtonMap=new Map();

var vitalsKeysAsArray;
var pumpKeysAsArray;

var hrChart;
var mapChart;
var rrChart;
var flowChart;
var pressureChart;
var volumeChart;

var udiToMakeAndModel=new Map();

var currentSys;
var currentDia;

var lastOpMode=-1;

let server=window.location.host;

let lastDia=0;
let lastSys=0;

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

    PUMP_MANU.push("QCore");
    PUMP_MODELS.push("Sapphire");
    
    PUMP_MANU.push("ICE");
    PUMP_MODELS.push("Controllable Pump");

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
    metricToControlMap.set("MDC_PRESS_BLD_ART_ABP_DIA","dia_cell");
    metricToControlMap.set("MDC_PRESS_BLD_ART_ABP_SYS","sys_cell");
    metricToControlMap.set("MDC_ECG_HEART_RATE","hr_cell");
    
    metricToPumpControlMap.set("MDC_FLOW_FLUID_PUMP","infusion_rate");
    metricToPumpControlMap.set("PUMP_VTBI_REMAINING","volume_remaining");
    metricToPumpControlMap.set("PUMP_VTBI_SO_FAR","volume_infused");
	
    vitalsKeysAsArray=Array.from(metricToControlMap.keys());
    pumpKeysAsArray=Array.from(metricToPumpControlMap.keys());
}

function findDevices() {
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
    });
    
    if(VITALS_DEVICE==null) {
        console.log("Did not find v device");
	updateDeviceList();
        setTimeout(findDevices,5000);
    }
}

function startVitalSignsMonitoring() {
	//startDeviceIdentitySocket();
	startNumericSocket();
	//runSamplesLoop();
	//runNumericsLoop();
}

function startNumericSocket() {
	let socketName="NumericSocketConnection";
	let nameObject=new Object();
	nameObject.name=socketName;
	let theJson=JSON.stringify([ { "name" : socketName } ]);
	console.log("theJson to request numeric socket is "+theJson);
	$.ajax({
		type: "POST",
		url: "/dds/v1/websocket_connections",
		data: theJson,
		contentType: "application/dds-web+json",
		dataType: "json"

	}).done( function(data) {
		console.log("Created new socket with name "+socketName);
		readFromWebSocketForNumerics(socketName);
	}).fail( function(data) {
		console.log("post to websocket_connections failed");					
	});
}

function startDeviceIdentitySocket() {
	let socketName="DeviceIdentitySocketConnection";
	let nameObject=new Object();
	nameObject.name=socketName;
	let theJson=JSON.stringify([ { "name" : socketName } ]);
	console.log("theJson to request deivce identity socket is "+theJson);
	$.ajax({
		type: "POST",
		url: "/dds/v1/websocket_connections",
		data: theJson,
		contentType: "application/dds-web+json",
		dataType: "json"

	}).done( function(data) {
		console.log("Created new socket with name "+socketName);
		readFromWebSocketForDeviceIdentity(socketName);
	}).fail( function(data) {
		console.log("post to websocket_connections failed");					
	});
}


function readFromWebSocketForNumerics(socketName) {
	let webSocket=new WebSocket("ws://"+server+"/dds/websocket/"+socketName);	//No protocols for now
	webSocket.onmessage = (event) => {
		let response=event.data;
		if(response.startsWith("HELLO OK:")) {
			console.log("HELLO was OK - "+response);
			return;
		}
		if(response.startsWith("HELLO FAIL:")) {
			console.log("HELLO failed - "+response);
			return;
		}
		let responseObj=JSON.parse(response);
		let numericArray=responseObj.body.read_sample_seq;
		//console.log("readFromWebSocketForNumeric has "+numericArray.length+" elements");
		for(let i=0;i<numericArray.length;i++) {
			let numeric=numericArray[i];
			let sampleInfo=numeric.read_sample_info;
			let numericData=numeric.data;
			createOrUpdateNumericDisplay(numericData);
		}
	}
	webSocket.onopen = (event) => {
		webSocket.send("Content-Type:application/dds-web+json\r\nAccept:application/dds-web+json\r\nOMG-DDS-API-Key:streamingkey\r\nVersion:1\r\n\r")
		console.log("Sent HELLO");

		let bindMsg=
		{
			"kind": "bind",
			"body": [{
				"bind_kind": "bind_datareader",
				"bind_id": "numeric",
				"uri": "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/NumericSubscriber/data_readers/NumericReader"
			}]
		};
		webSocket.send(JSON.stringify(bindMsg));
		console.log("Sent bind...");
	}

}

function readFromWebSocketForDeviceIdentity(socketName) {
	let webSocket=new WebSocket("ws://"+server+"/dds/websocket/"+socketName);	//No protocols for now
	webSocket.onmessage = (event) => {
		let response=event.data;
		if(response.startsWith("HELLO OK:")) {
			console.log("HELLO was OK - "+response);
			return;
		}
		if(response.startsWith("HELLO FAIL:")) {
			console.log("HELLO failed - "+response);
			return;
		}
		let responseObj=JSON.parse(response);
		let deviceIdentityArray=responseObj.body.read_sample_seq;
		//console.log("readFromWebSocketForNumeric has "+numericArray.length+" elements");
		for(let i=0;i<deviceIdentityArray.length;i++) {
			let identity=deviceIdentityArray[i];
			let sampleInfo=identity.read_sample_info;
			let identityData=identity.data;
			updateDeviceInfo(identityData);
		}
	}
	webSocket.onopen = (event) => {
		webSocket.send("Content-Type:application/dds-web+json\r\nAccept:application/dds-web+json\r\nOMG-DDS-API-Key:streamingkey\r\nVersion:1\r\n\r")
		console.log("Sent HELLO");

		let bindMsg=
		{
			"kind": "bind",
			"body": [{
				"bind_kind": "bind_datareader",
				"bind_id": "deviceidentity",
				"uri": "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/DeviceIdentitySubscriber/data_readers/DeviceIdentityReader"
			}]
		};
		webSocket.send(JSON.stringify(bindMsg));
		console.log("Sent bind...");
	}

}

function updateDeviceInfo(identityData) {
  console.log("got identityData via websocket - "+identityData);
}

function createOrUpdateNumericDisplay(numericData) {
  let res=getPumpData(numericData);
  if(res) return;
  res=getVitalsData(numericData);
}

function runSamplesLoop() {
	latestSamples();
	setTimeout(runSamplesLoop, 1000);
	//TODO check if sample time is the same as the one already on the graph.
}

function runNumericsLoop() {
	latestNumerics(getPumpData, getVitalsData);
	setTimeout(runNumericsLoop, 1000);
}

function getVitalsData(vital) {
	if(VITALS_DEVICE!=null) {
            let targetField=metricToControlMap.get(vital.metric_id);
            if(targetField!=null) {
                let updateThis=document.getElementById(targetField);
                updateThis.innerText=vital.value.toFixed(2);
            }
            //Later, we need another mapping for graphs
            if(vital.metric_id=='MDC_ECG_HEART_RATE') {
                let presentationTS=new Date(vital.presentation_time.sec*1000);
                hrChart.data.labels.push(presentationTS);
                hrChart.data.datasets[0].data.push(vital.value);
                hrChart.update();
            }
            if(vital.metric_id=='MDC_CO2_RESP_RATE') {
                let presentationTS=new Date(vital.presentation_time.sec*1000);
                rrChart.data.labels.push(presentationTS);
                rrChart.data.datasets[0].data.push(vital.value);
                rrChart.update();
            }
	    //Use DIA as the trigger to calculate the mean
	    if(vital.metric_id=='MDC_PRESS_BLD_ART_ABP_DIA') {
	        lastDia=vital.value;
		if(lastSys==0) return;	//We don't have a systolic yet
		let mean=(lastSys+2*lastDia)/3;
                let presentationTS=new Date(vital.presentation_time.sec*1000);
                mapChart.data.labels.push(presentationTS);
                mapChart.data.datasets[0].data.push(mean);
                mapChart.update();
		document.getElementById("map_cell").innerText=mean.toFixed(2);
	    }
	    if(vital.metric_id=='MDC_PRESS_BLD_ART_ABP_SYS') {
	        lastSys=vital.value;
	    }
	    
		/*
	    if(vital.metric_id=='MDC_PRESS_BLD_NONINV_MEAN') {
                let presentationTS=new Date(vital.presentation_time.sec*1000);
                mapChart.data.labels.push(presentationTS);
                mapChart.data.datasets[0].data.push(vital.value);
                mapChart.update();
            }
	    */
		/*
	    let samples=getLatestSamples(VITALS_DEVICE,"MDC_PRESS_BLD_ART_ABP");
   	    let outerArray=samples[0];
	    let outerCount=outerArray.length-1;
	    let bpSource=outerArray[outerCount];
	    if(bpSource!=undefined) {
	        getSysDia(bpSource);
	    }
	    */
    } else {
        console.log("getVitalsData doesn't have VITALS_DEVICE yet");
    }
}

/*
 * Return true if this handled the data, so we can avoid calling other updates,
 * false otherwise
 */
function getPumpData(vital) {
    if(PUMP_DEVICE!=null) {
            let targetField=metricToPumpControlMap.get(vital.metric_id);
            if(targetField!=null) {
                let updateThis=document.getElementById(targetField);
                updateThis.innerText=vital.value.toFixed(2);
		return true;	//Indicate we handled it
            }
    }
    return false;
}

function setFlowRate() {
    let speed=$("#target_infusion_rate").val();
    programPump(PUMP_DEVICE, 1, speed);
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
	if(val==null) {
		return;	//Cancelled
	}
	let keyToSet=targetButton.getAttribute("data-metricid");
	set550KeyValue(VENT_DEVICE, keyToSet, val);
	console.log("return val was "+val);
	
	
	
}
