package com.zzx.zk;

import org.apache.zookeeper.*;
import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 综合zk工具，异步创建节点和获取节点
 * 1.通过异步都方式，不需要关心KeeperException和InterruptedException这两个异常
 * 2.异步都方式不会阻塞zk处理线程
 */
public class AsynMasterAll implements Watcher {
    private ZooKeeper zk;
    private String hostPort;
    private String serverId;

    //异步创建节点时的回调函数
    private AsyncCallback.StringCallback createNodeCallback;
    //异步获取节点数据时的回调函数
    private AsyncCallback.DataCallback getNodeCallback;
    //异步获取节点是否存在回调函数
    private  AsyncCallback.StatCallback existsCallback;
    //获取自节点列表回调函数
    private AsyncCallback.ChildrenCallback childrenCallback;

    public AsynMasterAll(String hostPort){
        this.hostPort = hostPort;
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 150000,this);
        this.init();
    }

    private void init() {
        //创建节点回调函数,ctx:上下文透传参数
        createNodeCallback = (rc,path,ctx,name)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    getNode(path);
                    break;
                case OK:
                    //回调返回ok，则表示已经创建了节点，设置当前线程为主线程
                    serverId = (String)ctx;
                    System.out.println("节点创建成功，开始自己的业务逻辑：" + path);
                    break;
                case NODEEXISTS:
                    System.out.println("节点已存在：" + path);
                    break;
                default:
                    System.out.println("节点创建出现未知错误，放弃创建：" + path);
            }
        };

        //获取节点数据回调函数
        getNodeCallback = (rc,path,ctx,data,stat)->{
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    getNode(path);
                    break;
                case NONODE:
                    createNodeEphemeral(path,(String)ctx);
                    break;
                case OK:
                    System.out.println("节点获取成功，开始自己的业务逻辑：" + path);
                    //判断节点数据是否是当前节点创建的
                    String d = new String(data);
                    if (d.equals(serverId)){
                        //自己创建的节点，做自己的业务逻辑
                        System.out.println("节点是自己创建的，开始自己的业务逻辑：" + path);
                    }else {
                        //节点不是自己创建的，设置监听事件
                        System.out.println("节点不是自己创建的，开始处理逻辑：" + path);
                    }
                default:
                    System.out.println("节点获取出现未知错误，放弃获取：" + path);
            }
        };

        existsCallback = (rc,path,ctx,stat)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    existsNode(path);
                    break;
                case OK:
                    //回调返回ok，则表示节点已经存在
                    System.out.println("节点已经存在：" + path);
                    break;
                case NONODE:
                    System.out.println("节点不存在：" + path);
                    break;
                default:
                    System.out.println("节点创建出现未知错误，放弃创建：" + path);
            }
        };

        childrenCallback = (rc,path,ctx,children)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    getChildren(path);
                    break;
                case OK:
                    //回调返回ok，则表示节点已经存在
                    System.out.println("子节点获取成功：" + children.toString());
                    break;
                case NONODE:
                    System.out.println("节点不存在：" + path);
                    break;
                default:
                    System.out.println("节点创建出现未知错误，放弃创建：" + path);
            }
        };
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
    public void createNodeEphemeral(String path,String data) {
        //其中ctx参数为上下文参数，回调调时候会透传到回调函数中
        zk.create(path,data.getBytes(),OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,createNodeCallback,data);
    }

    public void createNodePersistent(String path,String data) {
        //其中ctx参数为上下文参数，回调调时候会透传到回调函数中
        zk.create(path,data.getBytes(),OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,createNodeCallback,data);
    }
    /**
     * 检查自身时都是主节点
     * @return
     */
    public void getNode(String path){
        zk.getData(path,false,getNodeCallback,null);
    }

    /**
     * 检查节点是否存在
     * @param path
     */
    public void existsNode(String path) {
        zk.exists(path,this,existsCallback,null);
    }

    /**
     * 获取子节点
     * @param path
     */
    public void getChildren(String path) {
        zk.getChildren(path,this,childrenCallback,null);
    }
}
