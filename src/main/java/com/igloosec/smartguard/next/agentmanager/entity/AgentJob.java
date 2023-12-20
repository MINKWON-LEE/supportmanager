package com.igloosec.smartguard.next.agentmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentJob {

    private String jobType;

    private String checkFlag;   // 에이전트가 job 가져갔으면 1, WAS로부터 요청만 있어서 큐에만 등록된 상태면 0.
}
