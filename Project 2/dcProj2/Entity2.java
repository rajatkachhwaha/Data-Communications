public class Entity2 extends Entity
{    
    // Perform any necessary initialization in the constructor
    public Entity2()
    {
     Packet packet;
     
   //Setting the default Distance Table for the node 2
     for(int i=0;i<=NetworkSimulator.NUMENTITIES-1;i++)
        for(int j=0;j<=NetworkSimulator.NUMENTITIES-1;++j)
            distanceTable[i][j] = 999;

   //Initializing the distanceTable for node 2
     distanceTable[0][0] = 3;
     distanceTable[1][1] = 1;
     distanceTable[2][2] = 0;
     distanceTable[3][3] = 2;
     
     int[] mincost = getMinCost();
     
   //Creating the packet of distance vector and layer2 will send the packet to the neighboring nodes
     packet = new Packet(2,0,mincost);
     NetworkSimulator.toLayer2(packet);

     packet = new Packet(2,1,mincost);
     NetworkSimulator.toLayer2(packet);

     packet = new Packet(2,3,mincost);
     NetworkSimulator.toLayer2(packet);

  //Print the initial distance vector table
     System.out.println("Initial Distance Vector Table of NODE 2 (Two) \n");
     printDT();
   }
    // Handle updates when a packet is received.  Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p)
    {
      int destID;
      Packet packet;
      int srcID = p.getSource();
      boolean newMinCost = false;

      int[] oldmincost = getMinCost();

      for(destID=0;destID<NetworkSimulator.NUMENTITIES;destID++)
      {
    	//if there is new distance costs
       if(p.getMincost(destID)!= 999)
         {
    	 //update distanceTable
           distanceTable[destID][srcID] = distanceTable[srcID][srcID] + p.getMincost(destID);

           if(distanceTable[destID][srcID] < oldmincost[destID])
               newMinCost = true;
         }
      }
      if(newMinCost)
      {
       int[] mincost = getMinCost();

       packet = new Packet(2,0,mincost);
       NetworkSimulator.toLayer2(packet);

       packet = new Packet(2,1,mincost);
       NetworkSimulator.toLayer2(packet);

       packet = new Packet(2,3,mincost);
       NetworkSimulator.toLayer2(packet);
      }
      printDT();
     }
      
 // Helps Fetching the minimum cost when the information is sent by the neighboring nodes
   private int[] getMinCost()
   {
    int[] mincost= null;
    mincost = new int[NetworkSimulator.NUMENTITIES];

    for(int i=0;i<=NetworkSimulator.NUMENTITIES-1;i++)
        mincost[i] =999;

    for(int i=0;i<=NetworkSimulator.NUMENTITIES-1;i++)
      {
       for(int j=0;j<=NetworkSimulator.NUMENTITIES-1;j++)
        {
          if(distanceTable[i][j]<mincost[i])
            {
             mincost[i] = distanceTable[i][j];
            }
        }
      }
      return mincost;
    }
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
    }
    
    public void printDT()
    {
        System.out.println();
        System.out.println("           through");
        System.out.println(" D2 |   0   1   3");
        System.out.println("____+_____________");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            if (i == 2)
            {
                continue;
            }
            
            System.out.print("   " + i + "|");
            for (int j = 0; j < NetworkSimulator.NUMENTITIES; j++)
            {
                if (j == 2)
                {
                    continue;
                }
                
                if (distanceTable[i][j] < 10)
                {    
                    System.out.print("   ");
                }
                else if (distanceTable[i][j] < 100)
                {
                    System.out.print("  ");
                }
                else 
                {
                    System.out.print(" ");
                }
                
                System.out.print(distanceTable[i][j]);
            }
            System.out.println();
        }
    }
}
