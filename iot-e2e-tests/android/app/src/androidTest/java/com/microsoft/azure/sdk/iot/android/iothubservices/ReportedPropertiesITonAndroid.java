/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.android.helper.Rerun;
import com.microsoft.azure.sdk.iot.common.helpers.ClientType;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.ReportedPropertiesTests;
import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import static junit.framework.Assert.fail;

@RunWith(Parameterized.class)
public class ReportedPropertiesITonAndroid extends ReportedPropertiesTests
{
    static String[] devicesToDeleteAfterTestClassFinishes;
    static String[][] modulesToDeleteAfterTestClassFinishes;

    @Rule
    public Rerun count = new Rerun(3);

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    public ReportedPropertiesITonAndroid(String deviceId, String moduleId, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, String[] devicesToDeleteAfterTestClassFinishes, String[][] modulesToDeleteAfterTestClassFinishes, String publicKeyCert, String privateKey, String x509Thumbprint)
    {
        super(deviceId, moduleId, protocol, authenticationType, clientType, devicesToDeleteAfterTestClassFinishes, modulesToDeleteAfterTestClassFinishes, publicKeyCert, privateKey, x509Thumbprint);
    }

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{2}_{3}_{4}")
    public static Collection inputsCommons() throws IOException, GeneralSecurityException
    {
        String privateKeyBase64Encoded = BuildConfig.IotHubPrivateKeyBase64;
        String publicKeyCertBase64Encoded = BuildConfig.IotHubPublicCertBase64;
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        String x509Thumbprint = BuildConfig.IotHubThumbprint;
        String privateKey = new String(Base64.decodeBase64Local(privateKeyBase64Encoded.getBytes()));
        String publicKeyCert = new String(Base64.decodeBase64Local(publicKeyCertBase64Encoded.getBytes()));
        Collection inputs = inputsCommon(ClientType.DEVICE_CLIENT, publicKeyCert, privateKey, x509Thumbprint);

        devicesToDeleteAfterTestClassFinishes = (String[])((Object[])inputs.toArray()[0])[5];
        modulesToDeleteAfterTestClassFinishes = (String[][])((Object[])inputs.toArray()[0])[6];

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
