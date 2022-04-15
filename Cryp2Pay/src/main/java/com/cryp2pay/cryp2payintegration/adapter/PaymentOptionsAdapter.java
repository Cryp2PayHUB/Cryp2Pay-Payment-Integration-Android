package com.cryp2pay.cryp2payintegration.adapter;

import static com.cryp2pay.cryp2payintegration.Utils.setCoinLogo;
import static com.cryp2pay.cryp2payintegration.Utils.setCoinLogo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cryp2pay.cryp2payintegration.Cryp2Pay;
import com.cryp2pay.cryp2payintegration.R;
import com.cryp2pay.cryp2payintegration.model.WalletModel;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentOptionsAdapter extends RecyclerView.Adapter<PaymentOptionsAdapter.PaymentOptionsViewHolder> {

    private final WalletModel[] model;
    private final Activity activity;
//    private final String name, id, phone_number, user_type, email;
    private final String token, merchantID, phone, name;
    private double amount;
    private Class resultActivity;
    private ProgressDialog dialog;

    public PaymentOptionsAdapter(WalletModel[] model, Activity activity, Class secondActivity, double amount, String token, String merchantID, String phone, String nameOfMerchant, ProgressDialog dialog){
        this.model = model;
        this.activity = activity;
//        this.name = name;
//        this.id = id;
//        this.phone_number = phone_number;
//        this.user_type = user_type;
//        this.email = email;
        this.name = nameOfMerchant;
        this.phone = phone;
        this.token = token;
        this.amount = amount;
        this.merchantID = merchantID;
        this.resultActivity = secondActivity;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public PaymentOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_paymentoptions, parent, false);
        return new PaymentOptionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentOptionsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final WalletModel myModel = model[position];
        holder.name.setText(myModel.getCoin());
        if (myModel.getCoin().equals("Bitcoin")){
            holder.amount.setText(myModel.getValue());
        }
        else {
            holder.amount.setText(String.format("%.4f", Double.parseDouble(myModel.getValue())));
        }
        holder.inr.setText(myModel.getInr());
//        holder.imgCV.setTransitionName("trans_image" + position);
        setCoinLogo(holder.coinImg, returnCoinName(myModel.getCoin()));
        dialog.dismiss();
        setAnimation(holder.cardView);
        setAnimation(holder.coinImg);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(holder.cardView.getContext(), PaymentProceedActivity.class);
//                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.imgCV,
//                        "trans_image" + position);
//                intent.putExtra("trans_image", "trans_image" + position);
//                holder.cardView.getContext().startActivity(intent, optionsCompat.toBundle());

                    processVal(returnCoinName(myModel.getCoin()), myModel.getValue(), String.valueOf(amount));


            }
        });
    }

    @Override
    public int getItemCount() {
        return model.length;
    }

    public class PaymentOptionsViewHolder extends RecyclerView.ViewHolder {
        final TextView name, amount, inr;
        final ImageView coinImg;
        final MaterialCardView cardView;
        public PaymentOptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.coinNameOptions);
            amount = itemView.findViewById(R.id.coinValOptions);
            inr = itemView.findViewById(R.id.coinInrOptions);
            coinImg = itemView.findViewById(R.id.coinLogoOptions);
            cardView = itemView.findViewById(R.id.payment_option_cv);

        }
    }


    private void setAnimation(View view){
        Animation fall_down = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
        view.startAnimation(fall_down);
    }

    private String returnCoinName(String coin){
        String finalName = "";
        switch (coin){
            case "Bitcoin":
                finalName = "BTC";
                break;
            case "Algorand":
                finalName = "ALGO";
                break;
            case "DogeCoin":
                finalName = "DOGE";
                break;
            case "Tron":
                finalName = "TRX";
                break;
            case "XRP":
                finalName = "XRP";
                break;
            case "XLM":
                finalName = "XLM";
                break;
            case "Digibyte":
                finalName = "DGB";
                break;
            case "Zilliqa":
                finalName = "ZIL";
                break;
        }
        return finalName;
    }

    private void processVal(String coin,String amount, String enteredAmount){
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency="+ coin +"&to_currency=INR&apikey=ZEOHYLMZ8WIMTUAJ&fbclid=IwAR22vEckEjEfp9e-KTDl_VsNacjPbMX8PdCyuWspQvM7c0rUou8uoJwaO80", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("Realtime Currency Exchange Rate");
                            String getVal = object.getString("5. Exchange Rate");
                            Toast.makeText(activity, getVal + coin, Toast.LENGTH_SHORT).show();
                            double finalValue = Double.parseDouble(enteredAmount) / Double.parseDouble(getVal);
                            Toast.makeText(activity, finalValue + "", Toast.LENGTH_SHORT).show();
                            if (finalValue > Double.parseDouble(amount)){
                                Toast.makeText(activity, "Insufficient funds", Toast.LENGTH_SHORT).show();
                            }
                            else {
//                                Log.d("myapp", merchantID + "adapter");
                                Cryp2Pay cryp2Pay = new Cryp2Pay(activity, resultActivity, phone, merchantID) ;
                                cryp2Pay.paymentProcessUI(activity, coin, enteredAmount, String.valueOf(finalValue), token, merchantID, phone, name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
