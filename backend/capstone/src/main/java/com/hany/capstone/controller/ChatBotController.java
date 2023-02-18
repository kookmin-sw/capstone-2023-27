package com.hany.capstone.controller;

import com.hany.capstone.dto.*;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping(value = "/chatbot")
@RestController
public class ChatBotController {
    @Autowired
    private ApiService apiService;

    @RequestMapping(value = "/ask")
    public String chatBotApi(@RequestParam String question){

        Map<Integer,String[]> dic = new HashMap<Integer, String[]>();

        String chat_dic[][] = {{"뜻"}, {"업종","종목"},{"테마","종목"},{"관련","종목"},{"관련","뉴스"},{"현재","주가"},{"현재","등락률"},{"코스피","지수"},{"코스닥","지수"},{".help"}};
        String terms_dic[]= {"기업개요","기업가치","per","eps","추정per","추정eps","pbr","dvr" };
        for (int i =0 ; i< chat_dic.length;i++){
            dic.put(i,chat_dic[i]);
        }
        String [] word = question.split(" ");
        String result = "99";


        boolean flag = false;
        for ( int key : dic.keySet() ){
            for( String w:dic.get(key)){
                if (question.contains(w)){
                    flag = true;
                }
                else{
                    flag= false;
                    break;
                }
            }
            if (flag==true){
                result = Integer.toString(key);
                break;
            }
        }

        if(flag ==false){
            result = "99";
            for(String terms:terms_dic){
                for(String w:word){
                    if(w.equals(terms)){
                        result = terms;
                    }
                }
            }
        }
        System.out.println(result);
        //주식 용어 검색
        if(result.equals("0")) {
            String terms = word[0];
            System.out.println(terms);
            TermsDto meaning = apiService.searchTerms(terms);
            if (meaning != null) {
                return meaning.getMeaning();
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }
        // 검색한 업종의 종목들
        else if(result.equals("1")) {
            String sector = word[0];
            List<PriceDto> ListPriceDto = apiService.sectorInclude(sector, 10);
            if (ListPriceDto != null) {
                String company_name = "";
                for (PriceDto price : ListPriceDto) {
                    company_name += price.getCompany_name() + " ";
                }
                return company_name;
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }
        // 검색한 테마의 종목들
        else if(result.equals("2")){
            String thema = word[0];
            List<PriceDto> ListPriceDto = apiService.themaInclude(thema,10);
            String company_name= "";
            if (ListPriceDto != null) {
                for(PriceDto price:ListPriceDto){
                    company_name+=price.getCompany_name()+" ";
                }
                return company_name;            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }

        }
        // 키워드로 종목 검샥
        else if(result.equals("3")) {
            String keyword = word[0];
            List<NounDto> ListNounDto = apiService.nounMatching(keyword, 10);
            String company_name = "";
            if (ListNounDto != null) {
                for (NounDto nd : ListNounDto) {
                    company_name += nd.getCompany_name() + " ";
                }
                return company_name;
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }
        //최근 키워드 관련 뉴스
        else if(result.equals("4")){
            String keyword = word[0];
            List<NewsDto> news = apiService.newsLike(keyword,5);
            String rinks = "";
            if (news != null) {
                for (NewsDto nd : news) {
                    rinks += nd.getRink() + "\n";
                }
                return rinks;
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }

        else if(result.equals("5")){
            String nameOrTicker = word[0];
            String ticker = "";
            // 검색을 종목명으로 한경우
            if(nameOrTicker.matches("[0-9]+")==false){
                ticker= apiService.stockTicker(nameOrTicker);
                if(ticker==null){
                    return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
                }
            }
            else{
                ticker=nameOrTicker;
            }
            NowPriceDto nowPrice = apiService.stockNowPrice(ticker);
            if( nowPrice !=null){
                return Integer.toString(nowPrice.getEnd_price());
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }

        else if(result.equals("6")){
            String nameOrTicker = word[0];
            String ticker = "";
            // 검색을 종목명으로 한경우
            if(nameOrTicker.matches("[0-9]+")==false){
                ticker= apiService.stockTicker(nameOrTicker);
                if(ticker==null){
                    return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
                }
            }
            else{
                ticker=nameOrTicker;
            }
            NowPriceDto nowRate = apiService.stockNowPrice(ticker);
            if( nowRate !=null){
                return Integer.toString(nowRate.getEnd_price());
            }
            else{
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
        }
        else if(result.equals("7")) {
            MarketIndexDto mi = apiService.StockMarketId("코스피");
            if (mi ==null){
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
            return  "value : "+mi.getNow_value() +" change_value : "+mi.getChange_value()+" change_rate : "+mi.getChange_value();
        }
        else if(result.equals("8")) {
            MarketIndexDto mi = apiService.StockMarketId("코스닥");
            if (mi ==null){
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
            return  "value : "+mi.getNow_value() +" change_value: "+mi.getChange_value()+" change_rate : "+mi.getChange_value();
        }
        //챗봇 도우미
        else if(result.equals("9")) {
            String str = "주식 용어 검색 ex) per{주식 용어} 뜻\n" +
                    "종목별 기업 정보 ex) 삼성전자{종목명,ticker} per{기업개요,기업가치,per,eps,추정per,추정eps,pbr,dvr} \n" +
                    "키워드 관련 뉴스 ex) 반도체{키워드} 관련 뉴스\n" +
                    "키워드 관련 종목 ex) 반도체{키워드} 관련 종목\n" +
                    "종목별 현재 주가 ex) 삼성전자{종목명,ticker} 현재 주가\n" +
                    "종목별 현재 등락률 ex) 삼성전자{종목명,ticker} 현재 등락률\n" +
                    "코스피 코스닥 지수 ex) {코스피 or 코스닥} 지수\n" +
                    "테마 포함 종목 ex) 전기차 테마 종목\n" +
                    "업종 포함 종목 ex) 제약 업종 종목";
            return str;
        }
        else if(result.equals("99")){
            return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";

        }
        else{
            String nameOrTicker = word[0];
            String ticker = "";
            // 검색을 종목명으로 한경우
            if(nameOrTicker.matches("[0-9]+")==false){
                ticker= apiService.stockTicker(nameOrTicker);
                if (ticker ==null){
                    return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
                }
            }
            else{
                ticker=nameOrTicker;
            }
            CompanyInfoDto info = apiService.stockInfo(ticker);
            if (info ==null){
                return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
            }
            String terms = result;
            if(terms =="기업개요"){
                return  info.getCompany_info();
            }
            else if(terms=="기업가치"){
                return  Integer.toString(info.getMarket_cap());
            }
            else if(terms=="per"){
                return  Float.toString(info.getPer());
            }
            else if(terms=="eps"){
                return Integer.toString(info.getEps());
            }
            else if(terms=="추정per"){
                return Float.toString(info.getEst_per());
            }
            else if(terms=="추정eps"){
                return  Integer.toString(info.getEst_eps());
            }
            else if(terms=="pbr"){
                return  Float.toString(info.getPbr());
            }
            else if(terms=="dvr"){
                return  Float.toString(info.getDvr());
            }
        }
        return "질문을 이해 못하겠어요. 사용법 .help를 입력하세요";
    }
}
