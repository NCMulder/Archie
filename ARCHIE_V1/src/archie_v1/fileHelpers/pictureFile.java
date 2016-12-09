//License
package archie_v1.fileHelpers;

import java.nio.file.Path;
import java.util.Map;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class pictureFile extends FileHelper {

    public pictureFile(Path filePath) {
        super(filePath);
    }
    
    @Override
    public Element[] getRelevantElements(Namespace ns){
        Element[] elArray = new Element[5];
        
        elArray[0] = getTitle(ns);
        elArray[1] = getCreator(ns, null, null);
        elArray[2] = getSubject(ns);
        elArray[3] = getOriginInfo(ns, null, "dcterms:created");
        elArray[4] = getTypeOfResource(ns);
        
        return elArray;
    }
    
    
    @Override
    public Element getSubject(Namespace namespace){
        Element subject = new Element("Subject", namespace);
        Element topic = new Element("topic", namespace);
        String topicString = (metadata.get("Scene Type")==null)? "unknown":metadata.get("Scene Type");
        topic.setText(topicString);
        subject.addContent(topic);
        return subject;
    }
}
