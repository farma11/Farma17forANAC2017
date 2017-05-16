package Farma17forANAC2017.etc;

import java.util.ArrayList;

import negotiator.Bid;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

/**
 * Created by tatsuya_toyama on 2017/05/07.
 */
public class NegoStrategy {
    private NegotiationInfo info;
    private boolean isPrinting = false; // デバッグ用
    private boolean isPrinting_Strategy = false;

    private double rv = 0.0;                // 留保価格
    private double df = 0.0;                // 割引効用


    public NegoStrategy(NegotiationInfo info, boolean isPrinting){
        this.info = info;
        this.isPrinting = isPrinting;

        rv = info.getUtilitySpace().getReservationValueUndiscounted();
        df = info.getUtilitySpace().getDiscountFactor();

    }

    // 受容判定
    public boolean selectAccept(Bid offeredBid, double time) {
        try {
            if(info.getUtilitySpace().getUtility(offeredBid) >= getThreshold(time)){
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("[Exception] 受容判定に失敗しました");
            e.printStackTrace();
            return false;
        }
    }

    // 交渉終了判定
    public boolean selectEndNegotiation(double time) {
        return false;
    }

    // 閾値を返す
    public double getThreshold(double time) {
        double threshold = 1.0 - time;
        if(isPrinting_Strategy){
            System.out.println("[isPrint_Strategy] threshold = " + threshold + " (time: " + time + ")");
        }
        return threshold;
    }
}
