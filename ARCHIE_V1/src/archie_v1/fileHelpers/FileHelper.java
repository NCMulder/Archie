//License header
package archie_v1.fileHelpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
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
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xml.sax.SAXException;

public abstract class FileHelper {

    public Path filePath;
    public Map<String, String> metadata;
    public MetadataContainer metadataContainer;
    public LinkedList<FileHelper> children;
    public boolean root = false;

    public FileHelper(Path filePath, boolean Islandora) {
        this.filePath = filePath;
        metadataContainer = new MetadataContainer(Islandora);
        children = new LinkedList();

        Initialize();
    }
    
    public FileHelper(Path filePath, boolean Islandora, boolean root){
        this.root = root;
        this.filePath = filePath;
        metadataContainer = new MetadataContainer(Islandora);
        children = new LinkedList();

        Initialize();
    }

    public void Initialize() {
        if (root) {
            for (int i = 0; i < MetadataContainer.MetadataKey.values().length; i++) {
                if (MetadataContainer.MetadataKey.values()[i].dataset) {
                    setRecord(MetadataContainer.MetadataKey.values()[i], MetadataContainer.MetadataKey.values()[i].getDefaultValue(), false, true);
                }
            }
            return;
        }

        if (!(this instanceof FolderHelper)) {
            metadata = getMetaData();
            setRecord(MetadataContainer.MetadataKey.Title, FilenameUtils.removeExtension(filePath.getFileName().toString()), false, true);
            setRecord(MetadataContainer.MetadataKey.FileContentType, "." + FilenameUtils.getExtension(filePath.toString()) + " file", false, true);
        }

        for (int i = 0; i < MetadataContainer.MetadataKey.values().length; i++) {
            if (MetadataContainer.MetadataKey.values()[i].file) {
                setRecord(MetadataContainer.MetadataKey.values()[i], MetadataContainer.MetadataKey.values()[i].getDefaultValue(), false, true);
            }
        }
    }

    //Helper functions for all filehandlers.
    public Map<String, String> getMetaData() {
        Map basedata = new HashMap<>();

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

        return basedata;
    }

    public void setRecord(MetadataContainer.MetadataKey key, String value, boolean hardSet, boolean init) {
        if (!hardSet && (!key.getDefaultValue().equals(metadataContainer.metadataMap.get(key))) && metadataContainer.metadataMap.containsKey(key) && (!"".equals(metadataContainer.metadataMap.get(key)))) {
            return;
        }
        if (init || metadataContainer.metadataMap.containsKey(key)) {
            metadataContainer.metadataMap.put(key, value);
        }
    }

    public void setRecord(MetadataContainer.MetadataKey key, String value, boolean hardSet) {
        setRecord(key, value, hardSet, false);
    }

    public void setRecordThroughTika(MetadataContainer.MetadataKey key, String tikaString) {
        String tikaValue = metadata.get(tikaString);
        if (tikaValue != null) {
            metadataContainer.metadataMap.put(key, tikaValue);
        }
    }
}
