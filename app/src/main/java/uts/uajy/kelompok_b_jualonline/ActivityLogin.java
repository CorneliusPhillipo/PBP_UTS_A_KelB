package uts.uajy.kelompok_b_jualonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import uts.uajy.kelompok_b_jualonline.userModel.User;

import uts.uajy.kelompok_b_jualonline.api.UserAPI;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class ActivityLogin extends AppCompatActivity {
    TextInputEditText email,password;
    Button  signup,signin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String CHANNEL_ID = "Channel 1";
    private Boolean checkTheme;
    private List<User> users;
    private View view;

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

        users = new ArrayList<User>();
        getUser();
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
                    String sEmail = email.getText().toString();
                    String sPassword = password.getText().toString();
                    int cekValidateLogin = 0;
                    int getIndex = 0;

                    for(int i =0;i<users.size();i++){
                        if(email.getText().toString().equalsIgnoreCase(users.get(i).getEmail())){
                            if(password.getText().toString().equalsIgnoreCase(users.get(i).getPassword())){
                                cekValidateLogin = 1;
                                getIndex = i;
                            }else{
                                cekValidateLogin = 2;
                            }
                        }
                    }
                    if(cekValidateLogin==0){
                        Toast.makeText(ActivityLogin.this, "Username Tidak ada", Toast.LENGTH_SHORT).show();
                    }else if(cekValidateLogin==2){
                        Toast.makeText(ActivityLogin.this,"Password Salah",Toast.LENGTH_SHORT).show();
                    }else if(cekValidateLogin==1){
                        if(users.get(getIndex).getVerification_status()==NULL){
                            Toast.makeText(ActivityLogin.this,"Anda belum Verifikasi Email",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ActivityLogin.this,"Login Berhasil",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
//                    mFirebaseAuth.signInWithEmailAndPassword(input1,input2).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
//                            if(!task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "SignIn Unccessfull, Please Try Again", Toast.LENGTH_SHORT).show();
//                            }else{
//                                createNotificationChannel();
//                                addNotification();
//                                Intent i = new Intent(ActivityLogin.this,MainActivity.class);
//                                startActivity(i);
//                                finish();
//                            }
//                        }
//                    });
                    loginUser(sEmail, sPassword);
                }
            }
        });
    }


    public void getUser(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, UserAPI.URL_GET
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //Mengambil data response json object yang berupa data mahasiswa

                    if(!users.isEmpty())
                        users.clear();
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        //Mengubah data jsonArray tertentu menjadi json Object
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        String id                   = jsonObject.optString("id");
                        String nama_depan           = jsonObject.optString("nama_depan");
                        String nama_belakang        = jsonObject.optString("nama_belakang");
                        String alamat               = jsonObject.optString("alamat");
                        String tanggal_lahir        = jsonObject.optString("tanggal_lahir");
                        String nomor_telepon        = jsonObject.optString("nomor_telepon");
                        String email                = jsonObject.optString("email");
                        String password             = jsonObject.optString("password");
                        String imageUrl             = jsonObject.optString("imageUrl");
                        String verification_status  = jsonObject.optString("email_verified_at");

                        //Membuat objek user
                        User user = new User(id, nama_depan, nama_belakang,alamat,tanggal_lahir,nomor_telepon,email,
                                password,imageUrl,verification_status);

                        //Menambahkan objek user tadi ke list user
                        users.add(user);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), response.optString("message"),
                        Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Disini bagian jika response jaringan terdapat ganguan/error
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Disini proses penambahan request yang sudah kita buat ke reuest queue yang sudah dideklarasi
        queue.add(stringRequest);
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
                .setContentTitle("Login Success")
                .setContentText("Welcome Back")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }


    public void loginUser(String email, String password){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Memulai membuat permintaan request menghapus data ke jaringan

        StringRequest stringRequest = new StringRequest(POST, UserAPI.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Disini bagian jika response jaringan berhasil tidak terdapat ganguan/error
//                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("message").equals("Login Success"))
                    {
                        createNotificationChannel();
                        addNotification();
                        Toast.makeText(ActivityLogin.this, "User ID : "+obj.getString("user"), Toast.LENGTH_SHORT).show();
                        saveUserId(obj.getString("user"));
                        Intent i = new Intent(ActivityLogin.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Disini bagian jika response jaringan terdapat ganguan/error
                Toast.makeText(getApplicationContext(), "Masuk error response", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                /*
                    Disini adalah proses memasukan/mengirimkan parameter key dengan data value,
                    dan nama key nya harus sesuai dengan parameter key yang diminta oleh jaringan
                    API.
                */
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        //Disini proses penambahan request yang sudah kita buat ke reuest queue yang sudah dideklarasi
        queue.add(stringRequest);
    }

    public void saveUserId(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("id_user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id_user",id);
        editor.commit();
    }
}