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


public class TrainingFragment extends Fragment {

    private TextView acceleration;

    private Button startButton;
    private Button stopButton;

    private Intent sensorServiceIntent;

    private EditText netId;

    private String id;
    // ROUTER_5, ssh machine eddy
//    private String endpoint = "http://192.168.29.219:3000/train/";
    // DONT LET YOUR MEMES BE DREAMS, local eddy
    // private String endpoint = "http://192.168.29.228:3000/train/";

    // private String endpoint = "http://fc249.pythonanywhere.com/api/v1/training_data/raw/";
    private String endpoint = "http://128.84.33.52:3000/train/";

    private String rawData;
    private SensorService mService;

    private ServiceConnection serviceConnection;
    private Handler messageHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Training view created");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.training_fragment, container, false);

        acceleration = (TextView) root.findViewById(R.id.acceleration_values);

        startButton = (Button) root.findViewById(R.id.button_start);
        stopButton = (Button) root.findViewById(R.id.button_stop);
        stopButton.setEnabled(false);

        netId = (EditText) root.findViewById(R.id.netid_text);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                System.out.println("mService set to Training Service");
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

        System.out.println("Binding SensorService to TrainingFragment");
        getActivity().bindService(sensorServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        /**
         * BUTTON HANDLERS
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = netId.getText().toString();
                if(id.equals("")) {
                    acceleration.setText("Please enter your Net Id before training");
                } else {
                    mService.startService(40000);
                    acceleration.setText("Currently training/recording acceleration.");
                    stopButton.setEnabled(true);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawData = mService.getRawDataAndStop();
                //TODO: Handle the return form PostDataTask
                new PostDataTask().execute(rawData, id, endpoint);
                acceleration.setText("Training stopped.");
                stopButton.setEnabled(false);
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
                acceleration.setText("Training stopped.");
                stopButton.setEnabled(false);
            }
        }
    }
}
