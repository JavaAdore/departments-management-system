/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package departments.management.system.dto;

import java.io.Serializable;

/**
 *
 * @author orcl
 */
public abstract class Base implements Serializable{
    
    
    public abstract Integer getId();

    @Override
    public boolean equals(Object obj) {
       if(obj instanceof Base )
       {
       
           if(((Base)(obj)).getId() !=null && getId() !=null)
           {
               return ((Base)(obj)).getId().equals(getId()); 
           
           }
                   
       }
       return false;
    }
    
    
    
    
    
}
