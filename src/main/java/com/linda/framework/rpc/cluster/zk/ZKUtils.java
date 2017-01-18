package com.linda.framework.rpc.cluster.zk;

import com.linda.framework.rpc.exception.RpcException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.Random;

/**
 * Created by lin on 2016/12/21.
 */
public class ZKUtils {


    public static String genWeightKey(String application,String hostkey){
        return "/weights/"+application+"/"+hostkey;
    }

    public static String genApplicationWeightsKey(String application){
        return "/weights/"+application;
    }

    public static String genServerKey(String serverMd5) {
        return "/servers/" + serverMd5;
    }

    public static String genServerServiceKey(String serverMd5) {
        return "/services/" + serverMd5;
    }

    public static String genServiceKey(String serverMd5,String serviceMd5) {
        return "/services/" + serverMd5 + "/" + serviceMd5;
    }

    /**
     * 设置权重列表
     * @param application
     * @param key
     * @param weight
     * @param override
     */
    public static void doSetWehgit(CuratorFramework zkclient,String application, String key, int weight, boolean override){
        String path = genWeightKey(application,key);
        byte[] data = (""+weight).getBytes();
        if(override){
            try{
                zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
            }catch(Exception e){
                throw new RpcException(e);
            }

        }else{
            try{
                byte[] bytes = zkclient.getData().forPath(path);
                if(bytes==null){
                    zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
                }
                return;
            }catch(Exception e){
                if(e instanceof KeeperException.NoNodeException){
                    try {
                        zkclient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,data);
                    } catch (Exception e1) {
                        throw new RpcException(e1);
                    }
                }
            }
        }
        //通过weight app data通知
        //notify change
        String applicationWeightsKey = genApplicationWeightsKey(application);
        int idx = new Random().nextInt(100000000);
        byte[] appData = (application+"_"+idx).getBytes();
        try {
            zkclient.setData().forPath(applicationWeightsKey,appData);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }
}
