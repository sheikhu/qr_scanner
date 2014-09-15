package com.sheikhu.qrscanner;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import lib.RestClient;
import services.Services;


public class MainActivity extends ActionBarActivity {

    private Button btnScan, btnLogin;

    private EditText passwordField;

    private TextView label_password, label_status;

    private ProgressBar progress;
    private RestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = (Button) findViewById(R.id.btn_scan);
        btnLogin = (Button)this.findViewById(R.id.login_button);
        passwordField = (EditText) this.findViewById(R.id.password_field);

        label_password = (TextView) this.findViewById(R.id.password_label);
        label_status = (TextView) this.findViewById(R.id.label_logged_in);

        progress = (ProgressBar) this.findViewById(R.id.loader);
        btnScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        "com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {


                progress.setVisibility(View.VISIBLE);
                final String identifier = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result

                Log.i("qr_code_scanned", identifier);
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Toast.makeText(MainActivity.this, identifier+"#"+format, Toast.LENGTH_LONG).show();
                TextView tv = (TextView) findViewById(R.id.text_view);



                String fetch_user_url = getString(R.string.url_fetch_user, identifier);
                client = new RestClient(fetch_user_url);
                Log.i("fetch_user_url", fetch_user_url);

                try {
                    client.Execute(RestClient.RequestMethod.GET);
                    String response = client.getResponse();

                    JSONObject jsonResponse = new JSONObject(response);

                    Log.i("json_response", response);

                    if(jsonResponse.getString("status").equals("success"))
                    {
                        tv.setText(jsonResponse.getJSONObject("data").getString("identifier"));

                        statusLoginElements(View.VISIBLE);
                        progress.setVisibility(View.GONE);

                        btnLogin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progress.setVisibility(View.VISIBLE);

                                String password = passwordField.getText().toString();

                                Services.url = getString(R.string.url_auth);

                                JSONObject response = Services.auth(identifier, password);

                                if(response != null)
                                {
                                    progress.setVisibility(View.GONE);
                                    try {
                                        if(response.getString("status").equals("success"))
                                        {
                                            Toast.makeText(MainActivity.this, "Authentication successfully !", Toast.LENGTH_LONG).show();
                                            statusLoginElements(View.GONE);
                                            label_status.setVisibility(View.VISIBLE);

                                        }
                                        else
                                            Toast.makeText(MainActivity.this, "Authentication failed !", Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    } else {
                        tv.setText(jsonResponse.getString("message"));
                    }
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void statusLoginElements(int status) {
        label_password.setVisibility(status);
        passwordField.setVisibility(status);
        btnLogin.setVisibility(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
