package com.mikejones.maestro;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ClassroomActivity extends AppCompatActivity implements IStudentAddable{

    private TextView mClassTextView;
    private RecyclerView mPostsRecyclerView;
    private FloatingActionButton mAddStudentFAB;
    private FloatingActionButton mAddPostFAB;
    private ProgressBar mProgressBar;
    private ArrayList<Post> mPostList = new ArrayList<>();
    private boolean isStudent = false;

    private String mClassName;
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Bundle extas = getIntent().getExtras();
        mClassName = extas.getString("singleClassroomName");
        mClassId = extas.getString("singleClassroomId");

        mClassTextView = findViewById(R.id.classroomNameTextView);
        mProgressBar = findViewById(R.id.classroomProgressBar);
        mAddStudentFAB = findViewById(R.id.classroomAddStudentFloatingActionButton);

        mAddPostFAB = findViewById(R.id.classroomAddTopicFAB);
        mPostsRecyclerView = findViewById(R.id.classroomTopicList);
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPostsRecyclerView.setAdapter(new PostListAdapter(this, mClassId, mPostList));
        mPostsRecyclerView.addItemDecoration(new DividerItemDecoration(mPostsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mClassTextView.setText(mClassName.toUpperCase());

        if(!checkPermission()){
            requestPermission();
        }else{
            setupScreen();
        }





    }

    private void setupScreen(){
        setupScreenForRole();

        mAddStudentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClassroomActivity.this);

                builder.setTitle("ADD STUDENT");
                View v = LayoutInflater.from(ClassroomActivity.this).inflate(R.layout.add_student_dialog_layout, null);
                final TextInputEditText studentEmailEditText = v.findViewById(R.id.studentEmailEditText);
                builder.setView(v);
                builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String studentEmail = studentEmailEditText.getText().toString();

                        if (!Validator.isValidEmail(studentEmail)) {
                            showError("Invalid email address");
                        } else {
                            //TODO: create class
                            showSpinner();
                            DBManager.getInstance().addStudent(studentEmail, mClassId ,ClassroomActivity.this);


                        }
                        dialogInterface.cancel();
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

        mAddPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClassroomActivity.this, CreatePostActivity.class);
                i.putExtra("classroomName", mClassName.toUpperCase());
                i.putExtra("classroomId", mClassId);
                startActivityForResult(i, 12);


            }
        });

        showSpinner();
        updateDataList();
    }

    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupScreenForRole() {
        PrefManager pm = new PrefManager(this);

        if (pm.getRole().equals("Student")) {
            isStudent = true;
            mAddStudentFAB.hide();
        }

    }

    private void updateDataList(){
        DBManager.getInstance().getPosts(mClassId, new DBManager.DataListener() {
            @Override
            public void onDataPrepared() {

            }

            @Override
            public void onDataSucceeded(Object o) {
                ArrayList<Post> postData = (ArrayList<Post>) o;
                for(Post p: postData){
                    boolean exists = false;
                    for(Post z: mPostList){
                        if(z.getPostId().equals(p.getPostId())){
                            exists = true;
                        }
                    }
                    if(!exists){
                        mPostList.add(p);
                    }
                }
                Collections.sort(mPostList);
                mPostsRecyclerView.getAdapter().notifyDataSetChanged();
                hideSpinner();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode == RESULT_OK){
            showSpinner();
            updateDataList();

        }
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

    private void showError(String message){

        AlertDialog.Builder dialog = new AlertDialog.Builder(ClassroomActivity.this);
        dialog.setTitle("MESSAGE");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int id) {
                di.cancel();
            }
        });
        AlertDialog d = dialog.create();
        d.show();
    }

    @Override
    public void onStudentAdded() {
        hideSpinner();
        showError("student added");
    }

    @Override
    public void onStudentAddedFailed(String message) {
        hideSpinner();
        showError(message);

    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result2 =  ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ClassroomActivity.this, new
                String[]{RECORD_AUDIO, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        setupScreen();
    }
}
