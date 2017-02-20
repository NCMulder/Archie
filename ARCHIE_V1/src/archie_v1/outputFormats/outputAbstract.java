//License
package archie_v1.outputFormats;

import archie_v1.fileHelpers.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JComponent;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;

public abstract class outputAbstract {

    public abstract void Save(String destination, ArrayList<FileHelper> files, JComponent parent) throws IOException;
}
