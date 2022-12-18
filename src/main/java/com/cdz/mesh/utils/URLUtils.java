package com.cdz.mesh.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {
    private static final Pattern pattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        //String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        //Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            String link = text.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
            containedUrls.add(link.substring(0,link.indexOf(".jpg")+4));
        }

        return containedUrls;
    }

   /* public static TextView tvToLink(TextView tv){

        tv.setMovementMethod(LinkMovementMethod.getInstance());

        String content = tv.getText().toString();
        List<String> links = new ArrayList<String>();

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(content);
        while (m.find()) {
            String urlStr = m.group();
            links.add(urlStr);
        }

        SpannableString f = new SpannableString(content);

        for (int i = 0; i < links.size(); i++) {
            final String url = links.get(i);

            f.setSpan(new InternalURLSpan(new View.OnClickListener() {
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    String urlToOpen = url;
                    if (!urlToOpen.startsWith("http://") || !urlToOpen.startsWith("https://"))
                        urlToOpen = "http://" + urlToOpen;
                    openURLInBrowser(urlToOpen, ctx);
                }
            }), content.indexOf(url), content.indexOf(url) + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tv.setText(f);

    }*/

}
