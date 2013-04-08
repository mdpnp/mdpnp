package org.mdpnp.apps.testapp.xray;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@SuppressWarnings("serial")
public class FramePanel extends ImagePanel implements Runnable {

	
	public enum State {
		Freezing,
		Frozen,
		Thawed
	}
	
	
	public void start() {
		if(cameraThread == null) {
			running = true;
			cameraThread = new Thread(this, "Camera Thread");
			cameraThread.setDaemon(true);
			cameraThread.start();
		}
	}
	
	public FramePanel(int cameraId) {
		
	}

	public void freeze() {
		freeze(0);
	}
	
	public synchronized void freeze(long exposureTime) {
		if(State.Frozen.equals(state)) {
			return;
		}
		if(exposureTime > 0L) {
			freezeBy = System.currentTimeMillis() + exposureTime;
			System.out.println("will freeze:"+freezeBy);
			state = State.Freezing;
			notifyAll();
		} else {
			state = State.Frozen;
			notifyAll();
		}
	}

	public synchronized void unfreeze() {
		while(State.Freezing.equals(state)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		state = State.Thawed;
		notifyAll();
	}

	private Thread cameraThread;

	public synchronized void toggle() {
		while(State.Freezing.equals(state)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		state = State.Thawed.equals(state)?State.Frozen:State.Thawed;
		notifyAll();
	}

	public void stop() {
		if (cameraThread != null) {
			running = false;
			unfreeze();
			try {
				cameraThread.join();
				cameraThread = null;
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	private long freezeBy;
	private State state = State.Thawed;
	private FrameGrabber grabber;
	private BufferedImage bufferedCameraImage;
	private BufferedImage renderCameraImage;
	private Graphics2D renderCameraGraphics;
	private Graphics2D bufferedCameraGraphics;
	private volatile boolean running = true;

	private final AlphaComposite composite = AlphaComposite.SrcOver.derive(0.1f);
	private final AlphaComposite noComposite = AlphaComposite.Src;
	
	private static final void gray(BufferedImage bi) {
		WritableRaster wr = bi.getRaster();
		float[] rgb = new float[4];
		for(int i = 0; i < wr.getWidth(); i++) {
			for(int j = 0; j < wr.getHeight(); j++) {
				rgb = wr.getPixel(i, j, rgb);
				float f = (rgb[0]+rgb[1]+rgb[2])/3.0f;
				rgb[0]=rgb[1]=rgb[2]=f;
				wr.setPixel(i, j, rgb);
			}
		}
	}
	
	private static final long FRAME_INTERVAL = 100L;
	private static final Logger log = LoggerFactory.getLogger(FramePanel.class);
	@Override
	public void run() {
		CvMemStorage storage = CvMemStorage.create();
		grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
			IplImage image = grabber.grab();
			bufferedCameraImage = image.getBufferedImage();
//			bufferedCameraImage = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_ARGB);
//			image.copyTo(bufferedCameraImage);
			bufferedCameraGraphics = bufferedCameraImage.createGraphics();
			
//			renderCameraImage = image.getBufferedImage();
			renderCameraImage = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_ARGB);
			System.out.println(renderCameraImage.getColorModel());
			renderCameraGraphics = renderCameraImage.createGraphics();
			renderCameraGraphics.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
			renderCameraGraphics.fillRect(0, 0, image.width(), image.height());
			renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
			
			
			setImage(renderCameraImage);
//			float[] scales = { 1f, 1f, 1f, 0.5f };
//			float[] offsets = new float[4];
//			RescaleOp rop = new RescaleOp(scales, offsets, null);
			
			long lastStart = 0L;
			
			while (running && (image = grabber.grab()) != null) {
				lastStart = System.currentTimeMillis();
				cvClearMemStorage(storage);
				
				
				
//				renderCameraGraphics.setComposite(AlphaComposite.Src.derive(0.5f));
//				renderCameraGraphics.set
//				renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, null);
				switch(state) {
				case Freezing:
					renderCameraGraphics.setComposite(composite);

					break;
				case Thawed:
					renderCameraGraphics.setComposite(noComposite);
//					image.copyTo(renderCameraImage);
					break;
				default:
				}

				image.copyTo(bufferedCameraImage);
				renderCameraGraphics.drawImage(bufferedCameraImage, 0, 0, this);
				

				
				synchronized (this) {
					if(State.Freezing.equals(state) && System.currentTimeMillis() >= freezeBy) {
						System.out.println("frozen:"+System.currentTimeMillis());
						state = State.Frozen;
					}
					while(State.Frozen.equals(state)) {
						gray(renderCameraImage);
						repaint();
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
//					renderCameraGraphics.fillRect(0, 0, image.width(), image.height());
				}
				repaint();
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
		}
	}
	
	
	private static final String ACQUIRING_IMAGE = "Acquiring image...";
	private static final String IMAGE_ACQUIRED = "Image Acquired";
	private static final String LIVE_VIDEO = "Live Video";
	private static final Color transparentWhite = new Color(1.0f, 1.0f, 1.0f, 0.8f);
	private static final Color transparentRed = new Color(1.0f, 0.0f, 0.0f, 0.8f);
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		FontMetrics fontMetrics;
		
		switch(state) {
		case Freezing:
			g.setColor(transparentWhite);
			fontMetrics = g.getFontMetrics();
			g.fillRect(20, 20 - fontMetrics.getHeight() + fontMetrics.getDescent(), fontMetrics.stringWidth(ACQUIRING_IMAGE), fontMetrics.getHeight());
			g.setColor(Color.black);
			g.drawString(ACQUIRING_IMAGE, 20, 20);
			break;
		case Frozen:
			g.setColor(transparentRed);
			fontMetrics = g.getFontMetrics();
			g.fillRect(20, 20 - fontMetrics.getHeight() + fontMetrics.getDescent(), fontMetrics.stringWidth(IMAGE_ACQUIRED), fontMetrics.getHeight());
			g.setColor(Color.black);
			g.drawString(IMAGE_ACQUIRED, 20, 20);
			break;
		case Thawed:
			g.setColor(transparentWhite);
			fontMetrics = g.getFontMetrics();
			g.fillRect(20, 20 - fontMetrics.getHeight() + fontMetrics.getDescent(), fontMetrics.stringWidth(LIVE_VIDEO), fontMetrics.getHeight());
			g.setColor(Color.black);
			g.drawString(LIVE_VIDEO, 20, 20);
			break;
		default:
		}
		
	}
}
