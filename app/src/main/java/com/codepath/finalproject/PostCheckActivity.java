package com.codepath.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/11/17.
 */

public class PostCheckActivity extends AppCompatActivity {

    TextView tvTextBody;
    TextBody textBody;
    String message;
    String subject;
    String recipient;
    Button btSend;
    Button btEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);

        //stores info in intent for sending back to MainActivity
        message = getIntent().getStringExtra("message");
        recipient = getIntent().getStringExtra("to");
        subject = getIntent().getStringExtra("subject");

        //makes a Textbody with the user's message
        textBody = new TextBody();
        textBody.setMessage(message);

        //gets the textbody's score
        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);
        client.getStyleScores(textBody);
        client.getSocialScores(textBody);

        //sets the message on the activity
        tvTextBody = (TextView) findViewById(R.id.tvTextBody);
        tvTextBody.setText(message);
        tvTextBody.setTextColor(Color.parseColor(textBody.getTextColor()));

        //Code for tabs below

        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", textBody);
        adapter.addFrag(new StylesFragment(), "Styles", textBody);
        adapter.addFrag(new SocialFragment(), "Social", textBody);

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

        initializeViews();
        setOnClickListeners();
    }

    public void sendEmail(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        //for only emails
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(PostCheckActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnClickListeners(){

        btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostCheckActivity.this, MainActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("subject", subject);
                intent.putExtra("recipient", recipient);
                PostCheckActivity.this.startActivity(intent);
            }
        });
    }

    public void initializeViews(){
        btSend = (Button) findViewById(R.id.btSend);
        btEdit = (Button) findViewById(R.id.btEdit);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFrag(Fragment fragment, String title, TextBody textBody) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("textBody", textBody);
                fragment.setArguments(bundle);
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
    }
}

