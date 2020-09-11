/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.community;

import core.DTNHost;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jarkom
 */
public interface GameTheoryInterface {
     public List<Double> getBuyerMap();
     public List<Double> getSallerMap ();
     public List<Double> getValm();
     public List<Double> getProfitSeller();
     public List<Double> getProfitBuyer ();
     public List<Double> getPutaranBuyer();
     public List<Double> getPutaranSeller();

     
}
