package _2017_;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public class 更新统计值校验数据问题 {

    public static void main(String[] args) throws IOException, BiffException {

        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("formDateSource");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        /**
         * select object_code,table_name_chn,table_name_eng from sg_datacenter.SG_META_TABLE where table_name_chn='发电厂电压等级统计信息';
         select code,name_chn,name_eng from sg_datacenter.SG_META_OBJECT where code='0111';(作废)
         select table_name_eng,property_name_chn,property_name_eng,"CONSTRAINT" as pk  from sg_datacenter.SG_META_PROPERTY where table_name_eng='SG_CON_PLANT_S_VOLTAGETYPE';
         select code,name from sg_datacenter.SG_DIC_VOLTAGETYPE ;
         *
         * */
        String filepath="C:\\Users\\xucl\\Desktop\\统计\\";
        String[] filename ={"统计值校验.xls"};
        for (int j = 0; j < filename.length; j++) {
            File file = new File(filepath+filename[j].toString());
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
           // System.out.println(sheet.getColumns());
            int rows = sheet.getRows();
            int colums = sheet.getColumns();
            for (int i = 1; i < rows; i++) {
           /* for (int i1 = 0; i1 < colums; i1++) {
                Cell cell = sheet.getCell(i1, i);
                System.out.println(cell.getContents());

            }*/
                String id = sheet.getCell(0, i).getContents();
                String duixiang = sheet.getCell(1, i).getContents();
                String tablename = sheet.getCell(2, i).getContents();
                String stationname = sheet.getCell(3, i).getContents();
                String coloumname = sheet.getCell(4, i).getContents();
                String currentnum = sheet.getCell(5, i).getContents();
                String jsnum = sheet.getCell(6, i).getContents();
                // System.out.println(id + ";" + duixiang + ";" + tableid + ";" + stationname + ";" + coloumname + ";" + currentnum + ";" + jsnum);
                //System.out.println(getTableid(tablename, jdbcTemplate));
                //  System.out.println(getColomid(tablename, jdbcTemplate,coloumname));

                String sql="";
                String volteid=getVolteid(tablename,jdbcTemplate);
                if (volteid==null) {

                    sql = "update sg_datacenter."+getTableid(tablename,jdbcTemplate)+" set "+getColomid(tablename, jdbcTemplate,coloumname)+"="+jsnum+ " where id='"+id+"'  ;";

                }else{

                    sql = "update sg_datacenter."+getTableid(tablename,jdbcTemplate)+" set "+getColomid(tablename, jdbcTemplate,coloumname)+"="+jsnum+ " where id='"+id+"' and VOLTAGE_TYPE='"+getVolteid(tablename,jdbcTemplate)+"';";
                }
             //   System.out.println("----"+i+"----");
                System.out.println(sql);

            }

        }



    }

    public static String getTableid(String tablename, JdbcTemplate jdbcTemplate) {
        String sql = "select object_code,table_name_chn,table_name_eng from sg_datacenter.SG_META_TABLE where instr(?,table_name_chn)>0";
        return jdbcTemplate.queryForList(sql, new Object[]{tablename}).get(0).get("table_name_eng").toString();
    }

    public static String getVolteid(String tablename, JdbcTemplate jdbcTemplate) {
        String resutl = null;
        if (tablename.contains("(")) {
            tablename = tablename.substring(tablename.indexOf("(") + 1, tablename.indexOf(")"));
            //  System.out.println(tableid);
            String sql = "select code,name from sg_datacenter.SG_DIC_VOLTAGETYPE where instr(?,name)>0";
            resutl = jdbcTemplate.queryForList(sql, new Object[]{tablename}).get(0).get("code").toString();
        }


        return resutl;
    }

    public static String getColomid(String tablename, JdbcTemplate jdbcTemplate, String coloumname) {

        String tableid = getTableid(tablename, jdbcTemplate);
        String sql = "select table_name_eng,property_name_chn,property_name_eng,\"CONSTRAINT\" as pk  from sg_datacenter.SG_META_PROPERTY where table_name_eng=? and property_name_chn=?";
        return jdbcTemplate.queryForList(sql, new Object[]{tableid,coloumname}).get(0).get("property_name_eng").toString();
    }

}
