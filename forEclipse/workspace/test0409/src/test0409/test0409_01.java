package test0409;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class test0409_01 {

	/* 0.打开excel
	 * 1.计算日本支出条数和国内支出条数（取大的一个作为插入行数）
	 * 2.遍历指定日期所有对象
	 * 3.找到对应日期起始行
	  *        ※删除既存的当天日期行数，重新写入
	 * 4.插入上面1计算出的应插条数
	 * 5.逐条写入数据 ↓
	 *   ----- 公式用数组创建 -----
	 *    For day m tol :日本现金场合 jpXmoneyList
	 *    For m1        :日本JCB场合 jcbList
	 *    For m2        :4.国内信用卡1 }
	 *                   5.国内信用卡2 } → chTzList 
	 *                   6.花呗白条等  }
	 *    For m3        :收入 日本收入的场合 jpInmoneyList
	 *    For m4        :支出 3.国内现金 chXmoneyList
	 *                   收入 国内收入的场合 chInmoneyList
	 *   ----- 公式用数组创建 -----
	 * 
	 *   支出的场合 costMoney > 0
	 * 	   日本支出的场合 costType in {1.日本现金 2.JCB信用卡}
	 * 		 costName  → [B] + RowIndex
	 * 		 costMoney → [C] + RowIndex
	 * 		 costType  → [D] + RowIndex
	 * 
	 * 		 ※ 日本现金的场合
	 * 			jpXmoneyList.add [C] + RowIndex
	 * 		   JCB的场合
	 * 			jcbList.add [C] + RowIndex
	 * 
	 * 	   国内支出的场合 costType in {3.国内现金 4.国内信用卡1 5.国内信用卡2 6.花呗白条等}
	 *       costName  → [K] + RowIndex
	 * 		 costMoney → [L] + RowIndex
	 * 
	 * 		 ※ [3.国内现金]的场合
	 * 			chXmoneyList.add [M] + RowIndex
	 * 		  [4.国内信用卡1 5.国内信用卡2 6.花呗白条等]的场合
	 * 			chTzList.add [M] + RowIndex
	 * 
	 *   收入的场合 incomeMoney > 0
	 * 		 incomeName  → [H] + RowIndex
	 * 		 incomeMoney → [I] + RowIndex
	 * 		 incomeType  → [J] + RowIndex
	 * 
	 * 		 ※  日本收入的场合 incomeType in {1.日本工资}
	 * 			jpInmoneyList.add [I] + RowIndex
	 *		   国内收入的场合 incomeType not in {1.日本工资}
	 * 			chInmoneyList.add [I] + RowIndex
	 *
	 * 6.单元格合并
	 *   3000       E列合并
	 *   day m tol  F列合并
	 *   diff       G列合并
	 *   m1         O列合并
	 *   m2         P列合并
	 *   m3         Q列合并
	 *   m4         R列合并
	 *   
	  *       ※ 合并行数为当日对象行数
	 *
	 * 7.上记6中合并的单元格中，写入对应公式
	 * 8.导出excel到modified路径
	 *
	 */
	public static void main(String[] args) {

		List<mockData> mockDataList = makeMockData();
		
		// TODO
		String excelFilePath = "C:\\Users\\INNOX-002\\Desktop\\shao\\forEclipse\\workspace\\test.xlsx";
		String modifiedFilePath = "C:\\Users\\INNOX-002\\Desktop\\shao\\forEclipse\\workspace\\test_modified.xlsx";
		 
        try (FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        	XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
        	
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            int colIndex = 0;
            
            int startWriteRowIndex = 0;
            int insertRowIndex = 0;
            int insertRowCounts = 0;
            
            int inOrOut_in_count = (int)mockDataList.stream().filter(obj -> obj.getCostMoney() != 0).count();
            int inOrOut_out_count = (int)mockDataList.stream().filter(obj -> obj.getIncomeMoney() != 0).count();
            
			insertRowCounts = inOrOut_in_count > inOrOut_out_count ? inOrOut_in_count : inOrOut_out_count;
            
            for (mockData mockData : mockDataList) {
            	String writeTime = mockData.getWriteTime();
            	
            	// 遍历行
                for (Row row : sheet) {
                	Cell cell = row.getCell(colIndex);
                	
                	if (cell != null) {
                		// 获取单元格数据
//                        String cellValue = String.valueOf(cell.getNumericCellValue());
                		cell.setCellType(CellType.STRING);
                		
                        String cellValue = cell.getStringCellValue();
                        
                        // 记录对象匹配excel日期第一条
                        if (writeTime.equals(cellValue)) {
                        	startWriteRowIndex = row.getRowNum();
                        	break;
                        }
                	}
                }
                
                
                
                System.out.println(startWriteRowIndex);
                
                // 将修改后的工作簿写入文件
                try (FileOutputStream out = new FileOutputStream(modifiedFilePath)) {
                    workbook.write(out);
                }
            }    
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	static List<mockData> makeMockData() {
		List<mockData> mockDataList = new ArrayList<mockData>();

		mockData mockData1 = new mockData(true, "2024/4/1", "morningFD1", 501, "1", "2", true, "memotest",
				"incomeName1", 900, "1");
		mockData mockData2 = new mockData(false, "2024/4/1", "morningFD2", 502, "1", "2", true, "memotest",
				"incomeName1", 900, "1");
		mockData mockData3 = new mockData(true, "2024/4/1", "morningFD3", 503, "1", "2", false, "memotest",
				"incomeName1", 900, "1");
		mockData mockData4 = new mockData(true, "2024/4/1", "morningFD4", 504, "1", "2", true, "memotest",
				"incomeName1", 900, "1");

		mockDataList.add(mockData1);
		mockDataList.add(mockData2);
		mockDataList.add(mockData3);
		mockDataList.add(mockData4);

		return mockDataList;
	}

}
