package com.example.szczocik.yafa_yetanotherfitnessapp.Activities;

import android.app.ActionBar;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

public class HistoryDetails extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LAYOUT_HEIGHT = 0;

    GoogleMap mMap;
    GoogleApiClient googleApiClient;

    private RunningSession rs;

    TextView duration;
    TextView avgSpeed;
    TextView avgPace;
    TextView maxSpeed;
    TextView elevationGain;
    TextView elevationLoss;
    TextView startTime;
    TextView date;
    TextView distance;

    SupportMapFragment mapFragment;

    LinearLayout mapContainer;
    LinearLayout rsDetails;
    LinearLayout additionalInfo;

    private boolean mapView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rs = extras.getParcelable("rs");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetails);
        mapFragment.getMapAsync(this);

        setupView();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        PolylineOptions po = new PolylineOptions();
        if (rs.getLocList().size() > 0) {
            for (Location l : rs.getLocList()) {
                po.add(new LatLng(l.getLatitude(), l.getLongitude()));
            }

            LatLng start = new LatLng(rs.getLocList().get(0).getLatitude(), rs.getLocList().get(0).getLongitude());
            mMap.addPolyline(po);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                swapViews();
            }
        });
    }

    private void setupView(){
        duration = (TextView) findViewById(R.id.duration);
        avgSpeed = (TextView) findViewById(R.id.avgSpeedDetail);
        avgPace = (TextView) findViewById(R.id.avgPaceDetail);
        maxSpeed = (TextView) findViewById(R.id.maxSpeed);
        elevationGain = (TextView) findViewById(R.id.elevationGain);
        elevationLoss = (TextView) findViewById(R.id.elevationLoss);
        startTime = (TextView) findViewById(R.id.startTime);
        date = (TextView) findViewById(R.id.date);
        distance = (TextView) findViewById(R.id.distanceDetails);

        mapContainer = (LinearLayout) findViewById(R.id.mapContainer);
        rsDetails = (LinearLayout) findViewById(R.id.rsDetails);
        additionalInfo = (LinearLayout) findViewById(R.id.additionalInfo);

        updateUI();
    }

    private void swapViews(){
        Log.d("View", String.valueOf(mapView));
        if (!mapView) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapContainer.getLayoutParams();
            params.weight = 0.9f;
            mapContainer.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) rsDetails.getLayoutParams();
            params.weight = 0.15f;
            rsDetails.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) additionalInfo.getLayoutParams();
            params.weight = 0;
            additionalInfo.setLayoutParams(params);
            mapView = true;
            setupMap();
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapContainer.getLayoutParams();
            params.weight = 0.2f;
            mapContainer.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) rsDetails.getLayoutParams();
            params.weight = 0.15f;
            rsDetails.setLayoutParams(params);

            params = (LinearLayout.LayoutParams) additionalInfo.getLayoutParams();
            params.weight = 0.7f;
            additionalInfo.setLayoutParams(params);
            mapView = false;
            setupMap();
        }
    }

    private void updateUI() {
        duration.setText(rs.getTotalTime());
        avgSpeed.setText(String.format("%.2f", rs.getAvgSpeed()));
        avgPace.setText(String.valueOf(rs.getPaceAsString()));
        startTime.setText(rs.getStartTime());
        date.setText(rs.getDate());
        elevationGain.setText(String.valueOf(rs.getElevationGain()));
        elevationLoss.setText(String.valueOf(rs.getElevationLoss()));
        maxSpeed.setText(String.valueOf(rs.getMaxSpeed()));
        distance.setText(String.format("%.3f", rs.getDistanceInMiles()));
    }

    private void setupMap() {
        UiSettings settings = mMap.getUiSettings();
        if (!mapView) {
            settings.setAllGesturesEnabled(false);
            settings.setCompassEnabled(false);
            settings.setMyLocationButtonEnabled(false);
            settings.setRotateGesturesEnabled(false);
            settings.setScrollGesturesEnabled(false);
            settings.setTiltGesturesEnabled(false);
            settings.setZoomControlsEnabled(false);
            settings.setZoomGesturesEnabled(false);
        } else {
            settings.setAllGesturesEnabled(false);
            settings.setCompassEnabled(false);
            settings.setMyLocationButtonEnabled(false);
            settings.setRotateGesturesEnabled(true);
            settings.setScrollGesturesEnabled(true);
            settings.setTiltGesturesEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setZoomGesturesEnabled(true);
        }
    }

}
