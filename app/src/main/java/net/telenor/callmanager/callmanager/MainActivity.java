package net.telenor.callmanager.callmanager;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    private Spinner spinner;
    private ArrayList<String> brojevi;
    private JSONArray result;
    private TextView textViewPoziv,textViewSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicijalizacija liste brojeva
        brojevi = new ArrayList<String>();

        //Inicijalizacija spinnera
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        textViewPoziv = (TextView) findViewById(R.id.textViewPoziv);
        textViewSms = (TextView) findViewById(R.id.textViewSms);

        //pozivanje URL-a
        getData();
    }

    private void getData(){
        StringRequest stringRequest = new StringRequest(Config.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //parsiranje json stringa u json objekat
                            j = new JSONObject(response);

                            //JSON String u JSON Array
                            result = j.getJSONArray(Config.JSON_ARRAY);

                            getPhone(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getPhone(JSONArray j){
        //prolazak kroz ceo json array
        for(int i=0;i<j.length();i++){
            try {
                //json objekat
                JSONObject json = j.getJSONObject(i);

                //dodavanje broja telefona u array list
                brojevi.add(json.getString(Config.TAG_PHONE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //popunjavanje spinnera
        spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, brojevi));
    }

    //Status poziva za odredjeni broj
    private String getPoziv(int position){
        String poziv="";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Poziv za odredjeni broj
            poziv = json.getString(Config.TAG_POZIV);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return poziv;
    }
    //Status sms za odredjeni broj
    private String getSms(int position){
        String sms="";
        try {
            JSONObject json = result.getJSONObject(position);
            sms = json.getString(Config.TAG_SMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sms;
    }


    public void startAlert(View view) {
        String spinervrednost = spinner.getSelectedItem().toString();
        EditText text = (EditText) findViewById(R.id.time);
        int i = Integer.parseInt(text.getText().toString());
        Intent intent = new Intent(this, SampleAlarmReceiver.class);
        intent.putExtra("key", spinervrednost);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textViewPoziv.setText(getPoziv(position));
        textViewSms.setText(getSms(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewPoziv.setText("");
        textViewSms.setText("");
    }
}
