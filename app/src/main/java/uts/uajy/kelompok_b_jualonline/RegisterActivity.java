package uts.uajy.kelompok_b_jualonline;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uts.uajy.kelompok_b_jualonline.api.UserAPI;

import static com.android.volley.Request.Method.POST;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText txtInputNamaDepan, txtInputNamaBelakang, txtInputAlamat, txtInputTanggalLahir, txtInputNomorTelepon, txtInputEmail, txtInputPassword;
    private MaterialButton btnRegisterF;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth auth;

    private String namaDepan, namaBelakang, alamat, tanggalLahir, nomorTelepon, email, passowrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtInputNamaDepan = findViewById(R.id.txtInputNamaDepan);
        txtInputNamaBelakang = findViewById(R.id.txtInputNamaBelakang);
        txtInputAlamat = findViewById(R.id.txtInputAlamat);
        txtInputTanggalLahir = findViewById(R.id.txtInputTanggalLahir);
        txtInputNomorTelepon = findViewById(R.id.txtInputNomorTelepon);
        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        btnRegisterF = findViewById(R.id.btnRegisterF);

        btnRegisterF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(txtInputNamaDepan.getText().toString(),
                             txtInputNamaBelakang.getText().toString(),
                             txtInputAlamat.getText().toString(),
                             txtInputTanggalLahir.getText().toString(),
                             txtInputNomorTelepon.getText().toString(),
                             txtInputEmail.getText().toString(),
                             txtInputPassword.getText().toString());
            }
        });
    }

    public void registerUser(String namaDepan, String namaBelakang, String alamat, String tanggalLahir, String nomorTelepon, String email, String password){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Memulai membuat permintaan request menghapus data ke jaringan
        StringRequest stringRequest = new StringRequest(POST, UserAPI.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Disini bagian jika response jaringan berhasil tidak terdapat ganguan/error
//                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
//                    FirebaseUser user = auth.getCurrentUser();
                    if(obj.getString("message").equals("Add User Success"))
                    {
                        Intent i = new Intent(RegisterActivity.this,ActivityLogin.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Adding User Success, Please Check Your Email", Toast.LENGTH_SHORT).show();
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
                params.put("nama_depan", namaDepan);
                params.put("nama_belakang", namaBelakang);
                params.put("alamat", alamat);
                params.put("tanggal_lahir", tanggalLahir);
                params.put("nomor_telepon", nomorTelepon);
                params.put("alamat", alamat);
                params.put("email", email);
                params.put("password", password);
                params.put("imageUrl", "haha");
                params.put("verification_status", "belum");
                return params;
            }
        };

        //Disini proses penambahan request yang sudah kita buat ke reuest queue yang sudah dideklarasi
        queue.add(stringRequest);
    }
}