package Farma17forANAC2017;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Farma17forANAC2017.etc.BidSearch;
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

import Farma17forANAC2017.etc.NegoStrategy;
import Farma17forANAC2017.etc.NegoStats;

/**
 * This is your negotiation party.
 */
public class Farma17 extends AbstractNegotiationParty {
    private NegotiationInfo info;
    private StandardInfoList history;
    private Bid lastReceivedBid = null;
    private boolean isPrinting = false; // デバッグ用

    private NegoStrategy negoStrategy;
    private NegoStats negoStats;
    private BidSearch bidSearch;


    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        this.info = info;
        negoStrategy    = new NegoStrategy(info, isPrinting);
        negoStats       = new NegoStats(info, isPrinting);

            
        try {
            bidSearch = new BidSearch(info, isPrinting, negoStats);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // PersistentDataType が Standard の場合
        if (getData().getPersistentDataType() == PersistentDataType.STANDARD) {
            history = (StandardInfoList) getData().get();

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

        if(isPrinting) {
            System.out.println("[isPrint] ** isPrinting == True **");
            System.out.println("[isPrint] Discount Factor is " + info.getUtilitySpace().getDiscountFactor());
            System.out.println("[isPrint] Reservation Value is " + info.getUtilitySpace().getReservationValueUndiscounted());
        }

        // if you need to initialize some variables, please initialize them
        // below

    }

    /**
     * Each round this method gets called and ask you to accept or offer. The
     * first party in the first round is a bit different, it can only propose an
     * offer.
     *
     * @param validActions
     *            Either a list containing both accept and offer or only offer.
     * @return The chosen action.
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> validActions) {
        double time = info.getTimeline().getTime();

        if(validActions.contains(Accept.class) && negoStrategy.selectAccept(lastReceivedBid, time)){
            return new Accept(getPartyId(), lastReceivedBid);
        } else if (negoStrategy.selectEndNegotiation(time)){
            return new EndNegotiation(getPartyId());
        }

        Bid offerBid = generateRandomBid();
        return new Offer(getPartyId(), offerBid);
    }

    /**
     * All offers proposed by the other parties will be received as a message.
     * You can use this information to your advantage, for example to predict
     * their utility.
     *
     * @param sender
     *            The party that did the action. Can be null.
     * @param action
     *            The action that party did.
     */
    @Override
    public void receiveMessage(AgentID sender, Action action) {
        super.receiveMessage(sender, action);

        if(isPrinting){
            System.out.println("[isPrint] Sender:" + sender + ", Action:"+action);
        }

        if(action != null){
            if (action instanceof Offer) {
                if(!negoStats.getRivals().contains(sender)) {
                    negoStats.initRivals(sender);
                }

                lastReceivedBid = ((Offer) action).getBid();
                try {
                    negoStats.updateInfo(sender, lastReceivedBid);
                } catch (Exception e) {
                    System.out.println("[Exception] 交渉情報の更新に失敗しました");
                    e.printStackTrace();
                }
            } else if (action instanceof Accept){
                if(!negoStats.getRivals().contains(sender)) {
                    negoStats.initRivals(sender);
                }
            } else if (action instanceof EndNegotiation){

            }
        }

    }

    @Override
    public String getDescription() {
        return "Template Agent";
    }

}