package com.steto.diaw.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import roboguice.util.Ln;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;

public class EpisodeDao extends BaseDaoImpl<Episode, String> {

	public EpisodeDao(ConnectionSource connection) throws SQLException {
		super(connection, Episode.class);
	}

	@Override
	public List<Episode> queryForAll() throws SQLException {
		List<Episode> episodeList = super.queryForAll();
		Collections.sort(episodeList);
		return episodeList;
	}

	public List<Episode> queryWithLimit(int number) throws SQLException {
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.orderBy(Episode.COLUMN_UPDATED_AT, false);
		queryBuilder.limit(Long.valueOf(number));
		queryBuilder.prepare();
		return queryBuilder.query();
	}

	public List<Episode> getAllEpisodeFromShowName(String name) throws SQLException {
		SelectArg nameArg = new SelectArg();
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SHOWNAME, nameArg);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		nameArg.setValue(name);
		return query(prepare);
	}
	
	public List<Episode> getAllEpisodeFromShowNameSeen(String name) throws SQLException {
		SelectArg nameArg = new SelectArg();
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SHOWNAME, nameArg);
		queryBuilder.where().eq(Episode.COLUMN_SEEN, true);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		nameArg.setValue(name);
		return query(prepare);
	}

	public int countAllEpisodeFromShowNameSeen(String name) throws SQLException {
		SelectArg nameArg = new SelectArg();
		nameArg.setMetaInfo(Episode.COLUMN_SHOWNAME);
		nameArg.setValue(name);
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.setCountOf(true);
		Where<Episode, String> where = queryBuilder.where();
		where.eq(Episode.COLUMN_SHOWNAME, nameArg);
		where.and().eq(Episode.COLUMN_SEEN, true);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		return (int) countOf(prepare);
	}
	/**
	 * @param allEp la liste des episodes à ajouter
	 * @return le nombre d'épisode ajouté en base et pas updaté
	 */
	public int createOrUpdate(List<Episode> allEp) throws SQLException {
		int nbCreated = 0;

		for (Episode episode : allEp) {
			CreateOrUpdateStatus ret = createOrUpdate(episode);
			if (ret.isCreated()) {
				nbCreated++;
			} else {
				Ln.d("Episode : " + episode.getShowName() + " " + episode.getSeasonNumber() + " " + episode.getEpisodeNumber() + " déjà present en base");
			}
		}
		return nbCreated;
	}

	public Show getShowFromEpisode(Episode ep) throws SQLException {
		ShowDao myDAO = null;
		Show associated = new Show(ep.getShowName());
		myDAO = DaoManager.createDao(getConnectionSource(), Show.class);
		List<Show> allShow = myDAO.queryForAll();
		for (Show show : allShow) {
			if (show.equals(associated)) {
				return show;
			}
		}
		return associated;
	}

	public int deleteEpisodeAfterRename(Episode ep) throws SQLException {
		DeleteBuilder<Episode, String> deleteBuilder = deleteBuilder();
		deleteBuilder.where().eq(Episode.COLUMN_OBJECT_ID, ep.getObjectId());
		delete(deleteBuilder.prepare());

		return create(ep);
	}

	public List<Episode> queryForAllSeen() throws SQLException {
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SEEN, true);
		queryBuilder.orderBy(Episode.COLUMN_UPDATED_AT, false);
		return query(queryBuilder.prepare());
	}

	public void updateEpisode(Episode episode, String keyOfNewValue, String newValue) throws SQLException {
		Episode episodeUpdated = queryForEq(Episode.COLUMN_OBJECT_ID, episode.getObjectId()).get(0);
		String oldShowName = episodeUpdated.getShowName();
		episodeUpdated.setUpdatedAt(new Date());

		if (Episode.COLUMN_SHOWNAME.equals(keyOfNewValue)) {
			episodeUpdated.setShowName(newValue);

			ShowDao showDao = new ShowDao(getConnectionSource());
			if (showDao.queryFromName(newValue) == null) {
				Ln.d("recherche si le show n'existe pas pour l'ajouter en base");

				Show show = new Show(newValue);
				showDao.create(show);
			}

			deleteEpisodeAfterRename(episodeUpdated);

			if (getAllEpisodeFromShowName(oldShowName).isEmpty()) {
				showDao.deleteFromName(oldShowName);
				Ln.d("Suppression de l'ancien Show " + oldShowName + " devenu inutile");
			}
		}
	}
}
