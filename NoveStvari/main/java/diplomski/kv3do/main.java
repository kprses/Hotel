package diplomski.kv3do;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class main extends Activity implements SensorEventListener {

    private boolean OmogucenaKonekcija = false;
    private String Scena;

    private boolean DesnaUkljucena = false;
    private boolean LijevaUkljucena = false;

    private String X = "600";
    private String Y = "600";
    private String Z = "600";

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public Button btnLijva;
    public Button btnDesna;

    public TextView txtLijeva;
    public TextView txtDesna;

    public Button button;
    public TextView tv;
    public EditText editText;

    private float[] mValuesMagnet      = new float[3];
    private float[] mValuesAccel       = new float[3];
    private float[] mValuesOrientation = new float[3];

    private float[] mRotationMatrix    = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.btnKonektujSe);
        btnLijva = (Button) findViewById(R.id.button);
        btnDesna = (Button) findViewById(R.id.button2);

        txtLijeva = (TextView) findViewById(R.id.textView4);
        txtDesna = (TextView) findViewById(R.id.textView5);

        tv = (TextView) findViewById(R.id.textView2);
        editText = (EditText)findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(OmogucenaKonekcija)
                {
                    button.setText("OMOGUĆI");
                    tv.setText("Upravljanje onemogućeno");
                    tv.setTextColor(Color.GRAY);
                    OmogucenaKonekcija = false;

                    LijevaUkljucena = false;
                    DesnaUkljucena = false;

                    txtDesna.setText("ISKLJUČENA");
                    txtDesna.setTextColor(Color.GRAY);

                    txtLijeva.setText("ISKLJUČENA");
                    txtLijeva.setTextColor(Color.GRAY);
                }
                else
                {
                    button.setText("ONEMOGUĆI");
                    tv.setText("Upravljanje omogućeno izaberite scenu!");
                    tv.setTextColor(Color.GREEN);
                    OmogucenaKonekcija = true;

                }

            }
        });

        btnLijva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                LijevaUkljucena = true;
                DesnaUkljucena = false;

                txtDesna.setText("ISKLJUČENA");
                txtDesna.setTextColor(Color.RED);

                txtLijeva.setText("UKLJUČENA");
                txtLijeva.setTextColor(Color.GREEN);
            }
        });

        btnDesna.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                LijevaUkljucena = false;
                DesnaUkljucena = true;

                txtDesna.setText("UKLJUČENA");
                txtDesna.setTextColor(Color.GREEN);

                txtLijeva.setText("ISKLJUČENA");
                txtLijeva.setTextColor(Color.RED);
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                break;
        }

        SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
        SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);



        float pi = 3.14f;

        String s1 = Float.toString(Math.round(mValuesOrientation[0] * 180/pi));
        String s2 = Float.toString(Math.round(mValuesOrientation[1] * 180/pi));
        String s3 = Float.toString(Math.round(mValuesOrientation[2] * 180/pi));

        // konverzija u stepenima
        String vrij = s1  + " " + s2 + " " + s3;

        JSONObject json = new JSONObject();
        try
        {
            json.put("KoordX", s1);
            json.put("KoordY", s2);
            json.put("KoordZ", s3);
            json.put("DESNA_SCENA", DesnaUkljucena);
            json.put("LIJEVA_SCENA", LijevaUkljucena);
            json.put("Konektovan", OmogucenaKonekcija);
        }
        catch (JSONException e)
        {
            Log.v("IZUZETAK", e.getMessage());
        }

        editText.setVisibility(View.INVISIBLE);
        editText.setText(vrij);
        //String poruka = editText.getText().toString();

        NetworkUtilizer utilajzer = new NetworkUtilizer(json);
        utilajzer.execute();
       /* if(s1.equals(X) && s2.equals(Y) && s3.equals(Z))
        {

        }
        else
        {
            X = s1;
            Y = s2;
            Z = s3;

            String poruka = editText.getText().toString();

            NetworkUtilizer utilajzer = new NetworkUtilizer(poruka);
            utilajzer.execute();
        }*/

        /*
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            double[] gravity = new double[3];
            double[] linear_acceleration = new double[3];

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

            editText.setText(s1 + "," + s2 + "," + s3 + "," + s4);

            if(s1.equals(X) && s2.equals(Y) && s3.equals(Z))
            {

            }
            else
            {
                X = s1;
                Y = s2;
                Z = s3;

                String poruka = editText.getText().toString();

                NetworkUtilizer utilajzer = new NetworkUtilizer(poruka);
                utilajzer.execute();
            }



        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
