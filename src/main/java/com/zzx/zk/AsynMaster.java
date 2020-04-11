package com.zzx.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 异步创建节点和获取节点
 * 1.通过异步都方式，不需要关心KeeperException和InterruptedException这两个异常
 * 2.异步都方式不会阻塞zk处理线程
 */
public class AsynMaster implements Watcher {
    ZooKeeper zk;
    String hostPort;
    public static String path = "/master";
    Boolean isLeader = false;
    Random random = new Random();
    String serverId = Integer.toHexString(random.nextInt());

    //异步创建节点时的回调函数
    AsyncCallback.StringCallback masterCreatedCallback;
    //异步获取节点数据时的回调函数
    AsyncCallback.DataCallback masterDataCallback;

    public AsynMaster(String hostPort){
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000,this);
        //创建节点回调函数,ctx:上下文透传参数
        masterCreatedCallback = (rc,path,ctx,name)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点是否创建成功
                    chechMaster();
                    return;
                case OK:
                    //回调返回ok，则表示已经创建了节点，设置当前线程为主线程
                    isLeader = true;
                    break;
                default:
                    isLeader = false;
            }
            System.out.println("我" + (isLeader ? "是" : "不是") + "leader");
        };

        //获取节点数据回调函数
        masterDataCallback = (rc,path,ctx,data,stat)->{
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    chechMaster();
                    return;
                case NONODE:
                    runForMaster();
                    return;
            }
        };
    }

    @Override
    public void process(WatchedEvent e) {
        System.out.println("监听到zk事件：" + e);
    }

    /**
     * 创建一个master节点
     * 1.zk创建节点时会跑出两个异常，KeeperException和InterruptedException，这两个异常都需要处理
     * 2.KeeperException这个异常代表连接丢失，捕获到该异常时，我们并不知道节点创建是否已经成功，
     *      因为有可能是为创建成功到时候抛出来到，也可能是成功了，返回结果到时候抛出来的
     *      所以要在捕获这个异常后验证节点是否创建成功
     * 3.
     */
    public void runForMaster() {
        //其中ctx参数为上下文参数，回调调时候会透传到回调函数中
        zk.create(path,serverId.getBytes(),OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,masterCreatedCallback,null);
    }

    /**
     * 检查自身时都是主节点
     * @return
     */
    public void chechMaster(){
        zk.getData(path,false,masterDataCallback,null);
    }
}
