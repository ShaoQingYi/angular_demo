package test0409;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
 
public class LogToFile {
    public static final Logger logger = Logger.getLogger(LogToFile.class.getName());
    
    public static void initLogger() {
    	// TODO
    	String logPath = "C:\\\\Users\\\\INNOX-002\\\\Desktop\\\\shao\\\\forEclipse\\\\workspace\\\\log.log";
    	
    	// 配置Logger输出到指定文件
        FileHandler fileHandler;
        try {
        	// 每次清空文件再出力log
        	Files.newOutputStream(Paths.get(logPath)).close();
        	
        	// 建立文件
            fileHandler = new FileHandler(logPath, true);
            logger.addHandler(fileHandler);
            
            // 设置出力format
            CustomFormatter formatter = new CustomFormatter();
            fileHandler.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            logger.severe("Error occurred while setting up logger: " + e.getMessage());
        }
    }
    
    // 自定义输出样式
    static class CustomFormatter extends Formatter{
    	@Override
    	public String format(LogRecord record) {
			return record.getMessage() + System.lineSeparator();
    	}
    }
}
