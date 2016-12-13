//License
package archie_v1.fileHelpers;

import java.util.LinkedHashMap;


public class MetadataContainer {
    
    public LinkedHashMap<MetadataKey, String> metadataMap;
    public Boolean includeIslandora, includeDANS;
    public static String toas[] = {"None","Dr.","Drs.","Prof.","MSc","BSc"};
    public static String langs[] = {"English","French","German","Dutch","Spanish"};
    public static String access[] = {"Open access","Archaeology group","Request permission","Other access"};
    
    public enum MetadataKey{Title, Identifier, CreatorName("Creator name"), CreatorAffiliation("Creator affiliation"), CreatorTOA("Creator TOA", false, toas),
                            CreatorIdentifier("Creator identifier"), ContributorName("Contributor name"), Rightsholder, 
                            RelatedItem("Related item"), Subject, Description, Publisher, DateCreated("Date created"), TotalSize("Total size"), 
                            Language(false, langs), TemporalCoverage("Temporal coverage"), SpatialCoverage("Spatial coverage"), 
                            AccesLevel("Access level", false, access), Collector, FileDescription("File description"), 
                            FilePurpose("File purpose"), FileCollection("File collection"), FileUnits("File units"), 
                            FileAppreciation("File appreciation"), FileSource("File source"), FileCitation("File citation"), 
                            FileNotes("File notes"), FileCoordinates("File coordinates"), FileContentType("File content type"), 
                            FileForm("File form"), FileSize("File size");
    
    String text = this.name();
    String[] setOptions;
    public boolean settable = true;
    
    MetadataKey(){
        
    }
    
    MetadataKey(String text){
        this.text = text;
    }
    
    MetadataKey(boolean settable, String[] setOptions){
        this.settable = settable;
        this.setOptions = setOptions;
    }
    
    MetadataKey(String text, boolean settable, String[] setOptions){
        this.text = text;
        this.settable = settable;
        this.setOptions = setOptions;
    }
    
    @Override
    public String toString(){
        return text;
    }
    
    public String[] getSetOptions(){
        return setOptions;
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
