package org.mdpnp.apps.testapp.pleth.validity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class PlethValidityApp {
	
	private DeviceListModel deviceListModel;
	private NumericFxList numericList;
	private SampleArrayFxList sampleList;
	
	private HashMap<ObjectProperty<Date>,ChangeListener<Date>> listenerMap=new HashMap<>();
	
	private static ArrayList<String> waveformIDs=new ArrayList<>(
		Arrays.asList(
			rosetta.MDC_PRESS_BLD.VALUE
			//TODO: Add more metric IDs here
		)
	);
	
	private static ArrayList<String> numericIDs=new ArrayList<>(
		Arrays.asList(
			rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE,
			rosetta.MDC_PULS_OXIM_SAT_O2.VALUE
			//TODO: Add more metric IDs here
		)
	);

	public void set(DeviceListModel deviceListModel, NumericFxList numericList, SampleArrayFxList sampleList) {
		this.deviceListModel=deviceListModel;
		this.numericList=numericList;
		this.sampleList=sampleList;
	}

	public void start() {
		sampleList.addListener(new ListChangeListener<SampleArrayFx>() {
			@Override
			public void onChanged(Change<? extends SampleArrayFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach( s -> {
						if( waveformIDs.contains( s.getMetric_id() ) ) {
							attachWaveformListener(s);
						}
					});
					c.getRemoved().forEach( s -> {
						ChangeListener<Date> removeThis=listenerMap.get(s.presentation_timeProperty());
						if(removeThis instanceof ChangeListener) {
							s.presentation_timeProperty().removeListener(removeThis);
						}
					});
				}
			}
		});

		numericList.addListener(new ListChangeListener<NumericFx>() {
			@Override
			public void onChanged(Change<? extends NumericFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach( n -> {
						if( numericIDs.contains( n.getMetric_id() ) ) {
							attachNumericListener(n);
						}
					});
					c.getRemoved().forEach( n -> {
						ChangeListener<Date> removeThis=listenerMap.get(n.presentation_timeProperty());
						if(removeThis instanceof ChangeListener) {
							n.presentation_timeProperty().removeListener(removeThis);
						}
					});
				}
			}
		});
	}
	
	private void attachWaveformListener(SampleArrayFx s) {
		ChangeListener<Date> l=new ChangeListener<Date>() {
			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				System.err.println(s.getMetric_id()+" published at "+newValue+" first value is "+s.getValues()[0]);
			}
		};
		s.presentation_timeProperty().addListener(l);
		listenerMap.put(s.presentation_timeProperty(), l);
	}
	
	private void attachNumericListener(NumericFx n) {
		ChangeListener<Date> l=new ChangeListener<Date>() {
			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				System.err.println(n.getMetric_id()+" published at "+newValue+" first value is "+n.getValue());
			}
		};
		n.presentation_timeProperty().addListener(l);
		listenerMap.put(n.presentation_timeProperty(), l);
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
