package com.example.dermatia;
import static com.google.common.io.Files.getFileExtension;
import org.tensorflow.lite.support.image.TensorImage;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dermatia.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Camara extends AppCompatActivity {
    Button btnSeleccionarImagen, btnTomarFoto, btnConsultar;
    Bitmap bitmap;
    ImageView ivFoto;
    DatabaseReference root  = FirebaseDatabase.getInstance().getReference("Image");
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    Uri imageUri;
    TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camara);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
    btnTomarFoto = findViewById(R.id.btnTomarfoto);
    ivFoto = findViewById(R.id.ivFoto);
    btnConsultar = findViewById(R.id.btnConsultar);
    tvResultado = findViewById(R.id.tvResultado);
    ObtenerPermiso();
    /*String[] labels = new String[1];
    int cnt=0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line=bufferedReader.readLine();
            while(line!=null){
                labels[cnt]=line;
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10);

        }
    });

    btnTomarFoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 12);
        }
    });

    btnConsultar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if(imageUri != null){
            //    SubirFirebase(imageUri);
            //}else{
            //    Toast.makeText(Camara.this, "Seleccione o tome una foto", Toast.LENGTH_SHORT).show();
            //}
            try {
                Model model = Model.newInstance(Camara.this);

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                bitmap= Bitmap.createScaledBitmap(bitmap, 224, 224, true);

                inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                // Runs model inference and gets result.
                Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                tvResultado.setText(obtenerMaximo(outputFeature0.getFloatArray())+" ");

                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                tvResultado.setText("No pudimos analizar esta imagen.");
            }
        }
    });
    }

    int obtenerMaximo(float[] arr){
        int max=0;
        for(int i=0; i<arr.length; i++){
            if(arr[i] > arr[max]) max=i;
        }
        return max;
    }

    private void SubirFirebase(Uri uri) {

        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       Modelo modelo = new Modelo(uri.toString());
                       String modelId = root.push().getKey();
                       root.child(modelId).setValue(modelo);
                       Toast.makeText(Camara.this, "Subido exitosamente", Toast.LENGTH_SHORT).show();
                   }
               });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Camara.this, "Error !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void ObtenerPermiso() {
        if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Camara.this, new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==11){
            if(grantResults.length>0){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    this.ObtenerPermiso();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==10){
            if(data!=null){
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ivFoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else if(requestCode==12){
            bitmap = (Bitmap)data.getExtras().get("data");
            ivFoto.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



}