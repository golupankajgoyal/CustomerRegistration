package com.example.customerregistration;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddItemActivity extends AppCompatActivity {


    @BindView(R.id.ed_username)
    TextInputEditText name;

    @BindView(R.id.ed_address)
    TextInputEditText address;

    @BindView(R.id.ed_addhar)
    TextInputEditText addhar;

    @BindView(R.id.ed_mobile)
    TextInputEditText mobile;

    @BindView(R.id.photo)
    ImageView photo;

    @BindView(R.id.image_tv)
    TextView img_tv;

    @BindView(R.id.document)
    ImageView document;

    @BindView(R.id.document_tv)
    TextView doc_tv;

    @BindView(R.id.addhar_front_image)
    ImageView addharFrontImage;

    @BindView(R.id.addhar_front_tv)
    TextView addharFrontTv;

    @BindView(R.id.addhar_back_image)
    ImageView addharBackImage;

    @BindView(R.id.addhar_back_tv)
    TextView addharBackTv;

    @BindView(R.id.ed_employee_id)
    TextInputEditText employeeId;

    private Context context = this;
    private ProgressDialog loading;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int PICK_DOCUMENT_REQUEST = 2;
    public static final int PICK_ADDHAR_FRONT_REQUEST = 3;
    public static final int PICK_ADDHAR_BACK_REQUEST = 4;
    private Uri mImageUri;
    private String mStringImageUrl;
    private String mStringDocumentUrl;
    private String mStringAddharFrontUrl;
    private String mStringAddharBackUrl;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mImageUploadTask;
    private StorageTask mDocumentUploadTask;
    private String stringCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        img_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openFileChooser(v);
            }
        });

        doc_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openFileChooser(v);
            }
        });

        addharFrontTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
            }
        });

        addharBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
            }
        });

    }


    @OnClick(R.id.txt_save)
    public void onViewClicked() {
        if (validation()) {
            addItemToSheet();
        }
    }


    public void addItemToSheet() {

        loading = ProgressDialog.show(this, "Adding Item", "Please wait");
        final String mName = name.getText().toString().trim();
        final String mAddress = address.getText().toString().trim();
        final String mAddhar = addhar.getText().toString().trim();
        final String mMobile = mobile.getText().toString().trim();
        final String mEmployeeId= employeeId.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbw6lqynZdpWevCLJPB6rq47C0CMwQDAyZW5AbIf97Kf0P1luLob/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(AddItemActivity.this, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddItemActivity.this, RazorPayActivity.class);
                        intent.putExtra("mobile",mMobile);
                        intent.putExtra("customerId",stringCustomerId);
                        startActivity(intent);

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

                long customerId = (long) (Math.random()* 10000000000000000L);

                stringCustomerId = String.valueOf(customerId);
                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("name", mName);
                parmas.put("address", mAddress);
                parmas.put("addhar", mAddhar);
                parmas.put("mobile", mMobile);
                parmas.put("image",mStringImageUrl);
                parmas.put("document",mStringDocumentUrl);
                parmas.put("employeeId",mEmployeeId);
                parmas.put("addharFront",mStringAddharBackUrl);
                parmas.put("addharBack",mStringAddharFrontUrl);
                parmas.put("welFareId",stringCustomerId);
                return parmas;
            }
        };

        int socketTimeOut = 10000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(int identity) {
        if (mImageUri != null) {
            loading = ProgressDialog.show(this, "Adding Item", "Please wait");
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Handler handler=new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//                                }
//                            },5000);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri=uri;
                                    if(identity==PICK_ADDHAR_FRONT_REQUEST){
                                        mStringAddharFrontUrl=downloadUri.toString().trim();
                                        addharFrontTv.setVisibility(View.GONE);
                                    }else if (identity==PICK_ADDHAR_BACK_REQUEST){
                                        mStringAddharBackUrl=downloadUri.toString().trim();
                                        addharBackTv.setVisibility(View.GONE);
                                    }else  if (identity==PICK_IMAGE_REQUEST){
                                        mStringImageUrl=downloadUri.toString().trim();
                                        img_tv.setVisibility(View.GONE);
                                    }else if (identity==PICK_DOCUMENT_REQUEST){
                                        mStringDocumentUrl=downloadUri.toString().trim();
                                        doc_tv.setVisibility(View.GONE);
                                    }
                                    Upload upload = new Upload(uri.toString(), "Enter Anything");
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                            loading.dismiss();
                            Toast.makeText(AddItemActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                    for showing progress
//                    double progress=(100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount( ));
//                    mProgressBar.setProgress((int)progress);
                        }
                    });
        } else {
            Toast.makeText(AddItemActivity.this, "Image Url is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (view.getId() == R.id.image_tv) {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else if (view.getId() == R.id.document_tv) {
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
        } else if (view.getId() == R.id.addhar_front_tv) {
            startActivityForResult(intent, PICK_ADDHAR_FRONT_REQUEST);
        } else if (view.getId() == R.id.addhar_back_tv) {
            startActivityForResult(intent, PICK_ADDHAR_BACK_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Toast.makeText(AddItemActivity.this, "Photo Selected", Toast.LENGTH_SHORT).show();
            mImageUri = data.getData();

            Glide.with(this)
                    .load(mImageUri)
                    .into(photo);
            uploadFile(PICK_IMAGE_REQUEST);
        } else if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Toast.makeText(AddItemActivity.this, "Document Selected", Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(mImageUri)
                    .into(document);
            uploadFile(PICK_DOCUMENT_REQUEST);
        } else if(requestCode == PICK_ADDHAR_FRONT_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            Toast.makeText(AddItemActivity.this, "Aadhar Front Selected", Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(mImageUri)
                    .into(addharFrontImage);
            uploadFile(PICK_ADDHAR_FRONT_REQUEST);

        }else if(requestCode == PICK_ADDHAR_BACK_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            Toast.makeText(AddItemActivity.this, "Aadhar Back Selected", Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(mImageUri)
                    .into(addharBackImage);
            uploadFile(PICK_ADDHAR_BACK_REQUEST);
        }
    }

    public boolean validation() {



        if (name.getText().toString().isEmpty()) {
            name.setError("Enter Name");
            return false;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError("Enter Address");
            return false;
        }
        if (addhar.getText().toString().isEmpty()) {
            addhar.setError("Enter Aadhar No.");
            return false;
        }

        if (mobile.getText().toString().isEmpty() || !validatePhoneNumber(mobile.getText().toString()) ) {
            mobile.setError("Enter Mobile No.");
            return false;
        }
        if (mStringImageUrl==null) {
            Toast.makeText(AddItemActivity.this,"Upload Photo",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mStringDocumentUrl==null) {
            Toast.makeText(AddItemActivity.this,"Upload Document", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mStringAddharBackUrl==null) {
            Toast.makeText(AddItemActivity.this,"Upload Aadhar Back Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mStringAddharFrontUrl==null) {
            Toast.makeText(AddItemActivity.this,"Upload Aadhar Front Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }
}