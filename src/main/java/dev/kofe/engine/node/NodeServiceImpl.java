package dev.kofe.engine.node;

import dev.kofe.engine.common.DoubleResult;
import dev.kofe.engine.descriptor.Descriptor;
import dev.kofe.engine.descriptor.DescriptorDto;
import dev.kofe.engine.descriptor.DescriptorService;
import dev.kofe.engine.language.Language;
import dev.kofe.engine.language.LanguageRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NodeServiceImpl implements NodeService {

    private final NodeRepo nodeRepo;
    private final LanguageRepo languageRepo;
    private final DescriptorService descriptorService;
    private static final Logger logger = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Autowired
    public NodeServiceImpl(NodeRepo nodeRepo,
                           LanguageRepo languageRepo,
                           DescriptorService descriptorService) {
        this.nodeRepo = nodeRepo;
        this.languageRepo = languageRepo;
        this.descriptorService = descriptorService;
    }

    public List<Node> findAllNodes () {
        return nodeRepo.findAll();
    }

    public List<Node> findAllActiveNodes () {
        return nodeRepo.findAllByActive(true);
    }

    @Transactional
    public Node addNewNodeAndExpandEmptyDescriptors (NodeDto nodeToAdd) {
        Node parent = null;
        // Determining the parent
        if (nodeToAdd.getNodeParentDto() != null) {
            parent = nodeRepo.findById(nodeToAdd.getNodeParentDto().getId()).orElse(null);
            if (parent == null) {
                logger.error("Parent node with ID=" + nodeToAdd.getNodeParentDto().getId() + " not found");
                return null;
            }
        }
        // Creating process
        Node newNode = new Node();
        newNode.setActive(nodeToAdd.isActive());
        newNode.setNote(nodeToAdd.getNote());
        nodeRepo.save(newNode);
        if (parent != null) {
            parent.addSubNode(newNode);
        }
        // Expanding the descriptors
        List<Language> languages = languageRepo.findAll();
        for (Language language : languages) {
            newNode.addDescriptor(descriptorService.createNewDescriptorForLanguage(language));
        }
        // final matters
        newNode.setMsg( "Node has been added. Parent: "
                + ((parent == null) ? "null" : ("id=" + nodeToAdd.getNodeParentDto().getId()))
                + ". Expanded " + newNode.getDescriptors().size() + " descriptor(s)" );

        return newNode;
    }

    private boolean isPossibleToRelocate (Long nodeId, Long destinationParentId) {
        boolean decision = true;
        if (nodeId != null) {
            if (destinationParentId != null) {
                Node parent = nodeRepo.findById(destinationParentId).orElse(null);
                while (parent != null) {
                    Long parentId = parent.getId();
                    if (parentId.equals(nodeId)) {
                        decision = false;
                        break;
                    }
                    if (parent.getParent() == null) {
                        break;
                    }
                    parent = nodeRepo.findById(parent.getParent().getId()).orElse(null);
                }
            }
        } else {
            logger.warn("Relocate: node ID is null");
            decision = false;
        }

        return decision;
    }

    public DoubleResult<Boolean, Boolean> getRelocationDecision (long idNode, long idDest) {
        Node node = nodeRepo.findById(idNode).orElse(null);
        Node nodeDest = nodeRepo.findById(idDest).orElse(null);
        if (node != null && nodeDest != null) {
            return new DoubleResult<>(isPossibleToRelocate(idNode, idDest), true);
        } else {
              logger.warn("Node(s) not found:"
                            + (node==null ? " id=" + idNode : "")
                            + (nodeDest==null ? " id=" + idDest : ""));
            return new DoubleResult<>(null, false);
        }
    }

    @Transactional
    public Node relocateToAnotherParentNode (Long nodeToRelocateId, Long destinationParentId) {
        Node nodeToRelocate = nodeRepo.findById(nodeToRelocateId).orElse(null);
        if (nodeToRelocate == null) {
            logger.error("Relocate: node is null: " + " node id=" + nodeToRelocateId);
        } else {
            if (isPossibleToRelocate(nodeToRelocateId, destinationParentId)) {
                do {
                    // get destination parent
                    Node destinationParent = null;
                    if (destinationParentId != null) {
                        destinationParent = nodeRepo.findById(destinationParentId).orElse(null);
                        if (destinationParent == null) {
                            nodeToRelocate = null;
                            logger.error("Relocate: destination node is null: " + " node id=" + destinationParentId);
                            break;
                        }
                    }
                    // get current parent
                    if (destinationParent != null) {
                        destinationParent.addSubNode(nodeToRelocate);
                    }
                    nodeToRelocate.setParent(destinationParent);
                    break;
                } while (true);
            } else {
                nodeToRelocate.setDone(false);
                nodeToRelocate.setMsg("It's no possible to relocate the node with id=" +
                        nodeToRelocateId + " to the node with id=" + destinationParentId);
                logger.warn("It's no possible to relocate the node with id=" +
                        nodeToRelocateId + " to the node with id=" + destinationParentId);
            }
        }

        return nodeToRelocate;
    }

    @Transactional
    public Node updateNodeByActiveStatusNoteDescriptors(NodeDto nodeDto) {
        Node node = nodeRepo.findById(nodeDto.getId()).orElse(null);
        if (node != null) {
            // basic matter
            node.setActive(nodeDto.isActive());
            node.setNote(nodeDto.getNote());
            // descriptors matter
            for (DescriptorDto descriptorDto : nodeDto.getDescriptorsDto()) {
                // try to get a descriptor
                Descriptor descriptor = descriptorService.findDescriptorById(descriptorDto.getId());
                if (descriptor != null) {
                    descriptor.setTitle(descriptorDto.getTitle());
                    descriptor.setBrief(descriptorDto.getBrief());
                    descriptor.setFullDescr(descriptorDto.getFullDescr());
                } else {
                    node.setDone(false);
                    node.setMsg(node.getMsg() + "Descriptor with id=" + descriptorDto.getId() + " not found." + " | ");
                    logger.warn("Descriptor with id=" + descriptorDto.getId() + " not found");
                }
            } // for
            nodeRepo.save(node);
        } else {
            // in case of node == null
            logger.error("Node with id=" + nodeDto.getId() + " not found");
        }

        return node;
    }

    public Node findNodeById (long id) {
        return nodeRepo.findById(id).orElse(null);
    }

    @Transactional
    public void deleteNodeById(long id) {
        nodeRepo.deleteById(id);
    }

}
