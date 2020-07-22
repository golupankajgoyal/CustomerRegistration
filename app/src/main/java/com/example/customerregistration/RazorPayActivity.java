package com.example.customerregistration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {


    @BindView(R.id.pay_button)
    Button payButton;

    @BindView(R.id.ed_amount)
    TextInputEditText edAmount;

    @BindView(R.id.profile_photo)
    ImageView profileImage;

    @BindView(R.id.name_tv)
    TextView nameTv;

    @BindView(R.id.id_tv)
    TextView idTv;

    @BindView(R.id.balance_tv_razorpay)
    TextView balanceTv;

    @BindView(R.id.main_container)
    ConstraintLayout mainLayout;

    String amount = null;
    String mobile= null;
    String balance=null;
    String imageUrl=null;
    String Id=null;
    String name=null;
    private ArrayList<ItemData> customersList;
    private ProgressDialog loading;
    private Context context=this;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_razor_pay);
        ButterKnife.bind(this);
        mainLayout.setVisibility(View.INVISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(getIntent().getExtras() != null){
            if(getIntent().getStringExtra("intentId")!="walletIntent"){
                mobile= getIntent().getStringExtra("mobile");
            }

            Id=getIntent().getStringExtra("customerId");
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = edAmount.getText().toString().trim();
                startPayment(amount);
            }
        });

        getItems();
    }


    public void startPayment(String amount) {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Customer Registration");
            options.put("description", "Add Money in Wallet");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");

            double total = Double.parseDouble(amount);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("contact", mobile);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPaymentSuccess(String s) {

        Log.e("onPaymentSuccess ", "-->" + s);
        Toast.makeText(RazorPayActivity.this,"Payment Successful"+s,Toast.LENGTH_SHORT);
        updateItemToSheet();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.e("error", "-->" + i);
        Log.e("error", "-->" + s);
        finish();
    }

    public void updateItemToSheet() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbw6lqynZdpWevCLJPB6rq47C0CMwQDAyZW5AbIf97Kf0P1luLob/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Toast.makeText(RazorPayActivity.this, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RazorPayActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RazorPayActivity.this, "Error in data loading", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                parmas.put("action","update");
                parmas.put("welFareId",Id);
                parmas.put("money",amount);
                return parmas;
            }
        };

        int socketTimeOut = 10000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }

    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://script.google.com/macros/s/AKfycbw6lqynZdpWevCLJPB6rq47C0CMwQDAyZW5AbIf97Kf0P1luLob/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Error in data loading",Toast.LENGTH_SHORT).show();
                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {


        customersList = new ArrayList<>();
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);
                String mWelfareId = jo.getString("customerWelfareId").trim();

                if(mWelfareId.equals(Id)){
                    name = jo.getString("name");
                   imageUrl = jo.getString("image").trim();
                   balance = jo.getString("balance").trim();
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainLayout.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.user)
                .circleCrop()
                .into(profileImage);
        nameTv.setText(name);
        idTv.setText(Id);
        balanceTv.setText(balance);
        loading.dismiss();

    }
}