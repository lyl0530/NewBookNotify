// INewBookArriverListened.aidl
package com.lyl.newbooknotify;
import com.lyl.newbooknotify.Book;
// Declare any non-default types here with import statements

interface INewBookArrivedListener {
    void notify(in Book book);
}
