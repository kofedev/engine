package dev.kofe.engine.descriptor;

import dev.kofe.engine.language.Language;
import dev.kofe.engine.node.Node;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Descriptor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;
    private String brief;

    private String fullDescr; // as a link to the file with the full description

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    private Node node;
}
