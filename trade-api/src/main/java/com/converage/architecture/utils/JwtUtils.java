package com.converage.architecture.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.exception.LoginException;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.user.User;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    /**
     * 过期时间30天
     */
    public static final Integer ALIVE_DAY_TIME = 30;

    /**
     * App Token私钥
     */
    public static final String APP_TOKEN_SECRET = "680dd9891cda8ffa6a6259e5cbd8ab8e";

    /**
     * Admin Token私钥
     */
    public static final String ADMIN_TOKEN_SECRET = "ce8cf71fa3ee4a888698beab30e8ea38";

    /**
     * tokenName
     */
    public static final String ACCESS_TOKEN_NAME = "accessToken";

    /**
     * token claim userName
     */
    private static final String TOKEN_CLAIM_USERID = "userId";


    private static RedisClient redisClient;

    @Autowired
    public void setRedisClient(RedisClient redisClient) {
        JwtUtils.redisClient = redisClient;
    }


    /**
     * 创建后台管理系统token
     *
     * @param subscriber
     * @return
     */
    public static String createAdminToken(Subscriber subscriber) {
        if (BooleanUtils.isFalse(subscriber.getIfValid())) {
            throw new BusinessException("账号被限制登录");
        }
        return createToken(subscriber.getId(), ADMIN_TOKEN_SECRET, RedisKeyConst.ADMIN_ACCESS_TOKEN);
    }


    /**
     * 创建app token
     *
     * @param user
     * @return
     */
    public static String createAppToken(User user) {
        return createToken(user.getId(), APP_TOKEN_SECRET, RedisKeyConst.APP_ACCESS_TOKEN);
    }

    private static String createToken(String userId, String tokenSecret, String redisKey) {
        String token;
        try {
            Date expireDate = DateUtils.addMonths(new Date(), ALIVE_DAY_TIME);
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
            Map<String, Object> map = new HashMap<>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            token = JWT.create().withHeader(map)
                    .withClaim(TOKEN_CLAIM_USERID, userId)
                    .withExpiresAt(expireDate)
                    .sign(algorithm);
            redisClient.setForTimeCustom(String.format(redisKey, userId), token, ALIVE_DAY_TIME, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("create token error");
        }
        return token;
    }

    /**
     * 验证 app token
     */
    public static boolean verifyJwt4App(String token) {
        return verifyJwt(token, APP_TOKEN_SECRET, RedisKeyConst.APP_ACCESS_TOKEN);
    }


    /**
     * 验证 admin token
     */
    public static boolean verifyJwt4Admin(String token) {
//        return verifyJwt(token, ADMIN_TOKEN_SECRET, RedisKeyConst.ADMIN_ACCESS_TOKEN);
        return true;
    }

    private static boolean verifyJwt(String token, String appTokenSecret, String redisKey) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(appTokenSecret);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            decodedJWT = jwtVerifier.verify(token);
            String userId = decodedJWT.getClaim(TOKEN_CLAIM_USERID).asString();
            String accessToken = redisClient.get(String.format(redisKey, userId));
            return accessToken.equals(token);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 获取app端 token信息
     */
    public static User getUserByToken(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token, APP_TOKEN_SECRET);
        User user = new User();
        user.setId(decodedJWT.getClaim(TOKEN_CLAIM_USERID).asString());
        return user;
    }

    public static String getUserIdByToken(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token, APP_TOKEN_SECRET);
        return decodedJWT.getClaim(TOKEN_CLAIM_USERID).asString();
    }

    /**
     * 获取pc端 toke信息
     *
     * @param token
     */
    public static Subscriber getAdminByToken(String token) {
        DecodedJWT decodedJWT;
        decodedJWT = getDecodedJWT(token, ADMIN_TOKEN_SECRET);
        String id = decodedJWT.getClaim(TOKEN_CLAIM_USERID).asString();
        Subscriber subscriber = new Subscriber();
        subscriber.setId(id);
        return subscriber;
    }

    public static String getAdminIdByToken(String token) {
        return getDecodedJWT(token, ADMIN_TOKEN_SECRET).getClaim(TOKEN_CLAIM_USERID).asString();
    }

    private static DecodedJWT getDecodedJWT(String token, String adminTokenSecret) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(adminTokenSecret);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            decodedJWT = jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new LoginException();
        }
        return decodedJWT;
    }
}
