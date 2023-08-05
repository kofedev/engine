package dev.kofe.engine.node;

import dev.kofe.engine.descriptor.Descriptor;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Node {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY, note: spring.jpa.open-in-view is enabled by default
    private Node parent;

    @OneToMany (mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Node> subs = new ArrayList<>();

    // descriptors
    @OneToMany (mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Descriptor> descriptors = new ArrayList<>();

    // business
    // ...

    // images
    // ...

    private String note;
    private boolean active = true;

    @Transient private boolean done = true; // service; done = true in the case of a fully successful operation
    @Transient private String msg = ""; // service; for the status message

    public void addSubNode (Node subNode) {
        this.subs.add(subNode);
        subNode.setParent(this);
    }

    public void removeSubNode (Node subNode) {
        this.subs.remove(subNode);
        subNode.setParent(null);
    }

    public void addDescriptor (Descriptor descriptor) {
        this.descriptors.add(descriptor);
        descriptor.setNode(this);
    }

    public void removeDescriptor (Descriptor descriptor) {
        this.descriptors.remove(descriptor);
        descriptor.setNode(null);
    }

    @Override
    public String toString () {
        String stringToPrint = "\nNode { " + "id=" + id + ", note=" + note;
        // ...building the parent matter
        if (parent != null) {
            stringToPrint += ", parentId=" + parent.id + ", parentNote=" + parent.getNote();
        } else {
            stringToPrint += ", parentId=" + "no_parent";
        }
        // ...building the subs
        if (subs.size() > 0) {
            stringToPrint += "\n       subs: { ";
            for (Node node : this.subs) {
                stringToPrint +=
                           "\n               Sub { id=" + node.getId() + ", note=" + node.getNote()
                           + ", volume of subs: " + node.getSubs().size() + " }";
            }
            stringToPrint += "\n       } (end of subs)";
        } // if subs
        return stringToPrint + "\n     } (end of node with id=" + this.id + ")";
    }
}
