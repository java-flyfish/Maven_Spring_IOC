package com.zzx.zk;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

/**
 * 创建元数据
 */
public class ZkBaseData implements Watcher {
    ZooKeeper zk;
    String hostPort;
    Random random = new Random();
    String serverId = Integer.toHexString(random.nextInt());

    /**
     * 异步创建节点时的回调函数
     */
    AsyncCallback.StringCallback parentCreatedCallback;

    public ZkBaseData(String hostPort){
        this.hostPort = hostPort;
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000,this);
        this.init();
    }

    @Override
    public void process(WatchedEvent e) {
        System.out.println("监听到zk事件：" + e);
    }

    public void init(){
        //创建节点回调函数,ctx:上下文透传参数
        parentCreatedCallback = (rc,path,ctx,name)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点是否创建成功
                    System.out.println("连接丢失，重新创建节点：" + path);
                    createParent(path,(byte[])ctx);
                    break;
                case NODEEXISTS:
                    System.out.println("节点已经存在：" + path);
                    break;
                case OK:
                    //回调返回ok，则表示已经创建了节点，设置当前线程为主线程
                    System.out.println("节点创建成功，path：" + path);
                    break;
                default:
                    System.out.println("出现了某些错误：" + KeeperException.create(KeeperException.Code.get(rc),path).getMessage());
            }
        };
    }

    public void createParent(String path,byte[] data){
        this.zk.create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT,parentCreatedCallback,data);
    }
}
