package com.klickit.kcpeteacherapp.interfaces;

public interface RecyclerItemClickListener<T> {
    void onItemClicked(T item);

    T getItemAt(int position);

    //used to restore deleted item on swipe to delete functionality.
    // this is mostly when you want to perform the UNDO DELETE.
    void restoreItem(T item, int position);

}


