/*
 * AutoTask.java 
 *
 * Created on 20.03.2008, 19:51
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

//#ifdef AUTOTASK
package autotask;

import util.Time;

/* * * * * * * * *
 * @author Mars  *
 * * * * * * * * */

public class AutoTask implements Runnable{
        //extends DefForm implements Runnable {
    
    private static final int SLEEPTIME= 1000;
    private static boolean flag= false;

    //public static Vector taskList;

    private static AutoTask instance;

    public AutoTask(){
        //super( null);
    }

    public static AutoTask getInstance(){
	if (instance==null) {
	    instance=new AutoTask();
	}
	return instance;
    }

    public void startTask(){
        if( flag) return;
        new Thread( this).start();
        flag= true;
    }

    public void run(){
        while( flag){
            if( Time.getSecond() ==0)
                autotask.TaskList.checkTasks();
            try{
                Thread.sleep( SLEEPTIME);
            }catch( Exception e){
                flag= false;
                break;
            }// try
        }// while
    }// run()
}
//#endif
