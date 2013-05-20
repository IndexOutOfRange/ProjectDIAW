package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.steto.diaw.activity.EpisodeView;
import com.steto.diaw.model.Episode;

import java.util.List;

/**
 * Created by Stephane on 20/05/13.
 */
public class ListEpisodeHomeAdapter extends BaseAdapter{

    private List<Episode> mAllEp;
    private Context mContext;

    public ListEpisodeHomeAdapter( Context ctx, List<Episode> all ) {
        mContext = ctx;
        mAllEp = all;
    }

    @Override
    public int getCount() {
        return mAllEp.size();
    }

    @Override
    public Object getItem(int i) {
        return mAllEp.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        EpisodeView current;
        if (view == null || !(view instanceof EpisodeView)) {
            current = new EpisodeView(mContext);
        } else {
            current = (EpisodeView)view;
        }

        current.setData(mAllEp.get(i));

        return current;
    }
}
