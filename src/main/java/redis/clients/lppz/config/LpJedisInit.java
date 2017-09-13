package redis.clients.lppz.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.lppz.jedis.LpJedis;
import redis.clients.zkprocess.entity.JedisBaseYamlBean;

public class LpJedisInit {
	private final static Logger logger = LoggerFactory.getLogger(LpJedisInit.class);
	public static final List<String> support_multi_commands = Arrays.asList("del","mset","msetnx","watch","mget","smembersmulti","zoff","hgetallmulti");
	public static final List<String> support_multi_keyvalue_commands = Arrays.asList("mset","msetnx");
	public static final List<String> refuse_multi_commands = Arrays.asList("rpoplpush","brpoplpush","smove","sinter","sinterstore"
			,"sunion","sunionstore","sdiff","sdiffstore","rename","renamenx","blpop","brpop"
			,"zunionstore","zunionstore","zinterstore","zinterstore","bitop");
	public static final List<String> script_commands = Arrays.asList("eval", "evalsha", "scriptExists", "scriptLoad");
	private LpJedisInit(){}
	private static LpJedisInit instance;
	private LpJedis jedis;
	public static LpJedisInit getInstance(){
		if(instance == null){
			instance = new LpJedisInit();
		}
		return instance;
	}
	
	public void initJedis(List<JedisBaseYamlBean> tmpjedisObjs){
		jedis = buildJedisSentinelProxy(tmpjedisObjs);
	}
	
	public LpJedis getJedis(){
		return jedis;
	}
	
	private LpJedis buildJedisSentinelProxy(List<JedisBaseYamlBean> tmpjedisObjs) {
		List<Jedis> jedisList = new ArrayList<>();
		for (JedisBaseYamlBean tmpjedisObj : tmpjedisObjs) {
			if (tmpjedisObj == null || "".equals(tmpjedisObj)) {
				throw new RuntimeException("jedis need a set of sentinel nodes!!!");
			} else {
				List<Properties> properties = tmpjedisObj.getJedisClusterNode();
				if (properties != null) {
					for (Properties pro : properties) {
						Jedis jedis = new Jedis(pro.getProperty("host"), (Integer)pro.get("port"),tmpjedisObj.getTimeout());
						jedisList.add(jedis);
					}
				}
			}
		}
		return buildProxyRedis(jedisList);
	}
	
	private LpJedis buildProxyRedis(final List<Jedis> jedisList) {
		LpJedis lpJedis = new LpJedis();
		final List<Jedis> jedisPool = new ArrayList<>();
		lpJedis.setJedisPool(jedisPool);
		final ExecutorService threadPool = Executors.newFixedThreadPool(10);
		final AtomicInteger count = new AtomicInteger();
		for (Jedis jedis : jedisList) {
			final Jedis tmpJedis = jedis;
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						Enhancer enhancer = new Enhancer();
						enhancer.setSuperclass(Jedis.class);
						enhancer.setCallback(new MethodInterceptor(){
							public Object intercept(Object proxy, Method method, Object[] args,
									MethodProxy methodProxy) throws Throwable {
								return execute(method, args,tmpJedis);
							}
						});
						enhancer.setClassLoader(Jedis.class.getClassLoader());
						jedisPool.add((Jedis) enhancer.create());
					} catch (Exception e) {
						logger.error(e.toString(),e);
					}finally{
						count.incrementAndGet();
					}
				}
			});
		}
		
		while (count.get() < jedisList.size()) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}
		}
		
		return lpJedis;
	}
	
	private Object execute(final Method method, final Object[] args,
			final Jedis jesenpool) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String methodName = method.getName();
		if (refuse_multi_commands.indexOf(methodName) > -1) {
			throw new RuntimeException("not support command");
		}else{
			boolean isRetry = false;
			int retryNum = 5;
			do {
				try {
					return invoke(jesenpool,method,args);
				} catch (Exception e) {
					if (retryNum-- > 0) {
						isRetry = true;
					}
				}
			} while (isRetry);
			return null;
		}
	}

	private Map<Integer, Object[]> groupArgs(String methodName, Object[] args, int size) {
		Map<Integer, Object[]> groupMap = new HashMap<>();
		Map<Integer, List<Object>> tmpGroupMap = new HashMap<>();
		//参数中key-value以一个数组形式传入的情况
		if (support_multi_keyvalue_commands.indexOf(methodName) > -1) {
			Object[] keys = new Object[args.length / 2];
			Object[] values = new Object[args.length / 2];
			
			for (int keyIdx = 0; keyIdx < keys.length; keyIdx++) {
				keys[keyIdx] = args[keyIdx * 2];
				values[keyIdx] = args[keyIdx * 2 + 1];
			}
			for (int i = 0; i < keys.length; i++) {
				Integer index =getShardindex(keys[i], size);
				List<Object> tmpList = tmpGroupMap.get(index);
				if (tmpList==null) {
					tmpList = new ArrayList<>();
					tmpGroupMap.put(index, tmpList);
				}
				tmpList.add(keys[i]);
				tmpList.add(values[i]);
			}
		}else{
			for (Object obj : args) {
				Integer index =getShardindex(obj, size);
				List<Object> tmpList = tmpGroupMap.get(index);
				if (tmpList==null) {
					tmpList = new ArrayList<>();
					tmpGroupMap.put(index, tmpList);
				}
				tmpList.add(obj);
			}
		}
		Set<Entry<Integer, List<Object>>> set = tmpGroupMap.entrySet();
		for (Entry<Integer, List<Object>> entry : set) {
			groupMap.put(entry.getKey(), entry.getValue().toArray());
		}
		return groupMap;
	}

	private Object invoke(Jedis jedis, Method method, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		try{
			return method.invoke(jedis,args);
		}finally{
			jedis.close();
		}
	}

	@Deprecated
	private int getShardindex(Object object, int size) {
		if (object instanceof String) {
			int hashValue = Math.abs(object.hashCode());
			return hashValue % size;
		}
		throw new RuntimeException("first parameter is not String key");
	}
}
