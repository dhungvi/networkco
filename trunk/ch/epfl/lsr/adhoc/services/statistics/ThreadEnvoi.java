package ch.epfl.lsr.adhoc.services.statistics;

import ch.epfl.lsr.adhoc.runtime.MessagePool;
import ch.epfl.lsr.adhoc.runtime.FrancThread;
import ch.epfl.lsr.adhoc.runtime.SendMessageFailedException;

/**
 * Cette classe est la classe reli�e � la classe statisticsLayerClient qui sert � envoyer le nombre de messages
 * convenus � la cadence voulue.
 *
 *@see StatisticsLayerClient
 */

class ThreadEnvoi extends FrancThread
  {
   /** if this variable is true, the thread is active */
   private boolean active;
   /** parameter of the constructor so the thread can use the message pool and access to th type of message */
   private StatisticsLayerClient clientLayer;
   
   /** the number of messages per seconds to be send */
   private int frequence;
   
   /** the total number of messages to be sent */
   private int nbreMess;
   
   /** message pool of the client layer */
   private MessagePool mp;
   
		/** creates a new instance of ThreadEnvoi
		*
		*@param StatisicsLayerClient StatisticsLayerClient for who tis thread works
		*@param MessagePool The Message Pool of the StatisticsLayerClient
		*@param frequence The number of message per second to be sent
		*@param nbreMess The number of messages to be sent
		*/ 
     public ThreadEnvoi (StatisticsLayerClient clientLayer,MessagePool mp, int frequence, int nbreMess)
     {
      super();
      this.clientLayer = clientLayer;
      this.frequence = frequence;
      this.mp = mp;
      this.nbreMess = nbreMess;
     }
	 
	 /** This method is called to start the thread */	
     public void run()
     {
      int cpt = 0; 
      active = true;
      ThroughputMessage msg = null;
	  long sleepTime = 0;
	  if (frequence > 0)
	  {
	  	sleepTime = 1000/(long)frequence;
      	System.out.println ("Je vais envoyer "+nbreMess+" messages a la cadence de "+frequence+" par seconde");
		System.out.println("Soit un sleep de : "+sleepTime);
	  }
	  else
	  	System.out.println ("Je vais envoyer "+nbreMess+" messages a la cadence maximale");
	  	
	  long heureDebut = System.currentTimeMillis ();	
      while(cpt < nbreMess && active)
      {
      	
      	 cpt ++;
			 if(sleepTime>0)
	         try{FrancThread.sleep(sleepTime);} // il attend pour envoyer � la bonne frequence
	  	 
	  	 catch (Exception e){System.out.println(e.toString());}
	     
	     try
	  	 {
		  	msg = (ThroughputMessage)mp.getMessage(clientLayer.getmessType());
		  	msg.setParameters(System.currentTimeMillis (),false, 0,0);
		  	
	  	 }
	  	 catch (Exception e){System.out.println ("> # Could not create Message: " +e.getMessage());}
	     
	     
	     try {clientLayer.sendMessage (msg);} catch (SendMessageFailedException e) {e.printStackTrace();}
	     
	  }
	  System.out.println ();
	  long heureFin = System.currentTimeMillis ();	
	  
	  double throughputEnvoi = (double)cpt*1000.0 /((double)heureFin-(double)heureDebut);
	  clientLayer.setThroughputEnvoi (throughputEnvoi); 
	  System.out.println (cpt +" messages envoyes");
	  
	  // envoi d'un dernier message pour avertir le serveur que l'experience est finie
	  
	  try
	  {
	  	FrancThread.sleep (2000);
	  	msg = (ThroughputMessage)mp.getMessage(clientLayer.getmessType());
	  	msg.setParameters(System.currentTimeMillis (),true,0,0);
	  }
	  catch (Exception e){System.out.println ("> # Could not create Message: " +e.getMessage());}

	  try {clientLayer.sendMessage (msg);} catch (SendMessageFailedException e) {e.printStackTrace();} 
     
  }
		
     /** this method is called to stop the thread */
     public void arreter(){active = false;}
      
      	
   }
