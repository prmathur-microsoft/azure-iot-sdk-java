/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.android.helper.Rerun;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.TransportClientCommon;

import org.junit.BeforeClass;
import org.junit.Rule;

public class TransportClientITonAndroid extends TransportClientCommon
{
    @Rule
    public Rerun count = new Rerun(3);

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    @BeforeClass
    public static void setup() throws Exception
    {
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        TransportClientCommon.setUpCommon();
    }
}
