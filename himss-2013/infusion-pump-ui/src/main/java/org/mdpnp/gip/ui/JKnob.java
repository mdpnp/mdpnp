package org.mdpnp.gip.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;



@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JKnob 
    extends JComponent {

	private static final double spotFraction = 0.4;
	
    private int radiusWidth, radiusHeight;
    private int spotRadiusWidth, spotRadiusHeight;

    private double theta;
    private Color knobColor;
    private Color spotColor;

    private boolean pressedOnSpot;

    /**
     * No-Arg constructor that initializes the position
     * of the knob to 0 radians (Up).
     */
    public JKnob() {
	this(0);
    }

    /**
     * Constructor that initializes the position
     * of the knob to the specified angle in radians.
     *
     * @param initAngle the initial angle of the knob.
     */
    public JKnob(double initTheta) {
	this(initTheta, Color.gray, Color.black);
    }
    
    /**
     * Constructor that initializes the position of the
     * knob to the specified position and also allows the
     * colors of the knob and spot to be specified.
     *
     * @param initAngle the initial angle of the knob.
     * @param initColor the color of the knob.
     * @param initSpotColor the color of the spot.
     */
    public JKnob(double initTheta, Color initKnobColor, 
		 Color initSpotColor) {
    	setMinimumSize(new Dimension(50,50));
    	setPreferredSize(new Dimension(50,50));
	theta = initTheta;
	pressedOnSpot = false;
	knobColor = initKnobColor;
	spotColor = initSpotColor;

	enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
    }

    /**
     * Paint the JKnob on the graphics context given.  The knob
     * is a filled circle with a small filled circle offset 
     * within it to show the current angular position of the 
     * knob.
     *
     * @param g The graphics context on which to paint the knob.
     */
    public void paint(Graphics g) {

	// Draw the knob.
	g.setColor(knobColor);
	g.fillOval(0,0,2*radiusWidth,2*radiusHeight);

	// Find the center of the spot.
	Point pt = getSpotCenter();
	int xc = (int)pt.getX();
	int yc = (int)pt.getY();

	// Draw the spot.
	g.setColor(spotColor);
	g.fillOval(xc-spotRadiusWidth, yc-spotRadiusHeight,
		   2*spotRadiusWidth, 2*spotRadiusHeight);
    }

    
    
    @Override
    protected void processComponentEvent(ComponentEvent e) {
    	
    	switch(e.getID()) {
    	case ComponentEvent.COMPONENT_RESIZED:
    		radiusWidth = e.getComponent().getWidth() / 2 - 1;
    		radiusHeight = e.getComponent().getHeight() / 2 - 1;
    		spotRadiusWidth = (int)(spotFraction * radiusWidth);
    		spotRadiusHeight = (int)(spotFraction * radiusHeight);
    		break;
    	}
    	super.processComponentEvent(e);
    }
    
    /**
     * Get the current anglular position of the knob.
     *
     * @return the current anglular position of the knob.
     */
    public double getAngle() {
	return theta;
    }


    /** 
     * Calculate the x, y coordinates of the center of the spot.
     *
     * @return a Point containing the x,y position of the center
     *         of the spot.
     */ 
    private Point getSpotCenter() {

	// Calculate the center point of the spot RELATIVE to the
	// center of the of the circle.
	int xcp = (int)( (radiusWidth - spotRadiusWidth) * Math.sin(theta));
	int ycp = (int)( (radiusHeight - spotRadiusHeight) * Math.cos(theta));

	// Adjust the center point of the spot so that it is offset
	// from the center of the circle.  This is necessary becasue
	// 0,0 is not actually the center of the circle, it is  the 
        // upper left corner of the component!
	int xc = radiusWidth + xcp;
	int yc = radiusHeight - ycp;

	// Create a new Point to return since we can't  
	// return 2 values!
	return new Point(xc,yc);
    }

    /**
     * Determine if the mouse click was on the spot or
     * not.  If it was return true, otherwise return 
     * false.
     *
     * @return true if x,y is on the spot and false if not.
     */
    private boolean isOnSpot(Point pt) {
	return (pt.distance(getSpotCenter()) < (spotRadiusWidth+spotRadiusHeight)/2);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
    	switch(e.getID()) {
    	case MouseEvent.MOUSE_PRESSED:
    		Point mouseLoc = e.getPoint();
    		pressedOnSpot = isOnSpot(mouseLoc);
    		break;
    	case MouseEvent.MOUSE_RELEASED:
    		pressedOnSpot = false;
    		break;
    	}
    	super.processMouseEvent(e);
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
    	switch(e.getID()) {
    	case MouseEvent.MOUSE_DRAGGED:
    	case MouseEvent.MOUSE_MOVED:
    		if (pressedOnSpot) {

    		    int mx = e.getX();
    		    int my = e.getY();

    		    // Compute the x, y position of the mouse RELATIVE
    		    // to the center of the knob.
    		    int mxp = mx - radiusWidth;
    		    int myp = radiusHeight - my;

    		    // Compute the new angle of the knob from the
    		    // new x and y position of the mouse.  
    		    // Math.atan2(...) computes the angle at which
    		    // x,y lies from the positive y axis with cw rotations
    		    // being positive and ccw being negative.
    		    theta = Math.atan2(mxp, myp);

    		    repaint();
    		}
    		break;
    	}
    	super.processMouseMotionEvent(e);
    }

    /**
     * Here main is used simply as a test method.  If this file
     * is executed "java JKnob" then this main() method will be
     * run.  However, if another file uses a JKnob as a component
     * and that file is run then this main is ignored.
     */
    public static void main(String[] args) {

	JFrame myFrame = new JFrame("JKnob Test method");
	
	Container thePane = myFrame.getContentPane();

	// Add a JKnob to the pane.
	thePane.add(new JKnob());

	myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	myFrame.pack();
	myFrame.setVisible(true);
    }
}
