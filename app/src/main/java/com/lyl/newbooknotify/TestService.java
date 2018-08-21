package com.lyl.newbooknotify;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dengjifu on 18-8-21.
 */

public class TestService extends Service {
    private static final String TAG = "TestService lyl123";

    //AIDL方法运行在服务端的Binder线程池中，多个客户端同时连接是，可能有多个线程并发访问，
    //故要在AIDL方法中处理线程同步问题。CopyOnWriteArrayList:支持并发读写，用于线程同步。
    private CopyOnWriteArrayList<Book> mBookList =
            new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<INewBookArrivedListener> mListener =
            new CopyOnWriteArrayList<>();

    //RemoteCallbackList:是系统提供的用于删除跨进程listener的接口，且内部自动实现了线程同步功能
    private RemoteCallbackList<INewBookArrivedListener> mListener1 =
            new RemoteCallbackList<>();

    private AtomicBoolean isLive = new AtomicBoolean(true);

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                notifyToClient();
            }
        }).start();
    }

    private void notifyToClient() {
        Log.e(TAG, "notifyToClient");
        while (isLive.get()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int size = mBookList.size();
            Book book = new Book(size, "book of " + size);
            mBookList.add(book);

//            for (INewBookArrivedListener l : mListener){
//                try {
//                    l.notify(book);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
            int N = mListener1.beginBroadcast();
            for (int i = 0; i < N; i++){
                INewBookArrivedListener l = mListener1.getBroadcastItem(i);
                try {
                    l.notify(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mListener1.finishBroadcast();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Binder mBinder = new IBookManage.Stub() {
        @Override
        public void register(INewBookArrivedListener l) throws RemoteException {
//            if (!mListener.contains(l)){
//                Log.e(TAG, "will add to list!");
//                mListener.add(l);
//            } else {
//                Log.e(TAG, "have already add to list!");
//            }
            mListener1.register(l);
            Log.e(TAG, "listener's num = " + mListener1.getRegisteredCallbackCount());
        }

        @Override
        public void unregister(INewBookArrivedListener l) throws RemoteException {
//            if (mListener.contains(l)){
//                Log.e(TAG, "will remove from list!");
//                mListener.remove(l);
//            } else {
//                Log.e(TAG, "have already remove from list!");
//            }
            mListener1.unregister(l);
            Log.e(TAG, "listener's num = " + mListener1.getRegisteredCallbackCount());
        }
    };

    @Override
    public void onDestroy() {
        isLive.set(false);
        super.onDestroy();
    }
}
