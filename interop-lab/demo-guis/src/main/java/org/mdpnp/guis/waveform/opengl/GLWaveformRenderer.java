package org.mdpnp.guis.waveform.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.mdpnp.guis.opengl.GLRenderer;
import org.mdpnp.guis.opengl.OpenGL;
import org.mdpnp.guis.waveform.AbstractNestedWaveformSource;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.EvenTempoWaveformSource;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

public class GLWaveformRenderer implements GLRenderer, WaveformSourceListener {

	private long[] startTime = new long[FRAME_SAMPLE];
	private int frames = 0;
	private static final int FRAME_SAMPLE = 100;

	public static class Color {
		public float red, green, blue, alpha;

		public Color(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}

	private Color background = new Color(0,0,0,1), foreground = new Color(0,1,0,1);

	public void setBackground(Color color) {
		this.background = color;
	}

	public void setForeground(Color color) {
		this.background = color;
	}

	private WaveformSource source;

	public GLWaveformRenderer() {
	}
	
	public GLWaveformRenderer(WaveformSource source) {
		setSource(source);
	}

	public void setSource(WaveformSource source) {
		if (null != this.source) {
			this.source.removeListener(this);
		}
		this.source = null == source ? null : new EvenTempoWaveformSource(
				new CachingWaveformSource(source, 5000L));

		if (null != this.source) {
			this.source.addListener(this);
		}
	}

	protected FloatBuffer buffer; 
	
	protected int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE, minX = 0,
			maxX = 1, curX = 1, maxCurX;
	protected int x_gap = 0;

	protected static final void gluOrtho2D(OpenGL gl, float left, float right,
			float bottom, float top) {
		gl.glOrthof(left, right, bottom, top, -1.0f, 1.0f);
	}

	private static final int CIRCLE_POINTS = 100;
	private static final ByteBuffer unitCircle = unitCircle2d(CIRCLE_POINTS);
	
	@Override
	public void render(OpenGL gl, int width, int height) {
		startTime[frames] = System.currentTimeMillis();
		frames = (frames + 1) >= FRAME_SAMPLE ? 0 : (frames + 1);

		gl.glClearColor(background.red, background.green, background.blue,
				background.alpha);
		gl.glColor4f(foreground.red, foreground.green, foreground.blue,
				foreground.alpha);

		gl.glClear(OpenGL.GL_COLOR_BUFFER_BIT);

		FloatBuffer myBuffer = this.buffer;
		if (null == myBuffer) {
			return;
		}

		gl.glMatrixMode(OpenGL.GL_PROJECTION);
		gl.glLoadIdentity();

		gluOrtho2D(gl, minX, maxX, minY, maxY);

//		gl.glEnable(OpenGL.GL_LINE_SMOOTH);
//		gl.glHint(OpenGL.GL_LINE_SMOOTH_HINT, OpenGL.GL_NICEST);
//		gl.glLineWidth(2.0f);

		gl.glEnableClientState(OpenGL.GL_VERTEX_ARRAY);

		gl.glVertexPointer(2, OpenGL.GL_FLOAT, 0, myBuffer);
		gl.glDrawArrays(OpenGL.GL_LINE_STRIP, 0, curX);
		if (maxCurX > (curX + x_gap)) {
			gl.glDrawArrays(OpenGL.GL_LINE_STRIP, curX + x_gap, maxCurX - curX
					- x_gap);
		}

		gl.glDisableClientState(OpenGL.GL_VERTEX_ARRAY);

		gl.glMatrixMode(OpenGL.GL_MODELVIEW);
		//
		// // DRAW A CIRCLE
		//
		 if(outOfTrack) {
		
			 gl.glMatrixMode(OpenGL.GL_PROJECTION);
			
			 gl.glLoadIdentity();
			 gl.glPushMatrix();
			 gluOrtho2D(gl, 0,width,0,height);
			
			 gl.glTranslatef(width-20, height-20, 0);
			 gl.glScalef(10, 10, 0);
			
			
			 gl.glEnable(OpenGL.GL_LINE_SMOOTH);
			 gl.glHint(OpenGL.GL_LINE_SMOOTH_HINT, OpenGL.GL_NICEST);
//			 gl.glLineWidth(2);
			 gl.glEnableClientState(OpenGL.GL_VERTEX_ARRAY);
			
			 gl.glVertexPointer(2, OpenGL.GL_FLOAT, 0, unitCircle);
			 gl.glDrawArrays(OpenGL.GL_LINE_LOOP, 0, CIRCLE_POINTS);
			 gl.glDisableClientState(OpenGL.GL_VERTEX_ARRAY);
			
			 gl.glPopMatrix();
			
			 gl.glMatrixMode(OpenGL.GL_MODELVIEW);
		 }

		// GLES10.glPopMatrix();
		
		if (frames == 0) {
			int prevFrame = (frames - FRAME_SAMPLE + 1) < 0 ? (frames + 1)
					: (frames - FRAME_SAMPLE + 1);
			long msPerFrame = (System.currentTimeMillis() - startTime[prevFrame])
					/ FRAME_SAMPLE;
			// Log.d(GLWaveformView.class.getName(),
			// "FPS="+(1.0/msPerFrame*1000.0));
		}
	}
	
	protected int lastCount = 0;

	protected static final int decr(int x, int max) {
		return --x<0?(max-1):x;
	}
	protected static final int incr(int x, int max) {
		return ++x>=max?0:x;
	}
	
	@Override
	public void waveform(WaveformSource source) {
		maxX = source.getMax();
		curX = source.getCount();
		
		if((curX-1)>=maxCurX) {
			maxCurX = curX-1;
		}

		if(buffer == null || buffer.capacity()<(2*maxX)) {
			x_gap = (int)(0.05 * maxX); 
			buffer = ByteBuffer.allocateDirect(Float.SIZE * 2 * maxX ).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}
		
		boolean incremental = true;

		
		if(incremental) {
			int x = lastCount;
			int x1;
//			buffer.position(2 * x);
			
//			maxX = max;
			
			if(curX >= 0) {
				
				while(x != curX) {
					int y = source.getValue(x);
					if(y < minY) {
						minY = Math.min(y, minY);
						x = 0;
//						buffer.position(0);
						continue;
					}
					if(y > maxY) {
						maxY = Math.max(y, maxY);
						x = 0;
//						buffer.position(0);
						continue;
					}
					x1 = incr(x, maxX);
//					Log.d(GLWaveformView.class.getName(), "point x="+x+",y="+y);
					// Don't draw the wraparound line (from max back to 0)
					if(x1 > x && x != x1) {
//						Log.d(GLWaveformView.class.getName(), "point applied");
						buffer.put(2*x, x).put(2*x+1,y);
					} else {
//						buffer.position(0);
					}
					x = x1;
				}
			}
			lastCount = curX;
		} else {
//			buffer.position(0);
			for(int i = 0; i < maxX; i++) {
				int y = source.getValue(i);
				if(y<minY) {
					minY = y;
				}
				if(y>maxY) {
					maxY = y;
				}
				buffer.put(2*i, i).put(2*i+1,y);
			}
		}
		
		
//		Log.d(GLWaveformView.class.getName(), "TOOK " + (System.currentTimeMillis()-start) + "ms to populate");
//		requestRender();
//		renderer.render(glRenderer, rect);
	}

	@Override
	public void reset(WaveformSource source) {
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		minX = 0;
		maxX = 1;
		curX = 1;
		maxCurX = 0;
		x_gap = 0;
	}

	@Override
	public void init(OpenGL gl, int width, int height) {
		gl.glViewport(0,0,width,height);
	}
	
	private boolean outOfTrack = false;
	
	public void setOutOfTrack(boolean outOfTrack) {
		this.outOfTrack = outOfTrack;
	}
	public boolean getOutOfTrack() {
		return outOfTrack;
	}
	private static final ByteBuffer unitCircle2d(final int points) {
		ByteBuffer bb = ByteBuffer.allocateDirect(2 * Float.SIZE * points).order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		double PI_OVER_4 = (Math.PI/4.0);
		double PI_TIMES_2 = (Math.PI * 2.0);
		fb.put((float) Math.cos(PI_OVER_4));
		fb.put((float) Math.sin(PI_OVER_4));
		fb.put((float) Math.cos(5.0f * PI_OVER_4));
		fb.put((float) Math.sin(5.0f * PI_OVER_4));
		
		final int points_minus_2 = points - 2;
		for(int i = 0; i < points_minus_2; i++) {
			fb.put((float) Math.cos(PI_OVER_4 + i * PI_TIMES_2 / points_minus_2));
			fb.put((float) Math.sin(PI_OVER_4 + i * PI_TIMES_2 / points_minus_2));
		}
		
		return bb;
	}
	public void rescaleValue() {
		maxY = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
//		log.debug("Rescaling the y axis"); 
	}
	
	public EvenTempoWaveformSource evenTempoSource() {
		return AbstractNestedWaveformSource.source(EvenTempoWaveformSource.class, source);
	}
	
	public CachingWaveformSource cachingSource() {
		return AbstractNestedWaveformSource.source(CachingWaveformSource.class, source);
	}

	public WaveformSource getSource() {
		return source;
	}

}
