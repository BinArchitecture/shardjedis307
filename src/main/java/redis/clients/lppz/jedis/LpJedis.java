package redis.clients.lppz.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.set.SetParams;
import redis.clients.lppz.config.ShardingFunction;

public class LpJedis {
	
	private List<Jedis> jedisPool;
	
	public void setJedisPool(List<Jedis> jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	private Jedis getJedis(ShardingFunction sf){
		if (sf == null) {
			throw new RuntimeException("sharding function must not be null");
		}
		return jedisPool.get(Math.abs(sf.getShardingIndex())%jedisPool.size());
	}

	/**
	   * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (ShardingFunction sf, 1
	   * GB).
	   * <p>
	   * Time complexity: O(1)
	   * @param key
	   * @param value
	   * @return Status code reply
	   */
	  public String set(ShardingFunction sf, final String key, String value) {
	    return getJedis(sf).set(key, value);
	  }

	  /**
	   * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
	   * GB).
	   * @param key
	   * @param value
	   * @param params NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the
	   *          key if it already exist. EX|PX, expire time units: EX = seconds; PX = milliseconds
	   * @return Status code reply
	   */
	  public String set(ShardingFunction sf, final String key, final String value, final SetParams params) {
	    return getJedis(sf).set(key, value, params);
	  }

	  /**
	   * Get the value of the specified key. If the key does not exist null is returned. If the value
	   * stored at key is not a string an error is returned because GET can only handle string values.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @return Bulk reply
	   */
	  
	  public String get(ShardingFunction sf, final String key) {
	    return  getJedis(sf).get(key);
	  }

	  /**
	   * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is
	   * returned. Note that even keys set with an empty string as value will return "1". Time
	   * complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @return Boolean reply, true if the key exists, otherwise false
	   */
	  
	  public Boolean exists(ShardingFunction sf, final String key) {
	    return getJedis(sf).exists(key);
	  }

	  public Long del(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).del(keys);
	  }

	  
	  public Long del(ShardingFunction sf, String key) {
	    return getJedis(sf).del(key);
	  }

	  /**
	   * Return the type of the value stored at key in form of a string. The type can be one of "none",
	   * "string", "list", "set". "none" is returned if the key does not exist. Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
	   *         contains a String value "list" if the key contains a List value "set" if the key
	   *         contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
	   *         contains a Hash value
	   */
	  
	  public String type(ShardingFunction sf, final String key) {
	    return getJedis(sf).type(key);
	  }

	  /**
	   * Returns all the keys matching the glob-style pattern as space separated strings. For example if
	   * you have in the database the keys "foo" and "foobar" the command "KEYS foo*" will return
	   * "foo foobar".
	   * <p>
	   * Note that while the time complexity for this operation is O(ShardingFunction sf, n) the constant times are pretty
	   * low. For example Redis running on an entry level laptop can scan a 1 million keys database in
	   * 40 milliseconds. <b>Still it's better to consider this one of the slow commands that may ruin
	   * the DB performance if not used with care.</b>
	   * <p>
	   * In other words this command is intended only for debugging and special operations like creating
	   * a script to change the DB schema. Don't use it in your normal code. Use Redis Sets in order to
	   * group together a subset of objects.
	   * <p>
	   * Glob style patterns examples:
	   * <ul>
	   * <li>h?llo will match hello hallo hhllo
	   * <li>h*llo will match hllo heeeello
	   * <li>h[ae]llo will match hello and hallo, but not hillo
	   * </ul>
	   * <p>
	   * Use \ to escape special chars if you want to match them verbatim.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, n) (ShardingFunction sf, with n being the number of keys in the DB, and assuming keys and pattern
	   * of limited length)
	   * @param pattern
	   * @return Multi bulk reply
	   */
	  
	  public Set<String> keys(ShardingFunction sf, final String pattern) {
	    return getJedis(sf).keys(pattern);
	  }

	  /**
	   * Return a randomly selected key from the currently selected DB.
	   * <p>
	   * Time complexity: O(1)
	   * @return Singe line reply, specifically the randomly selected key or an empty string is the
	   *         database is empty
	   */
	  
	  public String randomKey() {
	    return jedisPool.get(0).randomKey();
	  }

	  /**
	   * Atomically renames the key oldkey to newkey. If the source and destination name are the same an
	   * error is returned. If newkey already exists it is overwritten.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param oldkey
	   * @param newkey
	   * @return Status code repy
	   */
	  
	  public String rename(ShardingFunction sf, final String oldkey, final String newkey) {
	    return getJedis(sf).rename(oldkey, newkey);
	  }

	  /**
	   * Rename oldkey into newkey but fails if the destination key newkey already exists.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param oldkey
	   * @param newkey
	   * @return Integer reply, specifically: 1 if the key was renamed 0 if the target key already exist
	   */
	  
	  public Long renamenx(ShardingFunction sf, final String oldkey, final String newkey) {
	    return getJedis(sf).renamenx(oldkey, newkey);
	  }

	  /**
	   * Set a timeout on the specified key. After the timeout the key will be automatically deleted by
	   * the server. A key with an associated timeout is said to be volatile in Redis terminology.
	   * <p>
	   * Voltile keys are stored on disk like the other keys, the timeout is persistent too like all the
	   * other aspects of the dataset. Saving a dataset containing expires and stopping the server does
	   * not stop the flow of time as Redis stores on disk the time when the key will no longer be
	   * available as Unix time, and not the remaining seconds.
	   * <p>
	   * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
	   * set. It is also possible to undo the expire at all turning the key into a normal key using the
	   * {@link #persist(ShardingFunction sf, String) PERSIST} command.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see <a href="http://redis.io/commands/expire">Expire Command</a>
	   * @param key
	   * @param seconds
	   * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
	   *         the key already has an associated timeout (ShardingFunction sf, this may happen only in Redis versions &lt;
	   *         2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
	   */
	  
	  public Long expire(ShardingFunction sf, final String key, final int seconds) {
	    return getJedis(sf).expire(key, seconds);
	  }

	  /**
	   * EXPIREAT works exctly like {@link #expire(ShardingFunction sf, String, int) EXPIRE} but instead to get the number of
	   * seconds representing the Time To Live of the key as a second argument (ShardingFunction sf, that is a relative way
	   * of specifing the TTL), it takes an absolute one in the form of a UNIX timestamp (ShardingFunction sf, Number of
	   * seconds elapsed since 1 Gen 1970).
	   * <p>
	   * EXPIREAT was introduced in order to implement the Append Only File persistence mode so that
	   * EXPIRE commands are automatically translated into EXPIREAT commands for the append only file.
	   * Of course EXPIREAT can also used by programmers that need a way to simply specify that a given
	   * key should expire at a given time in the future.
	   * <p>
	   * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire
	   * set. It is also possible to undo the expire at all turning the key into a normal key using the
	   * {@link #persist(ShardingFunction sf, String) PERSIST} command.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see <a href="http://redis.io/commands/expire">Expire Command</a>
	   * @param key
	   * @param unixTime
	   * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
	   *         the key already has an associated timeout (ShardingFunction sf, this may happen only in Redis versions &lt;
	   *         2.1.3, Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
	   */
	  
	  public Long expireAt(ShardingFunction sf, final String key, final long unixTime) {
	    return getJedis(sf).expireAt(key, unixTime);
	  }

	  /**
	   * The TTL command returns the remaining time to live in seconds of a key that has an
	   * {@link #expire(ShardingFunction sf, String, int) EXPIRE} set. This introspection capability allows a Redis client to
	   * check how many seconds a given key will continue to be part of the dataset.
	   * @param key
	   * @return Integer reply, returns the remaining time to live in seconds of a key that has an
	   *         EXPIRE. In Redis 2.6 or older, if the Key does not exists or does not have an
	   *         associated expire, -1 is returned. In Redis 2.8 or newer, if the Key does not have an
	   *         associated expire, -1 is returned or if the Key does not exists, -2 is returned.
	   */
	  
	  public Long ttl(ShardingFunction sf, final String key) {
	    return getJedis(sf).ttl(key);
	  }

	  /**
	   * Move the specified key from the currently selected DB to the specified destination DB. Note
	   * that this command returns 1 only if the key was successfully moved, and 0 if the target key was
	   * already there or if the source key was not found at all, so it is possible to use MOVE as a
	   * locking primitive.
	   * @param key
	   * @param dbIndex
	   * @return Integer reply, specifically: 1 if the key was moved 0 if the key was not moved because
	   *         already present on the target DB or was not found in the current DB.
	   */
	  
	  public Long move(ShardingFunction sf, final String key, final int dbIndex) {
	    return getJedis(sf).move(key, dbIndex);
	  }

	  /**
	   * GETSET is an atomic set this value and return the old value command. Set key to the string
	   * value and return the old value stored at key. The string can't be longer than 1073741824 bytes
	   * (ShardingFunction sf, 1 GB).
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param value
	   * @return Bulk reply
	   */
	  
	  public String getSet(ShardingFunction sf, final String key, final String value) {
	    return getJedis(sf).getSet(key, value);
	  }

	  /**
	   * Get the values of all the specified keys. If one or more keys dont exist or is not of type
	   * String, a 'nil' value is returned instead of the value of the specified key, but the operation
	   * never fails.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1) for every key
	   * @param keys
	   * @return Multi bulk reply
	   */
	  
	  public List<String> mget(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).mget(keys);
	  }

	  /**
	   * SETNX works exactly like {@link #set(ShardingFunction sf, String, String) SET} with the only difference that if the
	   * key already exists no operation is performed. SETNX actually means "SET if Not eXists".
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param value
	   * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
	   */
	  
	  public Long setnx(ShardingFunction sf, final String key, final String value) {
	    return getJedis(sf).setnx(key, value);
	  }

	  /**
	   * The command is exactly equivalent to the following group of commands:
	   * {@link #set(ShardingFunction sf, String, String) SET} + {@link #expire(ShardingFunction sf, String, int) EXPIRE}. The operation is
	   * atomic.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param seconds
	   * @param value
	   * @return Status code reply
	   */
	  
	  public String setex(ShardingFunction sf, final String key, final int seconds, final String value) {
	    return getJedis(sf).setex(key, seconds, value);
	  }

	  /**
	   * Set the the respective keys to the respective values. MSET will replace old values with new
	   * values, while {@link #msetnx(ShardingFunction sf, String...) MSETNX} will not perform any operation at all even if
	   * just a single key already exists.
	   * <p>
	   * Because of this semantic MSETNX can be used in order to set different keys representing
	   * different fields of an unique logic object in a way that ensures that either all the fields or
	   * none at all are set.
	   * <p>
	   * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
	   * are modified, another client talking to Redis can either see the changes to both A and B at
	   * once, or no modification at all.
	   * @see #msetnx(ShardingFunction sf, String...)
	   * @param keysvalues
	   * @return Status code reply Basically +OK as MSET can't fail
	   */
	  
	  public String mset(ShardingFunction sf, final String... keysvalues) {
	    return getJedis(sf).mset(keysvalues);
	  }

	  /**
	   * Set the the respective keys to the respective values. {@link #mset(ShardingFunction sf, String...) MSET} will
	   * replace old values with new values, while MSETNX will not perform any operation at all even if
	   * just a single key already exists.
	   * <p>
	   * Because of this semantic MSETNX can be used in order to set different keys representing
	   * different fields of an unique logic object in a way that ensures that either all the fields or
	   * none at all are set.
	   * <p>
	   * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B
	   * are modified, another client talking to Redis can either see the changes to both A and B at
	   * once, or no modification at all.
	   * @see #mset(ShardingFunction sf, String...)
	   * @param keysvalues
	   * @return Integer reply, specifically: 1 if the all the keys were set 0 if no key was set (ShardingFunction sf, at
	   *         least one key already existed)
	   */
	  
	  public Long msetnx(ShardingFunction sf, final String... keysvalues) {
	    return getJedis(sf).msetnx(keysvalues);
	  }

	  /**
	   * IDECRBY work just like {@link #decr(ShardingFunction sf, String) INCR} but instead to decrement by 1 the decrement
	   * is integer.
	   * <p>
	   * INCR commands are limited to 64 bit signed integers.
	   * <p>
	   * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
	   * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
	   * and then converted back as a string.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #incr(ShardingFunction sf, String)
	   * @see #decr(ShardingFunction sf, String)
	   * @see #incrBy(ShardingFunction sf, String, long)
	   * @param key
	   * @param integer
	   * @return Integer reply, this commands will reply with the new value of key after the increment.
	   */
	  
	  public Long decrBy(ShardingFunction sf, final String key, final long integer) {
	    return getJedis(sf).decrBy(key, integer);
	  }

	  /**
	   * Decrement the number stored at key by one. If the key does not exist or contains a value of a
	   * wrong type, set the key to the value of "0" before to perform the decrement operation.
	   * <p>
	   * INCR commands are limited to 64 bit signed integers.
	   * <p>
	   * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
	   * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
	   * and then converted back as a string.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #incr(ShardingFunction sf, String)
	   * @see #incrBy(ShardingFunction sf, String, long)
	   * @see #decrBy(ShardingFunction sf, String, long)
	   * @param key
	   * @return Integer reply, this commands will reply with the new value of key after the increment.
	   */
	  
	  public Long decr(ShardingFunction sf, final String key) {
	    return getJedis(sf).decr(key);
	  }

	  /**
	   * INCRBY work just like {@link #incr(ShardingFunction sf, String) INCR} but instead to increment by 1 the increment is
	   * integer.
	   * <p>
	   * INCR commands are limited to 64 bit signed integers.
	   * <p>
	   * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
	   * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
	   * and then converted back as a string.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #incr(ShardingFunction sf, String)
	   * @see #decr(ShardingFunction sf, String)
	   * @see #decrBy(ShardingFunction sf, String, long)
	   * @param key
	   * @param integer
	   * @return Integer reply, this commands will reply with the new value of key after the increment.
	   */
	  
	  public Long incrBy(ShardingFunction sf, final String key, final long integer) {
	    return getJedis(sf).incrBy(key, integer);
	  }

	  /**
	   * INCRBYFLOAT
	   * <p>
	   * INCRBYFLOAT commands are limited to double precision floating point values.
	   * <p>
	   * Note: this is actually a string operation, that is, in Redis there are not "double" types.
	   * Simply the string stored at the key is parsed as a base double precision floating point value,
	   * incremented, and then converted back as a string. There is no DECRYBYFLOAT but providing a
	   * negative value will work as expected.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param value
	   * @return Double reply, this commands will reply with the new value of key after the increment.
	   */
	  
	  public Double incrByFloat(ShardingFunction sf, final String key, final double value) {
	    return getJedis(sf).incrByFloat(key, value);
	  }

	  /**
	   * Increment the number stored at key by one. If the key does not exist or contains a value of a
	   * wrong type, set the key to the value of "0" before to perform the increment operation.
	   * <p>
	   * INCR commands are limited to 64 bit signed integers.
	   * <p>
	   * Note: this is actually a string operation, that is, in Redis there are not "integer" types.
	   * Simply the string stored at the key is parsed as a base 10 64 bit signed integer, incremented,
	   * and then converted back as a string.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #incrBy(ShardingFunction sf, String, long)
	   * @see #decr(ShardingFunction sf, String)
	   * @see #decrBy(ShardingFunction sf, String, long)
	   * @param key
	   * @return Integer reply, this commands will reply with the new value of key after the increment.
	   */
	  
	  public Long incr(ShardingFunction sf, final String key) {
	    return getJedis(sf).incr(key);
	  }

	  /**
	   * If the key already exists and is a string, this command appends the provided value at the end
	   * of the string. If the key does not exist it is created and set as an empty string, so APPEND
	   * will be very similar to SET in this special case.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1). The amortized time complexity is O(ShardingFunction sf, 1) assuming the appended value is
	   * small and the already present value is of any size, since the dynamic string library used by
	   * Redis will double the free space available on every reallocation.
	   * @param key
	   * @param value
	   * @return Integer reply, specifically the total length of the string after the append operation.
	   */
	  
	  public Long append(ShardingFunction sf, final String key, final String value) {
	    return getJedis(sf).append(key, value);
	  }

	  /**
	   * Return a subset of the string from offset start to offset end (ShardingFunction sf, both offsets are inclusive).
	   * Negative offsets can be used in order to provide an offset starting from the end of the string.
	   * So -1 means the last char, -2 the penultimate and so forth.
	   * <p>
	   * The function handles out of range requests without raising an error, but just limiting the
	   * resulting range to the actual length of the string.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, start+n) (ShardingFunction sf, with start being the start index and n the total length of the
	   * requested range). Note that the lookup part of this command is O(ShardingFunction sf, 1) so for small strings this
	   * is actually an O(ShardingFunction sf, 1) command.
	   * @param key
	   * @param start
	   * @param end
	   * @return Bulk reply
	   */
	  
	  public String substr(ShardingFunction sf, final String key, final int start, final int end) {
	    return getJedis(sf).substr(key, start, end);
	  }

	  /**
	   * Set the specified hash field to the specified value.
	   * <p>
	   * If key does not exist, a new key holding a hash is created.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @param value
	   * @return If the field already exists, and the HSET just produced an update of the value, 0 is
	   *         returned, otherwise if a new field is created 1 is returned.
	   */
	  
	  public Long hset(ShardingFunction sf, final String key, final String field, final String value) {
	    return getJedis(sf).hset(key, field, value);
	  }

	  /**
	   * If key holds a hash, retrieve the value associated to the specified field.
	   * <p>
	   * If the field is not found or the key does not exist, a special 'nil' value is returned.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @return Bulk reply
	   */
	  
	  public String hget(ShardingFunction sf, final String key, final String field) {
	    return getJedis(sf).hget(key, field);
	  }

	  /**
	   * Set the specified hash field to the specified value if the field not exists. <b>Time
	   * complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @param value
	   * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is
	   *         returned.
	   */
	  
	  public Long hsetnx(ShardingFunction sf, final String key, final String field, final String value) {
	    return getJedis(sf).hsetnx(key, field, value);
	  }

	  /**
	   * Set the respective fields to the respective values. HMSET replaces old values with new values.
	   * <p>
	   * If key does not exist, a new key holding a hash is created.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) (ShardingFunction sf, with N being the number of fields)
	   * @param key
	   * @param hash
	   * @return Return OK or Exception if hash is empty
	   */
	  
	  public String hmset(ShardingFunction sf, final String key, final Map<String, String> hash) {
	    return getJedis(sf).hmset(key, hash);
	  }

	  /**
	   * Retrieve the values associated to the specified fields.
	   * <p>
	   * If some of the specified fields do not exist, nil values are returned. Non existing keys are
	   * considered like empty hashes.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) (ShardingFunction sf, with N being the number of fields)
	   * @param key
	   * @param fields
	   * @return Multi Bulk Reply specifically a list of all the values associated with the specified
	   *         fields, in the same order of the request.
	   */
	  
	  public List<String> hmget(ShardingFunction sf, final String key, final String... fields) {
	    return getJedis(sf).hmget(key, fields);
	  }

	  /**
	   * Increment the number stored at field in the hash at key by value. If key does not exist, a new
	   * key holding a hash is created. If field does not exist or holds a string, the value is set to 0
	   * before applying the operation. Since the value argument is signed you can use this command to
	   * perform both increments and decrements.
	   * <p>
	   * The range of values supported by HINCRBY is limited to 64 bit signed integers.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @param value
	   * @return Integer reply The new value at field after the increment operation.
	   */
	  
	  public Long hincrBy(ShardingFunction sf, final String key, final String field, final long value) {
	    return getJedis(sf).hincrBy(key, field, value);
	  }

	  /**
	   * Increment the number stored at field in the hash at key by a double precision floating point
	   * value. If key does not exist, a new key holding a hash is created. If field does not exist or
	   * holds a string, the value is set to 0 before applying the operation. Since the value argument
	   * is signed you can use this command to perform both increments and decrements.
	   * <p>
	   * The range of values supported by HINCRBYFLOAT is limited to double precision floating point
	   * values.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @param value
	   * @return Double precision floating point reply The new value at field after the increment
	   *         operation.
	   */
	  
	  public Double hincrByFloat(ShardingFunction sf, final String key, final String field, final double value) {
	    return getJedis(sf).hincrByFloat(key, field, value);
	  }

	  /**
	   * Test for existence of a specified field in a hash. <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param field
	   * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key is
	   *         not found or the field is not present.
	   */
	  
	  public Boolean hexists(ShardingFunction sf, final String key, final String field) {
	    return getJedis(sf).hexists(key, field);
	  }

	  /**
	   * Remove the specified field from an hash stored at key.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param fields
	   * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is
	   *         returned and no operation is performed.
	   */
	  
	  public Long hdel(ShardingFunction sf, final String key, final String... fields) {
	    return getJedis(sf).hdel(key, fields);
	  }

	  /**
	   * Return the number of items in a hash.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @return The number of entries (ShardingFunction sf, fields) contained in the hash stored at key. If the specified
	   *         key does not exist, 0 is returned assuming an empty hash.
	   */
	  
	  public Long hlen(ShardingFunction sf, final String key) {
	    return getJedis(sf).hlen(key);
	  }

	  /**
	   * Return all the fields in a hash.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N), where N is the total number of entries
	   * @param key
	   * @return All the fields names contained into a hash.
	   */
	  
	  public Set<String> hkeys(ShardingFunction sf, final String key) {
	    return getJedis(sf).hkeys(key);
	  }

	  /**
	   * Return all the values in a hash.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N), where N is the total number of entries
	   * @param key
	   * @return All the fields values contained into a hash.
	   */
	  
	  public List<String> hvals(ShardingFunction sf, final String key) {
	    return getJedis(sf).hvals(key);
	  }

	  /**
	   * Return all the fields and associated values in a hash.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N), where N is the total number of entries
	   * @param key
	   * @return All the fields and values contained into a hash.
	   */
	  
	  public Map<String, String> hgetAll(ShardingFunction sf, final String key) {
	    return getJedis(sf).hgetAll(key);
	  }

	  /**
	   * Add the string value to the head (ShardingFunction sf, LPUSH) or tail (ShardingFunction sf, RPUSH) of the list stored at key. If the key
	   * does not exist an empty list is created just before the append operation. If the key exists but
	   * is not a List an error is returned.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param strings
	   * @return Integer reply, specifically, the number of elements inside the list after the push
	   *         operation.
	   */
	  
	  public Long rpush(ShardingFunction sf, final String key, final String... strings) {
	    return getJedis(sf).rpush(key, strings);
	  }

	  /**
	   * Add the string value to the head (ShardingFunction sf, LPUSH) or tail (ShardingFunction sf, RPUSH) of the list stored at key. If the key
	   * does not exist an empty list is created just before the append operation. If the key exists but
	   * is not a List an error is returned.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param strings
	   * @return Integer reply, specifically, the number of elements inside the list after the push
	   *         operation.
	   */
	  
	  public Long lpush(ShardingFunction sf, final String key, final String... strings) {
	    return getJedis(sf).lpush(key, strings);
	  }

	  /**
	   * Return the length of the list stored at the specified key. If the key does not exist zero is
	   * returned (ShardingFunction sf, the same behaviour as for empty lists). If the value stored at key is not a list an
	   * error is returned.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @return The length of the list.
	   */
	  
	  public Long llen(ShardingFunction sf, final String key) {
	    return getJedis(sf).llen(key);
	  }

	  /**
	   * Return the specified elements of the list stored at the specified key. Start and end are
	   * zero-based indexes. 0 is the first element of the list (ShardingFunction sf, the list head), 1 the next element and
	   * so on.
	   * <p>
	   * For example LRANGE foobar 0 2 will return the first three elements of the list.
	   * <p>
	   * start and end can also be negative numbers indicating offsets from the end of the list. For
	   * example -1 is the last element of the list, -2 the penultimate element and so on.
	   * <p>
	   * <b>Consistency with range functions in various programming languages</b>
	   * <p>
	   * Note that if you have a list of numbers from 0 to 100, LRANGE 0 10 will return 11 elements,
	   * that is, rightmost item is included. This may or may not be consistent with behavior of
	   * range-related functions in your programming language of choice (ShardingFunction sf, think Ruby's Range.new,
	   * Array#slice or Python's range() function).
	   * <p>
	   * LRANGE behavior is consistent with one of Tcl.
	   * <p>
	   * <b>Out-of-range indexes</b>
	   * <p>
	   * Indexes out of range will not produce an error: if start is over the end of the list, or start
	   * &gt; end, an empty list is returned. If end is over the end of the list Redis will threat it
	   * just like the last element of the list.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, start+n) (ShardingFunction sf, with n being the length of the range and start being the start
	   * offset)
	   * @param key
	   * @param start
	   * @param end
	   * @return Multi bulk reply, specifically a list of elements in the specified range.
	   */
	  
	  public List<String> lrange(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).lrange(key, start, end);
	  }

	  /**
	   * Trim an existing list so that it will contain only the specified range of elements specified.
	   * Start and end are zero-based indexes. 0 is the first element of the list (ShardingFunction sf, the list head), 1 the
	   * next element and so on.
	   * <p>
	   * For example LTRIM foobar 0 2 will modify the list stored at foobar key so that only the first
	   * three elements of the list will remain.
	   * <p>
	   * start and end can also be negative numbers indicating offsets from the end of the list. For
	   * example -1 is the last element of the list, -2 the penultimate element and so on.
	   * <p>
	   * Indexes out of range will not produce an error: if start is over the end of the list, or start
	   * &gt; end, an empty list is left as value. If end over the end of the list Redis will threat it
	   * just like the last element of the list.
	   * <p>
	   * Hint: the obvious use of LTRIM is together with LPUSH/RPUSH. For example:
	   * <p>
	   * {@code lpush(ShardingFunction sf, "mylist", "someelement"); ltrim(ShardingFunction sf, "mylist", 0, 99); * }
	   * <p>
	   * The above two commands will push elements in the list taking care that the list will not grow
	   * without limits. This is very useful when using Redis to store logs for example. It is important
	   * to note that when used in this way LTRIM is an O(ShardingFunction sf, 1) operation because in the average case just
	   * one element is removed from the tail of the list.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, n) (ShardingFunction sf, with n being len of list - len of range)
	   * @param key
	   * @param start
	   * @param end
	   * @return Status code reply
	   */
	  
	  public String ltrim(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).ltrim(key, start, end);
	  }

	  /**
	   * Return the specified element of the list stored at the specified key. 0 is the first element, 1
	   * the second and so on. Negative indexes are supported, for example -1 is the last element, -2
	   * the penultimate and so on.
	   * <p>
	   * If the value stored at key is not of list type an error is returned. If the index is out of
	   * range a 'nil' reply is returned.
	   * <p>
	   * Note that even if the average time complexity is O(ShardingFunction sf, n) asking for the first or the last element
	   * of the list is O(ShardingFunction sf, 1).
	   * <p>
	   * Time complexity: O(ShardingFunction sf, n) (ShardingFunction sf, with n being the length of the list)
	   * @param key
	   * @param index
	   * @return Bulk reply, specifically the requested element
	   */
	  
	  public String lindex(ShardingFunction sf, final String key, final long index) {
	    return getJedis(sf).lindex(key, index);
	  }

	  /**
	   * Set a new value as the element at index position of the List at key.
	   * <p>
	   * Out of range indexes will generate an error.
	   * <p>
	   * Similarly to other list commands accepting indexes, the index can be negative to access
	   * elements starting from the end of the list. So -1 is the last element, -2 is the penultimate,
	   * and so forth.
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, N) (ShardingFunction sf, with N being the length of the list), setting the first or last elements of the list is
	   * O(ShardingFunction sf, 1).
	   * @see #lindex(ShardingFunction sf, String, long)
	   * @param key
	   * @param index
	   * @param value
	   * @return Status code reply
	   */
	  
	  public String lset(ShardingFunction sf, final String key, final long index, final String value) {
	    return getJedis(sf).lset(key, index, value);
	  }

	  /**
	   * Remove the first count occurrences of the value element from the list. If count is zero all the
	   * elements are removed. If count is negative elements are removed from tail to head, instead to
	   * go from head to tail that is the normal behaviour. So for example LREM with count -2 and hello
	   * as value to remove against the list (ShardingFunction sf, a,b,c,hello,x,hello,hello) will lave the list
	   * (ShardingFunction sf, a,b,c,hello,x). The number of removed elements is returned as an integer, see below for more
	   * information about the returned value. Note that non existing keys are considered like empty
	   * lists by LREM, so LREM against non existing keys will always return 0.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, N) (ShardingFunction sf, with N being the length of the list)
	   * @param key
	   * @param count
	   * @param value
	   * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
	   */
	  
	  public Long lrem(ShardingFunction sf, final String key, final long count, final String value) {
	    return getJedis(sf).lrem(key, count, value);
	  }

	  /**
	   * Atomically return and remove the first (ShardingFunction sf, LPOP) or last (ShardingFunction sf, RPOP) element of the list. For example
	   * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
	   * "b","c".
	   * <p>
	   * If the key does not exist or the list is already empty the special value 'nil' is returned.
	   * @see #rpop(ShardingFunction sf, String)
	   * @param key
	   * @return Bulk reply
	   */
	  
	  public String lpop(ShardingFunction sf, final String key) {
	    return getJedis(sf).lpop(key);
	  }

	  /**
	   * Atomically return and remove the first (ShardingFunction sf, LPOP) or last (ShardingFunction sf, RPOP) element of the list. For example
	   * if the list contains the elements "a","b","c" RPOP will return "c" and the list will become
	   * "a","b".
	   * <p>
	   * If the key does not exist or the list is already empty the special value 'nil' is returned.
	   * @see #lpop(ShardingFunction sf, String)
	   * @param key
	   * @return Bulk reply
	   */
	  
	  public String rpop(ShardingFunction sf, final String key) {
	    return getJedis(sf).rpop(key);
	  }

	  /**
	   * Atomically return and remove the last (ShardingFunction sf, tail) element of the srckey list, and push the element
	   * as the first (ShardingFunction sf, head) element of the dstkey list. For example if the source list contains the
	   * elements "a","b","c" and the destination list contains the elements "foo","bar" after an
	   * RPOPLPUSH command the content of the two lists will be "a","b" and "c","foo","bar".
	   * <p>
	   * If the key does not exist or the list is already empty the special value 'nil' is returned. If
	   * the srckey and dstkey are the same the operation is equivalent to removing the last element
	   * from the list and pusing it as first element of the list, so it's a "list rotation" command.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param srckey
	   * @param dstkey
	   * @return Bulk reply
	   */
	  
	  public String rpoplpush(ShardingFunction sf, final String srckey, final String dstkey) {
	    return getJedis(sf).rpoplpush(srckey, dstkey);
	  }

	  /**
	   * Add the specified member to the set value stored at key. If member is already a member of the
	   * set no operation is performed. If key does not exist a new set with the specified member as
	   * sole member is created. If the key exists but does not hold a set value an error is returned.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @param members
	   * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
	   *         already a member of the set
	   */
	  
	  public Long sadd(ShardingFunction sf, final String key, final String... members) {
	    return getJedis(sf).sadd(key, members);
	  }

	  /**
	   * Return all the members (ShardingFunction sf, elements) of the set value stored at key. This is just syntax glue for
	   * {@link #sinter(ShardingFunction sf, String...) SINTER}.
	   * <p>
	   * Time complexity O(ShardingFunction sf, N)
	   * @param key
	   * @return Multi bulk reply
	   */
	  
	  public Set<String> smembers(ShardingFunction sf, final String key) {
	    return getJedis(sf).smembers(key);
	  }

	  /**
	   * Remove the specified member from the set value stored at key. If member was not a member of the
	   * set no operation is performed. If key does not hold a set value an error is returned.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @param members
	   * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
	   *         not a member of the set
	   */
	  
	  public Long srem(ShardingFunction sf, final String key, final String... members) {
	    return getJedis(sf).srem(key, members);
	  }

	  /**
	   * Remove a random element from a Set returning it as return value. If the Set is empty or the key
	   * does not exist, a nil object is returned.
	   * <p>
	   * The {@link #srandmember(ShardingFunction sf, String)} command does a similar work but the returned element is not
	   * removed from the Set.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @return Bulk reply
	   */
	  
	  public String spop(ShardingFunction sf, final String key) {
	    return getJedis(sf).spop(key);
	  }

	  
	  public Set<String> spop(ShardingFunction sf, final String key, final long count) {
	    return getJedis(sf).spop(key, count);
	  }

	  /**
	   * Move the specifided member from the set at srckey to the set at dstkey. This operation is
	   * atomic, in every given moment the element will appear to be in the source or destination set
	   * for accessing clients.
	   * <p>
	   * If the source set does not exist or does not contain the specified element no operation is
	   * performed and zero is returned, otherwise the element is removed from the source set and added
	   * to the destination set. On success one is returned, even if the element was already present in
	   * the destination set.
	   * <p>
	   * An error is raised if the source or destination keys contain a non Set value.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param srckey
	   * @param dstkey
	   * @param member
	   * @return Integer reply, specifically: 1 if the element was moved 0 if the element was not found
	   *         on the first set and no operation was performed
	   */
	  
	  public Long smove(ShardingFunction sf, final String srckey, final String dstkey, final String member) {
	    return getJedis(sf).smove(srckey, dstkey, member);
	  }

	  /**
	   * Return the set cardinality (ShardingFunction sf, number of elements). If the key does not exist 0 is returned, like
	   * for empty sets.
	   * @param key
	   * @return Integer reply, specifically: the cardinality (ShardingFunction sf, number of elements) of the set as an
	   *         integer.
	   */
	  
	  public Long scard(ShardingFunction sf, final String key) {
	    return getJedis(sf).scard(key);
	  }

	  /**
	   * Return 1 if member is a member of the set stored at key, otherwise 0 is returned.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @param member
	   * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element
	   *         is not a member of the set OR if the key does not exist
	   */
	  
	  public Boolean sismember(ShardingFunction sf, final String key, final String member) {
	    return getJedis(sf).sismember(key, member);
	  }

	  /**
	   * Return the members of a set resulting from the intersection of all the sets hold at the
	   * specified keys. Like in {@link #lrange(ShardingFunction sf, String, long, long) LRANGE} the result is sent to the
	   * client as a multi-bulk reply (ShardingFunction sf, see the protocol specification for more information). If just a
	   * single key is specified, then this command produces the same result as
	   * {@link #smembers(ShardingFunction sf, String) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER.
	   * <p>
	   * Non existing keys are considered like empty sets, so if one of the keys is missing an empty set
	   * is returned (ShardingFunction sf, since the intersection with an empty set always is an empty set).
	   * <p>
	   * Time complexity O(ShardingFunction sf, N*M) worst case where N is the cardinality of the smallest set and M the
	   * number of sets
	   * @param keys
	   * @return Multi bulk reply, specifically the list of common elements.
	   */
	  
	  public Set<String> sinter(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).sinter(keys);
	  }

	  /**
	   * This commnad works exactly like {@link #sinter(ShardingFunction sf, String...) SINTER} but instead of being returned
	   * the resulting set is sotred as dstkey.
	   * <p>
	   * Time complexity O(ShardingFunction sf, N*M) worst case where N is the cardinality of the smallest set and M the
	   * number of sets
	   * @param dstkey
	   * @param keys
	   * @return Status code reply
	   */
	  
	  public Long sinterstore(ShardingFunction sf, final String dstkey, final String... keys) {
	    return getJedis(sf).sinterstore(dstkey, keys);
	  }

	  /**
	   * Return the members of a set resulting from the union of all the sets hold at the specified
	   * keys. Like in {@link #lrange(ShardingFunction sf, String, long, long) LRANGE} the result is sent to the client as a
	   * multi-bulk reply (ShardingFunction sf, see the protocol specification for more information). If just a single key is
	   * specified, then this command produces the same result as {@link #smembers(ShardingFunction sf, String) SMEMBERS}.
	   * <p>
	   * Non existing keys are considered like empty sets.
	   * <p>
	   * Time complexity O(ShardingFunction sf, N) where N is the total number of elements in all the provided sets
	   * @param keys
	   * @return Multi bulk reply, specifically the list of common elements.
	   */
	  
	  public Set<String> sunion(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).sunion(keys);
	  }

	  /**
	   * This command works exactly like {@link #sunion(ShardingFunction sf, String...) SUNION} but instead of being returned
	   * the resulting set is stored as dstkey. Any existing value in dstkey will be over-written.
	   * <p>
	   * Time complexity O(ShardingFunction sf, N) where N is the total number of elements in all the provided sets
	   * @param dstkey
	   * @param keys
	   * @return Status code reply
	   */
	  
	  public Long sunionstore(ShardingFunction sf, final String dstkey, final String... keys) {
	    return getJedis(sf).sunionstore(dstkey, keys);
	  }

	  /**
	   * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN
	   * <p>
	   * <b>Example:</b>
	   * 
	   * <pre>
	   * key1 = [x, a, b, c]
	   * key2 = [c]
	   * key3 = [a, d]
	   * SDIFF key1,key2,key3 =&gt; [x, b]
	   * </pre>
	   * 
	   * Non existing keys are considered like empty sets.
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, N) with N being the total number of elements of all the sets
	   * @param keys
	   * @return Return the members of a set resulting from the difference between the first set
	   *         provided and all the successive sets.
	   */
	  
	  public Set<String> sdiff(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).sdiff(keys);
	  }

	  /**
	   * This command works exactly like {@link #sdiff(ShardingFunction sf, String...) SDIFF} but instead of being returned
	   * the resulting set is stored in dstkey.
	   * @param dstkey
	   * @param keys
	   * @return Status code reply
	   */
	  
	  public Long sdiffstore(ShardingFunction sf, final String dstkey, final String... keys) {
	    return getJedis(sf).sdiffstore(dstkey, keys);
	  }

	  /**
	   * Return a random element from a Set, without removing the element. If the Set is empty or the
	   * key does not exist, a nil object is returned.
	   * <p>
	   * The SPOP command does a similar work but the returned element is popped (ShardingFunction sf, removed) from the Set.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @return Bulk reply
	   */
	  
	  public String srandmember(ShardingFunction sf, final String key) {
	    return getJedis(sf).srandmember(key);
	  }

	  
	  public List<String> srandmember(ShardingFunction sf, final String key, final int count) {
	    return getJedis(sf).srandmember(key, count);
	  }

	  /**
	   * Add the specified member having the specifeid score to the sorted set stored at key. If member
	   * is already a member of the sorted set the score is updated, and the element reinserted in the
	   * right position to ensure sorting. If key does not exist a new sorted set with the specified
	   * member as sole member is crated. If the key exists but does not hold a sorted set value an
	   * error is returned.
	   * <p>
	   * The score value can be the string representation of a double precision floating point number.
	   * <p>
	   * Time complexity O(ShardingFunction sf, log(ShardingFunction sf, N)) with N being the number of elements in the sorted set
	   * @param key
	   * @param score
	   * @param member
	   * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
	   *         already a member of the sorted set and the score was updated
	   */
	  
	  public Long zadd(ShardingFunction sf, final String key, final double score, final String member) {
	    return getJedis(sf).zadd(key, score, member);
	  }

	  
	  public Long zadd(ShardingFunction sf, final String key, final Map<String, Double> scoreMembers) {
	    return getJedis(sf).zadd(key, scoreMembers);
	  }

	  
	  public Set<String> zrange(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).zrange(key, start, end);
	  }

	  /**
	   * Remove the specified member from the sorted set value stored at key. If member was not a member
	   * of the set no operation is performed. If key does not not hold a set value an error is
	   * returned.
	   * <p>
	   * Time complexity O(ShardingFunction sf, log(ShardingFunction sf, N)) with N being the number of elements in the sorted set
	   * @param key
	   * @param members
	   * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was
	   *         not a member of the set
	   */
	  
	  public Long zrem(ShardingFunction sf, final String key, final String... members) {
	    return getJedis(sf).zrem(key, members);
	  }

	  /**
	   * If member already exists in the sorted set adds the increment to its score and updates the
	   * position of the element in the sorted set accordingly. If member does not already exist in the
	   * sorted set it is added with increment as score (ShardingFunction sf, that is, like if the previous score was
	   * virtually zero). If key does not exist a new sorted set with the specified member as sole
	   * member is crated. If the key exists but does not hold a sorted set value an error is returned.
	   * <p>
	   * The score value can be the string representation of a double precision floating point number.
	   * It's possible to provide a negative value to perform a decrement.
	   * <p>
	   * For an introduction to sorted sets check the Introduction to Redis data types page.
	   * <p>
	   * Time complexity O(ShardingFunction sf, log(ShardingFunction sf, N)) with N being the number of elements in the sorted set
	   * @param key
	   * @param score
	   * @param member
	   * @return The new score
	   */
	  
	  public Double zincrby(ShardingFunction sf, final String key, final double score, final String member) {
	    return getJedis(sf).zincrby(key, score, member);
	  }

	  /**
	   * Return the rank (ShardingFunction sf, or index) or member in the sorted set at key, with scores being ordered from
	   * low to high.
	   * <p>
	   * When the given member does not exist in the sorted set, the special value 'nil' is returned.
	   * The returned rank (ShardingFunction sf, or index) of the member is 0-based for both commands.
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrevrank(ShardingFunction sf, String, String)
	   * @param key
	   * @param member
	   * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
	   *         reply if the element exists. A nil bulk reply if there is no such element.
	   */
	  
	  public Long zrank(ShardingFunction sf, final String key, final String member) {
	    return getJedis(sf).zrank(key, member);
	  }

	  /**
	   * Return the rank (ShardingFunction sf, or index) or member in the sorted set at key, with scores being ordered from
	   * high to low.
	   * <p>
	   * When the given member does not exist in the sorted set, the special value 'nil' is returned.
	   * The returned rank (ShardingFunction sf, or index) of the member is 0-based for both commands.
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrank(ShardingFunction sf, String, String)
	   * @param key
	   * @param member
	   * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer
	   *         reply if the element exists. A nil bulk reply if there is no such element.
	   */
	  
	  public Long zrevrank(ShardingFunction sf, final String key, final String member) {
	    return getJedis(sf).zrevrank(key, member);
	  }

	  
	  public Set<String> zrevrange(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).zrevrange(key, start, end);
	  }

	  
	  public Set<Tuple> zrangeWithScores(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).zrangeWithScores(key, start, end);
	  }

	  
	  public Set<Tuple> zrevrangeWithScores(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).zrevrangeWithScores(key, start, end);
	  }

	  /**
	   * Return the sorted set cardinality (ShardingFunction sf, number of elements). If the key does not exist 0 is
	   * returned, like for empty sorted sets.
	   * <p>
	   * Time complexity O(ShardingFunction sf, 1)
	   * @param key
	   * @return the cardinality (ShardingFunction sf, number of elements) of the set as an integer.
	   */
	  
	  public Long zcard(ShardingFunction sf, final String key) {
	    return getJedis(sf).zcard(key);
	  }

	  /**
	   * Return the score of the specified element of the sorted set at key. If the specified element
	   * does not exist in the sorted set, or the key does not exist at all, a special 'nil' value is
	   * returned.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, 1)
	   * @param key
	   * @param member
	   * @return the score
	   */
	  
	  public Double zscore(ShardingFunction sf, final String key, final String member) {
	    return getJedis(sf).zscore(key, member);
	  }

	  
	  public String watch(ShardingFunction sf, final String... keys) {
	    return getJedis(sf).watch(keys);
	  }

	  /**
	   * Sort a Set or a List.
	   * <p>
	   * Sort the elements contained in the List, Set, or Sorted Set value at key. By default sorting is
	   * numeric with elements being compared as double precision floating point numbers. This is the
	   * simplest form of SORT.
	   * @see #sort(ShardingFunction sf, String, String)
	   * @see #sort(ShardingFunction sf, String, SortingParams)
	   * @see #sort(ShardingFunction sf, String, SortingParams, String)
	   * @param key
	   * @return Assuming the Set/List at key contains a list of numbers, the return value will be the
	   *         list of numbers ordered from the smallest to the biggest number.
	   */
	  
	  public List<String> sort(ShardingFunction sf, final String key) {
	    return getJedis(sf).sort(key);
	  }

	  /**
	   * Sort a Set or a List accordingly to the specified parameters.
	   * <p>
	   * <b>examples:</b>
	   * <p>
	   * Given are the following sets and key/values:
	   * 
	   * <pre>
	   * x = [1, 2, 3]
	   * y = [a, b, c]
	   * 
	   * k1 = z
	   * k2 = y
	   * k3 = x
	   * 
	   * w1 = 9
	   * w2 = 8
	   * w3 = 7
	   * </pre>
	   * 
	   * Sort Order:
	   * 
	   * <pre>
	   * sort(ShardingFunction sf, x) or sort(ShardingFunction sf, x, sp.asc())
	   * -&gt; [1, 2, 3]
	   * 
	   * sort(ShardingFunction sf, x, sp.desc())
	   * -&gt; [3, 2, 1]
	   * 
	   * sort(ShardingFunction sf, y)
	   * -&gt; [c, a, b]
	   * 
	   * sort(ShardingFunction sf, y, sp.alpha())
	   * -&gt; [a, b, c]
	   * 
	   * sort(ShardingFunction sf, y, sp.alpha().desc())
	   * -&gt; [c, a, b]
	   * </pre>
	   * 
	   * Limit (ShardingFunction sf, e.g. for Pagination):
	   * 
	   * <pre>
	   * sort(ShardingFunction sf, x, sp.limit(ShardingFunction sf, 0, 2))
	   * -&gt; [1, 2]
	   * 
	   * sort(ShardingFunction sf, y, sp.alpha().desc().limit(ShardingFunction sf, 1, 2))
	   * -&gt; [b, a]
	   * </pre>
	   * 
	   * Sorting by external keys:
	   * 
	   * <pre>
	   * sort(ShardingFunction sf, x, sb.by(ShardingFunction sf, w*))
	   * -&gt; [3, 2, 1]
	   * 
	   * sort(ShardingFunction sf, x, sb.by(ShardingFunction sf, w*).desc())
	   * -&gt; [1, 2, 3]
	   * </pre>
	   * 
	   * Getting external keys:
	   * 
	   * <pre>
	   * sort(ShardingFunction sf, x, sp.by(ShardingFunction sf, w*).get(ShardingFunction sf, k*))
	   * -&gt; [x, y, z]
	   * 
	   * sort(ShardingFunction sf, x, sp.by(ShardingFunction sf, w*).get(ShardingFunction sf, #).get(ShardingFunction sf, k*))
	   * -&gt; [3, x, 2, y, 1, z]
	   * </pre>
	   * @see #sort(ShardingFunction sf, String)
	   * @see #sort(ShardingFunction sf, String, SortingParams, String)
	   * @param key
	   * @param sortingParameters
	   * @return a list of sorted elements.
	   */
	  
	  public List<String> sort(ShardingFunction sf, final String key, final SortingParams sortingParameters) {
	    return getJedis(sf).sort(key, sortingParameters);
	  }

	  /**
	   * BLPOP (ShardingFunction sf, and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
	   * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
	   * lists.
	   * <p>
	   * The following is a description of the exact semantic. We describe BLPOP but the two commands
	   * are identical, the only difference is that BLPOP pops the element from the left (ShardingFunction sf, head) of the
	   * list, and BRPOP pops from the right (ShardingFunction sf, tail).
	   * <p>
	   * <b>Non blocking behavior</b>
	   * <p>
	   * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
	   * element is popped from the head of the list and returned to the caller together with the name
	   * of the key (ShardingFunction sf, BLPOP returns a two elements array, the first element is the key, the second the
	   * popped value).
	   * <p>
	   * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
	   * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
	   * guarantees to return an element from the list stored at list2 (ShardingFunction sf, since it is the first non empty
	   * list starting from the left).
	   * <p>
	   * <b>Blocking behavior</b>
	   * <p>
	   * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
	   * client performs a LPUSH or an RPUSH operation against one of the lists.
	   * <p>
	   * Once new data is present on one of the lists, the client finally returns with the name of the
	   * key unblocking it and the popped value.
	   * <p>
	   * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
	   * special value if the specified amount of seconds passed without a push operation against at
	   * least one of the specified keys.
	   * <p>
	   * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
	   * block forever.
	   * <p>
	   * <b>Multiple clients blocking for the same keys</b>
	   * <p>
	   * Multiple clients can block for the same key. They are put into a queue, so the first to be
	   * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
	   * <p>
	   * <b>blocking POP inside a MULTI/EXEC transaction</b>
	   * <p>
	   * BLPOP and BRPOP can be used with pipelining (ShardingFunction sf, sending multiple commands and reading the replies
	   * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (ShardingFunction sf, a Redis
	   * transaction).
	   * <p>
	   * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
	   * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
	   * it like if inside MULTI/EXEC the time will flow at infinite speed :)
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #brpop(ShardingFunction sf, int, String...)
	   * @param timeout
	   * @param keys
	   * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
	   *         unblocking key and the popped value.
	   *         <p>
	   *         When a non-zero timeout is specified, and the BLPOP operation timed out, the return
	   *         value is a nil multi bulk reply. Most client values will return false or nil
	   *         accordingly to the programming language used.
	   */
	  
	  public List<String> blpop(ShardingFunction sf, final int timeout, final String... keys) {
	    return getJedis(sf).blpop(timeout, keys);
	  }

	  
	  public List<String> blpop(ShardingFunction sf, String... args) {
		  return getJedis(sf).blpop(args);
	  }

	  
	  public List<String> brpop(ShardingFunction sf, String... args) {
	      return getJedis(sf).brpop(args);
	  }

	  /**
	   * Sort a Set or a List accordingly to the specified parameters and store the result at dstkey.
	   * @see #sort(ShardingFunction sf, String, SortingParams)
	   * @see #sort(ShardingFunction sf, String)
	   * @see #sort(ShardingFunction sf, String, String)
	   * @param key
	   * @param sortingParameters
	   * @param dstkey
	   * @return The number of elements of the list at dstkey.
	   */
	  
	  public Long sort(ShardingFunction sf, final String key, final SortingParams sortingParameters, final String dstkey) {
	    return getJedis(sf).sort(key,  sortingParameters, dstkey);
	  }

	  /**
	   * Sort a Set or a List and Store the Result at dstkey.
	   * <p>
	   * Sort the elements contained in the List, Set, or Sorted Set value at key and store the result
	   * at dstkey. By default sorting is numeric with elements being compared as double precision
	   * floating point numbers. This is the simplest form of SORT.
	   * @see #sort(ShardingFunction sf, String)
	   * @see #sort(ShardingFunction sf, String, SortingParams)
	   * @see #sort(ShardingFunction sf, String, SortingParams, String)
	   * @param key
	   * @param dstkey
	   * @return The number of elements of the list at dstkey.
	   */
	  
	  public Long sort(ShardingFunction sf, final String key, final String dstkey) {
	    return getJedis(sf).sort(key, dstkey);
	  }

	  /**
	   * BLPOP (ShardingFunction sf, and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
	   * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
	   * lists.
	   * <p>
	   * The following is a description of the exact semantic. We describe BLPOP but the two commands
	   * are identical, the only difference is that BLPOP pops the element from the left (ShardingFunction sf, head) of the
	   * list, and BRPOP pops from the right (ShardingFunction sf, tail).
	   * <p>
	   * <b>Non blocking behavior</b>
	   * <p>
	   * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
	   * element is popped from the head of the list and returned to the caller together with the name
	   * of the key (ShardingFunction sf, BLPOP returns a two elements array, the first element is the key, the second the
	   * popped value).
	   * <p>
	   * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
	   * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
	   * guarantees to return an element from the list stored at list2 (ShardingFunction sf, since it is the first non empty
	   * list starting from the left).
	   * <p>
	   * <b>Blocking behavior</b>
	   * <p>
	   * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
	   * client performs a LPUSH or an RPUSH operation against one of the lists.
	   * <p>
	   * Once new data is present on one of the lists, the client finally returns with the name of the
	   * key unblocking it and the popped value.
	   * <p>
	   * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
	   * special value if the specified amount of seconds passed without a push operation against at
	   * least one of the specified keys.
	   * <p>
	   * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
	   * block forever.
	   * <p>
	   * <b>Multiple clients blocking for the same keys</b>
	   * <p>
	   * Multiple clients can block for the same key. They are put into a queue, so the first to be
	   * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
	   * <p>
	   * <b>blocking POP inside a MULTI/EXEC transaction</b>
	   * <p>
	   * BLPOP and BRPOP can be used with pipelining (ShardingFunction sf, sending multiple commands and reading the replies
	   * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (ShardingFunction sf, a Redis
	   * transaction).
	   * <p>
	   * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
	   * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
	   * it like if inside MULTI/EXEC the time will flow at infinite speed :)
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @see #blpop(ShardingFunction sf, int, String...)
	   * @param timeout
	   * @param keys
	   * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
	   *         unblocking key and the popped value.
	   *         <p>
	   *         When a non-zero timeout is specified, and the BLPOP operation timed out, the return
	   *         value is a nil multi bulk reply. Most client values will return false or nil
	   *         accordingly to the programming language used.
	   */
	  
	  public List<String> brpop(ShardingFunction sf, final int timeout, final String... keys) {
	    return getJedis(sf).brpop(timeout, keys);
	  }

	  
	  public Long zcount(ShardingFunction sf, final String key, final double min, final double max) {
	    return getJedis(sf).zcount(key, min, max);
	  }

	  
	  public Long zcount(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zcount(key, min, max);
	  }

	  /**
	   * Return the all the elements in the sorted set at key with a score between min and max
	   * (ShardingFunction sf, including elements with score equal to min or max).
	   * <p>
	   * The elements having the same score are returned sorted lexicographically as ASCII strings (ShardingFunction sf, this
	   * follows from a property of Redis sorted sets and does not involve further computation).
	   * <p>
	   * Using the optional {@link #zrangeByScore(ShardingFunction sf, String, double, double, int, int) LIMIT} it's possible
	   * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
	   * the commands needs to traverse the list for offset elements and this adds up to the O(ShardingFunction sf, M)
	   * figure.
	   * <p>
	   * The {@link #zcount(ShardingFunction sf, String, double, double) ZCOUNT} command is similar to
	   * {@link #zrangeByScore(ShardingFunction sf, String, double, double) ZRANGEBYSCORE} but instead of returning the
	   * actual elements in the specified interval, it just returns the number of matching elements.
	   * <p>
	   * <b>Exclusive intervals and infinity</b>
	   * <p>
	   * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
	   * smallest element in order to take, for instance, elements "up to a given value".
	   * <p>
	   * Also while the interval is for default closed (ShardingFunction sf, inclusive) it's possible to specify open
	   * intervals prefixing the score with a "(ShardingFunction sf, " character, so for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 1.3 5}
	   * <p>
	   * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 5 (ShardingFunction sf, 10}
	   * <p>
	   * Will return all the values with score &gt; 5 and &lt; 10 (ShardingFunction sf, 5 and 10 excluded).
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set and M the number of
	   * elements returned by the command, so if M is constant (ShardingFunction sf, for instance you always ask for the
	   * first ten elements with LIMIT) you can consider it O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double, int, int)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, String, String)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double, int, int)
	   * @see #zcount(ShardingFunction sf, String, double, double)
	   * @param key
	   * @param min a double or Double.MIN_VALUE for "-inf"
	   * @param max a double or Double.MAX_VALUE for "+inf"
	   * @return Multi bulk reply specifically a list of elements in the specified score range.
	   */
	  
	  public Set<String> zrangeByScore(ShardingFunction sf, final String key, final double min, final double max) {
	    return getJedis(sf).zrangeByScore(key, min, max);
	  }

	  
	  public Set<String> zrangeByScore(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zrangeByScore(key, min, max);
	  }

	  /**
	   * Return the all the elements in the sorted set at key with a score between min and max
	   * (ShardingFunction sf, including elements with score equal to min or max).
	   * <p>
	   * The elements having the same score are returned sorted lexicographically as ASCII strings (ShardingFunction sf, this
	   * follows from a property of Redis sorted sets and does not involve further computation).
	   * <p>
	   * Using the optional {@link #zrangeByScore(ShardingFunction sf, String, double, double, int, int) LIMIT} it's possible
	   * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
	   * the commands needs to traverse the list for offset elements and this adds up to the O(ShardingFunction sf, M)
	   * figure.
	   * <p>
	   * The {@link #zcount(ShardingFunction sf, String, double, double) ZCOUNT} command is similar to
	   * {@link #zrangeByScore(ShardingFunction sf, String, double, double) ZRANGEBYSCORE} but instead of returning the
	   * actual elements in the specified interval, it just returns the number of matching elements.
	   * <p>
	   * <b>Exclusive intervals and infinity</b>
	   * <p>
	   * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
	   * smallest element in order to take, for instance, elements "up to a given value".
	   * <p>
	   * Also while the interval is for default closed (ShardingFunction sf, inclusive) it's possible to specify open
	   * intervals prefixing the score with a "(ShardingFunction sf, " character, so for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 1.3 5}
	   * <p>
	   * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 5 (ShardingFunction sf, 10}
	   * <p>
	   * Will return all the values with score &gt; 5 and &lt; 10 (ShardingFunction sf, 5 and 10 excluded).
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set and M the number of
	   * elements returned by the command, so if M is constant (ShardingFunction sf, for instance you always ask for the
	   * first ten elements with LIMIT) you can consider it O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double, int, int)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double, int, int)
	   * @see #zcount(ShardingFunction sf, String, double, double)
	   * @param key
	   * @param min
	   * @param max
	   * @return Multi bulk reply specifically a list of elements in the specified score range.
	   */
	  
	  public Set<String> zrangeByScore(ShardingFunction sf, final String key, final double min, final double max,
	      final int offset, final int count) {
	    return getJedis(sf).zrangeByScore(key, min, max, offset, count);
	  }

	  
	  public Set<String> zrangeByScore(ShardingFunction sf, final String key, final String min, final String max,
	      final int offset, final int count) {
	    return getJedis(sf).zrangeByScore(key, min, max, offset, count);
	  }

	  /**
	   * Return the all the elements in the sorted set at key with a score between min and max
	   * (ShardingFunction sf, including elements with score equal to min or max).
	   * <p>
	   * The elements having the same score are returned sorted lexicographically as ASCII strings (ShardingFunction sf, this
	   * follows from a property of Redis sorted sets and does not involve further computation).
	   * <p>
	   * Using the optional {@link #zrangeByScore(ShardingFunction sf, String, double, double, int, int) LIMIT} it's possible
	   * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
	   * the commands needs to traverse the list for offset elements and this adds up to the O(ShardingFunction sf, M)
	   * figure.
	   * <p>
	   * The {@link #zcount(ShardingFunction sf, String, double, double) ZCOUNT} command is similar to
	   * {@link #zrangeByScore(ShardingFunction sf, String, double, double) ZRANGEBYSCORE} but instead of returning the
	   * actual elements in the specified interval, it just returns the number of matching elements.
	   * <p>
	   * <b>Exclusive intervals and infinity</b>
	   * <p>
	   * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
	   * smallest element in order to take, for instance, elements "up to a given value".
	   * <p>
	   * Also while the interval is for default closed (ShardingFunction sf, inclusive) it's possible to specify open
	   * intervals prefixing the score with a "(ShardingFunction sf, " character, so for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 1.3 5}
	   * <p>
	   * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 5 (ShardingFunction sf, 10}
	   * <p>
	   * Will return all the values with score &gt; 5 and &lt; 10 (ShardingFunction sf, 5 and 10 excluded).
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set and M the number of
	   * elements returned by the command, so if M is constant (ShardingFunction sf, for instance you always ask for the
	   * first ten elements with LIMIT) you can consider it O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double, int, int)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double, int, int)
	   * @see #zcount(ShardingFunction sf, String, double, double)
	   * @param key
	   * @param min
	   * @param max
	   * @return Multi bulk reply specifically a list of elements in the specified score range.
	   */
	  
	  public Set<Tuple> zrangeByScoreWithScores(ShardingFunction sf, final String key, final double min, final double max) {
	    return getJedis(sf).zrangeByScoreWithScores(key, min, max);
	  }

	  
	  public Set<Tuple> zrangeByScoreWithScores(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zrangeByScoreWithScores(key, min, max);
	  }

	  /**
	   * Return the all the elements in the sorted set at key with a score between min and max
	   * (ShardingFunction sf, including elements with score equal to min or max).
	   * <p>
	   * The elements having the same score are returned sorted lexicographically as ASCII strings (ShardingFunction sf, this
	   * follows from a property of Redis sorted sets and does not involve further computation).
	   * <p>
	   * Using the optional {@link #zrangeByScore(ShardingFunction sf, String, double, double, int, int) LIMIT} it's possible
	   * to get only a range of the matching elements in an SQL-alike way. Note that if offset is large
	   * the commands needs to traverse the list for offset elements and this adds up to the O(ShardingFunction sf, M)
	   * figure.
	   * <p>
	   * The {@link #zcount(ShardingFunction sf, String, double, double) ZCOUNT} command is similar to
	   * {@link #zrangeByScore(ShardingFunction sf, String, double, double) ZRANGEBYSCORE} but instead of returning the
	   * actual elements in the specified interval, it just returns the number of matching elements.
	   * <p>
	   * <b>Exclusive intervals and infinity</b>
	   * <p>
	   * min and max can be -inf and +inf, so that you are not required to know what's the greatest or
	   * smallest element in order to take, for instance, elements "up to a given value".
	   * <p>
	   * Also while the interval is for default closed (ShardingFunction sf, inclusive) it's possible to specify open
	   * intervals prefixing the score with a "(ShardingFunction sf, " character, so for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 1.3 5}
	   * <p>
	   * Will return all the values with score &gt; 1.3 and &lt;= 5, while for instance:
	   * <p>
	   * {@code ZRANGEBYSCORE zset (ShardingFunction sf, 5 (ShardingFunction sf, 10}
	   * <p>
	   * Will return all the values with score &gt; 5 and &lt; 10 (ShardingFunction sf, 5 and 10 excluded).
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set and M the number of
	   * elements returned by the command, so if M is constant (ShardingFunction sf, for instance you always ask for the
	   * first ten elements with LIMIT) you can consider it O(ShardingFunction sf, log(ShardingFunction sf, N))
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScore(ShardingFunction sf, String, double, double, int, int)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double)
	   * @see #zrangeByScoreWithScores(ShardingFunction sf, String, double, double, int, int)
	   * @see #zcount(ShardingFunction sf, String, double, double)
	   * @param key
	   * @param min
	   * @param max
	   * @return Multi bulk reply specifically a list of elements in the specified score range.
	   */
	  
	  public Set<Tuple> zrangeByScoreWithScores(ShardingFunction sf, final String key, final double min, final double max,
	      final int offset, final int count) {
	    return getJedis(sf).zrangeByScoreWithScores(key, min, max, offset, count);
	  }

	  
	  public Set<Tuple> zrangeByScoreWithScores(ShardingFunction sf, final String key, final String min, final String max,
	      final int offset, final int count) {
	    return getJedis(sf).zrangeByScoreWithScores(key, min, max, offset, count);
	  }

	  public Set<String> zrevrangeByScore(ShardingFunction sf, final String key, final double max, final double min) {
	    return getJedis(sf).zrevrangeByScore(key, max, min);
	  }

	  
	  public Set<String> zrevrangeByScore(ShardingFunction sf, final String key, final String max, final String min) {
	    return getJedis(sf).zrevrangeByScore(key, max, min);
	  }

	  
	  public Set<String> zrevrangeByScore(ShardingFunction sf, final String key, final double max, final double min,
	      final int offset, final int count) {
	    return getJedis(sf).zrevrangeByScore(key, max, min, offset, count);
	  }

	  
	  public Set<Tuple> zrevrangeByScoreWithScores(ShardingFunction sf, final String key, final double max, final double min) {
	    return getJedis(sf).zrevrangeByScoreWithScores(key, max, min);
	  }

	  
	  public Set<Tuple> zrevrangeByScoreWithScores(ShardingFunction sf, final String key, final double max,
	      final double min, final int offset, final int count) {
	    return getJedis(sf).zrevrangeByScoreWithScores(key, max, min, offset, count);
	  }

	  
	  public Set<Tuple> zrevrangeByScoreWithScores(ShardingFunction sf, final String key, final String max,
	      final String min, final int offset, final int count) {
	    return getJedis(sf).zrevrangeByScoreWithScores(key, max, min, offset, count);
	  }

	  
	  public Set<String> zrevrangeByScore(ShardingFunction sf, final String key, final String max, final String min,
	      final int offset, final int count) {
	    return getJedis(sf).zrevrangeByScore(key, max, min, offset, count);
	  }

	  
	  public Set<Tuple> zrevrangeByScoreWithScores(ShardingFunction sf, final String key, final String max, final String min) {
	    return getJedis(sf).zrevrangeByScoreWithScores(key, max, min);
	  }

	  /**
	   * Remove all elements in the sorted set at key with rank between start and end. Start and end are
	   * 0-based with rank 0 being the element with the lowest score. Both start and end can be negative
	   * numbers, where they indicate offsets starting at the element with the highest rank. For
	   * example: -1 is the element with the highest score, -2 the element with the second highest score
	   * and so forth.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set
	   * and M the number of elements removed by the operation
	   */
	  
	  public Long zremrangeByRank(ShardingFunction sf, final String key, final long start, final long end) {
	    return getJedis(sf).zremrangeByRank(key, start, end);
	  }

	  /**
	   * Remove all the elements in the sorted set at key with a score between min and max (ShardingFunction sf, including
	   * elements with score equal to min or max).
	   * <p>
	   * <b>Time complexity:</b>
	   * <p>
	   * O(ShardingFunction sf, log(ShardingFunction sf, N))+O(ShardingFunction sf, M) with N being the number of elements in the sorted set and M the number of
	   * elements removed by the operation
	   * @param key
	   * @param start
	   * @param end
	   * @return Integer reply, specifically the number of elements removed.
	   */
	  
	  public Long zremrangeByScore(ShardingFunction sf, final String key, final double start, final double end) {
	    return getJedis(sf).zremrangeByScore(key, start, end);
	  }

	  
	  public Long zremrangeByScore(ShardingFunction sf, final String key, final String start, final String end) {
	    return getJedis(sf).zremrangeByScore(key, start, end);
	  }

	  /**
	   * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
	   * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
	   * and the other (ShardingFunction sf, optional) arguments.
	   * <p>
	   * As the terms imply, the {@link #zinterstore(ShardingFunction sf, String, String...) ZINTERSTORE} command requires an
	   * element to be present in each of the given inputs to be inserted in the result. The
	   * {@link #zunionstore(ShardingFunction sf, String, String...) ZUNIONSTORE} command inserts all elements across all
	   * inputs.
	   * <p>
	   * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
	   * that the score of each element in the sorted set is first multiplied by this weight before
	   * being passed to the aggregation. When this option is not given, all weights default to 1.
	   * <p>
	   * With the AGGREGATE option, it's possible to specify how the results of the union or
	   * intersection are aggregated. This option defaults to SUM, where the score of an element is
	   * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
	   * resulting set will contain the minimum or maximum score of an element across the inputs where
	   * it exists.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) + O(ShardingFunction sf, M log(ShardingFunction sf, M)) with N being the sum of the sizes of the input
	   * sorted sets, and M being the number of elements in the resulting sorted set
	   * @see #zunionstore(ShardingFunction sf, String, String...)
	   * @see #zunionstore(ShardingFunction sf, String, ZParams, String...)
	   * @see #zinterstore(ShardingFunction sf, String, String...)
	   * @see #zinterstore(ShardingFunction sf, String, ZParams, String...)
	   * @param dstkey
	   * @param sets
	   * @return Integer reply, specifically the number of elements in the sorted set at dstkey
	   */
	  
	  public Long zunionstore(ShardingFunction sf, final String dstkey, final String... sets) {
	    return getJedis(sf).zunionstore(dstkey, sets);
	  }

	  /**
	   * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
	   * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
	   * and the other (ShardingFunction sf, optional) arguments.
	   * <p>
	   * As the terms imply, the {@link #zinterstore(ShardingFunction sf, String, String...) ZINTERSTORE} command requires an
	   * element to be present in each of the given inputs to be inserted in the result. The
	   * {@link #zunionstore(ShardingFunction sf, String, String...) ZUNIONSTORE} command inserts all elements across all
	   * inputs.
	   * <p>
	   * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
	   * that the score of each element in the sorted set is first multiplied by this weight before
	   * being passed to the aggregation. When this option is not given, all weights default to 1.
	   * <p>
	   * With the AGGREGATE option, it's possible to specify how the results of the union or
	   * intersection are aggregated. This option defaults to SUM, where the score of an element is
	   * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
	   * resulting set will contain the minimum or maximum score of an element across the inputs where
	   * it exists.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) + O(ShardingFunction sf, M log(ShardingFunction sf, M)) with N being the sum of the sizes of the input
	   * sorted sets, and M being the number of elements in the resulting sorted set
	   * @see #zunionstore(ShardingFunction sf, String, String...)
	   * @see #zunionstore(ShardingFunction sf, String, ZParams, String...)
	   * @see #zinterstore(ShardingFunction sf, String, String...)
	   * @see #zinterstore(ShardingFunction sf, String, ZParams, String...)
	   * @param dstkey
	   * @param sets
	   * @param params
	   * @return Integer reply, specifically the number of elements in the sorted set at dstkey
	   */
	  
	  public Long zunionstore(ShardingFunction sf, final String dstkey, final ZParams params, final String... sets) {
	    return getJedis(sf).zunionstore(dstkey, params, sets);
	  }

	  /**
	   * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
	   * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
	   * and the other (ShardingFunction sf, optional) arguments.
	   * <p>
	   * As the terms imply, the {@link #zinterstore(ShardingFunction sf, String, String...) ZINTERSTORE} command requires an
	   * element to be present in each of the given inputs to be inserted in the result. The
	   * {@link #zunionstore(ShardingFunction sf, String, String...) ZUNIONSTORE} command inserts all elements across all
	   * inputs.
	   * <p>
	   * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
	   * that the score of each element in the sorted set is first multiplied by this weight before
	   * being passed to the aggregation. When this option is not given, all weights default to 1.
	   * <p>
	   * With the AGGREGATE option, it's possible to specify how the results of the union or
	   * intersection are aggregated. This option defaults to SUM, where the score of an element is
	   * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
	   * resulting set will contain the minimum or maximum score of an element across the inputs where
	   * it exists.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) + O(ShardingFunction sf, M log(ShardingFunction sf, M)) with N being the sum of the sizes of the input
	   * sorted sets, and M being the number of elements in the resulting sorted set
	   * @see #zunionstore(ShardingFunction sf, String, String...)
	   * @see #zunionstore(ShardingFunction sf, String, ZParams, String...)
	   * @see #zinterstore(ShardingFunction sf, String, String...)
	   * @see #zinterstore(ShardingFunction sf, String, ZParams, String...)
	   * @param dstkey
	   * @param sets
	   * @return Integer reply, specifically the number of elements in the sorted set at dstkey
	   */
	  
	  public Long zinterstore(ShardingFunction sf, final String dstkey, final String... sets) {
	    return getJedis(sf).zinterstore(dstkey, sets);
	  }

	  /**
	   * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it at
	   * dstkey. It is mandatory to provide the number of input keys N, before passing the input keys
	   * and the other (ShardingFunction sf, optional) arguments.
	   * <p>
	   * As the terms imply, the {@link #zinterstore(ShardingFunction sf, String, String...) ZINTERSTORE} command requires an
	   * element to be present in each of the given inputs to be inserted in the result. The
	   * {@link #zunionstore(ShardingFunction sf, String, String...) ZUNIONSTORE} command inserts all elements across all
	   * inputs.
	   * <p>
	   * Using the WEIGHTS option, it is possible to add weight to each input sorted set. This means
	   * that the score of each element in the sorted set is first multiplied by this weight before
	   * being passed to the aggregation. When this option is not given, all weights default to 1.
	   * <p>
	   * With the AGGREGATE option, it's possible to specify how the results of the union or
	   * intersection are aggregated. This option defaults to SUM, where the score of an element is
	   * summed across the inputs where it exists. When this option is set to be either MIN or MAX, the
	   * resulting set will contain the minimum or maximum score of an element across the inputs where
	   * it exists.
	   * <p>
	   * <b>Time complexity:</b> O(ShardingFunction sf, N) + O(ShardingFunction sf, M log(ShardingFunction sf, M)) with N being the sum of the sizes of the input
	   * sorted sets, and M being the number of elements in the resulting sorted set
	   * @see #zunionstore(ShardingFunction sf, String, String...)
	   * @see #zunionstore(ShardingFunction sf, String, ZParams, String...)
	   * @see #zinterstore(ShardingFunction sf, String, String...)
	   * @see #zinterstore(ShardingFunction sf, String, ZParams, String...)
	   * @param dstkey
	   * @param sets
	   * @param params
	   * @return Integer reply, specifically the number of elements in the sorted set at dstkey
	   */
	  
	  public Long zinterstore(ShardingFunction sf, final String dstkey, final ZParams params, final String... sets) {
	    return getJedis(sf).zinterstore(dstkey, params, sets);
	  }

	  
	  public Long zlexcount(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zlexcount(key, min, max);
	  }

	  
	  public Set<String> zrangeByLex(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zrangeByLex(key, min, max);
	  }

	  
	  public Set<String> zrangeByLex(ShardingFunction sf, final String key, final String min, final String max,
	      final int offset, final int count) {
		  return getJedis(sf).zrangeByLex(key, min, max);
	  }

	  
	  public Set<String> zrevrangeByLex(ShardingFunction sf, String key, String max, String min) {
		  return getJedis(sf).zrevrangeByLex(key, max, min);
	  }

	  
	  public Set<String> zrevrangeByLex(ShardingFunction sf, String key, String max, String min, int offset, int count) {
		  return getJedis(sf).zrevrangeByLex(key, max, min);
	  }

	  public Long zremrangeByLex(ShardingFunction sf, final String key, final String min, final String max) {
	    return getJedis(sf).zremrangeByLex(key, max, max);
	  }

	  
	  public Long strlen(ShardingFunction sf, final String key) {
	    return getJedis(sf).strlen(key);
	  }

	  
	  public Long lpushx(ShardingFunction sf, final String key, final String... string) {
	    return getJedis(sf).lpushx(key, string);
	  }

	  /**
	   * Undo a {@link #expire(ShardingFunction sf, String, int) expire} at turning the expire key into a normal key.
	   * <p>
	   * Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @return Integer reply, specifically: 1: the key is now persist. 0: the key is not persist (ShardingFunction sf, only
	   *         happens when key not set).
	   */
	  
	  public Long persist(ShardingFunction sf, final String key) {
	    return getJedis(sf).persist(key);
	  }

	  
	  public Long rpushx(ShardingFunction sf, final String key, final String... string) {
	    return getJedis(sf).rpushx(key, string);
	  }

	  
	  public String echo(ShardingFunction sf, final String string) {
	    return getJedis(sf).echo(string);
	  }

	  
	  public Long linsert(ShardingFunction sf, final String key, final LIST_POSITION where, final String pivot,
	      final String value) {
	    return getJedis(sf).linsert(key, where, pivot, value);
	  }

	  /**
	   * Pop a value from a list, push it to another list and return it; or block until one is available
	   * @param source
	   * @param destination
	   * @param timeout
	   * @return the element
	   */
	  
	  public String brpoplpush(ShardingFunction sf, String source, String destination, int timeout) {
	      return getJedis(sf).brpoplpush(source, destination, timeout);
	  }

	  /**
	   * Sets or clears the bit at offset in the string value stored at key
	   * @param key
	   * @param offset
	   * @param value
	   * @return
	   */
	  
	  public Boolean setbit(ShardingFunction sf, String key, long offset, boolean value) {
	    return getJedis(sf).setbit(key, offset, value);
	  }

	  
	  public Boolean setbit(ShardingFunction sf, String key, long offset, String value) {
	    return getJedis(sf).setbit(key, offset, value);
	  }

	  /**
	   * Returns the bit value at offset in the string value stored at key
	   * @param key
	   * @param offset
	   * @return
	   */
	  
	  public Boolean getbit(ShardingFunction sf, String key, long offset) {
	    return getJedis(sf).getbit(key, offset);
	  }

	  
	  public Long setrange(ShardingFunction sf, String key, long offset, String value) {
	    return getJedis(sf).setrange(key, offset, value);
	  }

	  
	  public String getrange(ShardingFunction sf, String key, long startOffset, long endOffset) {
	    return getJedis(sf).getrange(key, startOffset, endOffset);
	  }

	  
	  public Long bitpos(ShardingFunction sf, final String key, final boolean value) {
	    return getJedis(sf).bitpos(key, value);
	  }

	  
	  public Long bitpos(ShardingFunction sf, final String key, final boolean value, final BitPosParams params) {
	    return getJedis(sf).bitpos(key, value, params);
	  }

	  /**
	   * Retrieve the configuration of a running Redis server. Not all the configuration parameters are
	   * supported.
	   * <p>
	   * CONFIG GET returns the current configuration parameters. This sub command only accepts a single
	   * argument, that is glob style pattern. All the configuration parameters matching this parameter
	   * are reported as a list of key-value pairs.
	   * <p>
	   * <b>Example:</b>
	   * 
	   * <pre>
	   * $ redis-cli config get '*'
	   * 1. "dbfilename"
	   * 2. "dump.rdb"
	   * 3. "requirepass"
	   * 4. (ShardingFunction sf, nil)
	   * 5. "masterauth"
	   * 6. (ShardingFunction sf, nil)
	   * 7. "maxmemory"
	   * 8. "0\n"
	   * 9. "appendfsync"
	   * 10. "everysec"
	   * 11. "save"
	   * 12. "3600 1 300 100 60 10000"
	   * 
	   * $ redis-cli config get 'm*'
	   * 1. "masterauth"
	   * 2. (ShardingFunction sf, nil)
	   * 3. "maxmemory"
	   * 4. "0\n"
	   * </pre>
	   * @param pattern
	   * @return Bulk reply.
	   */
	  
	  public List<String> configGet(ShardingFunction sf, final String pattern) {
	    return getJedis(sf).configGet(pattern);
	  }

	  /**
	   * Alter the configuration of a running Redis server. Not all the configuration parameters are
	   * supported.
	   * <p>
	   * The list of configuration parameters supported by CONFIG SET can be obtained issuing a
	   * {@link #configGet(ShardingFunction sf, String) CONFIG GET *} command.
	   * <p>
	   * The configuration set using CONFIG SET is immediately loaded by the Redis server that will
	   * start acting as specified starting from the next command.
	   * <p>
	   * <b>Parameters value format</b>
	   * <p>
	   * The value of the configuration parameter is the same as the one of the same parameter in the
	   * Redis configuration file, with the following exceptions:
	   * <p>
	   * <ul>
	   * <li>The save paramter is a list of space-separated integers. Every pair of integers specify the
	   * time and number of changes limit to trigger a save. For instance the command CONFIG SET save
	   * "3600 10 60 10000" will configure the server to issue a background saving of the RDB file every
	   * 3600 seconds if there are at least 10 changes in the dataset, and every 60 seconds if there are
	   * at least 10000 changes. To completely disable automatic snapshots just set the parameter as an
	   * empty string.
	   * <li>All the integer parameters representing memory are returned and accepted only using bytes
	   * as unit.
	   * </ul>
	   * @param parameter
	   * @param value
	   * @return Status code reply
	   */
	  
	  public String configSet(ShardingFunction sf, final String parameter, final String value) {
	    return getJedis(sf).configSet(parameter, value);
	  }

	  
	  public Object eval(ShardingFunction sf, String script, int keyCount, String... params) {
		  return getJedis(sf).eval(script, keyCount, params);
	  }

	  
	  public void subscribe(ShardingFunction sf, final JedisPubSub jedisPubSub, final String... channels) {
		  getJedis(sf).subscribe(jedisPubSub, channels);
	  }

	  
	  public Long publish(ShardingFunction sf, final String channel, final String message) {
	    return getJedis(sf).publish(channel, message);
	  }

	  
	  public void psubscribe(ShardingFunction sf, final JedisPubSub jedisPubSub, final String... patterns) {
		  getJedis(sf).psubscribe(jedisPubSub, patterns);
	  }

	  public Object eval(ShardingFunction sf, String script, List<String> keys, List<String> args) {
	    return getJedis(sf).eval(script, keys, args);
	  }

	  
	  public Object eval(ShardingFunction sf, String script) {
	    return getJedis(sf).eval(script);
	  }

	  
	  public Object evalsha(ShardingFunction sf, String script) {
	    return getJedis(sf).evalsha(script);
	  }

	  public Object evalsha(ShardingFunction sf, String sha1, List<String> keys, List<String> args) {
	    return getJedis(sf).evalsha(sha1, keys, args);
	  }

	  
	  public Object evalsha(ShardingFunction sf, String sha1, int keyCount, String... params) {
	    return getJedis(sf).evalsha(sha1, keyCount, params);
	  }

	  
	  public Boolean scriptExists(ShardingFunction sf, String sha1) {
	    return getJedis(sf).scriptExists(sha1);
	  }

	  
	  public List<Boolean> scriptExists(ShardingFunction sf, String... sha1) {
	    return getJedis(sf).scriptExists(sha1);
	  }

	  
	  public String scriptLoad(ShardingFunction sf, String script) {
	    return getJedis(sf).scriptLoad(script);
	  }

	  public Long objectRefcount(ShardingFunction sf, String string) {
	    return getJedis(sf).objectRefcount(string);
	  }

	  
	  public String objectEncoding(ShardingFunction sf, String string) {
	    return getJedis(sf).objectEncoding(string);
	  }

	  
	  public Long objectIdletime(ShardingFunction sf, String string) {
	    return getJedis(sf).objectIdletime(string);
	  }

	  
	  public Long bitcount(ShardingFunction sf, final String key) {
	    return getJedis(sf).bitcount(key);
	  }

	  
	  public Long bitcount(ShardingFunction sf, final String key, long start, long end) {
	    return getJedis(sf).bitcount(key, start, end);
	  }

	  
	  public Long bitop(ShardingFunction sf, BitOP op, final String destKey, String... srcKeys) {
	    return getJedis(sf).bitop(op, destKey, srcKeys);
	  }

	  /**
	   * <pre>
	   * redis 127.0.0.1:26381&gt; sentinel masters
	   * 1)  1) "name"
	   *     2) "mymaster"
	   *     3) "ip"
	   *     4) "127.0.0.1"
	   *     5) "port"
	   *     6) "6379"
	   *     7) "runid"
	   *     8) "93d4d4e6e9c06d0eea36e27f31924ac26576081d"
	   *     9) "flags"
	   *    10) "master"
	   *    11) "pending-commands"
	   *    12) "0"
	   *    13) "last-ok-ping-reply"
	   *    14) "423"
	   *    15) "last-ping-reply"
	   *    16) "423"
	   *    17) "info-refresh"
	   *    18) "6107"
	   *    19) "num-slaves"
	   *    20) "1"
	   *    21) "num-other-sentinels"
	   *    22) "2"
	   *    23) "quorum"
	   *    24) "2"
	   * 
	   * </pre>
	   * @return
	   */
	  
	  public byte[] dump(ShardingFunction sf, final String key) {
	    return getJedis(sf).dump(key);
	  }

	  public String restore(ShardingFunction sf, final String key, final int ttl, final byte[] serializedValue) {
	    return getJedis(sf).restore(key, ttl, serializedValue);
	  }

	  
	  public Long pexpire(ShardingFunction sf, final String key, final long milliseconds) {
	    return getJedis(sf).pexpire(key, milliseconds);
	  }

	  
	  public Long pexpireAt(ShardingFunction sf, final String key, final long millisecondsTimestamp) {
	    return getJedis(sf).pexpireAt(key, millisecondsTimestamp);
	  }

	  
	  public Long pttl(ShardingFunction sf, final String key) {
	    return getJedis(sf).pttl(key);
	  }

	  /**
	   * PSETEX works exactly like {@link #setex(ShardingFunction sf, String, int, String)} with the sole difference that the
	   * expire time is specified in milliseconds instead of seconds. Time complexity: O(ShardingFunction sf, 1)
	   * @param key
	   * @param milliseconds
	   * @param value
	   * @return Status code reply
	   */

	  
	  public String psetex(ShardingFunction sf, final String key, final long milliseconds, final String value) {
	    return getJedis(sf).psetex(key, milliseconds, value);
	  }

	  public String migrate(ShardingFunction sf, final String host, final int port, final String key,
	      final int destinationDb, final int timeout) {
	    return getJedis(sf).migrate(host, port, key, destinationDb, timeout);
	  }

	  
	  public ScanResult<String> scan(ShardingFunction sf, final String cursor) {
	    return getJedis(sf).scan(cursor);
	  }

	  
	  public ScanResult<String> scan(ShardingFunction sf, final String cursor, final ScanParams params) {
	    return getJedis(sf).scan(cursor, params);
	  }

	  
	  public ScanResult<Map.Entry<String, String>> hscan(ShardingFunction sf, final String key, final String cursor) {
	    return getJedis(sf).hscan(key, cursor);
	  }

	  
	  public ScanResult<Map.Entry<String, String>> hscan(ShardingFunction sf, final String key, final String cursor,
	      final ScanParams params) {
	    return getJedis(sf).hscan(key, cursor, params);
	  }

	  
	  public ScanResult<String> sscan(ShardingFunction sf, final String key, final String cursor) {
	    return getJedis(sf).sscan(key, cursor);
	  }

	  
	  public ScanResult<String> sscan(ShardingFunction sf, final String key, final String cursor, final ScanParams params) {
	    return getJedis(sf).sscan(key, cursor, params);
	  }

	  
	  public ScanResult<Tuple> zscan(ShardingFunction sf, final String key, final String cursor) {
	    return getJedis(sf).zscan(key, cursor);
	  }

	  
	  public ScanResult<Tuple> zscan(ShardingFunction sf, final String key, final String cursor, final ScanParams params) {
	    return getJedis(sf).zscan(key, cursor, params);
	  }

	  public void close() {
		  if (jedisPool != null) {
			  for (Jedis jedis : jedisPool) {
				  jedis.close();
			  }
		}
	  }

	  public Long pfadd(ShardingFunction sf, final String key, final String... elements) {
	    return getJedis(sf).pfadd(key, elements);
	  }
	  
	  public long pfcount(ShardingFunction sf, final String key) {
	    return getJedis(sf).pfcount(key);
	  }

	  
	  public long pfcount(ShardingFunction sf, String... keys) {
	    return getJedis(sf).pfcount(keys);
	  }

	  
	  public String pfmerge(ShardingFunction sf, final String destkey, final String... sourcekeys) {
	    return getJedis(sf).pfmerge(destkey, sourcekeys);
	  }

	  
	  public List<String> blpop(ShardingFunction sf, int timeout, String key) {
	    return getJedis(sf).blpop(timeout, key);
	  }

	  
	  public List<String> brpop(ShardingFunction sf, int timeout, String key) {
	    return getJedis(sf).brpop(timeout, key);
	  }

	  
	  public Long xpush(ShardingFunction sf, final String key, final String... strings) {
	    return getJedis(sf).xpush(key, strings);
	  }
	  
	  
	  public Long xreset(ShardingFunction sf, final String key, final String value) {
	    return getJedis(sf).xreset(key, value);
	  }
	  
		
	public Map<String, Set<String>> smembersmulti(ShardingFunction sf, String... keys) {
		return getJedis(sf).smembersmulti(keys);
	}
	  
	  public Long zoff(ShardingFunction sf, final String key, final double score, final String member) {
		    return getJedis(sf).zoff(key, score, member);
	  }
	 
	  
	  public Long zoff(ShardingFunction sf, final String key, final Map<String, Double> scoreMembers) {
	    return getJedis(sf).zoff(key, scoreMembers);
	  }
	  
	  
	  public Long zreset(ShardingFunction sf, final String key, final String value) {
	    return getJedis(sf).zreset(key, value);
	  }
	  
	  
	  public Map<String, Map<String, String>> hgetallmulti(ShardingFunction sf, String... keys) {
		  return getJedis(sf).hgetallmulti(keys);
	  }
}
