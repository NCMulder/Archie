//License

//This class needs a major revision after the metadata-extraction refactor.
package archie_v1.outputFormats;

import archie_v1.ARCHIE;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.pictureFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.XMLOutputter;

public class outputIslandora extends outputAbstract {
    Namespace ns1;
    
    public outputIslandora() {
        super();
        
        
        ns1 = Namespace.getNamespace("http://www.loc.gov/mods/v3");
        Namespace ns2 = Namespace.getNamespace("xsi", "https://www.w3.org/2001/XMLSchema-instance");
        Namespace ns3 = Namespace.getNamespace("schemaLocation", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
        Element modsXML = new Element("mods", ns1);
        modsXML.addNamespaceDeclaration(ns1);
        modsXML.addNamespaceDeclaration(ns2);
        modsXML.addNamespaceDeclaration(ns3);
        modsXML.setAttribute("version", "3.6");
        
        output = new Document(modsXML);
        output.setDocType(new DocType("mods"));
    }
    
    @Override
    public void Save(String destination, Document archieXML) throws IOException{
        Zipper zipper = new Zipper();
        Iterator<Content> files = archieXML.getDescendants();
        ArrayList<Document> toSave = new ArrayList();
        ArrayList<String> sources = new ArrayList();

        //Redo bits of this for better element detection; maybe use filter for getDescendants
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
        return new Document(root);
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
    
    //redo this for all file types (e.g. public Document singleIslandora(pictureFile file)? Maybe not if not all is needed 
    public Document singleItem(Element element){
        Element root = output.getRootElement();
        
        //identifier
        Element identifier = new Element("identifier", ns1);
        identifier.setAttribute("type", "unknown");
        identifier.setText("unknown");
        root.addContent(identifier);
        
        
        //contributors - should be addable in metadatachanger
        
        
        //related item
        Element relatedItem = new Element("relatedItem", ns1);
        Element titleInfo2 = new Element("titleInfo", ns1);
        Element title2 = new Element("title", ns1);
        title2.setText("unknown");
        titleInfo2.addContent(title2);
        relatedItem.addContent(titleInfo2);
        Element location = new Element("location", ns1);
        Element url = new Element("url", ns1);
        url.setText("unknown");
        location.addContent(url);
        relatedItem.addContent(location);
        root.addContent(relatedItem);
        
        //subject/topic
        
        //abstract
        
        //originInfo/publisher
        
        //originInfo/dateCreated
        Element originInfo = new Element("originInfo", ns1);
        Element dateCreated = new Element ("dateCreated", ns1);
        dateCreated.setAttribute("encoding", "unknown");
        //dateCreated.setText(element.getAttributeValue("modified"));
        originInfo.addContent(dateCreated);
        root.addContent(originInfo);
        
        //physicalDescription
        
        //language/languageTerm
        
        //subject/temporal
        
        //subject/cartographic/coordinates
        
        //accesCondition
        
        //collector
        
        //abstract (description)
        
        //note (purpose)
        
        //note (collection)
        
        //note (units)
        
        //note (appreciation)
        
        //note (source)
        
        //note (citation)
        
        //note (notes_
        
        //subject/cartographic/coordinates
        
        //extras
        
        
        return output;
    }
}
