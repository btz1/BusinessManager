package pk.temp.bm.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;

public class ZipService {

	
	public static File zipFile(File to_zip_file){
		byte[] buffer = new byte[1024];
		File zip_file = new File(FilenameUtils.removeExtension(to_zip_file.getName())+".zip");
		try{
		zip_file.createNewFile();
		FileOutputStream fos = new FileOutputStream(zip_file);
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		if(to_zip_file.exists()){
			FileInputStream fis = new FileInputStream(to_zip_file);
			zos.putNextEntry(new ZipEntry(to_zip_file.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
		zos.close();
		fos.close();
		}catch( Exception ex)
		{
			//SendEmail.sent("ZipService", "zipFile", "error in FileZip function", ex);
		}
		return zip_file;
	}
	
	public String unzipFile(File file){
		
		return null;
	}
	
}
