package com.dandan.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArearange;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;

import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;

import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;


import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {


    ArrayList<Forecast> forecasts;


    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(ArrayList<Forecast> forecasts) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("forecasts",forecasts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forecasts = getArguments().getParcelableArrayList("forecasts");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Object[][] seriesData = new Object[forecasts.size()][3];
        for (int i = 0; i < forecasts.size(); i++) {
            seriesData[i][0] = i;

            seriesData[i][1] = forecasts.get(i).temperatureMin;
            seriesData[i][2] = forecasts.get(i).temperatureMax;
        }

            HIChartView chartView = getView().findViewById(R.id.temperatureChart);

            HIOptions options = new HIOptions();

            HIChart chart = new HIChart();
            chart.setType("arearange");
            chart.setZoomType("x");
            options.setChart(chart);

            HITitle title = new HITitle();
            title.setText("Temperature variation by day");
            options.setTitle(title);




            HIXAxis xaxis = new HIXAxis();
            HILabels xlabel = new HILabels();

            xlabel.setStep(2);
            xaxis.setLabels(xlabel);
            xaxis.setType("category");
            options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

            HIYAxis yaxis = new HIYAxis();
            yaxis.setTitle(new HITitle());
            options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

            HITooltip tooltip = new HITooltip();
            tooltip.setShadow(true);
            tooltip.setValueSuffix("°F");
            options.setTooltip(tooltip);

            HILegend legend = new HILegend();
            legend.setEnabled(false);
            options.setLegend(legend);

            HIArearange series = new HIArearange();
            HIGradient hiGradient = new HIGradient(0,1,1,0);
            LinkedList<HIStop> hiStops = new LinkedList<>();
            hiStops.add(new HIStop(0, HIColor.initWithHexValue("dbc3ae")));
            hiStops.add(new HIStop(0.1f, HIColor.initWithHexValue("d2b593")));
            hiStops.add(new HIStop(0.5f,HIColor.initWithHexValue("b2b2b2")));
            hiStops.add(new HIStop(0.8f,HIColor.initWithHexValue("9eb4c7")));
            hiStops.add(new HIStop(1,HIColor.initWithHexValue("799bbb")));
            HIColor gradientColor = HIColor.initWithRadialGradient(hiGradient,hiStops);
            series.setFillColor(gradientColor);

            series.setTrackByArea(true);
            series.setName("Temperatures");

            series.setData(new ArrayList<>(Arrays.asList(seriesData)));
            options.setSeries(new ArrayList<>(Arrays.asList(series)));

            chartView.setOptions(options);

    }
}