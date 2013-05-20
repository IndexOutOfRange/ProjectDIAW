package com.steto.diaw.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

import java.util.List;

public class HomeActivity extends Activity{

    public static String BUNDLE_LIST_EPISODE = "BUNDLE_LIST_EPISODE";
    private List<Episode> mAllEp;
    private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAllEp = (List < Episode > )getIntent().getExtras().get(BUNDLE_LIST_EPISODE);
        mList = (ListView)findViewById(R.id.list_episode);

        ListEpisodeHomeAdapter myAdapter = new ListEpisodeHomeAdapter(this, mAllEp);

        mList.setAdapter(myAdapter);

	}
}
