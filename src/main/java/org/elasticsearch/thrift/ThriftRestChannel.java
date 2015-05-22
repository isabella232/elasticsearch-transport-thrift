package org.elasticsearch.thrift;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.elasticsearch.common.netty.handler.codec.http.HttpHeaders;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 */
public class ThriftRestChannel extends org.elasticsearch.rest.RestChannel {
    private CountDownLatch latch;
    private AtomicReference<org.elasticsearch.thrift.RestResponse> ref;

    public ThriftRestChannel(org.elasticsearch.rest.RestRequest request, CountDownLatch latch, AtomicReference<org.elasticsearch.thrift.RestResponse> ref) {
        super(request, true);
        this.latch = latch;
        this.ref = ref;
    }

    @Override
    public void sendResponse(RestResponse response) {
        try {
            ref.set(convert(response));
        } catch (IOException e) {
            // ignore, should not happen...
        }
        latch.countDown();
    }

    private org.elasticsearch.thrift.RestResponse convert(org.elasticsearch.rest.RestResponse response) throws IOException {
        org.elasticsearch.thrift.RestResponse tResponse = new org.elasticsearch.thrift.RestResponse(getStatus(response.status()));
        
        int contentLength = response.content().length();
        if (contentLength > 0) {
            // TODO: do we always need a copy?
            // there was an optimization previously, but it did not compile, so it was killed without mercy.
            tResponse.setBody(ByteBuffer.wrap(response.content().copyBytesArray().toBytes(), 0, contentLength));
            tResponse.putToHeaders(HttpHeaders.Names.CONTENT_TYPE, response.contentType());
        }
        return tResponse;
    }

    private Status getStatus(RestStatus status) {
        switch (status) {
            case CONTINUE:
                return Status.CONT;
            case SWITCHING_PROTOCOLS:
                return Status.SWITCHING_PROTOCOLS;
            case OK:
                return Status.OK;
            case CREATED:
                return Status.CREATED;
            case ACCEPTED:
                return Status.ACCEPTED;
            case NON_AUTHORITATIVE_INFORMATION:
                return Status.NON_AUTHORITATIVE_INFORMATION;
            case NO_CONTENT:
                return Status.NO_CONTENT;
            case RESET_CONTENT:
                return Status.RESET_CONTENT;
            case PARTIAL_CONTENT:
                return Status.PARTIAL_CONTENT;
            case MULTI_STATUS:
                // no status for this??
                return Status.INTERNAL_SERVER_ERROR;
            case MULTIPLE_CHOICES:
                return Status.MULTIPLE_CHOICES;
            case MOVED_PERMANENTLY:
                return Status.MOVED_PERMANENTLY;
            case FOUND:
                return Status.FOUND;
            case SEE_OTHER:
                return Status.SEE_OTHER;
            case NOT_MODIFIED:
                return Status.NOT_MODIFIED;
            case USE_PROXY:
                return Status.USE_PROXY;
            case TEMPORARY_REDIRECT:
                return Status.TEMPORARY_REDIRECT;
            case BAD_REQUEST:
                return Status.BAD_REQUEST;
            case UNAUTHORIZED:
                return Status.UNAUTHORIZED;
            case PAYMENT_REQUIRED:
                return Status.PAYMENT_REQUIRED;
            case FORBIDDEN:
                return Status.FORBIDDEN;
            case NOT_FOUND:
                return Status.NOT_FOUND;
            case METHOD_NOT_ALLOWED:
                return Status.METHOD_NOT_ALLOWED;
            case NOT_ACCEPTABLE:
                return Status.NOT_ACCEPTABLE;
            case PROXY_AUTHENTICATION:
                return Status.INTERNAL_SERVER_ERROR;
            case REQUEST_TIMEOUT:
                return Status.REQUEST_TIMEOUT;
            case CONFLICT:
                return Status.CONFLICT;
            case GONE:
                return Status.GONE;
            case LENGTH_REQUIRED:
                return Status.LENGTH_REQUIRED;
            case PRECONDITION_FAILED:
                return Status.PRECONDITION_FAILED;
            case REQUEST_ENTITY_TOO_LARGE:
                return Status.REQUEST_ENTITY_TOO_LARGE;
            case REQUEST_URI_TOO_LONG:
                return Status.REQUEST_URI_TOO_LONG;
            case UNSUPPORTED_MEDIA_TYPE:
                return Status.UNSUPPORTED_MEDIA_TYPE;
            case REQUESTED_RANGE_NOT_SATISFIED:
                return Status.INTERNAL_SERVER_ERROR;
            case EXPECTATION_FAILED:
                return Status.EXPECTATION_FAILED;
            case UNPROCESSABLE_ENTITY:
                return Status.BAD_REQUEST;
            case LOCKED:
                return Status.BAD_REQUEST;
            case FAILED_DEPENDENCY:
                return Status.BAD_REQUEST;
            case INTERNAL_SERVER_ERROR:
                return Status.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED:
                return Status.NOT_IMPLEMENTED;
            case BAD_GATEWAY:
                return Status.BAD_GATEWAY;
            case SERVICE_UNAVAILABLE:
                return Status.SERVICE_UNAVAILABLE;
            case GATEWAY_TIMEOUT:
                return Status.GATEWAY_TIMEOUT;
            case HTTP_VERSION_NOT_SUPPORTED:
                return Status.INTERNAL_SERVER_ERROR;
            default:
                return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
