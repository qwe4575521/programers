package _2017_;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class 禁用龚思南测试库 {
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");

        DataSource dataSource1 = (DataSource) context.getBean("formDateSource");
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(dataSource1);
        String dataBaseTo = "DC_CLOUD1";
        String sql="";
        sql = "select name,id from " + dataBaseTo + ".SYSDBA.SYSTABLES WHERE schid='150995946'   ";
        List<Map<String, Object>> list=jdbcTemplate1.queryForList(sql);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map= list.get(i);
            String id= map.get("id").toString();
            String name = map.get("name").toString();
            //获取表下的所有建表语句
           /* sql = "SELECT TABLEDEF('" + dataBaseTo + "', 'SG_DATACENTER', '" + name + "') as name from sysdual";
            List<Map<String, Object>> creatView = jdbcTemplate1.queryForList(sql);
            String creatViewName = creatView.get(0).get("name").toString();
            System.out.println("");
            System.out.println(name+"建表语句-------------------------------------------");
            System.out.println("select * from "+dataBaseTo+".SG_DATACENTER."+name+";");
            System.out.println(creatViewName+";");
            System.out.println("-----------------------------------------------");*/
                sql = "select name from " + dataBaseTo + ".SYSDBA.SYSCONSTRAINTS where tableid='" + id + "' and type!='P' ";
                try {
                    List<Map<String, Object>> list2 = jdbcTemplate1.queryForList(sql);
                    for (int i1 = 0; i1 < list2.size(); i1++) {
                        Map<String, Object> map2 = list2.get(i1);
                        String indexname = map2.get("name").toString();
                        sql = "ALTER TABLE " + dataBaseTo + ".SG_DATACENTER." + name + "  DISABLE  CONSTRAINT " + indexname + ";";
    //                      sql="ALTER TABLE "+dataBaseFrom+".SG_DATACENTER."+Tablename+"  ENABLE  CONSTRAINT "+indexname+";";
                        System.out.println(sql);
                    }
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }


           //     System.out.println("end-----------------------------------------------");

        }


    }
}
