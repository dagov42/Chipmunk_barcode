package ru.chipmunkbarcode.util.gs1;

public class Internals {

    static boolean startsWithNZeroes(String s, int n) {
        if (s == null || n < 0 || s.length() < n) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    static int countLeadingZeroes(String s) {
        if (s == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < s.length() && s.charAt(i) == '0'; i++) {
            n++;
        }
        return n;
    }

    static String leftPadWithZeroes(String s, int n) {
        if (s == null || n < 0 || s.length() >= n) {
            return s;
        }
        char[] chars = new char[n];
        for (int i = 0; i < n - s.length(); i++) {
            chars[i] = '0';
        }
        s.getChars(0, s.length(), chars, n - s.length());
        return new String(chars);
    }

    static boolean isDigits(String s) {
        if (s == null || s.length() < 1) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    static String validateFormat(String type, int length, String s) {
        if (s == null) {
            throw new NullPointerException(type + " must not be null");
        }
        if (!isDigits(s)) {
            throw new IllegalArgumentException("Invalid " + type + " " + s + ", must be digits");
        }
        if (s.length() != length) {
            throw new IllegalArgumentException("Invalid " + type + " " + s + ", must be " + length + " digits long");
        }
        return s;
    }
}
