package com.robotemplates.cookbook.database.dao;

import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.utility.Logcat;

import java.sql.SQLException;


public class DAO
{
	public static void printDatabaseInfo()
	{
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		try
		{
			Logcat.d("DAO.printDatabaseInfo(): categories " + databaseHelper.getCategoryDao().countOf());
			Logcat.d("DAO.printDatabaseInfo(): recipes " + databaseHelper.getRecipeDao().countOf());
			Logcat.d("DAO.printDatabaseInfo(): ingredients " + databaseHelper.getIngredientDao().countOf());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
