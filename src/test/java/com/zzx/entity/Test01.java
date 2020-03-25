package com.zzx.entity;

import com.zzx.entity.iface.TestInterface;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
}
