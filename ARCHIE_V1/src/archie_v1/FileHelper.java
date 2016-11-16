//License header
package archie_v1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;



public abstract class FileHelper {
    
public enum MetaDataType{
    Size, CreationDate, CreationTime, FileType, Author, LastModifiedBy
}

    Path filePath;
    protected Map metadata;

    public FileHelper(Path filePath) {
        this.filePath = filePath;
        metadata = getBasicMetaData();
    }

    public Map<Object, String> getBasicMetaData() {
        Map basedata = new HashMap<String, String>();
        //Todo: fill base metadata

        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Filetype setting.
        String fileTypeString = null;
        try {
            fileTypeString = Files.probeContentType(filePath);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
        }
        basedata.put(MetaDataType.FileType, fileTypeString);
        
        //File creation date setting.
        //basedata.put(MetaDataType.CreationDate, attr.creationTime());

        //File size setting.
        basedata.put(MetaDataType.Size, Long.toString(attr.size()));
        
        return basedata;
    }

    public abstract Map<MetaDataType, String> getMetaData();
}
