package com.emulator.whatsthatdog;



import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emulator.whatsthatdog.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class homePage extends AppCompatActivity implements View.OnClickListener {


    private ImageView UploadPic;
    private ImageView CameraPic;
    private ImageView Settings;
    private ImageView Gallery;
    private String Thepass;
    public Uri imageUri;
    private StorageReference fireReference;
    private DatabaseReference DataReference;
    int imageSize = 64;


    private final ActivityResultLauncher<Intent> launchPad = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                CheckResults(result);
            });
    private final ActivityResultLauncher<Intent> launchPad2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {

                CheckResults2(result);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().hide();
        UploadPic = (ImageView) findViewById(R.id.UploadPic);
        CameraPic = findViewById(R.id.CameraPic);
        Settings = findViewById(R.id.SettingsPic);
        Gallery = findViewById(R.id.GalleryPic);

        Bundle grab = getIntent().getExtras();
        if(grab != null)
        {
            Thepass = grab.getString("key");
        }


        fireReference = FirebaseStorage.getInstance().getReference();
        DataReference = FirebaseDatabase.getInstance().getReference().child(Thepass);

        UploadPic.setOnClickListener(this);
        CameraPic.setOnClickListener(this);
        Settings.setOnClickListener(this);
        Gallery.setOnClickListener(this);

    }


    public void CheckResults(ActivityResult result){
        if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){

            imageUri = result.getData().getData();


            final String TheKey = UUID.randomUUID().toString();


            StorageReference mountainsRef = fireReference.child("images/" + TheKey);


            mountainsRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DataReference.push().setValue(uri.toString());
                        }
                    });
                }
            });

            imageuri2(imageUri);

        }
    }
    public void CheckResults2(ActivityResult result)  {
        if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){


            Bundle extras = result.getData().getExtras();
            Bitmap image = (Bitmap)extras.get("data");

            ByteArrayOutputStream OutStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, OutStream);

            byte[] bytes = OutStream.toByteArray();

            final String TheKey = UUID.randomUUID().toString();


            StorageReference mountainsRef = fireReference.child("images/" + TheKey);

            mountainsRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DataReference.push().setValue(uri.toString());
                        }
                    });
                }
            });
            imageCamera2(image);



        }
    }
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());


            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] Values = new int[imageSize * imageSize];
            image.getPixels(Values,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());

            int pixel = 0;
            for(int i = 0; i < imageSize;i++){
                for(int j = 0;j < imageSize;j++){
                    int val = Values[pixel++];

                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);


            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();

            int Pos = 0;
            float maxConfidence = 0;

            for(int i =  0;i<confidence.length;i++){
                if(confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    Pos = i;
                }
            }
            String[] animals = {"Butterfly","Cat","Chicken","Cow","Dog","Elephant","Horse","Sheep","Spider","Squirrel"};
            Toast.makeText(getApplicationContext(),animals[Pos], Toast.LENGTH_LONG).show();

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }
    public void imageCamera2(Bitmap image){
        int dimension = Math.min(image.getWidth(),image.getHeight());
        image = ThumbnailUtils.extractThumbnail(image, dimension,dimension);

        image = Bitmap.createScaledBitmap(image, imageSize, imageSize,false);
        classifyImage(image);

    }
    public void imageuri2(Uri uri){
        Bitmap image = null;

        try{
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        }catch (IOException e){
            e.printStackTrace();
        }
        image = Bitmap.createScaledBitmap(image, imageSize, imageSize,false);
        classifyImage(image);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.UploadPic:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launchPad.launch(intent);
                break;
            case R.id.CameraPic:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                launchPad2.launch(intent2);
                break;
            case R.id.SettingsPic:
                Intent intent3 = new Intent (homePage.this, ProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.GalleryPic:
                Intent intent4 = new Intent(homePage.this,ShowGallery.class);
                intent4.putExtra("key",Thepass);
                startActivity(intent4);
                break;
        }
    }


}