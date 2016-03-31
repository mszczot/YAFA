package com.example.szczocik.yafa_yetanotherfitnessapp.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.DatabaseHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.LocationHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses.PermissionUtils;
import com.example.szczocik.yafa_yetanotherfitnessapp.MainActivity;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_UPDATE_INTERVAL = 2 * 1000; //10 seconds
    private static final int LOCATION_UPDATE_FASTEST_INTERVAL = 1 * 1000; //3 seconds
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ACCURACY = 25;

    LocationHandler locationHandler;
    private DatabaseHandler db;

    protected boolean isInSession = false;

    private OnFragmentInteractionListener mListener;

    Button startButton;
    TimerFragment timerFragment;

    FrameLayout mapContainer;
    GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    PolylineOptions polyLine;

    public RunningFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RunningFragment newInstance(LocationHandler lh) {
        RunningFragment fragment = new RunningFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        locationHandler = LocationHandler.getInstance(db);

        timerFragment = new TimerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.timerFrame, timerFragment).commit();

        mapContainer = (FrameLayout) view.findViewById(R.id.map_container);
        //initializeMap();

        startButton = (Button) view.findViewById(R.id.startSession);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButton();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }

    public void initializeMap() {
        if (mMap == null) {
//            FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
//            SupportMapFragment mFRaFragment = new SupportMapFragment();
//
//
//            mTransaction.add(R.id.map_container, mFRaFragment);
//            mTransaction.commit();
//            mFRaFragment.getMapAsync(this);
//            try {
//                MapsInitializer.initialize(getActivity());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    public void setupMapSettings() {
        UiSettings settings = mMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setScrollGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        setupMapSettings();
        polyLine = new PolylineOptions();
    }

    public void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Accuracy", String.valueOf(location.getAccuracy()));
        if (location != null) {
            updateMap(location);
        }
        if (location.getAccuracy() <= ACCURACY) {
            locationHandler.addLocationToSession(location);
            if (locationHandler.isInSession()) {
                updateUI();
                polyLine.add(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.addPolyline(polyLine);
            }
        }
    }

    private void updateMap(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));
        }
    }

    private void updateUI() {
        timerFragment.updatePace(locationHandler.getPace());
        timerFragment.avgSpeedTV.setText(String.format("%.2f", locationHandler.getCurrentAvgSpeed()));
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    public void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void changeButton() {
        if (isInSession) {
            isInSession = false;
            startButton.setBackgroundColor(getResources().getColor(R.color.green));
            startButton.setText(getResources().getString(R.string.start));
            stopSession();

            RunningSession rs = locationHandler.getCurrentSession();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Would you like to save this session?")
                    .setMessage("Duration: " + rs.getTotalTime() +
                            "\nDistance: " + rs.getDistance() )
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            locationHandler.saveCurrentSession((MainActivity) getActivity());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            locationHandler.disardCurrentSession();
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            isInSession = true;
            startButton.setBackgroundColor(getResources().getColor(R.color.red));
            startButton.setText(getResources().getString(R.string.stop));
            startSession();
        }
    }

    public void startSession() {
        locationHandler.startSession();
        timerFragment.resetChronometer();
        timerFragment.startChronometer();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
            locationHandler.addLocationToSession(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
        }
    }

    public void stopSession(){
        locationHandler.stopSession();
        timerFragment.stopChronometer();
        timerFragment.pace.setText(getResources().getString(R.string.none));
    }

}
