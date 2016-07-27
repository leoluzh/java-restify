package com.restify.http.metadata.reflection;

import static com.restify.http.metadata.Preconditions.isTrue;

import java.util.Optional;
import java.util.stream.Stream;

import com.restify.http.contract.BodyParameter;
import com.restify.http.contract.HeaderParameter;
import com.restify.http.contract.Parameter;
import com.restify.http.contract.PathParameter;
import com.restify.http.contract.QueryString;
import com.restify.http.metadata.EndpointMethodParameterSerializer;
import com.restify.http.metadata.SimpleEndpointMethodParameterSerializer;

public class JavaMethodParameterMetadata {

	private final String name;
	private final PathParameter pathParameter;
	private final HeaderParameter headerParameter;
	private final BodyParameter bodyParameter;
	private final QueryString queryParameters;
	private final Class<? extends EndpointMethodParameterSerializer> serializerType;

	public JavaMethodParameterMetadata(java.lang.reflect.Parameter javaMethodParameter) {
		this.pathParameter = javaMethodParameter.getAnnotation(PathParameter.class);
		this.headerParameter = javaMethodParameter.getAnnotation(HeaderParameter.class);
		this.bodyParameter = javaMethodParameter.getAnnotation(BodyParameter.class);
		this.queryParameters = javaMethodParameter.getAnnotation(QueryString.class);

		isTrue(Stream.of(javaMethodParameter.getAnnotations())
				.filter(a -> a.annotationType().isAnnotationPresent(Parameter.class))
					.count() <= 1, "Parameter " + javaMethodParameter + " has more than one annotation.");

		this.name = Optional.ofNullable(pathParameter)
				.map(PathParameter::value)
					.filter(s -> !s.trim().isEmpty())
						.orElseGet(() -> Optional.ofNullable(headerParameter)
							.map(HeaderParameter::value)
								.filter(s -> !s.trim().isEmpty())
									.orElseGet(() -> Optional.ofNullable(javaMethodParameter.getName())
										.orElseThrow(() -> new IllegalStateException("Could not get the name of the parameter " + javaMethodParameter))));

		this.serializerType = pathParameter != null ? pathParameter.serializer()
				: queryParameters != null ? queryParameters.serializer()
				: SimpleEndpointMethodParameterSerializer.class;
	}

	public String name() {
		return name;
	}

	public boolean ofPath() {
		return pathParameter != null || (headerParameter == null && bodyParameter == null && queryParameters == null);
	}

	public boolean ofBody() {
		return bodyParameter != null;
	}

	public boolean ofHeader() {
		return headerParameter != null;
	}

	public Class<? extends EndpointMethodParameterSerializer> serializer() {
		return serializerType;
	}

}
