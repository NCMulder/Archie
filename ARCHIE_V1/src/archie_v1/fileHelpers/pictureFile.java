//License
package archie_v1.fileHelpers;

import java.nio.file.Path;

public class pictureFile extends FileHelper {

    //File specific setters
    public pictureFile(Path filePath) {
        super(filePath);
        setRecordThroughTika(MetadataKey.DateCreated, "dcterms:created");
        //setRecordThroughTika(MetadataKey.Subject, "User Comment");
        setRecordThroughTika(MetadataKey.FileSize, "File Size");
        setRecordThroughTika(MetadataKey.FileDescription, "title");
    }
}
