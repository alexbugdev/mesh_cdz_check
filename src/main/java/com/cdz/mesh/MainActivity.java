package com.cdz.mesh;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.cdz.mesh.utils.URLUtils;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static boolean isCooldowned = false;

    @Override
    protected void onStart() {
        super.onStart();
        scheduledTask();
        sendToast(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        ScrollView view = (ScrollView) findViewById(R.id.scrollview);
        LinearLayout layouts = (LinearLayout) findViewById(R.id.customlinear);
        EditText valueTV = new EditText(this);
        TextView views = (TextView) findViewById(R.id.textView);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        Button button = (Button) findViewById(R.id.butt);
        EditText edit = (EditText) findViewById(R.id.custombar);
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton);

        TextView vieww = (TextView)findViewById(R.id.textView3);
        vieww.setText(Html.fromHtml("<a href=\"https://uchebnik.mos.ru/cms/system_2/atomic_objects/files/008/639/140/icon/%D0%B4%D0%B2%D1%83%D0%BF%D0%BE%D0%BB%D1%8C%D0%B5_%D0%B8_%D1%82%D1%80%D1%91%D1%85%D0%BF%D0%BE%D0%BB%D1%8C%D0%B5.jpg\">Ссылка на тест</a>"));


        Button button2 = (Button) findViewById(R.id.button2);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setText("");
                if (valueTV != null){
                    valueTV.setText("");
                    valueTV.setFocusable(false);
                    layouts.removeAllViews();
                    layouts.addView(valueTV);

                }
            }
        });
        vieww.setMovementMethod(LinkMovementMethod.getInstance());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setText(getClipboardData());
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetAvailable();
                if(!isNetworkConnected()) {
                        sendCustomToast("У вас нету подключения к интернету. ");
                        return;
                }
                if(edit.getText().toString().isEmpty() || edit.getText().toString() == null) {
                    sendCustomToast("Введите ссылку на тест ЦДЗ.");
                    return;
                }
                String text = edit.getText().toString();
                if(!isLink(text)){
                    sendCustomToast("Введённая ссылка не является валидной ссылкой");
                    return;
                }
                if(isCooldowned){
                   sendCustomToast("Подождите немного, перед следущим запросом.");
                   return;
                }

                if (valueTV != null){
                    valueTV.setText("");
                    layouts.removeAllViews();
                    valueTV.setFocusable(false);

                }

                isCooldowned = true;
                cooldownTimer();

                onClc(layouts,text);

                scheduledTask();
            }

        });
        //String[] elements1 = maps.get(0).get(0).toJava(String[].class);
        //String[] elements2 = maps.get(1).get(1).toJava(String[].class);

        //sb.append(elements2).append(" ");
        /*for (int i = 0; i<maps.size(); ++i) {
             sb.append("Элементы: " + maps.get(i).asList().size()).append(" ");
        }*/
        scheduledTask();


    }
    private final void cooldownTimer() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                isCooldowned = false;
            }
        }, TimeUnit.SECONDS.toMillis(25));
    }
    private final void scheduledTask() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            synchronized public void run() {

                NotificationManager notifManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notifManager.cancelAll();
                System.out.println("Completed!");

            }

        }, TimeUnit.SECONDS.toMillis(1));
    }

    public String getClipboardData() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text = "";
           ClipData clip= clipboard.getPrimaryClip();
           if(clip == null
                   || clip.getItemCount() == 0
                   || clip.getItemCount() > 0 && clip.getItemAt(0).getText() == null) {
               return "Nothing";
               //.getItemAt(0).getText().toString();
        }
        return clip.getItemAt(0).getText().toString();
    }

    public void sendToast(Context activity) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast mToast = Toast.makeText(activity, "Приложение ещё находится в разработке, пишите в телеграмм если найдете баги.", Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    private boolean isLink(String link){
        return new UrlValidator().isValid(link) && link.contains("exam") && link.contains("test_by_binding");
    }

    public void sendCustomToast(String message) {
        Toast mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public static TextView valueTV = null;

    public static ImageView imageView = null;

    public void tfToLink() {

    }

    public void onClc(LinearLayout layouts, String link) {

        valueTV = new TextView(this);

        StringBuilder sb = new StringBuilder();

        Python py = Python.getInstance();

        PyObject pyobj = py.getModule("cdzbackend");
        PyObject customObj = null;
        try {
            customObj = pyobj.callAttr("get_answers",
                    link, "orElseReturn=True");
        }
        catch (Exception ex) {
            sendCustomToast("Что-то пошло не так, попробуйте снова. Код ошибки " +
                    ex.getMessage().toString());
            return;
        }
        if (customObj == null){
            sendCustomToast("Объект вернул null, попробуйте снова.");
            return;
        }
        Map<PyObject,PyObject> questions = customObj.asMap();
        valueTV.setMovementMethod(LinkMovementMethod.getInstance());
        for(PyObject objs : questions.keySet()){
            List<String> elements = URLUtils.extractUrls(objs.toString());
            String objects = objs.toString();

            for(String values : elements) {
                System.out.println("Values " + values);
                objects = objects.replace(values," <a href=\""+values+"\">Ссылка на картинку</a>");
            }

            sb.append("Вопрос: " + objects + "<br><br>" + " Ответ: " + questions.get(objs).toString()).append("<br><br>");


        }
        sb.append("Конец Документа\n").append("-----------------------");
        valueTV.setText(Html.fromHtml(sb.toString()));
        valueTV.setLinksClickable(true);

        valueTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        valueTV.setMovementMethod(LinkMovementMethod.getInstance());
        valueTV.setSingleLine(false);
        valueTV.setTextIsSelectable(true);
        //valueTV.setInputType(InputType.TYPE_NULL);
        valueTV.setFocusableInTouchMode(true);
        //valueTV.setAutoLinkMask(Linkify.ALL);
        //valueTV.setMovementMethod(LinkMovementMethod.getInstance());
        layouts.addView(valueTV);

    }
    protected static Boolean isConnected = false;
    public boolean isInternetAvailable() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                    try {
                        InetAddress ipAddr = InetAddress.getByName("google.com");
                        //You can replace it with your name
                        isConnected = true;

                    } catch (UnknownHostException e) {
                       isConnected = false;
                    }
            }
        });
        thread.start();
        return isConnected;
    }

}