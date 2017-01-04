//License
package archie_v1.fileHelpers;

import java.util.LinkedHashMap;

public class MetadataContainer {
    
    public LinkedHashMap<MetadataKey, String> metadataMap;
    public Boolean includeIslandora, includeDANS;
    public static String toas[] = {"None","Dr.","Drs.","Prof.","MSc","BSc"};
    public static String langs[] = {"English","French","German","Dutch","Spanish","Other"};
    public static String access[] = {"Open access","Archaeology group","Request permission","Other access"};
    
    public enum MetadataKey{Title("unknown", false, true), 
                            Identifier("DOI1:0.1006/jmbi.1995.0238", true, false), 
                            CreatorName("Creator name", "J.M. de Vries"), 
                            CreatorAffiliation("Creator affiliation", "Universiteit Leiden"), 
                            CreatorTOA("Creator TOA", false, toas),
                            CreatorIdentifier("Creator identifier", "DAI_1"), 
                            ContributorName("Contributor name", "J.M. de Vries; A.F. Cornelis"),
                            ContributorAffiliation("Contributor affiliation", "Universiteit Leiden; UCLA"),
                            ContributorTOA("Contributor TOA", "None; Dr."),
                            ContributorIdentifier("Contributor identifier","DAI_1; DAI_2"),
                            Rightsholder("Universiteit Leiden", true, false), 
                            RelatedDatasetName("Related dataset name", "Dataset_01", true, false),
                            RelatedDatasetLocation("Related dataset location", "doi", true, false),
                            Subject("Short description", true, false), 
                            Description("Longer description", true, false), 
                            Publisher("Universiteit Leiden", true, false), 
                            DateCreated("Date created", "2016-12-13"), 
                            TotalSize("Total size", "234221324 bytes", true, false), 
                            Language(false, langs, true, false), 
                            TemporalCoverage("Temporal coverage", "-3000 - 1267", true, false), 
                            SpatialCoverage("Spatial coverage", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"), 
                            AccessLevel("Access level", false, access, true, false), 
                            Collector("J.M. de Vries; A.F. Cornelis", false, true), 
                            FileDescription("File description", "Longer description", false, true), 
                            FilePurpose("File purpose", "Research/saving purpose", false, true), 
                            FileCollection("File collection", "format to be decided", false, true), 
                            FileUnits("File units", "Imperial", false, true), 
                            FileAppreciation("File appreciation", "Great", false, true), 
                            FileSource("File source", "Oral tradition", false, true), 
                            FileCitation("File citation", "format to be decided", false, true), 
                            FileNotes("File notes", "Research notes", false, true), 
                            FileCoordinates("File coordinates", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W", false, true), 
                            FileContentType("File content type", "Recording", false, true), 
                            FileForm("File form", "Mediocre", false, true), 
                            FileSize("File size", "234221324 bytes", false, true);
    
    String keyText = this.name();
    String defaultValue;
    String[] setOptions;
    public boolean settable = true, dataset = true, file = true;
    
    MetadataKey(String defaultValue){
        this.defaultValue = defaultValue;
    }
    
    MetadataKey(String defaultValue, boolean dataset, boolean file){
        this.defaultValue = defaultValue;
        this.dataset = dataset;
        this.file = file;
    }
    
    MetadataKey(String text, String defaultValue){
        this.keyText = text;
        this.defaultValue = defaultValue;
    }
    
    MetadataKey(String text, String defaultValue, boolean dataset, boolean file){
        this.keyText = text;
        this.defaultValue = defaultValue;
        this.dataset = dataset;
        this.file = file;
    }
    
    MetadataKey(boolean settable, String[] setOptions){
        this.settable = settable;
        this.defaultValue = setOptions[0];
        this.setOptions = setOptions;
    }
    
    MetadataKey(boolean settable, String[] setOptions, boolean dataset, boolean file){
        this.settable = settable;
        this.defaultValue = setOptions[0];
        this.setOptions = setOptions;
        this.dataset = dataset;
        this.file = file;
    }
    
    MetadataKey(String text, boolean settable, String[] setOptions){
        this.keyText = text;
        this.defaultValue = setOptions[0];
        this.settable = settable;
        this.setOptions = setOptions;
    }
    
    MetadataKey(String text, boolean settable, String[] setOptions, boolean dataset, boolean file){
        this.keyText = text;
        this.defaultValue = setOptions[0];
        this.settable = settable;
        this.setOptions = setOptions;
        this.dataset = dataset;
        this.file = file;
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
