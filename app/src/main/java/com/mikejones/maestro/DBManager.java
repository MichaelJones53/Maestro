package com.mikejones.maestro;

import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DBManager {

    private static final String TAG = "DBManager";

    private static DBManager manager = null;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private DBManager(){
    }

    public static DBManager getInstance(){
        if(manager == null){
            manager = new DBManager();
        }
        return manager;
    }
    public static void getAsset(String path, final DataListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child(path);

        ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                listener.onDataSucceeded(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void getPost(String classId, String postId, final DataListener listener){
        db.collection(DBConstants.CLASSROOMS_TABLE).document(classId).collection(DBConstants.POSTS_TABLE).document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                Post p = new Post();
                p.setPostId(snapshot.getId());
                if(snapshot.contains(DBConstants.POST_TITLE)){
                    p.setPostTitle(snapshot.getString(DBConstants.POST_TITLE));
                }
                if(snapshot.contains(DBConstants.POST_AUTHOR)){
                    p.setAuthorName(snapshot.getString(DBConstants.POST_AUTHOR));
                }
                if(snapshot.contains(DBConstants.POST_TIMESTAMP)){
                    p.setTimestamp(snapshot.getString(DBConstants.POST_TIMESTAMP));
                }

                if(snapshot.contains(DBConstants.POST_IMAGE_RUL)){
                    p.setImageURL(snapshot.getString(DBConstants.POST_IMAGE_RUL));
                }
                if(snapshot.contains(DBConstants.POST_AUDIO_URL)){
                    p.setAudioURL(snapshot.getString(DBConstants.POST_AUDIO_URL));
                }
                if(snapshot.contains(DBConstants.POST_TEXT)){
                    p.setText(snapshot.getString(DBConstants.POST_TEXT));
                }

                listener.onDataSucceeded(p);
            }
        });

    }
    public void getCommentsForPost(String postId, String classId, final DataListener listener){
        db.collection(DBConstants.CLASSROOMS_TABLE).document(classId).collection(DBConstants.POSTS_TABLE)
                .document(postId).collection(DBConstants.POST_COMMENTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Post> posts = new ArrayList<>();
                    for(DocumentSnapshot snapshot: task.getResult().getDocuments()) {

                        Post p = new Post();
                        p.setPostId(snapshot.getId());
                        p.setPostTitle(null);

                        if(snapshot.contains(DBConstants.POST_IMAGE_RUL)){
                            p.setImageURL(snapshot.getString(DBConstants.POST_IMAGE_RUL));
                        }
                        if(snapshot.contains(DBConstants.POST_AUDIO_URL)){
                            p.setAudioURL(snapshot.getString(DBConstants.POST_AUDIO_URL));
                        }
                        if(snapshot.contains(DBConstants.POST_TEXT)){
                            p.setText(snapshot.getString(DBConstants.POST_TEXT));
                        }

                        if(snapshot.contains(DBConstants.POST_AUTHOR)){
                            p.setAuthorName(snapshot.getString(DBConstants.POST_AUTHOR));
                        }
                        if(snapshot.contains(DBConstants.POST_TIMESTAMP)){
                            p.setTimestamp(snapshot.getString(DBConstants.POST_TIMESTAMP));
                        }
                        posts.add(p);
                    }

                    listener.onDataSucceeded(posts);
                }
            }
        });

    }

    public void getPosts(String classId, final DataListener listener){
        db.collection(DBConstants.CLASSROOMS_TABLE).document(classId).collection(DBConstants.POSTS_TABLE).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Post> posts = new ArrayList<>();
                            for(DocumentSnapshot snapshot: task.getResult().getDocuments()) {

                                Post p = new Post();
                                p.setPostId(snapshot.getId());
                                if(snapshot.contains(DBConstants.POST_TITLE)){
                                    p.setPostTitle(snapshot.getString(DBConstants.POST_TITLE));
                                }
                                if(snapshot.contains(DBConstants.POST_AUTHOR)){
                                    p.setAuthorName(snapshot.getString(DBConstants.POST_AUTHOR));
                                }
                                if(snapshot.contains(DBConstants.POST_TIMESTAMP)){
                                    p.setTimestamp(snapshot.getString(DBConstants.POST_TIMESTAMP));
                                }
                                posts.add(p);
                            }

                            listener.onDataSucceeded(posts);
                        }
                    }
                });


    }

    private Task addCommentImageToCloudstore(Bitmap imageBitmap, final String classId, final String postId, final String commentId){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageStorage = storage.getReference().child(DBConstants.IMAGES_PATH+"/"+ UUID.randomUUID()+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask uploadImage = imageStorage.putBytes(imageData);
        uploadImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                db.collection(DBConstants.CLASSROOMS_TABLE).document(classId)
                        .collection(DBConstants.POSTS_TABLE).document(postId)
                        .collection(DBConstants.POST_COMMENTS).document(commentId)
                        .update(DBConstants.POST_IMAGE_RUL, imageStorage.getPath()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DBMANGER: ","post succedded");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DBMANGER: ",e.getLocalizedMessage());
                    }
                });

            }
        });
        return uploadImage;
    }



    private void addImageToCloudstore(Bitmap imageBitmap, final String classId, final String postId){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageStorage = storage.getReference().child(DBConstants.IMAGES_PATH+"/"+ UUID.randomUUID()+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask uploadImage = imageStorage.putBytes(imageData);
        uploadImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                db.collection(DBConstants.CLASSROOMS_TABLE).document(classId)
                        .collection(DBConstants.POSTS_TABLE).document(postId)
                        .update(DBConstants.POST_IMAGE_RUL, imageStorage.getPath());
            }
        });
    }

    private void addAudioToCloudstore(String filePath, final String classId, final String postId){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference audioStorage = storage.getReference().child(DBConstants.AUDIO_PATH);

        Uri file = Uri.fromFile(new File(filePath));
        final StorageReference fileRef = audioStorage.child(UUID.randomUUID()+".3gp");
        UploadTask uploadTask = fileRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                db.collection(DBConstants.CLASSROOMS_TABLE).document(classId)
                        .collection(DBConstants.POSTS_TABLE).document(postId)
                        .update(DBConstants.POST_AUDIO_URL, fileRef.getPath());
            }
        });
    }

    public void addNewComment(final String classId, final String postId, String authorName,
                           String postText, final Bitmap imageBitmap,
                           final String audioPath , final DataListener dataListener){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        Map<String, Object> post = new HashMap<>();
        post.put(DBConstants.POST_TEXT, postText);
        post.put(DBConstants.POST_AUTHOR, authorName);
        post.put(DBConstants.POST_AUTHOR_ID, auth.getUid());
        post.put(DBConstants.POST_TIMESTAMP, new Date().toString());

        db.collection(DBConstants.CLASSROOMS_TABLE).document(classId).collection(DBConstants.POSTS_TABLE).document(postId)
                .collection(DBConstants.POST_COMMENTS)
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        List<Task<Object>> taskList = new ArrayList<>();

                        if(imageBitmap != null){
                            Task img = addCommentImageToCloudstore(imageBitmap, classId, postId, documentReference.getId());
                            taskList.add(img);
                        }
                        if(audioPath != null){
                          //  addCommentAudioToCloudstore(audioPath, classId, postId, documentReference.getId());
                        }
                        Tasks.whenAllSuccess(taskList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Object>> task) {
                                dataListener.onDataSucceeded(null);
                            }
                        });

                    }
                });
    }

    public void addNewPost(final String classId, String authorName,
                           String postTitle, String postText, final Bitmap imageBitmap,
                           final String audioPath , final DataListener dataListener){

        FirebaseStorage storage = FirebaseStorage.getInstance();


        Map<String, Object> post = new HashMap<>();
        post.put(DBConstants.POST_TITLE, postTitle);
        post.put(DBConstants.POST_TEXT, postText);
        post.put(DBConstants.POST_AUTHOR, authorName);
        post.put(DBConstants.POST_AUTHOR_ID, auth.getUid());
        post.put(DBConstants.POST_TIMESTAMP, new Date().toString());


        db.collection(DBConstants.CLASSROOMS_TABLE).document(classId).collection(DBConstants.POSTS_TABLE)
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if(imageBitmap != null){
                    addImageToCloudstore(imageBitmap, classId,documentReference.getId());
                }
                if(audioPath != null){
                    addAudioToCloudstore(audioPath, classId, documentReference.getId());
                }
                dataListener.onDataSucceeded(null);
            }
        });


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


    public interface DataListener{
        void onDataPrepared();
        void onDataSucceeded(Object o);
    }

}

