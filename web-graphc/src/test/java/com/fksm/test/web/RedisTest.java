package com.fksm.test.web;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2016/5/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class RedisTest {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> template;
    private String stringKey = "stringKey";
    private String listKey = "listKey";
    /**
     *
     *
     * @auth nibili 2015年5月13日
     */
    @Test
    public void sendString() {
        try {
            template.boundValueOps(stringKey).set("hello", 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     *
     *
     * @auth nibili 2015年5月13日
     */
    @Test
    public void getString() {
        try {
            String temp = template.boundValueOps(stringKey).get();
            System.out.println("key:" + stringKey + "，value:" + temp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void pushList() {
        try {
            template.boundListOps(listKey).leftPush("list1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void getList() {
        try {
            System.out.println(template.boundListOps(listKey).leftPop());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
