//License
package archie_v1.outputFormats;

import archie_v1.ARCHIE;
import archie_v1.outputAbstract;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
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
    }

    @Override
    public Document createOutput (Document archieXML) {
        
        //Todo: change to MODS output.
        Iterator<Content> files = archieXML.getDescendants();

        while (files.hasNext()) {
            Element temp = (Element) files.next();
            if ("file".equals(temp.getName())) {
                Element file = new Element(temp.getName());
                file.setNamespace(ns1);
                for (Attribute attribute : temp.getAttributes()) {
                    Element element = new Element(attribute.getName());
                    element.setNamespace(ns1);
                    element.setText(attribute.getValue());
                    file.addContent(element);
                }
                output.getRootElement().addContent(file);
            }
        }

        return output;
    }
    
    
    //redo this for all file types (e.g. public Document singleIslandora(pictureFile file)? Maybe not if not all is needed 
    public Document singleItem(Element element){
        Element root = output.getRootElement();
        
        //title info
        Element titleInfo = new Element("titleInfo", ns1);
        Element title = new Element("title", ns1);
        title.setText(element.getAttributeValue("name"));
        titleInfo.addContent(title);
        root.addContent(titleInfo);
        
        //identifier
        Element identifier = new Element("identifier", ns1);
        identifier.setAttribute("type", "unknown");
        identifier.setText("unknown");
        root.addContent(identifier);
        
        //creator
        Element name = new Element("name", ns1);
        name.setAttribute("type", "personal");
        Element role = new Element("role", ns1);
        Element roleTerm = new Element("roleTerm", ns1);
        roleTerm.setAttribute("type", "text");
        roleTerm.setAttribute("authority", "unknown");
        roleTerm.setText("creator");
        role.addContent(roleTerm);
        name.addContent(role);
        Element namePartTOA = new Element("namePart", ns1);
        namePartTOA.setAttribute("type", "termsOfAdress");
        namePartTOA.setText("unknown");
        name.addContent(namePartTOA);
        Element namePartGiven = new Element ("namePart", ns1);
        namePartGiven.setAttribute("type", "given");
        namePartGiven.setText(element.getAttributeValue("creator").split(", ")[1]);
        name.addContent(namePartGiven);
        Element namePartFamily = new Element("namePart", ns1);
        namePartFamily.setAttribute("type", "family");
        namePartFamily.setText(element.getAttributeValue("creator").split(", ")[0]);
        name.addContent(namePartFamily);
        Element nameIdentifier = new Element("nameIdentifier", ns1);
        nameIdentifier.setText("unknown");
        name.addContent(nameIdentifier);
        Element affiliation = new Element("affiliation", ns1);
        affiliation.setText(element.getAttributeValue("publisher"));
        name.addContent(affiliation);
        root.addContent(name);
        
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
        dateCreated.setText(element.getAttributeValue("modified"));
        originInfo.addContent(dateCreated);
        root.addContent(originInfo);
        
        //typeOfResource/collection/text
        Element typeOfResource = new Element("typeOfResource", ns1);
        typeOfResource.setAttribute("collection", "yes");
        typeOfResource.setText("mixed material");
        root.addContent(typeOfResource);
        
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

    @Override
    public Document createOutput(Element archieElement) {
        
        
        
        for (Attribute attribute : archieElement.getAttributes()) {
            Element element = new Element(attribute.getName());
            element.setText(attribute.getValue());
            output.getRootElement().addContent(element);
        }
        
        
        
        XMLOutputter outputter = new XMLOutputter();
        try {
            PrintWriter writer = new PrintWriter("fileXMLS/" + archieElement.getAttributeValue("name") + ".xml");
            outputter.output(output, writer);
        } catch (IOException ex) {
            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
        }
        
        try {
            byte[] buffer = new byte[1024];
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream("testoutput/" + archieElement.getAttributeValue("name") + ".zip"));
            
            //fef
            File srcFile = new File(archieElement.getAttributeValue("path"));
            FileInputStream input = new FileInputStream(srcFile);
            out.putNextEntry(new ZipEntry(srcFile.getName()));
            int length;
            while((length=input.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            out.closeEntry();
            input.close();
            srcFile = new File("fileXMLS/" + archieElement.getAttributeValue("name") + ".xml");
            input = new FileInputStream(srcFile);
            out.putNextEntry(new ZipEntry(srcFile.getName()));
            while((length=input.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            out.closeEntry();
            input.close();
            //fef
            
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(outputIslandora.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return output;
    }
}
