//License
package archie_v1;

import java.nio.file.Path;
import java.util.Map;

public class basicFile extends FileHelper {

    public basicFile(Path filePath) {
        super(filePath);
    }

    @Override
    public Map<MetaDataType, String> getMetaData() {
        return metadata;
    }
}