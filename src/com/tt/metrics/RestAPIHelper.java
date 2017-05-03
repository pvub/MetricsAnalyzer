package com.tt.metrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Helper class to access Rest API
 * @author Udai
 */
public class RestAPIHelper {
    
    private String m_url = null;
    
    public RestAPIHelper(String url) {
        m_url = url;
    }
    
    public String getData() 
    {
        String response = null;
        
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");

        HttpRequestBase httpRequest = null;
        URI uri = null;

        try {
            uri = new URI(m_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        httpRequest = new HttpGet(uri);
        httpRequest.addHeader("content-type", "application/json");
        httpRequest.addHeader("Accept","application/json");

//        try {
//            authorize(httpRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        HttpResponse httpResponse = null;
        try 
        {
            System.out.println("Host: " + uri.getHost());
            System.out.println("Scheme: " + uri.getScheme());
            HttpHost target = new HttpHost(uri.getHost(), -1, uri.getScheme());
            httpResponse = client.execute(/*target,*/ httpRequest);
            System.out.println("Connection status : " + httpResponse.getStatusLine());

            //InputStream inputStraem = httpResponse.getEntity().getContent();

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String readline = null;
            while (((readline = br.readLine()) != null)) {
                sb.append(readline + "\n");
            }

            //System.out.println(sb.toString());
            response = sb.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return response;
        
    }
}
