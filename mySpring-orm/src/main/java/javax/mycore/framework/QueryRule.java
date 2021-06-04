package javax.mycore.framework;

import java.io.Serializable;

/**
 * @ClassName QueryRule
 * @Description TODO
 * @Author ykq
 * @Date 2021/06/04
 * @Version v1.0.0
 */
public class QueryRule implements Serializable {

    public static QueryRule getInstance() {
        return new QueryRule();
    }

    public void andLike(String s, String s1) {
    }
}
