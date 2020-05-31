package com.rairmmd.andmvp.utils;

import android.content.ClipData;
import android.content.ClipboardManager;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @author Rair
 * @date 2019/3/25
 * <p>
 * desc:
 */
public class ClipboardUtils {

    public static void clipText(String text) {
        ClipboardManager clipboard = (ClipboardManager) AppUtils.getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clipData);
        }
    }
}
