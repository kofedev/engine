package dev.kofe.engine.node;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kofe.engine.descriptor.DescriptorDto;
import lombok.Data;

@Data
public class NodeDto {

    private Long id;

    @JsonProperty("parent")
    private NodeParentDto nodeParentDto;

    @JsonProperty("subs")
    private List<NodeDto> subsDto = new ArrayList<>();

    @JsonProperty("descriptors")
    private List<DescriptorDto> descriptorsDto = new ArrayList<>();

    private String note;
    private boolean active;

    private boolean done = true; // service, transient
    private String msg; // service, transient
}
