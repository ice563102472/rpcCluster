package com.linda.framework.rpc.cluster.generic;

import java.util.HashMap;

import com.linda.framework.rpc.cluster.etcd.EtcdRpcClient;
import com.linda.framework.rpc.generic.GenericService;
import com.linda.framework.rpc.utils.RpcUtils;
import com.linda.jetcd.JSONUtils;

public class EtcdGenericServiceTest {
	
	public static void main(String[] args) {
		
		EtcdRpcClient client = new EtcdRpcClient();
		client.setEtcdUrl("http://192.168.139.129:2911");
		client.setNamespace("lindezhi");
		client.startService();
		GenericService genericService = client.register(GenericService.class);
		int index = 10000;
		while(true){
			String[] getBeanTypes = new String[]{"com.linda.framework.rpc.cluster.TestBean","int"};
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("limit", index);
			map.put("offset", index*10);
			map.put("order", "index-"+index);
			map.put("message", "this is a test index+"+index);
			Object[] getBeanArgs = new Object[]{map,index*100+5};
			Object hh = genericService.invoke(null,"com.linda.framework.rpc.cluster.HelloRpcService", RpcUtils.DEFAULT_VERSION, "getBean", getBeanTypes, getBeanArgs);
			System.out.println(JSONUtils.toJSON(hh));
			index++;
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
