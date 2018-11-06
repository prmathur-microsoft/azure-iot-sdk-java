/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.common.tests.iothubservices;

import com.microsoft.azure.sdk.iot.common.helpers.*;
import com.microsoft.azure.sdk.iot.common.setup.ReceiveMessagesCommon;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.service.*;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.microsoft.azure.sdk.iot.device.IotHubClientProtocol.*;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReceiveMessagesTests extends ReceiveMessagesCommon
{
    public ReceiveMessagesTests(InternalClient client, IotHubClientProtocol protocol, Device device, Module module, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        super(client, protocol, device, module, authenticationType, clientType, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    @Test (timeout = DEFAULT_TEST_TIMEOUT)
    public void receiveMessagesOverIncludingProperties() throws Exception
    {
        if (testInstance.protocol == HTTPS)
        {
            testInstance.client.setOption(SET_MINIMUM_POLLING_INTERVAL, ONE_SECOND_POLLING_INTERVAL);
        }

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
    }

    @Test (timeout = DEFAULT_TEST_TIMEOUT)
    public void receiveBackToBackUniqueC2DCommandsOverAmqpsUsingSendAsync() throws Exception
    {
        if (this.testInstance.protocol != AMQPS)
        {
            //only want to test for AMQPS
            return;
        }

        // This E2E test is for testing multiple C2D sends and make sure buffers are not getting overwritten
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // set device to receive back to back different commands using AMQPS protocol
        IotHubServicesCommon.openClientWithRetry(testInstance.client);

        // set call back for device client for receiving message
        com.microsoft.azure.sdk.iot.device.MessageCallback callBackOnRx = new MessageCallbackForBackToBackC2DMessages();

        if (testInstance.client instanceof DeviceClient)
        {
            ((DeviceClient) testInstance.client).setMessageCallback(callBackOnRx, null);
        }
        else if (testInstance.client instanceof ModuleClient)
        {
            ((ModuleClient) testInstance.client).setMessageCallback(callBackOnRx, null);
        }

        // send back to back unique commands from service client using sendAsync operation.
        for (int i = 0; i < MAX_COMMANDS_TO_SEND; i++)
        {
            String messageString = Integer.toString(i);
            com.microsoft.azure.sdk.iot.service.Message serviceMessage = new com.microsoft.azure.sdk.iot.service.Message(messageString);

            // set message id
            serviceMessage.setMessageId(Integer.toString(i));

            // set expected list of messaged id's
            messageIdListStoredOnC2DSend.add(Integer.toString(i));

            // send the message. Service client uses AMQPS protocol
            CompletableFuture<Void> future;
            if (testInstance.client instanceof DeviceClient)
            {
                future = serviceClient.sendAsync(testInstance.device.getDeviceId(), serviceMessage);
                futureList.add(future);
            }
            else if (testInstance.client instanceof ModuleClient)
            {
                serviceClient.send(testInstance.device.getDeviceId(), testInstance.module.getId(), serviceMessage);
            }
        }

        for (CompletableFuture<Void> future : futureList)
        {
            try
            {
                future.get();
            }
            catch (ExecutionException e)
            {
                Assert.fail("Exception : " + e.getMessage());
            }
        }

        // Now wait for messages to be received in the device client
        waitForBackToBackC2DMessagesToBeReceived();
        testInstance.client.closeNow(); //close the device client connection
        assertTrue(testInstance.protocol + ", " + testInstance.authenticationType + ": Received messages don't match up with sent messages", messageIdListStoredOnReceive.containsAll(messageIdListStoredOnC2DSend)); // check if the received list is same as the actual list that was created on sending the messages
        messageIdListStoredOnReceive.clear();
    }
}