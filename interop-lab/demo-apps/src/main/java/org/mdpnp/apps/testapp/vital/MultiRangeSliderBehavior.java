package org.mdpnp.apps.testapp.vital;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.END;
import static javafx.scene.input.KeyCode.F4;
import static javafx.scene.input.KeyCode.HOME;
import static javafx.scene.input.KeyCode.KP_DOWN;
import static javafx.scene.input.KeyCode.KP_LEFT;
import static javafx.scene.input.KeyCode.KP_RIGHT;
import static javafx.scene.input.KeyCode.KP_UP;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.UP;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.behavior.OrientedKeyBinding;
import com.sun.javafx.util.Utils;

public class MultiRangeSliderBehavior extends BehaviorBase<MultiRangeSlider> {
    
     /**************************************************************************
     *                          Setup KeyBindings                             *
     *                                                                        *
     * We manually specify the focus traversal keys because Slider has        *
     * different usage for up/down arrow keys.                                *
     *************************************************************************/
    private static final List<KeyBinding> RANGESLIDER_BINDINGS = new ArrayList<>();
    static {
        RANGESLIDER_BINDINGS.add(new KeyBinding(F4, "TraverseDebug").alt().ctrl().shift()); //$NON-NLS-1$

        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(LEFT, "DecrementValue")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_LEFT, "DecrementValue")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(UP, "IncrementValue").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_UP, "IncrementValue").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(RIGHT, "IncrementValue")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_RIGHT, "IncrementValue")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(DOWN, "DecrementValue").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_DOWN, "DecrementValue").vertical()); //$NON-NLS-1$

        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(LEFT, "TraverseLeft").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_LEFT, "TraverseLeft").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(UP, "TraverseUp")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_UP, "TraverseUp")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(RIGHT, "TraverseRight").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_RIGHT, "TraverseRight").vertical()); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(DOWN, "TraverseDown")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new RangeSliderKeyBinding(KP_DOWN, "TraverseDown")); //$NON-NLS-1$

        RANGESLIDER_BINDINGS.add(new KeyBinding(HOME, KEY_RELEASED, "Home")); //$NON-NLS-1$
        RANGESLIDER_BINDINGS.add(new KeyBinding(END, KEY_RELEASED, "End")); //$NON-NLS-1$
    }
    
    public MultiRangeSliderBehavior(MultiRangeSlider slider) {
        super(slider, RANGESLIDER_BINDINGS);
    }

    @Override protected void callAction(String s) {
        if ("Home".equals(s) || "Home2".equals(s)) home(); //$NON-NLS-1$ //$NON-NLS-2$
        else if ("End".equals(s) || "End2".equals(s)) end(); //$NON-NLS-1$ //$NON-NLS-2$
        else if ("IncrementValue".equals(s) || "IncrementValue2".equals(s)) incrementValue(); //$NON-NLS-1$ //$NON-NLS-2$
        else if ("DecrementValue".equals(s) || "DecrementValue2".equals(s)) decrementValue(); //$NON-NLS-1$ //$NON-NLS-2$
        else super.callAction(s);
    }
     
    /**************************************************************************
     *                         State and Functions                            *
     *************************************************************************/

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
        final MultiRangeSlider rangeSlider = getControl();
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
        final MultiRangeSlider rangeSlider = getControl();
        if (!rangeSlider.isFocused())  rangeSlider.requestFocus();
        rangeSlider.setLowerValueChanging(true);
    }
    public void lowestThumbPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        final MultiRangeSlider rangeSlider = getControl();
        if (!rangeSlider.isFocused())  rangeSlider.requestFocus();
        rangeSlider.setLowestValueChanging(true);
    }    

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *        track and 1.0 being the end
     */
    public void lowerThumbDragged(MouseEvent e, double position) {
        final MultiRangeSlider rangeSlider = getControl();
        double newValue = Utils.clamp(rangeSlider.getMin(), 
                (position * (rangeSlider.getMax() - rangeSlider.getMin())) + rangeSlider.getMin(), 
                rangeSlider.getMax());
        rangeSlider.setLowerValue(newValue);
    }
    
    public void lowestThumbDragged(MouseEvent e, double position) {
        final MultiRangeSlider rangeSlider = getControl();
        double newValue = Utils.clamp(rangeSlider.getMin(), 
                (position * (rangeSlider.getMax() - rangeSlider.getMin())) + rangeSlider.getMin(), 
                rangeSlider.getMax());
        rangeSlider.setLowestValue(newValue);
    }    
    
    /**
     * When lowThumb is released lowValueChanging should be set to false.
     */
    public void lowerThumbReleased(MouseEvent e) {
        final MultiRangeSlider rangeSlider = getControl();
        rangeSlider.setLowerValueChanging(false);
        // RT-15207 When snapToTicks is true, slider value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        if (rangeSlider.isSnapToTicks()) {
            rangeSlider.setLowerValue(snapValueToTicks(rangeSlider.getLowerValue()));
        }
    }
    
    public void lowestThumbReleased(MouseEvent e) {
        final MultiRangeSlider rangeSlider = getControl();
        rangeSlider.setLowestValueChanging(false);
        // RT-15207 When snapToTicks is true, slider value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        if (rangeSlider.isSnapToTicks()) {
            rangeSlider.setLowestValue(snapValueToTicks(rangeSlider.getLowestValue()));
        }
    }    
    
    void home() {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.adjustHighestValue(slider.getMin());
    }

    void decrementValue() {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
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

    void end() {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.adjustHighestValue(slider.getMax());
    }

    void incrementValue() {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
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

    double computeIncrement() {
        MultiRangeSlider rangeSlider = (MultiRangeSlider) getControl();
        double d = 0.0D;
        if (rangeSlider.getMinorTickCount() != 0)
            d = rangeSlider.getMajorTickUnit() / (double) (Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
        else
            d = rangeSlider.getMajorTickUnit();
        if (rangeSlider.getBlockIncrement() > 0.0D && rangeSlider.getBlockIncrement() < d)
            return d;
        else
            return rangeSlider.getBlockIncrement();
    }

    private double snapValueToTicks(double d) {
        MultiRangeSlider rangeSlider = (MultiRangeSlider) getControl();
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
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.setHigherValueChanging(false);
        if (slider.isSnapToTicks())
            slider.setHigherValue(snapValueToTicks(slider.getHigherValue()));
    }
    public void highestThumbReleased(MouseEvent e) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.setHighestValueChanging(false);
        if (slider.isSnapToTicks())
            slider.setHighestValue(snapValueToTicks(slider.getHighestValue()));
    }

    public void higherThumbPressed(MouseEvent e, double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        if (!slider.isFocused())
            slider.requestFocus();
        slider.setHigherValueChanging(true);
    }
    
    public void highestThumbPressed(MouseEvent e, double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        if (!slider.isFocused())
            slider.requestFocus();
        slider.setHighestValueChanging(true);
    }    

    public void higherThumbDragged(MouseEvent e, double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.setHigherValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
    }
    
    public void highestThumbDragged(MouseEvent e, double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
        slider.setHighestValue(Utils.clamp(slider.getMin(), position * (slider.getMax() - slider.getMin()) + slider.getMin(), slider.getMax()));
    }    
    
    public void moveRange(double position) {
        MultiRangeSlider slider = (MultiRangeSlider) getControl();
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
          MultiRangeSlider slider = (MultiRangeSlider) getControl();

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
    
    public static class RangeSliderKeyBinding extends OrientedKeyBinding {
        public RangeSliderKeyBinding(KeyCode code, String action) {
            super(code, action);
        }

        public RangeSliderKeyBinding(KeyCode code, EventType<KeyEvent> type, String action) {
            super(code, type, action);
        }

        public @Override boolean getVertical(Control control) {
            return ((MultiRangeSlider)control).getOrientation() == Orientation.VERTICAL;
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