package edu.northeastern.smartspendmax;

import static android.app.Activity.RESULT_OK;
import static android.nfc.NdefRecord.createUri;
import static androidx.databinding.DataBindingUtil.setContentView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.northeastern.smartspendmax.databinding.ActivityMainBinding;

public class InvoiceFragment extends Fragment {

    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int GALLERY_PERMISSION_CODE = 2;
    private Uri imageUri;
    private ImageView imageView;
    private String imageUrl = "https://templates.invoicehome.com/invoice-template-us-neat-750px.png";
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Bitmap invoiceImage;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_invoice, container, false);
            imageView = view.findViewById(R.id.iv_display);
            Button btnConfirm = view.findViewById(R.id.btn_confirm);
            imageUri = createUri();

            btnConfirm.setOnClickListener(v -> {
                //onConfirmButtonPressed();
                // Replace the current fragment with AddNewTransactionFragment
                if(getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new AddNewTransactionAIFragment())
                            .addToBackStack(null)
                            .commit();
                }

            });

            view.findViewById(R.id.selectFromGallery).setOnClickListener(v -> loadImageFromGallery());
            view.findViewById(R.id.selectFromCamera).setOnClickListener(v -> loadImageFromCamera());
            view.findViewById(R.id.selectFromLink).setOnClickListener(v -> loadImageFromLink( imageUrl));

            return view;
        }

        private Uri createUri(){
            File imageFile = new File(getContext().getFilesDir(), "camera_photo.jpg");
            return FileProvider.getUriForFile(
                    getContext(),
                    "smartspendmax.provider",
                    imageFile
            );
        }

        private void loadImageFromGallery() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_PERMISSION_CODE);
        }



    private void setupTakePictureLauncher() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                imageView.setImageURI(null);
                imageView.setImageURI(imageUri); // Refresh the ImageView with new image
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    invoiceImage = bitmap; // Save bitmap for other uses
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageFromCamera() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = createUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // Set the file Uri to save the photo
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                takePictureLauncher.launch(imageUri);
            } else {
                Toast.makeText(getContext(), "No application can handle this request.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImageFromCamera();
        } else {
            Toast.makeText(getContext(), "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageFromLink(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        invoiceImage = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PERMISSION_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
                invoiceImage = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            invoiceImage = imageBitmap;
        }
    }


}
