package com.steto.diaw.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.steto.diaw.model.Episode;

public class EpisodeDao extends BaseDaoImpl<Episode, String> {

	
	public EpisodeDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Episode.class);
	}
}
