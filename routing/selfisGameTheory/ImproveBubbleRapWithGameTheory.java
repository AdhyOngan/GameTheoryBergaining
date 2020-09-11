
package routing.selfisGameTheory;

import routing.community.*;
import java.util.*;

import core.*;
import routing.DecisionEngineRouterPunyaKu;
import routing.MessageRouter;
import routing.RoutingDecisionEnginePunyaku;

public class ImproveBubbleRapWithGameTheory
        implements RoutingDecisionEnginePunyaku, CommunityDetectionEngine, GameTheoryInterface {

    /**
     * Community Detection Algorithm to employ -setting id {@value}
     */
    public static final String COMMUNITY_ALG_SETTING = "communityDetectAlg";
    /**
     * Centrality Computation Algorithm to employ -setting id {@value}
     */
    public static final String CENTRALITY_ALG_SETTING = "centralityAlg";

    public static final String UANG_VIRTUAL = "UangV";
    public static final String W1 = "BobotBuffer";
    public static final String W2 = "BobotEnergy";
    public static final String P1 = "BobotResidu";
    public static final String P2 = "BobotTTL";
    public static final String XS = "BobotXs";
    public static final String XB = "BobotXb";
    public static final String TresholdUtil = "Treshold";

    public double uangVirtual;
    public double bobotBuffer;
    public double bobotEnergy;
    public double bobotResidu;
    public double bobotTTL;
    public double Xs;
    public double Xb;
    public double treshold;



//    private Double UangSekarang = uangVirtual;
    private Double Valm;
   
    private Double profitBuyer;
    private Double profitSeller;

    protected Map<DTNHost, Double> startTimestamps;
    protected Map<DTNHost, List<Duration>> connHistory;
//    protected DTNHost thisHosts;
    public CommunityDetection community;
    public Centrality centrality;

    
    private List<Double> valmMap;
    private List<Double> sellerMap;
    private List<Double> buyerMap;
    private List<Double> profitBuyerMap;
    private List<Double> profitSellerMap;
    private List<Double> putaranSeller;
    private List<Double> putaranBuyer;


    /**
     * Constructs a DistributedBubbleRap Decision Engine based upon the settings
     * defined in the Settings object parameter. The class looks for the class
     * names of the community detection and centrality algorithms that should be
     * employed used to perform the routing.
     *
     * @param s Settings to configure the object
     */
    public ImproveBubbleRapWithGameTheory(Settings s) {

        this.uangVirtual = s.getDouble(UANG_VIRTUAL);
        this.bobotBuffer = s.getDouble(W1);
        this.bobotEnergy = s.getDouble(W2);
        this.bobotResidu = s.getDouble(P1);
        this.bobotTTL = s.getDouble(P2);
        this.Xs = s.getDouble(XS);
        this.Xb = s.getDouble(XB);
        this.treshold = s.getDouble(TresholdUtil);

        if (s.contains(COMMUNITY_ALG_SETTING)) {
            this.community = (CommunityDetection) s.createIntializedObject(s.getSetting(COMMUNITY_ALG_SETTING));
        } else {
            this.community = new SimpleCommunityDetection(s);
        }

        if (s.contains(CENTRALITY_ALG_SETTING)) {
            this.centrality = (Centrality) s.createIntializedObject(s.getSetting(CENTRALITY_ALG_SETTING));
        } else {
            this.centrality = new SWindowCentrality(s);
        }
    }

    /**
     * Constructs a DistributedBubbleRap Decision Engine from the argument
     * prototype.
     *
     * @param proto Prototype DistributedBubbleRap upon which to base this
     * object
     */
    public ImproveBubbleRapWithGameTheory(ImproveBubbleRapWithGameTheory proto) {
        this.community = proto.community.replicate();
        this.centrality = proto.centrality.replicate();
        startTimestamps = new HashMap<DTNHost, Double>();
        connHistory = new HashMap<DTNHost, List<Duration>>();
        buyerMap = new ArrayList<>();
        sellerMap = new ArrayList<>();
        profitBuyerMap = new ArrayList<>();
        profitSellerMap = new ArrayList<>();
        this.uangVirtual = proto.uangVirtual;
        this.bobotBuffer = proto.bobotBuffer;
        this.bobotEnergy = proto.bobotEnergy;
        this.bobotResidu = proto.bobotResidu;
        this.bobotTTL = proto.bobotTTL;
        this.Xb = proto.Xb;
        this.Xs = proto.Xs;
        this.treshold = proto.treshold;
    
        valmMap = new ArrayList<>();
        putaranBuyer = new ArrayList<>();
        putaranSeller = new ArrayList<>();
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
//        getResidualResource(thisHost);
//     thisHosts = thisHost;
    }

    /**
     * Starts timing the duration of this new connection and informs the
     * community detection object that a new connection was formed.
     *
     * @see
     * routing.RoutingDecisionEngine#doExchangeForNewConnection(core.Connection,
     * core.DTNHost)
     */
    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost myHost = con.getOtherNode(peer);
        ImproveBubbleRapWithGameTheory de = this.getOtherDecisionEngine(peer);

        this.startTimestamps.put(peer, SimClock.getTime());
        de.startTimestamps.put(myHost, SimClock.getTime());

        this.community.newConnection(myHost, peer, de.community);
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {
        double time = startTimestamps.get(peer);
        double etime = SimClock.getTime();

        // Find or create the connection history list
        List<Duration> history;
        if (!connHistory.containsKey(peer)) {
            history = new LinkedList<Duration>();
            connHistory.put(peer, history);
        } else {
            history = connHistory.get(peer);
        }

        // add this connection to the list
        if (etime - time > 0) {
            history.add(new Duration(time, etime));
        }

        CommunityDetection peerCD = this.getOtherDecisionEngine(peer).community;

        // inform the community detection object that a connection was lost.
        // The object might need the whole connection history at this point.
        community.connectionLost(thisHost, peer, peerCD, history);

        startTimestamps.remove(peer);
    }

    @Override
    public boolean newMessage(Message m) {
        return true; // Always keep and attempt to forward a created message
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return m.getTo() == aHost; // Unicast Routing
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
//        getHitungBuyer(thisHost, m);
//        hitungValm(thisHost, m);

        return m.getTo() != thisHost;
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost, DTNHost thisHost) {
        if (m.getTo() == otherHost) {
            return true; // trivial to deliver to final dest
        }
        /*
		 * Here is where we decide when to forward along a message. 
		 * 
		 * DiBuBB works such that it first forwards to the most globally central
		 * nodes in the network until it finds a node that has the message's 
		 * destination as part of it's local community. At this point, it uses 
		 * the local centrality metric to forward a message within the community. 
         */
//        UtilitasBuyer(thisHost, otherHost, m);
        DTNHost dest = m.getTo();
        ImproveBubbleRapWithGameTheory de = getOtherDecisionEngine(otherHost);

        // Which of us has the dest in our local communities, this host or the peer
        double me = this.getHitungBuyer(thisHost, m);
        double peer = getHitungSeller(otherHost, thisHost, m);
     
            if (me >= peer) {
                buyerMap.add(me);
                sellerMap.add(peer);
//                gethitungValm(thisHost, otherHost, m);
                while (getSelisihUtilitas(thisHost, otherHost, m) > treshold) {
                    Xb = Xb + 0.1;
                    putaranBuyer.add(Xb);
                                System.out.println("XB :"+Xb);
                    Xs = Xs - 0.1;
                    putaranSeller.add(Xs);
                                 System.out.println("XS :"+Xs);  
                }
                if (getSelisihUtilitas(thisHost, otherHost, m) < treshold) {
//                    valmMap.add(gethitungValm(thisHost, otherHost, m));         
                    profitBuyerMap.add(getUtilitasBuyer(thisHost, otherHost, m));
                    profitSellerMap.add(getUtilitasSeller(thisHost, otherHost, m));
                }
                return true;
            
        }
        return false;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
        // DiBuBB allows a node to remove a message once it's forwarded it into the
        // local community of the destination
        ImproveBubbleRapWithGameTheory de = this.getOtherDecisionEngine(otherHost);
        return de.commumesWithHost(m.getTo())
                && !this.commumesWithHost(m.getTo());
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        ImproveBubbleRapWithGameTheory de = this.getOtherDecisionEngine(hostReportingOld);
        return de.commumesWithHost(m.getTo())
                && !this.commumesWithHost(m.getTo());
    }

    //DAFTAR METHOD GAME TEORI
    //Method Residual
    public List<Duration> getListDuration(DTNHost otherHost) {

        if (connHistory.containsKey(otherHost)) {
            return connHistory.get(otherHost);
        } else {
            List<Duration> d = new LinkedList<>();
            return d;
        }
    }

    private double getAverageContactOfNodes(DTNHost otherHost) {
        //untuk ambil rata2 shortes Separation Of Nodes
        List<Duration> list = getListDuration(otherHost);
        Iterator<Duration> duration = list.iterator();
        //berfungsi untuk penunjuk dalam list
        double hasil = 0;
        while (duration.hasNext()) {
            Duration d = duration.next();
            hasil += (d.end - d.start);
        }
        return hasil / list.size();
    }

    private Double getClosenessOfNodes(DTNHost otherHost) {
        double rataContactSeparation = getAverageContactOfNodes(otherHost);
        double variansi = getVarianceOfNodes(otherHost);

        Double c = Math.exp(-(Math.pow(rataContactSeparation, 2) / (2 * variansi)));
        //System.out.println(c);
        return c;
    }

    private double getVarianceOfNodes(DTNHost otherHost) {
        //ambil variansinya dari node
        List<Duration> list = getListDuration(otherHost);
        Iterator<Duration> duration = list.iterator();
        double temp = 0;
        double mean = getAverageContactOfNodes(otherHost);
        while (duration.hasNext()) {
            Duration d = duration.next();
            temp += Math.pow((d.end - d.start) - mean, 2);
        }
        return temp / list.size();
    }

    private double getResidualresource(DTNHost thisHost) {
        double energy = (double) thisHost.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
//        double eAwal = (double) thisHost.getComBus().getProperty(routing.util.EnergyModel.INIT_ENERGY_S);
        int freebuffer = thisHost.getRouter().getFreeBufferSize();
//        int initial = thisHost.getRouter().getBufferSize();
        return ((bobotEnergy * energy)) + ((bobotBuffer * freebuffer));
//        System.out.println("R :"+residualResource);

    }

    private double getHitungBuyer(DTNHost thisHost, Message m) {
        double residualTTL = m.getTtl();
        double residuR = getResidualresource(thisHost);
        double panjangM = m.getSize();
//        System.out.println("TTL"+ residualTTL);
        double buyer = panjangM * getUangVirtual() * (1 / (bobotResidu * residuR + bobotTTL * residualTTL));
        return buyer;

    }

    private double getHitungSeller(DTNHost otherHost, DTNHost thisHost, Message m) {
        double soscialT = getClosenessOfNodes(otherHost);
        double resiudalR = getResidualresource(thisHost);
        double panjangM = m.getSize();

        double seller = panjangM * (1 / soscialT) * getUangVirtual() * (1 / resiudalR);
        return seller;      
    }

    private double getUtilitasBuyer(DTNHost thisHost, DTNHost otherHost, Message m) {
        double buyer = getHitungBuyer(thisHost, m);
        double seller = getHitungSeller(otherHost, thisHost, m);
    
        double profit = buyer-seller;
        return Xb * profit;

    }

    private double getUtilitasSeller(DTNHost thisHost, DTNHost otherHost, Message m) {
        double buyer = getHitungBuyer(thisHost, m);
        double seller = getHitungSeller(otherHost, thisHost, m);
        double profit = buyer-seller;
        return Xs * profit;
//        System.out.println("XS : "+Xs);
//        System.out.println("UTS : "+utilityS);
    }

    private double getSelisihUtilitas(DTNHost thisHost, DTNHost otherHost, Message m) {
        double ub = getUtilitasBuyer(thisHost, otherHost, m);
        double us = getUtilitasSeller(thisHost, otherHost, m);
        return Math.abs(ub - us);
    }

 @Override
    public RoutingDecisionEnginePunyaku replicate() {
        return new ImproveBubbleRapWithGameTheory(this);
    }

    protected boolean commumesWithHost(DTNHost h) {
        return community.isHostInCommunity(h);
    }

    protected double getLocalCentrality() {
        return this.centrality.getLocalCentrality(connHistory, community);
    }

    protected double getGlobalCentrality() {
        return this.centrality.getGlobalCentrality(connHistory);
    }

    private ImproveBubbleRapWithGameTheory getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouterPunyaKu : "This router only works "
                + " with other routers of same type";

        return (ImproveBubbleRapWithGameTheory) ((DecisionEngineRouterPunyaKu) otherRouter).getDecisionEngine();
    }

    public Set<DTNHost> getLocalCommunity() {
        return this.community.getLocalCommunity();
    }

    private Double getUangVirtual() {
        return uangVirtual;
    }



    @Override
    public List<Double> getValm() {
        return valmMap;
    }

    @Override
    public List<Double> getBuyerMap() {
        return buyerMap;
    }

    @Override
    public List<Double> getSallerMap() {
        return sellerMap;
    }

    @Override
    public List<Double> getProfitSeller() {
        return profitSellerMap;
    }

    @Override
    public List<Double> getProfitBuyer() {
        return profitSellerMap;
    }

    @Override
    public List<Double> getPutaranBuyer() {
        return putaranBuyer;
    }

    @Override
    public List<Double> getPutaranSeller() {
        return putaranSeller;
    }

   

  
    }


