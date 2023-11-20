package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cedricapp.utils.SessionUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.interfaces.ShoppingItemDeleteCallback;
import com.cedricapp.model.NutritionDataModel;
import com.cedricapp.R;
import com.cedricapp.fragment.Specific_Nutrition_Fragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShoppingListRecipeAdapter extends RecyclerView.Adapter<ShoppingListRecipeAdapter.RecipeViewHolder> {
    private FirebaseAnalytics m_nutrition_FirebaseAnalytics;
    Context context;
    List<NutritionDataModel.Recipe> recipes;
    ShoppingItemDeleteCallback shoppingItemDeleteCallback;
    Resources resources;

    public ShoppingListRecipeAdapter(Context context, List<NutritionDataModel.Recipe> recipes, Resources resources) {
        this.context = context;
        this.recipes = recipes;
        this.resources=resources;

    }

    public void setShoppingItemDeleteCallback(ShoppingItemDeleteCallback callback) {
        shoppingItemDeleteCallback = callback;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_to_shoppinglist_recyclerview, parent, false);

        RecipeViewHolder myViewHolder = new RecipeViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.shimmerFrameLayout.startShimmerAnimation();

        holder.name.setText(recipes.get(position).getName());
        m_nutrition_FirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Glide.with(context).load(/*Common.IMG_BASE_URL + "" +*/ recipes.get(position).getImageURL())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException
                                                        e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        FirebaseCrashlytics.getInstance().recordException(e);
                        holder.shimmerFrameLayout.stopShimmerAnimation();
                        holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                        holder.constraintLayout.setVisibility(View.VISIBLE);
                        if (Common.isLoggingEnabled) {
                            if (e != null) {
                                e.printStackTrace();
                                Log.d(Common.LOG, "Exception occurred while displaying image in recipe adapter: " + e.toString());
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.shimmerFrameLayout.stopShimmerAnimation();
                        holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                        holder.constraintLayout.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.imageViewIcon);


        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(resources.getString(R.string.delete_nutrition)).setMessage(resources.getString(R.string.are_you_sure_want_to_delete))
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //shoppingListInterface.onLongDeleteClickListener(position);
                                if (context != null)
                                    if (ConnectionDetector.isConnectedWithInternet(context)) {
                                        shoppingItemDeleteCallback.deleteItemCallback("" + recipes.get(position).getId(), position);

                                    } else {

                                        Toast.makeText(context, resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }


                            }
                        }).setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //  holder.deleteIcon.setVisibility(View.INVISIBLE);

                            }
                        });
                builder.show();

                //  });
                return true;
            }
        });


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {

                    if (ConnectionDetector.isConnectedWithInternet(context)) {

                        Bundle nutritionParam = new Bundle();
                        nutritionParam.putInt("NutritionID",recipes.get(position).getId());
                        m_nutrition_FirebaseAnalytics.logEvent("Recipes",  nutritionParam);
                        m_nutrition_FirebaseAnalytics.setUserProperty("Gender", SessionUtil.getUserGender(context));
                       // mFirebaseAnalytics.setDefaultEventParameters(params);


                        Fragment fragment = new Specific_Nutrition_Fragment();
                        FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                                .getSupportFragmentManager().beginTransaction();

                        Bundle bundle = new Bundle();
                        bundle.putString("profileImage", /*Common.IMG_BASE_URL+*/recipes.get(position).getImageURL());
                        bundle.putString("selectedNutritionName", "" + recipes.get(position).getName());
                        bundle.putInt("selectedNutritionId", recipes.get(position).getId());

                        fragment.setArguments(bundle);

                        mFragmentTransaction.replace(R.id.navigation_container, fragment);
                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.commit();
                    } else {
                        Toast.makeText(context, resources.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    protected static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageViewIcon;
        ConstraintLayout constraintLayout;
        ShimmerFrameLayout shimmerFrameLayout;

        public RecipeViewHolder(View view) {
            super(view);
            this.name = (TextView) itemView.findViewById(R.id.textViewNutritionName);
            this.imageViewIcon = (CircleImageView) itemView.findViewById(R.id.nutritionImage);
            constraintLayout = view.findViewById(R.id.constraintNutritionLayout);
            shimmerFrameLayout = view.findViewById(R.id.shimmerLayoutForList);
        }
    }

}
