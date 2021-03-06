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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
public class SendMessageService102 {

    @Value("${app.l102.queue.name1}")
    public String sendQueue;

/*    @Value("${app.l102.topic.name1}")
    public String sendTopic;*/

    final private JmsTemplate myPutGetTemplate;
    final private JmsTemplate myPubSubTemplate;

    SendMessageService102(JmsTemplate myPutGetTemplate, JmsTemplate myPubSubTemplate) {
        this.myPutGetTemplate = myPutGetTemplate;
        this.myPubSubTemplate = myPubSubTemplate;
    }

    public void put(String msg) {
        myPutGetTemplate.convertAndSend(sendQueue, msg);
    }
/*    public void publish(String msg) {
        myPubSubTemplate.convertAndSend(sendTopic, msg);
    }*/

}



