/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package tests.integration.com.microsoft.azure.sdk.iot.iothubservices.Methods;

import com.microsoft.azure.sdk.iot.common.helpers.*;
import com.microsoft.azure.sdk.iot.common.setup.DeviceMethodCommon;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.methods.DeviceMethodTests;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DeviceMethodModuleJVMRunner extends DeviceMethodTests
{
    static String[] devicesToDeleteAfterTestClassFinishes;
    static String[][] modulesToDeleteAfterTestClassFinishes;
    static ArrayList<DeviceTestManager> testManagers;

    public DeviceMethodModuleJVMRunner(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, Device device, Module module, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        super(deviceTestManager, protocol, authenticationType, clientType, device, module, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{1} with {2} auth using {3}")
    public static Collection inputs() throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, InterruptedException, ModuleClientException
    {
        iotHubConnectionString = Tools.retrieveEnvironmentVariableValue(TestConstants.IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME);
        X509Cert cert = new X509Cert(0,false, "TestLeaf", "TestRoot");
        String privateKey =  cert.getPrivateKeyLeafPem();
        String publicKeyCert = cert.getPublicCertLeafPem();
        String x509Thumbprint = cert.getThumbPrintLeaf();
        Collection inputs = inputsCommon(ClientType.MODULE_CLIENT, publicKeyCert, privateKey, x509Thumbprint);
        Object[] inputsArray = inputs.toArray();

        testManagers = new ArrayList<>();
        for (int i = 0; i < inputsArray.length; i++)
        {
            Object[] inputCollection = (Object[])inputsArray[i];
            testManagers.add((DeviceTestManager) inputCollection[0]);
        }

        devicesToDeleteAfterTestClassFinishes = (String[])((Object[])inputs.toArray()[0])[6];
        modulesToDeleteAfterTestClassFinishes = (String[][])((Object[])inputs.toArray()[0])[7];
        return inputs;
    }

    @AfterClass
    public static void cleanUpResources()
    {
        tearDown(devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, testManagers);
    }
}
