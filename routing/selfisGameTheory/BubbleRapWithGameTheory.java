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
import routing.RoutingDecisionEnginePunyaku;
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
public class BubbleRapWithGameTheory 
        implements RoutingDecisionEngine, CommunityDetectionEngine, GameTheoryInterface {

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
    
    private List<Double> uangSekarangMap;
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
    private Double Seller;
    private Double Buyer;
    private Double UangSekarang = uangSeller;
//    private Double Buyer;
//   private Double Buyer;
//   private Double Seller;
    private Double HitungValm;

    /**
     * List Start Time temps
     */
    /**
     * Membangun DistributedBubbleRap Decision Engine berbasis Settings
     * menggambarkan Settings objek parameter. Class mencari Class Nama
     * Detection Komunitas dan Algoritma Centrality yang seharusnya digunakan
     * untuk melakukan routing
     *
     * @param s
     * @parameter s Settings untuk mengkonfigurasi obyek
     */
    public BubbleRapWithGameTheory(Settings s) {
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
        /**
         * jika Centrality bernilai, maka nilai centrality akan dilemparkan ke
         * Centrality (kelas Interface) maka buat inisialisasi boyek dari
         * Centrality_alg_settings jika tidak, maka centrality yang digunakan
         * adalah Windows Centrality
         */
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
    public BubbleRapWithGameTheory(BubbleRapWithGameTheory proto) {
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
        thisHosts = thisHost;//

        /**
         * Menghitung waktu durasi ketika koneksi baru saja terbentuk dan
         * meginformasikan obyek deteksi komunitas bahwa koneksi bar telah
         * terbentuk
         *
         * @lihat routing.RoutingDecisionEngine
         * #doExchangeForNewConnection(core.connection, core.DTNHost)
         */    //jika terjadi kontak, masing2 node akan mulai kalkulasi, maka
        //jika dia buyer :
        //hitung buyer
        //else
        //hitung seller
//      double currentTime = SimClock.getTime();
//      List<Duration> history;
//      if (!connHistory.containsKey(peer)){
//          //jika belum ada peer dalam con hidtory maka buat
//          history = new LinkedList<Duration>();
//          
//      }else{
//          //kalau sudah ada buat aja 
//          history = connHistory.get(peer);    
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
        BubbleRapWithGameTheory de = this.getOtherDecisionEngine(peer);
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
        return m.getTo() == aHost; //unicast Routing
        //jika host adalah final destination berikan pesan

    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        return m.getTo() != thisHost;
        //jika host bukan destination teruskan !
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {

        if (m.getTo() == otherHost) {
            return true; //sepele (trivial) untuk disampaikan ke destination
        }

        hitungBuyer(thisHosts, m);
//        System.out.println("Buyer :" + Buyer);
        gethitungSeller(otherHost, m);
//        System.out.println(uangBuyer + "\t" + uangSeller + "\t" + diskonBuyer + "\t" + diskonSeller);
//        System.out.println("Saller :" + Seller);
//        Double HitungValm = Buyer-Seller;  
//        Double utilityS = HitungUtilitasBuyer(Seller);
        hitungValm(Buyer);
//        System.out.println("Laba Total :" + Valm);
        Double utilityS = hitungUtilitasSeller(Seller);
//        System.out.println("Utilitas Seller :" + utilityS);
        Double utilityB = hitungUtilitasBuyer(Buyer);
//        System.out.println("Utilitas Buyer :" + utilityB);
//        Double SPNB = HitungSPNbuyer();
//        System.out.println("SPN B :" + SPNB);
//        Double SPNS = HitungSPNsaller();
//        System.out.println("SPN S :" + SPNS);
//        Double Untung = uangSeller+Valm;
//        System.out.println(Untung);

        /**
         * disini akan diputuskan kapan akan meneruskan pesan
         *
         * DiBuBB bekerja keras pada awal forwarding pesan di Global Central ia
         * akan terus mencari hingga menemukan node yang memiliki tujuan pesan
         * yang sama dengan destination dalam lokal komunitas yang sama Pada
         * saat masuk di lokal Community dia akan menggunakan matriks lokal
         * centrality untuk meneruskan pesan kedalam komunitas
         */
        DTNHost dest = m.getTo();
        BubbleRapWithGameTheory de = getOtherDecisionEngine(otherHost);

        //yang memiliki lokal communities terbaik dengan destination, host atau peer
        boolean peerInCommunity = de.commumesWithHost(dest);
        boolean meInCommunity = this.commumesWithHost(dest);

        if (peerInCommunity && !meInCommunity) // peer node ada dalam komunitas lokal Destination
        //jika tetangga saya ada dalam komunitas, dan saya tidak di komunitas
        {
            if (Buyer >= Seller) { //Jika Buyer > Saller teruskan pesan 
                if (utilityB >= 0) { //Jika Utilitas > 0 maka berikan uang virtual
//                    System.out.println("peerInCommunity && !meInCommunity");
                    UangSekarang += HitungValm;
                    uangSekarangMap.add(UangSekarang);
//                    System.out.println("Uang untung :" + UangSekarang);
//                    System.out.println("Valm :" + HitungValm);
//                    System.out.println(" ");
//                    System.out.println("Valm :"+ Valm);
                    return true;
                }
            } else {
                return false;
            }
            return true;
        } else if (!peerInCommunity && meInCommunity) // saya ada dalam komunitas lokal destination
        //jika tetangga saya tidak dalam komunitas, dan saya dalam komunitas
        {
            return false;
        } else if (peerInCommunity) //Jika jika tetangga saya di komunitas
        {
            //di forward ke node yang memiliki centrality lokal yang lebih tinggi
            if (de.getLokalCentrality() > this.getLokalCentrality()) {
                if (Buyer >= Seller) {//Jika Buyer > Saller teruskan pesan 
                    if (utilityB >= 0) {//Jika Utilitas > 0 maka berikan uang virtual
//                        System.out.println("de.getLokalCentrality() > this.getLokalCentrality()");
                        UangSekarang += HitungValm;
                        uangSekarangMap.add(UangSekarang);
//                        System.out.println("Uang Untung :" + UangSekarang);
//                        System.out.println("Valm :" + HitungValm);
//                        System.out.println(" ");   
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } //tidak semua lokal comunity, dia diteruskan di global central node
        else if (de.getGlobalCentrality() > this.getGlobalCentrality()) {

            if (Buyer >= Seller) {//Jika Buyer > Saller teruskan pesan 
                if (utilityB >= 0) {//Jika Utilitas > 0 maka berikan uang virtual
//                    System.out.println("de.getGlobalCentrality() > this.getGlobalCentrality()");
                    UangSekarang += HitungValm;
                    uangSekarangMap.add(UangSekarang);
//                    System.out.println("Uang Untung :" + UangSekarang);
//                    System.out.println("Valm :" + HitungValm);
//                    System.out.println(" ");
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost
    ) {
        //DiBuBB memungkinkan suatu node untuk remove pesan setelah itu diteruskan kedalam
        //lokal community dari destination.
        BubbleRapWithGameTheory de = this.getOtherDecisionEngine(otherHost);
        return de.commumesWithHost(m.getTo())
                && !this.commumesWithHost(m.getTo());
        //jika 
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld
    ) {
        BubbleRapWithGameTheory de = this.getOtherDecisionEngine(hostReportingOld);
        return de.commumesWithHost(m.getTo())
                && !this.commumesWithHost(m.getTo());
    }

    @Override
    public RoutingDecisionEngine replicate() {
        //return new BubbleRapWithGameTheory (this);
        return new BubbleRapWithGameTheory(this);
    }
    //PENYESUAIAN ATRIBUT BUYER DAN SELLER

    private Double getEnergy(DTNHost thisHost) {
        //ambil energy level dari energy modelnya
        return (Double) thisHost.getComBus().getProperty(routing.util.EnergyModel.ENERGY_VALUE_ID);
    }

//    private Double getInitialEnergy(DTNHost h) {
//        //inisialisasi Energy awal
//        return (Double) h.getComBus().getProperty(routing.util.EnergyModel.INIT_ENERGY_S);
//    }

    //ambil waktu TTL dari pesan m
    private int getTTL(DTNHost nodes) {
        //ambil semua colection message
        Collection<Message> list = nodes.getMessageCollection();
        // pakai itrator untuk ambil list TTL
        Iterator<Message> message = list.iterator();
        while (message.hasNext()) {
            Message m = message.next();
            if (m.getTo() == nodes) {
            }
            //kembalikan mesage.getTTL
//            return Double.valueOf(m.getTtl());
//System.out.println(m.getTtl());
            return m.getTtl();
            
        }
        //kembalikan null.
        return 0;
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

    private Double residualResource (DTNHost nodes) {
        
        //Hitung Residual dari Resourch Total
        //Sisa Energi
        Double Energy = getEnergy(nodes);
        
        //Sisa Buffer
        Double freebuffer = Double.valueOf(nodes.getRouter().getFreeBufferSize());
        Double bAwal = Double.valueOf(nodes.getRouter().getBufferSize());
        //RESIDUAL : ((rENERGY + rBUFFER)/R.TOTAL)'
//         System.out.println(Energy);
//         System.out.println(eAwal);
//         System.out.println(Buffer);
//         System.out.println(bAwal);
        Double TResidual = (Energy) + (getBuffer(nodes)/freebuffer);
//        System.out.println(freebuffer);
//        System.out.println(TResidual);
        return TResidual;
    }

    private int residualTTL(DTNHost nodes) {
        //Hitung Residual TTL
//        Double TTL = Double.valueOf(getTTL(h));
        int TTL = getTTL(nodes);
//        System.out.println(TTL);
//        Double tAwal = getInitialTTL();
      
        //rTTL = kTTL/aTT
//        System.out.println(tAwal);
        int ResidualTTL = TTL / msgTtl;
        //System.out.println(tAwal);
        return ResidualTTL;
    }

    private void hitungBuyer(DTNHost nodes, Message m) {
        //masukan harga buyer dari :
        Double UangV = getUangBuyer();
        Double rResource = residualResource(nodes);
        Double rTTL = Double.valueOf(residualTTL(nodes));
        //BUYER : Panjang Pesan * UANG V *(1/(R.Residual *R.TTL))
        Buyer = m.getSize() * this.getUangBuyer() * (1 / (rResource + rTTL));
       
//        System.out.println("");
        System.out.println("Buyer \t:"+Buyer);
//        System.out.println();
//        System.out.println("hasil"+atas+"/"+bawah);
        buyerMap.add(Buyer);
    }

    private double gethitungSeller(DTNHost nodes, Message m) {
        //nasukan harga buyer dari :
        double UangV = getUangSeller();
        double scSimilarity = getClosenessOfNodes(nodes);
//       this.getUangBuyer();

        double rResource = residualResource(nodes);
        //SELLER : Panjang Pesan * (1/similarity)* (1/R.Residual)
        double Seller = m.getSize() * (1 / scSimilarity) * UangV * (1 / rResource);
        
//        System.out.println("Uang awal :"+UangV);
        System.out.println("Seller \t:"+ Seller);
//        System.out.println(me);
//        System.out.println(peer);
        SallerMap.add(Seller);
//        return Seller;
        return Seller;
    }
// hitung total laba dari hasil pengurangan seller dan buyer

    private Double hitungValm(Double nodes) {
//        System.out.println("Buyer2 :"+ Buyer);
//        System.out.println("Seller2 : "+ Saller);
          HitungValm = Buyer - Seller;
//        System.out.println("Buyer"+Buyer);
//        System.out.println("Seller"+Saller);
//        System.out.println("Hitung Laba Total = "+HitungValm);
          return HitungValm;

    }

    private double hitungUtilitasSeller(Double nodes) {
        
        double hitungSPNseller = ((getDiskonBuyer() * (1 - getDiskonSeller())) / (1 - (getDiskonBuyer() * getDiskonSeller())));
        double hitungUtilitasSeller = hitungSPNseller * Seller * hitungValm(nodes);
//        System.out.println("U Seller :"+HitungUtilitasSeller);
        return hitungUtilitasSeller;
    }

    private double hitungUtilitasBuyer(Double nodes) {
        double hitungSPNbuyer = ((1 - getDiskonBuyer()) / (1 - getDiskonBuyer() * getDiskonSeller()));
        Double hitungUtilitasBuyer = hitungSPNbuyer * Buyer * HitungValm;
//        System.out.println("U Buyer :"+HitungUtilitasBuyer);
        return hitungUtilitasBuyer;
    }

    protected boolean commumesWithHost(DTNHost h) {
        return community.isHostInCommunity(h);
    }

    protected double getLokalCentrality() {
        return this.centrality.getLocalCentrality(connHistory, community);
    }

    protected double getGlobalCentrality() {
        return this.centrality.getGlobalCentrality(connHistory);
    }

    @Override
    public Set<DTNHost> getLocalCommunity() {
        return this.community.getLocalCommunity();
    }

    private BubbleRapWithGameTheory getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only Works"
                + "with other routers of same type";
        //assert fungsinya sama kaya If yaitu perbandingan

        return (BubbleRapWithGameTheory) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
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
