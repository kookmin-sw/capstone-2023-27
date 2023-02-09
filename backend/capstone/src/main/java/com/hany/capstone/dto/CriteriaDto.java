package com.hany.capstone.dto;

import lombok.Data;

@Data
public class CriteriaDto {
    private int pageNum;	//페이지 번호

    private int size;	//한 페이지당 출력 DATA 개수

    public CriteriaDto(){
        this(1,10);
    }
    public CriteriaDto(int pageNum,int size) {
        this.pageNum=pageNum;
        this.size=size;
    }

    public int getSkip() {
        return this.pageNum = (pageNum-1) * size;
    }
}
