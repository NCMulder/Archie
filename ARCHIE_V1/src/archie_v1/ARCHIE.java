//License header
package archie_v1;

import archie_v1.UI.ArchieUIManager;
import archie_v1.fileHelpers.*;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.io.FilenameUtils;

public class ARCHIE {
    static ArchieUIManager ui;

    public static void main(String[] args) {
        if(args.length>=2)
            System.out.println(args[1]);
        //User preferences
        
        Preferences prefs = Preferences.userNodeForPackage(MetadataKey.class);
        prefs.put("Publisher", "hahatesttitel");
        
        
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
        }
        ui = new ArchieUIManager();
    }
    
    /**
     * FileHelper selects the correct FileHelper subclass based on the filePath input.
     * @param filePath The file path for the desired FileHelper.
     * @param includeIslandora Selects whether or not the file should be included in an Islandora output.
     * @return
     */
    static public FileHelper fileSelector(Path filePath){
        String fileExtension = FilenameUtils.getExtension(filePath.toString());
        switch(fileExtension){
            case "jpg":
            case "JPG":
            case "jpeg": 
            case "JPEG": return new pictureFile(filePath);
            case "accdb": return new databaseFile(filePath);
            case "doc":
            case "docx":
            case "xls":
            case "xlsx": return new xlsxFile(filePath);
            case "dbf":
            case "pdf":
            case "mxd":
            case "cpg":
            case "sbn":
            case "sbx":
            case "shp":
            case "shx":
            case "png":
            default: return new basicFile(filePath, false, false);
        }
    }
}
