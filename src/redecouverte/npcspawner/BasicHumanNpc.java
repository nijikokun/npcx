package redecouverte.npcspawner;
import net.gamerservices.npcx.*;


import java.io.Console;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;

public class BasicHumanNpc extends BasicNpc {

	public LivingEntity follow;
	public LivingEntity aggro;
	public int hp = 100;
	public int dmg = 3;
	public double spawnx;
	public double spawny;
	public double spawnz;
	public double spawnyaw;
	public double spawnpitch;
	
	public double seekx;
	public double seeky;
	public double seekz;
	public double seekyaw;
	public double seekpitch;
	
	
    public CHumanNpc mcEntity;
    private static final Logger logger = Logger.getLogger("Minecraft");
    public myNPC parent;
    public BasicHumanNpc(CHumanNpc entity, String uniqueId, String name, double spawnx, double spawny, double spawnz,double spawnyaw, double spawnpitch) {
        super(uniqueId, name);
    	this.spawnx = spawnx;
    	this.spawny = spawny;
    	this.spawnz = spawnz;
    	this.spawnyaw = spawnyaw;
    	this.spawnpitch = spawnpitch;
    	
    	

        this.mcEntity = entity;
    }

    public HumanEntity getBukkitEntity() {
        return (HumanEntity) this.mcEntity.getBukkitEntity();
    }

    protected CHumanNpc getMCEntity() {
        return this.mcEntity;
    }

    public void moveto(LivingEntity target)
    {
    	double x = target.getLocation().getX();
		double y = target.getLocation().getY();
		double z = target.getLocation().getZ();
		double x2 = this.getBukkitEntity().getLocation().getX();
		double y2 = this.getBukkitEntity().getLocation().getY();
		double z2 = this.getBukkitEntity().getLocation().getZ();
		
		
		int xdist = (int) (x - x2);
		int ydist = (int) (y - y2);
		int zdist = (int) (z - z2);
		if ((x - x2) <= 10 && (x - x2) >= -10 && xdist != 0)
		{
			this.movecloser(target);
		}
		
		if ((y - y2) <= 10 && (y - y2) >= -10 && ydist != 0)
		{
			this.movecloser(target);
		}
		
		if ((z - z2) <= 10 && (z - z2) >= -10 && zdist != 0)
		{
			this.movecloser(target);
		}
    }
    
    public void think()
    {
    	//System.out.println("npcx : think");
    			
    	if (this.parent != null)
    	{
    		// PATH GROUP MOVEMENT
    		if (this.parent.pathgroup != null)
    		{
    			//System.out.println("Countdown : "+ this.parent.movecountdown);
    			if (this.parent.movecountdown == 0)
    			{
    				if (this.parent.currentpathspot == 0)
					{
    					// Not on a path yet spot
    					this.parent.currentpathspot = 1;
    					return;
					}
    				
    				//System.out.println("Countdown reached. Pathspot: " + this.parent.currentpathspot + "("+this.parent.pathgroup.pathgroupentries.size()+")");
    				// skip this
    				if (this.parent.currentpathspot < (this.parent.pathgroup.pathgroupentries.size()+1))
    				{
		    			for (myPathgroup_entry entry : this.parent.pathgroup.pathgroupentries)
		    			{
		    				//System.out.println("Checking: " + entry.s + "("+this.parent.currentpathspot+")");
		    				if (entry.s == this.parent.currentpathspot)
		    				{
		    					// Moved to correct entry
		    					
		    					this.moveTo(entry.x,entry.y,entry.z,entry.pitch,entry.yaw);
		    					// Set next spot
		    					this.parent.currentpathspot++;
		    					// Set cooldown on move
		    					this.parent.movecountdown = 5;
		    					return;
		    				}
		    			}
		    		} else {
		    			
		    			// End of path, move back home
		    			for (myPathgroup_entry entry : this.parent.pathgroup.pathgroupentries)
		    			{
		    				if (entry.s == 1)
		    				{
		    					this.moveTo(entry.x,entry.y,entry.z,entry.pitch,entry.yaw);
		    					this.parent.currentpathspot = 1;
		    					this.parent.movecountdown = 5;
		    					return;
		    				}
		    			}
		    		}
    				
    				
    				
    			} else {
	    				
    				
    					if (this.parent.currentpathspot > 0)
    					{
    						this.parent.movecountdown--;
    						
    					}
	    		}
    			
    		} else {
    			//this.parent.pathgroup is null
    			//lets see if we can get it from the parent spawngroup
    			if (this.parent.spawngroup != null)
    			{
	    			if (this.parent.spawngroup.pathgroup != null)
	    			{
	    				this.parent.pathgroup = this.parent.spawngroup.pathgroup;
	    			}
    			}
    			
    		}
    		
    		// END PATH GROUP MOVEMENT
    		
    		// PLAYER TARGET DISTANCE
    		
	    	for (myPlayer p : this.parent.parent.players.values())
	    	{
	    		if (p.target == this)
	    		{
		    		double x1 = p.player.getLocation().getX();
		    		double y1 = p.player.getLocation().getX();
		    		double z1 = p.player.getLocation().getX();
		    		
		    		double x2 = this.getBukkitEntity().getLocation().getX();
		    		double y2 = this.getBukkitEntity().getLocation().getY();
		    		double z2 = this.getBukkitEntity().getLocation().getZ();
		    		int xdist = (int) (x1 - x2);
		    		int ydist = (int) (y1 - y2);
		    		int zdist = (int) (z1 - z2);
		    		//System.out.println("Checking player: " + xdist );
		    		if ((xdist < -1 || xdist > 1) && (ydist < -1 || ydist > 1) && (zdist < -1 || zdist > 1))
		    		{
		    			//System.out.println("player out of range, removing target");
			    		p.target = null;
			    		p.player.sendMessage("You have lost your target");
		    		}
		    			
		    		
		    		
			    	// remove target
	    		}
	    		
	    	}
	    	
	    	// END PLAYER TARGET DISTANCE
    	}
    	
    	
    	// NPC RETURN TO SPAWN IDLE
    	if (this.parent.pathgroup != null)
		{
    		// let them carry on for guard sequences
    		
    		//return;
		} else {
    		
	    	if (follow == null && aggro == null)
	    	{
	    		//System.out.println("npcx : moving  ["+ spawnx + "] ["+ spawny + "] ["+ spawnz + "]");
	    		
	    		// not aggrod or following, time to go home :)
	    		double x2 = this.getBukkitEntity().getLocation().getX();
	    		double y2 = this.getBukkitEntity().getLocation().getY();
	    		double z2 = this.getBukkitEntity().getLocation().getZ();
	    		
	    		if (!(x2 == spawnx && y2 == spawny && z2 == spawnz))
	    		{
	    			
	    		//	System.out.println("Going home");
	    			
	    			Double yaw2 = new Double(spawnyaw);
	                Double pitch2 = new Double(spawnpitch);
	                
		    		moveTo(spawnx,spawny,spawnz,yaw2.floatValue(),pitch2.floatValue());
	    		}
	    	}
		}
    	// NPC FOLLOW

    	
		if (follow instanceof LivingEntity)
		{
			if (this.follow != null)
			{
	    		//System.out.println("follow" + follow.toString()+":"+follow.getHealth());
				// lets follow this entity
				if (this.hp == 0 || this.follow.getHealth() == 0)
				{
					this.follow = null;
					this.aggro = null;
				} else {
					moveto(follow);
				}
			}
		}
		
		// NPC ATTACK
		
		if (aggro instanceof LivingEntity)
		{
//    		System.out.println("GRR");

			// lets follow this entity
			if (this.aggro != null)
				{
				if (this.hp == 0)
				{
	
					this.follow = null;
					this.aggro = null;
	
				} else {
					attackLivingEntity(aggro);
	
				}
			}
			
		}
		
		
				
		
    }
    
    public void movecloser(LivingEntity e)
    {
    	try
    	{
	    	
	    		if (e instanceof BasicHumanNpc)
		    	{
	    			return;	    			
		    	}
		    	double x = e.getLocation().getX();
		    	double y = e.getLocation().getY();
		    	double z = e.getLocation().getZ();
		    	double mx = this.getBukkitEntity().getLocation().getX();
		    	double my = this.getBukkitEntity().getLocation().getY();
		    	double mz = this.getBukkitEntity().getLocation().getZ();
		    	double newx = mx;
		    	double newy = my;
		    	double newz = mz;
		    	
		    	if (mx != x)
		    	{
		    		//System.out.println("npcx : moving x ["+ mx+ "] -> ["+ x + "]("+ (mx - x) + ")");
			    	
		    		if ((mx - x) > 0.5)
		    		{
		    			newx = mx-2;		    		
		    		}
		    		if ((mx - x) < -0.5)
		    		{
		    			newx = mx+2;
		    		}
		    	}
		    	
		    	if (my != y)
		    	{
		    		//System.out.println("npcx : moving y ["+ my+ "] -> ["+ y + "]("+ (my - y) + ")");
			    	
		    		if ((my - y) > 2)
		    		{
		    			newy = my-0.5;
		    		}
		    		if ((my - y) < -2)
		    		{
		    			newy = my+0.5;
		    		}
		    	}
	    		
	    		if (mz != z)
		    	{
	    			//System.out.println("npcz : moving z ["+ mz+ "] -> ["+ z + "]("+ (mz - z) + ")");
			    	
		    		if ((mz - z) > 2)
		    		{
		    			newz = mz-0.5;
		    		}
		    		if ((mz - z) < -2)
		    		{
		    			newz = mz+0.5;
		    			
		    		}
		    	}    
	    		this.moveTo(newx, newy, newz,e.getLocation().getYaw()+180,e.getLocation().getPitch());
                
	    
    	} catch (Exception x)
    	{
    		 x.printStackTrace();
    	}
    	
    }
    
    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.mcEntity.c(x, y, z, yaw, pitch);
    }

    public void attackLivingEntity(LivingEntity ent) {
        try {
        	
            
            if ((ent.getHealth() - dmg) < 0)
            {
            	ent.setHealth(0);
            	follow = null;
            	aggro = null;
            	if (ent instanceof Player)
            	{
            		
            		((Player) ent).sendMessage("You have been slaughtered by " + getName());
            		
            	}
            } else {
            	if (ent instanceof Monster)
            	{
            		
	            	//System.out.println("Gahh! ");
	            	this.mcEntity.animateArmSwing();
	            	ent.setHealth(ent.getHealth()-dmg);
		            //ent.damage(dmg);
		            
	            	
	            	
	            } else {
	            	if (ent instanceof Player)
	            	{
		            	this.mcEntity.animateArmSwing();
		            	ent.damage(dmg);
		            }
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animateArmSwing()
    {
        this.mcEntity.animateArmSwing();
    }


}
