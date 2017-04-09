//License header
package archie_v1;

import archie_v1.UI.ArchieUIManager;
import archie_v1.fileHelpers.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.io.FilenameUtils;

public class ARCHIE {

    static public ArchieUIManager ui;

    public static void main(String[] args) {
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
     * @param includeIslandora Selects whether or not the file should be
     * included in an Islandora output.
     * @return
     */
    static public FileHelper fileSelector(Path filePath) {
        String fileExtension = FilenameUtils.getExtension(filePath.toString());
        switch (fileExtension) {
            case "jpg":
            case "JPG":
            case "jpeg":
            case "JPEG":
                return new pictureFile(filePath);
            case "accdb":
                return new databaseFile(filePath);
            case "xls":
            case "xlsx":
                return new xlsxFile(filePath);
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
                return new basicFile(filePath);
        }
    }

    public static String getRecentlyOpened() {
        File tempDir = new File("temp");
        tempDir.mkdirs();
        File prefsFile = new File(tempDir, "prefs.txt");
        if (prefsFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(prefsFile));
                String line;
                while(!"opened:".equals(line = reader.readLine())){
                    
                }
                return reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public static void setRecentlyOpened(Path path) {
        File tempDir = new File("temp");
        tempDir.mkdirs();
        File prefsFile = new File(tempDir, "prefs.txt");
        try {
            if (!prefsFile.exists()) {
                prefsFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(prefsFile));
                writer.write("opened:\n");
                writer.write(path.toString() + "\n");
                writer.write("generated:\n");
                writer.close();
                return;
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(prefsFile));
                LinkedList<String> prefsList = new LinkedList();
                String line;
                while ((line = reader.readLine()) != null) {
                    prefsList.add(line);
                }
                prefsFile.delete();
                prefsFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(prefsFile));
                for (int i = 0; i < prefsList.size(); i++) {
                    String oneLine = prefsList.get(i);
                    if (oneLine.equals("opened:")) {
                        writer.write("opened:\n");
                        writer.write(path.toString() + "\n");
                        i++;
                    } else {
                        writer.write(oneLine + "\n");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getRecentlyGenerated() {
        File tempDir = new File("temp");
        tempDir.mkdirs();
        File prefsFile = new File(tempDir, "prefs.txt");
        if (prefsFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(prefsFile));
                String line;
                while(!"generated:".equals(line = reader.readLine())){
                    
                }
                return reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public static void setRecentlyGenerated(Path path) {
        File tempDir = new File("temp");
        tempDir.mkdirs();
        File prefsFile = new File(tempDir, "prefs.txt");
        try {
            if (!prefsFile.exists()) {
                prefsFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(prefsFile));
                writer.write("generated:\n");
                writer.write(path.toString() + "\n");
                writer.write("opened:\n");
                
                writer.close();
                return;
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(prefsFile));
                LinkedList<String> prefsList = new LinkedList();
                String line;
                while ((line = reader.readLine()) != null) {
                    prefsList.add(line);
                }
                prefsFile.delete();
                prefsFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(prefsFile));
                for (int i = 0; i < prefsList.size(); i++) {
                    String oneLine = prefsList.get(i);
                    if (oneLine.equals("generated:")) {
                        writer.write("generated:\n");
                        writer.write(path.toString() + "\n");
                        i++;
                    } else {
                        writer.write(oneLine + "\n");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
