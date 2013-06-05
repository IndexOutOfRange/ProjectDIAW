package com.steto.diaw.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Season;
import com.steto.diaw.model.Show;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowDao extends BaseDaoImpl<Show, Integer> {

	private static final String TAG = "ShowDao";
    private ConnectionSource mConnection;

	public ShowDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Show.class);
        mConnection = connectionSource;
	}

	@Override
	public List<Show> queryForAll() throws SQLException {
		List<Show> showList = super.queryForAll();
		Collections.sort(showList);
		return showList;
	}

    public List<Episode> getEpisodeFromShow(Show show) throws SQLException{
        EpisodeDao episodesDao = new EpisodeDao(mConnection);
        return episodesDao.queryForEq(Episode.SHOWNAME, show.getShowName());

    }

    public List<Season> getSeasonsFromShow(Show show) {
        List<Episode> episodes = null;
        List<Season> seasons = new ArrayList<Season>();
        try {
            episodes = getEpisodeFromShow(show);
            if (episodes == null || episodes.isEmpty()) {
                return new ArrayList<Season>();
            }

            Collections.sort(episodes, new Episode.OrderShowComparator());

            Season season = new Season();
            List<Episode> episodesToAdd = new ArrayList<Episode>();

            season.setNumber(episodes.get(0).getSeasonNumber());
            episodesToAdd.add(episodes.get(0));

            for (int i = 1; i < episodes.size(); i++) {
                if (episodes.get(i).getSeasonNumber() == season.getNumber()) {
                    episodesToAdd.add(episodes.get(i));
                } else {
                    // change of season
                    season.setEpisodes(episodesToAdd);
                    seasons.add(season);

                    season = new Season();
                    episodesToAdd = new ArrayList<Episode>();
                    season.setNumber(episodes.get(i).getSeasonNumber());
                    episodesToAdd.add(episodes.get(i));
                }
            }

            season.setEpisodes(episodesToAdd);
            seasons.add(season);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seasons;
    }
}
