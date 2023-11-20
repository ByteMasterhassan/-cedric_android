package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.localdatabase.DBHelper;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cedricapp.interfaces.IngredientListInterface;
import com.cedricapp.model.ShoppingDetailsModel;
import com.cedricapp.R;
import com.cedricapp.common.SharedData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingNutritionDetailsAdapter extends
        RecyclerView.Adapter<ShoppingNutritionDetailsAdapter.ShoppingDetailViewHolder> {

    private final List<ShoppingDetailsModel> dataArrayList;
    /*
        ArrayList<ShoppingDetailsModel> checkedItems = new ArrayList<ShoppingDetailsModel>();
    */
    Context context;
    int categoryId;
    IngredientListInterface ingredientListInterface;
    private int getCatId;
    String currentUserId;
    private ArrayList<String> checkedList = null;
    private ArrayList<String> uncheckedList = null;
    private ShoppingDetailViewHolder holder;
    ShoppingDetailsModel myListData;
    DBHelper dbHelper;

    public ShoppingNutritionDetailsAdapter(List<ShoppingDetailsModel> dataArrayList, Context context, IngredientListInterface ingredientListInterface) {
        this.dataArrayList = dataArrayList;
        this.context = context;
        this.ingredientListInterface = ingredientListInterface;
        this.uncheckedList = uncheckedList;
        dbHelper = new DBHelper(context);
    }

    @Override
    public ShoppingDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_details, parent, false);

        ShoppingDetailViewHolder myViewHolder = new ShoppingDetailViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingDetailViewHolder holder, @SuppressLint("RecyclerView") int position) {
        myListData = dataArrayList.get(position);
        currentUserId = SharedData.id;
        Log.d("sid", currentUserId);

        String quantity = myListData.getNutritionShoppingQuantity();

        String catName = myListData.getNutritionShoppingDetails();

        String space = "    ";
        if (Integer.parseInt(quantity) >= 1000) {
            quantity = String.valueOf(Float.parseFloat(quantity) / 1000);
            String ingredientName = space + quantity + "kg" + " " + myListData.getNutritionShoppingDetails();
            holder.nutrientDetails.setText(ingredientName);
        } else {
            //holder.nutrientDetails.setText(space + quantity + "g" + " " + myListData.getNutritionShoppingDetails());
            String ingredientName = space + quantity + "g" + " " + myListData.getNutritionShoppingDetails();
            holder.nutrientDetails.setText(ingredientName);
        }


        checkForCheckList();

        if (checkedList != null) {
            if (checkedList.contains(myListData.getNutritionShoppingDetails())) {
                boolean isChecked = true;
                holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.nutrientDetails.setChecked(isChecked);
            }

        }
       /* boolean isIngredientChecked = dbHelper.isIngredientChecked(myListData.getId());
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Ingredient ID is " + myListData.getId() + " and checked Status is " + isIngredientChecked);
        }
        if (isIngredientChecked) {
            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.nutrientDetails.setChecked(true);
        } else {
            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.nutrientDetails.setChecked(false);
        }*/


        holder.nutrientDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                boolean checked = ((CheckBox) compoundButton).isChecked();


                if (isChecked) {

                    //  Toast.makeText(compoundButton.getContext(), checkedItems +" check status",Toast.LENGTH_LONG).show();
                    Log.d("Ingredient data", String.valueOf(myListData.getId()));
                    holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ingredientListInterface.onCheckedListener(catName, isChecked, null);
                } else {
                    // categoryId=  myListData.getNutritionShoppingCategoryId();
                    holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    ingredientListInterface.onCheckedListener(catName, isChecked, null);
                    // putCheckedState(isChecked, position,categoryId);
                }
            }
        });

    }

    private void checkForCheckList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(currentUserId, Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.

        String json = sharedPreferences.getString("checkedList", "null");

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        checkedList = gson.fromJson(json, type);

        // checking below if the array list is empty or not


    }

    private void putCheckedState(boolean isChecked, int position, int categoryId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(position), isChecked);
        editor.putInt("catId", categoryId);
        editor.apply();
    }

   /* private Set<String> getCheckedState() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        Set<String> isChecked = sharedPreferences.getStringSet("checkedList",null);
//       return  isChecked;

    }*/

   /* private int getCategoryId(int getCatId) {

//        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        int getCateId=sharedPreferences.getInt("catId",0);
//        return getCateId;
    }*/


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }


    public class ShoppingDetailViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox nutrientDetails;

        public ShoppingDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nutrientDetails = (MaterialCheckBox) itemView.findViewById(R.id.checkboxForNutritionShopping);
        }
    }
}
