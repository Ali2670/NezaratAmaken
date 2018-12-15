package com.ibm.hamsafar.utils;

public class SearchUtil {
    private static char[] farsiChars = {'ض', 'ص', 'ث', 'ق', 'ف', 'غ', 'ع', 'ه', 'خ', 'ح', 'ج', 'چ', 'پ', 'ش', 'س', 'ي', 'ب', 'ل', 'ا', 'ت', 'ن', 'م', 'ک', 'گ', 'ظ', 'ط', 'ز', 'ر', 'ذ', 'د', 'ئ', 'و', 'آ', 'ژ', 'ك', 'ى'};
    private static String farsiAllChars = "ضصثقفغعهخحجچپشسيبلاتنمکگظطزرذدئوژآىك";
    private static String farsiAllCharsNums = "ضصثقفغعهخحجچپشسيبلاتنمکگظطزرذدئوژآك0123456789-_ى/";
    private static String farsiAllNums = "0123456789";
    private static String KharejiAllChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String fixFarsiString(String inStr) {

        StringBuilder returnString = new StringBuilder();

        if (inStr == null) return "";

        for (int j = 0; j < inStr.length(); j++)
            returnString.append(checkChar(inStr.charAt(j)));
        inStr = returnString.toString();
        returnString = new StringBuilder();

        char chTmp = ' ';
        char ch = ' ';
        if (inStr == null) return "";
        else {

            int vzt = 0;

            while (vzt == 0) {
                vzt = 1;
                for (int i = 0; i < inStr.length(); i++) {
                    ch = inStr.charAt(i);
                    if (i == inStr.length() - 1)
                        chTmp = ' ';
                    else
                        chTmp = inStr.charAt(i + 1);
                    returnString.append(ch);
                    if (ch == chTmp) {
                        i = i + 1;
                        vzt = 0;
                    }
                }

                inStr = returnString.toString();
                returnString = new StringBuilder();
            }
            return inStr;
        }
    }
  /*
   public static String fixFarsiString(String inStr)  {

    String returnString = "";
    if (inStr == null) return "";
    else
   {
     for (int j=0 ; j < inStr.length();j++)
      returnString+= checkChar(inStr.charAt(j));
     return returnString;
   }
 }
  */

    public static String fixShoName(String inStr) {
        String returnString = "";
        if (inStr == null) return "";
        else {
            for (int i = 0; i < inStr.length(); i++)
                returnString += checkSho(inStr.charAt(i));
            return returnString;
        }
    }

    public static String fixShomare(String inStr) {
        String returnString = "";
        if (inStr == null) return "0";
        else {
            inStr = inStr.trim();
            for (int i = 0; i < inStr.length(); i++)
                returnString += checkShomare(inStr.charAt(i));
            if (returnString.length() == 0) return "0";
            return returnString;
        }
    }

//

    public static String checkChar(char chr) {
//        if (farsiAllChars.indexOf(chr) == -1) return "";
//        else {
        if (chr == 'آ') return "ا";
        if (chr == 'إ') return "ا";
        if (chr == 'أ') return "ا";
        if (chr == 'ء') return "ي";
        if (chr == 'ئ') return "ي";
        if (chr == 'ي') return "ي";
        if (chr == 'ى') return "ي";

        if (chr == 'ة') return "ه";
        if (chr == 'ؤ') return "و";
        if (chr == 'ک') return "ك";
//        }
        return String.valueOf(chr);
    }

    private static String checkSho(char chr) {
        if (farsiAllCharsNums.indexOf(chr) == -1) return "";
        else {
            if (chr == 'آ') return "ا";
            if (chr == 'إ') return "ا";
            if (chr == 'أ') return "ا";
            if (chr == 'ء') return "ي";
            if (chr == 'ئ') return "ي";
            if (chr == 'ي') return "ي";
            if (chr == 'ى') return "ي";
            if (chr == 'ة') return "ه";
            if (chr == 'ؤ') return "و";
            if (chr == 'ک') return "ك";

            if (chr == '/') return "-";
            if (chr == '_') return "-";
            if (chr == '-') return "-";
        }
        return String.valueOf(chr);
    }

    private static String checkShomare(char chr) {
        if (farsiAllNums.indexOf(chr) == -1)
            return "";
        return String.valueOf(chr);
    }

    public static String fixKharejiString(String inStr) {

        String returnString = "";
        if (inStr == null) return "";
        for (int j = 0; j < inStr.length(); j++)
            returnString += checkCharKhareji(inStr.charAt(j));
        inStr = returnString;
        return inStr;
    }

    private static String checkCharKhareji(char chr) {
        if (KharejiAllChars.indexOf(chr) == -1) return "";
        return String.valueOf(chr);
    }
}