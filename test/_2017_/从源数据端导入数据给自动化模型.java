package _2017_;

import myprogramers.util.DaoHepler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class 从源数据端导入数据给自动化模型 {
    public 从源数据端导入数据给自动化模型() {
    }

    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        //String tables="SG_ORG_SUPPLIER_B,SG_AUT_BLADESVRCON_P,SG_AUT_BLADESVR_P,SG_AUT_CRAC_P,SG_AUT_DB_P,SG_AUT_FIREWALL_P,SG_AUT_HORISOLATOR_P,SG_AUT_IDD_P,SG_AUT_INDUSSWITCH_P,SG_AUT_MIDWARE_P,SG_AUT_OEC_P,SG_AUT_OS_P,SG_AUT_PRINTER_P,SG_AUT_ROUTER_P,SG_AUT_SERVER_P,SG_AUT_SOFTWAREPACK_P,SG_AUT_SPSERVER_P,SG_AUT_STORAGE_P,SG_AUT_SWITCH_P,SG_AUT_TIMESYNCDEV_P,SG_AUT_UPS_P,SG_AUT_VERTENCRYPTOR_P,SG_AUT_WORKSTATION_P,SG_CON_MASTERAS_P,SG_CON_SLAVEAS_P,SG_DA_DTU_P,SG_DA_FTU_P,SG_DA_STU_P,SG_DA_TTU_P,\n" +
          //      "SG_SSD_BCU_P,SG_SSD_ERTU_P,SG_SSD_IED_P,SG_SSD_MU_P,SG_SSD_NETANALYZER_P,SG_SSD_PMU_P,SG_SSD_RTUGATEWAY_P,SG_SSD_RTU_P,SG_AUT_KVM_P";
        //String tables="SG_AUT_KVM_P,SG_AUT_UPS_P,SG_AUT_OS_P,SG_SSD_BCU_P,SG_CON_SLAVEAS_P,SG_AUT_SPSERVER_P,SG_AUT_STORAGE_P,SG_AUT_PRINTER_P,SG_AUT_BLADESVRCON_P,SG_AUT_BLADESVR_P,SG_SSD_ERTU_P,SG_AUT_FIREWALL_P,SG_AUT_INDUSSWITCH_P,SG_AUT_WORKSTATION_P,SG_AUT_OEC_P,SG_SSD_MU_P,SG_AUT_HORISOLATOR_P,SG_AUT_SWITCH_P,SG_AUT_CRAC_P,SG_AUT_ROUTER_P,SG_AUT_IDD_P,SG_ORG_SUPPLIER_B,SG_AUT_TIMESYNCDEV_P,SG_AUT_DB_P,SG_SSD_PMU_P,SG_AUT_SOFTWAREPACK_P,SG_SSD_RTU_P,SG_SSD_IED_P,SG_CON_MASTERAS_P,SG_SSD_RTUGATEWAY_P,SG_AUT_VERTENCRYPTOR_P";
       String tables="SG_SSD_RTUGATEWAY_P";
        String[] split=tables.split(",");
        for (int i = 0; i < split.length; i++) {
            String table=split[i];
            getsql(context,table);

        }

    }

    public static void getsql(ApplicationContext context, String tablename){
        DataSource tokindbase = (DataSource) context.getBean("kindbase");
        //  String table="SG_AUT_KVM_P";
        String frombase="DC_CLOUD1.sg_datacenter.";
        String tobase="sg_datacenter.";
        //String tablename="SG_AUT_KVM_P";
        DataSource formDateSource = (DataSource) context.getBean("formDateSource");
        String sql="select A.attrelid,A.attname,c.relname,m.nspname from SYS_CLASS c ,SYS_NAMESPACE m ,SYS_ATTRIBUTE A  where relname=? and \n" +
                "m.nspname='SG_DATACENTER' and c.oid=a.attrelid and  c.RELNAMESPACE=m.oid and attname not in('TABLEOID','CMAX','XMAX','CMIN','XMIN','CTID')";//查询金仓数据库下的所有表及结构
        JdbcTemplate kingbasetemplate=new JdbcTemplate(tokindbase);
        List<Map<String, Object>> list=kingbasetemplate.queryForList(sql,new Object[]{tablename});
        String colnames="";
        String colnameto="";
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map= list.get(i);
            String attname= "IS_NATIVE".equals(map.get("attname").toString()) ?"0 as IS_NATIVE":map.get("attname").toString();
            attname= "SCREEN_SIZE".equals(attname) ?"'' as  SCREEN_SIZE ":attname;
            colnames+=attname+",";
            colnameto+=map.get("attname").toString();
        }
        if(colnames.length()!=0){
      String   sql1="SELECT "+colnames.substring(0,colnames.length()-1) +" FROM "+frombase+tablename+";";
       // System.out.println(sql1);
        JdbcTemplate fromtemplate=new JdbcTemplate(formDateSource);
        List<Map<String, Object>> alldate=fromtemplate.queryForList(sql1);
        for (int i = 0; i < alldate.size(); i++) {
            Map<String, Object> dateMap= alldate.get(i);
            sql="SELECT * FROM  "+tobase+tablename+" WHERE ID='"+dateMap.get("ID").toString()+"'";
            //System.out.println(sql);
            List<Map<String, Object>> list1=kingbasetemplate.queryForList(sql);
            if (list1.size()==0) {
                //需要添加数据
                try {
                    DaoHepler daoHepler =new DaoHepler(tokindbase);
                    daoHepler.insert(tobase+tablename,dateMap);
                    System.out.println("数据录入成功；" + "id为" + dateMap.get("ID").toString());
                } catch (Exception e) {
                    System.out.println("表中" + frombase + tablename + "数据添加异常；" + "id为" + dateMap.get("ID").toString());
                    e.printStackTrace();
                }
            }
            else {
                try {
                    DaoHepler daoHepler =new DaoHepler(tokindbase);
                    daoHepler.update(tobase+tablename,dateMap,"ID='"+dateMap.get("ID").toString()+"'");
                   // System.out.println("数据修改成功；" + "id为" + dateMap.get("ID").toString());
                } catch (Exception e) {
                    System.out.println("表中" + frombase + tablename + "数据修改异常；" + "id为" + dateMap.get("ID").toString());
                    e.printStackTrace();
                }
            }




        }




    }
     else{
            System.out.println("自动化模型参数无该表" + tablename);
        }
    }

}
