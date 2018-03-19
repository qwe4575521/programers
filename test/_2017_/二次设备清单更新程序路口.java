package _2017_;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import myprogramers.util.ChineseToEnglish;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by xucl on 2017-11-15.
 * ��OP���ζ�Ӧ�嵥��ϢPMS��Ϣ�޸ģ�
 */
public class �����豸�嵥���³���·�� {
    public static void main(String[] args) throws IOException, BiffException {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:springdateSource.xml");
        DataSource source1 = (DataSource) context.getBean("sjxfgs");//ʡ������
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(source1);
        String sql = "";
        String deptnames = "";
        File file = new File("C:\\Users\\xucl\\Desktop\\OP���ζ�Ӧ�嵥����1108.xls");
        Workbook workbook = Workbook.getWorkbook(file);
        //�����豸
        Sheet sheet = workbook.getSheet(0);
        System.out.println(sheet.getRows());
        System.out.println(sheet.getColumns());
        int rows = sheet.getRows();
        int colums = sheet.getColumns();
        for (int i = 1; i < rows; i++) {
            String pmssbid = "";
            String oms_id = "";
            String deptname = "";
            for (int i1 = 0; i1 < colums; i1++) {
                Cell cell = sheet.getCell(i1, i);
                if (i1 == 1) {
                    pmssbid = cell.getContents();
                }
                if (i1 == 2) {
                    oms_id = cell.getContents();
                }
                if (i1 == 4) {
                    deptname = cell.getContents();
                }
            }
            sql = "update equip.T_EC_PROTECTDEVICE set pms_id=? where equip_id=?";
            if (deptname.contains("���繫˾")) {
                String sourceid = ChineseToEnglish.getPinYinHeadChar(deptname);
                DataSource source = (DataSource) context.getBean(sourceid);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
//                sql="select equip_id,pms_id from equip.T_EC_PROTECTDEVICE where equip_id=?";
//                System.out.println(deptname+"="+jdbcTemplate.queryForList(sql, new Object[]{oms_id}));
                jdbcTemplate.update(sql, new Object[]{pmssbid, oms_id});
            }
            jdbcTemplate1.update(sql, new Object[]{pmssbid, oms_id});
        }

        //�Զ����豸
        Sheet sheet1 = workbook.getSheet(1);
        System.out.println(sheet1.getRows());
        System.out.println(sheet1.getColumns());
        int rows1 = sheet1.getRows();
        int colums1 = sheet1.getColumns();
        String json = "[\n" +
                "{name:\"����ϵ�Դ\",table:\"TH_AUT_UPS_B\"}\n" +
                ",{name:\"���ڷ�����\",table:\"TH_AUT_SPSERVER_B\"}\n" +
                ",{name:\"����ǽ\",table:\"TH_AUT_FIREWALL_B\"}\n" +
                ",{name:\"������\",table:\"TH_AUT_SERVER_B\"}\n" +
                ",{name:\"��ҵ������\",table:\"TH_AUT_INDUSSWITCH_B\"}\n" +
                ",{name:\"����վ\",table:\"TH_AUT_WORKSTATION_B\"}\n" +
                ",{name:\"�������װ��\",table:\"TH_AUT_HORISOLATOR_B\"}\n" +
                ",{name:\"������\",table:\"TH_AUT_SWITCH_B\"}\n" +
                ",{name:\"·����\",table:\"TH_AUT_ROUTER_B\"}\n" +
                ",{name:\"ʱ��ͬ��װ��\",table:\"TH_AUT_TIMESYNCDEV_B\"}\n" +
                ",{name:\"��������װ��\",table:\"TH_SSD_PMU_B\"}\n" +
                ",{name:\"ר��Զ�����ػ�\",table:\"TH_SSD_RTUGATEWAY_B\"}\n" +
                ",{name:\"�������װ��\",table:\"TH_AUT_VERTENCRYPTOR_B\"}\n" +
                "]";
        for (int i = 0; i < rows1; i++) {
            String pmssbid = "";
            String oms_id = "";
            String name = "";
            for (int i1 = 0; i1 < colums1; i1++) {
                Cell cell = sheet1.getCell(i1, i);
                if (i1 == 1) {
                    pmssbid = cell.getContents();
                }
                if (i1 == 2) {
                    oms_id = cell.getContents();
                }
                if (i1 == 4) {
                    name = cell.getContents();
                }
            }
            JSONArray jsonArray = JSONArray.fromObject(json);
            Iterator iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonArray1 = JSONObject.fromObject(iterator.next());
                if (name.contains(jsonArray1.get("name").toString())) {
                    String tablename = jsonArray1.get("table").toString();
                    sql = "update  SG_DATACENTER." + tablename + " set pms_id='" + pmssbid + "' where id='" + oms_id + "'";
                    jdbcTemplate1.update(sql);
                    sql = "SELECT pms_id,id  FROM SG_DATACENTER." + tablename + "  where id='" + oms_id + "'";
                    System.out.println(sql + ";" + jdbcTemplate1.queryForList(sql));
                }
            }
        }
        //���װ��
        Sheet sheet2 = workbook.getSheet(2);
        System.out.println(sheet2.getRows());
        System.out.println(sheet2.getColumns());
        int rows2 = sheet2.getRows();
        int colums2 = sheet2.getColumns();
        for (int i = 1; i < rows2; i++) {
            String pmssbid = "";
            String oms_id = "";
            for (int i1 = 0; i1 < colums2; i1++) {
                Cell cell = sheet2.getCell(i1, i);
                if (i1 == 1) {
                    pmssbid = cell.getContents();
                }
                if (i1 == 2) {
                    oms_id = cell.getContents();
                }
            }
            System.out.println(pmssbid + ";" + oms_id);
            // sql="SELECT pms_id,id FROM SG_DATACENTER.TH_SSD_BCU_B where id=?";
            //  System.out.println(pmssbid+";"+jdbcTemplate1.queryForList(sql, new Object[]{oms_id}));
            sql = "update SG_DATACENTER.TH_SSD_BCU_B set pms_id=? where id=?";//SG_DATACENTER.TH_SSD_BCU_B ���״̬
            jdbcTemplate1.update(sql, new Object[]{pmssbid, oms_id});
        }


    }
}
