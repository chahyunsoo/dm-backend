package com.DM.DeveloperMatching.dto.Resume;

import com.DM.DeveloperMatching.domain.Career;
import com.DM.DeveloperMatching.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CareerDto {
    private Date startDate;
    private Date endDate;
    private String career;

    public Career toEntity(User user) {
        Career career = new Career();
        career.setUser(user);
        career.setStartDate(this.startDate);
        career.setEndDate(this.endDate);
        career.setContent(this.career);
        return career;
    }
}