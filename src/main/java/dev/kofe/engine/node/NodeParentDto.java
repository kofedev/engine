package dev.kofe.engine.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kofe.engine.descriptor.DescriptorDto;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class NodeParentDto {

    private Long id;

    @JsonProperty("descriptors")
    private List<DescriptorDto> descriptorsDto = new ArrayList<>();

    private String note;
    private boolean active;

    private boolean done = true; // service, transient
    private String msg; // service, transient

}
