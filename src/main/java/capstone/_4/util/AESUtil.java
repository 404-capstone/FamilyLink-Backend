package capstone._4.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// 암호화 클래스

@Component
public class AESUtil {

    private final String ALGORITHM = "AES";

    private final String SECRET_KEY;

    public AESUtil(@Value("${api.secret_key}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    //암호화 수행
    public String encrypt(String input) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM); //키를 바이트로 변환해 aes 알고리즘용 객체로 만듬
            Cipher cipher = Cipher.getInstance(ALGORITHM); //암호화 도구 가져오기.
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(input.getBytes());  //문자열 바이트 변환하고 암호화 처리.
            return Base64.getEncoder().encodeToString(encrypted); //암호화된 문자 반환
        }
        catch (Exception e) {
            throw new RuntimeException("암호화 실패.");
        }

    }

    //복호화 수행.
    public String decrypt(String input) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encryptedBytes = Base64.getDecoder().decode(input);
            byte[] decryptedBytes=cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        }catch (Exception e) {
            throw new RuntimeException("복호화 실패");
        }

    }
}
