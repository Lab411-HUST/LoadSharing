package lab411.android2pc.loadsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Connect extends Activity {
	Button btn_connect;
	EditText edt_port, edt_ip;
	String HOSTNAME;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		btn_connect = (Button) findViewById(R.id.btn_ketnoi);
		edt_port = (EditText) findViewById(R.id.edt_port);
		edt_ip = (EditText) findViewById(R.id.edt_ip);
		edt_port.setText("9999");
		edt_ip.setText("192.168.43.213");
		
		btn_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectClient cc = new ConnectClient();
				cc.start();
			}
		});
	}

	class ConnectClient extends Thread {
		Socket socket;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			AndroidDeviceName dn = new AndroidDeviceName();
			String DEVICENAME = dn.DeviceName;
			int p = Integer.parseInt(edt_port.getText().toString());
			String ip = edt_ip.getText().toString();
			PCServer.set(p, ip);
			try {
				socket = new Socket(PCServer.IP, PCServer.PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(DEVICENAME);
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				HOSTNAME = input.readLine();
				Connect.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Attempting to connect to "+HOSTNAME, Toast.LENGTH_SHORT).show();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Toast.makeText(getApplicationContext(), "Connection Established", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(Connect.this, MainActivity.class);
						startActivity(intent);
		            	finish();
					}
				});
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

}
