//License
package archie_v1.outputFormats;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

public class Zipper {
    ArrayList<String> names = new ArrayList();

    public void SaveAsZip(String destination, HashMap<Path, Document> documentMap) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(FilenameUtils.removeExtension(destination) + ".zip"));
        XMLOutputter xmlOutputter = new XMLOutputter();
        
        for (Map.Entry<Path, Document> documentEntry : documentMap.entrySet()) {
            Path filePath = documentEntry.getKey();
            Document document = documentEntry.getValue();
            
            String[] fileNames = checkDuplicateNames(filePath);
            
            //Writing the xml file.
            ZipEntry zEntry = new ZipEntry(fileNames[0] + ".xml");
            out.putNextEntry(zEntry);
            byte[] data = xmlOutputter.outputString(document).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            
            //Writing the associated file.
            FileInputStream fIS = new FileInputStream(filePath.toString());
            //For now, xml files get an extra .xml in their file name.
            ZipEntry fileZipEntry = new ZipEntry(fileNames[0] + (".xml".equals(fileNames[1])? ".xml" : "") + fileNames[1]);
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
    
    private String[] checkDuplicateNames(Path path){
        int version = 0;
            String fileName = path.getFileName().toString();
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
            
        String[] stringArray = {fileName, fileExtension};
        return stringArray;
    }
}
