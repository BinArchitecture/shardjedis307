package redis.clients.zkprocess.utils;

public class StringUtils {
    public static boolean isBlank(String str){
    	if(str ==null || str.length()==0){
    		return true;
    	}
    	return false;
    }
}
