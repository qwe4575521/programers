package _2017_;

import myprogramers.util.DaoHepler;
import myprogramers.util.ListToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class 调度云从测试环境中新增的数据导入到正式环境中 {
    public static void main(String[] args) {
        //从39服务器中的监听日志找出新增加的数据并同步到40正式环境中
        /**
         * select object_code,table_name_chn,table_name_eng from sg_datacenter.SG_META_TABLE where table_name_chn=?;
         select table_name_eng,property_name_chn,property_name_eng,"CONSTRAINT" as pk  from sg_datacenter.SG_META_PROPERTY where table_name_eng=?;
         *
         * */
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSourceth39 = (DataSource) context.getBean("thDateSource");
        JdbcTemplate jdbcTemplateth39 = new JdbcTemplate(dataSourceth39);
        DataSource dataSourcedc39 = (DataSource) context.getBean("formDateSource");
        JdbcTemplate jdbcTemplatedc39 = new JdbcTemplate(dataSourcedc39);
        DataSource dataSourcedc40 = (DataSource) context.getBean("formDateSource40");
        JdbcTemplate jdbcTemplatedc40 = new JdbcTemplate(dataSourcedc40);
        DaoHepler daoHepler = new DaoHepler(dataSourcedc40);
        String sql = "select table_name, pk_name, pk_value, op_type, op_timestamp, data_source, op_seq, field_value  from datasync.th_trigger_data_log where   table_name ='SG_COM_RUNOFFRESV_B'";
        List<Map<String, Object>> list = jdbcTemplateth39.queryForList(sql, new Object[]{});
        for (Map<String, Object> map : list) {
            String table_name = map.get("table_name").toString().trim();
            String[] pk_name = map.get("pk_name").toString().split(",");
            String[] pk_value = map.get("pk_value").toString().split(",");
            String wherecase = pk_name[0].toString().trim() + " =" + "'" + pk_value[0].toString().trim() + "'";
            for (int i = 1; i < pk_name.length; i++) {
                wherecase +=" and "+ pk_name[i].toString().trim() + " ='" + pk_value[i].toString().trim() + "'";
            }
            String op_type = map.get("op_type").toString();
            String data_source = map.get("data_source").toString();
            if (data_source.equals("360000")) {
                if (!op_type.equals("D")) {
              //  if (op_type.equals("I")) {
                    sql = "select property_name_eng from   sg_datacenter.SG_META_TABLE h ,sg_datacenter.SG_META_PROPERTY y where y.table_name_eng=h.table_name_eng and h.table_name_eng=? ";
                    List<Map<String, Object>> colomList = jdbcTemplatedc39.queryForList(sql, new Object[]{table_name});
                    String coloname = ListToString.getListToString(colomList, "property_name_eng");
                    //判断正式环境中是否有相关数据，如果没有则进行insert 操作；如果有则进行update操作；
                    sql = "SELECT * FROM sg_datacenter." + table_name + " WHERE " + wherecase;
                    int count40 = jdbcTemplatedc40.queryForList(sql).size();
                    sql = "SELECT " + coloname + " FROM sg_datacenter." + table_name + " WHERE " + wherecase;
                    List<Map<String, Object>> list39 = jdbcTemplatedc39.queryForList(sql);
                    if (list39.size() > 0) {
                        if (count40 == 0) {
                            //insert 操作
                            try {
                                System.out.println("执行insert操作：主键为"+pk_value[0]);
                                daoHepler.insert("sg_datacenter." + table_name, list39.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("存在该记录，忽略：主键为"+pk_value[0]);
                          /*  try {
                                System.out.println("执行update操作：主键为"+pk_value[0]);
                                daoHepler.update("sg_datacenter." + table_name, list39.get(0), wherecase);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }*/
                        }
                    } else {
                        System.out.println("该数据已经删除,主键为："+pk_value[0]);
                    }
                }
            }
        }
    }
}
