package com.kaomao.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaomao.usercenter.model.domain.Team;
import com.kaomao.usercenter.model.domain.User;
import com.kaomao.usercenter.model.dto.TeamQuery;
import com.kaomao.usercenter.model.request.TeamJoinRequest;
import com.kaomao.usercenter.model.request.TeamQuitRequest;
import com.kaomao.usercenter.model.request.TeamUpdateRequest;
import com.kaomao.usercenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-09-30 02:14:32
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User logininUser);

    boolean updateTeam(TeamUpdateRequest team, User loginUser);

    boolean deleteTeam(long id, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean b);

}
