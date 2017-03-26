/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.outputFormats;

import archie_v1.Dataset;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
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
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

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

    public outputDANS(Dataset dataset) {
        super();
        this.dataset = dataset;
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
            ZipEntry zEntry = new ZipEntry(dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_DANS_FileList.xls");
            out.putNextEntry(zEntry);

            workbook.write(out);
            out.closeEntry();

            HSSFWorkbook datasetWB = createDatasetWorkbook();
            zEntry = new ZipEntry(dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "_DANS_Bulk.xls");
            out.putNextEntry(zEntry);
            datasetWB.write(out);
            out.closeEntry();

            for (FileHelper fh : files) {
                setProgress(this.getProgress() + 1);
                Path filePath = fh.filePath;
                if (filePath.toFile().isDirectory()) {
                    continue;
                }

                String[] fileNames = checkDuplicateNames(filePath);

                //Writing the associated file.
                FileInputStream fIS = new FileInputStream(filePath.toString());
                ZipEntry fileZipEntry = new ZipEntry(fileNames[0] + fileNames[1]);
                out.putNextEntry(fileZipEntry);
                byte[] readBuffer = new byte[2048];
                int length;
                while ((length = fIS.read(readBuffer)) > 0) {
                    out.write(readBuffer, 0, length);
                }
                out.closeEntry();
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

        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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

    public void createCodeBooks() {

    }

    public HSSFWorkbook createDatasetWorkbook() {
        HSSFWorkbook datasetWB = new HSSFWorkbook();
        HSSFSheet datasetSheet = datasetWB.createSheet();
        HSSFRow firstRow = datasetSheet.createRow(0);
        String[] columnTitles = {"DATASET", "DC_TITLE", "DCT_ALTERNATIVE", "DCX_CREATOR_TITLES", "DCX_CREATOR_INITIALS", "DCX_CREATOR_INSERTIONS", "DCX_CREATOR_SURNAME", "DCX_CREATOR_DAI", "DCX_CREATOR_ORGANIZATION", "DCX_CONTRIBUTOR_TITLES", "DCX_CONTRIBUTOR_INITIALS", "DCX_CONTRIBUTOR_INSERTIONS", "DCX_CONTRIBUTOR_SURNAME", "DCX_CONTRIBUTOR_DAI", "DCX_CONTRIBUTOR_ORGANIZATION", "DDM_CREATED", "DCT_RIGHTSHOLDER", "DC_PUBLISHER", "DC_DESCRIPTION", "DC_SUBJECT", "DCT_TEMPORAL", "DCT_SPATIAL", "DCX_SPATIAL_SCHEME", "DCX_SPATIAL_X", "DCX_SPATIAL_Y", "DCX_SPATIAL_NORTH", "DCX_SPATIAL_SOUTH", "DCX_SPATIAL_EAST", "DCX_SPATIAL_WEST", "DC_IDENTIFIER", "DCX_RELATION_QUALIFIER", "DCX_RELATION_TITLE", "DCX_RELATION_LINK", "DC_TYPE", "DC_FORMAT", "DC_LANGUAGE", "DC_SOURCE", "DDM_ACCESSRIGHTS", "DDM_AVAILABLE", "DDM_AUDIENCE", "DepositorID", "ArchivistID", "DatasetState"};
        for (int i = 0; i < columnTitles.length; i++) {
            HSSFCell cell = firstRow.createCell(i);
            cell.setCellValue(columnTitles[i]);
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

    private String[] checkDuplicateNames(Path path) {
        int version = 0;
        String fileName = FilenameUtils.removeExtension(path.getFileName().toString());
        String fileExtension = path.getFileName().toString().replace(fileName, "");
        while (names.contains(fileName)) {
            if (version != 0) {
                fileName = fileName.substring(0, fileName.length() - 3);
            }
            version++;
            fileName += "[" + version + "]";
        }
        names.add(fileName);

        String[] stringArray = {fileName, fileExtension};
        return stringArray;
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
