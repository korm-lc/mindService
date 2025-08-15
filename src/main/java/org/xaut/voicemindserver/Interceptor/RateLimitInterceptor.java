package org.xaut.voicemindserver.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xaut.voicemindserver.utils.JwtUtil;
import org.xaut.voicemindserver.utils.TokenBucketManager;

import java.io.IOException;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final TokenBucketManager tokenBucketManager;
    private final JwtUtil jwtUtil;  // 解析token取userId

    public RateLimitInterceptor(TokenBucketManager tokenBucketManager, JwtUtil jwtUtil) {
        this.tokenBucketManager = tokenBucketManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authHeader = request.getHeader("Authorization");
        String userId = jwtUtil.parseUserIdFromAuthHeader(authHeader);

        if (userId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("无效Token");
            return false;
        }

        boolean allowed = tokenBucketManager.tryConsume(userId);
        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("访问频率过高，请稍后再试");
            return false;
        }
        return true;
    }
}
