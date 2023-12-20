package com.mobigen.snet.supportagent.dao.was;

import com.mobigen.snet.supportagent.models.was.DepartmentTreeModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WAS → Support Manager 기능 이관
 */
@Repository("auditCommonMapper")
public interface AuditCommonMapper {

    List<DepartmentTreeModel> selectPersonInfoAll() throws Exception;
    List<DepartmentTreeModel> selectDepartmentAll() throws Exception;
    List<DepartmentTreeModel> selectDepartmentApplyFilter() throws Exception;

    // 부서정보 업데이트용 Hook Start ==========================================================================================
    void updateDepartmentAboutSnetAssetMaster(DepartmentTreeModel departmentTreeModel) throws Exception;
    void updateDepartmentAboutSnetAssetUser(DepartmentTreeModel departmentTreeModel) throws Exception;
    void updateDepartmentAboutSnetConfigUserAuth(DepartmentTreeModel departmentTreeModel) throws Exception;
    void updateDepartmentAboutSnetConfigUserAuthCheck(DepartmentTreeModel departmentTreeModel) throws Exception;
    // 부서정보 업데이트용 Hook End   ==========================================================================================
}
