SELECT 
    B.sw_type,
    SUM(CASE WHEN a.V3_SEVERITY = 'LOW' THEN 1 ELSE 0 END) AS v3_LOW,
    SUM(CASE WHEN a.V3_SEVERITY = 'MEDIUM' THEN 1 ELSE 0 END) AS v3_MEDIUM,
    SUM(CASE WHEN a.V3_SEVERITY = 'HIGH' THEN 1 ELSE 0 END) AS v3_HIGH,
    SUM(CASE WHEN a.V3_SEVERITY = 'CRITICAL' THEN 1 ELSE 0 END) AS v3_CRITICAL,
    a.min_PublishedDate,
    a.max_PublishedDate
FROM
    snet_asset_sw_cvecode b
	 INNER JOIN
    (SELECT 
        cve_code,
        V3_SEVERITY,
        (SELECT MIN(PUBLISH_DATE) FROM snet_asset_sw_cvecode_data) AS min_PublishedDate,
        (SELECT  MAX(PUBLISH_DATE) FROM snet_asset_sw_cvecode_data ) AS max_PublishedDate
    FROM snet_asset_sw_cvecode_data) a ON FIND_IN_SET(a.CVE_CODE, b.CVE_CODE) > 0
GROUP BY B.SW_TYPE;


 
SELECT 
    B.sw_type,
    B.SW_NM,
    B.SW_INFO,
    GROUP_CONCAT(A.cve_code),
    COUNT(1)
FROM
    snet_asset_sw_cvecode b
    INNER JOIN
    (SELECT cve_code 
	 FROM snet_asset_sw_cvecode_data
    WHERE
        CVE_REG_DATE LIKE '2022-07-05%') a ON FIND_IN_SET(a.CVE_CODE, b.CVE_CODE) > 0
WHERE
    B.REG_DATE NOT LIKE '2022-07-05%'
GROUP BY B.ASSET_SW_CD 
UNION SELECT 
    sw_type, SW_NM, SW_INFO, CVE_CODE, CVE_COUNT
FROM
    snet_asset_sw_cvecode
WHERE
    REG_DATE LIKE '2022-07-05%';
  
  
SELECT 
    cve_code,
    cwe_code,
    description,
    id,
    reference,
    v2_score,
    v2_severity,
    v3_score,
    v3_severity
FROM
    snet_asset_sw_cvecode_data
WHERE
    CVE_REG_DATE LIKE '2022-07-05%';