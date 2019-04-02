package com.mikejones.maestro;

import android.text.TextUtils;
import android.util.Patterns;

public class Validator {

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidUsername(String username)
    {

        return username.length() >= 6;
    }
    public static boolean isValidPassword(String password)
    {
        return password.length() >= 6;
    }

    public static boolean isValidUserType(String usertype){
        return usertype.equals("Student") || usertype.equals("Educator");
    }

}
