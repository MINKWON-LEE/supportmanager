package com.mobigen.snet.supportagent.models;

import org.apache.ibatis.type.Alias;

@Alias("ResultDtoList")
public class ResultDto extends Entity {

    private String grpname;
    private String name;
    private String result;
    private String itemCode;
    private int count;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getGrpname() {
        return grpname;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setGrpname(String grpname) {
        this.grpname = grpname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
