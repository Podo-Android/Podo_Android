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
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.meong.podoandroid.data.StoreItem;
import com.meong.podoandroid.helper.MapDBHelper;
import com.meong.podoandroid.ui.feed.FeedRecommendActivity;
import com.meong.podoandroid.ui.home.MainActivity;
import com.meong.podoandroid.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "MapActivityTAG";

    private GoogleMap mMap;
    private LatLng currentPosition;

    private Circle mCircle;

    private boolean circleflag = false;

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
                Log.d(TAG, "onLocationResult");

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mMap.clear();

                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                        setOnCurLocationClickListener(currentPosition);

                        Log.d(TAG, "onLocationResult : " + location.getLatitude() + "," + location.getLongitude());

                        focusToCurPosition(currentPosition);

                        selectStoreData(currentPosition);
                    }
                }
            }
        };

        getMap();
        requestLocation();

        setDrawer();
        onDrawerItemClickListener();

        setOnBtnClickListener();

        MapDBHelper.openDatabase(getApplicationContext(), "StoreLocation");
        MapDBHelper.createTable("store");
        insertStoreData();
    }

    private void insertStoreData() {
        ArrayList<StoreItem> items = new ArrayList<>();

        //한성대학교
        items.add(new StoreItem("베스트동물병원", (float) 37.5909, (float) 127.0100, "서울특별시 성북구 동소문동3가 32"));
        items.add(new StoreItem("서울종합동물병원", (float) 37.5888, (float) 127.0181, "서울특별시 성북구 보문동1가 9-1"));
        items.add(new StoreItem("대학로동물병원", (float) 37.5857, (float) 127.0003, "서울특별시 종로구 명륜2가 14-1"));

        //홍대
        items.add(new StoreItem("마리동물병원", (float) 37.558603, (float)126.928186, "서울특별시 마포구 동교동"));
        items.add(new StoreItem("파티마동물병원", (float)37.558418, (float)126.929008, "서울특별시 마포구 동교동 179-1"));
        items.add(new StoreItem("워너비동물병원", (float) 37.558525, (float)126.922513, "서울특별시 마포구 서교동 동교로 193"));
        items.add(new StoreItem("아프리카동물종합병원", (float)37.556991, (float)126.919976, "서울특별시 마포구 서교동 445-3"));

        MapDBHelper.insertStoreData(items);
    }

    private void selectStoreData(LatLng currentPosition) {

        ArrayList<StoreItem> items = MapDBHelper.storeSelect("store");

        floatMarker(items, currentPosition);
    }

    private void floatMarker(ArrayList<StoreItem> items, LatLng currentPosition) {
        double distance;

        Location locationNow = new Location("A");

        locationNow.setLatitude(currentPosition.latitude);
        locationNow.setLongitude(currentPosition.longitude);

        for (int i = 0; i < items.size(); i++) {
            Location locationNew = new Location("B");
            locationNew.setLatitude(items.get(i).getLatitude());
            locationNew.setLongitude(items.get(i).getLongtitude());

            distance = locationNow.distanceTo(locationNew) / 1000; //in km
            if (distance < 1.3) { //1 km 내의 마커만 띄웁니다.

                LatLng marker = new LatLng(items.get(i).getLatitude(), items.get(i).getLongtitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(marker)
                        .title(items.get(i).getName())
                        .snippet(items.get(i).getAddress());

                mMap.addMarker(markerOptions);
            }
        }
    }

    private void setOnCurLocationClickListener(final LatLng currentPosition) {

        imgCurrentLocation = (ImageView) findViewById(R.id.img_map_act_location);
        imgCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                focusToCurPosition(currentPosition);
            }
        });
    }

    private void floatSearchResultMarker(StoreItem storeItem) {
        LatLng currentPosition = new LatLng(storeItem.getLatitude(), storeItem.getLongtitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition)
                .title(storeItem.getName())
                .snippet(storeItem.getAddress())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_now));

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

        txtCurrentLocation.setText(storeItem.getAddress());

        drawCircle(mMap, currentPosition);
    }

    private void focusToCurPosition(LatLng currentPosition) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition)
                .title("현재 위치")
                .snippet(getGeocode(currentPosition) + "")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_now));

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

        txtCurrentLocation.setText(getGeocode(currentPosition));

        drawCircle(mMap, currentPosition);
    }

    private void drawCircle(GoogleMap googleMap, LatLng latlng) {
        if (circleflag)
            mCircle.remove();

        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(latlng)
                .radius(1300.0)
                .strokeColor(getResources().getColor(R.color.point_pink))
                .fillColor(Color.parseColor("#4de1b2a3"))
                .strokeWidth(1);

        mCircle = googleMap.addCircle(circleOptions);
        circleflag = true;

        selectStoreData(currentPosition);
    }


    private void getMap() {
        Log.d(TAG, "getMap");

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
                            Log.d(TAG, "" + location.getLatitude() + "," + location.getLongitude());
                        } else {
                            Log.e(TAG, "location get fail");
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
        locationRequest.setInterval(600 * 1000);

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
        drawer = (DrawerLayout) findViewById(R.id.drawer_map_act);
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

        // 병원 위치 눌렸을 때
        TextView txtMap = (TextView) findViewById(R.id.txt_nav_main_hospital);
        txtMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);

                finish();
            }
        });

        // 사료 추천 눌렸을 때
        TextView txtRecommend = (TextView) findViewById(R.id.txt_nav_main_recommend);
        txtRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedRecommendActivity.class);
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
        Log.d(TAG, "onMapReady");
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL)
                .title("아직 위치가 지정되지 않았습니다.");

        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1004) {
                String name = intent.getStringExtra("name");
                String address = intent.getStringExtra("address");
                Float lat = intent.getFloatExtra("lat", 37);
                Float lon = intent.getFloatExtra("lon", 126);

                StoreItem storeItem = new StoreItem(name, lat, lon, address);

                mMap.clear();
                selectStoreData(new LatLng(storeItem.getLatitude(),storeItem.getLongtitude()));
                floatSearchResultMarker(storeItem);
            }
        }
    }
}