package com.cedricapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.SharedData;
import com.cedricapp.interfaces.SubscriptionClickListener;
import com.cedricapp.model.PlansDataModel;
import com.cedricapp.R;
import com.cedricapp.utils.SessionUtil;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {

    // creating a variable for our array list and context.
    private PlansDataModel plansDataModel;
    private Context mcontext;
    boolean isSubscribed;
    SubscriptionClickListener planClickListener;
    String subscriptionStatus;

    public SubscriptionAdapter(PlansDataModel plansDataModel, Context mcontext, boolean isSubscribed, SubscriptionClickListener planClickListener, String subscription_status) {
        this.plansDataModel = plansDataModel;
        this.mcontext = mcontext;
        this.isSubscribed = isSubscribed;
        this.planClickListener=planClickListener;
        this.subscriptionStatus=subscription_status;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_packages, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlansDataModel model = plansDataModel;

        if (model.getData().get(position).getCurrency() != null &&
                model.getData().get(position).getAmount() != null &&
                model.getData().get(position).getIntervalCount() != null &&
                model.getData().get(position).getInterval() != null) {
            String planDetails = model.getData().get(position).getCurrency() + " " + Integer.parseInt(model.getData().get(position).getAmount()); /*/ Integer.parseInt(model.getData().get(position).getIntervalCount()) + "/"*//* + model.getData().get(position).getIntervalCount()*//*
                    + model.getData().get(position).getInterval();*/
            holder.packageDetailsTv.setText(planDetails);

            if (model.getData().get(position).getNameEn() != null)
                holder.packageTypeTv.setText(model.getData().get(position).getNameEn());

            if (model.getData().get(position).getOriginalPrice() != null && model.getData().get(position).getIntervalCount() != null)
                if (Integer.parseInt(model.getData().get(position).getOriginalPrice()) == 0) {
                    holder.planDiscountTv.setVisibility(View.GONE);
                } else {
                    holder.planDiscountTv.setText("Instead of " + model.getData().get(position).getCurrency().toUpperCase() + " " + Integer.parseInt(model.getData().get(position).getOriginalPrice())/* / Integer.parseInt(model.getData().get(position).getIntervalCount())+ "/Month"*/);
                }
            /*if (model.getData().get(position).getName() != null) {
                if ((model.getData().get(position).getIntervalCount() != null)) {
                    if (model.getData().get(position).getIntervalCount().matches("1"))
                        holder.packageTypeTv.setText("Monthly");
                    else if (model.getData().get(position).getIntervalCount().matches("6"))
                        holder.packageTypeTv.setText("Half-Yearly");
                    else
                        holder.packageTypeTv.setText("Yearly");
                }

            }*/

            /*if(model.getData().get(position).getName().matches("Monthly")){
                holder.packageTypeTv.setText("1 Month");
                planDetails = "SEK 59";
                holder.planDiscountTv.setVisibility(View.GONE);
            }else if(model.getData().get(position).getName().matches("Quarterly")){
                holder.packageTypeTv.setText("6 Months");
                planDetails = "SEK 294";
                holder.planDiscountTv.setText("Instead of SEK 354");
            }else if(model.getData().get(position).getName().matches("Yearly")){
                planDetails = "SEK 468";
                holder.packageTypeTv.setText("12 Months");
                holder.planDiscountTv.setText("Instead of SEK 708");
            }*/



     /*   holder.textViewDuration.setText(model.getData().data.get(position).getInterval_count() +" "+ model.getData().data.get(position).getInterval());
        String discount = "Discount of " + model.getDiscount();*/

     /*  if (model.getId().equalsIgnoreCase("OKt9wko90loYfqRn5ohN")){
            holder.packageTypeTv.setText("BASIC");
        }else if (model.getId().equalsIgnoreCase("S8HnHZJxtferiZrvGXxb")){
            holder.packageTypeTv.setText("PREMIUM");
        }else if (model.getId().equalsIgnoreCase("wx39uQrWwqyUhfIgHCJ8") ){
            holder.packageTypeTv.setText("STANDARD");
        }*/


        }

     /*   if(isSubscribed)
            holder.subscribeTv.setText("Subscribe");
        else
            holder.subscribeTv.setText("Re-Subscribe");*/

        /*if(model.getData().get(position).getStatus()!=null)
        {
            holder.subscriptionStatusTv.setText("Status: "+model.getData().get(position).getStatus());
        }*/
        //  holder.planDiscountTv.setText(discount);

        holder.subscribeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fragmentKey = 1;
                int goalId=0;
                String name = SharedData.username;
                String email = SharedData.email;
                String token = SharedData.token;
                String goal = SessionUtil.getUserGoal(mcontext);
                if (SessionUtil.getUserGoal(mcontext).matches("lose_weight")) {
                    goalId = 1;
                } else if (SessionUtil.getUserGoal(mcontext).matches("muscles_gain")) {
                    goalId = 2;
                } else if (SessionUtil.getUserGoal(mcontext).matches("get_fitted_and_tone")) {
                    goalId = 3;
                }
                String price_id = model.getData().get(position).getPlanId();

                //Toast.makeText(holder.itemView.getContext(),name+" "+email+" "+ SharedData.token+ price_id+"Not Implemented yet..!",Toast.LENGTH_SHORT).show();

                /*Intent intent = new Intent(mcontext, PaymentCategory.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra(Common.SESSION_USER_PLAN_ID, price_id);
                intent.putExtra(Common.SESSION_USER_PLAN_POSITION, position);
                intent.putExtra(Common.SESSION_USER_GOAL_ID, goalId);
                intent.putExtra(Common.SESSION_USER_PRODUCT_ID, SessionUtil.getProductID(mcontext));
                intent.putExtra("token", token);
                intent.putExtra("fragmentKey", fragmentKey);
                mcontext.startActivity(intent);*/

                planClickListener.openPaymentSheet1(model, position,subscriptionStatus);
            }
        });

      /*  holder.textViewDiscount.setText(model.getDiscount());
        holder.textViewDuration.setText(model.getDuration());
        holder.textViewPerMonthPrice.setText(model.getMonthlyPrice());
        holder.textViewTotalPrice.setText(model.getTotalPrice());

        holder.mCardViewForPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                notifyDataSetChanged();



            }
        });
        if (index == position) {

            holder.textViewDuration.setTextColor(Color.BLACK);
            holder.textViewTotalPrice.setTextColor(Color.BLACK);
            holder.mCardViewForPlans.setCardBackgroundColor(Color.parseColor("#D5A243"));
            //disabling the button after one click


            SharedData.planId = plansDataArrayList.get(position).getId();
            Log.d("planId", SharedData.planId);
            SharedData.plan = holder.textViewDuration.getText().toString();
            SharedData.planDiscount = holder.textViewDiscount.getText().toString();
            SharedData.planMonthlyPrice = holder.textViewPerMonthPrice.getText().toString();
            String price = holder.textViewTotalPrice.getText().toString();
            System.out.println(price +"///////////////////////////////////////////////;;;;;;;;;");
            try {
                SharedData.planPrice= price.replaceAll("[^0-9]", "");
                System.out.println(SharedData.planPrice +"lllllllllllllllllllllllllllllllllllllllllllllllllllllllllll;;;;;;;;;;");
            } catch (Exception e) {
                e.printStackTrace();
            }
            PaymentCategory.check=true;

        } else {
          //  PaymentCategory.check=false;
            holder.textViewDuration.setTextColor(Color.WHITE);
            holder.textViewTotalPrice.setTextColor(Color.WHITE);
            holder.mCardViewForPlans.setCardBackgroundColor(Color.TRANSPARENT);


        }*/


    }

    @Override
    public int getItemCount() {
        return plansDataModel.getData().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView packageTypeTv, packageDetailsTv, planDiscountTv, subscribeTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            packageTypeTv = itemView.findViewById(R.id.packageTypeTv);
            packageDetailsTv = itemView.findViewById(R.id.packageDetailsTv);
            planDiscountTv = itemView.findViewById(R.id.planDiscountTv);
            subscribeTv = itemView.findViewById(R.id.subscribeTv);

        }
    }
}
