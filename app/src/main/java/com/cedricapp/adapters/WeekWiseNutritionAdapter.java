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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.common.SharedData;
import com.cedricapp.model.ChecklistModel;
import com.cedricapp.model.DashboardNutrition;
import com.cedricapp.model.RecipeUpdateModel;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cedricapp.common.Common;
import com.cedricapp.interfaces.QuantityCheckListener;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeekWiseNutritionAdapter extends RecyclerView.Adapter<WeekWiseNutritionAdapter.MyViewHolder> {

    List<DashboardNutrition.Data.Recipe> weekWiseList;
    private final Context context;
    private DBHelper dbHelper;
    NutritionDataModel nutritionDataModel;
    DashboardNutrition.Data.Recipe weekWiseNutritionModel;
    QuantityCheckListener quantityCheckListener;
    boolean isServingHandlerRunning;
    int btn;
    Resources resources;

    ArrayList<RecipeUpdateModel> recipeServingUpdateModels;
    ArrayList<Integer> integerArrayList;

    public WeekWiseNutritionAdapter(List<DashboardNutrition.Data.Recipe> weekWiseNutritionModelArrayList, Context context, QuantityCheckListener quantityCheckListener, int button,Resources resources) {
        this.weekWiseList = weekWiseNutritionModelArrayList;
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.quantityCheckListener = quantityCheckListener;
        btn = button;
        this.resources=resources;
    }


    @NonNull
    @Override
    public WeekWiseNutritionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_weekly_nutrition, parent, false);


        WeekWiseNutritionAdapter.MyViewHolder myViewHolder = new WeekWiseNutritionAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeekWiseNutritionAdapter.MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        weekWiseNutritionModel = weekWiseList.get(position);
        holder.shimmerFrameLayout.startShimmerAnimation();
        System.out.println("image" + weekWiseNutritionModel.getImageURL().toString());
        holder.nutritionName.setText(weekWiseNutritionModel.getName());
        holder.minusImage.setVisibility(View.VISIBLE);
        holder.plusImage.setVisibility(View.VISIBLE);
        holder.nutritionName.setVisibility(View.VISIBLE);
        holder.mConstraintLayout.setVisibility(View.VISIBLE);
        System.out.println("image" + weekWiseNutritionModel.getImageURL().toString());
        recipeServingUpdateModels = new ArrayList<>();
        integerArrayList = new ArrayList<>();

//       Glide.with(context).asBitmap().load(weekWiseNutritionModel.getNutritionImage())
//               .placeholder(R.drawable.progress_animation)
//               .error(R.drawable.try_later)
//               .into(holder.nutritionImage);
        if (weekWiseList.get(position).getImageURL() != null) {
            Glide.with(context).load(/*Common.IMG_BASE_URL+*/weekWiseList.get(position).getImageURL().toString())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException
                                                            e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerFrameLayout.stopShimmerAnimation();
                            holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            holder.mConstraintLayout.setVisibility(View.VISIBLE);
                            holder.nutritionImage.setVisibility(View.VISIBLE);

                            return false;
                        }
                    })
                    .into(holder.nutritionImage);
        }
        /*if (weekWiseNutritionModel.get != null) {
            holder.quantityTV.setText(weekWiseNutritionModel.getQuantity());
            integerArrayList.add(Integer.parseInt(weekWiseNutritionModel.getQuantity()));

        } else {*/
            holder.quantityTV.setText("1");
            integerArrayList.add(1);
       /* }*/


        AtomicInteger quantity = new AtomicInteger(Integer.parseInt(holder.quantityTV.getText().toString()));


        holder.minusImage.setOnClickListener(v -> {
            if(weekWiseNutritionModel.getId()!=null) {
                if (context != null) {
                    //if (ConnectionDetector.isConnectedWithInternet(context)) {
                    if (quantity.get() > 1) {
                        int qty = quantity.addAndGet(-1);
                        holder.quantityTV.setText(quantity.toString());
                        //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                        quantityCheckListener.onQuantityChangeListener(qty, position, weekWiseList.get(position).getId(), btn);
                    }
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                }
            }else{
                showToast(resources.getString(R.string.unable_to_decrease));
            }



        });

        holder.plusImage.setOnClickListener(v -> {
            if(weekWiseNutritionModel.getId()!=null) {
                if (context != null) {
                    int qty = quantity.addAndGet(+1);
                    holder.quantityTV.setText(quantity.toString());
                    //System.out.println(qty + "kkk" + currentIndex + "kkk");
                    //int day= Integer.parseInt(weekWiseList.get(position).getDay());

                    quantityCheckListener.onQuantityChangeListener(qty, position, weekWiseList.get(position).getId(), btn);
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Context is null in WeekWiseNutritionAdapter");
                }
            }else{
                showToast(resources.getString(R.string.unable_to_increase));
            }



        });

    }

    private void updateToDb(int quantity) {
      /*  weekWiseList.get(currentIndex).setIntake(String.valueOf(quantity));

        dbHelper.updateNutritionIntake(nutritionShoppingList.get(currentIndex));*/
    }


    @Override
    public int getItemCount() {
        return weekWiseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nutritionName,nutritionNameLunch,nutritionNameDinner ,quantityTV,
        quantityTVDinner,quantityTVLunch;
        ImageView nutritionImage, minusImage, plusImage;
        ConstraintLayout mConstraintLayout;
        ShimmerFrameLayout shimmerFrameLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.minusImage = itemView.findViewById(R.id.minus_btn_iv);
            this.plusImage = itemView.findViewById(R.id.add_btn_iv);
            this.nutritionName = itemView.findViewById(R.id.textViewNutritionName);
            this.nutritionImage = itemView.findViewById(R.id.nutritionImage);
            this.quantityTV = itemView.findViewById(R.id.quantity_tv);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayoutItemList);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayoutItemList);
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



    void updateShoppingToServer(String JSON_Recipe) {
        Call<ChecklistModel> call = ApiClient.getService().updateShoppingList("Bearer " + SharedData.token, "" + SessionUtil.getUserID(context), JSON_Recipe);
        call.enqueue(new Callback<ChecklistModel>() {
            @Override
            public void onResponse(Call<ChecklistModel> call, Response<ChecklistModel> response) {
                if (response.isSuccessful()) {
                    ChecklistModel checklistModel = response.body();
                    if (checklistModel != null) {
                        if (checklistModel.getMessage() != null) {
                            recipeServingUpdateModels.clear();
                            if (context != null)
                                Toast.makeText(context, checklistModel.getMessage().toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            if (Common.isLoggingEnabled)
                                Log.d(Common.LOG, "Response message is null");
                        }
                    } else {
                        if (Common.isLoggingEnabled)
                            Log.d(Common.LOG, "Response is not successful");
                    }

                } else {
                    /*if (isAdded() && getContext() != null)
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();*/
                    try {
                        Gson gson = new GsonBuilder().create();
                        ChecklistModel checkListJSON_Response = new ChecklistModel();
                        checkListJSON_Response = gson.fromJson(response.errorBody().string(), ChecklistModel.class);
                        if (checkListJSON_Response.getMessage() != null) {
                            if (context != null)
                                Toast.makeText(context, "" + checkListJSON_Response.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                        if (Common.isLoggingEnabled)
                            ex.printStackTrace();
                        if (context != null)
                            Toast.makeText(context, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChecklistModel> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                if (Common.isLoggingEnabled)
                    t.printStackTrace();
            }
        });
    }
}
