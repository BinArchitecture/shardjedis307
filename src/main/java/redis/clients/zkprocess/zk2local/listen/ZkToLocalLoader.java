package redis.clients.zkprocess.zk2local.listen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.lppz.config.LpJedisInit;
import redis.clients.zkprocess.comm.NotiflyService;
import redis.clients.zkprocess.comm.ZookeeperProcessListen;
import redis.clients.zkprocess.entity.JedisBaseYamlBean;
import redis.clients.zkprocess.entity.LpJedisClusterYamlBean;
import redis.clients.zkprocess.utils.CollectionUtils;
import redis.clients.zkprocess.zookeeper.DiretoryInf;
import redis.clients.zkprocess.zookeeper.process.ZkDataImpl;
import redis.clients.zkprocess.zookeeper.process.ZkDirectoryImpl;
import redis.clients.zkprocess.zookeeper.process.ZkMultLoader;

/**
 * 
*/
public class ZkToLocalLoader extends ZkMultLoader implements NotiflyService {

    /**
     * 日志
    * @字段说明 LOGGER
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkToLocalLoader.class);

    /**
     * zk的监控路径信息
    * @字段说明 zookeeperListen
    */
    private ZookeeperProcessListen zookeeperListen;
    
    private String basePath;

    public ZkToLocalLoader(ZookeeperProcessListen zookeeperListen, CuratorFramework curator) {

        this.setCurator(curator);

        this.zookeeperListen = zookeeperListen;
        
        this.basePath = zookeeperListen.getBasePath();
    }
    
    public void addListen(){
    	// 将当前自己注册为事件接收对象
    	this.zookeeperListen.addListen(basePath, this);
    }
    
    public ZkToLocalLoader(CuratorFramework curator, String basePath) {
    	this.setCurator(curator);
	}

    @Override
    public Object notiflyProcess(boolean write) throws Exception {
        
        DiretoryInf baseDirectory = new ZkDirectoryImpl(basePath, null);
        // 进行递归的数据获取
        this.getTreeDirectory(basePath, null, baseDirectory);
        
        List<Object> subList=baseDirectory.getSubordinateInfo();
        if(CollectionUtils.isEmpty(subList))
        	return false;
        // 从当前的下一级开始进行遍历,获得到
        ZkDirectoryImpl zkDirectory = (ZkDirectoryImpl) subList.get(0);
        
        List<JedisBaseYamlBean> tmpjedisObjs = getJedisNode(zkDirectory, zookeeperListen.getClusterYamlBean());
        
        //根据zk上获取的哨兵配置初始化jedis
		LpJedisInit.getInstance().initJedis(tmpjedisObjs);

        LOGGER.debug("BastoresSwitchLoader notiflyProcess zk to object  zk basetoreSwitch Object  :");
        return true;
    }

	private List<JedisBaseYamlBean> getJedisNode(DiretoryInf baseDir, LpJedisClusterYamlBean clusterYamlBean) {
		ZkDataImpl tmpSubNode = null;
		ZkDirectoryImpl tmpDir = null;
		List<JedisBaseYamlBean> jedisBeans = new ArrayList<>();
		JedisBaseYamlBean tmp  = null;
		Properties properties = null;
		List<Object> nodeList = baseDir.getSubordinateInfo();
		
		if (CollectionUtils.isNotEmpty(nodeList)) {
			for (Object obj : nodeList) {
				if(obj instanceof ZkDirectoryImpl){
					tmpDir = (ZkDirectoryImpl) obj;
					List<Object> nodes = tmpDir.getSubordinateInfo();
					if (CollectionUtils.isNotEmpty(nodes)) {
						for (Object node : nodes) {
							tmpSubNode = (ZkDataImpl) node;
							if("M".equals(tmpSubNode.getDataValue())){
								tmp = new JedisBaseYamlBean();
								tmp.setJedisClusterPool(clusterYamlBean.getJedisClusterPool());
								tmp.setTimeout(clusterYamlBean.getTimeout());
								String [] MasterIpAndPort = tmpSubNode.getName().split(":");
								properties = new Properties();
								properties.put("host", MasterIpAndPort[0]);
								properties.put("port", Integer.valueOf(MasterIpAndPort[1]));
								tmp.setJedisClusterNode(Arrays.asList(properties));
							}
							LOGGER.info("{}",tmpSubNode);
						}
					}
				}
			}
		}
		return jedisBeans;
	}

}
