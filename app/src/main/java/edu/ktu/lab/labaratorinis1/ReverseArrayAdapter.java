package edu.ktu.lab.labaratorinis1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class ReverseArrayAdapter<T> extends ArrayAdapter<T> {

    public ReverseArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return super.getItem(getCount() - position - 1);
    }
}
