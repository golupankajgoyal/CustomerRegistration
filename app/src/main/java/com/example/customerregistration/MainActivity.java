package com.example.customerregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private ItemRecyclerViewAdapter  adapter;
    private ProgressDialog loading;
    private Context context=this;
    private ArrayList<ItemData> customersList;
    private MenuItem walletItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        getItems();

    }

    @OnClick(R.id.fab)
    public void onClick(){
        Intent intent = new Intent(context,AddItemActivity.class);
        startActivity(intent);
    }

    void filter(String text){
        ArrayList<ItemData> temp = new ArrayList();
        for(ItemData d: customersList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase()) || d.getMobile().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
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

                String mName = jo.getString("name");
                String mAddress = jo.getString("address");
                String mAddhar = jo.getString("addhar").trim();
                String mMobile = jo.getString("mobile").trim();
                String mEmployeeId = jo.getString("employeeId").trim();
                String mImageUrl = jo.getString("image").trim();
                String mDocument = jo.getString("document").trim();
                String mAddharFront = jo.getString("addharFront").trim();
                String mAddharBack = jo.getString("addharBack").trim();
                String mWelfareId = jo.getString("customerWelfareId").trim();

                ItemData customer=new ItemData(mName,mAddress,mAddhar,mMobile,mEmployeeId,mImageUrl,mDocument,mAddharFront,mAddharBack,mWelfareId);
                customersList.add(customer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter=new ItemRecyclerViewAdapter(context,customersList);
        recyclerView.setAdapter(adapter);
        loading.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem= menu.findItem(R.id.search);

        walletItem = menu.findItem(R.id.wallet);
        SearchView searchView=(SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toString());
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.wallet){
            Intent intent = new Intent(MainActivity.this,WalletActivtiy.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
