package com.wilderpereira.reciclo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wilderpereira.reciclo.models.Recipe;
import com.wilderpereira.reciclo.models.Resource;
import com.wilderpereira.reciclo.R;
import com.wilderpereira.reciclo.models.Steps;
import com.wilderpereira.reciclo.utils.Utils;

public class  RecipeActivity extends AppCompatActivity {

    private ImageView itemImage;
    private TextView itemName;
    private TextView owner;
    private TextView favoriteCount;
    private TextView recyleCount;
    private LinearLayout linearIngredients;
    private LinearLayout linearPreparation;
    private Button btnReciclo;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mDatabase = Utils.getDatabase().getReference();

        Bundle extras = getIntent().getExtras();
        Recipe recipe = (Recipe) extras.getSerializable(getString(R.string.recipe_extra_key));

        itemName = (TextView) findViewById(R.id.tv_item);
        owner = (TextView) findViewById(R.id.tv_by);
        favoriteCount = (TextView) findViewById(R.id.tv_favorite_count);
        recyleCount = (TextView) findViewById(R.id.tv_recycled_count);
        linearIngredients = (LinearLayout) findViewById(R.id.linear_ingredients);
        linearPreparation = (LinearLayout) findViewById(R.id.linear_preparation);
        btnReciclo = (Button) findViewById(R.id.btn_reciclo);
        itemImage = (ImageView) findViewById(R.id.iv_item_image);


        itemName.setText(recipe.getName());
        favoriteCount.setText(recipe.getFavoriteCount()+""); //TODO: Change to favorite count (Also add on firebase)
        recyleCount.setText(recipe.getRecycleCount()+"");

        String preparationId = recipe.getPreparation();
        getPreparation(preparationId);

        String resources = recipe.getResource();
        getResources(resources);
    }

    public void getResources(String resourcesId){
        mDatabase.child("resources").child(resourcesId).child("resource").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot resourceSnapshot: dataSnapshot.getChildren()) {
                            Resource res = resourceSnapshot.getValue(Resource.class);
                            addTextViewToLinearLayout(linearIngredients,res.getName() + " x"+res.getAmount());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("RecipeActivity", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getPreparation(String preparationId){
        mDatabase.child("preparation").child(preparationId).child("steps").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot preparationSnapshot: dataSnapshot.getChildren()) {
                            Steps step = preparationSnapshot.getValue(Steps.class);
                            addTextViewToLinearLayout(linearPreparation,step.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("RecipeActivity", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    //TODO: Move to helper class
    private void addTextViewToLinearLayout(LinearLayout container, String text){
        TextView textView = new TextView(this);
        textView.setText(" - "+text);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        container.addView(textView);
    }

}
