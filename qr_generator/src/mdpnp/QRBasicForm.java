package mdpnp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * @author diego@mdpnp.org
 * 
 * This is a basic window to enable customizations of properties to call QR Generator
 *
 */
public class QRBasicForm extends JFrame{
	
	private int FILE_DEFAULT_SIZE = 125;
	private int FORM_WIDTH = 450;
	private int FORM_HEIGHT = 250;
	
    public QRBasicForm() {
    	super("MD PnP QR Generator");
    	init();
    }
    
    //file chooser to chane image path
	JFileChooser fc = new JFileChooser();	
    
    //name of the file to generate
    JLabel labelQRName = new JLabel("QR Filename:");
    JTextField tfFileName = new JTextField("filename",25);
    //extension of the file
    String[] extension ={"gif", "png", "jpg"};
    JComboBox jcExt = new JComboBox(extension);

    //info on the file to generate
    JLabel labelQRInfo= new JLabel("QR info / link:");
   // JTextField tflink= new JTextField("link",30);
    JTextArea tflink = new JTextArea("link", 5, 30);

    
    //size of the image file
    JLabel labelQRWidth= new JLabel("Image Width  px:");
    JTextField tfWidth= new JTextField(String.valueOf(FILE_DEFAULT_SIZE),3);
    JLabel labelQRHeight= new JLabel("Image Height  px:");
    JTextField tfHeight= new JTextField(String.valueOf(FILE_DEFAULT_SIZE),3);

    
    //button
    JButton buttonGenerate = new JButton("Generate QR file");
    
    //extra row for custom message
    JLabel jlDone = new JLabel();
      
    private void init(){
    	/**
    	 * TODO Use gridBagLayout to align components.
    	 * Right now it has a quick-and-dirty approach of using panel on a JFrame
    	 * and disabling the resizing ability.
    	 * The correct approach should be using a gridBagLayout to display
    	 * component beautifully for different platforms/screens...
    	 */
    	FlowLayout layout = new FlowLayout();
    	layout.setAlignment(FlowLayout.LEFT);
		getContentPane().setLayout(layout);
		
		//file chooser
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
		//QR File Name
		JPanel panel1 = new JPanel();
		panel1.add(labelQRName);
		panel1.add(tfFileName);
		panel1.add(jcExt);	
		getContentPane().add(panel1);

		/**
		 * Listener for the file_name text field
		 */
		tfFileName.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    checkText();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  checkText();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  checkText();
				  }

				  //will disable the "Generate QR" button if we have no file_name or qr_link
				  public void checkText() {
					jlDone.setText("");
					if(tfFileName.getText().trim().equals("") || tflink.getText().trim().equals(""))
						buttonGenerate.setEnabled(false);						
					else
						buttonGenerate.setEnabled(true);
				     }
		});
		
		
	    //QR Info
		JPanel panel2 = new JPanel();
		panel2.add(labelQRInfo);
//		panel2.add(tflink);
		//Scroll panel for the test area
		JScrollPane areaScrollPane = new JScrollPane(tflink);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(335, 50));
		panel2.add(areaScrollPane);
		tflink.setAutoscrolls(true);
		tflink.setLineWrap(true);
		tflink.setWrapStyleWord(true);
		
		getContentPane().add(panel2);
		
		/**
		 * Listener for the QR link (information)
		 */
		tflink.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    checkText();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  checkText();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  checkText();
				  }

				  //will disable the "Generate QR" button if we have no file_name or qr_link
				  public void checkText() {
					jlDone.setText("");
					if(tfFileName.getText().trim().equals("") ||tflink.getText().trim().equals(""))
						buttonGenerate.setEnabled(false);						
					else
						buttonGenerate.setEnabled(true);
				     }
		});
		
	    //Size
		JPanel panel3 = new JPanel();
		panel3.add(labelQRWidth);
		panel3.add(tfWidth);
		panel3.add(labelQRHeight);
		panel3.add(tfHeight);

	    
	    //button
		panel3.add(buttonGenerate);
		/**
		 * Action listener for the GEnerate QR Button
		 */
	    buttonGenerate.addActionListener(new ActionListener() {
			/**action is:
			 * 1: Launch file chooser to allow image PATH SELECTION
			 * 2: Fetch data and generate QR
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				// Launch file chooser 
				int returnVal = fc.showDialog(getContentPane(), "Select path for file "+tfFileName.getText()+"."+jcExt.getSelectedItem().toString());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            String path = file.getAbsolutePath();
		            
					QRGenerator qrGen;
					qrGen= new QRGenerator(path, tfFileName.getText(), tflink.getText(), jcExt.getSelectedItem().toString());	

					//change size if necesary
					int iWidth = (tfWidth.getText()!=null && !tfWidth.getText().trim().equals(""))?Integer.parseInt(tfWidth.getText()):FILE_DEFAULT_SIZE;			
					qrGen.setWidth(Math.abs(iWidth));
					int iHeigth = (tfHeight.getText()!=null && !tfHeight.getText().trim().equals(""))?Integer.parseInt(tfHeight.getText()):FILE_DEFAULT_SIZE;
					qrGen.setHeight(Math.abs(iHeigth));
					
					qrGen.generateQR();
					
					//Optional line to clarify
					jlDone.setText("Created : "+path +"\\" +tfFileName.getText()+"."+jcExt.getSelectedItem());
				}

			}
		});
	    
	    getContentPane().add(panel3);
	    
	    //extra
	    JPanel panel4 = new JPanel();	    
	    panel4.add(jlDone);
	    getContentPane().add(panel4);
	    
	    setSize(FORM_WIDTH,FORM_HEIGHT);
	    setVisible(true);
	    setResizable(false);
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// launch form
		QRBasicForm fb = new QRBasicForm();
	}

}
