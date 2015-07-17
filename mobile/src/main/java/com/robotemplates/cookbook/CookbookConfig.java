package com.robotemplates.cookbook;


public class CookbookConfig
{
	// true for enabling debug logs, should be false in production release
	public static final boolean LOGS = false;

	// true for enabling Google Analytics, should be true in production release
	public static final boolean ANALYTICS = true;

	// true for enabling Google AdMob banner on recipe list screen, should be true in production release
	public static final boolean ADMOB_RECIPE_LIST_BANNER = true;

	// true for enabling Google AdMob on recipe detail screen, should be true in production release
	public static final boolean ADMOB_RECIPE_DETAIL_BANNER = true;

	// file name of the SQLite database, this file should be placed in assets folder
	public static final String DATABASE_NAME = "cookbook.db";

	// database version, should be incremented if database has been changed
	public static final int DATABASE_VERSION = 1;
}
