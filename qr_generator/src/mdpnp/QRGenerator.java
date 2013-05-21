package mdpnp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 * 
 * @author diego@mdpnp.org
 * 
 * The aim of this class is to generate QR codes (2D barcodes).
 * It uses Zebra Crossing (ZXing), an open source library, to generate / parse QR Codes
 * and the QRGen as a library to create a layer on top of ZXing to make QR generation really easy.
 * 
 * ZXing:
 *	 https://code.google.com/p/zxing/
 * QRGen:
 *	 http://kenglxn.github.io/QRGen/
 *   (see for usage)
 *
 */
public class QRGenerator {

 private int FILE_DEFAULT_SIZE = 125;//image default size
	
 private String sPath ="./output"; //default output path
 private String qrName; //image file name
 private String qrInfo; //info coded in the QR
 private String extension; //file extension (allowed by XZing)
 private int width = FILE_DEFAULT_SIZE;
 private int height = FILE_DEFAULT_SIZE;
 
 //cons
 public QRGenerator(String qrName, String qrInfo, String extension){
	 this.qrName = qrName;
	 this.qrInfo = qrInfo;
	 this.extension = extension; 
}
 
 public QRGenerator(String path, String qrName, String qrInfo, String extension){
	 this.sPath = path;
	 this.qrName = qrName;
	 this.qrInfo = qrInfo;
	 this.extension = extension; 
}
 
 public QRGenerator(String qrName, String qrInfo, String extension, int width, int height){
	 this.qrName = qrName;
	 this.qrInfo = qrInfo;
	 this.extension = extension; 
	 this.height = height;
	 this.width = width;
 }
 
 public QRGenerator(String path, String qrName, String qrInfo, String extension, int width, int height){
	 this.sPath = path;
	 this.qrName = qrName;
	 this.qrInfo = qrInfo;
	 this.extension = extension; 
	 this.height = height;
	 this.width = width;
 }
 
 
 public void generateQR(){
 	ImageType imageType;
	
 	if(this.extension.equalsIgnoreCase("gif"))	imageType= ImageType.GIF;
 	else if(this.extension.equalsIgnoreCase("jpg")) imageType= ImageType.JPG;
 	else if(this.extension.equalsIgnoreCase("png")) imageType = ImageType.PNG;
 	else{
 		//System.out.println("unsuported format!!");
 		return;
 	}
 	
	//Stream w/ our output info
	 ByteArrayOutputStream out;
	 if(this.width != FILE_DEFAULT_SIZE ||  this.height!=FILE_DEFAULT_SIZE)
		 out =  QRCode.from(qrInfo).to(imageType).withSize(width, height).stream();
	 //XXX width and height my be different, for the image file, but the QR Code is always square
	 else
		 out = QRCode.from(qrInfo).to(imageType).stream();
	 //QRCode.from("Hello World").withSize(250, 250).file();
	 

    try {
    	//Output file
    	File file = new File(sPath+"//"+qrName+"."+extension);
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	FileOutputStream fout = new FileOutputStream(file);
        
        fout.write(out.toByteArray());

        fout.flush();
        fout.close();

    } catch (FileNotFoundException e) {
       e.printStackTrace();
    } catch (IOException e) {
       e.printStackTrace();
    }
 }
 
 
 //getters / setters
public String getQrName() {
	return qrName;
}

public void setQrName(String qrName) {
	this.qrName = qrName;
}

public String getQrInfo() {
	return qrInfo;
}

public void setQrInfo(String qrInfo) {
	this.qrInfo = qrInfo;
}

public String getExtension() {
	return extension;
}

public void setExtension(String extension) {
	this.extension = extension;
}

public int getWidth() {
	return width;
}

public void setWidth(int width) {
	this.width = width;
}

public int getHeight() {
	return height;
}

public void setHeight(int height) {
	this.height = height;
}

public String getsPath() {
	return sPath;
}

public void setsPath(String sPath) {
	this.sPath = sPath;
}
 

 

}
