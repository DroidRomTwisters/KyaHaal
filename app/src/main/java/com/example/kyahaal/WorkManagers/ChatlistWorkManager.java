package com.example.kyahaal.WorkManagers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kyahaal.database.DataBaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ChatlistWorkManager extends Worker {
    FirebaseAuth mAuth;
    DatabaseReference mRootref,mChatref;
    DataBaseHelper myDb;
    Context context;
    public ChatlistWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super ( context, workerParams );
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.success ();
    }

}
