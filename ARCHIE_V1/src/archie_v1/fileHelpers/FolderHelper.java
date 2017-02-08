/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class FolderHelper extends FileHelper {
    
    public LinkedList<FileHelper> children;

    // public LinkedList<FileHelper> children;
    public FolderHelper(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
        children = new LinkedList();
    }

    public FolderHelper(Path filePath, boolean Islandora, boolean root) {
        super(filePath, Islandora, root);
        children = new LinkedList();
    }

    @Override
    public void setRecord(MetadataKey key, String value, boolean hardSet, boolean init) {
        super.metadataMap.put(key, value);
        if (init) {
            return;
        }
        for (FileHelper fh : children) {
            fh.setRecord(key, value, hardSet);
        }
    }

    @Override
    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray, boolean hardSet) {
        super.SetAddableRecord(Values, valueArray, hardSet);
        if(hardSet){
            for(FileHelper fh : children){
                fh.SetAddableRecord(Values, valueArray, hardSet);
            }
        }
    }
}
