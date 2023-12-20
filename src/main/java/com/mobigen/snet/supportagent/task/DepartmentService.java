package com.mobigen.snet.supportagent.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobigen.snet.supportagent.dao.was.AuditCommonMapper;
import com.mobigen.snet.supportagent.models.was.DepartmentTreeModel;
import com.mobigen.snet.supportagent.service.AbstractService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WAS → Support Manager 기능 이관
 * DepartmentService.departmentBatchJob
 */
@Component
public class DepartmentService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<DepartmentTreeModel> filteredDepartment;
    private List<DepartmentTreeModel> allDepartment;
    private DepartmentTreeModel root;

    @Value("${person.info.rcv.current.json.path}")
    private String personInfoRcvCurrentJsonPath;

    @Value("${department.batch.job.apply.update.hook}")
    private boolean applyUpdateHook;

    @Autowired (required = false)
    AuditCommonMapper auditCommonMapper;

    @PostConstruct
    public void init() {
        File jsonDirectory = new File(FilenameUtils.getFullPath(personInfoRcvCurrentJsonPath));
        if (!jsonDirectory.exists()) jsonDirectory.mkdirs();

        departmentBatchJob();
    }

    /**
     * 진단상세보기 화면내의 부서 및 팀정보 조회용 인터페이스를 변경
     */
    @Scheduled(cron = "${department.batch.job.cron}")
    public void departmentBatchJob() {
        FileReader inputJsonReader = null;
        OutputStream outputJsonStream = null;

        if (applyUpdateHook) {

            logger.info("*[departmentBatchJob] start");

            // Step1: 부서정보 Update용 Hook 호출
            logger.info(String.format("*[departmentBatchJob] personInfoRcvCurrentJsonPath : %s", personInfoRcvCurrentJsonPath));
            logger.info(String.format("*[departmentBatchJob] applyUpdateHook status : %b", applyUpdateHook));

            List<DepartmentTreeModel> listPersonInfoLegacy = null;
            List<DepartmentTreeModel> listPersonInfoCurrent = null;
            List<DepartmentTreeModel> listPersonInfoChanged = new ArrayList<>();

            try {

                listPersonInfoCurrent = auditCommonMapper.selectPersonInfoAll();

                if (new File(personInfoRcvCurrentJsonPath).exists()) {
                    Type listType = new TypeToken<ArrayList<DepartmentTreeModel>>(){}.getType();
                    inputJsonReader = new FileReader(personInfoRcvCurrentJsonPath);
                    listPersonInfoLegacy = new Gson().fromJson(inputJsonReader, listType);
                    for (DepartmentTreeModel current : listPersonInfoCurrent) {
                        legacyLoop:
                        for (DepartmentTreeModel legacy : listPersonInfoLegacy) {
                            if (StringUtils.equals(current.EMPNO, legacy.EMPNO)) {
                                if (!StringUtils.equals(current.DEPTNM, legacy.DEPTNM)
                                        || !StringUtils.equals(current.INDEPT, legacy.INDEPT)
                                        || !StringUtils.equals(current.HIGHPARTDEPT, legacy.HIGHPARTDEPT)
                                        || !StringUtils.equals(current.HIGHPARTDEPTNM, legacy.HIGHPARTDEPTNM)) {
                                    listPersonInfoChanged.add(current);
                                }
                                break legacyLoop;
                            }
                        }
                    }
                } else {
                    listPersonInfoChanged = listPersonInfoCurrent;
                }

                int index = 1;
                logger.info("*[departmentBatchJob] " + String.format("changed person informations total %d", listPersonInfoChanged.size()));
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                for (DepartmentTreeModel departmentTreeModel : listPersonInfoChanged) {
                    logger.info("*[departmentBatchJob] " + String.format("changed person informations %d of %d", index++, listPersonInfoChanged.size()));
                    updateAllTableDepartmentInfoBy(departmentTreeModel);
                }
                stopWatch.stop();
                logger.info(String.format("elapsed time %d ", stopWatch.getTime()));

                // 작업이 정상적으로 완료되면 local json file 교체
                if (new File(personInfoRcvCurrentJsonPath).exists()) {
                    new File(personInfoRcvCurrentJsonPath).renameTo(new File(personInfoRcvCurrentJsonPath + ".bak"));
                }
                outputJsonStream = new FileOutputStream(personInfoRcvCurrentJsonPath);
                IOUtils.write(new Gson().toJson(listPersonInfoCurrent).toString(), outputJsonStream);

            } catch (Exception e) {
                e.printStackTrace();
                IOUtils.closeQuietly(inputJsonReader);
                IOUtils.closeQuietly(outputJsonStream);
            }

            // Step2: 부서정보 Tree 업데이트
            initTree();

            logger.info("*[departmentBatchJob] end");
        }
    } // end method departmentBatchJob

    public void updateAllTableDepartmentInfoBy(DepartmentTreeModel departmentTreeModel) {
        logger.info("*[departmentBatchJob] " + String.format("updateAllTableDepartmentInfoBy INFO: %s %s" , departmentTreeModel.EMPNO, departmentTreeModel.DEPTNM));
        updateDepartmentAboutSnetAssetMaster(departmentTreeModel);
        logger.info("*[departmentBatchJob] updateDepartmentAboutSnetAssetMaster");
        updateDepartmentAboutSnetAssetUser(departmentTreeModel);
        logger.info("*[departmentBatchJob] updateDepartmentAboutSnetAssetUser");
        updateDepartmentAboutSnetConfigUserAuth(departmentTreeModel);
        logger.info("*[departmentBatchJob] updateDepartmentAboutSnetConfigUserAuth");
        updateDepartmentAboutSnetConfigUserAuthCheck(departmentTreeModel);
        logger.info("*[departmentBatchJob] updateDepartmentAboutSnetConfigUserAuthCheck");
    } // end method updateAllTableDepartmentInfoBy

    public void updateDepartmentAboutSnetAssetMaster(DepartmentTreeModel departmentTreeModel) {
        try {
            auditCommonMapper.updateDepartmentAboutSnetAssetMaster(departmentTreeModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end method updateDepartmentAboutSnetAssetMaster

    public void updateDepartmentAboutSnetAssetUser(DepartmentTreeModel departmentTreeModel) {
        try {
            auditCommonMapper.updateDepartmentAboutSnetAssetUser(departmentTreeModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end method updateDepartmentAboutSnetAssetUser

    public void updateDepartmentAboutSnetConfigUserAuth(DepartmentTreeModel departmentTreeModel) {
        try {
            auditCommonMapper.updateDepartmentAboutSnetConfigUserAuth(departmentTreeModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end method updateDepartmentAboutSnetConfigUserAuth

    public void updateDepartmentAboutSnetConfigUserAuthCheck(DepartmentTreeModel departmentTreeModel) {
        try {
            auditCommonMapper.updateDepartmentAboutSnetConfigUserAuthCheck(departmentTreeModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end method updateDepartmentAboutSnetConfigUserAuthCheck

    private synchronized void initTree() {
        try {

            allDepartment = auditCommonMapper.selectDepartmentAll();
            logger.info("*[departmentBatchJob] selectDepartmentAll size : {}", allDepartment.size());
            filteredDepartment = auditCommonMapper.selectDepartmentApplyFilter();
            logger.info("*[departmentBatchJob] selectDepartmentApplyFilter size : {}", filteredDepartment.size());

            // 상위노드 추가
            addParentsNodes();
            Collections.sort(filteredDepartment);

            // root model 생성
            root = getRootDept("00000002"); // 최상위 노드 사장 (INDEPT: 00000002)

            if (root == null)
                root = new DepartmentTreeModel();

            // 전체 트리생성
            root.initChildNode(getSubDept(root.INDEPT), filteredDepartment);

            // 정책에 따른 특정노드 제거
            removeNode();

            printFullTree(root.rows, 0, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // end method initTree


    public void addParentsNodes() {
        DepartmentTreeModel parents = null;
        for (DepartmentTreeModel target : filteredDepartment) {
            if (!isContainDepartment(target.HIGHPARTDEPT)) {
                parents = getParents(target.HIGHPARTDEPT);
                if (parents != null) break; // 전체 부서목록에서 상위부서 코드를 찾지 못하면 skip
            }
        }
        if (parents != null) {
            filteredDepartment.add(parents);
            addParentsNodes();
        }
    } // end method addParentsNodes

    public boolean isContainDepartment(String highPartDept) {
        boolean result = false;
        for (DepartmentTreeModel target : filteredDepartment) {
            if (StringUtils.equals(target.INDEPT, highPartDept)) {
                result = true;
                break;
            }
        }
        return result;
    } // end method isContainDepartment

    public DepartmentTreeModel getParents(String highPartDept) {
        DepartmentTreeModel highDepartment = null;
        for (DepartmentTreeModel department : allDepartment) {
            if (StringUtils.equals(department.INDEPT, highPartDept)) {
                highDepartment = department;
            }
        }
        return highDepartment;
    } // end method getParents

    public DepartmentTreeModel getRootDept(String inDept) {
        DepartmentTreeModel root = null;
        for (DepartmentTreeModel dept : filteredDepartment) {
            if (StringUtils.equals(inDept, dept.INDEPT)) {
                root = dept;
                break;
            }
        }
        return root;
    } // end method getRootDept

    public List<DepartmentTreeModel> getSubDept(String indept) {
        List<DepartmentTreeModel> subList = new ArrayList<>();
        for (DepartmentTreeModel dept : filteredDepartment) {
            if (StringUtils.equals(indept, dept.HIGHPARTDEPT)) {
                subList.add(dept);
            }
        }
        return subList;
    } // end method getSubDept

    public void removeNode() {
        String[] excludeNodeArr = {
                "00003915", "00009980", "00003657", "00003653", "00003820",
                "00003823", "00003967", "00003652", "00001371", "00003236",
                "00003824", "00003987","00003994", "00003996"
        };
        for (String targetDept : excludeNodeArr) {
            removeNode(root.rows, targetDept);
        }
    } // end method removeNode

    public void removeNode(List<DepartmentTreeModel> startNode, String targetDept) {
        for (DepartmentTreeModel model : startNode) {
            if (StringUtils.equals(model.INDEPT, targetDept)) {
                startNode.remove(model);
                break;
            }

            if (model.rows.size() > 0) removeNode(model.rows, targetDept);
        }
    } // end method removeNode

    public void printFullTree(List<DepartmentTreeModel> list, int spacingSize, boolean excludeLastNode) {
        int nextSpacingSize = spacingSize + 1;
        for (DepartmentTreeModel model : list) {
            if (model.rows.size() > 0 || !excludeLastNode) {
                printFullTree(model.rows, nextSpacingSize, excludeLastNode);
            }
        }
    } // end method printFullTree

    public String getSpacing(int spacingSize) {
        String spacing = null;
        if (spacingSize > 0) {
            spacing = StringUtils.leftPad("└─", (spacingSize * 2) + 2 , " 　");
        } else {
            spacing = StringUtils.EMPTY;
        }
        return spacing;
    } // end method getSpacing
}
