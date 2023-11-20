package com.cedricapp.common;

import android.os.Build;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class JWebToken {

    private static final String SECRET_KEY = "FREE_MASON"; //@TODO Add Signature here
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final String ISSUER = "mason.metamug.net";
    private static String JWT_HEADER = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9";
    private JSONObject payload = new JSONObject();
    private String signature;
    private String encodedHeader;

    private JWebToken() {
        try {
            encodedHeader = encode(new JSONObject(JWT_HEADER));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JWebToken(JSONObject payload) throws JSONException {
        this(payload.getString("sub"), payload.getJSONArray("aud"), payload.getLong("exp"));
    }

    public JWebToken(String sub, JSONArray aud, long expires) {
        this();
        try {
            payload.put("sub", sub);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            payload.put("aud", aud);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            payload.put("exp", expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            payload.put("iss", ISSUER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            payload.put("jti", UUID.randomUUID().toString()); //how do we use this?
        } catch (JSONException e) {
            e.printStackTrace();
        }
        signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
    }

    /**
     * For verification
     *
     * @param token
     * @throws java.security.NoSuchAlgorithmException
     */
    public JWebToken(String token) throws NoSuchAlgorithmException, JSONException {
        this();
        String[] parts = token.split("\\.");
       // JWT_HEADER=parts[0];
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Token format");
        }
        if (encodedHeader.equals(parts[0])) {
            encodedHeader = parts[0];
        } else {
            throw new NoSuchAlgorithmException("JWT Header is Incorrect: " + parts[0]);
        }

        try {
            payload = new JSONObject(decode(parts[1]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (payload== null) {
            throw new JSONException("Payload is Empty: ");
        }
        if (!payload.has("exp")) {
            throw new JSONException("Payload doesn't contain expiry " + payload);
        }
        signature = parts[2];
    }

    @Override
    public String toString() {
        return encodedHeader + "." + encode(payload) + "." + signature;
    }

    public boolean isValid() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return payload.getLong("exp") > (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) //token not expired
                        && signature.equals(hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY)); //signature matched
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getSubject() {
        try {
            return payload.getString("sub");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAudience() throws JSONException {
        JSONArray arr = payload.getJSONArray("aud");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    private static String encode(JSONObject obj) {
        return encode(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] bytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }
        return null;
    }

    private static String decode(String encodedString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new String(Base64.getUrlDecoder().decode(encodedString));
        }
        return encodedString;
    }

    /**
     * Sign with HMAC SHA256 (HS256)
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String hmacSha256(String data, String secret) {
        try {

            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);//digest.digest(secret.getBytes(StandardCharsets.UTF_8));

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(JWebToken.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

}
