package com.mobigen.snet.supportagent.models.was;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import lombok.Data;

@Data
public class DepartmentTreeModel implements Comparable<DepartmentTreeModel> {
    public String EMPNO;
    public String INDEPT;
    public String LEVELCD;
    public String DEPTNM;
    public String HIGHPARTDEPT;
    public String HIGHPARTDEPTNM;
    public String USER_NM;
    public String USER_MS;
    public String USER_MAIL;
    public List<DepartmentTreeModel> rows;

    public void initChildNode(List<DepartmentTreeModel> rows, List<DepartmentTreeModel> filteredDepartment) {
        this.rows = rows == null ? new ArrayList<>() : rows;
        List<DepartmentTreeModel> listTemp = null;
        for (DepartmentTreeModel model : rows) {
            listTemp = getSubDept(model.INDEPT, filteredDepartment);
            model.initChildNode(listTemp, filteredDepartment);
        }
    }

    public List<DepartmentTreeModel> getSubDept(String indept, List<DepartmentTreeModel> filteredDepartment) {
        List<DepartmentTreeModel> subList = new ArrayList<>();
        for (DepartmentTreeModel dept : filteredDepartment) {
            if (StringUtils.equals(indept, dept.HIGHPARTDEPT)) {
                subList.add(dept);
            }
        }
        return subList;
    }

    @Override
    public int compareTo(DepartmentTreeModel model) {
        return DEPTNM.compareTo(model.DEPTNM);
    }
}
