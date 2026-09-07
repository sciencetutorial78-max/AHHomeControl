package com.example.ahhomecontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


        // GUI Components
        private TextView mBluetoothStatus;
        private EditText editText;
        boolean state_pressed =  false;
        private Button mScanBtn, btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8;
        private Button mListPairedDevicesBtn;
        private Button mDiscoverBtn;
        private BluetoothAdapter mBTAdapter;
        private Set<BluetoothDevice> mPairedDevices;
        private BluetoothDevice mmDevice;
        private ArrayAdapter<String> mBTArrayAdapter;
        private ListView mDevicesListView;
        private CheckBox mLED1;
        private static final String appName = "HH";
        private String val = "1";
        private LinearLayout outLayout;
        private Handler mHandler; // Our main handler that will receive callback notifications
        private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
        private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
        private MediaPlayer mediaPlayerON,mediaPlayerOFF;
        private static final UUID BTMODULEUUID = UUID.fromString( "00001101-0000-1000-8000-00805f9b34fb" ); // "random" unique identifier


        // #defines for identifying shared types between calling functions
        private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
        private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
        private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
        private final static int REQUEST_BLUETOOTH_PERMISSIONS = 4; // used when asking for runtime Bluetooth permissions
        boolean connection_state = false;
        String getval[] =  new String[9];
        int index = 1;
         boolean [] btn_state =  {true,true,true,true,true,true,true,true,true};
         boolean bluetoothConnection =  false;

        public boolean isBluetoothEnabled()
        {
            BluetoothAdapter mBluetoothAdapter = getBluetoothAdapter();
            return mBluetoothAdapter != null && hasConnectPermission() && mBluetoothAdapter.isEnabled();

        }

        // Modern, non-deprecated way to obtain the BluetoothAdapter (works on every API level)
        private BluetoothAdapter getBluetoothAdapter() {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            return bluetoothManager != null ? bluetoothManager.getAdapter() : null;
        }

        // Returns the list of dangerous permissions this app needs on the device's current API level
        private String[] getRequiredBluetoothPermissions() {
            List<String> permissions = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+ (API 31+)
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            } else { // Android 6-11 needed location to receive scan results
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            return permissions.toArray(new String[0]);
        }

        private boolean hasBluetoothPermissions() {
            for (String permission : getRequiredBluetoothPermissions()) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        // BLUETOOTH_CONNECT specifically gates calls like isEnabled()/connect() on API 31+
        private boolean hasConnectPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED;
            }
            return true; // permission granted at install time on older Android versions
        }

        private boolean hasScanPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                        == PackageManager.PERMISSION_GRANTED;
            }
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        private void requestBluetoothPermissions() {
            ActivityCompat.requestPermissions(this, getRequiredBluetoothPermissions(), REQUEST_BLUETOOTH_PERMISSIONS);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
                if (hasBluetoothPermissions()) {
                    Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Bluetooth permissions are required for this app to work", Toast.LENGTH_LONG).show();
                }
            }
        }
         void buttonpressed(String i)
         {

           //Toast.makeText( getApplicationContext(),i.trim(), Toast.LENGTH_SHORT ).show();
             switch (i.trim())
             {
                 case "A":
                     mediaPlayerON.start();
                     btn1.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "a" :
                     mediaPlayerOFF.start();
                     btn1.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "B":
                     mediaPlayerON.start();
                     btn2.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "b" :
                     mediaPlayerOFF.start();
                     btn2.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "C":
                     mediaPlayerON.start();
                     btn3.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "c" :
                     mediaPlayerOFF.start();
                     btn3.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "D":
                     mediaPlayerON.start();
                     btn4.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "d" :
                     mediaPlayerOFF.start();
                     btn4.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "E":
                     mediaPlayerON.start();
                     btn5.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "e" :
                     mediaPlayerOFF.start();
                     btn5.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "F":
                     mediaPlayerON.start();
                     btn6.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "f" :
                     mediaPlayerOFF.start();
                     btn6.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "G":
                     mediaPlayerON.start();
                     btn7.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "g" :
                     mediaPlayerOFF.start();
                     btn7.setBackgroundResource( R.drawable.btnoff );
                     break;
                 case "H":
                     mediaPlayerON.start();
                     btn8.setBackgroundResource( R.drawable.btnon );
                     break;
                 case "h" :
                     mediaPlayerOFF.start();
                     btn8.setBackgroundResource( R.drawable.btnoff );
                     break;


             }
         }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
            btn1 = (Button) findViewById( R.id.btn1 );
            btn2 = (Button) findViewById( R.id.btn2 );
            btn3 = (Button) findViewById( R.id.btn3 );
            btn4 = (Button) findViewById( R.id.btn4 );
            btn5 = (Button) findViewById( R.id.btn5 );
            btn6 = (Button) findViewById( R.id.btn6 );
            btn7 = (Button) findViewById( R.id.btn7 );
            btn8 = (Button) findViewById( R.id.btn8 );
            outLayout = (LinearLayout) findViewById( R.id.outLayout );
            mBluetoothStatus = (TextView) findViewById( R.id.bluetoothStatus );
            mScanBtn = (Button) findViewById( R.id.scan );
            mediaPlayerON = MediaPlayer.create( this,R.raw.on);
            mediaPlayerOFF = MediaPlayer.create( this,R.raw.off);
            mDiscoverBtn = (Button) findViewById( R.id.discover );
            mListPairedDevicesBtn = (Button) findViewById( R.id.PairedBtn );

            // Ask for the runtime permissions this API level needs (Bluetooth or Location)
            if (!hasBluetoothPermissions()) {
                requestBluetoothPermissions();
            }

            mBTArrayAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
            mBTAdapter = getBluetoothAdapter(); // get a handle on the bluetooth radio

            if(isBluetoothEnabled())
            {
                bluetoothConnection =  true;
               mScanBtn.setBackgroundResource( R.drawable.bluetoothon );
            }

            mDevicesListView = (ListView) findViewById( R.id.devicesListView );
            mDevicesListView.setAdapter( mBTArrayAdapter ); // assign model to view
            mDevicesListView.setOnItemClickListener( mDeviceClickListener );


            mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == MESSAGE_READ) {
                        String readMessage = null;
                        try {
                            readMessage = new String( (byte[]) msg.obj, "UTF-8" );

                            buttonpressed(readMessage);
                           // Toast.makeText( getApplicationContext(),, Toast.LENGTH_SHORT ).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }



                    }

                    if (msg.what == CONNECTING_STATUS) {
                        if (msg.arg1 == 1) {
                            mDevicesListView.setVisibility(View.INVISIBLE);
                            outLayout.setVisibility(View.VISIBLE);
                            connection_state =  true;
                            mConnectedThread.write("C");

                            mBluetoothStatus.setText( "Connected to Device: " + (String) (msg.obj) );
                        }
                        else
                            mBluetoothStatus.setText( "Connection Failed" );
                    }
                }
            };

            if (mBTArrayAdapter == null) {
                // Device does not support Bluetooth
                mBluetoothStatus.setText( "Status: Bluetooth not found" );
                Toast.makeText( getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT ).show();
            } else   {

                    btn1.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "1";
                                if (btn_state[1]) {
                                   // mediaPlayerON.start();
                                    btn1.setBackgroundResource( R.drawable.btnon );
                                    btn_state[1] = false;
                                } else {
                                    //mediaPlayerOFF.start();
                                    btn1.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[1] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn2.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "2";
                                if (btn_state[2]) {
                                    btn2.setBackgroundResource( R.drawable.btnon );
                                    btn_state[2] = false;
                                } else {
                                    btn2.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[2] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn3.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "3";
                                if (btn_state[3]) {
                                    btn3.setBackgroundResource( R.drawable.btnon );
                                    btn_state[3] = false;
                                } else {
                                    btn3.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[3] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn4.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "4";
                                if (btn_state[4]) {
                                    btn4.setBackgroundResource( R.drawable.btnon );
                                    btn_state[4] = false;
                                } else {
                                    btn4.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[4] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn5.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "5";
                                if (btn_state[5]) {
                                    btn5.setBackgroundResource( R.drawable.btnon );
                                    btn_state[5] = false;
                                } else {
                                    btn5.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[5] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn6.setOnClickListener( new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "6";

                                if (btn_state[6]) {
                                    btn6.setBackgroundResource( R.drawable.btnon );
                                    btn_state[6] = false;
                                } else {
                                    btn6.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[6] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn7.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "7";
                                if (btn_state[7]) {
                                    btn7.setBackgroundResource( R.drawable.btnon );
                                    btn_state[7] = false;
                                } else {
                                    btn7.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[7] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                    btn8.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (connection_state) {
                                val = "8";
                                if (btn_state[8]) {
                                    btn8.setBackgroundResource( R.drawable.btnon );
                                    btn_state[8] = false;
                                } else {
                                    btn8.setBackgroundResource( R.drawable.btnoff );
                                    btn_state[8] = true;
                                }
                                mConnectedThread.write( val );

                            }else{
                                Toast.makeText( getApplicationContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );


                mScanBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!bluetoothConnection){
                            bluetoothConnection =  true;
                            mScanBtn.setBackgroundResource( R.drawable.bluetoothon );
                            bluetoothOn( v );
                        }
                        else {
                            bluetoothConnection =  false;
                            mScanBtn.setBackgroundResource( R.drawable.bluetoothoff );
                            bluetoothOff( v );
                        }

                    }
                } );

              /*  mOffBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bluetoothOff( v );
                    }
                } );
                */
                mListPairedDevicesBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDevicesListView.setVisibility(View.VISIBLE);
                        outLayout.setVisibility(View.INVISIBLE);
                        listPairedDevices( v );
                    }
                } );

                mDiscoverBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDevicesListView.setVisibility(View.VISIBLE);
                        outLayout.setVisibility(View.INVISIBLE);
                        discover( v );
                    }
                } );
            }
        }

        private void bluetoothOn(View view) {
            if (!hasConnectPermission()) {
                requestBluetoothPermissions();
                return;
            }
            if (!mBTAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
                startActivityForResult( enableBtIntent, REQUEST_ENABLE_BT );
                mBluetoothStatus.setText( "Bluetooth enabled" );
                Toast.makeText( getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT ).show();

            } else {
                Toast.makeText( getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT ).show();
            }
        }

        // Enter here after user selects "yes" or "no" to enabling radio
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent Data) {

            // Check which request we're responding to
            if (requestCode == REQUEST_ENABLE_BT) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // The user picked a contact.
                    // The Intent's data Uri identifies which contact was selected.
                    mBluetoothStatus.setText( "Enabled" );
                } else
                    mBluetoothStatus.setText( "Disabled" );
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            try {
                unregisterReceiver( blReceiver );
            } catch (IllegalArgumentException e) {
                // receiver was never registered - safe to ignore
            }
        }

        private void bluetoothOff(View view) {
            if (!hasConnectPermission()) {
                requestBluetoothPermissions();
                return;
            }
            mBTAdapter.disable(); // turn off
            mBluetoothStatus.setText( "Bluetooth disabled" );

            Toast.makeText( getApplicationContext(), "Bluetooth turned Off", Toast.LENGTH_SHORT ).show();
        }

        private void discover(View view) {
            if (!hasScanPermission() || !hasConnectPermission()) {
                requestBluetoothPermissions();
                return;
            }
            // Check if the device is already discovering
            if (mBTAdapter.isDiscovering()) {
                mBTAdapter.cancelDiscovery();
                Toast.makeText( getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT ).show();
            } else {
                if (mBTAdapter.isEnabled()) {
                    mBTArrayAdapter.clear(); // clear items
                    mBTAdapter.startDiscovery();
                    Toast.makeText( getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT ).show();
                    IntentFilter foundFilter = new IntentFilter( BluetoothDevice.ACTION_FOUND );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ requires explicitly declaring whether a receiver is exported
                        registerReceiver( blReceiver, foundFilter, Context.RECEIVER_EXPORTED );
                    } else {
                        registerReceiver( blReceiver, foundFilter );
                    }
                } else {
                    Toast.makeText( getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT ).show();
                }
            }
        }

        final BroadcastReceiver blReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals( action ) && hasConnectPermission()) {
                    BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                    if (device != null) {
                        // add the name to the list
                        mBTArrayAdapter.add( device.getName() + "\n" + device.getAddress() );
                        mBTArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        private void listPairedDevices(View view) {
            if (!hasConnectPermission()) {
                requestBluetoothPermissions();
                return;
            }
            mPairedDevices = mBTAdapter.getBondedDevices();
            if (mBTAdapter.isEnabled()) {
                // put it's one to the adapter

                for (BluetoothDevice device : mPairedDevices)
                    mBTArrayAdapter.add( device.getName() + "\n" + device.getAddress() );

                Toast.makeText( getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT ).show();
            } else
                Toast.makeText( getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT ).show();
        }

        private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                if (!hasConnectPermission()) {
                    requestBluetoothPermissions();
                    return;
                }

                if (!mBTAdapter.isEnabled()) {
                    Toast.makeText( getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT ).show();
                    return;
                }

                mBluetoothStatus.setText( "Connecting..." );
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) v).getText().toString();
                final String address = info.substring( info.length() - 17 );
                final String name = info.substring( 0, info.length() - 17 );

                // Spawn a new thread to avoid blocking the GUI one
                new Thread() {
                    public void run() {
                        boolean fail = false;

                        BluetoothDevice device = mBTAdapter.getRemoteDevice( address );

                        try {
                            mBTSocket = createBluetoothSocket( device );
                        } catch (IOException e) {
                            fail = true;
                            Toast.makeText( getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT ).show();
                        }
                        // Establish the Bluetooth socket connection.
                        try {
                            mBTSocket.connect();


                        } catch (IOException e) {
                            try {
                                fail = true;
                                mBTSocket.close();
                                mHandler.obtainMessage( CONNECTING_STATUS, -1, -1 )
                                        .sendToTarget();
                            } catch (IOException e2) {
                                //insert code to deal with this
                                Toast.makeText( getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT ).show();
                            }
                        }
                        if (fail == false) {
                            mConnectedThread = new ConnectedThread( mBTSocket );
                            mConnectedThread.start();

                            mHandler.obtainMessage( CONNECTING_STATUS, 1, -1, name ).sendToTarget();
                        }
                    }
                }.start();
            }
        };

        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
            return device.createRfcommSocketToServiceRecord( BTMODULEUUID );
            //creates secure outgoing connection with BT device using UUID
        }

        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams, using temp objects because
                // member streams are final
                try {
                    tmpIn = mmSocket.getInputStream();
                    tmpOut = mmSocket.getOutputStream();
                } catch (IOException e) {
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()
                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = mmInStream.available();

                        if (bytes != 0) {
                            SystemClock.sleep( 100 ); //pause and wait for rest of data. Adjust this depending on your sending speed.
                            bytes = mmInStream.available(); // how many bytes are ready to be read?
                            bytes = mmInStream.read( buffer, 0, bytes ); // record how many bytes we actually read
                            mHandler.obtainMessage( MESSAGE_READ, bytes, -1, buffer )
                                    .sendToTarget(); // Send the obtained bytes to the UI activity

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        break;
                    }
                }
            }

            /* Call this from the main activity to send data to the remote device */
            public void write(String message) {
                //Log.d(TAG, "...Data to send: " + message + "...");
                byte[] msgBuffer = message.getBytes();
                try {
                    mmOutStream.write( msgBuffer );

                } catch (IOException e) {
                    Toast.makeText( getApplicationContext(), "Error Sending", Toast.LENGTH_SHORT ).show();
                }
            }

            /* Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                }
            }
        }

        private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
            // Log.d(TAG, "connected: Starting.");

            // Start the thread to manage the connection and perform transmissions
            mConnectedThread = new ConnectedThread( mmSocket );
            mConnectedThread.start();
        }

        private class AcceptThread extends Thread {

            // The local server socket
            private final BluetoothServerSocket mmServerSocket;

            public AcceptThread() {
                BluetoothServerSocket tmp = null;

                // Create a new listening server socket
                try {
                    tmp = mBTAdapter.listenUsingInsecureRfcommWithServiceRecord( appName, BTMODULEUUID );

                    // Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
                } catch (IOException e) {
                    //Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
                }

                mmServerSocket = tmp;
            }

            public void run() {
                // Log.d(TAG, "run: AcceptThread Running.");

                BluetoothSocket socket = null;

                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    //  Log.d(TAG, "run: RFCOM server socket start.....");

                    socket = mmServerSocket.accept();

                    // Log.d(TAG, "run: RFCOM server socket accepted connection.");

                } catch (IOException e) {
                    //Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
                }

                //talk about this is in the 3rd
                if (socket != null) {
                    connected( socket, mmDevice );
                }


            }

            public void cancel() {

                try {
                    mmServerSocket.close();
                } catch (IOException e) {

                }
            }

        }


    }


