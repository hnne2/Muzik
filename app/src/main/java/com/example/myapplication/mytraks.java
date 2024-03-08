package com.example.myapplication;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class mytraks extends AppCompatActivity {
    private ImageButton addTrack,fire,next,play,previos,updait,addFriend;private ProgressBar progressBar;private TextView mrecordbtn;private ListView listView;private StorageReference mStroge;private ProgressDialog mprogress;private FirebaseAuth mAuth=FirebaseAuth.getInstance();private String mailFromAvtorizaton;private String tt="";private ArrayList<String> NAMEStRACK = new ArrayList<>();private String nauPlay ="";final Context context = this;private TextView final_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytraks);
        find(); //сопостовление обектов
        Download1txtFromBase();  // сканирование плейлиста с базы
        updait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Отправить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        final_text.setVisibility(View.VISIBLE);
                                        final_text.setText(userInput.getText());
                                        getrack(false);
                                    }
                                }).setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();

                //и отображаем его:
                alertDialog.show();

            }
        });
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });  // выход
        addTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getrack(true);
            }
        });
    }
    private void find(){
        updait=findViewById(R.id.updait_batton);
        final_text=findViewById(R.id.final_text);
        next=findViewById(R.id.imageButton_next);
        play=findViewById(R.id.Play_Batton);
        previos=findViewById(R.id.Previos_button);
        addFriend=findViewById(R.id.addFriend);
        Intent intent = getIntent();
        mailFromAvtorizaton = intent.getStringExtra("mailFromAvtorizaton");
        progressBar = findViewById(R.id.progressBar);
        addTrack = findViewById(R.id.button_play);
        fire = findViewById(R.id.button_fire);
        mrecordbtn=findViewById(R.id.mRecordbtn);
        mStroge = FirebaseStorage.getInstance().getReference();
        mprogress = new ProgressDialog(this);
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data !=null && data.getData() !=null){     //загрузка себе
            if(resultCode==RESULT_OK){
                loadMuzikInBase(true,data);
                addNameTrackIn1Txt(true,data);
            }
        }
        if(requestCode==2 && data !=null && data.getData() !=null){    //отправка
            if(resultCode==RESULT_OK){
                loadMuzikInBase(false,data);
                addNameTrackIn1Txt(false,data);
            }
        }
    } //активити ризульт из getTrack()
    private void getrack(Boolean sebe1){
        Intent intentchooser = new Intent();
        intentchooser.setType("audio/*");
        intentchooser.setAction(Intent.ACTION_GET_CONTENT);
        if (sebe1){
        startActivityForResult(intentchooser, 1);}
        else startActivityForResult(intentchooser, 2);
    } //интент для выбора файла, возвращает юри
    private void addNameTrackIn1Txt(Boolean sebe,Intent data) {
            DeleteFile1txtFromStroge();
            File getNameFile = new File(data.getData().getPath());
            String name =getNameFile.getName();
            File file = new File(getCacheDir().getPath()+"1.txt");

                StorageReference filepathTxt=mStroge.child(mailFromAvtorizaton).child("1.txt");
                if(sebe==false){
                    filepathTxt = mStroge.child(final_text.getText().toString()).child("1.txt");
                }
                filepathTxt.getBytes(100000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        String q = new String(bytes);
                        try {
                            FileWriter pw = new FileWriter(file,true);
                            pw.write(q);
                            if(bytes.length!=0)
                            {
                            pw.write('\n');
                            }
                            pw.write(name);
                            pw.close();
                            if(sebe){
                            Load1txtInBase(true);}
                            if(sebe==false){
                                Load1txtInBase(false);
                            }

                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });

              }//создает файл в кеше, скачивает содержимое 1.тхт из базы в файл, вписывает имя трека, загружает обратно
    private void Load1txtInBase(Boolean sebe){
            StorageReference filepathTxt = mStroge.child(mailFromAvtorizaton).child("1.txt");
            if(sebe==false){
                filepathTxt =mStroge.child(final_text.getText().toString()).child("1.txt");
            }
            File file = new File(getCacheDir().getPath()+"1.txt");
            filepathTxt.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mprogress.dismiss();
                    mrecordbtn.setText("upload finished");
                    Toast.makeText(getApplicationContext(), "загрузил 1тхи",Toast.LENGTH_LONG);
                }
            });
        }//загружает список треков
    private void DeleteFile1txtFromStroge(){
            File file = new File(getCacheDir().getPath()+"1.txt");
            file.delete();
        } // удаляет файл при создании нового
    private void loadMuzikInBase(Boolean sebe,Intent data){
            File file = new File(data.getData().getPath());    // имя файла;
            String nameUploadTrack=file.getName();
            mprogress.setMessage("upload audio");
            if(sebe){
            StorageReference filepath = mStroge.child(mailFromAvtorizaton).child(nameUploadTrack);
                filepath.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mprogress.dismiss();
                        mrecordbtn.setText("upload finished");
                        finish();
                    }
                });}
            else {
                StorageReference filepath = mStroge.child(final_text.getText().toString()).child(nameUploadTrack);
                filepath.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mprogress.dismiss();
                        mrecordbtn.setText("upload finished");
                        finish();
                    }
                });
            }

        }  //загрузка по юри в базу себе или не себе
    private void getdownloadUriAndPlay(String nameUploadTrack){
            StorageReference filepath = mStroge.child(mailFromAvtorizaton).child(nameUploadTrack);
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    playWithDownloadUri(downloadUrl);
                }
            });
        }  // по имени получаю ссылку доступа к файлу и запускаю  playWithDownloadUri
    private void playWithDownloadUri(Uri downloadUrl){
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(downloadUrl.toString());

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mp.stop();
                               getdownloadUriAndPlay(NAMEStRACK.get((int)( Math.random()*NAMEStRACK.size())));   // случайно выбирает проигрываемый трек
                            }
                        });
                        play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               if(mp.isPlaying())
                               {
                                   mp.stop();
                               }
                               else mp.start();
                            }
                        });
                        previos.setOnClickListener(new View.OnClickListener() {  // случайно выбирает проигрываемый трек
                            @Override
                            public void onClick(View v) {
                                mp.stop();
                                getdownloadUriAndPlay(NAMEStRACK.get((int)( Math.random()*NAMEStRACK.size())));
                            }
                        });
                        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {    // слушатель нажатий на адаптер
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                if(mp.isPlaying()){
                                    mp.stop();
                                }
                                TraksList selectedState = (TraksList) parent.getItemAtPosition(position);
                                getdownloadUriAndPlay(selectedState.getName());           //запускает трек по нажатию
                            }
                        };
                        listView.setOnItemClickListener(itemListener);
                    }
                });
                mediaPlayer.prepare();
            }catch (IOException E){
                E.printStackTrace();
            }
        } //воспроизведение по юри внутри которого слушатели нажатий
    private void Download1txtFromBase(){
        setProgressBarvalue(0);
            StorageReference filepathTxt = mStroge.child(mailFromAvtorizaton).child("1.txt");
            filepathTxt.getBytes(100000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    String q = new String(bytes);
                    tt=tt+q;
                    Scanner scanner =new Scanner(q);
                    List<TraksList> Traks = new ArrayList<TraksList>();
                    listView = findViewById(R.id.listviesms);
                    StateAdapter stateAdapter =new StateAdapter(getApplicationContext(), R.layout.list_item, Traks);  //создание адаптера для выведения списка треков
                    listView.setAdapter(stateAdapter);
                    while (scanner.hasNextLine()){
                        String t = scanner.nextLine();
                        Traks.add(new TraksList(t));
                        NAMEStRACK.add(t);
                    }
                    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) { // получаем выбранный пункт
                            TraksList selectedState = (TraksList) parent.getItemAtPosition(position);
                            getdownloadUriAndPlay(selectedState.getName());
                        }
                    };
                    listView.setOnItemClickListener(itemListener);
                }
            });
        }    //скчивания названий треков на аккаунте юзера и добавления в список
    private void setProgressBarvalue(int position){ //прогресс бар
        progressBar.setProgress(position);
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                Thread.sleep(9000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            setProgressBarvalue(position+5);
            }
        });
        thread2.start();
        }
}
