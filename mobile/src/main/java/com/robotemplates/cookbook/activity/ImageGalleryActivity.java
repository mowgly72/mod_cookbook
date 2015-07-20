package com.robotemplates.cookbook.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.CookbookConfig;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.adapter.ImageListAdapter;
import com.robotemplates.cookbook.adapter.RecipeListAdapter;
import com.robotemplates.cookbook.database.DBApp;
import com.robotemplates.cookbook.database.query.Query;
import com.robotemplates.cookbook.database.query.RecipeReadAllQuery;
import com.robotemplates.cookbook.database.query.RecipeReadByCategoryQuery;
import com.robotemplates.cookbook.database.query.RecipeReadFavoritesQuery;
import com.robotemplates.cookbook.database.query.RecipeSearchQuery;
import com.robotemplates.cookbook.network.VolleySingleton;
import com.robotemplates.cookbook.pojo.Image;
import com.robotemplates.cookbook.pojo.SubReddit;
import com.robotemplates.cookbook.utility.NetworkManager;
import com.robotemplates.cookbook.view.GridSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends ActionBarActivity implements  ImageListAdapter.ImageViewHolder.OnItemClickListener {

    private static final int LAZY_LOADING_TAKE = 128;
    private static final int LAZY_LOADING_OFFSET = 4;
    private boolean mLazyLoading = false;
    private ImageListAdapter mAdapter;

    private String urlJsonObj = "http://nsfwapp-weyewe1.c9.io/api2/images.json";

    private List<Image> mRecipeList = new ArrayList<>();
    private List<Object> mFooterList = new ArrayList<>();

    private long selectedSubRedditId;
    private ProgressDialog pDialog;


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedSubRedditId = extras.getLong("SUB_REDDIT_ID");


            Toast.makeText( getApplicationContext(), "fhe sub_reddit_id: " + selectedSubRedditId  ,
                    Toast.LENGTH_LONG).show();
        }



        setupProgressDialog();
        setupRecyclerView();
        Log.d(">>>>>>>>> beforeLoad:", "the length of data: " + mRecipeList.size());

        loadNSFWData();








    }

    private void setupProgressDialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
    }

    private void loadNSFWData(){
        Log.d(">>>>> tracer", "gonna load nsfwdata");
        showpDialog();



        JSONObject jsonBody = new JSONObject();
        JSONObject userLogin = new JSONObject();

        String targetImageUrl = urlJsonObj  + "?parent_id=" + selectedSubRedditId ;
        Log.d(">>>> bkaboom", targetImageUrl);

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
                targetImageUrl,
                jsonBody,
                createMyReqSuccessListener(),
                createMyReqErrorListener()
        ){

        };

        Log.d(">>>>> bkaboom", "adding request to queue");
        VolleySingleton.getInstance().getRequestQueue().add(jsonObjReq);
        Log.d(">>>>> bkaboom", "AFTER adding request to queue");

    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {


        Log.d(">>>>>>> bkaboom", "inside the success listener");
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

//                mRecipeList
                Log.d(">>>>>> BOOM", response.toString());
                try {
//                    String auth_token = response.getString("auth_token");
//                    String email = response.getString("email");
                    JSONArray subRedditsArray = response.getJSONArray("images");

                    for (int i = 0; i < subRedditsArray.length(); i++) {
                        JSONObject row = subRedditsArray.getJSONObject(i);
                        String url = row.getString("main_url");

                        String jsonElementText  = "\n";
                        jsonElementText += "image url: " + url + "\n\n";

                        Log.d( "element " + i, jsonElementText);

                        Image newObject= new Image();
                        newObject.setUrl(url);

                        mRecipeList.add( newObject );

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

//                mRecipeList = CookbookApplication.getWritableDatabase().readMovies(1)

                hidepDialog();
                renderView();

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


    private void renderView()
    {
        // reference
        final RecyclerView recyclerView = getRecyclerView();
        final AdView adView = (AdView) this.findViewById(R.id.fragment_recipe_list_adview);

        // content
        if(recyclerView.getAdapter()==null)
        {
            Log.d("TRACER", "no adapter yet");
            Log.d("TRACER", "The length of recipeList: " + mRecipeList.size());
            // create adapter
            mAdapter = new ImageListAdapter(mRecipeList, mFooterList, this, getGridSpanCount());
        }
        else
        {
            // refill adapter
            mAdapter.refill(mRecipeList, mFooterList, this, getGridSpanCount());
        }

        // set fixed size
        recyclerView.setHasFixedSize(false);

        // add decoration
        RecyclerView.ItemDecoration itemDecoration = new GridSpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.fragment_recipe_list_recycler_item_padding));
        recyclerView.addItemDecoration(itemDecoration);

        // set animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // set adapter
        recyclerView.setAdapter(mAdapter);

        // lazy loading
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int THRESHOLD = 100;

            private int mCounter = 0;
            private Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // reset counter
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mCounter = 0;
                }

                // disable item animation in adapter
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mAdapter.setAnimationEnabled(false);
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = firstVisibleItem + visibleItemCount;

                // lazy loading
                if (totalItemCount - lastVisibleItem <= LAZY_LOADING_OFFSET && mRecipeList.size() % LAZY_LOADING_TAKE == 0 && !mRecipeList.isEmpty()) {
//                    if (!mLazyLoading) lazyLoadData();
                }

            }
        });




        // admob
//        if(CookbookConfig.ADMOB_RECIPE_LIST_BANNER && NetworkManager.isOnline( this ))
//        {
//            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                    .addTestDevice(getString(R.string.admob_test_device_id))
//                    .build();
//            adView.loadAd(adRequest);
//            adView.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            adView.setVisibility(View.GONE);
//        }
    }


//
//    private void lazyLoadData()
//    {
//        // show lazy loading progress
//        showLazyLoadingProgress(true);
//
//        // run async task
//        Query query;
//        if(mCategoryId==CATEGORY_ID_ALL)
//        {
//            query = new RecipeReadAllQuery(mRecipeList.size(), LAZY_LOADING_TAKE);
//        }
//        else if(mCategoryId==CATEGORY_ID_FAVORITES)
//        {
//            query = new RecipeReadFavoritesQuery(mRecipeList.size(), LAZY_LOADING_TAKE);
//        }
//        else if(mCategoryId==CATEGORY_ID_SEARCH)
//        {
//            query = new RecipeSearchQuery(mSearchQuery, mRecipeList.size(), LAZY_LOADING_TAKE);
//        }
//        else
//        {
//            query = new RecipeReadByCategoryQuery(mCategoryId, mRecipeList.size(), LAZY_LOADING_TAKE);
//        }
//        mDatabaseCallManager.executeTask(query, this);
//    }


    private void showLazyLoadingProgress(boolean visible)
    {
        if(visible)
        {
            mLazyLoading = true;

            // show footer
            if(mFooterList.size()<=0)
            {
                mFooterList.add(new Object());
                mAdapter.notifyItemInserted(mAdapter.getRecyclerPositionByFooter(0));
            }
        }
        else
        {
            // hide footer
            if(mFooterList.size()>0)
            {
                mFooterList.remove(0);
                mAdapter.notifyItemRemoved(mAdapter.getRecyclerPositionByFooter(0));
            }

            mLazyLoading = false;
        }
    }



    private RecyclerView getRecyclerView()
    {
        return (RecyclerView) findViewById(R.id.fragment_recipe_list_recycler);
    }

    private void setupRecyclerView()
    {
        Log.d(">>> RV", "ahaha");
        GridLayoutManager gridLayoutManager = new GridLayoutManager( this, getGridSpanCount());
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(gridLayoutManager);

//
//        mAdapter = new ImageListAdapter(mRecipeList, mFooterList, this, getGridSpanCount());
//
//        Log.d(">>> Adapter", mAdapter.toString() );
//        recyclerView.setAdapter( mAdapter);
    }

    private int getGridSpanCount()
    {
        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = getResources().getDimension(R.dimen.fragment_recipe_list_recycler_item_size);
        return Math.round(screenWidth / cellWidth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position, long id, int viewType) {
        int recipePosition = mAdapter.getRecipePosition(position);

//		Logcat.d("I am clicked. position: " + position);

//
        Toast.makeText( getApplicationContext(),  "fhe url  == " + position,
                Toast.LENGTH_LONG).show();
    }
}
