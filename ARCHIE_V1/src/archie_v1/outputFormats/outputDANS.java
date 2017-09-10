//License
package archie_v1.outputFormats;

import archie_v1.Dataset;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import archie_v1.fileHelpers.databaseFile;
import archie_v1.fileHelpers.xlsxFile;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Table;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.plexus.util.FileUtils;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class outputDANS extends outputAbstract implements PropertyChangeListener {

    ArrayList<FileHelper> files;
    HSSFWorkbook workbook;
    Dataset dataset;
    private HSSFSheet sheet;
    ArrayList<String> names = new ArrayList();
    int size;

    ProgressMonitor pm;
    DANSSaver saver;
    private JComponent parent;

    String[] columnTitles = {"DATASET", "DC_TITLE", "DCT_ALTERNATIVE", "DCX_CREATOR_TITLES", "DCX_CREATOR_INITIALS", "DCX_CREATOR_INSERTIONS",
        "DCX_CREATOR_SURNAME", "DCX_CREATOR_DAI", "DCX_CREATOR_ORGANIZATION", "DCX_CONTRIBUTOR_TITLES", "DCX_CONTRIBUTOR_INITIALS",
        "DCX_CONTRIBUTOR_INSERTIONS", "DCX_CONTRIBUTOR_SURNAME", "DCX_CONTRIBUTOR_DAI", "DCX_CONTRIBUTOR_ORGANIZATION", "DDM_CREATED",
        "DCT_RIGHTSHOLDER", "DC_PUBLISHER", "DC_DESCRIPTION", "DC_SUBJECT", "DCT_TEMPORAL", "DCT_SPATIAL", "DCX_SPATIAL_SCHEME",
        "DCX_SPATIAL_X", "DCX_SPATIAL_Y", "DCX_SPATIAL_NORTH", "DCX_SPATIAL_SOUTH", "DCX_SPATIAL_EAST", "DCX_SPATIAL_WEST",
        "DC_IDENTIFIER", "DCX_RELATION_QUALIFIER", "DCX_RELATION_TITLE", "DCX_RELATION_LINK", "DC_TYPE", "DC_FORMAT", "DC_LANGUAGE",
        "DC_SOURCE", "DDM_ACCESSRIGHTS", "DDM_AVAILABLE", "DDM_AUDIENCE", "DepositorID", "ArchivistID", "DatasetState"};

    MetadataKey[] columnKeys = {null, MetadataKey.DatasetTitle, MetadataKey.AlternativeTitle, MetadataKey.CreatorTOA, MetadataKey.CreatorGivenName, null,
        MetadataKey.CreatorFamilyName, MetadataKey.CreatorIdentifier, MetadataKey.CreatorAffiliation, MetadataKey.ContributorTOA, MetadataKey.ContributorGivenName,
        null, MetadataKey.ContributorFamilyName, MetadataKey.ContributorIdentifier, MetadataKey.CreatorAffiliation, MetadataKey.DateCreated,
        null /*Hier hoort rightsholder; moet deze er weer bij?*/, MetadataKey.Publisher, MetadataKey.Description, MetadataKey.Subject, MetadataKey.TemporalCoverage, MetadataKey.SpatialCoverage, MetadataKey.FileGeopgraphicUnit,
        null, null, null, null, null, null,
        MetadataKey.Identifier, null, MetadataKey.RelatedDatasetName, MetadataKey.RelatedDatasetLocation, null, MetadataKey.Software, MetadataKey.Language,
        MetadataKey.FileSource, MetadataKey.AccessLevel, MetadataKey.Embargo, null, null, null, null};

    public outputDANS(Dataset dataset) {
        super();
        this.dataset = dataset;
        createDatasetWorkbook();
        setupWorkbook();
    }

    private class DANSSaver extends SwingWorker<Void, Void> {

        String destination;

        public DANSSaver(String destination) {
            this.destination = destination;
        }

        @Override
        protected Void doInBackground() throws Exception {
            setProgress(0);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(FilenameUtils.removeExtension(destination) + ".zip"));

            out.putNextEntry(new ZipEntry("codebooks/"));
            out.closeEntry();

            ZipEntry zEntry = new ZipEntry(dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_DANS_FileList.xls");
            out.putNextEntry(zEntry);
            workbook.write(out);
            out.closeEntry();

            System.out.println("Do we get here?");
            HSSFWorkbook datasetWB = createDatasetWorkbook();
            System.out.println("Do we get here?");
            zEntry = new ZipEntry(dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_DANS_Bulk.xls");
            out.putNextEntry(zEntry);
            datasetWB.write(out);
            out.closeEntry();

            for (FileHelper fh : files) {
                setProgress(this.getProgress() + 1);
                Path filePath = fh.filePath;
                if (filePath.toFile().isDirectory()) {
                    zEntry = new ZipEntry(dataset.mainDirectory.relativize(fh.filePath).toString() + "/");
                    out.putNextEntry(zEntry);
                    out.closeEntry();
                    continue;
                }

                //String[] fileNames = checkDuplicateNames(filePath);
                //Writing the associated file.
                FileInputStream fIS = new FileInputStream(filePath.toString());
                ZipEntry fileZipEntry = new ZipEntry(dataset.mainDirectory.relativize(fh.filePath).toString());
                out.putNextEntry(fileZipEntry);
                byte[] readBuffer = new byte[2048];
                int length;
                while ((length = fIS.read(readBuffer)) > 0) {
                    out.write(readBuffer, 0, length);
                }
                out.closeEntry();
                
                //Writing the associated codebook, if applicable
                try {
                    String codebookPath = fh.metadataMap.get(MetadataKey.RelatedCodeBookLocation);
                    if (codebookPath != null && !codebookPath.equals("will be generated upon export")) {
                        System.out.println("Writing associated codebook for file " + filePath.getFileName());

                        FileInputStream codeStream = new FileInputStream(codebookPath);
                        ZipEntry codebookEntry = new ZipEntry("codebooks/" + Paths.get(codebookPath).getFileName());
                        out.putNextEntry(codebookEntry);
                        readBuffer = new byte[2048];
                        length = 0;
                        while ((length = codeStream.read(readBuffer)) > 0) {
                            out.write(readBuffer, 0, length);
                        }
                        out.closeEntry();
                    } else if (fh instanceof xlsxFile && codebookPath.equals("will be generated upon export")) {
                        ArrayList<Workbook> codebooks = ((xlsxFile) fh).codebooks;
                        for (Workbook codebook : codebooks) {
                            System.out.println("Writing codebook...");
                            String fileName = FileUtils.removeExtension(fh.filePath.getFileName().toString()) + "_" + codebook.getSheetName(0) + "." + FileUtils.getExtension(((xlsxFile) fh).filePath.toString());
                            ZipEntry cbEntry = new ZipEntry("codebooks/" + fileName);

                            File tempFile = new File("temp");
                            tempFile.mkdirs();
                            File codeFile = new File(tempFile, codebook.getSheetName(0) + "_" + ((xlsxFile) fh).filePath.getFileName());
                            FileOutputStream FOS = new FileOutputStream(codeFile);
                            codebook.write(FOS);
                            FOS.close();

                            FileInputStream FIS = new FileInputStream(codeFile);

                            out.putNextEntry(cbEntry);
                            readBuffer = new byte[2048];
                            length = 0;
                            while ((length = FIS.read(readBuffer)) > 0) {
                                out.write(readBuffer, 0, length);
                            }
                            FIS.close();
                            out.closeEntry();
                        }
                    } else if (fh instanceof databaseFile){
                        exportAccessCodebook((databaseFile)fh, out);
                    }

                } catch (Exception e) {
                    System.out.println("Duplicate codebook, not writing twice.");
                }
            }

            out.close();
            return null;
        }

    }

    @Override
    public void Save(String destination, ArrayList<FileHelper> files, JComponent parent) throws IOException {
        this.files = files;
        this.parent = parent;
        size = files.size();
        pm = new ProgressMonitor(parent, "Saving files...", "", 0, size);
        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);

        //parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        createFilesList();

        saver = new DANSSaver(destination);
        saver.addPropertyChangeListener(this);
        saver.execute();
    }

    public void createFilesList() {
        int folderHelpers = 0;
        for (int i = 1; i < files.size(); i++) {
            FileHelper file = files.get(i);
            if (file instanceof FolderHelper) {
                folderHelpers++;
                continue;
            }
            HSSFRow row = sheet.createRow(i - folderHelpers);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(file.filePath.getFileName().toString());

            int notDANS = 0;
            for (int j = 0; j < MetadataKey.values().length; j++) {
                MetadataKey key = MetadataKey.values()[j];
                if (key.getDANSname() == null) {
                    notDANS++;
                    continue;
                }
                String value = file.metadataMap.get(key);
                if (key.equals(MetadataKey.CreatorGivenName)) {
                    String[] givenNames = (file.metadataMap.get(MetadataKey.CreatorGivenName) + ";" + file.metadataMap.get(MetadataKey.ContributorGivenName)).split(";");
                    String[] familyNames = (file.metadataMap.get(MetadataKey.CreatorFamilyName) + ";" + file.metadataMap.get(MetadataKey.ContributorFamilyName)).split(";");
                    value = "";
                    for (int k = 0; k < givenNames.length; k++) {
                        String givenName = givenNames[k];
                        String familyName = familyNames[k];
                        if (givenName.equals("null") && familyName.equals("null")) {
                            continue;
                        } else if (givenName.equals("null")) {
                            value += familyName + ";";
                        } else {
                            value += givenName + " " + familyName + ";";
                        }
                    }
                    if (value.equals("")) {
                        value = null;
                    }
                }
                if (value != null) {
                    cell = row.createCell(j + 1 - notDANS);
                    cell.setCellValue(value);
                }
            }
        }

    }

    public HSSFWorkbook createDatasetWorkbook() {
        HSSFWorkbook datasetWB = new HSSFWorkbook();
        HSSFSheet datasetSheet = datasetWB.createSheet();

        int rowcount = 2;
        for (MetadataKey key : MetadataKey.values()) {
            if (key.addable) {
                String value = dataset.datasetHelper.metadataMap.get(key);
                if (value != null) {
                    rowcount = Math.max(rowcount, value.split(";").length + 1);
                }
            }
        }

        HSSFRow[] rows = new HSSFRow[rowcount];
        for (int i = 0; i < rowcount; i++) {
            rows[i] = datasetSheet.createRow(i);
        }

        for (int i = 0; i < columnTitles.length; i++) {
            HSSFCell[] valueCells = new HSSFCell[rowcount];
            for (int j = 0; j < rowcount; j++) {
                valueCells[j] = rows[j].createCell(i);
            }

            String headerValue = columnTitles[i];
            valueCells[0].setCellValue(headerValue);

            MetadataKey key = columnKeys[i];
            String value = null;

            if (key != null) {
                value = dataset.datasetHelper.metadataMap.get(key);
                if (value == null) {
                    continue;
                }
                if (key.addable) {
                    String[] values = value.split(";");
                    for (int j = 0; j < values.length; j++) {
                        valueCells[j + 1].setCellValue(values[j]);
                    }
                } else {
                    valueCells[1].setCellValue(value);
                }
            } else if (headerValue.equals("DATASET")) {
                for (int j = 1; j < rowcount; j++) {
                    valueCells[j].setCellValue("Dataset_01");
                }
            } else if (headerValue.equals("DCX_RELATION_QUALIFIER")) {
                String columnValue = dataset.datasetHelper.metadataMap.get(MetadataKey.RelatedDatasetName);
                if (columnValue == null) {
                    continue;
                }
                for (int j = 1; j < columnValue.split(";").length + 1; j++) {
                    valueCells[j].setCellValue("references");
                }
            } else if (headerValue.equals("DC_TYPE")) {
                valueCells[1].setCellValue("dataset");
            }
        }

        return datasetWB;
    }

    private void setupWorkbook() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle));
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("file_name");

        int notDANS = 0;
        for (int i = 0; i < MetadataKey.values().length; i++) {
            String value = MetadataKey.values()[i].getDANSname();
            if (value == null) {
                notDANS++;
                continue;
            }
            cell = row.createCell(i + 1 - notDANS);
            cell.setCellValue(value);
        }
    }

    private void exportAccessCodebook(databaseFile fh, ZipOutputStream out) {
        FileOutputStream FOS = null;
        FileInputStream FIS = null;
        try {
            System.out.println("Writing codebook...");
            Workbook codebook = fh.codebook;
            String fileName = FileUtils.removeExtension(fh.filePath.getFileName().toString()) + "_codebook.xlsx";
            ZipEntry cbEntry = new ZipEntry("codebooks/" + fileName);
            File tempFile = new File("temp");
            tempFile.mkdirs();
            File codeFile = new File(tempFile, codebook.getSheetName(0) + "_" + (fh.filePath.getFileName()));
            FOS = new FileOutputStream(codeFile);
            codebook.write(FOS);
            FIS = new FileInputStream(codeFile);
            out.putNextEntry(cbEntry);
            byte[] readBuffer = new byte[2048];
            int length = 0;
            while ((length = FIS.read(readBuffer)) > 0) {
                out.write(readBuffer, 0, length);
            }   
            out.closeEntry();
        } catch (IOException ex) {
            Logger.getLogger(outputDANS.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                FOS.close();
                FIS.close();
            } catch (IOException ex) {
                Logger.getLogger(outputDANS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (int) evt.getNewValue();
            pm.setProgress(progress);
            pm.setNote("File " + progress + " of " + size);
            if (saver.isCancelled() || saver.isDone()) {
                pm.close();
                parent.setCursor(null);
                JOptionPane.showMessageDialog(parent, "The DANS-export has been succesfully saved.");
            }
        }
    }
}
