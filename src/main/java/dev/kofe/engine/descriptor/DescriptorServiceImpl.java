package dev.kofe.engine.descriptor;

import dev.kofe.engine.language.Language;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DescriptorServiceImpl implements DescriptorService {

    private final DescriptorRepo descriptorRepo;

    @Autowired
    public DescriptorServiceImpl(DescriptorRepo descriptorRepo) {
        this.descriptorRepo = descriptorRepo;
    }

    @Transactional
    public Descriptor createNewDescriptorForLanguage (Language language) {
        Descriptor descriptor = new Descriptor();
        descriptorRepo.save(descriptor);
        language.addDescriptor(descriptor);
        return descriptor;
    }

    public Descriptor findDescriptorById (Long id) {
        return descriptorRepo.findById(id).orElse(null);
    }
    public List<Descriptor> findAllDescriptors () {
        return descriptorRepo.findAll();
    }
    public List<Descriptor> findAllDescriptorsByNodeId (Long id) {
        return descriptorRepo.findAllByNode_Id(id);
    }

}
