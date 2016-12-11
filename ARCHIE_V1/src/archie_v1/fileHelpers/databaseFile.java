//License
package archie_v1.fileHelpers;

import java.nio.file.Path;
import java.util.Map;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class databaseFile extends FileHelper {

    public databaseFile(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
        setRecordThroughTika(MetadataContainer.MetadataKey.CreatorName, "creator");
        setRecordThroughTika(MetadataContainer.MetadataKey.CreatorAffiliation, "extended-properties_Company");
    }
    
    @Override
    public Element[] getRelevantElements(Namespace ns) {
        Element[] elArray = new Element[4];
        
        elArray[0] = getTitle(ns);
        elArray[1] = getCreator(ns);
        elArray[2] = getTypeOfResource(ns);
        
        return elArray;
    }
    
}
