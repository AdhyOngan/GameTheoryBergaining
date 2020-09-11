/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.selfisGameTheory;

import com.sun.javafx.scene.text.HitInfo;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;
import routing.community.Centrality;
import routing.community.CommunityDetection;
import routing.community.CommunityDetectionEngine;
import routing.community.DistributedBubbleRap;
import routing.community.Duration;
import routing.community.GameTheoryInterface;
import routing.community.SWindowCentrality;
import routing.community.SimpleCommunityDetection;
import routing.util.EnergyModel;
  
/**
 *
 * @author Jarkom
 */
public class GameTheory
        implements RoutingDecisionEngine, GameTheoryInterface {

    /**
     * Setting ID untuk Algoritma Deteksi Komunitas : setting id {@value{}
     */
    public static final String COMMUNITY_ALG_SETTING = "communtyDetectionAlg";
    /**
     * Setting ID untuk Algoritma Komputasi Centrality : setting id {@value{}
     */
    public static final String CENTRALITY_ALG_SETTING = "centralityAlg";
    /**
     * Maps dari startTimestamp
     */
    protected Map<DTNHost, Double> startTimestamps;
    /**
     * ambil TTL dari Energy modul
     */
    public static final String MSG_TTL_S = "msgTtl";
    /**
     * Maps dari connection History, Duration menggunakan List
     */
    public static final String DISKON_BUYER = "DiskonB";

    public static final String DISKON_SELLER = "DiskonS";

    public static final String UANG_BUYER = "UangB";

    public static final String UANG_SELLER = "UangS";
    public double diskonBuyer;
    public double diskonSeller;
    public double uangBuyer;
    public double uangSeller;

    protected Map<DTNHost, List<Duration>> connHistory;
    /**
     * ambil messsage ttl
     */
    protected int msgTtl = 300;
    /**
     * inisialisasi comunity
     */
    protected CommunityDetection community;
    /**
     * inisialisasi Centralitye
     */
    protected Centrality centrality;
    protected DTNHost thisHosts;
    
    private List<Double> buyerMap;
    private List<Double> SallerMap;
    
    private List<Double> uangSekarangMap;
    private Double Seller;
    
    private Double Buyer;
    private Double UangSekarang = uangSeller;
    private Double HitungValm;
    
    public GameTheory(Settings s) {
        /**
         * Contains : Mengembalikan nilai true jika nama settingan memiliki
         * beberapa nilai tertentu Jika Community Bernilai maka, nilai community
         * akan dilemparkan ke CommunityDetection (Kelas Interface) maka buat
         * inisialisasi obyek dari Community_alg_settings Jika tidak maka
         * community detection yang digunakan adalah simpleCommunityDetection
         */
        this.uangBuyer = s.getDouble(UANG_BUYER);
        this.uangSeller = s.getDouble(UANG_SELLER);
        this.diskonBuyer = s.getDouble(DISKON_BUYER);
        this.diskonSeller = s.getDouble(DISKON_SELLER);

        this.msgTtl = Message.INFINITE_TTL;
        if (s.contains(MSG_TTL_S)) {
            this.msgTtl = s.getInt(MSG_TTL_S);
        }
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
     * Membangun DistributionBubbleRap Decision Engine dari argumen Prototype
     *
     * @param proto prototype distributedBubbleRap menjadi dasar obyek ini
     */
    public GameTheory(GameTheory proto) {
        this.community = proto.community.replicate();
        this.centrality = proto.centrality.replicate();
        startTimestamps = new HashMap<DTNHost, Double>();
        connHistory = new HashMap<DTNHost, List<Duration>>();
        
        buyerMap = new ArrayList<>();
        SallerMap = new ArrayList<>();
        
        uangSekarangMap = new ArrayList<>();
        
        this.diskonBuyer = proto.diskonBuyer;
        this.diskonSeller = proto.diskonSeller;
        this.uangBuyer = proto.uangBuyer;
        this.uangSeller = proto.uangSeller;
        this.uangSeller = proto.uangSeller;
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
        thisHosts = thisHost;

        /**
         * Menghitung waktu durasi ketika koneksi baru saja terbentuk dan
         * meginformasikan obyek deteksi komunitas bahwa koneksi bar telah
         * terbentuk
         *
         * @lihat routing.RoutingDecisionEngine
         * #doExchangeForNewConnection(core.connection, core.DTNHost)
         */    //jika terjadi kontak, masing2 node akan mulai kalkulasi, maka  
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {

        double time = startTimestamps.get(peer);
        double etime = SimClock.getTime();

        //menemukan atau membuat daftar Connection History
        List<Duration> history;
        if (!connHistory.containsKey(peer)) {
            history = new LinkedList<Duration>();
            connHistory.put(peer, history);

        } else {
            history = connHistory.get(peer);
        }

        //menambahkan koneksi kedalam daftar
        if (etime - time > 0) {
            history.add(new Duration(time, etime));
        }
        //perhatikan
        CommunityDetection peerCD = this.getOtherDecisionEngine(peer).community;

        //menginformasikan obeyek komunitas bahwa koneksi telah terputus
        //Obyek mungkin membutuhkan Connection History saat ini
        community.connectionLost(thisHost, peer, peerCD, history);

//        startTimestamps.remove(peer);
    }

    @Override
    //pertukaran untuk koneksi baru
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost myHost = con.getOtherNode(peer);
        GameTheory de = this.getOtherDecisionEngine(peer);
        //deklari obyek bary getOtherDecisionEngine

        this.startTimestamps.put(peer, SimClock.getTime());
        //berarti 
        de.startTimestamps.put(myHost, SimClock.getTime());
        //berarti 
        this.community.newConnection(myHost, peer, de.community);
    }

    @Override
    public boolean newMessage(Message m) {
        //pesan baru
        return true; //selalu simpan dan meneruskan pesan yang dibuat
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
         return false; //unicast Routing
        //jika host adalah final destination berikan pesan
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
       return true;
        //jika host bukan destination teruskan !
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {

        HitungBuyer(thisHosts, m);
        HitungSeller(otherHost, m);
        HitungValm(Buyer, Seller);
        Double utilityS = HitungUtilitasSeller(Seller);
        Double utilityB = HitungUtilitasBuyer(Buyer);
        DTNHost dest = m.getTo();
        GameTheory de = getOtherDecisionEngine(otherHost);
    
        if (m.getTo()== otherHost){
            return true;
        }
            if (Buyer >= Seller){  
              if (utilityB >= 0) {
                    UangSekarang += HitungValm;
                    uangSekarangMap.add(UangSekarang);
                     System.out.println("Uang :"+ UangSekarang);
                     return true;   
              }
              return true;
            }
        return false;
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
        //return new BubbleRapWithGameTheory (this);
        return new GameTheory(this);
    }
    //PENYESUAIAN ATRIBUT BUYER DAN SELLER

    private Double getEnergy(DTNHost h) {
        //ambil energy level dari energy modelnya
        return (Double) h.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
    }    

    //ambil waktu TTL dari pesan m
    private int getTTL(DTNHost h) {
        //ambil semua colection message
        Collection<Message> list = h.getMessageCollection();
        // pakai itrator untuk ambil list TTL
        Iterator<Message> message = list.iterator();
        while (message.hasNext()) {
            Message m = message.next();
            if (m.getTo() == h) {
            }
            //kembalikan mesage.getTTL
//            return Double.valueOf(m.getTtl());
            return m.getTtl();
        }
        //kembalikan null.
        return 0;
    }

    private int getInitialTTL() {
        //inisialisasi nilai TTL awal
//        return Double.valueOf(msgTtl);
        return msgTtl;
    }

    private Double getBuffer(DTNHost h) {
        //mengambil data obyek dari buffer sisa
        Double initial = Double.valueOf(h.getRouter().getBufferSize());
        Double freebuffer = Double.valueOf(h.getRouter().getFreeBufferSize());
//        int residualbuffer = initial - buffer;
        return initial - freebuffer;
    }

    // Ambil List Duration
    public List<Duration> getListDuration(DTNHost nodes) {

        if (connHistory.containsKey(nodes)) {
            return connHistory.get(nodes);
        } else {
            List<Duration> d = new LinkedList<>();
            return d;
        }
    }

    private Double getClosenessOfNodes(DTNHost nodes) {
        double rataContactSeparation = getAverageContactOfNodes(nodes);
        double variansi = getVarianceOfNodes(nodes);

        Double c = Math.exp(-(Math.pow(rataContactSeparation, 2) / (2 * variansi)));
        //System.out.println(c);

        return c;
    }
    
    private double getVarianceOfNodes(DTNHost nodes) {
        //ambil variansinya dari node
        List<Duration> list = getListDuration(nodes);
        Iterator<Duration> duration = list.iterator();
        double temp = 0;
        double mean = getAverageContactOfNodes(nodes);
        while (duration.hasNext()) {
            Duration d = duration.next();
            temp += Math.pow((d.end - d.start) - mean, 2);
        }
        return temp / list.size();
    }

    private double getAverageContactOfNodes(DTNHost nodes) {
        //untuk ambil rata2 shortes Separation Of Nodes
        List<Duration> list = getListDuration(nodes);
        Iterator<Duration> duration = list.iterator();
        //berfungsi untuk penunjuk dalam list
        double hasil = 0;
        while (duration.hasNext()) {
            Duration d = duration.next();
            hasil += (d.end - d.start);
        }
        return hasil / list.size();
    }

    private Double ResidualResource(DTNHost h) {
        //Hitung Residual dari Resourch Total
        Double Energy = getEnergy(h);
        Double Buffer = getBuffer(h);
        System.out.println("energi"+Energy);
        Double bAwal = Double.valueOf(h.getRouter().getBufferSize());
        //RESIDUAL : ((rENERGY + rBUFFER)/R.TOTAL)'
        Double TResidual = Energy + (Buffer / bAwal);
        return TResidual;

    }

    private int ResidualTTL(DTNHost h) {
        //Hitung Residual TTL
//        Double TTL = Double.valueOf(getTTL(h));
        int TTL = getTTL(h);
//        System.out.println(TTL);
//        Double tAwal = getInitialTTL();
        int tAwal = getInitialTTL();
        //rTTL = kTTL/aTT
//        System.out.println(tAwal);
        int ResidualTTL = TTL / tAwal;
        //System.out.println(tAwal);
        return ResidualTTL;
    }

    private void HitungBuyer(DTNHost h, Message m) {
        //masukan harga buyer dari :
        Double UangV = getUangBuyer();
        Double rResource = ResidualResource(h);
        Double rTTL = Double.valueOf(ResidualTTL(h));
        //BUYER : Panjang Pesan * UANG V *(1/(R.Residual *R.TTL))
        Buyer = m.getSize() * UangV * (1 / (rResource + rTTL));
        buyerMap.add(Buyer);
    }

    private void HitungSeller(DTNHost h, Message m) {
        //nasukan harga buyer dari :
        Double UangV = getUangSeller();
        Double scSimilarity = getClosenessOfNodes(h);
//        System.out.println("Closeness : "+ scSimilarity);
        Double rResource = ResidualResource(h);
        //SELLER : Panjang Pesan * (1/similarity)* (1/R.Residual)
        Seller = m.getSize() * (1 / scSimilarity) * UangV * (1 / rResource);
        SallerMap.add(Seller);
//        return Seller;
    }

    private void HitungValm(Double Buyer, Double Seller) {
        HitungValm = Buyer - Seller;
    }

    private Double HitungSPNbuyer() {
        Double HitungSPNbuyer = ((1 - getDiskonBuyer()) / (1 - getDiskonBuyer() * getDiskonSeller()));
        return HitungSPNbuyer;
    }

    private Double HitungSPNsaller() {
        double HitungSPNsaller = ((getDiskonBuyer() * (1 - getDiskonSeller())) / (1 - (getDiskonBuyer() * getDiskonSeller())));
        return HitungSPNsaller;
    }

    private Double HitungUtilitasSeller(Double Seller) {
        Double HitungUtilitasSeller = HitungSPNsaller() * Seller * HitungValm;
        return HitungUtilitasSeller;
    }

    private Double HitungUtilitasBuyer(Double Buyer) {
        Double HitungUtilitasBuyer = HitungSPNbuyer() * Buyer * HitungValm;
        return HitungUtilitasBuyer;
    }
  
    private GameTheory getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only Works"
                + "with other routers of same type";
        //assert fungsinya sama kaya If yaitu perbandingan

        return (GameTheory) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

    @Override
    public List<Double> getBuyerMap() {
        return buyerMap;
    }

    @Override
    public List<Double> getSallerMap() {
        return SallerMap;
    }
    @Override
    public List<Double> getUangSekarang(){
        return uangSekarangMap;
    }

    private Double getDiskonBuyer() {
        return diskonBuyer;
    }

    private Double getDiskonSeller() {
        return diskonSeller;
    }

    private Double getUangBuyer() {
        return uangBuyer;
    }

    private Double getUangSeller() {
        return uangSeller;
    }
}
