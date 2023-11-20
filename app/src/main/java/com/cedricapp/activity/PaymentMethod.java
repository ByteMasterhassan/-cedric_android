package com.cedricapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidwidgets.formatedittext.widgets.FormatEditText;
import com.cedricapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PaymentMethod extends AppCompatActivity {
    private MaterialButton mPaymentButton;
    private TextInputEditText mCvv, mExpDate;
    private FormatEditText mCardNumber;
    private TextInputLayout mCardType;
    private AutoCompleteTextView mAutoCompleteCardType;
    ArrayList<String> arrayList_cardType;
    ArrayAdapter<String> arrayAdapter_cardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
      /*  mCvv = findViewById(R.id.editTextCvv);
        mExpDate = findViewById(R.id.editTextExpDate);
        mCardNumber = findViewById(R.id.editTextCardNumber);
        mCardNumber.setFormat("---- ---- ---- ----");

        mPaymentButton = findViewById(R.id.btnPaymentMethod);


        //For Dropdown icon
        mCardType = findViewById(R.id.textInputLayoutCardType);
        mAutoCompleteCardType = findViewById(R.id.paymentCard);
        mAutoCompleteCardType.setDropDownBackgroundDrawable(Drawable.createFromPath("#FFC153"));
        arrayList_cardType = new ArrayList<>();
        arrayList_cardType.add("Visa Card");
        arrayList_cardType.add("Master Card");
        arrayList_cardType.add("PayPal");
        arrayAdapter_cardType = new ArrayAdapter<>(getApplicationContext(),
                R.layout.dropdown_item, arrayList_cardType);
        mAutoCompleteCardType.setAdapter(arrayAdapter_cardType);
        mAutoCompleteCardType.setThreshold(1);


        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentValidation();
            }
        });


    }

    private void paymentValidation() {
        if (mAutoCompleteCardType.getText().toString().isEmpty()) {
            mAutoCompleteCardType.setError(getResources().getString(R.string.cardType_error));

        } else if (mCvv.getText().toString().isEmpty()) {
            mCvv.setError(getResources().getString(R.string.Cvv_error));

        } else if (mCardNumber.getText().toString().isEmpty()) {
            mCardNumber.setError(getResources().getString(R.string.card_error));

        } else if (mExpDate.getText().toString().isEmpty()) {
            mExpDate.setError(getResources().getString(R.string.date_error));
        } else {
            Intent intent = new Intent(com.cedricapp.activity.PaymentMethod.this,
                    com.cedricapp.fragment.PaymentCategory.class);
            intent.putExtra("cardType", mAutoCompleteCardType.getText().toString());
            intent.putExtra("cardNumber", mCardNumber.getText().toString());
            intent.putExtra("cardCvv", mCvv.getText().toString());
            intent.putExtra("expireDate", mExpDate.getText().toString());
            Toast.makeText(PaymentMethod.this, "operation successful", Toast.LENGTH_LONG).show();
            startActivity(intent);

        }*/
    }
}