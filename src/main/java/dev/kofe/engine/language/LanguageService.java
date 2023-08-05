package dev.kofe.engine.language;

import java.util.List;

public interface LanguageService {
    Language initializationFirstLanguage (LanguageDto newLanguageAsDto);
    Language setLanguageActiveStatus (Long languageId, boolean status);
    Language setDefaultLanguage (Long languageId);
    Language findDefaultLanguage ();
    Language findLanguageById (Long id);
    Language addNewLanguage(LanguageDto languageDto);
    Language updateLanguageBasicData (LanguageDto languageDto);
    List<Language> findActiveLanguages ();
    List<Language> findAllLanguages ();
    void deleteLanguageById (long id);
}
