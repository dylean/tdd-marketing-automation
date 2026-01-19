package com.tdd.ma.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 活动 Mapper
 */
@Mapper
public interface CampaignMapper extends BaseMapper<CampaignPO> {

    @Select("SELECT COUNT(*) > 0 FROM t_campaign WHERE name = #{name}")
    boolean existsByName(String name);
}
