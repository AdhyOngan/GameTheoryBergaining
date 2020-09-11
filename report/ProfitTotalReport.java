/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import com.sun.xml.internal.fastinfoset.stax.events.ReadIterator;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;
import routing.community.GameTheoryInterface;

/**
 *
 * @author Jarkom
 */
public class ProfitTotalReport extends Report{

    public static final String NODE_ID = "ToNodeID"; //Node yang ditemui
    private int nodeAddress;// Nodeku
    private Map<DTNHost, Double> buyerData;
    private Map<DTNHost, Double> avgBuffer;
//    private Double max;
//    private Double min;
    private List<Double> Valm;

    public ProfitTotalReport() {
        super();
        Settings s = getSettings();
        if (s.contains(NODE_ID)) {
            nodeAddress = s.getInt(NODE_ID);
        } else {
            nodeAddress = 0;
        }
        buyerData = new HashMap<>();
        avgBuffer = new HashMap<>();
    }

    public void done() {
        List<DTNHost> nodes = SimScenario.getInstance().getHosts();
        
        for (DTNHost host : nodes) {
            MessageRouter router = host.getRouter();
            if (!(router instanceof DecisionEngineRouter)) {
                continue;
            }
            RoutingDecisionEngine de = ((DecisionEngineRouter) router).getDecisionEngine();
            if (!(de instanceof RoutingDecisionEngine)) {
                continue;
            }
            GameTheoryInterface cd = (GameTheoryInterface) de;
            List<Double> listValm = cd.getValm();
            String printLn = host+ " ";
            for (Double nilai : listValm) {
                printLn = printLn + "\t" +nilai;
            }
            write(printLn);
        }
        super.done();
        
    }
}

