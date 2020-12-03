package uts.uajy.kelompok_b_jualonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ActivityLogin extends AppCompatActivity {
    TextInputEditText email,password;
    Button  signup,signin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String CHANNEL_ID = "Channel 1";
    private Boolean checkTheme;

    SharedPreferences sharedPEmail;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("filename", Context.MODE_PRIVATE);
        checkTheme = sharedPreferences.getBoolean("NightMode",false);
        if(checkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            this.setTheme(R.style.darktheme);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            this.setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        mFirebaseAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.pass);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);



        if(mFirebaseAuth.getCurrentUser() != null) {

           save(mFirebaseAuth.getCurrentUser());

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mFirebaseUser != null){
                    Toast.makeText(ActivityLogin.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                    save(mFirebaseUser);
                    Intent i = new Intent (ActivityLogin.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(ActivityLogin.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(email.getText().toString().equalsIgnoreCase("")){
//                    Toast.makeText(getApplicationContext(),"Email Tidak boleh kosong",Toast.LENGTH_SHORT).show();
//                }else if(password.getText().toString().equalsIgnoreCase("")){
//                    Toast.makeText(getApplicationContext(),"Password tidak boleh kosong",Toast.LENGTH_SHORT).show();
//                }else if(!isValidEmailId(email.getText().toString().trim())){
//                    Toast.makeText(getApplicationContext(), "Email Tidak Valid", Toast.LENGTH_SHORT).show();
//                }else if(password.getText().toString().length()<6){
//                    Toast.makeText(getApplicationContext(), "Password Harus 6 Karakter", Toast.LENGTH_SHORT).show();
//                }else{
////                    Toast.makeText(getApplicationContext(), "Sukses", Toast.LENGTH_SHORT).show();
//                    String input1 = email.getText().toString();
//                    String input2 = password.getText().toString();
//                    mFirebaseAuth.createUserWithEmailAndPassword(input1,input2).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
//                            if(!task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "SignUp Unccessfull, Please Try Again", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(getApplicationContext(), "SignUp Sucessfull", Toast.LENGTH_SHORT).show();
//                                email.setText("");
//                                password.setText("");
//                            }
//                        }
//                    });
//                }

                Intent i = new Intent(ActivityLogin.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Email Tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Password tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else if(!isValidEmailId(email.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(), "Email Tidak Valid", Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(), "Password Harus 6 Karakter", Toast.LENGTH_SHORT).show();
                }else{
                    String input1 = email.getText().toString();
                    String input2 = password.getText().toString();
                    mFirebaseAuth.signInWithEmailAndPassword(input1,input2).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "SignIn Unccessfull, Please Try Again", Toast.LENGTH_SHORT).show();
                            }else{
                                createNotificationChannel();
                                addNotification();
                                Intent i = new Intent(ActivityLogin.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }


    public void save(FirebaseUser mFirebaseAuth){
        String helo = mFirebaseAuth.getEmail();
        sharedPEmail = getSharedPreferences("userEmail",Context.MODE_PRIVATE);
        editor = sharedPEmail.edit();
        editor.putString("email",helo);
        editor.apply();
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Channel 1";
            String description = "This is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Hello")
                .setContentText("Welcome Back")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}
