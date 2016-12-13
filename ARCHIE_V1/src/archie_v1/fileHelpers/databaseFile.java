//License
package archie_v1.fileHelpers;

import java.nio.file.Path;

public class databaseFile extends FileHelper {

    //File specific setters
    public databaseFile(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
        setRecordThroughTika(MetadataContainer.MetadataKey.CreatorName, "creator");
        setRecordThroughTika(MetadataContainer.MetadataKey.CreatorAffiliation, "extended-properties_Company");
    }
}
