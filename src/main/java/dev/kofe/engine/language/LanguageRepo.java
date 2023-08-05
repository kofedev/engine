package dev.kofe.engine.language;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LanguageRepo extends JpaRepository<Language, Long> {
    Language findTopByOrderByIdAsc();
    Language findByBydefault(boolean bydefault);
    List<Language> findByActive(boolean active);
}
