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
    DatasetTitle("Dataset Title", "Dataset_01", true, false, false),
    /*Title("unknown", false, true),*/
    Identifier("Identifier","DOI1:0.1006/jmbi.1995.0238", true, false, false),
    CreatorGivenName("Creator given name", "G.F.", true, true, true),
    CreatorFamilyName("Creator family name", "Coninck", true, true, true),
    CreatorAffiliation("Creator affiliation", "Universiteit Leiden", true, true, true),
    CreatorTOA("Creator Terms of Adress", "Prof. Dr.", true, true, true),
    CreatorIdentifier("Creator identifier", "12345678", true, true, true),
    ContributorGivenName("Contributor given name", "H.T.", true, true, true),
    ContributorFamilyName("Contributor family name", "de Vries", true, true, true),
    ContributorAffiliation("Contributor affiliation", "Cambridge University", true, true, true),
    ContributorTOA("Contributor TOA", "Drs.", true, true, true),
    ContributorIdentifier("Contributor identifier", "62935461", true, true, true),
    RelatedDatasetName("Related dataset name", "Philosophy of the Caribean in the Third Century", true, false, true),
    RelatedDatasetLocation("Related dataset location", "DOI: 12683128361", true, false, true),
    Subject("Subject", "Philosophy", true, false, true),
    Description("Description", "This dataset encompasses all pottery from Grenada, as well as several papers.", true, false, false),
    Rightsholder("Rightsholder", "Universiteit Leiden", true, false, false),
    Publisher("Publisher", "Universiteit Leiden", true, false, false),
    DateCreated("Date created", LocalDateTime.now().toLocalDate().toString()),
    /*TotalSize("Total size", "234221324 bytes", true, false),*/
    Language("Language", false, true, false, "English", "French", "German", "Dutch", "Spanish", "Other"),
    TemporalCoverage("Temporal coverage", "-3000 - 1267", true, false, false),
    SpatialCoverage("Spatial coverage", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"),
    AccessLevel("Access level", false, true, false, "Open access", "Archaeology group", "Request permission", "Other access"),
    Collector("Collector", "J.M. de Vries", false, true, false),
    FileDescription("File description", "This urn from the late 400s was probably used to wash cabbage.", false, true, false),
    FilePurpose("File purpose", "Research/saving purpose", false, true, false),
    FileCollection("File collection", "format to be decided", false, true, false),
    FileUnits("File units", "Metric", false, true, false),
    FileAppreciation("File appreciation", "Great", false, true, false),
    FileSource("File source", "Oral tradition", false, true, false),
    FileCitation("File citation", "format to be decided", false, true, false),
    FileNotes("File notes", "Research notes", false, true, false),
    FileCoordinates("File coordinates", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W", false, true, false),
    FileContentType("File content type", "Recording", false, true, false),
    FileForm("File form", "Mediocre", false, true, false),
    FileSize("File size", "234221324 bytes", false, true, false);

    String displayValue = this.name();
    String hintValue;
    String[] setOptions;
    public boolean unrestricted = true, dataset = true, file = true, addable = false;
    public static HashMap<MetadataKey, String> defaultValues;

    static public MetadataKey[] creatorKeys = {CreatorIdentifier,  CreatorTOA, CreatorGivenName, CreatorFamilyName,CreatorAffiliation};
    static public MetadataKey[] contributorKeys = {ContributorIdentifier, ContributorTOA, ContributorGivenName, ContributorFamilyName, ContributorAffiliation};
    static public MetadataKey[] relatedDatasetKeys = {RelatedDatasetName, RelatedDatasetLocation};
    static public MetadataKey[] subjectKeys = {Subject};
    
    MetadataKey(String displayValue, String hintValue){
        this.displayValue = displayValue;
        this.hintValue = hintValue;
    }
    
    MetadataKey(String displayValue, boolean unrestricted, String... settableValues){
        this.displayValue = displayValue;
        this.unrestricted = unrestricted;
        this.setOptions = settableValues;
    }
    
    MetadataKey(String displayValue, String hintValue, boolean dataset, boolean file, boolean addable){
        this.displayValue = displayValue;
        this.hintValue = hintValue;
        this.dataset = dataset;
        this.file = file;
        this.addable = addable;
    }
    
    MetadataKey(String displayValue, boolean unrestricted, boolean dataset, boolean file, String... settableValues){
        this.displayValue = displayValue;
        this.unrestricted = unrestricted;
        this.setOptions = settableValues;
        this.dataset = dataset;
        this.file = file;
    }

//    MetadataKey(String text, boolean dataset, boolean file) {
//        this.displayValue = text;
//        this.dataset = dataset;
//        this.file = file;
//    }
//
//    MetadataKey(String text, String defaultValue) {
//        this.displayValue = text;
//        setDefaultValue(defaultValue);
//    }
//
//    MetadataKey(String text, String defaultValue, boolean dataset, boolean file) {
//        this.displayValue = text;
//        setDefaultValue(defaultValue);
//        this.dataset = dataset;
//        this.file = file;
//    }
//
//    MetadataKey(boolean settable, String... setOptions) {
//        this.unrestricted = settable;
//        setDefaultValue(hintValue);
//        this.setOptions = setOptions;
//    }
//
//    MetadataKey(boolean settable, boolean dataset, boolean file, String... setOptions) {
//        this.unrestricted = settable;
//        setDefaultValue(hintValue);
//        this.setOptions = setOptions;
//        this.dataset = dataset;
//        this.file = file;
//    }
//
//    MetadataKey(String text, boolean settable, String... setOptions) {
//        this.displayValue = text;
//        setDefaultValue(hintValue);
//        this.unrestricted = settable;
//        this.setOptions = setOptions;
//    }
//
//    MetadataKey(String text, boolean settable, boolean dataset, boolean file, String... setOptions) {
//        this.displayValue = text;
//        setDefaultValue(hintValue);
//        this.unrestricted = settable;
//        this.setOptions = setOptions;
//        this.dataset = dataset;
//        this.file = file;
//    }

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
