package com.wilderpereira.reciclo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.util.Log;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.wilderpereira.reciclo.models.Recipe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wilder on 17/07/16.
 */
public class Utils {

    public static void setTextInputLayoutError(TextInputLayout textInputLayout, String message){
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(message);
    }

    public static void onStarClicked(final DatabaseReference postRef, final String uid) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Recipe p = mutableData.getValue(Recipe.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.favoritedBy.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    p.setFavoriteCount(p.getFavoriteCount()-1);
                    p.favoritedBy.remove(uid);

                    DatabaseReference favorites = FirebaseUtils.getDatabase().getReference().child("favorites").child(uid).child(postRef.getKey());
                    favorites.removeValue();

                    DatabaseReference recipe = FirebaseUtils.getDatabase().getReference().child("recipes").child(postRef.getKey()).child("favoritedBy").child(uid);
                    recipe.removeValue();
                    Log.d("TEST","Removing favs");

                } else {
                    //Only enters here if is not favorite
                    // Star the post and add self to stars
                    p.setFavoriteCount(p.getFavoriteCount()+1);
                    p.favoritedBy.put(uid, true);

                    Map<String, Object> favorite = new HashMap<>();
                    favorite.put("favorites/"+uid+"/"+postRef.getKey(),p.toMap());
                    //Adding to users favorite
                    DatabaseReference favorites =  postRef.getRoot();
                    favorites.updateChildren(favorite);

                    Log.d("TEST","Creating favs");
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(getClass().getSimpleName(), "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public static boolean isUserConnectionOk(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public static void reciclo(ShareDialog shareDialog){
        //TODO Get random phrases
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Reciclô")
                .setContentDescription(
                        "Veja só o que acabei de fazer utilizando o Reciclô!")
                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                .build();

        shareDialog.show(linkContent);
    }
}
