package dev.kofe.engine.descriptor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DescriptorRepo extends JpaRepository<Descriptor, Long> {
    List<Descriptor> findAllByNode_Id(long id);
}
