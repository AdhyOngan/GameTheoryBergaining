/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.selfisGameTheory;

import core.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import routing.*;

/**
 *
 * @author Clara
 */
public class EpidemicDecisionEngine implements RoutingDecisionEngine {

    public EpidemicDecisionEngine(Settings s) {
    }

    public EpidemicDecisionEngine(EpidemicDecisionEngine proto) {
        
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
    }

    @Override
    public boolean newMessage(Message m) {
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return false;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        return true;
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {
        System.out.println("Hallo Sayang");
        return true;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
    return false;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
    return true;
    }

    @Override
    public RoutingDecisionEngine replicate() {
    return new EpidemicDecisionEngine(this);
    }
    
    private EpidemicDecisionEngine getOtherDecisionEngine(DTNHost h){
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works " 
                + "with other routers of same type";
        return(EpidemicDecisionEngine) ((DecisionEngineRouter)otherRouter).getDecisionEngine();
    }

  
}
