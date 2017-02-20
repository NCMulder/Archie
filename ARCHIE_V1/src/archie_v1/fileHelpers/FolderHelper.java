/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class FolderHelper extends FileHelper {

    public LinkedList<FileHelper> children;

    public FolderHelper(Path filePath) {
        super(filePath);
    }

    public FolderHelper(Path filePath, boolean root) {
        super(filePath, root);
    }

    public FolderHelper(BufferedReader br, Path path) {
        super(path, true);
        try {
            String line;
            while (!(line = br.readLine()).equals("--")) {
                String[] keyValue = line.split(": ");
                if (keyValue.length > 1) {
                    MetadataKey key = MetadataKey.valueOf(line.split(": ")[0]);
                    String value = line.split(": ")[1];
                    setRecord(key, value, true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FolderHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void Initialize() {
        children = new LinkedList();
        
        if (root) {
            for (int i = 0; i < MetadataKey.values().length; i++) {
                if (MetadataKey.values()[i].dataset) {
                    setRecord(MetadataKey.values()[i], null, false, true);
                }
            }
            return;
        }

        for (int i = 0; i < MetadataKey.values().length; i++) {
            if (MetadataKey.values()[i].file) {
                setRecord(MetadataKey.values()[i], null, false, true);
            }
        }
    }

    @Override
    protected void setRecord(MetadataKey key, String value, boolean softSet, boolean init) {
        super.setRecord(key, value, softSet, init);
        
        for (FileHelper fh : children) {
            fh.setRecord(key, value, softSet, init);
        }
    }
    
    @Override
    public void setRecord(MetadataKey key, String value, boolean softSet){
        setRecord(key, value, softSet, false);
    }

    @Override
    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray, boolean softSet) {
        super.SetAddableRecord(Values, valueArray, softSet);
        for (FileHelper fh : children) {
            fh.SetAddableRecord(Values, valueArray, softSet);
        }
    }

    @Override
    public void saveDataset(BufferedWriter writer, String prefix) {
        super.saveDataset(writer, prefix);
        for (FileHelper fh : children) {
            fh.saveDataset(writer, prefix + ">>");
        }
    }
}
