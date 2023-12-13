package com.DM.DeveloperMatching.dto.User;

import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.dto.Resume.CareerDto;
import com.DM.DeveloperMatching.dto.Resume.HistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//public class ResumeRequest {
//    private String part;
//    private Level level;
//    private String introduction;
//    private List<String> tech;
//    private List<CareerDto> careerList;
//    private List<HistoryDto> history;
//}
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResumeRequest {
    private String part;
    private Level level;
    private String introduction;
    private List<String> tech;
    private List<CareerDto> careerList;
    private List<HistoryDto> history;
    private MultipartFile userImg;
}
