package dev.kofe.engine.language;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kofe.engine.descriptor.Descriptor;
import dev.kofe.engine.descriptor.DescriptorDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LanguageDto {
    private Long id;
    private String code;
    private String name;
    private String note;
    private boolean initial;
    private boolean bydefault;
    private boolean active;

    private boolean done = true; // service field (Transient)
    private String msg;   // service field (Transient)
}
