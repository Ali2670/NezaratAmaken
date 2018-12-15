package com.ibm.hamsafar.activity;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ValidateCodeMeli {
    public static int COD_SIZE = 10;
    public static String CODE_MELI_SHENASNAME_EQUAL_DATE = "1368/01/01";
    public static boolean checkCocdeMeli(String code) {
      if(code.length() != 10)
        return false ;
      if ( validateNumber(code) == false)
        return false;
        int[] X = tokenizeCodeToArray(code);
        int N = calculateN(X);
//        System.out.println("Sum Of Total Digit=" + N);
        int R =  (N % 11);
//        System.out.println("Remind Of Divide Sum On 11 = " + R);
        int resultC;
//        System.out.println("Check Digit Number In Current Cod Is: " + X[COD_SIZE - 1]);
        switch (R) {
        case 0:
            resultC = 0;
//            System.out.println("Correct Check Digit Is: 0" );
            break;
        case 1:
            resultC = 1;
//            System.out.println("Correct Check Digit Is: 1" );
            break;
        default:
          resultC = 11 - R;
//          System.out.println("Correct Check Digit Is: " + resultC );
            break;
        }
        if (resultC == X[COD_SIZE - 1])
        {
//          System.out.println("CodMeli IS Correct" );
          return true;
        }
        else
        {
//          System.out.println("CodMeli IS Not Correct" );
          return false;
        }



    }

    private static int[] tokenizeCodeToArray(String code) {
        char[] charList = code.toCharArray();
        int[] result = new int[10];
        for (int i = 0; i < charList.length; i++) {
            result[i] = Integer.parseInt(String.valueOf(charList[i]));
        }
        return result;
    }

    private static int calculateN(int[] l) {
        int n = 0;
        for (int ii = 0; ii < COD_SIZE - 1; ii++)
        {
//          System.out.println("The Value is " + l[ii] + " AND Arzesh Is: " + (COD_SIZE - ii));
          n += l[ii] * (COD_SIZE - ii);
        }
        return n;
    }
    private static boolean validateNumber(String code)
    {
      char[] charList = code.toCharArray();
      for (int jj = 0; jj < charList.length; jj++)
      {

        switch (charList[jj])
      {
        case '0':

          break;
        case '1':

          break;
        case '2':

          break;
        case '3':

          break;
        case '4':

          break;
        case '5':

          break;
        case '6':

          break;
        case '7':

          break;
        case '8':

          break;
        case '9':

          break;
        default:
//          System.out.println("not vali code" + charList[jj]);
          return false;
      }
      }
      return true;
    }

    public static String completeCodMeliWithDash(String code){
        if (code.length() != 10) return code;
        return code.substring(0,3).concat("-").concat(code.substring(3,9)).concat("-").concat(code.substring(9));
    }

    public static String completeCodMeliWithoutDash(String code){
        return  code.replaceAll("-","");
//        if (code.length() != 10) return code;
//        return code.substring(0,3).concat("-").concat(code.substring(3,9)).concat("-").concat(code.substring(9));
    }

    public static String completeCodMeliTo10(String code){
        for (int i = 0; i < 10 - code.length(); i++) {
            code = "0".concat(code);
        }
         return code;
    }

}