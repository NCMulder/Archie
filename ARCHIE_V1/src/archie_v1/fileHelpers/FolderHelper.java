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
        if(!hardSet && (!key.getDefaultValue().equals(metadataContainer.metadataMap.get(key))) && metadataContainer.metadataMap.containsKey(key)) {
            return;
        }
        metadataContainer.metadataMap.put(key, value);
        for(FileHelper fh : children){
            fh.setRecord(key, value, hardSet);
        }
    }
    
    @Override
    public void Initialize(){
        setRecord(MetadataContainer.MetadataKey.CreatorName, MetadataContainer.MetadataKey.CreatorName.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.CreatorAffiliation, MetadataContainer.MetadataKey.CreatorAffiliation.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.CreatorTOA, MetadataContainer.MetadataKey.CreatorTOA.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.CreatorIdentifier, MetadataContainer.MetadataKey.CreatorIdentifier.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.ContributorName, MetadataContainer.MetadataKey.ContributorName.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.Rightsholder, MetadataContainer.MetadataKey.Rightsholder.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.Subject, MetadataContainer.MetadataKey.Subject.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.Description, MetadataContainer.MetadataKey.Description.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.Publisher, MetadataContainer.MetadataKey.Publisher.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.Language, MetadataContainer.MetadataKey.Language.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.TemporalCoverage, MetadataContainer.MetadataKey.TemporalCoverage.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.SpatialCoverage, MetadataContainer.MetadataKey.SpatialCoverage.defaultValue, false);
        setRecord(MetadataContainer.MetadataKey.AccesLevel, MetadataContainer.MetadataKey.AccesLevel.defaultValue, false);
    }
}
