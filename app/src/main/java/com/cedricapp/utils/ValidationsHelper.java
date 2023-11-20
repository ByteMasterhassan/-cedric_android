package com.cedricapp.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationsHelper {


    // check email
    public boolean isValidEmail(String email) {
     //   String regexEmail = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}";
        String regexEmail ="^[A-Z0-9a-z._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,64}+$";
        Pattern emailPattern = Pattern.compile(regexEmail);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    // check username validations
    public boolean isValidName(String name) {
        String regex = "^[a-zA-Z\\s]+";
        Pattern namePattern = Pattern.compile(regex);
        Matcher matcher = namePattern.matcher(name);
        return matcher.matches();
    }

    // check null or empty String
    public boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    public boolean isValidPassword(String password){
        final  Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[0-9])" +         //at least 1 digit
                        "(?=.*[a-z])" +         //at least 1 lower case letter
                        "(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[$&+,:;=?@#|'<>.^*()%!-])"+ //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{8,30}" +               //at least 4 characters
                        "$");

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();


    }
}
