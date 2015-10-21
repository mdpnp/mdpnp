package org.mdpnp.apps.testapp.vital;

import static org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild.HIGHER_THUMB;
import static org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild.HIGHEST_THUMB;
import static org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild.LOWER_THUMB;
import static org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild.LOWEST_THUMB;
import static org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild.NONE;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.vital.MultiRangeSliderBehavior.FocusedChild;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class MultiRangeSliderSkin extends BehaviorSkinBase<MultiRangeSlider, MultiRangeSliderBehavior> {

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
    
    private FocusedChild currentFocus = LOWEST_THUMB;
    
    public MultiRangeSliderSkin(final MultiRangeSlider rangeSlider) {
        super(rangeSlider, new MultiRangeSliderBehavior(rangeSlider));
        orientation = getSkinnable().getOrientation();
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
        
        registerChangeListener(rangeSlider.lowestValueProperty(), "LOWEST_VALUE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.lowerValueProperty(), "LOWER_VALUE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.higherValueProperty(), "HIGHER_VALUE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.highestValueProperty(), "HIGHEST_VALUE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.lowestValueVisibleProperty(), "LOWEST_VALUE_VISIBLE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.lowerValueVisibleProperty(), "LOWER_VALUE_VISIBLE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.higherValueVisibleProperty(), "HIGHER_VALUE_VISIBLE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.highestValueVisibleProperty(), "HIGHEST_VALUE_VISIBLE"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.minProperty(), "MIN"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.maxProperty(), "MAX"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.orientationProperty(), "ORIENTATION"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.showTickMarksProperty(), "SHOW_TICK_MARKS"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.showTickLabelsProperty(), "SHOW_TICK_LABELS"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.majorTickUnitProperty(), "MAJOR_TICK_UNIT"); //$NON-NLS-1$
        registerChangeListener(rangeSlider.minorTickCountProperty(), "MINOR_TICK_COUNT"); //$NON-NLS-1$
        lowestThumb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = LOWEST_THUMB;
                }
            }
        });
        lowerThumb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = LOWER_THUMB;
                }
            }
        });        
        higherThumb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = HIGHER_THUMB;
                }
            }
        });
        highestThumb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = HIGHEST_THUMB;
                }
            }
        });        
        rangeBarLowest.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = FocusedChild.RANGE_BAR_LOWEST;
                }
            }
        });
        rangeBarLower.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = FocusedChild.RANGE_BAR_LOWER;
                }
            }
        });
        rangeBarMid.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = FocusedChild.RANGE_BAR_MID;
                }
            }
        });
        rangeBarHigher.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = FocusedChild.RANGE_BAR_HIGHER;
                }
            }
        });
        rangeBarHighest.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    currentFocus = FocusedChild.RANGE_BAR_HIGHEST;
                }
            }
        });        
        rangeSlider.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean hasFocus) {
                if (hasFocus) {
                    lowestThumb.setFocus(true);
                } else {
                    lowestThumb.setFocus(false);
                    lowerThumb.setFocus(false);
                    higherThumb.setFocus(false);
                    highestThumb.setFocus(false);
                    currentFocus = NONE;
                }
            }
        });

//        EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
//            @Override public void handle(KeyEvent event) {
//                if (KeyCode.TAB.equals(event.getCode())) {
//                    if (lowestThumb.isFocused()) {
//                        if (event.isShiftDown()) {
//                            lowestThumb.setFocus(false);
//                            new ParentTraversalEngine(rangeSlider).select(rangeSlider, Direction.PREVIOUS);
//                        } else {
//                            lowestThumb.setFocus(false);
//                            lowerThumb.setFocus(true);
//                        }
//                        event.consume();
//                    } else if(lowerThumb.isFocused()) {
//                        if (event.isShiftDown()) {
//                            lowerThumb.setFocus(false);
//                            lowestThumb.setFocus(true);
//                        } else {
//                            lowerThumb.setFocus(false);
//                            higherThumb.setFocus(true);
//                        }
//                        event.consume();
//                    } else if (higherThumb.isFocused()) {
//                        // TODO is this right?
//                        if(event.isShiftDown()) {
//                            higherThumb.setFocus(false);
//                            lowerThumb.setFocus(true);
//                        } else {
//                            higherThumb.setFocus(false);
//                            highestThumb.setFocus(true);
//                        }
//                        event.consume();
//                    } else if (highestThumb.isFocused()) {
//                        // TODO is this right?
//                        if(event.isShiftDown()) {
//                            highestThumb.setFocus(false);
//                            higherThumb.setFocus(true);
//                        } else {
//                            highestThumb.setFocus(false);
//                                new ParentTraversalEngine(rangeSlider).select(rangeSlider, Direction.NEXT);
//                        }
//                        event.consume();
//                    }
//                }
//            }
//        };
//        getSkinnable().addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);  
        // set up a callback on the behavior to indicate which thumb is currently 
        // selected (via enum).
        getBehavior().setSelectedValue(new Callback<Void, FocusedChild>() {
            @Override public FocusedChild call(Void v) {
                return currentFocus;
            }
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
                        getBehavior().trackPress(me, (me.getX() / track.length));
                    } else {
                        getBehavior().trackPress(me, (me.getY() / track.length));
                    }
                }
            }
        });

        track.setOnMouseReleased( new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                 //Nothing being done with the second param in sliderBehavior
                //So, passing a dummy value
                getBehavior().trackRelease(me, 0.0f);
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
                getBehavior().lowestThumbPressed(me, 0.0f);
                preDragThumbPoint = lowestThumb.localToParent(me.getX(), me.getY());
                preDragPos = (getSkinnable().getLowestValue() - getSkinnable().getMin()) /
                        (getMaxMinusMinNoZero());
            }
        });

        lowestThumb.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                getBehavior().lowestThumbReleased(me);
            }
        });

        lowestThumb.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                Scalar track = getTrackScalar();
                Point2D cur = lowestThumb.localToParent(me.getX(), me.getY());
                double dragPos = (isHorizontal())?
                    cur.getX() - preDragThumbPoint.getX() : -(cur.getY() - preDragThumbPoint.getY());
                getBehavior().lowestThumbDragged(me, preDragPos + dragPos / track.length);
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
                getBehavior().lowerThumbPressed(me, 0.0f);
                preDragThumbPoint = lowerThumb.localToParent(me.getX(), me.getY());
                preDragPos = (getSkinnable().getLowerValue() - getSkinnable().getMin()) /
                        (getMaxMinusMinNoZero());
            }
        });

        lowerThumb.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                getBehavior().lowerThumbReleased(me);
            }
        });

        lowerThumb.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                Scalar track = getTrackScalar();
                Point2D cur = lowerThumb.localToParent(me.getX(), me.getY());
                double dragPos = (isHorizontal())?
                    cur.getX() - preDragThumbPoint.getX() : -(cur.getY() - preDragThumbPoint.getY());
                getBehavior().lowerThumbDragged(me, preDragPos + dragPos / track.length);
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
                ((MultiRangeSliderBehavior) getBehavior()).higherThumbPressed(e, 0.0D);
                preDragThumbPoint = higherThumb.localToParent(e.getX(), e.getY());
                preDragPos = (((MultiRangeSlider) getSkinnable()).getHigherValue() - ((MultiRangeSlider) getSkinnable()).getMin()) / 
                            (getMaxMinusMinNoZero());
            }
        }
        );
        higherThumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                ((MultiRangeSliderBehavior) getBehavior()).higherThumbReleased(e);
            }
        }
        );
        higherThumb.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                Scalar track = getTrackScalar();
                Point2D point2d = higherThumb.localToParent(e.getX(), e.getY());
                double d = ((MultiRangeSlider) getSkinnable()).getOrientation() != Orientation.HORIZONTAL ? -(point2d.getY() - preDragThumbPoint.getY()) : point2d.getX() - preDragThumbPoint.getX();
                ((MultiRangeSliderBehavior) getBehavior()).higherThumbDragged(e, preDragPos + d / track.length);
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
                ((MultiRangeSliderBehavior) getBehavior()).highestThumbPressed(e, 0.0D);
                preDragThumbPoint = highestThumb.localToParent(e.getX(), e.getY());
                preDragPos = (((MultiRangeSlider) getSkinnable()).getHighestValue() - ((MultiRangeSlider) getSkinnable()).getMin()) / 
                            (getMaxMinusMinNoZero());
            }
        }
        );
        highestThumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                ((MultiRangeSliderBehavior) getBehavior()).highestThumbReleased(e);
            }
        }
        );
        highestThumb.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                Scalar track = getTrackScalar();
                Point2D point2d = highestThumb.localToParent(e.getX(), e.getY());
                double d = ((MultiRangeSlider) getSkinnable()).getOrientation() != Orientation.HORIZONTAL ? -(point2d.getY() - preDragThumbPoint.getY()) : point2d.getX() - preDragThumbPoint.getX();
                ((MultiRangeSliderBehavior) getBehavior()).highestThumbDragged(e, preDragPos + d / track.length);
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

    private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
        showTickMarks = (ticksVisible || labelsVisible);
        MultiRangeSlider rangeSlider = getSkinnable();
        if (showTickMarks) {
            if (tickLine == null) {
                tickLine = new NumberAxis();
                tickLine.setAnimated(false);
                tickLine.setAutoRanging(false);
                tickLine.setSide(isHorizontal() ? Side.BOTTOM : Side.RIGHT);
                tickLine.setUpperBound(rangeSlider.getMax());
                tickLine.setLowerBound(rangeSlider.getMin());
                tickLine.setTickUnit(rangeSlider.getMajorTickUnit());
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setMinorTickVisible(ticksVisible);
                // add 1 to the slider minor tick count since the axis draws one
                // less minor ticks than the number given.
                tickLine.setMinorTickCount(Math.max(rangeSlider.getMinorTickCount(),0) + 1);
                // TODO change slider API to Integer from Number
        //            if (slider.getLabelFormatter() != null)
        //                tickLine.setFormatTickLabel(slider.getLabelFormatter());
        //            tickLine.dataChanged();
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
            tickLine = null;
        }

        getSkinnable().requestLayout();
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        MultiRangeSlider slider = (MultiRangeSlider) getSkinnable();
        if ("ORIENTATION".equals(p)) { //$NON-NLS-1$
            orientation = getSkinnable().getOrientation();
            if (showTickMarks && tickLine != null) {
                tickLine.setSide(isHorizontal() ? Side.BOTTOM : Side.RIGHT);
            }
            getSkinnable().requestLayout();
        } else if ("MIN".equals(p) ) { //$NON-NLS-1$
            if (showTickMarks && tickLine != null) {
                tickLine.setLowerBound(getSkinnable().getMin());
            }
            getSkinnable().requestLayout();
            rangeBarLowest.resizeRelocate(rangeStartLowest, rangeBarLowest.getLayoutY(),
                    rangeEndLowest - rangeStartLowest, rangeBarLowest.getHeight());
            
        } else if ("MAX".equals(p)) { //$NON-NLS-1$
            if (showTickMarks && tickLine != null) {
                tickLine.setUpperBound(getSkinnable().getMax());
            }
            getSkinnable().requestLayout();
            rangeBarHighest.resizeRelocate(rangeStartHighest, rangeBarHighest.getLayoutY(),
                    rangeEndHighest - rangeStartHighest, rangeBarHighest.getHeight());
        } else if ("SHOW_TICK_MARKS".equals(p) || "SHOW_TICK_LABELS".equals(p)) { //$NON-NLS-1$ //$NON-NLS-2$
            setShowTickMarks(getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels());
        }  else if ("MAJOR_TICK_UNIT".equals(p)) { //$NON-NLS-1$
            if (tickLine != null) {
                tickLine.setTickUnit(getSkinnable().getMajorTickUnit());
                getSkinnable().requestLayout();
            }
        } else if ("MINOR_TICK_COUNT".equals(p)) { //$NON-NLS-1$
            if (tickLine != null) {
                tickLine.setMinorTickCount(Math.max(getSkinnable().getMinorTickCount(),0) + 1);
                getSkinnable().requestLayout();
            }
        } else if ("LOWEST_VALUE".equals(p)) { //$NON-NLS-1$
            positionLowestThumb();
            rangeBarLowest.resize(rangeEndLowest - rangeStartLowest, rangeBarLowest.getHeight());
            rangeBarLower.resizeRelocate(rangeStartLower, rangeBarLower.getLayoutY(), 
                    rangeEndLower - rangeStartLower, rangeBarLower.getHeight());
        } else if ("LOWER_VALUE".equals(p)) {
            positionLowerThumb();
            rangeBarLower.resize(rangeEndLower - rangeStartLower, rangeBarLower.getHeight());
            rangeBarMid.resizeRelocate(rangeStartMid, rangeBarMid.getLayoutY(), 
                    rangeEndMid - rangeStartMid, rangeBarMid.getHeight());
        } else if ("HIGHER_VALUE".equals(p)) { //$NON-NLS-1$
            positionHigherThumb();
            rangeBarMid.resize(rangeEndMid - rangeStartMid, rangeBarMid.getHeight());
            rangeBarHigher.resizeRelocate(rangeStartHigher, rangeBarHigher.getLayoutY(), 
                    rangeEndHigher - rangeStartHigher, rangeBarHigher.getHeight());
        } else if ("HIGHEST_VALUE".equals(p)) { //$NON-NLS-1$
            positionHighestThumb();
            rangeBarHigher.resize(rangeEndHigher - rangeStartHigher, rangeBarHigher.getHeight());
            rangeBarHighest.resizeRelocate(rangeStartHighest, rangeBarHighest.getLayoutY(), 
                    rangeEndHighest - rangeStartHighest, rangeBarHighest.getHeight());    
        } else if("LOWEST_VALUE_VISIBLE".equals(p)) {
            lowestThumb.setVisible(slider.isLowestValueVisible());
        } else if("LOWER_VALUE_VISIBLE".equals(p)) {
            lowerThumb.setVisible(slider.isLowerValueVisible());
        } else if("HIGHER_VALUE_VISIBLE".equals(p)) {
            higherThumb.setVisible(slider.isHigherValueVisible());
        } else if("HIGHEST_VALUE_VISIBLE".equals(p)) {
            highestThumb.setVisible(slider.isHighestValueVisible());
        } else if ("SHOW_TICK_MARKS".equals(p) || "SHOW_TICK_LABELS".equals(p)) { //$NON-NLS-1$ //$NON-NLS-2$
            if (!getChildren().contains(lowerThumb))
                getChildren().add(lowerThumb);
            if (!getChildren().contains(highestThumb))
                getChildren().add(highestThumb);            
            if (!getChildren().contains(higherThumb))
                getChildren().add(higherThumb);

        }
        super.handleControlPropertyChanged(p);
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
    
    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        // resize thumb to preferred size
        thumbWidth = lowestThumb.prefWidth(-1);
        thumbHeight = lowestThumb.prefHeight(-1);
        lowestThumb.resize(thumbWidth, thumbHeight);
        // we are assuming the is common radius's for all corners on the track
        double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ?
                track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;

        if (isHorizontal()) {
            double tickLineHeight =  (showTickMarks) ? tickLine.prefHeight(-1) : 0;
            double trackHeight = track.prefHeight(-1);
            double trackAreaHeight = Math.max(trackHeight,thumbHeight);
            double totalHeightNeeded = trackAreaHeight  + ((showTickMarks) ? trackToTickGap+tickLineHeight : 0);
            double startY = y + ((h - totalHeightNeeded)/2); // center slider in available height vertically
            double trackLength = w - thumbWidth;
            double trackStart = x + (thumbWidth/2);
            double trackTop = (int)(startY + ((trackAreaHeight-trackHeight)/2));
            lowestThumbPos = (int)(startY + ((trackAreaHeight-thumbHeight)/2));
            

            // layout track
            track.resizeRelocate(trackStart - trackRadius, trackTop , trackLength + trackRadius + trackRadius, trackHeight);

            positionLowestThumb();
            positionLowerThumb();
            positionHigherThumb();
            positionHighestThumb();

            // layout range bar
            rangeBarLowest.resizeRelocate(rangeStartLowest, trackTop, rangeEndLowest - rangeStartLowest, trackHeight);
            rangeBarLower.resizeRelocate(rangeStartLower, trackTop, rangeEndLower - rangeStartLower, trackHeight);
            rangeBarMid.resizeRelocate(rangeStartMid, trackTop, rangeEndMid - rangeStartMid, trackHeight);
            rangeBarHigher.resizeRelocate(rangeStartHigher, trackTop, rangeEndHigher - rangeStartHigher, trackHeight);
            rangeBarHighest.resizeRelocate(rangeStartHighest, trackTop, rangeEndHighest - rangeStartHighest, trackHeight);
            // layout tick line
            if (showTickMarks) {
                tickLine.setLayoutX(trackStart);
                tickLine.setLayoutY(trackTop+trackHeight+trackToTickGap);
                tickLine.resize(trackLength, tickLineHeight);
                tickLine.requestAxisLayout();
            } else {
                if (tickLine != null) {
                    tickLine.resize(0,0);
                    tickLine.requestAxisLayout();
                }
                tickLine = null;
            }
        } else {
            double tickLineWidth = (showTickMarks) ? tickLine.prefWidth(-1) : 0;
            double trackWidth = track.prefWidth(-1);
            double trackAreaWidth = Math.max(trackWidth,thumbWidth);
            double totalWidthNeeded = trackAreaWidth  + ((showTickMarks) ? trackToTickGap+tickLineWidth : 0) ;
            double startX = x + ((w - totalWidthNeeded)/2); // center slider in available width horizontally
            double trackLength = h - thumbHeight;
            double trackStart = y + (thumbHeight/2);
            double trackLeft = (int)(startX + ((trackAreaWidth-trackWidth)/2));
            lowestThumbPos = (int)(startX + ((trackAreaWidth-thumbWidth)/2));

            positionLowestThumb();
            positionLowerThumb();
            // layout track
            track.resizeRelocate(trackLeft, trackStart - trackRadius, trackWidth, trackLength + trackRadius + trackRadius);
            positionHigherThumb();
            positionHighestThumb();
            // layout range bar
            rangeBarLowest.resizeRelocate(trackLeft, rangeStartLowest, trackWidth, rangeEndLowest - rangeStartLowest);
            rangeBarLower.resizeRelocate(trackLeft, rangeStartLower, trackWidth, rangeEndLower - rangeStartLower);
            rangeBarMid.resizeRelocate(trackLeft, rangeStartMid, trackWidth, rangeEndMid - rangeStartMid);
            rangeBarHigher.resizeRelocate(trackLeft, rangeStartHigher, trackWidth, rangeEndHigher - rangeStartHigher);
            rangeBarHighest.resizeRelocate(trackLeft, rangeStartHighest, trackWidth, rangeEndHighest - rangeStartHighest);
            // layout tick line
            if (showTickMarks) {
                tickLine.setLayoutX(trackLeft+trackWidth+trackToTickGap);
                tickLine.setLayoutY(trackStart);
                tickLine.resize(tickLineWidth, trackLength);
                tickLine.requestAxisLayout();
            } else {
                if (tickLine != null) {
                    tickLine.resize(0,0);
                    tickLine.requestAxisLayout();
                }
                tickLine = null;
            }
        }
    }
    
    private double minTrackLength() {
        return 2*lowestThumb.prefWidth(-1);
    }
    
    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return (leftInset + minTrackLength() + lowestThumb.minWidth(-1) + rightInset);
        } else {
            return (leftInset + lowestThumb.prefWidth(-1) + rightInset);
        }
    }

    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
         if (isHorizontal()) {
            return (topInset + lowestThumb.prefHeight(-1) + bottomInset);
        } else {
            return (topInset + minTrackLength() + lowestThumb.prefHeight(-1) + bottomInset);
        }
    }

    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            if(showTickMarks) {
                return Math.max(140, tickLine.prefWidth(-1));
            } else {
                return 140;
            }
        } else {
            //return (padding.getLeft()) + Math.max(thumb.prefWidth(-1), track.prefWidth(-1)) + padding.getRight();
            return leftInset + Math.max(lowestThumb.prefWidth(-1), track.prefWidth(-1)) +
            ((showTickMarks) ? (trackToTickGap+tickLine.prefWidth(-1)) : 0) + rightInset;
        }
    }

    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return getSkinnable().getInsets().getTop() + Math.max(lowestThumb.prefHeight(-1), track.prefHeight(-1)) +
             ((showTickMarks) ? (trackToTickGap+tickLine.prefHeight(-1)) : 0)  + bottomInset;
        } else {
            if(showTickMarks) {
                return Math.max(140, tickLine.prefHeight(-1));
            } else {
                return 140;
            }
        }
    }

    @Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return Double.MAX_VALUE;
        } else {
            return getSkinnable().prefWidth(-1);
        }
    }

    @Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return getSkinnable().prefHeight(width);
        } else {
            return Double.MAX_VALUE;
        }
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
