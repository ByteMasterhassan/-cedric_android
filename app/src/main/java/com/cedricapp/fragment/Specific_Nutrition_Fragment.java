package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.cedricapp.adapters.CategoryWiseIngredientAdapter;
import com.cedricapp.adapters.ShoppingListAdapter;
import com.cedricapp.adapters.ShoppingListRecipeAdapter;
import com.cedricapp.adapters.SpecificNutritionDetailsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.IngredientListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.CheckUncheckDbModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeUpdateModel;
import com.cedricapp.model.ShoppingDetailsModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.activity.HomeActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")

public class Specific_Nutrition_Fragment extends Fragment implements PopupMenu.OnMenuItemClickListener, IngredientListInterface {
    private ImageButton menu_Icon, mBackButton, minus_btn_cv, plus_btn_cv;
    ImageView selectedNutritionImage;
    protected MaterialTextView selectedNutritionName, txt_shoppinglist, txt_numberofsaving;
    private TextView meat_tv, dairy_tv, veg_tv, pantry_tv, freshFruits_tv;
    private RecyclerView mfreshFruitsRecyclerview, mPantryRecyclerview, mVegatablesRecyclerview, dairyRecyclerView, meatRecyclerView;
    private RecyclerView.LayoutManager nutritionlayoutManager, fruitlayoutManager, generallayoutManager,
            meatlayoutManager, vegetablelayoutManager, dairylayoutManager;
    private ShoppingListAdapter adapter;
    private ShoppingListRecipeAdapter recipeAdapter;
    private CategoryWiseIngredientAdapter categoryAdpater;
    private RecyclerView categoriesRR;
    private RecyclerView.LayoutManager categoryLayoutManager;

    public static List<ShoppingDetailsModel> allIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> fruitIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> generalIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> meatIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> dairyIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> vegitableIngredientsList = new ArrayList<ShoppingDetailsModel>();

    private SpecificNutritionDetailsAdapter nutritionDetailsAdapter;


    private TextView textViewSerialNo;
    private View view1;
    private DBHelper dbHelper;
    public static List<NutritionDataModel> nutritionShoppingList = new ArrayList<>();
    public int nut_id, user_id;
    String name;
    private MaterialCardView mServingCardView;
    public int currentIndex;
    private NutritionDataModel nutritionShoppingDateModel;
    Resources resources;
    private Context mContext;
    ArrayList<String> ingridentsCheckedlist = new ArrayList<>();
    ArrayList<String> ingridentsUnCheckedlist = new ArrayList<>();
    ArrayList<RecipeUpdateModel> recipeUpdateModels;
    ArrayList<RecipeUpdateModel> unCheckRecipeUpdateList;
    ArrayList<RecipeUpdateModel> recipeServingUpdateModels;
    boolean isHandlerRunning, isServingHandlerRunning;
    Handler handler, servingHandler;
    Runnable runnable, servingRunnbale;
    int quantity = 0;
    BlurView blurView;
    LottieAnimationView loading_lav;
    private String message;

    String TAG = "SPECIFIC_NUTRITION_TAG";


    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.hideBottomNav();
        SharedData.redirectToDashboard = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    public Specific_Nutrition_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific__nutrition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
       // resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        Init();

        //loadAllShoppingListData();

        try {
//            String img = getArguments().getString("profileImage");
//            Glide.with(getContext()).load(img).into(selectedNutritionImage);

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }

        menu_Icon.setOnClickListener(view1 -> {
            AddPopUpMenu(view1);

        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        plus_btn_cv.setOnClickListener(view1 -> {
            IncrementQuantity();

        });
        minus_btn_cv.setOnClickListener(view1 -> {
            DecrementQuantity();

        });
    }

    private void DecrementQuantity() {
        quantity = Integer.parseInt(textViewSerialNo.getText().toString());
        if (quantity > 1) {
            quantity -= 1;
            textViewSerialNo.setText(String.format("%d", quantity));
            if (nut_id != 0) {
                dbHelper.updateServing(nut_id, quantity);
                dbHelper.updateServingCheckUnCheck(nut_id, quantity);
                //getDataFromDB();
                /*if(categoryAdpater!=null){
                    categoryAdpater.notifyDataSetChanged();
                }*/
                updateDataFromDb();
                /*if (categoryAdpater != null) {
                    categoryAdpater.notifyDataSetChanged();
                }*/
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "nut_id == 0");
                }
            }
        }
        /*updateToDb(quantity);
        //  loadAllShoppingListData();
        if(isAdded() && getContext()!=null)
            Toast.makeText(getContext(),"Under maintenace", LENGTH_SHORT).show();*/

        /*if (isServingHandlerRunning) {
            if (servingHandler != null && servingRunnbale != null) {
                servingHandler.removeCallbacks(servingRunnbale);
                //servingHandler = new Handler();
                isServingHandlerRunning = false;
            }
        }
        updateServing();*/
    }

    private void IncrementQuantity() {
        quantity = Integer.parseInt(textViewSerialNo.getText().toString());
        quantity += 1;
        textViewSerialNo.setText(String.format("%d", quantity));
        if (nut_id != 0) {
            dbHelper.updateServing(nut_id, quantity);
            dbHelper.updateServingCheckUnCheck(nut_id, quantity);
            //getDataFromDB();
            updateDataFromDb();
            /*if (categoryAdpater != null) {
                categoryAdpater.notifyDataSetChanged();
            }*/
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "nut_id == 0");
            }
        }
        /*updateToDb(quantity);
        clearAllList();
        //loadAllShoppingListData();
        if(isAdded() && getContext()!=null)
            Toast.makeText(getContext(),"Under maintenace", LENGTH_SHORT).show();*/

        /*if (isServingHandlerRunning) {
            if (servingHandler != null && servingRunnbale != null) {
                servingHandler.removeCallbacks(servingRunnbale);
                //servingHandler = new Handler();
                isServingHandlerRunning = false;
            }
        }
        updateServing();*/
    }

   /* void updateServing() {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Quantity is " + quantity);
        }
        servingRunnbale = new Runnable() {
            @Override
            public void run() {
                if (!isServingHandlerRunning) {
                    isServingHandlerRunning = true;
                    Gson gson = new Gson();
                    recipeServingUpdateModels = new ArrayList<>();
                    recipeServingUpdateModels.add(new RecipeUpdateModel(nut_id, new ArrayList<Integer>(), "" + quantity));
                    String JSON_Recipe = gson.toJson(recipeServingUpdateModels);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Serving JSON Response: " + JSON_Recipe);
                    }
                    updateShoppingToServer(JSON_Recipe);
                }
            }
        };
        servingHandler.postDelayed(servingRunnbale, SHOPPING_DELAY_TIME);
    }*/


    private void updateToDb(int quantity) {
        //Todo
       /* nutritionShoppingList.get(currentIndex).setIntake(String.valueOf(quantity));

        dbHelper.updateNutritionIntake(nutritionShoppingList.get(currentIndex));*/
    }

    private void AddPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);

        popup.setOnMenuItemClickListener(Specific_Nutrition_Fragment.this);
        popup.inflate(R.menu.menu_specific_shopping);
        popup.show();
    }

    public void Init() {
        mContext = getContext();
        dbHelper = new DBHelper(getContext());
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        user_id = Integer.parseInt(SharedData.id);
        String token = SharedData.token;

        mBackButton = view1.findViewById(R.id.backArrow);
        menu_Icon = view1.findViewById(R.id.menu_Icon);
        mServingCardView = view1.findViewById(R.id.servingCardView);
        selectedNutritionImage = view1.findViewById(R.id.selected_nutrition);
        selectedNutritionName = view1.findViewById(R.id.selectedNutritionName);
        txt_shoppinglist = view1.findViewById(R.id.txt_shoppinglist);
        txt_numberofsaving = view1.findViewById(R.id.txt_numberofsavings);
        name = getArguments().getString("selectedNutritionName");
        selectedNutritionName.setText(name);
        nut_id = getArguments().getInt("selectedNutritionId");
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "USerID is " + SharedData.id + " and Nutrtion ID: " + nut_id);
        }
        categoriesRR = view1.findViewById(R.id.categoriesRR);
        categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recipeUpdateModels = new ArrayList<>();
        unCheckRecipeUpdateList = new ArrayList<>();
        handler = new Handler();
        servingHandler = new Handler();


        /*mfreshFruitsRecyclerview = view1.findViewById(R.id.fruitsRecyclerView);
        mPantryRecyclerview = view1.findViewById(R.id.pantryRecyclerView);
        mVegatablesRecyclerview = view1.findViewById(R.id.vegRecyclerView);
        dairyRecyclerView = view1.findViewById(R.id.dairyRecyclerView);
        meatRecyclerView = view1.findViewById(R.id.meatRecyclerView);

        freshFruits_tv = view1.findViewById(R.id.freshFruits_tv);
        veg_tv = view1.findViewById(R.id.veg_tv);
        meat_tv = view1.findViewById(R.id.meat_tv);
        dairy_tv = view1.findViewById(R.id.dairy_tv);
        pantry_tv = view1.findViewById(R.id.pantry_tv);*/

       /* fruitlayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        generallayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        meatlayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        dairylayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        vegetablelayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        nutritionlayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);


        //for fresh fruit rv
        nutritionDetailsAdapter = new SpecificNutritionDetailsAdapter(fruitIngredientsList, requireContext());
        mfreshFruitsRecyclerview.setHasFixedSize(true);
        mfreshFruitsRecyclerview.setLayoutManager(fruitlayoutManager);
        mfreshFruitsRecyclerview.setAdapter(nutritionDetailsAdapter);


        // for pantry-General rv
        nutritionDetailsAdapter = new SpecificNutritionDetailsAdapter(generalIngredientsList, requireContext());
        mPantryRecyclerview.setHasFixedSize(true);
        mPantryRecyclerview.setLayoutManager(generallayoutManager);
        mPantryRecyclerview.setAdapter(nutritionDetailsAdapter);

        // for vegitable rv
        nutritionDetailsAdapter = new SpecificNutritionDetailsAdapter(vegitableIngredientsList, requireContext());
        mVegatablesRecyclerview.setHasFixedSize(true);
        mVegatablesRecyclerview.setLayoutManager(vegetablelayoutManager);
        mVegatablesRecyclerview.setAdapter(nutritionDetailsAdapter);

        // for Dairy rv
        nutritionDetailsAdapter = new SpecificNutritionDetailsAdapter(dairyIngredientsList, requireContext());
        dairyRecyclerView.setHasFixedSize(true);
        dairyRecyclerView.setLayoutManager(dairylayoutManager);
        dairyRecyclerView.setAdapter(nutritionDetailsAdapter);


        // for Meat rv
        nutritionDetailsAdapter = new SpecificNutritionDetailsAdapter(meatIngredientsList, requireContext());
        meatRecyclerView.setHasFixedSize(true);
        meatRecyclerView.setLayoutManager(meatlayoutManager);
        meatRecyclerView.setAdapter(nutritionDetailsAdapter);*/

        minus_btn_cv = view1.findViewById(R.id.minus_btn_cv);
        plus_btn_cv = view1.findViewById(R.id.plus_btn_cv);
        textViewSerialNo = view1.findViewById(R.id.textViewSerialNo);

        setlanguageToWidget();

        // nutritionShoppingList = dbHelper.getAllShoppingList();
        //nutritionListApiCall();
        getDataFromDB();


    }

    private void setlanguageToWidget() {
        txt_shoppinglist.setText(resources.getString(R.string.shopping_list_tv));
        txt_numberofsaving.setText(resources.getString(R.string.number_of_servings));
    }


    void updateDataFromDb() {
        List<NutritionDataModel.Category> categories = dbHelper.getCategories();
        List<NutritionDataModel.Ingredient> ingredients = dbHelper.getIngredients(String.valueOf(nut_id));
        if (categories != null && ingredients != null) {
            categoryAdpater = new CategoryWiseIngredientAdapter(getActivity(), categories, /*ingredients,*/ Specific_Nutrition_Fragment.this, "specific", nut_id,resources);
            categoriesRR.setAdapter(categoryAdpater);
            categoryAdpater.notifyDataSetChanged();
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "SpecificNutrtionFragment:updateDataFromDb:categories == null && ingredients == null");
            }
        }
    }

    void getDataFromDB() {
        NutritionDataModel.Recipe recipe = dbHelper.getNutritionByID(String.valueOf(nut_id));
        List<NutritionDataModel.Category> categories = dbHelper.getCategories();
        List<NutritionDataModel.Ingredient> ingredients = dbHelper.getIngredients(String.valueOf(nut_id));
        if (recipe != null && categories != null && ingredients != null) {
            if (ingredients.size() > 0) {
                textViewSerialNo.setText(ingredients.get(0).getServing());
            }
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Recipe: " + recipe.toString() + "\nCategories: " + categories.toString() + "\nIngredients: " + ingredients.toString());
            }
            Glide.with(getActivity()).asBitmap().load(recipe.getImageURL()).into(selectedNutritionImage);
            categoryAdpater = new CategoryWiseIngredientAdapter(getActivity(), categories, /*ingredients,*/ Specific_Nutrition_Fragment.this, "specific", nut_id,resources);
            categoriesRR.setHasFixedSize(true);
            categoriesRR.setLayoutManager(categoryLayoutManager);
            categoriesRR.setAdapter(categoryAdpater);

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Specific_Nutrition_Fragment: getDataFromDB, recipe == null");
            }
        }

    }

    private void nutritionListApiCall() {
        clearAllList();
        blurrBackground();
        startLoading();
        Call<NutritionDataModel> call = ApiClient.getService().getShoppingList("Bearer " + SharedData.token, "" + user_id);
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                stopLoading();
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    nutritionShoppingDateModel = response.body();
                    nutritionShoppingList.add(nutritionShoppingDateModel);

                    loadAllShoppingListData(nutritionShoppingList);

                    if (nutritionShoppingDateModel != null) {
                        if (nutritionShoppingDateModel.getData() != null) {
                            if (nutritionShoppingDateModel.getData().getRecipes() != null) {
                                if (nutritionShoppingDateModel.getData().getRecipes().size() > 0) {

                                    //List of recipes
                                   /*     List<NutritionDataModel.Recipe> recipes = nutritionShoppingDateModel.getData().getRecipes();
                                        recipeAdapter = new ShoppingListRecipeAdapter(getActivity(), recipes);
                                        mAddToShoppingListRecyclerview.setHasFixedSize(true);
                                        mAddToShoppingListRecyclerview.setLayoutManager(nutritionlayoutManager);
                                        mAddToShoppingListRecyclerview.setAdapter(recipeAdapter);*/


                                    //List of Categories
                                    List<NutritionDataModel.Category> categories = nutritionShoppingDateModel.getData().getCategories();
                                    categoryAdpater = new CategoryWiseIngredientAdapter(getActivity(), categories, /*getAllIngredients(nutritionShoppingDateModel)*/ Specific_Nutrition_Fragment.this, "specific", nut_id,resources);
                                    categoriesRR.setHasFixedSize(true);
                                    categoriesRR.setLayoutManager(categoryLayoutManager);
                                    categoriesRR.setAdapter(categoryAdpater);


                                        /*for (int i = 0; i < nutritionShoppingDateModel.getData().getRecipes().size(); i++) {

                                            if (!dbHelper.isRecipeByIdInShoppingCart(currentUserId, "" + nutritionShoppingDateModel.getData().getRecipes().get(i).getId())) {
                                                dbHelper.addShoppingList(Integer.parseInt(currentUserId), nutritionShoppingDateModel.getData().getRecipes().get(i).getId());
                                            } else {
                                                if (Common.isLoggingEnabled)
                                                    Log.d(TAG, "Nutrition Shopping recipe named as " + nutritionShoppingDateModel.getData().getRecipes().get(i).getName() + " and id is " + nutritionShoppingDateModel.getData().getRecipes().get(i).getId() + " already available in local db");
                                            }
                                        }*/
                                    //getCartDataFromLocalDB();
                                } else {
                                    //manageEmptyShoppingList();
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "Nutrition Shopping recipe list size is 0");
                                    }
                                }
                            } else {
                                //manageEmptyShoppingList();
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Nutrition Shopping getRecipe is null");
                                }
                            }

                        } else {
                            // manageEmptyShoppingList();
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Nutrition Shopping getData is null");
                            }
                        }

                    } else {
                        // manageEmptyShoppingList();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Nutrition Shopping Data Model is null");
                        }
                    }

                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.e(TAG, "Response Status " + message.toString());
                    }
                    if (message != null)
                        Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_NutritonAPICall",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });
    }

    List<NutritionDataModel.Ingredient> getAllIngredients(NutritionDataModel nutritionDataModel) {
        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
            //ingredientList.clear();
            if (nutritionDataModel.getData().getRecipes().get(i).getId() == nut_id) {
                if (nutritionDataModel.getData().getRecipes().get(i).getServing() != null) {
                    textViewSerialNo.setText(nutritionDataModel.getData().getRecipes().get(i).getServing());
                }

                for (int j = 0; j < nutritionDataModel.getData().getRecipes().get(i).getIngredients().size(); j++) {
                    NutritionDataModel.Ingredient ingredient = nutritionDataModel.getData().getRecipes().get(i).getIngredients().get(j);
                    ingredient.setRecipeID(nutritionDataModel.getData().getRecipes().get(i).getId());
                    ingredient.setServing(nutritionDataModel.getData().getRecipes().get(i).getServing());
                    ingredientList.add(ingredient);
                }
            }
        }
        return ingredientList;
    }


    private void clearAllList() {
        allIngredientsList.clear();
        fruitIngredientsList.clear();
        meatIngredientsList.clear();
        dairyIngredientsList.clear();
        generalIngredientsList.clear();
        vegitableIngredientsList.clear();
        unCheckRecipeUpdateList.clear();
        recipeUpdateModels.clear();
    }

    private void updateRecyclerAdapter() {
        mfreshFruitsRecyclerview.getAdapter().notifyDataSetChanged();
        meatRecyclerView.getAdapter().notifyDataSetChanged();
        dairyRecyclerView.getAdapter().notifyDataSetChanged();
        mPantryRecyclerview.getAdapter().notifyDataSetChanged();
        mVegatablesRecyclerview.getAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.remove_recipe:
                deleteSpecificNutriton(nut_id, user_id);

                return true;
            //ToDO needs to be change

            /*case R.id.remove_from_list:
                Toast.makeText(getContext(), "view", LENGTH_SHORT).show();
                return true;*/

            default:
                return false;
        }
    }

    private void deleteSpecificNutriton(int nut_id, int user_id) {
        blurrBackground();
        startLoading();

        Call<NutritionDataModel> call = ApiClient.getService().deleteShoppingList("Bearer " + SharedData.token, String.valueOf(nut_id));
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                stopLoading();
                if (response.isSuccessful()) {
                    /* if (response.body().status == true) {*/
                    message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();

                    //nutritionListApiCall();
                    Toast.makeText(mContext, resources.getString(R.string.recipe_removed),
                            Toast.LENGTH_LONG).show();
                    //delete specific nutrition
                    dbHelper.deleteSpecificShoppingListData(nut_id, user_id);

                    nutritionShoppingList.clear();
                    //  nutritionShoppingDateModel.getData().getCategories().clear();
                    categoriesRR.setVisibility(View.GONE);
                    selectedNutritionName.setVisibility(View.GONE);
                    selectedNutritionImage.setVisibility(View.GONE);
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }

                    if (getFragmentManager() != null /*&& getFragmentManager().isStateSaved()*/) {
                        if (getFragmentManager().getBackStackEntryCount() != 0) {
                            getFragmentManager().popBackStack();
                        }
                    }

                  /*  }else if (response.body().status == true) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(),resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.e(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                }
                mServingCardView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_deleteSpecific",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });
    }

    private void loadAllShoppingListData(List<NutritionDataModel> nutritionShoppingList) {

        //clearAllList();

        selectedNutritionName.setText(name);
        for (int i = 0; i < nutritionShoppingList.size(); i++) {
            if (nutritionShoppingList.get(i).getData().getCategories().size() != 0) {
                for (int j = 0; j < nutritionShoppingList.get(i).getData().getRecipes().size(); j++) {

                    if (nutritionShoppingList.get(i).getData().getRecipes().get(j).getId() == nut_id) {
                        currentIndex = i;
                        nut_id = nutritionShoppingList.get(i).getData().getRecipes().get(j).id;
                        //  textViewSerialNo.setText(Specific_Nutrition_Fragment.nutritionShoppingList.get(i).getIntake());
                        //Toast.makeText(requireContext(),nutritionShoppingList.get(i).name, LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            Glide.with(getActivity()).asBitmap().load(/*Common.IMG_BASE_URL +*/ nutritionShoppingList.get(i).getData().getRecipes().get(j).getImageURL()).into(selectedNutritionImage);
                        }
                        for (int k = 0; k < nutritionShoppingList.get(i).getData().getRecipes().get(j).ingredients.size(); k++) {
                            Double quantity = Double.parseDouble(String.valueOf(nutritionShoppingList.get(i).getData().getRecipes().get(j).ingredients.get(k).quantity));
                            // int intake = Integer.parseInt(Specific_Nutrition_Fragment.nutritionShoppingList.get(i).getIntake());

                            int intake = 1;
                            quantity *= intake;


                            allIngredientsList.add(new ShoppingDetailsModel(
                                    nutritionShoppingList.get(i).getData().getRecipes().get(j).ingredients.get(k).ingredient,
                                    k,
                                    // Specific_Nutrition_Fragment.nutritionShoppingList.get(i).getData().get(j).ingredients.get(k).g,
                                    String.valueOf(quantity),
                                    nutritionShoppingList.get(i).getData().getRecipes().get(j).ingredients.get(k).category));
                        }
                        break;
                    }
                }
            }
        }
        //  SortIngredientsCategories(allIngredientsList);
    }

   /* private void SortIngredientsCategories(List<ShoppingDetailsModel> allIngredientsList) {
        for (int i = 0; i < allIngredientsList.size(); i++) {
            if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 1) {
                fruitIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 2) {
                vegitableIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 3) {
                meatIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 4) {
                dairyIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 5) {
                generalIngredientsList.add(allIngredientsList.get(i));
            }
        }
        updateRecyclerAdapter();
        hideEmptyCategory();
    }*/


    private void hideEmptyCategory() {
        if (fruitIngredientsList.isEmpty()) {
            freshFruits_tv.setVisibility(View.GONE);
        }
        if (vegitableIngredientsList.isEmpty()) {
            veg_tv.setVisibility(View.GONE);
        }
        if (dairyIngredientsList.isEmpty()) {
            dairy_tv.setVisibility(View.GONE);
        }
        if (generalIngredientsList.isEmpty()) {
            pantry_tv.setVisibility(View.GONE);
        }
        if (meatIngredientsList.isEmpty()) {
            meat_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedListener(String ingredientId, boolean checkedState, RecipeUpdateModel recipeUpdateModel) {
        CheckUncheckDbModel checkUncheckDbModel = dbHelper.getServerCheckedUncheckedItems(Integer.parseInt(ingredientId), nut_id);
        if (checkUncheckDbModel.getServer_checked_state() != null && !checkUncheckDbModel.getServer_checked_state().matches("")) {
            if (checkedState) {
                if (checkUncheckDbModel.getServer_checked_state().matches("checked")) {
                    dbHelper.updateCheckUncheckOnly(nut_id, ingredientId, "checked", "checked", true);
                    dbHelper.updateCheckUncheckInIngredientTable(nut_id, ingredientId, "checked");
                } else {
                    dbHelper.updateCheckUncheckOnly(nut_id, ingredientId, "checked", "un-checked", false);
                    dbHelper.updateCheckUncheckInIngredientTable(nut_id, ingredientId, "checked");
                }
            }/* else {
                if (checkUncheckDbModel.getServer_checked_state().matches("checked")) {
                    dbHelper.updateCheckUncheckOnly(nut_id, "un-checked", "checked", false);
                } else {
                    dbHelper.updateCheckUncheckOnly(nut_id, "un-checked", "un-checked", true);
                }
            }*/
        }
        //dbHelper.updateIngredientCheckStatus(Integer.parseInt(ingredientId),checkedState);
        /* if (ingridentsCheckedlist.isEmpty() && (checkedState != false)) {
            ingridentsCheckedlist.add(ingredientId);
        } else if (!ingridentsCheckedlist.contains(ingredientId) && (checkedState != false)) {
            ingridentsCheckedlist.add(ingredientId);
        } else if (ingridentsCheckedlist.contains(ingredientId) && (checkedState == false)) {
            ingridentsCheckedlist.remove(ingredientId);
        } else if (ingridentsCheckedlist.isEmpty() && checkedState == false) {
            ingridentsCheckedlist.add("null");
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(String.valueOf(user_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        // getting data from gson and storing it in a string.
        String json = gson.toJson(ingridentsCheckedlist);
        //  Log.d("mylist",json);

        // below line is to save data in shared
        // prefs in the form of string.

        editor.putString("checkedList", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.commit();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "Ingredient Checked List: " + ingridentsCheckedlist.toString());
        }*/
        //set model for request
        /*if (recipeUpdateModel != null) {
            recipeUpdateModels.add(recipeUpdateModel);
            // if (isHandlerRunning) {
            if (handler != null && runnable != null) {
                //handler.removeCallbacks(runnable);
                handler.removeCallbacksAndMessages(null);
                //handler = new Handler();
                isHandlerRunning = false;
            }
            // }
            //setRecipeUpdateModel(checkedState);
            removeSameIDsFromCheckedUncheckedList(checkedState);
        }*/
    }

    @Override
    public void onUnCheckedListener(String ingredientId, boolean checkedState, RecipeUpdateModel recipeUpdateModel) {
        CheckUncheckDbModel checkUncheckDbModel = dbHelper.getServerCheckedUncheckedItems(Integer.parseInt(ingredientId), nut_id);
        if (checkUncheckDbModel.getServer_checked_state() != null && !checkUncheckDbModel.getServer_checked_state().matches("")) {
            if (!checkedState) {
                if (checkUncheckDbModel.getServer_checked_state().matches("checked")) {
                    dbHelper.updateCheckUncheckOnly(nut_id, ingredientId, "un-checked", "checked", false);
                    dbHelper.updateCheckUncheckInIngredientTable(nut_id, ingredientId, "un-checked");
                } else {
                    dbHelper.updateCheckUncheckOnly(nut_id, ingredientId, "un-checked", "un-checked", true);
                    dbHelper.updateCheckUncheckInIngredientTable(nut_id, ingredientId, "un-checked");
                }
            }
        }

        /* if (ingridentsUnCheckedlist.isEmpty() && (checkedState != false)) {
            ingridentsUnCheckedlist.add(ingredientId);
        } else if (!ingridentsUnCheckedlist.contains(ingredientId) && (checkedState != false)) {
            ingridentsUnCheckedlist.add(ingredientId);
        } else if (ingridentsUnCheckedlist.contains(ingredientId) && (checkedState == false)) {
            ingridentsUnCheckedlist.remove(ingredientId);
        } else if (ingridentsUnCheckedlist.isEmpty() && checkedState == false) {
            ingridentsUnCheckedlist.add("null");
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(String.valueOf(user_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        // getting data from gson and storing it in a string.
        String json = gson.toJson(ingridentsUnCheckedlist);
        //  Log.d("mylist",json);

        // below line is to save data in shared
        // prefs in the form of string.

        editor.putString("unCheckedList", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.commit();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, ingridentsUnCheckedlist.toString());
        }*/

        //set model for request
       /* if (recipeUpdateModel != null) {
            unCheckRecipeUpdateList.add(recipeUpdateModel);
            //  if (isHandlerRunning) {
            if (handler != null && runnable != null) {
                // handler.removeCallbacks(runnable);
                handler.removeCallbacksAndMessages(null);
                //handler = new Handler();
                isHandlerRunning = false;
            }
            //   }
            //setRecipeUpdateModel(checkedState);
            removeSameIDsFromCheckedUncheckedList(checkedState);
        }*/
    }

    /*private void removeSameIDsFromCheckedUncheckedList(boolean checkedState) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkedState) {
                    if (recipeUpdateModels != null) {
                        for (int i = 0; i < recipeUpdateModels.size(); i++) {
                            if (unCheckRecipeUpdateList != null) {
                                for (int j = 0; j < unCheckRecipeUpdateList.size(); j++) {
                                    if (recipeUpdateModels.get(i).getRecipe_id() == unCheckRecipeUpdateList.get(j).getRecipe_id()) {
                                        if (recipeUpdateModels.get(i).getIngredient_ids() != null &&
                                                unCheckRecipeUpdateList.get(j).getIngredient_ids() != null) {
                                            for (int k = 0; k < recipeUpdateModels.get(i).getIngredient_ids().size(); k++) {
                                                for (int l = 0; l < unCheckRecipeUpdateList.get(j).getIngredient_ids().size(); l++) {
                                                    if (recipeUpdateModels.get(i).getIngredient_ids().get(k)
                                                            .equals(unCheckRecipeUpdateList.get(j).getIngredient_ids().get(l))) {
                                                        unCheckRecipeUpdateList.remove(unCheckRecipeUpdateList.get(j).getIngredient_ids().get(l));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        setRecipeUpdateModel(checkedState);
                    }
                } else {
                    if (unCheckRecipeUpdateList != null) {
                        for (int i = 0; i < unCheckRecipeUpdateList.size(); i++) {
                            if (recipeUpdateModels != null) {
                                for (int j = 0; j < recipeUpdateModels.size(); j++) {
                                    if (unCheckRecipeUpdateList.get(i).getRecipe_id() == recipeUpdateModels.get(j).getRecipe_id()) {
                                        if (unCheckRecipeUpdateList.get(i).getIngredient_ids() != null &&
                                                recipeUpdateModels.get(j).getIngredient_ids() != null) {
                                            for (int k = 0; k < unCheckRecipeUpdateList.get(i).getIngredient_ids().size(); k++) {
                                                for (int l = 0; l < recipeUpdateModels.get(j).getIngredient_ids().size(); l++) {
                                                    if (unCheckRecipeUpdateList.get(i).getIngredient_ids().get(k)
                                                            .equals(recipeUpdateModels.get(j).getIngredient_ids().get(l))) {
                                                        recipeUpdateModels.remove(recipeUpdateModels.get(j).getIngredient_ids().get(l));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        setRecipeUpdateModel(checkedState);
                    }
                }
            }
        });
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    void setRecipeUpdateModel(boolean checkedState) {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isHandlerRunning) {
                    isHandlerRunning = true;
                    Gson gson = new Gson();

                    if (checkedState) {
                        String JSON_Recipe = gson.toJson(addSameRecipeIngredientsInSameRecipeList());
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "JSON Response: " + JSON_Recipe);
                        }
                        sendCheckedListToServer(JSON_Recipe);
                    } else {
                        String JSON_Recipe = gson.toJson(addSameRecipeUnCheckIngredientsInSameRecipeList());
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "JSON Response: " + JSON_Recipe);
                        }
                        sendUnCheckedListToServer(JSON_Recipe);
                    }

                }

            }
        };
        handler.postDelayed(runnable, SHOPPING_DELAY_TIME);
    }*/

    /*private void sendCheckedListToServer(String JSON_Recipe) {
        Call<ChecklistModel> call = ApiClient.getService().checkShoppingListItem("Bearer " + SharedData.token, "" + user_id, JSON_Recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code());
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    recipeUpdateModels.clear();
                    ChecklistModel checklistModel = response.body();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {
                            //
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response message is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response is not successful");
                        }
                    }

                } else {
                    // nutritionListApiCall();
                    *//*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*//*
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code());
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response Status " + message.toString());
                            }
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_SendCheckListToServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_sendCheckedListToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void sendUnCheckedListToServer(String json_recipe) {
        Call<ChecklistModel> call = ApiClient.getService().unCheckShoppingListItem("Bearer " + SharedData.token, "" + user_id, json_recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code());
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    unCheckRecipeUpdateList.clear();
                    ChecklistModel checklistModel = response.body();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {
                            nutritionListApiCall();
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response message is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response is not successful");
                        }
                    }

                } else {
                    //nutritionListApiCall();
                    *//*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*//*
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code());
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response Status " + message.toString());
                            }
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_SendUnCheckListToServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_sendUncheckList",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }*/

    /*ArrayList<RecipeUpdateModel> addSameRecipeUnCheckIngredientsInSameRecipeList() {
        ArrayList<com.cedricapp.Model.RecipeUpdateModel> tempList = new ArrayList();

        ArrayList<Integer> recipeIDsArrayList = new ArrayList<>();
        for (int i = 0; i < unCheckRecipeUpdateList.size(); i++) {
            recipeIDsArrayList.add(unCheckRecipeUpdateList.get(i).getRecipe_id());
        }
        HashSet<Integer> intHashSet = new HashSet<Integer>();
        intHashSet.addAll(recipeIDsArrayList);
        ArrayList<Integer> tempIntegerList = new ArrayList<>();
        tempIntegerList.addAll(intHashSet);

        for (int i = 0; i < tempIntegerList.size(); i++) {
            ArrayList<Integer> ingredientList = new ArrayList<>();
            com.cedricapp.Model.RecipeUpdateModel tempRecipeUpdateModel = null;
            for (int j = 0; j < unCheckRecipeUpdateList.size(); j++) {
                if (tempIntegerList.get(i) == unCheckRecipeUpdateList.get(j).getRecipe_id()) {
                    if (!ingredientList.contains(unCheckRecipeUpdateList.get(j).getIngredient_ids().get(0))) {
                        ingredientList.add(unCheckRecipeUpdateList.get(j).getIngredient_ids().get(0));
                    }
                    tempRecipeUpdateModel = new com.cedricapp.Model.RecipeUpdateModel(unCheckRecipeUpdateList.get(j).getRecipe_id(), ingredientList,
                            unCheckRecipeUpdateList.get(j).getServings());
                }
            }
            tempList.add(tempRecipeUpdateModel);
        }
        return tempList;
    }

    ArrayList<RecipeUpdateModel> addSameRecipeIngredientsInSameRecipeList() {
        ArrayList<RecipeUpdateModel> tempList = new ArrayList();

        ArrayList<Integer> recipeIDsArrayList = new ArrayList<>();
        for (int i = 0; i < recipeUpdateModels.size(); i++) {
            recipeIDsArrayList.add(recipeUpdateModels.get(i).getRecipe_id());
        }
        HashSet<Integer> intHashSet = new HashSet<Integer>();
        intHashSet.addAll(recipeIDsArrayList);
        ArrayList<Integer> tempIntegerList = new ArrayList<>();
        tempIntegerList.addAll(intHashSet);

        for (int i = 0; i < tempIntegerList.size(); i++) {
            ArrayList<Integer> ingredientList = new ArrayList<>();
            RecipeUpdateModel tempRecipeUpdateModel = null;
            for (int j = 0; j < recipeUpdateModels.size(); j++) {
                if (tempIntegerList.get(i) == recipeUpdateModels.get(j).getRecipe_id()) {
                    ingredientList.add(recipeUpdateModels.get(j).getIngredient_ids().get(0));
                    tempRecipeUpdateModel = new RecipeUpdateModel(recipeUpdateModels.get(j).getRecipe_id(), ingredientList, recipeUpdateModels.get(j).getServings());
                }
            }
            tempList.add(tempRecipeUpdateModel);
        }
        return tempList;
    }

    void updateShoppingToServer(String JSON_Recipe) {
        Call<ChecklistModel> call = ApiClient.getService().updateShoppingList("Bearer " + SharedData.token, "" + user_id, JSON_Recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    recipeUpdateModels.clear();
                    message = ResponseStatus.getResponseCodeMessage(response.code());
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Response Status " + message.toString());
                    }
                    //  Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    ChecklistModel checklistModel = response.body();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                SessionUtil.setShoppingLoading(true, getContext());
                            }
                            nutritionListApiCall();

                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response message is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Response is not successful");
                        }
                    }

                } else {
                    *//*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*//*
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code());
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response Status " + message.toString());
                            }
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_updateShoppingList",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_UpdateShoppingList",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }*/

    private void blurrBackground() {

        if (isAdded()) {
            if (requireActivity() != null) {
                blurView.setVisibility(View.VISIBLE);
                float radius = 1f;

                //======================add disable button when load
        /*this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();*/
                this.getView().setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            return true;
                        }
                        return false;
                    }
                });

                View decorView = requireActivity().getWindow().getDecorView();
                ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

                Drawable windowBackground = decorView.getBackground();

                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurAlgorithm(new RenderScriptBlur(requireContext()))
                        .setBlurRadius(radius)
                        .setBlurAutoUpdate(true)
                        .setHasFixedTransformationMatrix(false);
            }
        }
    }

    private void startLoading() {
        if (isAdded()) {
            if (requireActivity() != null) {
                //dissable user interaction
                disableUserInteraction();

      /*  this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();*/
                this.getView().setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            return true;
                        }
                        return false;
                    }
                });

                loading_lav.setVisibility(View.VISIBLE);
                loading_lav.playAnimation();
            }
        }
    }

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        //Enable user interaction

        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("SpecificNutritionFragment_StopLoadingMethod",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        //loading_lav.pauseAnimation();
        loading_lav.cancelAnimation();
    }
}