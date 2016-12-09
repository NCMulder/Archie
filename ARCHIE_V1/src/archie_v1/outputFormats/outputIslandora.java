//License

package archie_v1.outputFormats;

import archie_v1.fileHelpers.FileHelper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class outputIslandora extends outputAbstract {
    Namespace ns1;
    
    public outputIslandora() {
        super();
    }
    
    @Override
    public void Save(String destination, Document archieXML) throws IOException{
        Zipper zipper = new Zipper();
        Iterator<Content> files = archieXML.getDescendants();
        ArrayList<Document> toSave = new ArrayList();
        ArrayList<String> sources = new ArrayList();

        //Redo bits of this for better element detection; maybe use filter for getDescendants TODO
        while (files.hasNext()) {
            Element fileElement;
            Content temp = files.next();
            if(temp.getCType() == CType.Element)
                fileElement = (Element)temp;
            else continue;
            if ("file".equals(fileElement.getName()) && "file".equals(fileElement.getAttributeValue("type"))) {
                Path filePath = Paths.get(fileElement.getAttributeValue("path"));
                Document doc = FileToDocument(filePath);
                toSave.add(doc);
                sources.add(filePath.toString());
                }
        }
        
        //Instruct the zipper to save the generated .xml-documents and their associated files to a zip.
        zipper.SaveAsZip(destination, toSave.toArray(new Document[toSave.size()]), sources.toArray(new String[sources.size()]));
    }
    
    public Document FileToDocument(Path filePath){
        FileHelper fileHelper = fileSelector(filePath);
        Element root = getRootElement();
        
        Element[] fileElements = fileHelper.getRelevantElements(ns1);
        for(Element element : fileElements){
            if(element!=null)
                root.addContent(element);
        }
        
//        //not sure if necessary
//        HashMap piMap = new HashMap(2);
//        piMap.put("type", "text/xsd");
//        piMap.put("href", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
//        ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", piMap);
        
        Document fileXML = new Document(root);
//        fileXML.addContent(0, pi);
        
        return fileXML;
    }
    
    private Element getRootElement(){
        ns1 = Namespace.getNamespace("http://www.loc.gov/mods/v3");
        Namespace ns2 = Namespace.getNamespace("xsi", "https://www.w3.org/2001/XMLSchema-instance");
        Namespace ns3 = Namespace.getNamespace("schemaLocation", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
        Element modsXML = new Element("mods", ns1);
        modsXML.addNamespaceDeclaration(ns1);
        modsXML.addNamespaceDeclaration(ns2);
        modsXML.addNamespaceDeclaration(ns3);
        modsXML.setAttribute("version", "3.6");
        
        return modsXML;
    }
}
