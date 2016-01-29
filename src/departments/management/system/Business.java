/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package departments.management.system;

import departments.management.system.dto.Section;
import departments.management.system.dto.Unit;
import departments.management.system.dto.UnitSection;
import departments.management.system.exception.CannotInitateConenctionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author orcl
 */
public class Business {

    public List<Unit> loadAllUnits() {

        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from units");
                List<Unit> result;
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    result = new ArrayList();
                    while (rs.next()) {
                        Integer id = rs.getInt(1);
                        String unitName = rs.getString(2);
                        result.add(new Unit(id, unitName));
                    }
                }
                return result;

            } catch (SQLException ex) {
                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;
                throw new RuntimeException("Cannot load units");
            }

        } else {
            throw new CannotInitateConenctionException();

        }

    }

    public List<Section> loadAllSections() {

        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from sections");
                ResultSet rs = preparedStatement.executeQuery();
                List<Section> result = new ArrayList();
                while (rs.next()) {
                    Integer id = rs.getInt(1);
                    String unitName = rs.getString(2);
                    result.add(new Section(id, unitName));
                }

                rs.close();

                return result;

            } catch (SQLException ex) {
                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;

                throw new RuntimeException("Cannot load sections");
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }

        } else {
            throw new CannotInitateConenctionException();

        }

    }

    public UnitSection getUnitSection(Integer unitId, Integer sectionId) {
        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select s.section_name ,  us.* from unit_sections us , sections s where unit_id = ? and section_id =? and us.section_id = s.id ");
                preparedStatement.setInt(1, unitId);
                preparedStatement.setInt(2, sectionId);

                ResultSet rs = preparedStatement.executeQuery();

                UnitSection unitSection = new UnitSection();
                unitSection.setSectionId(sectionId);
                unitSection.setUnitId(unitId);
                while (rs.next()) {
                    String sectionName = rs.getString(1);
                    Integer id = rs.getInt(2);

                    String arrears = rs.getString(5);
                    String actions = rs.getString(6);
                    unitSection.setActions(actions);
                    unitSection.setArrears(arrears);
                    unitSection.setSectionName(sectionName);
                    unitSection.setId(id);
                    break;

                }

                rs.close();
                return unitSection;

            } catch (SQLException ex) {
                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new RuntimeException("Cannot load sections");
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }

        } else {
            throw new CannotInitateConenctionException();

        }

    }

    public static void main(String[] args) {
        System.out.println(new Business().loadAllUnits().size());
    }

    Unit addNewUnit(String unitName) {
        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into units(unit_name) values(?); ");
                preparedStatement.setString(1, unitName);
                preparedStatement.executeUpdate();

                ResultSet rs = preparedStatement.getGeneratedKeys();
                Integer id = null;
                while (rs.next()) {
                    id = rs.getInt(1);
                    break;
                }
                if (id != null) {
                    Unit unit = new Unit(id, unitName);

                    preparedStatement = connection.prepareStatement(" select  s.id from sections s  ");
                    rs = preparedStatement.executeQuery();

                    while (rs.next()) {
                        preparedStatement = connection.prepareStatement(" insert into unit_sections (unit_id , section_id) values(?,?) ");
                        preparedStatement.setInt(1, id);
                        preparedStatement.setInt(2, rs.getInt(1));
                        preparedStatement.executeUpdate();

                    }

                    connection.commit();
                    return unit;
                }
            } catch (SQLException ex) {

                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;
                throw new RuntimeException("Cannot add unit sections");
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        } else {
            throw new CannotInitateConenctionException();

        }
        return null;

    }

    void deleteUnit(Unit selectedItem) {

        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from unit_sections where unit_id = ? ");
                preparedStatement.setInt(1, selectedItem.getId());
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement(" delete from units where id = ?  ");
                preparedStatement.setInt(1, selectedItem.getId());
                preparedStatement.executeUpdate();

                connection.commit();

            } catch (SQLException ex) {

                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;
                throw new RuntimeException("Cannot xxx  unit");
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        } else {
            throw new CannotInitateConenctionException();

        }

    }

    void updateUnitSection(UnitSection selectedUnitSection) {

        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("update  unit_sections set arrears =? , actions =?  where id = ? ");
                preparedStatement.setString(1, selectedUnitSection.getArrears());
                preparedStatement.setString(2, selectedUnitSection.getActions());
                preparedStatement.setInt(3, selectedUnitSection.getId());
                preparedStatement.executeUpdate();

                connection.commit();

            } catch (SQLException ex) {

                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;
                throw new RuntimeException("Cannot add delete unit");
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        } else {
            throw new CannotInitateConenctionException();

        }

    }

    List<Unit> getUnit(String targetUnitName) {

        Connection connection = DataSource.initiateConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from units where unit_name like  ?");
                preparedStatement.setString(1,"%"+ targetUnitName.trim()+"%");
                List<Unit> result;
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    result = new ArrayList();
                    while (rs.next()) {
                        Integer id = rs.getInt(1);
                        String unitName = rs.getString(2);
                        result.add(new Unit(id, unitName));
                    }
                return result;
                }
               

            } catch (SQLException ex) {
                Logger.getLogger(Business.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();;
                throw new RuntimeException("Cannot load units");
            }

        } else {
            throw new CannotInitateConenctionException();

        }

    }

}
