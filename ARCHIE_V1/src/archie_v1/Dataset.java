//License
package archie_v1;

import archie_v1.UI.ProgressPanel;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import archie_v1.fileHelpers.ReadmeParser;
import archie_v1.fileHelpers.basicFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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

    private FolderHelper datasetHelper;

    //Debugging
    private ArrayList<String> probfiles = new ArrayList();

    ProgressPanel pP;

    public Dataset(String name, Path path, boolean fromArchie, ProgressPanel pP, FolderHelper datasetHelper, BufferedReader br, int childCount) {
        long now, start = System.nanoTime();
        
        this.name = name;
        this.mainDirectory = path;
        this.pP = pP;
        this.datasetHelper = datasetHelper;

        if (!fromArchie) {
            dirToTree(path);
        } else {
            openDataset(br, childCount);
        }

        //Temporary debugging.
        if (!probfiles.isEmpty()) {
            String errorMessage = "There was a problem parsing the following files:\n\n";
            for (String s : probfiles) {
                errorMessage += s + "\n";
            }

            try {
                File archielog = new File("Archie(o)Lo(o)g.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(archielog));
                writer.write("Log file for dataset " + name + "\n\n");
                writer.write(errorMessage);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            }
            errorMessage += "\nPlease report this error, enclosing all problematic file names.\nA log file has been saved to the ARCHIE directory.";

            JOptionPane.showMessageDialog(pP, errorMessage);
        }
        
        now = System.nanoTime();
        System.out.println("Creating the dataset: " + (now - start)/1000000 + "ms");
    }

    /**
     *
     * @param path
     * @return
     */
    public void dirToTree(Path path) {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(mainDirectory);
        //FolderHelper folderHelper = new FolderHelper(path, includeIslandora, true);
        for (File file : mainDirectory.toFile().listFiles()) {
            createNodes(file.toPath(), dirTree, datasetHelper);
        }
        files.add(datasetHelper);

        fileTree = dirTree;
    }

    public void openDataset(BufferedReader br, int children) {
        try {
            DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(mainDirectory);
            br.readLine();
            for (int i = 0; i < children; i++) {
                createNodes(br, ">>", dirTree, datasetHelper);
            }
            files.add(datasetHelper);
            fileTree = dirTree;
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createNodes(BufferedReader br, String prefix, DefaultMutableTreeNode parent, FolderHelper folderHelper) {
        try {
            Path path = Paths.get(br.readLine().replaceFirst(prefix, ""));
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path);

            parent.add(fileNode);
            String nextLine = br.readLine().replaceFirst(prefix, "");
            int childCount = Integer.parseInt(nextLine);
            boolean isDir = childCount > 0;

            System.out.println("Creating a filehelper for file " + path + ", parent is " + folderHelper.filePath);
            FileHelper fileHelper;

            if (isDir) {
                fileHelper = new FolderHelper(path, false, true);
            } else {
                fileHelper = new basicFile(path, false, true);
            }

            while (!(nextLine = br.readLine()).equals("--")) {
                //First, parse metadatakeys
                String[] keyValue = nextLine.replaceFirst(prefix, "").split(": ");
                if (keyValue.length > 1) {
                    MetadataKey key = MetadataKey.valueOf(keyValue[0]);
                    String value = keyValue[1];
                    fileHelper.setRecord(key, value, true, true);
                }
            }
            while (!(nextLine = br.readLine()).equals("--")) {
                //Secondly, parse metadata
                String[] keyValue = nextLine.replaceFirst(prefix, "").split(": ");
                if (keyValue.length > 1) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    fileHelper.metadata.put(key, value);
                }
            }

            if (isDir) {
                //now, do it for childnodes
                for (int i = 0; i < childCount; i++) {
                    createNodes(br, prefix + ">>", fileNode, (FolderHelper) fileHelper);
                }

            }
            folderHelper.children.add(fileHelper);
            files.add(fileHelper);

        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        if (pP != null) {
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
        } //Possibly also filter out all files starting with ".".

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

            FolderHelper folderHelper = new FolderHelper(file);
            for (File dirFile : file.toFile().listFiles()) {
                createNodes(dirFile.toPath(), fileNode, folderHelper);
            }

            folderH.children.add(folderHelper);

            if (readmes.containsKey(file)) {
                Path readmePath = readmes.get(file);
                ReadmeParser rms = new ReadmeParser(folderHelper, readmePath);
            }

            files.add(folderHelper);
        } else {
            //possibly do this concurrently, most time lost doing this.
            FileHelper fileHelper = ARCHIE.fileSelector(file);
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

    public boolean saveDataset() {
        try {
            (new File("saves")).mkdirs();
            File datasetSave = new File("saves\\" + name + ".archie");
            BufferedWriter writer = new BufferedWriter(new FileWriter(datasetSave));

            writer.write(name + "\n\n");

            datasetHelper.saveDataset(writer, "");

            writer.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
