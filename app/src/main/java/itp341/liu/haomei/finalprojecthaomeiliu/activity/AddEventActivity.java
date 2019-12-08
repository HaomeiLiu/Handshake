package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ImageUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;

public class AddEventActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button buttonAdd;
    private EditText editTextName;
    private EditText editTextTime;
    private EditText editTextLocation;
    private EditText editTextKey;

    private static final String TAG = AddEventActivity.class.getPackage().getName() + "tag";

    //Upload Pic
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setTitle("Add Event");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.event_iv);
        buttonAdd = findViewById(R.id.add_event_button);
        editTextName = findViewById(R.id.event_editText_name);
        editTextTime = findViewById(R.id.add_event_editText_time);
        editTextLocation = findViewById(R.id.add_event_editText_location);
        editTextKey = findViewById(R.id.add_event_editText_key);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextName.getText().toString().equals("")){
                    ToastUtil.shortToast(AddEventActivity.this, "Event name cannot be null.");
                }
                else if(editTextTime.getText().toString().equals("")){
                    ToastUtil.shortToast(AddEventActivity.this, "Time cannot be null.");
                }
                else if(editTextLocation.getText().toString().equals("")){
                    ToastUtil.shortToast(AddEventActivity.this, "Location cannot be null.");
                }
                else{
                    Event event = new Event(editTextName.getText().toString(),
                            editTextTime.getText().toString(),
                            editTextLocation.getText().toString(),
                            editTextKey.getText().toString(),
                    0);
                    /*Map<String, Object> event = new HashMap<>();
                    event.put("Title", editTextName.getText().toString());
                    event.put("Time", editTextTime.getText().toString());
                    event.put("Location", editTextLocation.getText().toString());
                    event.put("Key", editTextKey.getText().toString());
                    event.put("Participants", 0);*/

                    db.collection("events")
                            .add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    ToastUtil.shortToast(getBaseContext(), "Unable to add the event.");
                                }
                            });

                }
            }
        });

    }

    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        String[] items = { "Choose From Gallery", "Take Picture" };
        builder.setNegativeButton("Cancel", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // Choose pic from gallery
                        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder1.build());
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(openAlbumIntent, "Select Picture"), CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // Take pic
                        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder2.build());
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "event_image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(AddEventActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Log.d("AddEvent", "CHOOSE PICTURE");
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri does not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = ImageUtil.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
    }




    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
