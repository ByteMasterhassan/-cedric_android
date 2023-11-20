package com.cedricapp.utils;

import static com.cedricapp.common.Common.ERROR;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.cedricapp.BuildConfig;
import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.addresselement.AddressDetails;
import com.stripe.android.paymentsheet.addresselement.AddressLauncher;
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult;

import java.util.Arrays;
import java.util.HashSet;

public class StripePaymentUtil {
    Activity activity;

    PaymentSheet.CustomerConfiguration customerConfig;

    private PaymentSheet paymentSheet;
    private AddressLauncher addressLauncher;

    String countryCode;

    Resources resources;

    String TAG = "PAYMENT_SHEET_TAG";

    private String paymentIntentClientSecret;

    private String customer;

    private String ephemeralKey;

    private String stripeKey;

    private AddressDetails shippingDetails;

    private final AddressLauncher.Configuration addressConfiguration =
            new AddressLauncher.Configuration.Builder()
                    .additionalFields(
                            new AddressLauncher.AdditionalFieldsConfiguration(
                                    AddressLauncher.AdditionalFieldsConfiguration.FieldConfiguration.REQUIRED
                            )
                    )
                    .allowedCountries(new HashSet<>(Arrays.asList("SE", "AE", "AU", "BE", "BR", "CA", "CH", "DE", "ES", "FR", "GB", "IE", "IT", "MX", "NO", "NL",
                            "PL", "RU", "TR", "US", "ZA", "PK")))
                    .title("Shipping Address")
                    .googlePlacesApiKey(BuildConfig.place_api_key)
                    .build();

    public StripePaymentUtil(Activity activity) {
        this.activity = activity;
        PaymentConfiguration.init(
                activity,
                BuildConfig.stripe_api_key
        );
        LocationUtil.getLocationByGeoLocationAPI(activity);
        paymentSheet = new PaymentSheet((ComponentActivity) activity, this::onPaymentSheetResult);
        addressLauncher = new AddressLauncher((ComponentActivity) activity, this::onAddressLauncherResult);
    }

    public void initSheet(String stripeKey, String customer, String ephemeralKey, String paymentIntentClientSecret) {
        this.stripeKey = stripeKey;
        this.customer = customer;
        this.ephemeralKey = ephemeralKey;
        this.paymentIntentClientSecret = paymentIntentClientSecret;
        customerConfig = new PaymentSheet.CustomerConfiguration(
                customer,
                ephemeralKey
        );
        PaymentConfiguration.init(activity, stripeKey);
        onPayClicked(activity.getWindow().getDecorView().getRootView());
        resources = Localization.setLanguage(activity, activity.getResources());
    }

    private void onPayClicked(View view) {
        if (SharedData.countryCode != null) {
            countryCode = SharedData.countryCode;
        } else {
            countryCode = "SE";
        }
        PaymentSheet.GooglePayConfiguration googlePayConfiguration;
        if (SessionUtil.getAPP_Environment(activity).matches("") ||
                SessionUtil.getAPP_Environment(activity).matches("production")
                || SessionUtil.getAPP_Environment(activity).matches("beta")) {
            googlePayConfiguration =
                    new PaymentSheet.GooglePayConfiguration(
                            PaymentSheet.GooglePayConfiguration.Environment.Production,
                            countryCode
                    );
        } else {
            googlePayConfiguration =
                    new PaymentSheet.GooglePayConfiguration(
                            PaymentSheet.GooglePayConfiguration.Environment.Test,
                            countryCode
                    );
        }
        PaymentSheet.Address address = new PaymentSheet.Address.Builder()
                .country(countryCode)
                .build();
        PaymentSheet.BillingDetails billingDetails = new PaymentSheet.BillingDetails.Builder()
                .address(address)
                .email("info@cedrics.se")
                .build();
        PaymentSheet.Configuration config = new PaymentSheet.Configuration.Builder(resources.getString(R.string.cedric_fitness_app))
                .customer(customerConfig)
                .googlePay(googlePayConfiguration)
                .defaultBillingDetails(billingDetails)
                .allowsDelayedPaymentMethods(false)
                .build();

        if (Common.isLoggingEnabled) {
            Log.e(TAG, "Google Pay: " + config.getGooglePay().toString());
        }

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, config);

        /*if (stripeKey != null && !stripeKey.matches("")) {
            onAddressClicked(view);
        } else {
            if(Common.isLoggingEnabled){
                Log.e(TAG,"stripe key is null or empty::"+stripeKey);
            }
        }*/
    }


    private void onAddressLauncherResult(AddressLauncherResult result) {
        // TODO: Handle result and update your UI
        if (result instanceof AddressLauncherResult.Succeeded) {
            shippingDetails = ((AddressLauncherResult.Succeeded) result).getAddress();
        } else if (result instanceof AddressLauncherResult.Canceled) {
            // TODO: Handle cancel
        }
    }

    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (Common.isLoggingEnabled) {
            if (paymentSheetResult != null)
                Log.d(TAG, "Payment Sheet Result: " + paymentSheetResult.toString());
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(activity, resources.getString(R.string.paymentcomplet), Toast.LENGTH_SHORT).show();

            /* getUserDetails(token);*/

            //stripePayment(name,email,"4242424242424242",12,26,"123",planId);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "Payment canceled!");
            }
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {

            /*Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();*/
            showAlert(resources.getString(R.string.paymentfaild), /*error.getLocalizedMessage()*/resources.getString(R.string.something_went_wrong));
            if (Common.isLoggingEnabled) {
                Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            }
            if (ConnectionDetector.isConnectedWithInternet(activity)) {
                new LogsHandlersUtils(activity)
                        .getLogsDetails("New User Payment Failed", SessionUtil.getUserEmailFromSession(activity)
                                , ERROR, "" + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            }
        }

    }

    private void showAlert(String title, @Nullable String message) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok), null)
                .create();
        dialog.show();
    }


}
