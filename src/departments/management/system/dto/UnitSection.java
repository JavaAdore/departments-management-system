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
public class UnitSection extends Base implements Serializable{
    
    private Integer id;
    
    private Integer unitId;
    
    private Integer sectionId;
    
    
    private String arrears;
    
    private String actions;

    public UnitSection() {
    }

    public UnitSection(Integer id, Integer unitId, Integer sectionId, String arrears, String actions) {
        this.id = id;
        this.unitId = unitId;
        this.sectionId = sectionId;
        this.arrears = arrears;
        this.actions = actions;
    }
    
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getArrears() {
        return arrears;
    }

    public void setArrears(String arrears) {
        this.arrears = arrears;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }
    
    
    
    
}
