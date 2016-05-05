package com.kerim.kv3do;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class MainActivity extends Activity implements SensorEventListener {

    TextView textResponse,textResponse1;
    EditText editTextAddress, editTextPort, editUnesiText;
    Button buttonConnect, buttonClear;

    private SensorManager mSensorManager1;
    private Sensor mSensor;


    Socket socket;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mSensorManager1 = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager1.registerListener(this, mSensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        textResponse1 = (TextView)findViewById(R.id.response1);
        editUnesiText = (EditText)findViewById(R.id.unos);

        socket = null;
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();
                }};

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {



            try {
                socket = new Socket(dstAddress, dstPort);
                //socket.send("Hello Server!");
               /* ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

*/
    /*
     * notice:
     * inputStream.read() will block if no data return
     */
               /* while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }*/

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }


        static final float NS2S = 1.0f / 1000000000.0f;
        float[] last_values = null;
        float[] velocity = null;
        float[] position = null;
        long last_timestamp = 0;

        //  @Override
      /*  public void onSensorChanged(SensorEvent event) {
            System.out.println("Doso");
            if(last_values != null){
                float dt = (event.timestamp - last_timestamp) * NS2S;

                for(int index = 0; index < 3;++index){
                    velocity[index] += (event.values[index] + last_values[index])/2 * dt;
                    position[index] += velocity[index] * dt;
                }
            }
            else{
                last_values = new float[3];
                velocity = new float[3];
                position = new float[3];
                velocity[0] = velocity[1] = velocity[2] = 0f;
                position[0] = position[1] = position[2] = 0f;
            }
            System.arraycopy(event.values, 0, last_values, 0, 3);
            last_timestamp = event.timestamp;

            textResponse1.setText(position.toString());

        }*/


    }

            public void SensorMetoda ()
    {
        mSensorManager1 = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager1.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){

            System.out.println("dosooooGore");
            List<Sensor> gravSensors = mSensorManager1.getSensorList(Sensor.TYPE_GRAVITY);
            for(int i=0; i<gravSensors.size(); i++) {
                if ((gravSensors.get(i).getVendor().contains("Google Inc.")) &&
                        (gravSensors.get(i).getVersion() == 3)){
                    // Use the version 3 gravity sensor.
                    mSensor = gravSensors.get(i);
                }
            }
        }
        else{
            System.out.println("dosooooDole");
            // Use the accelerometer.
            if (mSensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                System.out.println("dosooooAceeel");
                mSensor = mSensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                System.out.println(mSensor.toString());

            }
            else{
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
            }

        }

    }



    public void onSensorChanged(SensorEvent event){

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){


            double [] gravity = new double[3];
            double [] linear_acceleration= new double[3];

            final double alpha = 0.8;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            String s1 = Double.toString(Math.round(linear_acceleration[0]));
            String s2 = Double.toString(Math.round(linear_acceleration[1]));
            String s3 = Double.toString(Math.round(linear_acceleration[2]));

            editUnesiText.setText(s1+","+s2+","+s3);

            if(socket!=null) {
                try {
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

// Send first message
                    dOut.writeByte(1);
                    dOut.writeUTF("This is the first type of message.");
                    dOut.flush(); // Send off the data

// Send the second message
                    dOut.writeByte(2);
                    dOut.writeUTF("This is the second type of message.");
                    dOut.flush(); // Send off the data

// Send the third message
                    dOut.writeByte(3);
                    dOut.writeUTF("This is the third type of message (Part 1).");
                    dOut.writeUTF("This is the third type of message (Part 2).");
                    dOut.flush(); // Send off the data

// Send the exit message
                    dOut.writeByte(-1);
                    dOut.flush();

                    dOut.close();
                }
                catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
            else
            {

            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}