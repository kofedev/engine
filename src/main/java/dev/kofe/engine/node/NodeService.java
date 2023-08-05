package dev.kofe.engine.node;

import dev.kofe.engine.common.DoubleResult;
import java.util.List;

public interface NodeService  {
    List<Node> findAllNodes();
    List<Node> findAllActiveNodes();
    Node addNewNodeAndExpandEmptyDescriptors (NodeDto nodeToAdd);
    DoubleResult<Boolean, Boolean> getRelocationDecision (long idNode, long idDest);
    Node relocateToAnotherParentNode (Long nodeToRelocateId, Long destinationParentId);
    Node updateNodeByActiveStatusNoteDescriptors(NodeDto nodeDto);
    Node findNodeById (long id);
    void deleteNodeById(long id);
}
