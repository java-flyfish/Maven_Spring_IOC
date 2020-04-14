package com.zzx.zk;

import org.junit.Test;

import java.io.IOException;

public class MasterTest {
    /**
     *  在这个例子中，我们模拟一个需要有一个进程获取到管理权的例子
     *  1.这是同步的方式
     */
    @Test
    public void testMaster() {
        String hostPort = "49.235.174.180:2182,49.235.174.180:2183,49.235.174.180:2184";
        Master master = new Master(hostPort);
        try {
            master.startZK();
            master.runForMaster();
            if (master.isLeader){
                System.out.println("我是master：" + master.serverId);
                Thread.sleep(60000l);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  在这个例子中，我们模拟一个需要有一个进程获取到管理权的例子
     *  1.这是异步的方式，
     *  2.zk分布式锁的实现推荐使用异步方式，性能更好
     */
    @Test
    public void testAsynMaster() {
        String hostPort = "49.235.174.180:2182,49.235.174.180:2183,49.235.174.180:2184";
        AsynMaster master = new AsynMaster(hostPort);
        try {
            master.startZK();
            master.runForMaster();
            Thread.sleep(60000l);
            if (master.isLeader){
                System.out.println("我是master：" + master.serverId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void createParent(){
        String hostPort = "49.235.174.180:2182,49.235.174.180:2183,49.235.174.180:2184";
        ZkBaseData zk = new ZkBaseData(hostPort);
        try {
            zk.startZK();
            zk.createParent("/workers",new byte[0]);
            zk.createParent("/assign",new byte[0]);
            zk.createParent("/tasks",new byte[0]);
            zk.createParent("/status",new byte[0]);
            Thread.sleep(60000l);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void createAll(){
        String hostPort = "49.235.174.180:2182,49.235.174.180:2183,49.235.174.180:2184";
        AsynMasterAll zk = new AsynMasterAll(hostPort);
        try {
            zk.startZK();
            zk.createNodePersistent("/father","");
            zk.createNodeEphemeral("/father/child01-","first node 1");
            zk.createNodeEphemeral("/father/child01-","first node 2");
            zk.createNodeEphemeral("/father/child01-","first node 3");
            Thread.sleep(1000l);
            zk.getChildren("/father");
            Thread.sleep(60000l);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
