package com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;

import java.util.List;

/**
 * Created by szczocik on 09/03/16.
 */
public class SessionsList extends ArrayAdapter<RunningSession> {

    private final Activity context;
    private final List<RunningSession> rsList;


    public SessionsList(Activity context, int resource, List<RunningSession> rsList) {
        super(context, R.layout.sessions_list, rsList);
        this.context = context;
        this.rsList = rsList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sessions_list, null, true);

        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);
        TextView totalTime = (TextView) rowView.findViewById(R.id.totalTime);



        return rowView;
    }
}
