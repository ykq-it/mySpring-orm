package javax.mycore.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ClassName GenericsUtils
 * @Description 泛型操作类
 * @Author ykq
 * @Date 2021/06/07
 * @Version v1.0.0
 */
@Slf4j
public class GenericsUtils {

    /**
     * clazz 要解析的泛型class的子类（参数化类型的类的子类）
     * index 参数化类型的参数的初始下标
     **/
    public static Class getSuperClassGenericType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        // 具有<>符号的变量是参数化类型
        if (!(genType instanceof ParameterizedType)) {
            log.info(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        // 返回了一个Type数组,数组里是参数化类型的参数
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.error("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }

        // 如果不是类型类
        if (!(params[index] instanceof Class)) {
            // 没有设置真实类父类的泛型参数
            log.error(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        // 返回下标为index的类型类
        return (Class) params[index];
    }
}
