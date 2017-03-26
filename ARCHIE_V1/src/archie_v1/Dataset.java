//License
package archie_v1;

import archie_v1.UI.NewDataset;
import archie_v1.fileHelpers.FileHelper;
import archie_v1.fileHelpers.FolderHelper;
import archie_v1.fileHelpers.MetadataKey;
import archie_v1.fileHelpers.ReadmeParser;
import archie_v1.fileHelpers.basicFile;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * The Dataset class encapsulates a single dataset, defined by a name and
 * directory. Multiple ways of generating a dataset are supported (from a
 * directory or from a previously generated dataset file), and several
 * dataset-wide properties can be set upon initialization.
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class Dataset implements PropertyChangeListener {

    public Path mainDirectory;
    public DefaultMutableTreeNode fileTree;
    public ArrayList<FileHelper> files = new ArrayList();
    public HashMap<Path, Path> readmes = new HashMap();
    Lock filesArrayLock = new ReentrantLock();
    Lock readmesMapLock = new ReentrantLock();

    private ProgressMonitor pm;
    int progress = 0;
    int childCount;

    public FolderHelper datasetHelper;

    //Debugging
    private ArrayList<String> probfiles = new ArrayList();
    Lock probfilesArrayLock = new ReentrantLock();

    public Dataset(Path path, FolderHelper datasetHelper, int childCount) {
        this.mainDirectory = path;
        this.datasetHelper = datasetHelper;
        this.childCount = childCount;
        pm = new ProgressMonitor(ARCHIE.ui.mf, "Processing files...", "", 0, childCount);
        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);

        dirToTree(path);

        for (FileHelper fh : datasetHelper.children) {
            fh.Save();
        }

        pm.close();

        debugFiles();
    }

    public ArrayList<String> Scan() {
        ArrayList<String> newFiles = new ArrayList();
        ArrayList<Path> currentPaths = new ArrayList();
        Collection<File> currentFiles = FileUtils.listFilesAndDirs(mainDirectory.toFile(), new RegexFileFilter("^(.*?)"), DirectoryFileFilter.INSTANCE);
        System.out.println("Files found: " + currentFiles.size());
        for (File file : currentFiles) {
            currentPaths.add(file.toPath());
        }

        System.out.println("Paths found: " + currentPaths.size());

        for (Path path : currentPaths) {
            if (!isFileSafe(path, true)) {
                continue;
            }

            boolean brk = false;
            for (FileHelper fileH : files) {
                if (fileH.filePath.equals(path)) {
                    brk = true;
                    break;
                }
            }
            if (brk) {
                continue;
            }

            newFiles.add(path.toString());
        }

        AddFiles(newFiles);

        return newFiles;
    }

    private void AddFiles(ArrayList<String> newFiles) {
        for (String fileString : newFiles) {
            Path filePath = Paths.get(fileString);
            FileHelper fileHelper;
            if (filePath.toFile().isDirectory()) {
                fileHelper = new FolderHelper(filePath);
            } else {
                fileHelper = ARCHIE.fileSelector(filePath);
            }
            files.add(fileHelper);
            Path parent = filePath.getParent();

            for (FileHelper fh : files) {
                if (fh.filePath.equals(parent)) {
                    ((FolderHelper) fh).addToChildren(fileHelper);
                    break;
                }
            }

            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(filePath);
            Enumeration<DefaultMutableTreeNode> e = fileTree.breadthFirstEnumeration();
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode parentNode = e.nextElement();
                if (parentNode.getUserObject().equals(parent)) {
                    parentNode.add(childNode);
                    break;
                }
            }
        }
    }

    public Dataset(File selectedFile) {
        pm = new ProgressMonitor(ARCHIE.ui.mf, "Processing files...", "", 0, childCount);
        try {
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
            br.readLine();
            br.readLine();
            this.mainDirectory = Paths.get(br.readLine());

            if (Files.notExists(mainDirectory)) {
                Object[] options = {"Choose directory", "Ignore", "Cancel"};
                int result = JOptionPane.showOptionDialog(
                        ARCHIE.ui.mf, "The provided directory could not be found.", "Invalid directory",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, null);
                switch (result) {
                    case 0:
                        JFileChooser fc = new JFileChooser();
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int rv = fc.showOpenDialog(ARCHIE.ui.mf);
                        if (rv == JFileChooser.APPROVE_OPTION) {
                            this.mainDirectory = fc.getSelectedFile().toPath();
                        } else {
                            return;
                        }
                    case 1:
                        break;
                    case 2:
                    default:
                        return;
                }
            }

            this.childCount = Integer.parseInt(br.readLine());
            this.datasetHelper = new FolderHelper(br, mainDirectory);

            openDataset(br, childCount);
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = childCount;
        pm.close();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (int) evt.getNewValue();
            pm.setProgress(progress);
            System.out.println("Firing event " + evt.getPropertyName());
            pm.setNote("Processing file " + progress + " of " + childCount);
        }
    }

    private void debugFiles() {
        if (!probfiles.isEmpty()) {
            String errorMessage = "There was a problem parsing the following files:\n\n";
            for (String s : probfiles) {
                errorMessage += s + "\n";
            }

            try {
                File archielog = new File("Archie(o)Lo(o)g.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(archielog));
                writer.write("Log file for dataset " + datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "\n\n");
                writer.write(errorMessage);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            }
            errorMessage += "\nPlease report this error, enclosing all problematic file names.\nA log file has been saved to the ARCHIE directory.";

            //TODO
            //JOptionPane.showMessageDialog(sw.parent, errorMessage);
        }
    }

    private boolean isFileSafe(Path filePath, boolean testForReadmes) {
        if (testForReadmes) {
            if (filePath.getFileName().toString().equals("readme.csv")) {
                return false;
            }
        }
        if ("Thumbs.db".equals(filePath.getFileName().toString())) {
            return false;
        } else if (filePath.getFileName().toString().contains("datanow_meta")) {
            return false;
        } else if (filePath.getFileName().toString().equals(".dataNowFolderUploads_")) {
            return false;
        } else if (filePath.getFileName().toString().startsWith(".")) {
            return false;
        }//Possibly also filter out all filePaths starting with ".".

        String[] errFiles = {".zip", ".cache", ".svn-base",};
        String fileType = filePath.getFileName().toString().replace(FilenameUtils.removeExtension(filePath.getFileName().toString()), "");
        if (Arrays.asList(errFiles).contains(fileType)) {
            return false;
        }
        return true;
    }

    private class Runnable1 implements Runnable {

        Path file;
        DefaultMutableTreeNode tree;
        FolderHelper folderH;

        public Runnable1(Path file, DefaultMutableTreeNode tree, FolderHelper folderH) {
            this.file = file;
            this.tree = tree;
            this.folderH = folderH;
        }

        @Override
        public void run() {
            createNodes(file, tree, folderH);
        }

        private void createNodes(Path file, DefaultMutableTreeNode tree, FolderHelper folderH) {
            System.out.println("Processing file " + progress + " of " + childCount + " [" + file.getFileName() + "]");
            
            if ("readme".equals(FilenameUtils.removeExtension(file.getFileName().toString()))) {
                readmesMapLock.lock();
                readmes.put(file.getParent(), file);
                readmesMapLock.unlock();
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
                    probfilesArrayLock.lock();
                    probfiles.add(file.toString());
                    probfilesArrayLock.unlock();
                    return;
                }

                FolderHelper folderHelper = new FolderHelper(file);
                for (File dirFile : file.toFile().listFiles()) {
                    createNodes(dirFile.toPath(), fileNode, folderHelper);
                }

                folderH.addToChildren(folderHelper);

                if (readmes.containsKey(file)) {
                    Path readmePath = readmes.get(file);
                    ReadmeParser rms = new ReadmeParser(folderHelper, readmePath);
                }

                filesArrayLock.lock();
                files.add(folderHelper);
                filesArrayLock.unlock();
            } else {
                //possibly do this concurrently, most time lost doing this.
                FileHelper fileHelper = ARCHIE.fileSelector(file);
                folderH.addToChildren(fileHelper);

                filesArrayLock.lock();
                files.add(fileHelper);
                filesArrayLock.unlock();
            }
        }
    }

    public void dirToTree(Path path) {
        DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(mainDirectory);

        Thread[] ts = new Thread[mainDirectory.toFile().listFiles().length];
        for (int i = 0; i < mainDirectory.toFile().listFiles().length; i++) {
            Path p = mainDirectory.toFile().listFiles()[i].toPath();
            Runnable1 r = new Runnable1(p, dirTree, datasetHelper);
            Thread t = new Thread(r);
            ts[i] = t;
            t.start();
        }

        pm.setMaximum(ts.length);

        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        files.add(datasetHelper);

        fileTree = dirTree;
    }

    public void openDataset(BufferedReader br, int children) {
        pm = new ProgressMonitor(ARCHIE.ui.mf, "Processing files...", "", 0, children);
        pm.setMillisToDecideToPopup(0);
        pm.setMillisToPopup(0);
        DatasetOpener dO = new DatasetOpener(br, children);
        dO.addPropertyChangeListener(this);
        dO.execute();
        
        try {
            dO.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        pm.close();
        JOptionPane.showMessageDialog(ARCHIE.ui.mf, "Successfully opened save file.");
    }
    
    private class DatasetOpener extends SwingWorker<Void, Void>{
        BufferedReader br;
        int children;
        
        public DatasetOpener(BufferedReader br, int children){
            this.br = br;
            this.children = children;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            setProgress(0);
            DefaultMutableTreeNode dirTree = new DefaultMutableTreeNode(mainDirectory);
            br.readLine();
            int myprogress = 0;
            for (int i = 0; i < children; i++) {
                setProgress(myprogress++);
                createNodes(br, ">>", dirTree, datasetHelper);
            }
            files.add(datasetHelper);
            fileTree = dirTree;
            
            return null;
        }
        
    }

    public void createNodes(BufferedReader br, String prefix, DefaultMutableTreeNode parent, FolderHelper folderHelper) {
        try {
            Path path = Paths.get(((Path) parent.getUserObject()).toString(), br.readLine().replaceFirst(prefix, ""));
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(path);

            parent.add(fileNode);
            String nextLine = br.readLine().replaceFirst(prefix, "");
            int childCount = Integer.parseInt(nextLine);
            boolean isDir = childCount > 0;

            System.out.println("Creating a filehelper for file " + path + ", parent is " + folderHelper.filePath);
            FileHelper fileHelper;

            if (isDir) {
                fileHelper = new FolderHelper(path, false);
            } else {
                fileHelper = new basicFile(path);
            }

            while (!(nextLine = br.readLine()).equals("--")) {
                //First, parse metadatakeys
                String[] keyValue = nextLine.replaceFirst(prefix, "").split(": ");
                if (keyValue.length > 1) {
                    MetadataKey key = MetadataKey.valueOf(keyValue[0]);
                    String value = keyValue[1];
                    fileHelper.setRecord(key, value, false);
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
            folderHelper.addToChildren(fileHelper);
            files.add(fileHelper);

        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean saveDataset(File saveFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));

            writer.write(datasetHelper.metadataMap.get(MetadataKey.DatasetTitle) + "\n\n");

            datasetHelper.saveDataset(writer, "");

            writer.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
