package com.mikejones.maestro;


import android.content.DialogInterface;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements IClassCreateable {

    private FirebaseAuth mAuth;
    private RecyclerView mClassesListView;
    private RecyclerView mInvitesListView;
    private LinearLayout mInvitesLinearLayout;
    private FloatingActionButton mFloatingActionButton;
    private ArrayList<UserClass> mUserClasses = new ArrayList<>();
    private boolean isStudent = true;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();

        mClassesListView = findViewById(R.id.classesListView);
        mFloatingActionButton = findViewById(R.id.dashboardFloatingActionButton);
        mProgressBar = findViewById(R.id.dashboardProgressBar);
        setupScreenForRole();


        mClassesListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mClassesListView.setAdapter(new SelectorListAdapter(this, mUserClasses));
        mClassesListView.addItemDecoration(new DividerItemDecoration(mClassesListView.getContext(), DividerItemDecoration.VERTICAL));


        if(!isStudent) {


            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("CREATE CLASS");
                    View v = LayoutInflater.from(DashboardActivity.this).inflate(R.layout.class_dialog_layout, null);
                    final TextInputEditText classNameEditText = v.findViewById(R.id.classNameEditText);
                    builder.setView(v);
                    builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String className = classNameEditText.getText().toString();

                            if (className == null || className.length() < 5) {
                                Toast.makeText(DashboardActivity.this, "Class name must be at least 5 characters", Toast.LENGTH_SHORT).show();
                            } else {
                                //TODO: create class
                                PrefManager pf = new PrefManager(DashboardActivity.this);
                                showSpinner();
                                DBManager.getInstance().createClass(className, pf.getUsername(),DashboardActivity.this);


                            }
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }


        showSpinner();
        DBManager.getInstance().getClasses(DashboardActivity.this);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.accountButton) {
            // do something here
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClassCreated(String classId) {
        hideSpinner();
        DBManager.getInstance().getClasses(DashboardActivity.this);

    }

    @Override
    public void onUpdateClassList(ArrayList<UserClass> classes) {
        Log.d("updateclass", "updateClassCalled");
        hideSpinner();
        for(UserClass c: classes){
            boolean exists = false;
            for( UserClass u: mUserClasses){
                if(u.getClassId().equals(c.getClassId())){
                    exists = true;
                }
            }
            if(!exists){
                mUserClasses.add(c);
            }
        }

        mClassesListView.getAdapter().notifyDataSetChanged();
    }

    private void setupScreenForRole() {
        PrefManager pm = new PrefManager(this);

        if (pm.getRole().equals("Student")) {
            isStudent = true;
            mFloatingActionButton.hide();
        }else{
            isStudent = false;
        }

    }
    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
    }
}
