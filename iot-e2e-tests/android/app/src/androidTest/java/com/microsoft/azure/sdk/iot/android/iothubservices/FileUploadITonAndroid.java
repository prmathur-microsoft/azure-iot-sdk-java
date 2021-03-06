/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.android.iothubservices;

import com.microsoft.appcenter.espresso.Factory;
import com.microsoft.appcenter.espresso.ReportHelper;
import com.microsoft.azure.sdk.iot.android.BuildConfig;
import com.microsoft.azure.sdk.iot.android.helper.Rerun;
import com.microsoft.azure.sdk.iot.common.iothubservices.FileUploadCommon;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class FileUploadITonAndroid extends FileUploadCommon
{
    @Rule
    public Rerun count = new Rerun(3);

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    @BeforeClass
    public static void setup() throws IOException
    {
        iotHubConnectionString = BuildConfig.IotHubConnectionString;
        FileUploadCommon.setUp();
    }

    @Ignore
    @Override
    @Test
    public void uploadToBlobAsyncSingleFileZeroLength()
    {
    }
}
