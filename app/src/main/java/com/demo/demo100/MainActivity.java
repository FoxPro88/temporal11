package com.demo.demo100;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import lib.visanet.com.pe.visanetlib.VisaNet;
import lib.visanet.com.pe.visanetlib.data.custom.Channel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    TextInputEditText etAmount;
    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.et_amount);
        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AndroidNetworking.get("https://apitestenv.vnforapps.com/api.security/v1/security")
                        .addHeaders("Authorization", "Basic Z2lhbmNhZ2FsbGFyZG9AZ21haWwuY29tOkF2MyR0cnV6")
                        .setTag("test")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                callSdkNiubiz(response);
                                Log.i(TAG, "onResponse: " + response);
                            }

                            @Override
                            public void onError(ANError error) {

                                Log.i(TAG, "onError: "  + error.getErrorBody());
                            }
                        });

            }
        });



    }

    private void callSdkNiubiz(String tokenJson) {

        try {
            double value = Double.parseDouble(etAmount.getText().toString());
            Map<String, Object> data = new HashMap<>();

            data.put(VisaNet.VISANET_CHANNEL, Channel.MOBILE);
            data.put(VisaNet.VISANET_COUNTABLE, true);
            data.put(VisaNet.VISANET_MERCHANT, "341198210");
            data.put(VisaNet.VISANET_PURCHASE_NUMBER, "2020111701");
            data.put(VisaNet.VISANET_AMOUNT, value);

            HashMap<String, String> MDDdata = new HashMap<String, String>();
            MDDdata.put("19", "LIM");
            MDDdata.put("20", "AQP");
            MDDdata.put("21", "AFKI345");
            MDDdata.put("94", "ABC123DEF");

            data.put(VisaNet.VISANET_MDD, MDDdata);
            data.put(VisaNet.VISANET_ENDPOINT_URL, "https://apitestenv.vnforapps.com/");
            data.put(VisaNet.VISANET_CERTIFICATE_HOST, "apitestenv.vnforapps.com");
            data.put(VisaNet.VISANET_CERTIFICATE_PIN, "sha256/O9OQ6mdMjuzEUP6hfANg0eHJm42TvwO0u+dKliofuvw=");
            data.put(VisaNet.VISANET_SECURITY_TOKEN, tokenJson);

            VisaNet.authorization(this, data);

        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

    }

    private void dialogResult(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("NIUBIZ")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> dialog1.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(this));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VisaNet.VISANET_AUTHORIZATION) {

            if (data != null) {
                if (resultCode == RESULT_OK) {
                    String returnString = data.getExtras().getString("keySuccess");
                    Log.i(TAG, "onActivityResult: " + returnString);
                    dialogResult(returnString);
                } else {
                    String returnString = data.getExtras().getString("keyError");
                    returnString = returnString != null ? returnString : "";
                    Log.i(TAG, "onActivityResult: " + returnString);
                    dialogResult(returnString);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelado....", Toast.LENGTH_SHORT).show();
            }
        }
    }

}