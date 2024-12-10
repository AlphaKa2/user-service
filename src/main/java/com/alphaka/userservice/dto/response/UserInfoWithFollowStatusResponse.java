package com.alphaka.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoWithFollowStatusResponse {

    @Schema(example = "1")
    private Long userId;
    @Schema(example = "ImUser1")
    private String nickname;
    @Schema(example = "/img/default")
    private String profileImage;
    @Schema(example = "true")
    private Boolean followStatus = false;

}
