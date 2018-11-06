/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package tests.integration.com.microsoft.azure.sdk.iot.serviceclient;

import com.microsoft.azure.sdk.iot.common.TestConstants;
import com.microsoft.azure.sdk.iot.common.helpers.Tools;
import com.microsoft.azure.sdk.iot.common.serviceclient.JobClientCommon;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;

public class JobClientIT extends JobClientCommon
{
    @BeforeClass
    public static void setUp() throws IOException, IotHubException, InterruptedException, URISyntaxException
    {
        iotHubConnectionString = Tools.retrieveEnvironmentVariableValue(TestConstants.IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME);
        JobClientCommon.setUp();
    }
}
