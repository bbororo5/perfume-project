package com.bside405.perfume.project.perfume.dto;


import com.bside405.perfume.project.perfume.Perfume;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfumeResponseDTO {
    private Long id;
    private String name;
    private String eName;
    private String brand;
    private String imageURL;

    public PerfumeResponseDTO (Perfume perfume) {
        this.id = perfume.getId();
        this.name = perfume.getName();
        this.eName = perfume.getEnglishName();
        this.brand = perfume.getBrand();
        this.imageURL = perfume.getImageUrl();
    }
}
