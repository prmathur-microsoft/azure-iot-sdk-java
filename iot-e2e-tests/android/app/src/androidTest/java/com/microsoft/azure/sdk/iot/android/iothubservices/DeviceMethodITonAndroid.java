/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.android.helper.Rerun;
import com.microsoft.azure.sdk.iot.common.helpers.DeviceTestManager;
import com.microsoft.azure.sdk.iot.common.iothubservices.DeviceMethodCommon;
import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.exceptions.ModuleClientException;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.Module;
import com.microsoft.azure.sdk.iot.service.auth.AuthenticationType;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

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
public class DeviceMethodITonAndroid extends DeviceMethodCommon
{
    @Rule
    public Rerun count = new Rerun(3);

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    //This function is run before even the @BeforeClass annotation, so it is used as the @BeforeClass method
    @Parameterized.Parameters(name = "{1}_{2}_{3}")
    public static Collection inputsCommons() throws IOException, IotHubException, GeneralSecurityException, URISyntaxException, InterruptedException, ModuleClientException
    {
        String privateKeyBase64Encoded = BuildConfig.IotHubPrivateKeyBase64;
        String publicKeyCertBase64Encoded = BuildConfig.IotHubPublicCertBase64;
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        x509Thumbprint = BuildConfig.IotHubThumbprint;
        privateKey = new String(Base64.decodeBase64Local(privateKeyBase64Encoded.getBytes()));
        publicKeyCert = new String(Base64.decodeBase64Local(publicKeyCertBase64Encoded.getBytes()));
        includeModuleClientTest = false;

        return DeviceMethodCommon.inputsCommon();
    }

    public DeviceMethodITonAndroid(DeviceTestManager deviceTestManager, IotHubClientProtocol protocol, AuthenticationType authenticationType, String clientType, Device device, Module module)
    {
        super(deviceTestManager, protocol, authenticationType, clientType, device, module);
    }

//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromTcpConnectionDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsConnectionDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsSessionDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsCBSReqLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsCBSRespLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsD2CLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsC2DLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsMethodReqLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsMethodRespLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsTwinReqLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromAmqpsTwinRespLinkDrop() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromGracefulShutdownAmqp() throws Exception
//    {
//    }
//
//    @Ignore
//    @Override
//    @Test
//    public void invokeMethodRecoveredFromGracefulShutdownMqtt() throws Exception
//    {
//    }
}
