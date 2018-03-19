package _2017_;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class 查询线路下表的数据那些有差 {
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource= (DataSource) context.getBean("treeJxk");
        String sql="select substr(name,instr(name,'.')+1) as NAME,NAME AS ALLNAME from D5000.ACLINESEGMENT";
        JdbcTemplate template=new JdbcTemplate(dataSource);
        List<Map<String, Object>> list=template.queryForList(sql);
        DataSource dataSource1= (DataSource) context.getBean("formDateSource");
        JdbcTemplate template1=new JdbcTemplate(dataSource1);
        int count=0;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map= list.get(i);
            String name=map.get("NAME").toString();
            String ALLNAME=map.get("ALLNAME").toString();
            sql="SELECT count(*) FROM dc_cloud1.sg_datacenter.SG_DEV_ACLINE_B WHERE instr(NAME,?)>0";
            int i1 = template1.queryForList(sql,new Object[]{name}).size();
            if (i1==0){
                count++;
                System.out.println(count+";"+ALLNAME + " 未匹配");
            }


        }






    }
}
