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
    
    public LinkedHashMap<MetadataContainer.MetadataKey, String> initInfo;
    public LinkedList<MetadataContainer.MetadataKey> initKeys;
    
    public DatasetInitialInformation(){
        initInfo = new LinkedHashMap();
        initKeys = new LinkedList();
        initKeys.add(MetadataContainer.MetadataKey.CreatorTOA);
        initKeys.add(MetadataContainer.MetadataKey.CreatorName);
        initKeys.add(MetadataContainer.MetadataKey.CreatorIdentifier);
        initKeys.add(MetadataContainer.MetadataKey.CreatorAffiliation);
        initKeys.add(MetadataContainer.MetadataKey.ContributorTOA);
        initKeys.add(MetadataContainer.MetadataKey.ContributorName);
        initKeys.add(MetadataContainer.MetadataKey.ContributorIdentifier);
        initKeys.add(MetadataContainer.MetadataKey.ContributorAffiliation);
        initKeys.add(MetadataContainer.MetadataKey.Rightsholder);
        initKeys.add(MetadataContainer.MetadataKey.Subject);
        initKeys.add(MetadataContainer.MetadataKey.Description);
        initKeys.add(MetadataContainer.MetadataKey.Publisher);
        initKeys.add(MetadataContainer.MetadataKey.Language);
        initKeys.add(MetadataContainer.MetadataKey.TemporalCoverage);
        initKeys.add(MetadataContainer.MetadataKey.SpatialCoverage);
        initKeys.add(MetadataContainer.MetadataKey.AccessLevel);
    }
    
}
