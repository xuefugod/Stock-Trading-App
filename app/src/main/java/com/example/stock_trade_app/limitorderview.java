package com.example.stock_trade_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class limitorderview extends AppCompatActivity {
    ListView listView1;
    Button button1;
    EditText editTextid;
    private Timer timer = null;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limitorderview);
        int delay = 0; // delay for 0 sec.
        int period = 3000; // repeat every 10 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new limitorderview.displaylimitorder(),delay, period);
        Intent intent = getIntent();
        String username = intent.getStringExtra(("key"));
        editTextid = findViewById((R.id.editTextNumber));
        button1 = findViewById((R.id.deletelimitorder));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String value1 = editText.getText().toString();
                String value2 = editTextid.getText().toString();
                if ((!value2.equals(""))){


                    //date1 = new SimpleDateFormat("yyyy/MM/dd").format(dateObject);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody formbody= new FormBody.Builder()
                            .add("id",value2).add("username",username)
                            .build();
                    Request request = new Request.Builder().url("http://192.168.56.1:5000/deleteorder").post(formbody).build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(limitorderview.this, "network not found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            TextView testView = findViewById(R.id.ordertextview);
                            testView.setText(response.body().string());
                        }
                    });


                }else{
                    Toast.makeText(limitorderview.this, "Field cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class displaylimitorder extends TimerTask {
        @Override
        public void run() {


            handler.post(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient okHttpClient = new OkHttpClient();

                    Intent intent = getIntent();
                    String username = intent.getStringExtra(("key"));
                    RequestBody formbody= new FormBody.Builder().add("username",username)
                            .build();
                    Request request = new Request.Builder().url("http://192.168.56.1:5000/limitorderview").post(formbody).build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(limitorderview.this, "network not found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            if (response.isSuccessful()){
                                JSONArray jarray = new JSONArray();
                                Gson gson = new Gson();
                                try{
                                    final String jsonData = response.body().string().trim();
                                    JSONObject jobject = new JSONObject(jsonData);
                                    jarray = jobject.getJSONArray("userlimitorder");
                                    JSONArray finalJarray = jarray;
                                    //Log.d("mytag1",jsonData);
                                    //Log.d("mytag3",jarray.());
                                    Log.d("mytag2",finalJarray.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override


                                        public void run() {
                                            ArrayList<Limitorder> arrayList = new ArrayList<Limitorder>();
                                            try {
                                                for (int i=0, l=finalJarray.length(); i<l; i++){
                                                    arrayList.add(new Limitorder(finalJarray.getJSONObject(i).get("id"),finalJarray.getJSONObject(i).get("username"),
                                                            finalJarray.getJSONObject(i).get("stock_symbol"),finalJarray.getJSONObject(i).get("buy"),
                                                            finalJarray.getJSONObject(i).get("shares"),finalJarray.getJSONObject(i).get("prices"),finalJarray.getJSONObject(i).get("expire_date")));
                                                    //cash_amount[0] = finalJarray.getJSONObject(i).get("cashamount").toString();
                                                    Log.d("mytag", arrayList.toString());

                                                }
                                            } catch (JSONException e) {}
                                            listView1=  (ListView) findViewById(R.id.orderview);
                                            limitorderAdapter adapter = new limitorderAdapter (limitorderview.this,R.layout.activity_limitorder_adapter,arrayList);//jArray is your json array
                                            //Log.d("mytag3",adapter.toString());
                                            //Set the above adapter as the adapter of choice for our list
                                            listView1.setAdapter(adapter);
                                            //String cashstirng = "Your cash amount: ";
                                            //cashamount.setText(cashstirng + cash_amount[0]);


                                        }
                                    });
                                }catch(IOException | JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}