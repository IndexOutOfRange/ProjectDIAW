package com.steto.diaw.dao;

import android.util.Log;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class EpisodeDao extends BaseDaoImpl<Episode, Integer> {

	private static final String TAG = "EpisodeDao";
    private ConnectionSource mSource;

	public EpisodeDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Episode.class);
        mSource = connectionSource;
	}

	@Override
	public CreateOrUpdateStatus createOrUpdate(Episode data) throws SQLException {
		List<Episode> listEpisodes = queryForAll();
		if (listEpisodes.isEmpty()) {
			Log.d(TAG, "createOrUpdate liste vide");
			create(data);
			return new CreateOrUpdateStatus(true, false, 0);
		}

		boolean create = true;
		for (int i = 0; i < listEpisodes.size() && create; i++) {
			Episode episode = listEpisodes.get(i);
			if (episode.getShowName().equalsIgnoreCase(data.getShowName())
					&& episode.getSeasonNumber() == data.getSeasonNumber()
					&& episode.getEpisodeNumber() == data.getEpisodeNumber()) {
				Log.d(TAG, "createOrUpdate episode already in base");
				create = false;
			}
		}

		if (create) {
			create(data);
			return new CreateOrUpdateStatus(true, false, 0);
		} else {
			return new CreateOrUpdateStatus(false, false, 0);
		}
	}

	@Override
	public List<Episode> queryForAll() throws SQLException {
		List<Episode> episodeList = super.queryForAll();
		Collections.sort(episodeList);
		return episodeList;
	}


    /**
     *
     * @param allEp la liste des episodes à ajouter
     * @return le nombre d'épisode ajouté en base et pas updaté
     */
    public int createOrUpdate(List<Episode> allEp) throws SQLException {
        int nbCreated = 0;
        for (Episode episode : allEp) {
            CreateOrUpdateStatus ret = createOrUpdate(episode);
            if(ret.isCreated()) {
                nbCreated++;
            }
        }
        return nbCreated;
    }

    public Show getShowFromEpisode(Episode ep) {
        ShowDao myDAO = null;
        Show associated = new Show(ep.getShowName());
        try {
            myDAO = new ShowDao(mSource);
            List<Show> allShow = myDAO.queryForAll();
            for (Show show : allShow) {
                if (show.equals(associated)) {
                    return show;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return associated;
    }
}
