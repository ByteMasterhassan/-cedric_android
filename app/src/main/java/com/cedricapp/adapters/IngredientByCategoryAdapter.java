package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.IngredientListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeUpdateModel;
import com.cedricapp.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IngredientByCategoryAdapter extends RecyclerView.Adapter<IngredientByCategoryAdapter.IngredientByCategoryAdapterHolder> {

    List<NutritionDataModel.Ingredient> ingredientList;
    IngredientListInterface ingredientListInterface;
    ArrayList<String> ingredientsCheckedList;
    Context context;
    String currentUserId;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private ArrayList<String> checkedList = null;
    ArrayList<RecipeUpdateModel> recipes;

    boolean isHandlerRunning = false;
    DBHelper dbHelper;
    String requestFrom;
    Resources resources;

    public IngredientByCategoryAdapter(Context context, List<NutritionDataModel.Ingredient> ingredientList, IngredientListInterface ingredientListInterface, ArrayList<RecipeUpdateModel> recipes, String requestFrom,   Resources resources) {
        this.context = context;
        this.ingredientList = ingredientList;
        this.ingredientListInterface = ingredientListInterface;
        this.recipes = recipes;
        dbHelper = new DBHelper(context);
        this.requestFrom = requestFrom;
        this.resources=resources;
    }

    @NonNull
    @Override
    public IngredientByCategoryAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_details, parent, false);
        return new IngredientByCategoryAdapterHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull IngredientByCategoryAdapterHolder holder, @SuppressLint("RecyclerView") int position) {
        //   df = new DecimalFormat("0.00");
        if (ingredientList.get(position).getServing() != null) {
            try {
                if (requestFrom.matches("cart")) {
                    /*Runnable runnable = new Runnable() {
                        @Override
                        public void run() {*/
                    int checkedIngredients = dbHelper.getAllCheckedIngredientsCount(ingredientList.get(position).getId());
                    if (checkedIngredients > 0) {
                        int totalIngredients = dbHelper.getTotalNumberOfIngredients(ingredientList.get(position).getId());
                        if (totalIngredients > checkedIngredients) {
                            List<NutritionDataModel.Ingredient> ingredients = dbHelper.getUncheckedIngredientsByIngredientID(ingredientList.get(position).getId());
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "In Cart and unchecked available: " + ingredients.toString());
                            }
                            if (ingredients != null && ingredients.size() > 0) {
                                holder.nutrientDetails.setText("" + ingredients.get(0).getTotal() + "" + ingredientList.get(position).getUnit() + " " + ingredientList.get(position).getIngredient()/* + " Recipe ID: " + ingredientList.get(position).getRecipeID() + ", Ing ID: " + ingredientList.get(position).getId()*/);
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(Common.LOG, "In Cart and ingredients==null OR ingredients.size()==0 ");
                                }
                            }

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Total Ingredients of " + ingredientList.get(position).getId() + " in DB are " + totalIngredients + " and Checked ingredients in DB are " + checkedIngredients);
                            }
                            holder.nutrientDetails.setText("" + ingredientList.get(position).getTotal() + "" + ingredientList.get(position).getUnit() + " " + ingredientList.get(position).getIngredient()/* + " Recipe ID: " + ingredientList.get(position).getRecipeID() + ", Ing ID: " + ingredientList.get(position).getId()*/);
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Total Ingredients of " + ingredientList.get(position).getId() + " and Checked ingredients in DB is zero i.e. " + checkedIngredients);
                        }
                        holder.nutrientDetails.setText("" + ingredientList.get(position).getTotal() + "" + ingredientList.get(position).getUnit() + " " + ingredientList.get(position).getIngredient()/* + " Recipe ID: " + ingredientList.get(position).getRecipeID() + ", Ing ID: " + ingredientList.get(position).getId()*/);
                    }
                    // }
                    /*};
                    new Thread(runnable).start();*/

                } else if (requestFrom.matches("specific")) {
                    //double servingQty = Double.parseDouble(ingredientList.get(position).getServing()) * ingredientList.get(position).getQuantity();
                    holder.nutrientDetails.setText("" + ingredientList.get(position).getTotal() + "" + ingredientList.get(position).getUnit() + " " + ingredientList.get(position).getIngredient() /*+ " Recipe ID: " + ingredientList.get(position).getRecipeID() + ", Ing ID: " + ingredientList.get(position).getId()*/);
                }
            } catch (Exception ex) {
                FirebaseCrashlytics.getInstance().recordException(ex);
                ex.printStackTrace();
            }
        } /*else {
            holder.nutrientDetails.setText("" + ingredientList.get(position).getTotal() + "" + ingredientList.get(position).getUnit() + " " + ingredientList.get(position).getIngredient() *//*+ " Recipe ID: " + ingredientList.get(position).getRecipeID() + ", Ing ID: " + ingredientList.get(position).getId()*//*);
        }*/

        String catName = String.valueOf(ingredientList.get(position).id);
        currentUserId = SharedData.id;
    /*checkForCheckList();
    if (checkedList != null) {
        if (ingredientList.get(position).getStatus() != null)
            if (ingredientList.get(position).getStatus().matches("checked")) {
                boolean isChecked = true;
                holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.nutrientDetails.setChecked(isChecked);
            }
    }*/
        boolean isIngredientChecked = false;
        if (requestFrom.matches("cart")) {
            isIngredientChecked = dbHelper.isIngredientChecked(ingredientList.get(position).getId());
        } else if (requestFrom.matches("specific")) {
            isIngredientChecked = dbHelper.isIngredientCheckedByIngredientAndRecipeID(ingredientList.get(position).getRecipeID(), ingredientList.get(position).getId());
        }

        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "User id is " + currentUserId + ", Ingredient ID is " + ingredientList.get(position).getId() + " and checked Status is " + isIngredientChecked);
        }
        if (isIngredientChecked) {
            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.nutrientDetails.setChecked(true);
        } else {
            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.HINTING_OFF);
            holder.nutrientDetails.setChecked(false);
        }

        /*checkForCheckList();

        if (checkedList != null) {
            if (ingredientList != null && ingredientList.size() != 0) {

                if (checkedList.contains("" + ingredientList.get(position).getId())) {
                    boolean isChecked = true;
                    holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.nutrientDetails.setChecked(isChecked);
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Not Contains Data");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Ingredients list is empty");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "Checked List is empty");
            }
        }*/

        holder.nutrientDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                boolean checked = ((CheckBox) compoundButton).isChecked();
                if (context != null) {
                    if (ConnectionDetector.isConnectedWithInternet(context)) {
                        boolean statusCheck = false;
                        if (isChecked) {

                            //  Toast.makeText(compoundButton.getContext(), checkedItems +" check status",Toast.LENGTH_LONG).show();
                            //  Log.d("Ingredient data", String.valueOf(myListData.getId()));

                            String checkedStatus = ingredientList.get(position).status;
                            System.out.println(checkedStatus + "check1");
                            System.out.println(ingredientList.toString() + "check1");
                            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            //boolean statusCheck;
                         /*  if(checkedStatus.equals("checked")){
                               statusCheck=true;
                               System.out.println(statusCheck+"check1");
                           }
                            System.out.println(statusCheck+"check2");*/


                            //if (checkedStatus.matches("un-checked")) {
                            //  System.out.println(statusCheck + "check3");


                            checkIngredient(ingredientList.get(position).getId(), catName, isChecked, checkedStatus);
                            //  }


                        } else {
                            // categoryId=  myListData.getNutritionShoppingCategoryId();
                            holder.nutrientDetails.setPaintFlags(holder.nutrientDetails.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            // ingredientListInterface.onCheckedListener(catName, isChecked, null);
                            String checkedStatus = ingredientList.get(position).status;
                            //TODO comitted if statementent due to local db
                            // if (checkedStatus.matches("checked")) {

                            unCheckIngredient(ingredientList.get(position).getId(), catName, isChecked, checkedStatus);

                            //  }

                            //putCheckedState(isChecked, position,categoryId);
                        }
                    } else {
                        Toast.makeText(context, resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Common.isLoggingEnabled)
                        Log.e(Common.LOG, "Context is null in Ingredient By Category Adapter");
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
        if (Common.isLoggingEnabled) {
            if (checkedList != null) {
                Log.d("checkedList", checkedList.toString());
            }
        }

    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    static class IngredientByCategoryAdapterHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox nutrientDetails;

        public IngredientByCategoryAdapterHolder(@NonNull View itemView) {
            super(itemView);
            nutrientDetails = itemView.findViewById(R.id.checkboxForNutritionShopping);
        }

    }


    void checkIngredient(int ingredientID, String catName, boolean isChecked, String checkedStatus) {
        if (Common.isLoggingEnabled)
            Log.d(Common.LOG, "Recipe size:" + recipes.size());
        //ArrayList<RecipeUpdateModel> recipeUpdateModels = new ArrayList<>();
        if (requestFrom.matches("specific")) {
            ingredientListInterface.onCheckedListener(catName, isChecked, null);
        } else if (requestFrom.matches("cart")) {
            dbHelper.updateAllCheckUncheckInIngredientTable(String.valueOf(ingredientID),"checked");
            dbHelper.updateAllCheckUncheckOnly(String.valueOf(ingredientID),"checked");

            /*for (int i = 0; i < recipes.size(); i++) {
                //for (int j = 0; j < recipes.get(i).getIngredient_ids().size(); j++) {
                // if (recipes.get(i).getIngredient_ids().get(0) == ingredientID) {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Clicked recipesID: " + recipes.get(i).getRecipe_id() + " and ingredient ID is " + recipes.get(i).getIngredient_ids().get(0));
                }
                for (int j = 0; j < recipes.get(i).getIngredient_ids().size(); j++) {
                    CheckUncheckDbModel checkUncheckDbModel = dbHelper.getServerCheckedUncheckedItems(recipes.get(i).getIngredient_ids().get(j), recipes.get(i).getRecipe_id());
                    if (checkUncheckDbModel != null) {
                        String check_server = checkUncheckDbModel.getServer_checked_state();
                        String dbCheckState = checkUncheckDbModel.getChecked_state();
                        Log.d(Common.LOG, "checked State" + dbCheckState);
                        Log.d(Common.LOG, "checked State" + check_server);
                        //change check status in dbHelper
                    *//*if (dbHelper.getServerCheckedUncheckedItemsIDs(ingredientID,
                            recipes.get(i).getRecipe_id())) {*//*
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Clicked recipesID: " + recipes.get(i).getRecipe_id()
                                    + " and ingredient ID is " + recipes.get(i).getIngredient_ids().get(0));
                        }

                        if (check_server != null && !check_server.matches("")) {
                            if (check_server.matches("checked")) {
                                dbHelper.updateCheckUncheck(recipes.get(i).getRecipe_id(), recipes.get(i).getIngredient_ids().get(j), Integer.parseInt(recipes.get(i).getServings()), "checked", check_server, true);
                                dbHelper.updateCheckUncheckInIngredientTable(recipes.get(i).getRecipe_id(), "" + recipes.get(i).getIngredient_ids().get(j), "checked");
                            } else {
                                dbHelper.updateCheckUncheck(recipes.get(i).getRecipe_id(), recipes.get(i).getIngredient_ids().get(j), Integer.parseInt(recipes.get(i).getServings()), "checked", check_server, false);
                                dbHelper.updateCheckUncheckInIngredientTable(recipes.get(i).getRecipe_id(), "" + recipes.get(i).getIngredient_ids().get(j), "checked");
                            }
                        }

                    }
                    // }

                }
            }*/
            //  }
            //  }
        }
        //makeJSON(recipeUpdateModels);
    }

    private void unCheckIngredient(int ingredientID, String catName, boolean isChecked, String checkedStatus) {
        if (Common.isLoggingEnabled)
            Log.d(Common.LOG, "Recipe size:" + recipes.size());
        //ArrayList<RecipeUpdateModel> recipeUpdateModels = new ArrayList<>();
        if (requestFrom.matches("specific")) {
            ingredientListInterface.onUnCheckedListener(catName, isChecked, null);
        } else if (requestFrom.matches("cart")) {
            dbHelper.updateAllCheckUncheckInIngredientTable(String.valueOf(ingredientID),"un-checked");
            dbHelper.updateAllCheckUncheckOnly(String.valueOf(ingredientID),"un-checked");
            /*for (int i = 0; i < recipes.size(); i++) {
                //if (recipes.get(i).getIngredient_ids().get(0) == ingredientID) {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Uncheck recipesID: " + recipes.get(i).getRecipe_id()
                            + " and ingredient ID is " + recipes.get(i).getIngredient_ids().get(0));
                }
                for (int j = 0; j < recipes.get(i).getIngredient_ids().size(); j++) {
                    CheckUncheckDbModel checkUncheckDbModel = dbHelper.getServerCheckedUncheckedItems(recipes.get(i).getIngredient_ids().get(j), recipes.get(i).getRecipe_id());
                    if (checkUncheckDbModel != null) {
                        String check_server = checkUncheckDbModel.getServer_checked_state();
                        String dbCheckState = checkUncheckDbModel.getChecked_state();
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "checked State" + dbCheckState);
                            Log.d(Common.LOG, "checked State" + check_server);
                        }
                        //change check status in dbHelper
                    *//*if (dbHelper.getServerCheckedUncheckedItemsIDs(ingredientID,
                            recipes.get(i).getRecipe_id())) {*//*
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Uncheck recipesID: " + recipes.get(i).getRecipe_id()
                                    + " and ingredient ID is " + recipes.get(i).getIngredient_ids().get(0));
                        }

                        if (check_server != null && !check_server.matches("")) {
                            if (check_server.matches("un-checked")) {                              //change check status in dbHelper
                                dbHelper.updateCheckUncheck(recipes.get(i).getRecipe_id(), recipes.get(i).getIngredient_ids().get(j), Integer.parseInt(recipes.get(i).getServings()), "un-checked", check_server, true);
                                dbHelper.updateCheckUncheckInIngredientTable(recipes.get(i).getRecipe_id(), "" + recipes.get(i).getIngredient_ids().get(j), "un-checked");
                            } else {
                                //change check status in dbHelper
                                dbHelper.updateCheckUncheck(recipes.get(i).getRecipe_id(), recipes.get(i).getIngredient_ids().get(j), Integer.parseInt(recipes.get(i).getServings()), "un-checked", check_server, false);
                                dbHelper.updateCheckUncheckInIngredientTable(recipes.get(i).getRecipe_id(), "" + recipes.get(i).getIngredient_ids().get(j), "un-checked");
                            }
                        }

                    }
                    //}

                }

            }*/
            // }
        }
        //makeJSON(recipeUpdateModels);
    }
}

