/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.common.setup;

import com.microsoft.azure.sdk.iot.common.helpers.*;
import com.microsoft.azure.sdk.iot.common.helpers.Tools;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.ReceiveMessagesTests;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.service.*;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.microsoft.azure.sdk.iot.device.IotHubClientProtocol.*;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SAS;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SELF_SIGNED;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReceiveMessagesCommon extends MethodNameLoggingIntegrationTest
{
    public static final long DEFAULT_TEST_TIMEOUT = 3 * 60 * 1000;
    public static Map<String, String> messageProperties = new HashMap<>(3);

    public final static String SET_MINIMUM_POLLING_INTERVAL = "SetMinimumPollingInterval";
    public final static Long ONE_SECOND_POLLING_INTERVAL = 1000L;

    // variables used in E2E test for sending back to back messages using C2D sendAsync method
    public static final int MAX_COMMANDS_TO_SEND = 5; // maximum commands to be sent in a loop
    public static final List messageIdListStoredOnC2DSend = new ArrayList(); // store the message id list on sending C2D commands using service client
    public static final List messageIdListStoredOnReceive = new ArrayList(); // store the message id list on receiving C2D commands using device client

    public static String IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME = "IOTHUB_CONNECTION_STRING";
    public static String iotHubConnectionString = "";
    public static RegistryManager registryManager;

    public static Device device;
    public static Device deviceX509;

    public static Module module;
    public static Module moduleX509;

    public static ServiceClient serviceClient;

    // How much to wait until receiving a message from the server, in milliseconds
    public static final int RECEIVE_TIMEOUT = 3 * 60 * 1000; // 3 minutes

    public static String expectedCorrelationId = "1234";
    public static String expectedMessageId = "5678";
    public static final int INTERTEST_GUARDIAN_DELAY_MILLISECONDS = 2000;
    public static final long ERROR_INJECTION_RECOVERY_TIMEOUT = 1 * 60 * 1000; // 1 minute

    public ReceiveMessagesITRunner testInstance;

    @BeforeClass
    public static void classSetup()
    {
        try
        {
            registryManager = RegistryManager.createFromConnectionString(iotHubConnectionString);
            serviceClient = ServiceClient.createFromConnectionString(iotHubConnectionString, IotHubServiceClientProtocol.AMQPS);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("Unexpected exception occurred");
        }
    }

    public static Collection inputsCommon(ClientType clientType, String publicKeyCert, String privateKey, String x509Thumbprint) throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, ModuleClientException, InterruptedException
    {
        registryManager = RegistryManager.createFromConnectionString(iotHubConnectionString);
        String uuid = UUID.randomUUID().toString();

        String deviceId = "java-device-client-e2e-test-receive-messages".concat("-" + uuid);
        String deviceIdX509 = "java-device-client-e2e-test-receive-messages-x509".concat("-" + uuid);
        String moduleId = "java-module-client-e2e-test-send-messages".concat("-" + uuid);
        String moduleIdX509 = "java-module-client-e2e-test-send-messages-X509".concat("-" + uuid);

        String[] devicesToDeleteAfterTestClassFinishes = new String[] {deviceId, deviceIdX509};
        String[][] modulesToDeleteAfterTestClassFinishes = new String[][] {{deviceId, moduleId}, {deviceIdX509, moduleIdX509}};

        device = Device.createFromId(deviceId, null, null);
        deviceX509 = Device.createDevice(deviceIdX509, SELF_SIGNED);

        module = Module.createFromId(deviceId, moduleId, null);
        moduleX509 = Module.createModule(deviceIdX509, moduleIdX509, SELF_SIGNED);

        deviceX509.setThumbprint(x509Thumbprint, x509Thumbprint);
        moduleX509.setThumbprint(x509Thumbprint, x509Thumbprint);

        module = Module.createFromId(deviceId, moduleId, null);
        moduleX509 = Module.createModule(deviceIdX509, moduleIdX509, SELF_SIGNED);

        deviceX509.setThumbprint(x509Thumbprint, x509Thumbprint);
        moduleX509.setThumbprint(x509Thumbprint, x509Thumbprint);

        registryManager.addDevice(device);
        registryManager.addDevice(deviceX509);

        registryManager.addModule(module);
        registryManager.addModule(moduleX509);
        Thread.sleep(2000);

        messageProperties = new HashMap<>(3);
        messageProperties.put("name1", "value1");
        messageProperties.put("name2", "value2");
        messageProperties.put("name3", "value3");

        serviceClient = ServiceClient.createFromConnectionString(iotHubConnectionString, IotHubServiceClientProtocol.AMQPS);
        serviceClient.open();

        List inputs;
        if (clientType == ClientType.DEVICE_CLIENT)
        {
            inputs = Arrays.asList(
                    new Object[][]
                            {
                                    //sas token
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, device), HTTPS), HTTPS, device, null, SAS, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, device), MQTT), MQTT, device, null, SAS, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, device), MQTT_WS), MQTT_WS, device, null, SAS, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, device), AMQPS), AMQPS, device, null, SAS, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, device), AMQPS_WS), AMQPS_WS, device, null, SAS, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},

                                    //x509
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, deviceX509), HTTPS, publicKeyCert, false, privateKey, false), HTTPS, deviceX509, null, SELF_SIGNED, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, deviceX509), MQTT, publicKeyCert, false, privateKey, false), MQTT, deviceX509, null, SELF_SIGNED, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new DeviceClient(DeviceConnectionString.get(iotHubConnectionString, deviceX509), AMQPS, publicKeyCert, false, privateKey, false), AMQPS, deviceX509, null, SELF_SIGNED, "DeviceClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint}
                            }
            );
        }
        else
        {
            inputs = Arrays.asList(
                    new Object[][]
                            {
                                    //sas token module client
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, device, module), MQTT), MQTT, device, module, SAS, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, device, module), MQTT_WS), MQTT_WS, device, module, SAS, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, device, module), AMQPS), AMQPS, device, module, SAS, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, device, module), AMQPS_WS), AMQPS_WS, device, module, SAS, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},

                                    //x509 module client
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, deviceX509, moduleX509), MQTT, publicKeyCert, false, privateKey, false), MQTT, deviceX509, moduleX509, SELF_SIGNED, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint},
                                    {new ModuleClient(DeviceConnectionString.get(iotHubConnectionString, deviceX509, moduleX509), AMQPS, publicKeyCert, false, privateKey, false), AMQPS, deviceX509, moduleX509, SELF_SIGNED, "ModuleClient", devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint}                           }
            );
        }

        return inputs;
    }

    public ReceiveMessagesCommon(InternalClient client, IotHubClientProtocol protocol, Device device, Module module, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        this.testInstance = new ReceiveMessagesITRunner(client, protocol, device, module, authenticationType, clientType, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    public class ReceiveMessagesITRunner
    {
        public InternalClient client;
        public IotHubClientProtocol protocol;
        public Device device;
        public Module module;
        public AuthenticationType authenticationType;
        public String clientType;
        public String[] devicesToDeleteAfterTestClassFinishes;
        public String[][] modulesToDeleteAfterTestClassFinishes;
        public String publicKeyCert;
        public String privateKey;
        public String x509Thumbprint;

        public ReceiveMessagesITRunner(InternalClient client, IotHubClientProtocol protocol, Device device, Module module, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
        {
            this.client = client;
            this.protocol = protocol;
            this.device = device;
            this.module = module;
            this.authenticationType = authenticationType;
            this.clientType = clientType;
            this.devicesToDeleteAfterTestClassFinishes = devicesToDeleteAfterTestClassFinishes;
            this.modulesToDeleteAfterTestClassFinishes = modulesToDeleteAfterTestClassFinishes;
            this.publicKeyCert = publicKeyCert;
            this.privateKey = privateKey;
            this.x509Thumbprint = x509Thumbprint;
        }
    }

    public static void tearDown(String[] deviceIdsToDispose, String[][] moduleIdsToDispose) throws IOException, IotHubException
    {
        if (registryManager != null)
        {
            Tools.removeDevicesAndModules(registryManager, deviceIdsToDispose, moduleIdsToDispose);
            registryManager.close();
            registryManager = null;
        }
    }

    @After
    public void delayTests()
    {
        //since these lists are recycled between multiple tests, they need to be cleared between each test
        messageIdListStoredOnC2DSend.clear();
        messageIdListStoredOnReceive.clear();

        try
        {
            Thread.sleep(INTERTEST_GUARDIAN_DELAY_MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class MessageCallbackForBackToBackC2DMessages implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult execute(com.microsoft.azure.sdk.iot.device.Message msg, Object context)
        {
            messageIdListStoredOnReceive.add(msg.getMessageId()); // add received messsage id to messageList
            return IotHubMessageResult.COMPLETE;
        }
    }

    public static class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult execute(com.microsoft.azure.sdk.iot.device.Message msg, Object context)
        {
            Boolean resultValue = true;
            HashMap<String, String> messageProperties = (HashMap<String, String>) ReceiveMessagesTests.messageProperties;
            Success messageReceived = (Success)context;
            if (!hasExpectedProperties(msg, messageProperties) || !hasExpectedSystemProperties(msg))
            {
                resultValue = false;
            }

            messageReceived.callbackWasFired();
            messageReceived.setResult(resultValue);
            return IotHubMessageResult.COMPLETE;
        }
    }

    public class MessageCallbackMqtt implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult execute(com.microsoft.azure.sdk.iot.device.Message msg, Object context)
        {
            HashMap<String, String> messageProperties = (HashMap<String, String>) ReceiveMessagesTests.messageProperties;
            Success messageReceived = (Success)context;
            if (hasExpectedProperties(msg, messageProperties) && hasExpectedSystemProperties(msg))
            {
                messageReceived.setResult(true);
            }

            messageReceived.callbackWasFired();

            return IotHubMessageResult.COMPLETE;
        }
    }

    public static boolean hasExpectedProperties(com.microsoft.azure.sdk.iot.device.Message msg, Map<String, String> messageProperties)
    {
        for (String key : messageProperties.keySet())
        {
            if (msg.getProperty(key) == null || !msg.getProperty(key).equals(messageProperties.get(key)))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean hasExpectedSystemProperties(com.microsoft.azure.sdk.iot.device.Message msg)
    {
        if (msg.getCorrelationId() == null || !msg.getCorrelationId().equals(expectedCorrelationId))
        {
            return false;
        }

        if (msg.getMessageId() == null || !msg.getMessageId().equals(expectedMessageId))
        {
            return false;
        }

        //all system properties are as expected
        return true;
    }

    public void sendMessageToDevice(String deviceId, String protocolName) throws IotHubException, IOException
    {
        String messageString = "Java service e2e test message to be received over " + protocolName + " protocol";
        com.microsoft.azure.sdk.iot.service.Message serviceMessage = new com.microsoft.azure.sdk.iot.service.Message(messageString);
        serviceMessage.setCorrelationId(expectedCorrelationId);
        serviceMessage.setMessageId(expectedMessageId);
        serviceMessage.setProperties(messageProperties);
        serviceClient.open();
        serviceClient.send(deviceId, serviceMessage);
    }

    public void sendMessageToModule(String deviceId, String moduleId, String protocolName) throws IotHubException, IOException
    {
        String messageString = "Java service e2e test message to be received over " + protocolName + " protocol";
        com.microsoft.azure.sdk.iot.service.Message serviceMessage = new com.microsoft.azure.sdk.iot.service.Message(messageString);
        serviceMessage.setCorrelationId(expectedCorrelationId);
        serviceMessage.setMessageId(expectedMessageId);
        serviceMessage.setProperties(messageProperties);
        serviceClient.open();
        serviceClient.send(deviceId, moduleId, serviceMessage);
    }

    public void waitForMessageToBeReceived(Success messageReceived, String protocolName)
    {
        try
        {
            long startTime = System.currentTimeMillis();
            while (!messageReceived.wasCallbackFired())
            {
                Thread.sleep(300);

                if (System.currentTimeMillis() - startTime > RECEIVE_TIMEOUT)
                {
                    fail(testInstance.protocol + ", " + testInstance.authenticationType + ": Timed out waiting to receive message");
                }
            }

            if (!messageReceived.getResult())
            {
                Assert.fail(testInstance.protocol + ", " + testInstance.authenticationType + ": Receiving message over " + protocolName + " protocol failed. Received message was missing expected properties");
            }
        }
        catch (InterruptedException e)
        {
            Assert.fail(testInstance.protocol + ", " + testInstance.authenticationType + ": Receiving message over " + protocolName + " protocol failed. Unexpected interrupted exception occurred");
        }
    }

    public void waitForBackToBackC2DMessagesToBeReceived()
    {
        try
        {
            long startTime = System.currentTimeMillis();

            // check if all messages are received.
            while (messageIdListStoredOnReceive.size() != MAX_COMMANDS_TO_SEND)
            {
                Thread.sleep(100);

                System.out.println(messageIdListStoredOnReceive.size());

                if (System.currentTimeMillis() - startTime > RECEIVE_TIMEOUT)
                {
                    Assert.fail(testInstance.protocol + ", " + testInstance.authenticationType + ": Receiving messages timed out.");
                }
            }
        }
        catch (InterruptedException e)
        {
            Assert.fail(testInstance.protocol + ", " + testInstance.authenticationType + ": Receiving message failed. Unexpected interrupted exception occurred.");
        }
    }

    public void errorInjectionTestFlow(com.microsoft.azure.sdk.iot.device.Message errorInjectionMessage) throws IOException, IotHubException, InterruptedException
    {
        final ArrayList<IotHubConnectionStatus> connectionStatusUpdates = new ArrayList<>();
        testInstance.client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallback()
        {
            @Override
            public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext)
            {
                connectionStatusUpdates.add(status);
            }
        }, null);

        IotHubServicesCommon.openClientWithRetry(testInstance.client);

        com.microsoft.azure.sdk.iot.device.MessageCallback callback = new MessageCallback();

        if (testInstance.protocol == MQTT || testInstance.protocol == MQTT_WS)
        {
            callback = new MessageCallbackMqtt();
        }

        Success messageReceived = new Success();
        if (testInstance.client instanceof DeviceClient)
        {
            ((DeviceClient) testInstance.client).setMessageCallback(callback, messageReceived);
        }
        else if (testInstance.client instanceof ModuleClient)
        {
            ((ModuleClient) testInstance.client).setMessageCallback(callback, messageReceived);
        }

        //error injection message is not guaranteed to be ack'd by service so it may be re-sent. By setting expiry time,
        // we ensure that error injection message isn't resent to service too many times. The message will still likely
        // be sent 3 or 4 times causing 3 or 4 disconnections, but the test should recover anyways.
        errorInjectionMessage.setExpiryTime(200);
        testInstance.client.sendEventAsync(errorInjectionMessage, new EventCallback(null), null);

        //wait to send the message because we want to ensure that the tcp connection drop happens beforehand and we
        // want the connection to be re-established before sending anything from service client
        IotHubServicesCommon.waitForStabilizedConnection(connectionStatusUpdates, ERROR_INJECTION_RECOVERY_TIMEOUT);

        if (testInstance.client instanceof DeviceClient)
        {
            sendMessageToDevice(testInstance.device.getDeviceId(), testInstance.protocol.toString());
        }
        else if (testInstance.client instanceof ModuleClient)
        {
            sendMessageToModule(testInstance.device.getDeviceId(), testInstance.module.getId(), testInstance.protocol.toString());
        }

        waitForMessageToBeReceived(messageReceived, testInstance.protocol.toString());

        Thread.sleep(200);
        testInstance.client.closeNow();

        assertTrue(testInstance.protocol + ", " + testInstance.authenticationType + ": Error Injection message did not cause service to drop TCP connection", connectionStatusUpdates.contains(IotHubConnectionStatus.DISCONNECTED_RETRYING));
    }
}
