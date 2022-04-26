package com.cryp2pay.cryp2payintegration;

import static com.cryp2pay.cryp2payintegration.Utils.GET_WALLET_INFO_ENDPOINT;
import static com.cryp2pay.cryp2payintegration.Utils.LOGIN_ENDPOINT;
import static com.cryp2pay.cryp2payintegration.Utils.LOGOUT_ENDPOINT;
import static com.cryp2pay.cryp2payintegration.Utils.MERCHANT_TRANSFER_ENDPOINT;
import static com.cryp2pay.cryp2payintegration.Utils.ReturnCoinValue;
import static com.cryp2pay.cryp2payintegration.Utils.VERIFY_PHONE_ENDPOINT;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cryp2pay.cryp2payintegration.adapter.PaymentOptionsAdapter;
import com.cryp2pay.cryp2payintegration.model.BasicInfoModel;
import com.cryp2pay.cryp2payintegration.model.WalletModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Cryp2Pay {
    Activity activity;
    Class secondActivity;
    EditText loginMail, loginPassword;
    CircularProgressButton loginBtn;
    Bitmap checkMark, crossMark;
    String token, shopName, merchantID, phoneNumber, enteredPhone, userID, id;
    TextView shopNameView, amountView, transferAmount, inrAmount, toName, toNumber;
    TextView secureText;
    AppCompatButton pay, done;
    double amount;
    RecyclerView recyclerView;
    private String btc, algo, doge, dgb, xlm, xrp, trx, zil, eth, ltc, one, btt;
    PaymentOptionsAdapter adapter;
    LinearLayoutManager layoutManager;
    ImageView coinView, checkCircle;
    LinearLayout linearLayout, linearLayout2;
    Animation animFadeOut, animMoveUp, animFadeIn;
    String resTimeStamp, status, mid, mid1;
    ProgressDialog dialog;


    public Cryp2Pay(Activity activity,Class secondActivity, double amount, String phone, String mID){
        activity.setContentView(R.layout.login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.activity = activity;
        this.secondActivity = secondActivity;
        checkMark = BitmapFactory.decodeResource(activity.getResources(), R.drawable.checkmark);
        crossMark =  BitmapFactory.decodeResource(activity.getResources(), R.drawable.close);
        loginMail = activity.findViewById(R.id.loginEmail);
        loginPassword = activity.findViewById(R.id.emailPassword);
        loginBtn = activity.findViewById(R.id.loginNow);
        this.mid = mID;
        this.amount = amount;
        this.enteredPhone = phone;
        animFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        animFadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        animMoveUp = AnimationUtils.loadAnimation(activity, R.anim.move_up);
//        Log.d("myapp", mid + "main constructor");
        init();
    }


    public Cryp2Pay(Activity activity, Class resultActivity, String phone, String mid){
        this.activity = activity;
        this.secondActivity = resultActivity;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        VerifyNumber(phone);
        animFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        animFadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        animMoveUp = AnimationUtils.loadAnimation(activity, R.anim.move_up);
        this.mid1 = mid;
        this.phoneNumber = phone;
//        Log.d("myapp", mid +" " + mid1 + "second constructor");
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

    private void loadMainUI(){
        activity.setContentView(R.layout.mainui);
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Cryp2Pay Wallet");
        dialog.setMessage("Loading your wallet...");
        dialog.setCancelable(false);
        dialog.show();
        shopNameView = activity.findViewById(R.id.merchantShopName);
        amountView = activity.findViewById(R.id.transactionAmount);
        recyclerView = activity.findViewById(R.id.paymentReview);
//        shopNameView.setText(shopName);
        amountView.setText("Rs. " + amount);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        VerifyNumber(enteredPhone);
        LoadWallet();
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
                        try {
                            token = response.getString("token");
                            userID = response.getJSONObject("user_data").getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SuccessButtonAnim();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadMainUI();
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
                Log.d("myapp", body);
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

    private void VerifyNumber(String phone_number) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, VERIFY_PHONE_ENDPOINT, CreateJsonObject(phone_number),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            try {
                                merchantID = response.getString("id");
                                phoneNumber = response.getString("phone_number");
                                shopName = response.getString("name");
                                shopNameView.setText(shopName);
                            } catch (JSONException e) {
                                e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject CreateJsonObject(String phone_number){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", phone_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

    private void LoadWallet(){
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_WALLET_INFO_ENDPOINT  +
                userID + "/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    btc = ReturnCoinValue("BTC", response);
                    algo = ReturnCoinValue("ALGO", response);
                    doge = ReturnCoinValue("DOGE", response);
                    trx = ReturnCoinValue("TRX", response);
                    xrp = ReturnCoinValue("XRP", response);
                    xlm = ReturnCoinValue("XLM", response);
                    dgb = ReturnCoinValue("DGB", response);
                    zil = ReturnCoinValue("ZIL", response);
                    eth = ReturnCoinValue("ETH", response);
                    ltc = ReturnCoinValue("LTC", response);
                    one = ReturnCoinValue("ONE", response);
                    btt = ReturnCoinValue("BTT", response);



                    WalletModel[] walletModel = new WalletModel[]{
                            new WalletModel("Bitcoin",  btc, ""),
                            new WalletModel("Algorand",  algo, ""),
                            new WalletModel("DogeCoin",  doge, ""),
                            new WalletModel("Tron",  trx, ""),
                            new WalletModel("XRP", xrp, ""),
                            new WalletModel("XLM",  xlm, ""),
                            new WalletModel("Digibyte",  dgb, ""),
                            new WalletModel("Zilliqa", zil, ""),
                            new WalletModel("Ethereum", eth, ""),
                            new WalletModel("Litecoin", ltc, ""),
                            new WalletModel("Harmony", one, ""),
                            new WalletModel("BitTorrent", btt, ""),
                    };
//                    Log.d("myapp", merchantID + "merchant");
                    adapter = new PaymentOptionsAdapter(walletModel, activity, secondActivity, amount, token, mid, phoneNumber, shopName, dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);
                Log.d("myapp", token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
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


    public void paymentProcessUI(Activity activity1, String coin, String enteredAmount, String amount, String token1, String merchantID1, String phone1, String nameOfMerchant){
        activity1.setContentView(R.layout.payment_proceed);
        coinView = activity1.findViewById(R.id.coinLogoOptions);
        checkCircle = activity1.findViewById(R.id.checkCircle);
        transferAmount = activity1.findViewById(R.id.transferAmount);
        inrAmount = activity1.findViewById(R.id.inrAmount);
        toName = activity1.findViewById(R.id.toUserNameField);
        toNumber = activity1.findViewById(R.id.phoneNumberField);
        pay = activity1.findViewById(R.id.payButton);
        secureText = activity1.findViewById(R.id.secureText);
        done = activity1.findViewById(R.id.doneButton);
        linearLayout = activity1.findViewById(R.id.linearLayout1);
        linearLayout2 = activity1.findViewById(R.id.linearLayout2);

        Utils.setCoinLogo(coinView, coin);
        transferAmount.setText(String.format("%.2f", Double.parseDouble(amount))+ " " + coin);
        inrAmount.setText(String.format("%.2f", Double.parseDouble(enteredAmount)) + " Rs");
        toName.setText(nameOfMerchant);
        toNumber.setText(phone1);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToMerchant(activity1, coin, amount, enteredAmount, token1, merchantID1);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("success")){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("coin", Utils.returnCoinName(coin));
                        jsonObject.put("amount", amount);
                        jsonObject.put("inr", enteredAmount);
                        jsonObject.put("timestamp", resTimeStamp);
                        jsonObject.put("status", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(activity1, secondActivity);
                    intent.putExtra("result", jsonObject.toString());
                    activity1.startActivity(intent);
                    activity1.finish();
                }
            }
        });


    }


    private void SendToMerchant(Activity activity2, String coin, String amount, String inr, String token1, String merchantID1){
        RequestQueue requestQueue = Volley.newRequestQueue(activity2);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MERCHANT_TRANSFER_ENDPOINT, PrepareObjectToTransfer(coin, amount, inr,merchantID1), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    resTimeStamp = response.getString("Timestamp");
                    status = "success";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                secureText.setVisibility(View.GONE);
                startAnimation();
                linearLayout.setVisibility(View.GONE);
                coinView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkCircle.setVisibility(View.VISIBLE);
                        checkCircle.startAnimation(animMoveUp);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showNextView();
                                RequestQueue requestQueue = Volley.newRequestQueue(activity2);
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGOUT_ENDPOINT, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(activity2, "Logged Out!", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> hashMap = new HashMap<>();
                                        hashMap.put("token", token1);
                                        return hashMap;

                                    }

                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Authorization", "Token " + token1);
                                        return params;
                                    }
                                };
                                requestQueue.add(stringRequest);
                            }
                        }, 800);
                    }
                }, 800);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetError(error);
                Toast.makeText(activity2, error + "", Toast.LENGTH_SHORT).show();
                status = "false";
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token1);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject PrepareObjectToTransfer(String coin, String amount1, String inr, String merchantID1){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sent_to", mid1);
            jsonObject.put("coin_name", coin);
            jsonObject.put("amount", Double.parseDouble(amount1));
            jsonObject.put("inr", Double.parseDouble(inr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void startAnimation(){
        linearLayout.startAnimation(animFadeOut);
    }

    private void showNextView(){
        linearLayout2.startAnimation(animFadeIn);
        linearLayout2.setVisibility(View.VISIBLE);
    }

    private void LogOut(String token){
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGOUT_ENDPOINT, PostObject(token), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(activity, "Logged out!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GetError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject PostObject(String token){
        JSONObject jsonObject = new JSONObject();
        try {
//            Log.d("myapp", token + "logout");
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }






}
