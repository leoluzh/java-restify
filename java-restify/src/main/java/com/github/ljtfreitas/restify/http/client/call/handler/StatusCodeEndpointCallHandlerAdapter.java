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
package com.github.ljtfreitas.restify.http.client.call.handler;

import com.github.ljtfreitas.restify.http.client.call.EndpointCall;
import com.github.ljtfreitas.restify.http.client.message.response.StatusCode;
import com.github.ljtfreitas.restify.http.client.response.EndpointResponse;
import com.github.ljtfreitas.restify.http.contract.metadata.EndpointMethod;
import com.github.ljtfreitas.restify.reflection.JavaType;
import com.github.ljtfreitas.restify.reflection.SimpleParameterizedType;

public class StatusCodeEndpointCallHandlerAdapter implements EndpointCallHandlerAdapter<StatusCode, EndpointResponse<Void>, Void> {

	private static final StatusCodeEndpointCallHandlerAdapter DEFAULT_INSTANCE = new StatusCodeEndpointCallHandlerAdapter();

	private static final JavaType DEFAULT_RETURN_TYPE = JavaType.of(new SimpleParameterizedType(EndpointResponse.class, null, Void.class));

	@Override
	public boolean supports(EndpointMethod endpointMethod) {
		return endpointMethod.returnType().is(StatusCode.class);
	}

	@Override
	public JavaType returnType(EndpointMethod endpointMethod) {
		return DEFAULT_RETURN_TYPE;
	}

	@Override
	public EndpointCallHandler<StatusCode, Void> adapt(EndpointMethod endpointMethod, EndpointCallHandler<EndpointResponse<Void>, Void> handler) {
		return new StatusCodeEndpointCallHandler(handler);
	}

	private class StatusCodeEndpointCallHandler implements EndpointCallHandler<StatusCode, Void> {

		private final EndpointCallHandler<EndpointResponse<Void>, Void> delegate;

		public StatusCodeEndpointCallHandler(EndpointCallHandler<EndpointResponse<Void>, Void> handler) {
			this.delegate = handler;
		}

		@Override
		public JavaType returnType() {
			return delegate.returnType();
		}

		@Override
		public StatusCode handle(EndpointCall<Void> call, Object[] args) {
			return delegate.handle(call, args).status();
		}
	}

	public static StatusCodeEndpointCallHandlerAdapter instance() {
		return DEFAULT_INSTANCE;
	}
}
