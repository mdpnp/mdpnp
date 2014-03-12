package org.mdpnp.gip.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class Keypad extends javax.swing.JComponent {
	private JButton[] digits = new JButton[10];
	private JButton decimal = createButton(".");
	private JButton backspace = createButton(new JArrowIcon(270));
	private JButton close = createButton("X");
	private JButton up = createButton(new JArrowIcon(0));
	private JButton down = createButton(new JArrowIcon(180));
	protected Component component;
	protected Popup popup;
	
	public void setPopup(Popup popup) {
		this.popup = popup;
	}
	
	public void setComponent(Component component) {
		this.component = component;
		if(component != null && component instanceof JTextComponent) {
			((JTextComponent)component).setCaretPosition(((JTextComponent)component).getDocument().getLength());
		}
	}

	public class KeyEventDispatchAction implements ActionListener {
		private final int id;
		private final int keyCode;
		private final char keyChar;
		
		public KeyEventDispatchAction(final int id, final int keyCode, final char keyChar) {
			this.id = id;
			this.keyCode = keyCode;
			this.keyChar = keyChar;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Component c = Keypad.this.component;
			if(c != null) {
				if(c instanceof JTextComponent) {
					JTextComponent jtc = (JTextComponent) c;
					jtc.setCaretPosition(jtc.getDocument().getLength());
				}
				KeyEvent event = new KeyEvent(component, this.id, e.getWhen(), 0, keyCode, keyChar);
				component.dispatchEvent(event);
			}
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
	}
	
	private static final JButton createButton(String txt) {
		JButton button = new JButton(txt);
		return button;
	}
	private static final JButton createButton(Icon ico) {
		JButton button = new JButton();
		button.setIcon(ico);
		return button;
	}

	public Keypad() {
		setLayout(new GridLayout(4, 4, 5, 5));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		enableEvents(MouseEvent.MOUSE_EVENT_MASK);
		for(int i = 0; i < digits.length; i++) {
			digits[i] = createButton(Integer.toString(i));
			digits[i].addActionListener(new KeyEventDispatchAction(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, (char)('0'+i)));
		}
		decimal.addActionListener(new KeyEventDispatchAction(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, '.'));
		backspace.addActionListener(new KeyEventDispatchAction(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, ' '));
		up.addActionListener(new KeyEventDispatchAction(KeyEvent.KEY_PRESSED,  KeyEvent.VK_UP, ' '));
		down.addActionListener(new KeyEventDispatchAction(KeyEvent.KEY_PRESSED,  KeyEvent.VK_DOWN, ' '));
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				component.transferFocus();
			}
			
		});
		add(digits[1]);
		add(digits[2]);
		add(digits[3]);
		add(close);
		add(digits[4]);
		add(digits[5]);
		add(digits[6]);
		add(new JLabel(""));
		add(digits[7]);
		add(digits[8]);
		add(digits[9]);
		add(up);
		add(digits[0]);
		add(backspace);
		add(decimal);
		add(down);
		
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("TEST");
		frame.getContentPane().setLayout(new BorderLayout());
		final JTextField field = new JTextField(10);
		final Keypad keypad = new Keypad();
		keypad.setComponent(field);
		frame.getContentPane().add(field, BorderLayout.NORTH);
		frame.getContentPane().add(keypad, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setVisible(true);
		
	}
}
