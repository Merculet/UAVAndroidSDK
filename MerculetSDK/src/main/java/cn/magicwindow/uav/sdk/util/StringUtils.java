package cn.magicwindow.uav.sdk.util;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony Shen on 15/9/30.
 */
public class StringUtils {

    /**
     * MD5 encrypt做过处理，取的是中间16位。
     */
    public static String md5(String str) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(str.getBytes(Charset.defaultCharset()));
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuilder stringBuffer = new StringBuilder();
            for (byte anArrayOfByte : arrayOfByte) {
                int j = 0xFF & anArrayOfByte;
                if (j < 16)
                    stringBuffer.append("0");
                stringBuffer.append(Integer.toHexString(j));
            }
            return stringBuffer.toString().toLowerCase().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * user java reg to check phone number and replace 86 or +86
     * only check start with "+86" or "86" ex +8615911119999 13100009999 replace +86 or 86 with ""
     *
     * @param phoneNum
     * @return
     */
    public static String formatPhoneNum(String phoneNum) {

        Pattern p1 = Pattern.compile("^((\\+?86)?\\s?\\-?)1[0-9]{10}");
        Matcher m1 = p1.matcher(phoneNum);
        if (m1.matches()) {
            Pattern p2 = Pattern.compile("^((\\+?86)?)\\s?\\-?");
            Matcher m2 = p2.matcher(phoneNum);
            StringBuffer sb = new StringBuffer();
            while (m2.find()) {
                m2.appendReplacement(sb, "");
            }
            m2.appendTail(sb);
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * @param a
     * @return
     */
    public static boolean equals(String a, String b) {
        return TextUtils.isEmpty(a) && TextUtils.isEmpty(b) || Preconditions.isNotBlank(a) && a.equals(b);
    }

    /**
     * user java reg to check phone number and replace 86 or +86
     * only check start with "+86" or "86" ex +8615911119999 13100009999 replace +86 or 86 with ""
     *
     * @param phoneNum 被检测值
     * @return
     */
    public static boolean checkPhoneNum(String phoneNum) {

        Pattern p1 = Pattern.compile("^((\\+?86)?\\s?\\-?)1[0-9]{10}");
        Matcher m1 = p1.matcher(phoneNum);
        return m1.matches();
    }

    /**
     * @param email 被检测值
     * @return
     */
    public static boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
