//License
package archie_v1.fileHelpers;

import java.nio.file.Path;

public class pictureFile extends FileHelper {

    //File specific setters
    public pictureFile(Path filePath, boolean open) {
        super(filePath, open);
        if(open)
            return;
        setRecordThroughTika(MetadataKey.DateCreated, "dcterms:created");
        setRecordThroughTika(MetadataKey.Description, "title");
    }
}
