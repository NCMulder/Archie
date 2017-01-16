//License
package archie_v1.fileHelpers;

import java.nio.file.Path;

public class pictureFile extends FileHelper {

    //File specific setters
    public pictureFile(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
        setRecordThroughTika(MetadataContainer.MetadataKey.DateCreated, "dcterms:created");
        setRecordThroughTika(MetadataContainer.MetadataKey.Subject, "User Comment");
        setRecordThroughTika(MetadataContainer.MetadataKey.FileSize, "File Size");
        setRecordThroughTika(MetadataContainer.MetadataKey.FileDescription, "title");
    }
}
