//License
package archie_v1;

import org.jdom2.Document;

public abstract class outputAbstract {
    protected Document output;
    
    public abstract void createOutput(Document archieXML);
}
