package redis.clients.jedis;

public abstract class MultiOperation {
	public MultiOperation(Object[] params){
		this.params=params;
	}
	public Object[] getParams() {
		return params;
	}
	private Object[] params;
	public abstract void operate(String key,Client client);
}
