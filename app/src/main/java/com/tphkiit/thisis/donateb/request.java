package com.tphkiit.thisis.donateb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class request extends Fragment implements View.OnClickListener {
    EditText pin;
    Button submit;
    Spinner spinner;
    String pinCode, group;
    ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        pin = (EditText) view.findViewById(R.id.pinInput);
        submit = (Button) view.findViewById(R.id.button);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        submit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        pinCode = pin.getText().toString().trim();
        group = spinner.getSelectedItem().toString().trim();

        if (pinCode.length() < 6) {
            Toast.makeText(getActivity(), "Please enter correct pin code!", Toast.LENGTH_LONG).show();
        } else if (group.equals("None")) {
            Toast.makeText(getActivity(), "Please select a blood group", Toast.LENGTH_LONG).show();
        } else {
            YourAsyncTask task = new YourAsyncTask(getActivity());
            task.execute("http://postalpincode.in/api/pincode/" + pinCode);
        }
    }

    public class YourAsyncTask extends AsyncTask<String, String, String> {
        private Context mContext;

//private TaskCompleted mCallback;

        public YourAsyncTask(Activity activity) {
            this.mContext = activity;
/*    try {
        this.mCallback = (TaskCompleted) activity;
    } catch (ClassCastException e) {
        ClassCastException tothrow = new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        tothrow.initCause(e);
        throw tothrow;
    }*/
        }

        @Override
        public void onPreExecute() {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("Please wait...");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                return "NULL";
            }
        }

        @Override
        protected void onPostExecute(String results) {
            //This is where you return data back to caller
            onTaskComplete(results);

        }


    }

    public void onTaskComplete(String result) {
        mProgress.dismiss();
        if (result.equals("NULL")) {
            Toast.makeText(getActivity(), "Please turn on your internet connection!", Toast.LENGTH_LONG).show();
        } else{
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String status = null;
            String postOffice = null;
            String district = null;

            try {
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equals(null)) {
                Toast.makeText(getActivity(), "Please turn on your internet connection!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Error")) {
                Toast.makeText(getActivity(), "Wrong Pin Code!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Success")) {

                try {
                    postOffice = jsonObject.getString("PostOffice");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray jsonArray = new JSONArray(postOffice);
                    JSONObject jsonpart = jsonArray.getJSONObject(0);
                    district = jsonpart.getString("District");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                group = spinner.getSelectedItem().toString();
                pinCode = pin.getText().toString().trim();


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Location : "+district+"\nConfirm to submit!")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myintent = new Intent(getActivity(), FindDonor.class);
                                myintent.putExtra("pin",pinCode);
                                myintent.putExtra("group",group);
                                startActivity(myintent);
                                //getActivity().finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                }
        }
        }
    }
