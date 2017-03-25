/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.prefs.Preferences;


public enum MetadataKey {
    DatasetTitle("Dataset Title", "Dataset_01", KeyCategory.Main, true, false, false),
    Identifier("Identifier","DOI1:0.1006/jmbi.1995.0238", KeyCategory.Main, true, false, false),
    CreatorGivenName("Creator given name", "G.F.", KeyCategory.Main, true, true, true),
    CreatorFamilyName("Creator family name", "Coninck", KeyCategory.Main, true, true, true),
    CreatorAffiliation("Creator affiliation", "Universiteit Leiden", KeyCategory.Main, true, true, true),
    CreatorTOA("Creator Terms of Adress", "Prof. Dr.", KeyCategory.Main, true, true, true),
    CreatorIdentifier("Creator identifier", "12345678", KeyCategory.Main, true, true, true),
    ContributorGivenName("Contributor given name", "H.T.", KeyCategory.Main, true, true, true),
    ContributorFamilyName("Contributor family name", "de Vries", KeyCategory.Main, true, true, true),
    ContributorAffiliation("Contributor affiliation", "Cambridge University", KeyCategory.Main, true, true, true),
    ContributorTOA("Contributor TOA", "Drs.", KeyCategory.Main, true, true, true),
    ContributorIdentifier("Contributor identifier", "62935461", KeyCategory.Main, true, true, true),
    RelatedDatasetName("Related dataset name", "Philosophy of the Caribean in the Third Century", KeyCategory.Main, true, false, true),
    RelatedDatasetLocation("Related dataset location", "DOI: 12683128361", KeyCategory.Main, true, false, true),
    RelatedCodeBookLocation("Codebook location", "C://", KeyCategory.Main, false, true, false),
    Subject("Subject", "Philosophy", KeyCategory.Main, true, false, true),
    Description("Description *", "Front side of a Saladoid jar", KeyCategory.Main, true, true, false),
    Rightsholder("Rightsholder", "Universiteit Leiden", KeyCategory.Main, true, false, false),
    Publisher("Publisher", "Universiteit Leiden", KeyCategory.Main, true, false, false),
    DateCreated("Date created", "2016-03-16", KeyCategory.Main, true, true, false),
    Embargo("Embargo", "Embargo date", KeyCategory.Main, true, false, false),
    Language("Language", KeyCategory.Main, true, false, "English", "French", "German", "Dutch", "Spanish", "Other"),
    TemporalCoverage("Temporal coverage", "-3000 - 1267", KeyCategory.Main, true, false, false),
    SpatialCoverage("Spatial coverage *", "Dominican Republic", KeyCategory.Geographical, true, true, false),
    Coordinates("Coordinates", "Preferably use decimal degrees, f.i. -74.967637, 14.540111", KeyCategory.Geographical, true, true, false),
    AccessLevel("Access level", KeyCategory.Main, true, false, "Open access", "On Request", "Research group", "Restricted Subgroup", "Privacy Sensitive", "Dark Archive", "Data Vault"),
    Software("Software *", "Software used to create file", KeyCategory.Technical, false, true, false),
    FilePurpose("Purpose", "Purpose of data collection", KeyCategory.Detailed, false, true, false),
    FileCollection("Collection", "Measurements in a grid of 5x5 metres", KeyCategory.Detailed, false, true, false),
    FileUnits("Analytic unit", "Metres, UTM", KeyCategory.Detailed, false, true, false),
    FileGeopgraphicUnit("Geographic unit", "Decimal degrees, UTM", KeyCategory.Geographical, false, true, false),
    FileAppreciation("Appreciation", "Island of Grenada was not included in this survey", KeyCategory.Detailed, false, true, false),
    FileSource("Source", "Interviews conducted in March 2015", KeyCategory.Detailed, false, true, false),
    FileCitation("Citation", "See also survey of the Montecristi area", KeyCategory.Detailed, false, true, false),
    FileContentType("Content type", "Recording", KeyCategory.Main, false, true, false),
    FileFormat("Format", "Mediocre", KeyCategory.Technical, false, true, false),
    FileSize("File size", "234221324 bytes", KeyCategory.Technical, false, true, false),
    FileNotes("Notes", "Research notes", KeyCategory.Detailed, false, true, false);

    public enum KeyCategory{Main, Geographical, Technical, Detailed;}
    
    String displayValue = this.name();
    String hintValue;
    String[] setOptions;
    public boolean unrestricted = true, dataset = true, file = true, addable = false;
    public static HashMap<MetadataKey, String> defaultValues;
    public KeyCategory keyCategory;

    static public MetadataKey[] creatorKeys = {CreatorIdentifier,  CreatorTOA, CreatorGivenName, CreatorFamilyName,CreatorAffiliation};
    static public MetadataKey[] contributorKeys = {ContributorIdentifier, ContributorTOA, ContributorGivenName, ContributorFamilyName, ContributorAffiliation};
    static public MetadataKey[] relatedDatasetKeys = {RelatedDatasetName, RelatedDatasetLocation};
    static public MetadataKey[] subjectKeys = {Subject};
    
    MetadataKey(String displayValue, String hintValue, KeyCategory keyCategory, boolean dataset, boolean file, boolean addable){
        this.displayValue = displayValue;
        this.hintValue = hintValue;
        this.dataset = dataset;
        this.file = file;
        this.addable = addable;
        this.keyCategory = keyCategory;
    }
    
    MetadataKey(String displayValue, KeyCategory keyCategory, boolean dataset, boolean file, String... settableValues){
        this.displayValue = displayValue;
        this.unrestricted = false;
        this.setOptions = settableValues;
        this.dataset = dataset;
        this.file = file;
        this.keyCategory = keyCategory;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    public String[] getSetOptions() {
        return setOptions;
    }

    public String getDefaultValue() {
        return hintValue;
    }

    private void setDefaultValue(String value) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        hintValue = prefs.get(name(), value);
    }

    public static String[] names() {
        MetadataKey[] keys = values();
        String[] names = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            names[i] = keys[i].name();
        }
        return names;
    }
}
