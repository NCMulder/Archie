//License
package archie_v1.outputFormats;

import archie_v1.ARCHIE;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class Zipper {

    public boolean SaveAsZip(String destination, Document[] xmls, String[] sources) throws IOException {

//        
//        for (Attribute attribute : archieElement.getAttributes()) {
//            Element element = new Element(attribute.getName());
//            element.setText(attribute.getValue());
//            output.getRootElement().addContent(element);
//        }
//        
//        XMLOutputter outputter = new XMLOutputter();
//        try {
//            PrintWriter writer = new PrintWriter("fileXMLS/" + archieElement.getAttributeValue("name") + ".xml");
//            outputter.output(output, writer);
//        } catch (IOException ex) {
//            Logger.getLogger(ARCHIE.class.getName()).log(Level.SEVERE, "Writer not found", ex);
//        }
        File destFile = new File(destination);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));
        XMLOutputter xmlOutputter = new XMLOutputter();
        ArrayList<String> names = new ArrayList();
        
        for (int i = 0; i < xmls.length; i++) {
            //The +i is a temporary solution to prevent duplicate files overwriting one another.
            //Todo: fix overwrite, add files.
            int version = 0;
            String xmlFileName = Paths.get(sources[i]).getFileName().toString();
            while(names.contains(xmlFileName)){
                if(version!=0)
                    xmlFileName = xmlFileName.substring(0, xmlFileName.length() - 3);
                version++;
                xmlFileName += "[" + version + "]";
            }
            names.add(xmlFileName);
            ZipEntry zEntry = new ZipEntry(xmlFileName + ".xml");
            out.putNextEntry(zEntry);
            
            byte[] data = xmlOutputter.outputString(xmls[i]).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
        }
        out.close();
//        try {
//            byte[] buffer = new byte[1024];
//            
//            //fef
//            File srcFile = new File(sources[0]);
//            FileInputStream input = new FileInputStream(srcFile);
//            out.putNextEntry(new ZipEntry(srcFile.getName()));
//            int length;
//            while((length=input.read(buffer)) > 0){
//                out.write(buffer, 0, length);
//            }
//            out.closeEntry();
//            input.close();
//            srcFile = new File("fileXMLS/" + archieElement.getAttributeValue("name") + ".xml");
//            input = new FileInputStream(srcFile);
//            out.putNextEntry(new ZipEntry(srcFile.getName()));
//            while((length=input.read(buffer)) > 0){
//                out.write(buffer, 0, length);
//            }
//            out.closeEntry();
//            input.close();
//            //fef
//            
//            out.close();
//        } catch (IOException ex) {
//            Logger.getLogger(outputIslandora.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return output;
        return true;
    }
}
