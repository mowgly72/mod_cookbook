package com.robotemplates.cookbook.activity;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.network.VolleySingleton;
import com.robotemplates.cookbook.pojo.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageActivityFragment extends Fragment {

    private String urlJsonObj = "http://nsfwapp-weyewe1.c9.io/api2/images.json";
    private long selectedSubRedditId;
    private ProgressDialog pDialog;
    private List<Image> mRecipeList = new ArrayList<>();

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }





    private void setupProgressDialog(){
        pDialog = new ProgressDialog(this.getActivity());
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
                        String url = row.getString("url");

                        String jsonElementText  = "\n";
                        jsonElementText += "image url: " + url + "\n\n";

                        Log.d( "element " + i, jsonElementText);

                        Image newObject= new Image();
                        newObject.setUrl(url);

                        mRecipeList.add( newObject );

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( getActivity().getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

//                mRecipeList = CookbookApplication.getWritableDatabase().readMovies(1)

                hidepDialog();
//                renderView();

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


    public ImageActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setupProgressDialog();
        loadNSFWData();

        return inflater.inflate(R.layout.fragment_image2, container, false);


    }
}
