/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class ReadmeParser {

    String splitter = ",";

    public ReadmeParser() {

    }

    public HashMap<MetadataContainer.MetadataKey, String> getData(Path path) {
        System.out.println("Parsing readme for folder " + path.getParent().toString());
        
        HashMap<MetadataContainer.MetadataKey, String> dict = new HashMap();
        String[] ContributorInitials = null, ContributorNames = null;
        //Because the readme's use "Title" for multiple fields (item, creator and contributor),
        //these have to be separated using booleans to track where we are in the readme.
        boolean item = true, creator = false, contributor = false;
        Path readmePath = path;
        try {
            BufferedReader br = new BufferedReader(new FileReader(readmePath.toFile()));
            String line = "";
            while ((line = br.readLine()) != null) {
                //String[] keyValue = line.split(splitter);
                String[] keyValue = line.split(splitter, 2);
                String key = keyValue[0].replace("o ", "").trim().toLowerCase();
                switch (key) {
                    case "item":
                        item = true;
                        creator = false;
                        contributor = false;
                        break;
                    case "author":
                        item = false;
                        creator = true;
                        contributor = false;
                        break;
                    case "contributor":
                        item = false;
                        creator = false;
                        contributor = true;
                        break;
                    case "identifier":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Identifier, keyValue[1].replaceAll(",$", ""));
                        break;
                    case "title":
                        if (item) {
                            //putInDictionary(dict, MetadataContainer.MetadataKey.Title, keyValue[1].replace("\"", "").replaceAll(",$", ""));
                        } else if (creator) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.CreatorTOA, keyValue[1].replaceAll(",$", ""));
                        } else if (contributor) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.ContributorTOA, keyValue[1].replaceAll(",$", "").replace(",", "; "));
                        }
                        break;
                    case "initials":
                        if (creator) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.CreatorName, keyValue[1].replaceAll(",$", ""));
                        } else if (contributor) {
                            ContributorInitials = keyValue[1].replaceAll(",$", "").split(",");
                        }
                        break;
                    case "last name":
                        if (creator) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.CreatorName, dict.get(MetadataContainer.MetadataKey.CreatorName) + " " + keyValue[1].replaceAll(",$", ""));
                        } else if (contributor) {
                            ContributorNames = keyValue[1].replaceAll(",$", "").split(",");
                        }
                        break;
                    case "dai":
                        if (creator) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.CreatorIdentifier, keyValue[1].replaceAll(",$", ""));
                        } else if (contributor) {
                            putInDictionary(dict, MetadataContainer.MetadataKey.ContributorIdentifier, keyValue[1].replaceAll(",$", "").replace(",", "; "));
                        }
                        break;
                    case "organization":
                        if(creator){
                            putInDictionary(dict, MetadataContainer.MetadataKey.CreatorAffiliation, keyValue[1].replaceAll(",$", ""));
                        } else if (contributor){
                            putInDictionary(dict, MetadataContainer.MetadataKey.ContributorAffiliation, keyValue[1].replaceAll(",$", "").replace(",", "; "));
                        }
                        break;
                    case "date created":
                        putInDictionary(dict, MetadataContainer.MetadataKey.DateCreated, keyValue[1].replaceAll(",$", "")); 
                        break;
                    case "rights holder":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Rightsholder, keyValue[1].replaceAll(",$", ""));
                        break;
                    case "publisher":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Publisher, keyValue[1].replaceAll(",$", ""));
                        break;
                    case "description":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Description, keyValue[1].replaceAll(",$", ""));
                        break;
                    case "subject":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Subject, keyValue[1].replaceAll(",$", ""));
                        break;
                    case "spatial coverage":
                        putInDictionary(dict, MetadataContainer.MetadataKey.SpatialCoverage, keyValue[1].substring(0, keyValue[1].indexOf(",\"")).replace("\"", ""));
                        break;
                    case "temporal coverage":
                        putInDictionary(dict, MetadataContainer.MetadataKey.TemporalCoverage, keyValue[1].substring(0, keyValue[1].indexOf(",\"")));
                        break;
                    case "related datasets":
                        putInDictionary(dict, MetadataContainer.MetadataKey.RelatedDatasetName, keyValue[1].substring(0, keyValue[1].indexOf(",Names")));
                        break;
                    case "type":
                        putInDictionary(dict, MetadataContainer.MetadataKey.FileContentType, keyValue[1].substring(0, keyValue[1].indexOf(",always")));
                        break;
                    case "language":
                        putInDictionary(dict, MetadataContainer.MetadataKey.Language, keyValue[1].substring(0, keyValue[1].indexOf(",\"default") + 1));
                        break;
                    case "rights":
                        putInDictionary(dict, MetadataContainer.MetadataKey.AccessLevel, keyValue[1].substring(0, keyValue[1].indexOf(",\"either") + 1));
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
            return null;
        }

        //do things with names
        if(ContributorNames!=null && ContributorInitials!=null){
            String names = "";
            for(int i = 0; i < ContributorInitials.length; i++){
                String initial = ContributorInitials[i];
                if ("".equals(initial))
                    continue;
                names+=initial + " " + ContributorNames[i] + "; ";
            }
            //names = names.substring(0, names.length());
            putInDictionary(dict, MetadataContainer.MetadataKey.ContributorName, names.replaceAll("; $", ""));
        }
        return dict;
    }

    public void putInDictionary(Map<MetadataContainer.MetadataKey, String> dict, MetadataContainer.MetadataKey key, String value) {
        if (value == null || "".equals(value)) {
            return;
        }
        dict.put(key, value);
    }
}
