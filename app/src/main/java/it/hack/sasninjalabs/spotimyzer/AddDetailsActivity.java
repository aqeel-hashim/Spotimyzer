package it.hack.sasninjalabs.spotimyzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.hack.sasninjalabs.spotimyzer.model.User;

public class AddDetailsActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    @BindView(R.id.nameTxt)
    public EditText name;

    @BindView(R.id.userImage)
    public ImageView image;

    private Uri picturePath;
    private boolean isRenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        ButterKnife.bind(this);

        isRenter = getIntent().getBooleanExtra("IS_RENTER", false);
    }

    @OnClick(R.id.userImage)
    public void browseImage(){
        Toast.makeText(this, "Browse Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            try {
                picturePath = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(picturePath);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    
    @OnClick(R.id.saveBtn)
    public void saveDetails(){
        if(name.getText().toString().isEmpty() || picturePath == null){
            Toast.makeText(this, "Please input a valid name and select a valid image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/profile_picture/"+picturePath.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(picturePath);

        SharedPreferences sharedPref = getSharedPreferences(
                "SpotymizerAppStorage", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.apply();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailsActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(!isRenter) {
                    User owner = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), name.getText().toString(), getIntent().getStringExtra("PHONE_NUMBER"), taskSnapshot.getDownloadUrl().toString());
                    editor.putBoolean("IS_RENTER", false);
                    editor.apply();
                    FirebaseDatabase.getInstance().getReference("owner").child(owner.getUUID()).setValue(owner);
                    Intent i = new Intent(AddDetailsActivity.this, MainActivity.class);
                    i.putExtra("CURRENT_USER", owner);
                    startActivity(i);
                }else{
                    User owner = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), name.getText().toString(), getIntent().getStringExtra("PHONE_NUMBER"), taskSnapshot.getDownloadUrl().toString());
                    editor.putBoolean("IS_RENTER", true);
                    editor.apply();
                    FirebaseDatabase.getInstance().getReference("user").child(owner.getUUID()).setValue(owner);
                    Intent i = new Intent(AddDetailsActivity.this, MainRenteeActivity.class);
                    i.putExtra("CURRENT_USER", owner);
                    startActivity(i);
                }
            }
        });
    }
}
