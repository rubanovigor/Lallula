package com.aiworker.lallula;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    public static TextView tv_facebookInfo, tv_AppStat;
    private LoginButton loginButton;
    ProfilePictureView profilePicture;
    private CallbackManager callbackManager;
//    private LoginManager loginManager;
    Button btn_AppStat, btn_facebookShare;
    ShareDialog shareDialog;

//    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
//    private String tempM="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        /** You need this method to be used only once to configure
        your key hash in your App Console at developers.facebook.com/apps */
        getFbKeyHash("com.aiworker.lallula");

        /** declare layout and corresponding elements */
        setContentView(R.layout.activity_main);

        tv_facebookInfo = (TextView)findViewById(R.id.tv_facebookInfo);
        tv_AppStat = (TextView)findViewById(R.id.tv_AppStat);
        btn_AppStat = (Button) findViewById(R.id.btn_AppStat);
        btn_facebookShare = (Button) findViewById(R.id.btn_facebookShare);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        profilePicture = (ProfilePictureView)findViewById(R.id.profile_picture);

        shareDialog = new ShareDialog(this);

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
//        loginManager = LoginManager.getInstance();
//        loginManager.logInWithPublishPermissions(this, PERMISSIONS);

        /** get app usage statistics and displayed it in TextView */
        btn_AppStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {UStats.printCurrentUsageStatus(MainActivity.this); }
        });

        /** Check if permission enabled */
        if (UStats.getUsageStatsList(this).isEmpty()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        profilePicture.setProfileId(null);
        /** register the custom callback */
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                System.out.println("onSuccess");
                GraphRequest request = GraphRequest.newMeRequest
                        (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                // Application code
                                Log.v("LoginActivity", response.toString());
                                //System.out.println("Check: " + response.toString());
                                try
                                {
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String gender = object.getString("gender");
                                    String birthday = object.getString("birthday");
                                    System.out.println(id + ", " + name + ", " + email + ", " + gender + ", " + birthday);
//                                    tv_facebookInfo.setText(birthday);
                                    tv_facebookInfo.setText("ID: " + id + " | " + email + "\n" + name + ", " + " | " + gender + " | " + birthday);
                                    profilePicture.setProfileId(object.getString("id"));
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                tv_facebookInfo.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                tv_facebookInfo.setText("Login attempt failed.");
            }

        });

        /** check if user already login, get and display profile info if yes */
        if(AccessToken.getCurrentAccessToken()!=null){
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            try
                            {
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String gender = object.getString("gender");
                                String birthday = object.getString("birthday");
                                System.out.println(id + ", " + name + ", " + email + ", " + gender + ", " + birthday);
//                                tv_facebookInfo.setText(birthday);
                                tv_facebookInfo.setText("ID: " + id + " | " + email + "\n" + name + ", " + " | " + gender + " | " + birthday);
                                profilePicture.setProfileId(object.getString("id"));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                tv_facebookInfo.setText("exception...");
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday, picture");
            request.setParameters(parameters);
            request.executeAsync();

        } else {
            profilePicture.setProfileId(null);
            tv_facebookInfo.setText("please login");
        }

        /** display sharing dialog */
        btn_facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                shareDialog.show(content);

            }
        });


    }


    private void sharePhotoToFacebook(){
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("Give me my code or I will ... you know, do that thing you don't like!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

        tv_AppStat.setText("sharing");
    }

    public void getFbKeyHash(String packageName) {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("YourKeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first


    }

}
