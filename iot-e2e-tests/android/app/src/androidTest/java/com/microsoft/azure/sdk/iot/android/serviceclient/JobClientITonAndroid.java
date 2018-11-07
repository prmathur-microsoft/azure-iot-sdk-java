/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.android.serviceclient;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.common.helpers.DeviceTestManager;
import com.microsoft.azure.sdk.iot.common.serviceclient.JobClientCommon;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobClientITonAndroid extends JobClientCommon
{
    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    @BeforeClass
    public static void setUp() throws IOException, IotHubException, InterruptedException, URISyntaxException
    {
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        JobClientCommon.setUp();
    }
/*
    @Override
    @Test
    @Ignore
    public void scheduleUpdateTwinSucceed() throws IOException, IotHubException, InterruptedException
    {

    }
*/
    @Override
    @Test
    @Ignore
    public void scheduleDeviceMethodSucceed() throws IOException, IotHubException, InterruptedException
    {
    }

    @Override
    @Test
    @Ignore
    public void mixScheduleInFutureSucceed() throws IOException, IotHubException, InterruptedException
    {
    }

    @Override
    @Test
    public void cancelScheduleDeviceMethodSucceed() throws IOException, IotHubException, InterruptedException
    {

    }
}
