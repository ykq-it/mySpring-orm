package javax.mycore.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @ClassName ClassMappings
 * @Description 类和Map的转换工具
 * @Author ykq
 * @Date 2021/06/07
 * @Version v1.0.0
 */
public class ClassMappings {

    /** 定义支持数据库和java能互相转换的数据类型 */
    static final Set<Class> SUPPORTED_SQL_OBJECTS = new HashSet<>();
    static {
        // 默认支持自动转换的类型
        Class[] classes = {
                boolean.class, Boolean.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                String.class,
                Date.class,
                Timestamp.class,
                BigDecimal.class
        };
        SUPPORTED_SQL_OBJECTS.addAll(Arrays.asList(classes));
    }

    /**
     * 获取public getter
     * @param entityClass
     * @return
     */
    public static Map<String, Method> findPublicGetters(Class entityClass) {
        Map<String, Method> map = new HashMap<>();
        Method[] methods = entityClass.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                // 修饰语是static
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                // get方法不需要入参
                continue;
            }
            if (method.getName().equals("getClass")) {
                // getClass方法时
                continue;
            }

            Class returnType = method.getReturnType();
            if (void.class.equals(returnType)) {
                // 返回值是空
                continue;
            }
            if (!isSupportedSQLObject(returnType)) {
                // 不是sql支持的返回值类型
                continue;
            }
            if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
                continue;
            }
            if (method.getName().length() < 4) {
                continue;
            }
            map.put(getGetterName(method), method);
        }
        return map;
    }

    /**
     * 获取get方法获得的成员变量名称
     * @param method
     * @return
     */
    static String getGetterName(Method method) {
        String name = method.getName();
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            name = name.substring(3);
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static boolean isSupportedSQLObject(Class clazz) {
        return clazz.isEnum() || SUPPORTED_SQL_OBJECTS.contains(clazz);
    }

    /**
     * 获取public setter，返回的是以setter的方法名去掉set前缀且首字母小写的字符串作为key，method作为value的map
     * @param entityClass
     * @return
     */
    public static <T> Map<String, Method> findPublicSetters(Class<T> entityClass) {
        Map<String, Method> map = new HashMap<>();
        Method[] methods = entityClass.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                // 修饰语是static
                continue;
            }
            if (method.getParameterTypes().length != 1) {
                // set方法需要1个入参
                continue;
            }
            if (!void.class.equals(method.getReturnType())) {
                // 返回值不是空，set方法不需要返回值
                continue;
            }
            if (!isSupportedSQLObject(method.getParameterTypes()[0])) {
                // 不是sql支持的返回值类型
                continue;
            }
            if (!method.getName().startsWith("set")) {
                continue;
            }
            if (method.getName().length() < 4) {
                continue;
            }
            map.put(getSetterName(method), method);
        }
        return map;
    }

    private static String getSetterName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static Field[] findFields(Class entityClass) {
        return entityClass.getDeclaredFields();
    }
}
