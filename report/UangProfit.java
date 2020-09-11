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
public class UangProfit extends Report {

    public static final String NODE_ID = "ToNodeID"; //Node yang ditemui
    private int nodeAddress;// Nodeku
    private Map<DTNHost, Double> uangProfit;
    private Map<DTNHost, Double> avgBuffer;
//    private Double max;
//    private Double min;
    private List<Double> listUangProfit;

    public UangProfit() {

        Settings s = getSettings();
        if (s.contains(NODE_ID)) {
            nodeAddress = s.getInt(NODE_ID);
        } else {
            nodeAddress = 0;
        }
        listUangProfit = new LinkedList<>();
        uangProfit = new HashMap<>();
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
            List<Double> listUangProfit = cd.getValm();
            String printLn = host+ " ";
            for (Double nilai : listUangProfit) {
                printLn = printLn + "\t" +nilai;
            }
            write(printLn);
           
        }
        
        super.done();
    } 
}

