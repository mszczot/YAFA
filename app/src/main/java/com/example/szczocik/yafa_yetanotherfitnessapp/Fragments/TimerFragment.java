package com.example.szczocik.yafa_yetanotherfitnessapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.R;

import java.io.Serializable;

/**
 * Create by: Marcin Szczot (40180425)
 * Fragment for displaying running session details inside the running fragment
 */
public class TimerFragment extends Fragment implements Serializable {

    public Chronometer chronometer;
    public TextView pace;
    public TextView avgSpeedTV;

    private OnFragmentInteractionListener mListener;

    //region boilerplate code
    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        chronometer = (Chronometer) view.findViewById(R.id.chronometer);
        pace = (TextView) view.findViewById(R.id.pace);
        avgSpeedTV = (TextView) view.findViewById(R.id.avgSpeed);
        return view;
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
    //endregion

    /**
     * Method to start chronometer
     */
    public void startChronometer() {
        chronometer.start();
    }

    /**
     * Method to stop chronometer
     */
    public void stopChronometer() {
        chronometer.stop();
    }

    /**
     * Method to reset chronometer
     */
    public void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    /**
     * Method to update the displayed pace
     * @param speed - pace as string
     */
    public void updatePace(String speed){
        pace.setText(speed);
    }
}
