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
package com.github.ljtfreitas.restify.http.client.request.interceptor.gzip;

import java.net.URI;
import java.nio.charset.Charset;

import com.github.ljtfreitas.restify.http.client.HttpClientException;
import com.github.ljtfreitas.restify.http.client.message.Header;
import com.github.ljtfreitas.restify.http.client.message.Headers;
import com.github.ljtfreitas.restify.http.client.message.request.HttpRequestBody;
import com.github.ljtfreitas.restify.http.client.message.request.HttpRequestMessage;
import com.github.ljtfreitas.restify.http.client.request.HttpClientRequest;
import com.github.ljtfreitas.restify.http.client.request.interceptor.HttpClientRequestInterceptor;
import com.github.ljtfreitas.restify.http.client.response.HttpClientResponse;

public class GzipHttpClientRequestInterceptor implements HttpClientRequestInterceptor {

	private final boolean applyToRequest;
	private final boolean applyToResponse;

	public GzipHttpClientRequestInterceptor() {
		this(false, true);
	}

	private GzipHttpClientRequestInterceptor(boolean request, boolean response) {
		this.applyToRequest = request;
		this.applyToResponse = response;
	}

	@Override
	public HttpClientRequest intercepts(HttpClientRequest request) {
		return applyToRequest || applyToResponse ? new GzipHttpClientRequest(request) : request;
	}

	private class GzipHttpClientRequest implements HttpClientRequest {

		private final HttpClientRequest source;
		private final HttpRequestBody body;

		private GzipHttpClientRequest(HttpClientRequest source) {
			this.source = source;
			this.body = applyToRequest ? new GzipHttpRequestBody(source.body()) : source.body();
		}

		@Override
		public URI uri() {
			return source.uri();
		}

		@Override
		public String method() {
			return source.method();
		}

		@Override
		public HttpRequestBody body() {
			return body;
		}

		@Override
		public Charset charset() {
			return source.charset();
		}

		@Override
		public HttpRequestMessage replace(Header header) {
			return source.replace(header);
		}

		@Override
		public Headers headers() {
			return source.headers();
		}

		@Override
		public HttpClientResponse execute() throws HttpClientException {
			Gzip gzip = new Gzip();

			HttpClientResponse response = applyToRequest ? gzip.applyTo(source).execute() : source.execute();

			return applyToResponse ? gzip.applyTo(response) : response;
		}
	}

	public static class Builder {

		private final GzipHttpClientEncodingBuilder encoding = new GzipHttpClientEncodingBuilder();

		public GzipHttpClientEncodingBuilder encoding() {
			return encoding;
		}

		public GzipHttpClientRequestInterceptor build() {
			return new GzipHttpClientRequestInterceptor(encoding.request, encoding.response);
		}

		public class GzipHttpClientEncodingBuilder {

			private boolean request = false;
			private boolean response = true;

			public GzipHttpClientEncodingBuilder request() {
				this.request = true;
				return this;
			}

			public GzipHttpClientEncodingBuilder request(boolean enabled) {
				this.request = enabled;
				return this;
			}

			public GzipHttpClientEncodingBuilder response() {
				this.response = true;
				return this;
			}

			public GzipHttpClientEncodingBuilder response(boolean enabled) {
				this.response = enabled;
				return this;
			}

			public Builder both() {
				this.request = true;
				this.response = true;
				return Builder.this;
			}

			public GzipHttpClientRequestInterceptor build() {
				return Builder.this.build();
			}
		}
	}
}
