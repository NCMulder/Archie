/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class xlsxFile extends FileHelper {

    public ArrayList<Workbook> codebooks = new ArrayList();

    //Create a better addable adding method. Also helpful for removing, perhabs.
    public xlsxFile(Path filePath) {
        super(filePath);

        System.out.println("Parsing " + filePath.getFileName() + " for codebooks.");

        FileInputStream FIS = null;
        try {
            //Creating codebooks
            FIS = new FileInputStream(this.filePath.toFile());
            Workbook workbook = WorkbookFactory.create(FIS);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String name = sheet.getSheetName();
                if (name.toLowerCase().contains("codebook")) {
                    System.out.println("Sheet " + name + " not parsed");
                    continue;
                }

                Sheet codebookSheet = workbook.getSheet("codebook_" + name);
                if (codebookSheet == null) {
                    System.out.println("Codebook not found, attempting to generate empty codebook");
                    //Creating the new codebook workbook and filling the first row with default header values
                    XSSFWorkbook newCodebook = new XSSFWorkbook();
                    XSSFSheet newCodebookSheet = newCodebook.createSheet(name);
                    XSSFRow headerRow = newCodebookSheet.createRow(0);
                    String[] headerNames = {"variable", "value", "description"};
                    for (int k = 0; k < headerNames.length; k++) {
                        XSSFCell headerCell = headerRow.createCell(k);
                        headerCell.setCellValue(headerNames[k]);
                    }

                    //Getting the original table header values and putting them in the codebook worksheet
                    Row headers = sheet.getRow(0);
                    if (headers == null) {
                        System.out.println("No variables present, codebook will remain empty");
                    } else {
                        int numberofCellsFound = 0;
                        for (int j = 0; numberofCellsFound <= headers.getPhysicalNumberOfCells(); j++) {
                            Cell headerCell = headers.getCell(j);
                            if (headerCell == null || headerCell.toString() == null || headerCell.toString().equals("")) {
                                break;
                            }
                            numberofCellsFound++;
                            XSSFRow newHeader = newCodebookSheet.createRow(numberofCellsFound);
                            XSSFCell newHeaderCell = newHeader.createCell(0);
                            newHeaderCell.setCellValue(headerCell.toString());
                        }
                    }
                    codebooks.add(newCodebook);

                    continue;
                } else {
                    //Copying the codebook to a new file
                    File tempDir = new File("temp/");
                    tempDir.mkdirs();
                    File temp = new File(tempDir, filePath.getFileName().toString());
                    for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
                        if (!workbook.getSheetAt(i).getSheetName().equals(codebookSheet.getSheetName())) {
                            workbook.removeSheetAt(i);
                        }
                    }
                    FileOutputStream FOS = new FileOutputStream(temp);
                    workbook.write(FOS);
                    FOS.close();

                    FileInputStream getCodeBook = new FileInputStream(temp);
                    Workbook tempBook = WorkbookFactory.create(getCodeBook);
                    codebooks.add(tempBook);
                    getCodeBook.close();
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(xlsxFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InvalidFormatException | EncryptedDocumentException ex) {
            Logger.getLogger(xlsxFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                FIS.close();
            } catch (IOException ex) {
                Logger.getLogger(xlsxFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(!codebooks.isEmpty())
            setRecord(MetadataKey.RelatedCodeBookLocation, "will be generated upon export", true);
    }

}
