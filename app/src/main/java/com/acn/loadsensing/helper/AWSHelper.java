package com.acn.loadsensing.helper;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class AWSHelper {

    private static final String LOG_TAG = "***";
    private AWSIotMqttManager mqttManager;

    public AWSHelper(AWSIotMqttManager mqttManager) {
        this.mqttManager = mqttManager;
    }

    public void connectToAWS() {
        try {
            mqttManager.connect(AWSMobileClient.getInstance(), new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + status);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (throwable != null) {
                                Log.e(LOG_TAG, "Connection error.", throwable);
                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
        }
    }

    public void turnLightOn() {
        final String msg = createStateJson("on");

        publishToTopic(msg);
    }

    public void turnLightOff() {
        final String msg = createStateJson("off");

        publishToTopic(msg);
    }

    public void disconnectFromAWS() {
        try {
            mqttManager.disconnect();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Disconnect error.", e);
        }
    }

    private void publishToTopic(String msg) {
        final String topic = "$aws/things/IoTLight/shadow/update";
        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    private String createStateJson(String state) {
        return "{\n" +
                "\"state\": {\n" +
                "\"desired\": {\n" +
                "\"state\": \"" + state + "\"\n" +
                "}\n" +
                "}\n" +
                "}";
    }
}
