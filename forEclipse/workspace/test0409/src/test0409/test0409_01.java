package test0409;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class test0409_01 {

	/* 0.打开excel
	 * 1.计算日本支出条数和国内支出条数（取大的一个作为插入行数）
	 * 2.遍历指定日期所有对象
	 * 3.找到对应日期起始行
	  *        ※ 删除既存的当天日期行数，重新写入
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
	 *    For Diff      :累加对象的场合 isSumRecordList
	 *   ----- 公式用数组创建 -----
	 * 
	 *   支出的场合 costMoney > 0
	 * 	   日本支出的场合 payMethod in {1.日本现金 2.JCB信用卡}
	 * 		 costName  → [B] + RowIndex
	 * 		 costMoney → [C] + RowIndex
	 * 		 costType  → [D] + RowIndex
	 * 
	 * 		 ※ 日本现金的场合
	 * 			jpXmoneyList.add [C] + RowIndex
	 *          
	  *                         ※ 累加对象的场合
	 * 			  isSumRecordList.add [C] + RowIndex
	 * 
	 * 		   JCB的场合
	 * 			jcbList.add [C] + RowIndex
	 * 
	 * 	   国内支出的场合 payMethod in {3.国内现金 4.国内信用卡1 5.国内信用卡2 6.花呗白条等}
	 *       costName  → [L] + RowIndex
	 * 		 costMoney → [M] + RowIndex
	 *       payMethod → [N] + RowIndex
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
            int startWriteRowIndexForCHout = 0;
            int startWriteRowIndexForIncome = 0;
            int startWriteRowIndexBefore_forStep6 = 0;
            
            int insertRowIndex = 0;
            int insertRowCounts = 0;
            
            // 入账件数
            int inOrOut_in_count = (int)mockDataList.stream().filter(obj -> obj.getIncomeMoney() != 0).count();
            // 日本花销件数
            int inOrOut_out_Jp_count = (int)mockDataList.stream()
            		.filter(obj -> obj.getCostMoney() != 0)
            		.filter(obj -> obj.getPayMethod() == "1" || obj.getPayMethod() == "2")
            		.count();
            // 国内花销件数
            int inOrOut_out_Ch_count = (int)mockDataList.stream()
            		.filter(obj -> obj.getCostMoney() != 0)
            		.filter(obj -> obj.getPayMethod() != "1" && obj.getPayMethod() != "2")
            		.count();
            
            int temp_insertRowCounts = inOrOut_in_count > inOrOut_out_Jp_count ? inOrOut_in_count : inOrOut_out_Jp_count;
			insertRowCounts = temp_insertRowCounts > inOrOut_out_Ch_count ? temp_insertRowCounts : inOrOut_out_Ch_count;
			
			boolean isFirstRecord = true;
			
			// TODO
			if (mockDataList.isEmpty()) {
				// TODO
				
				return;
			}

			 /*   ----- 公式用数组创建 -----
			 *    For day m tol :日本现金场合 jpXmoneyList
			 *    For m1        :日本JCB场合 jcbList
			 *    For m2        :4.国内信用卡1 }
			 *                   5.国内信用卡2 } → chTzList 
			 *                   6.花呗白条等  }
			 *    For m3        :收入 日本收入的场合 jpInmoneyList
			 *    For m4        :支出 3.国内现金 chXmoneyList
			 *                   收入 国内收入的场合 chInmoneyList
			 *    For Diff      :累加对象的场合 isSumRecordList
			 *   ----- 公式用数组创建 -----*/
			List<String> jpXmoneyList = new ArrayList<String>();
			List<String> jcbList = new ArrayList<String>();
			List<String> chTzList = new ArrayList<String>();
			List<String> jpInmoneyList = new ArrayList<String>();
			List<String> chXmoneyList = new ArrayList<String>();
			List<String> chInmoneyList = new ArrayList<String>();
			List<String> isSumRecordList = new ArrayList<String>();
			
			// memo用
			StringBuilder memoSb = new StringBuilder();
			
            for (mockData mockData : mockDataList) {
            	String writeTime = mockData.getWriteTime();
      
                /* 1.找到当天日期的起始行
                 * 2.删除当天日期之前记录的条数，重新写入
                 * 
                 * 找到起始行之后其它数据index循环+1
                 * 只有第一条时做删除操作，之后不做
                 * 
                 */
                if (isFirstRecord) {
                	// 遍历行
                    for (Row row : sheet) {
                    	Cell cell = row.getCell(colIndex);
                    	
                    	if (cell != null) {
                    		// 获取单元格数据
                    		cell.setCellType(CellType.STRING);
                    		
                            String cellValue = cell.getStringCellValue();
                            
                            // 记录对象匹配excel日期第一条
                            if (writeTime.equals(cellValue)) {
                            	startWriteRowIndex = row.getRowNum();
								startWriteRowIndexForCHout = startWriteRowIndex;
								startWriteRowIndexForIncome = startWriteRowIndex;
								startWriteRowIndexBefore_forStep6 = startWriteRowIndex;
                            	break;
                            }
                    	}
                    }
                	
                    // 删除当日既存旧数据
                	deleteOldRows(sheet, startWriteRowIndex, writeTime);
                	
                	// 插入当日对象行数
                	insertNewRows(sheet, startWriteRowIndex, insertRowCounts - 1);
                	
                    isFirstRecord = false;
                }
                
                String costName = mockData.getCostName();
                Integer costMoney = mockData.getCostMoney();
                String costType = mockData.getCostType();
                String payMethod = mockData.getPayMethod();
                boolean isDifferenceObject = mockData.isDifferenceObject();
                String memo = mockData.getMemo();
                String incomeName = mockData.getIncomeName();
                Integer incomeMoney = mockData.getIncomeMoney();
                String incomeType = mockData.getIncomeType();
                
                // 获取当前要写入行
                Row row = sheet.getRow(startWriteRowIndex);
                
                // 支出的场合 costMoney > 0

                if(costMoney > 0) {
                	// 日本支出的场合 costType in {1.日本现金 2.JCB信用卡}
                	if (payMethod=="1" || payMethod=="2") {
                		/*
                		 * 	costName  → [B] + RowIndex
                		 *  costMoney → [C] + RowIndex
                		 * 	costType  → [D] + RowIndex
                		 */
                		setCellValue(row,1,costName);
                		setCellValue(row,2,costMoney);
                		setCellValue(row,3,costType);
//                		row.getCell(1).setCellValue(costName);
//                		row.getCell(2).setCellValue(costMoney);
//                		row.getCell(3).setCellValue(costType);
                		
                		 /* 日本现金的场合
                		 * 	  jpXmoneyList.add [C] + RowIndex
                		 *          
                		  *           ※ 累加对象的场合
                		 *    isSumRecordList.add [C] + RowIndex
                		 * 
                		 * JCB的场合
                		 * 	  jcbList.add [C] + RowIndex
                		 */
                		
                		// 日本现金的场合
                		if(payMethod=="1") {
                		  jpXmoneyList.add("C" + (startWriteRowIndex + 1));
                		  
                		  // 累加对象的场合
                		  if(isDifferenceObject) {
                			  isSumRecordList.add("C" + (startWriteRowIndex + 1));
                		  }
                		} else {
                		  // JCB的场合
                		  jcbList.add("C" + (startWriteRowIndex + 1));
                		}
                	} else {
                		// 国内支出的场合
                		
	            		 /* 国内支出的场合 costType in {3.国内现金 4.国内信用卡1 5.国内信用卡2 6.花呗白条等}
	            		 *       costName  → [L] + RowIndex
	            		 * 		 costMoney → [M] + RowIndex
	            		 *       payMethod  → [N] + RowIndex
	            		 *       
	            		 * 	  ※ [3.国内现金]的场合
	            		 * 			chXmoneyList.add [M] + RowIndex
	            		 * 	   [4.国内信用卡1 5.国内信用卡2 6.花呗白条等]的场合
	            		 * 			chTzList.add [M] + RowIndex
	            		 *
	            		 */
	                		
	            		 /*   costName  → [L] + RowIndex
	           		     * 	  costMoney → [M] + RowIndex
	           		     *    payMethod  → [N] + RowIndex
	           		     */
//	            		row.getCell(11).setCellValue(costName);
//	            		row.getCell(12).setCellValue(costMoney);
//	            		row.getCell(13).setCellValue(payMethod);
                		
                		// 重新获取第一行，因为income也要从第一行开始记录
                		row = sheet.getRow(startWriteRowIndexForCHout);
	            		
	            		setCellValue(row,11,costName);
                		setCellValue(row,12,costMoney);
                		setCellValue(row,13,payMethod);
	            		
	            		//  [3.国内现金]的场合
	            		if(payMethod == "3") {
	            			chXmoneyList.add("M" + (startWriteRowIndexForCHout + 1));
	            		}else {
	            			// [4.国内信用卡1 5.国内信用卡2 6.花呗白条等]的场合
	            			chTzList.add("M" + (startWriteRowIndexForCHout + 1));
	            		}
	            		
	            		startWriteRowIndexForCHout++;
                	}
                	
                } else {
	                 // 收入的场合
	                	
	               	 /*   收入的场合 incomeMoney > 0
	            	 * 		 incomeName  → [H] + RowIndex
	            	 * 		 incomeMoney → [I] + RowIndex
	            	 * 		 incomeType  → [J] + RowIndex
	            	 * 
	            	 * 		 ※  日本收入的场合 incomeType in {1.日本工资}
	            	 * 			jpInmoneyList.add [I] + RowIndex
	            	 *		   国内收入的场合 incomeType not in {1.日本工资}
	            	 * 			chInmoneyList.add [I] + RowIndex
	            	 */
                	
                	// 重新获取第一行，因为income也要从第一行开始记录
            		row = sheet.getRow(startWriteRowIndexForIncome);
                	
                	setCellValue(row,7,incomeName);
            		setCellValue(row,8,incomeMoney);
            		setCellValue(row,9,incomeType);
            		
            		// 日本收入的场合 incomeType in {1.日本工资}
            		if(incomeType == "1") {
            			jpInmoneyList.add("I" + (startWriteRowIndexForIncome + 1));
            		}else {
            			// 国内收入的场合 incomeType not in {1.日本工资}
            			chInmoneyList.add("I" + (startWriteRowIndexForIncome + 1));
            		}
            		
            		startWriteRowIndexForIncome++;
                }
                
                
                startWriteRowIndex++;
                
                System.out.println(startWriteRowIndex);
                
                memoSb.append(memo).append("\r\n");
            }
            
	       	 /* 6.单元格合并
	    	 *   3000       E列合并
	    	 *   day m tol  F列合并
	    	 *   diff       G列合并
	    	 *   m1         O列合并
	    	 *   m2         P列合并
	    	 *   m3         Q列合并
	    	 *   m4         R列合并
	    	 *   memo       S列合并
	    	 *   
	    	  *       ※ 合并行数为当日对象行数
	    	 */
             mergedRegion(sheet,
            		 startWriteRowIndexBefore_forStep6,
            		 startWriteRowIndexBefore_forStep6+insertRowCounts-1);
            
            // 7.上记6中合并的单元格中，写入对应公式
        	/*   ----- 公式用数组创建 -----
        	 *    For day m tol :日本现金场合 jpXmoneyList
        	 *    For m1        :日本JCB场合 jcbList
        	 *    For m2        :4.国内信用卡1 }
        	 *                   5.国内信用卡2 } → chTzList 
        	 *                   6.花呗白条等  }
        	 *    For m3        :收入 日本收入的场合 jpInmoneyList
        	 *    For m4        :支出 3.国内现金 chXmoneyList
        	 *                   收入 国内收入的场合 chInmoneyList
        	 *    For Diff      :累加对象的场合 isSumRecordList
        	 *   ----- 公式用数组创建 -----
        	 */
             writeFormulaToRow(sheet,startWriteRowIndexBefore_forStep6,
            		 jpXmoneyList,jcbList,chTzList,jpInmoneyList,chXmoneyList,chInmoneyList,isSumRecordList);
             
             // 写入memo
             setCellValue(sheet.getRow(startWriteRowIndexBefore_forStep6), 18, memoSb.toString());
            
             // 强制公式生效，不然tool写入的公式需要点击一下cell才生效
             sheet.setForceFormulaRecalculation(true);
             
			// 8.导出excel到modified路径
			try (FileOutputStream out = new FileOutputStream(modifiedFilePath)) {
				workbook.write(out);
			}
			
			workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/*   ----- 公式用数组创建 -----
	 *    For day m tol :日本现金场合 jpXmoneyList
	 *    For m1        :日本JCB场合 jcbList
	 *    For m2        :4.国内信用卡1 }
	 *                   5.国内信用卡2 } → chTzList 
	 *                   6.花呗白条等  }
	 *    For m3        :收入 日本收入的场合 jpInmoneyList
	 *    For m4        :支出 3.国内现金 chXmoneyList
	 *                   收入 国内收入的场合 chInmoneyList
	 *    For Diff      :累加对象的场合 isSumRecordList
	 *   ----- 公式用数组创建 -----
	 */
	static void writeFormulaToRow(XSSFSheet sheet,Integer rowIndex,
			List<String> jpXmoneyList,List<String> jcbList,List<String> chTzList,
			List<String> jpInmoneyList,List<String> chXmoneyList,List<String> chInmoneyList,
			List<String> isSumRecordList) {
		Row row = sheet.getRow(rowIndex);
		
		String strjpXmoneyList = transListToStringFormula(jpXmoneyList);
        String strjcbList = transListToStringFormula(jcbList);
        String strchTzList = transListToStringFormula(chTzList);
        String strjpInmoneyList = transListToStringFormula(jpInmoneyList);
        String strchXmoneyList = transListToStringFormula(chXmoneyList);
        String strchInmoneyList = transListToStringFormula(chInmoneyList);
        String strisSumRecordList = transListToStringFormula(isSumRecordList);
		
        // For day m tol :日本现金场合
		writeFormulaToCell(row, 5, strjpXmoneyList);
		// For Diff
		writeFormulaToCell(row, 6, ("E" + (rowIndex + 1)) + "-" + strisSumRecordList);
		// For m1  上一行的m1 + 当日JCB付款
		writeFormulaToCell(row, 14, ("O" + (rowIndex)) + "+" + strjcbList);
		// For m2  上一行的m2 + 当日国内信用卡等付款
		writeFormulaToCell(row, 15, ("P" + (rowIndex)) + "+" + strchTzList);
		// For m3  上一行的m3 - 当日日本花费 + 当日日本入账
		writeFormulaToCell(row, 16, ("Q" + (rowIndex)) + "-" +
									("F" + (rowIndex + 1)) + "+" +
									strjpInmoneyList);
		// For m4  上一行的m4 - 当日国内现金花费 + 当日国内入账
		writeFormulaToCell(row, 17, ("R" + (rowIndex)) + "-" + 
									strchXmoneyList + "+" + 
									strchInmoneyList);		
		
	}
	
	static void writeFormulaToCell(Row row,Integer columnIndex, String value) {
		if(row.getCell(columnIndex) == null) {
			row.createCell(columnIndex).setCellFormula(value);
		} else {
			row.getCell(columnIndex).setCellFormula(value);
		}
	}

    // 7.上记6中合并的单元格中，写入对应公式
	/*   ----- 公式用数组创建 -----
	 *    For day m tol :日本现金场合 jpXmoneyList
	 *    For m1        :日本JCB场合 jcbList
	 *    For m2        :4.国内信用卡1 }
	 *                   5.国内信用卡2 } → chTzList 
	 *                   6.花呗白条等  }
	 *    For m3        :收入 日本收入的场合 jpInmoneyList
	 *    For m4        :支出 3.国内现金 chXmoneyList
	 *                   收入 国内收入的场合 chInmoneyList
	 *    For Diff      :累加对象的场合 isSumRecordList
	 *   ----- 公式用数组创建 -----
	 */
	static String transListToStringFormula(List<String> moneyList) {
		StringBuilder strReturn = new StringBuilder();
		
		strReturn = strReturn.append("(");
		
		for(int i = 0; i<moneyList.size(); i++) {
			
			strReturn = strReturn.append(moneyList.get(i));
			
			if(i != moneyList.size() - 1) {
				strReturn = strReturn.append("+");
			}
		}
		
		strReturn = strReturn.append(")");
		
		return strReturn.toString();
	}

  	 /* 6.单元格合并
	 *   3000       E列合并
	 *   day m tol  F列合并
	 *   diff       G列合并
	 *   m1         O列合并
	 *   m2         P列合并
	 *   m3         Q列合并
	 *   m4         R列合并
	 *   memo       S列合并
	 *   
	  *       ※ 合并行数为当日对象行数
	 */
	static void mergedRegion(XSSFSheet sheet, int firstRow, int lastRow) {
		// 3000 E列合并
        mergedRegion(sheet, firstRow, lastRow, 4, 4);
        // day m tol  F列合并
        mergedRegion(sheet, firstRow, lastRow, 5, 5);
        // diff       G列合并
        mergedRegion(sheet, firstRow, lastRow, 6, 6);
        
        // m1         O列合并
        mergedRegion(sheet, firstRow, lastRow, 14, 14);
        // m2         P列合并
        mergedRegion(sheet, firstRow, lastRow, 15, 15);
        // m3         Q列合并
        mergedRegion(sheet, firstRow, lastRow, 16, 16);
        // m4         R列合并
        mergedRegion(sheet, firstRow, lastRow, 17, 17);
        
        // memo       S列合并
        mergedRegion(sheet, firstRow, lastRow, 18, 18);
	}
	
	static void mergedRegion(XSSFSheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn));
	}
	
	static void setCellValue(Row row,Integer columnIndex, String value) {
		if(row.getCell(columnIndex) == null) {
			row.createCell(columnIndex).setCellValue(value);
		} else {
			row.getCell(columnIndex).setCellValue(value);
		}
	}
	
	static void setCellValue(Row row,Integer columnIndex, Integer value) {
		if(row.getCell(columnIndex) == null) {
			row.createCell(columnIndex).setCellValue(value);
		} else {
			row.getCell(columnIndex).setCellValue(value);
		}
	}
	
	// 插入对象行数-1 因为之前删除的时候留了一行
	static void insertNewRows(XSSFSheet sheet, int startWriteRowIndex, int insertRowCounts) {
		Row sourceRow = sheet.getRow(startWriteRowIndex);
		
		// 先把下一个日期留出一行
		sheet.shiftRows(startWriteRowIndex + 1, sheet.getLastRowNum(), 1);
		
		for (int j = startWriteRowIndex; j < startWriteRowIndex + insertRowCounts; j++) {
			
	        Row targetRow = sheet.createRow(j+1);
	        
	        // 复制行的样式
            ExcelUtils.copyRowStyle(sourceRow, targetRow);

            // 复制单元格
            for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
                Cell sourceCell = sourceRow.getCell(i);
                Cell targetCell = targetRow.getCell(i);
                if (targetCell == null) {
                    targetCell = targetRow.createCell(i);
                }
                ExcelUtils.copyCell(sourceCell, targetCell);
            }
            
            if (j != startWriteRowIndex + insertRowCounts - 1) {
            	sheet.shiftRows(j + 2, sheet.getLastRowNum(), 1);
            }
		}
	}	
	
	// 删除执行日期既存的数据，只保留一行作为拷贝元
	static void deleteOldRows(XSSFSheet sheet, int startWriteRowIndex, String writeTime) {
		int blankRowsCount = 0;
		
		// 遍历行
        for (int i = 0; i<sheet.getLastRowNum(); i++) {
        	Row row = sheet.getRow(i);
        	
        	if (i <= startWriteRowIndex) {
        		continue;
        	}
        	
        	Cell cell = row.getCell(0);
        	
        	if (cell != null) {
        		// 获取单元格数据
        		cell.setCellType(CellType.STRING);
        		
                String cellValue = cell.getStringCellValue();
                
                if (writeTime.equals(cellValue)) {
                	// removeRow方法有bug，删除后行依然存在 只是清除里面的内容
                	// sheet.removeRow(row);
                	// 真正要彻底删掉Row，不是用removeRow，而是用shiftRows，即将后面的行往上移
//                	sheet.shiftRows(i+1, sheet.getLastRowNum(), -1);
                	
                	// 行删除
                	sheet.removeRow(sheet.getRow(i));
                	
					blankRowsCount++;
                }
        	}
        }
        
		sheet.shiftRows(startWriteRowIndex + blankRowsCount + 1, sheet.getLastRowNum(), -blankRowsCount);
	}
	
	static List<mockData> makeMockData() {
		List<mockData> mockDataList = new ArrayList<mockData>();


		mockData mockData1 = new mockData(true, "2024/4/1", "morningFD1", 501, "10", "1", true, "memotest",
				"incomeName1", 0, "1");
		mockData mockData2 = new mockData(false, "2024/4/1", "morningFD2", 502, "11", "2", true, "memotest",
				"incomeName2", 0, "1");
		mockData mockData11 = new mockData(true, "2024/4/1", "morningFD1", 501, "10", "1", true, "memotest",
				"incomeName11", 0, "1");
		mockData mockData22 = new mockData(false, "2024/4/1", "morningFD2", 502, "11", "2", true, "memotest",
				"incomeName22", 0, "1");
		
		mockData mockData3 = new mockData(true, "2024/4/1", "morningFD3", 503, "1", "3", false, "memotest",
				"incomeName3", 0, "1");
		mockData mockData4 = new mockData(true, "2024/4/1", "morningFD4", 504, "1", "4", true, "memotest",
				"incomeName4", 0, "1");
		
		mockData mockData5 = new mockData(true, "2024/4/1", "morningFD4", 0, "1", "4", true, "memotest",
				"incomeName5", 70000, "1");
		mockData mockData6 = new mockData(true, "2024/4/1", "morningFD4", 0, "1", "4", true, "memotest",
				"incomeName6", 50000, "2");
		mockData mockData7 = new mockData(true, "2024/4/1", "morningFD4", 0, "1", "4", true, "memotest",
				"incomeName7", 150000, "3");

		mockDataList.add(mockData1);
		mockDataList.add(mockData2);
		mockDataList.add(mockData11);
		mockDataList.add(mockData22);
		
		mockDataList.add(mockData3);
		mockDataList.add(mockData4);
		
		mockDataList.add(mockData5);
		mockDataList.add(mockData6);
		mockDataList.add(mockData7);

		return mockDataList;
	}

}
