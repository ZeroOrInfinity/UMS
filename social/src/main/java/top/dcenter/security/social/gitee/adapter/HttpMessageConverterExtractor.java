/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.dcenter.security.social.gitee.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 针对 Gitee，处理 gitee 服务商回调时返回的 JSON 进行解析。
 * Response extractor that uses the given {@linkplain HttpMessageConverter entity converters}
 * to convert the response into a type {@code T}.
 *
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 3.0
 * @param <T> the data type
 * @see RestTemplate
 */
public class HttpMessageConverterExtractor<T> implements ResponseExtractor<T> {

	private final Type responseType;

	@Nullable
	private final Class<T> responseClass;

	private final List<HttpMessageConverter<?>> messageConverters;

	private final Log logger;


	/**
	 * Create a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
		this((Type) responseType, messageConverters);
	}

	/**
	 * Creates a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
		this(responseType, messageConverters, LogFactory.getLog(org.springframework.web.client.HttpMessageConverterExtractor.class));
	}

	@SuppressWarnings("unchecked")
	HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Log logger) {
		Assert.notNull(responseType, "'responseType' must not be null");
		Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
		Assert.noNullElements(messageConverters, "'messageConverters' must not contain null elements");
		this.responseType = responseType;
		this.responseClass = (responseType instanceof Class ? (Class<T>) responseType : null);
		this.messageConverters = messageConverters;
		this.logger = logger;
	}


	@Override
	@SuppressWarnings({"unchecked", "rawtypes", "resource"})
	public T extractData(ClientHttpResponse response) throws IOException {
		MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
		if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
			return null;
		}
		MediaType contentType = getContentType(responseWrapper);

		try {
			for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
				if (messageConverter instanceof GenericHttpMessageConverter) {
					GenericHttpMessageConverter<?> genericMessageConverter =
							(GenericHttpMessageConverter<?>) messageConverter;
					if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
						if (logger.isDebugEnabled()) {
							ResolvableType resolvableType = ResolvableType.forType(this.responseType);
							logger.debug("Reading to [" + resolvableType + "]");
						}
						return (T) genericMessageConverter.read(this.responseType, null, responseWrapper);
					}
				}
				if (this.responseClass != null) {
					if (messageConverter.canRead(this.responseClass, contentType)) {
						if (logger.isDebugEnabled()) {
							String className = this.responseClass.getName();
							logger.debug("Reading to [" + className + "] as \"" + contentType + "\"");
						}
						return (T) messageConverter.read((Class) this.responseClass, responseWrapper);
					}
				}
			}
		}
		catch (IOException | HttpMessageNotReadableException ex) {
			throw new RestClientException("Error while extracting response for type [" +
					                              this.responseType + "] and content type [" + contentType + "]", ex);
		}

		throw new RestClientException("Could not extract response: no suitable HttpMessageConverter found " +
				                              "for response type [" + this.responseType + "] and content type [" + contentType + "]");
	}

	/**
	 * Determine the Content-Type of the response based on the "Content-Type"
	 * header or otherwise default to {@link MediaType#APPLICATION_OCTET_STREAM}.
	 * @param response the response
	 * @return the MediaType, possibly {@code null}.
	 */
	@Nullable
	protected MediaType getContentType(ClientHttpResponse response) {
		MediaType contentType = response.getHeaders().getContentType();
		if (contentType == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No content-type, using 'application/octet-stream'");
			}
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}

	/**
	 * Implementation of {@link ClientHttpResponse} that can not only check if
	 * the response has a message body, but also if its length is 0 (i.e. empty)
	 * by actually reading the input stream.
	 *
	 * @author Brian Clozel
	 * @since 4.1.5
	 * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230 Section 3.3.3</a>
	 */
	class MessageBodyClientHttpResponseWrapper implements ClientHttpResponse {

		private final ClientHttpResponse response;

		@Nullable
		private PushbackInputStream pushbackInputStream;


		public MessageBodyClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
			this.response = response;
		}


		/**
		 * Indicates whether the response has a message body.
		 * <p>Implementation returns {@code false} for:
		 * <ul>
		 * <li>a response status of {@code 1XX}, {@code 204} or {@code 304}</li>
		 * <li>a {@code Content-Length} header of {@code 0}</li>
		 * </ul>
		 * @return {@code true} if the response has a message body, {@code false} otherwise
		 * @throws IOException in case of I/O errors
		 */
		public boolean hasMessageBody() throws IOException {
			HttpStatus status = HttpStatus.resolve(getRawStatusCode());
			if (status != null && (status.is1xxInformational() || status == HttpStatus.NO_CONTENT ||
					status == HttpStatus.NOT_MODIFIED)) {
				return false;
			}
			if (getHeaders().getContentLength() == 0) {
				return false;
			}
			return true;
		}

		/**
		 * Indicates whether the response has an empty message body.
		 * <p>Implementation tries to read the first bytes of the response stream:
		 * <ul>
		 * <li>if no bytes are available, the message body is empty</li>
		 * <li>otherwise it is not empty and the stream is reset to its start for further reading</li>
		 * </ul>
		 * @return {@code true} if the response has a zero-length message body, {@code false} otherwise
		 * @throws IOException in case of I/O errors
		 */
		@SuppressWarnings("ConstantConditions")
		public boolean hasEmptyMessageBody() throws IOException {
			InputStream body = this.response.getBody();
			// Per contract body shouldn't be null, but check anyway..
			if (body == null) {
				return true;
			}
			if (body.markSupported()) {
				body.mark(1);
				if (body.read() == -1) {
					return true;
				}
				else {
					body.reset();
					return false;
				}
			}
			else {
				this.pushbackInputStream = new PushbackInputStream(body);
				int b = this.pushbackInputStream.read();
				if (b == -1) {
					return true;
				}
				else {
					this.pushbackInputStream.unread(b);
					return false;
				}
			}
		}


		@Override
		public HttpHeaders getHeaders() {
			return this.response.getHeaders();
		}

		@Override
		public InputStream getBody() throws IOException {
			return (this.pushbackInputStream != null ? this.pushbackInputStream : this.response.getBody());
		}

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return this.response.getStatusCode();
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return this.response.getRawStatusCode();
		}

		@Override
		public String getStatusText() throws IOException {
			return this.response.getStatusText();
		}

		@Override
		public void close() {
			this.response.close();
		}

	}


}

