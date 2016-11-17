//License header
package archie_v1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jdom2.Element;
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
        Map basedata = new HashMap<String, String>();
        //Todo: fill base metadata

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        ParseContext context = new ParseContext();
        
        try {
            parser.parse(inputstream, handler, metadata, context);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TikaException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        for (String name : metadata.names()) {
//            System.out.println(name);
//        }
        //System.out.println(metadata.names());
        //System.out.println(metadata.get("Content-Type"));
        
        String[] names = metadata.names();
        for(String metaType : names){
            basedata.put(metaType, metadata.get(metaType));
        }
        
//        BasicFileAttributes attr = null;
//        try {
//            attr = Files.readAttributes(filePath, BasicFileAttributes.class);
//        } catch (IOException ex) {
//            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        //Filetype setting.
//        String fileTypeString = null;
//        try {
//            fileTypeString = Files.probeContentType(filePath);
//        } catch (IOException ex) {
//            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        basedata.put(MetaDataType.FileType, fileTypeString);
//
//        //File creation date setting.
//        //basedata.put(MetaDataType.CreationDate, attr.creationTime());
//        //File size setting.
//        basedata.put(MetaDataType.Size, Long.toString(attr.size()));

        return basedata;
    }

    public abstract Map<String, String> getMetaData();
}
