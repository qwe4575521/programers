package myprogramers.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class DaoHepler {


    /**
     * @Title: getJdbcTemplate
     * @Description: 获取JDBC 数据源
     * @param
     * @return JdbcTemplate
     * @throws
     */
 private DataSource dataSource;

    public DaoHepler(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * <br>miniui插入数据方法
     *
     * @param TableName 表名称
     * @param valuesMap Map对象（表字段及对应的数据）
     */
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(this.dataSource);
    }
    public  boolean insert(String TableName, Map<String, Object> valuesMap) {
        valuesMap.remove("_id");
        valuesMap.remove("_uid");
        valuesMap.remove("_state");
        String sql = buildInsertSql(TableName, valuesMap);
        MapSqlParameterSource parameterSource = buildParameterSource(valuesMap);
        return executeUpdate(dataSource, sql, parameterSource);

    }

    /**
     * <br>miniui更新数据方法
     *
     * @param tableName  表名称
     * @param valueMap   Map对象（表字段及对应的数据）
     * @param whereCause where条件语句
     */
    public  boolean update(String tableName, Map<String, Object> valueMap,
                           String whereCause) throws SQLException {
        valueMap.remove("_id");
        valueMap.remove("_uid");
        valueMap.remove("_state");
        valueMap.remove("nom");
        String sql = buildUpdateSql(tableName, valueMap, whereCause);
        MapSqlParameterSource parameterSource = buildParameterSource(valueMap);
        return executeUpdate(dataSource, sql, parameterSource);
    }

    /**
     * <br>miniui删除数据方法
     *
     * @param tableName  表名称
     * @param valuesMap  Map对象（表字段及对应的数据）
     * @param whereCause where条件语句
     */
    public  boolean delete(String tableName, Map<String, Object> valuesMap,
                           String whereCause) throws SQLException {
        valuesMap.remove("_id");
        valuesMap.remove("_uid");
        valuesMap.remove("_state");
       // valuesMap.remove("nom");
        String sql = buildDeleteSql(tableName, whereCause);
        MapSqlParameterSource parameterSource = buildParameterSource(valuesMap);
        return executeUpdate(dataSource, sql, parameterSource);
    }

    private static MapSqlParameterSource buildParameterSource(Map<String, Object> values) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        Iterator<Entry<String, Object>> it = values.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            parameterSource.addValue(entry.getKey(), entry.getValue());
        }

        return parameterSource;
    }

    private static boolean executeUpdate(DataSource dataSource, String sql,
                                         MapSqlParameterSource parameterSource) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(
                dataSource);
        int effectedRow = jdbcTemplate.update(sql, parameterSource);
        return effectedRow >= 1;
    }

    private static Number executeUpdateWithReturnKey(DataSource dataSource,
                                                     String sql, MapSqlParameterSource parameterSource, String primaryKey) {
        int i = 0;
        GeneratedKeyHolder keyholder = new GeneratedKeyHolder();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(
                dataSource);

        jdbcTemplate.update(sql, parameterSource, keyholder,
                new String[]{primaryKey});
        return keyholder.getKey();
    }

    private static String buildUpdateSql(String tableName, Map<String, Object> valuesMap,
                                         String whereCause) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>(valuesMap.keySet());
        sb.append("UPDATE ".concat(tableName).concat(" SET "));
        sb.append((list.get(0)).concat("= :").concat(
                list.get(0)));

        for (int i = 1; i < list.size(); ++i) {
            sb.append(", ".concat(list.get(i)).concat("= :")
                    .concat(list.get(i)));
        }

        if (whereCause != null && whereCause.trim().length() > 0) {
            sb.append(" WHERE ".concat(whereCause));
        }

        return sb.toString();
    }

    private static String buildDeleteSql(String tableName,
                                         String whereCause) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE ".concat(tableName));
        if (whereCause != null && whereCause.trim().length() > 0) {
            sb.append(" WHERE ".concat(whereCause));
        }

        return sb.toString();
    }


    private static String buildInsertSql(String tableName, Map<String, Object> valuesMap) {

        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<String>(valuesMap.keySet());
        sb.append("INSERT INTO ".concat(tableName));
        sb.append("(");
        sb.append(list.get(0));
        for (int value = 1; value < list.size(); ++value) {
            sb.append(" ,".concat(list.get(value)));
        }
        sb.append(") VALUES (");
        sb.append(":".concat(list.get(0)));
        for (int var9 = 1; var9 < list.size(); ++var9) {
            sb.append(", :".concat(list.get(var9)));
        }
        sb.append(")");
        //System.out.println(sb.toString());
        return sb.toString();

    }


}
