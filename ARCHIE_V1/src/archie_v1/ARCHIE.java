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

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ARCHIE {

    static public ArchieUIManager ui;
    static public Preferences prefs;
    public static String RECENTLY_OPENED_ARCHIEFILE = "recentlyOpenedArchieFile", RECENTLY_OPENED_DIRECTORY = "recentlyOpenedDirectory", RECENTLY_SAVED = "recentlySaved";
    public static String RECENTLY_OPENED_CODEBOOK = "recentlyOpenedCodebook";

    public static void main(String[] args) {
        prefs = Preferences.userNodeForPackage(ARCHIE.class);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
        }

        ui = new ArchieUIManager();
    }

    /**
     * FileHelper selects the correct FileHelper subclass based on the filePath
     * input.
     *
     * @param filePath The file path for the desired FileHelper.
     * @return
     */
    static public FileHelper fileSelector(Path filePath, boolean open) {
        String fileExtension = FilenameUtils.getExtension(filePath.toString());
        switch (fileExtension) {
            case "jpg":
            case "JPG":
            case "jpeg":
            case "JPEG":
                return new pictureFile(filePath, open);
            case "accdb":
                return new databaseFile(filePath, open);
            case "xls":
            case "xlsx":
                return new xlsxFile(filePath, open);
            case "doc":
            case "docx":
            case "dbf":
            case "pdf":
            case "mxd":
            case "cpg":
            case "sbn":
            case "sbx":
            case "shp":
            case "shx":
            case "png":
            default:
                return new basicFile(filePath, open);
        }
    }

    static public FileHelper fileSelector(Path filePath) {
        return fileSelector(filePath, false);
    }
}
