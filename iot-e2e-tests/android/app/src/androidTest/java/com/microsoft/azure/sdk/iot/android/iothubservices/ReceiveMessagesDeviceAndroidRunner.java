package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.common.helpers.ClientType;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.ReceiveMessagesTests;
import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.InternalClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import static junit.framework.Assert.fail;

@RunWith(Parameterized.class)
public class ReceiveMessagesDeviceAndroidRunner extends ReceiveMessagesTests
{
    static String[] devicesToDeleteAfterTestClassFinishes;
    static String[][] modulesToDeleteAfterTestClassFinishes;

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    public ReceiveMessagesDeviceAndroidRunner(InternalClient client, IotHubClientProtocol protocol, Device device, Module module, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        super(client, protocol, device, module, authenticationType, clientType, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{1}_{4}_{5}")
    public static Collection inputs() throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, ModuleClientException, InterruptedException {
        String privateKeyBase64Encoded = BuildConfig.IotHubPrivateKeyBase64;
        String publicKeyCertBase64Encoded = BuildConfig.IotHubPublicCertBase64;
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        String x509Thumbprint = BuildConfig.IotHubThumbprint;
        String privateKey = new String(Base64.decodeBase64Local(privateKeyBase64Encoded.getBytes()));
        String publicKeyCert = new String(Base64.decodeBase64Local(publicKeyCertBase64Encoded.getBytes()));

        Collection inputs = inputsCommon(ClientType.DEVICE_CLIENT, publicKeyCert, privateKey, x509Thumbprint);

        devicesToDeleteAfterTestClassFinishes = (String[])((Object[])inputs.toArray()[0])[6];
        modulesToDeleteAfterTestClassFinishes = (String[][])((Object[])inputs.toArray()[0])[7];

        return inputs;
    }

    @AfterClass
    public static void cleanUpResources()
    {
        try
        {
            tearDown(devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Failed to clean up resources after class ran");
        }
    }
}
