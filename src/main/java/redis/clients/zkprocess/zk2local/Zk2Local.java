package redis.clients.zkprocess.zk2local;

import java.io.InputStream;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import redis.clients.zkprocess.comm.ZookeeperProcessListen;
import redis.clients.zkprocess.entity.LpJedisClusterYamlBean;
import redis.clients.zkprocess.enums.ZkNofiflyCfg;
import redis.clients.zkprocess.enums.ZookeeperPath;
import redis.clients.zkprocess.zk2local.listen.ZkToLocalLoader;

public class Zk2Local {
	private static final Logger logger = LoggerFactory.getLogger(Zk2Local.class);
	public static final String LP_JEDISCLUSTER_YAML_PATH="META-INF/lpjedis-cluster.yaml";
	
	public static void loadZktoFile() throws Exception {
		try {
			// 加载zk总服务
			ZookeeperProcessListen zkListen = new ZookeeperProcessListen();
			
			LpJedisClusterYamlBean clusterYmalBean = getLpJedisClusterYaml();
			
			zkListen.setClusterYamlBean(clusterYmalBean);
			// 得到基本路径
			String basePath = getBasePath(clusterYmalBean.getSharding());
			zkListen.setBasePath(basePath);
			// 获得zk的连接信息
			CuratorFramework zkConn = buildConnection(clusterYmalBean.getZkNodes());
			new ZkToLocalLoader(zkListen, zkConn).addListen();
			
			// 加载通知进程
			zkListen.notifly(ZkNofiflyCfg.ZK_NOTIFLY_LOAD_ALL.getKey());
			// 加载watch
			loadZkWatch(zkListen.getWatchPath(), zkConn, zkListen);
		} catch (Exception e) {
			logger.warn("加载lpredis失败, {}", e.toString());
		}
	}
	
	private static String getBasePath(String sharding) {
		return ZookeeperPath.ZK_SEPARATOR.getKey() + ZookeeperPath.FLOW_ZK_PATH_REDIS.getKey() 
	    		+ ZookeeperPath.ZK_SEPARATOR.getKey() + sharding;
//		return ZookeeperPath.ZK_SEPARATOR.getKey() + ZookeeperPath.FLOW_ZK_PATH_BASE.getKey() 
//				+ ZookeeperPath.ZK_SEPARATOR.getKey() + sharding;
	}

	private static LpJedisClusterYamlBean getLpJedisClusterYaml() {
		LpJedisClusterYamlBean clusterYamlBean = null;
		try {
			InputStream in=ClassLoader.getSystemResourceAsStream(LP_JEDISCLUSTER_YAML_PATH);
			clusterYamlBean = new Yaml().loadAs(in, LpJedisClusterYamlBean.class);
			return clusterYamlBean;
		} catch (Exception e) {
			throw e;
		}
	}

	private static CuratorFramework buildConnection(String url) {
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
				url, new ExponentialBackoffRetry(1000, 6));

		// start connection
		curatorFramework.start();
		return curatorFramework;
	}
	
	private static void loadZkWatch(Set<String> setPaths,
			final CuratorFramework zkConn, final ZookeeperProcessListen zkListen)
			throws Exception {
		if (null != setPaths && !setPaths.isEmpty()) {
			for (String path : setPaths) {
				runWatch(zkConn, path, zkListen);
				logger.info("Zk2Local loadZkWatch path:" + path
						+ " regist success");
			}
		}
	}

	/**
	 * 进行zk的watch操作 方法描述
	 * 
	 * @param zkConn
	 *            zk的连接信息
	 * @param path
	 *            路径信息
	 * @param zkListen
	 *            监控路径信息
	 * @throws Exception
	 * @创建日期 2016年9月20日
	 */
	private static void runWatch(final CuratorFramework zkConn, String path,
			final ZookeeperProcessListen zkListen) throws Exception {
//		pathChildrenCache(zkConn, path, zkListen);
		treeCache(zkConn, path, zkListen);
	}
	
	private static void pathChildrenCache(CuratorFramework zkConn ,final String path,
			final ZookeeperProcessListen zkListen) throws Exception{
		PathChildrenCache childrenCache = new PathChildrenCache(zkConn, path, false);
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
					throws Exception {
				ChildData data = event.getData();
                if(data !=null){
                	if(event.getType()==PathChildrenCacheEvent.Type.CHILD_ADDED){
                		zkListen.watchPath(path, data.getPath());
                	}
                	logger.debug("--------------- event type {} path {} data {}",event.getType(), data.getPath(), data.getData());
                	zkListen.notifly(data.getPath());
                }else{  
                	logger.debug( "data is null : "+ event.getType());  
                }  
			}
		});
		childrenCache.start();
	}
	
	private static void treeCache(CuratorFramework zkConn ,final String path,
			final ZookeeperProcessListen zkListen) throws Exception{
		TreeCache treeCache = new TreeCache(zkConn,path);
		treeCache.getListenable().addListener(new TreeCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event)
					throws Exception {
				ChildData data = event.getData();
                if(data !=null){
                	if(event.getType()==TreeCacheEvent.Type.NODE_ADDED){
                		zkListen.watchPath(path, data.getPath());
                	}
                	logger.debug("--------------- event type {} path {} data {}",event.getType(), data.getPath(), data.getData());
                	zkListen.notifly(data.getPath());
                }else{  
                	logger.debug( "data is null : "+ event.getType());  
                }  
			}
		});
		treeCache.start();
	}
}
