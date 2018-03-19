package myprogramers.configReader;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xucl on 2017-04-01.
 */
public class ConfigReader {

    static  Properties props = new Properties();
    static {
        InputStream iStream  = null;
        try {
            String path=System.getProperty("user.dir")+File.separator+"src\\com\\myprogramers\\configReader\\allsql.properties";
       /*String path=     ConfigReader.class.getClassLoader().getResource(
                    "/com/myprogramers/configReader/allsql.properties").getPath();*/
            System.out.println(path);
            iStream = new BufferedInputStream(new FileInputStream(path));
            props.load(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public  static Map<String,Object> getPropertys(){
        Map<String,Object> map = new HashMap<String, Object>();
        Enumeration<String> enumeration= (Enumeration<String>) props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key=enumeration.nextElement();
            String value=props.getProperty(key);
            map.put(key,value);
        }
    return  map;
    }


}
