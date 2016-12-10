//License
package archie_v1.outputFormats;

import archie_v1.fileHelpers.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;

public abstract class outputAbstract {

    public abstract void Save(String destination, ArrayList<FileHelper> files) throws IOException;
    
    public FileHelper fileSelector(Element fileElement){
        Path filePath = Paths.get(fileElement.getAttributeValue("path"));
        return fileSelector(filePath);
    }
    
    public FileHelper fileSelector(Path filePath){
        String fileExtension = FilenameUtils.getExtension(filePath.toString());
        switch(fileExtension){
            case "jpg":
            case "JPG":
            case "jpeg": 
            case "JPEG": return new pictureFile(filePath);
            case "accdb": return new databaseFile(filePath);
            default: return new basicFile(filePath);
        }
    }
}
