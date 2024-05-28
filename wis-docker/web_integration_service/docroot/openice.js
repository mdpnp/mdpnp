var lineChart;
var flowChart;
var pressureChart;
const maxPoints=300;
const maxVentPoints=600;
var position=0;
var positions=Array();
positions.push(0);
positions.push(0);
var lastNulledPoint=null;
var lastNulledValue=null;

function initialiseCO2() {
	createChart();
	setInterval(function() {
		var mimeType="application/dds-web+json";
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/NumericSubscriber/data_readers/NumericReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(numerics) {
			 	//console.log(numerics);
				var deviceRoot=$("#devices_root");
				deviceRoot.empty();
				//Inefficient, but we can improve later.
				var udis=Array();
				$.each(numerics, function(counter1, numeric) {
					//console.log(numeric.data.presentation_time.sec);
					console.log(numeric);
					var udi=numeric.data.unique_device_identifier;
					if(udis[udi]===undefined) {
						udis[udi]=new Array();
					}
					udis[udi].push(numeric.data);
				});
				var udi_entries=Object.entries(udis);
				for(var i=0;i<udi_entries.length;i++) {
					var currentUDI=udi_entries[i][0];
					var numericsForUDI=udi_entries[i][1];
					//console.log("Looping through udis with udi "+currentUDI);
					var htmlforudi="";
					if( $("#"+currentUDI).length==0) {
						htmlforudi="<div id=\""+currentUDI+"\">";
					}
					for(var j=0;j<numericsForUDI.length;j++) {
						var numeric=numericsForUDI[j];
						htmlforudi=htmlforudi+"<p>"+numeric.metric_id+" "+numeric.value+"</p>";
					}
					htmlforudi=htmlforudi+"</div>";
					deviceRoot.append(htmlforudi);
				}
			}
		
		).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch NumericReader contents");
			console.log(textStatus);
			console.log(errorThrown);
		});	//End of fail

		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/SampleArraySubscriber/data_readers/SampleArrayReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(samples) {
			$.each(samples, function(counter99, sample) {
				//Do sample stuff here...
				//console.log(sample.data);
				var newData=sample.data.values;
				var len=newData.length;
				if(lastNulledPoint!=null) {
					lineChart.data.datasets[0].data[lastNulledPoint]=lastNulledValue;
				}
				for(var i=0;i<newData.length;i++) {
				  lineChart.data.datasets[0].data[position++%maxPoints]=newData[i];
				  //console.log("Set position "+position%maxPoints+" to "+newData[i]);
				}
				lastNulledPoint=position++%maxPoints; 
				lastNulledValue=lineChart.data.datasets[0].data[lastNulledPoint];

				lineChart.data.datasets[0].data[ lastNulledPoint ]=null;
				  console.log("Added null behind position");
				lineChart.update();
			});	//End of each


		}).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch SampleReader contents");
			console.log(textStatus);
			console.log(errorThrown);
		});





	}, 1000);
}

function initialiseVent() {
	getDeviceInfo();
	createVentCharts();
	setInterval(function() {
		var mimeType="application/dds-web+json";
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/NumericSubscriber/data_readers/NumericReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(numerics) {
			 	//console.log(numerics);
				var deviceRoot=$("#devices_root");
				//Inefficient, but we can improve later.
				var udis=Array();
				$.each(numerics, function(counter1, numeric) {
					//console.log(numeric.data.presentation_time.sec);
					//console.log(numeric);
					var udi=numeric.data.unique_device_identifier;
					if(udis[udi]===undefined) {
						udis[udi]=new Array();
					}
					udis[udi].push(numeric.data);
				});
				var udi_entries=Object.entries(udis);
				for(var i=0;i<udi_entries.length;i++) {
					var currentUDI=udi_entries[i][0];
					var numericsForUDI=udi_entries[i][1];
					//console.log("Looping through udis with udi "+currentUDI);
					var htmlforudi="";
					if( $("#"+currentUDI).length==0) {
						htmlforudi="<div id=\""+currentUDI+"\">";
						htmlforudi=htmlforudi+"<table id=\"table_"+currentUDI+"\">";
						htmlforudi=htmlforudi+"<thead>";
						htmlforudi=htmlforudi+"<tr><th>Metric</th><th>Value</th></tr>";
						htmlforudi=htmlforudi+"</thead><tbody></tbody></table></div>";
						deviceRoot.append(htmlforudi);
					}
					//Extra array list of seen metrics, as we can get multiple samples from the reader
					//on the first time we go through the loop
					var seenThisTime=Array();
					for(var j=0;j<numericsForUDI.length;j++) {
						var numeric=numericsForUDI[j];
						var checkForThis=currentUDI+"_"+numeric.metric_id+"_id";
						var updateThis=currentUDI+"_"+numeric.metric_id+"_val";
						var possibleRow=document.getElementById(checkForThis);
						if(possibleRow==null && !seenThisTime.includes(checkForThis)) {
							let newNumericRow="<tr id=\""+checkForThis+"\"><td>"+numeric.metric_id+"</td><td id=\""+updateThis+"\">"+numeric.value+"</td></tr>";
							$("#table_"+currentUDI+" > tbody").append(newNumericRow);
							seenThisTime.push(checkForThis);
						} else {
							var valueCell=document.getElementById(updateThis);
							valueCell.innerText=numeric.value;
						}
					}
				}
			}
		
		).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch NumericReader contents");
			console.log(textStatus);
			console.log(errorThrown);
		});	//End of fail
		let samplesRead=0;
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/SampleArraySubscriber/data_readers/SampleArrayReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(samples) {
			$.each(samples, function(counter99, sample) {
				samplesRead++;
				//Do sample stuff here...
				console.log(sample);
				var newData=sample.data.values;
				console.log("Got sample with id "+sample.data.metric_id);
				if(sample.data.metric_id=="MDC_FLOW_AWAY") {
					var len=newData.length;
					for(var i=0;i<newData.length;i++) {
					  flowChart.data.datasets[0].data[positions[0]++%maxVentPoints]=newData[i];
					}
					flowChart.update();

				}
				if(sample.data.metric_id=="MDC_PRESS_AWAY") {
					var len=newData.length;
					console.log("MDS_PRESS_AWAY len is "+len);
					for(var i=0;i<newData.length;i++) {
					  pressureChart.data.datasets[0].data[positions[1]++%maxVentPoints]=newData[i];
					}
					pressureChart.update();
					
				}

			});	//End of each
			console.log("Received "+samplesRead+"on this read");


		}).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch SampleReader contents");
			console.log(textStatus);
			console.log(errorThrown);
		});





	}, 500);
}

function getDeviceInfo() {
		var mimeType="application/dds-web+json";
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/DeviceIdentitySubscriber/data_readers/DeviceIdentityReader?sampleFormat=json&removeFromReaderCache=false",
				contentType: mimeType
			}
		).done( function(deviceInfos) {
			console.log("Done in deviceInfos");
			deviceInfos.forEach(function(deviceInfo) {
				let udi=deviceInfo.data.unique_device_identifier;
				let manu=deviceInfo.data.manufacturer;
				let model=deviceInfo.data.model;
				console.log("Next udi is "+udi);
				document.getElementById("device_manu").cells[1].innerText=manu;
				document.getElementById("device_model").cells[1].innerText=model;
				document.getElementById("device_udi").cells[1].innerText=udi;
			});
		}).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch DeviceIdentity contents");
			console.log(textStatus);
			console.log(errorThrown);
		});


}

function createChart() {
	var ctx=document.getElementById('waveform_canvas');
	var emptyLabels=Array(maxPoints);
	var initialData=Array(maxPoints);
	emptyLabels.fill('');
	lineChart = new Chart(ctx, {
	    type: 'line',
	    data: {
		labels: emptyLabels,
		datasets: [
		{
		    label: 'CO2',
		    data: initialData,
		    borderColor: 'rgb(75, 192, 192)',
		    pointRadius: 0,
		},
		]
	    },
		//lineChart.options.scales.x.grid.display=false
	    options: {
	        scales: {
		    x: {
		        grid: {
			    display: false
		        }
		    },
		    y: {
		        grid: {
			    display: false
		        }
		    }
		}
	    }
	});
}

function createVentCharts() {
	var pressureCtx=document.getElementById('pressure_canvas');
	var flowCtx=document.getElementById('flow_canvas');
	var emptyFlowLabels=Array(maxVentPoints);
	var initialFlowData=Array(maxVentPoints);
	emptyFlowLabels.fill('');
	flowChart = new Chart(flowCtx, {
	    type: 'line',
	    data: {
		labels: emptyFlowLabels,
		datasets: [
		{
		    label: 'Flow',
		    data: initialFlowData,
		    borderColor: 'rgb(75, 192, 192)',
		    pointRadius: 0,
		}
		]
	    },
		//lineChart.options.scales.x.grid.display=false
	    options: {
	        scales: {
		    x: {
		        grid: {
			    display: false
		        }
		    },
		    y: {
		        grid: {
			    display: false
		        }
		    }
		},
		responsive: true,
		maintainAspectRation: false
	    }
	});
	var emptyFlowLabels=Array(maxVentPoints);
	var initialFlowData=Array(maxVentPoints);
	emptyFlowLabels.fill('');
	pressureChart = new Chart(pressureCtx, {
	    type: 'line',
	    data: {
		labels: emptyFlowLabels,
		datasets: [
		{
		    label: 'Pressure',
		    data: initialFlowData,
		    borderColor: 'rgb(75, 192, 192)',
		    pointRadius: 0,
		}
		]
	    },
		//lineChart.options.scales.x.grid.display=false
	    options: {
	        scales: {
		    x: {
		        grid: {
			    display: false
		        }
		    },
		    y: {
		        grid: {
			    display: false
		        }
		    }
		},
		responsive: true,
		maintainAspectRation: false
	    }
	});
}



function initialisePumps() {
	setInterval(function() {
		var mimeType="application/dds-web+json";
		var mimeType="application/dds-web+json";
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/NumericSubscriber/data_readers/NumericReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(numerics) {
			 	//console.log(numerics);
				var deviceRoot=$("#devices_root");
				//deviceRoot.empty();
				//Inefficient, but we can improve later.
				var udis=Array();
				$.each(numerics, function(counter1, numeric) {
					//console.log(numeric.data.presentation_time.sec);
					//console.log(numeric);
					var udi=numeric.data.unique_device_identifier;
					if(udis[udi]===undefined) {
						udis[udi]=new Array();
					}
					udis[udi].push(numeric.data);
				});
				var udi_entries=Object.entries(udis);
				for(var i=0;i<udi_entries.length;i++) {
					var currentUDI=udi_entries[i][0];
					var numericsForUDI=udi_entries[i][1];
					//console.log("Looping through udis with udi "+currentUDI);
					var htmlforudi="";
					if( $("#"+currentUDI).length==0) {
						htmlforudi="<div id=\""+currentUDI+"\">";
						htmlforudi=htmlforudi+"<table id=\"table_"+currentUDI+"\">";
						htmlforudi=htmlforudi+"<tr><td>UDI</td><td>"+currentUDI+"</td></tr>";
					}
					var addedToTable=false;
					//Extra array list of seen metrics, as we can get multiple samples from the reader
					//on the first time we go through the loop
					var seenThisTime=Array();
					for(var j=0;j<numericsForUDI.length;j++) {
						var numeric=numericsForUDI[j];
						var checkForThis=currentUDI+"_"+numeric.metric_id+"_id";
						var updateThis=currentUDI+"_"+numeric.metric_id+"_val";
						var possibleRow=document.getElementById(checkForThis);
						if(possibleRow==null && !seenThisTime.includes(checkForThis)) {
							htmlforudi=htmlforudi+"<tr id=\""+checkForThis+"\"><td>Metric</td><td>"+numeric.metric_id+"</td></tr>";
							htmlforudi=htmlforudi+"<tr id=\""+updateThis+"\"><td>Value</td><td>"+numeric.value+"</td></tr>";
							htmlforudi=htmlforudi+"<tr><td><input id=\"speed_"+currentUDI+"\" type=\"text\"/></td><td><button type=\"button\" onclick=\"setSpeed('"+currentUDI+"')\">Set</button></td></tr>";
							addedToTable=true;
							seenThisTime.push(checkForThis);
						} else {
							var valueRow=document.getElementById(updateThis);
							var cells=valueRow.cells;
							cells[1].innerText=numeric.value;
						}
					}
					if(addedToTable) {
						htmlforudi=htmlforudi+"</table>";
					}
					htmlforudi=htmlforudi+"</div>";
					deviceRoot.append(htmlforudi);
				}
			}
		
		).fail(function( jqXHR, textStatus, errorThrown ) {
			alert("Could not fetch NumericReader contents");
			console.log(textStatus);
			console.log(errorThrown);
		});	//End of fail
		/*
		$.ajax(
			{
				url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/DeviceIdentitySubscriber/data_readers/DeviceIdentityReader?sampleFormat=json",
				contentType: mimeType
			}
		).done( function(identities) {
			$.each(identities, function(counter, identity) {
				console.log(identity.data.unique_device_identifier);
			});
		}).fail(function( jqXHR, textStatus, errorThrown ) {
			console.log("Could not fetch DeviceIdentity contents");
			console.log(textStatus);
			console.log(errorThrown);
		});
		*/
	}, 1000);
}

function setSpeed(device_to_set) {
  var mimeType="application/dds-web+json";
  var inputBox=document.getElementById("speed_"+device_to_set);
  var targetSpeed=inputBox.value;
  var speedObj={
	  unique_device_identifier: device_to_set,
	  requestor: "WebIntegrationService",
	  newFlowRate: targetSpeed,
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
	//			url: "/dds/rest1/applications/OpenICE/domain_participants/ICEParticipant/subscribers/DeviceIdentitySubscriber/data_readers/DeviceIdentityReader?sampleFormat=json",
