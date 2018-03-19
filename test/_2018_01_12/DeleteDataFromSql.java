package _2018_01_12;

import myprogramers.configReader.ConfigReader;
import myprogramers.util.DaoHepler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

public class DeleteDataFromSql {
    //    ��ѯ41���Կ���allsql.properties�µ������и��²���
    /**SG_DEV_BUSBAR_B@ID@_DEL=from DC_CLOUD1.SG_DATACENTER.SG_DEV_BUSBAR_B where name ='ĸ��'
     * SG_DEV_BUSBAR_B@ID@_DEL �ṹΪ��������@���������Ϊ���Ÿ�����@���±�ʶ��_DELɾ��_INSERT����_UPDATE���£�
     * */
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        Map<String, Object> sqlconfig = ConfigReader.getPropertys();
        DaoHepler daoHepler = new DaoHepler((DataSource) context.getBean("formDateSource40"));
        Iterator iterator = sqlconfig.entrySet().iterator();
        String path="C:\\Users\\xucl\\Desktop\\�Զ���ȫ������\\";
        String filename="delete"+ DateFormatUtils.format(new Date(), "yyyyMMddHHmmssss")+".sql";
        File file=new File(path+filename);
        FileOutputStream fileOutputStream = null;
        try {
             fileOutputStream=new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String getkey = entry.getKey().toString();
            String table, type = null, pramkey = "";
            table = getTable(getkey);
            pramkey = getPramKey(getkey);
            if (getkey.contains("_DEL")) {
                getkey = getkey.replace("@_DEL", "");
                type = "D";

            }
            if (getkey.contains("_INSERT")) {
                getkey = getkey.replace("@_INSERT", "");
                type = "I";

            }
            if (getkey.contains("_UPDATE")) {
                getkey = getkey.replace("@_UPDATE", "");
                type = "U";
            }
            try {
                updateSql(daoHepler, table, pramkey, entry.getValue().toString(), type,fileOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateSql(DaoHepler daoHepler, String table, String pramkey, String s, String type,FileOutputStream fileOutputStream) throws IOException {

        String sql = "select " + pramkey + " " + s;
        List<Map<String, Object>> dataList = daoHepler.getJdbcTemplate().queryForList(sql, new Object[]{});
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            String[] split = pramkey.split(",");
            String pramkeyValue="";
            pramkeyValue=map.get(split[0].toString()).toString();
            for (int i1 = 1; i1 < split.length; i1++) {
                pramkeyValue+=","+map.get(split[i1].toString()).toString();
            }
            sql="INSERT INTO TH_DB.DATASYNC.TH_TRIGGER_DATA_LOG (TABLE_NAME,PK_NAME,PK_VALUE,OP_TYPE,OP_TIMESTAMP,DATA_SOURCE,OP_SEQ,FIELD_VALUE)" +
                    " VALUES ('"+table+"','"+pramkey+"','"+pramkeyValue+"','"+type+"','2018-01-06 16:31:12.0','360000',TH_DB.DATASYNC.TH_TRIGGER_DATA_LOG_SEQ.nextval,'')";
          //  System.out.println(sql+"\r\n");
            sql+="\r\n";
            fileOutputStream.write(sql.getBytes());
        }
       // fileOutputStream.flush();
       // fileOutputStream.close();
    }

    private static String getTable(String getkey) {
        return getkey.split("@")[0].toUpperCase().trim().toString();
    }

    private static String  getPramKey(String getkey) {
        String getParamKey = getkey.split("@")[1].toString();
        String[] split = getParamKey.split("\\+");
        String result = split[0].toUpperCase().trim().toString();
        for (int i = 1; i < split.length; i++) {
            result += "," + split[i].toUpperCase().trim().toString();
        }
        return result;
    }


}
