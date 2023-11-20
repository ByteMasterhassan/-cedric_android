package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;
import static com.cedricapp.activity.LoginActivity.SHARED_PREF_NAME;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.activity.HomeActivity;
import com.cedricapp.adapters.IngredientsAdapter;
import com.cedricapp.adapters.NutritionMethodAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeIdAndServingJsonModel;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.model.SingleRecipeDataModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ALL")
public class Nutrition_Fragment extends Fragment {
    private ImageButton backArrow;
    private MaterialButton mAddToShoppingListButton;
    private MaterialTextView mNutritionName, totalcaloriesMTV, mNutritionTimeTitle, mPrepareTime, mCooking, mIngredients, mIngredient1, mIngredient2, mIngredient3, mIngredient4, txt_method, txt_ingredients, txt_cook,
            txt_prep, txt_ingredientsList, txt_quantity, txt_fats, txt_kcal, txt_proteins, txt_carbs;


    int count = 5;
    private DBHelper dbHelper;
    ProgressBar loadingPB, loadingPB1;
    private ImageView mNutritionImage;
    DatabaseReference mDatabaseReference;
    RecyclerView mMethodRecyclerView, mIngredientsRecyclerview;
    private static RecyclerView.Adapter adapter;
    IngredientsAdapter ingredientsAdapter;
    public static List<SingleRecipeDataModel> nutritionList = new ArrayList<>();
    /*public static List<DashboardNutrition.Data.Recipe> nutritionDataList = new ArrayList<>();*/
    public static List<SingleRecipeDataModel.Recipe> nutritionDataList = new ArrayList<>();
    public static List<NutritionDataModel> nutritionShoppingList = new ArrayList<>();
    ArrayList<RecipeIdAndServingJsonModel> recipeIdAndServingList = new ArrayList<>();

    private FirebaseAnalytics m_nutrition_FirebaseAnalytics;

    String nName;
    String nPreparationTime;
    String nCookStatus;
    String nImg, cookOptions;
    String nutritionResponse;
    //String day;
    String nutritionTime, recipes_id;
    int dayNumber, weekNumber, goal_id, level_id;
    String method, Json_recipe;
    ArrayList<String> methodArryalist = new ArrayList<String>();
    List<SingleRecipeDataModel.Ingredient> ingredsList = new ArrayList<>();
    // NutritionDataModel ingredsList=new NutritionDataModel();
    SingleRecipeDataModel nutritionDataModel;
    DatabaseReference nutritionIngredientsReference;
    ShimmerFrameLayout shimmerForNutrition;
    private View view1;
    private int clickCounter = 0;
    boolean click = false;
    private Context mContext;
    BlurView blurView;
    LottieAnimationView loading_lav;
    int nutritionID;
    private WeekDaysHelper weekDaysHelper;
    private String toDate;
    SwipeRefreshLayout swipeRefreshLayout;
    private String message;

    Resources resources;
    private String htmlString;

    LinearLayout totalCaloriesLL;

    String TAG = "NUTRITION_TAG";

    boolean isRecipeAlreadyAddedInCart;


    public Nutrition_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weekDaysHelper = new WeekDaysHelper();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.canToastShow = false;
        SharedData.redirectToDashboard = true;
        HomeActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    @Override
    public void onPause() {
        super.onPause();
        //requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nutritions, container, false);
        return v;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();
        m_nutrition_FirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        //dissable user interaction
        // requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        init();

        StartShimmer();

        //getting strings and set on specific views
        getBundledData();

        // Api calling and check network
        checkNetworkAndPopulateData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isAdded()) {
                    if (getContext() != null) {
                        if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                            //StartLoading();
                            //getUserProfileData(currentUserId);
                            if (nutritionID != 0) {
                                StartShimmer();
                                //startLoading();
                                //blurrBackground();
                               /* loadingPB.setVisibility(View.VISIBLE);
                                loadingPB1.setVisibility(View.VISIBLE);*/
                                loadNutritionData(nutritionID);
                            }
                        } else {
                            if (swipeRefreshLayout != null) {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                            if (getContext() != null && getResources() != null)
                                showToast(resources.getString(R.string.no_internet_connection));
                            //StoptShimmer();
                        }
                    } else {
                        if (swipeRefreshLayout != null) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        // showToast("No Internet, Please turn ON Internet");
                    }
                } else {
                    if (swipeRefreshLayout != null) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

            }
        });

        //listener for add to shopping list button
        mAddToShoppingListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //add data to shopping list
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    Bundle nutritionParam = new Bundle();
                    nutritionParam.putInt("NutritionID", nutritionID);
                    m_nutrition_FirebaseAnalytics.logEvent("Recipes", nutritionParam);
                    m_nutrition_FirebaseAnalytics.setUserProperty("Gender", SessionUtil.getUserGender(getContext()));

                    clickCounter = clickCounter + 1;
                    if (clickCounter < 2) {
                        //requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Gson gson = new Gson();
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "JSON Response: " + recipeIdAndServingList.toString());
                        }
                        if (recipeIdAndServingList != null) {
                            Json_recipe = gson.toJson(recipeIdAndServingList);
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "JSON Response: " + Json_recipe);
                            }
                            addDataToShoppingList(Json_recipe);
                            recipeIdAndServingList.clear();
                        }

                    }
                } else {
                    showToast(resources.getString(R.string.no_internet_connection));
                }

                //  Toast.makeText(requireContext(), "Nutrition  added to Shopping List", Toast.LENGTH_SHORT).show();

            }
        });

        //listener for back button
        backArrow.setOnClickListener(v -> {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }
        });

    }

    void showToast(String message) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Fragement is not added to activity");
                }
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("NutritonFragment_showToast", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(exception));
            }
            exception.printStackTrace();
        }
    }

    private void getBundledData() {
        if(getArguments()!=null) {
            nutritionID = getArguments().getInt("nutritionID");

            nutritionTime = getArguments().getString("nutritionTime");

            SharedData.token = getArguments().getString("token");
            SharedData.refresh_token = getArguments().getString("refresh_token");
            // SharedData.token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMTAxMmRiNDQxYzdlODgzODNlZmUzOTQ3MDgxOGI3YzdjMjA1NDEwMDIzNzEzM2VmMDdmMGZlNDkzYjVlZmRjYzQxNDM1ZmU3MTc4Yzg1MWYiLCJpYXQiOjE2NTgxNDYxNjcuNzAyMTEzLCJuYmYiOjE2NTgxNDYxNjcuNzAyMTE2LCJleHAiOjE2ODk2ODIxNjcuNjk3Mjk1LCJzdWIiOiI0Iiwic2NvcGVzIjpbXX0.DctxwgevSLdLsRBLGVaz73P8R4sjECwVpfOdSlcBmD6LnxAhjrQyoqqG-tWwwvRLFyN55-NREGAyVFAXylZ9FolZnJPTg1hishaVIl6QoHFaDUBy1E3GxXU8mwerrRNlJP9SayRGFxKqxdabU3ExOJptmimCOv_6jNwHI-rJ4IHuRNb71xrZf-UFPQabutBJ3eAZBr88YFvb5KqhRKeEwboE6lTG_WlB-0Hwlvn0YcqPYhqEQF10yWmMky0bGP3Jtno7u-0nkNs6rBeN2GtSxLqCa-BRcuwcpHdNaS89ESJ4fq3NwhRNmaGCqUOrc5qc-Kc4kfiF-7MrclciNJjx8nMnTAZFe4LQJQ5q9ehZPiJ6a8Oy1GqGirMCtWjq9LcHfu_pr2jXViPDU8pDLv-A0GhEI4sHk131BDe-sNaGT7w8bEbqvXw7FA4t9QQmyAKKRV0yzaKsl0-9vByMUWYMHYwwek4YN2Aoc6ZECw27hNGIS4o_NqW22D9xP6CSTBDJ7bOyjJgPr63fTm01mVNqra-h3Gdu3qoPsZTKqJX-rTRl5dy_ytq6La_El8zEnZRh8THUNsyqOCSmJetquc9H3hbN6yYRO1g8yzCUIeem068Ryt-mjy0F50mCBdE_7G6v-HlfYCSRggFVnx6xBBYe-HFaMIYuA6-DlJw7CvBmYmE";
            dayNumber = getArguments().getInt("dayNumber");
            weekNumber = getArguments().getInt("weekNumber");
            if (getArguments().getString("level_id") != null) {
                level_id = Integer.parseInt(getArguments().getString("level_id"));
            }
            if (getArguments().getString("goal_id") != null) {
                goal_id = Integer.parseInt(getArguments().getString("goal_id"));
            }

            if(getArguments().getBoolean("isAddedInCart")){
                isRecipeAlreadyAddedInCart = getArguments().getBoolean("isAddedInCart");
            }else{
                isRecipeAlreadyAddedInCart = false;
            }

   /*     weekNumber = 1;
        dayNumber = 1;
        goal_id = 1;
        level_id = 1;*/
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Nutrition ID: " + nutritionID);
                Log.d(TAG, "Nutrition Time: " + nutritionTime);
                Log.d(TAG, "User ID: " + SharedData.id);
                Log.d(TAG, "Level ID : " + level_id);
                Log.d(TAG, "Goal ID  : " + goal_id);
                Log.d(TAG, "weekNumber : " + weekNumber);
                Log.d(TAG, "dayNumber : " + dayNumber);
                Log.d(TAG, "SharedData.token : " + SharedData.token);
            }
        }
    }

    private void init() {

        totalCaloriesLL = view1.findViewById(R.id.totalCaloriesLL);
        totalcaloriesMTV = view1.findViewById(R.id.totalcaloriesMTV);
        swipeRefreshLayout = view1.findViewById(R.id.pullToRefreshNutrition);
        blurView = view1.findViewById(R.id.blurView);
        loading_lav = view1.findViewById(R.id.loading_lav);
        dbHelper = new DBHelper(getContext());
        mContext = getContext();
        backArrow = view1.findViewById(R.id.backArrow);
        loadingPB = view1.findViewById(R.id.progress);
        loadingPB1 = view1.findViewById(R.id.progress1);

        //getting id's
//        mMethodRecyclerView = view1.findViewById(R.id.methodRecyclerview);
        mAddToShoppingListButton = view1.findViewById(R.id.btnAddToShoppingList);
        mNutritionName = view1.findViewById(R.id.textViewNutritionName);
        mNutritionTimeTitle = view1.findViewById(R.id.nutritionTime);
        mNutritionImage = view1.findViewById(R.id.nutritionImage);
        mCooking = view1.findViewById(R.id.textViewCookStatus);
        mPrepareTime = view1.findViewById(R.id.textViewPrepareTime);
        shimmerForNutrition = view1.findViewById(R.id.shimmerForNutrition);
        txt_prep = view1.findViewById(R.id.prep);
        txt_cook = view1.findViewById(R.id.cook);
        txt_ingredients = view1.findViewById(R.id.ingredients);
        txt_method = view1.findViewById(R.id.method);
        txt_ingredientsList = view1.findViewById(R.id.ingredientslist);
        txt_quantity = view1.findViewById(R.id.quantity);
        txt_fats = view1.findViewById(R.id.fats);
        txt_kcal = view1.findViewById(R.id.kcal);
        txt_proteins = view1.findViewById(R.id.proteins);
        txt_carbs = view1.findViewById(R.id.carbs);
        //recyclerview for nutrition Ingredients
        mIngredientsRecyclerview = view1.findViewById(R.id.ingredientsRecyclerview);
        mIngredientsRecyclerview.setHasFixedSize(true);


        //recyclerview for nutrition preparing method
        mMethodRecyclerView = view1.findViewById(R.id.methodRecyclerview);
        mMethodRecyclerView.setHasFixedSize(true);

        setlanguageToWidget();

    }

    private void setlanguageToWidget() {
        txt_prep.setText(resources.getString(R.string.prep));
        txt_cook.setText(resources.getString(R.string.cook));
        txt_ingredients.setText(resources.getString(R.string.ingredient));
        txt_method.setText(resources.getString(R.string.method));
        txt_ingredientsList.setText(resources.getString(R.string.ingredients));
        txt_quantity.setText(resources.getString(R.string.quantity));
        txt_fats.setText(resources.getString(R.string.fats));
        txt_kcal.setText(resources.getString(R.string.kcal));
        txt_proteins.setText(resources.getString(R.string.proteins));
        txt_carbs.setText(resources.getString(R.string.carbs));
    }

    public void checkNetworkAndPopulateData() {

        if (isAdded() && getContext() != null) {
            toDate = SessionUtil.getSelectedDate(getContext());
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Selected Date is " + toDate);
            }
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                int currentDateDifference = WeekDaysHelper.getCountOfDays(weekDaysHelper.getCurrentDateLikeServer(), toDate);
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "Selecter Date Difference is " + currentDateDifference);
                }
                if (currentDateDifference == 1) {
                    if (dbHelper.isRecipeAvailable(String.valueOf(nutritionID))) {
                        populateNutritionData();
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Data not available in DB and retreiving from server");
                        }
                        startLoading();
                        blurrBackground();
                        loadingPB.setVisibility(View.VISIBLE);
                        loadingPB1.setVisibility(View.VISIBLE);
                        loadNutritionData(nutritionID);
                    }
                } else {
                    startLoading();
                    blurrBackground();
                    loadingPB.setVisibility(View.VISIBLE);
                    loadingPB1.setVisibility(View.VISIBLE);
                    if (WeekDaysHelper.getCountOfDays(SessionUtil.getDailyDate(getContext()), WeekDaysHelper.getDateTimeNow_yyyyMMdd()) > 1) {
                        dbHelper.clearRecipes();
                        dbHelper.clearRecipesIngredients();
                    }
                    loadNutritionData(nutritionID);
                }

            } else {
                if (dbHelper.isRecipeAvailable(String.valueOf(nutritionID))) {
                    populateNutritionData();
                } else {
                    if (mContext != null && getResources() != null)
                        Toast.makeText(mContext, resources.getString(R.string.no_internet_connection_no_data_available), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

       /* if (isOnline()) {
            loadNutritionData(dayNumber, level_id, goal_id, weekNumber);
        } else {
            //ToDo needs to be change
            //  nutritionDataList = dbHelper.getAllNutrition();

            if (nutritionDataList.size() != 0) {

                loadNutritionDataFromLocalDb(nutritionDataList);
                mAddToShoppingListButton.setVisibility(View.VISIBLE);
            } else {

                Toast.makeText(mContext, "No internet Connection and ,No data Available", Toast.LENGTH_LONG).show();
            }
        }*/


    private void loadNutritionData(int nutritionID) {
        //dbHelper.clearRecipes();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "NutritionFragement: nutritionID = " + nutritionID);
        }
        Call<SingleRecipeDataModel> call;
        if (SessionUtil.isAPI_V3(mContext)) {
            if(Common.isLoggingEnabled){
                Log.d(TAG,"V3");
            }
            call = ApiClient.getService().nutritionSingleRecipeDataV3("Bearer " + SharedData.token, nutritionID);
        }else{
            if(Common.isLoggingEnabled){
                Log.d(TAG,"V2");
            }
            call = ApiClient.getService().nutritionSingleRecipeData("Bearer " + SharedData.token, nutritionID);
        }
        // on below line we are calling method to enqueue and calling
        // all the data from array list.
        call.enqueue(new Callback<SingleRecipeDataModel>() {
            @Override
            public void onResponse(Call<SingleRecipeDataModel> call, Response<SingleRecipeDataModel> response) {
                try {
                    stopLoading();
                    // inside on response method we are checking
                    // if the response is success or not.
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();

                        //  Log.d("myRes", response.body().getData().toString());
                        nutritionDataModel = null;
                        //nutritionList.clear();
                        nutritionDataModel = response.body();
                        /*if (nutritionDataModel.getStatus() == true) {*/
                        if (nutritionDataModel != null) {
                            //nutritionList.add(nutritionDataModel);
                            if (nutritionDataModel.getData() != null
                                    && nutritionDataModel.getData().getRecipe() != null
                                    && nutritionDataModel.getData().getRecipe().getId() != null) {
                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    nutritionDataModel.getData().getRecipe().setMethods(Html.fromHtml(nutritionDataModel.getData().getRecipe().getMethods(), Html.FROM_HTML_MODE_COMPACT).toString());
                                } else {
                                    nutritionDataModel.getData().getRecipe().setMethods(Html.fromHtml(nutritionDataModel.getData().getRecipe().getMethods()).toString());
                                }*/
                                if (!dbHelper.isRecipeAvailable(String.valueOf(nutritionDataModel.getData().getRecipe().getId()))) {
                                    dbHelper.addIndividualRecipe(nutritionDataModel.getData().getRecipe());
                                } else {
                                    dbHelper.updateIndividualRecipe(nutritionDataModel.getData().getRecipe());
                                }
                                loadingPB.setVisibility(View.GONE);
                                loadingPB1.setVisibility(View.GONE);
                                mAddToShoppingListButton.setVisibility(View.VISIBLE);
                                //nutritionDataModel.getData().getRecipe().setTotalCalories("1 Kcal");
                                if (nutritionDataModel.getData().getRecipe().getTotalCalories() != null) {
                                    if (!nutritionDataModel.getData().getRecipe().getTotalCalories().matches("")) {
                                        totalCaloriesLL.setVisibility(View.VISIBLE);
                                        totalcaloriesMTV.setText(nutritionDataModel.getData().getRecipe().getTotalCalories());
                                    } else {
                                        totalCaloriesLL.setVisibility(View.GONE);
                                    }
                                } else {
                                    totalCaloriesLL.setVisibility(View.GONE);
                                }
                                //Add recipe ingredients into local database
                                if (nutritionDataModel.getData().getRecipe().getIngredients() != null) {
                                    List<NutritionDataModel.Ingredient> ingredients = new ArrayList<>();
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipe().getIngredients().size(); i++) {
                                        NutritionDataModel.Ingredient ingredient = new NutritionDataModel.Ingredient();
                                        ingredient.setId(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getId());
                                        ingredient.setIngredient(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getIngredient());
                                        ingredient.setCalories(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getCalories());
                                        ingredient.setCarbs(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getCarbs());
                                        ingredient.setCategory(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getCategory());
                                        ingredient.setFats(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getFats());
                                        ingredient.setProtein(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getProtein());
                                        ingredient.setQuantity(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getQuantity());
                                        ingredient.setUnit(nutritionDataModel.getData().getRecipe().getIngredients().get(i).getUnit());
                                        ingredient.setRecipeID(nutritionDataModel.getData().getRecipe().getId());
                                        ingredient.setServing("1");
                                        ingredient.setStatus("true");
                                        ingredients.add(ingredient);
                                    }
                                    dbHelper.addOrUpdateIngredient(ingredients);
                                }
                                populateNutritionData();

                            } else {
                                loadingPB.setVisibility(View.GONE);
                                loadingPB1.setVisibility(View.GONE);
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "Nutrition Fragment: nutritionDataModel.getData() is null or nutritionDataModel.getData().getRecipe() or nutritionDataModel.getData().getRecipe().getId()");
                                }
                            }


                            // dbHelper.addRecipe(nutritionDataModel);
                            //nutritionList= dbHelper.getAllRecipes();

                            //System.out.println(nutritionList.toString() + "=====list=======");


                            // add data to local db;

                            //  dbHelper.addRecipe((List<NutritionDataModel.Recipe>) nutritionDataModel);

                            //add ingredients and category to local db
                            //TODO call needs to be changed after single API
                            //addCategoryAndIngridientsInDB(nutritionDataModel);
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "nutritionDataModel is null");
                            }
                        }
                       /* } else if (nutritionDataModel.getStatus() == false) {
                            if (nutritionDataModel.getMessage() != null)

                                Toast.makeText(getContext(), nutritionDataModel.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            if (nutritionDataModel.getMessage().toString().equals("Access token expires!")) {
                                generateNewToken(SharedData.id);
                            }
                        }*/
                        loadingPB.setVisibility(View.GONE);
                        loadingPB1.setVisibility(View.GONE);
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.d(TAG, "Response Status " + message.toString());
                            }
                        }
                        if (message != null) {
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                        loadingPB.setVisibility(View.GONE);
                        loadingPB1.setVisibility(View.GONE);

                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_loadNutrition_API", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                }

                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleRecipeDataModel> call, Throwable t) {
                // in the method of on failure we are displaying a
                // toast message for fail to get data.
                stopLoading();
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_singleRecipeAPI", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (mContext != null) {
                    Toast.makeText(mContext, resources.getString(R.string.failed_to_load_nutrition), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCategoryAndIngridientsInDB(SingleRecipeDataModel nutritionDataModel) {
        //Toast.makeText(getContext(),"Under maintenance",Toast.LENGTH_SHORT).show();
        if (dbHelper != null) {
            String category = " ";
            int recipe_id;
            int category_id, ingrident_id;

            for (int i = 0; i < nutritionDataModel.getData().getCategories().size(); i++) {
                if (!dbHelper.checkCategoryByID("" + nutritionDataModel.getData().getCategories().get(i).getId())) {
                    //TODO single
                    //  dbHelper.addCategory(nutritionDataModel.getData().getCategories());
                }
            }

            //  for (int i = 0; i < nutritionDataModel.getData().getRecipe().size(); i++) {
            if (nutritionDataModel.getData().getRecipe() != null) {

                if (!dbHelper.checkUserRecipeId(String.valueOf(nutritionDataModel.getData().getRecipe().getId()))) {

                    //dbHelper.addRecipe(nutritionDataModel.getData().getRecipes());

                    recipe_id = nutritionDataModel.getData().getRecipe().getId();


                    for (int i = 0; i < nutritionDataModel.getData().getRecipe().getIngredients().size(); i++) {

                        category = nutritionDataModel.getData().getRecipe().getIngredients().get(i).getCategory();
                        ingrident_id = nutritionDataModel.getData().getRecipe().getIngredients().get(i).getId();
                        if (nutritionDataModel.getData().getCategories().size() > 0 && nutritionDataModel.getData().getCategories() != null) {
                            category_id = nutritionDataModel.getData().getCategories().get(i).getId();
                        }

                        //  dbHelper.addCategory(category);

                        //TODO needs to be changed
                        //  dbHelper.addIngredient(nutritionDataModel.getData().getRecipe().getIngredients());

                        //category_id = dbHelper.getCategoryIdFromName(category);

                        // dbHelper.addPivoteTableEntries(recipe_id, ingrident_id, category_id);

                        //Log.d("Category_id", String.valueOf(category_id));


                        //  dbHelper.addPivoteTableEntries(recipe_id,ingrident_id,category_id);
                    }

                }
            }

        }
    }

    private void generateNewToken(String id) {
        Call<SignupResponse> call = ApiClient.getService().getNewToken("Bearer " + SharedData.refresh_token, id);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        SignupResponse responseToken = response.body();
                        if (responseToken.status == true) {

                            SharedData.token = responseToken.data.getAccess_token();
                            SharedData.refresh_token = responseToken.data.getRefresh_token();
                            //update tokens
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", SharedData.token);
                            editor.putString("refresh_token", SharedData.refresh_token);
                            editor.apply();

                            loadNutritionData(nutritionID);


                        } else if (responseToken.status == false) {
                            Toast.makeText(mContext, responseToken.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.d(TAG, "Response Status " + message.toString());
                        }
                        if (message != null)
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_genrateNewToken", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_generateNewToken", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
            }
        });
    }

    //get nutrition data from Local Db
    //ToDo check
    private void loadNutritionDataFromLocalDb
    (List<SingleRecipeDataModel.Recipe> nutritionList) {

        // NutritionDataModel.Recipe nutritionDataModel1 = new NutritionDataModel.Recipe();
       /* for (int i = 0; i < nutritionList.size(); i++) {
            nutritionDataModel1 = nutritionList.get(i);
        }*/
      /*  if (nutritionDataModel1.getDay().equals(dayNumber) && nutritionDataModel1.get.equals(nutritionTime)) {
            List<String> arrayMethod = nutritionDataModel1.method;


            mNutritionName.setText(nutritionDataModel1.name);
            // mPrepareTime.setText(nutritionDataModel.time);
            //mCooking.setText(nutritionDataModel.status);
            try {
                nImg = nutritionDataModel1.imageURL;
                Glide.with(getContext()).load(nImg)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException
                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                StoptShimmer();
                                return false;
                            }
                        })
                        .into(mNutritionImage);
                mAddToShoppingListButton.setVisibility(View.VISIBLE);
                // StoptShimmer();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                StoptShimmer();
                System.out.println("not working...");
            }

            method = (ArrayList<String>) nutritionDataModel1.method;
            System.out.println(method + "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkmmmmmmmmmmmmmm");
            ingredsList = (ArrayList<NutritionDataModel.Ingredients>) nutritionDataModel1.getIngredients();
            System.out.println(ingredsList + "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkmmmmmmmmmmmmmm");


            //set on method adpater
            mMethodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new NutritionMethodAdapter(getContext(), method);
            mMethodRecyclerView.setAdapter(adapter);


            //set on Ingridents adpater
            mIngredientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(getContext(), ingredsList);
            mIngredientsRecyclerview.setAdapter(ingredientsAdapter);

        } else {
            mAddToShoppingListButton.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "No Data available against this data", Toast.LENGTH_SHORT).show();
        }*/
        try {

            loadingPB.setVisibility(View.VISIBLE);
            loadingPB1.setVisibility(View.VISIBLE);

            if (isAdded()) {
                for (int i = 0; i < nutritionList.size(); i++) {

                    /* for (int j = 0; j < nutritionList.size(); j++) {*/
                    if ((nutritionList.get(i).getId() == nutritionID)) {

                        //System.out.println(nutritionList.get(i).getData().getRecipes().get(j).getTitle().toString() + "llll");
                        mNutritionTimeTitle.setText(nutritionList.get(i).getTitle());
                        mNutritionName.setText(nutritionList.get(i).getName());
                        mPrepareTime.setText(nutritionList.get(i).getDuration());
                        nImg = nutritionList.get(i).getImageURL().toString();

                        mCooking.setText(nutritionList.get(i).getCook());
                        ingredsList = nutritionList.get(i).getIngredients();
                        recipes_id = String.valueOf(nutritionList.get(i).getId());
                        recipeIdAndServingList.add(new RecipeIdAndServingJsonModel(Integer.parseInt(recipes_id), 1));
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "JSON Response: " + recipeIdAndServingList.toString());
                        }
                        // dbHelper.isRecipeByIdInShoppingCart(recipes_id);
                        htmlString = nutritionList.get(i).getMethods().replaceAll("\n", "");

                        Document doc = Jsoup.parse(htmlString);
                        // Document doc = Jsoup.parse(nutritionList.get(i).getMethods());
                        //System.out.println(doc + " method===========doc==========");
                        //System.out.println(recipes_id + " method===========doc==========");
                        methodArryalist = new ArrayList<>();


                        Elements elements = doc.body().select("\n");
                        for (int k = 0; k < elements.size(); k++) {
                            Element para = elements.get(k);
                            String method;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                method = String.valueOf(Html.fromHtml(String.valueOf(para), Html.FROM_HTML_MODE_COMPACT)).trim();
                                System.out.println(method.toString() + "method");
                            } else {
                                method = String.valueOf(Html.fromHtml(String.valueOf(para))).trim();
                                System.out.println(method.toString() + "method");
                            }
                            if (!method.trim().isEmpty() && method != "") {
                                methodArryalist.add(method);
                                System.out.println(methodArryalist.toString() + "method list");
                            }
                        }

                        /*System.out.println(method + " method=====================");
                        System.out.println(elements.toString() + " method=====================");
                        System.out.println(methodArryalist + " method============list=========");*/

                        Glide.with(mContext).load(/*Common.IMG_BASE_URL +*/ nImg).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                StoptShimmer();
                                return false;
                            }
                        }).into(mNutritionImage);

                    } else {
                        //mAddToShoppingListButton.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), resources.getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
                    }
                    //ToDo
                    //CheckIfAlreadyAdded(SharedData.id,recipes_id);
                }
            }
            /*System.out.println(method + "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkmmmmmmmmmmmmmm");

            System.out.println(ingredsList.toString() + "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkmmmmmmmmmmmmmm");
*/
            //set on method adpater
            mMethodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new NutritionMethodAdapter(getContext(), methodArryalist);
            mMethodRecyclerView.setAdapter(adapter);
            loadingPB.setVisibility(View.GONE);

            //set on Ingridents adpater
            mIngredientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(getContext(), (ArrayList<SingleRecipeDataModel.Ingredient>) ingredsList);
            mIngredientsRecyclerview.setAdapter(ingredientsAdapter);
            loadingPB1.setVisibility(View.GONE);

            //requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_loadNutrition_from_db", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }


    }

    //get nutrition data from Api
    private void populateNutritionData() {
        try {
            if (isAdded() && getContext() != null) {
                SingleRecipeDataModel.Recipe nutrition = dbHelper.getRecipeByID(String.valueOf(nutritionID));
                if (nutrition != null) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Nutrition Title: " + nutrition.getTitle().toString());
                    }
                    mNutritionTimeTitle.setText(nutrition.getTitle());
                    mNutritionName.setText(nutrition.getName());
                    mPrepareTime.setText(nutrition.getDuration());
                    nImg = nutrition.getImageURL().toString();
                    recipes_id = String.valueOf(nutrition.getId());
                    recipeIdAndServingList.add(new RecipeIdAndServingJsonModel(Integer.parseInt(recipes_id), 1));
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "JSON Response: " + recipeIdAndServingList.toString());
                    }
                    cookOptions = nutrition.getCook();
                    if (cookOptions.matches("0")) {
                        mCooking.setText(resources.getString(R.string.no));
                    } else if (cookOptions.matches("1")) {
                        mCooking.setText(resources.getString(R.string.yes));
                    }
                    // mCooking.setText(nutritionList.get(i).getData().getRecipes().get(j).getCook());
                    ingredsList = nutrition.getIngredients();
                    // dbHelper.isRecipeByIdInShoppingCart(recipes_id);
                    if (nutrition.getMethods() != null) {
                        Document doc = Jsoup.parse(nutrition.getMethods());
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Recipes ID : " + recipes_id);
                            Log.d(TAG, "JSOUP PARSED METHOD DATA: " + doc);
                        }
                        methodArryalist = new ArrayList<>();
                        Elements elements = doc.body().select("p");
                        for (int k = 0; k < elements.size(); k++) {
                            Element para = elements.get(k);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                method = String.valueOf(Html.fromHtml(String.valueOf(para), Html.FROM_HTML_MODE_COMPACT));

                            } else {
                                method = String.valueOf(Html.fromHtml(String.valueOf(para)));
                            }
                            if (!method.isEmpty()) {
                                methodArryalist.add(method);
                            }
                            //  methodArryalist.add(String.valueOf(para.nextSibling()));
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Method : " + method);
                                Log.d(TAG, "Element method: " + elements.toString());
                                Log.d(TAG, "Method Array list: " + methodArryalist);
                            }
                        }
                    } else {
                        if (nutrition.getMethodArray() != null) {
                            methodArryalist.addAll(nutrition.getMethodArray());
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "Method Array list: " + methodArryalist);
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Method Array list is null");
                            }
                        }
                    }


                    Glide.with(mContext).load(/*Common.IMG_BASE_URL +*/ nImg).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            StoptShimmer();
                            return false;
                        }
                    }).into(mNutritionImage);
                    //set on method adpater
                    mMethodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new NutritionMethodAdapter(getContext(), methodArryalist);
                    mMethodRecyclerView.setAdapter(adapter);
                    loadingPB.setVisibility(View.GONE);
                }

                ingredsList = dbHelper.getIngredientsByRecipeId(String.valueOf(nutrition.getId()));
                if (ingredsList != null && ingredsList.size() > 0) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Ingredient List: " + ingredsList.toString());
                    }
                    //set on Ingridents adpater
                    mIngredientsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    ingredientsAdapter = new IngredientsAdapter(getContext(), (ArrayList<SingleRecipeDataModel.Ingredient>) ingredsList);
                    mIngredientsRecyclerview.setAdapter(ingredientsAdapter);
                    loadingPB1.setVisibility(View.GONE);
                    mAddToShoppingListButton.setVisibility(View.VISIBLE);
                    CheckIfAlreadyAdded(SharedData.id, recipes_id);
                } else {
                    loadingPB.setVisibility(View.GONE);
                    loadingPB1.setVisibility(View.GONE);
                }
            }


            //requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_loadNutrition_from_API", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }

    }

    private void CheckIfAlreadyAdded(String id, String recipes_id) {
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "User ID: " + id);
        }
        if (dbHelper != null) {
            if (dbHelper.isRecipeByIdInShoppingCart(id, recipes_id) || isRecipeAlreadyAddedInCart) {
                //dbHelper.addCategory(nutritionDataModel.getData().categories);
                mAddToShoppingListButton.setText(resources.getString(R.string.added_to_shopping_list));
                mAddToShoppingListButton.setBackgroundResource(R.drawable.disable_drawable_button);
                mAddToShoppingListButton.setCheckable(false);
                mAddToShoppingListButton.setEnabled(false);
                mAddToShoppingListButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void addDataToShoppingList(String json_recipe) {
        //  dbHelper.addToShoppingList(nutritionDataModel);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "JSON Response1: " + Json_recipe);
        }

        blurrBackground();
        startLoading();
        int user_id = Integer.parseInt(SharedData.id);
        Call<NutritionDataModel> call = ApiClient.getService().saveRecipeToList("Bearer " + SharedData.token, SharedData.id, Json_recipe);
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                try {
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (message != null)
                            Log.d(TAG, "Response Status " + message.toString());
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        stopLoading();
                        if (getContext() != null) {
                            SessionUtil.setShoppingLoading(true, getContext());
                        }
                        //enableInteraction();
                        recipeIdAndServingList.clear();
                        NutritionDataModel responseData = response.body();
                        /*if (responseData.status == true) {*/
                        if (responseData != null) {
                            dbHelper.addShoppingList(user_id, Integer.parseInt(recipes_id));
                            CheckIfAlreadyAdded(SharedData.id, recipes_id);
                            //ToDo needs to be changed
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }


                        //  Toast.makeText(getContext(),"Recipe with id"+recipes_id+"added Successfully",Toast.LENGTH_LONG).show();
                       /* } else if (responseData.status == false) {
                            //ToDo needs to be changed
                            Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_LONG).show();

                        }*/
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        stopLoading();
                        Gson gson = new GsonBuilder().create();
                        NutritionDataModel nutritionJSON_Response = new NutritionDataModel();
                        nutritionJSON_Response = gson.fromJson(response.errorBody().string(), NutritionDataModel.class);
                        if (nutritionJSON_Response != null) {
                            if (nutritionJSON_Response.getMessage() != null) {
                                if (isAdded() && getContext() != null) {
                                    Toast.makeText(getContext(), nutritionJSON_Response.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                                    if (Common.isLoggingEnabled) {
                                        if (message != null)
                                            Log.d(TAG, "Response Status " + message.toString());
                                    }
                                    if (message != null)
                                        Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                if (message != null)
                                    Log.d(TAG, "Response Status " + message.toString());
                            }
                            if (message != null)
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_addDataToDhoopingList", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    stopLoading();
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_addToShoppinglist", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
            }
        });
    }

    /*private void enableInteraction() {
        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }*/


    private void StartShimmer() {
        shimmerForNutrition.startShimmerAnimation();
        shimmerForNutrition.setVisibility(View.VISIBLE);
        mNutritionImage.setVisibility(View.INVISIBLE);
    }

    private void StoptShimmer() {
        shimmerForNutrition.stopShimmerAnimation();
        shimmerForNutrition.setVisibility(View.GONE);
        mNutritionImage.setVisibility(View.VISIBLE);
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

                blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurAlgorithm(new RenderScriptBlur(getContext())).setBlurRadius(radius).setBlurAutoUpdate(true).setHasFixedTransformationMatrix(false);
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
                //blurView.setVisibility(View.VISIBLE);
                // blurView.setVisibility(View.GONE);

                loading_lav.setVisibility(View.VISIBLE);
                loading_lav.playAnimation();
            }
        }
    }

    void disableUserInteraction() {
        if (isAdded()) {
            if (requireActivity() != null) {
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                new LogsHandlersUtils(getContext()).getLogsDetails("NutritionFragment_stopLoading", SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();
    }
}