package org.mdpnp.devices.webcam;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2BGRA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.connected.AbstractConnectedDevice;
import org.mdpnp.comms.data.image.MutableImageUpdate;
import org.mdpnp.comms.data.image.MutableImageUpdateImpl;
import org.mdpnp.comms.nomenclature.Webcam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class WebcamImpl extends AbstractConnectedDevice implements Webcam, Runnable {

	private final FrameGrabber grabber;
	private final MutableImageUpdate miu;
	private final CvMemStorage storage;
	
	private final static long FRAME_INTERVAL = 50L;
	
	private static final Logger log = LoggerFactory.getLogger(WebcamImpl.class);
	
	public void run() {
		try {
			grabber.start();
			IplImage image = grabber.grab();
			
			int width = image.width();
			int height = image.height();
			
			int a_width = width / 8;
			int a_height = height / 8;
			
			IplImage smallerImage = IplImage.create(a_width, a_height, IPL_DEPTH_8U, 3);
			
			IplImage alphaImage = IplImage.create(a_width, a_height, IPL_DEPTH_8U, 4);
			cvSet(alphaImage, cvScalar(255,255,255,255));
			
			
			miu.setHeight(a_height);
			miu.setWidth(a_width);
			
			byte[] raster = new byte[a_height*a_width*4];
			miu.setRaster(raster);
			
			long lastStart;

			
			
			while (!State.Disconnecting.equals(getState()) && (image = grabber.grab()) != null) {
				lastStart = System.currentTimeMillis();
				cvClearMemStorage(storage);
				
				cvResize(image, smallerImage);
				cvCvtColor(smallerImage, alphaImage, CV_RGB2BGRA);
				
				BytePointer bp = alphaImage.imageData();
				bp.get(raster);
				
				gateway.update(this, miu);
				
				long now = System.currentTimeMillis();
				if( FRAME_INTERVAL > (now - lastStart) ) {
					try {
						Thread.sleep(FRAME_INTERVAL - (now - lastStart));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					log.warn("Frame took " + (now-lastStart) + "ms which exceeds FRAME_INTERVAL="+ FRAME_INTERVAL+"ms");
				}
						
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				grabber.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stateMachine.transitionWhenLegal(State.Disconnected, 2000L);
		}
	}
	
	public WebcamImpl(Gateway gateway) {
		super(gateway);
		miu = new MutableImageUpdateImpl(Webcam.LIVE_FRAME);
		add(miu);
		storage = CvMemStorage.create();
		grabber = new OpenCVFrameGrabber(0);
	}

	@Override
	protected void connect(String str) {
		synchronized(this) {
			switch(getState()) {
			case Connected:
			case Negotiating:
			case Connecting:
				return;
			case Disconnected:
			case Disconnecting:
				stateMachine.transitionWhenLegal(State.Connecting);
				Thread t = new Thread(this);
				t.setDaemon(true);
				t.start();
				break;
			}
		}
		
	}

	@Override
	protected void disconnect() {
		log.trace("disconnect requested");
		synchronized(stateMachine) {
			switch(getState()){
			case Disconnected:
			case Disconnecting:
				return;
			case Connecting:
			case Connected:
			case Negotiating:
				stateMachine.transitionWhenLegal(State.Disconnecting);
				break;
			}
		}
	}

	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Camera;
	}

}
