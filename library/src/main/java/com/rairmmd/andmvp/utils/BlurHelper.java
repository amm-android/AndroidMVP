package com.rairmmd.andmvp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;

public class BlurHelper {

    private static final String TAG = "BlurHelper";
    private static int statusBarHeight;
    private static long startTime;

    public static boolean renderScriptSupported() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static Bitmap blur(Context context, View view, float scaledRatio, float radius) {
        return blur(context, view, scaledRatio, radius, true);
    }

    public static Bitmap blur(Context context, View view, float scaledRatio, float radius, boolean fullScreen) {
        return blur(context, getViewBitmap(view, scaledRatio, fullScreen), view.getWidth(), view.getHeight(), radius);
    }

    public static Bitmap blur(Context context, Bitmap origin, int resultWidth, int resultHeight, float radius) {
        startTime = System.currentTimeMillis();
        if (renderScriptSupported()) {
            Log.i(TAG, "脚本模糊");
            return scriptBlur(context, origin, resultWidth, resultHeight, radius);
        } else {
            Log.i(TAG, "快速模糊");
            return fastBlur(context, origin, resultWidth, resultHeight, radius);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap scriptBlur(Context context, Bitmap origin, int outWidth, int outHeight, float radius) {
        if (origin == null || origin.isRecycled()) {
            return null;
        }
        RenderScript renderScript = RenderScript.create(context.getApplicationContext());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, origin);
        Allocation blurOutput = Allocation.createTyped(renderScript, blurInput.getType());

        ScriptIntrinsicBlur blur = null;
        try {
            blur = ScriptIntrinsicBlur.create(renderScript, blurInput.getElement());
        } catch (RSIllegalArgumentException e) {
            if (e.getMessage().contains("Unsuported element type")) {
                blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            }
        }

        if (blur == null) {
            Log.e(TAG, "脚本模糊失败，转fastBlur");
            return fastBlur(context, origin, outWidth, outHeight, radius);
        }

        blur.setRadius(range(radius, 0, 20));
        blur.setInput(blurInput);
        blur.forEach(blurOutput);
        blurOutput.copyTo(origin);

        //释放
        renderScript.destroy();
        blurInput.destroy();
        blurOutput.destroy();

        Bitmap result = Bitmap.createScaledBitmap(origin, outWidth, outHeight, true);
        origin.recycle();
        long time = (System.currentTimeMillis() - startTime);
        Log.i(TAG, "模糊用时：【" + time + "ms】");
        return result;
    }

    public static Bitmap fastBlur(Context context, Bitmap origin, int outWidth, int outHeight, float radius) {
        if (origin == null || origin.isRecycled()) {
            return null;
        }
        origin = doBlur(origin, (int) range(radius, 0, 20), false);
        if (origin == null || origin.isRecycled()) {
            return null;
        }
        origin = Bitmap.createScaledBitmap(origin, outWidth, outHeight, true);
        long time = (System.currentTimeMillis() - startTime);
        Log.i(TAG, "模糊用时：【" + time + "ms】");
        return origin;
    }

    public static Bitmap getViewBitmap(final View v, boolean fullScreen) {
        return getViewBitmap(v, 1.0f, fullScreen);
    }


    public static Bitmap getViewBitmap(final View v, float scaledRatio, boolean fullScreen) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            Log.e(TAG, "getViewBitmap  >>  宽或者高为空");
            return null;
        }
        if (statusBarHeight <= 0) {
            statusBarHeight = getStatusBarHeight(v.getContext());
        }
        Bitmap b;
        Log.i(TAG, "模糊原始图像分辨率 [" + v.getWidth() + " x " + v.getHeight() + "]");

        try {
            b = Bitmap.createBitmap((int) (v.getWidth() * scaledRatio), (int) (v.getHeight() * scaledRatio), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            System.gc();
            return null;
        }

        Canvas c = new Canvas(b);
        Matrix matrix = new Matrix();
        matrix.preScale(scaledRatio, scaledRatio);
        c.setMatrix(matrix);
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable == null) {
            c.drawColor(Color.parseColor("#FAFAFA"));
        } else {
            bgDrawable.draw(c);
        }
        if (fullScreen) {
            if (statusBarHeight > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && v.getContext() instanceof Activity) {
                int statusBarColor = ((Activity) v.getContext()).getWindow().getStatusBarColor();
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setColor(statusBarColor);
                Rect rect = new Rect(0, 0, v.getWidth(), statusBarHeight);
                c.drawRect(rect, p);
            }
        }
        v.draw(c);
        Log.i(TAG, "模糊缩放图像分辨率 [" + b.getWidth() + " x " + b.getHeight() + "]");
        return b;
    }


    public static float range(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }


    private static int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}