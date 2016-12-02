//License
package archie_v1;

import java.io.File;
import org.jdom2.Document;
import org.jdom2.Element;

public abstract class outputAbstract {

    protected Document output;

    public outputAbstract() {
    }

    public abstract Document createOutput(Document archieXML);
    public abstract Document createOutput(Element archieElement);
    //v replaces ^
    public abstract Document singleItem(Element element);
}
