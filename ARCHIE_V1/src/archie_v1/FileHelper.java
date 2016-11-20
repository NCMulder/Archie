//License header
package archie_v1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public abstract class FileHelper {

    public enum MetaDataType {
        Size, CreationDate, CreationTime, FileType, Author, LastModifiedBy
    }

    Path filePath;
    protected Map metadata;

    public FileHelper(Path filePath) {
        this.filePath = filePath;
        metadata = getBasicMetaData();
    }

    public Map<String, String> getBasicMetaData() {
        Map basedata = new HashMap<>();
        //Todo: fill base metadata

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadataTemp = new Metadata();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        ParseContext context = new ParseContext();

        try {
            parser.parse(inputstream, handler, metadataTemp, context);
        } catch (IOException | SAXException | TikaException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] names = metadataTemp.names();
        for (String metaType : names) {
            basedata.put(metaType, metadataTemp.get(metaType));
        }

        return basedata;
    }

    public abstract Map<String, String> getMetaData();
}
