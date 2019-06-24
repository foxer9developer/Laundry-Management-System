package com.example.laundry1app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity {
    private EditText english;
    private Spinner category;
    private DatabaseReference reference;
    private ImageButton product;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://laundryapp-f9f1c.appspot.com/");
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        product = findViewById(R.id.img);
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
        english = findViewById(R.id.english);
//        quantity = findViewById(R.id.quantity);
//        price = findViewById(R.id.price);
//        unit = findViewById(R.id.unit);
        category = findViewById(R.id.category);
        Button add = findViewById(R.id.add);
//        ArrayList<String> units = new ArrayList<>();
//        units.add("Select");
//        units.add("kg");
//        units.add("gm");
//        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getBaseContext()), android.R.layout.simple_spinner_item, units);
//        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        unit.setAdapter(unitsAdapter);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Select");
        categories.add("Wash and Press");
        categories.add("Dry Clean");
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getBaseContext()), android.R.layout.simple_spinner_item, categories);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoriesAdapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(english.getText().toString(), "")) {
                    english.requestFocus();
                    english.setError("This Is A Required Field");
                } //else if (Objects.equals(quantity.getText().toString(), "")) {
//                    quantity.requestFocus();
//                    quantity.setError("This Is A Required Field");
//                } else if (Objects.equals(unit.getSelectedItem().toString(), "Select")) {
//                    unit.performClick();
//                    Toast.makeText(getBaseContext(), "Please Select Unit", Toast.LENGTH_SHORT).show();
//                } else if (Objects.equals(price.getText().toString(), "")) {
//                    price.requestFocus();
//                    price.setError("This Is A Required Field");}
                else if (Objects.equals(category.getSelectedItem().toString(), "Select")) {
                    category.performClick();
                    Toast.makeText(getBaseContext(), "Please Select Category", Toast.LENGTH_SHORT).show();
                } else {

                    if (Objects.equals(category.getSelectedItem().toString(), "Wash and Press")) {
                        if(filePath != null) {
                            pd.show();
                            StorageReference childRef = storageRef.child("products").child(english.getText().toString());
                            UploadTask uploadTask = childRef.putFile(filePath);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageRef.child("products/" + english.getText().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            pd.dismiss();
                                            reference = FirebaseDatabase.getInstance().getReference("products").push();
                                            reference.child("english").setValue(english.getText().toString());
//                                            reference.child("hindi").setValue(hindi.getText().toString());
//                                            reference.child("quantity").setValue(quantity.getText().toString() + " " + unit.getSelectedItem().toString());
//                                            reference.child("price").setValue(Long.parseLong(price.getText().toString()));
                                            reference.child("url").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getBaseContext(), "Product Added Successfully", Toast.LENGTH_SHORT).show();
                                                        if (getFragmentManager() != null) {
                                                            startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
                                                            finish();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getBaseContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getBaseContext(), "Select an image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (Objects.equals(category.getSelectedItem().toString(), "dryclean")) {
                        if(filePath != null) {
                            pd.show();
                            StorageReference childRef = storageRef.child("dryclean").child(english.getText().toString());
                            UploadTask uploadTask = childRef.putFile(filePath);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageRef.child("dryclean/" + english.getText().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            pd.dismiss();
                                            reference = FirebaseDatabase.getInstance().getReference("dryclean").push();
                                            reference.child("english").setValue(english.getText().toString());
//                                            reference.child("hindi").setValue(hindi.getText().toString());
//                                            reference.child("quantity").setValue(quantity.getText().toString() + " " + unit.getSelectedItem().toString());
//                                            reference.child("price").setValue(Long.parseLong(price.getText().toString()));
                                            reference.child("url").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getBaseContext(), "Product Added Successfully", Toast.LENGTH_SHORT).show();
                                                        if (getFragmentManager() != null) {
                                                            startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
                                                            finish();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getBaseContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getBaseContext(), "Select an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                product.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), AdminDashboardActivity.class));
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
}