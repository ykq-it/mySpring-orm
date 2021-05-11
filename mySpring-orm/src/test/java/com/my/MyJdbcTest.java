package com.my;

import com.alibaba.fastjson.JSON;
import com.my.entity.Order;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MyJdbcTest
 * @Description TODO
 * @Author ykq
 * @Date 2021/05/10
 * @Version v1.0.0
 */
public class MyJdbcTest {

    public static void main(String[] args) {
        // 1、测试传统硬编码
        originJdbc();

        // 2、测试改进版
        dynamicJdbc();
    }

    private static void dynamicJdbc() {
        // 面向对象(Object Oriented,OO)
        Order select = new Order();
        select.setMemberId(2L);
        select.setId(2L);
        List<?> result = null;
        try {
            result = dynamicSelect(select);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(result, true));
    }

    private static List<?> dynamicSelect(Object condition) throws Exception {
        // 属性和列的映射关系
        Map<String, String> getFieldByColumn = new HashMap<>();
        Map<String, String> getColumnByField = new HashMap<>();

        // 通过条件反射获得类型
        Class clazz = condition.getClass();

        //无反射，不框架
        //无正则，不架构
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String columnName = fieldName;
            // 优先处理加注解的
            Column column = field.getAnnotation(Column.class);
            if (null != column) {
                columnName = column.name();
            }
            // 缓存属性和列的映射关系
            getColumnByField.put(fieldName, columnName);
            getFieldByColumn.put(columnName, fieldName);
        }


        Table table = (Table) clazz.getAnnotation(Table.class);
        String tableName = table.name();
        // sql拼装表名
        StringBuffer sql = new StringBuffer("select * from " + tableName + " where 1 = 1");
        // sql拼装条件
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(condition);

            if (null != value) {
                if (String.class == field.getType()) {
                    sql.append(" and " + getColumnByField.get(field.getName()) + " = " + value.toString());
                } else {
                    sql.append(" and " + getColumnByField.get(field.getName()) + " = " + value);
                }
            }
        }

        List<Object> result = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1、加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 2、创建连接
            con = DriverManager.getConnection("jdbc:mysql://101.200.177.197:3306/study","customer","123qwe");
            // 3、创建语句集
            ps = con.prepareStatement(String.valueOf(sql));
            // 4、执行
            rs = ps.executeQuery();
            // 5、整理结果
            while (rs.next()) {
                Object o = clazz.newInstance();
                int i = rs.getMetaData().getColumnCount();
                for (int j = 0; j < i; j++) {
                    String columnName = rs.getMetaData().getColumnName(j + 1);
                    String fieldName = getFieldByColumn.get(columnName);
                    Object value = rs.getObject(columnName);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(o, value);
                }
                result.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != rs) {
                    // 关闭结果集
                    rs.close();
                }
                if (null != ps) {
                    // 关闭预处理集
                    ps.close();
                }
                if (null != con) {
                    // 关闭连接
                    con.close();
                }
            } catch (Exception es) {
                es.printStackTrace();
            }
        }

        // 返回结果集
        return result;

    }

    private static void originJdbc() {
        String sql = "select * from t_order";
        List<Order> orders = originSelect(sql);
        System.out.println(JSON.toJSONString(orders, true));
    }

    private static List<Order> originSelect(String sql) {
        List<Order> orders = new ArrayList<>();
        // 连接对象
        Connection con = null;
        // 语句集
        PreparedStatement ps = null;
        // 结果集
        ResultSet rs = null;
        try {
            // 1、加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 2、创建连接
            con = DriverManager.getConnection("jdbc:mysql://101.200.177.197:3306/study","customer","123qwe");
            // 3、创建语句集
            ps = con.prepareStatement(sql);
            // 4、执行
            rs = ps.executeQuery();
            // 5、整理结果
            while (rs.next()) {
                Order order = mapperRowOrder(rs);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != rs) {
                    // 关闭结果集
                    rs.close();
                }
                if (null != ps) {
                    // 关闭预处理集
                    ps.close();
                }
                if (null != con) {
                    // 关闭连接
                    con.close();
                }
            } catch (Exception es) {
                es.printStackTrace();
            }
        }

        // 返回结果集
        return orders;
    }

    private static Order mapperRowOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setMemberId(rs.getLong("mid"));
        order.setDetail(rs.getString("detail"));
        order.setCreateTime(rs.getLong("createTime"));
        order.setCreateTimeFmt(rs.getString("createTimeFmt"));
        return order;
    }

}
