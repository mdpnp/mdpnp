package org.mdpnp.apps.testapp.vital;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.END;
import static javafx.scene.input.KeyCode.HOME;
import static javafx.scene.input.KeyCode.KP_DOWN;
import static javafx.scene.input.KeyCode.KP_LEFT;
import static javafx.scene.input.KeyCode.KP_RIGHT;
import static javafx.scene.input.KeyCode.KP_UP;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.UP;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.TwoLevelFocusBehavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.util.Utils;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class MultiRangeSliderBehavior extends BehaviorBase<MultiRangeSlider> {
	
	private final InputMap<MultiRangeSlider> sliderInputMap;
	
	private TwoLevelFocusBehavior tlFocus;

	public MultiRangeSliderBehavior(MultiRangeSlider slider) {
		super(slider);
		// create a map for slider-specific mappings (this reuses the default
        // InputMap installed on the control, if it is non-null, allowing us to pick up any user-specified mappings)
        sliderInputMap = createInputMap();

        // then slider-specific mappings for key input
        addDefaultMapping(sliderInputMap,
            new InputMap.KeyMapping(HOME, KeyEvent.KEY_RELEASED, e -> home()),
            new InputMap.KeyMapping(END, KeyEvent.KEY_RELEASED, e -> end())
        );
        
     // we split the rest of the mappings into vertical and horizontal slider
        // child input maps
        // -- horizontal
        InputMap<MultiRangeSlider> horizontalMappings = new InputMap<>(slider);
        horizontalMappings.setInterceptor(e -> slider.getOrientation() != Orientation.HORIZONTAL);
        horizontalMappings.getMappings().addAll(
            // we use the rtl method to translate depending on the RTL state of the UI
            new InputMap.KeyMapping(LEFT, e -> rtl(slider, this::incrementValue, this::decrementValue)),
            new InputMap.KeyMapping(KP_LEFT, e -> rtl(slider, this::incrementValue, this::decrementValue)),
            new InputMap.KeyMapping(RIGHT, e -> rtl(slider, this::decrementValue, this::incrementValue)),
            new InputMap.KeyMapping(KP_RIGHT, e -> rtl(slider, this::decrementValue, this::incrementValue))
        );
        addDefaultChildMap(sliderInputMap, horizontalMappings);
        
     // -- vertical
        InputMap<MultiRangeSlider> verticalMappings = new InputMap<>(slider);
        verticalMappings.setInterceptor(e -> slider.getOrientation() != Orientation.VERTICAL);
        verticalMappings.getMappings().addAll(
                new InputMap.KeyMapping(DOWN, e -> decrementValue()),
                new InputMap.KeyMapping(KP_DOWN, e -> decrementValue()),
                new InputMap.KeyMapping(UP, e -> incrementValue()),
                new InputMap.KeyMapping(KP_UP, e -> incrementValue())
        );
        addDefaultChildMap(sliderInputMap, verticalMappings);
        
     // Only add this if we're on an embedded platform that supports 5-button navigation
        if (com.sun.javafx.scene.control.skin.Utils.isTwoLevelFocus()) {
            tlFocus = new TwoLevelFocusBehavior(slider); // needs to be last.
        }
	}
	
	/**
	 * We have to steal this from BehaviorBase as it's package private in there
	 * @param node
	 * @param rtlMethod
	 * @param nonRtlMethod
	 */
	void rtl(Node node, Runnable rtlMethod, Runnable nonRtlMethod) {
        switch(node.getEffectiveNodeOrientation()) {
            case RIGHT_TO_LEFT: rtlMethod.run(); break;
            default: nonRtlMethod.run(); break;
        }
    }

	@Override
	public InputMap<MultiRangeSlider> getInputMap() {
		// TODO Auto-generated method stub
		return sliderInputMap;
	}
	
	private Callback<Void, FocusedChild> selectedValue;
    public void setSelectedValue(Callback<Void, FocusedChild> c) {
        selectedValue = c;
    }
    /**
     * Invoked by the RangeSlider {@link Skin} implementation whenever a mouse press
     * occurs on the "track" of the slider. This will cause the thumb to be
     * moved by some amount.
     *
     * @param position The mouse position on track with 0.0 being beginning of
     *        track and 1.0 being the end
     */
    public void trackPress(MouseEvent e, double position) {
        // determine the percentage of the way between min and max
        // represented by this mouse event
        final MultiRangeSlider rangeSlider = getNode();
        // If not already focused, request focus
        if (!rangeSlider.isFocused()) rangeSlider.requestFocus();
        if (selectedValue != null) {
            switch(selectedValue.call(null)) {
            case LOWEST_THUMB:
                if (rangeSlider.getOrientation().equals(Orientation.HORIZONTAL)) {
                    rangeSlider.adjustLowestValue(position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                } else {
                    rangeSlider.adjustLowestValue((1-position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                }
                break;
            case LOWER_THUMB:
                if (rangeSlider.getOrientation().equals(Orientation.HORIZONTAL)) {
                    rangeSlider.adjustLowerValue(position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                } else {
                    rangeSlider.adjustLowerValue((1-position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                }
                break;
            case HIGHER_THUMB:
                if (rangeSlider.getOrientation().equals(Orientation.HORIZONTAL)) {
                    rangeSlider.adjustHigherValue(position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                } else {
                    rangeSlider.adjustHigherValue((1-position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                }
                break;
            case HIGHEST_THUMB:
                if (rangeSlider.getOrientation().equals(Orientation.HORIZONTAL)) {
                    rangeSlider.adjustHighestValue(position * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                } else {
                    rangeSlider.adjustHighestValue((1-position) * (rangeSlider.getMax() - rangeSlider.getMin()) + rangeSlider.getMin());
                }
                break;
            default:
            }
        }
    }

    /**
     */
    public void trackRelease(MouseEvent e, double position) {
    }
    
    /**
     * @param position The mouse position on track with 0.0 being beginning of
      *       track and 1.0 being the end
     */
    public void lowerThumbPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        final MultiRangeSlider rangeSlider = getNode();
        if (!rangeSlider.isFocused())  rangeSlider.requestFocus();
        rangeSlider.setLowerValueChanging(true);
    }
    public void lowestThumbPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        final MultiRangeSlider rangeSlider = getNode();
        if (!rangeSlider.isFocused())  rangeSlider.requestFocus();
        rangeSlider.setLowestValueChanging(true);
    }    

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *        track and 1.0 being the end
     */
    public void lowerThumbDragged(MouseEvent e, double position) {
        final MultiRangeSlider rangeSlider = getNode();
        double newValue = Utils.clamp(rangeSlider.getMin(), 
                (position * (rangeSlider.getMax() - rangeSlider.getMin())) + rangeSlider.getMin(), 
                rangeSlider.getMax());
        rangeSlider.setLowerValue(newValue);
    }
    
    public void lowestThumbDragged(MouseEvent e, double position) {
        final MultiRangeSlider rangeSlider = getNode();
        double newValue = Utils.clamp(rangeSlider.getMin(), 
                (position * (rangeSlider.getMax() - rangeSlider.getMin())) + rangeSlider.getMin(), 
                rangeSlider.getMax());
        rangeSlider.setLowestValue(newValue);
    }    
    
    /**
     * When lowThumb is released lowValueChanging should be set to false.
     */
    public void lowerThumbReleased(MouseEvent e) {
        final MultiRangeSlider rangeSlider = getNode();
        rangeSlider.setLowerValueChanging(false);
        // RT-15207 When snapToTicks is true, slider value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        if (rangeSlider.isSnapToTicks()) {
            rangeSlider.setLowerValue(snapValueToTicks(rangeSlider.getLowerValue()));
        }
    }
    
    public void lowestThumbReleased(MouseEvent e) {
        final MultiRangeSlider rangeSlider = getNode();
        rangeSlider.setLowestValueChanging(false);
        // RT-15207 When snapToTicks is true, slider value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        if (rangeSlider.isSnapToTicks()) {
            rangeSlider.setLowestValue(snapValueToTicks(rangeSlider.getLowestValue()));
        }
    }
    
    private double snapValueToTicks(double d) {
        MultiRangeSlider rangeSlider = (MultiRangeSlider) getNode();
        double d1 = d;
        double d2 = 0.0D;
        if (rangeSlider.getMinorTickCount() != 0)
            d2 = rangeSlider.getMajorTickUnit() / (double) (Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
        else
            d2 = rangeSlider.getMajorTickUnit();
        int i = (int) ((d1 - rangeSlider.getMin()) / d2);
        double d3 = (double) i * d2 + rangeSlider.getMin();
        double d4 = (double) (i + 1) * d2 + rangeSlider.getMin();
        d1 = Utils.nearest(d3, d1, d4);
        return Utils.clamp(rangeSlider.getMin(), d1, rangeSlider.getMax());
    }
    
 // when high thumb is released, highValueChanging is set to false.
    public void higherThumbReleased(MouseEvent e) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        slider.setHigherValueChanging(false);
        if (slider.isSnapToTicks())
            slider.setHigherValue(snapValueToTicks(slider.getHigherValue()));
    }
    public void highestThumbReleased(MouseEvent e) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        slider.setHighestValueChanging(false);
        if (slider.isSnapToTicks())
            slider.setHighestValue(snapValueToTicks(slider.getHighestValue()));
    }

    public void higherThumbPressed(MouseEvent e, double position) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        if (!slider.isFocused())
            slider.requestFocus();
        slider.setHigherValueChanging(true);
    }
    
    public void highestThumbPressed(MouseEvent e, double position) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        if (!slider.isFocused())
            slider.requestFocus();
        slider.setHighestValueChanging(true);
    }    

    public void higherThumbDragged(MouseEvent e, double position) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        slider.setHigherValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
    }
    
    public void highestThumbDragged(MouseEvent e, double position) {
    	MultiRangeSlider slider = (MultiRangeSlider) getNode();
        slider.setHighestValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
    }

	
	void home() {
        final MultiRangeSlider slider = getNode();
        slider.adjustHighestValue(slider.getMin());
    }
	
	void end() {
        final MultiRangeSlider slider = getNode();
        slider.adjustHighestValue(slider.getMax());
    }
	
	void incrementValue() {
		MultiRangeSlider slider = (MultiRangeSlider) getNode();
        if (selectedValue != null) {
            switch(selectedValue.call(null)) {
            case HIGHEST_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustHighestValue(slider.getHighestValue() + computeIncrement());
                else
                    slider.incrementHighestValue();
                break;
            case HIGHER_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustHigherValue(slider.getHigherValue() + computeIncrement());
                else
                    slider.incrementHigherValue();
                break;
            case LOWER_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustLowerValue(slider.getLowerValue() + computeIncrement());
                else
                    slider.incrementLowerValue();
                break;
            case LOWEST_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustLowestValue(slider.getLowestValue() + computeIncrement());
                else
                    slider.incrementLowestValue();
                break;
            default:
            }
        }
    }
	
	void decrementValue() {
        MultiRangeSlider slider = (MultiRangeSlider) getNode();
        if (selectedValue != null) {
            switch(selectedValue.call(null)) {
            case HIGHEST_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustHighestValue(slider.getHighestValue() - computeIncrement());
                else
                    slider.decrementHighestValue();
                break;
            case HIGHER_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustHigherValue(slider.getHigherValue() - computeIncrement());
                else
                    slider.decrementHigherValue();
                break;
            case LOWER_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustLowerValue(slider.getLowerValue() - computeIncrement());
                else
                    slider.decrementLowerValue();
                break;
            case LOWEST_THUMB:
                if (slider.isSnapToTicks())
                    slider.adjustLowestValue(slider.getLowestValue() - computeIncrement());
                else
                    slider.decrementLowestValue();
                break;
            default:
            }

        }
    }

    // Used only if snapToTicks is true.
    double computeIncrement() {
        final MultiRangeSlider slider = getNode();
        double tickSpacing = 0;
        if (slider.getMinorTickCount() != 0) {
            tickSpacing = slider.getMajorTickUnit() / (Math.max(slider.getMinorTickCount(),0)+1);
        } else {
            tickSpacing = slider.getMajorTickUnit();
        }

        if (slider.getBlockIncrement() > 0 && slider.getBlockIncrement() < tickSpacing) {
                return tickSpacing;
        }

        return slider.getBlockIncrement();
    }
    
    public void moveRange(double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getNode();
        final double min = slider.getMin();
        final double max = slider.getMax();
        final double lowestValue = slider.getLowestValue();
        final double newLowestValue = Utils.clamp(min, lowestValue + position *(max-min) / 
                (slider.getOrientation() == Orientation.HORIZONTAL? slider.getWidth(): slider.getHeight()), max);
        final double lowerValue = slider.getLowerValue();
        final double newLowerValue = Utils.clamp(min, lowerValue + position *(max-min) / 
                (slider.getOrientation() == Orientation.HORIZONTAL? slider.getWidth(): slider.getHeight()), max);
        final double higherValue = slider.getHigherValue();
        final double newHigherValue = Utils.clamp(min, higherValue + position*(max-min) / 
                (slider.getOrientation() == Orientation.HORIZONTAL? slider.getWidth(): slider.getHeight()), max);
        final double highestValue = slider.getHighestValue();
        final double newHighestValue = Utils.clamp(min, highestValue + position*(max-min) / 
                (slider.getOrientation() == Orientation.HORIZONTAL? slider.getWidth(): slider.getHeight()), max);
        
        if (newLowestValue <= min || newHighestValue >= max) return;
        slider.setLowestValueChanging(true);
        slider.setLowerValueChanging(true);
        slider.setHigherValueChanging(true);
        slider.setHighestValueChanging(true);
        slider.setLowestValue(newLowestValue);
        slider.setLowerValue(newLowerValue);
        slider.setHigherValue(newHigherValue);
        slider.setHighestValue(newHighestValue);
    }
    
      public void confirmRange() {
          MultiRangeSlider slider = (MultiRangeSlider) getNode();

        slider.setLowestValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setLowestValue(snapValueToTicks(slider.getLowestValue()));
        }
        slider.setLowerValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setLowerValue(snapValueToTicks(slider.getLowerValue()));
        }
        slider.setHigherValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setHigherValue(snapValueToTicks(slider.getHigherValue()));
        }
        slider.setHighestValueChanging(false);
        if (slider.isSnapToTicks()) {
            slider.setHighestValue(snapValueToTicks(slider.getHighestValue()));
        }        

    }
	
      public enum FocusedChild {
          LOWEST_THUMB,
          LOWER_THUMB,
          HIGHER_THUMB,
          HIGHEST_THUMB,
          RANGE_BAR_LOWEST,
          RANGE_BAR_LOWER,
          RANGE_BAR_MID,
          RANGE_BAR_HIGHER,
          RANGE_BAR_HIGHEST,
          NONE
      }

}
