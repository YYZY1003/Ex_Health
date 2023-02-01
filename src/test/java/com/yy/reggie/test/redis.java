package com.yy.reggie.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.Set;
@SpringBootTest
@RunWith(SpringRunner.class)
public class redis {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedis(){
        //获取连接
        Jedis jedis = new Jedis("localhost",6379);

//        //执行具体操作
//        jedis.set("username","YY");
//        System.out.println(jedis.get("username"));

        jedis.hset("myhash","addr","gz");
        System.out.println("jedis.hget(\"myhash\",\"addr\") = " + jedis.hget("myhash", "addr"));

//        Set<String> keys = jedis.keys("*");
//        for (String key : keys) {
//            System.out.println("key = " + key);
//        }

        //关闭连接
        jedis.close();
    }

    @Test
    public void redisTest(){
        redisTemplate.opsForValue().set("city","贵州");
    }

}
