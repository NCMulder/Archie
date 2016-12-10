//License header
package archie_v1.fileHelpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xml.sax.SAXException;

public abstract class FileHelper {

    public enum MetaDataType {
        Size, CreationDate, CreationTime, FileType, Author, LastModifiedBy
    }

    public Path filePath;
    protected Map<String, String> metadata;

    public FileHelper(Path filePath) {
        this.filePath = filePath;
        metadata = getMetaData();
    }

    //Helper functions for all filehandlers.
    
    public Map<String, String> getMetaData() {
        Map basedata = new HashMap<>();

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadataTemp = new Metadata();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        ParseContext context = new ParseContext();

        try {
            parser.parse(inputstream, handler, metadataTemp, context);
        } catch (IOException | SAXException | TikaException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] names = metadataTemp.names();
        for (String metaType : names) {
            basedata.put(metaType, metadataTemp.get(metaType));
        }

        return basedata;
    }
    
    // A method for splitting a name in parts. 
    // Returns the MODS-elements in an element array,
    // in the order [family name, given name, terms of adress].
    public Element[] nameHandler(String name, Namespace namespace){
        String fullName = name;
        String familyName = "unknown", givenName = "unknown", termsOfAdress = "unknown";
        String[] splitNames, nameParticles;
        if(fullName.contains(", ")){
            splitNames = fullName.split(", ");
            if(splitNames[1].contains(" ")){
                nameParticles = splitNames[1].split(" ");
                givenName = nameParticles[0];
                familyName = "";
                for(int i = 1; i < nameParticles.length; i++)
                    familyName+=nameParticles[i] + " ";
            } else {
                givenName = splitNames[1];
            }
            familyName+=splitNames[0];
        } else if(fullName.contains(" ")){
            splitNames = fullName.split(" ");
            familyName = splitNames[splitNames.length - 1];
            givenName = "";
            for (int i = 0; i < splitNames.length - 1; i++) {
                givenName +=splitNames[i];
            }
        } else if(fullName!="") {
            familyName = fullName;
        }
        
        Element namePartTOA = new Element("namePart", namespace);
        namePartTOA.setAttribute("type", "termsOfAdress");
        namePartTOA.setText(termsOfAdress);
        
        Element namePartGiven = new Element ("namePart", namespace);
        namePartGiven.setAttribute("type", "given");
        namePartGiven.setText(givenName);
        
        Element namePartFamily = new Element("namePart", namespace);
        namePartFamily.setAttribute("type", "family");
        namePartFamily.setText(familyName);
        
        Element[] names = new Element[3];
        names[0] = namePartFamily;
        names[1] = namePartGiven;
        names[2] = namePartTOA;
        
        return names;
    }

    public Element[] getRelevantElements(Namespace ns){
        Element[] elArray = new Element[4];
        
        elArray[0] = getTitle(ns);
        elArray[1] = getCreator(ns, null, null);
        elArray[2] = getOriginInfo(ns, null, null);
        elArray[3] = getTypeOfResource(ns);
        
        return elArray;
    }
    
    //dataset elements?
    
    public Element getTitle(Namespace namespace){
        //Can be extracted directly from the filepath.
        Element titleInfo = new Element("titleInfo", namespace);
        Element title = new Element("title", namespace);
        title.setText(filePath.getFileName().toString());
        titleInfo.addContent(title);
        return titleInfo;
    }
    
    public Element getIdentifier(Namespace namespace){
        Element identifier = new Element("identifier", namespace);
        identifier.setAttribute("type", "unknown");
        identifier.setText("unknown");
        return identifier;
    }
    
    public Element getCreator(Namespace namespace, String tikaCreator, String tikaAffiliation){
        Element[] names;
        names = (tikaCreator==null)?nameHandler("", namespace):nameHandler(metadata.get(tikaCreator), namespace);
        
        Element name = new Element("name", namespace);
        name.setAttribute("type", "personal");
        
//        //how do we implement role terms?
//        Element role = new Element("role", namespace);
//        Element roleTerm = new Element("roleTerm", namespace);
//        roleTerm.setAttribute("type", "text");
//        roleTerm.setAttribute("authority", "unknown");
//        roleTerm.setText("creator");
//        role.addContent(roleTerm);
//        name.addContent(role);

        name.addContent(names[2]);
        name.addContent(names[1]);
        name.addContent(names[0]);
        
        //How do we implement name identifiers?
        Element nameIdentifier = new Element("nameIdentifier", namespace);
        nameIdentifier.setText("unknown");
        name.addContent(nameIdentifier);
        
        if(tikaAffiliation!=null){
        Element affiliation = new Element("affiliation", namespace);
        affiliation.setText(metadata.get(tikaAffiliation));
        name.addContent(affiliation);
        }
        
        return name;
    }
    
    public Element getContributor(Namespace namespace){
        //how to handle multiple contributors? array?
        return null;
    }
    
    public Element getRightsHolder(Namespace namespace){
        //almost always manually set
        return null;
    }
    
    public Element getRelatedItem(Namespace namespace){
        //almost always manually set
        return null;
    }
    
    public Element getRelatedDataSet(Namespace namespace){
        //almost always manually set
        return null;
    }
    
    public Element getSubject(Namespace namespace){
        return null;
    }
    
    public Element getAbstract(Namespace namespace){
        return null;
    }
    
    public Element getOriginInfo(Namespace namespace, String tikaPublisher, String tikaCreation){
        Element originInfo = new Element("originInfo", namespace);
        if(tikaPublisher!=null){
         Element publisher = new Element("publisher", namespace);
         String publisherText = (metadata.get(tikaPublisher) == null)?"unknown":metadata.get(tikaPublisher);
         publisher.setText(publisherText);
         originInfo.addContent(publisher);
        }
        if(tikaCreation!=null){
        Element creationDate = new Element("dateCreated", namespace);
        String creationText = (metadata.get(tikaCreation)== null)?"unknown":metadata.get(tikaCreation);
        creationDate.setText(creationText);
        originInfo.addContent(creationDate);
        }
        
        return originInfo;
    }
    
    public Element getTypeOfResource(Namespace namespace){
        Element typeOfResource = new Element("typeOfResource", namespace);
        typeOfResource.setAttribute("collection", "yes");
        typeOfResource.setText("mixed material");
        return typeOfResource;
    }
    
    public Element getTotalSize(Namespace namespace){
        return null;
    }
    
    public Element getLanguage(Namespace namespace){
        return null;
    }
    
    public Element getTemporalCoverage(Namespace namespace){
        return null;
    }
    
    public Element getSpatialCoverage(Namespace namespace){
        return null;
    }
    
    public Element getAccesLevel(Namespace namespace){
        return null;
    }
    
    //file elements?
    
    public Element getCollector(Namespace namespace){
        return null;
    }
    
    public Element getDescription(Namespace namespace){
        return null;
    }
    
    public Element getPurpose(Namespace namespace){
        return null;
    }
    
    public Element getCollection(Namespace namespace){
        return null;
    }
    
    public Element getUnits(Namespace namespace){
        return null;
    }
    
    public Element getAppreciation(Namespace namespace){
        return null;
    }
    
    public Element getSource(Namespace namespace){
        return null;
    }
    
    public Element getCitation(Namespace namespace){
        return null;
    }
    
    public Element getNotes(Namespace namespace){
        return null;
    }
    
    public Element getCoordinates(Namespace namespace){
        return null;
    }
    
    public Element getContentType(Namespace namespace){
        return null;
    }
    
    //more "optional" elements
    
    public Element getForm(Namespace namespace){
        return null;
    }
    
    public Element getFileSize(Namespace namespace){
        return null;
    }
    
    public Element getPhysicalForm(Namespace namespace){
        return null;
    }
    
    public Element getSourceType(Namespace namespace){
        return null;
    }
    
    public Element getPhysicalDescription(Namespace namespace){
        return null;
    }
}
