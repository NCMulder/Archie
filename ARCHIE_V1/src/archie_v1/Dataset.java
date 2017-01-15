//License
package archie_v1;

import archie_v1.UI.ProgressPanel;
import archie_v1.fileHelpers.DatasetInitialInformation;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataContainer;
import archie_v1.fileHelpers.ReadmeParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * The Dataset class encapsulates a single dataset, defined by a name and
 * directory. Multiple ways of generating a dataset are supported (from a
 * directory or from a previously generated dataset file), and several
 * dataset-wide properties can be set upon initialization.
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class Dataset {

    public String name;
    public Path mainDirectory;
    public DefaultMutableTreeNode fileTree;
    public ArrayList<FileHelper> files = new ArrayList();
    public HashMap<Path, Path> readmes = new HashMap();
    private DatasetInitialInformation dII;
    public boolean includeIslandora;

    //Debugging
    private ArrayList<String> probfiles = new ArrayList();

    private ReadmeParser readmeParser = new ReadmeParser();

    ProgressPanel pP;

    public Dataset(String name, Path path, boolean fromArchie, boolean includeIslandora, DatasetInitialInformation dII, ProgressPanel pP) {
        this.name = name;
        this.mainDirectory = path;
        this.includeIslandora = includeIslandora;
        this.dII = dII;
        this.pP = pP;

        if (!fromArchie) {
            Thread tB = new Thread(new TreeBuilder(path));
            tB.start();
            try {
                tB.join();
                //dirToTree(path);
            } catch (InterruptedException ex) {
                Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            fileTree = null;
        }

        //Temporary debugging.
        if (!probfiles.isEmpty()) {
            String test = "There was a problem parsing the following files:\n\n";
            for (String s : probfiles) {
                test += s + "\n";
            }
            //System.out.println(test);

            try {
                File archielog = new File("Archie(o)Lo(o)g.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(archielog));
                writer.write("Log file for dataset " + name + "\n\n");
                writer.write(test);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            }
            test += "\nPlease report this error, enclosing all problematic file names.\nA log file has been saved to the ARCHIE directory.";

            JOptionPane.showMessageDialog(pP, test);
        }
    }

    /**
     *
     * @param path
     * @return
     */
    public void dirToTree(Path path) {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(path);
        FolderHelper folderHelper = new FolderHelper(path, includeIslandora, true);
        for (File file : path.toFile().listFiles()) {
            createNodes(file.toPath(), dirTree, folderHelper);
        }
        files.add(folderHelper);

        for (Map.Entry<MetadataContainer.MetadataKey, String> kvPair : dII.initInfo.entrySet()) {
            folderHelper.setRecord(kvPair.getKey(), kvPair.getValue(), false, true);
        }
        fileTree = dirTree;
    }

    //Available for planned xml-to-dataset conversion. WIP.
    public DefaultMutableTreeNode docToTree(Document doc) {
        DefaultMutableTreeNode resultTree = new DefaultMutableTreeNode(doc.getRootElement());
        for (Element elem : doc.getRootElement().getChildren()) {
            createNodes(elem, resultTree);
        }
        return resultTree;
    }

    private void createNodes(Path file, DefaultMutableTreeNode tree, FolderHelper folderH) {
            if (null != pP) {
                pP.pingProgBar();
            }
        if ("readme".equals(FilenameUtils.removeExtension(file.getFileName().toString()))) {
            readmes.put(file.getParent(), file);
            return;
        } else if ("Thumbs.db".equals(file.getFileName().toString())) {
            return;
        } else if (file.getFileName().toString().contains("datanow_meta")) {
            return;
        } else if (file.getFileName().toString().equals(".dataNowFolderUploads_")) {
            return;
        }

        //Removing problematic filetypes:
        String[] errFiles = {".zip", ".cache", ".svn-base",};
        String fileType = file.getFileName().toString().replace(FilenameUtils.removeExtension(file.getFileName().toString()), "");
        if (Arrays.asList(errFiles).contains(fileType)) {
            return;
        }

        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
        tree.add(fileNode);
        if (file.toFile().isDirectory()) {
            if (file.toFile().listFiles() == null) {
                System.out.println("nullexeperror for file " + file.getFileName());
                probfiles.add(file.toString());
                return;
            }
            FolderHelper folderHelper = new FolderHelper(file, includeIslandora);
            for (File dirFile : file.toFile().listFiles()) {
                createNodes(dirFile.toPath(), fileNode, folderHelper);
            }
            folderH.children.add(folderHelper);

            if (readmes.containsKey(file)) {
                ReadmeParser rms = new ReadmeParser();
                Path readmePath = readmes.get(file);
                HashMap<MetadataContainer.MetadataKey, String> readmeMetadata = rms.getData(readmePath);

                for (Entry<MetadataContainer.MetadataKey, String> s : readmeMetadata.entrySet()) {
                    if(folderHelper.metadataContainer.metadataMap.containsKey(s.getKey()))
                    folderHelper.setRecord(s.getKey(), s.getValue(), true);
                }
            }

            files.add(folderHelper);
        } else {
            //possibly do this concurrently, most time lost doing this.
            FileHelper fileHelper = ARCHIE.fileSelector(file, includeIslandora);
            folderH.children.add(fileHelper);
            files.add(fileHelper);

            //WIP
        }
    }

    private void createNodes(Element elem, DefaultMutableTreeNode tree) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(elem);
        tree.add(fileNode);
        File f = new File(elem.getAttributeValue("path"));
        if (f.isDirectory()) {
            for (Element element : elem.getChildren()) {
                createNodes(element, fileNode);
            }
        }
    }

    private class TreeBuilder extends Thread {

        public TreeBuilder(Path path) {
            dirToTree(path);
        }
    }
}
