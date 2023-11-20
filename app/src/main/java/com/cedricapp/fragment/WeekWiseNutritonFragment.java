package com.cedricapp.fragment;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.cedricapp.adapters.DemoWeekWiseRecyclerViewAdapter;
import com.cedricapp.adapters.WeekWiseNutritionAdapter;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.QuantityCheckListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.DashboardNutrition;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.model.DateModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.model.RecipeIdAndServingJsonModel;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.Localization;
import com.cedricapp.utils.LogoutUtil;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.ResponseStatus;
import com.cedricapp.utils.SessionUtil;
import com.cedricapp.utils.WeekDaysHelper;
import com.cedricapp.activity.HomeActivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


@SuppressWarnings("ALL")
public class WeekWiseNutritonFragment extends Fragment implements QuantityCheckListener {
    ConstraintLayout firstDayLayout, secondDayLayout, thirdDayLayout, forthDayLayout,
            fifthLayout, sixthLayout, seventhLayout;
    boolean isFirstDayAvailable, isSecondDayAvailable, isThirdDayAvailable, isForthDayAvailable, isFifthDayAvailable, isSixthDayAvailable, isSeventhDayAvailable;
    int firstDay, secondDay, thirdDay, forthDay, fifthDay, sixthDay, seventhDay;
    String foodPreferenceID;

    private RecyclerView day1_rv;
    private RecyclerView day2_rv;
    private RecyclerView day3_rv;
    private RecyclerView day4_rv;
    private RecyclerView day5_rv;
    private RecyclerView day6_rv;
    private RecyclerView day7_rv;
    private WeekWiseNutritionAdapter weekWiseNutritionAdapter;
    /*ArrayList<NutritionDataModel> day1NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day2NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day3NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day4NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day5NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day6NutritionModelObject = new ArrayList<>();
    ArrayList<NutritionDataModel> day7NutritionModelObject = new ArrayList<>();*/
    private TextView firstDay_tv;
    private TextView secondDay_tv;
    private TextView thirdDay_tv;
    private TextView forthDay_tv;
    private TextView fifthDay_tv;
    private TextView sixthDay_tv;
    private TextView seventhDay_tv;
    private TextView whole_week_tv;

    private View view1;
    public static Context context;
    private DBHelper dbHelper;
    private WeekDaysHelper weekDaysHelper;
    ArrayList<DateModel> currentWeekDate;

    TextView addFirstDayBtn, addSecondDayBtn, addThirdDayBtn, addForthDayBtn, addFifthDayBtn, addSixthDayBtn, addSeventhDayBtn;
    ImageButton backButton, addFirstDayIB, addSecondDayIB, addThirdDayIB, addForthDayIB, addFifthDayIB, addSixthDayIB, addSeventhDayIB;
    LottieAnimationView loading_lav;
    BlurView blurView;
    private static int SPLASH_SCREEN_TIME_OUT = 4000;
    private String userId;
    private int goal_id, level_id;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdAndServingList = new ArrayList<>();
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFirstDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFirstDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFirstDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSecondDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSecondDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSecondDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsThirdDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsThirdDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsThirdDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsForthDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsForthDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsForthDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFifthDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFifthDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsFifthDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSixthDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSixthDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSixthDayPager3;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSeventhDayPager1;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSeventhDayPager2;
    ArrayList<RecipeIdAndServingJsonModel> recipeIdsSeventhDayPager3;

    ArrayList<String> shoppingCartRecipeIDs;
    private ViewPager2 pager2day1, pager2day2, pager2day3, pager2day4, pager2day5, pager2day6, pager2day7;
    DemoWeekWiseRecyclerViewAdapter pagerWeekWiseAdapter;
    ScrollingPagerIndicator pagerIndicator1, pagerIndicator2, pagerIndicator3, pagerIndicator4, pagerIndicator5,
            pagerIndicator6, pagerIndicator7;
    private LinearLayout linearLayoutDay1, linearLayoutDay2, linearLayoutDay3, linearLayoutDay4, linearLayoutDay5, linearLayoutDay6,
            linearLayoutDay7;
    private int pagerPosition;
    private int listSize;
    private int pagerCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String message;
    Resources resources;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedData.redirectToDashboard = false;
        HomeActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.showBottomNav();
    }

    public WeekWiseNutritonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week_wise_nutriton, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        //resources = Localization.setLanguage(getContext(), getResources());
        resources = getResources();

        init();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        addFirstDayBtn.setOnClickListener(view2 -> {
            //showToast("Under maintenance");
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    if (pagerPosition == 0) {
                        if (recipeIdsFirstDayPager1 != null) {
                            if (recipeIdsFirstDayPager1.size() > 0) {
                                if (recipeIdsFirstDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay1(recipeIdsFirstDayPager1, pagerPosition);
                                }

                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsFirstDayPager2 != null) {
                            if (recipeIdsFirstDayPager2.size() > 0) {
                                if (recipeIdsFirstDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay1(recipeIdsFirstDayPager2, pagerPosition);
                          /*  if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addFirstDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: firstDayLayout: Fragment is not attached with acticity or Context is null");
                            }*/
                                }

                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {
                        if (recipeIdsFirstDayPager3 != null) {
                            if (recipeIdsFirstDayPager3.size() > 0) {
                                if (recipeIdsFirstDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay1(recipeIdsFirstDayPager3, pagerPosition);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addFirstDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: firstDayLayout: Fragment is not attached with acticity or Context is null");
                            }*/
                                }

                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }

        });

        addSecondDayBtn.setOnClickListener(view2 -> {
            //showToast("Under maintenance");
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    if (pagerPosition == 0) {
                        if (recipeIdsSecondDayPager1 != null) {
                            if (recipeIdsSecondDayPager1.size() > 0) {
                                if (recipeIdsSecondDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay2(recipeIdsSecondDayPager1);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSecondDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSecondDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsSecondDayPager2 != null) {
                            if (recipeIdsSecondDayPager2.size() > 0) {
                                if (recipeIdsSecondDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay2(recipeIdsSecondDayPager2);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSecondDayDataToLocalDB(recipeIdsSecondDayPager2);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSecondDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {
                        if (recipeIdsSecondDayPager3 != null) {
                            if (recipeIdsSecondDayPager3.size() > 0) {
                                if (recipeIdsSecondDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay2(recipeIdsSecondDayPager3);
                          /*  if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSecondDayDataToLocalDB(recipeIdsSecondDayPager3);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSecondDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }

        });

        addThirdDayBtn.setOnClickListener(view2 -> {
            //showToast("Under maintenance");
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    if (pagerPosition == 0) {
                        if (recipeIdsThirdDayPager1 != null) {
                            if (recipeIdsThirdDayPager1.size() > 0) {
                                if (recipeIdsThirdDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay3(recipeIdsThirdDayPager1);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addThirdDayDataToLocalDB(recipeIdsThirdDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addThirdDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsThirdDayPager2 != null) {
                            if (recipeIdsThirdDayPager2.size() > 0) {
                                if (recipeIdsThirdDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay3(recipeIdsThirdDayPager2);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addThirdDayDataToLocalDB(recipeIdsThirdDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addThirdDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }

                    } else if (pagerPosition == 2) {
                        if (recipeIdsThirdDayPager3 != null) {
                            if (recipeIdsThirdDayPager3.size() > 0) {
                                if (recipeIdsThirdDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay3(recipeIdsThirdDayPager3);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addThirdDayDataToLocalDB(recipeIdsThirdDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addThirdDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }
        });

        addForthDayBtn.setOnClickListener(view2 -> {
            //  showToast("Under maintenance");
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    if (pagerPosition == 0) {
                        if (recipeIdsForthDayPager1 != null) {
                            if (recipeIdsForthDayPager1.size() > 0) {
                                if (recipeIdsForthDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay4(recipeIdsForthDayPager1);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addForthDayDataToLocalDB(recipeIdsForthDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addForthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsForthDayPager2 != null) {
                            if (recipeIdsForthDayPager2.size() > 0) {
                                if (recipeIdsForthDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay4(recipeIdsForthDayPager2);
                       /*     if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addForthDayDataToLocalDB(recipeIdsForthDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addForthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {

                        if (recipeIdsForthDayPager3 != null) {
                            if (recipeIdsForthDayPager3.size() > 0) {
                                if (recipeIdsForthDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay4(recipeIdsForthDayPager3);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addForthDayDataToLocalDB(recipeIdsForthDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addForthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }


        });

        addFifthDayBtn.setOnClickListener(view2 ->

        {
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    //showToast("Under maintenance");
                    if (pagerPosition == 0) {
                        if (recipeIdsFifthDayPager1 != null) {
                            if (recipeIdsFifthDayPager1.size() > 0) {
                                if (recipeIdsFifthDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay5(recipeIdsFifthDayPager1);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addFifthDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addFifthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsFifthDayPager2 != null) {
                            if (recipeIdsFifthDayPager2.size() > 0) {
                                if (recipeIdsFifthDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay5(recipeIdsFifthDayPager2);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addFifthDayDataToLocalDB(recipeIdsFifthDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addFifthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {
                        if (recipeIdsFifthDayPager3 != null) {
                            if (recipeIdsFifthDayPager3.size() > 0) {
                                if (recipeIdsFifthDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {

                                    addDaysRecipesToListDay5(recipeIdsFifthDayPager3);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addFifthDayDataToLocalDB(recipeIdsFifthDayPager);
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addFifthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }


        });

        addSixthDayBtn.setOnClickListener(view2 ->

        {
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    // showToast("Under maintenance");
                    if (pagerPosition == 0) {
                        if (recipeIdsSixthDayPager1 != null) {
                            if (recipeIdsSixthDayPager1.size() > 0) {
                                if (recipeIdsSixthDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay6(recipeIdsSixthDayPager1);
                        /*    if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSixthDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSixthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsSixthDayPager2 != null) {
                            if (recipeIdsSixthDayPager2.size() > 0) {
                                if (recipeIdsSixthDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay6(recipeIdsSixthDayPager2);

                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSixthDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSixthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {
                        if (recipeIdsSixthDayPager3 != null) {
                            if (recipeIdsSixthDayPager3.size() > 0) {
                                if (recipeIdsSixthDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay6(recipeIdsSixthDayPager3);
                        /*    if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSixthDayDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSixthDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }

        });

        addSeventhDayBtn.setOnClickListener(view2 ->

        {
            if (getContext() != null)
                if (SessionUtil.isSubscriptionAvailable(getContext())) {
                    //showToast("Under maintenance");
                    if (pagerPosition == 0) {
                        if (recipeIdsSeventhDayPager1 != null) {
                            if (recipeIdsSeventhDayPager1.size() > 0) {
                                if (recipeIdsSeventhDayPager1.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay7(recipeIdsSeventhDayPager1);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSeventhDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSeventhDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 1) {
                        if (recipeIdsSeventhDayPager2 != null) {
                            if (recipeIdsSeventhDayPager2.size() > 0) {
                                if (recipeIdsSeventhDayPager2.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay7(recipeIdsSeventhDayPager2);
                           /* if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSeventhDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSeventhDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    } else if (pagerPosition == 2) {
                        if (recipeIdsSeventhDayPager3 != null) {
                            if (recipeIdsSeventhDayPager3.size() > 0) {
                                if (recipeIdsSeventhDayPager3.get(0).getId() == null) {
                                    showToast(resources.getString(R.string.unable_to_add_in_shoppinglist));
                                } else {
                                    addDaysRecipesToListDay7(recipeIdsSeventhDayPager3);
                            /*if (isAdded() && getContext() != null) {
                                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                                    addSeventhDataToLocalDB();
                                } else {
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Common.isLoggingEnabled)
                                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSeventhDayBtn: Fragment is not attached with acticity or Context is null");
                            }*/
                                }
                            } else {
                                showToast(resources.getString(R.string.nutrition_not_aviable));
                            }

                        } else {
                            showToast(resources.getString(R.string.nutrition_not_aviable));
                        }
                    }
                } else {
                    DialogUtil.showSubscriptionEndDialogBox(getContext(), resources);
                }

        });

        firstDayLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    if (linearLayoutDay1.getVisibility() == View.GONE) {
                        if (!isFirstDayAvailable) {
                            getDataByDayIndex(0);
                        } else {
                            handleFirstDayVisibility();
                        }
                    } else {
                        handleFirstDayVisibility();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: firstDayLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        secondDayLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleTuesVisibility();
                    if (linearLayoutDay2.getVisibility() == View.GONE) {
                        if (!isSecondDayAvailable) {
                            getDataByDayIndex(1);
                        } else {
                            handleSecondDayVisibility();
                        }
                    } else {
                        handleSecondDayVisibility();
                    }
                    //setMyDataByBtnNumberWise(2);
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: secondDayLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        thirdDayLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleWednesVisibility();
                    if (linearLayoutDay3.getVisibility() == View.GONE) {
                        if (!isThirdDayAvailable) {
                            getDataByDayIndex(2);
                        } else {
                            handleThirdDayVisibility();
                        }
                    } else {
                        handleThirdDayVisibility();
                    }
                    //setMyDataByBtnNumberWise(3);
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: thirdDayLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        forthDayLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleThursVisibility();
                    //setMyDataByBtnNumberWise(4);
                    if (linearLayoutDay4.getVisibility() == View.GONE) {
                        if (!isForthDayAvailable) {
                            getDataByDayIndex(3);
                        } else {
                            handleForthDayVisibility();
                        }
                    } else {
                        handleForthDayVisibility();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: forthDayLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        fifthLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleFridaVisibility();
                    //setMyDataByBtnNumberWise(5);
                    if (linearLayoutDay5.getVisibility() == View.GONE) {
                        if (!isFifthDayAvailable) {
                            getDataByDayIndex(4);
                        } else {
                            handleFifthDayVisibility();
                        }
                    } else {
                        handleFifthDayVisibility();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: fifthLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        sixthLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleSaturVisibility();
                    //setMyDataByBtnNumberWise(6);
                    if (linearLayoutDay6.getVisibility() == View.GONE) {
                        if (!isSixthDayAvailable) {
                            getDataByDayIndex(5);
                        } else {
                            handleSixthDayVisibility();
                        }
                    } else {
                        handleSixthDayVisibility();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: sixthLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });

        seventhLayout.setOnClickListener(v ->

        {
            if (isAdded() && getContext() != null) {
                if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                    //HandleSunVisibility();
                    //setMyDataByBtnNumberWise(7);
                    if (linearLayoutDay7.getVisibility() == View.GONE) {
                        if (!isSeventhDayAvailable) {
                            getDataByDayIndex(6);
                        } else {
                            handleSeventhDayVisibility();
                        }
                    } else {
                        handleSeventhDayVisibility();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "WeekWiseNutrtionFragment: firstDayLayout: Fragment is not attached with acticity or Context is null");
                }
            }
        });
    }

    private void addDaysRecipesToListDay7(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSeventhDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addSeventhDataToLocalDB(recipeIdsSeventhDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSeventhDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }
    }

    private void addDaysRecipesToListDay6(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSixthDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addSixthDayDataToLocalDB(recipeIdsSixthDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSixthDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }
    }

    private void addDaysRecipesToListDay5(ArrayList<RecipeIdAndServingJsonModel> recipeIdsFifthDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addFifthDayDataToLocalDB(recipeIdsFifthDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addFifthDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }

    }

    private void addDaysRecipesToListDay4(ArrayList<RecipeIdAndServingJsonModel> recipeIdsForthDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addForthDayDataToLocalDB(recipeIdsForthDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addForthDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }

    }

    private void addDaysRecipesToListDay3(ArrayList<RecipeIdAndServingJsonModel> recipeIdsThirdDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addThirdDayDataToLocalDB(recipeIdsThirdDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addThirdDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }
    }

    private void addDaysRecipesToListDay2(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSecondDayPager) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addSecondDayDataToLocalDB(recipeIdsSecondDayPager);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: addSecondDayBtn: Fragment is not attached with acticity or Context is null");
            }
        }
    }

    private void addDaysRecipesToListDay1(ArrayList<RecipeIdAndServingJsonModel> recipeIdsFirstDayPager, int pagerPosition) {
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                addFirstDayDataToLocalDB(recipeIdsFirstDayPager, pagerPosition);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.turn_on_internet), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "WeekWiseNutrtionFragment: firstDayLayout: Fragment is not attached with acticity or Context is null");
            }
        }

    }

    private void init() {
        //set id's for linearLayout
        linearLayoutDay1 = view1.findViewById(R.id.day1Layout);
        linearLayoutDay2 = view1.findViewById(R.id.day2Layout);
        linearLayoutDay3 = view1.findViewById(R.id.day3Layout);
        linearLayoutDay4 = view1.findViewById(R.id.day4Layout);
        linearLayoutDay5 = view1.findViewById(R.id.day5Layout);
        linearLayoutDay6 = view1.findViewById(R.id.day6Layout);
        linearLayoutDay7 = view1.findViewById(R.id.day7Layout);


        //set id's for constraintLayout
        firstDayLayout = view1.findViewById(R.id.weekday1Layout);
        secondDayLayout = view1.findViewById(R.id.weekday2Layout);
        thirdDayLayout = view1.findViewById(R.id.weekday3Layout);
        forthDayLayout = view1.findViewById(R.id.weekday4Layout);
        fifthLayout = view1.findViewById(R.id.weekday5Layout);
        sixthLayout = view1.findViewById(R.id.weekday6Layout);
        seventhLayout = view1.findViewById(R.id.weekday7Layout);
        //set Up pager
        // Setup ViewPager2 with indicator
        pager2day1 = view1.findViewById(R.id.pager_view_day1);
        pagerIndicator1 = view1.findViewById(R.id.pager_indicator1);
        pager2day2 = view1.findViewById(R.id.pager_view_day2);
        pagerIndicator2 = view1.findViewById(R.id.pager_indicator2);
        pager2day3 = view1.findViewById(R.id.pager_view_day3);
        pagerIndicator3 = view1.findViewById(R.id.pager_indicator3);
        pager2day4 = view1.findViewById(R.id.pager_view_day4);
        pagerIndicator4 = view1.findViewById(R.id.pager_indicator4);
        pager2day5 = view1.findViewById(R.id.pager_view_day5);
        pagerIndicator5 = view1.findViewById(R.id.pager_indicator5);
        pager2day6 = view1.findViewById(R.id.pager_view_day6);
        pagerIndicator6 = view1.findViewById(R.id.pager_indicator6);
        pager2day7 = view1.findViewById(R.id.pager_view_day7);
        pagerIndicator7 = view1.findViewById(R.id.pager_indicator7);


       /* pagerWeekWiseAdapter = new DemoWeekWiseRecyclerViewAdapter(3, 3,
                ViewGroup.LayoutParams.MATCH_PARENT, recipes, context, this, ViewGroup.LayoutParams.MATCH_PARENT);
        pager2day1.setAdapter(pagerWeekWiseAdapter);
        pagerIndicator2.attachToPager(pager2day1);*/


        //method Recyclerview
      /* // day1_rv = view1.findViewById(R.id.day1_rv);
        day2_rv = view1.findViewById(R.id.day2_rv);
        day3_rv = view1.findViewById(R.id.day3_rv);
        day4_rv = view1.findViewById(R.id.day4_rv);
        day5_rv = view1.findViewById(R.id.day5_rv);
        day6_rv = view1.findViewById(R.id.day6_rv);
        day7_rv = view1.findViewById(R.id.day7_rv);*/

        //add buttons tv
        addFirstDayBtn = view1.findViewById(R.id.addDayFirstBtn);
        addSecondDayBtn = view1.findViewById(R.id.addSecondDayTV);
        addThirdDayBtn = view1.findViewById(R.id.addThirdDayTV);
        addForthDayBtn = view1.findViewById(R.id.addForthDayTV);
        addFifthDayBtn = view1.findViewById(R.id.addFifthDayBtn);
        addSixthDayBtn = view1.findViewById(R.id.addSixthDayBtn);
        addSeventhDayBtn = view1.findViewById(R.id.addSeventhDayBtn);

        //add back button
        backButton = view1.findViewById(R.id.backArrow);

        //week days date text views
        seventhDay_tv = view1.findViewById(R.id.seventhDay_tv);
        firstDay_tv = view1.findViewById(R.id.dayFirst_tv);
        secondDay_tv = view1.findViewById(R.id.secondDay_tv);
        thirdDay_tv = view1.findViewById(R.id.thirdDay_tv);
        forthDay_tv = view1.findViewById(R.id.forthDay_tv);
        fifthDay_tv = view1.findViewById(R.id.fifthDay_tv);
        sixthDay_tv = view1.findViewById(R.id.sixthDay_tv);
        whole_week_tv = view1.findViewById(R.id.whole_week_tv);

        addFirstDayIB = view1.findViewById(R.id.addFirstDayIB);
        addSecondDayIB = view1.findViewById(R.id.addSecondDayIB);
        addThirdDayIB = view1.findViewById(R.id.addThirdDayIB);
        addForthDayIB = view1.findViewById(R.id.addForthDayIB);
        addFifthDayIB = view1.findViewById(R.id.addFifthDayIB);
        addSixthDayIB = view1.findViewById(R.id.addSixthDayIB);
        addSeventhDayIB = view1.findViewById(R.id.addSeventhDayIB);


        dbHelper = new DBHelper(getContext());
        loading_lav = view1.findViewById(R.id.loading_lav);
        blurView = view1.findViewById(R.id.blurView);
        resources = Localization.setLanguage(getContext(), getResources());

        recipeIdsFirstDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsFirstDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsFirstDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSecondDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSecondDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSecondDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsThirdDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsThirdDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsThirdDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsForthDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsForthDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsForthDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsFifthDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsFifthDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsFifthDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSixthDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSixthDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSixthDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSeventhDayPager1 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSeventhDayPager2 = new ArrayList<RecipeIdAndServingJsonModel>();
        recipeIdsSeventhDayPager3 = new ArrayList<RecipeIdAndServingJsonModel>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            shoppingCartRecipeIDs = new ArrayList<>();
            shoppingCartRecipeIDs = bundle.getStringArrayList("shoppingRecipeIds");
        }

        try {
            if (SharedData.level_id != null) {
                level_id = Integer.parseInt(SharedData.level_id);
            }
            if (SharedData.goal_id != null) {
                goal_id = Integer.parseInt(SharedData.goal_id);
            }
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_getIntentData",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(ex));
            }
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
                Log.e(Common.LOG, ex.toString());
            }
        }
        userId = SessionUtil.getUserID(getContext());

        weekDaysHelper = new WeekDaysHelper();
        foodPreferenceID = SessionUtil.getFoodPreferenceID(getContext());

        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "User ID: " + userId);
            Log.d(Common.LOG, "User Level ID: " + level_id);
            Log.d(Common.LOG, "User Goal ID: " + goal_id);
            Log.d(Common.LOG, "Food Preference ID: " + foodPreferenceID);
        }

        currentWeekDate = weekDaysHelper.getCurrentWeek();

        ///Set date in txtview weekDays
        setWeekDates();

        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Week array list size is " + currentWeekDate.size());
        }

        //Get data from sever for 7 days
        /*if (currentWeekDate.size() == 7) {
            for (int i = 0; i < currentWeekDate.size(); i++) {
                getDataByDayIndex(i);
            }
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "current wee date size is less than 7");
        }*/

    }

    private void setWeekDates() {
        firstDay_tv.setText(currentWeekDate.get(0).getDayName() + " " + currentWeekDate.get(0).getDate());
        secondDay_tv.setText(currentWeekDate.get(1).getDayName() + " " + currentWeekDate.get(1).getDate());
        thirdDay_tv.setText(currentWeekDate.get(2).getDayName() + " " + currentWeekDate.get(2).getDate());
        forthDay_tv.setText(currentWeekDate.get(3).getDayName() + " " + currentWeekDate.get(3).getDate());
        fifthDay_tv.setText(currentWeekDate.get(4).getDayName() + " " + currentWeekDate.get(4).getDate());
        sixthDay_tv.setText(currentWeekDate.get(5).getDayName() + " " + currentWeekDate.get(5).getDate());
        seventhDay_tv.setText(currentWeekDate.get(6).getDayName() + " " + currentWeekDate.get(6).getDate());
        whole_week_tv.setText(currentWeekDate.get(0).getDate() + " - " + currentWeekDate.get(6).getDate());

    }

    private void addSeventhDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSeventhDayPager) {
        if (recipeIdsSeventhDayPager != null) {
            if (recipeIdsSeventhDayPager.size() > 0) {
                if (recipeIdsSeventhDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsSeventhDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsSeventhDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsSeventhDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsSeventhDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Seventh Nutrition JSON Response: " + recipeIdsSeventhDayPager.toString());
                }
                if (recipeIdsSeventhDayPager != null) {
                    String recipes = gson.toJson(recipeIdsSeventhDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Seventh Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 7, pagerPosition);
                }
            } else {
                showToast("No Internet Connection");
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }

    }

    private void addSixthDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSixthDayPager) {
        if (recipeIdsSixthDayPager != null) {
            if (recipeIdsSixthDayPager.size() > 0) {
                if (recipeIdsSixthDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsSixthDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsSixthDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsSixthDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsSixthDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }

        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Sixth Nutrition JSON Response: " + recipeIdsSixthDayPager.toString());
                }
                if (recipeIdsSixthDayPager != null) {
                    String recipes = gson.toJson(recipeIdsSixthDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Sixth Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 6, pagerPosition);
                }
            } else {
                showToast("No Internet Connection");
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }

    private void addFifthDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsFifthDayPager) {
        if (recipeIdsFifthDayPager != null) {
            if (recipeIdsFifthDayPager.size() > 0) {
                if (recipeIdsFifthDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsFifthDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsFifthDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsFifthDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsFifthDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Fifth Nutrition recipeIdsFifthDay String: " + recipeIdsFifthDayPager.toString());
                }
                if (recipeIdsFifthDayPager != null) {
                    String recipes = gson.toJson(recipeIdsFifthDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Fifth Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 5, pagerPosition);
                }
            } else {
                showToast(resources.getString(R.string.turn_on_internet));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }

    private void addForthDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsForthDayPager) {
        if (recipeIdsForthDayPager != null) {
            if (recipeIdsForthDayPager.size() > 0) {
                if (recipeIdsForthDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsForthDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsForthDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsForthDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsForthDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Forth Nutrition JSON Response: " + recipeIdsForthDayPager.toString());
                }
                if (recipeIdsForthDayPager != null) {
                    String recipes = gson.toJson(recipeIdsForthDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Forth Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 4, pagerPosition);
                }
            } else {
                showToast(resources.getString(R.string.turn_on_internet));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }

    private void addThirdDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsThirdDayPager) {
        if (recipeIdsThirdDayPager != null) {
            if (recipeIdsThirdDayPager.size() > 0) {
                if (recipeIdsThirdDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsThirdDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsThirdDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsThirdDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsThirdDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Third Nutrition JSON Response: " + recipeIdsThirdDayPager.toString());
                }
                if (recipeIdsThirdDayPager != null) {
                    String recipes = gson.toJson(recipeIdsThirdDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "Third Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 3, pagerPosition);
                }
            } else {
                showToast(resources.getString(R.string.turn_on_internet));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }

    private void addSecondDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsSecondDayPager) {
        if (recipeIdsSecondDayPager != null) {
            if (recipeIdsSecondDayPager.size() > 0) {
                if (recipeIdsSecondDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsSecondDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsSecondDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsSecondDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsSecondDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "Second Nutrition JSON Response: " + recipeIdsSecondDayPager.toString());
                }
                if (recipeIdsSecondDayPager != null) {
                    String recipes = gson.toJson(recipeIdsSecondDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Second Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 2, pagerPosition);
                }
            } else {
                showToast(resources.getString(R.string.turn_on_internet));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }

    private void addFirstDayDataToLocalDB(ArrayList<RecipeIdAndServingJsonModel> recipeIdsFirstDayPager, int pagerPosition) {
        if (recipeIdsFirstDayPager != null) {
            if (recipeIdsFirstDayPager.size() > 0) {
                if (recipeIdsFirstDayPager.get(0).getId() != null) {
                    for (int i = 0; i < recipeIdsFirstDayPager.size(); i++) {
                        //empty recipes list handled on categories size because in API no recipe variable provided
                        if (recipeIdsFirstDayPager.get(i).getId() != null) {
                            if (!dbHelper.isRecipeByIdInShoppingCart(userId, "" + recipeIdsFirstDayPager.get(i).getId())) {
                                dbHelper.addShoppingList(Integer.parseInt(userId), recipeIdsFirstDayPager.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
        if (isAdded() && getContext() != null) {
            if (ConnectionDetector.isConnectedWithInternet(getContext())) {
                Gson gson = new Gson();
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "First Nutrition JSON Response: " + recipeIdsFirstDayPager.toString());
                }
                if (recipeIdsFirstDayPager != null) {
                    String recipes = gson.toJson(recipeIdsFirstDayPager);
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "First Nutrition JSON Response: " + recipes);
                    }
                    addDataToShoppingList(recipes, 1, pagerPosition);
                }
            } else {
                showToast(resources.getString(R.string.turn_on_internet));
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Fragment is not attached with activity or context is null");
            }
        }
    }


    private void handleSeventhDayVisibility() {
        if (linearLayoutDay7.getVisibility() == View.VISIBLE) {
            addSeventhDayBtn.setVisibility(View.GONE);
            linearLayoutDay7.setVisibility(View.GONE);
            addSeventhDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay7.setVisibility(View.VISIBLE);
            addSeventhDayBtn.setVisibility(View.VISIBLE);
            addSeventhDayIB.setVisibility(View.GONE);
        }
    }

    private void handleSixthDayVisibility() {
        if (linearLayoutDay6.getVisibility() == View.VISIBLE) {
            addSixthDayBtn.setVisibility(View.GONE);
            linearLayoutDay6.setVisibility(View.GONE);
            addSixthDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay6.setVisibility(View.VISIBLE);
            addSixthDayBtn.setVisibility(View.VISIBLE);
            addSixthDayIB.setVisibility(View.GONE);
        }
    }

    private void handleFifthDayVisibility() {
        if (linearLayoutDay5.getVisibility() == View.VISIBLE) {
            addFifthDayBtn.setVisibility(View.GONE);
            linearLayoutDay5.setVisibility(View.GONE);
            addFifthDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay5.setVisibility(View.VISIBLE);
            addFifthDayBtn.setVisibility(View.VISIBLE);
            addFifthDayIB.setVisibility(View.GONE);
        }
    }

    private void handleForthDayVisibility() {
        if (linearLayoutDay4.getVisibility() == View.VISIBLE) {
            addForthDayBtn.setVisibility(View.GONE);
            linearLayoutDay4.setVisibility(View.GONE);
            addForthDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay4.setVisibility(View.VISIBLE);
            addForthDayBtn.setVisibility(View.VISIBLE);
            addForthDayIB.setVisibility(View.GONE);
        }
    }

    private void handleThirdDayVisibility() {
        if (linearLayoutDay3.getVisibility() == View.VISIBLE) {
            addThirdDayBtn.setVisibility(View.GONE);
            linearLayoutDay3.setVisibility(View.GONE);
            addThirdDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay3.setVisibility(View.VISIBLE);
            addThirdDayBtn.setVisibility(View.VISIBLE);
            addThirdDayIB.setVisibility(View.GONE);
        }
    }

    private void handleSecondDayVisibility() {
        if (linearLayoutDay2.getVisibility() == View.VISIBLE) {
            addSecondDayBtn.setVisibility(View.GONE);
            linearLayoutDay2.setVisibility(View.GONE);
            addSecondDayIB.setVisibility(View.VISIBLE);
        } else {
            linearLayoutDay2.setVisibility(View.VISIBLE);
            addSecondDayIB.setVisibility(View.GONE);
            addSecondDayBtn.setVisibility(View.VISIBLE);
        }
    }

    private void handleFirstDayVisibility() {
        if (linearLayoutDay1.getVisibility() == View.VISIBLE) {
            addFirstDayBtn.setVisibility(View.GONE);
            linearLayoutDay1.setVisibility(View.GONE);
            addFirstDayIB.setVisibility(View.VISIBLE);
        } else {
            addFirstDayBtn.setVisibility(View.VISIBLE);
            linearLayoutDay1.setVisibility(View.VISIBLE);
            addFirstDayIB.setVisibility(View.GONE);
        }
    }

    void setMyDataByBtnNumberWise(int btnNumber) {
        switch (btnNumber) {
            case 0:
                handleFirstDayVisibility();
                break;
            case 1:
                handleSecondDayVisibility();
                break;
            case 2:
                handleThirdDayVisibility();
                break;
            case 3:
                handleForthDayVisibility();
                break;
            case 4:
                handleFifthDayVisibility();
                break;
            case 5:
                handleSixthDayVisibility();
                break;
            case 6:
                handleSeventhDayVisibility();
                break;
        }
        ;
    }

    private void getDataByDayIndex(int index) {
        if (index >= 0 && index < 7) {
            int week = weekDaysHelper.getMyWeek(getContext(), currentWeekDate.get(index).getFullDate());
            int day = weekDaysHelper.getMyDay(getContext(), currentWeekDate.get(index).getFullDate());
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "User week: " + week);
                Log.d(Common.LOG, "User Day: " + day);
            }
            //loadNutritionData(day, level_id, goal_id, week, index);
            loadPagerNutritionData(day, level_id, goal_id, week, index);

        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Current week date size is zero");
            }
        }

    }

    private void loadPagerNutritionData(int day, int level_id, int goal_id, int week, int btnNumber) {
        blurrBackground();
        startLoading();
        Call<DashboardNutritionPagerModel> call;
        if (SessionUtil.isAPI_V3(context)) {
            call = ApiClient.getService().dashboardPagerNutritionV3("Bearer " + SharedData.token, day, week);
        } else {
            call = ApiClient.getService().dashboardPagerNutrition("Bearer " + SharedData.token, day, week);
        }
        // on below line we are calling method to enqueue and calling
        // all the data from array list.

        call.enqueue(new Callback<DashboardNutritionPagerModel>() {
            @Override
            public void onResponse(Call<DashboardNutritionPagerModel> call, Response<DashboardNutritionPagerModel> response) {
                try {
                    // inside on response method we are checking
                    // if the response is success or not.
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Response Status " + message.toString());
                        }
                        //Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Nutrtion Response in Week Wise Fragment: " + response.body().toString());
                        }
                        DashboardNutritionPagerModel nutritionDataModel = response.body();
                        setPagerData(nutritionDataModel, btnNumber);
                        setMyDataByBtnNumberWise(btnNumber);
                        stopLoading();
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            if (message != null)
                                Log.e(Common.LOG, "Response Status " + message.toString());
                        }
                        if (message != null && getContext() != null) {
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                        }
                        stopLoading();
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_loadPagerNutrition",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    stopLoading();
                }

            }

            @Override
            public void onFailure(Call<DashboardNutritionPagerModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_loadPagerNutrition",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }// in the method of on failure we are displaying a
                // toast message for fail to get data.
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
                if (getContext() != null) {
                    showToast(resources.getString(R.string.fail_to_laod_nutrition_data));
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "getContext is null");
                    }
                }

            }
        });

    }

    private void loadNutritionData(int day, int level_id, int goal_id, int week, int btnNumber) {
        blurrBackground();
        startLoading();
        Call<DashboardNutrition> call = ApiClient.getService().dashboardNutrition("Bearer " + SharedData.token, level_id, goal_id, day, week, foodPreferenceID);

        // on below line we are calling method to enqueue and calling
        // all the data from array list.

        call.enqueue(new Callback<DashboardNutrition>() {
            @Override
            public void onResponse(Call<DashboardNutrition> call, Response<DashboardNutrition> response) {
                try {
                    // inside on response method we are checking
                    // if the response is success or not.
                    if (response.isSuccessful()) {
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Nutrtion Response in Week Wise Fragment: " + response.body().toString());
                        }
                        DashboardNutrition nutritionDataModel = response.body();
                        //  setData(nutritionDataModel, btnNumber);

                        setMyDataByBtnNumberWise(btnNumber);
                        stopLoading();
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        stopLoading();
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_loadAllNutrition",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                    }
                    stopLoading();
                }

            }

            @Override
            public void onFailure(Call<DashboardNutrition> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_loadNutritionData",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }// in the method of on failure we are displaying a
                // toast message for fail to get data.
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
                showToast(resources.getString(R.string.fail_to_laod_nutrition_data));
            }
        });

    }

    /*void setData(DashboardNutrition nutritionDataModel, int btn) {
        if (nutritionDataModel != null) {
            if (nutritionDataModel.getData() != null) {
                if (nutritionDataModel.getData().getRecipes() != null) {
                    if (nutritionDataModel.getData().getRecipes().size() > 0) {
                        switch (btn) {
                            case 0:
                                isFirstDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsFirstDay.size() > 0)
                                    recipeIdsFirstDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFirstDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            firstDay++;
                                        }
                                    }
                                    if (firstDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addFirstDayBtn.setEnabled(false);
                                        addFirstDayBtn.setText("Added");
                                    }
                                }

                                setAdapter(nutritionDataModel.getData().getRecipes(), day1_rv,
                                        1);

                                break;
                            case 1:
                                isSecondDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsSecondDay.size() > 0)
                                    recipeIdsSecondDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSecondDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            secondDay++;
                                        }
                                    }
                                    if (secondDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addSecondDayBtn.setEnabled(false);
                                        addSecondDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day2_rv, 2);
                                break;
                            case 2:
                                isThirdDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsThirdDay.size() > 0)
                                    recipeIdsThirdDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsThirdDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            thirdDay++;
                                        }
                                    }
                                    if (thirdDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addThirdDayBtn.setEnabled(false);
                                        addThirdDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day3_rv, 3);
                                break;
                            case 3:
                                isForthDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsForthDay.size() > 0)
                                    recipeIdsForthDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsForthDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            forthDay++;
                                        }
                                    }
                                    if (forthDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addForthDayBtn.setEnabled(false);
                                        addForthDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day4_rv, 4);
                                break;
                            case 4:
                                isFifthDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsFifthDay.size() > 0)
                                    recipeIdsFifthDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFifthDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            fifthDay++;
                                        }
                                    }
                                    if (fifthDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addFifthDayBtn.setEnabled(false);
                                        addFifthDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day5_rv, 5);
                                break;
                            case 5:
                                isSixthDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsSixthDay.size() > 0)
                                    recipeIdsSixthDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSixthDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            sixthDay++;
                                        }
                                    }
                                    if (sixthDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addSixthDayBtn.setEnabled(false);
                                        addSixthDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day6_rv, 6);
                                break;
                            case 6:
                                isSeventhDayAvailable = true;
                                //set list for shopping cart
                                if (recipeIdsSeventhDay.size() > 0)
                                    recipeIdsSeventhDay.clear();
                                else {
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSeventhDay.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().get(i).getId())) {
                                            seventhDay++;
                                        }
                                    }
                                    if (seventhDay == nutritionDataModel.getData().getRecipes().size()) {
                                        addSeventhDayBtn.setEnabled(false);
                                        addSeventhDayBtn.setText("Added");
                                    }
                                }
                                setAdapter(nutritionDataModel.getData().getRecipes(), day7_rv, 7);
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(Common.LOG, "Nutrition Model recipes size is zero");
                    }
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Nutrition Model recipes is null");
                }

            } else {
                if (Common.isLoggingEnabled)
                    Log.d(Common.LOG, "Nutrition Model getData is null");
            }
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Nutrition Model is null");
        }

    }*/

    void setPagerData(DashboardNutritionPagerModel nutritionDataModel, int btn) {
        if (nutritionDataModel != null) {
            if (nutritionDataModel.getData() != null) {
                if (nutritionDataModel.getData().getRecipes() != null) {
                    switch (btn) {
                        case 0:

                            isFirstDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsFirstDayPager1.size() > 0) {
                                recipeIdsFirstDayPager1.clear();
                            } else {
                                listSize = 0;

                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    listSize = nutritionDataModel.getData().getRecipes().getPage1().size();
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFirstDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            firstDay++;
                                        }
                                    }

                                }
                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    listSize = nutritionDataModel.getData().getRecipes().getPage1().size() + listSize;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFirstDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            firstDay++;
                                        }
                                    }

                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    listSize = nutritionDataModel.getData().getRecipes().getPage3().size() + listSize;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFirstDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            firstDay++;
                                        }
                                    }

                                }
                                  /*  if (firstDay == listSize) {
                                       addFirstDayBtn.setEnabled(false);
                                        addFirstDayBtn.setText("Added");
                                    }*/
                            }

                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day1,
                                    1, pagerCount);
                            break;
                        case 1:
                            isSecondDayAvailable = true;

                            //set list for shopping cart
                            if (recipeIdsSecondDayPager1.size() > 0) {
                                recipeIdsSecondDayPager1.clear();
                            } else {
                                //  pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSecondDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            secondDay++;
                                        }
                                    }

                                }

                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSecondDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            secondDay++;
                                        }
                                    }

                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSecondDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            secondDay++;
                                        }
                                    }

                                }


                              /*      if (secondDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                        addSecondDayBtn.setEnabled(false);
                                        addSecondDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day2, 2, pagerCount);
                            break;
                        case 2:
                            isThirdDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsThirdDayPager1.size() > 0) {
                                recipeIdsThirdDayPager1.clear();
                            } else {
                                // pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsThirdDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            thirdDay++;
                                        }
                                    }

                                }

                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsThirdDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            thirdDay++;
                                        }
                                    }

                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsThirdDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            thirdDay++;
                                        }
                                    }

                                }


                                 /*   if (thirdDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                        addThirdDayBtn.setEnabled(false);
                                        addThirdDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day3, 3, pagerCount);
                            break;
                        case 3:
                            isForthDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsForthDayPager1.size() > 0) {
                                recipeIdsForthDayPager1.clear();
                            } else {
                                // pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsForthDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            forthDay++;
                                        }
                                    }
                                    //  pagerCount = pagerCount + 1;
                                }

                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsForthDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            forthDay++;
                                        }
                                    }
                                    // pagerCount = pagerCount + 1;
                                }


                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsForthDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            forthDay++;
                                        }
                                    }
                                    //pagerCount = pagerCount + 1;
                                }


                                  /*  if (forthDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                        addForthDayBtn.setEnabled(false);
                                        addForthDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day4, 4, pagerCount);
                            break;
                        case 4:
                            isFifthDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsFifthDayPager1.size() > 0) {
                                recipeIdsFifthDayPager1.clear();
                            } else {
                                // pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFifthDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            fifthDay++;
                                        }
                                    }
                                    // pagerCount = pagerCount + 1;
                                }


                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFifthDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            fifthDay++;
                                        }
                                    }
                                    // pagerCount = pagerCount + 1;
                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsFifthDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            fifthDay++;
                                        }
                                    }
                                    //  pagerCount = pagerCount + 1;
                                }


                                    /*if (fifthDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                        addFifthDayBtn.setEnabled(false);
                                        addFifthDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day5, 5, pagerCount);
                            break;
                        case 5:
                            isSixthDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsSixthDayPager1.size() > 0) {
                                recipeIdsSixthDayPager1.clear();
                            } else {
                                // pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSixthDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            sixthDay++;
                                        }
                                    }
                                    // pagerCount = pagerCount + 1;
                                }

                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSixthDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            sixthDay++;
                                        }
                                    }
                                    //pagerCount = pagerCount + 1;
                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSixthDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            sixthDay++;
                                        }
                                    }
                                    //pagerCount = pagerCount + 1;
                                }


                                   /* if (sixthDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                        addSixthDayBtn.setEnabled(false);
                                        addSixthDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day6, 6, pagerCount);
                            break;
                        case 6:
                            isSeventhDayAvailable = true;
                            //set list for shopping cart
                            if (recipeIdsSeventhDayPager1.size() > 0) {
                                recipeIdsSeventhDayPager1.clear();
                            } else {
                                // pagerCount = 0;
                                if (nutritionDataModel.getData().getRecipes().getPage1() != null
                                        && nutritionDataModel.getData().getRecipes().getPage1().size() > 0) {
                                    pagerCount = 1;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage1().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSeventhDayPager1.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage1().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage1().get(i).getId())) {
                                            seventhDay++;
                                        }
                                    }
                                    //  pagerCount = pagerCount + 1;
                                }


                                if (nutritionDataModel.getData().getRecipes().getPage2() != null
                                        && nutritionDataModel.getData().getRecipes().getPage2().size() > 0) {
                                    pagerCount = 2;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage2().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSeventhDayPager2.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage2().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage2().get(i).getId())) {
                                            seventhDay++;
                                        }
                                    }
                                    // pagerCount = pagerCount + 1;
                                }

                                if (nutritionDataModel.getData().getRecipes().getPage3() != null
                                        && nutritionDataModel.getData().getRecipes().getPage3().size() > 0) {
                                    pagerCount = 3;
                                    for (int i = 0; i < nutritionDataModel.getData().getRecipes().getPage3().size(); i++) {
                                        //Below array list is created to make add to cart list
                                        recipeIdsSeventhDayPager3.add(new RecipeIdAndServingJsonModel(nutritionDataModel.getData().getRecipes().getPage3().get(i).getId(), 1));
                                        if (dbHelper.isRecipeByIdInShoppingCart(userId, "" + nutritionDataModel.getData().getRecipes().getPage3().get(i).getId())) {
                                            seventhDay++;
                                        }
                                    }
                                    //   pagerCount = pagerCount + 1;
                                }


                                    /*if (seventhDay == nutritionDataModel.getData().getRecipes().getPage1().size()) {
                                      addSeventhDayBtn.setEnabled(false);
                                        addSeventhDayBtn.setText("Added");
                                    }*/
                            }
                            setPagerAdapter(nutritionDataModel.getData().getRecipes(), pager2day7, 7, pagerCount);
                            break;
                        default:
                            break;
                    }

                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "Nutrition Model recipes is null");
                    }
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(Common.LOG, "Nutrition Model getData is null");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.e(Common.LOG, "Nutrition Model is null");
            }
        }

    }

    void setPagerAdapter(DashboardNutritionPagerModel.Data.Recipes recipes, ViewPager2 pagerDay,
                         int btn, int pagerCount) {
       /* // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(recipes, context, this, btn);
        recyclerView.setAdapter(weekWiseNutritionAdapter);*/
        pagerWeekWiseAdapter = new DemoWeekWiseRecyclerViewAdapter(pagerCount,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, recipes, context, this, btn, resources);
        pagerDay.setAdapter(pagerWeekWiseAdapter);

        if (btn == 1) {
            pagerIndicator1.attachToPager(pagerDay);
        } else if (btn == 2) {
            pagerIndicator2.attachToPager(pagerDay);
        } else if (btn == 3) {
            pagerIndicator3.attachToPager(pagerDay);
        } else if (btn == 4) {
            pagerIndicator4.attachToPager(pagerDay);
        } else if (btn == 5) {
            pagerIndicator5.attachToPager(pagerDay);
        } else if (btn == 6) {
            pagerIndicator6.attachToPager(pagerDay);
        } else if (btn == 7) {
            pagerIndicator7.attachToPager(pagerDay);
        }

        // To get swipe event of viewpager2
        pagerDay.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                pagerPosition = position;
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, pagerPosition + " onPageScrolled page position");
                }
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, position + " onPageSelected Page position");
                }
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, state + " onPageScrollStateChanged Page state");
                }
            }
        });
    }


    void setAdapter(List<DashboardNutrition.Data.Recipe> recipes, RecyclerView recyclerView,
                    int btn) {
        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(recipes, context, this, btn, resources);
        recyclerView.setAdapter(weekWiseNutritionAdapter);
    }


    private void addDataToShoppingList(String recipes, int day, int pagerPosition) {
        blurrBackground();
        startLoading();
        Call<NutritionDataModel> call = ApiClient.getService().saveRecipeToList("Bearer " + SharedData.token, SharedData.id, recipes);
        call.enqueue(new Callback<NutritionDataModel>() {
            @Override
            public void onResponse(Call<NutritionDataModel> call, Response<NutritionDataModel> response) {
                try {
                    stopLoading();
                    if (response.isSuccessful()) {
                        message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Response Status " + message.toString());
                        }
                        // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Save Recipe response: " + response.body().toString());
                        }
                        NutritionDataModel responseData = response.body();
                        if (responseData != null) {
                            if (response.body().getMessage() != null) {
                                showToast("" + response.body().getMessage().toString());
                            }
                            if (getContext() != null) {
                                SessionUtil.setShoppingLoading(true, getContext());
                            }
                            switch (day) {
                                case 1:
                                   /* if (pagerPosition == 0) {
                                        addFirstDayBtn.setEnabled(false);
                                        addFirstDayBtn.setText("Added");
                                    } else if (pagerPosition == 1) {
                                        addFirstDayBtn.setEnabled(false);
                                        addFirstDayBtn.setText("Added");
                                    }
                                    else if(pagerPosition==2){
                                        addFirstDayBtn.setEnabled(false);
                                        addFirstDayBtn.setText("Added");
                                    }*/

                                    break;
                                case 2:
                                   /* addSecondDayBtn.setEnabled(false);
                                    addSecondDayBtn.setText("Added");*/
                                    break;
                                case 3:
                                   /* addThirdDayBtn.setEnabled(false);
                                    addThirdDayBtn.setText("Added");*/
                                    break;
                                case 4:
                                   /* addForthDayBtn.setEnabled(false);
                                    addForthDayBtn.setText("Added");*/
                                    break;
                                case 5:
                                    /*addFifthDayBtn.setEnabled(false);
                                    addFifthDayBtn.setText("Added");*/
                                    break;
                                case 6:
                                    /*addSixthDayBtn.setEnabled(false);
                                    addSixthDayBtn.setText("Added");*/
                                    break;
                                case 7:
                                    /*addSeventhDayBtn.setEnabled(false);
                                    addSeventhDayBtn.setText("Added");*/
                                    break;
                            }

                            //  Toast.makeText(getContext(),"Recipe with id"+recipes_id+"added Successfully",Toast.LENGTH_LONG).show();
                        }
                    } else if (response.code() == 401) {
                        if (getContext() != null) {
                            LogoutUtil.redirectToLogin(getContext());
                            Toast.makeText(getContext(), resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Gson gson = new GsonBuilder().create();
                        NutritionDataModel NutritionDataJSON_Response = new NutritionDataModel();
                        NutritionDataJSON_Response = gson.fromJson(response.errorBody().string(), NutritionDataModel.class);
                        if (NutritionDataJSON_Response != null) {
                            if (NutritionDataJSON_Response.getMessage() != null) {
                                showToast("" + NutritionDataJSON_Response.getMessage().toString());
                            } else {
                                message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                                if (Common.isLoggingEnabled) {
                                    Log.e(Common.LOG, "Response Status " + message.toString());
                                }
                                Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            message = ResponseStatus.getResponseCodeMessage(response.code(), resources);
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "Response Status " + message.toString());
                            }
                            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
                            showToast("Something went wrong");
                        }
                    }
                } catch (Exception ex) {
                    FirebaseCrashlytics.getInstance().recordException(ex);
                    if (getContext() != null) {
                        new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_loadPagerNutrition",
                                SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(ex));
                    }
                    if (Common.isLoggingEnabled) {
                        ex.printStackTrace();
                        Log.e(Common.LOG, "Exception: " + ex.toString());
                    }
                    showToast(resources.getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<NutritionDataModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (getContext() != null) {
                    new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_addDataToShoppingList",
                            SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(t));
                }
                if (Common.isLoggingEnabled) {
                    t.printStackTrace();
                }
                stopLoading();
                showToast("Error while adding into shopping cart");
            }
        });
    }

    private void blurrBackground() {
        blurView.setVisibility(View.VISIBLE);
        float radius = 1f;


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

    private void startLoading() {
        //dissable user interaction
        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

    private void stopLoading() {
        blurView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.GONE);
        Activity activity = getActivity();
        try {
            if (isAdded() && activity != null) {
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_stopLoading",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.throwableObject(e));
            }
            if (Common.isLoggingEnabled) {
                e.printStackTrace();
            }
        }
        loading_lav.setVisibility(View.GONE);
        loading_lav.pauseAnimation();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLoading();
    }

    @Override
    public void onQuantityChangeListener(int quantity, int currentIndex, int nutritionID,
                                         int day) {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "Ingredient ID: " + nutritionID + ", Qunatity: " + quantity + ", Index: " + currentIndex + ", Day: " + day);
        }
        switch (day) {
            case 1:
                if (pagerPosition == 0) {
                    recipeIdsFirstDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 1 quantity: " + recipeIdsFirstDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsFirstDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 1 quantity: " + recipeIdsFirstDayPager1.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsFirstDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 1 quantity: " + recipeIdsFirstDayPager1.toString());
                    }
                }

                break;
            case 2:
                if (pagerPosition == 0) {
                    recipeIdsSecondDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 2 quantity: " + recipeIdsSecondDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsSecondDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 2 quantity: " + recipeIdsSecondDayPager2.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsSecondDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 2 quantity: " + recipeIdsSecondDayPager3.toString());
                    }
                }

                break;
            case 3:
                if (pagerPosition == 0) {
                    recipeIdsThirdDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Day 3 quantity: " + recipeIdsThirdDayPager1.toString());
                } else if (pagerPosition == 1) {
                    recipeIdsThirdDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Day 3 quantity: " + recipeIdsThirdDayPager2.toString());
                } else if (pagerPosition == 2) {
                    recipeIdsThirdDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Day 3 quantity: " + recipeIdsThirdDayPager3.toString());
                }
                break;
            case 4:
                if (pagerPosition == 0) {
                    recipeIdsForthDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 4 quantity: " + recipeIdsForthDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsForthDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 4 quantity: " + recipeIdsForthDayPager2.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsForthDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 4 quantity: " + recipeIdsForthDayPager3.toString());
                    }
                }
                break;
            case 5:
                if (pagerPosition == 0) {
                    recipeIdsFifthDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 5 quantity: " + recipeIdsFifthDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsFifthDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 5 quantity: " + recipeIdsFifthDayPager2.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsFifthDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 5 quantity: " + recipeIdsFifthDayPager3.toString());
                    }
                }

                break;
            case 6:
                if (pagerPosition == 0) {
                    recipeIdsSixthDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 6 quantity: " + recipeIdsSixthDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsSixthDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 6 quantity: " + recipeIdsSixthDayPager2.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsSixthDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 6 quantity: " + recipeIdsSixthDayPager3.toString());
                    }
                }


                break;
            case 7:
                if (pagerPosition == 0) {
                    recipeIdsSeventhDayPager1.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 7 quantity: " + recipeIdsSeventhDayPager1.toString());
                    }
                } else if (pagerPosition == 1) {
                    recipeIdsSeventhDayPager2.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 7 quantity: " + recipeIdsSeventhDayPager2.toString());
                    }
                } else if (pagerPosition == 2) {
                    recipeIdsSeventhDayPager3.set(currentIndex, new RecipeIdAndServingJsonModel(nutritionID, quantity));
                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Day 7 quantity: " + recipeIdsSeventhDayPager3.toString());
                    }
                }

                break;
        }

        /*int servings= Integer.parseInt(quantity);
        System.out.println(servings +" Serving"+currentIndex +"index"+day +"day");*/
      /*  if(recipeIdAndServingList!=null && recipeIdAndServingList.size()!=0){
            for(int i=0 ;i<recipeIdAndServingList.size();i++){

                if(recipeIdAndServingList.get(i).getId().equals(nId)){
                    String ddex= String.valueOf(recipeIdAndServingList.get(i).toString());
                    Log.d("response",  ddex.toString());
                   // int index =Integer.parseInt(ddex);
                    recipeIdAndServingList.set(index,new RecipeIdAndServingJsonModel(nId,servings));
                }
                else{
                    recipeIdAndServingList.add(new RecipeIdAndServingJsonModel(nId,servings));
                }
            }

        }*/
        /*recipeIdAndServingList.add(new RecipeIdAndServingJsonModel(nId,servings));
        if (Common.isLoggingEnabled) {
            Log.d("response",  recipeIdAndServingList.toString());
        }*/
        //  mondayNutritionModelObject.get(currentIndex).setIntake(quantity);
        // nutritionShoppingList.get(currentIndex).setIntake(String.valueOf(quantity));
        /*dbHelper.updateNutritionIntake(mondayNutritionModelObject.get(currentIndex));*/
    }

    void showToast(String message) {
        try {
            if (isAdded()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.e(Common.LOG, "getContext is null");
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(Common.LOG, "Fragement is not added to activity");
                }
            }
        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
            if (getContext() != null) {
                new LogsHandlersUtils(getContext()).getLogsDetails("WeekWiseFragment_ShowToast",
                        SessionUtil.getUserEmailFromSession(getContext()), EXCEPTION, SharedData.caughtException(exception));
            }
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
    }


    /*private void addDataTwo() {
        day = 2;
      *//*  level_id = 1;
        goal_id = 1;*//*

        loadNutritionData(day, level_id, goal_id, week);
        *//* loadNutritionData("Tuesday", "Lunch");
        loadNutritionData("Tuesday", "Dinner");*//*

        // set up the RecyclerView
        tuesday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(tuesdayNutritionModel, context, this);
        tuesday_rv.setAdapter(weekWiseNutritionAdapter);

    }

    private void addDataThree() {

        day = 3;
       *//* level_id = 1;
        goal_id = 1;*//*

        loadNutritionData(day, level_id, goal_id, week);
      *//*  loadNutritionData("Wednesday", "Lunch");
        loadNutritionData("Wednesday", "Dinner");*//*
        // set up the RecyclerView
        wednesday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(wednesdayNutritionModel, context, this);
        wednesday_rv.setAdapter(weekWiseNutritionAdapter);


    }

    private void addDataFour() {

        day = 4;
        *//*level_id = 1;
        goal_id = 1;*//*

        loadNutritionData(day, level_id, goal_id, week);
        *//*loadNutritionData("Thursday", "Lunch");
        loadNutritionData("Thursday", "Dinner");*//*
        // set up the RecyclerView
        thursday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(thursdayNutritionModel, context, this);
        thursday_rv.setAdapter(weekWiseNutritionAdapter);

    }

    private void addDataFive() {
        day = 5;
       *//* level_id = 1;
        goal_id = 1;*//*

        loadNutritionData(day, level_id, goal_id, week);
       *//* loadNutritionData("Friday", "Lunch");
        loadNutritionData("Friday", "Dinner");*//*
        // set up the RecyclerView
        friday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(fridayNutritionModel, context, this);
        friday_rv.setAdapter(weekWiseNutritionAdapter);

    }


    private void addDataSix() {
        day = 6;
        *//*level_id=1;
        goal_id=1;*//*

        loadNutritionData(day, level_id, goal_id, week);
       *//* loadNutritionData("Saturday", "Lunch");
        loadNutritionData("Saturday", "Dinner");*//*
        // set up the RecyclerView
        saturday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(saturdayNutritionModel, context, this);
        saturday_rv.setAdapter(weekWiseNutritionAdapter);
    }

    private void addDataSeven() {
        day = 7;
        *//*level_id=1;
        goal_id=1;*//*

        loadNutritionData(day, level_id, goal_id, week);
        *//*loadNutritionData("Sunday", "Lunch");
        loadNutritionData("Sunday", "Dinner");*//*
        // set up the RecyclerView
        sunday_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        weekWiseNutritionAdapter = new WeekWiseNutritionAdapter(sundayNutritionModel, context, this);
        sunday_rv.setAdapter(weekWiseNutritionAdapter);

    }*/
}