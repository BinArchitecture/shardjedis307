package redis.clients.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.util.JedisByteHashMap;
import redis.clients.util.SafeEncoder;

public class BuilderFactory {
  public static final Builder<Double> DOUBLE = new Builder<Double>() {
    @Override
    public Double build(Object data) {
      String asString = STRING.build(data);
      return asString == null ? null : Double.valueOf(asString);
    }

    public String toString() {
      return "double";
    }
  };
  public static final Builder<Boolean> BOOLEAN = new Builder<Boolean>() {
    @Override
    public Boolean build(Object data) {
      return ((Long) data) == 1;
    }

    public String toString() {
      return "boolean";
    }
  };
  public static final Builder<byte[]> BYTE_ARRAY = new Builder<byte[]>() {
    @Override
    public byte[] build(Object data) {
      return ((byte[]) data); // deleted == 1
    }

    public String toString() {
      return "byte[]";
    }
  };

  public static final Builder<Long> LONG = new Builder<Long>() {
    @Override
    public Long build(Object data) {
      return (Long) data;
    }

    public String toString() {
      return "long";
    }

  };
  public static final Builder<String> STRING = new Builder<String>() {
    @Override
    public String build(Object data) {
      return data == null ? null : SafeEncoder.encode((byte[]) data);
    }

    public String toString() {
      return "string";
    }

  };
  public static final Builder<List<String>> STRING_LIST = new Builder<List<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final ArrayList<String> result = new ArrayList<String>(l.size());
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    public String toString() {
      return "List<String>";
    }

  };
  
  public static final Builder<Map<String,Set<String>>> FUCKSTRING_LIST = new Builder<Map<String,Set<String>>>() {
	    @Override
	    @SuppressWarnings("unchecked")
	    public Map<String,Set<String>> build(Object data) {
	      if (null == data) {
	        return null;
	      }
	      List<byte[]> l = (List<byte[]>) data;
	      final Map<String,Set<String>> result = new HashMap<String,Set<String>>();
	      String key=null;
	      boolean m=false;
	      for (final byte[] barray : l) {
	    	  	if(barray == null) {
 		         continue;
 		     }
	    	  	String bb=SafeEncoder.encode(barray);
	    	  	if(key==null||m){
	    	  		key=bb;
	    	  		result.put(key, new LinkedHashSet<String>());
	    	  		m=false;
	    	  		continue;
	    	  	}
	    	  	else if("\r\n".equals(bb)){
	    	  		m=true;
	    	  		continue;
	    	  	}
	    	  	else{
	    	  		Set<String> ss=result.get(key);
	    	  		ss.add(bb);
	    	  	}
	      }
	      return result;
	    }

	    public String toString() {
	      return "Map<String,List<String>>";
	    }

	  };
	  
  public static final Builder<Map<String, String>> STRING_MAP = new Builder<Map<String, String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> build(Object data) {
      final List<byte[]> flatHash = (List<byte[]>) data;
      final Map<String, String> hash = new HashMap<String, String>();
      final Iterator<byte[]> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next()));
      }

      return hash;
    }

    public String toString() {
      return "Map<String, String>";
    }

  };

  public static final Builder<Map<String, Map<String,String>>> FUCKSTRING_MAP = new Builder<Map<String, Map<String,String>>>() {
	    @Override
	    @SuppressWarnings("unchecked")
	    public Map<String, Map<String,String>> build(Object data) {
	      final List<byte[]> flatHash = (List<byte[]>) data;
	      final Map<String, Map<String,String>> hash = new LinkedHashMap<String, Map<String,String>>();
	      final Iterator<byte[]> iterator = flatHash.iterator();
	      String key=null;
	      while (iterator.hasNext()) {
	    	  String kk=SafeEncoder.encode(iterator.next());
	    	  	if(key==null){
	    	  		key=kk;
	    	  		hash.put(key, new HashMap<String,String>());
	    	  	}
	    	  	else if("\r\n".equals(kk)&&iterator.hasNext()){
	    	  		key=SafeEncoder.encode(iterator.next());
	    	  		hash.put(key, new HashMap<String,String>());
	    	  	}
	    	  	else if(iterator.hasNext()){
	    	  		Map<String,String> m=hash.get(key);
	    	  		m.put(kk, SafeEncoder.encode(iterator.next()));
	    	  	}
	      }
	      return hash;
	    }

	    public String toString() {
	      return "Map<String, Map<String,String>>";
	    }

	  };
  
  public static final Builder<Map<String, String>> PUBSUB_NUMSUB_MAP = new Builder<Map<String, String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> build(Object data) {
      final List<Object> flatHash = (List<Object>) data;
      final Map<String, String> hash = new HashMap<String, String>();
      final Iterator<Object> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(SafeEncoder.encode((byte[]) iterator.next()),
          String.valueOf((Long) iterator.next()));
      }

      return hash;
    }

    public String toString() {
      return "PUBSUB_NUMSUB_MAP<String, String>";
    }

  };

  public static final Builder<Set<String>> STRING_SET = new Builder<Set<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<String> result = new HashSet<String>(l.size());
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    public String toString() {
      return "Set<String>";
    }

  };

  public static final Builder<List<byte[]>> BYTE_ARRAY_LIST = new Builder<List<byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<byte[]> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;

      return l;
    }

    public String toString() {
      return "List<byte[]>";
    }
  };

  public static final Builder<Set<byte[]>> BYTE_ARRAY_ZSET = new Builder<Set<byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<byte[]> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<byte[]> result = new LinkedHashSet<byte[]>(l);
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(barray);
        }
      }
      return result;
    }

    public String toString() {
      return "ZSet<byte[]>";
    }
  };
  public static final Builder<Map<byte[], byte[]>> BYTE_ARRAY_MAP = new Builder<Map<byte[], byte[]>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<byte[], byte[]> build(Object data) {
      final List<byte[]> flatHash = (List<byte[]>) data;
      final Map<byte[], byte[]> hash = new JedisByteHashMap();
      final Iterator<byte[]> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(iterator.next(), iterator.next());
      }

      return hash;
    }

    public String toString() {
      return "Map<byte[], byte[]>";
    }

  };

  public static final Builder<Set<String>> STRING_ZSET = new Builder<Set<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<String> result = new LinkedHashSet<String>(l.size());
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    public String toString() {
      return "ZSet<String>";
    }

  };

  public static final Builder<Set<Tuple>> TUPLE_ZSET = new Builder<Set<Tuple>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<Tuple> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<Tuple> result = new LinkedHashSet<Tuple>(l.size());
      Iterator<byte[]> iterator = l.iterator();
      while (iterator.hasNext()) {
        result.add(new Tuple(SafeEncoder.encode(iterator.next()), Double.valueOf(SafeEncoder
            .encode(iterator.next()))));
      }
      return result;
    }

    public String toString() {
      return "ZSet<Tuple>";
    }

  };

  public static final Builder<Set<Tuple>> TUPLE_ZSET_BINARY = new Builder<Set<Tuple>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<Tuple> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<Tuple> result = new LinkedHashSet<Tuple>(l.size());
      Iterator<byte[]> iterator = l.iterator();
      while (iterator.hasNext()) {
        result.add(new Tuple(iterator.next(), Double.valueOf(SafeEncoder.encode(iterator.next()))));
      }

      return result;

    }

    public String toString() {
      return "ZSet<Tuple>";
    }
  };

  public static final Builder<Object> EVAL_RESULT = new Builder<Object>() {

    @Override
    public Object build(Object data) {
      return evalResult(data);
    }

    public String toString() {
      return "Eval<Object>";
    }

    private Object evalResult(Object result) {
      if (result instanceof byte[]) return SafeEncoder.encode((byte[]) result);

      if (result instanceof List<?>) {
        List<?> list = (List<?>) result;
        List<Object> listResult = new ArrayList<Object>(list.size());
        for (Object bin : list) {
          listResult.add(evalResult(bin));
        }

        return listResult;
      }

      return result;
    }

  };

  public static final Builder<Object> EVAL_BINARY_RESULT = new Builder<Object>() {

    @Override
    public Object build(Object data) {
      return evalResult(data);
    }

    public String toString() {
      return "Eval<Object>";
    }

    private Object evalResult(Object result) {
      if (result instanceof List<?>) {
        List<?> list = (List<?>) result;
        List<Object> listResult = new ArrayList<Object>(list.size());
        for (Object bin : list) {
          listResult.add(evalResult(bin));
        }

        return listResult;
      }

      return result;
    }

  };

}
