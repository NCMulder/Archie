//License header
package archie_v1.fileHelpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public abstract class FileHelper {

    public Path filePath;
    public Map<String, String> metadata;
    public LinkedList<FileHelper> children;
    public boolean root = false;
    public LinkedHashMap<MetadataKey, String> metadataMap;

    public FileHelper(Path filePath, boolean Islandora) {
        metadataMap = new LinkedHashMap();
        this.filePath = filePath;
        children = new LinkedList();

        Initialize();
    }
    
    public FileHelper(Path filePath, boolean Islandora, boolean root){
        metadataMap = new LinkedHashMap();
        this.root = root;
        this.filePath = filePath;
        children = new LinkedList();

        Initialize();
    }

    public void Initialize() {
        if (root) {
            for (int i = 0; i < MetadataKey.values().length; i++) {
                if (MetadataKey.values()[i].dataset) {
                    setRecord(MetadataKey.values()[i], null, false, true);
                }
            }
            return;
        }

        if (!(this instanceof FolderHelper)) {
            metadata = getMetaData();
            //setRecord(MetadataKey.Title, FilenameUtils.removeExtension(filePath.getFileName().toString()), false, true);
            setRecord(MetadataKey.FileContentType, "." + FilenameUtils.getExtension(filePath.toString()) + " file", false, true);
            setRecordThroughTika(MetadataKey.DateCreated, "date");
        }

        for (int i = 0; i < MetadataKey.values().length; i++) {
            if (MetadataKey.values()[i].file) {
                setRecord(MetadataKey.values()[i], null, false, true);
            }
        }
    }

    //Helper functions for all filehandlers.
    public Map<String, String> getMetaData() {
        HashMap<String, String> basedata = new HashMap();

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadataTemp = new Metadata();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
            return basedata;
        }
        ParseContext context = new ParseContext();

        try {
            parser.parse(inputstream, handler, metadataTemp, context);
        } catch (IOException | SAXException | TikaException ex) {
            System.out.println("Tika error debugger: " + filePath);
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, "test");
        }

        String[] names = metadataTemp.names();
        for (String metaType : names) {
            basedata.put(metaType, metadataTemp.get(metaType));
        }
        
        try{
            (new File("temp")).mkdirs();
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("temp/" + filePath.getFileName() + "_tika.txt")));
            
            for(Map.Entry<String, String> kvPair : basedata.entrySet()){
                out.println("[" + kvPair.getKey() + "] : " + kvPair.getValue());
            }
            
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return basedata;
    }

    public void setRecord(MetadataKey key, String value, boolean hardSet, boolean init) {
        if (!hardSet && metadataMap.containsKey(key) && (!"".equals(metadataMap.get(key)))) {
            return;
        }
        if (init || metadataMap.containsKey(key)) {
            if(key.addable)
                AddAddable(key, value);
            else
                metadataMap.put(key, value);
        }
    }
    
    /**
     * Only in use for addable fields, such as <code>CreatorName</code>s and <code>Subject</code>s.
     * @param key
     * @param value
     * @param hardSet
     * @param init
     */
    public void removeRecord(MetadataKey key, int index, boolean hardSet, boolean init){
        if (!hardSet && metadataMap.containsKey(key) && (!"".equals(metadataMap.get(key)))) {
            return;
        }
        if (init || metadataMap.containsKey(key)) {
            if(key.addable)
                RemoveAddable(key, index);
            else
                return;
        }
    }

    public void setRecord(MetadataKey key, String value, boolean hardSet) {
        setRecord(key, value, hardSet, false);
    }
    
    public void AddAddable(MetadataKey key, String value){
        String currentValue = metadataMap.get(key);
        if(currentValue != null && !currentValue.equals(""))
            currentValue+=";" + value;
        else
            currentValue = value;
        metadataMap.put(key, currentValue);
    }

    private void RemoveAddable(MetadataKey key, int index) {
        String[] keyValues = metadataMap.get(key).split(";");
        String newString = "";
        for (int j = 0; j < keyValues.length; j++) {
            newString += (j==index) ? "" : keyValues[j] + ";";
        }
        System.out.println("Before trimming:" + newString);
        newString = newString.replaceAll(";$", "");
        System.out.println("After trimming:" + newString);

        if (newString.equals("")) {
            newString = null;
        }
        metadataMap.put(key, newString);
    }

    public void setRecordThroughTika(MetadataKey key, String tikaString) {
        String tikaValue = metadata.get(tikaString);
        if (tikaValue != null) {
            setRecord(key, tikaValue, false, true);
            //metadataMap.put(key, tikaValue);
        }
    }
}
