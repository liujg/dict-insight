/**
 * 
 */
package dictinsight.redis;

import dictinsight.utils.io.FileUtils;

/**
 * @author liujg
 */
public class RedisConst {

    public static final String seedList = "http://config.corp.yodao.com/svn/yodaoconfig/redis-cluster/seedlist";

    public static String filename;
    
    public static final int MINUTEIMEOUT = 60;

    public static final int HOURIMEOUT = 60 * MINUTEIMEOUT;

    public static final int DAYTIMEOUT = 24 * HOURIMEOUT;

    public static final int MONTHIMEOUT = 30 * DAYTIMEOUT;

    public static final int HAFLYEARTIMEOUT = 6 * MONTHIMEOUT;

    public static final String EMPTY = "null";// 默认value缺省值
    
    public static final byte EMPTY_BYTE = '0';// 默认value缺省值

}
