package _2017_;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.util.List;
import java.util.Map;

public class CIME文件内容解析并导入39库Other {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");
        DataSource dataSource = (DataSource) context.getBean("formDateSourceHDDB");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String[] files = new String[]{"萍乡_201801041009.CIME"};//"抚州_201801041005.CIME",

        //   setdel(jdbcTemplate);
        //String[] files = new String[]{"抚州_201801041005.CIME"};//,
        for (int i = 0; i < files.length; i++) {
            String file = files[0].toString();
            setAnalysis(file, jdbcTemplate);
        }
    }

    public static void setAnalysis(String filename, JdbcTemplate jdbcTemplate) throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + "cime" + File.separator + filename);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String strTemp = "";
            String Tablename = "";
            int i = 1;
            while (null != (strTemp = br.readLine())) {
                //对每行文件做解析工作;
                Tablename = getTablename(strTemp, Tablename);
                if ("".equals(Tablename)) {
                    // System.out.println(strTemp + "内容忽略,不做解析");
                } else {
                    setInsertSql(Tablename, strTemp, jdbcTemplate);
                }
                i = i + 1;
                //  System.out.println("--"+i);
            }

            fileInputStream.close();
            inputStreamReader.close();
            br.close();
        } else {
            System.out.println("文件不存在，请核对好路径！！！");
        }
    }

    public static String getTablename(String strTemp, String Tablename) {
        String result = Tablename;
        if (strTemp.contains("<") && !strTemp.contains("<!")) {
            //  String[] tablenames = new String[]{"Breaker", "Disconnector", "Disconnector", "BusbarSection", "Load", "ACLineSegment", "ACLineDot", "PowerTransformer", "TransformerWinding", "ShuntCompensator"};
            String[] tablenames = new String[]{"ControlArea", "Substation", "VoltageLevel", "BaseVoltage"};
            for (int i = 0; i < tablenames.length; i++) {
                String tablename = tablenames[i].toString();
                if (strTemp.contains(tablename)) {
                    if (strTemp.contains("</")) {
                        result = ""; //表置空
                    } else {
                        result = tablename.trim().toUpperCase().toString();
                        // System.out.println(result);
                    }
                }
            }
        }

        return result;
    }

    public static void setdel(JdbcTemplate jdbcTemplate) {
        //  String[] tablenames = new String[]{"Breaker", "Disconnector", "Disconnector", "BusbarSection", "Load", "ACLineSegment", "ACLineDot", "PowerTransformer", "TransformerWinding", "ShuntCompensator"};
        String[] tablenames = new String[]{"ControlArea", "Substation", "VoltageLevel", "BaseVoltage"};
        for (int i = 0; i < tablenames.length; i++) {
            String tablename = tablenames[i].trim().toUpperCase().toString() + "_OTHER";
            String sql = "delete from HD_DB.CIME." + tablename;
            jdbcTemplate.update(sql);

        }

    }

    public static void setInsertSql(String Tablename, String strTemp, JdbcTemplate jdbcTemplate) {

        String sql = "SELECT B.NAME AS COLUMN_NAME FROM HD_DB.SYSDBA.SYSTABLES A,  HD_DB.SYSDBA.SYSCOLUMNS B WHERE A.NAME=? and A.SCHID='150995954' AND A.ID=B.ID ORDER by B.COLID asc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, new Object[]{Tablename + "_OTHER"});
        if (strTemp.length() != 0) {
            //找出表及字段内容
            if (strTemp.contains("#")) {
                strTemp = strTemp.replace("\t ", "\t").replace("    ", "\t").replace("   ", "\t").replace("  ", "\t").replace(" ", "\t");
                String[] split = strTemp.split("\t");
                //  System.out.println("--"+strTemp);
                StringBuffer result = new StringBuffer().append(" insert into HD_DB.CIME." + Tablename + "_OTHER" + " (");
                StringBuffer stringBufferone = new StringBuffer();
                StringBuffer stringBuffertwo = new StringBuffer();
                for (int i = 2; i < split.length; i++) {
                    String onesplit = split[i].toString();
                    String coloum = list.get(i - 2).get("COLUMN_NAME").toString();
                    stringBufferone.append(coloum).append(",");
                    stringBuffertwo.append("'").append(onesplit).append("',");
                }
                stringBufferone.append("CIMETYPE");
                stringBuffertwo.append("'PX'");
                result.append(stringBufferone).append(") values (").append(stringBuffertwo).append(" )").toString();
                //  System.out.println(result);
                jdbcTemplate.update(result.toString());
            }


        }


    }


}
