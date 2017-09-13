package redis.clients.zkprocess.entity;

import org.yaml.snakeyaml.Yaml;

/**
 * lppz 基于zk集群管理的jedis加载配置bean
 * @author licheng
 *
 */
public class LpJedisClusterYamlBean {
	
	private String zkNodes;
	
	private String sharding;
	
	private String redisNodePrefix;
	
	private JedisClusterPool jedisClusterPool;
	
	private Integer timeout;

	public String getZkNodes() {
		return zkNodes;
	}

	public void setZkNodes(String zkNodes) {
		this.zkNodes = zkNodes;
	}

	public JedisClusterPool getJedisClusterPool() {
		return jedisClusterPool;
	}

	public void setJedisClusterPool(JedisClusterPool jedisClusterPool) {
		this.jedisClusterPool = jedisClusterPool;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getSharding() {
		return sharding;
	}

	public void setSharding(String sharding) {
		this.sharding = sharding;
	}

	public String getRedisNodePrefix() {
		return redisNodePrefix;
	}

	public void setRedisNodePrefix(String redisNodePrefix) {
		this.redisNodePrefix = redisNodePrefix;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LpJedisClusterYamlBean [zkNodes=");
		builder.append(zkNodes);
		builder.append(", sharding=");
		builder.append(sharding);
		builder.append(", redisNodePrefix=");
		builder.append(redisNodePrefix);
		builder.append(", jedisClusterPool=");
		builder.append(jedisClusterPool);
		builder.append(", timeout=");
		builder.append(timeout);
		builder.append("]");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		LpJedisClusterYamlBean bean = new LpJedisClusterYamlBean();
		bean.setSharding("");
		bean.setRedisNodePrefix("node");
		bean.setZkNodes("192.168.37.243:2181");
		JedisClusterPool jedisClusterPool = new JedisClusterPool();
		jedisClusterPool.setBlockWhenExhausted(false);
		jedisClusterPool.setMaxIdle("5000");
		jedisClusterPool.setMaxTotal("50000");
		jedisClusterPool.setMaxWaitMillis("10000");
		jedisClusterPool.setNumTestsPerEvictionRun("100");
		jedisClusterPool.setTestOnBorrow(false);
		jedisClusterPool.setTestOnReturn(false);
		bean.setJedisClusterPool(jedisClusterPool);
		bean.setTimeout(5000);
		
		System.out.println(new Yaml().dump(bean));
	}
}
