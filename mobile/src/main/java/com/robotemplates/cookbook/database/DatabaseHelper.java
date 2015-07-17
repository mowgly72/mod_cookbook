package com.robotemplates.cookbook.database;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.CookbookConfig;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.database.model.IngredientModel;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.utility.Logcat;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
	private static final String DATABASE_NAME = CookbookConfig.DATABASE_NAME;
	private static final String DATABASE_PATH = "/data/data/" + CookbookApplication.getContext().getPackageName() + "/databases/";
	private static final int DATABASE_VERSION = CookbookConfig.DATABASE_VERSION;
	private static final String PREFS_KEY_DATABASE_VERSION = "database_version";

	private Dao<CategoryModel, Long> mCategoryDao = null;
	private Dao<RecipeModel, Long> mRecipeDao = null;
	private Dao<IngredientModel, Long> mIngredientDao = null;


	// singleton
	private static DatabaseHelper instance;
	public static synchronized DatabaseHelper getInstance()
	{
		if(instance==null) instance = new DatabaseHelper();
		return instance;
	}


	private DatabaseHelper()
	{
		super(CookbookApplication.getContext(), DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
		if(!databaseExists() || DATABASE_VERSION>getVersion())
		{
			synchronized(this)
			{
				boolean success = copyPrepopulatedDatabase();
				if(success)
				{
					setVersion(DATABASE_VERSION);
				}
			}
		}
	}


	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try
		{
			Logcat.d("DatabaseHelper.onCreate()");
			//TableUtils.createTable(connectionSource, CategoryModel.class);
			//TableUtils.createTable(connectionSource, RecipeModel.class);
			//TableUtils.createTable(connectionSource, IngredientModel.class);
		}
		catch(android.database.SQLException e)
		{
			Logcat.e("DatabaseHelper.onCreate(): can't create database", e);
			e.printStackTrace();
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try
		{
			Logcat.d("DatabaseHelper.onUpgrade()");
		}
		catch(android.database.SQLException e)
		{
			Logcat.e("DatabaseHelper.onUpgrade(): can't upgrade database", e);
			e.printStackTrace();
		}
	}


	@Override
	public void close()
	{
		super.close();
		mCategoryDao = null;
		mRecipeDao = null;
		mIngredientDao = null;
	}


	public synchronized void clearDatabase()
	{
		try
		{
			Logcat.d("DatabaseHelper.clearDatabase()");

			TableUtils.dropTable(getConnectionSource(), CategoryModel.class, true);
			TableUtils.dropTable(getConnectionSource(), RecipeModel.class, true);
			TableUtils.dropTable(getConnectionSource(), IngredientModel.class, true);

			TableUtils.createTable(getConnectionSource(), CategoryModel.class);
			TableUtils.createTable(getConnectionSource(), RecipeModel.class);
			TableUtils.createTable(getConnectionSource(), IngredientModel.class);
		}
		catch(android.database.SQLException e)
		{
			Logcat.e("DatabaseHelper.clearDatabase(): can't clear database", e);
			e.printStackTrace();
		}
		catch(java.sql.SQLException e)
		{
			Logcat.e("DatabaseHelper.clearDatabase(): can't clear database", e);
			e.printStackTrace();
		}
	}


	public synchronized Dao<CategoryModel, Long> getCategoryDao() throws java.sql.SQLException
	{
		if(mCategoryDao==null)
		{
			mCategoryDao = getDao(CategoryModel.class);
		}
		return mCategoryDao;
	}


	public synchronized Dao<RecipeModel, Long> getRecipeDao() throws java.sql.SQLException
	{
		if(mRecipeDao==null)
		{
			mRecipeDao = getDao(RecipeModel.class);
		}
		return mRecipeDao;
	}


	public synchronized Dao<IngredientModel, Long> getIngredientDao() throws java.sql.SQLException
	{
		if(mIngredientDao==null)
		{
			mIngredientDao = getDao(IngredientModel.class);
		}
		return mIngredientDao;
	}


	private boolean databaseExists()
	{
		File file = new File(DATABASE_PATH + DATABASE_NAME);
		boolean exists = file.exists();
		Logcat.d("DatabaseHelper.databaseExists(): " + exists);
		return exists;
	}


	private boolean copyPrepopulatedDatabase()
	{
		// copy database from assets
		try
		{
			// create directories
			File dir = new File(DATABASE_PATH);
			dir.mkdirs();

			// output file name
			String outputFileName = DATABASE_PATH + DATABASE_NAME;
			Logcat.d("DatabaseHelper.copyDatabase(): " + outputFileName);

			// create streams
			InputStream inputStream = CookbookApplication.getContext().getAssets().open(DATABASE_NAME);
			OutputStream outputStream = new FileOutputStream(outputFileName);

			// write input to output
			byte[] buffer = new byte[1024];
			int length;
			while((length = inputStream.read(buffer))>0)
			{
				outputStream.write(buffer, 0, length);
			}

			// close streams
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}


	private int getVersion()
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CookbookApplication.getContext());
		return sharedPreferences.getInt(PREFS_KEY_DATABASE_VERSION, 0);
	}


	private void setVersion(int version)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CookbookApplication.getContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(PREFS_KEY_DATABASE_VERSION, version);
		editor.commit();
	}
}
