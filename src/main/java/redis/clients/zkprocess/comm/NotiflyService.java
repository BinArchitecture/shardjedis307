package redis.clients.zkprocess.comm;

/**
 * 通知接口
 */
public interface NotiflyService {

    /**
     * 进行通知接口
     * @throws Exception 异常操作
     * @return true 通知更新成功，false ，更新失败
     */
    Object notiflyProcess(boolean write) throws Exception;

}
