package lab411.androidserver.loadsharing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import lab411.androidserver.ann.Net;
import lab411.androidserver.features.DataEntry;
import lab411.androidserver.features.MyVector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView responseTime, time, memProcess;
	Button btn_extract, btn_train, btn_detect;
	public static ActivityManager activityManager;
	
	long reTime;
	boolean detectRun = true;
	String stage;
	long detectStart, detectEnd;
	
	private XYMultipleSeriesDataset mDataSet1 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender1 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries1;
	private GraphicalView mChartView1;
	int x_detect = 0;
	double y_detect;
	
	private XYMultipleSeriesDataset mDataSet2 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender2 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries2;
	private GraphicalView mChartView2;
	int x_cpu = 0;
	
	private XYMultipleSeriesDataset mDataSet3 = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRender3 = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries3;
	private GraphicalView mChartView3;
	int x_mem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_extract = (Button) findViewById(R.id.btn_extract);
		btn_train = (Button) findViewById(R.id.btn_train);
		btn_detect = (Button) findViewById(R.id.btn_detect);
		responseTime = (TextView) findViewById(R.id.tv_reponsetime);
		time = (TextView) findViewById(R.id.tv_time);
		memProcess = (TextView) findViewById(R.id.tv_ramprocess);
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        graphInit1();
        graphInit2();
        graphInit3();
        
        Performance p = new Performance();
		p.start();
		
		DrawPerform dp = new DrawPerform();
		dp.start();
		
		btn_extract.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExtractThread et = new ExtractThread();
				et.start();
			}
		});
		btn_train.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TrainThread tt = new TrainThread();
				tt.start();
			}
		});
		btn_detect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DrawRealTime dt = new DrawRealTime();
				Thread t0 = new Thread(dt);
				t0.start();
			}
		});
    }
    class ExtractThread extends Thread
    {
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
				WritePerform wp = new WritePerform("cpu_extract_a.txt", "mem_extract_a.txt");
				wp.start();
				long start = System.currentTimeMillis();
				DataEntry.extract();
				long end = System.currentTimeMillis();
				reTime = end - start;
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						responseTime.setText(String.format("%.3f", (double)reTime/1000) + " s");
					}
				});
				wp.run = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    class TrainThread extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
				WritePerform wp = new WritePerform("cpu_train_a.txt", "mem_train_a.txt");
				wp.start();
				long start = System.currentTimeMillis();
				MyVector[] train = DataEntry.load();
				Net.initNet(7, 10, 2);
				Net.initializeWeights();
				Net.setLearningParameters(0.005, 0.05);
				Net.trainNetworks(train, 10000);
				Net.saveNetworks();
				long end = System.currentTimeMillis();
				reTime = end - start;
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						responseTime.setText(String.format("%.3f", (double)reTime/1000) + " s");
					}
				});
				wp.run = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    class DrawRealTime implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Time rt = new Time();
				rt.start();
				Tmp tmp = new Tmp();
				tmp.start();
				File sdcard = Environment.getExternalStorageDirectory();
				File file = new File(sdcard, "lsdata/REALTIME.txt");
				FileReader fr = new FileReader(file.getAbsoluteFile());
				BufferedReader br = new BufferedReader(fr);
				String line;
				ArrayList<String> list = new ArrayList<String>();
				double[] signal = new double[2000];
				while ((line = br.readLine()) != null) {
					y_detect = Double.parseDouble(line);
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCurrentSeries1.add(x_detect, y_detect);
							mRender1.setXAxisMin(x_detect - 4000);
							mRender1.setXAxisMax(x_detect);
							mChartView1.repaint();
							x_detect++;
						}
					});
					list.add(line);
					if (list.size() == 2000) {
						detectStart = System.currentTimeMillis();
						for (int i = 0; i < list.size(); i++) {
							signal[i] = Double.parseDouble(list.get(i));
						}
						stage = Net.detect(signal);
						detectEnd = System.currentTimeMillis();
						if (stage.equalsIgnoreCase("fail")) {
							list.subList(0, 100).clear();
						} else {
							MainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(), stage, Toast.LENGTH_LONG).show();
									responseTime.setText(Long.toString(detectEnd - detectStart) + " ms");
								}
							});
							list.clear();
						}
					}
					Thread.sleep(10);
				}
				detectRun = false;
				br.close();
				fr.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class Tmp extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				WritePerform wp = new WritePerform("cpu_detect_a.txt", "mem_detect_a.txt");
				wp.start();
				Thread.sleep(180000);
				wp.run = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	class Time extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			detectRun = true;
			try {
				while (detectRun) {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							int tmp = x_detect / 100;
							int h = tmp / 3600;
							int mi = (tmp - h * 3600) / 60;
							int sec = tmp - h * 3600 - mi * 60;
							time.setText(Integer.toString(h) + ":"
									+ Integer.toString(mi) + ":"
									+ Integer.toString(sec));
						}
					});
					Thread.sleep(1000);
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    class DrawPerform extends Thread
    {
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
			try {
				while(true)
	    		{
	    			MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCurrentSeries2.add(x_cpu, Performance.cpu);
							mRender2.setXAxisMin(x_cpu - 200);
							mRender2.setXAxisMax(x_cpu);
							mChartView2.repaint();
							x_cpu++;
							
							mCurrentSeries3.add(x_mem, Performance.mem);
							mRender3.setXAxisMin(x_mem - 200);
							mRender3.setXAxisMax(x_mem);
							mChartView3.repaint();
							x_mem++;
							
							memProcess.setText(String.format("%.2f", Performance.memProcess) + " MB");
						}
					});
	    			Thread.sleep(300);
	    		}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void graphInit1() {
		mRender1.setApplyBackgroundColor(true);
		mRender1.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender1.setAxisTitleTextSize(20);
		mRender1.setChartTitleTextSize(20);
		mRender1.setLabelsTextSize(20);
		mRender1.setLegendTextSize(20);
		mRender1.setMargins(new int[] { 10, 30, 10, 10 });
		mRender1.setPointSize(2.0f);
		mRender1.setXAxisMin(0);
		mRender1.setXAxisMax(4000);
		mRender1.setYAxisMax(50);
		mRender1.setYAxisMin(-50);
		mRender1.setYTitle("uV");
		mRender1.setPanEnabled(false, false);
		mRender1.setZoomEnabled(false, false);
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.detect_grap);
			mChartView1 = ChartFactory.getLineChartView(getApplicationContext(),
					mDataSet1, mRender1);
			mRender1.setClickEnabled(true);
			mRender1.setSelectableBuffer(10);
			mChartView1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView1.repaint();
		}

		XYSeries series = new XYSeries("Pz-Oz channel");
		mDataSet1.addSeries(series);
		mCurrentSeries1 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender1.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);
		
		mChartView1.repaint();
	}
    private void graphInit2() {
		mRender2.setApplyBackgroundColor(true);
		mRender2.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender2.setAxisTitleTextSize(20);
		mRender2.setChartTitleTextSize(20);
		mRender2.setLabelsTextSize(20);
		mRender2.setLegendTextSize(20);
		mRender2.setMargins(new int[] { 10, 30, 10, 10 });
		mRender2.setPointSize(2.0f);
		mRender2.setXAxisMin(0);
		mRender2.setXAxisMax(200);
		mRender2.setYAxisMax(100);
		mRender2.setYAxisMin(0);
		mRender2.setYTitle("%");
		mRender2.setPanEnabled(false, false);
		mRender2.setZoomEnabled(false, false);
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.cpu_grap);
			mChartView2 = ChartFactory.getLineChartView(getApplicationContext(),
					mDataSet2, mRender2);
			mRender2.setClickEnabled(true);
			mRender2.setSelectableBuffer(10);
			mChartView2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView2.repaint();
		}

		XYSeries series = new XYSeries("CPU");
		mDataSet2.addSeries(series);
		mCurrentSeries2 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.GREEN);
		mRender2.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView2.repaint();
	}
    private void graphInit3() {
		mRender3.setApplyBackgroundColor(true);
		mRender3.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender3.setAxisTitleTextSize(20);
		mRender3.setChartTitleTextSize(20);
		mRender3.setLabelsTextSize(20);
		mRender3.setLegendTextSize(20);
		mRender3.setMargins(new int[] { 10, 30, 10, 10 });
		mRender3.setPointSize(2.0f);
		mRender3.setXAxisMin(0);
		mRender3.setXAxisMax(200);
		mRender3.setYAxisMax(100);
		mRender3.setYAxisMin(0);
		mRender3.setYTitle("%");
		mRender3.setPanEnabled(false, false);
		mRender3.setZoomEnabled(false, false);
		if (mChartView3 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.ram_grap);
			mChartView3 = ChartFactory.getLineChartView(getApplicationContext(),
					mDataSet3, mRender3);
			mRender3.setClickEnabled(true);
			mRender3.setSelectableBuffer(10);
			mChartView3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			layout.addView(mChartView3, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView3.repaint();
		}

		XYSeries series = new XYSeries("Memory");
		mDataSet3.addSeries(series);
		mCurrentSeries3 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.GREEN);
		mRender3.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);

		mChartView3.repaint();
	}
}
