package com.zzx.zk;

import org.junit.Test;

public class MasterTest {
    /**
     *  在这个例子中，我们模拟一个需要有一个进程获取到管理权的例子
     *  1.
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
     *  1.
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
}
