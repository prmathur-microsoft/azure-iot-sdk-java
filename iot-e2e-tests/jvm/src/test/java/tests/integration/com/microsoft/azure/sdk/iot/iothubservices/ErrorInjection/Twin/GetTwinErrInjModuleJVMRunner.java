/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package tests.integration.com.microsoft.azure.sdk.iot.iothubservices.ErrorInjection.Twin;

import com.microsoft.azure.sdk.iot.common.helpers.ClientType;
import com.microsoft.azure.sdk.iot.common.helpers.TestConstants;
import com.microsoft.azure.sdk.iot.common.helpers.Tools;
import com.microsoft.azure.sdk.iot.common.helpers.X509Cert;
import com.microsoft.azure.sdk.iot.common.setup.DeviceTwinCommon;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.errorinjection.GetTwinErrInjTests;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class GetTwinErrInjModuleJVMRunner extends GetTwinErrInjTests
{
    static String[] devicesToDeleteAfterTestClassFinishes;
    static String[][] modulesToDeleteAfterTestClassFinishes;

    public GetTwinErrInjModuleJVMRunner(String deviceId, String moduleId, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        super(deviceId, moduleId, protocol, authenticationType, clientType, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{2} with {3} auth using {4}")
    public static Collection inputs() throws IOException, GeneralSecurityException
    {
        iotHubConnectionString = Tools.retrieveEnvironmentVariableValue(TestConstants.IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME);
        X509Cert cert = new X509Cert(0,false, "TestLeaf", "TestRoot");
        String privateKey =  cert.getPrivateKeyLeafPem();
        String publicKeyCert = cert.getPublicCertLeafPem();
        String x509Thumbprint = cert.getThumbPrintLeaf();
        Collection inputs = inputsCommon(ClientType.MODULE_CLIENT, publicKeyCert, privateKey, x509Thumbprint);
        devicesToDeleteAfterTestClassFinishes = (String[])((Object[])inputs.toArray()[0])[5];
        modulesToDeleteAfterTestClassFinishes = (String[][])((Object[])inputs.toArray()[0])[6];
        return inputs;
    }

    @AfterClass
    public static void cleanUpResources()
    {
        tearDown(devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes);
    }
}
