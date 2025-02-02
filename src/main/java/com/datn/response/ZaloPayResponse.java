package com.datn.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZaloPayResponse {
    private String orderUrl;
    private int returnCode;
    private String returnMessage;
}
