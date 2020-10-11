package com.digiflying.customerregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletActivtiy extends AppCompatActivity {

    @BindView(R.id.ed_customer_Id)
    TextInputEditText customerId;

    @BindView(R.id.balance_tv)
    TextView balanceTv;

    @BindView(R.id.add_balance_image)
    ImageView addImageView;

    @BindView(R.id.check_balance_image)
    ImageView checkImageView;

    @BindView(R.id.customer_id_container)
    TextInputLayout customerIdInputlayout;

    @BindView(R.id.balance_container)
    LinearLayout balanceLinearLayout;



    private String Id=null;
    private String Balance=null;
    private Context context = this;
    private ProgressDialog loading;
    private  int flag=0;
    private int check=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_activtiy);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Id=customerId.getText().toString().trim();
                check=1;
                searchItemToSheet();

            }
        });

        checkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Id=customerId.getText().toString().trim();
                check=0;
                if(flag==0){
                    searchItemToSheet();
                }

            }
        });
    }

    public void searchItemToSheet() {

        loading = ProgressDialog.show(this, "On progress", "Please wait");


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbw6lqynZdpWevCLJPB6rq47C0CMwQDAyZW5AbIf97Kf0P1luLob/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItem(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error in data loading", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("action", "search");
                parmas.put("welFareId",Id);
                return parmas;
            }
        };

        int socketTimeOut = 10000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);
    }



    public void startPayment() {

        Intent intent=new Intent(WalletActivtiy.this,RazorPayActivity.class);
        intent.putExtra("intentId","walletIntent");
        intent.putExtra("customerId",Id);
        startActivity(intent);
    }

    private void parseItem(String jsonResposnce) {


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            Balance = jobj.getString("money").trim();

        } catch (JSONException e) {
            e.printStackTrace();
        }




        if(Balance.equals("id not found")){
            customerId.setError("Invalid Customer Id.");
        }else if(check==0){
            customerId.setVisibility(View.GONE);
            customerIdInputlayout.setVisibility(View.GONE);
            flag=1;
            balanceLinearLayout.setVisibility(View.VISIBLE);
            balanceTv.setText(Balance);
        }else if(check==1){
            startPayment();
        }

        loading.dismiss();
    }
}