//License
package archie_v1.outputFormats;

import archie_v1.archieXMLcreator;
import archie_v1.outputAbstract;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class outputIslandora extends outputAbstract {

    public outputIslandora(){
        super();
        
        Namespace ns1 = Namespace.getNamespace("http://www.loc.gov/mods/v3");
        Namespace ns2 = Namespace.getNamespace("xsi", "https://www.w3.org/2001/XMLSchema-instance");
        Namespace ns3 = Namespace.getNamespace("schemaLocation", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
        Element modsXML = new Element("mods", ns1);
        modsXML.addNamespaceDeclaration(ns1);
        modsXML.addNamespaceDeclaration(ns2);
        modsXML.addNamespaceDeclaration(ns3);
        modsXML.setAttribute("version", "3.6");
        output = new Document(modsXML);
    }
    
    @Override
    public Document createOutput(Document archieXML) {
        //todo: islandora specific output
        return output;
    }
}
