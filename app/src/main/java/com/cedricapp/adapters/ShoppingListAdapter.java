package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cedricapp.interfaces.ShoppingListInterface;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.ShoppingDataModel;
import com.cedricapp.R;
import com.cedricapp.fragment.Specific_Nutrition_Fragment;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingViewHolder> {

    private final List<ShoppingDataModel> dataArrayList;
    private final Context context;
    ShoppingListInterface shoppingListInterface;

    private DBHelper dbHelper;


    public ShoppingListAdapter(Context context, List<ShoppingDataModel> dataArrayList) {
        this.dataArrayList = dataArrayList;
        this.context = context;
        //   this.shoppingListInterface = shoppingListInterface;
        dbHelper = new DBHelper(context);

    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_to_shoppinglist_recyclerview, parent, false);

        ShoppingListAdapter.ShoppingViewHolder myViewHolder = new ShoppingListAdapter.ShoppingViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        System.out.println(dataArrayList.toString() + "data hhy");
        final ShoppingDataModel myListData = dataArrayList.get(position);

        holder.shimmerFrameLayout.startShimmerAnimation();

        holder.name.setText(myListData.getNutritionName());
        //   Glide.with(context).asBitmap().load(myListData.getNutritionImage()).into(holder.imageViewIcon);
        //TODo needs to be replace after url provided
        /* myListData.getNutritionImage()*/
        Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/cedric-8cb7d.appspot.com/o/images%2Fnutritions%2FPeanut%20butter%20%26%20Jelly%20oats.jpg?alt=media&token=221a15a8-ba57-42b1-8341-9f03d9a424f6")
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
                        holder.constraintLayout.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.imageViewIcon);


        holder.imageViewIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Nutrition").setMessage("Are you sure want to delete? ")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //shoppingListInterface.onLongDeleteClickListener(position);


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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


        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new Specific_Nutrition_Fragment();
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                // bundle.putString("profileImage", String.valueOf(myListData.getNutritionImage()));
                bundle.putString("selectedNutritionName", myListData.getNutritionName());

                fragment.setArguments(bundle);

                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }


    protected class ShoppingViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageViewIcon, deleteIcon;
        ConstraintLayout constraintLayout;
        ShimmerFrameLayout shimmerFrameLayout;

        public ShoppingViewHolder(View view) {
            super(view);
            this.name = (TextView) itemView.findViewById(R.id.textViewNutritionName);
            this.imageViewIcon = (CircleImageView) itemView.findViewById(R.id.nutritionImage);
            //  this.deleteIcon = (ImageView) itemView.findViewById(R.id. deleteNutrition);
            constraintLayout = view.findViewById(R.id.constraintNutritionLayout);
            shimmerFrameLayout = view.findViewById(R.id.shimmerLayoutForList);

        }
    }
}
