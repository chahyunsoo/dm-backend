package com.DM.DeveloperMatching.dto.Project;

import com.DM.DeveloperMatching.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TeamMate {
    private String nickName;
    private String introduction;

    public TeamMate(String nickName) {
        this.nickName = nickName;
    }
}
