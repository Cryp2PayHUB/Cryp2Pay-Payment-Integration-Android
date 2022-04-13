package com.cryp2pay.cryp2payintegration;

import static com.cryp2pay.cryp2payintegration.Utils.LOGIN_ENDPOINT;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

abstract public class Cryp2Pay {
    Activity activity;
    EditText loginMail, loginPassword;
    CircularProgressButton loginBtn;
    Bitmap checkMark, crossMark;

    public Cryp2Pay(Activity activity){
        activity.setContentView(R.layout.login);
        this.activity = activity;
        checkMark = BitmapFactory.decodeResource(activity.getResources(), R.drawable.checkmark);
        crossMark =  BitmapFactory.decodeResource(activity.getResources(), R.drawable.close);
        loginMail = activity.findViewById(R.id.loginEmail);
        loginPassword = activity.findViewById(R.id.emailPassword);
        loginBtn = activity.findViewById(R.id.loginNow);
        init();
    }



    private void init(){

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.startAnimation();
                if (loginPassword.getText().toString().isEmpty() || loginMail.getText().toString().isEmpty()){
                    ErrorButtonAnim();
                    Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    LogIn();
                }
            }
        });
    }

    private void LogIn(){
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_ENDPOINT, CreateJsonObj()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getJSONObject("user_data").getString("user_type").equals("M")){
                        ErrorButtonAnim();
                        Toast.makeText(activity, "Please do not login with merchant's login id.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SuccessButtonAnim();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.setContentView(R.layout.mainui);
                            }
                        }, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorButtonAnim();
                GetError(error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject CreateJsonObj() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", loginMail.getText().toString().trim());
            jsonObject.put("username", loginMail.getText().toString().trim());
            jsonObject.put("password", loginPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void GetError(VolleyError error){
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the reuest has either time out or there is no connection
            Log.d("myapp", "No connection");
            Toast.makeText(activity, "No connection", Toast.LENGTH_SHORT).show();

        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            Log.d("myapp", "Auth Failure");
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            Log.d("myapp", "Server Error");
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                if (body != null){
                    try {
                        JSONObject object = new JSONObject(body);
                        JSONArray errorName = object.getJSONArray("non_field_errors");
                        Toast.makeText(activity, errorName.getString(0), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            Log.d("myapp", "Network error");

        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            Log.d("myapp", "Parse error");
        }
    }

    private void SuccessButtonAnim(){
       loginBtn.doneLoadingAnimation(Color.parseColor("#28D453"), checkMark);
        changeToOriginalState();
    }

    private void ErrorButtonAnim(){
        loginBtn.doneLoadingAnimation(Color.parseColor("#BF3131"), crossMark);
        changeToOriginalState();
    }

    private void changeToOriginalState(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginBtn.revertAnimation();
            }
        }, 2000);
    }

}
