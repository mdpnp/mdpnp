package org.mdpnp.guis.waveform.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.guis.opengl.jogl.GLPanel;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.EvenTempoWaveformSource;
import org.mdpnp.guis.waveform.NestedWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;
import org.mdpnp.guis.waveform.opengl.GLWaveformRenderer;

import com.jogamp.opengl.util.FPSAnimator;

@SuppressWarnings("serial")
public class GLWaveformPanel extends GLPanel implements WaveformPanel {
	private final GLWaveformRenderer renderer;
	private boolean dct = false;
	private SwingDCTSource dct_source;
	private final JPopupMenu popup;
	private JFrame dataFrame, coeffFrame, dataFrame2, cacheFrame;
	
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
			// TODO Auto-generated method stub
			
		}
	}

	public GLWaveformPanel() {
		this(new GLWaveformRenderer());
	}
	
//	private static final GLWaveformRenderer.Color color(Color c) {
//		if(null == c) {
//			return new GLWaveformRenderer.Color(1.0f, 1.0f, 1.0f, 0.0f);
//		} else {
//			return new GLWaveformRenderer.Color(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
//		}
//	}
//	
//	@Override
//	public void setBackground(Color c) {
//		super.setBackground(c); 
//		renderer.setBackground(color(c));
//	}
//	
//	@Override
//	public void setForeground(Color c) {
//		super.setForeground(c);
//		renderer.setForeground(color(c));
//	}
	
	
	public GLWaveformPanel(GLWaveformRenderer renderer) {
		super(renderer);
		

		enableEvents(ComponentEvent.COMPONENT_RESIZED | MouseEvent.MOUSE_PRESSED | MouseEvent.MOUSE_RELEASED);
		this.renderer = renderer;
		
//		setBackground(getBackground());
//		setForeground(getForeground());
		
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
		
		final JMenuItem dctItm = new JCheckBoxMenuItem("Enable DCT");
		dctItm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(dct ^ dctItm.isSelected()) {
					dct = dctItm.isSelected();
					WaveformSource src = getRenderer().getSource();
					while(src instanceof NestedWaveformSource) {
						src = ((NestedWaveformSource)src).getTarget();
					}
					setSource(null);
					setSource(src);
				}
				
				
			}
			
		});
		this.popup.add(dctItm);
		JMenuItem coeff = new JMenuItem("Control Coefficients");
		coeff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(null == coeffFrame) {
					coeffFrame = new JFrame("Coefficient Control");
					coeffFrame.getContentPane().setLayout(new BorderLayout());
					final JLabel valueLabel = new JLabel(Integer.toString(dct_source.getMaxCoeff()));
					
					final JSlider slider = new JSlider();
					slider.setMaximum(dct_source.getMax());
					slider.setValue(dct_source.getMaxCoeff());
					slider.addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent arg0) {
							dct_source.setMaxCoeff(slider.getValue());
							valueLabel.setText(Integer.toString(slider.getValue()));
						}
						
					});
					coeffFrame.getContentPane().add(slider, BorderLayout.CENTER);
					coeffFrame.getContentPane().add(valueLabel, BorderLayout.SOUTH);
					coeffFrame.setSize(640, 480);
					
				}
				coeffFrame.setLocationRelativeTo(GLWaveformPanel.this);
				coeffFrame.setVisible(true);
			}
		});
		this.popup.add(coeff);
		JMenuItem data = new JMenuItem("Show Data");
		this.popup.add(data);
		data.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(null == dataFrame) {
					dataFrame = new JFrame("Waveform Data");
					JTable table = new JTable(dct_source);
					dataFrame.getContentPane().add(new JScrollPane(table));
					dataFrame.setSize(640, 480);
				}
				dataFrame.setLocationRelativeTo(GLWaveformPanel.this);
				dataFrame.setVisible(true);
			}
		});
		JMenuItem realdata = new JMenuItem("Show Real Data");
		this.popup.add(realdata);
		realdata.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(null == dataFrame2) {
					dataFrame2 = new JFrame("Waveform Data");
					dataFrame2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					final JTable table = new JTable(new WaveformSourceTableModel(getRenderer().getSource()));
					dataFrame2.getContentPane().add(new JScrollPane(table));
					dataFrame2.setSize(640, 480);
					dataFrame2.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							getRenderer().getSource().removeListener((WaveformSourceListener) table.getModel());
							table.getModel().removeTableModelListener(table);
							super.windowClosing(e);
						}
					});
				}
				dataFrame2.setLocationRelativeTo(GLWaveformPanel.this);
				dataFrame2.setVisible(true);
			}
		});
		
//		
		

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
	    setAnimator(new FPSAnimator((GLAutoDrawable) this, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL));
        getAnimator().start();   
	}
	@Override
	public void stop() {
        getAnimator().stop();
        getAnimator().remove(this);    
	}
}
