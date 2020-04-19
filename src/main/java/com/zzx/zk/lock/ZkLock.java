package com.zzx.zk.lock;

/**
 *
 */
public interface ZkLock {

    /**
     * 尝试获取锁，传入的对象是获取锁成功是需要执行的业务逻辑
     * @param lockSuccess
     */
    void tryLock(LockSuccess lockSuccess);
}
