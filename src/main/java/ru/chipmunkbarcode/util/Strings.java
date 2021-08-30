package ru.chipmunkbarcode.util;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Strings {

    /**
     * Replaces raw values with special placeholders, where applicable.
     *
     * @param s the string to add placeholders to
     * @return the specified string, with placeholders added
     * @see <a href="http://www.zint.org.uk/Manual.aspx?type=p&page=4">Zint placeholders</a>
     * @see #unescape(String, boolean)
     */
    public static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 10);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\u0000':
                    sb.append("\\0"); // null
                    break;
                case '\u0004':
                    sb.append("\\E"); // end of transmission
                    break;
                case '\u0007':
                    sb.append("\\a"); // bell
                    break;
                case '\u0008':
                    sb.append("\\b"); // backspace
                    break;
                case '\u0009':
                    sb.append("\\t"); // horizontal tab
                    break;
                case '\n':
                    sb.append("\\n"); // line feed
                    break;
                case '\u000b':
                    sb.append("\\v"); // vertical tab
                    break;
                case '\u000c':
                    sb.append("\\f"); // form feed
                    break;
                case '\r':
                    sb.append("\\r"); // carriage return
                    break;
                case '\u001b':
                    sb.append("\\e"); // escape
                    break;
                case '\u001d':
                    sb.append("\\G"); // group separator
                    break;
                case '\u001e':
                    sb.append("\\R"); // record separator
                    break;
                case '\\':
                    sb.append("\\\\"); // escape the escape character
                    break;
                default:
                    if (c >= 32 && c <= 126) {
                        sb.append(c); // printable ASCII
                    } else {
                        byte[] bytes = String.valueOf(c).getBytes(ISO_8859_1);
                        String hex = String.format("%02X", bytes[0] & 0xFF);
                        sb.append("\\x").append(hex);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Replaces any special placeholders with their raw values (not including FNC values).
     *
     * @param s       the string to check for placeholders
     * @param lenient whether or not to be lenient with unrecognized escape sequences
     * @return the specified string, with placeholders replaced
     * @see <a href="http://www.zint.org.uk/Manual.aspx?type=p&page=4">Zint placeholders</a>
     * @see #escape(String)
     */
    public static String unescape(String s, boolean lenient) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\') {
                sb.append(c);
            } else {
                if (i + 1 >= s.length()) {
                    String msg = "Error processing escape sequences: expected escape character, found end of string";
                    throw new BarcodeException(msg);
                } else {
                    char c2 = s.charAt(i + 1);
                    switch (c2) {
                        case '0':
                            sb.append('\u0000'); // null
                            i++;
                            break;
                        case 'E':
                            sb.append('\u0004'); // end of transmission
                            i++;
                            break;
                        case 'a':
                            sb.append('\u0007'); // bell
                            i++;
                            break;
                        case 'b':
                            sb.append('\u0008'); // backspace
                            i++;
                            break;
                        case 't':
                            sb.append('\u0009'); // horizontal tab
                            i++;
                            break;
                        case 'n':
                            sb.append('\n'); // line feed
                            i++;
                            break;
                        case 'v':
                            sb.append('\u000b'); // vertical tab
                            i++;
                            break;
                        case 'f':
                            sb.append('\u000c'); // form feed
                            i++;
                            break;
                        case 'r':
                            sb.append('\r'); // carriage return
                            i++;
                            break;
                        case 'e':
                            sb.append('\u001b'); // escape
                            i++;
                            break;
                        case 'G':
                            sb.append('\u001d'); // group separator
                            i++;
                            break;
                        case 'R':
                            sb.append('\u001e'); // record separator
                            i++;
                            break;
                        case '\\':
                            sb.append('\\'); // escape the escape character
                            i++;
                            break;
                        case 'x':
                            if (i + 3 >= s.length()) {
                                String msg = "Error processing escape sequences: expected hex sequence, found end of string";
                                throw new BarcodeException(msg);
                            } else {
                                char c3 = s.charAt(i + 2);
                                char c4 = s.charAt(i + 3);
                                if (isHex(c3) && isHex(c4)) {
                                    byte b = (byte) Integer.parseInt("" + c3 + c4, 16);
                                    sb.append(new String(new byte[]{b}, StandardCharsets.ISO_8859_1));
                                    i += 3;
                                } else {
                                    String msg = "Error processing escape sequences: expected hex sequence, found '" + c3 + c4 + "'";
                                    throw new BarcodeException(msg);
                                }
                            }
                            break;
                        default:
                            if (lenient) {
                                sb.append(c);
                            } else {
                                throw new BarcodeException("Error processing escape sequences: expected valid escape character, found '" + c2 + "'");
                            }
                    }
                }
            }
        }
        return sb.toString();
    }

    private static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    /**
     * Appends the specific integer to the specified string, in binary format, padded to the specified number of digits.
     *
     * @param s      the string to append to
     * @param value  the value to append, in binary format
     * @param digits the number of digits to pad to
     */
    public static void binaryAppend(StringBuilder s, int value, int digits) {
        int start = 0x01 << (digits - 1);
        for (int i = 0; i < digits; i++) {
            if ((value & (start >> i)) == 0) {
                s.append('0');
            } else {
                s.append('1');
            }
        }
    }
}
