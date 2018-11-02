package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.ReceiveMessagesTests;
import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.InternalClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.common.helpers.ClientType;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ReceiveMessagesITonAndroid extends ReceiveMessagesTests
{
    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{1}_{4}_{5}")
    public static Collection inputs() throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, ModuleClientException
    {
        String privateKeyBase64Encoded = BuildConfig.IotHubPrivateKeyBase64;
        String publicKeyCertBase64Encoded = BuildConfig.IotHubPublicCertBase64;
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        x509Thumbprint = BuildConfig.IotHubThumbprint;
        privateKey = new String(Base64.decodeBase64Local(privateKeyBase64Encoded.getBytes()));
        publicKeyCert = new String(Base64.decodeBase64Local(publicKeyCertBase64Encoded.getBytes()));

        return ReceiveMessagesTests.inputsCommon(ClientType.DEVICE_CLIENT);
    }

    public ReceiveMessagesITonAndroid(InternalClient client, IotHubClientProtocol protocol, Device device, Module module, AuthenticationType authenticationType, String clientType, String uuid)
    {
        super(client, protocol, device, module, authenticationType, clientType, uuid);
    }
}
