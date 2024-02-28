package com.whatscolors.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.whatscolors.demo.base.BaseApplication;

import java.util.Arrays;
import java.util.List;


/**
 * 对SharedPreference文件中的各种类型的数据进行存取操作
 */
public class SPUtils {

    private static SharedPreferences sp;

    private static void init(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getAppContext());
        }
    }

    public static void setSharedIntData(Context context, String key, int value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getSharedIntData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getInt(key, 0);
    }

    public static void setSharedlongData(Context context, String key, long value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putLong(key, value).commit();
    }

    public static long getSharedlongData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getLong(key, 0l);
    }

    public static void setSharedFloatData(Context context, String key,
                                          float value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putFloat(key, value).commit();
    }

    public static Float getSharedFloatData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getFloat(key, 0f);
    }

    public static void setSharedBooleanData(Context context, String key,
                                            boolean value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    public static Boolean getSharedBooleanData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getBoolean(key, false);
    }

    public static void setSharedStringData(Context context, String key, String value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getSharedStringData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getString(key, "");
    }

    public static String getStringUnitData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getString(key, "dspc");
    }

    public static void setUser_unique_codeData(Context context, String value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putString("user_unique_code", value).commit();
    }

    public static String getUser_unique_codeData(Context context) {
        if (sp == null) {
            init(context);
        }
        return sp.getString("user_unique_code", "");
    }

    public static void setUuidData(Context context, String value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putString("uuid", value).commit();
    }

    public static String getUuidData(Context context) {
        if (sp == null) {
            init(context);
        }
        return sp.getString("uuid", "");
    }

    public static String getService_code(Context context) {
        if (sp == null) {
            init(context);
        }
        return sp.getString("service_code", "80aa25bf02064425853ecfbf2fe5543b");
    }

    public static void setIsVipBooleanData(Context context,
                                           boolean value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putBoolean("user_vip", value).commit();
    }

    public static Boolean getIsVipdBooleanData(Context context) {
        if (sp == null) {
            init(context);
        }
        return sp.getBoolean("user_vip", false);
    }

    public static Boolean getIsLogin(Context context) {
        if (sp == null) {
            init(context);
        }
        return sp.getBoolean("Use_islogin", false);
    }

    public static Boolean setIsLogin(Context context, boolean islogin) {
        if (sp == null) {
            init(context);
        }
        return sp.edit().putBoolean("Use_islogin", islogin).commit();
    }

    public static void setSearchOldData(Context context, String value) {
        if (TextUtils.isEmpty(value))
            return;
        if (sp == null) {
            init(context);
        }
        StringBuilder stringBuilder;
        String string = sp.getString("search_old_data", "");
        if (TextUtils.isEmpty(string)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(value);
        } else {
            stringBuilder = new StringBuilder(string);
            if (stringBuilder.toString().contains(value))
                return;
            stringBuilder.append("," + value);
        }
        sp.edit().putString("search_old_data", stringBuilder.toString()).commit();
    }

    public static List<String> getSearchOldData(Context context) {
        if (sp == null) {
            init(context);
        }
        StringBuilder stringBuilder;
        String string = sp.getString("search_old_data", "");
        if (TextUtils.isEmpty(string)) {
            return null;
        } else {
            stringBuilder = new StringBuilder(string);
            return Arrays.asList(stringBuilder.toString().split(","));
        }

    }

    public static void clearSearchOldData(Context context) {
        if (sp == null) {
            init(context);
        }

        sp.edit().putString("search_old_data", null).commit();
    }

    public static void deleteSearchOldData(Context context, String vaule) {
        if (sp == null) {
            init(context);
        }
        String string = sp.getString("search_old_data", "");
        if (!TextUtils.isEmpty(string)) {
            if (string.contains(vaule)) {
                if (string.startsWith(vaule)) {
                    string = string.replace(vaule + ",", "");
                } else {
                    string = string.replace("," + vaule, "");
                }
            }
        }
        sp.edit().putString("search_old_data", string).commit();
    }
}
