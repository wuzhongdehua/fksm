package com.fksm.common.dto;

import java.util.List;

/**
 * Created by root on 16-4-23.
 */
public class MessageDto<T extends BaseMassageDto> {
    private int type;

    private List<T> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public static MessageDto build(int type, List data){
        MessageDto result = new MessageDto();
        result.setData(data);
        result.setType(type);
        return result;
    }
}
