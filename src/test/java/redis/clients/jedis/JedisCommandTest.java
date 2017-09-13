package redis.clients.jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class JedisCommandTest {

	@Test  
	public void xpushTest() {
		  Jedis jed = new Jedis("192.168.37.246", 7381);
		//添加  
		  /*jed.xpush("userxx","liuling0");
		  jed.xpush("userxx","liuling1");
		  jed.xpush("userxx","liuling2");
		  jed.xpush("userxx","liuling3");
		  jed.xpush("userxx","liuling4");
		  jed.xpush("userxx","liuling5");
		  jed.xpush("userxx","liuling6");
		  jed.xpush("userxx","liuling7");
		  jed.xpush("userxx","liuling8");
		  jed.xpush("userxx","liuling9");
		  */
		  jed.xpush("userxx","liuling9");
	      System.out.println(jed.lrange("userxx",0,-1));
	      //System.out.println(jed.smembers("user"));
	 }

	@Test  
	public void xresetTest() {
		  Jedis jed = new Jedis("192.168.37.246", 7381);
		//添加  
	      jed.xreset("userxx","5");   
	      System.out.println(jed.lrange("userxx",0,-1));
	      //System.out.println(jed.smembers("user"));
	 }
	
	@Test  
	public void smembersmultiTest() {
		  Jedis jed = new Jedis("192.168.37.246", 7381);
		//添加  
//	      jed.sadd("user","liuling");  
//	      jed.sadd("user","xinxin");  
//	      jed.sadd("user1","ling");  
//	      jed.sadd("user1","zhangxinxin");
	      //jedis.sadd("user","who");  
	      //移除noname  
	      //jedis.srem("user","who");  
	      System.out.println(jed.smembersmulti("user","user1"));
	      System.out.println();
	 }
	
	@Test  
	public void zoffTest() {
		  Jedis jed = new Jedis("192.168.37.246", 7381);
		//添加  
	      jed.zoff("zuser",100,"liuling");  
	      jed.zoff("zuser",120,"xinxin");  
	      jed.zoff("zuser",130,"ling");  
	      jed.zoff("zuser",140,"zhangxinxin");
//		  Map<String,Double> scoreMembers = new HashMap<String,Double>();
//		  scoreMembers.put("Python",90d);
//		  scoreMembers.put("Javascript",80d);
//		  scoreMembers.put("Scala",70d);
//		  jed.zoff("zuser", scoreMembers);
//	      System.out.println(jed.zcard("zuser"));
//	      System.out.println(jed.zrange("zuser", 0, -1));
	      //System.out.println(jed.smembers("user"));
	 }
	
	@Test  
	public void zresetTest() {
		  Jedis jed = new Jedis("192.168.37.246", 7381);
	      jed.zreset("zuser","5");   
//	      System.out.println(jed.zrange("zuser",0,-1));
	 }
	
	@SuppressWarnings("rawtypes")
	@Test  
	public void hgetallmultiTest() {
		Jedis jed = new Jedis("192.168.37.246", 7381);
		  //添加  
//		  Map map = new HashMap(); 
//	      map.put("cardid", "123456");   
//	      map.put("username", "jzkangta");   
//	      jed.hmset("hash", map);
//	      jed.hmset("hash1", map);
	      //jed.hgetallmulti("hash","hash1");
	      Map<String, Map<String, String>> m = jed.hgetallmulti("hash","hash1");
	   
	    	  Set set = m.keySet();
	    	  Iterator it = set.iterator();
	    	  while (it.hasNext()) {
	    	  Object key = it.next();
	    	  Object value = m.get(key);
	    	  System.out.println(key.toString()+"--"+value.toString());
	    	  }

	 }
}
