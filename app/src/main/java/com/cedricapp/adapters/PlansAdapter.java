package com.cedricapp.adapters;

import static com.cedricapp.common.Common.EXCEPTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.PlanClickListener;
import com.cedricapp.model.PlansDataModel;
import com.cedricapp.fragment.PaymentCategory;
import com.cedricapp.R;
import com.cedricapp.common.SharedData;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Locale;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.MyViewHolder> {

    // creating a variable for our array list and context.
    private PlansDataModel plansData;
    private Context mcontext;
    String plan, planMonthlyPrice, planDiscount;
    String planPrice = "";
    MaterialCardView mCardViewForPlans;
    int index = -1;

    private int listSize;
    private String planId;
    PlanClickListener planClickListener;

    public PlansAdapter(PlansDataModel plansData, Context mcontext, PlanClickListener planClickListener) {
        this.plansData = plansData;
        this.mcontext = mcontext;
        this.planClickListener = planClickListener;
    }

    @Override
    public PlansAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plans_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlansDataModel model = plansData;

        //Show layout if status is active
        if (model.getData().get(position) != null && model.getData().get(position).getStatus() != null) {
            if (model.getData().get(position).getStatus().matches("active")) {
                holder.mCardLayout.setVisibility(View.VISIBLE);
            }
        }
        if (model.getData() != null) {
            if (model.getData().get(position) != null) {
                if (model.getData().get(position).getPlanId() != null) {
                    planId = model.getData().get(position).getPlanId();
                }
                /*if (model.getData().get(position).getIntervalCount() != null) {
                    if (model.getData().get(position).getIntervalCount().matches("1"))
                        holder.textViewDuration.setText("Monthly");
                    else if (model.getData().get(position).getIntervalCount().matches("3"))
                        holder.textViewDuration.setText("Quarterly");
                    else if (model.getData().get(position).getIntervalCount().matches("6"))
                        holder.textViewDuration.setText("Half-Yearly");
                    else if(model.getData().get(position).getIntervalCount().matches("12"))
                        holder.textViewDuration.setText("Yearly");
                }*/


                //-------------------THis is with formula
                try {
                    String savedLanguage = SessionUtil.getlangCode(mcontext);
                    Locale current = mcontext.getResources().getConfiguration().locale;
                    String language = current.getLanguage();
                    if (model.getData().get(position).getNameEn() != null
                            && model.getData().get(position).getNameSv()!=null) {
                        if (savedLanguage.matches("")) {
                            if (language.matches("sv")) {
                                holder.textViewDuration.setText(model.getData().get(position).getNameSv());
                            }else{
                                holder.textViewDuration.setText(model.getData().get(position).getNameEn());
                            }
                        }else{
                            if (savedLanguage.matches("sv")) {
                                holder.textViewDuration.setText(model.getData().get(position).getNameSv());
                            }else{
                                holder.textViewDuration.setText(model.getData().get(position).getNameEn());
                            }
                        }
                    }

                    if (model.getData().get(position).getCurrency() != null && model.getData().get(position).getAmount() != null && model.getData().get(position).getIntervalCount() != null) {
                        holder.textViewTotalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " " + model.getData().get(position).getAmount());
                    }

                    if (model.getData().get(position).getCurrency() != null && model.getData().get(position).getOriginalPrice() != null) {
                        holder.textViewOriginalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " " + (Integer.parseInt(model.getData().get(position).getAmount()) / Integer.parseInt(model.getData().get(position).getIntervalCount())) + "/"+mcontext.getString(R.string.month));
                        //holder.textViewOriginalPrice.setPaintFlags(holder.textViewOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        if (Integer.parseInt(model.getData().get(position).getOriginalPrice()) == 0) {
                            holder.totalPriceBeforeDiscountTV.setVisibility(View.GONE);
                        } else {
                            holder.totalPriceBeforeDiscountTV.setText(model.getData().get(position).getCurrency().toUpperCase() + " " + Integer.parseInt(model.getData().get(position).getOriginalPrice()));
                            holder.totalPriceBeforeDiscountTV.setPaintFlags(holder.totalPriceBeforeDiscountTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                    }
                    if (model.getData().get(position).getDiscount() != null) {

                        if (Integer.parseInt(model.getData().get(position).getDiscount()) == 0) {
                            holder.discountPercentageTV.setVisibility(View.GONE);
                        } else
                            holder.discountPercentageTV.setText(model.getData().get(position).getDiscount() + "% "+mcontext.getString(R.string.off));

                    } else {
                        holder.discountPercentageTV.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    if (Common.isLoggingEnabled)
                        if(mcontext!=null){
                            new LogsHandlersUtils(mcontext).getLogsDetails("PlansAdapater_imageLoadingException",
                                    SessionUtil.getUserEmailFromSession(mcontext),EXCEPTION, SharedData.caughtException(ex));
                        }
                        ex.printStackTrace();
                }

                /*if (model.getData().get(position).getName() != null && model.getData().get(position).getName().matches("Monthly")) {
                    holder.textViewDuration.setText("1 Month");
                    holder.textViewTotalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 59");
                    holder.totalPriceBeforeDiscountTV.setVisibility(View.GONE);
                    holder.textViewOriginalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 59/Month");
                    //holder.totalPriceBeforeDiscountTV.setPaintFlags(holder.totalPriceBeforeDiscountTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else if (model.getData().get(position).getName() != null && model.getData().get(position).getName().matches("Quarterly")) {
                    holder.textViewDuration.setText("6 Months");
                    holder.textViewTotalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 294");
                    holder.textViewOriginalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 49/Month");
                    holder.totalPriceBeforeDiscountTV.setText(model.getData().get(position).getCurrency().toUpperCase() + " 354");
                    holder.totalPriceBeforeDiscountTV.setPaintFlags(holder.totalPriceBeforeDiscountTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.discountPercentageTV.setText("17% OFF");
                }
                if (model.getData().get(position).getName() != null && model.getData().get(position).getName().matches("Yearly")) {
                    holder.textViewDuration.setText("12 Months");
                    holder.textViewTotalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 468");
                    holder.textViewOriginalPrice.setText(model.getData().get(position).getCurrency().toUpperCase() + " 39/Month");
                    holder.totalPriceBeforeDiscountTV.setText(model.getData().get(position).getCurrency().toUpperCase() + " 708");
                    holder.totalPriceBeforeDiscountTV.setPaintFlags(holder.totalPriceBeforeDiscountTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    //holder.textViewOriginalPrice.setPaintFlags(holder.totalPriceBeforeDiscountTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.discountPercentageTV.setText("34% OFF");
                }*/

                holder.mCardLayout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        index = position;
                        planId = model.getData().get(position).getPlanId();
                        planClickListener.openPaymentSheet(model, position);
                        notifyDataSetChanged();

                    }
                });
            }
        }

        //  holder.textViewDiscount.setText(model.getData().data.get(position).getAmount());

        /*holder.textViewDuration.setText(model.getData().get(position).getIntervalCount() +" "+ model.getData().get(position).getInterval());*/
        /* holder.textViewPerMonthPrice.setText(model.getMonthlyPrice());*/


        if (index == position) {

            holder.textViewDuration.setTextColor(Color.BLACK);
            holder.textViewTotalPrice.setTextColor(Color.BLACK);
            //   holder.mCardViewForPlans.setCardBackgroundColor(R.drawable.gradient_drawable_button);
            holder.mCardLayout.setBackgroundResource(R.drawable.gradient_drawable_button);
            //disabling the button after one click


            //SharedData.plan = holder.textViewDuration.getText().toString();
            //SharedData.planDiscount = holder.discountPercentageTV.getText().toString();
            //SharedData.planMonthlyPrice = holder.textViewOriginalPrice.getText().toString();
            //String price = holder.textViewTotalPrice.getText().toString();
            String price = model.getData().get(position).getAmount();
            SharedData.planId = planId.toString();
            SharedData.planPosition=position;

            try {
                SharedData.planPrice = price.replaceAll("[^0-9]", "");
                if (Common.isLoggingEnabled)
                    Log.d(Common.LOG, "Selected Plan price is " + SharedData.planPrice);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            PaymentCategory.check = true;

        } else {
            //  PaymentCategory.check=false;
            holder.textViewDuration.setTextColor(Color.WHITE);
            holder.textViewTotalPrice.setTextColor(Color.WHITE);
            holder.mCardLayout.setBackgroundResource(R.drawable.webview_style);
        }

    }

    @Override
    public int getItemCount() {
        if (plansData.getData().size() != 0) {
            listSize = plansData.getData().size();
            return listSize;

        } else {
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView textViewDuration, totalPriceBeforeDiscountTV, textViewOriginalPrice, textViewTotalPrice, discountPercentageTV;
        MaterialCardView mCardViewForPlans;
        LinearLayout mCardLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDuration = itemView.findViewById(R.id.planDurationTV);
            textViewOriginalPrice = itemView.findViewById(R.id.originalPrice);
            textViewTotalPrice = itemView.findViewById(R.id.planTotalPriceTV);
            discountPercentageTV = itemView.findViewById(R.id.discountPercentageTV);
            totalPriceBeforeDiscountTV = itemView.findViewById(R.id.totalPriceBeforeDiscountTV);
            //  mCardViewForPlans = itemView.findViewById(R.id.cardViewPayMethod);
            mCardLayout = itemView.findViewById(R.id.cardMainLayout);

        }
    }
}
