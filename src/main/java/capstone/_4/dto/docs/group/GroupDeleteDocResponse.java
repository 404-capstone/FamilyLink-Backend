package capstone._4.dto.docs.group;

import capstone._4.dto.ApiResponseDto;

public class GroupDeleteDocResponse extends ApiResponseDto<Integer> {

    public GroupDeleteDocResponse(int code, String message, Integer data) {
        super(code, message, data);
    }
}
