package com.hany.capstone;

import com.hany.capstone.dto.CriteriaDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapstoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args);
    }
//    public List<NewsDto> getList(CriteriaDto criteriaDto) {
//        return mapper.getListWithPaging(criteriaDto);
//    }

}
