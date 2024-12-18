package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class activity_account extends AppCompatActivity {

    TextView EditUsername, EditPassword, EditPhoneNumber, TextView3;
    FirebaseAuth mAuth;
    Button BtnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        if (username == null || username.isEmpty()) {
            username = "DefaultUser";
        }
        if (password == null || password.isEmpty()) {
            password = "DefaultPass";
        }

        EditUsername = findViewById(R.id.editUsername);
        EditPassword = findViewById(R.id.editPassword);
        EditPhoneNumber = findViewById(R.id.editPhoneNumber);
        TextView3 = findViewById(R.id.textView3);

        BtnSignOut = findViewById(R.id.btnSignOut);
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference;

        if (username != null && !username.isEmpty()) {
            reference = database.getReference("User").child(username);
        } else {
            Toast.makeText(this, "Username is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phoneNumber = snapshot.child("phonenumber").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);

                    TextView3.setText(username);
                    EditPhoneNumber.setText(phoneNumber);
                    EditUsername.setText(username);
                    EditPassword.setText(password);
                } else {
                    Toast.makeText(activity_account.this, "User not found!", Toast.LENGTH_SHORT).show();
                    EditPhoneNumber.setText("Phone number not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity_account.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        BtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(activity_account.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
