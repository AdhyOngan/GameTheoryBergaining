
package report;

import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import routing.DecisionEngineRouterPunyaKu;
import routing.MessageRouter;
import routing.RoutingDecisionEnginePunyaku;
import routing.community.GameTheoryInterface;

/**
 *
 * @author Jarkom
 */
public class UangProfitBuyerReport extends Report {
    public static final String NODE_ID = "ToNodeID"; //Node yang ditemui
    private  int nodeAddress;// Nodeku
    private  Map<DTNHost, Double> profitBuyer;
    private  Map<DTNHost, Double> avgBuffer;
    
    private  List<Double> listProfitBuyer;
    
    public UangProfitBuyerReport() {

        Settings s = getSettings();
        if (s.contains(NODE_ID)) {
            nodeAddress = s.getInt(NODE_ID);
        } else {
            nodeAddress = 0;
        }
        listProfitBuyer = new LinkedList<>();
        profitBuyer = new HashMap<>();
        avgBuffer = new HashMap<>();
    }

    @Override
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
            List<Double> listProfitBuyer = cd.getProfitBuyer();
            String printLn = host+ " ";
            for (Double nilai : listProfitBuyer) {
                printLn = printLn + "\t" +nilai;
            }
            write(printLn);
           
        }
        
        super.done();
    } 

}




      

