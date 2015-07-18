package com.robotemplates.cookbook.activity;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.adapter.DrawerAdapter;
import com.robotemplates.cookbook.database.DBApp;
import com.robotemplates.cookbook.database.dao.CategoryDAO;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.extras.SharedPrefUtil;
import com.robotemplates.cookbook.fragment.RecipeListFragment;
import com.robotemplates.cookbook.listener.OnSearchListener;
import com.robotemplates.cookbook.logging.L;
import com.robotemplates.cookbook.network.VolleySingleton;
import com.robotemplates.cookbook.pojo.SubReddit;
import com.robotemplates.cookbook.services.ServiceSubReddit;
import com.robotemplates.cookbook.utility.ResourcesHelper;
import com.robotemplates.cookbook.view.DrawerDividerItemDecoration;
import com.robotemplates.cookbook.view.ScrimInsetsFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity implements DrawerAdapter.CategoryViewHolder.OnItemClickListener, OnSearchListener
{
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ScrimInsetsFrameLayout mDrawerScrimInsetsFrameLayout;
	private DrawerAdapter mDrawerAdapter;
	private JobScheduler mJobScheduler;

	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private List<CategoryModel> mCategoryList;
	private static final int JOB_ID = 100;
	private static final long POLL_FREQUENCY = 28800000;

    private String urlJsonObj = "http://nsfwapp-weyewe1.c9.io/api2/sub_reddits.json";
    private VolleySingleton volleySingleton;
    private ArrayList<SubReddit> subRedditArrayList;
    private ProgressDialog pDialog;


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


	public static Intent newIntent(Context context)
	{
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupActionBar();
		setupRecyclerView();
		setupDrawer(savedInstanceState);
        setupProgressDialog();
//        setup DB : fill in all the subReddits. On periodical update, we will only change the image.
        setupDB();
//		setupJob();



		// init analytics tracker
		((CookbookApplication) getApplication()).getTracker();
	}

    private void setupProgressDialog(){
        pDialog = new ProgressDialog(this );
        pDialog.setMessage("Setting up...");
        pDialog.setCancelable(false);
    }

    private void setupDB(){

        if(  CookbookApplication.getWritableDatabase().readMovies(1).size() == 0 ){
            Log.d("need_to_populate_DB", "true");
            loadNSFWData();

        }else{

            Log.d("need_to_populate_DB", "false");
        }


    }


    private void loadNSFWData(){
        showpDialog();



        JSONObject jsonBody = new JSONObject();
        JSONObject userLogin = new JSONObject();


        try {
            userLogin.put("email", "willy@gmail.com");
            userLogin.put("password", "willy1234");

            jsonBody.put("user_login",  userLogin );
//            jsonBody.put("password", "willy1234");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                urlJsonObj,
                jsonBody,
                createMyReqSuccessListener(),
                createMyReqErrorListener()
        ){
//            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", "w.yunnal@gmail.com");
//                params.put("password", "willy1234");
//                return params;
//            };
        };

        // Adding request to request queue
//        volleySingleton.getRequestQueue().add(jsonObjReq) ;
        VolleySingleton.getInstance().getRequestQueue().add(jsonObjReq);

//				addToRequestQueue(jsonObjReq);
//        CookbookApplication.getAppContext().addToRequestQueue(jsonObjReq);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("BOOM", response.toString());
                ArrayList<SubReddit> subRedditList = new ArrayList<SubReddit>();
                try {
//                    String auth_token = response.getString("auth_token");
//                    String email = response.getString("email");
                    JSONArray subRedditsArray = response.getJSONArray("sub_reddits");

                    for (int i = 0; i < subRedditsArray.length(); i++) {
                        JSONObject row = subRedditsArray.getJSONObject(i);
                        long server_id = row.getLong("id");
                        String name = row.getString("name");
                        String urlImage = row.getString("image_url");

                        String jsonElementText  = "\n";
                        jsonElementText += "ServerId: " + server_id + "\n\n";
                        jsonElementText += "AuthToken: " + name + "\n\n";
                        jsonElementText += "Email: " + urlImage + "\n\n";

//                        Log.d( "element " + i, jsonElementText);

                        SubReddit newObject= new SubReddit();
                        newObject.setId( server_id );
                        newObject.setName(name) ;
                        newObject.setUrlImage(urlImage);

                        subRedditList.add( newObject );

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                Log.d("elementxxx ", " >>> hahaha. Total size: " + subRedditList.size() );
                Log.d("theWritableDatabase: ", CookbookApplication.getWritableDatabase().toString());
                CookbookApplication.getWritableDatabase().insertSubReddits(DBApp.BOX_OFFICE, subRedditList, true);
                hidepDialog();

            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();
            }
        };
    }


    private void setupJob() {
		mJobScheduler = JobScheduler.getInstance(this);
		//set an initial delay with a Handler so that the data loading by the JobScheduler does not clash with the loading inside the Fragment
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//schedule the job after the delay has been elapsed
				buildJob();
			}
		}, 30000);
	}

	private void buildJob() {
		//attach the job ID and the name of the Service that will work in the background
		JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, ServiceSubReddit.class));
		//set periodic polling that needs net connection and works across device reboots
		builder.setPeriodic(POLL_FREQUENCY)
				.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
				.setPersisted(true);
		mJobScheduler.schedule(builder.build());
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();

		// analytics
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	
	@Override
	public void onStop()
	{
		super.onStop();

		// analytics
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// open or close the drawer if home button is pressed
		if(mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		// action bar menu behaviour
		switch(item.getItemId())
		{
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfiguration)
	{
		super.onConfigurationChanged(newConfiguration);
		mDrawerToggle.onConfigurationChanged(newConfiguration);
	}


	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}


	@Override
	public void onItemClick(View view, int position, long id, int viewType)
	{
		// position
		int categoryPosition = mDrawerAdapter.getCategoryPosition(position);
		selectDrawerItem(categoryPosition);
	}


	@Override
	public void onSearch(String query)
	{
		Fragment fragment = RecipeListFragment.newInstance(query);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.activity_main_container, fragment).commitAllowingStateLoss();

		mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(0));
		setTitle(getString(R.string.title_search) + ": " + query);
	}


	private void setupActionBar()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayUseLogoEnabled(false);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
	}


	private void setupRecyclerView()
	{
		// reference
		RecyclerView recyclerView = getRecyclerView();

		// set layout manager
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(linearLayoutManager);

		// load categories from database
		loadCategoryList();

		// set adapter
		if(recyclerView.getAdapter()==null)
		{
			// create adapter
			mDrawerAdapter = new DrawerAdapter(mCategoryList, this);
		}
		else
		{
			// refill adapter
			mDrawerAdapter.refill(mCategoryList, this);
		}
		recyclerView.setAdapter(mDrawerAdapter);

		// add decoration
		List<Integer> dividerPositions = new ArrayList<>();
		dividerPositions.add(3);
		RecyclerView.ItemDecoration itemDecoration = new DrawerDividerItemDecoration(
				this,
				null,
				dividerPositions,
				getResources().getDimensionPixelSize(R.dimen.global_spacing_xxs));
		recyclerView.addItemDecoration(itemDecoration);
	}


	private void setupDrawer(Bundle savedInstanceState)
	{
		mTitle = getTitle();
		mDrawerTitle = getTitle();

		// reference
		mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_layout);
		mDrawerScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.activity_main_drawer);

		// set drawer
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setStatusBarBackgroundColor(ResourcesHelper.getValueOfAttribute(this, R.attr.colorPrimaryDark));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// show initial fragment
		if(savedInstanceState == null)
		{
			selectDrawerItem(0);
		}
	}


	private void selectDrawerItem(int position)
	{
		Fragment fragment = RecipeListFragment.newInstance(mCategoryList.get(position).getId());
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.activity_main_container, fragment).commitAllowingStateLoss();

		mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(position));
		setTitle(mCategoryList.get(position).getName());
		mDrawerLayout.closeDrawer(mDrawerScrimInsetsFrameLayout);
	}


	private void loadCategoryList()
	{
		try
		{
			mCategoryList = CategoryDAO.readAll(-1l, -1l);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		CategoryModel all = new CategoryModel();
		all.setId(RecipeListFragment.CATEGORY_ID_ALL);
		all.setName(getResources().getString(R.string.drawer_category_all));
		all.setImage("drawable://" + R.drawable.ic_category_all);

		CategoryModel favorites = new CategoryModel();
		favorites.setId(RecipeListFragment.CATEGORY_ID_FAVORITES);
		favorites.setName(getResources().getString(R.string.drawer_category_favorites));
		favorites.setImage("drawable://" + R.drawable.ic_category_favorites);

		mCategoryList.add(0, all);
		mCategoryList.add(1, favorites);
	}


	private RecyclerView getRecyclerView()
	{
		return (RecyclerView) findViewById(R.id.activity_main_drawer_recycler);
	}
}
