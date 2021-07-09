package com.my.dao;

import com.my.entity.Member;
import org.springframework.stereotype.Component;


import javax.mycore.common.Page;
import javax.mycore.framework.BaseDaoSupport;
import javax.mycore.framework.QueryRule;

/**
 * @ClassName MemberDao
 * @Description TODO
 * @Author ykq
 * @Date 2021/06/04
 * @Version v1.0.0
 */
@Component
public class MemberDao extends BaseDaoSupport<Member, Long> {

    /**
     * 继承并重写BaseDao的获取主键名
     * @return
     */
    @Override
    protected String getPKColumn() {
        return "id";
    }

    public Page<Member> selectForPage(int pageNo, int pageSize) throws Exception {
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andLike("name", "23");
        Page<Member> page = super.select(queryRule, pageNo, pageSize);
        return page;
    }
}
