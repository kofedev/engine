package dev.kofe.engine.node;

import dev.kofe.engine.common.DoubleResult;
import dev.kofe.engine.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*      Nodes API
 *      [Kofe Simple Engine]
 *
 *      POST /nodes                         :   add new node with empty descriptors
 *                                          :
 *                                          :   JSON
 *                                          :   {
 *                                          :       "active": true,
 *                                          :       "note": "note",
 *                                          :       "parent": {
 *                                          :           "id": 1
 *                                          :       }
 *                                          :   }
 *                                          :
 *                                          :   200 = ok
 *                                          :   204 = node has not been added
 *
 *      GET /nodes                          :   get all nodes
 *                                          :   200 = ok
 *
 *      GET /nodes/active                   :   get all active nodes
 *                                          :   200 = ok
 *
 *      PUT /nodes/relocate/{idNode}/{idDest}   :   relocation node to new "parent node" (destination)
 *                                              :   200 = ok
 *                                              :   404 = node and/or destination node not found
 *
 *      GET /nodes/decision/{idNode}/{idDest}   :   provide decision about possibility to relocation
 *                                              :   decision is not necessary if destination is null (root)
 *                                              :   200 = decision is positive
 *                                              :   204 = decision is negative
 *                                              :   404 = node and/or destination node not found
 *
 *      PUT /nodes/relocate/{idNode}        :   relocation node to the "root" (with parent = null)
 *                                          :   200 = ok
 *                                          :   404 = node not found
 *
 *      PUT /nodes                          :   update node
 *                                          :   reflects on fields: descriptors, active status, note
 *                                          :   ignores the fields: subs, parent
 *                                          :   it is a route to update descriptors
 *                                          :   200 = ok
 *                                          :   400 = node has not been updated: the ID parameter is required
 *                                          :   404 = node not found
 *
 *      GET /nodes/{id}                     :   get node by ID
 *                                          :   200 = ok
 *                                          :   404 = node not found
 *
 *      DELETE /nodes/{id}                  :   delete node by ID
 *                                          :   200 = ok
 *                                          :   404 = node not found
 */

@RestController
@RequestMapping("/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final Mapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    public NodeController (NodeService nodeService, Mapper mapper) {
        this.nodeService = nodeService;
        this.mapper = mapper;
    }

    @PostMapping("")
    public ResponseEntity<NodeDto> addNewNodeWithEmptyDescriptors (@RequestBody NodeDto nodeToAdd) {
        Node node = nodeService.addNewNodeAndExpandEmptyDescriptors(nodeToAdd);
        if (node != null) {
            return new ResponseEntity<>(mapper.convertToNodeDto(node), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<NodeDto>> getAllNodes () {
        List<Node> nodes = nodeService.findAllNodes();
        return new ResponseEntity<>(Mapper.convertList(nodes, (item) -> mapper.convertToNodeDto(item)), HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<NodeDto>> getAllActiveNodes () {
        List<Node> nodes = nodeService.findAllActiveNodes();
        return new ResponseEntity<>(Mapper.convertList(nodes, (item) -> mapper.convertToNodeDto(item)), HttpStatus.OK);
    }

    @PutMapping("/relocate/{idNode}/{idDest}")
    public ResponseEntity<NodeDto> relocateNodeToNewParent (@PathVariable long idNode, @PathVariable long idDest) {
        Node relocatedNode = nodeService.relocateToAnotherParentNode(idNode, idDest);
        if (relocatedNode != null) {
            return new ResponseEntity<>(mapper.convertToNodeDto(relocatedNode), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/relocate/{idNode}")
    public ResponseEntity<NodeDto> relocateNodeToRoot (@PathVariable long idNode) {
        Node relocatedNode = nodeService.relocateToAnotherParentNode(idNode, null);
        if (relocatedNode != null) {
            return new ResponseEntity<>(mapper.convertToNodeDto(relocatedNode), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("")
    public ResponseEntity<NodeDto> partlyUpdateNode (@RequestBody NodeDto nodeDto) {
        if (nodeDto.getId() != null) {
            Node updatedNode = nodeService.updateNodeByActiveStatusNoteDescriptors(nodeDto);
            if (updatedNode != null) {
                return new ResponseEntity<>(mapper.convertToNodeDto(updatedNode), HttpStatus.OK);
            } else {
                logger.error("Node with id=" + nodeDto.getId() + " not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logger.error("Node has not been updated: the ID parameter is required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/decision/{idNode}/{idDest}")
    public ResponseEntity<?> getRelocationDecision (@PathVariable long idNode, @PathVariable(required = false) long idDest) {
        DoubleResult<Boolean, Boolean> doubleResult = nodeService.getRelocationDecision(idNode, idDest);
        if (doubleResult.second()) {
            if (doubleResult.first()) {
                return new ResponseEntity<>(HttpStatus.OK); // 200
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeDto> getNodeById (@PathVariable long id) {
        Node node = nodeService.findNodeById(id);
        if (node != null) {
            return new ResponseEntity<>(mapper.convertToNodeDto(node), HttpStatus.OK);
        } else {
            logger.error("Node with id=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNodeById (@PathVariable long id) {
        Node node = nodeService.findNodeById(id);
        if (node != null) {
            nodeService.deleteNodeById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            logger.error("Node with id=" + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
