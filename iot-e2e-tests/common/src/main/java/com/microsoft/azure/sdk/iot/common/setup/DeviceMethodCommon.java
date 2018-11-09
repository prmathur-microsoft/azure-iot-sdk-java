/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package com.microsoft.azure.sdk.iot.common.setup;

import com.microsoft.azure.sdk.iot.common.helpers.*;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.methods.DeviceMethodTests;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.service.BaseDevice;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.microsoft.azure.sdk.iot.device.IotHubClientProtocol.*;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SAS;
import static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SELF_SIGNED;
import static org.junit.Assert.*;

/**
 * Utility functions, setup and teardown for all identity method integration tests. This class should not contain any tests,
 * but any children class should.
 */
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

        /* Create unique identity name */
        String deviceId = "java-method-e2e-test-device".concat("-" + TEST_UUID);
        String moduleId = "java-method-e2e-test-module".concat("-" + TEST_UUID);
        String deviceX509Id = "java-method-e2e-test-device-x509".concat("-" + TEST_UUID);
        String moduleX509Id = "java-method-e2e-test-module-x509".concat("-" + TEST_UUID);

        /* Create identity on the service */
        Device device = Device.createFromId(deviceId, null, null);
        Module module = Module.createFromId(deviceId, moduleId, null);

        Device deviceX509 = Device.createDevice(deviceX509Id, AuthenticationType.SELF_SIGNED);
        deviceX509.setThumbprint(x509Thumbprint, x509Thumbprint);
        Module moduleX509 = Module.createModule(deviceX509Id, moduleX509Id, AuthenticationType.SELF_SIGNED);
        moduleX509.setThumbprint(x509Thumbprint, x509Thumbprint);

        Collection<Object[]> inputs = new ArrayList<>();

        /* Add devices to the IoTHub */
        device = registryManager.addDevice(device);
        deviceX509 = registryManager.addDevice(deviceX509);

        if (clientType == ClientType.MODULE_CLIENT)
        {
            module = registryManager.addModule(module);
            moduleX509 = registryManager.addModule(moduleX509);
        }

        Thread.sleep(2000);

        for (IotHubClientProtocol protocol : IotHubClientProtocol.values())
        {
            if (protocol != HTTPS)
            {
                if (clientType == ClientType.DEVICE_CLIENT)
                {
                    //sas identity client
                    DeviceClient deviceClient = new DeviceClient(registryManager.getDeviceConnectionString(device), protocol);
                    DeviceTestManager deviceClientSasTestManager = new DeviceTestManager(deviceClient);
                    deviceTestManagers.add(deviceClientSasTestManager);
                    inputs.add(makeSubArray(deviceClientSasTestManager, protocol, SAS, "DeviceClient", device, publicKeyCert, privateKey, x509Thumbprint));
                }
                else if (clientType == ClientType.MODULE_CLIENT)
                {
                    //sas module client
                    ModuleClient moduleClient = new ModuleClient(registryManager.getDeviceConnectionString(device) + ";ModuleId=" + module.getId(), protocol);
                    DeviceTestManager moduleClientSasTestManager = new DeviceTestManager(moduleClient);
                    deviceTestManagers.add(moduleClientSasTestManager);
                    inputs.add(makeSubArray(moduleClientSasTestManager, protocol, SAS, "ModuleClient", module, publicKeyCert, privateKey, x509Thumbprint));
                }

                if (protocol != MQTT_WS && protocol != AMQPS_WS)
                {
                    if (clientType == ClientType.DEVICE_CLIENT)
                    {
                        //x509 identity client
                        DeviceClient deviceClientX509 = new DeviceClient(registryManager.getDeviceConnectionString(deviceX509), protocol, publicKeyCert, false, privateKey, false);
                        DeviceTestManager deviceClientX509TestManager = new DeviceTestManager(deviceClientX509);
                        deviceTestManagers.add(deviceClientX509TestManager);
                        inputs.add(makeSubArray(deviceClientX509TestManager, protocol, SELF_SIGNED, "DeviceClient", deviceX509, publicKeyCert, privateKey, x509Thumbprint));
                    }
                    else if (clientType == ClientType.MODULE_CLIENT)
                    {
                        //x509 module client
                        ModuleClient moduleClientX509 = new ModuleClient(registryManager.getDeviceConnectionString(deviceX509) + ";ModuleId=" + moduleX509.getId(), protocol, publicKeyCert, false, privateKey, false);
                        DeviceTestManager moduleClientX509TestManager = new DeviceTestManager(moduleClientX509);
                        deviceTestManagers.add(moduleClientX509TestManager);
                        inputs.add(makeSubArray(moduleClientX509TestManager, protocol, SELF_SIGNED, "ModuleClient", moduleX509, publicKeyCert, privateKey, x509Thumbprint));
                    }
                }
            }
        }

        return inputs;
    }

    public static Object[] makeSubArray(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, BaseDevice identity, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        Object[] inputSubArray = new Object[8];
        inputSubArray[0] = deviceTestManager;
        inputSubArray[1] = protocol;
        inputSubArray[2] = authenticationType;
        inputSubArray[3] = clientType;
        inputSubArray[4] = identity;
        inputSubArray[5] = publicKeyCert;
        inputSubArray[6] = privateKey;
        inputSubArray[7] = x509Thumbprint;
        return inputSubArray;
    }

    public DeviceMethodCommon(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, BaseDevice identity, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        this.testInstance = new DeviceMethodITRunner(deviceTestManager, protocol, authenticationType, clientType, identity, publicKeyCert, privateKey, x509Thumbprint);
    }

    public class DeviceMethodITRunner
    {
        public DeviceTestManager deviceTestManager;
        public IotHubClientProtocol protocol;
        public AuthenticationType authenticationType;
        public String clientType;
        public BaseDevice identity;
        public String publicKeyCert;
        public String privateKey;
        public String x509Thumbprint;

        public DeviceMethodITRunner(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, BaseDevice identity, String publicKeyCert, String privateKey, String x509Thumbprint)
        {
            this.deviceTestManager = deviceTestManager;
            this.protocol = protocol;
            this.authenticationType = authenticationType;
            this.clientType = clientType;
            this.identity = identity;
            this.publicKeyCert = publicKeyCert;
            this.privateKey = privateKey;
            this.x509Thumbprint = x509Thumbprint;
        }
    }

    public static Collection<BaseDevice> getIdentities(Collection inputs)
    {
        Set<BaseDevice> identities = new HashSet<>();

        Object[] inputArray = inputs.toArray();
        for (int i = 0; i < inputs.size(); i++)
        {
            Object[] o = (Object[]) inputArray[0];
            identities.add((BaseDevice) o[4]);
        }

        return identities;
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

    public static void tearDown(Collection<BaseDevice> identitiesToDispose, ArrayList<DeviceTestManager> deviceTestManagers)
    {
        try
        {
            for (DeviceTestManager deviceTestManager : deviceTestManagers)
            {
                if (deviceTestManager != null)
                {
                    deviceTestManager.stop();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Failed to stop identity test managers");
        }

        Tools.removeDevicesAndModules(registryManager, identitiesToDispose);

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

    public void invokeMethodSucceed() throws Exception
    {
        // Arrange
        DeviceTestManager deviceTestManger = this.testInstance.deviceTestManager;

        // Act
        MethodResult result;
        if (testInstance.identity instanceof Module)
        {
            result = methodServiceClient.invoke(testInstance.identity.getDeviceId(), ((Module)testInstance.identity).getId(), DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, PAYLOAD_STRING);
        }
        else
        {
            result = methodServiceClient.invoke(testInstance.identity.getDeviceId(), DeviceEmulator.METHOD_LOOPBACK, RESPONSE_TIMEOUT, CONNECTION_TIMEOUT, PAYLOAD_STRING);
        }

        deviceTestManger.waitIotHub(1, 10);

        // Assert
        assertNotNull(result);
        assertEquals((long)DeviceEmulator.METHOD_SUCCESS, (long)result.getStatus());
        assertEquals(DeviceEmulator.METHOD_LOOPBACK + ":" + PAYLOAD_STRING, result.getPayload());
        Assert.assertEquals(0, deviceTestManger.getStatusError());
    }
}
