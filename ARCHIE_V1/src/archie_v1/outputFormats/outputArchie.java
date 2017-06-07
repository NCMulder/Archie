//License
package archie_v1.outputFormats;

import archie_v1.Dataset;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.MetadataKey;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;

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
            
            ZipEntry datasetEntry = new ZipEntry(dataset.mainDirectory.getParent().relativize(dataset.mainDirectory).toString());
            zipOut.putNextEntry(datasetEntry);
            
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
            }
            zipOut.close();
            
            return null;
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
                JOptionPane.showMessageDialog(parent, "The Archie-export has been succesfully saved.");
            }
        }
    }

}
