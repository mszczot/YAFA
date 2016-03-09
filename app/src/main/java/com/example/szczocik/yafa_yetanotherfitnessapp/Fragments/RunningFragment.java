package com.example.szczocik.yafa_yetanotherfitnessapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses.DatabaseHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses.PermissionUtils;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

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
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //Database handler object
    DatabaseHandler db;

    RunningSession runningSession;
    protected boolean isInSession = false;

    private OnFragmentInteractionListener mListener;

    Button startButton;
    TimerFragment timerFragment;
    Location location;

    GoogleMap mMap;

    GoogleApiClient googleApiClient;

    LocationRequest locationRequest;


    public RunningFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RunningFragment newInstance(DatabaseHandler db) {
        RunningFragment fragment = new RunningFragment();
        Bundle args = new Bundle();
        args.putSerializable("db", db);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            db = (DatabaseHandler) getArguments().getSerializable("db");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        timerFragment = new TimerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.timerFrame, timerFragment).commit();

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

        googleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        runningSession = new RunningSession(location);
    }

    public void setupMapSettings() {
        UiSettings settings = mMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
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
        Toast.makeText(context, String.valueOf(this.getId()), Toast.LENGTH_SHORT).show();
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
        this.location = location;
        Toast.makeText(getContext(), location.toString(), Toast.LENGTH_LONG).show();
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));
        addLocation(location);
        if (isInSession) {
            timerFragment.speedMeasurement.setText(String.valueOf(location.getSpeed()));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Position Tracker", "Google APi Client connected");
        Toast.makeText(getContext(), "Google Api connected", Toast.LENGTH_LONG).show();
        requestLocationUpdates();
    }

    public void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
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
            db.addSession(runningSession);
            Log.d("Sessions", String.valueOf(db.getSessionsCount()));
        } else {
            isInSession = true;
            startButton.setBackgroundColor(getResources().getColor(R.color.red));
            startButton.setText(getResources().getString(R.string.stop));
            startSession();
        }
    }

    public void startSession(){
        runningSession = new RunningSession(location);
        timerFragment.resetChronometer();
        timerFragment.startChronometer();
        if (location != null) {
            timerFragment.speedMeasurement.setText(String.valueOf(location.getSpeed()));
        }
        else {
            timerFragment.speedMeasurement.setText(getResources().getString(R.string.speed0));
        }
    }

    public void stopSession(){
        runningSession.stop();
        timerFragment.stopChronometer();
        timerFragment.speedMeasurement.setText(getResources().getString(R.string.none));
    }

    public void addLocation(Location location) {
        runningSession.addLocation(location);
    }
}
