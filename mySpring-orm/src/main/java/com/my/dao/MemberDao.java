package com.my.dao;

import com.my.entity.Member;

import javax.mycore.common.Page;
import javax.mycore.framework.QueryRule;

/**
 * @ClassName MemberDao
 * @Description TODO
 * @Author ykq
 * @Date 2021/06/04
 * @Version v1.0.0
 */
public class MemberDao {
    public Page<Member> selectForPage(int pageNo, int pageSize) throws Exception {
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andLike("name", "23");
//        Page<Member>

        return null;
    }
}
