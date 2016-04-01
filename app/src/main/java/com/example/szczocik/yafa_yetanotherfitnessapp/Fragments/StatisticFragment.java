package com.example.szczocik.yafa_yetanotherfitnessapp.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.DatabaseHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.LocationHandler;
import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;
import com.example.szczocik.yafa_yetanotherfitnessapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Marcin Szczot (40180425)
 * Statistic fragment to display the statistics of previous sessions
 * Statistics are grouped by months
 *
 * For graphs I have used the external library:
 * hellocharts-android
 * Created by Leszek Wach
 * link: https://github.com/lecho/hellocharts-android
 */
public class StatisticFragment extends Fragment {

    private static String[] months = {"January","February","March","April","May","June",
            "July","August","September","October","November","December"};

    //region variables for charts
    //Line and column chart
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean isCubic = false;

    //PieChart
    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelsPieChart = true;
    //endregion

    private DatabaseHandler db;
    private LocationHandler locationHandler;

    private OnFragmentInteractionListener mListener;

    private TextView month;
    private TextView totalSessions;

    private Button left;
    private Button right;

    private int monthDisplayed;

    private Calendar cl;

    private ColumnChartView distances;
    private LineChartView avgPace;

    //data sources for charts
    private float[] dist;
    private float[] avgpace;
    private float elevationGain;
    private float elevationLoss;

    public StatisticFragment() {
        // Required empty public constructor
    }

    public static StatisticFragment newInstance() {
        StatisticFragment fragment = new StatisticFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //create new DatabaseHandler and get instance of locationHandler
        db = new DatabaseHandler(getActivity());
        locationHandler = LocationHandler.getInstance(db);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setView(view);
    }

    /**
     * Method for setting up the view
     * @param view
     */
    private void setView(View view) {
        //textviews
        month = (TextView) view.findViewById(R.id.statisticsMonth);
        totalSessions = (TextView) view.findViewById(R.id.totalSessions);

        //charts
        distances = (ColumnChartView) view.findViewById(R.id.distancesChart);
        avgPace = (LineChartView) view.findViewById(R.id.avgPaceChart);
        chart = (PieChartView) view.findViewById(R.id.elevationPieChart);

        //buttons
        right = (Button) view.findViewById(R.id.right);
        left = (Button) view.findViewById(R.id.left);

        cl = Calendar.getInstance();
        monthDisplayed = cl.get(Calendar.MONTH);
        Log.d("Date", cl.getTime().toString());

        month.setText(months[monthDisplayed]);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
            }
        });

        getGraphs();
    }

    /**
     * Method to update the view - change the name of the month
     */
    public void updateView() {
        month.setText(months[monthDisplayed]);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Method for onClickListener to navigate right
     */
    public void nextMonth() {
        //if the month isn't current month or December then it updates the view and creates the graphs
        cl = Calendar.getInstance();
        if (monthDisplayed != 11 && monthDisplayed != cl.get(Calendar.MONTH)) {
            monthDisplayed++;
            updateView();
            getGraphs();
        } else {
            Toast.makeText(getActivity(), "No more months for now", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method for onClickListener to navigate left
     */
    public void previousMonth() {
        //change the month and display graphs
        if (monthDisplayed != 0) {
            monthDisplayed--;
            updateView();
            getGraphs();
        }
    }

    /**
     * Method to set the graphs
     */
    private void getGraphs() {
        //change the calendar for 1st of the month displayed
        cl.set(Calendar.DAY_OF_MONTH, 1);
        cl.set(Calendar.MONTH, monthDisplayed);

        //get the list of Running session for this month
        ArrayList<RunningSession> rsForMonth = locationHandler.getSessionForMonth(cl);

        //get Data to be displayed from the list
        getData(rsForMonth);

        //set the graphs
        setDistanceGraphs();
        setAvgPaceGraph();
        setElevationGraph();

        //display total number of sessions
        totalSessions.setText(String.valueOf(rsForMonth.size()));
    }

    /**
     * Method to get data for the month displayed
     * @param rsList
     */
    private void getData(ArrayList<RunningSession> rsList) {
        //set calendar for the max day of the month
        cl.set(Calendar.DAY_OF_MONTH, cl.getActualMaximum(Calendar.DAY_OF_MONTH));

        //create new arrays for storing data
        avgpace = new float[cl.getActualMaximum(Calendar.DAY_OF_MONTH)];
        dist =  new float[cl.getActualMaximum(Calendar.DAY_OF_MONTH)];

        elevationLoss = 0;
        elevationGain = 0;

        //iterate thru every day of the month and collect the data
        for (int j = 1; j<=cl.getActualMaximum(Calendar.DAY_OF_MONTH); ++j) {
            cl.set(Calendar.DAY_OF_MONTH, j);

            int count = 0;
            for (RunningSession rs : rsList) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getLongStartTime());

                if (calendar.get(Calendar.DAY_OF_MONTH) == cl.get(Calendar.DAY_OF_MONTH)) {
                    avgpace[j-1] += rs.getPaceValue();
                    count++;

                    dist[j-1] += rs.getDistance();

                    elevationGain += rs.getElevationGain();
                    elevationLoss += rs.getElevationLoss();
                }
            }
            if (count != 0) {
                avgpace[j-1] = (avgpace[j-1] / count);
            }
            if (Float.isInfinite(avgpace[j-1])) {
                avgpace[j-1] = 0;
            }
        }
    }

    /**
     * Method to display Elevation Graph - Pie chart
     */
    public void setElevationGraph() {
        List<SliceValue> values = new ArrayList<>();
        SliceValue loss = new SliceValue(elevationLoss, ChartUtils.COLOR_GREEN);
        SliceValue gain = new SliceValue(elevationGain, ChartUtils.COLOR_RED);
        values.add(loss);
        values.add(gain);

        data = new PieChartData(values);
        data.setHasLabels(hasLabelsPieChart);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            data.setCenterText1("Hello!");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,42));
        }

        if (hasCenterText2) {
            data.setCenterText2("Charts (Roboto Italic)");

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,16));
        }

        chart.setPieChartData(data);
    }

    /**
     * Method to display Average Pace graph - Line graph
     */
    public void setAvgPaceGraph() {
        cl.set(Calendar.DAY_OF_MONTH, cl.getActualMaximum(Calendar.DAY_OF_MONTH));
        int numberOfPoints = cl.get(Calendar.DAY_OF_MONTH);

        List<Line> lines = new ArrayList<Line>();

        List<PointValue> values = new ArrayList<PointValue>();
        for (int j = 0; j < numberOfPoints; ++j) {

            cl.set(Calendar.DAY_OF_MONTH, j);

            values.add(new PointValue(j, avgpace[j]));
        }

        Line line = new Line(values);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        lines.add(line);
        line.setColor(ChartUtils.COLORS[2]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);

        LineChartData data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        avgPace.setLineChartData(data);
    }

    /**
     * Method to display Distance Graph - Histogram
     */
    public void setDistanceGraphs() {
        cl.set(Calendar.MONTH, monthDisplayed);
        cl.set(Calendar.DAY_OF_MONTH, 1);
        int numSubcolumns = 1;
        int numColumns = cl.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        for (int i = 0; i < numColumns; ++i) {
            cl.set(Calendar.DAY_OF_MONTH, i);

            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue(dist[i], ChartUtils.pickColor()));
            }

            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Days");
                axisY.setName("Distance (miles)");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        distances.setColumnChartData(data);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
