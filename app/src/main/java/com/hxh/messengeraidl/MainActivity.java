package com.hxh.messengeraidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final int MSG_FROM_CLIENT = 0x11;
    public static final int MSG_FROM_SERVICE = 0x12;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //1、发送消息给服务端
            Messenger clientMessenger = new Messenger(service);
            Message message = Message.obtain(null, MSG_FROM_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "Hello from client.");
            message.setData(bundle);
            //3、这句是服务端回复客户端使用
            message.replyTo = replyMessenger;
            try {
                clientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final Messenger replyMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.MSG_FROM_SERVICE:
                    //5、服务端回复消息给客户端，客户端接送消息
                    Log.d("wxl", "msg=" + msg.getData().getString("msg"));
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent service = new Intent(this, MessengerService.class);
        bindService(service, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
