package com.example.qrhunter;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/**
 * @description decode and get score
 *
 *
 */

public class HashScore {

    public String hash256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2hex(messageDigest.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }
    /**
     * @description Handling byte bits
     *
     *
     */

    private String byte2hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    /**
     * @description
     *
     *
     */

    public ArrayList<String> counter(String hashcode) {
        ArrayList<String> result = new ArrayList<String>();
        char c = hashcode.charAt(0);
        String s = String.valueOf(hashcode.charAt(0));

        for (int i = 1; i< hashcode.length(); i++) {
            if (c == hashcode.charAt(i)) {
                s = s + String.valueOf(hashcode.charAt(i));
            } else {
                if (s.length()>=1) {
                    result.add(s);
                }
                c = hashcode.charAt(i);
                s = String.valueOf(hashcode.charAt(i));
            }
        }
        if (s.length()>1) {
            result.add(s);
        }

        return result;
    }

    /**
     * @description calcualte score
     *
     *
     */

    public int score(ArrayList<String> chain) {
        char c = 0;
        int sum = 0;

        for (String s:chain){
            c = s.charAt(0);
            if (s.length() > 1) {
                if (c == '0') {
                    sum += Math.pow(20, s.length() - 1);
                } else if ((c >= '1') && (c <= '9')) {
                    sum += Math.pow(c - '0', s.length() - 1);
                } else if ((c >= 'a') && (c <= 'f')) {
                    sum += Math.pow(c - 'a' + 10, s.length() - 1);
                }
            } else {
                if (c == '0') {
                    sum += 1;
                }/* else if ((c >= '1') && (c <= '9')) {
                    sum += c-'0';
                } else if ((c >= 'a') && (c <= 'f')) {
                    sum += c-'a'+10;
                }*/
            }
//            Log.d("TAG", "score: " + String.valueOf(c) + "---" + s + "::::" + sum);
        }

        return sum;
    }
}