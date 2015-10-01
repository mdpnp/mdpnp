package org.mdpnp.apps.testapp.vital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;

import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.util.Utils;

/**
 * The RangeSlider control is simply a JavaFX {@link Slider} control with support
 * for two 'thumbs', rather than one. A thumb is the non-technical name for the
 * draggable area inside the Slider / RangeSlider that allows for a value to be
 * set. 
 * 
 * <p>Because the RangeSlider has two thumbs, it also has a few additional rules
 * and user interactions:
 * 
 * <ol>
 *   <li>The 'lower value' thumb can not move past the 'higher value' thumb.
 *   <li>Whereas the {@link Slider} control only has one 
 *       {@link Slider#valueProperty() value} property, the RangeSlider has a 
 *       {@link #lowValueProperty() low value} and a 
 *       {@link #highValueProperty() high value} property, not surprisingly 
 *       represented by the 'low value' and 'high value' thumbs.
 *   <li>The area between the low and high values represents the allowable range.
 *       For example, if the low value is 2 and the high value is 8, then the
 *       allowable range is between 2 and 8. 
 *   <li>The allowable range area is rendered differently. This area is able to 
 *       be dragged with mouse / touch input to allow for the entire range to
 *       be modified. For example, following on from the previous example of the
 *       allowable range being between 2 and 8, if the user drags the range bar
 *       to the right, the low value will adjust to 3, and the high value 9, and
 *       so on until the user stops adjusting. 
 * </ol>
 * 
 * <h3>Screenshots</h3>
 * Because the RangeSlider supports both horizontal and vertical 
 * {@link #orientationProperty() orientation}, there are two screenshots below:
 * 
 * <table border="0">
 *   <tr>
 *     <td width="75" valign="center"><strong>Horizontal:</strong></td>
 *     <td><img src="rangeSlider-horizontal.png"></td>
 *   </tr>
 *   <tr>
 *     <td width="75" valign="top"><strong>Vertical:</strong></td>
 *     <td><img src="rangeSlider-vertical.png"></td>
 *   </tr>
 * </table>
 * 
 * <h3>Code Samples</h3>
 * Instantiating a RangeSlider is simple. The first decision is to decide whether
 * a horizontal or a vertical track is more appropriate. By default RangeSlider
 * instances are horizontal, but this can be changed by setting the 
 * {@link #orientationProperty() orientation} property.
 * 
 * <p>Once the orientation is determined, the next most important decision is
 * to determine what the {@link #minProperty() min} / {@link #maxProperty() max}
 * and default {@link #lowValueProperty() low} / {@link #highValueProperty() high}
 * values are. The min / max values represent the smallest and largest legal
 * values for the thumbs to be set to, whereas the low / high values represent
 * where the thumbs are currently, within the bounds of the min / max values.
 * Because all four values are required in all circumstances, they are all
 * required parameters to instantiate a RangeSlider: the constructor takes
 * four doubles, representing min, max, lowValue and highValue (in that order).
 * 
 * <p>For example, here is a simple horizontal RangeSlider that has a minimum
 * value of 0, a maximum value of 100, a low value of 10 and a high value of 90: 
 * 
 * <pre>{@code final RangeSlider hSlider = new RangeSlider(0, 100, 10, 90);}</pre>
 * 
 * <p>To configure the hSlider to look like the RangeSlider in the horizontal
 * RangeSlider screenshot above only requires a few additional properties to be 
 * set:
 * 
 * <pre>
 * {@code
 * final RangeSlider hSlider = new RangeSlider(0, 100, 10, 90);
 * hSlider.setShowTickMarks(true);
 * hSlider.setShowTickLabels(true);
 * hSlider.setBlockIncrement(10);}</pre>
 * 
 * <p>To create a vertical slider, simply do the following:
 * 
 * <pre>
 * {@code
 * final RangeSlider vSlider = new RangeSlider(0, 200, 30, 150);
 * vSlider.setOrientation(Orientation.VERTICAL);}</pre>
 * 
 * <p>This code creates a RangeSlider with a min value of 0, a max value of 200,
 * a low value of 30, and a high value of 150.
 * 
 * @see Slider
 */
public class MultiRangeSlider extends Control {
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a new RangeSlider instance using default values of 0.0, 0.25, 0.75
     * and 1.0 for min/lowValue/highValue/max, respectively. 
     */
    public MultiRangeSlider() {
        this(0, 1.0, 0.20, 0.40, 0.60, 0.80);
    }

    /**
     * Instantiates a default, horizontal RangeSlider with the specified 
     * min/max/low/high values.
     * 
     * @param min The minimum allowable value that the RangeSlider will allow.
     * @param max The maximum allowable value that the RangeSlider will allow.
     * @param lowValue The initial value for the low value in the RangeSlider.
     * @param highValue The initial value for the high value in the RangeSlider.
     */
    public MultiRangeSlider(double min, double max, double lowestValue, double lowerValue, double higherValue, double highestValue) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        
        setMax(max);
        setMin(min);
        adjustValues();
        setLowerValue(lowerValue);
        setLowestValue(lowestValue);
        setHigherValue(higherValue);
        setHighestValue(highestValue);
    }
    
//    /**
//     * {@inheritDoc}
//     */
//    @Override protected String getUserAgentStylesheet() {
//        return RangeSlider.class.getResource("rangeslider.css").toExternalForm(); //$NON-NLS-1$
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override protected Skin<?> createDefaultSkin() {
        return new MultiRangeSliderSkin(this);
    }
  
    
    
    /***************************************************************************
     *                                                                         *
     * New properties (over and above what is in Slider)                       *
     *                                                                         *
     **************************************************************************/
    
    // --- low value
    
    private BooleanProperty lowerValueVisible = new SimpleBooleanProperty(this, "lowerValueVisible");
    
    public final BooleanProperty lowerValueVisibleProperty() {
        return lowerValueVisible;
    }    
    
    public boolean isLowerValueVisible() {
        return lowerValueVisible.get();
    }
    /**
     * The low value property represents the current position of the low value
     * thumb, and is within the allowable range as specified by the
     * {@link #minProperty() min} and {@link #maxProperty() max} properties. By
     * default this value is 0.
     */
    public final DoubleProperty lowerValueProperty() {
        return lowerValue;
    }
    private DoubleProperty lowerValue = new SimpleDoubleProperty(this, "lowerValue", 0.0D) { //$NON-NLS-1$
        @Override protected void invalidated() {
            adjustLowerValues();
        }
    };
    
    public final DoubleProperty lowestValueProperty() {
        return lowestValue;
    }
    private BooleanProperty lowestValueVisible = new SimpleBooleanProperty(this, "lowestValueVisible");
    
    public final BooleanProperty lowestValueVisibleProperty() {
        return lowestValueVisible;
    }
    
    public boolean isLowestValueVisible() {
        return lowestValueVisible.get();
    }
    
    private DoubleProperty lowestValue = new SimpleDoubleProperty(this, "lowestValue", 0.0D) { //$NON-NLS-1$
        @Override protected void invalidated() {
            adjustLowestValues();
        }
    };    
    
    /**
     * Sets the low value for the range slider, which may or may not be clamped
     * to be within the allowable range as specified by the
     * {@link #minProperty() min} and {@link #maxProperty() max} properties.
     */
    public final void setLowerValue(double d) {
        lowerValueProperty().set(d);
    }
    
    public final void setLowestValue(double d) {
        lowestValueProperty().set(d);
    }    

    /**
     * Returns the current low value for the range slider.
     */
    public final double getLowerValue() {
        return lowerValue != null ? lowerValue.get() : 0.0D;
    }
    
    public final double getLowestValue() {
        return lowestValue != null ? lowestValue.get() : 0.0D;
    }    

    
    
    // --- low value changing
    /**
     * When true, indicates the current low value of this RangeSlider is changing.
     * It provides notification that the low value is changing. Once the low 
     * value is computed, it is set back to false.
     */
    public final BooleanProperty lowerValueChangingProperty() {
        if (lowerValueChanging == null) {
            lowerValueChanging = new SimpleBooleanProperty(this, "lowerValueChanging", false); //$NON-NLS-1$
        }
        return lowerValueChanging;
    }
    
    private BooleanProperty lowerValueChanging;
    
    public final BooleanProperty lowestValueChangingProperty() {
        if (lowestValueChanging == null) {
            lowestValueChanging = new SimpleBooleanProperty(this, "lowestValueChanging", false); //$NON-NLS-1$
        }
        return lowestValueChanging;
    }
    
    private BooleanProperty lowestValueChanging;    

    /**
     * Call this when the low value is changing.
     * @param value True if the low value is changing, false otherwise.
     */
    public final void setLowerValueChanging(boolean value) {
        lowerValueChangingProperty().set(value);
    }
    
    public final void setLowestValueChanging(boolean value) {
        lowestValueChangingProperty().set(value);
    }    

    /**
     * Returns whether or not the low value of this RangeSlider is currently
     * changing.
     */
    public final boolean isLowerValueChanging() {
        return lowerValueChanging == null ? false : lowerValueChanging.get();
    }

    public final boolean isLowestValueChanging() {
        return lowestValueChanging == null ? false : lowestValueChanging.get();
    }
    
    // --- high value
    private BooleanProperty higherValueVisible = new SimpleBooleanProperty(this, "higherValueVisible");
    
    public final BooleanProperty higherValueVisibleProperty() {
        return higherValueVisible;
    }
    
    public final boolean isHigherValueVisible() {
        return higherValueVisible.get();
    }
    /**
     * The high value property represents the current position of the high value
     * thumb, and is within the allowable range as specified by the
     * {@link #minProperty() min} and {@link #maxProperty() max} properties. By
     * default this value is 100.
     */
    public final DoubleProperty higherValueProperty() {
        return higherValue;
    }
    private DoubleProperty higherValue = new SimpleDoubleProperty(this, "higherValue", 100D) { //$NON-NLS-1$
        @Override protected void invalidated() {
            adjustHigherValues();
        }
        
        @Override public Object getBean() {
            return MultiRangeSlider.this;
        }

        @Override public String getName() {
            return "higherValue"; //$NON-NLS-1$
        }
    };
    
    private BooleanProperty highestValueVisible = new SimpleBooleanProperty(this, "highestValueVisible");
    
    public final BooleanProperty highestValueVisibleProperty() {
        return highestValueVisible;
    }
    
    public boolean isHighestValueVisible() {
        return highestValueVisible.get();
    }
    
    public final DoubleProperty highestValueProperty() {
        return highestValue;
    }
    private DoubleProperty highestValue = new SimpleDoubleProperty(this, "highestValue", 100D) { //$NON-NLS-1$
        @Override protected void invalidated() {
            adjustHighestValues();
        }
        
        @Override public Object getBean() {
            return MultiRangeSlider.this;
        }

        @Override public String getName() {
            return "highestValue"; //$NON-NLS-1$
        }
    };    
    
    /**
     * Sets the high value for the range slider, which may or may not be clamped
     * to be within the allowable range as specified by the
     * {@link #minProperty() min} and {@link #maxProperty() max} properties.
     */
    public final void setHigherValue(double d) {
        if (!higherValueProperty().isBound()) higherValueProperty().set(d);
    }
    public final void setHighestValue(double d) {
        if (!highestValueProperty().isBound()) highestValueProperty().set(d);
    }    

    /**
     * Returns the current high value for the range slider.
     */
    public final double getHigherValue() {
        return higherValue != null ? higherValue.get() : 100D;
    }
    public final double getHighestValue() {
        return highestValue != null ? highestValue.get() : 100D;
    }
    

    // --- high value changing
    /**
     * When true, indicates the current high value of this RangeSlider is changing.
     * It provides notification that the high value is changing. Once the high 
     * value is computed, it is set back to false.
     */
    public final BooleanProperty higherValueChangingProperty() {
        if (higherValueChanging == null) {
            higherValueChanging = new SimpleBooleanProperty(this, "higherValueChanging", false); //$NON-NLS-1$
        }
        return higherValueChanging;
    }
    private BooleanProperty higherValueChanging;
    
    public final BooleanProperty highestValueChangingProperty() {
        if (highestValueChanging == null) {
            highestValueChanging = new SimpleBooleanProperty(this, "highestValueChanging", false); //$NON-NLS-1$
        }
        return highestValueChanging;
    }
    private BooleanProperty highestValueChanging;    

    /**
     * Call this when high low value is changing.
     * @param value True if the high value is changing, false otherwise.
     */
    public final void setHigherValueChanging(boolean value) {
        higherValueChangingProperty().set(value);
    }
    public final void setHighestValueChanging(boolean value) {
        highestValueChangingProperty().set(value);
    }

    /**
     * Returns whether or not the high value of this RangeSlider is currently
     * changing.
     */
    public final boolean isHigherValueChanging() {
        return higherValueChanging == null ? false : higherValueChanging.get();
    }
    public final boolean isHighestValueChanging() {
        return highestValueChanging == null ? false : highestValueChanging.get();
    }
    
    
    
    
    
    /***************************************************************************
     *                                                                         *
     * New public API                                                          *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Increments the {@link #lowValueProperty() low value} by the 
     * {@link #blockIncrementProperty() block increment} amount.
     */
    public void incrementLowerValue() {
        adjustLowerValue(getLowerValue() + getBlockIncrement());
    }
    public void incrementLowestValue() {
        adjustLowestValue(getLowestValue() + getBlockIncrement());
    }

    /**
     * Decrements the {@link #lowValueProperty() low value} by the 
     * {@link #blockIncrementProperty() block increment} amount.
     */
    public void decrementLowerValue() {
        adjustLowerValue(getLowerValue() - getBlockIncrement());
    }
    public void decrementLowestValue() {
        adjustLowestValue(getLowestValue() - getBlockIncrement());
    }
    
    /**
     * Increments the {@link #highValueProperty() high value} by the 
     * {@link #blockIncrementProperty() block increment} amount.
     */
    public void incrementHigherValue() {
        adjustHigherValue(getHigherValue() + getBlockIncrement());
    }
    public void incrementHighestValue() {
        adjustHighestValue(getHighestValue() + getBlockIncrement());
    }    

    /**
     * Decrements the {@link #highValueProperty() high value} by the 
     * {@link #blockIncrementProperty() block increment} amount.
     */
    public void decrementHigherValue() {
        adjustHigherValue(getHigherValue() - getBlockIncrement());
    }
    public void decrementHighestValue() {
        adjustHighestValue(getHighestValue() - getBlockIncrement());
    }    
    
    /**
     * Adjusts {@link #lowValueProperty() lowValue} to match <code>newValue</code>,
     * or as closely as possible within the constraints imposed by the 
     * {@link #minProperty() min} and {@link #maxProperty() max} properties. 
     * This function also takes into account 
     * {@link #snapToTicksProperty() snapToTicks}, which is the main difference 
     * between <code>adjustLowValue</code> and 
     * {@link #setLowValue(double) setLowValue}.
     */
    public void adjustLowerValue(double newValue) {
        double d1 = getMin();
        double d2 = getMax();
        if (d2 <= d1) {
            // no-op
        } else {
            newValue = newValue >= d1 ? newValue : d1;
            newValue = newValue <= d2 ? newValue : d2;
            setLowerValue(snapValueToTicks(newValue));
        }
    }
    public void adjustLowestValue(double newValue) {
        double d1 = getMin();
        double d2 = getMax();
        if (d2 <= d1) {
            // no-op
        } else {
            newValue = newValue >= d1 ? newValue : d1;
            newValue = newValue <= d2 ? newValue : d2;
            setLowestValue(snapValueToTicks(newValue));
        }
    }    

    /**
     * Adjusts {@link #highValueProperty() highValue} to match <code>newValue</code>,
     * or as closely as possible within the constraints imposed by the 
     * {@link #minProperty() min} and {@link #maxProperty() max} properties. 
     * This function also takes into account 
     * {@link #snapToTicksProperty() snapToTicks}, which is the main difference 
     * between <code>adjustHighValue</code> and 
     * {@link #setHighValue(double) setHighValue}.
     */
    public void adjustHigherValue(double newValue) {
        double d1 = getMin();
        double d2 = getMax();
        if (d2 <= d1) {
            // no-op
        } else {
            newValue = newValue >= d1 ? newValue : d1;
            newValue = newValue <= d2 ? newValue : d2;
            setHigherValue(snapValueToTicks(newValue));
        }
    }
    
    public void adjustHighestValue(double newValue) {
        double d1 = getMin();
        double d2 = getMax();
        if (d2 <= d1) {
            // no-op
        } else {
            newValue = newValue >= d1 ? newValue : d1;
            newValue = newValue <= d2 ? newValue : d2;
            setHighestValue(snapValueToTicks(newValue));
        }
    }    

    
    
    /***************************************************************************
     *                                                                         *
     * Properties copied from Slider (and slightly edited)                     *
     *                                                                         *
     **************************************************************************/
    
    private DoubleProperty max;
    
    /**
     * Sets the maximum value for this Slider.
     * @param value 
     */
    public final void setMax(double value) {
        maxProperty().set(value);
    }

    /**
     * @return The maximum value of this slider. 100 is returned if
     * the maximum value has never been set.
     */
    public final double getMax() {
        return max == null ? 100 : max.get();
    }

    /**
     * 
     * @return A DoubleProperty representing the maximum value of this Slider. 
     * This must be a value greater than {@link #minProperty() min}.
     */
    public final DoubleProperty maxProperty() {
        if (max == null) {
            max = new DoublePropertyBase(100) {
                @Override protected void invalidated() {
                    if (get() < getMin()) {
                        setMin(get());
                    }
                    adjustValues();
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "max"; //$NON-NLS-1$
                }
            };
        }
        return max;
    }

    private DoubleProperty min;
    
    /**
     * Sets the minimum value for this Slider.
     * @param value 
     */
    public final void setMin(double value) {
        minProperty().set(value);
    }

    /**
     * 
     * @return the minimum value for this Slider. 0 is returned if the minimum
     * has never been set.
     */
    public final double getMin() {
        return min == null ? 0 : min.get();
    }

    /**
     * 
     * @return A DoubleProperty representing The minimum value of this Slider. 
     * This must be a value less than {@link #maxProperty() max}.
     */
    public final DoubleProperty minProperty() {
        if (min == null) {
            min = new DoublePropertyBase(0) {
                @Override protected void invalidated() {
                    if (get() > getMax()) {
                        setMax(get());
                    }
                    adjustValues();
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "min"; //$NON-NLS-1$
                }
            };
        }
        return min;
    }
    
    /**
     * 
     */
    private BooleanProperty snapToTicks;
    
    /**
     * Sets the value of SnapToTicks. 
     * @see #snapToTicksProperty() 
     * @param value 
     */
    public final void setSnapToTicks(boolean value) {
        snapToTicksProperty().set(value);
    }

    /**
     * 
     * @return the value of SnapToTicks.
     * @see #snapToTicksProperty() 
     */
    public final boolean isSnapToTicks() {
        return snapToTicks == null ? false : snapToTicks.get();
    }

    /**
     * Indicates whether the {@link #lowValueProperty()} value} / 
     * {@link #highValueProperty()} value} of the {@code Slider} should always
     * be aligned with the tick marks. This is honored even if the tick marks
     * are not shown.
     * @return A BooleanProperty.
     */
    public final BooleanProperty snapToTicksProperty() {
        if (snapToTicks == null) {
            snapToTicks = new StyleableBooleanProperty(false) {
                @Override public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.SNAP_TO_TICKS;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "snapToTicks"; //$NON-NLS-1$
                }
            };
        }
        return snapToTicks;
    }
    /**
     * 
     */
    private DoubleProperty majorTickUnit;
    
    /**
     * Sets the unit distance between major tick marks.
     * @param value 
     * @see #majorTickUnitProperty() 
     */
    public final void setMajorTickUnit(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0."); //$NON-NLS-1$
        }
        majorTickUnitProperty().set(value);
    }

    /**
     * @see #majorTickUnitProperty() 
     * @return The unit distance between major tick marks.
     */
    public final double getMajorTickUnit() {
        return majorTickUnit == null ? 25 : majorTickUnit.get();
    }

    /**
     * The unit distance between major tick marks. For example, if
     * the {@link #minProperty() min} is 0 and the {@link #maxProperty() max} is 100 and the
     * {@link #majorTickUnitProperty() majorTickUnit} is 25, then there would be 5 tick marks: one at
     * position 0, one at position 25, one at position 50, one at position
     * 75, and a final one at position 100.
     * <p>
     * This value should be positive and should be a value less than the
     * span. Out of range values are essentially the same as disabling
     * tick marks.
     * 
     * @return A DoubleProperty
     */
    public final DoubleProperty majorTickUnitProperty() {
        if (majorTickUnit == null) {
            majorTickUnit = new StyleableDoubleProperty(25) {
                @Override public void invalidated() {
                    if (get() <= 0) {
                        throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0."); //$NON-NLS-1$
                    }
                }
                
                @Override public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return StyleableProperties.MAJOR_TICK_UNIT;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "majorTickUnit"; //$NON-NLS-1$
                }
            };
        }
        return majorTickUnit;
    }
    /**
     * 
     */
    private IntegerProperty minorTickCount;
    
    /**
     * Sets the number of minor ticks to place between any two major ticks.
     * @param value 
     * @see #minorTickCountProperty() 
     */
    public final void setMinorTickCount(int value) {
        minorTickCountProperty().set(value);
    }

    /**
     * @see #minorTickCountProperty() 
     * @return The number of minor ticks to place between any two major ticks.
     */
    public final int getMinorTickCount() {
        return minorTickCount == null ? 3 : minorTickCount.get();
    }

    /**
     * The number of minor ticks to place between any two major ticks. This
     * number should be positive or zero. Out of range values will disable
     * disable minor ticks, as will a value of zero.
     * @return An InterProperty
     */
    public final IntegerProperty minorTickCountProperty() {
        if (minorTickCount == null) {
            minorTickCount = new StyleableIntegerProperty(3) {
                @Override public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.MINOR_TICK_COUNT;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "minorTickCount"; //$NON-NLS-1$
                }
            };
        }
        return minorTickCount;
    }
    /**
     *
     */
    private DoubleProperty blockIncrement;
    
    /**
     * Sets the amount by which to adjust the slider if the track of the slider is
     * clicked.
     * @param value 
     * @see #blockIncrementProperty() 
     */
    public final void setBlockIncrement(double value) {
        blockIncrementProperty().set(value);
    }

    /**
     * @see #blockIncrementProperty() 
     * @return The amount by which to adjust the slider if the track of the slider is
     * clicked.
     */
    public final double getBlockIncrement() {
        return blockIncrement == null ? 10 : blockIncrement.get();
    }

    /**
     *  The amount by which to adjust the slider if the track of the slider is
     * clicked. This is used when manipulating the slider position using keys. If
     * {@link #snapToTicksProperty() snapToTicks} is true then the nearest tick mark to the adjusted
     * value will be used.
     * @return A DoubleProperty
     */
    public final DoubleProperty blockIncrementProperty() {
        if (blockIncrement == null) {
            blockIncrement = new StyleableDoubleProperty(10) {
                @Override public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.BLOCK_INCREMENT;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "blockIncrement"; //$NON-NLS-1$
                }
            };
        }
        return blockIncrement;
    }
    
    /**
     * 
     */
    private ObjectProperty<Orientation> orientation;
    
    /**
     * Sets the orientation of the Slider.
     * @param value 
     */
    public final void setOrientation(Orientation value) {
        orientationProperty().set(value);
    }

    /**
     * 
     * @return The orientation of the Slider. {@link Orientation#HORIZONTAL} is 
     * returned by default.
     */
    public final Orientation getOrientation() {
        return orientation == null ? Orientation.HORIZONTAL : orientation.get();
    }

    /**
     * The orientation of the {@code Slider} can either be horizontal
     * or vertical.
     * @return An Objectproperty representing the orientation of the Slider.
     */
    public final ObjectProperty<Orientation> orientationProperty() {
        if (orientation == null) {
            orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
                @Override protected void invalidated() {
                    final boolean vertical = (get() == Orientation.VERTICAL);
                    pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, vertical);
                    pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, ! vertical);
                }
                
                @Override public CssMetaData<? extends Styleable, Orientation> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.ORIENTATION;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "orientation"; //$NON-NLS-1$
                }
            };
        }
        return orientation;
    }
    
    private BooleanProperty showTickLabels;
    
    /**
     * Sets whether labels of tick marks should be shown or not.
     * @param value 
     */
    public final void setShowTickLabels(boolean value) {
        showTickLabelsProperty().set(value);
    }

    /**
     * @return whether labels of tick marks are being shown.
     */
    public final boolean isShowTickLabels() {
        return showTickLabels == null ? false : showTickLabels.get();
    }

    /**
     * Indicates that the labels for tick marks should be shown. Typically a
     * {@link Skin} implementation will only show labels if
     * {@link #showTickMarksProperty() showTickMarks} is also true.
     * @return A BooleanProperty
     */
    public final BooleanProperty showTickLabelsProperty() {
        if (showTickLabels == null) {
            showTickLabels = new StyleableBooleanProperty(false) {
                @Override public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.SHOW_TICK_LABELS;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "showTickLabels"; //$NON-NLS-1$
                }
            };
        }
        return showTickLabels;
    }
    /**
     * 
     */
    private BooleanProperty showTickMarks;
    
    /**
     * Specifies whether the {@link Skin} implementation should show tick marks.
     * @param value 
     */
    public final void setShowTickMarks(boolean value) {
        showTickMarksProperty().set(value);
    }

    /**
     * 
     * @return whether the {@link Skin} implementation should show tick marks.
     */
    public final boolean isShowTickMarks() {
        return showTickMarks == null ? false : showTickMarks.get();
    }

    /**
     * @return A BooleanProperty that specifies whether the {@link Skin} 
     * implementation should show tick marks.
     */
    public final BooleanProperty showTickMarksProperty() {
        if (showTickMarks == null) {
            showTickMarks = new StyleableBooleanProperty(false) {
                @Override public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRangeSlider.StyleableProperties.SHOW_TICK_MARKS;
                }

                @Override public Object getBean() {
                    return MultiRangeSlider.this;
                }

                @Override public String getName() {
                    return "showTickMarks"; //$NON-NLS-1$
                }
            };
        }
        return showTickMarks;
    }
    

    
     /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/    
    
    /**
     * Ensures that min is always < max, that value is always
     * somewhere between the two, and that if snapToTicks is set then the
     * value will always be set to align with a tick mark.
     */
    private void adjustValues() {
        adjustLowestValues();
        adjustLowerValues();
        adjustHigherValues();
        adjustHighestValues();
    }

    private void adjustLowestValues() {
        /**
         * We first look if the LowValue is between the min and max.
         */
        if (getLowestValue() < getMin() || getLowestValue() > getMax()) {
            double value = Utils.clamp(getMin(), getLowestValue(), getMax());
            setLowestValue(value);
        /**
         * If the LowValue seems right, we check if it's not superior to
         * HighValue ONLY if the highValue itself is right. Because it may
         * happen that the highValue has not yet been computed and is
         * wrong, and therefore force the lowValue to change in a wrong way
         * which may end up in an infinite loop.
         */
        } else if (getLowestValue() > getLowerValue() && (getLowerValue() >= getMin() && getLowerValue() <= getMax())) {
            double value = Utils.clamp(getMin(), getLowestValue(), getLowerValue());
            setLowestValue(value);
        }
    }
    
    private void adjustLowerValues() {
        /**
         * We first look if the LowValue is between the min and max.
         */
        if (getLowerValue() < getMin() || getLowerValue() > getMax()) {
            double value = Utils.clamp(getMin(), getLowerValue(), getMax());
            setLowerValue(value);
        /**
         * If the LowValue seems right, we check if it's not superior to
         * HighValue ONLY if the highValue itself is right. Because it may
         * happen that the highValue has not yet been computed and is
         * wrong, and therefore force the lowValue to change in a wrong way
         * which may end up in an infinite loop.
         */
        } else if (getLowerValue() > getHigherValue() && (getHigherValue() >= getMin() && getHigherValue() <= getMax())) {
            double value = Utils.clamp(getMin(), getLowerValue(), getHigherValue());
            setLowerValue(value);
        } else if (getLowerValue() < getLowestValue() && (getLowestValue() >= getMin() && getHigherValue() <= getMax())) {
            double value = Utils.clamp(getLowestValue(), getLowerValue(), getHigherValue());
            setLowerValue(value);
        }
    }
    
    private double snapValueToTicks(double d) {
        double d1 = d;
        if (isSnapToTicks()) {
            double d2 = 0.0D;
            if (getMinorTickCount() != 0) {
                d2 = getMajorTickUnit() / (double) (Math.max(getMinorTickCount(), 0) + 1);
            } else {
                d2 = getMajorTickUnit();
            }
            int i = (int) ((d1 - getMin()) / d2);
            double d3 = (double) i * d2 + getMin();
            double d4 = (double) (i + 1) * d2 + getMin();
            d1 = Utils.nearest(d3, d1, d4);
        }
        return Utils.clamp(getMin(), d1, getMax());
    }

    private void adjustHigherValues() {
        if (getHigherValue() < getMin() || getHigherValue() > getMax()) {
            setHigherValue(Utils.clamp(getMin(), getHigherValue(), getMax()));
        } else if (getHigherValue() < getLowerValue() && (getLowerValue() >= getMin() && getLowerValue() <= getMax())) {
            setHigherValue(Utils.clamp(getLowerValue(), getHigherValue(), getMax()));
        } else if (getHigherValue() > getHighestValue() && (getHighestValue() >= getMin() && getHighestValue() < getMax())) {
            setHigherValue(Utils.clamp(getLowerValue(), getHigherValue(), getHighestValue()));
        }
    }
    
    private void adjustHighestValues() {
        if (getHighestValue() < getMin() || getHighestValue() > getMax()) {
            setHighestValue(Utils.clamp(getMin(), getHighestValue(), getMax()));
        } else if (getHighestValue() < getHigherValue() && (getHigherValue() >= getMin() && getHigherValue() <= getMax())) {
            setHighestValue(Utils.clamp(getHigherValue(), getHighestValue(), getMax()));
        }
    }    

    
    
    /**************************************************************************
    *                                                                         *
    * Stylesheet Handling                                                     *
    *                                                                         *
    **************************************************************************/
    
    private static final String DEFAULT_STYLE_CLASS = "multirange-slider"; //$NON-NLS-1$
    
    private static class StyleableProperties {
        private static final CssMetaData<MultiRangeSlider,Number> BLOCK_INCREMENT =
            new CssMetaData<MultiRangeSlider,Number>("-fx-block-increment", //$NON-NLS-1$
                SizeConverter.getInstance(), 10.0) {

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.blockIncrement == null || !n.blockIncrement.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Number> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Number>)n.blockIncrementProperty();
            }
        };
        
        private static final CssMetaData<MultiRangeSlider,Boolean> SHOW_TICK_LABELS =
            new CssMetaData<MultiRangeSlider,Boolean>("-fx-show-tick-labels", //$NON-NLS-1$
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.showTickLabels == null || !n.showTickLabels.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Boolean> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Boolean>)n.showTickLabelsProperty();
            }
        };
                    
        private static final CssMetaData<MultiRangeSlider,Boolean> SHOW_TICK_MARKS =
            new CssMetaData<MultiRangeSlider,Boolean>("-fx-show-tick-marks", //$NON-NLS-1$
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.showTickMarks == null || !n.showTickMarks.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Boolean> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Boolean>)n.showTickMarksProperty();
            }
        };
            
        private static final CssMetaData<MultiRangeSlider,Boolean> SNAP_TO_TICKS =
            new CssMetaData<MultiRangeSlider,Boolean>("-fx-snap-to-ticks", //$NON-NLS-1$
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.snapToTicks == null || !n.snapToTicks.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Boolean> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Boolean>)n.snapToTicksProperty();
            }
        };
        
        private static final CssMetaData<MultiRangeSlider,Number> MAJOR_TICK_UNIT =
            new CssMetaData<MultiRangeSlider,Number>("-fx-major-tick-unit", //$NON-NLS-1$
                SizeConverter.getInstance(), 25.0) {

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.majorTickUnit == null || !n.majorTickUnit.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Number> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Number>)n.majorTickUnitProperty();
            }
        };
        
        private static final CssMetaData<MultiRangeSlider,Number> MINOR_TICK_COUNT =
            new CssMetaData<MultiRangeSlider,Number>("-fx-minor-tick-count", //$NON-NLS-1$
                SizeConverter.getInstance(), 3.0) {

            @SuppressWarnings("deprecation")
            @Override public void set(MultiRangeSlider node, Number value, StyleOrigin origin) {
                super.set(node, value.intValue(), origin);
            } 
            
            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.minorTickCount == null || !n.minorTickCount.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Number> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Number>)n.minorTickCountProperty();
            }
        };
        
        private static final CssMetaData<MultiRangeSlider,Orientation> ORIENTATION =
            new CssMetaData<MultiRangeSlider,Orientation>("-fx-orientation", //$NON-NLS-1$
                new EnumConverter<>(Orientation.class), 
                Orientation.HORIZONTAL) {

            @Override public Orientation getInitialValue(MultiRangeSlider node) {
                // A vertical Slider should remain vertical 
                return node.getOrientation();
            }

            @Override public boolean isSettable(MultiRangeSlider n) {
                return n.orientation == null || !n.orientation.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override public StyleableProperty<Orientation> getStyleableProperty(MultiRangeSlider n) {
                return (StyleableProperty<Orientation>)n.orientationProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = 
                    new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(BLOCK_INCREMENT);
            styleables.add(SHOW_TICK_LABELS);
            styleables.add(SHOW_TICK_MARKS);
            styleables.add(SNAP_TO_TICKS);
            styleables.add(MAJOR_TICK_UNIT);
            styleables.add(MINOR_TICK_COUNT);
            styleables.add(ORIENTATION);

            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
    

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * RT-19263
     * @deprecated This is an experimental API that is not intended for general use and is subject to change in future versions
     */
    @Deprecated
    @Override protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("vertical"); //$NON-NLS-1$
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("horizontal"); //$NON-NLS-1$
}