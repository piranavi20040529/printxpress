package com.example.printxpress.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {

    private static final String DATABASE_URL = "https://printxpress-66267-default-rtdb.firebaseio.com";

    private static FirebaseDatabase mDatabase;

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseDatabase getDatabaseInstance() {
        if (mDatabase == null) {

            mDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
            mDatabase.setPersistenceEnabled(false);
        }
        return mDatabase;
    }

    public static DatabaseReference getDatabase() {
        return getDatabaseInstance().getReference();
    }

    public static DatabaseReference getDbRef() {
        return getDatabase();
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    public static StorageReference getStorage() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getAuth().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public static boolean isLoggedIn() {
        return getAuth().getCurrentUser() != null;
    }

    public static DatabaseReference getUsersRef() {
        return getDatabase().child("Users");
    }

    public static DatabaseReference getOrdersRef() {
        return getDatabase().child("Orders");
    }

    public static void logout() {
        getAuth().signOut();
    }
}
