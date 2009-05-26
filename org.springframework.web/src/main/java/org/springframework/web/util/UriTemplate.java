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

package org.springframework.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * Represents a URI template. An URI template is a URI-like String that contained variables marked of in braces
 * (<code>{</code>, <code>}</code>), which can be expanded to produce a URI. <p>See {@link #expand(Map)}, {@link
 * #expand(String[])}, and {@link #match(String)} for example usages.
 *
 * @author Arjen Poutsma
 * @see <a href="http://bitworking.org/projects/URI-Templates/">URI Templates</a>
 * @since 3.0
 */
public class UriTemplate {

	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	/** Replaces template variables in the URI template. */
	private static final String VALUE_REGEX = "(.*)";

	private final List<String> variableNames;

	private final Pattern matchPattern;

	private final String uriTemplate;

	/**
	 * Construct a new {@link UriTemplate} with the given URI String.
	 *
	 * @param uriTemplate the URI template string
	 */
	public UriTemplate(String uriTemplate) {
		Parser parser = new Parser(uriTemplate);
		this.uriTemplate = uriTemplate;
		this.variableNames = parser.getVariableNames();
		this.matchPattern = parser.getMatchPattern();
	}

	/**
	 * Return the names of the variables in the template, in order.
	 *
	 * @return the template variable names
	 */
	public final List<String> getVariableNames() {
		return this.variableNames;
	}

	/**
	 * Given the Map of variables, expands this template into a URI. The Map keys represent variable names, the Map values
	 * variable values. The order of variables is not significant. <p>Example:
	 * <pre>
	 * UriTemplate template = new UriTemplate("http://example.com/hotels/{hotel}/bookings/{booking}");
	 * Map&lt;String, String&gt; uriVariables = new HashMap&lt;String, String&gt;();
	 * uriVariables.put("booking", "42");
	 * uriVariables.put("hotel", "1");
	 * System.out.println(template.expand(uriVariables));
	 * </pre>
	 * will print: <blockquote><code>http://example.com/hotels/1/bookings/42</code></blockquote>
	 *
	 * @param uriVariables the map of URI variables
	 * @return the expanded URI
	 * @throws IllegalArgumentException if <code>uriVariables</code> is <code>null</code>; or if it does not contain values
	 * for all the variable names
	 */
	public URI expand(Map<String, String> uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");
		String[] values = new String[this.variableNames.size()];
		for (int i = 0; i < this.variableNames.size(); i++) {
			String name = this.variableNames.get(i);
			if (!uriVariables.containsKey(name)) {
				throw new IllegalArgumentException("'uriVariables' Map has no value for '" + name + "'");
			}
			values[i] = uriVariables.get(name);
		}
		return expand(values);
	}

	/**
	 * Given an array of variables, expand this template into a full URI. The array represent variable values. The order of
	 * variables is significant. <p>Example: <pre class="code> UriTemplate template = new
	 * UriTemplate("http://example.com/hotels/{hotel}/bookings/{booking}"); System.out.println(template.expand("1", "42));
	 * </pre> will print: <blockquote><code>http://example.com/hotels/1/bookings/42</code></blockquote>
	 *
	 * @param uriVariableValues the array of URI variables
	 * @return the expanded URI
	 * @throws IllegalArgumentException if <code>uriVariables</code> is <code>null</code>; or if it does not contain
	 * sufficient variables
	 */
	public URI expand(String... uriVariableValues) {
		Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
		if (uriVariableValues.length != this.variableNames.size()) {
			throw new IllegalArgumentException(
					"Invalid amount of variables values in [" + this.uriTemplate + "]: expected " +
							this.variableNames.size() + "; got " + uriVariableValues.length);
		}
		Matcher matcher = NAMES_PATTERN.matcher(this.uriTemplate);
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		while (matcher.find()) {
			String uriVariable = uriVariableValues[i++];
			matcher.appendReplacement(buffer, uriVariable);
		}
		matcher.appendTail(buffer);
		return encodeUri(buffer.toString());
	}

	/**
	 * Indicate whether the given URI matches this template.
	 *
	 * @param uri the URI to match to
	 * @return <code>true</code> if it matches; <code>false</code> otherwise
	 */
	public boolean matches(String uri) {
		if (uri == null) {
			return false;
		}
		Matcher matcher = this.matchPattern.matcher(uri);
		return matcher.matches();
	}

	/**
	 * Match the given URI to a map of variable values. Keys in the returned map are variable names, values are variable
	 * values, as occurred in the given URI. <p>Example: <pre class="code"> UriTemplate template = new
	 * UriTemplate("http://example.com/hotels/{hotel}/bookings/{booking}"); System.out.println(template.match("http://example.com/hotels/1/bookings/42"));
	 * </pre> will print: <blockquote><code>{hotel=1, booking=42}</code></blockquote>
	 *
	 * @param uri the URI to match to
	 * @return a map of variable values
	 */
	public Map<String, String> match(String uri) {
		Assert.notNull(uri, "'uri' must not be null");
		Map<String, String> result = new LinkedHashMap<String, String>(this.variableNames.size());
		Matcher matcher = this.matchPattern.matcher(uri);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String name = this.variableNames.get(i - 1);
				String value = matcher.group(i);
				result.put(name, value);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return this.uriTemplate;
	}

	private static URI encodeUri(String uri) {
		try {
			int schemeIdx = uri.indexOf(':');
			if (schemeIdx == -1) {
				int queryIdx = uri.indexOf('?');
				if (queryIdx == -1) {
					int fragmentIdx = uri.indexOf('#');
					if (fragmentIdx == -1) {
						return new URI(null, null, uri, null);
					}
					else {
						String path = uri.substring(0, fragmentIdx);
						String fragment = uri.substring(fragmentIdx + 1);
						return new URI(null, null, path, fragment);
					}
				}
				else {
					int fragmentIdx = uri.indexOf('#', queryIdx + 1);
					if (fragmentIdx == -1) {
						String path = uri.substring(0, queryIdx);
						String query = uri.substring(queryIdx + 1);
						return new URI(null, null, path, query, null);
					}
					else {
						String path = uri.substring(0, queryIdx);
						String query = uri.substring(queryIdx + 1, fragmentIdx);
						String fragment = uri.substring(fragmentIdx + 1);
						return new URI(null, null, path, query, fragment);
					}
				}
			}
			else {
				int fragmentIdx = uri.indexOf('#', schemeIdx + 1);
				String scheme = uri.substring(0, schemeIdx);
				if (fragmentIdx == -1) {
					String ssp = uri.substring(schemeIdx + 1);
					return new URI(scheme, ssp, null);
				}
				else {
					String ssp = uri.substring(schemeIdx + 1, fragmentIdx);
					String fragment = uri.substring(fragmentIdx + 1);
					return new URI(scheme, ssp, fragment);
				}
			}
		}
		catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Could not create URI from [" + uri + "]: " + ex);
		}
	}

	/** Static inner class to parse uri template strings into a matching regular expression. */
	private static class Parser {

		private final List<String> variableNames = new LinkedList<String>();

		private final StringBuilder patternBuilder = new StringBuilder();

		private Parser(String uriTemplate) {
			Assert.hasText(uriTemplate, "'uriTemplate' must not be null");
			Matcher m = NAMES_PATTERN.matcher(uriTemplate);
			int end = 0;
			while (m.find()) {
				this.patternBuilder.append(encodeAndQuote(uriTemplate, end, m.start()));
				this.patternBuilder.append(VALUE_REGEX);
				this.variableNames.add(m.group(1));
				end = m.end();
			}
			this.patternBuilder.append(encodeAndQuote(uriTemplate, end, uriTemplate.length()));
			int lastIdx = this.patternBuilder.length() - 1;
			if (lastIdx >= 0 && this.patternBuilder.charAt(lastIdx) == '/') {
				this.patternBuilder.deleteCharAt(lastIdx);
			}
		}

		private String encodeAndQuote(String fullPath, int start, int end) {
			if (start == end) {
				return "";
			}
			String result = encodeUri(fullPath.substring(start, end)).toASCIIString();
			return Pattern.quote(result);
		}

		private List<String> getVariableNames() {
			return Collections.unmodifiableList(this.variableNames);
		}

		private Pattern getMatchPattern() {
			return Pattern.compile(this.patternBuilder.toString());
		}
	}

}
