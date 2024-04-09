package test0409;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class test0409_01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("test");
		
		String excelFilePath = "C:\\Users\\INNOX-002\\Desktop\\shao\\forEclipse\\workspace\\test.xlsx";
		 
        try (FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        	XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
 
            // 获取第一个工作表
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            int colIndex = 0;
            
            // 遍历行
            for (Row row : sheet) {
            	Cell cell = row.getCell(colIndex);
            	
            	if (cell != null) {
            		// 获取单元格数据
//                    String cellValue = String.valueOf(cell.getNumericCellValue());
            		cell.setCellType(CellType.STRING);
            		
                    String cellValue = cell.getStringCellValue();
                    System.out.println(cellValue);
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
