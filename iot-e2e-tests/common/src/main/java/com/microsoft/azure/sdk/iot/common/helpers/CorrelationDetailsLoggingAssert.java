/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.common.helpers;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.internal.ExactComparisonCriteria;
import org.junit.internal.InexactComparisonCriteria;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CorrelationDetailsLoggingAssert
{
    String hostname;
    String deviceId;
    String protocol;
    String moduleId;

    public CorrelationDetailsLoggingAssert(String hostname, String deviceId, String protocol, String moduleId)
    {
        this.hostname = hostname;
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.moduleId = moduleId;
    }

    private String buildExceptionMessage(String baseMessage)
    {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String correlationString = ": Correlation details : Hostname:" + hostname + " Device id: " + deviceId;

        if (moduleId != null && !moduleId.isEmpty())
        {
            correlationString = correlationString + " Module id: " + moduleId;
        }

        correlationString = correlationString + " Protocol: " + protocol + " Timestamp: " + timeStamp;

        return baseMessage + correlationString;
    }

    public void assertTrue(String message, boolean condition)
    {
        if (!condition)
        {
            fail(message);
        }
    }

    public void assertTrue(boolean condition)
    {
        assertTrue(null, condition);
    }

    public void assertFalse(String message, boolean condition)
    {
        assertTrue(message, !condition);
    }

    public void assertFalse(boolean condition)
    {
        assertFalse(null, condition);
    }

    public void fail(String message)
    {
        if (message == null)
        {
            throw new AssertionError(buildExceptionMessage(""));
        }

        throw new AssertionError(buildExceptionMessage(message));
    }

    public void fail() {
        fail(null);
    }

    public void assertEquals(String message, Object expected, Object actual)
    {
        if (equalsRegardingNull(expected, actual))
        {
            return;
        }
        else if (expected instanceof String && actual instanceof String)
        {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, (String) expected, (String) actual);
        }
        else
        {
            failNotEquals(message, expected, actual);
        }
    }

    public void assertEquals(Object expected, Object actual)
    {
        assertEquals(null, expected, actual);
    }

    private boolean equalsRegardingNull(Object expected, Object actual)
    {
        if (expected == null)
        {
            return actual == null;
        }

        return isEquals(expected, actual);
    }

    private void failNotEquals(String message, Object expected, Object actual)
    {
        fail(format(message, expected, actual));
    }

    String format(String message, Object expected, Object actual)
    {
        String formatted = "";
        if (message != null && !message.equals(""))
        {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString))
        {
            return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString);
        }
        else
        {
            return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private String formatClassAndValue(Object value, String valueString)
    {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    private boolean isEquals(Object expected, Object actual)
    {
        return expected.equals(actual);
    }
}
