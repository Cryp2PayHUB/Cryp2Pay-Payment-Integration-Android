package com.cryp2pay.cryp2payintegration;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {

    public static final String BASE_API_URL = "http://43.204.35.41/";
    public static final String LOGIN_ENDPOINT = BASE_API_URL + "api/login/";
    public static final String REGISTER_ENDPOINT = BASE_API_URL + "api/register/";
    public static final String VERIFY_PHONE_ENDPOINT = BASE_API_URL + "api/varify_number/";
    public static final String GET_WALLET_INFO_ENDPOINT = BASE_API_URL + "api/wallets/";
    public static final String TRANSACTION_HISTORY_ENDPOINT = BASE_API_URL + "api/wallets/transactions/";
    public static final String TOP_UP_WALLET_ENDPOINT = BASE_API_URL + "topup/";
    public static final String USER_TRANSFER_ENDPOINT = BASE_API_URL + "api/wallets/transfer/";
    public static final String MERCHANT_TRANSFER_ENDPOINT = BASE_API_URL + "merchant/transfer/";
    public static final String LOGOUT_ENDPOINT = BASE_API_URL + "api/logout/";
    public static final String WITHDRAW_COINS_ENDPOINT = BASE_API_URL + "api/wallets/withdraw/";
    public static final String AADHAR_URL_ENDPOINT = BASE_API_URL + "api/varify_customer_document/";
    public static final String IS_VERIFIED_ENDPOINT = BASE_API_URL  + "api/is_varified/";
    public static int VERIFIED_STATE;

    public static String returnCoinName(String coin){
        String finalName = "";
        switch (coin.toLowerCase(Locale.ROOT)){
            case "btc":
            case "bitcoin":
                finalName = "BTC";
                break;
            case "algo":
            case "algorand":
                finalName = "ALGO";
                break;
            case "doge":
            case "dogecoin":
                finalName = "DOGE";
                break;
            case "trx":
            case "tron":
                finalName = "TRX";
                break;
            case "ripple":
            case "xrp":
                finalName = "XRP";
                break;
            case "stellar":
            case "xlm":
                finalName = "XLM";
                break;
            case "dgb":
            case "digibyte":
                finalName = "DGB";
                break;
            case "zil":
            case "zilliqa":
                finalName = "ZIL";
                break;
        }
        return finalName;
    }

    public static void setCoinLogo(ImageView imageView, String coin){
        switch (coin){
            case "BTC":
//                imageView.setImageResource(R.drawable.bitcoin);
                Glide.with(imageView.getContext()).load(R.drawable.bitcoin)
                        .centerCrop()
                        .into(imageView);
                break;
            case "ALGO":
//                imageView.setImageResource(R.drawable.algorandlogo);
                Glide.with(imageView.getContext()).load(R.drawable.algorandlogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "ZIL":
//                imageView.setImageResource(R.drawable.zillogo);
                Glide.with(imageView.getContext()).load(R.drawable.zillogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "DOGE":
//                imageView.setImageResource(R.drawable.dogecoinlogo);
                Glide.with(imageView.getContext()).load(R.drawable.dogecoinlogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "DGB":
//                imageView.setImageResource(R.drawable.digibytelogo);
                Glide.with(imageView.getContext()).load(R.drawable.digibytelogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "TRX":
//                imageView.setImageResource(R.drawable.trxlogo);
                Glide.with(imageView.getContext()).load(R.drawable.trxlogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "XLM":
//                imageView.setImageResource(R.drawable.xlmlogo);
                Glide.with(imageView.getContext()).load(R.drawable.xlmlogo)
                        .centerCrop()
                        .into(imageView);
                break;
            case "XRP":
//                imageView.setImageResource(R.drawable.xrplogo);
                Glide.with(imageView.getContext()).load(R.drawable.xrplogo)
                        .centerCrop()
                        .into(imageView);
                break;
        }
    }

    public static String ReturnCoinValue(String coin, JSONObject response){
        String returnVal = null;
        try {
            JSONObject jsonObject = response.getJSONObject(coin);
            Object obj = jsonObject.get("value");
            if (obj instanceof String){
                returnVal = jsonObject.getString("value");
            }
            else if (obj instanceof Double){
                double val = jsonObject.getDouble("value");
                returnVal = Double.toString(val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }


    public static int IsUserVerified(Context context, String token){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, IS_VERIFIED_ENDPOINT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String getState = "";
                try {
                    getState = response.getString("state");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    switch (getState){
//                        case "-2":
//                            VERIFIED_STATE = -2;
//                            break;
//                        case "-1":
//                            VERIFIED_STATE = -1;
//                            break;
//                        case "0":
//                            VERIFIED_STATE = 0;
//                            break;
//                        case "1":
//                            VERIFIED_STATE = 1;
//                            break;
//                        default:
//                            VERIFIED_STATE = 2;
//                    }

                    if (getState.equals("-2")){
                        VERIFIED_STATE = -2;
                    }
                    else if (getState.equals("-1")){
                        VERIFIED_STATE = -1;
                    }
                    else if (getState.equals("0")){
                        VERIFIED_STATE = 0;
                    }
                    else if (getState.equals("1")){
                        VERIFIED_STATE = 1;
                    }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
        return VERIFIED_STATE;
    }

}
