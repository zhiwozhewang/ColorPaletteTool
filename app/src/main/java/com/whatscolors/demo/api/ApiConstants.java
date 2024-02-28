
package com.whatscolors.demo.api;

public class ApiConstants {
    private static boolean ISONLINE = true;
    public static final String NETEAST_HOST = ISONLINE ? "https://whatscolors.hkrstore.com/" : "https://whatscolors-test.hkrstore.com/";//"https://ws-test.hkrstore.com/";//
    public static final int STATE_OLD_LOGIN = 123, STATE_SUCCESS = 200, STATE_RELOGIN = -1;

}
