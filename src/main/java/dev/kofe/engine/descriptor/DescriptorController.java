package dev.kofe.engine.descriptor;

import dev.kofe.engine.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*      Descriptors API
 *      [Kofe Simple Engine]
 *
 *      GET /descriptors/{id}               :   get descriptor by ID
 *                                          :   200 = ok
 *                                          :   204 = Initial language has not been created
 *                                          :   404 = Descriptor with ID not found
 *
 *      GET /descriptors                    :   get all descriptors
 *                                          :   200 = ok
 *
 *      GET /descriptors/node/{id}          :   get descriptors by node_id
 *                                          :   200 = ok
 */

@RestController
@RequestMapping("/descriptors")
public class DescriptorController {

    private final DescriptorService descriptorService;
    private final Mapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(DescriptorController.class);

    @Autowired
    public DescriptorController (DescriptorService descriptorService,
                                 Mapper mapper) {
        this.descriptorService = descriptorService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescriptorDto> getDescriptorById (@PathVariable long id) {
        Descriptor descriptor = descriptorService.findDescriptorById(id);
        if (descriptor != null) {
            return new ResponseEntity<>(mapper.convertToDescriptorDto(descriptor), HttpStatus.OK);
        } else {
            logger.warn("Descriptor with ID=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/node/{id}")
    public ResponseEntity<List<DescriptorDto>> getAllDescriptorsByNodeId (@PathVariable long id) {
        List<Descriptor> descriptors = descriptorService.findAllDescriptorsByNodeId(id);
        return new ResponseEntity<>(
                Mapper.convertList(descriptors, (item) -> mapper.convertToDescriptorDto(item)),
                HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<DescriptorDto>> getAllDescriptors () {
        List<Descriptor> descriptors = descriptorService.findAllDescriptors();
        return new ResponseEntity<>(
                Mapper.convertList(descriptors, (item) -> mapper.convertToDescriptorDto(item)),
                HttpStatus.OK);
    }

}
