package capstone._4.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    private final RedisTemplate<String,Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveJwt(String key,int value){
        try{
            redisTemplate.opsForValue().set(key,value,7, TimeUnit.DAYS);
        }catch (RedisConnectionFailureException e){
            throw new RedisConnectionFailureException("redis 연결문제: "+ e.getMessage());
        }

    }

    public void saveCode(String Key,int value){
        try{
        redisTemplate.opsForValue().set(Key,value,10, TimeUnit.MINUTES);

        }catch (RedisConnectionFailureException e){
            throw new RedisConnectionFailureException("redis 연결문제: "+ e.getMessage());
        }
}
    //jwt를 통해서 refresh토큰을 찾는법. 또는 초대코드로 찾기.
    public Object getData(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            throw new RedisConnectionFailureException("redis 연결문제: "+ e.getMessage());
        }
    }

    public boolean delete(String key){
        try {
            return redisTemplate.delete(key);
        }catch (RedisConnectionFailureException e){
            throw new RedisConnectionFailureException("redis 연결문제: "+ e.getMessage());
        }
    }
}
