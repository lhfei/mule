/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.ftp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mule.api.retry.RetryContext;
import org.mule.api.retry.RetryNotifier;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.api.transport.Connector;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.probe.JUnitProbe;
import org.mule.tck.probe.PollingProber;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.Rule;
import org.junit.Test;


public class FtpReconnectionNotifierTestCase extends FunctionalTestCase
{

    private final Integer RECONNECTION_ATTEMPTS = 2;
    private final Integer POLLING_FREQUENCY = 100;

    @Rule
    public SystemProperty countSystemProperty = new SystemProperty("count", RECONNECTION_ATTEMPTS.toString());

    @Rule
    public SystemProperty pollingFrequencySystemProperty = new SystemProperty("pollingFrequency", POLLING_FREQUENCY.toString());

    private TestReconnectNotifier mockNotifier = null;


    protected String getConfigFile()
    {
        return "ftp-reconnection-notifier-fails-config.xml";
    }

    @Test
    public void testNotificationOnReconnectionAttemptFailure() throws Exception
    {
        mockNotifier = mock(TestReconnectNotifier.class);
        Connector c = muleContext.getRegistry().lookupConnector("FTP");
        assertNotNull(c);
        RetryPolicyTemplate rpf = c.getRetryPolicyTemplate();
        assertNotNull(rpf);
        rpf.setNotifier(mockNotifier);
        MuleFtpServer server = new MuleFtpServer(2221, 1);
        server.start();
        testServerConnected();
        server.stop();
        testServerDisconnected();
    }

    private void verifyOnSuccessNotification()
    {
        PollingProber pollingProber = new PollingProber(POLLING_FREQUENCY * 5, POLLING_FREQUENCY);
        pollingProber.check(new JUnitProbe()
        {
            @Override
            protected boolean test() throws Exception
            {
                verify(mockNotifier, times(1)).onSuccess(any(RetryContext.class));
                return true;
            }

            @Override
            public String describeFailure()
            {
                return "Success notification wasn't triggered";
            }
        });
    }

    private void verifyOnFailureNotification()
    {
        PollingProber pollingProber = new PollingProber(POLLING_FREQUENCY * 5, POLLING_FREQUENCY);
        pollingProber.check(new JUnitProbe()
        {
            @Override
            protected boolean test() throws Exception
            {
                verify(mockNotifier, atLeast(1)).onFailure(any(RetryContext.class), any(Throwable.class));
                return true;
            }

            @Override
            public String describeFailure()
            {
                return "Failure notification wasn't triggered";
            }
        });
    }

    private void testServerConnected() throws Exception
    {
        runFlow("main-test");
        verifyOnSuccessNotification();
    }

    private void testServerDisconnected()
    {
        try
        {
            runFlow("main-test");
        }
        catch (Exception e)
        {
            verifyOnFailureNotification();
        }
    }


    public static class MuleFtpServer
    {
        private FtpServer server;
        FtpServerFactory serverFactory;
        ListenerFactory factory;
        public MuleFtpServer(int port, long delay)
        {

            serverFactory = new FtpServerFactory();
            factory = new ListenerFactory();
            factory.setPort(port);
            serverFactory.addListener("default", factory.createListener());
            serverFactory.addListener("default", factory.createListener());
            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            serverFactory.setUserManager(userManagerFactory.createUserManager());
            BaseUser user = new BaseUser();
            user.setName("mule-test");
            user.setPassword("mule-test");
            String currentDir = System.getProperty("user.dir");
            user.setHomeDirectory(currentDir);
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            user.setAuthorities(authorities);
            try
            {
                serverFactory.getUserManager().save(user);
            }
            catch (FtpException e)
            {
                e.printStackTrace();
            }
            server = serverFactory.createServer();
        }

        public void start()
        {
            try
            {
                server.start();
            }
            catch (FtpException e)
            {
                e.printStackTrace();
            }
        }

        public void stop()
        {
            try
            {
                server.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    public static class TestReconnectNotifier implements RetryNotifier
    {

        public static int fails = 0;
        public static int successes = 0;

        @Override
        public void onFailure(RetryContext retryContext, Throwable throwable)
        {
            fails++;
        }

        @Override
        public void onSuccess(RetryContext retryContext)
        {
            successes++;
        }

    }


}

