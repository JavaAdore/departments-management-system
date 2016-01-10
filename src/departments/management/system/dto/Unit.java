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
public class Unit extends Base implements Serializable{
    
    
    private Integer id;
    
    private String unitName;

    public Unit(Integer id, String unitName) {
        this.id = id;
        this.unitName = unitName;
    }

    public Unit() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return unitName !=null ?unitName : "غير معرف";
    }
    
    
    
}
