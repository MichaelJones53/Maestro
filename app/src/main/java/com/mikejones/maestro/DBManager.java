package com.mikejones.maestro;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void addStudent(String studentEmail, final String classId, final IStudentAddable studentAddable){
        db.collection(DBConstants.USERS_TABLE).whereEqualTo(DBConstants.USER_EMAIL, studentEmail.toLowerCase()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> results = task.getResult().getDocuments();

                    if(results.isEmpty()){
                        studentAddable.onStudentAddedFailed("Student not found...");
                    }else{
                        DocumentSnapshot ds = results.get(0);

                        if(ds.contains(DBConstants.CLASSES)){
                            List<String> uc = (List<String>) ds.get(DBConstants.CLASSES);
                            if(uc.contains(classId)){
                                studentAddable.onStudentAddedFailed("Student already in class...");
                                return;
                            }
                        }
                        db.collection(DBConstants.USERS_TABLE).document(results.get(0).getId()).update(DBConstants.CLASSES, FieldValue.arrayUnion(classId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    studentAddable.onStudentAdded();
                                }else{
                                    studentAddable.onStudentAddedFailed("Failed to add student to class. Please Try again....");
                                }
                            }
                        });



                    }

                }
            }
        });
    }


    public void createUser(String email, String name, String role, final ISignable signable){
        Map<String, Object> user = new HashMap<>();
        user.put(DBConstants.USER_EMAIL, email.toLowerCase());
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


    public void createClass(String className, String userName, final IClassCreateable classCreateable){
        Map<String, Object> data = new HashMap<>();
        data.put(DBConstants.CLASSNAME, className);
        data.put(DBConstants.PROFESSOR_NAME, userName);
        data.put(DBConstants.PROFESSOR, auth.getUid());


        db.collection(DBConstants.CLASSROOMS_TABLE).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                db.collection(DBConstants.USERS_TABLE).document(auth.getUid()).update(DBConstants.CLASSES, FieldValue.arrayUnion(documentReference.getId()));
                classCreateable.onClassCreated(documentReference.getId());

            }
        });


    }

    public void getClasses(final IClassCreateable classCreateable){
        final Task<DocumentSnapshot> userAccountTask = db.collection(DBConstants.USERS_TABLE).document(auth.getUid()).get();
        userAccountTask.continueWithTask(new Continuation<DocumentSnapshot, Task<List<DocumentSnapshot>>>() {
            @Override
            public Task<List<DocumentSnapshot>> then(@NonNull Task<DocumentSnapshot> task) throws Exception {

               List<Task<DocumentSnapshot>> cl = new ArrayList<>();

                ArrayList<String> classes = (ArrayList<String>) task.getResult().get(DBConstants.CLASSES);

                if(classes == null || classes.isEmpty()){
                    //cl.add(userAccountTask);
                    return Tasks.whenAllSuccess();
                }
                for(String c: classes){

                    cl.add(db.collection(DBConstants.CLASSROOMS_TABLE).document(c).get());
                }

                return Tasks.whenAllSuccess(cl);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                ArrayList<UserClass> classData = new ArrayList<UserClass>();

                //TODO: there is an issue here...
                if(task != null){
                    List<DocumentSnapshot> data = task.getResult();
                    for (DocumentSnapshot doc : data){
                        classData.add(new UserClass(doc.getString(DBConstants.PROFESSOR_NAME),
                                doc.getString(DBConstants.PROFESSOR),
                                doc.getId(),
                                doc.getString(DBConstants.CLASSNAME),
                                null,
                                null
                        ));

                    }
                }


                classCreateable.onUpdateClassList(classData);
            }
        });




    }



}

