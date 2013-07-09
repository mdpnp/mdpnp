package org.mdpnp.dts.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * 
 * @author dalonso@mdpnp.org
 * <p>
 * This class is in charge of writing a tex file
 *
 */
public class DTSFileWriter {
	
	private FileWriter fw = null;
	private BufferedWriter bw = null;
	private File file;
	
	private String filePath; //path of the file
	private String fileName; //name of the file
	
	//cons
	public DTSFileWriter(String pathName, String fileName){
		this.filePath = pathName;
		this.fileName = fileName;
		try{
			File file = new File(filePath+fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * closes the file and the buffer
	 */
	public void close(){
		try{
			bw.flush();
			bw.close();
			fw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the string to the file
	 * @param s
	 */
	public void addRow(String s){
		try{
			bw.write(s+"\n");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
