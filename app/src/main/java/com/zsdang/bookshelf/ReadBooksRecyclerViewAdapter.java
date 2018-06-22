package com.zsdang.bookshelf;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zsdang.ImageLoader;
import com.zsdang.LogUtils;
import com.zsdang.R;
import com.zsdang.beans.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinyongSu on 2018/5/31.
 */

public class ReadBooksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "ReadBooksRecyclerViewAdapter";

    private static final int VIEW_TYPE_READING = 0;

    private static final int VIEW_TYPE_READ = 1;

    private List<Book> mReadBooks;

    public ReadBooksRecyclerViewAdapter() {
        mReadBooks = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == VIEW_TYPE_READING) {
            return VIEW_TYPE_READING;
        } else {
            return VIEW_TYPE_READ;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (viewType == VIEW_TYPE_READING) {
                        return 3;
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LogUtils.d(TAG, "onCreateViewHolder");
        if (viewType == VIEW_TYPE_READING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_bookshelf_reading, parent, false);
            return new ReadingBookItem(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_bookshelf_read, parent, false);
            return new ReadBookItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //LogUtils.d(TAG, "onBindViewHolder");
        Book book = mReadBooks.get(position);
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_READING) {
            ReadingBookItem readingBookItem = (ReadingBookItem) holder;
            readingBookItem.updateView(book);
        } else {
            ReadBookItem readBookItem = (ReadBookItem) holder;
            readBookItem.updateView(book);
        }
    }

    @Override
    public int getItemCount() {
        return mReadBooks.size();
    }

    public void notifyDataSetChanged(final List<Book> books) {
        if (books != null) {
            //LogUtils.d(TAG, "notifyDataSetChanged:" + books.size());
            mReadBooks = books;
            notifyDataSetChanged();
        }
    }

    private class ReadingBookItem extends RecyclerView.ViewHolder {

        private TextView bookNameTv;

        public ReadingBookItem(View itemView) {
            super(itemView);
            bookNameTv = (TextView) itemView.findViewById(R.id.reading_book_name);
        }

        public void updateView(Book book) {
            LogUtils.d(TAG, "updateView:" + book.getName());
            if (!TextUtils.isEmpty(book.getName())) {
                bookNameTv.setText(book.getName());
                ImageLoader imageLoader = new ImageLoader(bookNameTv);
                imageLoader.execute("1", "2", "3");
            }
        }
    }

    private class ReadBookItem extends RecyclerView.ViewHolder {

        private TextView bookNameTv;

        public ReadBookItem(View itemView) {
            super(itemView);
            bookNameTv = (TextView) itemView.findViewById(R.id.read_book_name);
        }

        public void updateView(Book book) {
            LogUtils.d(TAG, "updateView:" + book.getName());
            if (!TextUtils.isEmpty(book.getName())) {
                bookNameTv.setText(book.getName());
                ImageLoader imageLoader = new ImageLoader(bookNameTv);
                imageLoader.execute("1", "2", "3");
            }
        }
    }
}
