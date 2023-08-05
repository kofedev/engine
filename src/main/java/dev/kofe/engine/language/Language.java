package dev.kofe.engine.language;

import dev.kofe.engine.descriptor.Descriptor;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/*
 *  Language model
 *  [Kofe Simple Engine]
 *  *****************************************************************
 *  id        -- is a database ID
 *  code      -- is a short name: "ENG", "POL", "LT" ...
 *  name      -- is a long name: "English", "Polish", "Lithuanian" ...
 *  note      -- is a 255-max notes
 *  initial   -- initial language is a basic language (first language in the system); can not be deleted
 *  bydefault -- flag: language set as a default language to use
 *  active    -- is an active flag
 *  *****************************************************************
 */

@Entity
@Data
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String code; // basic data
    private String name; // basic data
    private String note; // basic data

    private boolean initial = false;
    private boolean bydefault = false;
    private boolean active = true;

    @OneToMany (mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Descriptor> descriptors = new ArrayList<>();

    @Transient private boolean done = true; // service field; done = true in the case of a fully successful operation
    @Transient private String msg; // service field; for the message

    public void addDescriptor (Descriptor descriptor) {
        this.descriptors.add(descriptor);
        descriptor.setLanguage(this);
    }

    public void removeDescriptor (Descriptor descriptor) {
        this.descriptors.remove(descriptor);
        descriptor.setLanguage(null);
    }
}
