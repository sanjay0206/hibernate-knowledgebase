package com.knowledgebase.security;

import com.knowledgebase.dao.LoginDao;
import com.knowledgebase.entities.UserRole;
import com.knowledgebase.utils.PropertyManager;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class JWTTokenProvider {
    private static final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
    private static final Logger logger = LogManager.getLogger(JWTTokenProvider.class);

    private static String buildJWTHeader() {
        JSONObject header = new JSONObject();
        header.put("alg", SignatureAlgorithm.HS256);
        header.put("typ", "JWT");
        logger.debug("JWT Header: {}", header.toString(4));
        return header.toString();
    }

    private static String buildJWTPayload(UserRole userRole, String username) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + TimeUnit.MINUTES.toMillis(30);

        JSONObject payload = new JSONObject();
        payload.put("iat", nowMillis);
        payload.put("exp", expMillis);
        payload.put("sub", username);
        payload.put("aud", userRole);
        logger.debug("JWT Payload: {}", payload.toString(4));
        return payload.toString();
    }

    private static String encodeToBase64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String calculateHmac(String encodedHeaderAndPayload, String secretKey) {
        String signature = null;
        try {
            byte[] secretKeyHash = secretKey.getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance(algorithm.getJcaName());
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyHash, algorithm.getJcaName());
            mac.init(keySpec);
            byte[] signedBytes = mac.doFinal(encodedHeaderAndPayload.getBytes(StandardCharsets.UTF_8));
            signature = encodeToBase64(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return signature;
    }

    public String generateJWT(String username) {
        String token = null;
        try {
            LoginDao loginDao = new LoginDao();
            UserRole userRole = loginDao.fetchUserRoleByUsername(username);
            logger.debug("userRole: {}", userRole);

            String tokenHeader = buildJWTHeader();
            String tokenPayload = buildJWTPayload(userRole, username);

            String encodedHeaderAndPayload = encodeToBase64(tokenHeader.getBytes()) + "." + encodeToBase64(tokenPayload.getBytes());
            String secretKey = PropertyManager.getProperty("SECRET_KEY");
            String signature = calculateHmac(encodedHeaderAndPayload, secretKey);
            if (signature != null) {
                token = encodeToBase64(tokenHeader.getBytes()) + "." + encodeToBase64(tokenPayload.getBytes()) + "." + signature;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("JWT token: {}", token);
        return token;
    }

    public boolean isValidToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            String tokenWithoutSignature = chunks[0] + "." + chunks[1];
            String signature = chunks[2];

            byte[] secretKeyHash = PropertyManager.getProperty("SECRET_KEY").getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyHash, algorithm.getJcaName());
            DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(algorithm, secretKeySpec);

            if (validator.isValid(tokenWithoutSignature, signature)) {
                JSONObject tokenPayload = decodeTokenPayload(token);
                long expLong = (long) tokenPayload.get("exp");
                LocalDateTime expiration = Instant.ofEpochMilli(expLong).atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();

                // If the expiration time is after current time then it is a valid token
                return expiration.isAfter(now);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject decodeTokenPayload(String token) {
        JSONObject decodedPayload = null;
        try {
            String[] chunks = token.split("\\.");
            String decodedPayloadStr = new String(Base64.getUrlDecoder().decode(chunks[1]));
            decodedPayload = new JSONObject(decodedPayloadStr);
            logger.info("Decoded JWT Payload: {}", decodedPayload.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodedPayload;
    }
}