package Farma17forANAC2017.etc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import list.Tuple;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Action;
import negotiator.actions.Accept;
import negotiator.actions.Offer;
import negotiator.actions.EndNegotiation;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.persistent.PersistentDataContainer;
import negotiator.persistent.PersistentDataType;
import negotiator.persistent.StandardInfo;
import negotiator.persistent.StandardInfoList;

/**
 * Created by tatsuya_toyama on 2017/05/16.
 */

public class NegoHistory {
    private NegotiationInfo info;
    private StandardInfoList history;
    private boolean historyAnalyzed = false;

    private boolean isPrinting = false; // デバッグ用

    /**
     * @param info
     * @param isPrinting
     * @param pData      public PersistentDataContainer getData(): persistent data
     */
    public NegoHistory(NegotiationInfo info, boolean isPrinting, PersistentDataContainer pData) {
        this.info = info;

        // PersistentDataType が Standard の場合
        if (pData.getPersistentDataType() == PersistentDataType.STANDARD) {
            history = (StandardInfoList) pData.get();

            if (!history.isEmpty()) {
                // example of using the history. Compute for each party the maximum
                // utility of the bids in last session.
                Map<String, Double> maxutils = new HashMap<String, Double>();
                StandardInfo lastinfo = history.get(history.size() - 1);
                for (Tuple<String, Double> offered : lastinfo.getUtilities()) {
                    String party = offered.get1();
                    Double util = offered.get2();
                    maxutils.put(party, maxutils.containsKey(party) ? Math.max(maxutils.get(party), util) : util);
                }
                System.out.println(maxutils); // notice tournament suppresses all
                // output.
            }
        }

    }

    public void analyzeHistory() {
        historyAnalyzed = true;

        // from recent to older history records
        for (int h = history.size() - 1; h >= 0; h--) {

            System.out.println("History index: " + h);

            StandardInfo lastinfo = history.get(h);

            int counter = 0;
            for (Tuple<String, Double> offered : lastinfo.getUtilities()) {
                counter++;

                String party = offered.get1();  // get partyID -> example: ConcederParty@15
                Double util = offered.get2();   // get the offer utility

                System.out.println("PartyID: " + party + " utilityForMe: " + util);
                System.out.println();
                //just print first 3 bids, not the whole history
                if (counter == 3)
                    break;
            }

            System.out.println("\n");

        }

    }
}
