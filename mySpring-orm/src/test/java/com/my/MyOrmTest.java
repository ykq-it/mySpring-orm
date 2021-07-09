package com.my;

import com.alibaba.fastjson.JSON;
import com.my.dao.MemberDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mycore.common.Page;

/**
 * @ClassName MyOrmTest
 * @Description 本项目不是springboot项目，使用@ContextConfiguration测试
 * @Author ykq
 * @Date 2021/06/04
 * @Version v1.0.0
 */
@Slf4j
@ContextConfiguration()
@RunWith(SpringJUnit4ClassRunner.class)
public class MyOrmTest {

    @Autowired
    private MemberDao memberDao;

    @Test
    public void testSelectForPage() {
        try {
            Page page = memberDao.selectForPage(2, 3);
            log.info("总条数： " + page.getTotal());
            log.info("当前第几页：" + page.getPageNo());
            log.info("每页多少条：" + page.getPageSize());
            log.info("本页的数据：" + JSON.toJSONString(page.getRows(),true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
