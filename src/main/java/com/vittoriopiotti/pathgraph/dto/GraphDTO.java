/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.stream.Collectors;


/**
 * GraphDTO represents a directed or undirected graph consisting of nodes and edges.
 * It provides functionality to read graph data from a JSON file or string, find paths between nodes,
 * and generate JSON representation of the graph.
 *
 * @author vittoriopiotti
 */
public class GraphDTO {

    /**
     * A list of nodes in the graph.
     */
    private final List<NodeDTO> nodes = new ArrayList<>();

    /**
     * A list of edges connecting the nodes in the graph.
     */
    private final List<EdgeDTO> edges = new ArrayList<>();

    /**
     * A map that associates each node with a list of its connections.
     * Each key is a NodeDTO, and the corresponding value is a list of ConnectionDTOs that represent
     * the connections from that node to its neighbors.
     */
    private final Map<NodeDTO, List<ConnectionDTO>> connections = new HashMap<>();

    /**
     * Constructs a GraphDTO using a file or JSON content to populate nodes and edges.
     *
     * @param file       the file containing graph data
     * @param jsonContent the JSON string representing the graph data
     * @param nodes      the list of nodes in the graph
     * @param edges      the list of edges in the graph
     */
    public GraphDTO(File file, String jsonContent, List<NodeDTO> nodes, List<EdgeDTO> edges) {
        if (file != null && Files.exists(file.toPath())) {
            try {
                String _jsonContent = Files.readString(file.toPath());
                setGraphDTO(_jsonContent);
                return;
            } catch (IOException ignored) {
            }
        }

        if (jsonContent != null && !jsonContent.isEmpty()) {
            setGraphDTO(jsonContent);
            return;
        }

        if (nodes != null && edges != null) {
            this.nodes.addAll(nodes);
            this.edges.addAll(edges);
            nodes.forEach(node -> this.connections.put(node,
                    edges.stream()
                            .filter(edge -> edge.getIsArrowed()
                                    ? edge.getFrom() == node.getLabel()
                                    : edge.getFrom() == node.getLabel() || edge.getTo() == node.getLabel()
                            )
                            .map(edge -> new ConnectionDTO(
                                    edge.getFrom() == node.getLabel() ? findNode(edge.getTo()).getLabel() : findNode(edge.getFrom()).getLabel(),
                                    edge.getCost()
                            ))
                            .collect(Collectors.toList())
            ));
        } else {
            this.nodes.clear();
            this.edges.clear();
        }
    }

    /**
     * Constructs a GraphDTO with the given nodes and edges.
     *
     * @param nodes the list of nodes in the graph
     * @param edges the list of edges in the graph
     */
    public GraphDTO(List<NodeDTO> nodes, List<EdgeDTO> edges) {
        this(null, "", nodes, edges);
    }

    /**
     * Constructs a GraphDTO by reading data from a file.
     *
     * @param file the file containing graph data
     */
    public GraphDTO(File file) {
        this(file, "", new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Constructs a GraphDTO using JSON content.
     *
     * @param jsonContent the JSON string representing the graph data
     */
    public GraphDTO(String jsonContent) {
        this(null, jsonContent, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Returns the list of nodes in the graph.
     *
     * @return a List of NodeDTO objects representing the nodes in the graph.
     */
    public List<NodeDTO> getNodes(){
        return nodes;
    }



    /**
     * Returns the list of edges in the graph.
     *
     * @return a List of EdgeDTO objects representing the edges connecting the nodes in the graph.
     */
    public List<EdgeDTO> getEdges(){
        return edges;
    }

    /**
     * Returns a map of connections for each node in the graph.
     * The map associates each node with a list of its connections to other nodes.
     *
     * @return a Map where the key is a NodeDTO and the value is a List of ConnectionDTOs.
     */
    public Map<NodeDTO, List<ConnectionDTO>> getConnections(){
        return connections;
    }

    /**
     * Finds a path from a starting node to an ending node using Dijkstra's algorithm.
     *
     * @param startLabel the label of the starting node
     * @param endLabel   the label of the ending node
     * @return a list of NodeDTO representing the path, or null if no path exists
     */
    public List<NodeDTO> findPath(char startLabel, char endLabel) {
        if (startLabel != '\0' && endLabel != '\0') {
            NodeDTO startNode = findNode(startLabel);
            NodeDTO endNode = findNode(endLabel);
            Map<NodeDTO, Integer> distanceMap = new HashMap<>();
            Map<NodeDTO, NodeDTO> predecessorMap = new HashMap<>();
            PriorityQueue<NodeDTO> queue = new PriorityQueue<>(Comparator.comparingInt(distanceMap::get));
            for (NodeDTO node : nodes) {
                distanceMap.put(node, Integer.MAX_VALUE);
            }
            distanceMap.put(startNode, 0);
            queue.add(startNode);
            while (!queue.isEmpty()) {
                NodeDTO current = queue.poll();
                int currentDistance = distanceMap.get(current);
                for (ConnectionDTO connection : connections.get(current)) {
                    NodeDTO neighbor = findNode(connection.getLabel());
                    if (neighbor == null) continue;
                    int edgeCost = connection.getCost();
                    int newDistance = currentDistance + edgeCost;
                    if (newDistance < distanceMap.get(neighbor)) {
                        distanceMap.put(neighbor, newDistance);
                        predecessorMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
            List<NodeDTO> path = new ArrayList<>();
            NodeDTO step = endNode;
            if (predecessorMap.get(step) == null) {
                return null;
            }
            path.add(step);
            while (predecessorMap.get(step) != null) {
                step = predecessorMap.get(step);
                path.add(step);
            }
            Collections.reverse(path);
            return path;
        }
        return null;

    }


    /**
     * Build json file including nodes and edges.
     *
     * @return json string
     */
    public String getGraphJson(){
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"nodes\": [\n");
        for (int j = 0; j < nodes.size(); j++) {
            jsonBuilder.append("    \"").append(nodes.get(j).getLabel()).append("\"");
            if (j < nodes.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ],\n");
        jsonBuilder.append("  \"edges\": [\n");
        for (int j = 0; j < edges.size(); j++) {
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"from\": \"").append(edges.get(j).getFrom() ).append("\",\n");
            jsonBuilder.append("      \"to\": \"").append(edges.get(j).getTo()).append("\",\n");
            jsonBuilder.append("      \"cost\": \"").append(edges.get(j).getCost()).append("\",\n");
            jsonBuilder.append("      \"isArrowed\": ").append(edges.get(j).getIsArrowed()).append("\n");
            jsonBuilder.append("    }");
            if (j < edges.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }


    /**
     * Builds a JSON string representing all nodes in the graph.
     *
     * @return a JSON object containing an array of node labels.
     */
    public String getNodesJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"nodes\": [\n");
        for (int j = 0; j < nodes.size(); j++) {
            jsonBuilder.append("    \"").append(nodes.get(j).getLabel()).append("\"");
            if (j < nodes.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }



    /**
     * Builds a JSON string representing all edges in the graph.
     *
     * @return a JSON object containing an array of edge objects.
     */
    public String getEdgesJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"edges\": [\n");
        for (int j = 0; j < edges.size(); j++) {
            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"from\": \"").append(edges.get(j).getFrom()).append("\",\n");
            jsonBuilder.append("      \"to\": \"").append(edges.get(j).getTo()).append("\",\n");
            jsonBuilder.append("      \"cost\": \"").append(edges.get(j).getCost()).append("\",\n");
            jsonBuilder.append("      \"isArrowed\": ").append(edges.get(j).getIsArrowed()).append("\n");
            jsonBuilder.append("    }");
            if (j < edges.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }



    /**
     * Generates a JSON representation of the node connections.
     * <br>
     * The JSON contains nodes and their corresponding connections,
     * including the destination node and the cost of each connection.
     *
     * @return a formatted JSON string representing the connections
     */
    public String getConnectionsJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"connections\": [\n");

        // Itera attraverso la mappa connections
        int nodeIndex = 0;
        for (Map.Entry<NodeDTO, List<ConnectionDTO>> entry : connections.entrySet()) {
            NodeDTO node = entry.getKey();
            List<ConnectionDTO> connectionList = entry.getValue();

            jsonBuilder.append("    {\n");
            jsonBuilder.append("      \"node\": \"").append(node.getLabel()).append("\",\n");
            jsonBuilder.append("      \"edges\": [\n");

            // Itera attraverso le connessioni di ciascun nodo
            for (int i = 0; i < connectionList.size(); i++) {
                ConnectionDTO connection = connectionList.get(i);
                jsonBuilder.append("        {\n");
                jsonBuilder.append("          \"to\": \"").append(connection.getLabel()).append("\",\n");
                jsonBuilder.append("          \"cost\": ").append(connection.getCost()).append("\n");
                jsonBuilder.append("        }");
                if (i < connectionList.size() - 1) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\n");
            }

            jsonBuilder.append("      ]\n");
            jsonBuilder.append("    }");
            if (nodeIndex < connections.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
            nodeIndex++;
        }

        jsonBuilder.append("  ]\n");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }


    /**
     * Finds a node in the graph by its label.
     *
     * @param label the label of the node to find.
     * @return the NodeDTO object representing the node with the specified label,
     *         or null if no such node exists in the graph.
     */
    private NodeDTO findNode(char label) {
        return nodes.stream().filter(n -> n.getLabel() == label).findFirst().orElse(null);
    }


    /**
     * Processes the JSON content to extract nodes and edges.
     *
     * @param jsonContent the JSON string representing the graph data
     */
    private void setGraphDTO(String jsonContent) {
        nodes.clear();
        edges.clear();
        setNodesDTO(jsonContent, nodes);
        setEdgesDTO(jsonContent, edges);
    }

    /**
     * Processes the JSON content to extract nodes.
     *
     * @param json   JSON string with graph data.
     * @param nodes  list to populate with parsed nodes.
     */
    private void setNodesDTO(String json, List<NodeDTO> nodes) {
        String nodesSection = json.split("\"nodes\":")[1].split("]")[0] + "]";
        String[] nodeArray = nodesSection.replaceAll("[\\[\\]\"]", "").trim().split(",");
        for (String nodeName : nodeArray) {
            nodeName = nodeName.trim();
            if (!nodeName.isEmpty()) {
                char nodeChar = nodeName.charAt(0);
                nodes.add(new NodeDTO(nodeChar)); // Assuming NodeDTO has a constructor that takes a char
            }
        }
    }

    /**
     * Processes the JSON content to extract edges.
     *
     * @param json   JSON string with graph data.
     * @param edges  list to populate with parsed edges.
     */
    private void setEdgesDTO(String json, List<EdgeDTO> edges) {
        String edgesSection = json.split("\"edges\":")[1].split("]")[0] + "]";
        String[] edgeStrings = edgesSection.split("},\\s*\\{");
        for (String edgeString : edgeStrings) {
            edgeString = edgeString.replaceAll("[\\[\\]{}]", "");
            edgeString = edgeString.trim();
            String[] properties = edgeString.split(",");
            char from = '\0';
            char to = '\0';
            int cost = -1;
            boolean isArrowed = false;
            for (String property : properties) {
                String[] keyValue = property.split(":");
                if (keyValue.length < 2) continue;
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                switch (key) {
                    case "from":
                        from = value.charAt(0);
                        break;
                    case "to":
                        to = value.charAt(0);
                        break;
                    case "cost":
                        cost = Integer.parseInt(value);
                        break;
                    case "isArrowed":
                        isArrowed = Boolean.parseBoolean(value);
                        break;
                }
            }
            if (from != '\0' && to != '\0' && cost != -1) {
                EdgeDTO edge = new EdgeDTO(from, to, cost, isArrowed);
                edges.add(edge);
            }
        }
    }


    /**
     * Returns a string representation of the GraphDTO.
     *
     * @return a string that includes the list of nodes, edges, and connections in the graph.
     */
    @Override
    public String toString() {
        return "GraphDTO{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", connections=" + connections +
                '}';
    }
}
