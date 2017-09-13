package redis.clients.zkprocess.utils;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkUtils {
	
	public static CuratorFramework buildConnection(String url) {
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
				url, new ExponentialBackoffRetry(100, 6));

		// start connection
		curatorFramework.start();
		// wait 3 second to establish connect
		try {
			curatorFramework.blockUntilConnected(3, TimeUnit.SECONDS);
			if (curatorFramework.getZookeeperClient().isConnected()) {
				return curatorFramework.usingNamespace("");
			}
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		// fail situation
		curatorFramework.close();
		throw new RuntimeException("failed to connect to zookeeper service : "
				+ url);
	}

}
