package redis.clients.jedis;

import org.junit.Test;

import redis.clients.lppz.config.LpJedisInit;
import redis.clients.lppz.config.ShardingFunction;
import redis.clients.lppz.jedis.LpJedis;
import redis.clients.zkprocess.zk2local.Zk2Local;

public class LpJedisClusterTest {
	
	@Test
	public void zkLoadTest() throws Exception{
		LpJedis jedis=null;
		try {
			Zk2Local.loadZktoFile();
			jedis = LpJedisInit.getInstance().getJedis();
		} catch (Exception e) {
		}
		final String key = "testKey";
		String value = "ttttt";
		ShardingFunction s = new ShardingFunction() {
			
			@Override
			public int getShardingIndex() {
				return Math.abs(key.hashCode());
			}
		};
		if (jedis != null) {
			jedis.set(s, key, value);
			
			System.out.println(jedis.get(s, key));
		}
	}
	
	

}
