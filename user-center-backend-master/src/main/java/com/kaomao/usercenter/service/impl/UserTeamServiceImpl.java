package com.kaomao.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaomao.usercenter.model.domain.UserTeam;
import com.kaomao.usercenter.service.UserTeamService;
import com.kaomao.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




