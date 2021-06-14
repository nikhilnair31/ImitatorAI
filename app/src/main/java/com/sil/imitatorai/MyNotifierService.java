package com.sil.imitatorai;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.sil.imitatorai.models.Action;
import com.sil.imitatorai.models.SaveCustomeMessage;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyNotifierService extends NotificationListenerService {
    private BufferedWriter bw;
    public static final String TAG = "Sil";

    private OkHttpClient client = new OkHttpClient();
    private SimpleDateFormat sdf;
    private MyHandler handler = new MyHandler();
    private String nMessage;
    private String data;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String msgString = (String) msg.obj;
            Toast.makeText(MyNotifierService.this.getApplicationContext(), msgString, Toast.LENGTH_LONG).show();
        }
    };

    public void getreply(String url, String msg, Action action) {
        String newurl = url+msg;
        Log.i(TAG, "getreply url: "+url);
        Log.i(TAG, "getreply msg: "+msg);
        Log.i(TAG, "getreply newurl: "+newurl);

        Request request = new Request.Builder().url(newurl).build();
        this.client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure botReply error: "+e);
            }
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                String botReply = response.body().string().replace("\"", "");
                Log.i(TAG, "onResponse botReply: "+botReply);
                try {
                    action.sendReply(getApplicationContext(), botReply);
                }
                catch (PendingIntent.CanceledException e) {
                    Log.i(TAG, "e "+e);
                }
            }
        });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service is started" + "-----");
        if (intent != null && intent.hasExtra("data"))
        data = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.i(TAG, "onNotificationRemoved");
    }

    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG, "Here");

        cancelNotification(sbn.getKey());
        Action action = NotificationUtils.getQuickReplyAction(sbn.getNotification(), getPackageName());

        if (action != null) {
            Log.i(TAG, "success");
            Log.i(TAG, "getPackageName: "+sbn.getPackageName());
            if (sbn.getPackageName().equalsIgnoreCase("com.whatsapp")) {
                String user = sbn.getNotification().extras.getString("android.title");
                String msg = sbn.getNotification().extras.getString("android.text");
                Log.i(TAG, "getNotification.extras: "+sbn.getNotification().extras);

                Map<String, ?> allEntries = getSharedPreferences("test", Context.MODE_PRIVATE).getAll();
                ArrayList<HashMap<String, String>> mlist = new ArrayList<>();
                for (Map.Entry<String, ?> item : allEntries.entrySet()) {
                    HashMap<String, String> minimap = new HashMap<>();
                    minimap.put(item.getKey(), item.getValue().toString());
                    mlist.add(minimap);
                }
                Log.i(TAG, "mlist: "+mlist);

                if (msg != null && !msg.equalsIgnoreCase( "📷 Photo")) {
                    for (HashMap<String, String> item : mlist) {
                        Log.i(TAG, item.toString());
                        if (item.containsKey(user)) {
                            Log.i(TAG, item.toString()+" | "+user);
                            this.getreply("https://8o8a00bofa.execute-api.ap-south-1.amazonaws.com/v2/reply?message=", msg, action);
                            break;
                        }
                    }
                }
            }
        }
        else {
            Log.i(TAG, "not success");
        }

        try {
            //Some notifications can't parse the TEXT content. Here is a message to judge.
            if (sbn.getNotification().tickerText != null) {
                SharedPreferences sp = getSharedPreferences("msg", MODE_PRIVATE);
                nMessage = sbn.getNotification().tickerText.toString();
                Log.e(TAG, "nMessage: " + nMessage);
                sp.edit().putString("getMsg", nMessage).apply();
                Message obtain = Message.obtain();
                Log.e(TAG, "og obtain: " + obtain);
                obtain.obj = nMessage;
                Log.e(TAG, "now obtain: " + obtain);
                mHandler.sendMessage(obtain);
                init();
                Log.e(TAG, "data: " + data);
                if (nMessage.contains(data)) {
                    Message message = handler.obtainMessage();
                    Log.e(TAG, "message: " + message);
                    message.what = 1;
                    handler.sendMessage(message);
                    writeData(sdf.format(new Date(System.currentTimeMillis())) + ":" + nMessage);
                }
            }
        }
        catch (Exception e) {
           Toast.makeText(this, "Unresolvable notification", Toast.LENGTH_SHORT).show();
        }

    }

    private void writeData(String str) {
        try {
            bw.newLine();
            bw.write(str);
            bw.newLine();
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            FileOutputStream fos = new FileOutputStream(newFile(), true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);
        }
        catch (IOException e) {
            Log.d("KEVIN", "BufferedWriter Initialization error");
        }
        Log.d("KEVIN", "Initialization Successful");
    }

    private File newFile() {
        File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "ANotification");
        fileDir.mkdir();
        String basePath = Environment.getExternalStorageDirectory() + File.separator + "ANotification" + File.separator + "record.txt";
        return new File(basePath);
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.e(TAG, "msg.what == 1");
            }
        }
    }
}