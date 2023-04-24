package com.celcius001.textblast;

import android.content.pm.PackageManager;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class MainActivity extends AppCompatActivity implements MessageListener {

    public PulsatorLayout pulsatorLayout;
    public String URL = "http://192.168.1.210:8001/api/v1/text-request?message="; // OG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pulsating Effect
        pulsatorLayout = findViewById(R.id.pulsator);
        //pulsatorLayout.setCount(4);
        pulsatorLayout.start();

        // Listener
        Receiver.bindListener(this);
    }

    @Override
    public void messageReceived(String message) {
        try {
            Log.d("msg", message);
            String splitMessage[] = message.split("---");
            String contactNo = splitMessage[1];
            String origMessage = splitMessage[0];
            Snackbar.make(pulsatorLayout, message, Snackbar.LENGTH_LONG).show();
            Log.d("textToUrlParam", textToUrlParam(origMessage));
            validateRequest(textToUrlParam(origMessage), contactNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String textToUrlParam(String message) {
        try {
            message = message.replaceAll("\n", "");
            message = message.replace("<", "");
            message = message.replace(">", "");
            message = message.replace("#", "");
//            message = message.replace(".", "");
            message = message.replace("~", "");
            message = message.replace("|", "");
            message = message.replace("{", "");
            message = message.replace("}", "");
            message = message.replace("[", "");
            message = message.replace("]", "");
            message = message.replace("!", "");
//            message = message.replace("@", "");
            message = message.replace("%", "");
            message = message.replace("^", "");
            message = message.replace("&", "");
            message = message.replace("*", "");
            message = message.replace("(", "");
            message = message.replace(")", "");
//            message = message.replace("_", "");
            message = message.replace(",", "");
            message = message.replace("=", "");
            message = message.replace("?", "");
            message = message.replace("/", "");
            message = message.replace(":", "");
            message = message.replace(";", "");
            String[] split = message.split(" ");
            String value = "";
            for (int i = 0; i < split.length; i++) {
                if (i == split.length - 1) {
                    value += split[i];
                } else {
                    value += split[i] + "+";
                }
            }

            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void validateRequest(String message, String contact) {
        Log.d("valmsg", URL + message);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.GET, URL + message, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // PARSE OBJECT
                    try {
                        JSONObject object = new JSONObject(response);
                        Log.d("ObjMsg", String.valueOf(object));

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(MainActivity.this);
                            SmsManager sms = SmsManager.getDefault(); // using android SmsManager

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                            SubscriptionInfo info2 = (SubscriptionInfo) localList.get(0);
                            ArrayList<String> parts = sms.divideMessage(object.getString("msg"));
                            SmsManager.getSmsManagerForSubscriptionId(info2.getSubscriptionId()).sendMultipartTextMessage(contact, null, parts, null, null);
                            Log.d("REPLY", "RPLY: SENDING... "+ contact);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            requestQueue.add(request);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}