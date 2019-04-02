package com.mikejones.maestro;

import com.google.firebase.firestore.FirebaseFirestore;

public class DBManager {
    private static DBManager manager = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DBManager(){
    }

    public static DBManager getInstance(){
        if(manager == null){
            manager = new DBManager();
        }
        return manager;
    }



}
