package org.mdpnp.apps.device;

import java.util.Set;

import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.scene.paint.Color;

import javafx.scene.paint.Paint;

public class WeightPanel extends AbstractWaveAndParamsPanel {
	
	private final static String[][] PARAMS = new String[][] {
        { rosetta.MDC_ATTR_PT_WEIGHT.VALUE }
	};
	
	private final static String[] PARAM_LABELS = new String[] { "Weight" };
	
	//00B0 is UTF-16 symbol for degrees
	private final static String[] PARAM_UNITS = new String[] { "g" };
	
	private final static String[] WEIGHT_LABELS = new String[] { };
	
	private final static String[] TEMP_WAVEFORMS = new String[] {  };
	
	private final static String[] TEMP_LABELS = new String[] { };

	public WeightPanel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getWaveformMetricIds() {
		return TEMP_WAVEFORMS;
	}

	@Override
	public String[] getWaveformLabels() {
		return TEMP_LABELS;
	}

	@Override
	public int getParameterCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String[] getParameterMetricIds(int i) {
		// TODO Auto-generated method stub
		return PARAMS[i];
	}

	@Override
	public String getParameterUnits(int i) {
		// TODO Auto-generated method stub
		return PARAM_UNITS[i];
	}

	@Override
	public String getParameterLabel(int i) {
		// TODO Auto-generated method stub
		return PARAM_LABELS[i];
	}

	@Override
	public String getStyleClassName() {
		// TODO Auto-generated method stub
		return "";	//For now
	}

	@Override
	public Paint getWaveformPaint() {
		// TODO Auto-generated method stub
		return Color.RED;	//Although we have no waveforms...
	}
	
    public static boolean supported(Set<String> identifiers) {
        for (String[] w : PARAMS) {
            for(String n : w) {
                if (identifiers.contains(n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void add(NumericFx data) {
        super.add(data,"%.4f");
    }

}
