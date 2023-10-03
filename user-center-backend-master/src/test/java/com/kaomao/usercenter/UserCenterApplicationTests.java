package com.kaomao.usercenter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaomao.usercenter.model.domain.User;
import com.kaomao.usercenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 启动类测试
 */
@SpringBootTest
class UserCenterApplicationTests {
    @Autowired
    UserService userService;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }
    @Test
    void testResdission(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Map<String, Integer> map = new HashMap<>();
        map.put("yupi", 10);
        map.put("kaopao",0);
        valueOperations.set("测试",map,3, TimeUnit.MINUTES);
        // valueOperations.append("测试","你好");
        RMap<Object, Object> map1 = redissonClient.getMap("test-map");
        map1.put("kaomao",1);
        map1.put("hh",0);
    }

    @Test
    void contextLoads() {
        Page<User> page = new Page<>(0, 12);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> resultPage = userService.page(page, queryWrapper);

        Page<User> page1 = new Page<>(12, 12);
        Page<User> resultPage1 = userService.page(page1, queryWrapper);
    }

}
