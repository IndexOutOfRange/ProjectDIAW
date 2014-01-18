package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.inject.Inject;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableInfo;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_viewdatabase)
public class DatabaseActivity extends RoboActivity {

	// ------------------------------
	// CONSTANTS
	// ------------------------------
	private static final long PAGESIZE = 30;
	private final static String[] ALLOWED_TYPE_FOR_SEARCH = { "int", "boolean", "String" };

	// ------------------------------
	// ATTRIBUTES
	// ------------------------------
	@InjectView(R.id.spinner)
	private Spinner mSpinner;
	@InjectView(R.id.et_search)
	private EditText mEditTextSearch;
	@InjectView(R.id.spinner_search_column)
	private Spinner mSpinnerSearch;
	@InjectView(R.id.btn_search)
	private ImageButton mBtnSearch;
	@InjectView(R.id.layout_search)
	private LinearLayout mLayoutSearch;
	@InjectView(R.id.tableLayout)
	private TableLayout mTL;

	private ArrayAdapter<String> mSpinnerAdapter;
	private int mCurrentPage = 1;
	private ArrayList<String> mColumnNames;
	private ArrayList<ArrayList<String>> mTableData = new ArrayList<ArrayList<String>>();
	private Long mTotalTableCount;
	protected int mSpinnerPosition;
	private ArrayList<Class<?>> mClassList;
	private String mSearch = "";
	private ArrayList<FieldType> mAllowedColumnInfo = new ArrayList<FieldType>();
	private int mSearchColumnPosition;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	// ------------------------------
	// LIFE CYCLE
	// ------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mClassList = new ArrayList<Class<?>>();
		mClassList.add(Episode.class);
		mClassList.add(Show.class);

		mSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.viewdatabase_multiline_spinner_item);

		for (Class<?> clazz : mClassList) {
			mSpinnerAdapter.add(clazz.getSimpleName());
		}

		mSpinner.setAdapter(mSpinnerAdapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mCurrentPage = 1;
				mSpinnerPosition = position;
				actualizeTableData(mSpinnerPosition, (mCurrentPage - 1) * PAGESIZE, PAGESIZE);
				initSearchSpinner();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		findViewById(R.id.btn_previousPage).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentPage > 1) {
					mCurrentPage--;
				}
				actualizeTableData(mSpinnerPosition, (mCurrentPage - 1) * PAGESIZE, PAGESIZE);
			}
		});

		findViewById(R.id.btn_nextPage).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentPage < mTotalTableCount / PAGESIZE + 1) {
					mCurrentPage++;
				}
				actualizeTableData(mSpinnerPosition, (mCurrentPage - 1) * PAGESIZE, PAGESIZE);
			}
		});

		mBtnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLayoutSearch.getVisibility() == View.VISIBLE) {
					mLayoutSearch.setVisibility(View.GONE);
				} else {
					mLayoutSearch.setVisibility(View.VISIBLE);
				}
			}
		});

		mEditTextSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mSearch = mEditTextSearch.getText().toString();
				actualizeTableData(mSpinnerPosition, (mCurrentPage - 1) * PAGESIZE, PAGESIZE);
			}

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		});

		mSpinnerSearch.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				android.util.Log.d("VGA", "clicked : " + mAllowedColumnInfo.get(position).getColumnName());
				mSearchColumnPosition = position;
				actualizeTableData(mSpinnerPosition, (mCurrentPage - 1) * PAGESIZE, PAGESIZE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}

		});
	}

	private void actualizeTableData(int position, long start, long offset) {
		mTableData.clear();

		Class<?> clazz = mClassList.get(position);
		mTotalTableCount = getTableCount(clazz);
		mColumnNames = getColomnNames(clazz);
		mTableData = getStringArrayDataFromDB(clazz, start, offset);

		buildTableLayoutTY();
		actualizeRecapPageField();
	}

	private void initSearchSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.viewdatabase_multiline_spinner_item);
		for (FieldType columnName : mAllowedColumnInfo) {
			adapter.add(columnName.getColumnName());
		}
		((Spinner) findViewById(R.id.spinner_search_column)).setAdapter(adapter);
	}

	protected void actualizeRecapPageField() {
		((TextView) findViewById(R.id.tv_pageRecap)).setText("Page " + mCurrentPage + " / " + (int) (mTotalTableCount / PAGESIZE + 1) + ", il y a "
				+ mTotalTableCount + " enregistrements dans cette table");
	}

	private <E> void buildTableLayoutTY() {

		mTL.removeAllViews();

		TableRow firstTr = new TableRow(this);
		firstTr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		for (int i = -1; i < mColumnNames.size(); i++) {
			TextView tv = new TextView(this);
			if (i == -1) {
				tv.setText("#");
			} else {
				tv.setText(mColumnNames.get(i));
			}
			tv.setTextColor(getResources().getColor(R.color.blanc));
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(2, 0, 2, 0);

			firstTr.addView(tv);
		}
		firstTr.setBackgroundResource(R.color.holo_blue_light);
		mTL.addView(firstTr);

		for (int i = 0; i < mTableData.size(); i++) {
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			if (i % 2 == 0) {
				tr.setBackgroundResource(R.color.blanc);
			} else {
				tr.setBackgroundResource(R.color.gris);
			}

			for (int j = -1; j < mTableData.get(i).size(); j++) {

				TextView tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				if (j == -1) {
					tv.setText(String.valueOf((mCurrentPage - 1) * PAGESIZE + i + 1)); // numero de l'enregistrement
				} else {
					tv.setText(mTableData.get(i).get(j));
				}
				tv.setTextColor(getResources().getColor(R.color.noir));
				tv.setPadding(2, 0, 2, 0);

				tr.addView(tv);
			}

			mTL.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		}
	}

	private <E> ArrayList<ArrayList<String>> getStringArrayDataFromDB(Class<E> clazz, long offset, long limit) {
		try {
			BaseDaoImpl<E, ?> daoBase;
			daoBase = mDatabaseHelper.getDao(clazz);
			TableInfo<E, ?> daoInfo = daoBase.getTableInfo();

			QueryBuilder<E, ?> qb = daoBase.queryBuilder();

			try {
				if (mSearch.length() > 0) {
					if ("String".equals(mAllowedColumnInfo.get(mSearchColumnPosition).getType().getSimpleName())) {
						qb.where().like(mAllowedColumnInfo.get(mSearchColumnPosition).getColumnName(), "%" + mSearch + "%");
					}
					if ("boolean".equals(mAllowedColumnInfo.get(mSearchColumnPosition).getType().getSimpleName())) {
						if ("false".startsWith(mSearch) || "0".equals(mSearch)) {
							qb.where().eq(mAllowedColumnInfo.get(mSearchColumnPosition).getColumnName(), false);
						}
						if ("true".startsWith(mSearch) || "1".equals(mSearch)) {
							qb.where().eq(mAllowedColumnInfo.get(mSearchColumnPosition).getColumnName(), true);
						}
					}
					if ("int".equals(mAllowedColumnInfo.get(mSearchColumnPosition).getType().getSimpleName())) {
						qb.where().eq(mAllowedColumnInfo.get(mSearchColumnPosition).getColumnName(), Integer.parseInt(mSearch));
					}
				}
			} catch (Exception e) {
				// Fail > no search
			}

			qb.offset(offset);
			qb.limit(limit);
			List<E> dbData = daoBase.query((PreparedQuery<E>) qb.prepare());

			ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();

			FieldType[] var1 = daoInfo.getFieldTypes();

			for (Object obj : dbData) {
				ArrayList<String> tmpArrayList = new ArrayList<String>();
				for (FieldType field : var1) {
					tmpArrayList.add(String.valueOf(field.extractJavaFieldValue(obj)));
				}
				res.add(tmpArrayList);
			}

			return res;

		} catch (SQLException e) {
			Ln.e("SQL Error : ", e);
			return null;
		}
	}

	private <E> ArrayList<String> getColomnNames(Class<E> clazz) {
		try {
			ArrayList<String> res = new ArrayList<String>();
			BaseDaoImpl<E, ?> baseDao = mDatabaseHelper.getDao(clazz);
			TableInfo<E, ?> daoInfo = baseDao.getTableInfo();
			FieldType[] fieldType = daoInfo.getFieldTypes();

			mAllowedColumnInfo.clear();

			for (FieldType field : fieldType) {
				res.add(field.getColumnName());

				for (int i = 0; i < ALLOWED_TYPE_FOR_SEARCH.length; i++) {
					if (ALLOWED_TYPE_FOR_SEARCH[i].equals(field.getType().getSimpleName())) {
						mAllowedColumnInfo.add(field);
					}
				}
			}
			return res;
		} catch (SQLException e) {
			Ln.e("SQL Error : ", e);
			return null;
		}
	}

	private <E> Long getTableCount(Class<E> clazz) {
		Long res = null;
		try {
			BaseDaoImpl<E, ?> baseDao = mDatabaseHelper.getDao(clazz);
			res = baseDao.countOf();
		} catch (SQLException e) {
			Ln.e("SQL Error : ", e);
		}
		return res;
	}
}
