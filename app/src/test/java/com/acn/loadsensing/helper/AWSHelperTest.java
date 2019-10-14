package com.acn.loadsensing.helper;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class AWSHelperTest {

    private AWSIotMqttManager mockManager;
    private AWSHelper awsHelper;

    @Before
    public void setUp() {
        mockManager = mock(AWSIotMqttManager.class);
        awsHelper = new AWSHelper(mockManager);
    }

    @Test
    public void disconnectFromAWS_disconnects() {
        awsHelper.disconnectFromAWS();

        verify(mockManager).disconnect();
    }

    @Test
    public void turnLightOn_publishesToTopic() {
        String expectedMessage = "{\n" +
                "\"state\": {\n" +
                "\"desired\": {\n" +
                "\"state\": \"on\"\n" +
                "}\n" +
                "}\n" +
                "}";

        awsHelper.turnLightOn();

        verify(mockManager).publishString(expectedMessage, "$aws/things/IoTLight/shadow/update", AWSIotMqttQos.QOS0);
    }

    @Test
    public void turnLightOff_publishesToTopic() {
        String expectedMessage = "{\n" +
                "\"state\": {\n" +
                "\"desired\": {\n" +
                "\"state\": \"off\"\n" +
                "}\n" +
                "}\n" +
                "}";

        awsHelper.turnLightOff();

        verify(mockManager).publishString(expectedMessage, "$aws/things/IoTLight/shadow/update", AWSIotMqttQos.QOS0);
    }
}