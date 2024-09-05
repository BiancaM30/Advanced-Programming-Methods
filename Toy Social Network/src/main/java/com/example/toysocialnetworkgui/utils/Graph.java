package com.example.toysocialnetworkgui.utils;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    int verticesNr;
    ArrayList<ArrayList<Long>> adjListArray;

    /**
     * Constructor
     * Initialize number of vertices and the adjacency lists
     *
     * @param verticesNr : number of vertices for the graph
     */
    public Graph(int verticesNr) {
        this.verticesNr = verticesNr;

        adjListArray = new ArrayList<>();

        for (int i = 0; i < verticesNr; i++) {
            adjListArray.add(i, new ArrayList<>());
        }
    }

    /**
     * Adds an edge to a directed graph
     *
     * @param src  : index of source vertex
     * @param dest : index of destination vertex
     */
    public void addEdge(Long src, Long dest) {
        adjListArray.get(Math.toIntExact(src)).add(dest);
    }

    /**
     * Depth first search algorithm that marks current node as visited and recur for all vertices adjacent to this vertex
     * Also, function will store all vertexes from current connected component
     *
     * @param crtVertex     : current vertex
     * @param visited       : boolean list containing information whether a vertex has been visited or not
     *                      - true(visited vertex) / false(not visited vertex)
     * @param crtComponent: integer list containing all vertexes from current component
     */
    void DFSUtil(int crtVertex, boolean[] visited, List<Integer> crtComponent) {
        visited[crtVertex] = true;
        crtComponent.add(crtVertex);
        for (long x : adjListArray.get(crtVertex)) {
            if (!visited[Math.toIntExact(x)])
                DFSUtil((int) x, visited, crtComponent);
        }
    }

    /**
     * Find number of connected components of graph and display all vertexes from connected components
     *
     * @return : number of connected component
     */
    public int connectedComponents() {

        int connectedComponentsNumber = 1;
        boolean[] visited = new boolean[this.verticesNr];

        for (int v = 1; v < this.verticesNr; v++) {
            if (!visited[v]) {
                List<Integer> crtComponent = new ArrayList<>();

                DFSUtil(v, visited, crtComponent);

                System.out.print("Component " + connectedComponentsNumber + ": ");
                for (int vertex : crtComponent) {
                    System.out.print(vertex + " ");
                }
                System.out.println();
                connectedComponentsNumber++;

            }
        }

        return connectedComponentsNumber - 1;
    }


    /**
     * Find maximum number of vertices from a connected component
     *
     * @return : maximum number of vertices from a connected component
     */
    public int largestConnectedComponent() {
        boolean[] visited = new boolean[verticesNr];
        int maxNr = 0;
        int indexOfCrtComponent = 1;

        for (int v = 1; v < verticesNr; v++) {
            if (!visited[v]) {
                List<Integer> crtComponent = new ArrayList<>();

                DFSUtil(v, visited, crtComponent);

                System.out.println("Component " + indexOfCrtComponent + " has " + crtComponent.size() + " vertices");
                System.out.println();
                indexOfCrtComponent++;

                if (crtComponent.size() > maxNr) maxNr = crtComponent.size();
            }
        }

        return maxNr;
    }

}
