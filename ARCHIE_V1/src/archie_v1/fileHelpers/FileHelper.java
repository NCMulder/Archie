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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
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
    public boolean root = false;
    public LinkedHashMap<MetadataKey, String> metadataMap;

    public FileHelper(Path filePath) {
        metadataMap = new LinkedHashMap();
        metadata = new HashMap();
        this.filePath = filePath;

        Initialize();
    }

    public FileHelper(Path filePath, boolean root) {
        metadataMap = new LinkedHashMap();
        metadata = new HashMap();
        this.root = root;
        this.filePath = filePath;

        Initialize();
    }

    public void Initialize() {
        for (int i = 0; i < MetadataKey.values().length; i++) {
            if (MetadataKey.values()[i].file) {
                setRecord(MetadataKey.values()[i], null, false, true);
            }
        }

        metadata = getMetaData();
        //Is this useful? TODO WIP WOUTER
        //setRecord(MetadataKey.FileContentType, "." + FilenameUtils.getExtension(filePath.toString()) + " file", false);
        setRecordThroughTika(MetadataKey.DateCreated, "date");
        setRecordThroughTika(MetadataKey.Software, "Creator tool");
        setRecordThroughTika(MetadataKey.Software, "Application-Name");
        setRecordThroughTika(MetadataKey.FileContentType, "Content-Type");
        File file = new File(filePath.toString());
        
        //Rounding is not nice; possible better solution: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
        setRecord(MetadataKey.FileSize, FileUtils.byteCountToDisplaySize(file.length()), false);
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

        try {
            (new File("temp")).mkdirs();
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("temp/" + filePath.getFileName() + "_tika.txt")));

            for (Map.Entry<String, String> kvPair : basedata.entrySet()) {
                out.println("[" + kvPair.getKey() + "] : " + kvPair.getValue());
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return basedata;
    }
    
    protected void setRecord(MetadataKey key, String value, boolean softset, boolean init) {
        if(value == "")
            System.out.println("Value should never be \"\"");
        
        if (init) {
            metadataMap.put(key, value);
            return;
        }

        if (!metadataMap.containsKey(key)) {
            return;
        }

        if (softset && metadataMap.get(key) != null) {
            return;
        }
        
        //System.out.println("Changing key " + key.displayValue + " to " + value + " for file " + filePath.getFileName() + ", softset:" + softset);

        metadataMap.put(key, value);
    }

    public void setRecord(MetadataKey key, String value, boolean softset) {
        setRecord(key, value, softset, false);
    }

    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray, boolean softSet) {
        if (softSet && metadataMap.get(Values[0]) != null) {
            return;
        }

        //Empty array should result in an empty array
        if (valueArray.size() == 0) {
            for (MetadataKey key : Values) {
                metadataMap.put(key, null);
            }
        } else {
            //Non-empty array should be copied verbatim
            for (int i = 0; i < Values.length; i++) {
                String value = "";
                for (String[] values : valueArray) {
                    value += values[i] + ";";
                }
                metadataMap.put(Values[i], value);
            }
        }
    }

    public void AddAddableRecord(MetadataKey[] Keys, String[] Values, boolean softSet) {
        if (softSet && metadataMap.get(Keys[0]) != null) {
            return;
        }

        for (int i = 0; i < Keys.length; i++) {
            String currentValue = metadataMap.get(Keys[i]);
            if (currentValue == null) {
                currentValue = "";
            }
            if (!currentValue.equals("")) {
                currentValue += ";";
            }
            currentValue += Values[i];
            metadataMap.put(Keys[i], currentValue);
        }
    }

    public void setRecordThroughTika(MetadataKey key, String tikaString) {
        assert !key.addable;
        String tikaValue = metadata.get(tikaString);
        if (tikaValue != null) {
            setRecord(key, tikaValue, false);
            //metadataMap.put(key, tikaValue);
        }
    }

    public void saveDataset(BufferedWriter writer, String prefix) {
        try {
            writer.write(prefix + ((root)? filePath : filePath.getFileName()) + "\n");
            if (this.getClass() == FolderHelper.class) {
                writer.write(prefix + ((FolderHelper) this).children.size() + "\n");
            } else {
                writer.write(prefix + "-1\n");
            }
            for (Map.Entry<MetadataKey, String> entry : metadataMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                writer.write(prefix + entry.getKey().name() + ": " + entry.getValue() + "\n");
            }
            writer.write("--\n");
            if (!(this instanceof FolderHelper)) {
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    if (entry.getValue() == null) {
                        continue;
                    }
                    writer.write(prefix + entry.getKey() + ": " + entry.getValue() + "\n");
                }
            }
            writer.write("--\n");
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
