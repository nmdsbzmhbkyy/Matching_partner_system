package com.kaomao.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaomao.usercenter.model.domain.User;
import com.kaomao.usercenter.service.UserService;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
// @EnableScheduling
// @EnableAsync
@Slf4j
public class TimingCache {
    // implements ApplicationContextAware
    // private static ApplicationContext context;
    //
    // @Override
    // public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
    //     context = applicationContext;
    // }
    //
    // public static Object getBean(String name) {
    //     return context.getBean(name);
    // }

    @Resource
    UserService userService;
    @Resource
    RedisTemplate<String, List<User>> redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    //重点标签页列表
    private final List<List<String>> mainTagsList = Arrays.asList(Arrays.asList("java","python"),
                                                                  Arrays.asList("python","java"));
    @Scheduled(cron = "30 40 16 * * *")   //自己设置时间测试
    public void doCacheRecommendUser() {
        // UserService  userService =  (UserService) this.getBean("userService");
        // RedisTemplate<String, List<User>> redisTemplate = (RedisTemplate<String, List<User>>) this.getBean("redisTemplate");
        // RedissonClient redissonClient = (RedissonClient) this.getBean("redissonClient");
        RLock lock = redissonClient.getLock("kaomao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                ValueOperations<String, List<User>> valueOperations = redisTemplate.opsForValue();
                for (List<String> mainTag : mainTagsList) {
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    for (String tagName : mainTag) {
                        wrapper = wrapper.like("tags", tagName).or();
                    }
                    for (int i = 0; i < 5; i++) {
                        String pageNum = Integer.toString(i);
                        List<User> userList = userService.getUserListByPage(pageNum,"15",wrapper);
                        try {
                            String redisKey = String.format("kaopao:user:getByTagList:%s%s",mainTag.toString(),pageNum);
                            valueOperations.set(redisKey,userList,3, TimeUnit.MINUTES);
                        } catch (Exception e) {
                            log.error("redis set key error",e);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            /*
                参考源码可以猜到是通过线程id来判断是不是自己加的锁
                @Override
                public boolean isHeldByCurrentThread() {
                    return isHeldByThread(Thread.currentThread().getId());
                }
            */
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }

}
