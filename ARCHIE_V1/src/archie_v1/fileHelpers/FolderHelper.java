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

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class FolderHelper extends FileHelper {

    public LinkedList<FileHelper> children;

    public FolderHelper(Path filePath) {
        super(filePath);
        children = new LinkedList();
    }

    public FolderHelper(Path filePath, boolean root, boolean fromArchie) {
        super(filePath, root, fromArchie);
        children = new LinkedList();
    }

    public FolderHelper(BufferedReader br, Path path) {
        super(path);

        children = new LinkedList();
        try {
            String line;
            while (!(line = br.readLine()).equals("--")) {
                String[] keyValue = line.split(": ");
                if (keyValue.length > 1) {
                    MetadataKey key = MetadataKey.valueOf(line.split(": ")[0]);
                    String value = line.split(": ")[1];
                    setRecord(key, value, false, true);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FolderHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setRecord(MetadataKey key, String value, boolean hardSet, boolean init) {
        if (init) {
            metadataMap.put(key, value);
            return;
        }
        if (!hardSet && metadataMap.containsKey(key) && (!"".equals(metadataMap.get(key)))) {
            return;
        }
        if (metadataMap.containsKey(key)) {
            metadataMap.put(key, value);
            for (FileHelper fh : children) {
                fh.setRecord(key, value, hardSet);
            }
        }
    }

    @Override
    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray, boolean hardSet) {
        super.SetAddableRecord(Values, valueArray, hardSet);
        for (FileHelper fh : children) {
            fh.SetAddableRecord(Values, valueArray, hardSet);
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
