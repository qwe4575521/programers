package _2018_02_07;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.*;

public class 合理性校验_公共一次 {
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("formDateSource");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String path = "C:\\Users\\xucl\\Desktop\\自动化合理性问题\\";
        String filename = "合理性校验_公共一次.xlsx";
        OutputStream out = null;
        try {
            out = new FileOutputStream("C:\\Users\\xucl\\Desktop\\自动化合理性问题\\合理性校验_公共一次_修改.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        XSSFWorkbook xssfWorkbook = null;
        try {
            fileInputStream = new FileInputStream(path + filename);
            xssfWorkbook = new XSSFWorkbook(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSheet(xssfWorkbook, "变电站监控系统不允许为零条", jdbcTemplate);
        try {
            xssfWorkbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSheet(XSSFWorkbook xssfWorkbook, String sheetname, JdbcTemplate jdbcTemplate) {
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetname);
        int row = sheet.getLastRowNum();
        int coloum = sheet.getRow(1).getLastCellNum();
        //System.out.println(row + ";" + coloum);
        String sql;
        for (int i = 1; i < row + 1; i++) {
            XSSFRow xssfRow = sheet.getRow(i);
            String tablename = getTableid(xssfRow.getCell(2).getStringCellValue(), jdbcTemplate);
            String key = xssfRow.getCell(0).getStringCellValue();
            sql = "select dcc_id from sg_datacenter.SG_CON_SUBSTATION_B b where b.id=?";
            String assets_ownership_com_id = jdbcTemplate.queryForList(sql, new Object[]{key}).get(0).get("dcc_id").toString();
            String getDw = getDw(jdbcTemplate, assets_ownership_com_id);
            String getparDw = getparDw(jdbcTemplate, assets_ownership_com_id);
            xssfRow.getCell(7).setCellValue(getDw);
            xssfRow.getCell(8).setCellValue(getparDw);
            System.out.println(xssfRow.getCell(7).getStringCellValue());
            // System.out.println(tablename + ";" + key);
        }
    }


    public static String getTableid(String tablename, JdbcTemplate jdbcTemplate) {

        String sql = "select object_code,table_name_chn,table_name_eng from sg_datacenter.SG_META_TABLE where instr(table_name_chn,?)>0";
        return jdbcTemplate.queryForList(sql, new Object[]{tablename}).get(0).get("table_name_eng").toString();
    }

    public static String getDw(JdbcTemplate jdbcTemplate, String id) {
        String sql = "select name from sg_datacenter.SG_ORG_DCC_B h where id=?";
        return jdbcTemplate.queryForList(sql, new Object[]{id}).get(0).get("name").toString();
    }
    public static String getparDw(JdbcTemplate jdbcTemplate, String id) {
        String sql = "select name from sg_datacenter.SG_ORG_DCC_B h where id IN (select parent_id from sg_datacenter.SG_ORG_DCC_B h where id=?)";
        return jdbcTemplate.queryForList(sql, new Object[]{id}).get(0).get("name").toString();
    }
}
