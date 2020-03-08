package com.shivam_gaba;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener {

    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private NavigationMapRoute navigationMapRoute;
    private DirectionsRoute currentRoute;

    ArrayList<marker> markers = new ArrayList<marker>();

    public String driverName, driverPhoneNumber, emailId, truckNumber, driverPicUrl, truckTrashLevel;

    int totalFlag = 1;
    Timer t;


    DatabaseReference mDatabaseReference;

    public LatLng startLatLng;
    public LatLng endLatLng;
    public double startLng, startLat, endLng, endLat;
    public double liveLat;
    public double liveLng;

    int binsCleared, binsLeft, totalBins;

    TextView tvTruckNumber, tvTruckTrashLevel, tvBinsCleared, tvBinsLeft;

    CardView cardNavView;
    Button btnNavigate, btnCancel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoic2hpdjg5NjhzaCIsImEiOiJjazVpZmY1eWIwY3Z3M21udnUwMHo5dzhnIn0.pkfihC9BK2VQTZwy-DMJLw");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        truckNumber = "0";
        truckTrashLevel = "0";

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        cardNavView = findViewById(R.id.cardNavView);
        btnNavigate = findViewById(R.id.btnNavigate);
        btnCancel = findViewById(R.id.btnCancel);
        cardNavView.setVisibility(View.GONE);

        tvTruckNumber = findViewById(R.id.tvTruckNumber);
        tvBinsCleared = findViewById(R.id.tvBinsCleared);
        tvBinsLeft = findViewById(R.id.tvBinsLeft);
        tvTruckTrashLevel = findViewById(R.id.tvTruckTrashLevel);

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardNavView.setVisibility(View.GONE);
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                    currentRoute = null;
                }
            }
        });

        final AlertDialog alertDialog = new SpotsDialog(MapHome.this);
        alertDialog.show();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    addMarkersFromDatabase();
                }
            }
        };
        handler.postDelayed(runnable, 3000);

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
    }

    private void updateLiveLocationsToDatabase() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    liveLat = locationComponent.getLastKnownLocation().getLatitude();
                    liveLng = locationComponent.getLastKnownLocation().getLongitude();
                    FirebaseDatabase firebaseDatabase;

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        mDatabaseReference = firebaseDatabase.getReferenceFromUrl("https://driver-app-9e22d.firebaseio.com/Managers");

                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {

                                    if (keyNode.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("liveLocation").getRef().setValue(new liveLocation(liveLat, liveLng));
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MapHome.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }, 0, 10000);
        } else {
            return;
        }
    }


    //  Ui functions====================================================================================================


    private void showMarkerInfoDialog() {
        try {
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = LayoutInflater.from(this);
            View marker_dialog = inflater.inflate(R.layout.marker_dialog, null);

            TextView tvDestination = marker_dialog.findViewById(R.id.tvDestination);

            Button btnGetRoute = marker_dialog.findViewById(R.id.btnGetRoute);
            Button btnCancel = marker_dialog.findViewById(R.id.btnCancel);


            btnGetRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    startLat = startLatLng.getLatitude();
                    endLat = endLatLng.getLatitude();
                    startLng = startLatLng.getLongitude();
                    endLng = endLatLng.getLongitude();

                    final AlertDialog alertDialog = new SpotsDialog(MapHome.this);
                    alertDialog.show();
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();

                                getRoute(Point.fromLngLat(startLng, startLat), Point.fromLngLat(endLng, endLat));
                            }
                        }
                    };
                    handler.postDelayed(runnable, 3000);

                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            handler.removeCallbacks(runnable);
                        }
                    });


                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(startLatLng)
                            .include(endLatLng)
                            .build();
                    mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 5000);
                    cardNavView.setVisibility(View.VISIBLE);
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            Geocoder geocoder = new Geocoder(MapHome.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(endLatLng.getLatitude(), endLatLng.getLongitude(), 1);
            String add;
            try {
                Address obj = addresses.get(0);
                add = obj.getAddressLine(0);
                addresses.clear();
            } catch (Exception e) {
                add = "Unable to Load Data , please restart !!";
                tvDestination.setTextColor(Color.RED);
            }
            tvDestination.append(add);
            dialog.setView(marker_dialog);
            dialog.show();

        } catch (Exception e) {
            Toast.makeText(MapHome.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDetailsFromDatabase() {

        ArrayList<driver> driverList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference driverReference = database.getReferenceFromUrl("https://driver-app-9e22d.firebaseio.com/Managers");
        driverReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    if (keyNode.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/driver")) {
                        driver d = keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/driver").getValue(driver.class);
                        driverList.add(d);
                    }
                }

                driverName = driverList.get(0).getDriverName();
                driverPhoneNumber = driverList.get(0).getDriverPhoneNumber();
                truckNumber = driverList.get(0).getTruckNumber();
                emailId = driverList.get(0).getEmailId();
                driverPicUrl = driverList.get(0).getDriverPicUrl();

                Intent profileIntent = new Intent(MapHome.this, Profile.class);
                profileIntent.putExtra("driverName", driverName);
                profileIntent.putExtra("driverPicUrl", driverPicUrl);
                profileIntent.putExtra("truckNumber", truckNumber);
                profileIntent.putExtra("emailId", emailId);
                profileIntent.putExtra("driverPhoneNumber", driverPhoneNumber);
                if (driverList.isEmpty()) {
                    Toast.makeText(MapHome.this, "No Data Available", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(profileIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "ERROR: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMarkersFromDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference markerReference = database.getReferenceFromUrl("https://driver-app-9e22d.firebaseio.com/Managers");
        DatabaseReference driverReference = database.getReferenceFromUrl("https://driver-app-9e22d.firebaseio.com/Managers");

        markerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                markers.clear();
                mapboxMap.getMarkers().clear();
                mapboxMap.clear();
                mapboxMap.removeAnnotations();


                    for (DataSnapshot parentNode : dataSnapshot.getChildren()) {

                        DataSnapshot childNode = parentNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Markers");

                        for (DataSnapshot keyNode : childNode.getChildren()) {
                            marker m = keyNode.getValue(marker.class);
                            markers.add(m);
                        }

                        binsLeft = markers.size();
                        tvBinsLeft.setText(binsLeft + "");

                        if (totalFlag == 1) {
                            totalBins = markers.size();
                            totalFlag = 0;
                        }
                        binsCleared = binsLeft > totalBins ? 0 : totalBins - binsLeft;
                        tvBinsCleared.setText(binsCleared + "");

                        ArrayList<Double> lat = new ArrayList<Double>();
                        ArrayList<Double> lng = new ArrayList<Double>();

                        for (int i = 0; i < markers.size(); i++) {
                            lat.add(markers.get(i).getLat());
                            lng.add(markers.get(i).getLng());
                        }

                        for (int i = 0; i < markers.size(); i++) {

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(lat.get(i), lng.get(i)));
                            mapboxMap.addMarker(markerOptions);
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapHome.this, "ERROR: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        ArrayList<driver> driverList = new ArrayList<>();
        driverReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    if (keyNode.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/driver")) {
                        driver d = keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/driver").getValue(driver.class);
                        driverList.add(d);
                    }
                }

                if (!driverList.isEmpty()) {
                    truckNumber = driverList.get(0).getTruckNumber();
                    truckTrashLevel = driverList.get(0).getTruckTrashLevel();
                }
                tvTruckTrashLevel.setText(truckTrashLevel + "%");
                tvTruckNumber.setText(truckNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "ERROR: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void putMarkersToIntent() {
        ArrayList<marker> markersListForIntent = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference markerReference = database.getReferenceFromUrl("https://driver-app-9e22d.firebaseio.com/Managers");
        markerReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                markersListForIntent.clear();
                for (DataSnapshot parentNode : dataSnapshot.getChildren()) {
                    DataSnapshot childNode = parentNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Markers");
                    for (DataSnapshot keyNode : childNode.getChildren()) {
                        marker m = keyNode.getValue(marker.class);
                        markersListForIntent.add(m);
                    }
                }
                    Intent intent = new Intent(MapHome.this, BinsActivity.class);
                    intent.putExtra("markers", (Serializable) markersListForIntent);
                    startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapHome.this, "ERROR: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

//Maps functions ================================================================================================================

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            enableLocationComponent(style);
            updateLiveLocationsToDatabase();

            try {
                mapboxMap.setOnMarkerClickListener(marker -> {
                    updateLiveLocationsToDatabase();
                    startLatLng = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                    endLatLng = marker.getPosition();
                    showMarkerInfoDialog();
                    return true;
                });
            } catch (Exception e) {
                Toast.makeText(MapHome.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigate() {
        if (currentRoute != null) {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    .build();
// Call this method with Context from within an Activity
            NavigationLauncher.startNavigation(MapHome.this, options);
        } else {
            Toast.makeText(this, "Loading Map. Please Click again !", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void getRoute(Point startLatLng, Point endLatLng) {
        if (currentRoute != null) {
            navigationMapRoute.removeRoute();
        }
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(startLatLng)
                .destination(endLatLng)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null || response.body().routes().size() == 00) {
                            Toast.makeText(MapHome.this, "No Routes found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(MapHome.this, "Failed :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {

        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        t.cancel();
        FirebaseDatabase.getInstance().getReference().child("Managers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("liveLocation").getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapHome.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        t.cancel();
        FirebaseDatabase.getInstance().getReference().child("Managers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("liveLocation").getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapHome.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


//navigation drawer menu======================================================================================================


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.logOut:
                t.cancel();
                FirebaseDatabase.getInstance().getReference().child("Managers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                            keyNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("liveLocation").getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MapHome.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(MapHome.this, MainActivity.class));
                MapHome.this.finish();
                break;

            case R.id.bins:
                putMarkersToIntent();
                break;

            case R.id.driverProfile:
                getDetailsFromDatabase();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}