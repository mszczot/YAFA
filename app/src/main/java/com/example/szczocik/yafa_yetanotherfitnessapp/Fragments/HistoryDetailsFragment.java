package com.example.szczocik.yafa_yetanotherfitnessapp.Fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryDetailsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    GoogleApiClient googleApiClient;

    private RunningSession rs;

    private OnFragmentInteractionListener mListener;

    public HistoryDetailsFragment() {
        // Required empty public constructor
    }


    public static HistoryDetailsFragment newInstance(RunningSession rs) {
        HistoryDetailsFragment fragment = new HistoryDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("rs", rs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rs = (RunningSession) getArguments().getSerializable("rs");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle SavedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        PolylineOptions po = new PolylineOptions();
        for (Location l:rs.getLocList()) {
            po.add(new LatLng(l.getLatitude(),l.getLongitude()));
        }
        mMap.addPolyline(po);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}