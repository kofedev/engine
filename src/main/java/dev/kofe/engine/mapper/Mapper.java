package dev.kofe.engine.mapper;

import dev.kofe.engine.descriptor.Descriptor;
import dev.kofe.engine.descriptor.DescriptorDto;
import dev.kofe.engine.language.Language;
import dev.kofe.engine.language.LanguageDto;
import dev.kofe.engine.node.Node;
import dev.kofe.engine.node.NodeDto;
import dev.kofe.engine.node.NodeParentDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class Mapper {

    private final ModelMapper modelMapper = new ModelMapper();

    private static final Logger logger = LoggerFactory.getLogger(Mapper.class);

    public static <R, E> List<R> convertList(List<E> list, Function<E, R> converter) {
        return (list != null) ? list.stream().map(converter).collect(Collectors.toList()) : null;
    }

    // Language
    public LanguageDto convertToLanguageDto (Language language) {
        if (language != null) {
            LanguageDto languageDto = modelMapper.map(language, LanguageDto.class);
            return languageDto;
        } else {
            logger.error("Parameter 'language' for the mapper can not be null");
            return null;
        }
    }

    // Descriptor
    public DescriptorDto convertToDescriptorDto (Descriptor descriptor) {
        if (descriptor != null) {
            DescriptorDto descriptorDto = modelMapper.map(descriptor, DescriptorDto.class);
            return descriptorDto;
        } else {
            logger.error("Parameter 'descriptor' for the mapper can not be null");
            return null;
        }
    }

    // Node
    public NodeDto convertToNodeDto (Node node) {
        if (node != null) {
            NodeDto nodeDto = modelMapper.map(node, NodeDto.class);
            nodeDto.setSubsDto(Mapper.convertList(node.getSubs(), (item) -> convertToNodeDto(item)));
            nodeDto.setDescriptorsDto(Mapper.convertList(node.getDescriptors(), (item) -> convertToDescriptorDto(item)));
            nodeDto.setNodeParentDto(convertToNodeParentDto(node));
            return nodeDto;
        } else {
            logger.error("Parameter 'node' for the mapper can not be null");
            return null;
        }
    }

    // Node, parent case
    public NodeParentDto convertToNodeParentDto (Node node) {
        if (node != null) {
            if (node.getParent() != null) {
                NodeParentDto nodeParentDto = modelMapper.map(node.getParent(), NodeParentDto.class);
                nodeParentDto.setDescriptorsDto(Mapper.convertList(node.getParent().getDescriptors(), (item) -> convertToDescriptorDto(item)));
                return nodeParentDto;
            } else {
                return null;
            }
        } else {
            logger.error("Parameter 'node' (parent case) for the mapper can not be null");
            return null;
        }
    }

}
