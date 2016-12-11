//License
package archie_v1.fileHelpers;

import java.nio.file.Path;
import java.util.Map;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class pictureFile extends FileHelper {

    public pictureFile(Path filePath, boolean Islandora) {
        super(filePath, Islandora);
        setRecordThroughTika(MetadataContainer.MetadataKey.DateCreated, "dcterms:created");
        setRecordThroughTika(MetadataContainer.MetadataKey.Subject, "Scene Type");
    }
    
    @Override
    public Element[] getRelevantElements(Namespace ns){
        Element[] elArray = new Element[5];
        
        elArray[0] = getTitle(ns);
        elArray[1] = getCreator(ns);
        elArray[2] = getSubject(ns);
        elArray[3] = getOriginInfo(ns);
        elArray[4] = getTypeOfResource(ns);
        
        return elArray;
    }
}
