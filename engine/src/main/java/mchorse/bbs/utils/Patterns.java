package mchorse.bbs.utils;

import java.util.regex.Pattern;

public class Patterns
{
    public static final Pattern FILENAME = Pattern.compile("^[\\w\\d-_.\\[\\]!@#$%^&()]*$");
    public static final Pattern UNICODE_CHARACTER = Pattern.compile("\\\\u([\\da-zA-Z]{4})");
}