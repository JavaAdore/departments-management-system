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
public class Section extends Base implements Serializable{
    
    
    private Integer id;
    
    private String sectionName;

    public Section() {
    }

    public Section(Integer id, String sectionName) {
        this.id = id;
        this.sectionName = sectionName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    
    
    

}
