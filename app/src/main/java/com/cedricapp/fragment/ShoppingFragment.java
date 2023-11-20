package com.cedricapp.fragment;

import static android.widget.Toast.LENGTH_SHORT;
import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.adapters.CategoryWiseIngredientAdapter;
import com.cedricapp.adapters.ShoppingListRecipeAdapter;
import com.cedricapp.adapters.ShoppingNutritionDetailsAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.IngredientListInterface;
import com.cedricapp.interfaces.ShoppingItemDeleteCallback;
import com.cedricapp.interfaces.ShoppingListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.Cart;
import com.cedricapp.model.CheckUncheckDbModel;
import com.cedricapp.model.CheckedUncheckedIngredientModel;
import com.cedricapp.model.CheckedUncheckedResponseModel;
import com.cedricapp.model.ChecklistModel;
import com.cedricapp.model.IngredientCategoriesModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeUpdateModel;
import com.cedricapp.model.ShoppingDataModel;
import com.cedricapp.model.ShoppingDetailsModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class ShoppingFragment extends Fragment implements PopupMenu.OnMenuItemClickListener,
        ShoppingListInterface, IngredientListInterface, ShoppingItemDeleteCallback, View.OnTouchListener {
    private ImageButton backArrow, menuIcon, mShoppingcart;
    private MaterialButton mAddToShoppingList;
    private MaterialTextView mNutritionName;
    private LinearLayout empty_list_ll;
    //private TextView meat_tv, dairy_tv, veg_tv, pantry_tv, freshFruits_tv;
    private ConstraintLayout mConstraintLayout;
    private RecyclerView mAddToShoppingListRecyclerview, categoriesRR;
    //private RecyclerView mfreshFruitsRecyclerview, mPantryRecyclerview, mVegatablesRecyclerview, dairyRecyclerView, meatRecyclerView;
    //private ShoppingListAdapter adapter;
    private ShoppingListRecipeAdapter recipeAdapter;
    private CategoryWiseIngredientAdapter categoryAdpater;
    private ShoppingNutritionDetailsAdapter nutritionDetailsAdapter1, nutritionDetailsAdapter2, nutritionDetailsAdapter3,
            nutritionDetailsAdapter4, nutritionDetailsAdapter5;
    private RecyclerView.LayoutManager nutritionlayoutManager, categoryLayoutManager;/*, fruitlayoutManager, generallayoutManager,
            meatlayoutManager, vegetablelayoutManager, dairylayoutManager;*/
    public static List<ShoppingDataModel> shoppingDataModels = new ArrayList<>();
    public static List<ShoppingDetailsModel> shoppingDetailsModels;
    public static List<NutritionDataModel> nutritionShoppingList = new ArrayList<>();
    ArrayList<String> ingridentsCheckedlist = new ArrayList<>();
    ArrayList<String> ingridentsUnCheckedlist = new ArrayList<>();
    ArrayList<String> categorylist = new ArrayList<>();
    HashSet<String> recipeIds = new HashSet<String>();
    ArrayList<IngredientCategoriesModel> ingredientCategories = new ArrayList<>();
    List<NutritionDataModel.Recipe> recipes = new ArrayList<>();
    List<NutritionDataModel.Category> categories = new ArrayList<>();


    public static List<ShoppingDetailsModel> allIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> allIngredientsList1 = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> fruitIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> generalIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> meatIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> dairyIngredientsList = new ArrayList<ShoppingDetailsModel>();
    public static List<ShoppingDetailsModel> vegitableIngredientsList = new ArrayList<ShoppingDetailsModel>();
    private Cart cart;
    //  private ArrayList<IngredientCategoriesModel> categories;
    private Context mContext;
    private DBHelper dbHelper;
    public View view1;
    private String currentUserId;
    private NutritionDataModel nutritionShoppingDateModel;
    ShoppingItemDeleteCallback shoppingItemDeleteCallback;
    ArrayList<RecipeUpdateModel> recipeUpdateModels;
    ArrayList<RecipeUpdateModel> unCheckRecipeUpdateList;
    boolean isHandlerRunning;
    Handler handler = new Handler();
    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Runnable runnable;
    Runnable runn;
    Runnable run;


    BlurView blurView;
    LottieAnimationView loading_lav;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private List<CheckUncheckDbModel> checkedUncheckedList;
    List<CheckUncheckDbModel> checkedCheckList;
    List<CheckUncheckDbModel> CheckedUnCheckList;
    boolean isAlreadyLoading;

    int delay = 2000;
    Gson gson;

    FragmentActivity requireActiivty;

    boolean isInteractionEnable;
    private String message;
    boolean loadDataFromServer = false;

    List<NutritionDataModel.Recipe> nutritionList;
    boolean handlerflag;

    boolean isCategoryNullInIngredient;

    MaterialTextView shoppingTitleTV;

    Resources resources;

    String TAG = "SHOPPING_TAG";

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {

        super.onResume();
        SharedData.redirectToDashboard = true;
        /*if(!handlerflag){
            startimer();
        }*/
    }


    @Override
    public void onStart() {
        super.onStart();
        startLoading();
        stopLoading();
        /*if (loadDataFromServer) {
            if (isAdded() && getContext() != null) {
                if (SessionUtil.isShoppingLoad(getContext())) {
                    loadShoppingListData();
                }
            }
        }*/
        manageCheckedUncheckedIngredientsFromDB();

    }

    @Override
    public void onPause() {
        //checkedUncheckedList = dbHelper.getAllCheckedUncheckedItems();
        // Log.d(TAG, "CheckedListItemsooo: " + checkedUncheckedList);
        // System.out.println(checkedUncheckedList.toString() + "CheckedListItems");
        //if (checkedUncheckedList != null && checkedUncheckedList.size() > 0) {
        //separateLists(checkedUncheckedList);
        /*if (handler != null) {
            handlerflag=false;
            handler.removeCallbacks(runnable);
        }*/
        stopLoading();
        manageCheckedUncheckedIngredientsFromDB();
        // }
        super.onPause();

    }


    private void manageCheckedUncheckedIngredientsFromDB() {
        try {
            /*stopLoading();*/
            nutritionList = new ArrayList<>();
            nutritionList = dbHelper.getAllShoppingList();
            if (nutritionList != null) {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "nutrition shopping cart is not null ");
                }
                if (nutritionList.size() > 0) {
                    /*if (dbHelper == null) {
                        dbHelper = new DBHelper(getContext());
                    }*/
                    ArrayList<CheckedUncheckedIngredientModel> checkedUncheckedIngredientModels = new ArrayList<>();
                    int count = 0;
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {*/
                    for (int i = 0; i < recipes.size(); i++) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "nutrtion ID is in shopping list " + recipes.get(i).getId());
                        }
                        int serving = 1;
                        List<CheckUncheckDbModel> checkedDBModel = new ArrayList<>();
                        int checkedCount = dbHelper.areCheckedAvailableForUploading(recipes.get(i).getId());
                        ArrayList<String> checkedIngredients = new ArrayList<>();
                        List<CheckUncheckDbModel> uncheckedDBModel = new ArrayList<>();
                        int uncheckedCount = dbHelper.areUnCheckedAvailableForUploading(recipes.get(i).getId());
                        ArrayList<String> uncheckedIngredients = new ArrayList<>();
                        if (checkedCount > 0 || uncheckedCount > 0) {
                            /*checkedList.addAll(checkedDBModel);*/
                            checkedDBModel = dbHelper.getAllCheckedIngredientByNutritionID(recipes.get(i).getId());

                            for (int j = 0; j < checkedDBModel.size(); j++) {
                                serving = checkedDBModel.get(j).getServing();
                                checkedIngredients.add("" + checkedDBModel.get(j).getIngredient_id());
                            }

                            uncheckedDBModel = dbHelper.getAllUnCheckedIngredientByNutritionID(recipes.get(i).getId());
                            for (int j = 0; j < uncheckedDBModel.size(); j++) {
                                serving = uncheckedDBModel.get(j).getServing();
                                uncheckedIngredients.add("" + uncheckedDBModel.get(j).getIngredient_id());
                            }
                        } else {
                            loadDataFromServer = true;
                        }
                        /*unCheckedList.addAll(uncheckedDBModel);*/
                        if (checkedIngredients.size() > 0 || uncheckedIngredients.size() > 0) {
                            CheckedUncheckedIngredientModel checkedModel = new CheckedUncheckedIngredientModel(recipes.get(i).getId(), checkedIngredients, uncheckedIngredients, serving);
                            checkedUncheckedIngredientModels.add(checkedModel);
                        } else {
                            loadDataFromServer = true;
                        }
                    }
                        /*}
                    }, 0);*/

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Checked and unchecked List from DB " + checkedUncheckedIngredientModels.toString());
                    }
                    if (checkedUncheckedIngredientModels.size() > 0) {
                        String JSON_Recipe = gson.toJson(checkedUncheckedIngredientModels);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "JSON Request Checked and Unchecked: " + JSON_Recipe);
                        } // System.out.println(JSON_Recipe+"JSON ResponseCheck");

                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            sendCheckedUncheckedListToServer(JSON_Recipe);
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "No Internet Connection");
                            }
                        }


                    } else {
                        if (isAdded() && getContext() != null) {
                            if (SessionUtil.isShoppingLoad(getContext())) {
                                isAlreadyLoading = false;
                                loadShoppingListData();
                            }
                        }
                    }

                } else {
                    if (isAdded() && getContext() != null) {
                        if (SessionUtil.isShoppingLoad(getContext())) {
                            isAlreadyLoading = false;
                            loadShoppingListData();
                        }
                    }
                }
            } else {
                if (isAdded() && getContext() != null) {
                    if (SessionUtil.isShoppingLoad(getContext())) {
                        isAlreadyLoading = false;
                        loadShoppingListData();
                    }
                }
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "nutrition shopping cart is null from local db");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // handler.removeCallbacks(runnable);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        handlerflag = true;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (checkedCheckList == null) {
            checkedCheckList = new ArrayList<>();
        }
        if (CheckedUnCheckList == null) {
            CheckedUnCheckList = new ArrayList<>();
        }
        // Inflate the layout for this
        //start handler as fragment become visible
        // startimer();

        return inflater.inflate(R.layout.fragment_shopping, container, false);

    }

    void startimer() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                manageCheckedUncheckedIngredientsFromDB();
                handler.postDelayed(runnable, delay);
            }
        }, delay);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        mContext = getContext();
        gson = new Gson();

        init();

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(requireContext(), view);

                popup.setOnMenuItemClickListener(ShoppingFragment.this);
                popup.inflate(R.menu.menu_option);
                popup.show();
            }
        });

        mShoppingcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new WeekWiseNutritonFragment();
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) getContext())
                        .getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                ArrayList<String> recipes = new ArrayList<String>();
                recipes.addAll(recipeIds);
                bundle.putStringArrayList("shoppingRecipeIds", recipes);

                fragment.setArguments(bundle);
                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            }
        });


    }


    private void init() {
        try {
            initializeCheckedListFromLastArray();
            blurView = view1.findViewById(R.id.blurView);
            loading_lav = view1.findViewById(R.id.loading_lav);
            //swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshShopping);
            currentUserId = SharedData.id;
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "UserID: " + currentUserId);
            }
            dbHelper = new DBHelper(requireContext());
            mAddToShoppingList = view1.findViewById(R.id.btnAddToShoppingList);
            mNutritionName = view1.findViewById(R.id.textViewNutritionName);
            backArrow = view1.findViewById(R.id.backArrow);
            menuIcon = view1.findViewById(R.id.menu_icon);
            mShoppingcart = view1.findViewById(R.id.shoppingCart);
            categoriesRR = view1.findViewById(R.id.categoriesRR);
            shoppingTitleTV = view1.findViewById(R.id.shoppingTitleTV);

            mConstraintLayout = view1.findViewById(R.id.shopping_fragment);
            empty_list_ll = view1.findViewById(R.id.empty_list_ll);
            recipeUpdateModels = new ArrayList<>();
            unCheckRecipeUpdateList = new ArrayList<>();
            mAddToShoppingListRecyclerview = view1.findViewById(R.id.addToShoppingListRecyclerview);


            nutritionlayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            categoryLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);


            shoppingDataModels.clear();
            allIngredientsList.clear();
            fruitIngredientsList.clear();
            meatIngredientsList.clear();
            dairyIngredientsList.clear();
            generalIngredientsList.clear();
            vegitableIngredientsList.clear();
            recipes.clear();
            categories.clear();
            if (isAdded()) {
                if (getContext() != null) {
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {*/
                    recipes = dbHelper.getAllShoppingList();
                    categories = dbHelper.getCategories();
                        /*}
                    }, 0);*/

                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        //List of recipes
                        //loadShoppingListData();
                    /*dbHelper.clearShoppingListItems();
                    dbHelper.clearShoppingRecipesCategories();
                    dbHelper.clearShoppingRecipesIngredients();*/
                        if (recipes != null && recipes.size() > 0) {
                            /*recipes = dbHelper.getAllShoppingList();*/
                            setRecipeAdapter(recipes);
                        }
                        //List of categories
                        if (categories != null && categories.size() > 0) {
                            /*categories = dbHelper.getCategories();*/
                            setCategoryAdapter(categories);

                        } else {
                            loadShoppingListData();
                        }


                    } else {
                        //List of recipes
                        if (recipes != null && recipes.size() > 0) {
                            /*recipes = dbHelper.getAllShoppingList();*/
                            setRecipeAdapter(recipes);
                        } else {
                            //DATA Base Is Empty
                            recipes = nutritionShoppingDateModel.getData().getRecipes();
                            setRecipeAdapter(recipes);
                            showToast(resources.getString(R.string.no_internet_connection));
                        }

                        //List of categories
                        if (categories != null && categories.size() > 0) {
                            /*categories = dbHelper.getCategories();*/
                            setCategoryAdapter(categories);

                        } else {
                            //DATA Base Is Empty
                            showToast(resources.getString(R.string.no_internet_connection));
                        }
                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Shopping Fragment: getContext() == null in init()");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Shopping Fragment: Activity is not added in init()");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

        shoppingTitleTV.setText(resources.getString(R.string.shopping_list));

    }

    void showToast(String message) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    if (message != null)
                        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Fragement is not added to activity");
                }
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_ShowToast",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(exception));
            }
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.clear_items:
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    if (dbHelper.areCheckedIngredientAvailableInDB() > 0) {
                        unCheckAllCheckedItems();
                    }
                    /*int uncheckedCount = dbHelper.areCheckedUnCheckedAvailableForUploading();
                    if (uncheckedCount > 0) {
                        dbHelper.uncheckAllCheckedIngredients();
                        //loadShoppingListData();
                        unCheckAllCheckedItems();
                    } else {
                        unCheckAllCheckedItems();
                    }*/
                } else {
                    if (getContext() != null) {
                        if (getResources() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.no_internet_connection), LENGTH_SHORT).show();
                        }
                    }
                }

                return true;
            case R.id.clear_whole_list:
                //working on it
                if (getContext() != null) {
                    if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                        deleteAllDataFromList();
                    } else {
                        Toast.makeText(getContext(),resources.getString(R.string.no_internet_connection), LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                return false;
        }
    }

    private void unCheckAllCheckedItems() {

        Call<ChecklistModel> call = ApiClient.getService().unCheckAllCheckedItems("Bearer " + SharedData.token);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    isAlreadyLoading = false;
                    loadShoppingListData();
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    ChecklistModel checklistModelresponse = new ChecklistModel();
                    if (Common.isLoggingEnabled) {
                        Log.e(TAG, "Response Code is " + response.code());
                    }
                    try {
                        if (response.errorBody() != null) {
                            checklistModelresponse = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        /*new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_uncheckAllItems",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                      */  if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }

                    if (response.code() == 400) {
                        if (getContext() != null) {
                            if (checklistModelresponse != null && checklistModelresponse.getMessage() != null) {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, checklistModelresponse.getMessage().toString());
                                }
                                loadShoppingListData();
                                /*Toast.makeText(getContext(), checklistModelresponse.getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();*/
                            }
                        }

                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(TAG, "Response Status " + message.toString());
                        }
                        //  Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_uncheckAllItems",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }


    private void deleteAllDataFromList() {
        startLoading();
        blurrBackground();
        Call<ChecklistModel> call = ApiClient.getService().deleteAllShoppingList("Bearer " + SharedData.token/*, SharedData.id, recipes*/);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    /*     if (response.body().getStatus()) {*/
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    // delete from shared pref
                    //ToDo
                    //clearAndDeleteCheckedItemsFromPref();
                    //manageEmptyShoppingList();
                    dbHelper.deleteAllShoppingListData(Integer.parseInt(currentUserId));
                    dbHelper.clearShoppingListItems();
                    dbHelper.clearShoppingRecipesCategories();
                    dbHelper.clearShoppingRecipesIngredients();
                    dbHelper.clearCheckUncheck();
                    nutritionShoppingDateModel = null;
                    //loadShoppingListData();
                    manageEmptyShoppingList();

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    ChecklistModel checklistModelresponse = new ChecklistModel();
                    try {
                        if (response.errorBody() != null) {
                            if (response.errorBody() != null) {
                                checklistModelresponse = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                            }
                        }
                    } catch (IOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_deleteShoppingList",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }

                    if (response.code() == 401) {
                        if (getContext() != null) {
                            if (checklistModelresponse != null && checklistModelresponse.getMessage() != null) {
                                Toast.makeText(getContext(), checklistModelresponse.getMessage().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                stopLoading();
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                stopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_deleteAllList_Failure",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });


        // Toast.makeText(getContext(), "All data deleted from shopping list", LENGTH_SHORT).show();
    }

    private void loadShoppingListData() {
        nutritionShoppingDateModel = null;
        dbHelper.clearShoppingListItems();
        dbHelper.clearShoppingRecipesCategories();
        dbHelper.clearShoppingRecipesIngredients();
        dbHelper.clearCheckUncheck();
        if (!isAlreadyLoading) {
            blurrBackground();
            startLoading();
        }

        Call<NutritionDataModel> call = ApiClient.getService().getShoppingList("Bearer " + SharedData.token, currentUserId);
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    //stopLoading();
                    nutritionShoppingDateModel = response.body();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(false, getContext());
                    }
                    if (loadDataFromServer) {
                        loadDataFromServer = false;
                    }

                    if (nutritionShoppingDateModel != null) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Shopping List Data retrieved from server: " + nutritionShoppingDateModel.toString());

                        }
                        if (nutritionShoppingDateModel.getData() != null) {
                            if (nutritionShoppingDateModel.getData().getRecipes() != null) {
                                if (nutritionShoppingDateModel.getData().getRecipes().size() > 0) {

                                    //add List to local db
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), resources.getString(R.string.shopping_list_updated), LENGTH_SHORT).show();
                                    }
                                    addAllShoppingToDb(nutritionShoppingDateModel);


                                    List<NutritionDataModel.Recipe> shoppingRecipeList = new ArrayList<>();
                                    // shoppingRecipeList = dbHelper.getAllShoppingList();
                                    //List<IngredientsDataModel> listIngredients=new ArrayList<>();
                                    // listIngredients= dbHelper.getIngredients();
                                    // dbHelper.getCategories();
                                    if (Common.isLoggingEnabled) {
                                        Log.d(TAG, "ShoppingList " + shoppingRecipeList.toString());
                                    }//Log.d("ShoppingList", listIngredients.toString());

                                    getAllShoppingListsRecipesIds(nutritionShoppingDateModel);

                                    getAllRecipesCategoriesFromDbAndPass(nutritionShoppingDateModel);

                                } else {
                                    manageEmptyShoppingList();
                                    if (Common.isLoggingEnabled) {
                                        Log.e(TAG, "Nutrition Shopping recipe list size is 0");
                                    }
                                }
                            } else {
                                manageEmptyShoppingList();
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Nutrition Shopping getRecipe is null");
                                }
                            }

                        } else {
                            manageEmptyShoppingList();
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Nutrition Shopping getData is null");
                            }
                        }

                    } else {
                        manageEmptyShoppingList();
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Nutrition Shopping Data Model is null");
                        }
                    }
                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 404) {
                    manageEmptyShoppingList();
                } else {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), LENGTH_SHORT).show();
                    }
                    // stopLoading();
                    /*try {
                     *//*Gson gson = new GsonBuilder().create();*//*
                        //NutritionDataModel nutritionJSON_Response = new NutritionDataModel();
                       // nutritionShoppingDateModel = gson.fromJson(response.errorBody().string(), NutritionDataModel.class);
                        *//*if (response.code() == 404) {
                            manageEmptyShoppingList();
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        }*//*
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        *//*if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_getShoppingList",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }*//*
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                    }*/

                }

                /*if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                }*/
                stopLoading();
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_getShoppingList",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                manageEmptyShoppingList();
                stopLoading();
                // stopLoading();
            }
        });
    }

    private void getAllRecipesCategoriesFromDbAndPass(NutritionDataModel nutritionShoppingDateModel) {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        //List of recipes
        recipes = dbHelper.getAllShoppingList();
        if (recipes != null && recipes.size() > 0) {
            setRecipeAdapter(recipes);
        } else {
            if (nutritionShoppingDateModel != null && nutritionShoppingDateModel.getData() != null
                    && nutritionShoppingDateModel.getData().getRecipes() != null) {
                recipes = nutritionShoppingDateModel.getData().getRecipes();
                setRecipeAdapter(recipes);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "ShoppingFragment:getAllRecipesCategoriesFromDbAndPass: nutritionShoppingDateModel==null OR nutritionShoppingDateModel.getData()==null " +
                            " OR nutritionShoppingDateModel.getData().getRecipes()==null AND recipes == null");
                }
            }
        }

        //List of categories
        categories = dbHelper.getCategories();
        if (categories != null && dbHelper.getAllShoppingList().size() > 0) {
            //categories = dbHelper.getCategories();
            setCategoryAdapter(categories);
        } else {
            if (nutritionShoppingDateModel != null && nutritionShoppingDateModel.getData() != null
                    && nutritionShoppingDateModel.getData().getRecipes() != null) {
                categories = nutritionShoppingDateModel.getData().getCategories();
                setCategoryAdapter(categories);
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "ShoppingFragment:getAllRecipesCategoriesFromDbAndPass: nutritionShoppingDateModel==null OR nutritionShoppingDateModel.getData()==null " +
                            " OR nutritionShoppingDateModel.getData().getRecipes()==null AND categories == null");
                }
            }
        }
            /*}
        }, 0);*/
    }

    private void setCategoryAdapter(List<NutritionDataModel.Category> categories) {
        categoryAdpater = new CategoryWiseIngredientAdapter(getActivity(), categories, /*getAllIngredients(nutritionShoppingDateModel),*/ ShoppingFragment.this, "cart", 0, resources);
        categoriesRR.setHasFixedSize(true);
        categoriesRR.setLayoutManager(categoryLayoutManager);
        categoriesRR.setAdapter(categoryAdpater);
    }

    private void setRecipeAdapter(List<NutritionDataModel.Recipe> recipes) {
        recipeAdapter = new ShoppingListRecipeAdapter(getActivity(), recipes, resources);
        recipeAdapter.setShoppingItemDeleteCallback(ShoppingFragment.this);
        mAddToShoppingListRecyclerview.setHasFixedSize(true);
        mAddToShoppingListRecyclerview.setLayoutManager(nutritionlayoutManager);
        mAddToShoppingListRecyclerview.setAdapter(recipeAdapter);
    }

    private void addAllShoppingToDb(NutritionDataModel nutritionShoppingDateModel) {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        if (nutritionShoppingDateModel.getData().getRecipes() != null) {
            //dbHelper.clearShoppingRecipesIngredients();
            for (int i = 0; i < nutritionShoppingDateModel.getData().getRecipes().size(); i++) {
                if (nutritionShoppingDateModel.getData().getRecipes().get(i) != null) {
                    if (nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients() != null) {
                        for (int j = 0; j < nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().size(); j++) {
                            if (nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getCategory() == null) {
                                isCategoryNullInIngredient = true;
                            }
                            //Add recipe ID and serving in ingredient model
                            if (nutritionShoppingDateModel.getData().getRecipes().get(i).getServing() != null &&
                                    !nutritionShoppingDateModel.getData().getRecipes().get(i).getServing().matches("")) {
                                nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).setServing(nutritionShoppingDateModel.getData().getRecipes().get(i).getServing());
                            } else {
                                nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).setServing("1");
                            }
                            nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).setRecipeID(nutritionShoppingDateModel.getData().getRecipes().get(i).getId());

                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "RecipeID: " + nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getRecipeID() + "\n Serving: " + nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getServing());
                            }
                            /*if (!dbHelper.checkIngredientID(String.valueOf(nutritionShoppingDateModel.getData().getRecipes()
                                    .get(i).getIngredients().get(j).getId()))) {*/

                            //}
                            String server_CheckedStatus = nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getStatus();
                            if (!dbHelper.isCheckUncheckExist(nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getId(), nutritionShoppingDateModel.getData().getRecipes().get(i).getId())) {

                                dbHelper.addCheckUncheckIngredient(nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getId(),
                                        nutritionShoppingDateModel.getData().getRecipes().get(i).getId(),
                                        Integer.parseInt(nutritionShoppingDateModel.getData().getRecipes().get(i).getServing()),
                                        nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients().get(j).getStatus(), server_CheckedStatus,
                                        true);
                            }

                        }
                        dbHelper.addOrUpdateShoppingIngredient(nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients());

                        if (!dbHelper.checkShoppingRecipeId(String.valueOf(nutritionShoppingDateModel.getData().getRecipes().get(i).getId()))) {
                            dbHelper.addUserShoppingList(nutritionShoppingDateModel);
                        }
                    }
                }
            }

        }

        if (nutritionShoppingDateModel.getData().getCategories() != null) {
            if (isCategoryNullInIngredient) {
                NutritionDataModel.Category cat = new NutritionDataModel.Category();
                cat.setId(0);
                cat.setName(resources.getString(R.string.other));
                nutritionShoppingDateModel.getData().getCategories().add(cat);
            }
            if (nutritionShoppingDateModel.getData().getCategories().size() > 0) {
                for (int i = 0; i < nutritionShoppingDateModel.getData().getCategories().size(); i++) {
                    if (!dbHelper.checkCategoryByID(String.valueOf(nutritionShoppingDateModel.getData().getCategories().get(i).getId()))) {
                        dbHelper.addOrUpdateCategory(nutritionShoppingDateModel.getData().getCategories());
                    }
                }
            }

        }
            /*}
        }, 0);*/


      /*  for (int i = 0; i < nutritionShoppingDateModel.getData().getRecipes().size(); i++) {

            dbHelper.addIngredient(nutritionShoppingDateModel.getData().getRecipes().get(i).getIngredients());
        }*/

    }

    List<NutritionDataModel.Ingredient> getAllIngredients(NutritionDataModel nutritionDataModel) {
        List<NutritionDataModel.Ingredient> ingredientList = new ArrayList<>();
        List<NutritionDataModel.Recipe> recipeListDB = dbHelper.getAllShoppingList();
        if (recipeListDB != null && recipeListDB.size() > 0) {
            for (int i = 0; i < recipeListDB.size(); i++) {
                List<NutritionDataModel.Ingredient> ingredientFromDB = dbHelper.getShoppingIngredients(String.valueOf(recipeListDB.get(i).getId()));
                ingredientList.addAll(ingredientFromDB);
            }
        } else {
            if (nutritionDataModel != null) {
                for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                    for (int j = 0; j < nutritionDataModel.getData().getRecipes().get(i).getIngredients().size(); j++) {
                        NutritionDataModel.Ingredient ingredient = nutritionDataModel.getData().getRecipes().get(i).getIngredients().get(j);
                        ingredient.setRecipeID(nutritionDataModel.getData().getRecipes().get(i).getId());
                        ingredient.setServing(nutritionDataModel.getData().getRecipes().get(i).getServing());
                        ingredientList.add(ingredient);
                    }
                }
            }
        }
        return ingredientList;
    }


    private void loadAllShoppingListData(List<NutritionDataModel> nutritionShoppingList) {

        // ShoppingFragment.nutritionShoppingList = dbHelper.getAllShoppingList();
        for (int i = 0; i < nutritionShoppingList.size(); i++) {
            if (nutritionShoppingList.get(i).getData().getCategories().size() == 0) {
                //manageEmptyShoppingList();
                /*   Toast.makeText(getContext(),"Shopping List is Empty", LENGTH_SHORT).show();*/
            } else {
                for (int j = 0; j < nutritionShoppingList.get(i).getData().getRecipes().size(); j++) {
                    shoppingDataModels.add(new ShoppingDataModel(
                            nutritionShoppingList.get(i).getData().getRecipes().get(j).name,
                            nutritionShoppingList.get(i).getData().getRecipes().get(j).imageURL,
                            nutritionShoppingList.get(i).getData().getRecipes().get(j).id));

                    //save recipe ids to hashset
                    recipeIds.add(String.valueOf(nutritionShoppingList.get(i).getData().getRecipes().get(j).id));

                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Set of String: " + recipeIds);
                    }


                    for (int k = 0; k < nutritionShoppingList.get(i).data.getRecipes().get(j).getIngredients().size(); k++) {
                        double quantity = Double.parseDouble(String.valueOf((nutritionShoppingList.get(i).data.getRecipes().get(j).getIngredients().get(k).quantity)));
                        //int intake = Integer.parseInt(ShoppingFragment.nutritionShoppingList.get(i).getIntake());
                        //  quantity *= intake;
                        if (allIngredientsList.isEmpty()) {
                            allIngredientsList.add(new ShoppingDetailsModel(
                                    nutritionShoppingList.get(i).data.getRecipes().get(j).getIngredients().get(k).ingredient,
                                    k,//set ingrd id
                                    String.valueOf(quantity),
                                    nutritionShoppingList.get(i).data.getRecipes().get(j).getIngredients().get(k).category));
                            categorylist.add(nutritionShoppingList.get(i).data.getRecipes().get(j).getIngredients().get(k).category);
                        }
                        //else {
                        //  checkAlredyExist(ShoppingFragment.nutritionShoppingList.get(i).ingredients.get(j), intake);
                        //  }
                    }
                }

            }
            //SortIngredientsCategories(allIngredientsList);
            // manageEmptyShoppingList();
        }


    }


    private void getAllShoppingListsRecipesIds(NutritionDataModel nutritionShoppingDateModel) {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        for (int i = 0; i < nutritionShoppingDateModel.getData().getRecipes().size(); i++) {
            recipeIds.add(String.valueOf(nutritionShoppingDateModel.getData().getRecipes().get(i).getId()));
        }
            /*}
        }, 0);*/
    }

    private void SortIngredientsCategories(List<ShoppingDetailsModel> allIngredientsList) {
        for (int i = 0; i < categorylist.size(); i++) {
            String categoryType = categorylist.get(i).toString();

            for (int j = 0; j < allIngredientsList.size(); j++) {
                if (allIngredientsList.get(j).getNutritionShoppingCategoryId().contains(categoryType)) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, allIngredientsList.get(i).toString() + "new data");
                        Log.d(TAG, allIngredientsList.get(i).toString() + "new data");
                    }
                }
            }


            if (categorylist.get(i).equals(allIngredientsList.get(i).getNutritionShoppingCategoryId())) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, categorylist.get(i).toString() + "check List");
                }
                fruitIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId().equals(categorylist)) {
                vegitableIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId().equals("Fresh Fruits")) {
                meatIngredientsList.add(allIngredientsList.get(i));
            }/* else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 4) {
                dairyIngredientsList.add(allIngredientsList.get(i));
            } else if (allIngredientsList.get(i).getNutritionShoppingCategoryId() == 5) {
                generalIngredientsList.add(allIngredientsList.get(i));
            }*/
        }
        // hideEmptyCategory();
    }


    private void manageEmptyShoppingList() {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        if (nutritionShoppingDateModel != null) {
            if (nutritionShoppingDateModel.getData() != null) {
                if (nutritionShoppingDateModel.getData().getRecipes() != null) {
                    if (nutritionShoppingDateModel.getData().getRecipes().size() > 0) {
                        mAddToShoppingListRecyclerview.setVisibility(View.VISIBLE);
                        categoriesRR.setVisibility(View.VISIBLE);
                        empty_list_ll.setVisibility(View.GONE);
                    } else {
                        empty_list_ll.setVisibility(View.VISIBLE);
                        mAddToShoppingListRecyclerview.setVisibility(View.GONE);
                        categoriesRR.setVisibility(View.GONE);
                        clearAndDeleteCheckedItemsFromPref();
                    }
                } else {
                    empty_list_ll.setVisibility(View.VISIBLE);
                    mAddToShoppingListRecyclerview.setVisibility(View.GONE);
                    categoriesRR.setVisibility(View.GONE);
                    clearAndDeleteCheckedItemsFromPref();
                }
            } else {
                empty_list_ll.setVisibility(View.VISIBLE);
                mAddToShoppingListRecyclerview.setVisibility(View.GONE);
                categoriesRR.setVisibility(View.GONE);
                clearAndDeleteCheckedItemsFromPref();
            }
        } else {
            empty_list_ll.setVisibility(View.VISIBLE);
            mAddToShoppingListRecyclerview.setVisibility(View.GONE);
            categoriesRR.setVisibility(View.GONE);
            clearAndDeleteCheckedItemsFromPref();
        }
           /* }
        }, 0);*/


    }

    private void clearAndDeleteCheckedItemsFromPref() {
        if (ingridentsCheckedlist != null) {
            ingridentsCheckedlist.clear();
        }
        if (isAdded() && getContext() != null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(currentUserId, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

    }

    @Override
    public void onLongDeleteClickListener(int position) {

        //ToDO
        // loadAllShoppingListData();
        // updateList();

    }

    private void updateList() {
        nutritionDetailsAdapter1.notifyDataSetChanged();
        nutritionDetailsAdapter2.notifyDataSetChanged();
        nutritionDetailsAdapter3.notifyDataSetChanged();
        nutritionDetailsAdapter4.notifyDataSetChanged();
        nutritionDetailsAdapter5.notifyDataSetChanged();
        recipeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedListener(String ingredientId, boolean checkedState,
                                  RecipeUpdateModel recipeUpdateModel) {

        if (getContext() != null) {
            SessionUtil.setShoppingLoading(true, getContext());
        }

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);


    }

    @Override
    public void onUnCheckedListener(String ingredientId, boolean checkedState, RecipeUpdateModel recipeUpdateModel) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
        if (getContext() != null) {
            SessionUtil.setShoppingLoading(true, getContext());
        }


    }


    private void sendUnCheckedListToServer(String json_recipe) {
       /* if (isAdded() && getContext() != null) {
            if (Common.isLoggingEnabled) {
                Toast.makeText(getContext(), "Un-Checked Ingredients: " + json_recipe, Toast.LENGTH_LONG).show();
            }
        }*/
        Call<ChecklistModel> call = ApiClient.getService().unCheckShoppingListItem("Bearer " + SharedData.token, currentUserId, json_recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null) {
                            Log.d(TAG, "Response Status " + message.toString());
                        }
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "JSON ResponseCheck0:");
                    }
                    unCheckRecipeUpdateList.clear();
                    ChecklistModel checklistModel = response.body();
                    loadShoppingListData();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Response message is null");
                            }
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.e(TAG, "Response is not successful");
                    }

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadShoppingListData();
                    /*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*/
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null)
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null)
                            new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_sendUncheckListToServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        if (Common.isLoggingEnabled)
                            ex.printStackTrace();
                        if (isAdded() && getContext() != null)
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
                //handler2.removeCallbacks(runn);
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                // handler2.removeCallbacks(runn);
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null)
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_unCheckListToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
            }
        });
    }

    private void sendCheckedUncheckedListToServer(String JSON_Recipe) {
        blurrBackground();
        startLoading();

        Call<CheckedUncheckedResponseModel> call = ApiClient.getService().checkedUncheckedShoppingListItem("Bearer " + SharedData.token, currentUserId, JSON_Recipe);
        call.enqueue(new Callback<CheckedUncheckedResponseModel>() {
            @Override
            public void onResponse(Call<CheckedUncheckedResponseModel> call, Response<CheckedUncheckedResponseModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    recipeUpdateModels.clear();
                    if (isAdded() && getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            isAlreadyLoading = true;
                            loadShoppingListData();
                        }
                    }


                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isAdded() && getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            loadShoppingListData();
                        }
                    }
                    /*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*/
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.e(TAG, "Response Status " + message.toString());
                            }
                            //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null) {
                            new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_sendCheckedListToServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        }
                        if (Common.isLoggingEnabled) {
                            ex.printStackTrace();
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                // handler1.removeCallbacks(run);
            }

            @Override
            public void onFailure(Call<CheckedUncheckedResponseModel> call, Throwable t) {
                isAlreadyLoading = false;
                stopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_sendCheckListToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void sendCheckedListToServer(String JSON_Recipe) {
        /*if (isAdded() && getContext() != null) {
            if (Common.isLoggingEnabled) {
                Toast.makeText(getContext(), "Checked Ingredients: " + JSON_Recipe, Toast.LENGTH_LONG).show();
            }
        }*/
        Call<ChecklistModel> call = ApiClient.getService().checkShoppingListItem("Bearer " + SharedData.token, currentUserId, JSON_Recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (message != null)
                        Log.d(TAG, "Response Status " + message.toString());
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    if (getContext() != null) {
                        SessionUtil.setShoppingLoading(true, getContext());
                    }
                    if (Common.isLoggingEnabled)
                        Log.d(TAG, "JSON ResponseCheck:");
                    recipeUpdateModels.clear();
                    loadShoppingListData();
                    ChecklistModel checklistModel = response.body();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {

                            if (isAdded() && getContext() != null)
                                Toast.makeText(getContext(), checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (Common.isLoggingEnabled)
                                Log.e(TAG, "Response message is null");
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.e(TAG, "Response is not successful");
                    }

                } else if (response.code() == 401) {
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadShoppingListData();
                    /*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*/
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (isAdded() && getContext() != null)
                                Toast.makeText(getContext(), "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (getContext() != null)
                            new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_sendCheckedListToServer",
                                    SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                        if (Common.isLoggingEnabled)
                            ex.printStackTrace();
                        if (isAdded() && getContext() != null)
                            Toast.makeText(getContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
                // handler1.removeCallbacks(run);
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null)
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_sendCheckListToServer",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
            }
        });
    }


    public void initializeCheckedListFromLastArray() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(currentUserId, Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.

        String json = sharedPreferences.getString("checkedList", "null");
        String json1 = sharedPreferences.getString("unCheckedList", "null");

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        // below line is to get the type of our array list.
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        if (gson.fromJson(json, type) != null) {
            ingridentsCheckedlist.clear();
            ingridentsCheckedlist = gson.fromJson(json, type);
        }
        if (gson.fromJson(json1, type1) != null) {
            ingridentsUnCheckedlist.clear();
            ingridentsUnCheckedlist = gson.fromJson(json, type);
        }

        // checking below if the array list is empty or not
    }


    @Override
    public void deleteItemCallback(String recipeID, int position) {
        manageCheckedUncheckedIngredientsFromDB();
        deleteRecipe(recipeID, position);
    }

    private void deleteRecipe(String recipeID, int position) {
        blurrBackground();
        startLoading();

        Call<NutritionDataModel> call = ApiClient.getService().deleteShoppingList("Bearer " + SharedData.token, recipeID);
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                if (response.isSuccessful()) {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    stopLoading();
                    /*nutritionShoppingDateModel.getData().getRecipes().remove(position);*/
                    try {
                        dbHelper.deleteSpecificShoppingListData(Integer.parseInt(recipeID), Integer.parseInt(SharedData.id));
                        loadShoppingListData();
                    } catch (Exception e) {
                        if (Common.isLoggingEnabled) {
                            e.printStackTrace();
                        }
                    }
                } else if (response.code() == 401) {
                    stopLoading();
                    if (getContext() != null) {
                        LogoutUtil.redirectToLogin(getContext());
                        Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                    if (Common.isLoggingEnabled) {
                        if (message != null)
                            Log.e(TAG, "Response Status " + message.toString());
                    }
                    //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                    stopLoading();
                }
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("ShoppingFragment_deleteRecipe",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });


        // Toast.makeText(getContext(), "All data deleted from shopping list", LENGTH_SHORT).show();
    }

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

                if (loading_lav != null) {
                    loading_lav.setVisibility(View.VISIBLE);
                }

                loading_lav.setVisibility(View.VISIBLE);
                loading_lav.playAnimation();
            }
        }
    }

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                isInteractionEnable = false;
                requireActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    private void stopLoading() {

        if (!isInteractionEnable) {
            blurView.setVisibility(View.GONE);

            //Enable user interaction
            Activity activity = getActivity();
            try {
                if (isAdded() && activity != null) {
                    isInteractionEnable = true;
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            } catch (ActivityNotFoundException e) {
                FirebaseCrashlytics.getInstance().recordException(e);

                if (Common.isLoggingEnabled) {
                    e.printStackTrace();
                }
            }

            //loading_lav.pauseAnimation();
            loading_lav.cancelAnimation();
            loading_lav.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        handler.postDelayed(runnable, delay);
        return true;
    }
}