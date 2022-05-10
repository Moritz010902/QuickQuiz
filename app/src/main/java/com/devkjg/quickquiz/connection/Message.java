package com.devkjg.quickquiz.connection;

import java.util.regex.Pattern;


public class Message {

    private static String finalMessage;

    Message(String issue, String message) {
        finalMessage = issue.hashCode() + ":" + message;
    }

    String getText() {
        return finalMessage;
    }

    static String getContent(String message) {
        String[] content = message.split(Pattern.quote(":"));
        if(content.length != 2)
            return null;
        return content[1];
    }

    public static Boolean isIssue(String message, String issue) {
        String[] content = message.split(Pattern.quote(":"));
        if(content.length != 2)
            return null;
        return Integer.parseInt(content[0]) == issue.hashCode();
    }

    Boolean isIssue(String issue) {
        String[] content = finalMessage.split(Pattern.quote(":"));
        if(content.length != 2)
            return null;
        return Integer.parseInt(content[0]) == issue.hashCode();
    }

    public static boolean isValid(String message) {
        return (message.split(Pattern.quote(":"))).length == 2;
    }

}
