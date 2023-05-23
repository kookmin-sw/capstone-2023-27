package com.hany.capstone.service;

import com.hany.capstone.dto.NounDto;
import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiServiceTest {
    @Autowired
    ApiService apiService = new ApiService();

    @Test
    void nounMatching() {
        //given
        String noun = "반도체";
        int num = 5;
        //when
        List<NounDto> company_list = apiService.nounMatching(noun,num);
        //then
        System.out.println(company_list);
    }
}