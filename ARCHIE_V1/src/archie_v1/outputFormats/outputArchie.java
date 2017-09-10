//License
package archie_v1.outputFormats;

import archie_v1.Dataset;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataKey;
import archie_v1.fileHelpers.databaseFile;
import archie_v1.fileHelpers.xlsxFile;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.plexus.util.FileUtils;

/**
 * The concrete outputter for verification purposes.
 * Packs all files from the dataset with their (generated) .xml-files
 * and linked codebooks, as well as a save file.
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class outputArchie extends outputAbstract implements PropertyChangeListener{

    Dataset dataset;
    ProgressMonitor pm;
    JComponent parent;
    doOutput dO;
    int size;

    public outputArchie(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public void Save(String destination, ArrayList<FileHelper> files, JComponent parent) throws IOException {
        this.size = files.size();
        this.parent = parent;
        pm = new ProgressMonitor(parent, "Saving files...", "", 0, size);
        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);

        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        dO = new doOutput(destination, files);
        dO.addPropertyChangeListener(this);
        dO.execute();
    }

    private class doOutput extends SwingWorker<Void, Void> {

        private ArrayList<FileHelper> files;
        private String destination;

        public doOutput(String destination, ArrayList<FileHelper> files) {
            this.files = files;
            this.destination = destination;
        }

        @Override
        protected Void doInBackground() throws Exception {
            setProgress(0);
            File temp = new File("temp");
            temp.mkdirs();

            //Saving the dataset temporarily and putting it in the zip
            File saveFile = new File(temp, dataset.datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + ".archie");
            dataset.saveDataset(saveFile);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(FilenameUtils.removeExtension(destination) + ".zip"));
            
//            ZipEntry datasetEntry = new ZipEntry(dataset.mainDirectory.getParent().relativize(dataset.mainDirectory).toString());
//            zipOut.putNextEntry(datasetEntry);
            
            FileInputStream fIS = new FileInputStream(saveFile.getPath());
            ZipEntry fileZipEntry = new ZipEntry(saveFile.getName());
            zipOut.putNextEntry(fileZipEntry);
            byte[] readBuffer = new byte[2048];
            int length;
            while ((length = fIS.read(readBuffer)) > 0) {
                zipOut.write(readBuffer, 0, length);
            }
            zipOut.closeEntry();

            for (FileHelper fh : files) {
                setProgress(this.getProgress() + 1);
                Path filePath = fh.filePath;
                if (filePath.toFile().isDirectory()) {
                    ZipEntry zEntry = new ZipEntry(dataset.mainDirectory.getParent().relativize(fh.filePath).toString() + "/");
                    zipOut.putNextEntry(zEntry);
                    zipOut.closeEntry();
                    continue;
                }

                //Writing the associated file.
                fIS = new FileInputStream(filePath.toString());
                fileZipEntry = new ZipEntry(dataset.mainDirectory.getParent().relativize(fh.filePath).toString());
                zipOut.putNextEntry(fileZipEntry);
                readBuffer = new byte[2048];
                length = 0;
                while ((length = fIS.read(readBuffer)) > 0) {
                    zipOut.write(readBuffer, 0, length);
                }
                zipOut.closeEntry();
                
                try {
                    String codebookPath = fh.metadataMap.get(MetadataKey.RelatedCodeBookLocation);
                    if (codebookPath != null && !codebookPath.equals("will be generated upon export")) {
                        System.out.println("Writing associated codebook for file " + filePath.getFileName());

                        FileInputStream codeStream = new FileInputStream(codebookPath);
                        ZipEntry codebookEntry = new ZipEntry("codebooks/" + Paths.get(codebookPath).getFileName());
                        zipOut.putNextEntry(codebookEntry);
                        readBuffer = new byte[2048];
                        length = 0;
                        while ((length = codeStream.read(readBuffer)) > 0) {
                            zipOut.write(readBuffer, 0, length);
                        }
                        zipOut.closeEntry();
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

                            zipOut.putNextEntry(cbEntry);
                            readBuffer = new byte[2048];
                            length = 0;
                            while ((length = FIS.read(readBuffer)) > 0) {
                                zipOut.write(readBuffer, 0, length);
                            }
                            FIS.close();
                            zipOut.closeEntry();
                        }
                    } else if (fh instanceof databaseFile){
                        exportAccessCodebook((databaseFile)fh, zipOut);
                    }

                } catch (Exception e) {
                    System.out.println("Duplicate codebook, not writing twice.");
                }
            }
            zipOut.close();
            
            
            
        
            parent.setCursor(null);
            return null;
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
            if (dO.isCancelled() || dO.isDone()) {
                pm.close();
                parent.setCursor(null);
                JOptionPane.showMessageDialog(parent, "The Nexus1492-export has been succesfully saved.");
            }
        }
    }

}
