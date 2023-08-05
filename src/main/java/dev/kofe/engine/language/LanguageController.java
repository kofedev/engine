package dev.kofe.engine.language;

import dev.kofe.engine.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/*      Languages API
 *      [Kofe Simple Engine]
 *
 *      POST /languages/init                :   initialization (create first language in the System)
 *                                          :   if body is null then empty default language will be created
 *                                          :
 *                                          :   JSON
 *                                          :   {
 *                                          :       "code": "LANG",
 *                                          :       "name": "Language",
 *                                          :       "note": "Note",
 *                                          :       "active": "true"
 *                                          :   }
 *                                          :
 *                                          :   200 = ok
 *                                          :   204 = Initial language has not been created
 *                                          :   400 = Initial language has not been created: the Code is required
 *
 *      POST /languages                     :   add new language
 *                                          :   200 = ok
 *                                          :   204 = New language has not been added
 *                                          :   400 = New language has not been added: the Code is required
 *
 *      PUT  /languages                     :   update base data of the language (affects on code, name, and note fields)
 *                                          :   200 = ok
 *                                          :   204 = Language has not been updated
 *                                          :   400 = Language has not been updated: the ID and Code are required
 *
 *      PUT  /languages/activate/{id}       :   activate language by ID (set active status to as true)
 *                                          :   200 = ok
 *                                          :   404 = Language not found
 *
 *      PUT  /languages/deactivate/{id}     :   de-activate language by ID (set active status to as false)
 *                                          :   It is not possible to de-activate a lone active language
 *                                          :   It is not possible to de-activate a lone language in the system (initial language)
 *                                          :   It is not possible to de-activate a default language
 *                                          :   200 = ok
 *                                          :   404 = Language not found
 *
 *      PUT  /languages/default/{id}        :   set language as default by ID
 *                                          :   It is not possible to set inactive language as default language
 *                                          :   200 = ok
 *                                          :   404 = Language not found
 *
 *      GET  /languages/default             :   get default language
 *                                          :   200 = ok
 *                                          :   404 = Default language not found
 *
 *      GET  /languages/active              :   get all active languages
 *                                          :   200 = ok
 *
 *      GET  /languages                     :   get all languages
 *                                          :   200 = ok
 *
 *      GET  /languages/{id}                :   get language by ID
 *                                          :   200 = ok
 *                                          :   404 = Language not found
 *
 *      DELETE /language/{id}               :   delete language by ID
 *                                          :   400 = Language with is initial language and can not be deleted
 *                                          :   404 = Language not found
 *
 *      Important note. Initialization (operation for creation of the initial language: POST /languages/init)
 *                      is a crucial operation for the whole system.
 *                      The system can not work without at least one language:
 *                      the initial language is an "entering point" for the system functionality.
 *                      The initial language (flag 'initial' = true) can not be deleted.
 *
 *      Note about Active and Bydefault cases.  Language may have active status (flag 'active' = true)
 *                                              and default status (flag 'bydefault' = true).
 *                                              Default language have to be active.
 *                                              There can be only one default language.
 *
 *      JSON:
 *              {
 *                  "id": N,
 *                  "code": "CODE",
 *                  "name": "Language",
 *                  "note": "Note about the language",
 *                  "initial": false,
 *                  "bydefault": true,
 *                  "active": true,
 *              }
 *
 *      JSON response contains two additional service fields (transient):
 *                      done = true    :  all asked data is provided
 *                      done = false   :  data is not provided fully
 *                      msg  = message :  message about result of the request
 */

@RestController
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;
    private final Mapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(LanguageController.class);

    @Autowired
    public LanguageController (LanguageService languageService, Mapper mapper) {
        this.languageService = languageService;
        this.mapper = mapper;
    }

    @PostMapping("/init")
    public ResponseEntity<LanguageDto> createNewLanguage (@RequestBody(required = false) LanguageDto languageDto) {
        if (languageDto != null && languageDto.getCode() == null) {
            logger.error("Initial language has not been created: the Code is required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Language language = languageService.initializationFirstLanguage(languageDto);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Initial language has not been created");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("")
    public ResponseEntity<LanguageDto> addNewLanguage (@RequestBody(required = true) LanguageDto languageDto) {
        if (languageDto.getCode() == null) {
            logger.error("New language has not been added: the Code is required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Language language = languageService.addNewLanguage(languageDto);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("New language " + languageDto.getCode() + " has not been added");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("")
    public ResponseEntity<LanguageDto> updateLanguageBasicData(@RequestBody(required = true) LanguageDto languageDto) {
        if (languageDto.getId() == null || languageDto.getCode() == null) {
            logger.error("Language has not been updated: the ID and Code are required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Language language = languageService.updateLanguageBasicData(languageDto);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Language " + languageDto.getCode() + " has not been updated");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<LanguageDto> activateLanguage (@PathVariable long id) {
        Language language = languageService.setLanguageActiveStatus(id, true);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Language with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<LanguageDto> deActivateLanguage (@PathVariable long id) {
        Language language = languageService.setLanguageActiveStatus(id, false);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Language with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/default/{id}")
    public ResponseEntity<LanguageDto> setDefaultLanguage (@PathVariable long id) {
        Language language = languageService.setDefaultLanguage(id);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Language with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/default")
    public ResponseEntity<LanguageDto> getDefaultLanguage () {
        Language language = languageService.findDefaultLanguage();
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.warn("Default language not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<LanguageDto>> getAllActiveLanguages () {
        List<Language> activeLanguages = languageService.findActiveLanguages();
        return new ResponseEntity<>(
                    Mapper.convertList(activeLanguages, (item) -> mapper.convertToLanguageDto(item)),
                    HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<LanguageDto>> getAllLanguages () {
        List<Language> activeLanguages = languageService.findAllLanguages();
        return new ResponseEntity<>(
                    Mapper.convertList(activeLanguages, (item) -> mapper.convertToLanguageDto(item)),
                    HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LanguageDto> getLanguageById (@PathVariable long id) {
        Language language = languageService.findLanguageById(id);
        if (language != null) {
            return new ResponseEntity<>(mapper.convertToLanguageDto(language), HttpStatus.OK);
        } else {
            logger.error("Language with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLanguageById(@PathVariable long id) {
        Language language = languageService.findLanguageById(id);
        if (language != null) {
            if (!language.isInitial()) {
                languageService.deleteLanguageById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.warn("Language with ID=" + id + " is initial language and can not be deleted");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.error("Language with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
