package com.example.frankchan.gaitkeeper;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by frankchan on 5/1/17.
 */

public class TestingFragment extends Fragment {

    private TextView status;

    private Button startLoginButton;
    private Button stopLoginButton;

    private Intent sensorServiceIntent;

    private EditText clientId;

    private String id;
    private String rawData;
    // ROUTER_5
    // private String endpoint = "http://192.168.29.219:3000/login/";
    // DONT LET YOUR MEMES BE DREAMS
//    private String endpoint = "http://192.168.29.228:3000/login/";
    private String endpoint = "http://128.84.33.52:3000/login/";

    private SensorService mService;

    private ServiceConnection serviceConnection;
    private Handler messageHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.testing_fragment, container, false);

        status = (TextView) root.findViewById(R.id.status);

        startLoginButton = (Button) root.findViewById(R.id.button_start_test);
        stopLoginButton = (Button) root.findViewById(R.id.button_stop_test);
        stopLoginButton.setEnabled(false);

        clientId = (EditText) root.findViewById(R.id.clientId_text);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((SensorService.LocalBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };

        messageHandler = new MessageHandler();

        sensorServiceIntent = new Intent(getActivity(), SensorService.class);
        sensorServiceIntent.putExtra("messenger", new Messenger(messageHandler));
        getActivity().bindService(sensorServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        startLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = clientId.getText().toString();
                if(id.equals("")) {
                    status.setText("Please enter the Client ID before logging in.");
                } else {
                    mService.startService(7000);
                    status.setText("Currently recording status.");
                    stopLoginButton.setEnabled(true);
                }
            }
        });

        stopLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawData = mService.getRawDataAndStop();
                //TODO: Handle the return form PostDataTask
                new PostDataTask().execute(rawData, id, endpoint);
                status.setText("Logging in.");
                stopLoginButton.setEnabled(false);
            }
        });

        return root;
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            System.out.println("Message received");
            int code = message.arg1;
            if(code == 1) {
                rawData = mService.getRawDataAndStop();
                //TODO: Handle the return form PostDataTask
                new PostDataTask().execute(rawData, id, endpoint);
                status.setText("Training stopped.");
                stopLoginButton.setEnabled(false);
            }
        }
    }
}
