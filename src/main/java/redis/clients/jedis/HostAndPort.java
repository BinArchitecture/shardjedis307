package redis.clients.jedis;

public class HostAndPort {
  public String getPasswd() {
		return passwd;
	}

public static final String LOCALHOST_STR = "localhost";

  private String host;
  public void setPasswd(String passwd) {
	this.passwd = passwd;
}

private int port;
  private String passwd;

  public HostAndPort(String host, int port,String passwd) {
    this.host = host;
    this.port = port;
    this.passwd = passwd;
  }
  
  public HostAndPort(String host, int port) {
	    this.host = host;
	    this.port = port;
	  }
  
  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }


  @Override
  public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((host == null) ? 0 : host.hashCode());
	result = prime * result + ((passwd == null) ? 0 : passwd.hashCode());
	result = prime * result + port;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	HostAndPort other = (HostAndPort) obj;
	if (host == null) {
		if (other.host != null)
			return false;
	} else if (!host.equals(other.host))
		return false;
	if (passwd == null) {
		if (other.passwd != null)
			return false;
	} else if (!passwd.equals(other.passwd))
		return false;
	if (port != other.port)
		return false;
	return true;
}

@Override
  public String toString() {
    return host + ":" + port;
  }

  private String convertHost(String host) {
    if (host.equals("127.0.0.1")) return LOCALHOST_STR;
    else if (host.equals("::1")) return LOCALHOST_STR;

    return host;
  }
}
