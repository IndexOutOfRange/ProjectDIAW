package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.diaw.tools.DatabaseHelper;
import com.steto.projectdiaw.R;

public class HomeActivity extends Activity{

    private List<Episode> mAllEp;
    private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        getActionBar().setTitle("Derniers Episodes Regardés");
        
		readDatabase();
		
        mList = (ListView)findViewById(R.id.list_episode);

        ListEpisodeHomeAdapter myAdapter = new ListEpisodeHomeAdapter(this, mAllEp);

        mList.setAdapter(myAdapter);

	}

	private void readDatabase() {
		try {
			mAllEp = DatabaseHelper.getInstance(this).getEpisodeDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
