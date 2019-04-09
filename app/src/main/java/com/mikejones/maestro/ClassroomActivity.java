package com.mikejones.maestro;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ClassroomActivity extends AppCompatActivity implements IStudentAddable{

    private TextView mClassTextView;
    private RecyclerView mPostsRecyclerView;
    private FloatingActionButton mAddStudentFAB;
    private FloatingActionButton mAddPostFAB;
    private ProgressBar mProgressBar;
    private boolean isStudent = false;

    private String mClassName;
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Bundle extas = getIntent().getExtras();
        mClassName = extas.getString("className");
        mClassId = extas.getString("classId");

        mClassTextView = findViewById(R.id.classroomNameTextView);
        mProgressBar = findViewById(R.id.classroomProgressBar);
        mAddStudentFAB = findViewById(R.id.classroomAddStudentFloatingActionButton);

        mAddPostFAB = findViewById(R.id.classroomAddTopicFAB);
        mPostsRecyclerView = findViewById(R.id.classroomTopicList);



        mClassTextView.setText(mClassName.toUpperCase());

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
                            Toast.makeText(ClassroomActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
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
                i.putExtra("className", mClassId);
                i.putExtra("classId", mClassId);
                startActivity(i);

            }
        });


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
}
