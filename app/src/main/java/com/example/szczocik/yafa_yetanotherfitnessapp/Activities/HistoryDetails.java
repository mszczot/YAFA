package com.example.szczocik.yafa_yetanotherfitnessapp.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Marcin Szczot (40180425)
 * Activity to display details of running session
 */
public class HistoryDetails extends AppCompatActivity implements OnMapReadyCallback {

    //region variables
    GoogleMap mMap;

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

    ShareButton shareButton;

    SupportMapFragment mapFragment;

    LinearLayout mapContainer;
    LinearLayout rsDetails;
    LinearLayout additionalInfo;

    private boolean mapView = false;

    private CallbackManager callbackManager;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        Bundle extras = getIntent().getExtras();
        //get runningSession from the intent
        if (extras != null) {
            rs = extras.getParcelable("rs");
        }

        //setup map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetails);
        mapFragment.getMapAsync(this);

        setupView();
    }


    /**
     * Callback when map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //setup map and add lines to the map
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

    /**
     * Method to setup the view
     */
    private void setupView(){
        //create the content for sharing on facebook
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "fitness.course")
                .putString("og:title", "Running session")
                .putString("og:description", "My running session.")
                .putLong("fitness:duration:value", rs.getDuration())
                .putString("fitness:duration:units", "s")
                .putInt("fitness:distance:value", (int) rs.getDistance())
                .putString("fitness:distance:units", "mi")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.runs")
                .putObject("fitness:course", object)
                .build();
         ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness:course")
                .setAction(action)
                .build();
        shareButton = (ShareButton) findViewById(R.id.share_button);
        shareButton.setShareContent(content);

        callbackManager = CallbackManager.Factory.create();
        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("Facebook", "SHARING SUCCESS!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook", "SHARING ERROR! - " + error.getMessage());
            }

            @Override
            public void onCancel() {
                Log.w("Facebook", "SHARING CANCEL!");
            }
        });

        //display details of the session
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

    /**
     * Method to change the view - between details and map
     */
    private void swapViews(){
        //if the user is not in map view remove the details view and show the map
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

    /**
     * Method to update the ui
     */
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

    /**
     * Method to setup the settings for the map
     */
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
