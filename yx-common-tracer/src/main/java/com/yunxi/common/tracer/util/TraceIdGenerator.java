package com.yunxi.common.tracer.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * TraceId生成器
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TraceIdGenerator.java, v 0.1 2016年12月26日 下午4:52:45 leukony Exp $
 */
public class TraceIdGenerator {

    private static String        IP_16   = "ffffffff";
    private static String        IP_int  = "255255255255";

    private static final String  regex   = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    private static final Pattern pattern = Pattern.compile(regex);
    private static AtomicInteger count   = new AtomicInteger(1000);

    static {
        try {
            String ipAddress = getInetAddress();
            if (ipAddress != null) {
                IP_16 = getIP_16(ipAddress);
                IP_int = getIP_int(ipAddress);
            }
        } catch (Throwable e) {
        }
    }

    private static String getTraceId(String ip, long timestamp, int nextId) {
        StringBuilder appender = new StringBuilder(25);
        appender.append(ip).append(timestamp).append(nextId);
        return appender.toString();
    }

    public static String generate() {
        return getTraceId(IP_16, System.currentTimeMillis(), getNextId());
    }

    static String generate(String ip) {
        if (ip != null && !ip.isEmpty() && validate(ip)) {
            return getTraceId(getIP_16(ip), System.currentTimeMillis(), getNextId());
        } else {
            return generate();
        }
    }

    static String generateIpv4Id() {
        return IP_int;
    }

    static String generateIpv4Id(String ip) {
        if (ip != null && !ip.isEmpty() && validate(ip)) {
            return getIP_int(ip);
        } else {
            return IP_int;
        }
    }

    private static boolean validate(String ip) {
        try {
            return pattern.matcher(ip).matches();
        } catch (Throwable e) {
            return false;
        }
    }

    private static String getIP_16(String ip) {
        String[] ips = ip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String column : ips) {
            String hex = Integer.toHexString(Integer.parseInt(column));
            if (hex.length() == 1) {
                sb.append('0').append(hex);
            } else {
                sb.append(hex);
            }

        }
        return sb.toString();
    }

    private static String getIP_int(String ip) {
        return ip.replace(".", "");
    }

    private static String getInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostAddress();
                    }
                }
            }
            return null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static int getNextId() {
        for (;;) {
            int current = count.get();
            int next = (current > 9000) ? 1000 : current + 1;
            if (count.compareAndSet(current, next))
                return next;
        }
    }
}