package com.zzx.zk.lock;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * zk分布式锁的实现
 */
public class ZkAsynMaster  implements Watcher {
    private ZooKeeper zk;
    private String hostPort;
    private String serverId;
    private String masterPath;



    public ZkAsynMaster(String hostPort,String serverId,String masterPath){
        this.hostPort = hostPort;
        this.serverId = serverId;
        this.masterPath = masterPath;
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 150000,this);
    }



    @Override
    public void process(WatchedEvent e) {
        System.out.println("监听到zk事件：" + e);
        //Event.EventType.NodeCreated：节点创建
        //Event.EventType.NodeDataChanged：节点数据变更
        //Event.EventType.NodeDeleted：节点删除
        if (e.getType().equals(Event.EventType.NodeChildrenChanged)){
            //子节点变更通知
            System.out.println("监听到zk事件，子节点数据变更" + e);
        }

    }

    /**
     * 创建一个master节点
     * 1.zk创建节点时会跑出两个异常，KeeperException和InterruptedException，这两个异常都需要处理
     * 2.KeeperException这个异常代表连接丢失，捕获到该异常时，我们并不知道节点创建是否已经成功，
     *      因为有可能是为创建成功到时候抛出来到，也可能是成功了，返回结果到时候抛出来的
     *      所以要在捕获这个异常后验证节点是否创建成功
     * 3.
     */
    public void createNodeEphemeral(String path,AsyncCallback.StringCallback createNodeCallback, String data) {
        //其中ctx参数为上下文参数，回调调时候会透传到回调函数中
        zk.create(path,data.getBytes(),OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,createNodeCallback,data);
    }

    public void createNodePersistent(String path,AsyncCallback.StringCallback createNodeCallback, String data) {
        //其中ctx参数为上下文参数，回调调时候会透传到回调函数中
        zk.create(path,data.getBytes(),OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,createNodeCallback,data);
    }
    /**
     * 检查自身时都是主节点
     * @return
     */
    public void getNode(String path,AsyncCallback.DataCallback getNodeCallback){
        zk.getData(path,false,getNodeCallback,null);
    }

    /**
     * 检查节点是否存在
     * @param path
     */
    public void existsNode(String path,AsyncCallback.StatCallback existsCallback) {
        zk.exists(path,this,existsCallback,null);
    }

    /**
     * 获取子节点
     * @param path
     */
    public void getChildren(String path,AsyncCallback.ChildrenCallback childrenCallback) {
        zk.getChildren(path,this,childrenCallback,null);
    }
}
