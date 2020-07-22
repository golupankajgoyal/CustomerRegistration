package com.example.customerregistration;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private ItemData details;

    @BindView(R.id.detail_name)
    TextView name_tv;

    @BindView(R.id.detail_address)
    TextView address_tv;

    @BindView(R.id.detail_addhar_no)
    TextView addhar_tv;

    @BindView(R.id.mobile_no_tv)
    TextView mobile_tv;

    @BindView(R.id.employee_id_tv)
    TextView employee_tv;

    @BindView(R.id.detail_addhar_front)
    ImageView addharFront_iv;

    @BindView(R.id.detail_addhar_back)
    ImageView addharBack_iv;

    @BindView(R.id.avatar)
    ImageView photo_iv;

    @BindView(R.id.detail_document_image)
    ImageView document_iv;

    @BindView(R.id.detail_customer_welfare_id)
    TextView welfareId;

    @BindView(R.id.detail_scroll_view)
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(getIntent().getExtras() != null) {
            details = getIntent().getParcelableExtra("customer");
        }


//        test=findViewById(R.id.detail_name);
//        test.setText(details.getName());
        name_tv.setText(details.getName());
        address_tv.setText(details.getAddress());
        addhar_tv.setText(details.getAddhar());
        mobile_tv.setText(details.getMobile());
        employee_tv.setText(details.getEmployeeId());
        welfareId.setText(details.getWefareId());
        Glide.with(this)
                .load(details.getAddharFront())
                .placeholder(R.drawable.id_card)
                .circleCrop()
                .into(addharFront_iv);

        Glide.with(this)
                .load(details.getAddharBack())
                .placeholder(R.drawable.id_card)
                .circleCrop()
                .into(addharBack_iv);

        Glide.with(this)
                .load(details.getImageUrl())
                .placeholder(R.drawable.user)
                .circleCrop()
                .into(photo_iv);

        Glide.with(this)
                .load(details.getDocument())
                .placeholder(R.drawable.file)
                .circleCrop()
                .into(document_iv);


        // check if we should used curved motion and load an appropriate transition

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.curve));
        }


    }



    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        final int startScroolPos=
                getResources().getDimensionPixelSize(R.dimen.init_scroll_up_distance);
        Animator animator= ObjectAnimator.ofInt(
                mScrollView,
                "scrollY",startScroolPos
        ).setDuration(500);
        animator.start();
    }

    }