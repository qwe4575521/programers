package myprogramers.Compare;

import myprogramers.util.DaoHepler;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.Map;

public class CompareBean {
    private String table_name;
    private DaoHepler FormHepler;
    private String formCloud;
    private  String formSchie;
    private DaoHepler ToHepler;
    private String toCloud;
    private  String toSchie;

    public CompareBean(String table_name, DaoHepler formHepler, String formCloud, String formSchie, DaoHepler toHepler, String toCloud, String toSchie, String[] pk_name) {
        this.table_name = table_name;
        FormHepler = formHepler;
        this.formCloud = formCloud;
        this.formSchie = formSchie;
        ToHepler = toHepler;
        this.toCloud = toCloud;
        this.toSchie = toSchie;
        this.pk_name = pk_name;
    }
    private String[] pk_name;
    public void setCompareForDel() {
        String formTables=formCloud+"."+formSchie+"."+table_name;
        String toTables=toCloud+"."+toSchie+"."+table_name;
        String sql="select * from "+formTables;
        List<Map<String,Object>> list = FormHepler.getJdbcTemplate().queryForList(sql,new Object[]{});
        String sql1="select *  from "+toTables;
        List<Map<String,Object>> list1 = FormHepler.getJdbcTemplate().queryForList(sql1,new Object[]{});
        System.out.println("--调度结构设计表：" + table_name + " 中源端个数为" + list.size() + " ;目标端个数据为" + list1 + "; 其中数据个数" + (list.size() == list1.size() ? "一致" : "不一致"));
        System.out.println("-----------数据对比开始------------");
        for (int i = 0; i < list1.size(); i++) {
            Map<String,Object> map1=list1.get(i);
            String[] pk_value=null;
            for (int i1 = 0; i1 < pk_name.length; i1++) {
                String value=map1.get(pk_name[i1].trim().toUpperCase()).toString();
                ArrayUtils.add(pk_value,value);
            }
            String wherecase="where "+pk_name[0].toUpperCase().trim().toString()+"='"+pk_value[0].trim().toString()+"'";
            String sql2="select count(*) as num from "+formTables+wherecase;
            int count=FormHepler.getJdbcTemplate().queryForInt(sql2);
            if (count==0) {
                sql2="DELETE FROM "+toTables+wherecase;
                System.out.println(sql2);
            }
        }
    }



}
