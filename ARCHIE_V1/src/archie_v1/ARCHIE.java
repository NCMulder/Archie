//License header
package archie_v1;

import archie_v1.UI.UIManager;
import archie_v1.fileHelpers.*;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;

public class ARCHIE {
    static UIManager ui;
    
    /**
     * This is a javadoc? Really? Squiggly lines?
     */
    public enum MetadataKey{CreatorName, RelatedItemID;
    
    public static String[] names(){
        MetadataKey[] keys = values();
        String[] names = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            names[i] = keys[i].name();
        }
        return names;
    }
}

    public static void main(String[] args) {
        ui = new UIManager();
    }
    
    static public FileHelper fileSelector(Path filePath, boolean includeIslandora){
        String fileExtension = FilenameUtils.getExtension(filePath.toString());
        switch(fileExtension){
            case "jpg":
            case "JPG":
            case "jpeg": 
            case "JPEG": return new pictureFile(filePath, includeIslandora);
            case "accdb": return new databaseFile(filePath, includeIslandora);
            default: return new basicFile(filePath, includeIslandora);
        }
    }
}
