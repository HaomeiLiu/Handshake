package itp341.liu.haomei.finalprojecthaomeiliu.activity.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.model.MyLocation;
import itp341.liu.haomei.finalprojecthaomeiliu.util.DialogCreator;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;

import static itp341.liu.haomei.finalprojecthaomeiliu.activity.im.UserChatActivity.REQUEST_CODE_MAP;
import static itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter.EXTRA_USER_NAME;

//This part of code references https://www.androidauthority.com/
// create-a-gps-tracking-application-with-firebase-realtime-databse-844343/

public class MapActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 108;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "MapActivity";
    private Context mContext;
    private String targetID;
    private boolean isSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;


        Intent intent = getIntent();
        targetID = intent.getStringExtra(EXTRA_USER_NAME);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
        if(isSuccess){
            setResult(RESULT_OK);
        }
        finish();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        }
        else {
            //Denied
           ToastUtil.shortToast(this, "Please enable location services to allow GPS tracking");
        }
    }


    private void startTrackerService() {
        //startService(new Intent(this, TrackingService.class));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Firebase to upload
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            MyLocation loc = new MyLocation(location.getLongitude(), location.getLatitude());

                            db.collection("location").document(targetID)
                                    .set(loc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            ToastUtil.shortToast(mContext, "Your location has been sent.");
                                            isSuccess = true;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                        }
                    }
                });

        ToastUtil.shortToast(this, "GPS tracking enabled");

    }
}
