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
    private boolean fromArchie = false;

    public FileHelper(Path filePath) {
        metadataMap = new LinkedHashMap();
        metadata = new HashMap();
        this.filePath = filePath;

        Initialize();
    }

    public FileHelper(Path filePath, boolean root, boolean fromArchie) {
        metadataMap = new LinkedHashMap();
        metadata = new HashMap();
        this.root = root;
        this.filePath = filePath;
        this.fromArchie = fromArchie;

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

        for (int i = 0; i < MetadataKey.values().length; i++) {
            if (MetadataKey.values()[i].file) {
                setRecord(MetadataKey.values()[i], null, true, true);
            }
        }

        if (!(this instanceof FolderHelper) && !fromArchie) {
            metadata = getMetaData();
            //Is this useful? TODO WIP WOUTER
            setRecord(MetadataKey.FileContentType, "." + FilenameUtils.getExtension(filePath.toString()) + " file", false, true);
            setRecordThroughTika(MetadataKey.DateCreated, "date");
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

    public void setRecord(MetadataKey key, String value, boolean hardSet, boolean init) {
        if (!hardSet && metadataMap.containsKey(key) && (!"".equals(metadataMap.get(key)))) {
            return;
        }
        if (init || metadataMap.containsKey(key)) {
            metadataMap.put(key, value);
        }
    }

    public void setRecord(MetadataKey key, String value, boolean hardSet) {
        setRecord(key, value, hardSet, false);
    }

    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray, boolean hardSet) {
        if(!hardSet && metadataMap.get(Values[0]) != null && (!metadataMap.get(Values[0]).equals("")))
            return;
        
        //Empty array should result in an empty array
        if (valueArray.size() == 0) {
            for (MetadataKey key : Values) {
                metadataMap.put(key, null);
            }
        } else {
            //Non-empty array should be copied verbatim
            for (String[] values : valueArray) {
                for (int i = 0; i < Values.length; i++) {
                    String currentValue = metadataMap.get(Values[i]);
                    if (currentValue == null) {
                        currentValue = "";
                    }
                    String[] currentValues = currentValue.split(";");
                    if (i == 0 && Arrays.asList(currentValues).contains(values[i])) {
                        break;
                    }

                    if (!currentValue.equals("")) {
                        currentValue += ";";
                    }
                    currentValue += values[i];
                    metadataMap.put(Values[i], currentValue);
                }
            }
        }
    }

    public void SetAddableRecord(MetadataKey[] Values, ArrayList<String[]> valueArray) {
        SetAddableRecord(Values, valueArray, false);
    }

    public void setRecordThroughTika(MetadataKey key, String tikaString) {
        String tikaValue = metadata.get(tikaString);
        if (tikaValue != null) {
            setRecord(key, tikaValue, false, true);
            //metadataMap.put(key, tikaValue);
        }
    }

    public void saveDataset(BufferedWriter writer, String prefix) {
        try {
            writer.write(prefix + filePath + "\n");
            if(this.getClass()==FolderHelper.class)
                writer.write(prefix + ((FolderHelper)this).children.size() + "\n");
            else
                writer.write(prefix + "-1\n");
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
