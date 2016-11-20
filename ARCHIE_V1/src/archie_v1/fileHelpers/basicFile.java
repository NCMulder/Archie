//License
package archie_v1.fileHelpers;

import archie_v1.FileHelper;
import java.nio.file.Path;
import java.util.Map;

public class basicFile extends FileHelper {

    public basicFile(Path filePath) {
        super(filePath);
    }

    @Override
    public Map<String, String> getMetaData() {
        return metadata;
    }
}
