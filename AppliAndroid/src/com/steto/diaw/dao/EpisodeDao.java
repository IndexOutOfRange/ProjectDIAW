package com.steto.diaw.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	/**
	 * 50
	 */
	private static final int NUMBER_EPISODES = 50;

	public EpisodeDao(ConnectionSource connection) throws SQLException {
		super(connection, Episode.class);
	}

	// ---------------------------- //
	// QUERY //
	// ---------------------------- //

	/**
	 * @return the last 50 (NUMBER_EPISODE) episodes with an update
	 * @throws SQLException
	 */
	public List<Episode> queryForAllSeen() throws SQLException {
		return queryForAllSeen(NUMBER_EPISODES);
	}

	public List<Episode> queryForAllSeen(long limit) throws SQLException {
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SEEN, true);
		queryBuilder.orderBy(Episode.COLUMN_UPDATED_AT, false);
		if (limit != 0) {
			queryBuilder.limit(limit);
		}
		return query(queryBuilder.prepare());
	}

	public Show queryShowFromEpisode(Episode ep) throws SQLException {
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

	public List<Episode> queryFromShowName(String name) throws SQLException {
		SelectArg nameArg = new SelectArg();
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SHOWNAME, nameArg);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		nameArg.setValue(name);
		return query(prepare);
	}

	public List<Episode> queryFromShowNameSeen(String name) throws SQLException {
		SelectArg nameArg = new SelectArg();
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		queryBuilder.where().eq(Episode.COLUMN_SHOWNAME, nameArg);
		queryBuilder.where().eq(Episode.COLUMN_SEEN, true);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		nameArg.setValue(name);
		return query(prepare);
	}

	/**
	 * Should not be used because can have to much data<br />
	 * Use it at your own risks...
	 */
	@Override
	@Deprecated
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
	
	@SuppressWarnings("unchecked")
	public List<Episode> queryForPlanning() throws SQLException {
		// yesterday <= show first aired <= today +7
		// FIXME change the database to have real dates...
		QueryBuilder<Episode, String> queryBuilder = queryBuilder();
		Where<Episode, String> where = queryBuilder.where();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String today = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
		String yest = sdf.format(calendar.getTime());		
		calendar.add(Calendar.DATE, 2);
		String t1 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t2 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t3 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t4 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t5 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t6 = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, 1);
		String t7 = sdf.format(calendar.getTime());
		where.or(where.eq(Episode.COLUMN_FIRST_AIRED, today), 
				where.eq(Episode.COLUMN_FIRST_AIRED, yest),
				where.eq(Episode.COLUMN_FIRST_AIRED, t1),
				where.eq(Episode.COLUMN_FIRST_AIRED, t2),
				where.eq(Episode.COLUMN_FIRST_AIRED, t3),
				where.eq(Episode.COLUMN_FIRST_AIRED, t4),
				where.eq(Episode.COLUMN_FIRST_AIRED, t5),
				where.eq(Episode.COLUMN_FIRST_AIRED, t6),
				where.eq(Episode.COLUMN_FIRST_AIRED, t7));
		queryBuilder.orderBy(Episode.COLUMN_FIRST_AIRED, true);
		PreparedQuery<Episode> prepare = queryBuilder.prepare();
		return query(prepare);
	}

	// ---------------------------- //
	// COUNT //
	// ---------------------------- //
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

	// ---------------------------- //
	// CREATE //
	// ---------------------------- //
	/**
	 * @param allEp la liste des episodes à ajouter
	 * @return les épisodes correspondant dans la base
	 */
	public List<Episode> createFromWebService(List<Episode> allEp) throws SQLException {
		List<Episode> episodeInDatabaseList = new ArrayList<Episode>();

		for (Episode episode : allEp) {
			Episode ret = createIfNotExists(episode);
			if (!ret.equals(episode)) {
				Ln.d("Episode : " + episode.getShowName() + " " + episode.getSeasonNumber() + " " + episode.getEpisodeNumber() + " déjà present en base");
			}
			episodeInDatabaseList.add(ret);
		}
		return episodeInDatabaseList;
	}

	public CreateOrUpdateStatus createFromWebService(Episode episode) throws SQLException {
		if (episode == null) {
			return new CreateOrUpdateStatus(false, false, 0);
		}
		String id = extractId(episode);
		// assume we need to create it if there is no id
		if (id == null || !idExists(id)) {
			int numRows = create(episode);
			return new CreateOrUpdateStatus(true, false, numRows);
		} else {
			// nothing
			return new CreateOrUpdateStatus(false, true, 0);
		}
	}

	// ---------------------------- //
	// DELETE //
	// ---------------------------- //
	public int deleteEpisodeAfterRename(Episode ep) throws SQLException {
		DeleteBuilder<Episode, String> deleteBuilder = deleteBuilder();
		deleteBuilder.where().eq(Episode.COLUMN_OBJECT_ID, ep.getObjectId());
		delete(deleteBuilder.prepare());

		return create(ep);
	}

	// ---------------------------- //
	// UPDATE //
	// ---------------------------- //
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

			if (queryFromShowName(oldShowName).isEmpty()) {
				showDao.deleteFromName(oldShowName);
				Ln.d("Suppression de l'ancien Show " + oldShowName + " devenu inutile");
			}
		}
	}
}
