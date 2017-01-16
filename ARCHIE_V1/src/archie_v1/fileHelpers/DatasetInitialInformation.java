/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author niels
 */
public class DatasetInitialInformation {
    
    public LinkedHashMap<MetadataKey, String> initInfo;
    public LinkedList<MetadataKey> initKeys;
    
    public DatasetInitialInformation(){
        initInfo = new LinkedHashMap();
        initKeys = new LinkedList();
        
        initKeys.add(MetadataKey.CreatorTOA);
        initKeys.add(MetadataKey.CreatorName);
        initKeys.add(MetadataKey.CreatorIdentifier);
        initKeys.add(MetadataKey.CreatorAffiliation);
        initKeys.add(MetadataKey.ContributorTOA);
        initKeys.add(MetadataKey.ContributorName);
        initKeys.add(MetadataKey.ContributorIdentifier);
        initKeys.add(MetadataKey.ContributorAffiliation);
        initKeys.add(MetadataKey.Rightsholder);
        initKeys.add(MetadataKey.Subject);
        initKeys.add(MetadataKey.Description);
        initKeys.add(MetadataKey.Publisher);
        initKeys.add(MetadataKey.Language);
        initKeys.add(MetadataKey.TemporalCoverage);
        initKeys.add(MetadataKey.SpatialCoverage);
        initKeys.add(MetadataKey.AccessLevel);
    }
    
}
