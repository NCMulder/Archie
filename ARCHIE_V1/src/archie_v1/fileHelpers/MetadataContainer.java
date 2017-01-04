//License
package archie_v1.fileHelpers;

import java.util.LinkedHashMap;

public class MetadataContainer {
    
    public LinkedHashMap<MetadataKey, String> metadataMap;
    public Boolean includeIslandora, includeDANS;
    public static String toas[] = {"None","Dr.","Drs.","Prof.","MSc","BSc"};
    public static String langs[] = {"English","French","German","Dutch","Spanish","Other"};
    public static String access[] = {"Open access","Archaeology group","Request permission","Other access"};
    
    public enum MetadataKey{Title("unknown"), 
                            Identifier("DOI1:0.1006/jmbi.1995.0238"), 
                            CreatorName("Creator name", "J.M. de Vries"), 
                            CreatorAffiliation("Creator affiliation", "Universiteit Leiden"), 
                            CreatorTOA("Creator TOA", false, toas),
                            CreatorIdentifier("Creator identifier", "DAI_1"), 
                            ContributorName("Contributor name", "J.M. de Vries; A.F. Cornelis"),
                            ContributorAffiliation("Contributor affiliation", "Universiteit Leiden; UCLA"),
                            ContributorTOA("Contributor TOA", "None; Dr."),
                            ContributorIdentifier("Contributor identifier","DAI_1; DAI_2"),
                            Rightsholder("Universiteit Leiden"), 
                            RelatedDatasetName("Related dataset name", "Dataset_01"),
                            RelatedDatasetLocation("Related dataset location", "doi"),
                            Subject("Short description"), 
                            Description("Longer description"), 
                            Publisher("Universiteit Leiden"), 
                            DateCreated("Date created", "2016-12-13"), 
                            TotalSize("Total size", "234221324 bytes"), 
                            Language(false, langs), 
                            TemporalCoverage("Temporal coverage", "3000 BC - 1267 AD"), 
                            SpatialCoverage("Spatial coverage", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"), 
                            AccessLevel("Access level", false, access), 
                            Collector("J.M. de Vries; A.F. Cornelis"), 
                            FileDescription("File description", "Longer description"), 
                            FilePurpose("File purpose", "Research/saving purpose"), 
                            FileCollection("File collection", "format to be decided"), 
                            FileUnits("File units", "Imperial"), 
                            FileAppreciation("File appreciation", "Great"), 
                            FileSource("File source", "Oral tradition"), 
                            FileCitation("File citation", "format to be decided"), 
                            FileNotes("File notes", "Research notes"), 
                            FileCoordinates("File coordinates", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"), 
                            FileContentType("File content type", "Recording"), 
                            FileForm("File form", "Mediocre"), 
                            FileSize("File size", "234221324 bytes");
    
    String keyText = this.name();
    String defaultValue;
    String[] setOptions;
    public boolean settable = true;
    
    MetadataKey(String defaultValue){
        this.defaultValue = defaultValue;
    }
    
    MetadataKey(String text, String defaultValue){
        this.keyText = text;
        this.defaultValue = defaultValue;
    }
    
    MetadataKey(boolean settable, String[] setOptions){
        this.settable = settable;
        this.defaultValue = setOptions[0];
        this.setOptions = setOptions;
    }
    
    MetadataKey(String text, boolean settable, String[] setOptions){
        this.keyText = text;
        this.defaultValue = setOptions[0];
        this.settable = settable;
        this.setOptions = setOptions;
    }
    
    @Override
    public String toString(){
        return keyText;
    }
    
    public String[] getSetOptions(){
        return setOptions;
    }
    
    public String getDefaultValue(){
        return defaultValue;
    }
                            
    public static String[] names(){
        MetadataKey[] keys = values();
        String[] names = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            names[i] = keys[i].name();
        }
        return names;
    }
    }
    
    public MetadataContainer(boolean includeIslandora){
        this.includeIslandora = includeIslandora;
        metadataMap = new LinkedHashMap();
    }
    
    public MetadataContainer(boolean includeIslandora, boolean includeDANS){
        this.includeIslandora = includeIslandora;
        this.includeDANS = includeDANS;
        metadataMap = new LinkedHashMap();
    }
}
