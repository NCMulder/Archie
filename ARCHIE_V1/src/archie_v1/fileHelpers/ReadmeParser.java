/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import archie_v1.UI.SetPart;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ReadmeParser {

    String splitter = ",";
    private FileHelper fileHelper;

    public ReadmeParser(FileHelper fileHelper, Path path) {
        this.fileHelper = fileHelper;
        parseData(path);
    }

    public void parseData(Path path) {
        System.out.println("Parsing readme for folder " + path.getParent().toString());

        HashMap<MetadataKey, String> dict = new HashMap();
        //String[] ContributorInitials = null, ContributorNames = null;
        //Because the readme's use "Title" for multiple fields (item, creator and contributor),
        //these have to be separated using booleans to track where we are in the readme.
        boolean item = true, creator = false, contributor = false;
        Path readmePath = path;
        try {
            BufferedReader br = new BufferedReader(new FileReader(readmePath.toFile()));
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitter, 2);
                String key = keyValue[0].replace("o ", "").trim().toLowerCase();
                switch (key) {
                    case "item":
                        //Identifier in ReadMe is wrong, should not be there
                        String itemIdentifier = br.readLine().split(splitter, 2)[1].replaceAll(",$", "");
                        //setForFileHelper(MetadataKey.Identifier, itemIdentifier, true, true);

                        String title = br.readLine().split(splitter, 2)[1].replace("\"", "").replaceAll(",$", "");
                        //do something with title?
                        item = true;
                        creator = false;
                        contributor = false;
                        break;
                    case "author":
                        String authorTitle = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String authorInitials = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String authorName = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String authorIdentifier = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String authorAffiliation = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String[] authorValues = {authorIdentifier, authorTitle, authorInitials, "",authorName, authorAffiliation};
                        setForFileHelper(MetadataKey.creatorKeys, authorValues, false);
                        item = false;
                        creator = true;
                        contributor = false;
                        break;
                    case "contributor":
                        String contributorTitle = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String contributorInitials = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String contributorName = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String contributorIdentifier = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String contributorAffiliation = br.readLine().split(splitter, 2)[1].replaceAll(",$", "").replaceAll(",", ";");
                        String[] contributorValues = {contributorIdentifier, contributorTitle, contributorInitials, "",contributorName, contributorAffiliation};

                        setForFileHelper(MetadataKey.contributorKeys, contributorValues, false);
                        item = false;
                        creator = false;
                        contributor = true;
                        break;
//                    case "identifier":
//                        setForFileHelper(MetadataKey.Identifier, keyValue[1].replaceAll(",$", ""), true, true);
////                        putInDictionary(dict, MetadataKey.Identifier, keyValue[1].replaceAll(",$", ""));
//                        break;
//                    case "title":
//                        if (item) {
//                            //putInDictionary(dict, MetadataKey.Title, keyValue[1].replace("\"", "").replaceAll(",$", ""));
//                        } else if (creator) {
//                            setForFileHelper(MetadataKey.CreatorTOA, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        } else if (contributor) {
//                            setForFileHelper(MetadataKey.ContributorTOA, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        }
//                        break;
//                    case "initials":
//                        if (creator) {
//                            setForFileHelper(MetadataKey.CreatorGivenName, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        } else if (contributor) {
//                            setForFileHelper(MetadataKey.ContributorGivenName, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        }
//                        break;
//                    case "last name":
//                        if (creator) {
//                            setForFileHelper(MetadataKey.CreatorFamilyName, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        } else if (contributor) {
//                            setForFileHelper(MetadataKey.ContributorFamilyName, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        }
//                        break;
//                    case "dai":
//                        if (creator) {
//                            setForFileHelper(MetadataKey.CreatorIdentifier, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        } else if (contributor) {
//                            setForFileHelper(MetadataKey.ContributorIdentifier, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        }
//                        break;
//                    case "organization":
//                        if (creator) {
//                            setForFileHelper(MetadataKey.CreatorAffiliation, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        } else if (contributor) {
//                            setForFileHelper(MetadataKey.ContributorAffiliation, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
//                        }
//                        break;
                    case "date created":
                        setForFileHelper(MetadataKey.DateCreated, keyValue[1].replaceAll(",$", ""), false);
                        break;
                    case "rights holder":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.Rightsholder, keyValue[1].replaceAll(",$", ""), true, true);
                        break;
                    case "publisher":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.Publisher, keyValue[1].replaceAll(",$", ""), true, true);
                        break;
                    case "description":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.Description, keyValue[1].replaceAll(",$", ""), true, true);
                        break;
                    case "subject":
                        //Should not be in readme, possibly propagate up?
                        //setForFileHelper(MetadataKey.Subject, keyValue[1].replaceAll(",$", "").replaceAll(",", ";"), true, true);
                        break;
                    case "spatial coverage":
                        //setForFileHelper(MetadataKey.SpatialCoverage, keyValue[1].substring(0, keyValue[1].indexOf(",\"")).replace("\"", ""), false);
                        setForFileHelper(new MetadataKey[]{MetadataKey.SpatialCoverage}, new String[]{keyValue[1].substring(0, keyValue[1].indexOf(",\"")).replaceAll(",$", "").replaceAll(",", ";")}, false);
                        break;
                    case "temporal coverage":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.TemporalCoverage, keyValue[1].substring(0, keyValue[1].indexOf(",\"")), true, true);
                        break;
                    case "related datasets":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.RelatedDatasetName, keyValue[1].substring(0, keyValue[1].indexOf(",Names")), true, true);
                        //setForFileHelper(MetadataKey.RelatedDatasetLocation, "empty", true, true);
                        break;
                    case "type":
                        setForFileHelper(MetadataKey.FileContent, keyValue[1].substring(0, keyValue[1].indexOf(",always")), false);
                        break;
                    case "language":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.FileContentType, keyValue[1].substring(0, keyValue[1].indexOf(",\"default") + 1), true, true);
                        break;
                    case "rights":
                        //Should not be in readme
                        //setForFileHelper(MetadataKey.AccessLevel, keyValue[1].substring(0, keyValue[1].indexOf(",\"either") + 1), true, true);
                        break;
                    case "embarg":
                        //to be implemented
                        break;
                    default:
                        System.out.println("Unable to parse \"" + line + "\".");
                        //implement error message
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Readme for folder " + path.getParent().toString() + " not found.");
            //return null;
        }

        //do things with names
//        if(ContributorNames!=null && ContributorInitials!=null){
//            String initials = "";
//            String names = "";
//            for(int i = 0; i < ContributorInitials.length; i++){
//                String initial = ContributorInitials[i];
//                if (!"".equals(initial))
//                    initials+=initial + ";";
//            }
//            for(int i = 0; i < ContributorNames.length; i++){
//                String name = ContributorNames[i];
//                if (!"".equals(name))
//                    names+=name + ";";
//            }
//            //names = names.substring(0, names.length());
//            putInDictionary(dict, MetadataKey.ContributorGivenName, initials.replaceAll("; $", ""));
//            putInDictionary(dict, MetadataKey.ContributorFamilyName, initials.replaceAll("; $", ""));
//        }
        //return dict;
    }

    public void setForFileHelper(MetadataKey key, String value, boolean softSet) {
        assert !key.addable;
        
        String defValue = value;
        
        if (defValue == null || defValue.equals("")) {
            SetPart sp = new SetPart(new MetadataKey[]{key});
            Object[] buttons = {"Add", "Cancel"};
            int result = JOptionPane.showOptionDialog(null, sp, "Missing " + key + " in readme.", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            if (result == JOptionPane.OK_OPTION && !sp.getInfo().containsValue("") && !sp.getInfo().containsValue(null)) {
                for (Map.Entry<MetadataKey, String> metadata : sp.getInfo().entrySet()) {
                    if (metadata.getKey() == key) {
                        defValue = metadata.getValue();
                    }
                }
            }
            return;
        }

        assert defValue != null : !defValue.equals("");

        fileHelper.setRecord(key, defValue, softSet);
    }

    public void setForFileHelper(MetadataKey[] keys, String[] values, boolean softSet) {
        assert values.length == keys.length;
        
        MetadataKey[] defKeys = keys;
        String[] defValues = values;

        //Check wether or not all fields are empty
        boolean cont = false;
        for (int i = 0; !cont && i < defValues.length; i++) {
            if (defValues[i] != null && !defValues[i].equals("")) {
                cont = true;
            }
        }
        if (!cont) {
            return;
        }

        if (Arrays.asList(defValues).contains(null) || Arrays.asList(defValues).contains("")) {
            SetPart sp = new SetPart(keys, values);
            Object[] buttons = {"Add", "Cancel"};
            int result = JOptionPane.showOptionDialog(null, sp, "Missing parts in readme.", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
            if (result == JOptionPane.OK_OPTION && !sp.getInfo().containsValue(null)){// && !sp.getInfo().containsValue("")) {
                HashMap<MetadataKey, String> hm = sp.getInfo();
                defKeys = hm.keySet().toArray(new MetadataKey[hm.keySet().size()]);
                defValues = hm.values().toArray(new String[hm.values().size()]);
            } else {
                return;
            }
        }
        
        assert !Arrays.asList(defValues).contains(null);
        //assert !Arrays.asList(defValues).contains("");
        assert !Arrays.asList(defKeys).contains(null);

        fileHelper.AddAddableRecord(defKeys, defValues, softSet);
    }
}
