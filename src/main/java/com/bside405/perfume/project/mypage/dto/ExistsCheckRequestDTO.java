package com.bside405.perfume.project.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExistsCheckRequestDTO {

    @JsonProperty("ids")
    List<Long> ids;
}
