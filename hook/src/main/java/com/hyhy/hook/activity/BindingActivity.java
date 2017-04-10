package com.hyhy.hook.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyhy.hook.R;

public class BindingActivity extends AppCompatActivity {
    String TAG = getClass().getName();
    boolean mBound = false;
    Messenger mServuce = null;
    private int MSG_SAY_HELLO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
    }


    public void bt_bind(View v) {
        sayHello();
//        Toast.makeText(this, "弹出", Toast.LENGTH_SHORT).show();
    }


    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been   established, giving us the object we can use to
            // interact with the service.  We are communicating with the  service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mServuce = new Messenger(service);
            mBound = true;

            //添加如下
            Message msg = Message.obtain(null, 1);
//            Bundle bundle = new Bundle();
//            bundle.putString("msg", "客户端： 连上了");
//            msg.setData(bundle);
            msg.replyTo = replyMessenger;
            try {
                mServuce.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServuce = null;
            mBound = false;
        }
    };

    Messenger replyMessenger = new Messenger(new MessengerHandler());

    public class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "服务端说:" + msg.getData().getString("reply"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    public void sayHello() {
        if (!mBound) return;
        Message msg = Message.obtain();
        try {
            msg.what = MSG_SAY_HELLO;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "jon message");
            msg.setData(bundle);
            mServuce.send(msg);
            Log.i(TAG, "sayHello ---");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setClassName("com.eoe.drugstore", "com.eoe.drugstore.service.MessengerService");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        sayHello();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

}
