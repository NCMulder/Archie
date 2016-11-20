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

    @Override
    public Document createOutput(Element archieElement) {
        
        
        
        for (Attribute attribute : archieElement.getAttributes()) {
            Element element = new Element(attribute.getName());
            element.setNamespace(ns1);
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
