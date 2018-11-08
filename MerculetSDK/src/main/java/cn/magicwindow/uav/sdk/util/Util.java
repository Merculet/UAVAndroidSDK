package cn.magicwindow.uav.sdk.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.magicwindow.uav.sdk.MConfiguration;
import cn.magicwindow.uav.sdk.config.Constant;
import cn.magicwindow.uav.sdk.log.DebugLog;

public class Util {

    private static ProgressDialog mProgressDialog;
    private static String MWAppId;
    private static String ACCOUNT_KEY;
    private static String SECRET_CODE;
    private static int SEND_EVENT_BATCH;
    private static String MWChannel;

    private static JSONObject jsonObject;

    public static long getCurrentTimeSecond() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getCurrentTimeSecondStr() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String getMChannel() {

        if (Preconditions.isBlank(MWChannel)) {
            MWChannel = getMetaDataFromApplication(Constant.M_CHANNEL);
        }

        return MWChannel;
    }

    /**
     * 读取application 节点  meta-data 信息
     */
    public static String getMetaDataFromApplication(String tag) {
        String metaData = "";
        if (Preconditions.isBlank(tag)) {
            return "";
        }

        try {
            ApplicationInfo appInfo = MConfiguration.get().getContext().getPackageManager()
                    .getApplicationInfo(MConfiguration.get().getContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                metaData = appInfo.metaData.getString(tag);
                if (Preconditions.isNotBlank(metaData)) {
                    metaData = metaData.trim();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            DebugLog.debug("please make sure the " + tag + " in AndroidManifest.xml is right! " + tag + " = " + metaData);
        }

        return metaData;
    }

    /**
     * 显示ProgressDialog
     */
    public static void showProgressDialog(final Activity act, String message, final boolean isFinishActivity) {
        dismissDialog();
        mProgressDialog = new ProgressDialog(act);
        // 设置进度条风格，风格为圆形，旋转的
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        mProgressDialog.setMessage(message);
        // 设置ProgressDialog 的进度条是否不明确
        mProgressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (isFinishActivity) {
                    act.finish();
                }
            }
        });
        mProgressDialog.show();
    }

    /**
     * dismiss ProgressDialog
     */
    public static void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 检查权限是否开启
     *
     * @param permission
     * @return true or false
     */
    public static boolean checkPermission(Context context, String permission) {

        if (context == null) {
            context = MConfiguration.get().getContext();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PackageManager localPackageManager = context.getApplicationContext().getPackageManager();
            return localPackageManager.checkPermission(permission, context.getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } else {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * dp to px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px to dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isTimeStamp(String str) {
        if (Preconditions.isBlank(str)) {
            return false;
        }

        Pattern validate = Pattern.compile("([0-9]{10}|[0-9]{13})", Pattern.CASE_INSENSITIVE);
        Matcher m = validate.matcher(str);

        return m.matches();
    }

    public static String getTextWithLanguage(String zh, String en) {
        if ("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            return zh;
        } else {
            return en;
        }
    }

    public static int getSendBatch() {
        if (SEND_EVENT_BATCH == 0) {
            SEND_EVENT_BATCH = SPHelper.create().getSendBatch();
        }
        return SEND_EVENT_BATCH;
    }

    private static String filterColor(String colorString) {
        Pattern p = Pattern.compile("([0-9A-Fa-f]{8})|([0-9A-Fa-f]{6})");
        Matcher m = p.matcher(colorString);

        while ((m.find())) {
            return m.group();
        }
        return "";
    }

    public static int parseColor(String colorString) {
        String filterColor = filterColor(colorString);
        String parsedColorString;
        if (!"#".equalsIgnoreCase(String.valueOf(filterColor.charAt(0)))) {
            parsedColorString = "#" + filterColor;
        } else {
            parsedColorString = filterColor;
        }
        return Color.parseColor(parsedColorString);
    }

    public static boolean isColor(String colorString) {

        if (Preconditions.isBlank(colorString)) {
            return false;
        }
        Pattern p = Pattern.compile("([0-9A-Fa-f]{8})|([0-9A-Fa-f]{6})");
        Matcher m = p.matcher(colorString);

        while ((m.find())) {
            return true;
        }
        return false;
    }

    /**
     * 防止内部Handler类引起内存泄露
     */
    public static class MyHandler extends Handler {
        private final WeakReference<Context> mContext;

        public MyHandler(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mContext.get() == null) {
                return;
            }
        }
    }


    public static boolean isDestroy(Object parent) {
        Class clz = parent.getClass();

        try {
            Method fragmentGetActivity = clz.getMethod("getActivity");
            if (null == fragmentGetActivity.invoke(parent)) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (IllegalAccessException e) {

        }

        //isDestroyed()只有在JELLY_BEAN_MR1之上才有
        try {
            Method activityIsDestroy = clz.getMethod("isDestroyed");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && (boolean) activityIsDestroy.invoke(parent)) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (IllegalAccessException e) {

        }

        try {
            Field ctx = clz.getDeclaredField("mContext");
            ctx.setAccessible(true);

            if (null == ctx) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {

        }
        try {
            Field ctx = clz.getDeclaredField("context");
            ctx.setAccessible(true);
            if (null == ctx) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {

        }

        try {
            Field ctx = clz.getDeclaredField("ctx");
            ctx.setAccessible(true);
            if (null == ctx) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {

        }
        try {
            Field ctx = clz.getDeclaredField("mActivity");
            ctx.setAccessible(true);
            if (null == ctx) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {

        }
        try {
            Field ctx = clz.getDeclaredField("activity");
            ctx.setAccessible(true);
            if (null == ctx) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {

        }

        return false;

    }

}
