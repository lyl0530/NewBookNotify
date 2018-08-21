// IBookManage.aidl
package com.lyl.newbooknotify;

import com.lyl.newbooknotify.INewBookArrivedListener;


interface IBookManage {
    void register(in INewBookArrivedListener l);
    void unregister(in INewBookArrivedListener l);
}
