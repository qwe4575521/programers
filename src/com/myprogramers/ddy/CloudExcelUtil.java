package myprogramers.ddy;


import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CloudExcelUtil {
    private String filename;
    private String localpath;

    public CloudExcelUtil(String filename, String localpath) {
        this.filename = filename;
        this.localpath = localpath;
    }

    public void setImpExcel() throws FileNotFoundException {
        if (StringUtils.isNotEmpty(filename)) {
            File file = new File(localpath + filename);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                if (filename.toLowerCase().endsWith(".xls")) {
                    implExcelxls(fileInputStream);
                } else if (filename.toLowerCase().endsWith(".xlsx")) {
                    implExcelxls2007(fileInputStream);
                }
            }
        } else {
            throw new NullPointerException("文件名称不能为空");
        }
    }

    private void implExcelxls2007(FileInputStream fileInputStream) {
        try {
            XSSFWorkbook xssfSheets = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = xssfSheets.getSheet(xssfSheets.getSheetName(0));
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                XSSFRow sheetRow = sheet.getRow(i);
                int cellNum = sheetRow.getLastCellNum();
                for (int i1 = 0; i1 < cellNum; i1++) {
                    Cell cell = sheetRow.getCell(i1);
                    String cellValue = this.getCellValue(cell);
                    System.out.println(cellValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void implExcelxls(FileInputStream fileInputStream) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                int lastCellNum = row.getLastCellNum();
                for (int i1 = 0; i1 < lastCellNum; i1++) {
                    HSSFCell cell = row.getCell(i1);
                    String cellValue = this.getCellValue(cell);
                    System.out.println(cellValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //判断从Excel文件中解析出来数据的格式
    public String getCellValue(Cell cell) {
        String value = null;
        //简单的查检列类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING://字符串
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC://数字
                if ("yyyy/mm;@".equals(cell.getCellStyle().getDataFormatString()) || "m/d/yy".equals(cell.getCellStyle().getDataFormatString())
                        || "yy/m/d".equals(cell.getCellStyle().getDataFormatString()) || "mm/dd/yy".equals(cell.getCellStyle().getDataFormatString())
                        || "yyyy-mm-dd".equals(cell.getCellStyle().getDataFormatString()) || "yyyy/mm/dd".equals(cell.getCellStyle().getDataFormatString())
                        || "dd-mmm-yy".equals(cell.getCellStyle().getDataFormatString()) || "yyyy/m/d".equals(cell.getCellStyle().getDataFormatString())) {
                     value = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    long dd = (long) cell.getNumericCellValue();
                    value = dd + "";
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BOOLEAN://boolean型值
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                break;
            default:
                break;
        }
        return value;
    }


}
