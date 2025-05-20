package com.example.myapplication.activitys.activitys;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.activitys.util.Constant;
import com.example.myapplication.activitys.util.PreferenseManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedIntanceState){

        super.onCreate(savedIntanceState);
        PreferenseManager preferenseManager = new PreferenseManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constant.KEY_COLLECTION_USER)
                .document(preferenseManager.getString(Constant.KEY_USER_ID));


    }

    @Override
    protected void onPause(){

        super.onPause();
        documentReference.update(Constant.KEY_AVAILABILITY,0);
    }

    @Override
    protected void onResume(){

        super.onResume();
        documentReference.update(Constant.KEY_AVAILABILITY,1);
    }

}
