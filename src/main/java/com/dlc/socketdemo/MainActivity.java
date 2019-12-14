package com.dlc.socketdemo;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dlc.dlcconversiontool.HexTool;
import com.dlc.dlcsocketlibrary.ConnectStatusListener;
import com.dlc.dlcsocketlibrary.DLCSocket;
import com.dlc.dlcsocketlibrary.HeartListener;
import com.dlc.dlcsocketlibrary.SendDataCallback;
import com.dlc.dlcsocketlibrary.SocketDataReceiveListener;
import com.dlc.dlcsocketlibrary.SocketManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);

//        final DLCSocket socket2 = connectSocket("192.168.56.1", 7519);
        final DLCSocket socket2 = connectSocket("120.77.72.190", 7519);
//        socket2.setCallbackMode(1);
//        socket2.setReceiveTimeOut(1000);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket2.closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                socket1.send("3344");
            }
        });
//        timerRequest();
    }

    private DLCSocket connectSocket(final String address, final int port) {

        final DLCSocket socket = SocketManager.getSocketManager().newDlcSocket();
        socket.setConnectStatusListener(new ConnectStatusListener() {
            @Override
            public void onConnectStatus(String address, int port, final int status, boolean isActiveDisconnection) {
                spm("onConnectStatus:" + status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(textView.getText() + "\n" + "onConnectStatus:" + status);
                    }
                });
                if (status == 1) {
                    socket.startHeartTimer(10000);
                } else {
                    if (!isActiveDisconnection) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        socket.connect(address, port);
                    }
                }
            }
        });
        socket.setSocketDataReceiveListener(new SocketDataReceiveListener() {
            @Override
            public void onSocketDataReceive(String address, int port, final byte[] data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(textView.getText() + "\n" + "onSocketDataReceive:" + new String(data));
                    }
                });
            }
        });
        socket.setLogListener(new DLCSocket.LogListener() {
            @Override
            public void onLog(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(textView.getText() + "\n" + msg);
                    }
                });
            }
        });
        socket.setHeartListener(new HeartListener() {
            @Override
            public void onHeart(String address, int port) {
                JSONObject object = new JSONObject();
                try {
                    object.put("macno", Settings.System.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.send(object.toString());
            }
        });
        socket.connect(address, port);
        return socket;
    }

    private String getDataLengthHexStr(String data) {
        int length = data.getBytes().length;
        String dataLength = Integer.toHexString(length);
        while (dataLength.length() < 4) {
            dataLength = "0" + dataLength;
        }
//        dataLength = HexTool.getInstance().hexStringToContent(dataLength);
        return dataLength;
    }

    private void sendData(DLCSocket socket) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        try {
            object.put("macno", "5566");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = "DLC" + getDataLengthHexStr(object.toString()) + object.toString();
        socket.send(data);
        spm("sendData:" + data);
    }

    private void spm(String msg) {
        Log.d("spm", msg);
    }
}


