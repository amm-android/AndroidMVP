package com.rairmmd.andmvp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author Rair
 * @date 2018/12/30
 * <p>
 * desc:
 */
public class RairUtils {

    /**
     * 隐藏输入法
     *
     * @param activity 上下文
     */
    public static void hideInput(Activity activity) {
        InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getWindow().peekDecorView();
        if (inputmanger != null && view != null) {
            if (inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null) {
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏输入法
     *
     * @param view view
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示输入法
     *
     * @param view view
     */
    public static void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return 屏幕宽高[宽, 高]
     */
    public static int[] screenWH(Context context) {
        int[] screenWH = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWH[0] = size.x;
        screenWH[1] = size.y;
        return screenWH;
    }

    /**
     * 抖动动画
     *
     * @param CycleTimes 动画重复的次数
     * @return 动画
     */
    public static Animation shakeAnimation(int CycleTimes) {
        Animation translateAnimation = new TranslateAnimation(0, 6, 0, 6);
        translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    /**
     * 随机生成字符串(nonce)
     *
     * @return 随机字符串
     */
    public static String randomStr(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {//32位
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机一个标签
     *
     * @param tag 标签
     * @return tag
     */
    public static String randomTag(String[] tag) {
        int random = new Random().nextInt(tag.length);
        return tag[random];
    }

    /**
     * 随机一个颜色
     */
    public static int randomColor(int[] colors) {
        int random = new Random().nextInt(colors.length);
        return colors[random];
    }

    /**
     * 随机一个颜色
     */
    public static int random80Color(int[] colors) {
        int random = new Random().nextInt(colors.length);
        return colors[random];
    }

    /**
     * 清除缓存
     *
     * @param context
     * @return
     */
    public static void clearCache(Context context) {
        File cacheDir = context.getCacheDir();
        for (File file : cacheDir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (File cacheFile : files) {
                    cacheFile.delete();
                }
            }
        }
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file);
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 大小
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("##.00");
        String fileSizeString;
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
