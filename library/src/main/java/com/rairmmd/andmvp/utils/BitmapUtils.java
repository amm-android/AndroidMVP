package com.rairmmd.andmvp.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Rair
 * @date 2017/11/30
 * <p>
 * desc:
 */

public class BitmapUtils {

    /**
     * Drawable转Bitmap
     *
     * @param drawable drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean saveBitmap2SD(File file, Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            boolean compress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return compress;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 绘制图片
     *
     * @param file 生成图片的地址
     * @param text 文字
     */
    public static boolean drawImage(File file, String text) {
        try {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            int width = rect.width() + 40;
            int height = rect.height() + 60;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawText(text, 20, 70, paint);
            return saveBitmap2SD(file, bitmap);
        } catch (Exception e) {
            return false;
        }
    }
}
