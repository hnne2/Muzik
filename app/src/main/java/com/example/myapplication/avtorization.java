package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
public class avtorization extends AppCompatActivity {
    private EditText login,pasword;private Button signIn,signUp, signOut;private FirebaseAuth mAuth;private StorageReference mStroge;private ProgressDialog mprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtorization);
        find();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(login.getText().toString()) && !TextUtils.isEmpty(pasword.getText().toString()))
                {
                    mAuth.createUserWithEmailAndPassword(login.getText().toString().toLowerCase(Locale.ROOT), pasword.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                StorageReference filepathTxt = mStroge.child(login.getText().toString().toLowerCase(Locale.ROOT)).child("1.txt");
                                File file = new File(getCacheDir().getPath()+"1.txt");
                                try {
                                    FileWriter fw = new FileWriter(file);
                                    fw.write("");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                filepathTxt.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mprogress.dismiss();
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "You create new Account", Toast.LENGTH_SHORT).show();
                            } else{
                                Log.i("FILE1","user ne zaregan");
                                Toast.makeText(getApplicationContext(), "Failed(wrong mail format or password(min 7 chars)",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "введите эмайл и пасворд", Toast.LENGTH_LONG).show();
                }
            }
        }); //создание аккаунта, кнопка рег
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(login.getText().toString()) && !TextUtils.isEmpty(pasword.getText().toString()))
                {
                    mAuth.signInWithEmailAndPassword(login.getText().toString(), pasword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "вы авторизованны", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), mytraks.class);
                                intent.putExtra("mailFromAvtorizaton", mAuth.getCurrentUser().getEmail());
                                startActivity(intent);
                            }
                            else Toast.makeText(getApplicationContext(), "вы не авторизованны", Toast.LENGTH_LONG).show();
                        } // создает файл 1тхт где будут хр названия файлов
                    });
                }

            }
        }); //вход в аккаунт;
    }
    private void find(){
        signIn=findViewById(R.id.signIn);
        signUp=findViewById(R.id.signUp);
        login=findViewById(R.id.LoginText);
        pasword=findViewById(R.id.PaswordText);
        mAuth = FirebaseAuth.getInstance();
        mprogress = new ProgressDialog(this);
        mStroge= FirebaseStorage.getInstance().getReference();
    }
     @Override
   protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if(cUser!=null){      //если юзер существует скрывает объекты авторизации, и перекидывает на основуню активити
            pasword.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);
            Toast.makeText(this, "Обновление данных", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, mytraks.class);
            intent.putExtra("mailFromAvtorizaton", mAuth.getCurrentUser().getEmail());
            startActivity(intent);
        }
        else {;    //в противном случае показывает объекты авторизации
            pasword.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.VISIBLE);
            Log.i("FILE1","полльзователь не зареган");
        }
    } //проверка на уже имеющую авторизацию

}