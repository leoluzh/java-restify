/*******************************************************************************
 *
 * MIT License
 *
 * Copyright (c) 2016 Tiago de Freitas Lima
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *******************************************************************************/
package com.github.ljtfreitas.restify.http.netflix.client.request.ribbon.async;

import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;

import com.github.ljtfreitas.restify.http.client.HttpClientException;
import com.github.ljtfreitas.restify.http.client.message.Header;
import com.github.ljtfreitas.restify.http.client.message.Headers;
import com.github.ljtfreitas.restify.http.client.message.request.BufferedByteArrayHttpRequestBody;
import com.github.ljtfreitas.restify.http.client.message.request.HttpRequestBody;
import com.github.ljtfreitas.restify.http.client.message.request.HttpRequestMessage;
import com.github.ljtfreitas.restify.http.client.message.request.StreamableHttpRequestBody;
import com.github.ljtfreitas.restify.http.client.request.EndpointRequest;
import com.github.ljtfreitas.restify.http.client.request.HttpClientRequest;
import com.github.ljtfreitas.restify.http.client.request.async.AsyncHttpClientRequest;
import com.github.ljtfreitas.restify.http.client.response.HttpClientResponse;
import com.github.ljtfreitas.restify.http.netflix.client.request.ribbon.BaseRibbonHttpClientRequest;
import com.github.ljtfreitas.restify.http.netflix.client.request.ribbon.RibbonHttpClientRequest;
import com.github.ljtfreitas.restify.http.netflix.client.request.ribbon.RibbonRequest;
import com.github.ljtfreitas.restify.http.netflix.client.request.ribbon.RibbonResponse;
import com.github.ljtfreitas.restify.util.Try;

public class AsyncRibbonHttpClientRequest extends BaseRibbonHttpClientRequest implements AsyncHttpClientRequest {

	private final EndpointRequest endpointRequest;
	private final AsyncRibbonLoadBalancedClient ribbonLoadBalancedClient;
	private final Charset charset;
	private final StreamableHttpRequestBody body;

	public AsyncRibbonHttpClientRequest(EndpointRequest endpointRequest, AsyncRibbonLoadBalancedClient ribbonLoadBalancedClient, Charset charset) {
		this(endpointRequest, ribbonLoadBalancedClient, charset, new BufferedByteArrayHttpRequestBody(charset));
	}

	private AsyncRibbonHttpClientRequest(EndpointRequest endpointRequest, AsyncRibbonLoadBalancedClient ribbonLoadBalancedClient, Charset charset,
			StreamableHttpRequestBody body) {
		super(endpointRequest);
		this.endpointRequest = endpointRequest;
		this.ribbonLoadBalancedClient = ribbonLoadBalancedClient;
		this.charset = charset;
		this.body = body;
	}

	@Override
	public URI uri() {
		return endpointRequest.endpoint();
	}

	@Override
	public String method() {
		return endpointRequest.method();
	}

	@Override
	public HttpRequestBody body() {
		return body;
	}

	@Override
	public Headers headers() {
		return endpointRequest.headers();
	}

	@Override
	public HttpRequestMessage replace(Header header) {
		return new AsyncRibbonHttpClientRequest(endpointRequest.replace(header), ribbonLoadBalancedClient, charset,
				body);
	}

	@Override
	public Charset charset() {
		return charset;
	}

	@Override
	public void writeTo(HttpClientRequest httpRequestMessage) {
		endpointRequest.body()
			.ifPresent(b -> {
				Try.withResources(httpRequestMessage.body()::output)
					.apply(o -> body.writeTo(o))
					.apply(OutputStream::flush);
			});
	}

	@Override
	public RibbonHttpClientRequest replace(URI ribbonEndpoint) {
		return new AsyncRibbonHttpClientRequest(endpointRequest.replace(ribbonEndpoint), ribbonLoadBalancedClient, charset,
				body);
	}

	@Override
	public CompletionStage<HttpClientResponse> executeAsync() throws HttpClientException {
		return ribbonLoadBalancedClient.executeAsync(new RibbonRequest(this))
			.thenApplyAsync(RibbonResponse::unwrap);
	}
}
