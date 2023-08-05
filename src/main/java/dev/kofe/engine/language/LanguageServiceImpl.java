package dev.kofe.engine.language;

import dev.kofe.engine.descriptor.DescriptorService;
import dev.kofe.engine.node.Node;
import dev.kofe.engine.node.NodeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepo languageRepo;
    private final NodeRepo nodeRepo;
    private final DescriptorService descriptorService;
    private static final Logger logger = LoggerFactory.getLogger(LanguageServiceImpl.class);

    @Value("${engine.init.language.code}") private String defaultLanguageCode;
    @Value("${engine.init.language.name}") private String defaultLanguageName;
    @Value("${engine.init.language.note}") private String defaultLanguageNote;

    @Autowired
    public LanguageServiceImpl (LanguageRepo languageRepo,
                                NodeRepo nodeRepo,
                                DescriptorService descriptorService) {
        this.languageRepo = languageRepo;
        this.nodeRepo = nodeRepo;
        this.descriptorService = descriptorService;
    }

    @Transactional
    public Language initializationFirstLanguage (LanguageDto newLanguageAsDto) {
        Language language = null;
        // checking: is there any language in the system
        if (languageRepo.count() == 0) {
            // creating the first language - basics default language
            language = new Language();
            if (newLanguageAsDto != null) {
                language.setCode(newLanguageAsDto.getCode());
                language.setName(newLanguageAsDto.getName());
                language.setNote(newLanguageAsDto.getNote());
                language.setMsg("Language has been created");
            } else {
                language.setCode(defaultLanguageCode);
                language.setName(defaultLanguageName);
                language.setNote(defaultLanguageNote);
                language.setMsg("Language has been created using a default pattern");
            }
            language.setInitial(true);
            language.setBydefault(true);
            language = languageRepo.save(language);
        } else {
            language = languageRepo.findTopByOrderByIdAsc();
            if (language != null) {
                language.setDone(false);
                language.setMsg("Language is already presented. Initialization is not needed");
            }
        }

        return language;
    }

    @Transactional
    public Language setLanguageActiveStatus (Long languageId, boolean status) {
        Language language = languageRepo.findById(languageId).orElse(null);
        if (language != null) {
            // if language is alone in the system and new status is false...
            if (languageRepo.count() == 1) {
                language.setDone(false);
                language.setMsg("It is not possible to change the active status for a lone language in the system");
            } else {
                if (status) {
                    // activated
                    language.setActive(true);
                    language.setMsg("Language has been activated");
                } else {
                    // deactivated
                    if (language.isBydefault()) {
                        // language is bydefault
                        language.setDone(false);
                        language.setMsg("It is not possible to deactivate default language");
                    } else {
                        // language is not bydefault, go on
                        if (languageRepo.findByActive(true).size() > 1) {
                            // it is not lone active language, go on
                            language.setActive(false);
                            language.setMsg("Language has been de-activated");
                        } else {
                            // it is lone active language
                            language.setDone(false);
                            language.setMsg("It is not possible to change the active status for a lone active language");
                        }
                    }
                }
            }
        }

        return language;
    }

    @Transactional
    public Language setDefaultLanguage (Long languageId) {
        Language language = languageRepo.findById(languageId).orElse(null);
        if (language != null) {
            if (!language.isActive()) {
                // It is not possible to set the bydefault status for the inactive language
                language.setDone(false);
                language.setMsg("It is not possible to set the bydefault status for the inactive language");
            } else {
                // language is active, go on
                clearBydefaultStatus();
                language.setBydefault(true);
                language = languageRepo.save(language);
                language.setMsg("Language " + language.getCode() + " set as default");
            }
        }

        return language;
    }

    private void clearBydefaultStatus () {
        Language language = languageRepo.findByBydefault(true);
        if (language != null) {
            language.setBydefault(false);
            languageRepo.save(language);
        }
    }

    public Language findDefaultLanguage () {
        return languageRepo.findByBydefault(true);
    }

    public Language findLanguageById (Long id) {
        return languageRepo.findById(id).orElse(null);
    }

    @Transactional
    public Language addNewLanguage(LanguageDto languageDto) {
        Language language = new Language();
        language.setCode(languageDto.getCode());
        language.setName(languageDto.getName());
        language.setNote(languageDto.getNote());
        language.setActive(languageDto.isActive());
        language = languageRepo.save(language);

        // Clone descriptors for new language
        List<Node> nodes = nodeRepo.findAll();
        for (Node node : nodes) {
            node.addDescriptor(descriptorService.createNewDescriptorForLanguage(language));
        }

        language.setMsg("Language " + languageDto.getCode() + " has been added. "
                        + "Descriptors has been cloned for " + nodes.size() + " nodes");

        return language;
    }

    @Transactional
    public Language updateLanguageBasicData (LanguageDto languageDto) {
        Language language = languageRepo.findById(languageDto.getId()).orElse(null);
        if (language != null) {
            language.setCode(languageDto.getCode());
            language.setName(languageDto.getName());
            language.setNote(languageDto.getNote());
            language = languageRepo.save(language);

            language.setMsg("Language " + languageDto.getCode() + " has been updated");
        }

        return language;
    }

    public List<Language> findActiveLanguages () {
        return languageRepo.findByActive(true);
    }

    public List<Language> findAllLanguages () {
        return languageRepo.findAll();
    }

    public void deleteLanguageById (long id) {
        languageRepo.deleteById(id);
    }

}
