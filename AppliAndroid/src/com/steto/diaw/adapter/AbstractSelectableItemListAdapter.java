package com.steto.diaw.adapter;

import android.content.Context;

import com.steto.diaw.model.Episode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Stephane on 09/12/13.
 */
public abstract class AbstractSelectableItemListAdapter<T> extends AbstractListDataAdapter<T> {

    protected HashSet<Integer> mCheckedItems; //TODO passe ca en fortement typé : T
    private boolean mMultiMode;

    public AbstractSelectableItemListAdapter(Context ctx, List<T> all) {
        super(ctx, all);
        mCheckedItems = new HashSet<Integer>();
    }

    public void enterMultiMode() {
        mMultiMode = true;
        notifyDataSetChanged();
    }

    public void exitMultiMode() {
        mCheckedItems.clear();
        mMultiMode = false;
        notifyDataSetChanged();
    }

    public void setChecked(int pos, boolean checked) {
        if (checked) {
            mCheckedItems.add(Integer.valueOf(pos));
        } else {
            mCheckedItems.remove(Integer.valueOf(pos));
        }
        if (mMultiMode) {
            notifyDataSetChanged();
        }
    }

    public boolean isChecked(int pos) {
        return mCheckedItems.contains(Integer.valueOf(pos));
    }

    public void toggleChecked(int pos) {
        final Integer v = Integer.valueOf(pos);
        if (mCheckedItems.contains(v)) {
            mCheckedItems.remove(v);
        } else {
            mCheckedItems.add(v);
        }
        this.notifyDataSetChanged();
    }

    public int getCheckedItemCount() {
        return mCheckedItems.size();
    }

    public T getFirstCheckedItem() {
        for (Integer i : mCheckedItems) {
            return mData.get(i.intValue());
        }
        return null;
    }

    public List<T> getCheckedItems() {
        List<T> checkedEpisodes = new ArrayList<T>();
        for (int index : mCheckedItems) {
            checkedEpisodes.add((T) getItem(index));
        }
        return checkedEpisodes;
    }

}
