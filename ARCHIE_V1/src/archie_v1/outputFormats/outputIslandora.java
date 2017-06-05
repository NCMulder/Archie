//License
package archie_v1.outputFormats;

import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JComponent;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * The concrete outputter for Islandora.
 * Packs all files from the dataset with their (generated) .xml-files
 * and linked codebooks.
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class outputIslandora extends outputAbstract {
    
    /** 
     * The namespace set for all elements, and its accompanying element.
     */
    Namespace rootNamespace;
    Element rootElement;

    /**
     * Creates a hashmap containing all dataset files and generates
     * the appropriate .xml-structures, and passes this hashmap to a zipper to
     * process the saving of these structures.
     * @param destination Location of the .zip the files need to be saved to.
     * @param files List of FileHelpers containing all dataset files.
     * @param parent UI element.
     * @throws IOException
     */
    @Override
    public void Save(String destination, ArrayList<FileHelper> files, JComponent parent) throws IOException {
        setRootElement();
        Zipper zipper = new Zipper();
        HashMap<Path, Document> documentMap = new HashMap();
        
        //Iterate through all passed files, processing them when necessary.
        for (FileHelper fileHelper : files) {
            if (fileHelper instanceof FolderHelper && !fileHelper.root) {
                continue;
            }
            Document doc = FileToDocument(fileHelper);
            documentMap.put(fileHelper.filePath, doc);
        }

        //Instruct the zipper to save the generated .xml-structures and their associated files to a zip.
        zipper.SaveAsZip(destination, documentMap, files, parent);
    }

    /**
     * Generates the .xml-document based on the passed FileHelper
     * @param fileHelper The FileHelper containing the file in need of the .xml-document
     * @return An .xml-document describing the passed file 
     * (formatted according to the MODS standard (http://www.loc.gov/standards/mods/userguide/index.html))
     */
    public Document FileToDocument(FileHelper fileHelper) {
        Element root = rootElement.clone();
        
        //Generate elements from the metadata saved in the FileHelper, and add them to the root element.
        for (Element element : (fileHelper.root)?getDatasetElements((FolderHelper)fileHelper) : getMODSElements(fileHelper))
            if (element != null)
                root.addContent(element);
        
        //Create the document from the root element.
        return new Document(root);
    }

    private void setRootElement() {
        rootNamespace = Namespace.getNamespace("http://www.loc.gov/mods/v3");
        Namespace ns2 = Namespace.getNamespace("xsi", "https://www.w3.org/2001/XMLSchema-instance");
        Namespace ns3 = Namespace.getNamespace("schemaLocation", "http://www.loc.gov/standards/mods/v3/mods-3-6.xsd");
        Element modsXML = new Element("mods", rootNamespace);
        modsXML.addNamespaceDeclaration(rootNamespace);
        modsXML.addNamespaceDeclaration(ns2);
        modsXML.addNamespaceDeclaration(ns3);
        modsXML.setAttribute("version", "3.6");
        
        rootElement = modsXML;
    }

    /**
     * Groups all MODS-elements relevant to the full dataset.
     * @param datasetHelper The FolderHelper encapsulating the dataset root.
     * @return A list of MODS-elements relevant to the full dataset.
     */
    public LinkedList<Element> getDatasetElements(FolderHelper datasetHelper) {
        LinkedList<Element> elementList = new LinkedList();
        
        //Convert all metadata to the appropriate MODS-elements.
        
        //Creators
        Element[] creators = getCreator(datasetHelper);
        if (creators != null) {
            elementList.addAll(Arrays.asList(creators));
        }
        
        //Contributors
        Element[] contributors = getContributors(datasetHelper);
        if (contributors != null) {
            elementList.addAll(Arrays.asList(contributors));
        }
        
        //Origin, type of resource and rightsholder
        elementList.add(getOriginInfo(datasetHelper));
        elementList.add(getTypeOfResource());
        elementList.add(getRightsHolder(datasetHelper));
        
        //Related datasets
        Element[] relatedDatasets = getRelatedDataSet(datasetHelper);
        if(relatedDatasets!=null){
            elementList.addAll(Arrays.asList(relatedDatasets));
        }
        
        //Subject, abstract, language, and access level
        elementList.add(getSubject(datasetHelper, true));
        elementList.add(getAbstract(datasetHelper));
        elementList.add(getLanguage(datasetHelper));
        elementList.add(getAccessLevel(datasetHelper));

        return elementList;
    }

    /**
     * Groups all MODS-elements relevant to the passed FileHelper.
     * @param fileHelper The FileHelper encapsulating the file.
     * @return A list of MODS-elements relevant to the passed file.
     */
    public LinkedList<Element> getMODSElements(FileHelper fileHelper) {
        LinkedList<Element> elementList = new LinkedList();
        
        //Convert all metadata to the appropriate MODS-elements.
        
        //Creators
        Element[] creators = getCreator(fileHelper);
        if (creators != null) {
            elementList.addAll(Arrays.asList(creators));
        }
        
        //Contributors
        Element[] contributors = getContributors(fileHelper);
        if (contributors != null) {
            elementList.addAll(Arrays.asList(contributors));
        }
        
        //Origin info, type of resource, subject, and abstract
        elementList.add(getOriginInfo(fileHelper));
        elementList.add(getTypeOfResource());
        elementList.add(getSubject(fileHelper, false));
        elementList.add(getAbstract(fileHelper));
        
        //Physical description, purpose, collection, and units
        elementList.add(getPhysicalDescription(fileHelper));
        elementList.add(getPurpose(fileHelper));
        elementList.add(getCollection(fileHelper));
        elementList.add(getUnits(fileHelper));
        
        //Appreciation, source, citation, and notes
        elementList.add(getAppreciation(fileHelper));
        elementList.add(getSource(fileHelper));
        elementList.add(getCitation(fileHelper));
        elementList.add(getNotes(fileHelper));
        
        return elementList;
    }

    /**
     * Converts identifier metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getIdentifier(FileHelper fileHelper) {
        Element identifier = new Element("identifier", rootNamespace);
        identifier.setAttribute("type", "doi");
        identifier.setText(fileHelper.metadataMap.get(MetadataKey.Identifier));
        return identifier;
    }

    /**
     * Converts creator metadata to MODS element(s).
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element[] getCreator(FileHelper fileHelper) {
        String creatorNames = fileHelper.metadataMap.get(MetadataKey.CreatorFamilyName);
        if (creatorNames == null) {
            return null;
        }
        int creatorCount = creatorNames.split(";").length;
        Element[] creators = new Element[creatorCount];
        for (int i = 0; i < creatorCount; i++) {
            Element name = new Element("name", rootNamespace);
            name.setAttribute("type", "personal");

            Element role = new Element("role", rootNamespace);
            Element roleTerm = new Element("roleTerm", rootNamespace);
            roleTerm.setAttribute("type", "text");
            roleTerm.setText("creator");
            role.addContent(roleTerm);
            name.addContent(role);

            String TOA = fileHelper.metadataMap.get(MetadataKey.CreatorTOA).split(";")[i];
            if (TOA != null && !"None".equals(TOA)) {
                Element namePartTOA = new Element("namePart", rootNamespace);
                namePartTOA.setAttribute("type", "termsOfAdress");
                namePartTOA.setText(TOA);
                name.addContent(namePartTOA);
            }

            Element namePartGiven = new Element("namePart", rootNamespace);
            namePartGiven.setAttribute("type", "given");
            namePartGiven.setText(fileHelper.metadataMap.get(MetadataKey.CreatorGivenName).split(";")[i]);

            Element namePartFamily = new Element("namePart", rootNamespace);
            namePartFamily.setAttribute("type", "family");
            namePartFamily.setText(fileHelper.metadataMap.get(MetadataKey.CreatorFamilyName).split(";")[i]);

            name.addContent(namePartGiven);
            name.addContent(namePartFamily);

            //How do we implement name identifiers?
            Element nameIdentifier = new Element("nameIdentifier", rootNamespace);
            nameIdentifier.setAttribute("type", "DAI");
            nameIdentifier.setText(fileHelper.metadataMap.get(MetadataKey.CreatorIdentifier).split(";")[i]);
            name.addContent(nameIdentifier);

            Element affiliation = new Element("affiliation", rootNamespace);
            affiliation.setText(fileHelper.metadataMap.get(MetadataKey.CreatorAffiliation).split(";")[i]);
            name.addContent(affiliation);

            creators[i] = name;
        }

        return creators;
    }

    /**
     * Converts contributor metadata to MODS element(s).
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element[] getContributors(FileHelper fileHelper) {
        String contributorNames = fileHelper.metadataMap.get(MetadataKey.ContributorFamilyName);
        if (contributorNames == null) {
            return null;
        }
        int contributorCount = contributorNames.split(";").length;
        Element[] contributors = new Element[contributorCount];

        for (int i = 0; i < contributorCount; i++) {
            Element name = new Element("name", rootNamespace);
            name.setAttribute("type", "personal");

            Element role = new Element("role", rootNamespace);
            Element roleTerm = new Element("roleTerm", rootNamespace);
            roleTerm.setAttribute("type", "text");
            roleTerm.setText("contributor");
            role.addContent(roleTerm);
            name.addContent(role);

            //redo this TODO
            String TOA = fileHelper.metadataMap.get(MetadataKey.ContributorTOA).split(";")[i];
            if (TOA != null && !"None".equals(TOA)) {
                Element namePartTOA = new Element("namePart", rootNamespace);
                namePartTOA.setAttribute("type", "termsOfAdress");
                namePartTOA.setText(TOA);
                name.addContent(namePartTOA);
            }

            Element namePartGiven = new Element("namePart", rootNamespace);
            namePartGiven.setAttribute("type", "given");
            namePartGiven.setText(fileHelper.metadataMap.get(MetadataKey.ContributorGivenName).split(";")[i]);

            Element namePartFamily = new Element("namePart", rootNamespace);
            namePartFamily.setAttribute("type", "family");
            namePartFamily.setText(fileHelper.metadataMap.get(MetadataKey.ContributorFamilyName).split(";")[i]);

            name.addContent(namePartGiven);
            name.addContent(namePartFamily);

            //How do we implement name identifiers?
            Element nameIdentifier = new Element("nameIdentifier", rootNamespace);
            nameIdentifier.setAttribute("type", "DAI");
            nameIdentifier.setText(fileHelper.metadataMap.get(MetadataKey.ContributorIdentifier).split(";")[i]);
            name.addContent(nameIdentifier);

            Element affiliation = new Element("affiliation", rootNamespace);
            affiliation.setText(fileHelper.metadataMap.get(MetadataKey.ContributorAffiliation).split(";")[i]);
            name.addContent(affiliation);

            contributors[i] = name;
        }

        return contributors;
    }

    //TODO: What MODS element is used for this?
    public Element getRightsHolder(FileHelper fileHelper) {
        return null;
    }

    /**
     * Converts related-dataset metadata to MODS element(s).
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element[] getRelatedDataSet(FileHelper fileHelper) {
        String relatedTitleValue = fileHelper.metadataMap.get(MetadataKey.RelatedDatasetName);
        if (relatedTitleValue == null) {
            return null;
        }
        String[] relatedTitles = relatedTitleValue.split(";");
        String[] relatedLocations = fileHelper.metadataMap.get(MetadataKey.RelatedDatasetLocation).split(";");
        Element[] relatedItems = new Element[relatedTitles.length];

        for (int i = 0; i < relatedTitles.length; i++) {
            String relatedTitleString = relatedTitles[i];
            if(relatedTitleString==null)
                continue;
            
            Element relatedItem = new Element("relatedItem", rootNamespace);

            Element relatedTitleInfo = new Element("titleInfo", rootNamespace);
            Element relatedTitle = new Element("title", rootNamespace);
            relatedTitle.setText(relatedTitleString);
            relatedTitleInfo.addContent(relatedTitle);
            relatedItem.addContent(relatedTitleInfo);

            String relatedLocationString = relatedLocations[i];
            if(relatedLocationString == null)
                continue;
            Element relatedLocation = new Element("location", rootNamespace);
            Element relatedURL = new Element("url", rootNamespace);
            //is this really necessary?
            relatedURL.setAttribute("usage", "primary");
            relatedURL.setText(relatedLocationString);
            relatedLocation.addContent(relatedURL);
            relatedItem.addContent(relatedLocation);
        }

        return relatedItems;
    }

    /**
     * Converts subject-relevant metadata to MODS element(s).
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getSubject(FileHelper fileHelper, boolean forDataset) {
        boolean subjectTest = fileHelper.metadataMap.get(MetadataKey.Subject) != null && !fileHelper.metadataMap.get(MetadataKey.Subject).equals("");
        boolean coordinatesTest = fileHelper.metadataMap.get(MetadataKey.Coordinates) != null && !fileHelper.metadataMap.get(MetadataKey.Coordinates).equals("");
        boolean temporalCoverageTest = fileHelper.metadataMap.get(MetadataKey.TemporalCoverage) != null && !fileHelper.metadataMap.get(MetadataKey.TemporalCoverage).equals("");
        boolean spatialCoverageTest = fileHelper.metadata.get(MetadataKey.SpatialCoverage) != null && fileHelper.metadata.get(MetadataKey.SpatialCoverage).equals("");

        if (!forDataset && !coordinatesTest) {
            return null;
        }

        Element subject = new Element("subject", rootNamespace);

        if (forDataset) {
            Element titleInfo = new Element("titleInfo", rootNamespace);
            Element title = new Element("title", rootNamespace);
            title.setText(fileHelper.metadataMap.get(MetadataKey.DatasetTitle));
            titleInfo.addContent(title);
            subject.addContent(titleInfo);
        }

        if (forDataset && subjectTest) {
            Element topic = new Element("topic", rootNamespace);
            topic.setText(fileHelper.metadataMap.get(MetadataKey.Subject));
            subject.addContent(topic);
        }

        //Not for all files...
        if (forDataset && temporalCoverageTest) {
            String[] temps = fileHelper.metadataMap.get(MetadataKey.TemporalCoverage).split(" - ");

            Element temporalStart = new Element("temporal", rootNamespace);
            temporalStart.setAttribute("point", "start");
            temporalStart.setText(temps[0].replace(" ", ""));
            subject.addContent(temporalStart);

            if (temps.length > 1) {
                Element temporalEnd = new Element("temporal", rootNamespace);
                temporalEnd.setAttribute("point", "end");
                temporalEnd.setText(temps[1].replace(" ", ""));
                subject.addContent(temporalEnd);
            }
        }

        if (coordinatesTest) {
            String[] coords = fileHelper.metadataMap.get(MetadataKey.Coordinates).split(",");
            Element cartographics = new Element("cartographics", rootNamespace);
            for (String coord : coords) {
                Element coordinates = new Element("coordinates", rootNamespace);
                coordinates.setText(coord.trim());
                cartographics.addContent(coordinates);
            }
            subject.addContent(cartographics);
        }

        if (spatialCoverageTest) {
            Element geo = new Element("geographic", rootNamespace);
            geo.setText(fileHelper.metadataMap.get(MetadataKey.SpatialCoverage));
            subject.addContent(geo);
        }

        return subject;
    }

    /**
     * Converts abstract metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getAbstract(FileHelper fileHelper) {
        Element abstractEl = new Element("abstract", rootNamespace);
        abstractEl.setText(fileHelper.metadataMap.get(MetadataKey.Description));

        return abstractEl;
    }

    /**
     * Converts origin metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getOriginInfo(FileHelper fileHelper) {
        boolean publisherTest = fileHelper.metadataMap.get(MetadataKey.Publisher) != null && !fileHelper.metadataMap.get(MetadataKey.Publisher).equals("");
        boolean dateTest = fileHelper.metadataMap.get(MetadataKey.DateCreated) != null && !fileHelper.metadataMap.get(MetadataKey.DateCreated).equals("");
        if (!publisherTest && !dateTest) {
            return null;
        }
        Element originInfo = new Element("originInfo", rootNamespace);
        if (publisherTest) {
            Element publisher = new Element("publisher", rootNamespace);
            publisher.setText(fileHelper.metadataMap.get(MetadataKey.Publisher));
            originInfo.addContent(publisher);
        }
        if (dateTest) {
            Element creationDate = new Element("dateCreated", rootNamespace);
            creationDate.setText(fileHelper.metadataMap.get(MetadataKey.DateCreated));
            originInfo.addContent(creationDate);
        }

        return originInfo;
    }

    
    /**
     * @return A MODS-appropriate element for type of resource.
     */
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

    /**
     * Converts language metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getLanguage(FileHelper fileHelper) {
        Element language = new Element("language", rootNamespace);
        Element languageTerm = new Element("languageTerm", rootNamespace);
        //todo: redo languages from text to code
        languageTerm.setAttribute("type", "text");
        languageTerm.setText(fileHelper.metadataMap.get(MetadataKey.Language));
        language.addContent(languageTerm);

        return language;
    }

    /**
     * Converts access level metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getAccessLevel(FileHelper fileHelper) {
        Element accessCondition = new Element("accessCondition", rootNamespace);
        accessCondition.setText(fileHelper.metadataMap.get(MetadataKey.AccessLevel));

        return accessCondition;
    }

    /**
     * Creates note-based elements.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @param metadataKey The key for which the note is created.
     * @param noteType The MODS note type.
     * @return A MODS-appropriate element.
     */
    public Element getNote(FileHelper fileHelper, MetadataKey metadataKey, String noteType) {
        String value = fileHelper.metadataMap.get(metadataKey);
        if (value == null) {
            return null;
        }
        Element note = new Element("Note", rootNamespace);
        if (noteType != null) {
            note.setAttribute("type", noteType);
        }
        note.setText(value);
        return note;
    }

    /**
     * Converts purpose metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getPurpose(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FilePurpose, null);
    }

    /**
     * Converts collection metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getCollection(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileCollection, "accrual method");
    }

    /**
     * Converts unit metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getUnits(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileUnits, "source dimensions");
    }

    /**
     * Converts appreciation metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getAppreciation(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileAppreciation, null);
    }

    /**
     * Converts source metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getSource(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileSource, "source note");
    }

    /**
     * Converts citation metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getCitation(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileCitation, "citation/reference");
    }

    /**
     * Converts note metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getNotes(FileHelper fileHelper) {
        return getNote(fileHelper, MetadataKey.FileNotes, null);
    }

    /**
     * Converts descriptive metadata to MODS element.
     * @param fileHelper The FileHelper containing the relevant metadata.
     * @return A MODS-appropriate element.
     */
    public Element getPhysicalDescription(FileHelper fileHelper) {
        String fileContentType = fileHelper.metadataMap.get(MetadataKey.FileContent);
        String fileFormat = fileHelper.metadataMap.get(MetadataKey.FileFormat);
        String fileSize = fileHelper.metadataMap.get(MetadataKey.FileSize);

        if (fileSize == null && fileFormat == null && fileContentType == null) {
            return null;
        }

        Element physDesc = new Element("physicalDescription", rootNamespace);

        if (fileContentType != null) {
            Element internetMT = new Element("internetMediaType", rootNamespace);
            internetMT.setText(fileContentType);
            physDesc.addContent(internetMT);
        }

        if (fileFormat != null) {
            Element format = new Element("form", rootNamespace);
            format.setText(fileFormat);
            physDesc.addContent(format);
        }

        if (fileSize != null) {
            Element extent = new Element("extent", rootNamespace);
            extent.setText(fileSize);
            physDesc.addContent(extent);
        }

        return physDesc;
    }

    public Element getPhysicalForm(FileHelper fileHelper) {
        return null;
    }

    public Element getSourceType(FileHelper fileHelper) {
        return null;
    }
}
