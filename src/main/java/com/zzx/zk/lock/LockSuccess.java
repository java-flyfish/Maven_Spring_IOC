package com.zzx.zk.lock;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;

public abstract class LockSuccess implements AsyncCallback.StringCallback {
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

    public LockSuccess() {//创建节点回调函数,ctx:上下文透传参数
        /*this.createEphemeralNodeCallback = (rc,path,ctx,name)->{
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
                    getNode(path,this.getEphemeralNodeCallback);
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
        };*/
        //初始化完成
        System.out.println("zk初始化完成，开始创建分布式锁主节点》》》");}

}
