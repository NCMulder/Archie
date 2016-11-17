//License
package archie_v1;

import java.nio.file.Path;
import java.util.Map;

public class pictureFile extends FileHelper {

    public pictureFile(Path filePath) {
        super(filePath);
    }

    @Override
    public Map<String, String> getMetaData() {
        //Todo: add picture specific metadata extraction
        return metadata;
    }
}
