package com.sadeemlight.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sadeemlight.Models.ModelParamsPair;
import com.sadeemlight.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sadeemlight.config.ConstValue;
import com.sadeemlight.util.Progress_dialog;
import com.sadeemlight.util.ServiceHandler;
import com.sadeemlight.util.Session_management;

/**
 * Created by Rajesh Dabhi on 2/9/2016.
 */
public class SuggestionFragment extends Fragment implements View.OnClickListener {

    View mRootView;
    EditText et_suggest;
    Button send;

    Session_management sessionManagement;

    public SuggestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.suggestion_activity, container, false);

        ((MainActivity) getActivity()).setTitle(R.string.action_suggest);

        sessionManagement = new Session_management(getActivity());

        et_suggest = (EditText) mRootView.findViewById(R.id.et_sugges);
        send = (Button) mRootView.findViewById(R.id.btn_sugges_send);

        send.setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onClick(View view)
    {
        String getSuggest = et_suggest.getText().toString().trim();
        if (!getSuggest.contentEquals(""))
        {
            new sendSuggest().execute();
        }
        else
        {
            Toast.makeText(getActivity(), "Please Fill Detail.", Toast.LENGTH_SHORT).show();
        }
    }

    public class sendSuggest extends AsyncTask<Void, Void, Void> {

        String response = "";
        String message = "";

        Progress_dialog pd = new Progress_dialog(getActivity());
        private List<ModelParamsPair> params = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.showProgressbar();

            String getSuggest = et_suggest.getText().toString().trim();
            String getstudentid = sessionManagement.getUserDetails().get(ConstValue.KEY_STUDENT_ID);
            params.add(new ModelParamsPair("student_id", getstudentid));
            params.add(new ModelParamsPair("suggestion", getSuggest));

        }

        @Override
        protected Void doInBackground(Void... voids) {

            response = "";

            ServiceHandler sh = new ServiceHandler();

            String url = ConstValue.SUGGESTION_SEND_URL;

            String access_token = sessionManagement.getUserDetails().get(ConstValue.KEY_ACCESSTOKEN);
            String jsonSTR = null;
            try {
                jsonSTR = sh.makeServiceCallWithTokeOk(url, ConstValue.POST, params, access_token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonSTR != null) {

                Log.e("response: ", jsonSTR);

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonSTR);

                    response = jsonObject.getString("Status");
                    message = jsonObject.getString("Message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismissProgress();

            Log.e("onPostExecute: ", response);

            if (response.contentEquals("true"))
            {
                et_suggest.setText("");
                SuggestionThanksDlg dlg= new SuggestionThanksDlg(SuggestionFragment.this);
                dlg.show(send);
            }
        }
    }
}
