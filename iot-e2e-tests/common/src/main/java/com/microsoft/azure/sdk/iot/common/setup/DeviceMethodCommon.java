/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package com.microsoft.azure.sdk.iot.common.setup;

import com.microsoft.azure.sdk.iot.common.helpers.*;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.DeviceMethodTests;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import junit.framework.TestCase;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.microsoft.azure.sdk.iot.device.IotHubClientProtocol.*;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SAS;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SELF_SIGNED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DeviceMethodCommon extends MethodNameLoggingIntegrationTest
{
    public static final String IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME = "IOTHUB_CONNECTION_STRING";
    public static String iotHubConnectionString = "";

    public static DeviceMethod methodServiceClient;
    public static RegistryManager registryManager;

    public static final long DEFAULT_TEST_TIMEOUT = 1 * 60 * 1000;

    public static final Long RESPONSE_TIMEOUT = TimeUnit.SECONDS.toSeconds(200);
    public static final Long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toSeconds(5);
    public static final String PAYLOAD_STRING = "This is a valid payload";

    public static final int NUMBER_INVOKES_PARALLEL = 10;
    public static final int INTERTEST_GUARDIAN_DELAY_MILLISECONDS = 2000;
    // How much to wait until a message makes it to the server, in milliseconds
    public static final Integer SEND_TIMEOUT_MILLISECONDS = 60000;

    //How many milliseconds between retry
    public static final Integer RETRY_MILLISECONDS = 100;

    public DeviceMethodTests.DeviceMethodITRunner testInstance;
    public static final long ERROR_INJECTION_WAIT_TIMEOUT = 1 * 60 * 1000; // 1 minute
    public static final long ERROR_INJECTION_EXECUTION_TIMEOUT = 2* 60 * 1000; // 2 minute

    public static Collection inputsCommon(ClientType clientType, String publicKeyCert, String privateKey, String x509Thumbprint) throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, InterruptedException, ModuleClientException
    {
        methodServiceClient = DeviceMethod.createFromConnectionString(iotHubConnectionString);
        registryManager = RegistryManager.createFromConnectionString(iotHubConnectionString);

        ArrayList<DeviceTestManager> deviceTestManagers = new ArrayList<>();

        String TEST_UUID = UUID.randomUUID().toString();

        /* Create unique device name */
        String deviceId = "java-method-e2e-test-device".concat("-" + TEST_UUID);
        String moduleId = "java-method-e2e-test-module".concat("-" + TEST_UUID);
        String deviceX509Id = "java-method-e2e-test-device-x509".concat("-" + TEST_UUID);
        String moduleX509Id = "java-method-e2e-test-module-x509".concat("-" + TEST_UUID);

        String[] devicesToDeleteAfterTestClassFinishes = new String[] {deviceId, deviceX509Id};
        String[][] modulesToDeleteAfterTestClassFinishes = new String[][] {{deviceId, moduleId}, {deviceX509Id, moduleX509Id}};

        /* Create device on the service */
        Device device = Device.createFromId(deviceId, null, null);
        Module module = Module.createFromId(deviceId, moduleId, null);

        Device deviceX509 = Device.createDevice(deviceX509Id, AuthenticationType.SELF_SIGNED);
        deviceX509.setThumbprint(x509Thumbprint, x509Thumbprint);
        Module moduleX509 = Module.createModule(deviceX509Id, moduleX509Id, AuthenticationType.SELF_SIGNED);
        moduleX509.setThumbprint(x509Thumbprint, x509Thumbprint);

        Collection<Object[]> inputs = new ArrayList<>();

        /* Add devices to the IoTHub */
        device = registryManager.addDevice(device);
        module = registryManager.addModule(module);
        deviceX509 = registryManager.addDevice(deviceX509);
        moduleX509 = registryManager.addModule(moduleX509);

        Thread.sleep(2000);

        for (IotHubClientProtocol protocol : IotHubClientProtocol.values())
        {
            if (protocol != HTTPS)
            {
                if (clientType == ClientType.DEVICE_CLIENT)
                {
                    //sas device client
                    DeviceClient deviceClient = new DeviceClient(registryManager.getDeviceConnectionString(device), protocol);
                    DeviceTestManager deviceClientSasTestManager = new DeviceTestManager(deviceClient);
                    deviceTestManagers.add(deviceClientSasTestManager);
                    inputs.add(makeSubArray(deviceClientSasTestManager, protocol, SAS, "DeviceClient", device, null, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint));
                }
                else if (clientType == ClientType.MODULE_CLIENT)
                {
                    //sas module client
                    ModuleClient moduleClient = new ModuleClient(registryManager.getDeviceConnectionString(device) + ";ModuleId=" + module.getId(), protocol);
                    DeviceTestManager moduleClientSasTestManager = new DeviceTestManager(moduleClient);
                    deviceTestManagers.add(moduleClientSasTestManager);
                    inputs.add(makeSubArray(moduleClientSasTestManager, protocol, SAS, "ModuleClient", device, module, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint));
                }

                if (protocol != MQTT_WS && protocol != AMQPS_WS)
                {
                    if (clientType == ClientType.DEVICE_CLIENT)
                    {
                        //x509 device client
                        DeviceClient deviceClientX509 = new DeviceClient(registryManager.getDeviceConnectionString(deviceX509), protocol, publicKeyCert, false, privateKey, false);
                        DeviceTestManager deviceClientX509TestManager = new DeviceTestManager(deviceClientX509);
                        deviceTestManagers.add(deviceClientX509TestManager);
                        inputs.add(makeSubArray(deviceClientX509TestManager, protocol, SELF_SIGNED, "DeviceClient", deviceX509, null, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint));
                    }
                    else if (clientType == ClientType.MODULE_CLIENT)
                    {
                        //x509 module client
                        ModuleClient moduleClientX509 = new ModuleClient(registryManager.getDeviceConnectionString(deviceX509) + ";ModuleId=" + moduleX509.getId(), protocol, publicKeyCert, false, privateKey, false);
                        DeviceTestManager moduleClientX509TestManager = new DeviceTestManager(moduleClientX509);
                        deviceTestManagers.add(moduleClientX509TestManager);
                        inputs.add(makeSubArray(moduleClientX509TestManager, protocol, SELF_SIGNED, "ModuleClient", deviceX509, moduleX509, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint));
                    }
                }
            }
        }

        return inputs;
    }

    public static Object[] makeSubArray(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, Device device, Module module, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        Object[] inputSubArray = new Object[11];
        inputSubArray[0] = deviceTestManager;
        inputSubArray[1] = protocol;
        inputSubArray[2] = authenticationType;
        inputSubArray[3] = clientType;
        inputSubArray[4] = device;
        inputSubArray[5] = module;
        inputSubArray[6] = devicesToDeleteAfterTestClassFinishes;
        inputSubArray[7] = modulesToDeleteAfterTestClassFinishes;
        inputSubArray[8] = publicKeyCert;
        inputSubArray[9] = privateKey;
        inputSubArray[10] = x509Thumbprint;
        return inputSubArray;
    }

    public DeviceMethodCommon(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, Device device, Module module, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        this.testInstance = new DeviceMethodITRunner(deviceTestManager, protocol, authenticationType, clientType, device, module, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    public class DeviceMethodITRunner
    {
        public DeviceTestManager deviceTestManager;
        public IotHubClientProtocol protocol;
        public AuthenticationType authenticationType;
        public String clientType;
        public Device device;
        public Module module;
        public String[] devicesToDeleteAfterTestClassFinishes;
        public String[][] modulesToDeleteAfterTestClassFinishes;
        public String publicKeyCert;
        public String privateKey;
        public String x509Thumbprint;

        public DeviceMethodITRunner(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, Device device, Module module, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
        {
            this.deviceTestManager = deviceTestManager;
            this.protocol = protocol;
            this.authenticationType = authenticationType;
            this.clientType = clientType;
            this.device = device;
            this.module = module;
            this.devicesToDeleteAfterTestClassFinishes = devicesToDeleteAfterTestClassFinishes;
            this.modulesToDeleteAfterTestClassFinishes = modulesToDeleteAfterTestClassFinishes;
            this.publicKeyCert = publicKeyCert;
            this.privateKey = privateKey;
            this.x509Thumbprint = x509Thumbprint;
        }
    }

    @BeforeClass
    public static void classSetup()
    {
        try
        {
            methodServiceClient = DeviceMethod.createFromConnectionString(iotHubConnectionString);
            registryManager = RegistryManager.createFromConnectionString(iotHubConnectionString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            TestCase.fail("Unexpected exception encountered");
        }
    }

    @Before
    public void cleanToStart()
    {

        try
        {
            this.testInstance.deviceTestManager.stop();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        this.testInstance.deviceTestManager.clearDevice();

        try
        {
            this.testInstance.deviceTestManager.start();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (UnsupportedOperationException e)
        {
            //Only thrown when twin was already initialized. Safe to ignore
        }
        
    }

    @After
    public void delayTests()
    {
        try
        {
            this.testInstance.deviceTestManager.stop();
            Thread.sleep(INTERTEST_GUARDIAN_DELAY_MILLISECONDS);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class RunnableInvoke implements Runnable
    {
        public String deviceId;
        public String moduleId;
        public String testName;
        public CountDownLatch latch;
        public MethodResult result = null;
        public DeviceMethod methodServiceClient;
        public Exception exception = null;

        public RunnableInvoke(DeviceMethod methodServiceClient, String deviceId, String moduleId, String testName, CountDownLatch latch)
        {
            this.methodServiceClient = methodServiceClient;
            this.deviceId = deviceId;
            this.moduleId = moduleId;
            this.testName = testName;
            this.latch = latch;
        }

        @Override
        public void run()
        {
            // Arrange
            exception = null;

            // Act
            try
            {
                if (moduleId != null)
                {
                    result = methodServiceClient.invoke(deviceId, moduleId, DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, testName);

                }
                else
                {
                    result = methodServiceClient.invoke(deviceId, DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, testName);
                }
            }
            catch (Exception e)
            {
                exception = e;
            }

            latch.countDown();
        }

        public String getExpectedPayload()
        {
            return DeviceEmulator.METHOD_LOOPBACK + ":" + testName;
        }

        public MethodResult getResult()
        {
            return result;
        }

        public Exception getException()
        {
            return exception;
        }
    }

    public static void tearDown(String[] deviceIdsToDispose, String[][] moduleIdsToDispose, ArrayList<DeviceTestManager> deviceTestManagers) throws Exception
    {
        for (DeviceTestManager deviceTestManager : deviceTestManagers)
        {
            if (deviceTestManager != null)
            {
                deviceTestManager.stop();
            }
        }

        Tools.removeDevicesAndModules(registryManager, deviceIdsToDispose, moduleIdsToDispose);

        registryManager.close();
    }

    public void setConnectionStatusCallBack(final List actualStatusUpdates)
    {

        IotHubConnectionStatusChangeCallback connectionStatusUpdateCallback = new IotHubConnectionStatusChangeCallback()
        {
            @Override
            public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext) {
                actualStatusUpdates.add(status);
            }
        };

        this.testInstance.deviceTestManager.client.registerConnectionStatusChangeCallback(connectionStatusUpdateCallback, null);
    }

    public void errorInjectionTestFlow(Message errorInjectionMessage) throws Exception
    {
        // Arrange
        final List<IotHubConnectionStatus> actualStatusUpdates = new ArrayList<>();
        setConnectionStatusCallBack(actualStatusUpdates);
        invokeMethodSucceed();

        // Act
        errorInjectionMessage.setExpiryTime(200);
        MessageAndResult errorInjectionMsgAndRet = new MessageAndResult(errorInjectionMessage, null);
        this.testInstance.deviceTestManager.sendMessageAndWaitForResponse(
                errorInjectionMsgAndRet,
                RETRY_MILLISECONDS,
                SEND_TIMEOUT_MILLISECONDS,
                this.testInstance.protocol);

        // Assert
        IotHubServicesCommon.waitForStabilizedConnection(actualStatusUpdates, ERROR_INJECTION_WAIT_TIMEOUT);
        invokeMethodSucceed();
    }

    public void invokeMethodSucceed() throws Exception
    {
        // Arrange
        DeviceTestManager deviceTestManger = this.testInstance.deviceTestManager;

        // Act
        MethodResult result;
        if (testInstance.module != null)
        {
            result = methodServiceClient.invoke(testInstance.device.getDeviceId(), testInstance.module.getId(), DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, PAYLOAD_STRING);
        }
        else
        {
            result = methodServiceClient.invoke(testInstance.device.getDeviceId(), DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, PAYLOAD_STRING);
        }

        deviceTestManger.waitIotHub(1, 10);

        // Assert
        assertNotNull(result);
        assertEquals((long)DeviceEmulator.METHOD_SUCCESS, (long)result.getStatus());
        assertEquals(DeviceEmulator.METHOD_LOOPBACK + ":" + PAYLOAD_STRING, result.getPayload());
        Assert.assertEquals(0, deviceTestManger.getStatusError());
    }
}
