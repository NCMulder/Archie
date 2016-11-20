//License
package archie_v1.outputFormats;

import archie_v1.outputAbstract;
import java.util.Iterator;
import org.jdom2.Attribute;
import org.jdom2.Content;
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
        Iterator<Content> files = archieXML.getDescendants();
        
        while(files.hasNext()){
            Content temp = files.next();
            if(temp.getCType() == Content.CType.Element){
            Element file = (Element)temp.clone();
            if("file".equals(file.getName())){
                //Todo: replace ugly attribute removal; perhabs make new element instead of cloning?
                Attribute[] attrList = new Attribute[file.getAttributes().size()];
                int i = 0;
                for(Attribute attribute : file.getAttributes()){
                    Element element = new Element(attribute.getName());
                    element.setText(attribute.getValue());
                    attrList[i] = attribute;
                    i++;
                    file.addContent(element);
                }
                
                for(Attribute attribute : attrList){
                    file.removeAttribute(attribute);
                }
                
                output.getRootElement().addContent(file);
            }
            }
        }
        
        return output;
    }
}
