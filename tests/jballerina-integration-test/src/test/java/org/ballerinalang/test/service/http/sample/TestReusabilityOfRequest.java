/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.test.service.http.sample;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.ballerinalang.test.service.http.HttpBaseTest;
import org.ballerinalang.test.util.HttpClientRequest;
import org.ballerinalang.test.util.HttpResponse;
import org.ballerinalang.test.util.TestConstant;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test reusability of requests.
 *
 * @since 0.970.0
 */
@Test(groups = "http-test")
public class TestReusabilityOfRequest extends HttpBaseTest {

    private final int servicePort = 9115;

    @Test
    public void reuseRequestWithoutEntity() throws IOException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(servicePort,
                "reuseObj/request_without_entity"));
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()),
                TestConstant.CONTENT_TYPE_TEXT_PLAIN, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), "Hello from GET!Hello from GET!",
                "Message content mismatched");
    }

    @Test
    public void reuseRequestWithEmptyEntity() throws IOException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(servicePort,
                "reuseObj/request_with_empty_entity"));
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()),
                TestConstant.CONTENT_TYPE_TEXT_PLAIN, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), "Hello from GET!Hello from GET!",
                "Message content mismatched");
    }

    @Test
    public void twoRequestsSameEntity() throws IOException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(servicePort,
                "reuseObj/two_request_same_entity"));
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()),
                TestConstant.CONTENT_TYPE_TEXT_PLAIN, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), "Hello from GET!Hello from GET!",
                "Message content mismatched");
    }

    @Test
    public void sameRequestWithADatasource() throws IOException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(servicePort,
                "reuseObj/request_with_datasource"));
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()),
                TestConstant.CONTENT_TYPE_TEXT_PLAIN, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), "Hello from POST!Hello from POST!",
                "Message content mismatched");
    }

    @Test
    public void sameRequestWithByteChannel() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain");
        HttpResponse response = HttpClientRequest.doPost(serverInstance.getServiceURLHttp(servicePort,
                "reuseObj/request_with_bytechannel"), "Hello from POST!", headers);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.CONTENT_TYPE.toString()),
                TestConstant.CONTENT_TYPE_TEXT_PLAIN, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), "Hello from POST!No payload", "Message content mismatched");
    }
}
