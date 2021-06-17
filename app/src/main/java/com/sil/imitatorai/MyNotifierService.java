package com.sil.imitatorai;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sil.imitatorai.models.Action;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyNotifierService extends NotificationListenerService {
    //private BufferedWriter bw;
    public static final String TAG = "MyNotifierService";

    private Gson gson = new Gson();
    private OkHttpClient client = new OkHttpClient();
    //private SimpleDateFormat sdf;
    private MyHandler handler = new MyHandler();
    private String nMessage;
    private String data;

    final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String msgString = (String) msg.obj;
            Toast.makeText(getApplicationContext(), msgString, Toast.LENGTH_SHORT).show();
        }
    };

    public void getreply(String url, String msg, Action action) {
        String newurl = url+msg;
        Log.d(TAG, "getreply msg: "+msg);
        Log.d(TAG, "getreply newurl: "+newurl);

        Request request = new Request.Builder().url(newurl).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure botReply error: "+e);
            }
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                String botReply = response.body().string().replace("\"", "");
                Log.d(TAG, "onResponse botReply: "+botReply);
                try {
                    action.sendReply(getApplicationContext(), botReply);
                }
                catch (PendingIntent.CanceledException e) {
                    Log.d(TAG, "e "+e);
                }
            }
        });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service is started" + "-----");
        if (intent != null && intent.hasExtra("data"))
        data = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
//        Log.d(TAG, "onNotificationRemoved");
//    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "Here");

        cancelNotification(sbn.getKey());
        Action action = NotificationUtils.getQuickReplyAction(sbn.getNotification(), getPackageName());
        Log.d(TAG, "action: "+action);

        if (action != null) {
            Log.d(TAG, "success");
            Log.d(TAG, "getPackageName: "+sbn.getPackageName());
            if (sbn.getPackageName().equalsIgnoreCase("com.whatsapp")) {
                String user = sbn.getNotification().extras.getString("android.title");
                String msg = sbn.getNotification().extras.getString("android.text");
                Log.d(TAG, "android.title: "+sbn.getNotification().extras.getString("android.title"));
                Log.d(TAG, "android.text: "+sbn.getNotification().extras.getString("android.text"));
                Log.d(TAG, "getNotification.extras: "+sbn.getNotification().extras);

                SharedPreferences prefs = getSharedPreferences("test", Context.MODE_PRIVATE);
                Type type = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
                ArrayList<HashMap<String, String>> mlist = gson.fromJson(prefs.getString("data", ""), type);
                Log.d(TAG, "mlist: "+mlist);

                if (msg != null && !msg.equalsIgnoreCase( "📷 Photo")) {
                    for (HashMap<String, String> item : mlist) {
                        Log.d(TAG, "item: "+item+" | user: "+user);
                        if (item.containsValue(user)) {
                            Log.d(TAG, "found user in item!");
                            int timeDelayI = Integer.parseInt(item.get("replydelay"))*1000;
                            Long timeDelayL = Long.parseLong(item.get("replydelay"))*1000;
                            Log.d(TAG, "user's timeDelayL: "+timeDelayL);
                            int random = new Random().nextInt(timeDelayI-1000) + 1000;
                            long finalDelay = Long.parseLong(String.valueOf(random));
                            Log.d(TAG, "finalDelay: "+finalDelay);
                            handler.postDelayed(() ->
                                    getreply("https://8o8a00bofa.execute-api.ap-south-1.amazonaws.com/v2/reply?message=", msg, action),
                                        finalDelay);
                            break;
                        }
                    }
                }
            } else {
                Log.d(TAG, "getPackageName not com.whatsapp");
            }
        }
        else {
            Log.d(TAG, "action is null");
        }

        try {
            //Some notifications can't parse the TEXT content. Here is a message to judge.
            Log.d(TAG, "sbn: "+sbn);
            Log.d(TAG, "sbn.getNotification(): "+sbn.getNotification());
            if (sbn.getNotification().tickerText != null) {
                SharedPreferences sp = getSharedPreferences("msg", MODE_PRIVATE);
                nMessage = sbn.getNotification().tickerText.toString();
                Log.d(TAG, "nMessage: " + nMessage);
                sp.edit().putString("getMsg", nMessage).apply();
                Message obtain = Message.obtain();
                Log.d(TAG, "og obtain: " + obtain);
                Log.d(TAG, "og obtain.obj: " + obtain.obj);
                obtain.obj = nMessage;
                mHandler.sendMessage(obtain);
                //init();
                Log.d(TAG, "data: " + data);
                if (nMessage.contains(data)) {
                    Message message = handler.obtainMessage();
                    Log.d(TAG, "message.contains(data) message: " + message);
                    message.what = 1;
                    handler.sendMessage(message);
                    //writeData(sdf.format(new Date(System.currentTimeMillis())) + ":" + nMessage);
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Unresolvable notification: "+e);
           //Toast.makeText(this, "Unresolvable notification", Toast.LENGTH_SHORT).show();
        }

    }

//    private void writeData(String str) {
//        try {
//            bw.newLine();
//            bw.write(str);
//            bw.newLine();
//            bw.close();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void init() {
//        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            FileOutputStream fos = new FileOutputStream(newFile(), true);
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            bw = new BufferedWriter(osw);
//        }
//        catch (IOException e) {
//            Log.d("KEVIN", "BufferedWriter Initialization error");
//        }
//        Log.d("KEVIN", "Initialization Successful");
//    }

//    private File newFile() {
//        File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "ANotification");
//        fileDir.mkdir();
//        String basePath = Environment.getExternalStorageDirectory() + File.separator + "ANotification" + File.separator + "record.txt";
//        return new File(basePath);
//    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG, "msg.what == 1");
            }
        }
    }
}