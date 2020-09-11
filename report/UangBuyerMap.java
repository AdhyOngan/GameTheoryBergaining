package report;

import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import routing.DecisionEngineRouter;
import routing.DecisionEngineRouterPunyaKu;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;
import routing.RoutingDecisionEnginePunyaku;
import routing.community.GameTheoryInterface;

/**
 *
 * @author Jarkom
 */
public class UangBuyerMap extends Report {

    public static final String NODE_ID = "ToNodeID"; //Node yang ditemui
    private int nodeAddress;// Nodeku
    private Map<DTNHost, Double> uangBuyer;
    private Map<DTNHost, Double> avgBuffer;
//    private Double max;
//    private Double min;
    private List<Double> listUangBuyer;

    public UangBuyerMap() {

        Settings s = getSettings();
        if (s.contains(NODE_ID)) {
            nodeAddress = s.getInt(NODE_ID);
        } else {
            nodeAddress = 0;
        }
        listUangBuyer = new LinkedList<>();
        uangBuyer = new HashMap<>();
        avgBuffer = new HashMap<>();
    }

    public void done() {
        List<DTNHost> nodes = SimScenario.getInstance().getHosts();
        
        for (DTNHost host : nodes) {
            MessageRouter router = host.getRouter();
            if (!(router instanceof DecisionEngineRouterPunyaKu)) {
                continue;
            }
            RoutingDecisionEnginePunyaku de = ((DecisionEngineRouterPunyaKu) router).getDecisionEngine();
            if (!(de instanceof RoutingDecisionEnginePunyaku)) {
                continue;
            }
            GameTheoryInterface cd = (GameTheoryInterface) de;
            List<Double> listUangBuyer = cd.getBuyerMap();
            String printLn = host+ " ";
            for (Double nilai : listUangBuyer) {
                printLn = printLn + "\t" +nilai;
            }
            write(printLn);
           
        }
        
        super.done();
    } 
}
