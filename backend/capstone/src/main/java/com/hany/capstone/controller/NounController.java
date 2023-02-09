package com.hany.capstone.controller;

import com.hany.capstone.dto.*;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/noun")
public class NounController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/matching")
    public List<NounDto> getNounMatching(@RequestParam String noun,@RequestParam int num){
        List<String> nounLi = List.of(noun.strip().split(" "));
        List<NounDto> noun_list;
        if(nounLi.size() <= 1){
            noun_list = apiService.nounMatching(noun.strip(),num);
        }
        else{
            int len = nounLi.size();
            noun_list = apiService.multNounMatching(noun.strip() ,num,nounLi,len);
        }
        return noun_list;
    }
}
