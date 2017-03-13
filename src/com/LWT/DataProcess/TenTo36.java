package com.LWT.DataProcess;

import java.math.BigInteger;

/**
 * Created by Badger on 15/7/23.
 */
public class TenTo36 {

    public static String baseString(int num) {
        return baseString(num, 36);
    }

    public static String baseString(BigInteger num) {
        return baseString(num, 36);
    }

    /**
     * baseString 递归调用
     *
     * @param num  十进制数
     * @param base 要转换成的进制数
     */
    public static String baseString(int num, int base) {
        String str = "", digit = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (num == 0) {
            return "";
        } else {
            str = baseString(num / base, base);
            return str + digit.charAt(num % base);
        }
    }


    public static String baseString(BigInteger num, int base) {
        String str = "", digit = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (num.shortValue() == 0) {
            return "";
        } else {
            BigInteger valueOf = BigInteger.valueOf(base);
            str = baseString(num.divide(valueOf), base);
            return str + digit.charAt(num.mod(valueOf).shortValue());
        }
    }
}