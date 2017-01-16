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
    Identifier("DOI1:0.1006/jmbi.1995.0238", true, false),
    CreatorName("Creator name", "J.M. de Vries"),
    CreatorAffiliation("Creator affiliation", "Universiteit Leiden"),
    CreatorTOA("Creator Terms of Adress", "Dr."),
    CreatorIdentifier("Creator identifier", "DAI_1"),
    ContributorName("Contributor name", "J.M. de Vries; A.F. Cornelis"),
    ContributorAffiliation("Contributor affiliation", "Universiteit Leiden; UCLA"),
    ContributorTOA("Contributor TOA", "None; Dr."),
    ContributorIdentifier("Contributor identifier", "DAI_1; DAI_2"),
    RelatedDatasetName("Related dataset name", "Dataset_01", true, false),
    RelatedDatasetLocation("Related dataset location", "doi", true, false),
    Subject("Short description", true, false),
    Description("Longer description", true, false),
    Rightsholder("Universiteit Leiden", true, false),
    Publisher("Universiteit Leiden", true, false),
    DateCreated("Date created", LocalDateTime.now().toLocalDate().toString()),
    /*TotalSize("Total size", "234221324 bytes", true, false),*/
    Language(false, true, false, "English", "French", "German", "Dutch", "Spanish", "Other"),
    TemporalCoverage("Temporal coverage", "-3000 - 1267", true, false),
    SpatialCoverage("Spatial coverage", "25°\"N 90°\"W; 10°\"N 90°\"W; 10°\"N 60°\"W; 25°\"N 60°\"W"),
    AccessLevel("Access level", false, true, false, "Open access", "Archaeology group", "Request permission", "Other access"),
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
    public static HashMap<MetadataKey, String> defaultValues;

    MetadataKey(String defaultValue) {
        setDefaultValue(defaultValue);
    }

    MetadataKey(String defaultValue, boolean dataset, boolean file) {
        setDefaultValue(defaultValue);
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
