package com.steto.diaw.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.steto.diaw.model.Show;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ShowDao extends BaseDaoImpl<Show, Integer> {

	private static final String TAG = "ShowDao";

	public ShowDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Show.class);
	}

	@Override
	public Show createIfNotExists(Show data) throws SQLException {
		List<Show> showList = queryForAll();
		for (Show show : showList) {
			if (show.equals(data)) {
				return null;
			}
		}
		return super.createIfNotExists(data);
	}

	@Override
	public List<Show> queryForAll() throws SQLException {
		List<Show> showList = super.queryForAll();
		Collections.sort(showList);
		return showList;
	}
}
