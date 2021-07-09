package javax.mycore.framework;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName EntityOperation
 * @Description 实体对象的反射操作
 * @Author ykq
 * @Date 2021/06/07
 * @Version v1.0.0
 */
@Data
@Slf4j
public class EntityOperation<T> {
    /**
     * 泛型指定的实体类类型
     */
    public Class<T> entityClass;
    public final String tableName;
    public Field pkField;
    public final Map<String, PropertyMapping> mappings;
    public String allColumn;
    public final RowMapper<T> rowMapper;

    /**
     * 构造方法
     *
     * @param entityClass 实体类类型
     * @param pkColumn    主键名
     */
    public EntityOperation(Class<T> entityClass, String pkColumn) throws Exception {
        // 如果没有被@Entity注解
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new Exception("在" + entityClass.getName() + "中没有找到Entity注解，不能做ORM映射");
        }
        this.entityClass = entityClass;

        Table table = entityClass.getAnnotation(Table.class);
        if (ObjectUtils.isEmpty(table)) {
            // 没有指定@Table的value，则使用SimpleName作为表名
            this.tableName = entityClass.getSimpleName();
        } else {
            this.tableName = table.name();
        }

        // 获取所有的public getter
        Map<String, Method> getters = ClassMappings.findPublicGetters(entityClass);
        // 获取所有的public setter
        Map<String, Method> setters = ClassMappings.findPublicSetters(entityClass);

        Field[] fields = ClassMappings.findFields(entityClass);
        fillPKFileAndAllColumn(pkColumn, fields);

        this.mappings = getPropertyMappings(getters, setters, fields);
        this.allColumn = this.mappings.keySet().toString().replace("[", "").replace("]", "").replace(" ", "");
        this.rowMapper = createRowMapper();
    }

    /**
     * 创建结果集映射
     * @return
     */
    private RowMapper<T> createRowMapper() {
        return new RowMapper<T>() {
            @Override
            public T mapRow(ResultSet resultSet, int i) throws SQLException {
                try {
                    // 创建一个实体类的对象
                    T t = entityClass.newInstance();
                    // 获取结果集
                    ResultSetMetaData meta = resultSet.getMetaData();
                    // 结果集元数据的数量
                    int columns = meta.getColumnCount();
                    String columnName;
                    for (int j = 0; j <= columns; j++) {
                        // 元数据的值
                        Object value = resultSet.getObject(j);
                        // 元数据列名
                        columnName = meta.getColumnName(j);
                        fillBeanFieldValue(t, columnName, value);
                    }
                    return t;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void fillBeanFieldValue(T t, String columnName, Object value) {
        if (!ObjectUtils.isEmpty(value)) {
            // 如果结果集的值不为空
            PropertyMapping pm = mappings.get(columnName);
            if (!ObjectUtils.isEmpty(pm)) {
                try {
                    pm.set(t, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillPKFileAndAllColumn(String pkColumn, Field[] fields) {
        try {
            // 设定主键
            if (StringUtils.hasText(pkColumn)) {
                pkField = entityClass.getDeclaredField(pkColumn);
                pkField.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            log.debug("没找到主键列，dao设定的主键列名必须与实体类属性名相同");
        }

        // 如果没有设定主键名，则判断属性是否被@Id注解，则使用Id对应的属性灌入pkField
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (!StringUtils.hasText(pkColumn)) {
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    pkField = f;
                    break;
                }
            }
        }
    }


    /**
     * @param getters
     * @param setters
     * @param fields  实体类的所有成员变量
     * @return
     */
    private Map<String, PropertyMapping> getPropertyMappings(Map<String, Method> getters, Map<String, Method> setters, Field[] fields) {
        Map<String, PropertyMapping> mappings = new HashMap<>();
        String name;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                // @transient 是在再给某个javabean上添加个属性，但是这个属性又不希望存到数据库中，仅仅是做个临时变量用一下。不修改已经存在数据库的数据的数据结构。
                continue;
            }
            // 获取成员变量的名称
            name = field.getName();
            if (name.startsWith("is")) {
                name = name.substring(2);
            }
            // 将首字母变成小写
            name = new StringBuilder().append(Character.toLowerCase(name.charAt(0))).append(name.substring(1)).toString();

            Method setter = setters.get(name);
            Method getter = getters.get(name);
            if (ObjectUtils.isEmpty(setter) || ObjectUtils.isEmpty(getter)) {
                // 必须set和get都有
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (ObjectUtils.isEmpty(column)) {
                mappings.put(field.getName(), new PropertyMapping(getter, setter, field));
            } else {
                mappings.put(column.name(), new PropertyMapping(getter, setter, field));
            }
        }
        return mappings;
    }
}

/**
 * 实体类成员变量的属性配置
 */
class PropertyMapping {
    /** 可插入的 */
    final boolean insertable;
    /** 可更新的 */
    final boolean updatable;
    final String columnName;
    final boolean id;
    final Method getter;
    final Method setter;
    final Class enumClass;
    final String fieldName;

    public PropertyMapping(Method getter, Method setter, Field field) {
        this.getter = getter;
        this.setter = setter;
        this.enumClass = getter.getReturnType().isEnum() ? getter.getReturnType() : null;
        Column column = field.getAnnotation(Column.class);
        this.insertable = ObjectUtils.isEmpty(column) || column.insertable();
        this.updatable = ObjectUtils.isEmpty(column) || column.updatable();
        this.columnName = ObjectUtils.isEmpty(column) ? ClassMappings.getGetterName(getter) : (StringUtils.hasText(column.name()) ? column.name() : ClassMappings.getGetterName(getter));
        this.id = field.isAnnotationPresent(Id.class);
        this.fieldName = field.getName();
    }

    public <T> void set(T t, Object value) {
        // TODO 干嘛用的
        if (!ObjectUtils.isEmpty(enumClass) && !ObjectUtils.isEmpty(value)) {
            value = Enum.valueOf(enumClass, String.valueOf(value));
        }
        try {
            if (!ObjectUtils.isEmpty(value)) {
                setter.invoke(t, setter.getParameterTypes()[0].cast(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
            /**
             * 出错原因如果是boolean字段 mysql字段类型 设置tinyint(1)
             */
            System.err.println(fieldName + "--" + value);
        }
    }
}
