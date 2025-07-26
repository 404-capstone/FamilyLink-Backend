package capstone._4.service;

import capstone._4.dto.user.TokenDto;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final Cache<String, TokenDto> loginCodeCache;

    public void store(String key,TokenDto data){
        loginCodeCache.put(key, data);
    }

    public TokenDto retrieveToken(String key){
        TokenDto data=loginCodeCache.getIfPresent(key); //존재할시반환
        if(data==null){
            throw new NoSuchElementException("로그인 정보가 존재하지 않습니다.");
        }
        loginCodeCache.invalidate(key);
        return data;
    }
}
