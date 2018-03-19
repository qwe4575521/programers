package _2017_;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by xucl on 2017-11-15.
 */
public class 数据结构校验入口 {
    public static void main(String[] args) throws IOException, BiffException {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        String sql = "";
        String dataBaseFrom = "DC_CLOUD1";
        String dataBaseTo = "HD_DB";
        DataSource dataSource = (DataSource) context.getBean("formDateSource");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        DataSource dataSource1 = (DataSource) context.getBean("toDateSource");
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(dataSource1);
        File file = new File("C:\\Users\\xucl\\Desktop\\oms与通用数据结构\\oms与通用数据结构对比【重要】.xls");
        Workbook workbook = Workbook.getWorkbook(file);

        getDisableIndex(dataBaseFrom, dataBaseTo, jdbcTemplate, jdbcTemplate1, workbook);
        //  System.out.println(System.getenv("HOME"));
        //getCheckResult(dataBaseFrom, dataBaseTo, jdbcTemplate, jdbcTemplate1, workbook);
      //  getUpdate(dataBaseFrom, dataBaseTo, jdbcTemplate, jdbcTemplate1, workbook);
    }

    private static void getCheckResult(String dataBaseFrom, String dataBaseTo, JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1, Workbook workbook) {
        String sql;
        Sheet sheet = workbook.getSheet(0);
        System.out.println(sheet.getRows());
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        int colums = sheet.getColumns();
        for (int i = 0; i < rows; i++) {
            Cell cell = sheet.getCell(1, i);
            String Tablename = cell.getContents().trim().toUpperCase();
            if (!"".equals(Tablename)) {
                sql = "select name,id from " + dataBaseFrom + ".SYSDBA.SYSTABLES WHERE  name='" + Tablename + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                if (list.size() != 1) {
                    System.out.println("表" + Tablename + "; 未有匹配到");
                } else {
                    Map<String, Object> map = list.get(0);
                    String name = map.get("name").toString();
                    String id = map.get("id").toString();
                    sql = "select name as coloums,type,length,nullable from " + dataBaseFrom + ".SYSDBA.SYSCOLUMNS WHERE id='" + id + "'";
                    //   System.out.println(sql);
                    List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
                    String paremkeys = "";
                    String alterColom = "";
                    for (int i1 = 0; i1 < list1.size(); i1++) {
                        Map<String, Object> map1 = list1.get(i1);
                        String coloums = map1.get("coloums").toString();
                        String type = "INTEGER".equals(map1.get("type").toString()) ? "INT" : map1.get("type").toString();
                        String length = map1.get("length").toString();
                        String nullable = map1.get("nullable").toString();
                        sql = "select \"" + coloums + "\" FROM " + dataBaseTo + ".SG_DATACENTER." + name + "  WHERE  rownum=1";
                        try {
                            jdbcTemplate1.queryForList(sql);
                        } catch (DataAccessException e) {
                            paremkeys += "," + coloums;
                            alterColom += "ALTER TABLE SG_DATACENTER." + name + " ADD " + coloums + " " + type + "(" + length + ")    ;\r\n";
                            // e.printStackTrace();
                        }
                    }
                    if (!"".equals(paremkeys)) {
                        // System.out.println(i+"   表：" + name + ",缺失字段为：" +paremkeys.substring(1,paremkeys.length()) );
                        System.out.println(alterColom);
                    }
                }
            }
        }
    }


    private static void getUpdate(String dataBaseFrom, String dataBaseTo, JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1, Workbook workbook) {
        String sql;
        Sheet sheet = workbook.getSheet(0);
        System.out.println(sheet.getRows());
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        int colums = sheet.getColumns();
        for (int i = 0; i < rows; i++) {
            Cell cell = sheet.getCell(1, i);
            String Tablename = cell.getContents().trim().toUpperCase();
            if (!"".equals(Tablename)) {
                sql = "select name,id from " + dataBaseFrom + ".SYSDBA.SYSTABLES WHERE   name='" + Tablename + "' AND rownum=1";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                if (list.size() != 1) {
                    // System.out.println("表" + Tablename + "; 未有匹配到");
                } else {
                    Map<String, Object> map = list.get(0);
                    String name = map.get("name").toString();
                    String id = map.get("id").toString();
                    sql = "select name as coloum from " + dataBaseFrom + ".SYSDBA.SYSCOLUMNS WHERE id='" + id + "'";
                    //   System.out.println(sql);
                    List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
                    String coloum = "";
                    for (int i1 = 0; i1 < list1.size(); i1++) {
                        Map<String, Object> map1 = list1.get(i1);
                        coloum += ",\"" + map1.get("coloum").toString() + "\"";
                    }
                    coloum = coloum.substring(1, coloum.length());
                    // System.out.println(coloum);
                    sql = "DELETE  FROM " + dataBaseFrom + ".SG_DATACENTER." + name;
                    System.out.println(sql + ";");
                    // jdbcTemplate.update(sql);
                    sql = "INSERT INTO " + dataBaseFrom + ".SG_DATACENTER." + name + " (" + coloum + ") select " + coloum + " FROM  " + dataBaseTo + ".SG_DATACENTER." + name + "";
                    // jdbcTemplate.update(sql);
                    System.out.println(sql + ";");


                }
            }
        }
    }


    private static void getDisableIndex(String dataBaseFrom, String dataBaseTo, JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1, Workbook workbook) {
        String sql;
        Sheet sheet = workbook.getSheet(0);
        System.out.println(sheet.getRows());
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        int colums = sheet.getColumns();
        for (int i = 0; i < rows; i++) {
            Cell cell = sheet.getCell(1, i);
            String Tablename = cell.getContents().trim().toUpperCase();
            if (!"".equals(Tablename)) {
                //获取表下的所有建表语句
                sql = "SELECT TABLEDEF('" + dataBaseFrom + "', 'SG_DATACENTER', '" + Tablename + "') as name from sysdual";
                List<Map<String, Object>> creatView = jdbcTemplate.queryForList(sql);
                String creatViewName = creatView.get(0).get("name").toString();
                System.out.println("");
                System.out.println(Tablename+"建表语句-------------------------------------------");
                System.out.println("select * from "+dataBaseFrom+".SG_DATACENTER."+Tablename+";");
                System.out.println(creatViewName+";");
                System.out.println("-----------------------------------------------");
                sql = "select name,id from " + dataBaseFrom + ".SYSDBA.SYSTABLES WHERE   name='" + Tablename + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                if (list.size() != 1) {
                    // System.out.println("表" + Tablename + "; 未有匹配到");
                } else {
                    Map<String, Object> map = list.get(0);
                    String name = map.get("name").toString();
                    String id = map.get("id").toString();
                    sql = "select name from " + dataBaseFrom + ".SYSDBA.SYSCONSTRAINTS where tableid='" + id + "' and type!='P' ";
                    List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
                    for (int i1 = 0; i1 < list1.size(); i1++) {
                        Map<String, Object> map1 = list1.get(i1);
                        String indexname = map1.get("name").toString();
                        sql = "ALTER TABLE " + dataBaseFrom + ".SG_DATACENTER." + Tablename + "  DISABLE  CONSTRAINT " + indexname + ";";
//                      sql="ALTER TABLE "+dataBaseFrom+".SG_DATACENTER."+Tablename+"  ENABLE  CONSTRAINT "+indexname+";";
                        System.out.println(sql);
                    }


                    System.out.println("end-----------------------------------------------");
                }
            }
        }
    }


}
