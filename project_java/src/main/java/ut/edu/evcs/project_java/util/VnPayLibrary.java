package ut.edu.evcs.project_java.util;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayLibrary {

    private final SortedMap<String, String> requestData = new TreeMap<>(new VnPayCompare());
    private final SortedMap<String, String> responseData = new TreeMap<>(new VnPayCompare());

    public void addRequestData(String key, String value) {
        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
            requestData.put(key, value);
        }
    }

    public void addResponseData(String key, String value) {
        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
            responseData.put(key, value);
        }
    }

    public String getResponseData(String key) {
        return responseData.getOrDefault(key, "");
    }

    public String createRequestUrl(String baseUrl, String vnpHashSecret) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : requestData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (query.length() > 0)
                    query.append("&");
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        SortedMap<String, String> toSign = new TreeMap<>(new VnPayCompare());
        for (Map.Entry<String, String> entry : requestData.entrySet()) {
            String k = entry.getKey();
            if (!"vnp_SecureHash".equals(k) && !"vnp_SecureHashType".equals(k)) {
                toSign.put(k, entry.getValue());
            }
        }

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : toSign.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (hashData.length() > 0)
                    hashData.append("&");
                hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        String vnpSecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        String url = baseUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnpSecureHash;
        try {
            System.out.println("VNPay request=" + url);
            System.out.println("VNPay hashData=" + hashData);
        } catch (Exception ignored) {
        }
        return url;
    }

    private String getResponseDataString() throws UnsupportedEncodingException {
        SortedMap<String, String> tempData = new TreeMap<>(new VnPayCompare());

        for (Map.Entry<String, String> entry : responseData.entrySet()) {
            if (!entry.getKey().equals("vnp_SecureHashType") && !entry.getKey().equals("vnp_SecureHash")) {
                tempData.put(entry.getKey(), entry.getValue());
            }
        }

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : tempData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (data.length() > 0)
                    data.append("&");
                data.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        return data.toString();
    }

    public boolean validateSignature(String inputHash, String secretKey) throws UnsupportedEncodingException {
        String rspRaw = getResponseDataString();
        String myChecksum = hmacSHA512(secretKey, rspRaw);
        return myChecksum.equalsIgnoreCase(inputHash);
    }

    public String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, 0, hmacKeyBytes.length, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
            if (ipAddress != null) {
                if (ipAddress.contains(",")) {
                    ipAddress = ipAddress.split(",")[0].trim();
                }
                if ("::1".equals(ipAddress) || ipAddress.contains(":")) {
                    ipAddress = "127.0.0.1";
                }
            }
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }

    public static class VnPayCompare implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o1.compareTo(o2);
        }
    }
}
