package dev.kofe.engine.descriptor;

import dev.kofe.engine.language.Language;
import java.util.List;

public interface DescriptorService {
    Descriptor createNewDescriptorForLanguage (Language language);
    Descriptor findDescriptorById (Long id);
    List<Descriptor> findAllDescriptors ();
    List<Descriptor> findAllDescriptorsByNodeId (Long id);
}
