//License
package archie_v1;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public abstract class outputAbstract {
    protected Document output;
    
    public outputAbstract(){
    }
    
    public abstract Document createOutput(Document archieXML);
}
