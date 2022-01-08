/*
 * (c) Copyright IBM Corporation 2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.mq.samples.jms.spring.level102;

import com.ibm.mq.samples.jms.spring.globals.handlers.OurDestinationResolver;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
public class MQConfiguration102 {
    protected final Log logger = LogFactory.getLog(getClass());

    private static final String HOST = "abdf-qm-c91e.qm.au-syd.mq.appdomain.cloud"; // Host name or IP address
    private static final int PORT = 31284; // Listener port for your queue manager
    private static final String CHANNEL = "CLOUD.APP.SVRCONN"; // Channel name
    private static final String QMGR = "ABDF_QM"; // Queue manager name
    private static final String APP_USER = "jmstest"; // User name that application uses to connect to MQ
    private static final String APP_PASSWORD = "OUNIrfe7xuCW0TAphZiezbhBeTLUbIfS_-iS1zkbF-EA"; // Password that the application uses to connect to MQ
    private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from

    @Value("${spring.jms.pub-sub-domain:false}")
    public Boolean pubsub;

    @Value("${ssl.keystore.password}")
    public String keystorePassword;

    @Value("${ssl.truststore.password}")
    public String truststorePassword;

    @PostConstruct
    public void loadSystemProperties() {
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
    }


    @Bean("connectionFactory")
    public ConnectionFactory myConnectionFactory() {
        ConnectionFactory factory = null;

        try {
        // Create a connection factory
        JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
        JmsConnectionFactory cf = ff.createConnectionFactory();

        // Set the properties
        cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
        cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
        cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
        cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
        cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "jmstest");
        cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12ORHIGHER");
        cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        cf.setStringProperty(WMQConstants.USERID, APP_USER);
        cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);

        factory = cf;

        } catch (JMSException jmsex) {
            logger.error(jmsex);
        }

        return factory;
    }

    @Bean("myPubSubTemplate")
    public JmsTemplate myPubSubJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(myConnectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean("myPutGetTemplate")
    public JmsTemplate myPutGetTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(myConnectionFactory());
        jmsTemplate.setPubSubDomain(false);
        return jmsTemplate;
    }


    @Bean
    public JmsListenerContainerFactory<?> mySubContainerFactory102() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(myConnectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> myGetContainerFactory102() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(myConnectionFactory());
        factory.setPubSubDomain(false);
        return factory;
    }

    // Need this bean to override default allowing Level 101 components to work in conjunction
    // with subsequent components
    @Bean("jmsTemplate")
    public JmsTemplate jmsTemplate() {
        //return new JmsTemplate(connectionFactory);
        JmsTemplate jmsTemplate = new JmsTemplate(myConnectionFactory());
        if (pubsub) {
            jmsTemplate.setPubSubDomain(true);
        } else {
            jmsTemplate.setPubSubDomain(false);
        }
        return jmsTemplate;
    }

}
