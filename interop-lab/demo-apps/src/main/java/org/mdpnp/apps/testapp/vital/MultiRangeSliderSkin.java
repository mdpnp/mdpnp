package org.mdpnp.apps.testapp.vital;

import org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild;

import com.sun.javafx.css.StyleManager;

import javafx.beans.binding.ObjectBinding;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

public class MultiRangeSliderSkin extends SkinBase<MultiRangeSlider> {

    /* *************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    static {
        // refer to ControlsFXControl for why this is necessary
        StyleManager.getInstance().addUserAgentStylesheet(
                MultiRangeSlider.class.getResource("multirangeslider.css").toExternalForm()); //$NON-NLS-1$
    }
    
    /** Track if slider is vertical/horizontal and cause re layout */
    private NumberAxis tickLine = null;
    private double trackToTickGap = 2;

    private boolean showTickMarks;
    private double thumbWidth;
    private double thumbHeight;
    
    private Orientation orientation;

    private StackPane track;
    private double lowestThumbPos;
    private double lowerThumbPos;
    private double rangeStartLowest, rangeStartLower, rangeStartMid, rangeStartHigher, rangeStartHighest;
    private double rangeEndLowest, rangeEndLower, rangeEndMid, rangeEndHigher, rangeEndHighest;
    private ThumbPane lowestThumb;
    private ThumbPane lowerThumb;
    private ThumbPane higherThumb;
    private ThumbPane highestThumb;
    private StackPane rangeBarLowest, rangeBarLower, rangeBarMid, rangeBarHigher, rangeBarHighest; // the bar between the two thumbs, can be dragged
    
    // temp fields for mouse drag handling
    private double preDragPos;          // used as a temp value for low and high thumbs
    private Point2D preDragThumbPoint;  // in skin coordinates
    
    private FocusedChild currentFocus = FocusedChild.LOWEST_THUMB;

    private final MultiRangeSliderBehavior behavior;
    
    private boolean trackClicked = false;

    StringConverter<Number> stringConverterWrapper = new StringConverter<Number>() {
        MultiRangeSlider slider = getSkinnable();
        @Override public String toString(Number object) {
            return(object != null) ? slider.getLabelFormatter().toString(object.doubleValue()) : "";
        }
        @Override public Number fromString(String string) {
            return slider.getLabelFormatter().fromString(string);
        }
    };
    

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new SliderSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public MultiRangeSliderSkin(MultiRangeSlider control) {
        super(control);

        behavior = new MultiRangeSliderBehavior(control);
//        control.setInputMap(behavior.getInputMap());
        
        initTrack();
        rangeBarLowest = initRangeBar("range-bar-lowest");
        rangeBarLower  = initRangeBar("range-bar-lower");
        rangeBarMid    = initRangeBar("range-bar-mid");
        rangeBarHigher  = initRangeBar("range-bar-higher");
        rangeBarHighest= initRangeBar("range-bar-highest");
        initFirstThumb();
        initSecondThumb();
        // It is important that the highest thumb is "below" the higher thumb in the Z-order
        // Otherwise when both are at the max the mouse cannot be used to drag down the higher
        // trapping the highest at the max extent
        // TODO when all sliders are dragged to the lowest extent they cannot be recovered
        initFourthThumb();
        initThirdThumb();

        control.requestLayout();
        registerChangeListener(control.minProperty(), e -> {
            if (showTickMarks && tickLine != null) {
                tickLine.setLowerBound(control.getMin());
            }
            getSkinnable().requestLayout();
        });
        registerChangeListener(control.maxProperty(), e -> {
            if (showTickMarks && tickLine != null) {
                tickLine.setUpperBound(control.getMax());
            }
            getSkinnable().requestLayout();
        });
        
        registerChangeListener(control.higherValueProperty(), e -> {
            // only animate thumb if the track was clicked - not if the thumb is dragged
        	//SK - ignore animation in our case.
            positionHigherThumb();
        });
        registerChangeListener(control.highestValueProperty(), e -> {
            // only animate thumb if the track was clicked - not if the thumb is dragged
        	//SK - ignore animation in our case.
            positionHighestThumb();
        });
        
        registerChangeListener(control.lowerValueProperty(), e -> {
            // only animate thumb if the track was clicked - not if the thumb is dragged
        	//SK - ignore animation in our case.
            positionLowerThumb();
        });
        
        registerChangeListener(control.lowestValueProperty(), e -> {
            // only animate thumb if the track was clicked - not if the thumb is dragged
        	//SK - ignore animation in our case.
            positionLowestThumb();
        });
        
        registerChangeListener(control.orientationProperty(), e -> {
            if (showTickMarks && tickLine != null) {
                tickLine.setSide(control.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (control.getOrientation() == null) ? Side.RIGHT: Side.BOTTOM);
            }
            getSkinnable().requestLayout();
        });
        registerChangeListener(control.showTickMarksProperty(), e -> setShowTickMarks(control.isShowTickMarks(), control.isShowTickLabels()));
        registerChangeListener(control.showTickLabelsProperty(), e -> setShowTickMarks(control.isShowTickMarks(), control.isShowTickLabels()));
        registerChangeListener(control.majorTickUnitProperty(), e -> {
            if (tickLine != null) {
                tickLine.setTickUnit(control.getMajorTickUnit());
                getSkinnable().requestLayout();
            }
        });
        registerChangeListener(control.minorTickCountProperty(), e -> {
            if (tickLine != null) {
                tickLine.setMinorTickCount(Math.max(control.getMinorTickCount(), 0) + 1);
                getSkinnable().requestLayout();
            }
        });
        registerChangeListener(control.labelFormatterProperty(), e -> {
            if (tickLine != null) {
                if (control.getLabelFormatter() == null) {
                    tickLine.setTickLabelFormatter(null);
                } else {
                    tickLine.setTickLabelFormatter(stringConverterWrapper);
                    tickLine.requestAxisLayout();
                }
            }
        });
        //is it appropriate to adjust all the values here?
        registerChangeListener(control.snapToTicksProperty(), e -> {
            control.adjustHigherValue(control.getHigherValue());
            control.adjustHighestValue(control.getHighestValue());
            control.adjustLowerValue(control.getLowerValue());
            control.adjustLowestValue(control.getLowerValue());
        });
    }
    
    private void initTrack() {
        track = new StackPane();
        track.getStyleClass().setAll("track"); //$NON-NLS-1$

//        getChildren().clear();
//        getChildren().addAll(track);
        
        track.setOnMousePressed( new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                if (!lowestThumb.isPressed() && !lowerThumb.isPressed() && !higherThumb.isPressed() && !highestThumb.isPressed()) {
                    Scalar track = getTrackScalar();
                    if (isHorizontal()) {
                        behavior.trackPress(me, (me.getX() / track.length));
                    } else {
                    	behavior.trackPress(me, (me.getY() / track.length));
                    }
                }
            }
        });

        track.setOnMouseReleased( new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                 //Nothing being done with the second param in sliderBehavior
                //So, passing a dummy value
            	behavior.trackRelease(me, 0.0f);
            }
        });
    }
    
    
    private void initFirstThumb() {
        lowestThumb = new ThumbPane();
        lowestThumb.getStyleClass().setAll("lowest-thumb"); //$NON-NLS-1$
        lowestThumb.setFocusTraversable(true);
        
        getChildren().add(lowestThumb);
        
        setShowTickMarks(getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels());

        lowestThumb.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                highestThumb.setFocus(false);
                higherThumb.setFocus(false);
                lowerThumb.setFocus(false);
                lowestThumb.setFocus(true);
                behavior.lowestThumbPressed(me, 0.0f);
                preDragThumbPoint = lowestThumb.localToParent(me.getX(), me.getY());
                preDragPos = (getSkinnable().getLowestValue() - getSkinnable().getMin()) /
                        (getMaxMinusMinNoZero());
            }
        });

        lowestThumb.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
            	behavior.lowestThumbReleased(me);
            }
        });

        lowestThumb.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                Scalar track = getTrackScalar();
                Point2D cur = lowestThumb.localToParent(me.getX(), me.getY());
                double dragPos = (isHorizontal())?
                    cur.getX() - preDragThumbPoint.getX() : -(cur.getY() - preDragThumbPoint.getY());
                behavior.lowestThumbDragged(me, preDragPos + dragPos / track.length);
            }
        });
    }
    
    private void initSecondThumb() {
        lowerThumb = new ThumbPane();
        lowerThumb.getStyleClass().setAll("lower-thumb"); //$NON-NLS-1$
        lowerThumb.setFocusTraversable(true);
        if (!getChildren().contains(lowerThumb)) {
            getChildren().add(lowerThumb);
        }

        lowerThumb.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                highestThumb.setFocus(false);
                higherThumb.setFocus(false);
                lowerThumb.setFocus(true);
                lowestThumb.setFocus(false);
                behavior.lowerThumbPressed(me, 0.0f);
                preDragThumbPoint = lowerThumb.localToParent(me.getX(), me.getY());
                preDragPos = (getSkinnable().getLowerValue() - getSkinnable().getMin()) /
                        (getMaxMinusMinNoZero());
            }
        });

        lowerThumb.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
            	behavior.lowerThumbReleased(me);
            }
        });

        lowerThumb.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                Scalar track = getTrackScalar();
                Point2D cur = lowerThumb.localToParent(me.getX(), me.getY());
                double dragPos = (isHorizontal())?
                    cur.getX() - preDragThumbPoint.getX() : -(cur.getY() - preDragThumbPoint.getY());
                behavior.lowerThumbDragged(me, preDragPos + dragPos / track.length);
            }
        });
    }    

    private void initThirdThumb() {
        higherThumb = new ThumbPane();
        higherThumb.getStyleClass().setAll("higher-thumb"); //$NON-NLS-1$
        higherThumb.setFocusTraversable(true);
        if (!getChildren().contains(higherThumb)) {
            getChildren().add(higherThumb);
        }

        higherThumb.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                lowestThumb.setFocus(false);
                lowerThumb.setFocus(false);
                higherThumb.setFocus(true);
                highestThumb.setFocus(false);
                ((MultiRangeSliderBehavior) behavior).higherThumbPressed(e, 0.0D);
                preDragThumbPoint = higherThumb.localToParent(e.getX(), e.getY());
                preDragPos = (((MultiRangeSlider) getSkinnable()).getHigherValue() - ((MultiRangeSlider) getSkinnable()).getMin()) / 
                            (getMaxMinusMinNoZero());
            }
        }
        );
        higherThumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                ((MultiRangeSliderBehavior) behavior).higherThumbReleased(e);
            }
        }
        );
        higherThumb.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                Scalar track = getTrackScalar();
                Point2D point2d = higherThumb.localToParent(e.getX(), e.getY());
                double d = ((MultiRangeSlider) getSkinnable()).getOrientation() != Orientation.HORIZONTAL ? -(point2d.getY() - preDragThumbPoint.getY()) : point2d.getX() - preDragThumbPoint.getX();
                ((MultiRangeSliderBehavior) behavior).higherThumbDragged(e, preDragPos + d / track.length);
            }
        });
    }
    
    private void initFourthThumb() {
        highestThumb = new ThumbPane();
        highestThumb.getStyleClass().setAll("highest-thumb"); //$NON-NLS-1$
        highestThumb.setFocusTraversable(true);
        if (!getChildren().contains(highestThumb)) {
            getChildren().add(highestThumb);
        }

        highestThumb.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                lowestThumb.setFocus(false);
                lowerThumb.setFocus(false);
                higherThumb.setFocus(false);
                highestThumb.setFocus(true);
                ((MultiRangeSliderBehavior) behavior).highestThumbPressed(e, 0.0D);
                preDragThumbPoint = highestThumb.localToParent(e.getX(), e.getY());
                preDragPos = (((MultiRangeSlider) getSkinnable()).getHighestValue() - ((MultiRangeSlider) getSkinnable()).getMin()) / 
                            (getMaxMinusMinNoZero());
            }
        }
        );
        highestThumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                ((MultiRangeSliderBehavior) behavior).highestThumbReleased(e);
            }
        }
        );
        highestThumb.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                Scalar track = getTrackScalar();
                Point2D point2d = highestThumb.localToParent(e.getX(), e.getY());
                double d = ((MultiRangeSlider) getSkinnable()).getOrientation() != Orientation.HORIZONTAL ? -(point2d.getY() - preDragThumbPoint.getY()) : point2d.getX() - preDragThumbPoint.getX();
                ((MultiRangeSliderBehavior) behavior).highestThumbDragged(e, preDragPos + d / track.length);
            }
        });
    }    
    
    private StackPane initRangeBar(String cssClass) {
        StackPane rangeBar = new StackPane();
        rangeBar.cursorProperty().bind(new ObjectBinding<Cursor>() {
            { bind(rangeBar.hoverProperty()); }

            @Override protected Cursor computeValue() {
                return rangeBar.isHover() ? Cursor.HAND : Cursor.DEFAULT;
            }
        });
        rangeBar.getStyleClass().setAll(cssClass); //$NON-NLS-1$
        
//        rangeBar.setOnMousePressed(new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent e) {
//                rangeBar.requestFocus();
//                preDragPos = isHorizontal() ? e.getX() : -e.getY();
//            }
//        });
//        
//        rangeBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent e) {
//                double delta = (isHorizontal() ? e.getX() : -e.getY()) - preDragPos;
//                ((MultiRangeSliderBehavior) getBehavior()).moveRange(delta);
//            }
//        });
//        
//         rangeBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent e) {
//                ((MultiRangeSliderBehavior) getBehavior()).confirmRange();
//            }
//        });
        
        getChildren().add(rangeBar);
        return rangeBar;
    }
    
    /**
    *
    * @return the difference between max and min, but if they have the same
    * value, 1 is returned instead of 0 because otherwise the division where it
    * can be used will return Nan.
    */
   private double getMaxMinusMinNoZero() {
       MultiRangeSlider s = getSkinnable();
       return s.getMax() - s.getMin() == 0 ? 1 : s.getMax() - s.getMin();
   }
    
    private static class Scalar {
        final double start;
        final double length;

        public Scalar(double trackStart, double length) {
            this.length = length;
            this.start = trackStart;
        }
    }
    
    /**
     * Called when ever either min, max or lowValue changes, so lowthumb's layoutX, Y is recomputed.
     */
    private void positionLowestThumb() {

        double thumbWidth = lowestThumb.getWidth();
        double thumbHeight = lowestThumb.getHeight();
        highestThumb.resize(thumbWidth, thumbHeight);

        MultiRangeSlider s = getSkinnable();
        boolean horizontal = isHorizontal();

        Scalar t  = getTrackScalar();
        
        double lx = (horizontal) ? t.start + (
                ((t.length * ((s.getLowestValue() - s.getMin()) /
                 (getMaxMinusMinNoZero()))) - thumbWidth/2)) 
                 : lowestThumbPos;
        double ly = (horizontal) ? lowestThumbPos :
            getSkinnable().getInsets().getTop() + t.length - (t.length * ((s.getLowestValue() - s.getMin()) /
                (getMaxMinusMinNoZero()))); //  - thumbHeight/2
        lowestThumb.setLayoutX(lx);
        lowestThumb.setLayoutY(ly);
        if (horizontal) rangeStartLowest = t.start; else rangeEndLowest = t.start + t.length;
        if (horizontal) rangeEndLowest = lx + thumbWidth / 2D; else rangeStartLowest = ly + thumbWidth / 2D;
        if (horizontal) rangeStartLower = lx + thumbWidth / 2D; else rangeEndLower = ly + thumbWidth / 2D;
    }

    private void positionLowerThumb() {
        MultiRangeSlider s = getSkinnable();
        boolean horizontal = isHorizontal();
        
        double thumbWidth = lowestThumb.getWidth();
        double thumbHeight = lowestThumb.getHeight();
        lowerThumb.resize(thumbWidth, thumbHeight);

        Scalar t  = getTrackScalar();

        double lx = (horizontal) ? t.start + (((t.length * ((s.getLowerValue() - s.getMin()) /
                (getMaxMinusMinNoZero()))) - thumbWidth/2)) : lowerThumbPos;
        double ly = (horizontal) ? lowerThumbPos :
            getSkinnable().getInsets().getTop() + t.length - (t.length * ((s.getLowerValue() - s.getMin()) /
                (getMaxMinusMinNoZero()))); //  - thumbHeight/2
        lowerThumb.setLayoutX(lx);
        lowerThumb.setLayoutY(ly);
        if (horizontal) rangeEndLower = lx + thumbWidth / 2D; else rangeStartLower = ly + thumbWidth / 2D;
        if (horizontal) rangeStartMid = lx + thumbWidth / 2D; else rangeEndMid = ly + thumbWidth / 2D;
    }    

    /**
     * Called when ever either min, max or highValue changes, so highthumb's layoutX, Y is recomputed.
     */
    private void positionHigherThumb() {

        double thumbWidth = lowestThumb.getWidth();
        double thumbHeight = lowestThumb.getHeight();
        higherThumb.resize(thumbWidth, thumbHeight);

        MultiRangeSlider s = getSkinnable();
        boolean horizontal = isHorizontal();

        Scalar t  = getTrackScalar();

        double x = horizontal ? t.start + (t.length * ((s.getHigherValue() - s.getMin()) / (getMaxMinusMinNoZero())) - thumbWidth / 2D) : lowestThumb.getLayoutX();
        double y = horizontal ? lowestThumb.getLayoutY() : (getSkinnable().getInsets().getTop() + t.length) - t.length * ((s.getHigherValue() - s.getMin()) / (getMaxMinusMinNoZero()));
        higherThumb.setLayoutX(x);
        higherThumb.setLayoutY(y);
        if (horizontal) rangeStartHigher = x + thumbWidth / 2D; else rangeEndHigher = y + thumbWidth / 2D;
        if (horizontal) rangeEndMid = x + thumbWidth / 2D; else rangeStartMid = y + thumbWidth / 2D;
    }
    
    private void positionHighestThumb() {

        double thumbWidth = lowestThumb.getWidth();
        double thumbHeight = lowestThumb.getHeight();
        highestThumb.resize(thumbWidth, thumbHeight);

        MultiRangeSlider s = getSkinnable();
        boolean horizontal = isHorizontal();

        Scalar t  = getTrackScalar();

        double x = horizontal ? t.start + (t.length * ((s.getHighestValue() - s.getMin()) / (getMaxMinusMinNoZero())) - thumbWidth / 2D) : lowestThumb.getLayoutX();
        double y = horizontal ? lowestThumb.getLayoutY() : (getSkinnable().getInsets().getTop() + t.length) - t.length * ((s.getHighestValue() - s.getMin()) / (getMaxMinusMinNoZero()));
        highestThumb.setLayoutX(x);
        highestThumb.setLayoutY(y);
        if (horizontal) rangeEndHigher = x + thumbWidth / 2D; else rangeStartHigher = y + thumbWidth /2D;
        if (horizontal) rangeStartHighest = x + thumbWidth / 2D; else rangeEndHighest = y + thumbWidth /2D;
        if (horizontal) rangeEndHighest = t.start + t.length; else rangeStartHighest = t.start;
    }

    private Scalar getTrackScalar() {

        boolean horizontal = isHorizontal();
        BackgroundFill fill = track.getBackground().getFills().isEmpty() ? null : track.getBackground().getFills().get(0);
        double pad = null == fill ? 0 : (horizontal ? fill.getRadii().getTopLeftHorizontalRadius() : fill.getRadii().getTopLeftVerticalRadius());

        double trackStart = horizontal ? track.getLayoutX() : track.getLayoutY();
        trackStart += pad;
        double trackLength = horizontal ? track.getWidth() : track.getHeight();
        trackLength -= 2 * pad;
        return new Scalar(trackStart, trackLength);
    }
    
    /* *************************************************************************
     *                                                                         *
     * Private implementation                                                  *
     *                                                                         *
     **************************************************************************/

    private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
        showTickMarks = (ticksVisible || labelsVisible);
        MultiRangeSlider slider = getSkinnable();
        if (showTickMarks) {
            if (tickLine == null) {
                tickLine = new NumberAxis();
                tickLine.setAutoRanging(false);
                tickLine.setSide(slider.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (slider.getOrientation() == null) ? Side.RIGHT: Side.BOTTOM);
                tickLine.setUpperBound(slider.getMax());
                tickLine.setLowerBound(slider.getMin());
                tickLine.setTickUnit(slider.getMajorTickUnit());
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setMinorTickVisible(ticksVisible);
                // add 1 to the slider minor tick count since the axis draws one
                // less minor ticks than the number given.
                tickLine.setMinorTickCount(Math.max(slider.getMinorTickCount(),0) + 1);
                if (slider.getLabelFormatter() != null) {
                    tickLine.setTickLabelFormatter(stringConverterWrapper);
                }
                getChildren().clear();
                getChildren().addAll(tickLine, track, rangeBarLowest, rangeBarLower, rangeBarMid, rangeBarHigher, rangeBarHighest, lowestThumb);
            } else {
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setMinorTickVisible(ticksVisible);
            }
        }
        else  {
            getChildren().clear();
            getChildren().addAll(track, rangeBarLowest, rangeBarLower, rangeBarMid, rangeBarHigher, rangeBarHighest, lowestThumb);
//            tickLine = null;
        }

        getSkinnable().requestLayout();
    }

    
    private boolean isHorizontal() {
        return orientation == null || orientation == Orientation.HORIZONTAL;
    }
    
    private static class ThumbPane extends StackPane {
        public void setFocus(boolean value) {
            setFocused(value);
        }
    }

}
