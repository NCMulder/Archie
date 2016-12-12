/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.util.LinkedHashMap;

/**
 *
 * @author niels
 */
public class DatasetInitialInformation {
    
    public LinkedHashMap<MetadataContainer.MetadataKey, String> initInfo;
    
    public DatasetInitialInformation(String creatorName, String creatorAffiliation,  String creatorTOA, 
            String creatorIdentifier, String contributorName, String rightsholder, 
            String subject, String description, String publisher, String language,
            String temporalCoverage, String spatialCoverage, String accessLevel){
        initInfo = new LinkedHashMap();
        initInfo.put(MetadataContainer.MetadataKey.CreatorName, creatorName);
        initInfo.put(MetadataContainer.MetadataKey.CreatorAffiliation, creatorAffiliation);
        initInfo.put(MetadataContainer.MetadataKey.CreatorTOA, creatorTOA);
        initInfo.put(MetadataContainer.MetadataKey.CreatorIdentifier, creatorIdentifier);
        initInfo.put(MetadataContainer.MetadataKey.ContributorName, contributorName);
        initInfo.put(MetadataContainer.MetadataKey.Rightsholder, rightsholder);
        initInfo.put(MetadataContainer.MetadataKey.Subject, subject);
        initInfo.put(MetadataContainer.MetadataKey.Description, description);
        initInfo.put(MetadataContainer.MetadataKey.Publisher, publisher);
        initInfo.put(MetadataContainer.MetadataKey.Language, language);
        initInfo.put(MetadataContainer.MetadataKey.TemporalCoverage, temporalCoverage);
        initInfo.put(MetadataContainer.MetadataKey.SpatialCoverage, spatialCoverage);
        initInfo.put(MetadataContainer.MetadataKey.AccesLevel, accessLevel);
    }
    
}
