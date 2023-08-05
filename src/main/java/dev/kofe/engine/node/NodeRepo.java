package dev.kofe.engine.node;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NodeRepo extends JpaRepository <Node, Long> {

    List<Node> findAllByActive(boolean active);
    Node findByNote (String note);
}
