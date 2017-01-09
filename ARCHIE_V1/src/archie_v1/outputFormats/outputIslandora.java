//License
package archie_v1.outputFormats;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataContainer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class outputIslandora extends outputAbstract {

    Namespace rootNamespace;

    public outputIslandora() {
        super();
    }

    @Override
    public void Save(String destination, ArrayList<FileHelper> files) throws IOException {
        Zipper zipper = new Zipper();
        HashMap<Path, Document> documentMap = new HashMap();

        for (FileHelper fileHelper : files) {
            Document doc = FileToDocument(fileHelper);
            documentMap.put(fileHelper.filePath, doc);
        }

        //Instruct the zipper to save the generated .xml-documents and their associated files to a zip.
        zipper.SaveAsZip(destination, documentMap);
    }

    public Document FileToDocument(FileHelper fileHelper) {
        Element root = getRootElement();

        for (Element element : getMODSElements(fileHelper)) {
            if (element != null) {
                root.addContent(element);
            }
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

    private Element getRootElement() {
        rootNamespace = Namespace.getNamespace("http://www.loc.gov/mods/v3");
        Namespace ns2 = Namespace.getNamespace("xsi", "https://www.w3.org/2001/XMLSchema-instance");
        Namespace ns3 = Namespace.getNamespace("schemaLocation", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
        Element modsXML = new Element("mods", rootNamespace);
        modsXML.addNamespaceDeclaration(rootNamespace);
        modsXML.addNamespaceDeclaration(ns2);
        modsXML.addNamespaceDeclaration(ns3);
        modsXML.setAttribute("version", "3.6");

        return modsXML;
    }

    //Getting MODS-elements using the metadata in the filehelpers.
    //check this method for improvements TODO
    public Element[] nameHandler(String name, FileHelper fileHelper) {
        String fullName = name;
        String familyName = "unknown", givenName = "unknown";
        String[] splitNames, nameParticles;
        if (fullName.contains(", ")) {
            splitNames = fullName.split(", ");
            if (splitNames[1].contains(" ")) {
                nameParticles = splitNames[1].split(" ");
                givenName = nameParticles[0];
                familyName = "";
                for (int i = 1; i < nameParticles.length; i++) {
                    familyName += nameParticles[i] + " ";
                }
            } else {
                givenName = splitNames[1];
            }
            familyName += splitNames[0];
        } else if (fullName.contains(" ")) {
            splitNames = fullName.split(" ");
            familyName = splitNames[splitNames.length - 1];
            givenName = "";
            for (int i = 0; i < splitNames.length - 1; i++) {
                givenName += splitNames[i] + " ";
            }
            givenName = givenName.substring(0, givenName.length() - 1);
        } else if (!"".equals(fullName)) {
            familyName = fullName;
        }

        Element namePartGiven = new Element("namePart", rootNamespace);
        namePartGiven.setAttribute("type", "given");
        namePartGiven.setText(givenName);

        Element namePartFamily = new Element("namePart", rootNamespace);
        namePartFamily.setAttribute("type", "family");
        namePartFamily.setText(familyName);

        Element[] names = new Element[2];
        names[0] = namePartFamily;
        names[1] = namePartGiven;

        return names;
    }

    public LinkedList<Element> getMODSElements(FileHelper fileHelper) {
        LinkedList<Element> elementList = new LinkedList();

        elementList.add(getTitle(fileHelper));
        elementList.add(getIdentifier(fileHelper));
        elementList.add(getCreator(fileHelper));
        elementList.addAll(Arrays.asList(getContributors(fileHelper)));
        //elementList.add(getRightsHolder(fileHelper));
        //elementList.add(getRelatedItem(fileHelper));
        elementList.add(getRelatedDataSet(fileHelper));
        elementList.add(getSubject(fileHelper));
        elementList.add(getAbstract(fileHelper));
        elementList.add(getOriginInfo(fileHelper));
        elementList.add(getTypeOfResource());
        elementList.add(getLanguage(fileHelper));
        elementList.add(getAccessLevel(fileHelper));
        elementList.add(getFileSize(fileHelper));

        return elementList;
    }

    //dataset elements?
    public Element getTitle(FileHelper fileHelper) {
        //Can be extracted directly from the filepath.
        Element titleInfo = new Element("titleInfo", rootNamespace);
        Element title = new Element("title", rootNamespace);
        title.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Title));
        titleInfo.addContent(title);

        return titleInfo;
    }

    public Element getIdentifier(FileHelper fileHelper) {
        Element identifier = new Element("identifier", rootNamespace);
        identifier.setAttribute("type", "doi");
        identifier.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Identifier));
        return identifier;
    }

    public Element getCreator(FileHelper fileHelper) {
        Element[] names = nameHandler(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorName), fileHelper);

        Element name = new Element("name", rootNamespace);
        name.setAttribute("type", "personal");

        //how do we implement role terms?
        Element role = new Element("role", rootNamespace);
        Element roleTerm = new Element("roleTerm", rootNamespace);
        roleTerm.setAttribute("type", "text");
        roleTerm.setAttribute("authority", "unknown");
        roleTerm.setText("creator");
        role.addContent(roleTerm);
        name.addContent(role);

        String TOA = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorTOA);
        if (!"None".equals(TOA)) {
            Element namePartTOA = new Element("namePart", rootNamespace);
            namePartTOA.setAttribute("type", "termsOfAdress");
            namePartTOA.setText(TOA);
        }

        name.addContent(names[1]);
        name.addContent(names[0]);

        //How do we implement name identifiers?
        Element nameIdentifier = new Element("nameIdentifier", rootNamespace);
        nameIdentifier.setAttribute("type", "DAI");
        nameIdentifier.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorIdentifier));
        name.addContent(nameIdentifier);

        Element affiliation = new Element("affiliation", rootNamespace);
        affiliation.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.CreatorAffiliation));
        name.addContent(affiliation);

        return name;
    }

    public Element[] getContributors(FileHelper fileHelper) {
        String[] contributorTOA = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorTOA).split(";");
        String[] contributorNames = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorName).split(";");
        String[] contributorIdentifier = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorIdentifier).split(";");
        String[] contributorAffiliation = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.ContributorAffiliation).split(";");
        Element[] contributors = new Element[contributorNames.length];

        for (int i = 0; i < contributorNames.length; i++) {
            Element[] names = nameHandler(contributorNames[i], fileHelper);

            Element name = new Element("name", rootNamespace);
            name.setAttribute("type", "personal");

            //how do we implement role terms?
            Element role = new Element("role", rootNamespace);
            Element roleTerm = new Element("roleTerm", rootNamespace);
            roleTerm.setAttribute("type", "text");
            roleTerm.setAttribute("authority", "unknown");
            roleTerm.setText("contributor");
            role.addContent(roleTerm);
            name.addContent(role);

            //redo this TODO
            if(i < contributorTOA.length){
            String TOA = contributorTOA[i].replace(" ", "");
            if (!"".equals(TOA)&&!"None".equals(TOA)) {
                Element namePartTOA = new Element("namePart", rootNamespace);
                namePartTOA.setAttribute("type", "termsOfAdress");
                namePartTOA.setText(TOA);
                name.addContent(namePartTOA);
            }
            }

            name.addContent(names[1]);
            name.addContent(names[0]);

            //redo this TODO
            //How do we implement name identifiers?
            if(i<contributorIdentifier.length){
            Element nameIdentifier = new Element("nameIdentifier", rootNamespace);
            nameIdentifier.setText(contributorIdentifier[i]);
            name.addContent(nameIdentifier);
            }

            if(i<contributorAffiliation.length){
            Element affiliation = new Element("affiliation", rootNamespace);
            affiliation.setText(contributorAffiliation[i]);
            name.addContent(affiliation);
            }
            
            contributors[i] = name;
        }

        return contributors;
    }

    //TODO
    public Element getRightsHolder(FileHelper fileHelper) {
        //What MODS element is used for this?
        //almost always manually set
        return null;
    }

    //TODO
    public Element getRelatedItem(FileHelper fileHelper) {
        //almost always manually set
        return null;
    }

    public Element getRelatedDataSet(FileHelper fileHelper) {
        Element relatedItem = new Element("relatedItem", rootNamespace);
        
        Element relatedTitleInfo = new Element("titleInfo", rootNamespace);
        Element relatedTitle = new Element("title", rootNamespace);
        relatedTitle.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.RelatedDatasetName));
        relatedTitleInfo.addContent(relatedTitle);
        relatedItem.addContent(relatedTitleInfo);
        
        Element relatedLocation = new Element("location", rootNamespace);
        Element relatedURL = new Element("url", rootNamespace);
        //is this really necessary?
        relatedURL.setAttribute("usage","primary");
        relatedURL.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.RelatedDatasetLocation));
        relatedLocation.addContent(relatedURL);
        relatedItem.addContent(relatedLocation);
        
        return relatedItem;
    }

    public Element getSubject(FileHelper fileHelper) {
        Element subject = new Element("subject", rootNamespace);
        
        Element topic = new Element("topic", rootNamespace);
        topic.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Subject));
        subject.addContent(topic);
        
        //Not for all files...
//        String[] temps = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.TemporalCoverage).split(" - ");
//        
//        Element temporalStart = new Element("temporal", rootNamespace);
//        temporalStart.setAttribute("point", "start");
//        temporalStart.setText(temps[0].replace(" ", ""));
//        subject.addContent(temporalStart);
//        
//        if(temps.length>1){
//            Element temporalEnd = new Element("temporal", rootNamespace);
//            temporalEnd.setAttribute("point", "end");
//            temporalEnd.setText(temps[1].replace(" ",""));
//            subject.addContent(temporalEnd);
//        }
        
        String[] coords = fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.SpatialCoverage).split(";");
        Element cartographics = new Element("cartographics", rootNamespace);
        for(String coord : coords){
            Element coordinates = new Element("coordinates", rootNamespace);
            coordinates.setText(coord.replace(" ", ""));
            cartographics.addContent(coordinates);
        }
        subject.addContent(cartographics);
        
        return subject;
    }

    public Element getAbstract(FileHelper fileHelper) {
        Element abstractEl = new Element("abstract", rootNamespace);
        abstractEl.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Description));
        
        return abstractEl;
    }

    public Element getOriginInfo(FileHelper fileHelper) {
        Element originInfo = new Element("originInfo", rootNamespace);
        Element publisher = new Element("publisher", rootNamespace);
        publisher.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Publisher));
        originInfo.addContent(publisher);
        Element creationDate = new Element("dateCreated", rootNamespace);
        creationDate.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.DateCreated));
        originInfo.addContent(creationDate);

        return originInfo;
    }

    //static element, not settable/viewable by user.
    public Element getTypeOfResource() {
        Element typeOfResource = new Element("typeOfResource", rootNamespace);
        typeOfResource.setAttribute("collection", "yes");
        typeOfResource.setText("mixed material");
        
        return typeOfResource;
    }

    //TODO
    public Element getTotalSize(FileHelper fileHelper) {
        return null;
    }

    public Element getLanguage(FileHelper fileHelper) {
        Element language = new Element("language", rootNamespace);
        Element languageTerm = new Element("languageTerm", rootNamespace);
        //todo: redo languages from text to code
        languageTerm.setAttribute("type","text");
        languageTerm.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.Language));
        language.addContent(languageTerm);
        
        return language;
    }

    public Element getAccessLevel(FileHelper fileHelper) {
        Element accessCondition = new Element("accessCondition", rootNamespace);
        accessCondition.setText(fileHelper.metadataContainer.metadataMap.get(MetadataContainer.MetadataKey.AccessLevel));
        
        return accessCondition;
    }

    //file elements?
    public Element getCollector(FileHelper fileHelper) {
        return null;
    }

    public Element getDescription(FileHelper fileHelper) {
        return null;
    }

    public Element getPurpose(FileHelper fileHelper) {
        return null;
    }

    public Element getCollection(FileHelper fileHelper) {
        return null;
    }

    public Element getUnits(FileHelper fileHelper) {
        return null;
    }

    public Element getAppreciation(FileHelper fileHelper) {
        return null;
    }

    public Element getSource(FileHelper fileHelper) {
        return null;
    }

    public Element getCitation(FileHelper fileHelper) {
        return null;
    }

    public Element getNotes(FileHelper fileHelper) {
        return null;
    }

    public Element getCoordinates(FileHelper fileHelper) {
        return null;
    }

    public Element getContentType(FileHelper fileHelper) {
        return null;
    }

    //more "optional" elements
    public Element getForm(FileHelper fileHelper) {
        return null;
    }

    public Element getFileSize(FileHelper fileHelper) {
        System.out.println(fileHelper.filePath.toFile().length());
        Element fileSize = new Element("FileSize");
        fileSize.setText("boe");
        return fileSize;
    }

    public Element getPhysicalForm(FileHelper fileHelper) {
        return null;
    }

    public Element getSourceType(FileHelper fileHelper) {
        return null;
    }

    public Element getPhysicalDescription(FileHelper fileHelper) {
        return null;
    }
}
