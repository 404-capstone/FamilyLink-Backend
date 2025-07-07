package capstone._4.filter;

import capstone._4.enums.ErrorCode;
import capstone._4.exception.TokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException | IllegalArgumentException e){
            setErrorResponse(response, ErrorCode.TOKEN_EXPIRED,e.getMessage());
        }catch (TokenException e){
            setErrorResponse(response,ErrorCode.TOKEN_INVALID,e.getMessage());
        }catch(RedisConnectionFailureException e){
            setErrorResponse(response,ErrorCode.CONNECT_FAILED,e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            setErrorResponse(response, ErrorCode.EXCEPTION,e.getMessage());
        }

    }

    private void setErrorResponse(HttpServletResponse httpServletResponse,ErrorCode error ,String message){
        ObjectMapper objectMapper = new ObjectMapper();
        httpServletResponse.setStatus(error.getStaus());
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(error.getStaus(),error.getMessage(),message);
        try{
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Data
    static class ErrorResponse{
        private final Integer code;
        private final String message;
        private final String data;
    }
}
