/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.client;

import java.net.URI;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Interface specifying a basic set of RESTful operations.
 * Implemented by {@link RestTemplate}. Not often used directly, but a useful
 * option to enhance testability, as it can easily be mocked or stubbed.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see RestTemplate
 */
public interface RestOperations {

	// GET

	/**
	 * Retrieve a representation by doing a GET on the specified URL.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 */
	<T> T getForObject(String uri, Class<T> responseType, String... uriVariables)
			throws RestClientException;

	/**
	 * Retrieve a representation by doing a GET on the URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param responseType the type of the return value
	 * @param uriVariables the map containing variables for the URI template
	 * @return the converted object
	 */
	<T> T getForObject(String uri, Class<T> responseType, Map<String, String> uriVariables)
			throws RestClientException;


	// HEAD

	/**
	 * Retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param uriVariables the variables to expand the template
	 * @return all HTTP headers of that resource
	 */
	HttpHeaders headForHeaders(String uri, String... uriVariables) throws RestClientException;

	/**
	 * Retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param uriVariables the map containing variables for the URI template
	 * @return all HTTP headers of that resource
	 */
	HttpHeaders headForHeaders(String uri, Map<String, String> uriVariables) throws RestClientException;


	// POST

	/**
	 * Create a new resource by POSTing the given object to the URI template. The value of the <code>Location</code>,
	 * indicating where the new resource is stored, is returned.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param request the Object to be POSTED
	 * @return the value for the <code>Location</code> header
	 */
	URI postForLocation(String uri, Object request, String... uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to URI template. The value of the <code>Location</code>,
	 * indicating where the new resource is stored, is returned.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param request the Object to be POSTed
	 * @param uriVariables the variables to expand the template
	 * @return the value for the <code>Location</code> header
	 */
	URI postForLocation(String uri, Object request, Map<String, String> uriVariables)
			throws RestClientException;


	// PUT

	/**
	 * Create or update a resource by PUTting the given object to the URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param request the Object to be POSTed
	 * @param uriVariables the variables to expand the template
	 */
	void put(String uri, Object request, String... uriVariables) throws RestClientException;

	/**
	 * Creates a new resource by PUTting the given object to URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param request the Object to be POSTed
	 * @param uriVariables the variables to expand the template
	 */
	void put(String uri, Object request, Map<String, String> uriVariables) throws RestClientException;


	// DELETE

	/**
	 * Delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param uriVariables the variables to expand in the template
	 */
	void delete(String uri, String... uriVariables) throws RestClientException;

	/**
	 * Delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param uriVariables the variables to expand the template
	 */
	void delete(String uri, Map<String, String> uriVariables) throws RestClientException;


	// OPTIONS

	/**
	 * Return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header
	 */
	Set<HttpMethod> optionsForAllow(String uri, String... uriVariables)
			throws RestClientException;

	/**
	 * Return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given map.
	 * @param uri the URI
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header
	 */
	Set<HttpMethod> optionsForAllow(String uri, Map<String, String> uriVariables)
			throws RestClientException;


	// general execution

	/**
	 * Execute the HTTP methods to the given URI, preparing the request with the {@link RequestCallback},
	 * and reading the response with a {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * @param uri the URI
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariables the variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> T execute(String uri, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, String... uriVariables)
			throws RestClientException;

	/**
	 * Execute the HTTP methods to the given URI, preparing the request with the {@link RequestCallback},
	 * and reading the response with a {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables map.
	 * @param uri the URI
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariablesthe variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> T execute(String uri, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, String> uriVariables)
			throws RestClientException;

}
