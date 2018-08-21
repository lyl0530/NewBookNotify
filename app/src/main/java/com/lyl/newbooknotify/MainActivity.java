package com.lyl.newbooknotify;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity lyl123";
    private IBookManage iBookManage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindTestService();
    }

    private INewBookArrivedListener mListener = new INewBookArrivedListener.Stub(){
        @Override
        public void notify(Book book) throws RemoteException {
            Log.e(TAG, "notify: book = " + book);

        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManage = IBookManage.Stub.asInterface(service);
            try {
                iBookManage.register(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void bindTestService() {
        Log.e(TAG, "bindTestService");
        Intent service = new Intent(this, TestService.class);
        bindService(service, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (null != conn) {
            Log.e(TAG, "will unbind TestService");
            try {
                if (null != iBookManage && iBookManage.asBinder().isBinderAlive()) {
                    iBookManage.unregister(mListener);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(conn);
            conn = null;
        } else {
            Log.e(TAG, "have unbind TestService");
        }
        super.onDestroy();
    }
}
