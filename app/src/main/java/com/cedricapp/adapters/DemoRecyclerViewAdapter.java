package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.common.Common;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.DashboardNutritionPagerModel;
import com.cedricapp.fragment.Nutrition_Fragment;
import com.cedricapp.R;
import com.cedricapp.utils.DialogUtil;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

public class DemoRecyclerViewAdapter extends RecyclerView.Adapter<DemoRecyclerViewAdapter.ViewHolder> {

    private int count;
    private final int itemWidth;
    int day, week;
    private final int matchParent;
    Context mContext;
    DashboardNutritionPagerModel.Data.Recipes dashboardRecipeList;

    String TAG = "NUTRITION_TAG";
    // private int nutritionId;

    public DemoRecyclerViewAdapter(int count, int itemWidth, int matchParent,
                                   DashboardNutritionPagerModel.Data.Recipes dashboardRecipeList, Context context, int myDay, int myWeek) {
        this.count = count;
        this.itemWidth = itemWidth;
        this.matchParent = matchParent;
        this.dashboardRecipeList = dashboardRecipeList;
        this.mContext = context;
        this.day = myDay;
        this.week = myWeek;
    }


    @SuppressLint("NotifyDataSetChanged")
    void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pager,
                parent, false);

        view.getLayoutParams().width = itemWidth;
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        List<DashboardNutritionPagerModel.Page1> pager1List = dashboardRecipeList.getPage1();
        List<DashboardNutritionPagerModel.Page2> pager2List = dashboardRecipeList.getPage2();
        List<DashboardNutritionPagerModel.Page3> pager3List = dashboardRecipeList.getPage3();
        if (position == 0) {
            if (pager1List != null) {
                int size = pager1List.size();
                /*if (size > 3) {
                    size = 3;
                }*/
                for (int i = 0; i < size; i++) {
                    if (pager1List.get(i).getIndex() == 1) {
                        if (pager1List.get(i).getTitle() == null) {
                            holder.textViewBreakFast.setVisibility(View.GONE);
                        } else {
                            holder.textViewBreakFast.setVisibility(View.VISIBLE);
                        }
                        if (pager1List.get(i).getTitle() != null && pager1List.get(i).getTitle().length() > 1) {
                            holder.textViewBreakFast.setText(pager1List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager1List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        if (pager1List.get(i).getTotalCalories() != null && !pager1List.get(i).getTotalCalories().matches("")) {
                            holder.breakfastCaloriesLL.setVisibility(View.VISIBLE);
                            holder.breakfastCaloriesTV.setText(pager1List.get(i).getTotalCalories());
                        } else {
                            holder.breakfastCaloriesLL.setVisibility(View.GONE);
                        }
                        // nutritionId = pager1List.get(i).getId();
                        if (mContext != null) {
                            if (pager1List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager1List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (Common.isLoggingEnabled) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        /*holder.nutritionShimmerLayout.stopShimmerAnimation();
                                        holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                        holder.nutritionLayout.setVisibility(View.VISIBLE);
                                        holder.imageBreakfast.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageBreakfast);
                        }

                    } else if (pager1List.get(i).getIndex() == 2) {
                        if (pager1List.get(i).getTitle() == null) {
                            holder.textViewLunch.setVisibility(View.GONE);
                        } else {
                            holder.textViewLunch.setVisibility(View.VISIBLE);
                        }
                        if (pager1List.get(i).getTitle() != null && pager1List.get(i).getTitle().length() > 1) {
                            holder.textViewLunch.setText(pager1List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager1List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        //holder.textViewLunch.setText(pager1List.get(i).getTitle());

                        if (pager1List.get(i).getTotalCalories() != null && !pager1List.get(i).getTotalCalories().matches("")) {
                            holder.lunchCaloriesLL.setVisibility(View.VISIBLE);
                            holder.lunchCaloriesTV.setText(pager1List.get(i).getTotalCalories());
                        } else {
                            holder.lunchCaloriesLL.setVisibility(View.GONE);
                        }

                        if (mContext != null) {
                            if (pager1List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager1List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (Common.isLoggingEnabled) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                     /*   holder.nutritionShimmerLayout.stopShimmerAnimation();
                                        holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                        holder.nutritionLayout.setVisibility(View.VISIBLE);
                                        holder.imageLunch.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageLunch);
                        }


                    } else if (pager1List.get(i).getIndex() == 3) {
                        if (pager1List.get(i).getTitle() == null) {
                            holder.textViewDinner.setVisibility(View.GONE);
                        } else {
                            holder.textViewDinner.setVisibility(View.VISIBLE);
                        }
                        if (pager1List.get(i).getTitle() != null && pager1List.get(i).getTitle().length() > 1) {
                            holder.textViewDinner.setText(pager1List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager1List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        // holder.textViewDinner.setText(pager1List.get(i).getTitle());
                        if (pager1List.get(i).getTotalCalories() != null && !pager1List.get(i).getTotalCalories().matches("")) {
                            holder.dinnerCaloriesLL.setVisibility(View.VISIBLE);
                            holder.dinnerCaloriesTV.setText(pager1List.get(i).getTotalCalories());
                        } else {
                            holder.dinnerCaloriesLL.setVisibility(View.GONE);
                        }

                        if (mContext != null) {
                            if (pager1List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager1List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (Common.isLoggingEnabled) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                       /* holder.nutritionShimmerLayout.stopShimmerAnimation();
                                        holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                        holder.nutritionLayout.setVisibility(View.VISIBLE);
                                        holder.imageDinner.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageDinner);
                        }
                    }
                }
            }
        } else if (position == 1) {

            if (pager2List != null && pager2List.size() > 0) {
                int size = pager2List.size();
               /* if (size > 3) {
                    size = 3;
                }*/
                for (int i = 0; i < size; i++) {
                    if (pager2List.get(i).getIndex() == 1) {
                        if (pager2List.get(i).getTitle() == null) {
                            holder.textViewBreakFast.setVisibility(View.GONE);
                        } else {
                            holder.textViewBreakFast.setVisibility(View.VISIBLE);
                        }
                        if (pager2List.get(i).getTitle() != null && pager2List.get(i).getTitle().length() > 1) {
                            holder.textViewBreakFast.setText(pager2List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager2List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        //holder.textViewBreakFast.setText(pager2List.get(i).getTitle());

                        if (pager2List.get(i).getTotalCalories() != null && !pager2List.get(i).getTotalCalories().matches("")) {
                            holder.breakfastCaloriesLL.setVisibility(View.VISIBLE);
                            holder.breakfastCaloriesTV.setText(pager2List.get(i).getTotalCalories());
                        } else {
                            holder.breakfastCaloriesLL.setVisibility(View.GONE);
                        }
                        // nutritionId = pager2List.get(i).getId();


                        if (mContext != null) {
                            if (pager2List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager2List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (Common.isLoggingEnabled) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            /*holder.nutritionShimmerLayout.stopShimmerAnimation();
                                            holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                            holder.nutritionLayout.setVisibility(View.VISIBLE);
                                            holder.imageBreakfast.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageBreakfast);
                        }

                    } else if (pager2List.get(i).getIndex() == 2) {
                        String title = pager2List.get(i).getTitle();
                        if (pager2List.get(i).getTitle() == null) {
                            holder.textViewLunch.setVisibility(View.GONE);
                        } else {
                            holder.textViewLunch.setVisibility(View.VISIBLE);
                        }
                        if (pager2List.get(i).getTitle() != null && pager2List.get(i).getTitle().length() > 1) {
                            holder.textViewLunch.setText(pager2List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager2List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        //holder.textViewLunch.setText(pager2List.get(i).getTitle());
                        if (pager2List.get(i).getTotalCalories() != null && !pager2List.get(i).getTotalCalories().matches("")) {
                            holder.lunchCaloriesLL.setVisibility(View.VISIBLE);
                            holder.lunchCaloriesTV.setText(pager2List.get(i).getTotalCalories());
                        } else {
                            holder.lunchCaloriesLL.setVisibility(View.GONE);
                        }
                        if (mContext != null) {
                            if (pager2List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager2List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                if (Common.isLoggingEnabled) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      /*      holder.nutritionShimmerLayout.stopShimmerAnimation();
                                            holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                            holder.nutritionLayout.setVisibility(View.VISIBLE);
                                            holder.imageLunch.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageLunch);
                        }


                    } else if (pager2List.get(i).getIndex() == 3) {
                        if (pager2List.get(i).getTitle() == null) {
                            holder.textViewDinner.setVisibility(View.GONE);
                        } else {
                            holder.textViewDinner.setVisibility(View.VISIBLE);
                        }
                        if (pager2List.get(i).getTitle() != null && pager2List.get(i).getTitle().length() > 1) {
                            holder.textViewDinner.setText(pager2List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager2List.get(i).getTitle().toString().substring(1).toLowerCase());
                        }
                        // holder.textViewDinner.setText(pager2List.get(i).getTitle());
                        //nutritionId = pager2List.get(i).getId();
                        if (pager2List.get(i).getTotalCalories() != null && !pager2List.get(i).getTotalCalories().matches("")) {
                            holder.dinnerCaloriesLL.setVisibility(View.VISIBLE);
                            holder.dinnerCaloriesTV.setText(pager2List.get(i).getTotalCalories());
                        } else {
                            holder.dinnerCaloriesLL.setVisibility(View.GONE);
                        }


                        if (mContext != null) {
                            if (pager2List.get(i).getImageURL() != null)
                                Glide.with(mContext).load(pager2List.get(i).getImageURL())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException
                                                                                e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        /*    holder.nutritionShimmerLayout.stopShimmerAnimation();
                                            holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                            holder.nutritionLayout.setVisibility(View.VISIBLE);
                                            holder.imageDinner.setVisibility(View.VISIBLE);*/
                                                return false;
                                            }
                                        })
                                        .into(holder.imageDinner);
                        }
                    }
                }
            }

        } else if (position == 2) {
            if (pager3List != null && pager3List.size() > 0) {
                int size = pager3List.size();
                /*if (size > 3) {
                    size = 3;
                }*/
                for (int i = 0; i < size; i++) {
                    if (pager3List.get(i).getIndex() != null) {
                        if (pager3List.get(i).getIndex() == 1) {
                            if (pager3List.get(i).getTitle() == null) {
                                holder.textViewBreakFast.setVisibility(View.GONE);
                            } else {
                                holder.textViewBreakFast.setVisibility(View.VISIBLE);
                            }
                            if (pager3List.get(i).getTitle() != null && pager3List.get(i).getTitle().length() > 1) {
                                holder.textViewBreakFast.setText(pager3List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager3List.get(i).getTitle().toString().substring(1).toLowerCase());
                            }
                            //holder.textViewBreakFast.setText(pager3List.get(i).getTitle());
                            if (pager3List.get(i).getTotalCalories() != null && !pager3List.get(i).getTotalCalories().matches("")) {
                                holder.breakfastCaloriesLL.setVisibility(View.VISIBLE);
                                holder.breakfastCaloriesTV.setText(pager3List.get(i).getTotalCalories());
                            } else {
                                holder.breakfastCaloriesLL.setVisibility(View.GONE);
                            }
                            if (mContext != null) {
                                if (pager3List.get(i).getImageURL() != null)
                                    Glide.with(mContext).load(pager3List.get(i).getImageURL())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException
                                                                                    e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    FirebaseCrashlytics.getInstance().recordException(e);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                /*holder.nutritionShimmerLayout.stopShimmerAnimation();
                                                holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                                holder.nutritionLayout.setVisibility(View.VISIBLE);
                                                holder.imageBreakfast.setVisibility(View.VISIBLE);*/
                                                    return false;
                                                }
                                            })
                                            .into(holder.imageBreakfast);


                            }

                        } else if (pager3List.get(i).getIndex() == 2) {
                            if (pager3List.get(i).getTitle() == null) {
                                holder.textViewLunch.setVisibility(View.GONE);
                            } else {
                                holder.textViewLunch.setVisibility(View.VISIBLE);
                            }
                            if (pager3List.get(i).getTitle() != null && pager3List.get(i).getTitle().length() > 1) {
                                holder.textViewLunch.setText(pager3List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager3List.get(i).getTitle().toString().substring(1).toLowerCase());
                            }
                            //holder.textViewLunch.setText(pager3List.get(i).getTitle());
                            if (pager3List.get(i).getTotalCalories() != null && !pager3List.get(i).getTotalCalories().matches("")) {
                                holder.lunchCaloriesLL.setVisibility(View.VISIBLE);
                                holder.lunchCaloriesTV.setText(pager3List.get(i).getTotalCalories());
                            } else {
                                holder.lunchCaloriesLL.setVisibility(View.GONE);
                            }
                            if (mContext != null) {
                                if (pager3List.get(i).getImageURL() != null)
                                    Glide.with(mContext).load(pager3List.get(i).getImageURL())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException
                                                                                    e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    FirebaseCrashlytics.getInstance().recordException(e);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                /*holder.nutritionShimmerLayout.stopShimmerAnimation();
                                                holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                                holder.nutritionLayout.setVisibility(View.VISIBLE);
                                                holder.imageLunch.setVisibility(View.VISIBLE);*/
                                                    return false;
                                                }
                                            })
                                            .into(holder.imageLunch);
                            }

                        } else if (pager3List.get(i).getIndex() == 3) {
                            String title = pager3List.get(i).getTitle();
                            if (pager3List.get(i).getTitle() == null)
                                holder.textViewDinner.setVisibility(View.GONE);
                            else
                                holder.textViewDinner.setVisibility(View.VISIBLE);
                            if (pager2List.get(i).getTitle() != null && pager2List.get(i).getTitle().length() > 1) {
                                holder.textViewDinner.setText(pager2List.get(i).getTitle().substring(0, 1).toUpperCase() + "" + pager2List.get(i).getTitle().toString().substring(1).toLowerCase());
                            }
                            //holder.textViewDinner.setText(pager3List.get(i).getTitle());
                            //nutritionId = pager3List.get(i).getId();
                            if (pager3List.get(i).getTotalCalories() != null && !pager3List.get(i).getTotalCalories().matches("")) {
                                holder.dinnerCaloriesLL.setVisibility(View.VISIBLE);
                                holder.dinnerCaloriesTV.setText(pager3List.get(i).getTotalCalories());
                            } else {
                                holder.dinnerCaloriesLL.setVisibility(View.GONE);
                            }

                            if (mContext != null) {
                                if (pager3List.get(i).getImageURL() != null)
                                    Glide.with(mContext).load(pager3List.get(i).getImageURL())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException
                                                                                    e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    FirebaseCrashlytics.getInstance().recordException(e);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                               /* holder.nutritionShimmerLayout.stopShimmerAnimation();
                                                holder.nutritionShimmerLayout.setVisibility(View.GONE);
                                                holder.nutritionLayout.setVisibility(View.VISIBLE);
                                                holder.imageDinner.setVisibility(View.VISIBLE);*/
                                                    return false;
                                                }
                                            })
                                            .into(holder.imageDinner);
                            }

                        }
                    }

                }
            }
        }

        holder.imageBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SessionUtil.isSubscriptionAvailable(mContext)) {
                    boolean isNutritionID = false;
                    Fragment nutritionFragment = new Nutrition_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", SharedData.token);
                    bundle.putString("refresh_token", SharedData.refresh_token);
                    if (position == 0) {
                        if (pager1List != null && pager1List.size() > 0) {
                            int myIndex = 0;
                            for (int i = 0; i < pager1List.size(); i++) {
                                if (pager1List.get(i).getIndex() != null &&
                                        pager1List.get(i).getIndex() == 1) {
                                    myIndex = i;
                                }
                            }
                            if (pager1List.get(myIndex).getIndex() != null &&
                                    pager1List.get(myIndex).getIndex() == 1 &&
                                    pager1List.get(myIndex).getTitle() != null) {
                                bundle.putInt("nutritionID", pager1List.get(myIndex).getId());
                                bundle.putString("nutritionTime", pager1List.get(myIndex).getTitle());
                                isNutritionID = true;
                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "pager1List:: Breakfast, " + position + " Response on click: " + pager1List.get(position).toString());
                                }
                                if(pager1List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                    bundle.putBoolean("isAddedInCart", pager1List.get(myIndex).isIs_added_in_shoppingList());
                                }else{
                                    bundle.putBoolean("isAddedInCart", false);
                                }
                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.e(TAG, "pager1List.get(" + myIndex + ").getIndex() == null OR" +
                                            " pager1List.get(" + myIndex + ").getIndex() != 1 OR " +
                                            "pager1List.get(" + myIndex + ").getTitle() == null");
                                }
                            }
                        } else {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "pager1List == null OR pager1List.size() == 0");
                            }
                        }
                    } else if (position == 1) {
                        if (pager2List != null && pager2List.size() > 0) {
                            int myIndex = 0;
                            for (int i = 0; i < pager2List.size(); i++) {
                                if (pager2List.get(i).getIndex() != null &&
                                        pager2List.get(i).getIndex() == 1) {
                                    myIndex = i;
                                }
                            }
                            if (pager2List.get(myIndex).getIndex() != null &&
                                    pager2List.get(myIndex).getIndex() == 1 &&
                                    pager2List.get(myIndex).getTitle() != null) {
                                bundle.putInt("nutritionID", pager2List.get(myIndex).getId());
                                bundle.putString("nutritionTime", pager2List.get(myIndex).getTitle());
                                isNutritionID = true;
                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "pager2List:: Breakfast, " + position + " Response on click: " + pager2List.get(position).toString());
                                }

                                if(pager2List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                    bundle.putBoolean("isAddedInCart", pager2List.get(myIndex).isIs_added_in_shoppingList());
                                }else{
                                    bundle.putBoolean("isAddedInCart", false);
                                }
                            }
                        }
                    } else if (position == 2) {
                        int myIndex = 0;
                        for (int i = 0; i < pager3List.size(); i++) {
                            if (pager3List.get(i).getIndex() != null &&
                                    pager3List.get(i).getIndex() == 1) {
                                myIndex = i;
                            }
                        }
                        if (pager3List != null && pager3List.size() > 0) {
                            if (pager3List.get(myIndex).getIndex() != null &&
                                    pager3List.get(myIndex).getIndex() == 1 &&
                                    pager3List.get(myIndex).getTitle() != null) {
                                bundle.putInt("nutritionID", pager3List.get(myIndex).getId());
                                bundle.putString("nutritionTime", pager3List.get(myIndex).getTitle());
                                isNutritionID = true;
                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "pager3List:: Breakfast, " + position + " Response on click: " + pager3List.get(position).toString());
                                }
                                if(pager3List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                    bundle.putBoolean("isAddedInCart", pager3List.get(myIndex).isIs_added_in_shoppingList());
                                }else{
                                    bundle.putBoolean("isAddedInCart", false);
                                }
                            }
                        }
                    } else {
                        isNutritionID = false;
                        return;
                    }
                    if (isNutritionID) {
                        bundle.putString("level_id", SharedData.level_id);
                        bundle.putString("goal_id", SharedData.goal_id);
                        bundle.putInt("dayNumber", day);
                        bundle.putInt("weekNumber", week);
                        nutritionFragment.setArguments(bundle);
                        loadFragment(nutritionFragment);
                    }
                } else {
                    if (mContext != null)
                        DialogUtil.showSubscriptionEndDialogBox(mContext, mContext.getResources());
                }
            }
        });

        holder.imageLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        boolean isNutritionID = false;
                        Fragment nutritionFragment = new Nutrition_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("token", SharedData.token);
                        bundle.putString("refresh_token", SharedData.refresh_token);
                        if (position == 0) {
                            if (pager1List != null && pager1List.size() > 1) {                        //if index is 1 its means
                                int myIndex = 0;
                                for (int i = 0; i < pager1List.size(); i++) {
                                    if (pager1List.get(i).getIndex() != null &&
                                            pager1List.get(i).getIndex() == 2) {
                                        myIndex = i;
                                    }
                                }
                                if (pager1List.get(myIndex).getIndex() != null &&
                                        pager1List.get(myIndex).getIndex() == 2 &&
                                        pager1List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager1List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager1List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager1List:: Lunch, " + position + " Response on click: " + pager1List.get(position).toString());
                                    }
                                    if(pager1List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager1List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else if (position == 1) {
                            if (pager2List != null && pager2List.size() > 1) {//if index is 1 its means
                                int myIndex = 0;
                                for (int i = 0; i < pager2List.size(); i++) {
                                    if (pager2List.get(i).getIndex() != null &&
                                            pager2List.get(i).getIndex() == 2) {
                                        myIndex = i;
                                    }
                                }
                                if (pager2List.get(myIndex).getIndex() != null &&
                                        pager2List.get(myIndex).getIndex() == 2 &&
                                        pager2List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager2List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager2List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager2List:: Lunch, " + position + " Response on click: " + pager2List.get(position).toString());
                                    }
                                    if(pager2List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager2List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else if (position == 2) {
                            if (pager3List != null && pager3List.size() > 1) {                        //if index is 1 its means
                                int myIndex = 0;
                                for (int i = 0; i < pager3List.size(); i++) {
                                    if (pager3List.get(i).getIndex() != null &&
                                            pager3List.get(i).getIndex() == 2) {
                                        myIndex = i;
                                    }
                                }
                                if (pager3List.get(myIndex).getIndex() != null &&
                                        pager3List.get(myIndex).getIndex() == 2 &&
                                        pager3List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager3List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager3List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager3List:: Lunch, " + position + " Response on click: " + pager3List.get(position).toString());
                                    }
                                    if(pager3List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager3List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else {
                            isNutritionID = false;
                            return;
                        }
                        if (isNutritionID) {
                            bundle.putString("level_id", SharedData.level_id);
                            bundle.putString("goal_id", SharedData.goal_id);
                            bundle.putInt("dayNumber", day);
                            bundle.putInt("weekNumber", week);
                            nutritionFragment.setArguments(bundle);
                            loadFragment(nutritionFragment);
                        }
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, mContext.getResources());
                    }

            }
        });

        holder.imageDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext != null)
                    if (SessionUtil.isSubscriptionAvailable(mContext)) {
                        boolean isNutritionID = false;
                        Fragment nutritionFragment = new Nutrition_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("token", SharedData.token);
                        bundle.putString("refresh_token", SharedData.refresh_token);
                        if (position == 0) {
                            if (pager1List != null && pager1List.size() > 2) {
                                int myIndex = 0;
                                for (int i = 0; i < pager1List.size(); i++) {
                                    if (pager1List.get(i).getIndex() != null &&
                                            pager1List.get(i).getIndex() == 3) {
                                        myIndex = i;
                                    }
                                }
                                if (pager1List.get(myIndex).getIndex() != null &&
                                        pager1List.get(myIndex).getIndex() == 3 &&
                                        pager1List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager1List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager1List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager1List:: Dinner, " + position + " Response on click: " + pager1List.get(position).toString());
                                    }
                                    if(pager1List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager1List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else if (position == 1) {
                            if (pager2List != null && pager2List.size() > 2) {
                                int myIndex = 0;
                                for (int i = 0; i < pager2List.size(); i++) {
                                    if (pager2List.get(i).getIndex() != null &&
                                            pager2List.get(i).getIndex() == 3) {
                                        myIndex = i;
                                    }
                                }
                                if (pager2List.get(myIndex).getIndex() != null &&
                                        pager2List.get(myIndex).getIndex() == 3 &&
                                        pager2List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager2List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager2List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager2List:: Dinner, " + position + " Response on click: " + pager2List.get(position).toString());
                                    }
                                    if(pager2List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager2List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else if (position == 2) {
                            if (pager3List != null && pager3List.size() > 2) {
                                int myIndex = 0;
                                for (int i = 0; i < pager3List.size(); i++) {
                                    if (pager3List.get(i).getIndex() != null &&
                                            pager3List.get(i).getIndex() == 3) {
                                        myIndex = i;
                                    }
                                }
                                if (pager3List.get(myIndex).getIndex() != null &&
                                        pager3List.get(myIndex).getIndex() == 3 &&
                                        pager3List.get(myIndex).getTitle() != null) {
                                    bundle.putInt("nutritionID", pager3List.get(myIndex).getId());
                                    bundle.putString("nutritionTime", pager3List.get(myIndex).getTitle());
                                    isNutritionID = true;
                                    if (Common.isLoggingEnabled) {
                                        Log.d(Common.LOG, "pager3List:: Dinner, " + position + " Response on click: " + pager3List.get(position).toString());
                                    }
                                    if(pager3List.get(myIndex).isIs_added_in_shoppingList()!=null) {
                                        bundle.putBoolean("isAddedInCart", pager3List.get(myIndex).isIs_added_in_shoppingList());
                                    }else{
                                        bundle.putBoolean("isAddedInCart", false);
                                    }
                                }
                            }
                        } else {
                            isNutritionID = false;
                            return;
                        }
                        if (isNutritionID) {
                            bundle.putString("level_id", SharedData.level_id);
                            bundle.putString("goal_id", SharedData.goal_id);
                            bundle.putInt("dayNumber", day);
                            bundle.putInt("weekNumber", week);
                            nutritionFragment.setArguments(bundle);
                            loadFragment(nutritionFragment);
                        }
                    } else {
                        DialogUtil.showSubscriptionEndDialogBox(mContext, mContext.getResources());
                    }
            }
        });

    }

    private void loadFragment(Fragment nutritionFragment) {

        // if (ConnectionDetector.isConnectedWithInternet(getActivity())) {
        @SuppressLint("CommitTransaction") FragmentTransaction mFragmentTransaction = ((FragmentActivity) mContext)
                .getSupportFragmentManager().beginTransaction();
        String backStateName = nutritionFragment.getClass().getName();

        FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.navigation_container, nutritionFragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }

      /* } else {
            showToast("Please turn ON your internet");

        }*/

    }

    @Override
    public int getItemCount() {
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final MaterialTextView textViewBreakFast;
        final MaterialTextView breakfastCaloriesTV, lunchCaloriesTV, dinnerCaloriesTV;

        final LinearLayout breakfastCaloriesLL, lunchCaloriesLL, dinnerCaloriesLL;
        final MaterialTextView textViewLunch;
        final MaterialTextView textViewDinner;
        MaterialCardView mBreakFastCardview, mLunchCardView, mDinnerCardView;
        ImageView imageBreakfast, imageLunch, imageDinner;
        ShimmerFrameLayout nutritionShimmerLayout;
        LinearLayout nutritionLayout;

        ViewHolder(View itemView) {
            super(itemView);
            textViewBreakFast = itemView.findViewById(R.id.textViewBreakfast);
            textViewLunch = itemView.findViewById(R.id.textViewLunch);
            textViewDinner = itemView.findViewById(R.id.textViewDinner);
            mBreakFastCardview = itemView.findViewById(R.id.breakfastCardView);
            mLunchCardView = itemView.findViewById(R.id.LunchCardView);
            mDinnerCardView = itemView.findViewById(R.id.DinnerCardView);

            //nutritions imageViews
            imageBreakfast = itemView.findViewById(R.id.imageBreakfast);
            imageLunch = itemView.findViewById(R.id.imageLunch);
            imageDinner = itemView.findViewById(R.id.imageDinner);
            nutritionShimmerLayout = itemView.findViewById(R.id.nutritionShimmerLayout);
            nutritionLayout = itemView.findViewById(R.id.nutritionLL);

            breakfastCaloriesLL = itemView.findViewById(R.id.breakfastCaloriesLL);
            lunchCaloriesLL = itemView.findViewById(R.id.lunchCaloriesLL);
            dinnerCaloriesLL = itemView.findViewById(R.id.dinnerCaloriesLL);

            breakfastCaloriesTV = itemView.findViewById(R.id.breakfastCaloriesTV);
            lunchCaloriesTV = itemView.findViewById(R.id.lunchCaloriesTV);
            dinnerCaloriesTV = itemView.findViewById(R.id.dinnerCaloriesTV);
        }

    }


}
