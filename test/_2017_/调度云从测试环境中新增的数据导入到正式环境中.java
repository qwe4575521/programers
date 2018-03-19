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

public class �����ƴӲ��Ի��������������ݵ��뵽��ʽ������ {
    public static void main(String[] args) {
        //��39�������еļ�����־�ҳ������ӵ����ݲ�ͬ����40��ʽ������
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
                    //�ж���ʽ�������Ƿ���������ݣ����û�������insert ����������������update������
                    sql = "SELECT * FROM sg_datacenter." + table_name + " WHERE " + wherecase;
                    int count40 = jdbcTemplatedc40.queryForList(sql).size();
                    sql = "SELECT " + coloname + " FROM sg_datacenter." + table_name + " WHERE " + wherecase;
                    List<Map<String, Object>> list39 = jdbcTemplatedc39.queryForList(sql);
                    if (list39.size() > 0) {
                        if (count40 == 0) {
                            //insert ����
                            try {
                                System.out.println("ִ��insert����������Ϊ"+pk_value[0]);
                                daoHepler.insert("sg_datacenter." + table_name, list39.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("���ڸü�¼�����ԣ�����Ϊ"+pk_value[0]);
                          /*  try {
                                System.out.println("ִ��update����������Ϊ"+pk_value[0]);
                                daoHepler.update("sg_datacenter." + table_name, list39.get(0), wherecase);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }*/
                        }
                    } else {
                        System.out.println("�������Ѿ�ɾ��,����Ϊ��"+pk_value[0]);
                    }
                }
            }
        }
    }
}
