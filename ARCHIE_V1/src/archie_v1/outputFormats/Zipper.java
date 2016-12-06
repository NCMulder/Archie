//License
package archie_v1.outputFormats;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

public class Zipper {

    public void SaveAsZip(String destination, Document[] xmls, String[] sources) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));
        XMLOutputter xmlOutputter = new XMLOutputter();
        ArrayList<String> names = new ArrayList();
        
        for (int i = 0; i < xmls.length; i++) {
            //Appending numbers to prevent overwriting files with identical names.
            int version = 0;
            String fileName = Paths.get(sources[i]).getFileName().toString();
            String fileExtension = "";
            if(fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            while(names.contains(fileName)){
                if(version!=0)
                    fileName = fileName.substring(0, fileName.length() - 3);
                version++;
                fileName += "[" + version + "]";
            }
            names.add(fileName);
            
            //Writing the xml file.
            ZipEntry zEntry = new ZipEntry(fileName + ".xml");
            out.putNextEntry(zEntry);
            byte[] data = xmlOutputter.outputString(xmls[i]).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            
            //Writing the associated file.
            FileInputStream fIS = new FileInputStream(sources[i]);
            //For now, xml files get an extra .xml in their file name.
            ZipEntry fileZipEntry = new ZipEntry(fileName + (".xml".equals(fileExtension)? ".xml" : "") + fileExtension);
            out.putNextEntry(fileZipEntry);
            byte[] readBuffer = new byte[2048];
            int length;
            while((length = fIS.read(readBuffer)) > 0){
                out.write(readBuffer, 0, length);
            }
            out.closeEntry();
        }
        
        out.close();
    }
}
