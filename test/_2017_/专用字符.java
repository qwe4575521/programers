package _2017_;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public class 专用字符 {
    public static void main(String[] args) {
        File file=new File("C:\\Users\\xucl\\Desktop\\重复变电站.xls");
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        DataSource tokindbase = (DataSource) context.getBean("kindbase");
        JdbcTemplate template=new JdbcTemplate(tokindbase);
        //保护设备
        String sql="";
        Sheet sheet = workbook.getSheet(0);
        System.out.println(sheet.getRows());
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        int colums = sheet.getColumns();
        for (int i = 1; i < rows; i++) {
            for (int i1 = 0; i1 < colums; i1++) {
                Cell cell = sheet.getCell(i1, i);
                if(i1==0){
                    System.out.print( cell.getContents());
                }if(i1==1){

                    System.out.print("\t" + cell.getContents());
                }
                if(i1==2){
                    sql="select name from APPFRAME.T_AF_TREE where treetype=1 and id=?" +
                            " UNION select deptname from equip.v_base_unit_all where deptcode=?";
               String names=     template.queryForList(sql,new Object[]{cell.getContents(),cell.getContents()}).size()!=0?template.queryForList(sql,new Object[]{cell.getContents(),cell.getContents()}).get(0).get("name").toString():cell.getContents();
                   System.out.print("\t" + names);
                }if(i1==3){
                    sql="select NAME from SG_DATACENTER.SG_DIC_VOLTAGETYPE where CODE=?";
                    String names=     template.queryForList(sql,new Object[]{cell.getContents()}).size()!=0?template.queryForList(sql,new Object[]{cell.getContents()}).get(0).get("NAME").toString():cell.getContents();
                    System.out.println("\t" + names);
                }



            }


        }

    }
}
