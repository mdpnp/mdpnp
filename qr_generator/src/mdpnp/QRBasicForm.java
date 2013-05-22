package mdpnp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
	private int FORM_WIDTH = 520; //450;
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
    JLabel labelQRWidth= new JLabel("Image Width px:");
    JTextField tfWidth= new JTextField(String.valueOf(FILE_DEFAULT_SIZE),7);
    JLabel labelQRHeight= new JLabel("Image Height px:");
    JTextField tfHeight= new JTextField(String.valueOf(FILE_DEFAULT_SIZE),7);

    
    //button
    JButton buttonGenerate = new JButton("Generate QR file");
      
    private void init(){

    	JPanel panel = new JPanel();
    	panel.setLayout(new GridBagLayout());
    	//GridBagConstraints gridConst = new GridBagConstraints();
    	Insets myInsets = new Insets(2, 5, 2, 5);
     	GridBagConstraints gridConst = 
    			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, myInsets, 0, 0);

		//file chooser
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
		//1st row: QR File Name + extension (combo)
		gridConst.insets = myInsets;
		gridConst.fill = GridBagConstraints.HORIZONTAL;
		gridConst.gridx = 0;
		gridConst.gridy = 0;
		panel.add(labelQRName, gridConst);
		
		gridConst.gridx = 1;
		gridConst.gridy = 0;
		gridConst.gridwidth = 2;
		panel.add(tfFileName, gridConst);
		
		gridConst.gridx = 3;
		gridConst.gridy = 0;
		gridConst.gridwidth = 1;
		panel.add(jcExt, gridConst);

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
					if(tfFileName.getText().trim().equals("") || tflink.getText().trim().equals(""))
						buttonGenerate.setEnabled(false);						
					else
						buttonGenerate.setEnabled(true);
				     }
		});
		
		
	    //2nd row QR Info (text area)
		//Scroll panel for the test area
		JScrollPane areaScrollPane = new JScrollPane(tflink);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		areaScrollPane.setAutoscrolls(false); //If I only wanted vertical scrollbar 

		//these two are ignored w/ autoscroll=true  
		tflink.setLineWrap(true);
		tflink.setWrapStyleWord(true);
		
		gridConst.gridx = 0;
		gridConst.gridy = 1;
		gridConst.anchor = GridBagConstraints.FIRST_LINE_START;
		panel.add(labelQRInfo, gridConst);
		
		gridConst.gridx = 1;
		gridConst.gridy = 1;
		gridConst.gridwidth = 3;
		panel.add(areaScrollPane, gridConst);

		
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
					if(tfFileName.getText().trim().equals("") ||tflink.getText().trim().equals(""))
						buttonGenerate.setEnabled(false);						
					else
						buttonGenerate.setEnabled(true);
				     }
		});
		
	    //3rd row Size text areas (and labels)
		gridConst.anchor = GridBagConstraints.LINE_END;
		gridConst.gridwidth = 1;
		gridConst.gridx = 0;
		gridConst.gridy = 2;
		panel.add(labelQRWidth, gridConst);
		
		gridConst.gridx = 1;
		gridConst.gridy = 2;
		panel.add(tfWidth, gridConst);
		
		gridConst.gridx = 2;
		gridConst.gridy = 2;
		gridConst.anchor = GridBagConstraints.LINE_END;
		panel.add(labelQRHeight, gridConst);
		
		gridConst.gridx = 3;
		gridConst.gridy = 2;
		panel.add(tfHeight, gridConst);
		getContentPane().add(panel);
		
		//4th Row: Generate button
		gridConst.gridx = 1;
		gridConst.gridy = 3;
		gridConst.gridwidth = 2;
		panel.add(buttonGenerate, gridConst);

		
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
					
				}

			}
		});
	    
		getContentPane().add(panel);
	    setSize(FORM_WIDTH,FORM_HEIGHT);
	    setMinimumSize(new Dimension(FORM_WIDTH,FORM_HEIGHT));
	    setVisible(true);
	    setResizable(true);
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
