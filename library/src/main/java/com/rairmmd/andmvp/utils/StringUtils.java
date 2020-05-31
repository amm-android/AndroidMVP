package com.rairmmd.andmvp.utils;

import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Rair
 * @date 2018/12/15
 * <p>
 * desc:
 */
public class StringUtils {

    /**
     * json转对象
     *
     * @param json   json
     * @param tClass 对象class
     * @return T
     */
    public static <T> T parseJson(String json, Class<T> tClass) {
        return new Gson().fromJson(json, tClass);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


    /**
     * 取出手机号里面的其他字符
     *
     * @param text 手机号
     * @return String
     */
    public static String getPhoneNumber(String text) {
        return text.replaceAll(" ", "").replaceAll("-", "").replace("+86", "");
    }

    /**
     * 获取EditText文本
     *
     * @param editText EditText
     * @return String
     */
    public static String getText(EditText editText) {
        return editText.getText().toString().trim().trim();
    }

    /**
     * 获取TextView文本
     *
     * @param textView TextView
     * @return String
     */
    public static String getText(TextView textView) {
        return textView.getText().toString().trim().trim();
    }

    /**
     * 获取EditText手机号
     *
     * @param editText EditText
     * @return String
     */
    public static String getPhoneNumber(EditText editText) {
        String phoneNumber = editText.getText().toString().trim();
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "").replace("+86", "");
        return phoneNumber;
    }

    /**
     * 去除转义字符
     *
     * @param str 字符串
     * @return String
     */
    public static String unescapeJava(String str) {
        Writer out = new StringWriter();
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        try {
            for (int i = 0; i < sz; ++i) {
                char ch = str.charAt(i);
                if (inUnicode) {
                    unicode.append(ch);
                    if (unicode.length() == 4) {
                        try {
                            int nfe = Integer.parseInt(unicode.toString(), 16);
                            out.write((char) nfe);
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                        } catch (NumberFormatException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (hadSlash) {
                    hadSlash = false;
                    switch (ch) {
                        case '\"':
                            out.write(34);
                            break;
                        case '\'':
                            out.write(39);
                            break;
                        case '\\':
                            out.write(92);
                            break;
                        case 'b':
                            out.write(8);
                            break;
                        case 'f':
                            out.write(12);
                            break;
                        case 'n':
                            out.write(10);
                            break;
                        case 'r':
                            out.write(13);
                            break;
                        case 't':
                            out.write(9);
                            break;
                        case 'u':
                            inUnicode = true;
                            break;
                        default:
                            out.write(ch);
                    }
                } else if (ch == 92) {
                    hadSlash = true;
                } else {
                    out.write(ch);
                }
            }
            if (hadSlash) {
                out.write(92);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    /**
     * 判断字符串中是否含有表情
     */
    public static boolean containsEmoji(String text) {
        String source = text.substring(0, 1);
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }

    /**
     * 判断某个字符是不是表情
     *
     * @param text char
     */
    public static boolean isEmojiChar(String text) {
        char codePoint = text.charAt(0);
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)));
    }
}
