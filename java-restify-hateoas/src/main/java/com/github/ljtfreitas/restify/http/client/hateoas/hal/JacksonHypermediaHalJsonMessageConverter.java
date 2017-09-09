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
package com.github.ljtfreitas.restify.http.client.hateoas.hal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ljtfreitas.restify.http.client.hateoas.browser.LinkBrowser;
import com.github.ljtfreitas.restify.http.client.hateoas.browser.LinkBrowserBuilder;
import com.github.ljtfreitas.restify.http.client.message.converter.json.JacksonMessageConverter;
import com.github.ljtfreitas.restify.http.contract.ContentType;

public class JacksonHypermediaHalJsonMessageConverter<T> extends JacksonMessageConverter<T> {

	private static final ContentType HYPERMEDIA_JSON_CONTENT_TYPE = ContentType.of("application", "hal+json");

	public JacksonHypermediaHalJsonMessageConverter() {
		super(configure(new ObjectMapper(), new LinkBrowserBuilder().build()));
	}

	public JacksonHypermediaHalJsonMessageConverter(LinkBrowser linkBrowser) {
		super(configure(new ObjectMapper(), linkBrowser));
	}

	public JacksonHypermediaHalJsonMessageConverter(ObjectMapper objectMapper) {
		super(configure(objectMapper, null));
	}

	public JacksonHypermediaHalJsonMessageConverter(ObjectMapper objectMapper, LinkBrowser linkBrowser) {
		super(configure(objectMapper, linkBrowser));
	}

	private static ObjectMapper configure(ObjectMapper objectMapper, LinkBrowser linkBrowser) {
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.registerModule(new HypermediaHalJsonModule());
		objectMapper.setHandlerInstantiator(new HypermediaHalHandlerInstantiator(linkBrowser));
		return objectMapper;
	}

	@Override
	public ContentType contentType() {
		return HYPERMEDIA_JSON_CONTENT_TYPE;
	}

	public static <T> JacksonHypermediaHalJsonMessageConverter<T> withoutBrowser() {
		return new JacksonHypermediaHalJsonMessageConverter<>((LinkBrowser) null);
	}
}
