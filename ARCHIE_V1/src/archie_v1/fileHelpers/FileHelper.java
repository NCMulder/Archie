//License header
package archie_v1.fileHelpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jdom2.Element;
import org.jdom2.Namespace;
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
                    setRecord(MetadataKey.values()[i], MetadataKey.values()[i].getDefaultValue(), false, true);
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
                setRecord(MetadataKey.values()[i], MetadataKey.values()[i].getDefaultValue(), false, true);
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
        
//        try{
//            (new File("temp")).mkdirs();
//            FileOutputStream fileOut = new FileOutputStream("temp/" + filePath.getFileName() + "_tika.txt");
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(basedata);
//            out.close();
//            fileOut.close();
//        } catch (IOException e){
//            e.printStackTrace();
//        }

        return basedata;
    }

    public void setRecord(MetadataKey key, String value, boolean hardSet, boolean init) {
        if (!hardSet && (!key.getDefaultValue().equals(metadataMap.get(key))) && metadataMap.containsKey(key) && (!"".equals(metadataMap.get(key)))) {
            return;
        }
        if (init || metadataMap.containsKey(key)) {
            metadataMap.put(key, value);
        }
    }

    public void setRecord(MetadataKey key, String value, boolean hardSet) {
        setRecord(key, value, hardSet, false);
    }

    public void setRecordThroughTika(MetadataKey key, String tikaString) {
        String tikaValue = metadata.get(tikaString);
        if (tikaValue != null) {
            metadataMap.put(key, tikaValue);
        }
    }
}
