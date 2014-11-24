package io.vov.vitamio.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * User: qii
 * Date: 12-12-19
 */
public class JavaHttpUtility {

    private static final int CONNECT_TIMEOUT = 60 * 1000;

    private static final int READ_TIMEOUT = 60 * 1000;

    private static final int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;

    private static final int DOWNLOAD_READ_TIMEOUT = 60 * 1000;

    private static final int UPLOAD_CONNECT_TIMEOUT = 15 * 1000;

    private static final int UPLOAD_READ_TIMEOUT = 5 * 60 * 1000;

    public class NullHostNameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public JavaHttpUtility() {
        //allow Android to use an untrusted certificate for SSL/HTTPS connection
        //so that when you debug app, you can use Fiddler http://fiddler2.com to logs all HTTPS traffic
    }


    public String executeNormalTask(HttpMethod httpMethod, String url, Map<String, String> param)
            throws Exception {
        switch (httpMethod) {
            case Post:
                return doPost(url, param);
            case Get:
                return doGet(url, param);
        }
        return "";
    }

    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
            return new Proxy(java.net.Proxy.Type.HTTP,
                    new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        } else {
            return null;
        }
    }

    public String doPost(String urlAddress, Map<String, String> param) throws Exception {
        String errorStr = "���������⣬������";
        try {
            URL url = new URL(urlAddress);
            Proxy proxy = getProxy();
            HttpsURLConnection uRLConnection;
            if (proxy != null) {
                uRLConnection = (HttpsURLConnection) url.openConnection(proxy);
            } else {
                uRLConnection = (HttpsURLConnection) url.openConnection();
            }

            uRLConnection.setDoInput(true);
            uRLConnection.setDoOutput(true);
            uRLConnection.setRequestMethod("POST");
            uRLConnection.setUseCaches(false);
            uRLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            uRLConnection.setReadTimeout(READ_TIMEOUT);
            uRLConnection.setInstanceFollowRedirects(false);
            uRLConnection.setRequestProperty("Connection", "Keep-Alive");
            uRLConnection.setRequestProperty("Charset", "UTF-8");
            uRLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            uRLConnection.connect();

            DataOutputStream out = new DataOutputStream(uRLConnection.getOutputStream());
            out.write(Utility.encodeUrl(param).getBytes());
            out.flush();
            out.close();
            return handleResponse(uRLConnection);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(errorStr, e);
        }
    }

    private String handleResponse(HttpURLConnection httpURLConnection) throws Exception {
        String errorStr = "���������⣬������";
        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpURLConnection.disconnect();
            throw new Exception(errorStr, e);
        }

        if (status != HttpURLConnection.HTTP_OK) {
            return handleError(httpURLConnection);
        }

        return readResult(httpURLConnection);
    }

    private String handleError(HttpURLConnection urlConnection) throws Exception {

        String result = readError(urlConnection);
        String err = null;
        int errCode = 0;
        try {
            JSONObject json = new JSONObject(result);
            err = json.optString("error_description", "");
            if (TextUtils.isEmpty(err)) {
                err = json.getString("error");
            }
            errCode = json.getInt("error_code");
            Exception exception = new Exception();

            throw exception;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String readResult(HttpURLConnection urlConnection) throws Exception {
        InputStream is = null;
        BufferedReader buffer = null;
        String errorStr = "���������⣬������";
        try {
            is = urlConnection.getInputStream();

            String content_encode = urlConnection.getContentEncoding();

            if (!TextUtils.isEmpty(content_encode) && content_encode
                    .equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
//            AppLogger.d("result=" + strBuilder.toString());
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(errorStr, e);
        } finally {
            Utility.closeSilently(is);
            Utility.closeSilently(buffer);
            urlConnection.disconnect();
        }

    }

    private String readError(HttpURLConnection urlConnection) throws Exception {
        InputStream is = null;
        BufferedReader buffer = null;
        String errorStr = "���������⣬������";

        try {
            is = urlConnection.getErrorStream();

            if (is == null) {
                throw new Exception(errorStr);
            }

            String content_encode = urlConnection.getContentEncoding();

            if (!TextUtils.isEmpty(content_encode) && content_encode
                    .equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(errorStr, e);
        } finally {
            Utility.closeSilently(is);
            Utility.closeSilently(buffer);
            urlConnection.disconnect();
        }

    }

    public String doGet(String urlStr, Map<String, String> param) throws Exception {
        String errorStr = "���������⣬������";
        InputStream is = null;
        try {

            StringBuilder urlBuilder = new StringBuilder(urlStr);
            if (param != null) {
            	urlBuilder.append("?").append(Utility.encodeUrl(param));
			}
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection urlConnection;
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();

            return handleResponse(urlConnection);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(errorStr, e);
        }


    }

    private static String getBoundry() {
        StringBuffer _sb = new StringBuffer();
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                _sb.append((char) time % 9);
            } else if (time % 3 == 1) {
                _sb.append((char) (65 + time % 26));
            } else {
                _sb.append((char) (97 + time % 26));
            }
        }
        return _sb.toString();
    }

    private String getBoundaryMessage(String boundary, Map params, String fileField,
            String fileName, String fileType) {
        StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");

        Iterator keys = params.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) params.get(key);
            res.append("Content-Disposition: form-data; name=\"")
                    .append(key).append("\"\r\n").append("\r\n")
                    .append(value).append("\r\n").append("--")
                    .append(boundary).append("\r\n");
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField)
                .append("\"; filename=\"").append(fileName)
                .append("\"\r\n").append("Content-Type: ")
                .append(fileType).append("\r\n\r\n");

        return res.toString();
    }

}



