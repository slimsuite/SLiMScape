package org.cytoscape.slimscape.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.slimscape.internal.ui.QSlimfinderOptions;
import org.cytoscape.slimscape.internal.ui.QSlimfinderOptionsPanel;

import java.util.ArrayList;
import java.util.List;

public class RunQSlimfinder {
    CyNetwork network;
    String url;
    String query;

    public RunQSlimfinder(CyNetwork network, List<CyNode> selected, List<String> uniprotIDs, String query, QSlimfinderOptionsPanel optionsPanel) {
        this.network = network;
        this.query = query;

        List<String> ids;

        if (uniprotIDs == null) {
            ids = getNodeIds(selected);
        } else {
            ids = uniprotIDs;
        }
        url = constructUrl(optionsPanel, ids, query);
    }

    /**
     * @desc gets the uniprot IDs of each selected node, and returns them as a list
     * @param selected - list of CyNodes that have been selected in the graph.
     * @return list containing the Uniprot ids of all selected nodes
     */
    private List<String> getNodeIds(List<CyNode> selected) {
        List<String> uniprotIDs = new ArrayList<String>();

        for (CyNode node : selected) {
            String name = network.getRow(node).get(CyNetwork.NAME, String.class); // Gets uniprot ID
            uniprotIDs.add(name);
        }

        return uniprotIDs;
    }

    /**
     * @desc Gets the current state of the options panel, and constructs the URL to send to the REST server
     * @param optionsPanel - slimpr options panel, containing all the options elements to be passed to the server
     * @param uniprotIDs - list containing the Uniprot ids of all selected nodes
     * @return the constructed URL to be passed to the server
     */
    public String constructUrl(QSlimfinderOptionsPanel optionsPanel, List<String> uniprotIDs, String query) {
        // Get state of SlimsearchOptionsPanel
        QSlimfinderOptions options = optionsPanel.getQSLiMFinderOptions();
        boolean dismask = options.getDismask();
        boolean conservation = options.getConsmask();
        boolean featuremask = options.getFeaturemask();
        boolean ambiguity = options.getAmbiguity();
        int cutoff = options.getCutoff();
        String custom = options.getCustomParameters();
        int wildcard = options.getWildcard();

        StringBuilder stringBuilder = new StringBuilder("http://rest.slimsuite.unsw.edu.au/qslimfinder");

        stringBuilder.append("&query=" + query);

        String ids = "&uniprotid=";
        for (String id : uniprotIDs) {
            if (id != query) {
                ids = ids + id + ",";
            }
        }
        ids = ids.substring(0, ids.length() - 1);
        stringBuilder.append(ids);

        // Construct properly formatted string components
        String dismaskS = "&dismask=";
        if (dismask) {
            stringBuilder.append(dismaskS + "T");
        } else {
            stringBuilder.append(dismaskS + "F");
        }

        String consmaskS = "&consmask=";
        if (conservation) {
            stringBuilder.append(consmaskS + "T");
        } else {
            stringBuilder.append(consmaskS + "F");
        }

        String featuremaskS = "&featuremask=";
        if (featuremask) {
            stringBuilder.append(featuremaskS + "T");
        } else {
            stringBuilder.append(featuremaskS + "F");
        }

        String ambiguityS = "&ambiguity=";
        if (ambiguity) {
            stringBuilder.append(ambiguityS + "T");
        } else {
            stringBuilder.append(ambiguityS + "F");
        }

        stringBuilder.append("&slimlen=" + cutoff);

        stringBuilder.append("&maxwild=" + wildcard);

        if (custom.length() > 0) {
            custom = custom.replace("\n", "&");
            custom = custom.replace(" ", "");
            stringBuilder.append("&" + custom);
        }

        // Make the final string
        return (stringBuilder.toString());
    }

    public String getUrl() {
        return url;
    }
}
