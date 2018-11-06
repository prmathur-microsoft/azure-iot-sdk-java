/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package tests.integration.com.microsoft.azure.sdk.iot.iothubservices.Twin;

import com.microsoft.azure.sdk.iot.common.helpers.TestConstants;
import com.microsoft.azure.sdk.iot.common.helpers.Tools;
import com.microsoft.azure.sdk.iot.common.tests.iothubservices.DeviceTwinWithVersionTests;
import org.junit.BeforeClass;

import java.io.IOException;

public class DeviceTwinWithVersionIT extends DeviceTwinWithVersionTests
{
    @BeforeClass
    public static void setup() throws IOException
    {
        iotHubConnectionString = Tools.retrieveEnvironmentVariableValue(TestConstants.IOT_HUB_CONNECTION_STRING_ENV_VAR_NAME);
        DeviceTwinWithVersionTests.setUp();
    }
}