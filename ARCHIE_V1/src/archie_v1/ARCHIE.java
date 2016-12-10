//License header
package archie_v1;

import archie_v1.UI.UIManager;
import archie_v1.fileHelpers.*;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;

public class ARCHIE {
    static UIManager ui;

    public static void main(String[] args) {
        ui = new UIManager();
    }
    
    static public FileHelper fileSelector(Path filePath){
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
