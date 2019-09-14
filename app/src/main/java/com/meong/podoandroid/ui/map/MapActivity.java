package com.meong.podoandroid.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.meong.podoandroid.ui.menu.MainActivity;
import com.meong.podoandroid.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "MapActivity";

    private GoogleMap mMap;
    private LatLng currentPosition;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    private Geocoder geocoder;

    private int PERMISSIONS_REQUEST_CODE = 100;

    private String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationCallback locationCallback;

    private TextView txtCurrentLocation;

    private DrawerLayout drawer;

    private ImageView imgCurrentLocation;
    private ImageView imgSearchStore;
    private ImageView imgHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG,"onLocationResult");

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                        setOnCurLocationClickListener(currentPosition);

                        Log.d(TAG, "onLocationResult : " + location.getLatitude() + "," + location.getLongitude());
                        txtCurrentLocation.setText(getGeocode(currentPosition));
                    }
                }
            }
        };

        getMap();
        requestLocation();

        setDrawer();
        onDrawerItemClickListener();

        setOnBtnClickListener();
    }

    private void setOnCurLocationClickListener(final LatLng currentPosition) {

        imgCurrentLocation = (ImageView) findViewById(R.id.img_map_act_location);
        imgCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusToCurPosition(currentPosition);
            }
        });
    }

    private void focusToCurPosition(LatLng currentPosition) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition);

        markerOptions.title("현재 위치");
        markerOptions.snippet(getGeocode(currentPosition)+"");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(100));
    }


    private void getMap() {
        Log.d(TAG,"getMap");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }


    private void getPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE
                );
            } else {

                //퍼미션 거부한 적 없을 때 퍼미션 요청
                ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE
                );
            }
        }
    }

    private void requestLocation() {

        txtCurrentLocation = (TextView) findViewById(R.id.txt_map_act_current_location);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.e(TAG, "location get fail");
                        } else {
                            Log.d(TAG, "" + location.getLatitude() + "," + location.getLongitude());
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "location error" + e.getMessage());
                e.printStackTrace();
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60 * 1000);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private String getGeocode(LatLng currentPosition) {
        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    currentPosition.latitude,
                    currentPosition.longitude,
                    1
            );
        } catch (IOException e) {
            return "지오코더 서비스 사용 불가";
        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    private void setDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_map_search_act);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void onDrawerItemClickListener() {
        // 홈이 눌렸을 때
        TextView txtHome = (TextView) findViewById(R.id.txt_nav_main_home);
        txtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

    }

    private void setOnBtnClickListener() {

        imgSearchStore = (ImageView) findViewById(R.id.img_map_act_search);
        imgSearchStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapSearchActivity.class);
                startActivityForResult(intent, 1004);
            }
        });

        imgHamburger = (ImageView) findViewById(R.id.map_hamburger);
        imgHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);

        markerOptions.title("아직 위치가 지정되지 않았습니다.");
//        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK) {
            if (requestCode == 1004) {
                //마커 띄우기
            }
        }
    }
}
