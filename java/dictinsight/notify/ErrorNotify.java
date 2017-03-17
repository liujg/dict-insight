/**
 * @(#)ErroNotify.java, 2015年11月20日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.notify;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dictinsight.utils.HashMapCache;
import smssender.client.SmsSender;

/**
 * it doesnot worth to thread safe
 * 
 * @author liujg
 */
public class ErrorNotify {

    public static String group = "liujg";

    public static String HOSTNAME;

    public static HashMapCache<String, Alarm> errorStaticMap = new HashMapCache<String, Alarm>(
            100, 2 * 60 * 60, 1);

    static {
        File file = new File("");
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName().toString() + ":"
                    + file.getAbsolutePath();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        errorStaticMap.init();
    }

    public static void addException(Exception e) {
        try {
            Alarm alarm = errorStaticMap.get(convertException2String(e));
            if (null == alarm) {
                alarm = new Alarm(e);
                errorStaticMap.put(convertException2String(e), alarm);
            } else
                alarm.errorIncrease();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void directAlarm(Exception e) {
        convertException2String(e);
    }

    public static class Alarm {

        public int alarmNum;

        public long lastTime;

        private Exception e;

        public Alarm(Exception e) {
            this.e = e;
            this.errorIncrease();
        }

        public void errorIncrease() {
            this.alarmNum += 1;
            if (0 == lastTime) {
                this.lastTime = System.currentTimeMillis();
                SmsSender.send(group, convertException2String(e));
            } else {
                double gap = Math.exp(alarmNum) - Math.exp(alarmNum - 1);// 时间指数级别增长报警
                gap = 5 * 1000 * gap;
                if (System.currentTimeMillis() - this.lastTime > gap) {
                    this.lastTime = System.currentTimeMillis();
                    SmsSender.send(group, convertException2String(e));
                }
            }
        }
    }

    public static String convertException2String(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String error = sw.toString();
        return (error.length() > 100 ? error.substring(0, 100) : error)
                + " error at " + HOSTNAME;
    }
}
