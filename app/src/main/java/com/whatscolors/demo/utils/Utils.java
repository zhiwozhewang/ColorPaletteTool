package com.whatscolors.demo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.base.BaseApplication;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Utils {

    private static long lastClickTime;
    private static InputMethodManager imm;
    private static Resources res;
    private static SharedPreferences preferences;


    public static String getHtmlData(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!sumHTML.equals("")) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/style.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + bodyHTML + "</body></html>";// summary + 去掉导言
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/style.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";// summary + 去掉导言
    }

    public static String getHtmlDataBig(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!TextUtils.isEmpty(sumHTML)) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylebig.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + bodyHTML + "</body></html>";//+ summary 去掉导言
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylebig.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";// + summary 去掉导言
    }

    public static String getHtmlDataSmall(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!TextUtils.isEmpty(sumHTML)) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylesmall.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + bodyHTML + "</body></html>"; //+ summary 去掉导言
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylesmall.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>"; //+ summary 去掉导言
    }

    public static String getHtmlDataSmallNew(String... datas) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><link rel='stylesheet' href='file:///android_asset/newstyles_2.css' type='text/css'/></head>" + "<body>" + datas[0] + "</body></html>"; //+ summary 去掉导言
    }

    // 截取图片路径集合中-m.jpg结尾的URL;
    public static String getImageListUrlString(String urlList) {
        int urlLength = urlList.substring(0, urlList.lastIndexOf("-z.jpg") + 6)
                .length();
        return urlList.substring(urlList.lastIndexOf("-z.jpg") + 7,
                urlList.lastIndexOf("-z.jpg") + 7 + urlLength);
    }

    // TODO 去掉简介信息中的<p>、</p>、<br>、&nbsp;标签;
    public static String replaceHtmlTag(String content) {
        String replace = content.replace("<p>", "");
        String replaced1 = replace.replace("</p>", "");
        String replaced2 = replaced1.replace("<P>", "");
        String replaced3 = replaced2.replace("</P>", "");
        String replaced4 = replaced3.replace("&nbsp;", "");
        String replaced5 = replaced4.replace("&lt;P&gt;&lt;FONT face=\"Times New Roman\"&gt;", "");
        String replaced6 = replaced5.replace("&ldquo;", "\"").replace("&rdquo;", "\"").replace("&#39;", "'").replace("&acute;", "'").replace("&mdash;", "—").replace("&ndash;", "-")
                .replace("&bdquo;", "\"").replace("&lsquo;", "'").replace("&rsquo;", "'").replace("&sbquo;", "'").replace("&circ;", "^").replace("&amp;", "&").replace("&quot;", "\"\"\"")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&hellip;", "……").replace("", "");
        return replaced6.replace("<br>", "");
    }


    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: checkRequestCode
     * @Description: 判断Code是否为1(这里用一句话描述这个方法的作用)
     */
    public static String checkRequestCode(JSONObject json) {
        return json.optString("Code");
    }

    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: jsonMessageParser
     * @Description: message信息(这里用一句话描述这个方法的作用)
     */
    public static String jsonMessageParser(JSONObject json) {
        if (json.optString("Message").equals("") || json.optString("Message").equals("null")) {
            return "";
        } else {
            return json.optString("Message");
        }
    }

    /**
     * encode by Base64
     */
    public static String encodeBase64(byte[] input) throws Exception {

        Class clazz = Class
                .forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, new Object[]{input});
        return (String) retObj;
    }

    /**
     * decode by Base64
     */
    public static byte[] decodeBase64(String input) throws Exception {
        Class clazz = Class
                .forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, input);
        return (byte[]) retObj;
    }

    public static String string2U8(String str) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static String MagazineCycleParser(String str) {
        if (str != null && !str.equals("null")) {
            if (str.equals("1")) {
                return "周刊";
            } else if (str.equals("2")) {
                return "半月刊";
            } else if (str.equals("3")) {
                return "月刊";
            } else if (str.equals("4")) {
                return "双月刊";
            } else if (str.equals("5")) {
                return "季刊";
            } else if (str.equals("6")) {
                return "旬刊";
            } else if (str.equals("7")) {
                return "双周刊";
            } else if (str.equals("8")) {
                return "半年刊";
            } else if (str.equals("9")) {
                return "年刊";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            lastClickTime = 0;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 控制字体颜色改变的方法 一个控件上显示不同的颜色
     *
     * @param num   = 123qwerewr
     * @param over  = 3， num.length-over 止
     * @param start 从第几位开始
     * @see
     */
    public static void controlColorCenter(TextView textView, String num,
                                          int start, int colorID, int over) {

        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), start, num.length()
                - over, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3 num.length-over
     * @see
     * @see
     */
    public static void controlColor(TextView textView, String num, int over,
                                    int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), 0, num.length() - over,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3
     * @see
     * @see
     */
    public static void controlColor2(TextView textView, String num, int over,
                                     int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), 0, over,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3
     * @see
     * @see
     */
    public static void controlColor3(TextView textView, String num, int over,
                                     int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), over, num.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * 对于提示语的封装，主要是针对“暂无咨询记录\n\n登录可同步您未登录状态下的问题，再次查看需要登录”
     *
     * @param tv
     *            展示文字的textview，一般为tvHint
     * @param text
     *            展示的内容，必须包含"\n"
     */
    // public static void tvHintsetText(Context context, TextView tv, String
    // text) {
    // int idx = text.lastIndexOf("\n");
    // if (res == null)
    // res = context.getResources();
    // Utils.controlColor3(tv, text, idx + 1,
    // res.getColor(R.color.text_color_888888));
    // }

    /**
     * 全角与半角的转化 将textview中的字符全角化
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号。利用正则表达式将所有特殊字符过滤，或利用replaceAll（）将中文标号替换为英文标号。则转化之后，
     * 则可解决排版混乱问题。
     *
     * @param str
     * @return
     * @throws PatternSyntaxException 替换、过滤特殊字符
     */

    public static String StringFilter(String str) throws PatternSyntaxException {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断字符串中是不是包含空格
     *
     * @param str
     * @return true:存在，false：不存在
     */
    public static boolean isExistSpaceInString(String str) {
        char[] psw = str.toCharArray();
        for (int i = 0; i < psw.length; i++) {
            if (psw[i] == ' ')
                return true;
        }
        return false;
    }

    /**
     * 键盘隐藏
     *
     * @param
     * @param e
     */
    public static void demissKeyBoard(Context c, EditText e) {
        if (imm == null)
            imm = (InputMethodManager) c
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
        e.clearFocus();
    }

    /**
     * 键盘显示
     *
     * @param
     * @param e
     */
    public static void showKeyBoard(Context c, EditText e) {
        if (imm == null)
            imm = (InputMethodManager) c
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 如果开启
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void copyTOClipboard(final Context c, final String param) {
        new AlertDialog.Builder(c)
                .setTitle("操作文本")
                .setItems(new String[]{"复制"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ClipboardManager clipboard = (ClipboardManager) c
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(param);
                            }
                        }
                ).show();

    }

    public static void setListViewAttr(ListView lv) {
        Class<? extends ListView> cls_lv = lv.getClass();
        try {
            Method setOverscrollFooter = cls_lv.getMethod(
                    "setOverscrollFooter", Drawable.class);
            try {
                Drawable drawable = null;
                setOverscrollFooter.invoke(lv, drawable);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getSharedPre(Context context) {

        if (preferences == null) {
            preferences = context.getSharedPreferences("count",
                    Context.MODE_WORLD_READABLE);
        }
        return preferences;
    }

    /**
     * 判断是否安装了某应用
     *
     * @param c
     * @return
     */
    public static int hasInstalledApp(Context c, String packageName) {
        List<PackageInfo> packs = c.getPackageManager().getInstalledPackages(0);//PackageManager.PERMISSION_GRANTED);
        // Sysout.i(packs.size());
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p == null) {
                continue;
            }
            if (packageName.equals(p.packageName))
                return 1;
        }
        return 0;
    }

    /**
     * 判断图片格式
     *
     * @param path
     * @return
     */
    public static boolean isImageFormat(String path) {

        if (path.endsWith("png") || path.endsWith("jpg")
                || path.endsWith("bmp") || path.endsWith("jpeg")) {

            return true;
        }
        return false;
    }

    public static int string2int(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long string2long(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static float string2float(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0F;
        }
    }

    public static double string2double(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0D;
        }
    }

    public static void hiddenNeterroe(TextView tvHint, TextView tvNetError,
                                      ViewGroup contentLayout) {
        tvHint.setVisibility(View.GONE);
        tvNetError.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isPassword(String mobiles) {

        Pattern p;
        p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isName(String mobiles) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5]{6,30}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isPhone(String phone) {
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 旧的手机判断
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmail(String mobiles) {
        String s = "^\\b[a-z_0-9.-]{1,64}@([a-z0-9-]{1,200}.){1,5}[a-z]{1,6}$";//"^[a-zA-Z0-9_.]+@[a-zA-Z0-9-]+[.]+[a-zA-Z]+";
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    /**
     * 判断应用市场是否存在的方法
     *
     * @param context
     * @param packageName 主流应用商店对应的包名
     *                    com.android.vending	-----Google Play
     *                    com.tencent.android.qqdownloader 	-----应用宝
     *                    com.qihoo.appstore	-----360手机助手
     *                    com.baidu.appsearch	-----百度手机助
     *                    com.xiaomi.market	-----小米应用商店
     *                    com.wandoujia.phoenix2	-----豌豆荚
     *                    com.huawei.appmarket	-----华为应用市场
     *                    com.taobao.appcenter	-----淘宝手机助手
     *                    com.hiapk.marketpho	-----安卓市场
     *                    cn.goapk.market	    -----安智市场
     */
    private static boolean isVisiabble(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> pName = new ArrayList<String>();
        // 从pinfo中将包名字取出
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pf = pinfo.get(i).packageName;
                pName.add(pf);
            }
        }
        // 判断pName中是否有目标程序的包名，有true，没有false
        return pName.contains(packageName);
    }

    private static boolean isVisiabbleLocal(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            ActivityInfo activityInfo = info.activityInfo;
            String packageNameLocal = activityInfo.packageName;            //获取应用市场的包名
            if (packageName.equalsIgnoreCase(packageNameLocal)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    private static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean toMarket(Context context) {
        for (String s : MyConfig.str_market) {
            if (isVisiabble(context, s)) {
                launchAppDetail(context, context.getPackageName(), s);
                return true;
            }
        }
        return false;
    }

    public static String getFbStr() {

        StringBuilder stringBuilder = new StringBuilder("https://whatscolors.hkrstore.com/app/chatra.io.html?");
        stringBuilder.append("email=" + getEncodeStr(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "email")));
        stringBuilder.append("&first_name=" + getEncodeStr(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "first_name")));
        stringBuilder.append("&last_name=" + getEncodeStr(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "last_name")));
        stringBuilder.append("&service_status=" + SPUtils.getSharedIntData(BaseApplication.getAppContext(), "status"));
        stringBuilder.append("&client_id=Android");

        LogUtils.loge("反馈UIL:" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private static String getEncodeStr(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFilePath() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
//            return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/com.whatscolors.demo.takephoto/files/Logs/log_2016-03-14_16-15-09.log
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return BaseApplication.getAppContext().getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
        }
    }
}
