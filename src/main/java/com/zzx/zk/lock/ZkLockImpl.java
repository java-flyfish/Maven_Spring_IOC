package com.zzx.zk.lock;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;

/**
 * 基于zk原始api的锁，还没完成
 */
public class ZkLockImpl implements ZkLock {

    private String hostPort;
    private String serverId;
    private ZkAsynMaster zkAsynMaster;

    private String masterPath = "/master";
    private String sleverPath = "/lock-";

    /**
     *  异步创建节点时的回调函数
     */
    private AsyncCallback.StringCallback createPersistentNodeCallback;
    /**
     *  异步获取节点数据时的回调函数
     */
    private AsyncCallback.DataCallback getPersistentNodeCallback;

    /**
     *  异步创建节点时的回调函数
     */
    private AsyncCallback.StringCallback createEphemeralNodeCallback;
    /**
     *  异步获取节点数据时的回调函数
     */
    private AsyncCallback.DataCallback getEphemeralNodeCallback;

    /**
     *  异步获取节点是否存在回调函数
     */
    private  AsyncCallback.StatCallback existsCallback;
    /**
     * 获取子节点列表回调函数
     */
    private AsyncCallback.ChildrenCallback childrenCallback;



    @Override
    public void tryLock(LockSuccess lockSuccess) {
        //会创建一个临时有序节点
        this.createNodeEphemeral(this.masterPath + this.sleverPath,lockSuccess,serverId);
    }

    public ZkLockImpl(String hostPort) throws IOException {
        this.hostPort = hostPort;
        Random random = new Random();
        this.serverId = Integer.toHexString(random.nextInt());
        this.init();

    }

    public ZkLockImpl(String hostPort,String masterPath,String sleverPath) throws IOException {
        this.hostPort = hostPort;
        Random random = new Random();
        this.serverId = Integer.toHexString(random.nextInt());
        this.masterPath = masterPath;
        this.sleverPath = sleverPath;
        this.init();
    }

    /**
     * 初始化
     * @throws IOException
     */
    public void init() throws IOException {
        ZkAsynMaster zkAsynMaster = new ZkAsynMaster(hostPort, serverId, masterPath);
        zkAsynMaster.startZK();

        //创建节点回调函数,ctx:上下文透传参数
        createEphemeralNodeCallback = (rc,path,ctx,name)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    this.getNode(path,this.getEphemeralNodeCallback);
                    break;
                case OK:
                    //回调返回ok，则表示已经创建了节点，设置当前线程为主线程
                    System.out.println("临时节点创建成功：" + path);
                    break;
                case NODEEXISTS:
                    System.out.println("临时节点已存在：" + path);
                    break;
                default:
                    System.out.println("临时节点创建出现未知错误，放弃创建：" + path +"; 错误信息：" + KeeperException.Code.get(rc));
                    break;
            }
        };

        //获取节点数据回调函数
        getEphemeralNodeCallback = (rc,path,ctx,data,stat)->{
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    this.getNode(path,this.getEphemeralNodeCallback);
                    break;
                case NONODE:
                    this.createNodeEphemeral(path,createEphemeralNodeCallback,"");
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
                    break;
                default:
                    System.out.println("节点获取出现未知错误，放弃获取：" + path);
                    break;
            }
        };


        //创建节点回调函数,ctx:上下文透传参数
        createPersistentNodeCallback = (rc,path,ctx,name)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    this.getNode(path,this.getPersistentNodeCallback);
                    break;
                case OK:
                    //回调返回ok，则表示已经创建了节点，设置当前线程为主线程
                    System.out.println("主节点创建成功：" + path);
                    break;
                case NODEEXISTS:
                    System.out.println("主节点已存在：" + path);
                    break;
                default:
                    System.out.println("主节点创建出现未知错误，放弃创建：" + path +"; 错误信息：" + KeeperException.Code.get(rc));
                    break;
            }
        };

        //获取节点数据回调函数
        getPersistentNodeCallback = (rc,path,ctx,data,stat)->{
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    getNode(path,this.getPersistentNodeCallback);
                    break;
                case NONODE:
                    this.createNodePersistent(path,createPersistentNodeCallback);
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
                    break;
                default:
                    System.out.println("节点获取出现未知错误，放弃获取：" + path);
                    break;
            }
        };

        existsCallback = (rc,path,ctx,stat)->{
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //连接丢失，检查节点创建
                    this.existsNode(path,this.existsCallback);
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
                    this.getChildren(path,this.childrenCallback);
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
        //初始化完成
        System.out.println("zk初始化完成，开始创建分布式锁主节点》》》");
        //开始创建锁的主节点（永久节点），如果没有就创建，如果已经存在，则不再创建
        this.createNodePersistent(this.masterPath, this.createPersistentNodeCallback);
    }

    private void existsNode(String path, AsyncCallback.StatCallback existsCallback) {
        zkAsynMaster.existsNode(path,existsCallback);
    }

    private void createNodeEphemeral(String path, AsyncCallback.StringCallback createNodeCallback, String ctx) {
        zkAsynMaster.createNodeEphemeral(path,createNodeCallback,ctx);
    }

    private void createNodePersistent(String path, AsyncCallback.StringCallback createNodeCallback) {
        zkAsynMaster.createNodePersistent(path,createNodeCallback,"");
    }

    private void getNode(String path, AsyncCallback.DataCallback getNodeCallback) {
        zkAsynMaster.getNode(path,getNodeCallback);
    }
    private void getChildren(String path, AsyncCallback.ChildrenCallback childrenCallback) {
        zkAsynMaster.getChildren(path,childrenCallback);
    }
}
