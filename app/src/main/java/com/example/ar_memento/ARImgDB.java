package com.example.ar_memento;

import android.content.Context;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Session;

import java.io.IOException;
import java.io.InputStream;

public class ARImgDB {
    // Constructor
//    public ARImgDB(Context context) {
//        this.ctx = context;
//    }

    public AugmentedImageDatabase loadARImgDB(Context ctx, Session session) throws IOException{
        InputStream inputStream;
        inputStream = ctx.getAssets().open("myimages.imgdb");
        AugmentedImageDatabase imgDB = AugmentedImageDatabase.deserialize(session, inputStream);
        return imgDB;
    }
}
