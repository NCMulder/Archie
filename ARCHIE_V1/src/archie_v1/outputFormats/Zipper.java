//License
package archie_v1.outputFormats;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JComponent;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

public class Zipper implements PropertyChangeListener {

    ArrayList<String> names = new ArrayList();
    ProgressMonitor pm;
    Task task;
    int size;
    JComponent parent;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (int) evt.getNewValue();
            pm.setProgress(progress);
            pm.setNote("File " + progress + " of " + size);
            if(task.isDone() || task.isCancelled()){
                pm.close();
                parent.setCursor(null);
            }
        }
    }

    class Task extends SwingWorker<Void, Void> {

        String destination;
        HashMap<Path, Document> documentMap;

        public Task(String destination, HashMap<Path, Document> documentMap) {
            this.destination = destination;
            this.documentMap = documentMap;
        }

        @Override
        protected Void doInBackground() throws Exception {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(FilenameUtils.removeExtension(destination) + ".zip"));
            XMLOutputter xmlOutputter = new XMLOutputter();

            setProgress(0);
            
            int progress = 0;
            
            for (Map.Entry<Path, Document> documentEntry : documentMap.entrySet()) {
                Path filePath = documentEntry.getKey();
                setProgress(progress++);
                if (filePath.toFile().isDirectory()) {
                    continue;
                }
                Document document = documentEntry.getValue();

                String[] fileNames = checkDuplicateNames(filePath);

                //Writing the xml file.
                ZipEntry zEntry = new ZipEntry(fileNames[0] + ".xml");
                out.putNextEntry(zEntry);
                byte[] data = xmlOutputter.outputString(document).getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();

                //Writing the associated file.
                FileInputStream fIS = new FileInputStream(filePath.toString());
                //For now, xml files get an extra .xml in their file name.
                ZipEntry fileZipEntry = new ZipEntry(fileNames[0] + (".xml".equals(fileNames[1]) ? ".xml" : "") + fileNames[1]);
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

    public void SaveAsZip(String destination, HashMap<Path, Document> documentMap, JComponent parent) throws IOException {
        this.size = documentMap.entrySet().size();
        this.parent = parent;
        
        pm = new ProgressMonitor(parent, "Saving files...", "", 0, size);
        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);
        
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task(destination, documentMap);
        task.addPropertyChangeListener(this);
        task.execute();
        
    }

    //redo this with name as key and amount as value in dict? todo
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
}
