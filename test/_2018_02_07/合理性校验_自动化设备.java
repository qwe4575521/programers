package _2018_02_07;

import org.apache.ibatis.jdbc.SQL;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.util.List;
import java.util.Map;

public class 合理性校验_自动化设备 {
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("formDateSource40");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String path = "C:\\Users\\xucl\\Desktop\\自动化合理性问题\\";
        String filename = "自动化统计值问题2018-03-01.xlsx";
        OutputStream out = null;

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
        setSheet3(xssfWorkbook,"系统型号不能为空",jdbcTemplate);

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
            sql = "select dcc_id from \n" +
                    "(select id,name,DCC_ID from sg_datacenter.SG_CON_SUBSTATION_B\n" +
                    "union select id,name,DCC_ID from sg_datacenter.SG_CON_PLANT_B) h\n" +
                    "where h.id in (select org_id from sg_datacenter.SG_CON_SLAVEAS_B where id =?)";
            String assets_ownership_com_id = jdbcTemplate.queryForList(sql, new Object[]{key}).get(0).get("dcc_id").toString();
            String getDw = getDw(jdbcTemplate, assets_ownership_com_id);
            String getparDw = getparDw(jdbcTemplate, assets_ownership_com_id);
            xssfRow.getCell(7).setCellValue(getDw);
            xssfRow.getCell(8).setCellValue(getparDw);
         //   System.out.println(xssfRow.getCell(7).getStringCellValue());
            // System.out.println(tablename + ";" + key);
        }
    }

    public static void setSheet1(XSSFWorkbook xssfWorkbook, String sheetname, JdbcTemplate jdbcTemplate) {
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetname);
        int row = sheet.getLastRowNum();
        //  int coloum = sheet.getRow(1).getLastCellNum();
        //System.out.println(row + ";" + coloum);
        String sql;
        for (int i = 1; i < row + 1; i++) {
            XSSFRow xssfRow = sheet.getRow(i);
            String tablename = getTableid(xssfRow.getCell(2).getStringCellValue(), jdbcTemplate);
            String key = xssfRow.getCell(0).getStringCellValue();
            sql="select runing_state from sg_datacenter."+tablename+" where id='"+key+"';";
            //System.out.println(sql);
            sql="update sg_datacenter."+tablename+" set runing_state='1003' where id='"+key+"';";
            System.out.println(sql);

            // System.out.println(tablename + ";" + key);
        }
    }


    public static void setSheet2(XSSFWorkbook xssfWorkbook, String sheetname, JdbcTemplate jdbcTemplate) {
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetname);
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("kindbase");
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(dataSource);
    String SQL="";
        int row = sheet.getLastRowNum();
        for (int i = 1; i < row + 1; i++) {
            XSSFRow xssfRow = sheet.getRow(i);
            String tablename = getTableid(xssfRow.getCell(2).getStringCellValue(), jdbcTemplate);
            String key = xssfRow.getCell(0).getStringCellValue();
            SQL="select model_id from sg_datacenter."+tablename+" where id=?";
            //System.out.println(jdbcTemplate1.queryForList(SQL, new Object[]{key}).size()!=0?jdbcTemplate1.queryForList(SQL, new Object[]{key}).get(0):"");
            List<Map<String, Object>> list = jdbcTemplate1.queryForList(SQL, new Object[]{key});
            if (list.size()>0) {
                if (list.get(0).get("model_id")!=null) {
                    String model_id = list.get(0).get("model_id").toString();
                    SQL="UPDATE sg_datacenter."+tablename+" SET model_id=? WHERE  id=?";
                    try {
                       // jdbcTemplate.update(SQL,new Object[]{model_id,key});
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                }



            }

        }


    }



    public static void setSheet3(XSSFWorkbook xssfWorkbook, String sheetname, JdbcTemplate jdbcTemplate) {
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetname);
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("kindbase");
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(dataSource);
        String SQL="";
        int row = sheet.getLastRowNum();
        for (int i = 1; i < row + 1; i++) {
            XSSFRow xssfRow = sheet.getRow(i);
            String tablename = getTableid(xssfRow.getCell(2).getStringCellValue(), jdbcTemplate);
            String key = xssfRow.getCell(0).getStringCellValue();
           // SQL="select model_id from sg_datacenter."+tablename+" where id=?";
            SQL="select p.name from sg_datacenter.SG_CON_SLAVEAS_B b, sg_datacenter.SG_CON_SLAVEAS_P p where b.id=? and p.id=b.model_id";
            List<Map<String, Object>> list = jdbcTemplate1.queryForList(SQL, new Object[]{key});
            if (list.size()>0) {
                if (list.get(0).get("name")!=null) {
                    String name = list.get(0).get("name").toString();
                    SQL="select id from sg_datacenter.SG_CON_SLAVEAS_P where name like '%"+name+"%'";
                    List<Map<String, Object>> list1 = jdbcTemplate.queryForList(SQL, new Object[]{});
                    if (list1.size()!=0) {
                        Map<String, Object> map = list1.get(0);
                        String id = map.get("id").toString();
                        SQL="UPDATE sg_datacenter."+tablename+" SET model_id='"+id+"' WHERE  id='"+key+"';";
                        System.out.println(SQL);


                    }

                }



            }

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
