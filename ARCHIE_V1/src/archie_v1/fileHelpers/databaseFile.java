//License
package archie_v1.fileHelpers;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class databaseFile extends FileHelper {

    public XSSFWorkbook codebook = new XSSFWorkbook();

    //File specific setters
    public databaseFile(Path filePath) {
        super(filePath);

        System.out.println("Creating codebooks for database file " + filePath.getFileName());

        try {
            Database db = DatabaseBuilder.open(filePath.toFile());
            for (String tableName : db.getTableNames()) {
                XSSFSheet codebookSheet = codebook.createSheet(tableName);
                XSSFRow headerRow = codebookSheet.createRow(0);
                
                String[] headerNames = {"variable", "value", "description"};
                for (int k = 0; k < headerNames.length; k++) {
                    XSSFCell headerCell = headerRow.createCell(k);
                    headerCell.setCellValue(headerNames[k]);
                }
                
                Table table = db.getTable(tableName);
                int i = 1;
                for (Column column : table.getColumns()) {
                    XSSFRow newRow = codebookSheet.createRow(i);
                    XSSFCell newCell = newRow.createCell(0);
                    newCell.setCellValue(column.getName());
                    i++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(databaseFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
