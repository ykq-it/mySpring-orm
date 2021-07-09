//package javax.mycore.framework;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.mycore.common.Page;
//import javax.mycore.common.utils.GenericsUtils;
//import javax.sql.DataSource;
//import java.io.Serializable;
//
///**
// * BaseDao扩展类，用于支持自动拼装sql语句，必须继承才能使用
// * 需要重写和定制以下三个方法
// * 1、设定主键列
// *  protected abstract String getPKColumn();
// * 2、对象反转为map
// *
// * 3、结果反转为对象的方法
// * @Author ykq
// * @Date 2021/06/07
// * @Version v1.0.0
// */
//
///**
// * T: Dao对应的实体
// * PK:
// * */
//@Data
//@Slf4j
//public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> {
//    /** 表名 */
//    private String tableName = "";
//
//    /** JdbcTemplate是Spring对原始JDBC封装之后提供的一个操作数据库的工具类
//     * JdbcTemplate主要提供以下三种类型的方法
//     *
//     * executeXxx() : 执行任何SQL语句，对数据库、表进行新建、修改、删除操作
//     * updateXxx() : 执行新增、修改、删除等语句
//     * queryXxx() : 执行查询相关的语句
//     **/
//    private JdbcTemplate jdbcTemplateWrite;
//    private JdbcTemplate jdbcTemplateReadOnly;
//
//    private DataSource dataSourceWrite;
//    private DataSource dataSourceReadOnly;
//
//    private EntityOperation<T> op;
//
//    /** protected作用域，当前类、同package、子孙类 */
//    protected BaseDaoSupport() {
//        try {
//            // index=0，是取dao的父类（BaseDaoSupport）的第一个泛型参数，目标是取出当前dao对应的实体类
//            Class<T> entityClass = GenericsUtils.getSuperClassGenericType(getClass(), 0);
//            // 入参：1、dao实体类型；2、主键名
//            op = new EntityOperation<T>(entityClass, this.getPKColumn());
//            this.setTableName(op.tableName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取主键列名称 建议子类重写
//     * @return
//     */
//    protected abstract String getPKColumn();
//
//    protected Page<T> select(QueryRule queryRule, int pageNo, int pageSize) {
//        return null;
//    }
//}
