package capstone._4.service.redis;

import lombok.extern.slf4j.Slf4j;
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

    public void saveJwt(String key,String value){
        redisTemplate.opsForValue().set(key,value,7, TimeUnit.DAYS);
    }

    public void saveCode(String Key,String value){
        redisTemplate.opsForValue().set(Key,value,10, TimeUnit.MINUTES);
    }

    //jwt를 통해서 refresh토큰을 찾는법. 또는 초대코드로 찾기.
    public Object getData(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
