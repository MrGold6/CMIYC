package com.ms.connector.dto;

import com.ms.connector.dto.response.data.RestApiResponseData;
import com.ms.connector.dto.response.data.WebsocketResponseData;
import com.ms.connector.dto.response.data.SoapApiResponseData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SearchResponse {

    private List<RestApiResponseData> api1Responses = new ArrayList<>();
    private List<SoapApiResponseData> api2Responses = new ArrayList<>();
    private List<WebsocketResponseData> api3Responses = new ArrayList<>();
}
