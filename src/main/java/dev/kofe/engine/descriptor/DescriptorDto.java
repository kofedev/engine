package dev.kofe.engine.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kofe.engine.language.LanguageDto;
import lombok.Data;

@Data
public class DescriptorDto {
    private Long id;
    private String title;
    private String brief;
    private String fullDescr;
    @JsonProperty("language") private LanguageDto language;
}
