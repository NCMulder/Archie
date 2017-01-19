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
    DatasetTitle("Dataset Title", "Dataset_01", true, false),
    /*Title("unknown", false, true),*/
    Identifier("Identifier","DOI1:0.1006/jmbi.1995.0238", true, false),
    CreatorName("Creator name"),
    CreatorAffiliation("Creator affiliation"),
    CreatorTOA("Creator Terms of Adress"),
    CreatorIdentifier("Creator identifier"),
    ContributorName("Contributor name"),
    ContributorAffiliation("Contributor affiliation"),
    ContributorTOA("Contributor TOA"),
    ContributorIdentifier("Contributor identifier"),
    RelatedDatasetName("Related dataset name", true, false),
    RelatedDatasetLocation("Related dataset location", true, false),
    Subject(true, false),
    Description("Description", "This dataset encompasses all pottery from Grenada, as well as several papers.", true, false),
    Rightsholder("Rightsholder", "Universiteit Leiden", true, false),
    Publisher("Publisher", "Universiteit Leiden", true, false),
    DateCreated("Date created", LocalDateTime.now().toLocalDate().toString()),
    /*TotalSize("Total size", "234221324 bytes", true, false),*/
    Language(false, true, false, "English", "French", "German", "Dutch", "Spanish", "Other"),
    TemporalCoverage("Temporal coverage", "-3000 - 1267", true, false),
    SpatialCoverage("Spatial coverage", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"),
    AccessLevel("Access level", false, true, false, "Open access", "Archaeology group", "Request permission", "Other access"),
    Collector("Collector", "J.M. de Vries; A.F. Cornelis", false, true),
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
    public static HashMap<MetadataKey, String> defaultValues;

    static public MetadataKey[] creatorKeys = {CreatorName, CreatorIdentifier, CreatorTOA, CreatorAffiliation};
    static public MetadataKey[] contributorKeys = {ContributorName, ContributorIdentifier, ContributorTOA, ContributorAffiliation};
    static public MetadataKey[] relatedDatasetKeys = {RelatedDatasetName, RelatedDatasetLocation};
    static public MetadataKey[] subjectKeys = {Subject};
    
    MetadataKey(String text) {
        this.keyText = text;
    }
    
    MetadataKey(boolean dataset, boolean file){
        this.dataset = dataset;
        this.file = file;
    }

    MetadataKey(String text, boolean dataset, boolean file) {
        this.keyText = text;
        this.dataset = dataset;
        this.file = file;
    }

    MetadataKey(String text, String defaultValue) {
        this.keyText = text;
        setDefaultValue(defaultValue);
    }

    MetadataKey(String text, String defaultValue, boolean dataset, boolean file) {
        this.keyText = text;
        setDefaultValue(defaultValue);
        this.dataset = dataset;
        this.file = file;
    }

    MetadataKey(boolean settable, String... setOptions) {
        this.settable = settable;
        setDefaultValue(defaultValue);
        this.setOptions = setOptions;
    }

    MetadataKey(boolean settable, boolean dataset, boolean file, String... setOptions) {
        this.settable = settable;
        setDefaultValue(defaultValue);
        this.setOptions = setOptions;
        this.dataset = dataset;
        this.file = file;
    }

    MetadataKey(String text, boolean settable, String... setOptions) {
        this.keyText = text;
        setDefaultValue(defaultValue);
        this.settable = settable;
        this.setOptions = setOptions;
    }

    MetadataKey(String text, boolean settable, boolean dataset, boolean file, String... setOptions) {
        this.keyText = text;
        setDefaultValue(defaultValue);
        this.settable = settable;
        this.setOptions = setOptions;
        this.dataset = dataset;
        this.file = file;
    }

    @Override
    public String toString() {
        return keyText;
    }

    public String[] getSetOptions() {
        return setOptions;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    private void setDefaultValue(String value) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        defaultValue = prefs.get(name(), value);
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
