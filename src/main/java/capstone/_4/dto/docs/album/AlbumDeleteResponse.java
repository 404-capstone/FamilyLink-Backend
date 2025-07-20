package capstone._4.dto.docs.album;

import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;

public class AlbumDeleteResponse extends ApiResponseDto<String> {
    public AlbumDeleteResponse() {
        super(ResponseEnum.DELETE_SUCCESS.getCode(),ResponseEnum.DELETE_SUCCESS.getMessage(),
                "1번 사진 삭제를 성공했습니다.");
    }


}
