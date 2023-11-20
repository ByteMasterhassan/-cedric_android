package com.cedricapp.service;

import static com.cedricapp.common.Common.EXCEPTION;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cedricapp.common.Common;
import com.cedricapp.common.ConnectionDetector;
import com.cedricapp.common.SharedData;
import com.cedricapp.localdatabase.DBHelper;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.R;
import com.cedricapp.retrofit.ApiClient;
import com.cedricapp.utils.LogsHandlersUtils;
import com.cedricapp.utils.SessionUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageService extends Service {

    FirebaseStorage storageRef;
    DBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        storageRef = FirebaseStorage.getInstance();
        dbHelper = new DBHelper(getApplicationContext());
        if (intent != null) {
            if (intent.hasExtra("imageURI")) {
                Uri uri = Uri.parse(intent.getStringExtra("imageURI"));
                String userID = SessionUtil.getUserID(getApplicationContext());
                String name = intent.getStringExtra("name");
                String email = SessionUtil.getUserEmailFromSession(getApplicationContext());
                String height = intent.getStringExtra("height");
                String weight = intent.getStringExtra("weight");
                String age = intent.getStringExtra("age");


                if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                    doSendBroadcast("initSnackbar", 0);
                    /*Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }*/

                    Bitmap bitmap = null;
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        if(Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();


                        storageRef.getReference("profile_images/" + userID)/*.putFile(uri)*/.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // progressDialog.dismiss();
                                        //Toast.makeText(requireContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                doSendBroadcast("Uploaded Successfully", 100);
                                                if (Common.isLoggingEnabled) {
                                                    Log.d(Common.LOG, "Firebase Stored path is " + uri.toString());
                                                }
                                                updateProfile(name, email, height, weight, age, uri.toString());
                                            }
                                        });
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                        doSendBroadcast("progress", (int) progress);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        FirebaseCrashlytics.getInstance().recordException(e);
                                        if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                            new LogsHandlersUtils(getApplicationContext()).getLogsDetails("UpdateProfileFragment_ImageUploadingFailure",
                                                    SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.caughtException(e));
                                        }
                                        if (Common.isLoggingEnabled) {
                                            Log.e(Common.LOG, "Error Message: " + e.getMessage());
                                        }
                                        updateProfile(name, email, height, weight, age, uri.toString());
                                    }
                                });
                    }else{
                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "BMP is null while compressing image" );
                        }
                    }
                }
            }
        }
        /* return super.onStartCommand(intent, flags, startId);*/
        return START_NOT_STICKY;
    }

    private void doSendBroadcast(String message, int progress) {
        Intent it = new Intent("EVENT_SNACKBAR");
        if (!TextUtils.isEmpty(message)) {
            it.putExtra("message", message);
            it.putExtra("progress", progress);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
    }

    private void updateProfile(String name, String email,
                               String height, String weight, String age, String img) {

        String levelID = SessionUtil.getUserLevelID(getApplicationContext());
        String goalID = SessionUtil.getUserGoalID(getApplicationContext());
        if (!levelID.matches("") && !levelID.matches("0") &&
                !goalID.matches("") && !goalID.matches("0")) {
            Call<SignupResponse> updateProfileCall = ApiClient.getService().updateProfileData("Bearer " + SharedData.token, SessionUtil.getUserID(getApplicationContext()), weight, height, age, SessionUtil.getUserGender(getApplicationContext()), Integer.parseInt(goalID), Integer.parseInt(levelID), SharedData.unitType, name, email, img, SessionUtil.getFoodPreferenceID(getApplicationContext()));


            // on below line we are executing our method.
            updateProfileCall.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(@NotNull Call<SignupResponse> call, @NotNull Response<SignupResponse> response) {
                    // this method is called when we get response from our api.
                    SignupResponse updateProfileModel = response.body();
                    String message;
                    if (response.isSuccessful()) {
                        //message = ResponseStatus.getResponseCodeMessage(response.code());
                        /*if (Common.isLoggingEnabled) {
                            if (message != null) {
                                Log.d(Common.LOG, "Response Status " + message.toString());
                            }
                        }*/
                        // Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
                        try {
                            if (Common.isLoggingEnabled) {
                                Log.d(Common.LOG, "Profile reponse after update: " + response.body().toString());
                            }
                            if (updateProfileModel != null) {
                                /*if (updateProfileModel.isStatus() == true) {*/
                                dbHelper.updateUserProfile(updateProfileModel);
                                SharedData.id = updateProfileModel.getData().user_id;
                                //SharedData.username = updateProfileModel.getData().getName();
                                // System.out.println("username ;;;;;;;;;;;;;;;;;;;" + SharedData.username);

                                //update username in seesion sharedpreference

                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "--------------save updated profile details in shared preferences------------");
                                    Log.d(Common.LOG, "Usernmae: " + SharedData.username);
                                    Log.d(Common.LOG, "Age: " + SharedData.age);
                                    Log.d(Common.LOG, "Height: " + SharedData.height);
                                    Log.d(Common.LOG, "Weight: " + SharedData.weight);
                                }
                                SharedData.username = updateProfileModel.getData().getName();
                                SessionUtil.setUsername(getApplicationContext(), SharedData.username);

                                SharedData.age = updateProfileModel.getData().age;
                                SessionUtil.setUserAge(getApplicationContext(), SharedData.age);

                                SharedData.height = updateProfileModel.getData().getHeight();
                                SessionUtil.setUserHeight(getApplicationContext(), SharedData.height);

                                SharedData.weight = updateProfileModel.getData().getWeight();
                                SessionUtil.setUserWeight(getApplicationContext(), SharedData.weight);

                                SessionUtil.setUserImgURL(getApplicationContext(), updateProfileModel.getData().getAvatar());

                            } else {
                                if (Common.isLoggingEnabled) {
                                    Log.d(Common.LOG, "Update Profile model is null");
                                }

                                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                            }

                        } catch (Exception e) {
                            if (Common.isLoggingEnabled) {
                                Log.e(Common.LOG, "Update Profile Fragment exception: " + e.toString());
                                e.printStackTrace();

                            }
                            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                                new LogsHandlersUtils(getApplicationContext()).getLogsDetails("UpdateProfileFragmentService_Update_Profile Fragment exception",
                                        SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.caughtException(e));
                            }
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                        //StopLoading();
                        // loadDashboardFragment();
                        //loadFragment();
                        //snackbar.dismiss();
                    } else {
                        // snackbar.dismiss();
                        if (updateProfileModel != null && updateProfileModel.getMessage() != null) {
                            Toast.makeText(getApplicationContext(), updateProfileModel.getMessage().toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }

                        if (Common.isLoggingEnabled) {
                            Log.e(Common.LOG, "Response is unsuccessful");
                        }
                    }
                    stopSelf();

                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                    FirebaseCrashlytics.getInstance().recordException(t);
                    if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                        new LogsHandlersUtils(getApplicationContext()).getLogsDetails("UpdateProfileFragmentService_updateProfileAPICall",
                                SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, SharedData.throwableObject(t));
                    }
                    stopSelf();

                }
            });
        } else {
            if (ConnectionDetector.isConnectedWithInternet(getApplicationContext())) {
                new LogsHandlersUtils(getApplicationContext()).getLogsDetails("UpdateProfileFragmentService_updateProfileAPICall",
                        SessionUtil.getUserEmailFromSession(getApplicationContext()), EXCEPTION, "Exception while uploading profile and reason is Goal ID : " + SessionUtil.getUserGoalID(getApplicationContext()) + " and Level ID: " + SessionUtil.getUserGoal(getApplicationContext()));
            }
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
