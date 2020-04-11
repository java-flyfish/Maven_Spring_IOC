package com.zzx.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Master implements Watcher {

    ZooKeeper zk;
    String hostPort;
    public static String path = "/master";
    Boolean isLeader = false;
    Random random = new Random();
    String serverId = Integer.toHexString(random.nextInt());

    public Master(String hostPort){
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000,this);
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
    public void runForMaster() throws InterruptedException {
        while(true){
            try {
                zk.create(path,serverId.getBytes(),OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException e) {
                //KeeperException这个异常代表连接丢失，捕获到该异常时，我们并不知道节点创建是否已经成功，
                //因为有可能是为创建成功到时候抛出来到，也可能是成功了，返回结果到时候抛出来的
                //所以要在捕获这个异常后验证节点是否创建成功
                isLeader = false;
                break;
            } catch (KeeperException e) {
                if(chechMaster()){
                    break;
                }
            }
        }
    }

    /**
     * 检查自身时都是主节点
     * @return
     */
    public Boolean chechMaster(){
        while (true){
            try {
                byte[] data = zk.getData(java.lang.String.valueOf(path), false, new Stat());
                isLeader = new String(data).equals(serverId);
                return true;
            }catch (KeeperException.NoNodeException e) {
                isLeader = false;
                return false;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
