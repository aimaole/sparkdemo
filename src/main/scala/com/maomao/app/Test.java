package com.maomao.app;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;

public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    private static final String now = DateUtil.format(new Date(), PURE_DATETIME_PATTERN);

    public static void main(String[] args) {
        List<List<String>> data = new ArrayList<>();
        String filePath = "C:\\Users\\Administrator\\Desktop\\aa.xlsx";
        String bakFil = filePath + now;
        FileUtil.copy(filePath, bakFil, false);

        List<String> line = new ArrayList<>();
        line.add("111");
        line.add("222");
        line.add("333");
        data.add(line);
        excelAppend(filePath, "目录", data);

    }

    public static void excelAppend(String filePath, String sheetName, List<List<String>> data) {

        if (data == null || data.size() <= 0) {
            return;
        }
        logger.info("excel更新开始");

        // 1. 读取已有文件
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);
            fis.close(); // 读完后可以关闭输入流

            // 2. 获取最后一行索引并在其后追加新行
            int lastRowNum = sheet.getLastRowNum(); // 最后一行的索引（从0开始）

            // 3. 写入内容
            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                Row newRow = sheet.createRow(lastRowNum + 1 + i);
                for (int j = 0; j < row.size(); j++) {
                    newRow.createCell(j).setCellValue(row.get(j));
                }
            }

            // 4. 写回原文件
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.setForceFormulaRecalculation(true);
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            workbook.write(fos);
            Thread.sleep(1000);
            workbook.close();
            fos.close();

            logger.info("excel更新完成");

        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }


    }
}
