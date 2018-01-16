package pk.temp.bm.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnzipFiles {
	
	private InputStream file_stream;
	private String path_to_unzip;
	private static final Logger logger = LoggerFactory.getLogger(UnzipFiles.class);
	public UnzipFiles(InputStream file_stream, String path) {
		super();
		this.file_stream = file_stream;
		this.path_to_unzip = path;
	}
	
	public String unzipSingleFileFromStream(){
		byte[] buffer = new byte[1024];
		File newFile = null;
		try{
			File folder = new File(this.path_to_unzip);
			if(!folder.exists()){
	    		folder.mkdir();
	    	}
			ZipInputStream zis = new ZipInputStream(this.file_stream);
			ZipEntry ze = zis.getNextEntry();
			while(ze != null){
				String fileName = ze.getName();
				newFile = new File(this.path_to_unzip + File.separator + fileName);
				logger.info("unzipping file: "+ newFile.getAbsoluteFile());
				FileOutputStream fos = new FileOutputStream(newFile); 
				int len;
	            while ((len = zis.read(buffer)) > 0) {
	            	fos.write(buffer, 0, len);
	            }
	            fos.close();
	            ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			this.file_stream.close();
		}catch(Exception ex){
//			logger.error("Error in unzipping", new Exception(SendEmail.getStrackTrace(ex)));
//			SendEmail.sent("UnzipFiles.java", "unzipFile", "Error in unzipping file", ex);
		}
		if (newFile != null)
		{
			return newFile.getAbsolutePath();
		}
			return null;
	}
	
	public File unzipSingleFileObjectFromStream(){
		byte[] buffer = new byte[1024];
		File newFile = null;
		logger.info("Path to unzipped file "+ this.path_to_unzip);
		try{
			File folder = new File(this.path_to_unzip);
			if(!folder.exists()){
	    		folder.mkdir();
	    	}
			ZipInputStream zis = new ZipInputStream(this.file_stream);
			ZipEntry ze = zis.getNextEntry();
			while(ze != null){
				String fileName = ze.getName();
				newFile = new File(this.path_to_unzip + File.separator + fileName);
				logger.info("unzipping file: "+ newFile.getAbsoluteFile());
				FileOutputStream fos = new FileOutputStream(newFile); 
				int len;
	            while ((len = zis.read(buffer)) > 0) {
	            	fos.write(buffer, 0, len);
	            }
	            fos.close();
	            ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			this.file_stream.close();
		}catch(Exception ex){
			//logger.error("Error in unzipping", new Exception(SendEmail.getStrackTrace(ex)));
			//SendEmail.sent("UnzipFiles.java", "unzipFile", "Error in unzipping file", ex);
		}
		if (newFile != null)
		{
			return newFile.getAbsoluteFile();
		}
			return null;
	}
	
}
