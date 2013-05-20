package com.steto.diaw.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

/**
 * Created by Stephane on 20/05/13.
 */
public class EpisodeView extends LinearLayout {

    private Context mContext;
    private Episode mEp;
    private TextView mTitle;
    private TextView mSubTitle;


    public EpisodeView(Context context) {
        super(context);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.celllist_show_title, this);
        mTitle = (TextView)findViewById(R.id.celllist_show_title_show_name);
        mSubTitle = (TextView)findViewById(R.id.celllist_show_title_episode_name);
    }

    public void setData( Episode ep) {
        mEp = ep;
        mTitle.setText(ep.getShowName());
        String sub = "Saison " + String.valueOf(ep.getSeasonNumber()) + " Episode " +  String.valueOf(ep.getEpisodeNumber());
        mSubTitle.setText(sub);
    }
}
