package myprogramers.util;

import java.util.List;
import java.util.Map;

public class ListToString {
    public static String getListToString(List<Map<String, Object>> list, String coloname) {
        String result = "";
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String pk_name = map.get(coloname).toString();
                result += pk_name + ",";
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

}
