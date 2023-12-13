package com.DM.DeveloperMatching.dto.Resume;

import com.DM.DeveloperMatching.domain.Career;
import com.DM.DeveloperMatching.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CareerDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String career;

    public Career toEntity(User user) throws ParseException {
        Career career = new Career();
        career.setUser(user);
        if(this.startDate != null) {
            career.setStartDate(this.startDate);
        }
        if(this.endDate != null) {
            career.setEndDate(this.endDate);
        }
        career.setContent(this.career);
        return career;
    }

//    public CareerDto(String startDate, String endDate, String career) throws ParseException {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date date1 = formatter.parse(startDate);
//        Date date2 = formatter.parse(startDate);
//        this.startDate = date1;
//        this.endDate = date2;
//        this.career = career;
//    }
}