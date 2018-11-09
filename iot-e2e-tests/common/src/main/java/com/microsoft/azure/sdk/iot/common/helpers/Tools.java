/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package com.microsoft.azure.sdk.iot.common.helpers;

import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;

import static org.junit.Assert.fail;

public class Tools
{
    public static String retrieveEnvironmentVariableValue(String environmentVariableName)
    {
        String environmentVariableValue = System.getenv().get(environmentVariableName);
        if ((environmentVariableValue == null) || environmentVariableValue.isEmpty())
        {
            environmentVariableValue = System.getProperty(environmentVariableName);
            if (environmentVariableValue == null || environmentVariableValue.isEmpty())
            {
                throw new IllegalArgumentException("Environment variable is not set: " + environmentVariableName);
            }
        }

        return environmentVariableValue;
    }

    /**
     * Checks if the provided exception contains a certain type of exception in its cause chain
     * @param possibleExceptionCause the type of exception to be searched for
     * @param exceptionToSearch the exception to search the stacktrace of
     * @return if any variant of the possibleExceptionCause is found at any depth of the exception cause chain
     */
    public static boolean isCause(Class<? extends Throwable> possibleExceptionCause, Throwable exceptionToSearch)
    {
        return possibleExceptionCause.isInstance(exceptionToSearch) || (exceptionToSearch != null && isCause(possibleExceptionCause, exceptionToSearch.getCause()));
    }


    /**
     * Uses the provided registry manager to delete all the devices and modules specified in the arguments
     * @param registryManager the registry manager to use. Will not be closed after this call
     * @param deviceIdsToDispose the list of device ids to delete
     * @param moduleIdsToDispose the list of pairs of device id and module id ie: {{deviceForModule1, Module1}, {deviceForModule2, Module2}, etc...}
     * @throws IOException if deleting the device or module fails
     * @throws IotHubException if deleting the device or module fails
     */
    public static void removeDevicesAndModules(RegistryManager registryManager, String[] deviceIdsToDispose, String[][] moduleIdsToDispose)
    {
        if (moduleIdsToDispose != null && moduleIdsToDispose.length > 0)
        {
            try
            {
                for (int i = 0; i < moduleIdsToDispose.length; i++)
                {
                    registryManager.removeModule(moduleIdsToDispose[i][0], moduleIdsToDispose[i][1]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Failed to remove modules: " + e.getMessage());
            }
        }

        if (deviceIdsToDispose != null && deviceIdsToDispose.length > 0)
        {
            try
            {
                for (int i = 0; i < deviceIdsToDispose.length; i++)
                {
                    registryManager.removeDevice(deviceIdsToDispose[i]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Failed to remove devices: " + e.getMessage());
            }
        }
    }
}