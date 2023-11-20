package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.common.Common;
import com.cedricapp.interfaces.QuantityCheckListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.DashboardNutrition;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoWeekWiseRecyclerViewAdapter extends RecyclerView.Adapter<DemoWeekWiseRecyclerViewAdapter.ViewHolder> {

    private int count;
    private final int itemWidth;
    private final int matchParent;
    DashboardNutritionPagerModel.Data.Recipes dashboardRecipeList;
    private List<DashboardNutritionPagerModel.Page1> pager1List;
    private List<DashboardNutritionPagerModel.Page2> pager2List;
    private List<DashboardNutritionPagerModel.Page3> pager3List;
    private final Context context;
    private DBHelper dbHelper;
    NutritionDataModel nutritionDataModel;
    DashboardNutrition.Data.Recipe weekWiseNutritionModel;
    QuantityCheckListener quantityCheckListener;
    boolean isServingHandlerRunning;
    int btn;
    private Integer nutritionId;
    ArrayList<Integer> integerArrayList;
    Resources resources;

    public DemoWeekWiseRecyclerViewAdapter(int count, int itemWidth, int matchParent, DashboardNutritionPagerModel.Data.Recipes recipes, Context context, QuantityCheckListener quantityCheckListener, int button, Resources resources) {
        this.count = count;
        this.itemWidth = itemWidth;
        this.matchParent = matchParent;
        this.dashboardRecipeList = recipes;
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.quantityCheckListener = quantityCheckListener;
        btn = button;
        this.resources=resources;
    }

    @SuppressLint("NotifyDataSetChanged")
    void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_weekly_nutrition_pager, parent, false);
        view.getLayoutParams().width = itemWidth;
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        pager1List = dashboardRecipeList.getPage1();
        pager2List = dashboardRecipeList.getPage2();
        pager3List = dashboardRecipeList.getPage3();
        integerArrayList = new ArrayList<>();

        holder.quantityTVBreak.setText("1");
        holder.quantityTVLunch.setText("1");
        holder.quantityTVDinner.setText("1");
        integerArrayList.add(1);

        AtomicInteger quantityBreak = new AtomicInteger(Integer.parseInt(holder.quantityTVBreak.getText().toString()));
        AtomicInteger quantityLunch = new AtomicInteger(Integer.parseInt(holder.quantityTVLunch.getText().toString()));
        AtomicInteger quantityDinner = new AtomicInteger(Integer.parseInt(holder.quantityTVDinner.getText().toString()));

        if (position == 0) {
            if (pager1List != null && pager1List.size() > 0) {
                for (int i = 0; i < pager1List.size(); i++) {

                    if (pager1List.get(i).getIndex() == 1) {
                        holder.nutritionNameBreak.setText(pager1List.get(i).getName());
                        nutritionId = pager1List.get(i).getId();
                        int pos = i;
                        if (context != null) {
                            Glide.with(context).load(pager1List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageBreak);
                        }

                        holder.minusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityBreak.get() > 1) {
                                        int qty = quantityBreak.addAndGet(-1);
                                        holder.quantityTVBreak.setText(quantityBreak.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityBreak.addAndGet(+1);
                                    holder.quantityTVBreak.setText(quantityBreak.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });





                    }
                    if (pager1List.get(i).getIndex() == 2) {
                        holder.nutritionNameLunch.setText(pager1List.get(i).getName());
                        nutritionId = pager1List.get(i).getId();
                        int pos = i;

                        if (context != null) {
                            Glide.with(context).load(pager1List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageLunch);
                        }

                        holder.minusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityLunch.get() > 1) {
                                        int qty = quantityLunch.addAndGet(-1);
                                        holder.quantityTVLunch.setText(quantityLunch.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityLunch.addAndGet(+1);
                                    holder.quantityTVLunch.setText(quantityLunch.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });
                    }
                    if (pager1List.get(i).getIndex() == 3) {
                        holder.nutritionNameDinner.setText(pager1List.get(i).getName());
                        nutritionId = pager1List.get(i).getId();
                        int pos = i;
                        if (context != null) {
                            Glide.with(context).load(pager1List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageDinner);
                        }

                        holder.minusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityDinner.get() > 1) {
                                        int qty = quantityDinner.addAndGet(-1);
                                        holder.quantityTVDinner.setText(quantityDinner.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {

                                    int qty = quantityDinner.addAndGet(+1);
                                    holder.quantityTVDinner.setText(quantityDinner.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager1List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });

                    /*holder.imageDinner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //loadNewFragment(position, pager1List);
                            Fragment nutritionFragment = new Nutrition_Fragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("token", SharedData.token);
                            bundle.putString("refresh_token", SharedData.refresh_token);
                            bundle.putInt("nutritionID", nutritionId);
                            bundle.putString("nutritionTime", pager1List.get(2).getTitle());
                            System.out.println(pager1List.get(position).toString()+"kkkkkkk");
                            Log.d(Common.LOG, "JSON Response....: " + pager1List.get(position).getId().toString());

                            bundle.putString("level_id", SharedData.level_id);
                            bundle.putString("goal_id", SharedData.goal_id);
                            bundle.putInt("dayNumber", day);
                            bundle.putInt("weekNumber", week);
                            nutritionFragment.setArguments(bundle);
                            loadFragment(nutritionFragment);
                        }
                    });*/
                    }


                }
            }


        }
        if (position == 1) {

            if (pager2List != null && pager2List.size() > 0) {
                for (int i = 0; i < pager2List.size(); i++) {

                    if (pager2List.get(i).getIndex() == 1) {
                        holder.nutritionNameBreak.setText(pager2List.get(i).getName());
                        nutritionId = pager2List.get(i).getId();
                        int pos=i;
                        if (context != null) {
                            Glide.with(context).load(pager2List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageBreak);
                        }

                        holder.minusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityBreak.get() > 1) {
                                        int qty = quantityBreak.addAndGet(-1);
                                        holder.quantityTVBreak.setText(quantityBreak.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityBreak.addAndGet(+1);
                                    holder.quantityTVBreak.setText(quantityBreak.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });

                    }

                    if (pager2List.get(i).getIndex() == 2) {
                        holder.nutritionNameLunch.setText(pager2List.get(i).getName());
                        nutritionId = pager2List.get(i).getId();
                        int pos=i;

                        if (context != null) {
                            Glide.with(context).load(pager2List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageLunch);
                        }

                        holder.minusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityLunch.get() > 1) {
                                        int qty = quantityLunch.addAndGet(-1);
                                        holder.quantityTVLunch.setText(quantityLunch.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityLunch.addAndGet(+1);
                                    holder.quantityTVLunch.setText(quantityLunch.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });

                    }

                    if (pager2List.get(i).getIndex() == 3) {
                        holder.nutritionNameDinner.setText(pager2List.get(i).getName());
                        nutritionId = pager2List.get(i).getId();
                        int pos=i;

                        if (context != null) {
                            Glide.with(context).load(pager2List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageDinner);
                        }

                        holder.minusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityDinner.get() > 1) {
                                        int qty = quantityDinner.addAndGet(-1);
                                        holder.quantityTVDinner.setText(quantityDinner.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityDinner.addAndGet(+1);
                                    holder.quantityTVDinner.setText(quantityDinner.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager2List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });

                    }


                }
            }

            //implementListener
            /*holder.imageBreakfast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID", pager2List.get(0).getId());
                    bundle.putString("nutritionTime", pager2List.get(position).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });
            holder.imageLunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID", pager2List.get(1).getId());
                    bundle.putString("nutritionTime", pager2List.get(position).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });
            holder.imageDinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID",  pager2List.get(2).getId());
                    bundle.putString("nutritionTime", pager2List.get(position).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });*/
        }
        if (position == 2) {
            if (pager3List != null && pager3List.size() > 0) {
                for (int i = 0; i < pager3List.size(); i++) {

                    if (pager3List.get(i).getIndex() == 1) {
                        holder.nutritionNameBreak.setText(pager3List.get(i).getName());
                        nutritionId = pager3List.get(i).getId();
                        int pos=i;


                        if (context != null) {
                            Glide.with(context).load(pager3List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageBreak);
                        }

                        holder.minusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityBreak.get() > 1) {
                                        int qty = quantityBreak.addAndGet(-1);
                                        holder.quantityTVBreak.setText(quantityBreak.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageBreak.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityBreak.addAndGet(+1);
                                    holder.quantityTVBreak.setText(quantityBreak.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });


                    }
                    if (pager3List.get(i).getIndex() == 2) {
                        holder.nutritionNameLunch.setText(pager3List.get(i).getName());
                        nutritionId = pager3List.get(i).getId();
                        int pos=i;


                        if (context != null) {
                            Glide.with(context).load(pager3List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageLunch);
                        }

                        holder.minusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityLunch.get() > 1) {
                                        int qty = quantityLunch.addAndGet(-1);
                                        holder.quantityTVLunch.setText(quantityLunch.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageLunch.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityLunch.addAndGet(+1);
                                    holder.quantityTVLunch.setText(quantityLunch.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });


                    }
                    if (pager3List.get(i).getIndex() == 3) {
                        holder.nutritionNameDinner.setText(pager3List.get(i).getName());
                        nutritionId = pager3List.get(i).getId();
                        int pos=i;

                        if (context != null) {
                            Glide.with(context).load(pager3List.get(i).getImageURL())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException
                                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            FirebaseCrashlytics.getInstance().recordException(e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  /*  holder.coaches_shimmer.stopShimmerAnimation();
                                    holder.coaches_shimmer.setVisibility(View.GONE);
                                    holder.coaches_conslay.setVisibility(View.VISIBLE);
                                    holder.imageViewIcon.setVisibility(View.VISIBLE);*/
                                            return false;
                                        }
                                    })
                                    .into(holder.nutritionImageDinner);
                        }

                        holder.minusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                                    if (quantityDinner.get() > 1) {
                                        int qty = quantityDinner.addAndGet(-1);
                                        holder.quantityTVDinner.setText(quantityDinner.toString());
                                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                        quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                    }
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_decrease));
                            }


                        });

                        holder.plusImageDinner.setOnClickListener(v -> {
                            if (nutritionId != null) {
                                if (context != null) {
                                    int qty = quantityDinner.addAndGet(+1);
                                    holder.quantityTVDinner.setText(quantityDinner.toString());
                                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                                    quantityCheckListener.onQuantityChangeListener(qty, pos, pager3List.get(pos).getId(), btn);
                                } else {
                                    if (Common.isLoggingEnabled)
                                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                                }
                            } else {
                                showToast(resources.getString(R.string.unable_to_increase));
                            }


                        });

                    }
                }
            }

            //implementListener
          /*  holder.imageBreakfast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID", pager3List.get(0).getId());
                    bundle.putString("nutritionTime", pager3List.get(0).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });
            holder.imageLunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID",  pager3List.get(1).getId());
                    bundle.putString("nutritionTime", pager3List.get(1).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });
            holder.imageDinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    bundle.putInt("nutritionID", pager3List.get(2).getId());
                    bundle.putString("nutritionTime", pager3List.get(position).getTitle());
                    bundle.putString("level_id", SharedData.level_id);
                    bundle.putString("goal_id", SharedData.goal_id);
                    bundle.putInt("dayNumber", day);
                    bundle.putInt("weekNumber", week);
                    nutritionFragment.setArguments(bundle);
                    loadFragment(nutritionFragment);
                }
            });*/


        }


    }

    void showToast(String message) {
        try {

            if (context != null) {
                Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(Common.LOG, "getContext is null");
                }
            }

        } catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);

            exception.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nutritionNameBreak, nutritionNameLunch, nutritionNameDinner, quantityTVBreak,
                quantityTVDinner, quantityTVLunch;
        ImageView nutritionImageBreak, nutritionImageLunch, nutritionImageDinner, minusImageBreak,
                plusImageBreak, minusImageLunch, plusImageLunch, minusImageDinner, plusImageDinner;
        ConstraintLayout mConstraintLayout;
        ShimmerFrameLayout shimmerFrameLayout;

        ViewHolder(View itemView) {
            super(itemView);

            this.minusImageBreak = itemView.findViewById(R.id.minus_btn_iv_break);
            this.plusImageBreak = itemView.findViewById(R.id.add_btn_iv_break);
            this.minusImageLunch = itemView.findViewById(R.id.minus_btn_iv_lunch);
            this.plusImageLunch = itemView.findViewById(R.id.add_btn_iv_lunch);
            this.minusImageDinner = itemView.findViewById(R.id.minus_btn_iv_dinner);
            this.plusImageDinner = itemView.findViewById(R.id.add_btn_iv_dinner);
            this.nutritionNameBreak = itemView.findViewById(R.id.textViewNutritionNameBreakFast);
            this.nutritionNameLunch = itemView.findViewById(R.id.textViewNutritionNameLunch);
            this.nutritionNameDinner = itemView.findViewById(R.id.textViewNutritionNameDinner);
            this.nutritionImageBreak = itemView.findViewById(R.id.nutritionImageBreakFast);
            this.nutritionImageLunch = itemView.findViewById(R.id.nutritionImageLunch);
            this.nutritionImageDinner = itemView.findViewById(R.id.nutritionImageDinner);
            this.quantityTVBreak = itemView.findViewById(R.id.quantity_tv_break);
            this.quantityTVLunch = itemView.findViewById(R.id.quantity_tv_lunch);
            this.quantityTVDinner = itemView.findViewById(R.id.quantity_tv_dinner);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayoutItemList);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayoutItemList);
        }
    }
}