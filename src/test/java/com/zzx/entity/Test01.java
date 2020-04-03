package com.zzx.entity;

import com.zzx.entity.iface.TestInterface;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Info:
 * @ClassName: Test01
 * @Author: weiyang
 * @Data: 2020/3/11 10:06 AM
 * @Version: V1.0
 **/
public class Test01 {

    @Test
    public void studentTest() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        Student s1 = applicationContext.getBean("s1", Student.class);
        Student s2 = applicationContext.getBean("s2", Student.class);
        System.out.println(s1); System.out.println(s2);

        TestInterface testInterface = applicationContext.getBean("TestInterface", TestInterface.class);
        testInterface.doSomeThing();
    }
    @Test
    public void testLock() {
        /**
         * Lock实现类ReentrantLock
         * 内部封装了一个AQS的静态实现，公平锁FairSync和非公平锁NonfairSync，默认是NonfairSync
         * lock()方法实际上是调用的Sync的lock()方法
         * 公平锁FairSync和非公平锁NonfairSync的lock()方法实现方式不一样，
         * 获取锁实际上是设置state的过程，设置成功了，表示拿到锁，否则进入等待队列
         * 当一个线程重复获取锁时，state会加一，代表当前线程多次获取锁，释放锁减一，当state=0时，表示没有锁占用
         * 公平锁是先到先得，非公平锁是释放锁后所有线程一起竞争锁
         */
        Lock lock = new ReentrantLock();
        lock.lock();
    }

}
