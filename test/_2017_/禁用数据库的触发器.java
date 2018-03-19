package _2017_;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class 禁用数据库的触发器 {
    //禁用39的sg_datacenter触发器
    //
    // select * from "HD_DB"."SYSDBA"."SYSSCHEMAS";
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource toDateSource39 = (DataSource) context.getBean("toDateSource");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(toDateSource39);
        //先查看SYSSCHEMAS模式下的schid，然后在找出表的触发器，进行关闭；
        String dataCloud = "HD_DB";
        String schiname = "sg_datacenter".toUpperCase().trim();
        String sql = "select schid,name from " + dataCloud + ".SYSDBA.SYSSCHEMAS where name=?";
        String schid = jdbcTemplate.queryForList(sql, new Object[]{schiname}).size() != 0 ? jdbcTemplate.queryForList(sql, new Object[]{schiname}).get(0).get("SCHID").toString() : "";
        if (!"".equals(schid)) {
             sql="select name,id from "+dataCloud+".SYSDBA.SYSTABLES where schid=?";
            List<Map<String, Object>> alltable=jdbcTemplate.queryForList(sql,new Object[]{schid});
            for (int i = 0; i < alltable.size(); i++) {
                Map<String,Object> tableMap= alltable.get(i);
                String tablename=tableMap.get("NAME").toString();
                String tablenid=tableMap.get("ID").toString();
                sql="select name from "+dataCloud+".SYSDBA.SYSTRIGGERS where tableid=?";
                List<Map<String,Object>> triggersList= jdbcTemplate.queryForList(sql,new Object[]{tablenid});
                for (int i1 = 0; i1 < triggersList.size(); i1++) {
                    Map<String,Object> triggerMap=triggersList.get(i1);
                    String triggername=triggerMap.get("NAME").toString();
                    String result="ALTER TRIGGER "+dataCloud+"."+schiname+"."+triggername+" DISABLE;";
                    System.out.println(result);
                }
            }



        }


    }


}
