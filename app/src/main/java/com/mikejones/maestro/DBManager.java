package com.mikejones.maestro;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBManager {

    private static final String TAG = "DBManager";

    private static DBManager manager = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private DBManager(){
    }

    public static DBManager getInstance(){
        if(manager == null){
            manager = new DBManager();
        }
        return manager;
    }

    public void createUser(String email, String name, String role, final ISignable signable){
        Map<String, Object> user = new HashMap<>();
        user.put(DBConstants.USER_EMAIL, email);
        user.put(DBConstants.USERNAME, name);
        user.put(DBConstants.USER_ROLE, role);
        db.collection(DBConstants.USERS_TABLE).document(auth.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + auth.getUid());
                        signable.signIn();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "failed to created User table for "+auth.getUid());
                signable.failedSignIn("failed to created User table entry: "+e.getLocalizedMessage());
            }
        });

    }

    public void getUser(String id, final IUpdateable updateable){
        db.collection(DBConstants.USERS_TABLE).document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        User user = new User(document.getString(DBConstants.USER_EMAIL),
                                document.getString(DBConstants.USERNAME),
                                document.getString(DBConstants.USER_ROLE));
                        updateable.updateUserData(user);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}

