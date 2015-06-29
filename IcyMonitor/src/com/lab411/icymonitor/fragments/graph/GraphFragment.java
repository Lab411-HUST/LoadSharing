/*
 * Copyright 2013 Thanasis Georgiou
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lab411.icymonitor.fragments.graph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.lab411.icymonitor.activities.EditActivity;
import com.lab411.icymonitor.activities.MainViewActivity;
import com.lab411.icymonitor.R;
import com.lab411.icymonitor.RandomColor;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Random;

/**
 * Displays a graph and a list bellow it.
 * Created by Thanasis Georgiou on 20/06/13.
 */
public class GraphFragment extends ListFragment {
    public static final String EXTRAS_COLOR = "com.lab411.icymonitor.colorextra";

    // Root view
    private View mRootView;

    // Chart
    private GraphicalView mChart;
    private XYSeries[] mSeries;
    private XYSeriesRenderer[] mSeriesRenderer;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private int mGraphX = 0;
    private String[] mNames;
    private float[] mLastData;
    private int[] mColors;
    private float mLineWidth = 1;

    // Stuff
    private String mDataType = "";
    private TextView[] mViewValues;
    private SharedPreferences mSettings;

    public GraphFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_graph, container, false);
        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            mRenderer = new XYMultipleSeriesRenderer();
            mDataset = new XYMultipleSeriesDataset();

            mRenderer.setXTitle("");
            mRenderer.setYTitle("");
            mRenderer.setXAxisMin(-5);
            mRenderer.setXAxisMax(80);
            mRenderer.setMargins(new int[]{0, 25, 0, 0});
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
            mRenderer.setBackgroundColor(Color.TRANSPARENT);
            mRenderer.setShowGridX(mSettings.getBoolean(getString(R.string.key_grid_x), false));
            mRenderer.setShowGridY(mSettings.getBoolean(getString(R.string.key_grid_y), false));
            mRenderer.setYLabels(5);
            mRenderer.setYLabelsAlign(Paint.Align.CENTER);
            mRenderer.setYLabelsPadding(15.0f);
            mRenderer.setYLabelsColor(0, getResources().getColor(android.R.color.secondary_text_light));
            mRenderer.setGridColor(getResources().getColor(android.R.color.darker_gray));
            mRenderer.setShowLegend(false);
            mRenderer.setZoomEnabled(false, false);
            mRenderer.setPanEnabled(false, false);

            mLineWidth = Float.valueOf(mSettings.getString(getString(R.string.key_line_width), "2"));
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLineWidth = Float.valueOf(mSettings.getString(getString(R.string.key_line_width), "2"));
        refreshColors(mNames);
        mRenderer.setShowGridX(mSettings.getBoolean(getString(R.string.key_grid_x), true));
        mRenderer.setShowGridY(mSettings.getBoolean(getString(R.string.key_grid_y), true));
    }

    /**
     * Clears all graphs
     */
    public void clearGraphs() {
        // We have two seperate for loops here because the items must be added in the same order
        // the renderers are added so the colors do not get messed up.

        if (mSeries != null) { // Only if we actually have data to erase
            // Remove from dataset
            for (int i = 0; i < mSeries.length; i++) {
                mDataset.removeSeries(mSeries[i]);
                mSeries[i] = new XYSeries("");
            }

            // Add back
            for (XYSeries series : mSeries) {
                mDataset.addSeries(series);
            }
        }

        mGraphX = 0;
    }

    private void refreshColors(String[] names) {
        if (mColors != null && names != null) {
            refreshColors(names, false);
            setListAdapter(new GraphListArrayAdapter(getActivity(), names));
        }
    }

    private void refreshColors(String[] names, boolean save) {
        int size = names.length;
        mColors = new int[size];

        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            mColors[i] = mSettings.getInt(names[i] + "_" + mDataType, RandomColor.getColor(rnd, getResources()));
            mSeriesRenderer[i].setColor(mColors[i]);
            mSeriesRenderer[i].setLineWidth(mLineWidth);
        }

        // Save colors in case they are random
        if (save) {
            for (int i = 0; i < size; i++) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(names[i] + "_" + mDataType, mColors[i]);
                editor.commit();
            }
        }
    }

    public void init(String[] names, String dataType, int maxY) {
        int size = names.length;
        if (size != 0) {
            mNames = names;
            mDataType = dataType;
            mViewValues = new TextView[names.length];

            // Setup lines
            mSeries = new XYSeries[size];
            mSeriesRenderer = new XYSeriesRenderer[size];

            for (int i = 0; i < size; i++) {
                mSeries[i] = new XYSeries("");
                mSeriesRenderer[i] = new XYSeriesRenderer();
                mSeriesRenderer[i].setLineWidth(mLineWidth);

                mDataset.addSeries(mSeries[i]);
                mRenderer.addSeriesRenderer(mSeriesRenderer[i]);
            }

            // Load colors
            refreshColors(names, true);

            // Setup list
            setListAdapter(new GraphListArrayAdapter(getActivity(), names));

            // Setup onclick listener
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    String name = (String) v.findViewById(R.id.text_name).getTag(R.string.key_name);
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtra(EXTRAS_COLOR, name + "_" + mDataType);
                    startActivity(intent);
                }
            });

            // Setup Y axis
            mRenderer.setYAxisMin(0);
            mRenderer.setYAxisMax(maxY);

            // Create graphs
            mChart = ChartFactory.getLineChartView(getActivity().getBaseContext(), mDataset, mRenderer);
            mChart.setBackgroundColor(Color.TRANSPARENT);

            // Add graphs to view
            ((FrameLayout) mRootView.findViewById(R.id.graph_container)).addView(mChart);
        } else {
            names = new String[1];
            mViewValues = new TextView[1];
            names[0] = "Unsupported";
            mDataType = "null";
            mSeriesRenderer = new XYSeriesRenderer[1];
            mSeriesRenderer[0] = new XYSeriesRenderer();
            refreshColors(names, true);
            setListAdapter(new GraphListArrayAdapter(getActivity(), names));
        }
    }

    /**
     * Refresh the list.
     */
    public void addData(float[] data) {
        mLastData = data;
        if (data.length != 0) {
            for (int i = 0; i < data.length; i++) {
                if (mSeries != null) mSeries[i].add(mGraphX, data[i]);
                if (mViewValues != null) {
                    if (mViewValues[i] != null) {
                        mViewValues[i].setText(data[i] + mDataType);
                    }
                }
            }

            mRenderer.setXAxisMin(mGraphX - 80);
            mRenderer.setXAxisMax(mGraphX);

            mChart.repaint();

            mGraphX++;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mLastData != null) {
            clearGraphs();
            Toast.makeText(getActivity(), "Low on memory. Cleared graphs.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        if (mSeries != null) {
            outState.putBoolean("notNull", true);
            for(int i = 0; i < mSeries.length; i++) {
                outState.putSerializable("current_series_" + i, mSeries[i]);
                outState.putSerializable("current_renderer_" + i, mSeriesRenderer[i]);
            }
            outState.putInt("seriesLength", mSeries.length);
            outState.putInt("x", mGraphX);
        } else {
            outState.putBoolean("notNull", false);
        }
    }

    public void onRestoreInstanceState(Bundle savedState) {
        // restore the current data, for instance when changing the screen orientation
        mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");

        if (savedState.getBoolean("notNull")) {
            int length = savedState.getInt("seriesLength");

            mSeries = new XYSeries[length];
            mSeriesRenderer = new XYSeriesRenderer[length];
            for(int i = 0; i < length; i++) {
                mSeries[i] = (XYSeries) savedState.getSerializable("current_series_" + i);
                mSeriesRenderer[i] = (XYSeriesRenderer) savedState.getSerializable("current_renderer_" + i);
            }
            mGraphX = savedState.getInt("x");
        }
    }

    private class GraphListArrayAdapter extends ArrayAdapter<String> {

        private final String[] mData;
        private final Context mContext;

        public GraphListArrayAdapter(Context context, String[] data) {
            super(context, R.layout.list_item_graph, data);
            mData = data;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_graph, parent, false);

            TextView label = (TextView) rowView.findViewById(R.id.text_name);
            label.setText(mSettings.getString("name_" + mData[position] + "_" + mDataType, mData[position]));
            label.setTag(R.string.key_name, mData[position]);
            if (mLastData != null && mLastData.length > 0) {
                ((TextView) rowView.findViewById(R.id.text_value)).setText(mLastData[position] + mDataType);
            }
            mViewValues[position] = (TextView) rowView.findViewById(R.id.text_value);
            rowView.findViewById(R.id.view_color).setBackgroundColor(mColors[position]);

            // Return view to be displayed
            return rowView;
        }
    }
}