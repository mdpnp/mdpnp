package org.mdpnp.guis.waveform.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.media.opengl.GLAnimatorControl;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.guis.opengl.jogl.GLPanel;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.EvenTempoWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;
import org.mdpnp.guis.waveform.opengl.GLWaveformRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class GLWaveformPanel extends GLPanel implements WaveformPanel {
	private final GLWaveformRenderer renderer;
	private final JPopupMenu popup;
	private JFrame dataFrame, cacheFrame;
	
	private final static class WaveformSourceTableModel extends AbstractTableModel implements TableModel, WaveformSourceListener {
		private final WaveformSource source;
		
		public WaveformSourceTableModel(WaveformSource source) {
			this.source = source;
		}

		@Override
		public void waveform(WaveformSource source) {
			fireTableDataChanged();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Integer.class;
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		@Override
		public String getColumnName(int column) {
			switch(column) {
			case 0:
				return "Index";
			case 1:
				return "Value";
			default:
				return null;
			}
		}
		@Override
		public int getRowCount() {
			return source.getMax();
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				return rowIndex;
			case 1:
				return source.getValue(rowIndex);
			default:
				return null;
			}
		}

		@Override
		public void reset(WaveformSource source) {
		}
	}

	public GLWaveformPanel() {
		this(new GLWaveformRenderer());
	}
	
	private final JMenuItem sampleRate = new JMenuItem();
	
	public GLWaveformPanel(GLWaveformRenderer renderer) {
		super(renderer);

		enableEvents(ComponentEvent.COMPONENT_RESIZED | MouseEvent.MOUSE_PRESSED | MouseEvent.MOUSE_RELEASED);
		this.renderer = renderer;
		
		this.popup = new JPopupMenu("Options");
		final JMenuItem cacheItem = new JMenuItem("Set Time Domain");
		cacheItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(null == cacheFrame) {
					final CachingWaveformSource cachesource = getRenderer().cachingSource();
					cacheFrame = new JFrame("Set Time Domain (seconds)");
					cacheFrame.getContentPane().setLayout(new BorderLayout());
					final JLabel valueLabel = new JLabel(Long.toString(cachesource.getFixedTimeDomain()/1000)+" seconds");
					
					final JSlider slider = new JSlider();
					slider.setMaximum(5 * 60);
//					slider.setSnapToTicks(true);
					slider.setPaintTicks(true);
					slider.setPaintLabels(true);
					slider.setMajorTickSpacing(60);
//					slider.setMinorTickSpacing(1000);
					slider.setValue((int) (long)cachesource.getFixedTimeDomain()/1000);

					slider.addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent arg0) {
							cachesource.setFixedTimeDomain(slider.getValue()*1000);
							valueLabel.setText(Long.toString(cachesource.getFixedTimeDomain()/1000)+" seconds");
						}
						
					});
					cacheFrame.getContentPane().add(slider, BorderLayout.CENTER);
					cacheFrame.getContentPane().add(valueLabel, BorderLayout.SOUTH);
					cacheFrame.setSize(640, 120);
					
				}
				cacheFrame.setLocationRelativeTo(GLWaveformPanel.this);
				cacheFrame.setVisible(true);
			}
		});
		
		this.popup.add(cacheItem);
		this.popup.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                WaveformSource source = getRenderer().getSource();
                if(null == source) {
                    sampleRate.setText("");
                } else {
                    sampleRate.setText(""+source.getMillisecondsPerSample()+" ms/sample");
                }
				if(null == getRenderer().cachingSource()) {
					cacheItem.setVisible(false);
				} else {
					cacheItem.setVisible(true);
				}
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				
			}
		});
		
		JMenuItem realdata = new JMenuItem("Show Real Data");
		this.popup.add(realdata);
		realdata.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(null == dataFrame) {
					dataFrame = new JFrame("Waveform Data");
					dataFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					final JTable table = new JTable(new WaveformSourceTableModel(getRenderer().getSource()));
					dataFrame.getContentPane().add(new JScrollPane(table));
					dataFrame.setSize(640, 480);
					dataFrame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							getRenderer().getSource().removeListener((WaveformSourceListener) table.getModel());
							table.getModel().removeTableModelListener(table);
							super.windowClosing(e);
						}
					});
				}
				dataFrame.setLocationRelativeTo(GLWaveformPanel.this);
				dataFrame.setVisible(true);
			}
		});
		JMenuItem aboutPanel = new JMenuItem(GLWaveformPanel.class.getSimpleName());
		this.popup.add(aboutPanel);
		this.popup.add(sampleRate);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
		case MouseEvent.MOUSE_RELEASED:
			if(e.isPopupTrigger()) {
				popup.show(this, e.getX(), e.getY());
			} else {
				getRenderer().rescaleValue();
			}
		}
	}
	
	public void setSource(WaveformSource source) {
		renderer.setSource(source);
	}
	
	public GLWaveformRenderer getRenderer() {
		return renderer;
	}

	@Override
	public Component asComponent() {
		return this;
	}

	@Override
	public CachingWaveformSource cachingSource() {
		return getRenderer().cachingSource();
	}

	@Override
	public EvenTempoWaveformSource evenTempoSource() {
		return getRenderer().evenTempoSource();
	}
	
	@Override
	public void setOutOfTrack(boolean outOfTrack) {
		renderer.setOutOfTrack(outOfTrack);
	}
	
	@Override
	public void start() {
	    GLAnimatorControl singleton = AnimatorSingleton.getInstance();
	    singleton.add(this);
	}
	@Override
	public void stop() {
	    final GLAnimatorControl singleton = getAnimator();
	    try {
	        // Making this call from the AWT thread because AWTAnimatorImpl seems to prefer it
	        Runnable r = new Runnable() {
                public void run() {
                    singleton.remove(GLWaveformPanel.this);
                }
            };
            if(SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeAndWait(r);
            }
        } catch (InvocationTargetException e) {
            log.error("Error removing from animator", e);
        } catch (InterruptedException e) {
            log.error("Error removing from animator", e);
        }
	    
        AnimatorSingleton.releaseInstance(singleton);
	}
	protected final static Logger log = LoggerFactory.getLogger(GLWaveformPanel.class);
}
