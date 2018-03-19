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
 * 对OP二次对应清单信息PMS信息修改；
 */
public class 二次设备清单更新程序路口 {
    public static void main(String[] args) throws IOException, BiffException {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:springdateSource.xml");
        DataSource source1 = (DataSource) context.getBean("sjxfgs");//省调更新
        JdbcTemplate jdbcTemplate1 = new JdbcTemplate(source1);
        String sql = "";
        String deptnames = "";
        File file = new File("C:\\Users\\xucl\\Desktop\\OP二次对应清单汇总1108.xls");
        Workbook workbook = Workbook.getWorkbook(file);
        //保护设备
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
            if (deptname.contains("供电公司")) {
                String sourceid = ChineseToEnglish.getPinYinHeadChar(deptname);
                DataSource source = (DataSource) context.getBean(sourceid);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
//                sql="select equip_id,pms_id from equip.T_EC_PROTECTDEVICE where equip_id=?";
//                System.out.println(deptname+"="+jdbcTemplate.queryForList(sql, new Object[]{oms_id}));
                jdbcTemplate.update(sql, new Object[]{pmssbid, oms_id});
            }
            jdbcTemplate1.update(sql, new Object[]{pmssbid, oms_id});
        }

        //自动化设备
        Sheet sheet1 = workbook.getSheet(1);
        System.out.println(sheet1.getRows());
        System.out.println(sheet1.getColumns());
        int rows1 = sheet1.getRows();
        int colums1 = sheet1.getColumns();
        String json = "[\n" +
                "{name:\"不间断电源\",table:\"TH_AUT_UPS_B\"}\n" +
                ",{name:\"串口服务器\",table:\"TH_AUT_SPSERVER_B\"}\n" +
                ",{name:\"防火墙\",table:\"TH_AUT_FIREWALL_B\"}\n" +
                ",{name:\"服务器\",table:\"TH_AUT_SERVER_B\"}\n" +
                ",{name:\"工业交换机\",table:\"TH_AUT_INDUSSWITCH_B\"}\n" +
                ",{name:\"工作站\",table:\"TH_AUT_WORKSTATION_B\"}\n" +
                ",{name:\"横向隔离装置\",table:\"TH_AUT_HORISOLATOR_B\"}\n" +
                ",{name:\"交换机\",table:\"TH_AUT_SWITCH_B\"}\n" +
                ",{name:\"路由器\",table:\"TH_AUT_ROUTER_B\"}\n" +
                ",{name:\"时间同步装置\",table:\"TH_AUT_TIMESYNCDEV_B\"}\n" +
                ",{name:\"相量测量装置\",table:\"TH_SSD_PMU_B\"}\n" +
                ",{name:\"专用远动网关机\",table:\"TH_SSD_RTUGATEWAY_B\"}\n" +
                ",{name:\"纵向加密装置\",table:\"TH_AUT_VERTENCRYPTOR_B\"}\n" +
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
        //测控装置
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
            sql = "update SG_DATACENTER.TH_SSD_BCU_B set pms_id=? where id=?";//SG_DATACENTER.TH_SSD_BCU_B 测控状态
            jdbcTemplate1.update(sql, new Object[]{pmssbid, oms_id});
        }


    }
}
