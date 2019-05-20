package com.chabuss.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import io.vertx.core.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

/**Class created to help made HttpRequests
 * using Apache Http classes.
 * @author chabuss
 */
@Accessors(chain=true)
public class HttpRequest {

	@Getter private HttpClient 	 client;
	@Getter private HttpPost   	 post;
	@Getter private HttpGet    	 get;
	@Getter private HttpPatch    patch;
	@Getter private HttpDelete   delete;
	@Getter private HttpPut      put;
	@Getter private HttpHead     head;
	@Getter private String 	     url;
	@Getter private String       json;
	private String responseBody;
	private HttpMethod 	 method;
	private HttpResponse response;
	private boolean 	 executed;
	@Getter private Map<String, String> headers = new HashMap<>(); 
	
	protected HttpRequest(String url, long timeOut) {
		this.client = timeOut == 0 ? HttpClientBuilder.create().build() : HttpClientBuilder.create()
				.setConnectionTimeToLive(timeOut, TimeUnit.MILLISECONDS).build();
		this.url      = url;
		this.response = null;
		this.executed = false;
	}
	
	public HttpResponse response() { return response; }
	public HttpRequest addHeaderAcceptJson() { this.putHeader("Accept", "application/json"); return this; }

	// --------------------------------------------------- GET -------------------------------------------------------
	public static HttpRequest get(String url) 				throws Exception { return HttpRequest.get(url, 0); }
	public static HttpRequest get(String url, long timeOut) throws Exception {
		HttpRequest request = new HttpRequest(url, timeOut);
		request.method = HttpMethod.GET;
		request.get   = new HttpGet(url);
		return request;
	}
	// --------------------------------------------------- POST -------------------------------------------------------
	public static HttpRequest post(String url) 				throws Exception { return HttpRequest.post(url, null, 0); }
	public static HttpRequest post(String url, String json) throws Exception { return HttpRequest.post(url, json, 0); }
	/**TimeOut em milliseconds.
	 * @author chabuss
	 */
	public static HttpRequest post(String url, String json, long timeOut) throws Exception {
		if(timeOut == 0) timeOut = 3*60*1000; // 3 minutes
		HttpRequest request = new HttpRequest(url, timeOut);
		request.method = HttpMethod.POST;
		request.post   = new HttpPost(url);
		if(json != null) {
			request.post.addHeader("Content-Type","application/json");
			request.post.setEntity(new StringEntity(json, "UTF-8"));
		}
		return request;
	}
	// --------------------------------------------------- HEAD -------------------------------------------------------
	public static HttpRequest head(String url) 				 throws Exception { return HttpRequest.head(url, null, 0); }
	public static HttpRequest head(String url, long timeOut) throws Exception { return HttpRequest.head(url, null, timeOut); }
	public static HttpRequest head(String url, String json, long timeOut) throws Exception {
		HttpRequest request = new HttpRequest(url, timeOut);
		request.method = HttpMethod.HEAD;
		request.head   = new HttpHead(url);
		request.putHeader("Accept", "*/*");
		request.putHeader("User-Agent", "Apache-Http");
		return request; 
	}
	
	public HttpRequest setFormData(List<NameValuePair> values) throws Exception {
		switch (this.method) {
			case POST:
				this.post.addHeader("Content-Type", "application/x-www-form-urlencoded");
				this.post.setEntity(new  UrlEncodedFormEntity(values));
				return this;
			case PATCH:
				this.patch.addHeader("Content-Type", "application/x-www-form-urlencoded");
				this.patch.setEntity(new UrlEncodedFormEntity(values));
				return this;
			case PUT:
				this.put.addHeader("Content-Type", "application/x-www-form-urlencoded");
				this.put.setEntity( new  UrlEncodedFormEntity(values));
				return this;
			default:
				throw new Exception("HttpRequest -> method not support this function, only for POST, PATCH, PUT");			
		}
	}
	
	public HttpRequest putHeader(String key, String value) {
		switch (this.method) {
			case GET:
				this.get.addHeader(key,value);
				return this;
			case POST:
				this.post.addHeader(key,value);
				return this;
			case PATCH:
				this.patch.addHeader(key,value);
				return this;
			case PUT:
				this.put.addHeader(key,value);
				return this;
			case DELETE:
				this.delete.addHeader(key,value);
				return this;
			case HEAD:
				this.head.addHeader(key,value);
				return this;
			default:
				return this;
		}
	}
	
	public HttpResponse execute() throws Exception {
		switch (this.method) {
			case GET:
				this.response = client.execute(get); 
				this.executed = true;
				this._getHeaders();
				this.responseBody = EntityUtils.toString(this.response.getEntity());
				return response;
			case POST:
				this.response = client.execute(post);
				this.executed = true;
				this._getHeaders();
				if(this.response.getEntity() != null)
					this.responseBody = EntityUtils.toString(this.response.getEntity());
				return response;
			case PATCH:
				this.response = client.execute(patch);
				this.executed = true;
				this._getHeaders();
				this.responseBody = EntityUtils.toString(this.response.getEntity());
				return this.response;
			case PUT:
				this.response = client.execute(put);
				this.executed = true;
				this._getHeaders();
				this.responseBody = EntityUtils.toString(this.response.getEntity());
				return this.response;
			case DELETE:
				this.response = client.execute(delete);
				this.executed = true;
				this._getHeaders();
				this.responseBody = EntityUtils.toString(this.response.getEntity());
				return this.response;
			case HEAD:
				this.response = client.execute(head);
				this.executed = true;
				this._getHeaders();
				return this.response;
			default:
				throw new Exception("No method configured..");
		}
	}
	
	private void _getHeaders() { 
		for (Header h : response.getAllHeaders())
			headers.put(h.getName(), h.getValue());
	}

	public String getResponseHeader(String key) { return headers.get(key); }
	
	public String getResponseBody() throws Exception {
		if(!executed)
			throw new Exception("Request not executed.. method execute needs to be called before this.");
		return this.responseBody;
	}
	
	public int getResponseStatusCode() throws Exception {
		if(!executed)
			throw new Exception("Request not executed.. method execute needs to be called before this.");
		return this.response.getStatusLine().getStatusCode();
	}
}
