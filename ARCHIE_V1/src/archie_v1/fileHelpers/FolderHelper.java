/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.nio.file.Path;
import java.util.LinkedList;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author niels
 */
public class FolderHelper extends FileHelper {
    
   // public LinkedList<FileHelper> children;
    
    public FolderHelper(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
    }
    
    @Override
    public void setRecord(MetadataContainer.MetadataKey key, String value, boolean hardSet){
        if(!hardSet && (!"unknown".equals(metadataContainer.metadataMap.get(key))) && metadataContainer.metadataMap.containsKey(key)) {
            return;
        }
        metadataContainer.metadataMap.put(key, value);
        for(FileHelper fh : children){
            fh.setRecord(key, value, hardSet);
        }
    }
    
    @Override
    public void Initialize(){
        setRecord(MetadataContainer.MetadataKey.CreatorName, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.CreatorAffiliation, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.CreatorTOA, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.CreatorIdentifier, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.ContributorName, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.Rightsholder, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.Subject, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.Description, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.Publisher, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.Language, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.TemporalCoverage, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.SpatialCoverage, "unknown", false);
        setRecord(MetadataContainer.MetadataKey.AccesLevel, "unknown", false);
    }
}
