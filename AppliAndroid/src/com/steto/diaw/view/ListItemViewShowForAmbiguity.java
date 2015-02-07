package com.steto.diaw.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

import java.text.SimpleDateFormat;

public class ListItemViewShowForAmbiguity extends LinearLayout {

    private TextView mFirstAiredTextView;
    private LinearLayout mLinearLayout;
    private TextView mNameTextView;
    private TextView mInfoTextView;

    public ListItemViewShowForAmbiguity(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem_show_view_for_ambiguity, this);

        mLinearLayout = (LinearLayout) findViewById(R.id.listitem_show_layout);
        mNameTextView = (TextView) findViewById(R.id.listitem_show_name_tv);
        mInfoTextView = (TextView) findViewById(R.id.listitem_show_info_tv);
        mFirstAiredTextView = (TextView) findViewById(R.id.listitem_show_first_aired);

    }

    public void setData(Show show) {
        if (show == null) {
            return;
        }
        if (!TextUtils.isEmpty(show.getShowName())) {
            mNameTextView.setText(show.getShowName());
        }

        mInfoTextView.setText(show.getResume());
        if( show.getDateDebut() != null ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            mFirstAiredTextView.setText(sdf.format(show.getDateDebut()));
        } else {
            mFirstAiredTextView.setText("");
        }

    }

    public void setLayoutBackgroundState(int[] state) {
        mLinearLayout.getBackground().setState(state);
    }
}
